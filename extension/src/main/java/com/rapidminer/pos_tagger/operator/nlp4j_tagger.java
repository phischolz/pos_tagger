package com.rapidminer.pos_tagger.operator;

import java.util.logging.Level;

import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;


import javax.xml.stream.events.Attribute;


public class nlp4j_tagger extends Operator{
    private InputPort exampleSetInput = getInputPorts().createPort("in 1");
    private OutputPort exampleSetOutput = getOutputPorts().createPort("out 1");

    public nlp4j_tagger(OperatorDescription description) {
        super(description);
        // TODO init and load NLP4J-Algorithm
    }

    @Override
    public void doWork() throws OperatorException {
        //read in
        //TODO Read actual Text, add Preconditions and Casting
    	//drop text into file
        LogService.getRoot().log(Level.INFO, "NLP4J: Read-in");
        
        //Handover to NLP4J
        //Grab from result file
        //parse file into output format
        exampleSetOutput.deliver(null);
    }

}
