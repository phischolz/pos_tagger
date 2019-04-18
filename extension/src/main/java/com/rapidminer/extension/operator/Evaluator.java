package com.rapidminer.extension.operator;

import java.util.List;
import java.util.logging.Level;

import com.rapidminer.extension.ioobjects.TagString;
import com.rapidminer.extension.ioobjects.TagToken;
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
import com.rapidminer.parameter.ParameterTypeString;
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
	private static final int PARENT_NOTATION = 2;
	private static final String PARAMETER_TAGONLY = "Inputdocs without tokens";
	private static final String PARAMETER_MATRIX = "Show confusion matrix";
	private static final String PARAMETER_ADVANCED = "Advanced Scores";
	
	
    
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
    	 
    	 types.add(new ParameterTypeString(
    			 PARAMETER_ADVANCED,
    			 "Specify Tags you want advanced Scores(Precision, Recall, F-Score) for. Divide with spaces.",
    			 ""));
    	 
    	 types.add(new ParameterTypeBoolean(
    			 PARAMETER_MATRIX,
    			 "Choose whether to include the confusion Matrix in the Result",
    			 false
    			 ));
    	 
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
    	 String[] FormatParams = {"None", "word\\tag notation", "parenthesis notation"};
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
    	 
    	 types.add(new ParameterTypeBoolean(
     			PARAMETER_TAGONLY,
     			"If the Input Documents do not contain the original Tokens, tick this.",
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
    	
       
    	
    	
    	
    	
    	int[] accuracyInfo  = {0,0,0,0};	
    	float ndist = 0;
    	float nprecision = 0;
    	float nbest_max = 0;
    	int[][] matrix = null;
    	
    	
    	
    	String[] tagset = Tagset.values(gold.getType());
    	
    	
    	if (gold.getType()==input.getType())	{
    		
    		
    		
    		 
    		
    		
    		
    		if ((gold.getRowCount()!=input.getRowCount())){
    			
    			gold.serialize();
    			input.serialize();
    		}
    		
    		//ACCURACY EVALUATION
    		accuracyInfo = calculateAccuracy(gold, input);
    		
    		
			//CONFUSION MATRIX
        	matrix = confusionMatrix(gold, input, tagset);
        	//PRECISION
        	
        	//RECALL
    		
    		if (getParameterAsBoolean(PARAMETER_TEXT_NBEST)){
    			float[] nres = calculateNdist(gold, input);
    			ndist = nres[0];
    			nprecision = nres[1];
    			nbest_max = nres[2];
    		}
    		
    		
    		
    	} else {
    		// Add logic here if you want to compare different Tagsets.
    		LogService.getRoot().log(Level.WARNING, "ERROR: TAGSETS DIFFER (CANNOT EVALUATE)");
    		throw new OperatorException("Tagsets in Evaluation differ - cannot evaluate");
    	}
    	
    	
    	
    	//Dummy Output ( TODO: proper format? )
    	
    	float accuracy = 0;
    	if (accuracyInfo[1]>0) accuracy = (float)accuracyInfo[0]/(float)accuracyInfo[1];
    	float sentenceAccuracy = 0;
    	if (accuracyInfo[3]>0) sentenceAccuracy = (float)accuracyInfo[2]/(float)accuracyInfo[3];;
    		
    	String Eval = "Accuracy:\t\t" + String.valueOf(accuracy) + "\t||\t" + accuracyInfo[0] + "/" + accuracyInfo[1] +"\n"
    			+ "Sentence Accuracy:\t" + String.valueOf(sentenceAccuracy) + "\t||\t" + accuracyInfo[2] + "/" + accuracyInfo[3] +"\n\n";
    	LogService.getRoot().log(Level.INFO, "Precision:" + accuracyInfo);
    	
    	if (getParameterAsBoolean(PARAMETER_TEXT_NBEST)){
    		Eval += "\n" + "Average Distance in N-Best(" + (int)nbest_max + "): " + String.valueOf(ndist);
    		Eval += "\n" + "Accuracy including all N-Best(" + (int)nbest_max + "): " + String.valueOf(nprecision) + "\n\n";
    	}
    	
    	//Precision, Recall, F-Score
    	String[] tagsToScore = getParameterAsString(PARAMETER_ADVANCED).split("[\\s,]+");
    	for (String s: tagsToScore){
    		float tagPrec = precision(s, tagset, matrix);
    		float tagRec = recall(s, tagset, matrix);
    		Eval += "Precision(" + s + "): " + String.valueOf(tagPrec) + "\n";
    		Eval += "Recall(" + s + "): " + String.valueOf(tagRec) + "\n";
    		if (tagPrec+tagRec>0){
    			Eval += "F-Score(" + s + "): " + String.valueOf(2*((tagPrec*tagRec)/(tagPrec+tagRec))) + "\n\n";
    		} else {
    			Eval += "F-Score(" + s + "): " + String.valueOf(0) + "\n\n";
    		}
    	}
    	
    	//Show Confusion Matrix if set
    	if (getParameterAsBoolean(PARAMETER_MATRIX)){
    		String matrixText = "CONFUSION MATRIX: \n\n";
    		for (int[] i: matrix){
    			for (int x: i){
    				matrixText += x + "\t";
    			}
    			matrixText += "\n";
    		}
    	
    		Eval += matrixText;
    	}
    	
    	
    	Document d = new Document(Eval);
    	
    	evalOutput.deliver(d);

    }
    
   

    private int[][] confusionMatrix(TagString gold, TagString input, String[] tags) {
		
		//Matrix init
	
		//Matrix format:
		//[index of tags referenced by gold][index of tags referenced by input] = amount of tags referenced in this combination
    	
		int[][] matrix = new int[tags.length][tags.length];
		for (int i=0; i<tags.length; i++){
			for (int j=0; j<tags.length; j++){
				matrix[i][j] = 0;
			}
		}
		
		//Matrix filling
		
		//iterate over Rows
		for (int i=0; i<java.lang.Math.min(gold.getRowCount(), input.getRowCount()); i++){
			
			
			//iterate over tokens
			for (int j=0; j<java.lang.Math.min(gold.getRowCount(), input.getRowCount()); j++){
				
				
				int indexGold = -1;
				int indexInput = -1;
				
				//find index of the tags referenced
				for (int t=0; t<tags.length; t++){
					if (gold.getTagToken(i, j).getFirstTag().equals(tags[t])) indexGold = t;
					if (input.getTagToken(i, j).getFirstTag().equals(tags[t])) indexInput = t;
				}
				
				//both indexes found -> ++ in that spot
				if(indexGold>=0 && indexInput>=0)
				matrix[indexGold][indexInput]++;
			}
		}
		
		return matrix;
	}

    private float precision (String tag, String[] tagset, int[][] cmatrix){	
    	//TODO
    	int index = -1;
    	for (int i=0; i<tagset.length; i++){
    		if (tag.equals(tagset[i])) index = i;
    	}
    	
    	if (index != -1){
    		int sum = 0;
    		for (int i=0; i<tagset.length; i++){
    			sum += cmatrix[i][index];
    		}
    		if (sum>0) return ((float)cmatrix[index][index]/(float)sum);
    	}
    	
    	return 0;
    }
    
    private float recall (String tag, String[] tagset, int[][] cmatrix){
    	//TODO
    	int index = -1;
    	for (int i=0; i<tagset.length; i++){
    		if (tag.equals(tagset[i])) index = i;
    	}    	
    	
    	if (index != -1){
    		int sum = 0;
    		for (int i=0; i<tagset.length; i++){
    			sum += cmatrix[index][i];
    		}
    		if (sum>0) return ((float)cmatrix[index][index]/(float)sum);
    	}
    	
    	return 0;
    }
    
    // takes a Document and parses it based on its format
    private TagString parse (Document doc, TagsetType t, int mode){
	   
    
	   TagString parseResult = new TagString(1, t);
	 
	   
	   String content = doc.getTokenText();
	   String[] words;
	   
	   
	   switch (mode){
	   case BACKSLASH_NOTATION:
	   	
	   	words = content.split("\\s+");
	   	for (String word: words){
	   		if (word.contains("\\")){
	   			String[] split = word.split("\\\\");
	   			if (split.length == 2){ 
	   				
	   				parseResult.addTag(split[0], split[1]);}
	   		}
	   	}
	   	break;
	   case PARENT_NOTATION:
		   words = content.split("[\\(\\)]+");
		   for (String word: words){
			   String[] str = word.split("\\s+");
			   if (str.length==2){
				   if (Tagset.isPOS(t, str[0], true))
						   parseResult.addTag(str[1], str[0]);
				   
				   else if (Tagset.isPOS(t, str[1], true))
						   parseResult.addTag(str[0], str[1]);
			   }
		   }
		   break;
	   case NO_NOTATION:
		   if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS)){
			   words = content.split("[\\s\\(\\)]+");
		   } else {
			   words = content.split("\\s");
		   }
		
		   
		   String previous = "";
		   for (String word: words){
			   if (word.equals(previous)==false){
				   if (Tagset.isPOS(t, word, true)) parseResult.addTag("null", word);
			   }
			   previous = word;
		   }
		
		   break;
	   default: mode = NO_NOTATION;
		   
	   }
	   return parseResult;
   }
   
   
   /**
    * Takes two Tagstrings and compares them (sentence-wise).
    * @param gold
    * @param input
    * @return number of matching tags, number of tags (sum of the larger rows), number of matching sentences, number of sentences.  
    */
   private int[] calculateAccuracy(TagString gold, TagString input){
	    int wordcount=0;
   		int correctTags=0;
   		int correctRows=0;
   		int rowCount = java.lang.Math.max(gold.getRowCount(), input.getRowCount());
   		
   		TagsetType type = gold.getType();
   		
   		for (int i= 0; i<rowCount ; i++){
			
			
			int correctTagsHere = 0;
			int wordMax= java.lang.Math.max(gold.getRowSize(i), input.getRowSize(i));
			wordcount += wordMax;
			for (int j=0; j<wordMax ; j++){    					
				
				
				 
				if(input.getTagToken(i, j).getFirstTag().equals(gold.getTagToken(i, j).getFirstTag())
						&& Tagset.isPOS(type, input.getTagToken(i, j).getTags()[0], true)){
				
					//check if 'ignore brackets' was set
					if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS) && (input.getTagToken(i, j).getToken()=="(" 
							|| gold.getTagToken(i, j).getToken()==")")) {
						wordcount--;
						
					} else {
						correctTags++;
						correctTagsHere++;
					}
				
				}
						
					
				
			}
			if (wordMax == correctTagsHere){correctRows++;}
		
   		}	
   		
   		
   		
   		
   		int[] f = new int[4];
   		f[0] = correctTags;
   		f[1] = wordcount;
   		f[2] = correctRows;
   		f[3] = rowCount;
   		return f;
   }
   
   /**
    * Takes two Tagstrings and compares them (sentence-wise).
    * If input has multiple (n-best) tags per Token, they will be evaluated too.
    * @param gold
    * @param input
    * @return average index of tags in input that correspond with gold (n+1 means no match), Accuracy including all n-best tokens. 
    */
   private float[] calculateNdist(TagString gold, TagString input) {
		float[] nres = {0, 0, 0};
		int maxdist =  input.getNbest();
		int ndistmax = 0;
   		int ndistsum = 0;
   		
   		int wordcount = 0;
   		int wordcorrect = 0;
   		
   		TagsetType type = gold.getType();
   		
   		int rowMax = java.lang.Math.max(gold.getRowCount(), input.getRowCount());
		for (int i=0; i<rowMax; i++){
			
			int wordMax= java.lang.Math.max(gold.getRowSize(i), input.getRowSize(i));
			ndistmax += wordMax * (maxdist+1);
			wordcount += wordMax;
			
			for (int j=0; j<wordMax; j++){    					
				int ndisthere = maxdist+1;
				
				boolean ndistfound = false;	
				for (int n=0; n< maxdist; n++){
				    
				
			
						
					if(input.getTagToken(i, j).getTags()[n].equals(gold.getTagToken(i, j).getFirstTag())
						&& Tagset.isPOS(type, input.getTagToken(i, j).getTags()[n], true) && ndistfound == false){
						
							//check if ignorebrackets() was set
							if (getParameterAsBoolean(PARAMETER_TEXT_IGNOREBRACKETS) 
								&& (input.getTagToken(i, j).getTags()[n]=="(" || input.getTagToken(i, j).getTags()[n]==")")) {
								ndistfound=true;
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
				}
				ndistsum += ndisthere;
				
			}
   		}
   			
		if (ndistmax!=0){
			nres[0] = ((float)ndistsum/((float)ndistmax))*(maxdist+1);
		} else nres[0] = 0;
		if (wordcount!=0){
			nres[1] = ((float)wordcorrect/(float)wordcount);
		} else nres[1] = 0;
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
