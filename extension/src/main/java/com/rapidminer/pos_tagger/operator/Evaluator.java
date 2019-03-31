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
import com.rapidminer.pos_tagger.ioobjects.PennTag;
import com.rapidminer.pos_tagger.ioobjects.ResultRow;
import com.rapidminer.pos_tagger.ioobjects.Tagset;
import com.rapidminer.pos_tagger.ioobjects.resultobj;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Ontology;

import javax.xml.stream.events.Attribute;


public class Evaluator extends Operator{
    private InputPort GoldInput = getInputPorts().createPort("Gold In", resultobj.class);
    private InputPort ResultInput = getInputPorts().createPort("Result In", resultobj.class);
    private OutputPort EvalOutput = getOutputPorts().createPort("Eval");
    //TODO Limitless Ports (Result [x] in)?

    public Evaluator(OperatorDescription description) {

        super(description);
    }

    @Override
    public void doWork() throws OperatorException {
        //TODO read in Gold object
    	resultobj gold = GoldInput.getData(resultobj.class);
    	//TODO read in other objects
    	resultobj input = ResultInput.getData(resultobj.class);
        
    	//determine whether sentences/rows were split correctly
    	boolean splitCorrect = true;
    	if (gold.countRows()!=input.countRows())
    		splitCorrect= false;
        
    	int wordcount=0;
    	int correctTags=0;
    	
    	if (gold.getType()==input.getType()){
    		List<ResultRow> goldData = gold.getContent();
    		List<ResultRow> inputData = input.getContent();
    		
    		
    	//EVALUATE PRECISION
    		if (splitCorrect){
    			//it is assumed the line-breaking Tags were identified correctly => line-by-line comparison 
    			// to avoid word-sequencing errors
    	
    			for (int i=0; i<goldData.size(); i++){
    				List<String> goldRow = goldData.get(i).get();
    				List<String> inputRow = inputData.get(i).get();
    				int max= java.lang.Math.max(goldRow.size(), inputRow.size());
    				
    				for (int j=0; j<max; j++){
    					wordcount++;
    					
    					switch (gold.getType()){
    					case UNDEFINED: break;
    					case PENN_TREEBANK: 
    						if(PennTag.findTag(inputRow.get(j))==PennTag.findTag(inputRow.get(j)) 
    							&& PennTag.findTag(inputRow.get(j))!=PennTag.None)
    							correctTags++;
    						break;
    					default: break;
    					}
    					
    				}
    				
    				
    				
    			}
    		} else {
    		//something went wrong with line-splitting
    		// => pray there are no word-sequencing errors
    		// TODO
    		
    		}
    	}
    		//TODO Eval
    	float Precision = ((float)correctTags)/((float)wordcount);
    	

    }
    
   
    
    public String[] superclass(String[] in){
    	return null;
    }

}
