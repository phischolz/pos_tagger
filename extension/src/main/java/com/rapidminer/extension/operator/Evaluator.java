package com.rapidminer.extension.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.extension.ioobjects.PennTag;
import com.rapidminer.extension.ioobjects.TagString;
import com.rapidminer.extension.ioobjects.Tagset;
import com.rapidminer.extension.ioobjects.TagsetType;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.tools.LogService;



public class Evaluator extends Operator{
    private static final String PARAMETER_TEXT_GOLD = "Gold Standard Tagset";
    private static final String PARAMETER_TEXT_IN = "Tagger Result Tagset";
    private static final String PARAMETER_TEXT_FORMATRES = "Result Input Format";
    private static final String PARAMETER_TEXT_FORMATGOLD = "Gold Input Format";
    private static final String PARAMETER_TEXT_IGNOREBRACKETS = "Ignore Brackets";
    private static final String PARAMETER_TEXT_NBEST = "Calculate N-Best Distance";
    
    private static final int NO_NOTATION = 0;
    private static final int BACKSLASH_NOTATION = 1;
	
    
	private InputPort goldInput = getInputPorts().createPort("Gold In", IOObject.class);
    private InputPort resultInput = getInputPorts().createPort("Result In", IOObject.class);
    private OutputPort evalOutput = getOutputPorts().createPort("Eval Out");
    private OutputPort DEBUG = getOutputPorts().createPort("DEBUG");
    
   

    public Evaluator(OperatorDescription description) {

        super(description);
    }
  
