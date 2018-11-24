package com.rapidminer.pos_tagger.operator;

import java.util.logging.Level;

import com.rapidminer.example.Attributes;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ResultObjectAdapter;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.pos_tagger.ioobjects.textobj;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;


import edu.stanford.nlp.tagger.maxent.MaxentTagger;

//import javax.xml.stream.events.Attribute;


public class stanford_tagger extends Operator{
    private InputPort StringInput = getInputPorts().createPort("Text In");
    private OutputPort exampleSetOutput = getOutputPorts().createPort("out 1");
    private MaxentTagger tagger;
    
    public stanford_tagger(OperatorDescription description) {
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
    	String in = StringInput.getData(textobj.class).getContent();
    	
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: Read-in complete:");
        LogService.getRoot().log(Level.INFO, "input: " + in);
        
        
        //Handover to SFT
        String tagged = tagger.tagString(in);
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: Tagging complete: ");
        LogService.getRoot().log(Level.INFO, "tagged: " + tagged);
        
        //TODO handle result &
        //parse file into output format
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: Output-Format parsed");
        
        exampleSetOutput.deliver(null);
        LogService.getRoot().log(Level.INFO, "Stanford-Tagger: DONE");
    }

}
