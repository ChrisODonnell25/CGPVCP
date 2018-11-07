package com.fyp.cgp.functions;

import java.util.HashSet;

public class FunctionAddU implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		HashSet<Integer> cover = (HashSet<Integer>) inputs[0];
		StatusEnum status = (StatusEnum)inputs[3];
		if(
				status == StatusEnum.START ||
				status == StatusEnum.IF_CONDITION_TRUE
				){
			int u = (int) inputs[1];
			cover.add(u);
		}
	}

	@Override
	public String getFunctionName(){
		return "<add_vertex_u_to_cover>";
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
