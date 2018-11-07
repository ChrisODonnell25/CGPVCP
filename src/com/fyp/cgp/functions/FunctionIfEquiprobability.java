package com.fyp.cgp.functions;

import java.util.HashSet;

public class FunctionIfEquiprobability implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		StatusEnum status = (StatusEnum) inputs[3];

		int v = (int)inputs[2];
		if(
				status == StatusEnum.START ||
				status == StatusEnum.IF_CONDITION_TRUE ||
				status == StatusEnum.AND_PREVIOUS_TRUE ||
				status == StatusEnum.OR_PREVIOUS_FALSE

				){
			if(Math.random() >= 0.5){
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
		return "<if_random_>=_0.5>";
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
