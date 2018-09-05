package com.rapidminer.extension.operator;

import java.util.logging.Level;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.LogService;


public class tester extends Operator{
    public tester(OperatorDescription description) {
        super(description);
    }

    @Override
    public void doWork() throws OperatorException {
        LogService.getRoot().log(Level.INFO, "Doing something...");
    }

}
