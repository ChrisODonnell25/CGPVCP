package com.fyp.cgp.genes;

import com.fyp.cgp.functions.GenericFunction;
import com.fyp.cgp.nodes.ConnectionNode;
import com.fyp.cgp.nodes.GenericNode;
import com.fyp.cgp.nodes.InputNode;
import com.fyp.cgp.nodes.OutputNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class VCPIndividual{
	private int columns;
	private int rows;
	private int levelsBack;
	private int numberOfInputs;
	private int numberOfOutputs;
	private double fitness;
	private GenericNode[] entireGrid;
	private int[] nodesUsed;
	private GenericFunction[] functionList;
	private int numberOfNodesUsed;
	private ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs;
	private HashSet<Integer>[] outputData;
	private HashSet<Integer>[] predictedOutput;
	private Random rand;

	VCPIndividual(int columns, int rows, int levelsBack, int inputs, int outputs, GenericFunction[] functionList, ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs, HashSet<Integer>[] outputData, boolean initFlag){
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
		this.numberOfOutputs = outputs;
		this.functionList = functionList;
		this.inputGraphs = inputGraphs;
		this.outputData = outputData;
		rand = new Random();
		if(initFlag){
			initialise();
		}
	}

	//this constructor takes a pre-existing grid essentially copying an individual, used for offspring creation
	VCPIndividual(int columns, int rows, int levelsBack, int inputs, int outputs, GenericFunction[] functionList, ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs, HashSet<Integer>[] outputData, GenericNode[] gridToCopy){
		this(columns, rows, levelsBack, inputs, outputs, functionList, inputGraphs, outputData, false);
		this.entireGrid = gridToCopy;
		nodesUsed = new int[entireGrid.length];
	}

	private void initialise(){
		entireGrid = new GenericNode[numberOfInputs + numberOfOutputs + rows * columns];
		int index = 0;
		nodesUsed = new int[entireGrid.length];

		//initialise inputs
		for(int input = 0; input < numberOfInputs; input++){
			entireGrid[index] = new InputNode(index);
			index++;
		}

		//initialise connections
		for(int col = 0; col < columns; col++){
			for(int row = 0; row < rows; row++){
				entireGrid[index] = newValidConnectionNode(index);
				index++;
			}
		}

		//initialise outputs
		for(int output = 0; output < numberOfOutputs; output++){
			entireGrid[index] = new OutputNode(index, rand.nextInt(numberOfInputs + columns * rows));
			index++;
		}
	}

	//coded and efficient
	private ConnectionNode newValidConnectionNode(int index){
		int maxAllowedIndex = calculateMaxAllowedIndex(index);
		int minAllowedIndex = calculateMinAllowedIndex(index);
		int function = rand.nextInt(functionList.length);
		int arityOfFunction = functionList[function].getFunctionArity();
		int[] connections = new int[arityOfFunction];
		for(int i = 0; i < arityOfFunction; i++){
			int connection = rand.nextInt((maxAllowedIndex - minAllowedIndex) + 1) + minAllowedIndex;
			connections[i] = connection;
		}
		return new ConnectionNode(index, function, connections);
	}

	//coded and efficient
	private int calculateMaxAllowedIndex(int index){
		//take number of inputs off to find out which -connection- grid spot it's in
		int connectionGridIndex = index - numberOfInputs;
		//finding which connection grid column it's in
		int column = connectionGridIndex / rows;
		//this column's first index-1 i.e. the last index of the previous column, then add back on the inputs
		return column * rows - 1 + numberOfInputs;
	}

	//coded and efficient
	private int calculateMinAllowedIndex(int index){
		//if the levels back and columns are the same then node can connect to anything (zero is the first input index)
		if(columns == levelsBack){
			return 0;
		}
		int connectionGridIndex = index - numberOfInputs;
		//finding which connection grid column it's in
		int column = connectionGridIndex / rows;
		if(levelsBack > column){
			return 0;
		}
		else{
			return column * rows - (levelsBack * rows) + numberOfInputs;
		}
	}

	//coded
	//TODO review - look at improvements
	void mutate(){
		boolean activeGeneModified = false;
		while(!activeGeneModified){
			int nodeIndex = rand.nextInt((entireGrid.length - numberOfInputs)) + numberOfInputs;
			int connectionOrFunction = rand.nextInt(2);
			if(entireGrid[nodeIndex] instanceof ConnectionNode){
				ConnectionNode node = (ConnectionNode) entireGrid[nodeIndex];
				int connectionNumber = rand.nextInt(node.getConnections().length);
				if(connectionOrFunction == 0){
					node.getConnections()[connectionNumber] = rand.nextInt((calculateMaxAllowedIndex(nodeIndex) - calculateMinAllowedIndex(nodeIndex)) + 1) + calculateMinAllowedIndex(nodeIndex);
				}
				else{
					int arityOld = functionList[node.getFunction()].getFunctionArity();
					node.setFunction(rand.nextInt(functionList.length));
					int arityNew = functionList[node.getFunction()].getFunctionArity();
					if(arityNew != arityOld){
						int[] connections = new int[arityNew];
						for(int i = 0; i < arityNew; i++){
							int connection;
							connection = rand.nextInt((calculateMaxAllowedIndex(nodeIndex) - calculateMinAllowedIndex(nodeIndex)) + 1) + calculateMinAllowedIndex(nodeIndex);
							connections[i] = connection;
						}
						node.setConnections(connections);
					}
				}
				if(node.isActive()){
					activeGeneModified = true;
				}
			}
			else{
				OutputNode node = (OutputNode) entireGrid[nodeIndex];
				node.setConnection(entireGrid.length - numberOfOutputs - 1);
				if(node.isActive()){
					activeGeneModified = true;
				}
			}
		}
	}

	//coded
	//TODO review - look at improvements
	void decodeGenotype(){
		//boolean array for all of the nodes
		boolean[] toEvaluate = new boolean[entireGrid.length];
		//set all of the outputs to be evaluated
		for(int i = 0; i < numberOfOutputs; i++){
			toEvaluate[toEvaluate.length - i - 1] = true;
		}
		//for all of the nodes except the inputs, count down and check what needs evaluating
		for(int i = entireGrid.length - 1; i >= numberOfInputs; i--){
			//if to evaluate is true, set its connections to true too
			if(toEvaluate[i]){
				//if its an output node there's only 1 connection
				if(entireGrid[i] instanceof OutputNode){
					OutputNode node = (OutputNode) entireGrid[i];
					toEvaluate[node.getConnection()] = true;
				}
				//if it's a connection node there could be more
				else if(entireGrid[i] instanceof ConnectionNode){
					ConnectionNode node = (ConnectionNode) entireGrid[i];
					for(int j = 0; j < node.getConnections().length; j++){
						toEvaluate[node.getConnections()[j]] = true;
					}
				}
			}
		}
		//gunna count the nodes that have been set to active
		numberOfNodesUsed = 0;
		//start after the last input and count up to all nodes
		for(int i = numberOfInputs; i < entireGrid.length; i++){
			//if to evaluate is true then set the node active and set that node's used index in the nodes
			//used array
			if(toEvaluate[i]){
				entireGrid[i].setActive();
				nodesUsed[numberOfNodesUsed] = i;
				numberOfNodesUsed++;
			}
		}
		//so this the output of the program - 1 for each input graph
		predictedOutput = new HashSet[inputGraphs.size()];
		//for all of the graphs in the input
		for(int inputIndex = 0; inputIndex < inputGraphs.size(); inputIndex++){
			HashSet<Integer> cover = new HashSet<>();
			ArrayList<ArrayList<Integer>> graph = inputGraphs.get(inputIndex);
			int index = 0; //same everywhere
			//todo check the second condition
			while(!isCovered(graph, cover) && index <= inputGraphs.get(inputIndex).size()){ //same everywhere
				if(!cover.contains(index)){ //same everywhere
					for(int v : graph.get(index)){ //same everywhere
						//for all the nodes that are used
						for(int i = 0; i < numberOfNodesUsed; i++){
							//if it's a connection node
							if(entireGrid[nodesUsed[i]] instanceof ConnectionNode){
								ConnectionNode node = (ConnectionNode) entireGrid[nodesUsed[i]];
								Object[] inputs = new Object[3];
								inputs[0] = cover;
								inputs[1] = index;
								inputs[2] = v;
								functionList[node.getFunction()].callFunction(inputs);
							}
							else{
								predictedOutput[inputIndex] = cover;
							}
						}
					}
				}
				index++;
			}
		}
		fitness = calculateFitness();
	}

	//coded and efficient
	private double calculateFitness(){
		double runningTotalFitness = 0.0;
		for(int i = 0; i < inputGraphs.size(); i++){
			runningTotalFitness -= Math.pow(outputData[i].size() - predictedOutput[i].size(), 2);
		}
		return runningTotalFitness - numberOfNodesUsed;
	}

	//coded
	String getExpressions(){
		String[] expressions = new String[entireGrid.length];
		for(int i = 0; i < numberOfInputs; i++){
			expressions[i] = "x" + i;
		}
		for(int i = 0; i < numberOfNodesUsed; i++){
			if(entireGrid[nodesUsed[i]] instanceof ConnectionNode){
				ConnectionNode node = (ConnectionNode) entireGrid[nodesUsed[i]];
				if(functionList[node.getFunction()].getFunctionArity() == 1){
					expressions[node.getIndex()] = "(" + functionList[node.getFunction()].getFunctionName() + "(" + expressions[node.getConnections()[0]] + "))";
				}
				else{
					StringBuilder fullExpression = new StringBuilder();
					for(int j = 0; j < functionList[node.getFunction()].getFunctionArity(); j++){
						if(j != functionList[node.getFunction()].getFunctionArity() - 1){
							fullExpression.append("(" + expressions[node.getConnections()[j]]);
							fullExpression.append(functionList[node.getFunction()].getFunctionName());
						}
						else{
							fullExpression.append(expressions[node.getConnections()[j]] + ")");
						}
					}
					expressions[node.getIndex()] = fullExpression.toString();
				}
			}
			else{
				OutputNode node = (OutputNode) entireGrid[nodesUsed[i]];
				expressions[node.getIndex()] = expressions[node.getConnection()];
			}
		}
		int outputIndex = 0;
		StringBuilder expressionList = new StringBuilder();
		for(int i = entireGrid.length - numberOfOutputs; i < entireGrid.length; i++){
			expressionList.append("y");
			expressionList.append(outputIndex);
			expressionList.append(" = ");
			expressionList.append(expressions[i]);
			expressionList.append("\n");
			outputIndex++;
		}
		return expressionList.toString().trim();
	}

	//coded and efficient
	double getFitness(){
		return fitness;
	}

	//coded and efficient - as yet not needed
	public GenericNode[] getEntireGrid(){
		return entireGrid;
	}

	//coded
	GenericNode[] getGridCopy(){
		GenericNode[] copy = new GenericNode[entireGrid.length];
		for(int i = 0; i < entireGrid.length; i++){
			if(entireGrid[i] instanceof InputNode){
				InputNode node = (InputNode) entireGrid[i];
				copy[i] = new InputNode(node.getIndex());
			}
			else if(entireGrid[i] instanceof ConnectionNode){
				ConnectionNode node = (ConnectionNode) entireGrid[i];
				copy[i] = new ConnectionNode(node.getIndex(), node.getFunction(), node.getConnections().clone());
			}
			else{
				OutputNode node = (OutputNode) entireGrid[i];
				copy[i] = new OutputNode(node.getIndex(), node.getConnection());
			}
		}
		return copy;
	}

	//todo implement
	private boolean isCovered(ArrayList<ArrayList<Integer>> graph, HashSet<Integer> cover){
		//todo: send list of edge lists in, pass the edge list into here with the cover and graph and set any new edges to covered
		return false;
	}
}