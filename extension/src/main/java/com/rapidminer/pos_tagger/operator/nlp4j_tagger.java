package com.rapidminer.pos_tagger.operator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import com.rapidminer.pos_tagger.ioobjects.TagsetType;
import com.rapidminer.pos_tagger.ioobjects.resultobj;
import com.rapidminer.pos_tagger.ioobjects.textobj;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

import javax.xml.stream.events.Attribute;


public class nlp4j_tagger extends Operator{
    private InputPort StringInput = getInputPorts().createPort("Text In");
    private OutputPort exampleSetOutput = getOutputPorts().createPort("out 1");

    public nlp4j_tagger(OperatorDescription description) {
        super(description);
        // nothing here
    }

    @Override
    public void doWork() throws OperatorException {
        //read in
    	String in = StringInput.getData(textobj.class).getContent();
        LogService.getRoot().log(Level.INFO, "NLP4J: Read-in");
        
        //Handover to NLP4J
        //TODO handover model file
        
       
        
      
        
        final String configurationFile = "C:\\Users\\phili\\Documents\\GitHub\\pos_tagger\\external\\configs\\config-decode-en-pos.xml";
        LogService.getRoot().log(Level.INFO, "ConfigFile: "+ configurationFile);
		NLPDecoder decoder = new NLPDecoder(IOUtils.getInputStream(configurationFile));
		LogService.getRoot().log(Level.INFO, "Decoder init'd");
		
		LogService.getRoot().log(Level.INFO, "input: " + in);
		NLPNode[] nodes = decoder.decode(in);
		LogService.getRoot().log(Level.INFO, decoder.toString(nodes));
		
        //Grab result file (POS-Array)
		String[] result = new String[nodes.length];
		for (int i = 0; i<nodes.length ; i++) {
			result[i] = nodes[i].getPartOfSpeechTag();
		}
		
		
        //parse result file into output format
		
		resultobj out = new resultobj(TagsetType.PENN_TREEBANK);
		for (String tag: result){out.addTag(tag);}
        exampleSetOutput.deliver(out);
    }

}
