package com.rapidminer.extension.operator;

import com.rapidminer.extension.ioobjects.TagString;
import com.rapidminer.extension.ioobjects.TagsetType;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.LogService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.ScoredTagging;
import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import com.aliasi.util.Streams;

public class LingPipe_tagger extends Operator {
	
	private static final String PARAMETER_NBEST = "Max N-Best";
	private static final String PARAMETER_MODELS = "Hidden Markov Model";
	private InputPort docInput = getInputPorts().createPort("Document In", IOObject.class);
    private OutputPort tagStringOutput = getOutputPorts().createPort("TagString out");
    private OutputPort docOutput = getOutputPorts().createPort("Document out");
	
	public LingPipe_tagger(OperatorDescription description) {

        super(description);
    }
	
	@Override
    public List<ParameterType> getParameterTypes(){
        List<ParameterType> types = super.getParameterTypes();

        types.add(new ParameterTypeInt(
            PARAMETER_NBEST,
            "This Tagger can determine the n most likely choices for a tag. Leave at 1 for regular Tagging",
            1,
            20,
            5,
            false));
        
        String[] categories = {"Brown Corpus (english general)", "GENIA Corpus (English Biomedical)", "MedPost Corpus (English Biomedical)"};
        types.add(new ParameterTypeCategory(
        		PARAMETER_MODELS, 
        		"Differently trained models are available. They perform better on their respective writing styles. Choose the Corpus, which the HMM should be trained on.", 
        		categories, 
        		0,
        		false));
        return types;
    }
	
	@Override
    public void doWork() throws OperatorException {
		
		Document iooDoc =(Document) docInput.getData(IOObject.class);
	    String in = iooDoc.getTokenText();
	    char[] cs = in.toCharArray();
		
		 TokenizerFactory TOKENIZER_FACTORY
	    = new RegExTokenizerFactory("(-|'|\\d|\\p{L})+|\\S");
		 
		String filepath;
		switch (getParameterAsInt(PARAMETER_MODELS)){
		case 0:
			filepath = "/lingpipe/pos-en-general-brown.HiddenMarkovModel";
			break;
		case 1:
			filepath = "/lingpipe/pos-en-bio-genia.HiddenMarkovModel";
			break;
		case 2:
			filepath = "/lingpipe/pos-en-bio-medpost.HiddenMarkovModel";
			break;
		default:
			filepath = "/lingpipe/pos-en-general-brown.HiddenMarkovModel";
			break;
		}
		 
		
		
		//Grab HMM
		Path temp = null;
		try {
			temp = Files.createTempFile("resource-", ".ext");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogService.getRoot().log(Level.INFO, e.getMessage());
		}
		try {
			Files.copy(this.getClass().getResourceAsStream(filepath), temp, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogService.getRoot().log(Level.INFO, e.getMessage());
		}
		FileInputStream fileIn = null;
		try {
			fileIn = new FileInputStream(temp.toFile());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			LogService.getRoot().log(Level.INFO, e.getMessage());
		}
			 
		 
		ObjectInputStream objIn = null;
		try {
			objIn = new ObjectInputStream(fileIn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogService.getRoot().log(Level.INFO, e.getMessage());
		}
		HiddenMarkovModel hmm = null;
		try {
			hmm = (HiddenMarkovModel) objIn.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			LogService.getRoot().log(Level.INFO, e.getMessage());
		}
		try {
			objIn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Create HMM-Decoder
		HmmDecoder decoder = new HmmDecoder(hmm);
		 
		//Tokenization (Destroys any previous Tokenization!
		Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs, 0 , cs.length);
	    String[] tokens = tokenizer.tokenize();
	    List<String> tokenList = Arrays.asList(tokens);
	    
	    
	    //Decoding
	    Iterator<ScoredTagging<String>> nBestIt = decoder.tagNBest(tokenList,getParameterAsInt(PARAMETER_NBEST));
	    
	    //Transformation into better Format
	    String[][][] tags = new String[tokenList.size()][getParameterAsInt(PARAMETER_NBEST)][2];
	    for (int n = 0; n < getParameterAsInt(PARAMETER_NBEST) && nBestIt.hasNext(); ++n){
	    	ScoredTagging<String> scoredTagging = nBestIt.next();
	    	
	    	// tagging confidence for a line can be grabbed like this:
		    // double score = scoredTagging.score();
	    	
	    	for (int i = 0; i < tokenList.size(); ++i){
	    		tags[i][n][0] = scoredTagging.token(i);
	            tags[i][n][1] = scoredTagging.tag(i);
	        }
	    }
	    
	    
	    
	    //Output Format creation
	    TagString out = new TagString();
	    out.setType(TagsetType.PENN_TREEBANK);
	    out.setNbest(getParameterAsInt(PARAMETER_NBEST));
	    
	    String strOut = "";
	    
	    
	    for (String[][] tag: tags){
	    	strOut += tag[0][0] + "\\" + tag[0][1] + " ";
	    }
	    for (String[][] line: tags){
	    	String[] s = new String[line.length];
	    	for (int i = 0 ; i<line.length; i++){
	    		s[i]=line[i][1];
	    	}
	    	out.addTag(s);
	    }
	        
	    
	   
	    
	   
	    //Output
	    Document docOut = new Document(strOut);
	    
	    docOutput.deliver(docOut);
	    tagStringOutput.deliver(out);
		
	}

	
}
