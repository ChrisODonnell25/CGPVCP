package com.fyp.cgp.functions;

import java.util.HashSet;

public class FunctionElse implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		StatusEnum status = (StatusEnum)inputs[3];
		if(status == StatusEnum.IF_CONDITION_FALSE){
			inputs[3] = StatusEnum.IF_CONDITION_TRUE;
		}
	}

	@Override
	public String getFunctionName(){
		return "<else>";
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
