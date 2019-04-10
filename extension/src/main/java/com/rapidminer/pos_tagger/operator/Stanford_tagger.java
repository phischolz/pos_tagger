package com.rapidminer.pos_tagger.operator;

import java.util.logging.Level;


import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.text.Document;
import com.rapidminer.tools.LogService;



import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//import javax.xml.stream.events.Attribute;


public class Stanford_tagger extends Operator{
    private InputPort docInput = getInputPorts().createPort("Document in", IOObject.class);
    private OutputPort docOutput = getOutputPorts().createPort("Document out");
    private MaxentTagger tagger;
    
    public Stanford_tagger(OperatorDescription description) {
        super(description);
       
    }
    
    

    /*
     * (non-Javadoc)
     * Author: Philipp Scholz
     * Operator Task: House Stanford Tagger, hand it a @textobj and parse results into (?) Format. 
     */
    @Override
    public void doWork() throws OperatorException {
    	 // TODO init and load Stanford-Algorithm --
    	 // - Permission Issue!
        try {
        	tagger = new MaxentTagger("C:/Users/phili/Documents/GitHub/pos_tagger/extension/lib/models/stanford/stanford-left3words-distsim.tagger");
        } catch (Exception c) {LogService.getRoot().log(Level.INFO, "Stanford-Tagger: failed to setup");}

        //read in
    	Document docIn = (Document) docInput.getData(IOObject.class);
    	String in = docIn.getTokenText();
    	
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: Read-in complete:");
        LogService.getRoot().log(Level.INFO, "input: " + in);
        
        
        //Handover to SFT
        String tagged = tagger.tagString(in);
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: Tagging complete: ");
        LogService.getRoot().log(Level.INFO, "tagged: " + tagged);
        
        //TODO handle result &
        //parse file into output format
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: Output-Format parsed");
        
        docOutput.deliver(null);
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: DONE");
    }

}