    @Override
    public List<ParameterType> getParameterTypes(){
    	 List<ParameterType> types = super.getParameterTypes();
    	 
    	 types.add(new ParameterTypeBoolean(
    			 PARAMETER_TEXT_NBEST,
    			 "If ticked, the average position of the correct tag in the N-Best results will be determined. The Distance (N-Best Maximum)+1 stands for not found.",
    			 false
    			 ));
    	 		
    	 
    	 types.add(new ParameterTypeCategory(
    	  		PARAMETER_TEXT_GOLD,
    	   		"Choose which Tagset the input Gold Standard uses. If the input is of the Type TagString, this is overwritten.",
    	   		typeParams(), 0));
    	    
    	 types.add(new ParameterTypeCategory(
     	  		PARAMETER_TEXT_IN,
     	   		"Choose which Tagset the input Tagger result uses. If the input is of the Type TagString, this is overwritten.",
     	   		typeParams(), 0));
    	 
    	 
    	 //If the InputType is Document and parse() is called, it's behavior will depend on these Parameters.
    	 //	0 -> None: Tries to grab every Tag
    	 // 1 -> \ Notation: Splits at every " " and "\", only grabs the tags after \.
    	 String[] FormatParams = {"None", "word\\tag notation"};
    	 types.add(new ParameterTypeCategory(
    			PARAMETER_TEXT_FORMATRES,
    			"Choose which Format the Result Input Documents have. Choose 'None' if unspecified. Only Relevant if Input Type is Document.",
    			FormatParams,
    			0
    			));
    	 types.add(new ParameterTypeCategory(
     			PARAMETER_TEXT_FORMATGOLD,
     			"Choose which Format the Gold Input Documents have. Choose 'None' if unspecified. Only Relevant if Input Type is Document.",
     			FormatParams,
     			0
     			));
    	 
    	 types.add(new ParameterTypeBoolean(
    			PARAMETER_TEXT_IGNOREBRACKETS,
    			"If any of the Input Documents formatted using '(' and ')', tick this.",
    			false
    			));
    	
     	 return types;
    }
    
    
    @Override
    public void doWork() throws OperatorException {
    	
    	TagString gold = null;
    	TagString input = null;
    	
    	IOObject goldInputObject = goldInput.getData(IOObject.class);
    	IOObject resultInputObject = resultInput.getData(IOObject.class);
    	
    	
    	//Preparation of Inputs
    	if (goldInputObject.getClass()==TagString.class){
    		gold = (TagString) goldInputObject;
    	} else if (goldInputObject.getClass()==Document.class) {
    		gold = parse((Document) goldInputObject, stringToType(getParameterAsString(PARAMETER_TEXT_GOLD)), getParameterAsInt(PARAMETER_TEXT_FORMATGOLD));
    		DEBUG.deliver(gold);
    	} else{
    		throw new OperatorException("Gold-Standard input expected to be of Type Document or TagString");
    	}
    	
    	if (resultInputObject.getClass()==TagString.class){
    		input = (TagString) resultInputObject;
    	} else if (resultInputObject.getClass()==Document.class){
    		input = parse((Document) resultInputObject, stringToType(getParameterAsString(PARAMETER_TEXT_IN)), getParameterAsInt(PARAMETER_TEXT_FORMATRES));
    	} else {
    		throw new OperatorException("Tagger Result input expected to be of Type Document or TagString");
    	}
    	
       
        
    	//determine whether sentences/rows were split correctly
    	boolean splitCorrect = true;
    	if (gold.countRows()!=input.countRows()){
    		splitCorrect= false;
    		LogService.getRoot().log(Level.INFO, "SplitCorrect is FALSE");
    	}
    	
    	
    	//PRECISION EVALUATION
    	float precision  = 0;	
    	float ndist = 0;
    	float nprecision = 0;
    	float nbest_max = 0;
    	if (gold.getType()==input.getType())	{
    		
    		if (splitCorrect==false){
    			//something went wrong with line-splitting
    			// -> pray there are no word-sequencing errors
    			
    			LogService.getRoot().log(Level.INFO, "1. goldnew: " + gold.toString());
    			
    			//turn both ResultObjs into one single-line TagString
    			List<List<String[]>> goldData = gold.getContent();
    			List<List<String[]>> inputData = input.getContent();
    			
    			List<String[]> newInputList= new ArrayList<String[]>();
    			List<String[]> newGoldList= new ArrayList<String[]>();
    			for (List<String[]> row: inputData){
    				for (String[] str: row){
    					newInputList.add(str);
    				}
    			}
    			for (List<String[]> row: goldData){
    				for (String[] str: row){
    					newGoldList.add(str);
    				}
    			}
    			
    			TagString goldNew = new TagString();
    			goldNew.setType(gold.getType());
    			goldNew.setNbest(gold.getNbest());
    			goldNew.appendRow(newGoldList);
    			gold = goldNew;
    			
    			LogService.getRoot().log(Level.INFO, "1. goldnew: " + gold.toString());
    			
    			TagString inputNew = new TagString();
    			inputNew.setType(input.getType());
    			inputNew.setNbest(input.getNbest());
    			input = inputNew;
    			input.appendRow(newInputList);
    			
    			LogService.getRoot().log(Level.INFO, "1. inputnew: " +  input.toString());
    			
    			
    		}
    		
    		precision = calculatePrecision(gold, input);
    		
    		if (getParameterAsBoolean(PARAMETER_TEXT_NBEST)){
    			float[] nres = calculateNdist(gold, input);
    			ndist = nres[0];
    			nprecision = nres[1];
    			nbest_max = nres[2];
    		}
    		
    	} else {
    		// Add logic here if you want to compare different Tagsets.
    		LogService.getRoot().log(Level.WARNING, "ERROR: TAGSETS DIFFER (CANNOT EVALUATE)");
    		precision = -1;
    		ndist = -1;
    		nprecision= -1;
    		nbest_max = -1;
    	}
    	
    	//Dummy Output ( TODO: for now just a document containing the Evaluation result)
    	
    	String Eval = "Precision: " + String.valueOf(precision);
    	LogService.getRoot().log(Level.INFO, "Precision:" + precision);
    	
    	if (getParameterAsBoolean(PARAMETER_TEXT_NBEST)){
    		Eval += "\n" + "Average Distance in N-Best(" + (int)nbest_max + "): " + String.valueOf(ndist);
    		Eval += "\n" + "Precision including all N-Best(" + (int)nbest_max + "): " + String.valueOf(nprecision);
    	}
    	
    	
    	
    	Document d = new Document(Eval);
    	
    	evalOutput.deliver(d);

    }
    
   

// takes a Document and parses it based on its format
   private TagString parse (Document doc, TagsetType t, int mode){
	   
	   TagString parseResult = new TagString();
	   parseResult.setType(t);
	   
	   String content = doc.getTokenText();
	   String[] words;
	   
	   switch (mode){
	   case BACKSLASH_NOTATION:
	   	
	   	words = content.split("\\s+");
	   	for (String word: words){
	   		if (word.contains("\\")){
	   			String[] split = word.split("\\\\");
	   			if (split.length == 2){ 
	   				parseResult.addTag(split[1]);}
	   		}
	   	}
	   	break;
	   case NO_NOTATION:
		   if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS)){
			   words = content.split("[\\s\\(\\)]+");
		   } else {
			   words = content.split("\\s");
		   }
		
		   boolean forfeit= false;
		   for (String word: words){
			   //substring must be a Tag AND not a separator identical to the last tag (so ". ." isn't falsely duplicated)
			   if ( Tagset.isPOSStrict(t, word) && (word.equals( parseResult.getLast1()) == false || forfeit == true )){
				   parseResult.addTag(word);
				   forfeit = false;
			   } else if (Tagset.isPOSStrict(t, word)){
				   forfeit = true;
					}
		   }
		
