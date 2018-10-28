package com.fyp.cgp.functions;

import java.util.HashSet;

public class VCPFunctions implements GenericFunctions{

	private HashSet<Integer> cover;

	public VCPFunctions(HashSet<Integer> cover){
		this.cover = cover;
	}

	@Override
	public Object callFunction(Object[] inputs, int function){
		switch(function){
			case 0:
				//add (Integer)Object[0] to cover
				break;
			case 1:
				break;
			default:
				break;
		}
		return null;
	}

	@Override
	public String getFunctionName(int function){
		switch(function){
			case 0:
				return "<vcp_add_vertex_v_to_cover>";
			case 1:
				return "<vcp_add_vertex_u_to_cover>";
			case 2:
				return "<if_vertex_v_not_in_cover>";
			case 3:
				return "<if_equiprobability>";
			case 4:
				return "<if_v_greater_than_u>";
			case 5:
				return "if_degree(v)_<_degree(u)_||_degree(v)_==_degree(u)_&&_v_>_u";
			default: throw new IllegalArgumentException("Invalid function.");
		}
	}

	@Override
	public int getFunctionArity(int function){
		return 0;
	}

	@Override
	public int getNumberOfFunctions(){
		return 0;
	}
}
