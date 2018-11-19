package com.rapidminer.pos_tagger.operator;

import java.util.List;
import java.util.logging.Level;

import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.studio.io.data.*;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.pos_tagger.ioobjects.textobj;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;

import javax.xml.stream.events.Attribute;
import java.io.*;


public class pos_txtreader extends Operator{
	public static final String PARAMETER_TEXT = "file path";
    private OutputPort stringOutput = getOutputPorts().createPort("out 1");

    public pos_txtreader(OperatorDescription description) {

        super(description);
    }
    
    @Override
    public List<ParameterType> getParameterTypes(){
        List<ParameterType> types = super.getParameterTypes();

        types.add(new ParameterTypeString(
            PARAMETER_TEXT,
            "Add the full File-Path of the file you want to read here.",
            "C:\\Users\\phili\\Documents\\samples\\sample.txt",
            false));
        return types;
    }

    @Override
    public void doWork() throws OperatorException {
    	
    	//TODO Parameter Type File?
       
        //read in specified .txt file
        File file = new File(getParameterAsString(PARAMETER_TEXT));
        LogService.getRoot().log(Level.INFO, "File Path valid");
        
        
        //buffered reader init
        BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			LogService.getRoot().log(Level.INFO, "ERROR: Failed to find File!");
			e.printStackTrace();
		}
		LogService.getRoot().log(Level.INFO, "File found");
       
        //read that stuff, bruh
        textobj res = new textobj("");
        String st; 
        try {
			while ((st = br.readLine()) != null) 
			  res.append(st);
		} catch (IOException e1) {
			LogService.getRoot().log(Level.INFO, "ERROR: Failed to read Line");
			e1.printStackTrace();
		}
        try {
			br.close();
		} catch (IOException e) {
			LogService.getRoot().log(Level.INFO, "ERROR: Failed to close File Reader");
		}
        
        // output finished Set
        stringOutput.deliver(res);
    }

}
