package com.fyp.cgp.functions;

import java.util.HashSet;

public class FunctionIfVLTU implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		StatusEnum status = (StatusEnum) inputs[3];

		int v = (int)inputs[2];
		int u = (int)inputs[1];
		if(
				status == StatusEnum.START ||
				status == StatusEnum.IF_CONDITION_TRUE ||
				status == StatusEnum.AND_PREVIOUS_TRUE ||
				status == StatusEnum.OR_PREVIOUS_FALSE

				){
			if(v < u){
				inputs[3] = StatusEnum.IF_CONDITION_TRUE;
			}
			else{
				inputs[3] = StatusEnum.IF_CONDITION_FALSE;
			}
		}
		else if(status == StatusEnum.OR_PREVIOUS_TRUE){
			inputs[3] = StatusEnum.IF_CONDITION_TRUE;
		}
		else if(status == StatusEnum.AND_PREVIOUS_FALSE){
			inputs[3] = StatusEnum.IF_CONDITION_FALSE;
		}
	}

	@Override
	public String getFunctionName(){
		return "<if_v_<_u>";
	}

	@Override
	public int getFunctionArity(){
		return 1;
	}

	@Override
	public boolean isIf(){
		return true;
	}
}
