package com.rapidminer.pos_tagger.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
import com.rapidminer.pos_tagger.ioobjects.PennTag;
import com.rapidminer.pos_tagger.ioobjects.TagString;
import com.rapidminer.pos_tagger.ioobjects.Tagset;
import com.rapidminer.pos_tagger.ioobjects.TagsetType;
import com.rapidminer.tools.LogService;



public class Evaluator extends Operator{
    private static final String PARAMETER_TEXT_GOLD = "Gold Standard Tagset";
    private static final String PARAMETER_TEXT_IN = "Tagger Result Tagset";
    private static final String PARAMETER_TEXT_FORMATRES = "Result Input Format";
    private static final String PARAMETER_TEXT_FORMATGOLD = "Gold Input Format";
    private static final String PARAMETER_TEXT_IGNOREBRACKETS = "Ignore Brackets";
    
    private static final int NO_NOTATION = 0;
    private static final int BACKSLASH_NOTATION = 1;
    
	private InputPort goldInput = getInputPorts().createPort("Gold In", IOObject.class);
    private InputPort resultInput = getInputPorts().createPort("Result In", IOObject.class);
    private OutputPort evalOutput = getOutputPorts().createPort("Eval Out");
    
   

    public Evaluator(OperatorDescription description) {

        super(description);
    }
  
    @Override
    public List<ParameterType> getParameterTypes(){
    	 List<ParameterType> types = super.getParameterTypes();
    	 
    	 
    	 
    	 
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
    	//prep
    	TagString gold = null;
    	TagString input = null;
    	
    	IOObject goldInputObject = goldInput.getData(IOObject.class);
    	IOObject resultInputObject = resultInput.getData(IOObject.class);
    	
    	
    	
    	if (goldInputObject.getClass()==TagString.class){
    		gold = (TagString) goldInputObject;
    	} else if (goldInputObject.getClass()==Document.class) {
    		gold = parse((Document) goldInputObject, stringToType(getParameterAsString(PARAMETER_TEXT_GOLD)), getParameterAsInt(PARAMETER_TEXT_FORMATGOLD));
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
    	if (gold.countRows()!=input.countRows())
    		splitCorrect= false;
        
    	int wordcount=0;
    	int correctTags=0;
    	
    	if (gold.getType()==input.getType()){
    		List<List<String>> goldData = gold.getContent();
    		List<List<String>> inputData = input.getContent();
    		
    		
    	//EVALUATE PRECISION
    		if (splitCorrect){
    			//it is assumed the line-breaking Tags were identified correctly => line-by-line comparison 
    			// to avoid word-sequencing errors
    	
    			for (int i=0; i<goldData.size(); i++){
    				List<String> goldRow = goldData.get(i);
    				List<String> inputRow = inputData.get(i);
    				int min= java.lang.Math.min(goldRow.size(), inputRow.size());
    				wordcount += java.lang.Math.max(goldRow.size(), inputRow.size());
    				for (int j=0; j<min; j++){    					
    					
    					switch (gold.getType()){
    					case UNDEFINED: break;
    					case PENN_TREEBANK: 
    						if(PennTag.findTag(inputRow.get(j))==PennTag.findTag(goldRow.get(j)) 
    							&& PennTag.findTag(inputRow.get(j))!=PennTag.None){
    							
    							//check if ignorebrackets() was set
    							if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS) && (inputRow.get(j)=="(" || inputRow.get(j)==")")) {
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
    		} else {
    			//something went wrong with line-splitting
    			// => pray there are no word-sequencing errors
    			
    			//turn both ResultObjs into huge resultRow
    			List<String> newInputList= new ArrayList<String>();
    			List<String> newGoldList= new ArrayList<String>();
    			for (List<String> row: inputData){
    				for (String str: row){
    					newInputList.add(str);
    				}
    			}
    			for (List<String> row: goldData){
    				for (String str: row){
    					newGoldList.add(str);
    				}
    			}
    			
    			
    			
    			//
    			wordcount = java.lang.Math.max(newInputList.size(), newInputList.size());
    			int min = java.lang.Math.min(newInputList.size(), newInputList.size());
    			
    			for(int i=0; i<min; i++){
    				switch (gold.getType()){
					case UNDEFINED: break;
					case PENN_TREEBANK: 
						if(PennTag.findTag(newInputList.get(i))==PennTag.findTag(newInputList.get(i)) 
							&& PennTag.findTag(newInputList.get(i))!=PennTag.None)
							//check if ignorebrackets() was set
							if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS) && (newInputList.get(i)=="(" || newInputList.get(i)==")")) {
									wordcount--;
									if (wordcount<0) wordcount=0;
							} else {
									correctTags++;
							}
								
						break;
					default: break;
					}
    			}
    			
    			
    		}
    	} else {
    		// Add logic here if you want to compare different Tagsets.
    	}
    	
    	//Dummy Output ( TODO: for now just a document containing the Eval result)
    	float precision = ((float)correctTags)/((float)wordcount);
    	String Eval = String.valueOf(precision);
    	LogService.getRoot().log(Level.INFO, "Precision:" + precision);
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
			words = content.split("(\\s\\(\\))+");
		} else {
			words = content.split("\\s");
		}
		
		for (String word: words){
			//substring must be a Tag AND not a separator identical to the last tag (so ". ." isn't falsely duplicated)
			if ( Tagset.isPOS(t, word) && ((word == parseResult.getLast() && Tagset.isSeparator(t, word)) == false)){
				parseResult.addTag(word);
			}
		}
		
		   break;
	   default: mode = NO_NOTATION;
		   
	   }
	   return parseResult;
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
