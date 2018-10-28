package com.fyp.VCPAlgorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Algorithms{
	private ArrayList<ArrayList<Integer>> graph;
	private HashSet<Integer> cover;
	private ArrayList<Edge> edges;

	public Algorithms(ArrayList<ArrayList<Integer>> graph, ArrayList<Edge> edges){
		this.graph = graph;
		cover = new HashSet<>();
		this.edges = edges;
	}

	//LR
	public void LR(){
		int index = 0; //same everywhere
		while(!isCovered()){ //same everywhere
			if(!cover.contains(index)){ //same everywhere
				for(int v : graph.get(index)){ //same everywhere
					if(!cover.contains(v)){
						cover.add(v);
					}
				}
			}
			index++; //same everywhere
		}
	}
	//ED
	public void ED(){
		int index = 0; //same everywhere
		while(!isCovered()){ //same everywhere
			if(!cover.contains(index)){ //same everywhere
				for(int v : graph.get(index)){ //same everywhere
					if(!cover.contains(v)){
						cover.add(index);
						cover.add(v);
					}
				}
			}
			index++; //same everywhere
		}
	}

	//S-Pitt
	public void SPitt(){
		int index = 0; //same everywhere
		while(!isCovered()){ //same everywhere
			if(!cover.contains(index)){ //same everywhere
				for(int v : graph.get(index)){ //same everywhere
					if(!cover.contains(v)){
						if(Math.random() > 0.5){
							cover.add(index);
						}
						else{
							cover.add(v);
						}
					}
				}
			}
			index++; //same everywhere
		}
	}

	//LL
	public void LL(){
		int index = 0; //same everywhere
		while(!isCovered()){ //same everywhere
			if(!cover.contains(index)){ //same everywhere
				for(int v : graph.get(index)){ //same everywhere
					if(v > index){
						cover.add(index);
						break;
					}
				}
			}
			index++; //same everywhere
		}
	}

	//SLL
	public void SLL(){
		int index = 0; //same everywhere
		while(!isCovered()){ //same everywhere
			if(!cover.contains(index)){ //same everywhere
				for(int v : graph.get(index)){ //same everywhere
					if(degree(v) < degree(index) || degree(v) == degree(index) && v > index){
						cover.add(index);
						break;
					}
				}
			}
			index++; //same everywhere
		}
	}

	//ASLL
	public void ASLL(){
		int index = 0; //same everywhere
		while(!isCovered()){ //same everywhere
			if(!cover.contains(index)){ //same everywhere
				for(int v : graph.get(index)){ //same everywhere
					if(degree(v) > degree(index) || degree(v) == degree(index) && v < index){
						cover.add(index);
						break;
					}
				}
			}
			index++; //same everywhere
		}
	}

	public boolean isCovered(){ //same everywhere
		for (Edge e : edges) { //same everywhere
			if(!e.isCovered(cover)){ //same everywhere
				return false; //same everywhere
			}
		}
		return true; //same everywhere
	}


	public int degree(int vertex){
		return graph.get(vertex).size();
	}
	//TODO: use adjacency lists
}
