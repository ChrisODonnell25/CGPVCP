package com.fyp.cgp.functions;

public class RegressionFunctions implements GenericFunctions {

    public Object callFunction(Object[] inputs, int function){
        switch (function){
            case 0: return (double)inputs[0] + (double)inputs[1];
            case 1: return (double)inputs[0] - (double)inputs[1];
            case 2: return (double)inputs[0] * (double)inputs[1];
            case 3:
                if((double)inputs[1] == 0){
                    //throw new ArithmeticException("Don't divide by zero.");
                    return 1.0;
                }
                return (double)inputs[0] / (double)inputs[1];
            case 4: return Math.sin((double)inputs[0]);
            default: throw new IllegalArgumentException("Invalid function.");
        }
    }

    public String getFunctionName(int function){
        switch (function){
            case 0: return "+";
            case 1: return "-";
            case 2: return "*";
            case 3: return "/";
            case 4: return "sin";
            default: throw new IllegalArgumentException("Invalid function.");
        }
    }
    public int getFunctionArity(int function) {
        switch (function) {
            case 0:
                return 2;
            case 1:
                return 2;
            case 2:
                return 2;
            case 3:
                return 2;
            case 4:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid function.");
        }
    }

    public int getNumberOfFunctions(){
        return 5;
    }
}
