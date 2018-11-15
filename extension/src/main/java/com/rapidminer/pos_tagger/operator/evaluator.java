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


public class evaluator extends Operator{
    private InputPort exampleSetInput = getInputPorts().createPort("in 1");
    private OutputPort exampleSetOutput = getOutputPorts().createPort("out 1");

    public evaluator(OperatorDescription description) {

        super(description);
    }

    @Override
    public void doWork() throws OperatorException {
        //read in
        ExampleSet exampleSet = exampleSetInput.getData(ExampleSet.class);
        Attributes attributes=exampleSet.getAttributes();
        String newN = "zweiundvierzig";
        //create new Attr
        com.rapidminer.example.Attribute targetAttribute = AttributeFactory.createAttribute(newN, Ontology.INTEGER);
        targetAttribute.setTableIndex(attributes.size());
        exampleSet.getExampleTable().addAttribute(targetAttribute);
        attributes.addRegular(targetAttribute);
        //insert random values in Set
        for(Example example:exampleSet){
            example.setValue(targetAttribute, Math.round(Math.random()*10+0.5));
        }
        //def Table index of new attr

        LogService.getRoot().log(Level.INFO, "Doing something...");
        // output finished Set
        exampleSetOutput.deliver(exampleSet);
    }

}
