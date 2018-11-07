package com.fyp.cgp.functions;

public class FunctionEndIf implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		if((StatusEnum)inputs[3] != StatusEnum.BROKEN){
			inputs[3] = StatusEnum.START;
		}
	}

	@Override
	public String getFunctionName(){
		return "<endif>";
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
