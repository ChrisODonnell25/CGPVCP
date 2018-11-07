package com.fyp.cgp.functions;

public class FunctionAnd implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		StatusEnum status = (StatusEnum)inputs[3];
		if(status == StatusEnum.IF_CONDITION_TRUE){
			inputs[3] = StatusEnum.AND_PREVIOUS_TRUE;
		}
		else if(status == StatusEnum.IF_CONDITION_FALSE){
			inputs[3] = StatusEnum.AND_PREVIOUS_FALSE;
		}
	}

	@Override
	public String getFunctionName(){
		return "<and>";
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
