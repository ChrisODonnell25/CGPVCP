package com.fyp.VCPAlgorithms;

import java.util.HashSet;

public class Edge{
	private int pointA, pointB;
	private boolean covered = false;

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

	public boolean isCovered(HashSet<Integer> cover){
		if(cover.contains(pointA) && cover.contains(pointB)){
			covered = true;
			return true;
		}
		else{
			return false;
		}
	}
}
