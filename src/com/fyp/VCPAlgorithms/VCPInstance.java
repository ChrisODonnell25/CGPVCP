package com.fyp.VCPAlgorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class VCPInstance{
	private ArrayList<ArrayList<Integer>> graph;
	private ArrayList<Edge> edges;
	private int minVCSize;

	public VCPInstance(File input){
		graph = new ArrayList<>();
		edges = new ArrayList<>();
		initialise(input);
	}

	private void initialise(File input){
		Scanner sc = null;
		try{
			sc = new Scanner(input);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		sc.next();
		sc.next();
		int numberOfVertices = sc.nextInt();
		for(int i = 0; i < numberOfVertices; i++){
			graph.add(new ArrayList<>());
		}
		int numberOfEdges = sc.nextInt();
//		minVCSize = sc.nextInt();
		while (sc.hasNext()){
			sc.next();
			int vertexOne = sc.nextInt()-1;
			int vertexTwo = sc.nextInt()-1;
			graph.get(vertexOne).add(vertexTwo);
			if(!graph.get(vertexTwo).contains(vertexOne)){
				graph.get(vertexTwo).add(vertexOne);
			}
			edges.add(new Edge(vertexOne, vertexTwo));
		}
	}

	public ArrayList<ArrayList<Integer>> getGraph(){
		return graph;
	}

	public ArrayList<Edge> getEdges(){
		return edges;
	}

	public int getMinVCSize(){
		return minVCSize;
	}
}
