package com.fyp.cgp.functions;

import java.util.HashSet;

public class FunctionAddV implements GenericFunction{
	@Override
	public void callFunction(Object[] inputs){
		HashSet<Integer> cover = (HashSet<Integer>) inputs[0];
		int v = (int)inputs[2];
		cover.add(v);
	}

	@Override
	public String getFunctionName(){
		return "<add_vertex_v_to_cover>";
	}

	@Override
	public int getFunctionArity(){
		return 1;
	}
}
