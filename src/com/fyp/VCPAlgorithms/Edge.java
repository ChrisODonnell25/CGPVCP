package com.fyp.VCPAlgorithms;

import java.util.HashSet;

public class Edge{
	private int pointA, pointB;
	private boolean covered;

	public Edge(int pointA, int pointB){
		this.pointA = pointA;
		this.pointB = pointB;
	}

	public int getPointA(){
		return pointA;
	}

	public int getPointB(){
		return pointB;
	}

	//TODO:
	public boolean isCovered(HashSet<Integer> cover){
		if(cover.contains(pointA) && cover.contains(pointB)){
			return true;
		}
		else{
			return false;
		}
	}
}
