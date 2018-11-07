package com.fyp.cgp.functions;

public class FunctionOr implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		StatusEnum status = (StatusEnum)inputs[3];
		if(status == StatusEnum.IF_CONDITION_TRUE){
			inputs[3] = StatusEnum.OR_PREVIOUS_TRUE;
		}
		else if(status == StatusEnum.IF_CONDITION_FALSE){
			inputs[3] = StatusEnum.OR_PREVIOUS_FALSE;
		}
	}

	@Override
	public String getFunctionName(){
		return "<or>";
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
