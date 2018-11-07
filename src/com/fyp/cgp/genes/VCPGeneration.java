package com.fyp.cgp.genes;

import com.fyp.VCPAlgorithms.Edge;
import com.fyp.VCPAlgorithms.VCPInstance;
import com.fyp.cgp.functions.GenericFunction;

import java.util.ArrayList;
import java.util.Random;

public class VCPGeneration extends GenericGeneration{
	private int columns;
	private int rows;
	private int levelsBack;
	private int numberOfInputs;
	private int numberOfOutputs;
	private GenericFunction[] functionList;
	private ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs;
	private ArrayList<ArrayList<Edge>> inputEdgeLists;
	private Integer[] outputData;
	private Random rand;
	private int generations;

	public VCPGeneration(
			int columns,
			int rows,
			int levelsBack,
			int inputs,
			GenericFunction[] functionList,
			ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs,
			ArrayList<ArrayList<Edge>> inputEdgeLists,
			Integer[] outputData,
			int generations
	){
		this.columns = columns;
		this.rows = rows;
		//this allows the user to say "no levels back constraint, connect to anything"
		if(levelsBack == 0){
			this.levelsBack = columns;
		}
		else{
			this.levelsBack = levelsBack;
		}
		this.numberOfInputs = inputs;
		this.numberOfOutputs = 1;
		this.functionList = functionList;
		this.inputGraphs = inputGraphs;
		this.inputEdgeLists = inputEdgeLists;
		this.outputData = outputData;
		rand = new Random();
		this.generations = generations;
		initialise();
	}

	@Override
	protected void initialise(){
		for (int i = 0; i < 5; i++){
			addIndividualToGeneration(new VCPIndividual(columns, rows, levelsBack, numberOfInputs, functionList, inputGraphs, inputEdgeLists, outputData, true));
		}
	}

	@Override
	public String run(){
		double time = System.currentTimeMillis();
		for (VCPIndividual individual:getGeneration()) {
			individual.decodeGenotype();
		}
		setBestIndividual();
		int generation = 0;
		//TODO stop conditions
		while(generation <= generations) {
			getGeneration().clear();
			VCPIndividual best = getBestIndividual();
			getGeneration().add(best);
			for (int i = 1; i < 5; i++) {
				VCPIndividual offSpring = new VCPIndividual(
						columns,
						rows,
						levelsBack,
						numberOfInputs,
						functionList,
						inputGraphs,
						inputEdgeLists,
						outputData,
						best.getGridCopy(),
						best.getStartList(),
						best.getStartPlusIfList(),
						best.getAddBreakList(),
						best.getIfList(),
						best.getOrAndList()
				);
				offSpring.mutate();
				offSpring.decodeGenotype();
				getGeneration().add(offSpring);
			}
			setBestIndividual();
			//            if (generation % 10000 == 0){
			//                System.out.println(generation);
			//                System.out.println(getBestIndividual().getExpressions() + " " + getBestIndividual().getFitness());
			//                getBestIndividual().printStuff();
			//                System.out.println();
			//
			//            }
			generation++;
			System.out.println(getBestIndividual().getFitness());
			System.out.println(getBestIndividual().getExpressions());
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Single run complete");
		//		System.out.println(generation);
		//		System.out.println(time);
		System.out.println(getBestIndividual().getExpressions() + " " + getBestIndividual().getFitness());
		//		getBestIndividual().printStuff();
		return generation + "," + time;
	}
}
