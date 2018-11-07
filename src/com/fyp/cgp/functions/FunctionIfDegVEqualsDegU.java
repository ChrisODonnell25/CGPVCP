package com.fyp.cgp.functions;

import java.util.ArrayList;

public class FunctionIfDegVEqualsDegU implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		StatusEnum status = (StatusEnum) inputs[3];
		ArrayList<ArrayList<Integer>> graph = (ArrayList<ArrayList<Integer>>) inputs[4];
		int v = (int)inputs[2];
		int u = (int)inputs[1];
		if(
				status == StatusEnum.START ||
				status == StatusEnum.IF_CONDITION_TRUE ||
				status == StatusEnum.AND_PREVIOUS_TRUE ||
				status == StatusEnum.OR_PREVIOUS_FALSE

				){
			if(graph.get(v).size() == graph.get(u).size()){
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
		return "<if_deg(v)_==_deg(u)>";
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
