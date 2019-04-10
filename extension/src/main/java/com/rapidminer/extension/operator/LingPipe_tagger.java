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
import com.rapidminer.tools.LogService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import com.aliasi.hmm.HiddenMarkovModel;
import com.aliasi.hmm.HmmDecoder;
import com.aliasi.tag.Tagging;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import com.aliasi.util.Streams;

public class LingPipe_tagger extends Operator {
	
	private InputPort docInput = getInputPorts().createPort("Document In", IOObject.class);
    private OutputPort tagStringOutput = getOutputPorts().createPort("TagString out");
    private OutputPort docOutput = getOutputPorts().createPort("Document out");
	
	public LingPipe_tagger(OperatorDescription description) {

        super(description);
    }
	
	@Override
    public void doWork() throws OperatorException {
		
		Document iooDoc =(Document) docInput.getData(IOObject.class);
	    String in = iooDoc.getTokenText();
	    char[] cs = in.toCharArray();
		
		 TokenizerFactory TOKENIZER_FACTORY
	    = new RegExTokenizerFactory("(-|'|\\d|\\p{L})+|\\S");
		 
		 
		
		
		
		Path temp = null;
		try {
			temp = Files.createTempFile("resource-", ".ext");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogService.getRoot().log(Level.INFO, e.getMessage());
		}
		try {
			Files.copy(this.getClass().getResourceAsStream("lingpipe/pos-en-general-brown.HiddenMarkovModel"), temp, StandardCopyOption.REPLACE_EXISTING);
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
		Streams.closeInputStream(objIn);
		 HmmDecoder decoder = new HmmDecoder(hmm);
		 
		 
		Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(cs, 0 , cs.length);
	    String[] tokens = tokenizer.tokenize();
	    List<String> tokenList = Arrays.asList(tokens);
	    
	    Tagging<String> tagging = decoder.tag(tokenList);
	    
	   
	    
	    //create TagString
	    TagString out = new TagString();
	    out.setType(TagsetType.PENN_TREEBANK);
	    for (int i = 0; i < tagging.size(); ++i){
	    	out.addTag(tagging.tag(i));
	    }
	    
	    //create Doc
	    String strOut = "";
	    for (int i = 0; i < tagging.size(); ++i){
	    	strOut += tagging.token(i) + " " + tagging.tag(i) + " ";
	    }
	    Document docOut = new Document(strOut);
	    
	    docOutput.deliver(docOut);
	    tagStringOutput.deliver(out);
		
	}

	
}
