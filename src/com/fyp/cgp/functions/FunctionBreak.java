package com.fyp.cgp.functions;

import java.util.HashSet;

public class FunctionBreak implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		inputs[3] = StatusEnum.BROKEN;
	}

	@Override
	public String getFunctionName(){
		return "<break>";
	}

	@Override
	public int getFunctionArity(){
		return 1;
	}

	@Override
	public boolean isIf(){
		return false;
	}
}
