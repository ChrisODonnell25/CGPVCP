package com.fyp.cgp.functions;

public interface GenericFunctions {
    public Object callFunction(Object[] inputs, int function);
    public String getFunctionName(int function);
    public int getFunctionArity(int function);
    public int getNumberOfFunctions();
}
