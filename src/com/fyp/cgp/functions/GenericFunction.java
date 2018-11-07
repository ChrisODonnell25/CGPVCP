package com.fyp.cgp.functions;

import java.util.HashSet;

public interface GenericFunction{

	public void callFunction(Object[] inputs);
	public String getFunctionName();
	public int getFunctionArity();
	public boolean isIf();
}
