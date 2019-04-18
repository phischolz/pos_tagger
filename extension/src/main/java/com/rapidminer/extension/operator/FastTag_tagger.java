package com.rapidminer.extension.operator;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

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

import fasttag.src.com.knowledgebooks.nlp.fasttag.FastTag;

public class FastTag_tagger extends Operator{
    private InputPort docInput = getInputPorts().createPort("Document In", IOObject.class);
    private OutputPort tagStringOutput = getOutputPorts().createPort("TagString out");
    private OutputPort docOutput = getOutputPorts().createPort("Document out");

    
    
    public FastTag_tagger(OperatorDescription description) {
        super(description);
        // nothing here
    }
    
    @Override
    public void doWork() throws OperatorException{
    	
    	Document iooDoc = (Document) docInput.getData(IOObject.class);
    	String in = iooDoc.getTokenText();
    	List<String> list = Arrays.asList(in.split("\\s+"));
    	
    	//decode
    	List<String> tagged = FastTag.tag(list);
    	for (int i=0; i<tagged.size(); i++){
    			tagged.set(i,  tagged.get(i).replaceAll("^", ""));
    	}
    	
    	//parse tagString
    	TagString out = new TagString(1, TagsetType.PENN_TREEBANK);
    	
    	if(list.size()==tagged.size()){
    		for (int i=0; i<tagged.size(); i++){
    			out.addTag(list.get(i), tagged.get(i));
    		}
    	} else {
    		LogService.getRoot().log(Level.SEVERE, "ERROR (FastTag): Tokenstring and Tagstring are not of identical length");
    		throw new OperatorException("ERROR (FastTag): Tokenstring and Tagstring are not of identical length");
    	}
    	
    	tagStringOutput.deliver(out);
    	
    	//parse Document
    	String strOut = "";
    	int min = java.lang.Math.min(list.size(), tagged.size());
    	int max = java.lang.Math.max(list.size(), tagged.size());
    	for (int i=0; i<min; i++){
    		strOut += list.get(i) + "\\" + tagged.get(i) + " ";
    	}
    	if (min<max){
    		if (list.size()>tagged.size()){
    			for (int i = min; min<max; i++){
    				strOut += list.get(i) + "\\NN ";
    			}
    		} else {
    			for (int i = min; min<max; i++){
    				strOut += "null\\" + tagged.get(i) + " ";
    			}
    		}
    		
    	}
    	Document docOut = new Document (strOut);
    	docOutput.deliver(docOut);
    }
    
}
