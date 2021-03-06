package com.rapidminer.extension.operator;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.rapidminer.extension.ioobjects.*;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.text.Document;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.tools.LogService;


import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.component.tokenizer.token.Token;
import edu.emory.mathcs.nlp.decode.NLPDecoder;

/**
 * Operator for POS-Tagging. 
 * for Setup see Project README
 * @author Philipp Scholz, Uni Bayreuth
 * Input: Tokenized Document.
 * Ouput: Tagged tagString and Document.
 */
public class Nlp4j_tagger extends Operator{
    private InputPort docInput = getInputPorts().createPort("Document In", IOObject.class);
    private OutputPort tagStringOutput = getOutputPorts().createPort("TagString out");
    private OutputPort docOutput = getOutputPorts().createPort("Document out");
    public static final String PARAMETER_TEXT = "config path";

    public Nlp4j_tagger(OperatorDescription description) {
        super(description);
        // nothing here
    }

    @Override
    public List<ParameterType> getParameterTypes(){
        List<ParameterType> types = super.getParameterTypes();

        types.add(new ParameterTypeString(
            PARAMETER_TEXT,
            "Add the full File-Path of the config you want to read here.",
            "[ABSOLUTE_PATH_TO]/pos_tagger/external/configs/config-decode-en-pos.xml",
            false));
        return types;
    }
    
    @Override
    public void doWork() throws OperatorException {
        
        
        
        
    	
    	
        //Input Read-In
        Document iooDoc =(Document) docInput.getData(IOObject.class);
        String in = iooDoc.getTokenText();
        String[] inTokens = in.split("\\s+");
        List <Token> tokens = new ArrayList<Token>();
		for(String s: inTokens){
		Token tok = new Token(s);
		tokens.add(tok);
		}
        
        
        
        //Decoder Configuration and Instantiation TODO: Configuration-Files inside resources
        String configurationFile = getParameterAsString(PARAMETER_TEXT);;
        LogService.getRoot().log(Level.INFO, "ConfigFile: "+ configurationFile);
		NLPDecoder decoder = new NLPDecoder(IOUtils.getInputStream(configurationFile));
		
		//Tagging Process
		
		NLPNode[] inputNodes = decoder.toNodeArray(tokens);
		NLPNode[] nodes = decoder.decode(inputNodes);
		
		
        //Grab result file (POS-Array)
		List<String> result = new ArrayList<String>();
		for (NLPNode node: nodes) {
			if (node.getPartOfSpeechTag()!= null) result.add(node.getPartOfSpeechTag());
		}
		result.remove(0);
		
		
       
		//parse result file into @TagString format
		TagString out = new TagString(1, TagsetType.PENN_TREEBANK);
		
		if (result.size()==tokens.size()){
			for (int i=0; i<result.size(); i++){
				
				out.addTag(tokens.get(i).getWordForm(), result.get(i));
			}
    	} else{
    		LogService.getRoot().log(Level.SEVERE, "ERROR (NLP4J): Tokenstring and Tagstring are not of identical length");
    		throw new OperatorException("ERROR (NLP4J): Tokenstring and Tagstring are not of identical length");
    	}
		
		
		
		String strOut = "";
		for (NLPNode node: nodes){
			strOut += node.getWordForm() + "\\" + node.getPartOfSpeechTag() + " ";
		}
		
		
		//Deliver String (Document)
		Document outDoc = new Document(strOut);
        docOutput.deliver(outDoc);
        
        //Deliver TagString
        tagStringOutput.deliver(out);
		

    }
}
