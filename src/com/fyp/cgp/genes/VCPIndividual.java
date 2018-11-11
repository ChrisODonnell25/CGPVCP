package com.fyp.cgp.genes;

import com.fyp.VCPAlgorithms.Edge;
import com.fyp.cgp.functions.GenericFunction;
import com.fyp.cgp.functions.StatusEnum;
import com.fyp.cgp.nodes.ConnectionNode;
import com.fyp.cgp.nodes.GenericNode;
import com.fyp.cgp.nodes.InputNode;
import com.fyp.cgp.nodes.OutputNode;

import java.util.*;

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
	private ArrayList<Integer> executionFunctions;
	private ArrayList<Integer> breakList;
	private ArrayList<Integer> ifFunctions;
	private ArrayList<Integer> orAndFunctions;
	private ArrayList<Integer> afterIfFunctions;
	private ArrayList<Integer> startList;
	private ArrayList<Integer> startPlusIfList;
	private ArrayList<Integer> addBreakList;
	private ArrayList<Integer> ifList;
	private ArrayList<Integer> orAndList;
	private int numberOfNodesUsed;
	private ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs;
	private ArrayList<ArrayList<Edge>> inputEdgeLists;
	private Integer[] outputData;
	private HashSet<Integer>[] predictedOutput;
	private Random rand;
	private String algorithm;

	public VCPIndividual(
			int columns,
			int rows,
			int levelsBack,
			int inputs,
			GenericFunction[] functionList,
			ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs,
			ArrayList<ArrayList<Edge>> inputEdgeLists,
			Integer[] outputData,
			boolean initFlag
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
		if(initFlag){
			initialise();
		}
	}

	//this constructor takes a pre-existing grid essentially copying an individual, used for offspring creation
	public VCPIndividual(int columns,
						 int rows,
						 int levelsBack,
						 int inputs,
						 GenericFunction[] functionList,
						 ArrayList<ArrayList<ArrayList<Integer>>> inputGraphs,
						 ArrayList<ArrayList<Edge>> inputEdgeLists,
						 Integer[] outputData,
						 GenericNode[] gridToCopy,
						 ArrayList<Integer> startList,
						 ArrayList<Integer> startPlusIfList,
						 ArrayList<Integer> addBreakList,
						 ArrayList<Integer> ifList,
						 ArrayList<Integer> orAndList
	){
		this(columns, rows, levelsBack, inputs, functionList, inputGraphs, inputEdgeLists, outputData, false);
		this.entireGrid = gridToCopy;
		this.startList = startList;
		this.startPlusIfList = startPlusIfList;
		this.addBreakList = addBreakList;
		this.ifList = ifList;
		this.orAndList = orAndList;

		nodesUsed = new int[entireGrid.length];
	}

	private void initialise(){
		entireGrid = new GenericNode[numberOfInputs + numberOfOutputs + rows * columns];
		int index = 0;
		nodesUsed = new int[entireGrid.length];

		executionFunctions = new ArrayList<>();
		breakList = new ArrayList<>();
		ifFunctions = new ArrayList<>();
		afterIfFunctions = new ArrayList<>();
		orAndFunctions = new ArrayList<>();

		for(int functionIndex = 0; functionIndex < functionList.length; functionIndex++){
			if(functionList[functionIndex].getFunctionName().contains("add")){
				executionFunctions.add(functionIndex);
			}
		}

		for(int functionIndex = 0; functionIndex < functionList.length; functionIndex++){
			if(functionList[functionIndex].getFunctionName().contains("break")){
				breakList.add(functionIndex);
			}
		}

		for(int functionIndex = 0; functionIndex < functionList.length; functionIndex++){
			if(functionList[functionIndex].isIf()){
				ifFunctions.add(functionIndex);
			}
		}

		for(int functionIndex = 0; functionIndex < functionList.length; functionIndex++){
			if(functionList[functionIndex].getFunctionName().contains("else") || functionList[functionIndex].getFunctionName().contains("endif")){
				afterIfFunctions.add(functionIndex);
			}
		}

		for(int functionIndex = 0; functionIndex < functionList.length; functionIndex++){
			if(functionList[functionIndex].getFunctionName().contains("and") && !(functionList[functionIndex].getFunctionName().contains("if")) || functionList[functionIndex].getFunctionName().contains("or")){
				orAndFunctions.add(functionIndex);
			}
		}

		startList = new ArrayList<>();
		startPlusIfList = new ArrayList<>();
		addBreakList = new ArrayList<>();
		ifList = new ArrayList<>();
		orAndList = new ArrayList<>();

		for(Integer function : ifFunctions){
			startList.add(function);
			startList.add(function);
			startList.addAll(executionFunctions);
			startList.addAll(breakList);
			startList.addAll(breakList);
		}


		startPlusIfList.addAll(startList);
		for(Integer function : ifFunctions){
			startPlusIfList.addAll(afterIfFunctions);
		}

		addBreakList.addAll(startList);

		ifList.addAll(startList);
		for(Integer function : ifFunctions){
			ifList.addAll(afterIfFunctions);
			ifList.addAll(orAndFunctions);
		}

		orAndList.addAll(ifFunctions);

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

		//initialise output
		int outputConnection = rand.nextInt(numberOfInputs + columns * rows);
		while(outputConnection != 0){
			String functionString = functionList[((ConnectionNode)entireGrid[outputConnection]).getFunction()].getFunctionName();
			if(functionString.contains("or") || functionString.contains("and") && !functionString.contains("if")){
				outputConnection = rand.nextInt(numberOfInputs + columns * rows);
			}
			else{
				break;
			}
		}
		entireGrid[index] = new OutputNode(index, outputConnection);
		//if there are any break functions in the line, find the index of the first one and set the output's connection to that
		int breakIndex = findFirstBreakIndex(((OutputNode)entireGrid[index]).getConnection());
		if(breakIndex > -1){
			((OutputNode)entireGrid[entireGrid.length-1]).setConnection(breakIndex);
		}

	}

	//coded and efficient
	private ConnectionNode newValidConnectionNode(int index){
		int maxAllowedIndex = calculateMaxAllowedIndex(index);
		int minAllowedIndex = calculateMinAllowedIndex(index);

		int arityOfFunction = 1;
		int[] connections = new int[arityOfFunction];
		for(int i = 0; i < arityOfFunction; i++){
			int connection = rand.nextInt((maxAllowedIndex - minAllowedIndex) + 1) + minAllowedIndex;
			connections[i] = connection;
		}

		ArrayList<Integer> availableFunctions = new ArrayList<>();
		boolean hadIf = checkIfHadIf(connections[0]);
		int function = -1;
		String functionName = "";
		if(!(index >= numberOfInputs && index < numberOfInputs + rows || connections[0] == 0)){
			functionName = functionList[((ConnectionNode) entireGrid[connections[0]]).getFunction()].getFunctionName();
		}
		//if it's the first connection node or if the previous node's function name has add in it (needs cast to
		// connection node and check for connection node type)
		if(index >= numberOfInputs && index < numberOfInputs + rows || connections[0] == 0){

			availableFunctions = startList;
		}
		else if(entireGrid[connections[0]] instanceof ConnectionNode &&
				(functionName.contains("add") ||
				functionName.contains("break"))){
			if(hadIf){
				availableFunctions = startPlusIfList;
			}
			else{
				availableFunctions = startList;
			}
		}
		else if(entireGrid[connections[0]] instanceof ConnectionNode &&
				functionList[((ConnectionNode)entireGrid[connections[0]]).getFunction()].isIf()){

			availableFunctions = ifList;
		}
		else if(entireGrid[connections[0]] instanceof ConnectionNode &&
				(functionName.contains("else") ||
				functionName.contains("endif"))){

			availableFunctions = startList;
		}
		else if(entireGrid[connections[0]] instanceof ConnectionNode &&
				(functionName.contains("or") ||
						functionName.contains("and") && !functionName.contains("if"))){

			availableFunctions = orAndList;
		}
		else {
			System.out.println("Something has gone wrong............................");
		}

 		function = availableFunctions.get(rand.nextInt(availableFunctions.size()));

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

	private boolean checkIfHadIf(int connectionParam){
		int connection = connectionParam;
		int ifCount = 0;
		while(!(entireGrid[connection] instanceof InputNode)){
			if(entireGrid[connection] instanceof ConnectionNode){
				String functionName = functionList[((ConnectionNode)entireGrid[connection]).getFunction()].getFunctionName();
				if(functionName.contains("else") ||	functionName.contains("endif") || functionName.contains("or")||	functionName.contains("and") && !functionName.contains("if")){
					ifCount--;
				}
				else if(functionList[((ConnectionNode)entireGrid[connection]).getFunction()].isIf()){
					ifCount++;
				}
				connection = ((ConnectionNode)entireGrid[connection]).getConnections()[0];
			}
		}
		return ifCount > 0;
	}

	private int findFirstBreakIndex(int connectionParam){
		int connection = connectionParam;
		int breakIndex = -1;
		while(!(entireGrid[connection] instanceof InputNode)){
			if(entireGrid[connection] instanceof ConnectionNode){
				if(functionList[((ConnectionNode)entireGrid[connection]).getFunction()].getFunctionName().contains("break")){
					breakIndex = connection;
				}
				connection = ((ConnectionNode)entireGrid[connection]).getConnections()[0];
			}
		}
		return breakIndex;
	}

	//coded
	//TODO change to handle if elses safely
	public void mutate(){
		boolean activeGeneModified = false;
		while(!activeGeneModified){
			int nodeIndex = rand.nextInt((entireGrid.length - numberOfInputs)) + numberOfInputs;
			int connectionOrFunction = rand.nextInt(2);
			if(entireGrid[nodeIndex] instanceof ConnectionNode){
				ConnectionNode node = (ConnectionNode) entireGrid[nodeIndex];
				int connectionNumber = rand.nextInt(node.getConnections().length);
				if(connectionOrFunction == 0){
					String currentFunctionName = functionList[node.getFunction()].getFunctionName();
					boolean foundValidNewConnection = false;
					int newConnection = -1;
					while(!foundValidNewConnection){
						newConnection = rand.nextInt((calculateMaxAllowedIndex(nodeIndex) - calculateMinAllowedIndex(nodeIndex)) + 1) + calculateMinAllowedIndex(nodeIndex);
						boolean hadIf = checkIfHadIf(newConnection);
						String newConnectionFunctionName = "input";
						if(entireGrid[newConnection] instanceof ConnectionNode){
							newConnectionFunctionName = functionList[((ConnectionNode) entireGrid[newConnection]).getFunction()].getFunctionName();
						}
						foundValidNewConnection = isNewFunctionValid(currentFunctionName, newConnectionFunctionName, hadIf);
					}
					node.getConnections()[connectionNumber] = newConnection;
				}
				else{
					ArrayList<Integer> availableFunctions = new ArrayList<>();
					boolean hadIf = checkIfHadIf(node.getConnections()[0]);
					int function;
					String functionName = "";
					if(!(nodeIndex >= numberOfInputs && nodeIndex < numberOfInputs + rows || node.getConnections()[0] == 0)){
						functionName = functionList[((ConnectionNode) entireGrid[node.getConnections()[0]]).getFunction()].getFunctionName();
					}
					//if it's the first connection node or if the previous node's function name has add in it (needs cast to
					// connection node and check for connection node type)
					if(nodeIndex >= numberOfInputs && nodeIndex < numberOfInputs + rows || node.getConnections()[0] == 0){

						availableFunctions = startList;
					}
					else if(entireGrid[node.getConnections()[0]] instanceof ConnectionNode &&
							(functionName.contains("add") ||
									functionName.contains("break"))){
						if(hadIf){
							availableFunctions = startPlusIfList;
						}
						else{
							availableFunctions = startList;
						}
					}
					else if(entireGrid[node.getConnections()[0]] instanceof ConnectionNode &&
							functionList[((ConnectionNode)entireGrid[node.getConnections()[0]]).getFunction()].isIf()){

						availableFunctions = ifList;
					}
					else if(entireGrid[node.getConnections()[0]] instanceof ConnectionNode &&
							(functionName.contains("else") ||
									functionName.contains("endif"))){

						availableFunctions = startList;
					}
					else if(entireGrid[node.getConnections()[0]] instanceof ConnectionNode &&
							(functionName.contains("or") ||
									functionName.contains("and") && !functionName.contains("if"))){

						availableFunctions = orAndList;
					}
					else {
						System.out.println("Something has gone wrong............................");
					}

					function = availableFunctions.get(rand.nextInt(availableFunctions.size()));
					node.setFunction(function);

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
			//if there are any break functions in the line, find the index of the first one and set the output's connection to that
			int breakIndex = findFirstBreakIndex(((OutputNode)entireGrid[entireGrid.length-1]).getConnection());
			if(breakIndex > -1){
				((OutputNode)entireGrid[entireGrid.length-1]).setConnection(breakIndex);
			}
		}
	}

	private boolean isNewFunctionValid(String currentFunction, String newFunction, boolean hadIf){
		if(newFunction.contains("input")){
			return true;
		}
		else if(newFunction.contains("add") || newFunction.contains("break")){
			if(currentFunction.contains("break") || currentFunction.contains("add") || (currentFunction.contains("if") && !currentFunction.contains("end"))){
				return true;
			}
			else if(currentFunction.contains("else") || currentFunction.contains("endif")){
				return hadIf;
			}
			else{
				return false;
			}
		}
		else if(newFunction.contains("else") || newFunction.contains("endif")){
			if(currentFunction.contains("break") || currentFunction.contains("add") || (currentFunction.contains("if") && !currentFunction.contains("end"))){
				return true;
			}
			else{
				return false;
			}
		}
		else if((newFunction.contains("if") && !newFunction.contains("end"))){
			return true;
		}
		else if(newFunction.contains("or") || newFunction.contains("and") && !newFunction.contains("if")){
			if(currentFunction.contains("if") && !currentFunction.contains("end")){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			System.out.println("Something has gone seriously wrong..........................");
			return false;
		}
	}

	//coded
	public void decodeGenotype(){
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
		boolean containsAdd = false;
		//start after the last input and count up to all nodes
		for(int i = numberOfInputs; i < entireGrid.length; i++){
			//if to evaluate is true then set the node active and set that node's used index in the nodes
			//used array
			if(toEvaluate[i]){
				entireGrid[i].setActive();
				nodesUsed[numberOfNodesUsed] = i;
				numberOfNodesUsed++;
				if(entireGrid[i] instanceof ConnectionNode){
					String funcName = functionList[((ConnectionNode)entireGrid[i]).getFunction()].getFunctionName();
					if(funcName.contains("add")){
						containsAdd = true;
					}
				}
			}
		}
		algorithm = getExpressions();
		//so this the output of the program - 1 for each input graph
		predictedOutput = new HashSet[inputGraphs.size()];
		if(containsAdd){
			//for all of the graphs in the input
			for(int inputIndex = 0; inputIndex < inputGraphs.size(); inputIndex++){
				HashSet<Integer> cover = new HashSet<>();
				ArrayList<ArrayList<Integer>> graph = inputGraphs.get(inputIndex);
				ArrayList<Edge> edgeList = inputEdgeLists.get(inputIndex);
				int index = 0; //same everywhere
				int totalIndex = 0; //same everywhere
				//todo check the second condition
				Object[] inputs = new Object[5];
				inputs[0] = cover;
				inputs[4] = graph;
				while(!isCovered(cover, edgeList) && totalIndex < inputGraphs.get(inputIndex).size() * 2){ //same everywhere
					if(!cover.contains(index)){ //same everywhere
						inputs[3] = StatusEnum.START;
						inputs[1] = index;
						for(int v : graph.get(index)){ //same everywhere
							inputs[2] = v;
							//for all the nodes that are used
							for(int i = 0; i < numberOfNodesUsed; i++){
								//if it's a connection node
								if(entireGrid[nodesUsed[i]] instanceof ConnectionNode){
									ConnectionNode node = (ConnectionNode) entireGrid[nodesUsed[i]];
									functionList[node.getFunction()].callFunction(inputs);
								}
								if(inputs[3] == StatusEnum.BROKEN){
									break;
								}
							}
							if(inputs[3] == StatusEnum.BROKEN){
								break;
							}
						}
					}
					index++;
					totalIndex++;
					if(index == inputGraphs.get(inputIndex).size()){
						index = 0;
					}
				}
				predictedOutput[inputIndex] = cover;
			}
		}
		fitness = calculateFitness();
	}

	//coded and efficient
	public double calculateFitness(){
		double runningTotalFitness = 0.0;
		for(int i = 0; i < inputGraphs.size(); i++){
			if(predictedOutput[i] != null){
				if(isCovered(predictedOutput[i], inputEdgeLists.get(i))){
					runningTotalFitness += 1;
					runningTotalFitness -= predictedOutput[i].size();
				}
				else{
					runningTotalFitness -= inputGraphs.get(i).size();
				}
			}
			else{
				runningTotalFitness -= 1000000000;
			}
		}
		return runningTotalFitness;
	}

	//coded
	public String getExpressions(){
		String[] expressions = new String[entireGrid.length];
		for(int i = 0; i < numberOfInputs; i++){
			expressions[i] = "";
		}
		int numberOfTabs = 0;
		for(int i = 0; i < numberOfNodesUsed; i++){
			if(entireGrid[nodesUsed[i]] instanceof ConnectionNode){
				ConnectionNode node = (ConnectionNode) entireGrid[nodesUsed[i]];
				String functionName = functionList[node.getFunction()].getFunctionName();
				if(functionName.contains("or") || functionName.contains("and") && !(functionName.contains("if")) ||
						expressions[node.getConnections()[0]].endsWith("<and>") || expressions[node.getConnections()[0]].endsWith("<or>")){
					expressions[node.getIndex()] = expressions[node.getConnections()[0]] + " " + functionName;
				}
				else{
					String tabs = "";
					if(functionName.contains("endif") || functionName.contains("else")){
						numberOfTabs--;
					}
					for(int j = 0; j < numberOfTabs; j++){
						tabs += "\t";
					}
					if(functionList[node.getFunction()].isIf() || functionName.contains("else")){
						numberOfTabs++;
					}
					expressions[node.getIndex()] = expressions[node.getConnections()[0]] + "\n" + tabs + functionName;
				}
			}
			else{
				OutputNode node = (OutputNode) entireGrid[nodesUsed[i]];
				expressions[node.getIndex()] = expressions[node.getConnection()];
			}
		}
		StringBuilder expressionList = new StringBuilder();
		for(int i = entireGrid.length - numberOfOutputs; i < entireGrid.length; i++){
			expressionList.append("alogorithm");
			expressionList.append(" = ");
			expressionList.append(expressions[i]);
			expressionList.append("\n");
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

	private boolean isCovered(HashSet<Integer> cover, ArrayList<Edge> edgeList){
		for(Edge e :
				edgeList){
			if(!e.isCovered(cover)){
				return false;
			}
		}
		return true;
	}

	public ArrayList<Integer> getStartList(){
		return startList;
	}

	public ArrayList<Integer> getStartPlusIfList(){
		return startPlusIfList;
	}

	public ArrayList<Integer> getAddBreakList(){
		return addBreakList;
	}

	public ArrayList<Integer> getIfList(){
		return ifList;
	}

	public ArrayList<Integer> getOrAndList(){
		return orAndList;
	}

	public String getCoverSizes(){
		StringBuilder sb = new StringBuilder();
		int index = 1;
		for(HashSet<Integer> set : predictedOutput){
			sb.append("Calculated cover " + index + " size = " + set.size() + " cover " + index + " MVC = " + outputData[index-1] + " covered: " + isCovered(set, inputEdgeLists.get(index-1)) + "\n");
			index++;
		}
		return sb.toString();
	}
}