		   break;
	   default: mode = NO_NOTATION;
		   
	   }
	   return parseResult;
   }
   
   
   
   private float calculatePrecision(TagString gold, TagString input){
	    int wordcount=0;
   		int correctTags=0;
   	
   		if (gold.getType()==input.getType()){
   			List<List<String[]>> goldData = gold.getContent();
   			List<List<String[]>> inputData = input.getContent();
   			
   			int rowMax = java.lang.Math.min(goldData.size(), inputData.size());
   			for (int i=0; i<rowMax; i++){
				List<String[]> goldRow = goldData.get(i);
				List<String[]> inputRow = inputData.get(i);
				int wordMax= java.lang.Math.min(goldRow.size(), inputRow.size());
				wordcount += java.lang.Math.max(goldRow.size(), inputRow.size());
				for (int j=0; j<wordMax; j++){    					
					
					if (inputRow.get(j).length != 0 && goldRow.get(j).length != 0)
						switch (gold.getType()){
						case UNDEFINED: break;
						case PENN_TREEBANK: 
							if(PennTag.findTag(inputRow.get(j)[0])==PennTag.findTag(goldRow.get(j)[0]) 
								&& PennTag.findTag(inputRow.get(j)[0])!=PennTag.None){
							
								//check if 'ignore brackets' was set
								if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS) && (inputRow.get(j)[0]=="(" || inputRow.get(j)[0]==")")) {
									wordcount--;
									if (wordcount<0) wordcount=0;
								} else {
									correctTags++;
								}
							}
							break;
						default: break;
						}
					}	
			}	
   		}
   		
   		LogService.getRoot().log(Level.INFO, "correct tags: " + correctTags);
   		LogService.getRoot().log(Level.INFO, "word count: " + wordcount);
   		return ((float)correctTags)/((float)wordcount);
   }
   
   private float[] calculateNdist(TagString gold, TagString input) {
		float[] nres = {0, 0, 0};
		int maxdist = java.lang.Math.min(gold.getNbest(), input.getNbest());
		int ndistmax = 0;
   		int ndistsum = 0;
   		
   		int wordcount = 0;
   		int wordcorrect = 0;
   		
   		List<List<String[]>> goldData = gold.getContent();
   		List<List<String[]>> inputData = input.getContent();
   		
   		int rowMax = java.lang.Math.min(goldData.size(), inputData.size());
		for (int i=0; i<rowMax; i++){
			List<String[]> goldRow = goldData.get(i);
			List<String[]> inputRow = inputData.get(i);
			int wordMax= java.lang.Math.min(goldRow.size(), inputRow.size());
			
			ndistmax += java.lang.Math.max(goldRow.size(), inputRow.size()) * (maxdist+1);
			wordcount += java.lang.Math.max(goldRow.size(), inputRow.size());
			for (int j=0; j<wordMax; j++){    					
				int ndisthere = maxdist+1;
				boolean ndistfound = false;
				
				for (int n=0; n< maxdist; n++){
				    
					
					if (ndistfound==false && inputRow.get(j).length > n && goldRow.get(j).length > n)
						switch (gold.getType()){
							case UNDEFINED: break;
							case PENN_TREEBANK: 
								if(PennTag.findTag(inputRow.get(j)[n])==PennTag.findTag(goldRow.get(j)[n]) ){
									// && PennTag.findTag(inputRow.get(j)[n])!=PennTag.None){
						
									//check if ignorebrackets() was set
									if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS) 
										&& (inputRow.get(j)[n]=="(" || inputRow.get(j)[n]==")")) {
										
										ndistmax -= maxdist+1;
										if (ndistmax<0) ndistmax=0;
										
										wordcount--;
										if (wordcount<0) wordcount=0;
									} else {
										ndisthere = n+1;
										ndistfound = true;
										wordcorrect++;
									}
								}
								break;
					default: break;
					}
				}
				ndistsum += ndisthere;
				
			}
   		}
   			
   		nres[0] = ((float)ndistsum/(float)ndistmax)*(maxdist+1);
   		nres[1] = ((float)wordcorrect/(float)wordcount);
   		nres[2] = maxdist;
		return nres;
	}
    

   private String[] typeParams(){
	   TagsetType[] typeList = TagsetType.values();
  	 String[] typeParams = new String[typeList.length];
  	 for (int i=0; i< typeList.length; i++){
  		 typeParams[i] = typeList[i].toString();
  	 }
  	 
  	 return typeParams;
   }

   private TagsetType stringToType(String s){
	   TagsetType[] types = TagsetType.values();
	   for (TagsetType t: types){
		   if(s.equalsIgnoreCase(t.toString())) return t;
	   }
	   return TagsetType.UNDEFINED;
   }
}
