package com.rapidminer.pos_tagger.operator;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.text.Document;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.pos_tagger.ioobjects.*;
import com.rapidminer.tools.LogService;


import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.template.node.NLPNode;
import edu.emory.mathcs.nlp.decode.NLPDecoder;


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
            "C:\\Users\\phili\\Documents\\GitHub\\pos_tagger\\external\\configs\\config-decode-en-pos.xml",
            false));
        return types;
    }
    
    @Override
    public void doWork() throws OperatorException {
        
        
        
        
    	
    	
        //Input Read-In
        Document iooDoc =(Document) docInput.getData(IOObject.class);
        String in = iooDoc.getTokenText();
        
        
        
        //Decoder Configuration and Instantiation TODO: Configuration-Files inside resources
        String configurationFile = getParameterAsString(PARAMETER_TEXT);;
        LogService.getRoot().log(Level.INFO, "ConfigFile: "+ configurationFile);
		NLPDecoder decoder = new NLPDecoder(IOUtils.getInputStream(configurationFile));
		
		//Tagging Process
		NLPNode[] nodes = decoder.decode(in);
		
		
        //Grab result file (POS-Array)
		List<String> result = new ArrayList<String>();
		for (NLPNode node: nodes) {
			if (node.getPartOfSpeechTag()!= null) result.add(node.getPartOfSpeechTag());
		}
		result.remove(0);
		
		
       
		//parse result file into @TagString format
		TagString out = new TagString();
		out.setType(TagsetType.PENN_TREEBANK);
		for (String tag: result){out.addTag(tag);}
		
		
		
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
