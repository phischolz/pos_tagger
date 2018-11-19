package com.rapidminer.pos_tagger.operator;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.ExampleTable;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;

import javax.xml.stream.events.Attribute;


public class evaluator extends Operator{
    private InputPort GoldInput = getInputPorts().createPort("Gold In");
    private InputPort ResultInput = getInputPorts().createPort("Result In");
    private OutputPort EvalOutput = getOutputPorts().createPort("Eval");
    //TODO Limitless Ports (Result [x] in)?

    public evaluator(OperatorDescription description) {

        super(description);
    }

    @Override
    public void doWork() throws OperatorException {
        //TODO read in Gold object
    	//TODO read in other objects
        //ExampleSet exampleSet = exampleSetInput.getData(ExampleSet.class);
        //TODO convert back from IOObj Into Matrix/List Objects
        
        //TODO EVALUATE PRECISION
    	
    	//TODO Eval-ExampleSet Creation
    	List<com.rapidminer.example.Attribute> listOfAtts = new LinkedList<>();
        ExampleSet result;
        com.rapidminer.example.Attribute nameColumn = AttributeFactory.createAttribute("Input Identifier", 
                Ontology.ATTRIBUTE_VALUE_TYPE.STRING);
        listOfAtts.add(nameColumn);
        com.rapidminer.example.Attribute precisionColumn = AttributeFactory.createAttribute("Precision",
                Ontology.ATTRIBUTE_VALUE_TYPE.NUMERICAL);
        listOfAtts.add(precisionColumn);
        
        ExampleTable table = new MemoryExampleTable(listOfAtts);
        
        
        //TODO Fill table with Eval Results. Pairs of [INPUT NAME] and corresponding [PRECISION]. Gold has 100% per default.
        
        LogService.getRoot().log(Level.INFO, "Doing something...");
        result=table.createExampleSet();
        // output finished Set
        EvalOutput.deliver(result);
    }

}
