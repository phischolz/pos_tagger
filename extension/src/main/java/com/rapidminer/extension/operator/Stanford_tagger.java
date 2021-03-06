package com.rapidminer.extension.operator;

import java.nio.file.Path;
import java.nio.file.Paths;
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



/**
 * POS-Tagger by Stanford. Can't be run due to Permission Issues.
 * If you work on this Operator and want to test it, uncomment its entry in com.rapidminer.extension.resources : Operatorspostagger.xml
 * @author Philipp Scholz, Uni Bayreuth
 *
 */
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
    	
    	Path path = Paths.get(".");
    	LogService.getRoot().log(Level.INFO, path.toString());
    	
    	
    	
        try {
        	tagger = new MaxentTagger(this.getClass().getResourceAsStream("/penn/english-left3words-distsim.tagger"));
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
