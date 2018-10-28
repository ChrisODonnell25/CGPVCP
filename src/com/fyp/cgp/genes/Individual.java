package com.fyp.cgp.genes;

import com.fyp.cgp.functions.GenericFunctions;
import com.fyp.cgp.nodes.ConnectionNode;
import com.fyp.cgp.nodes.GenericNode;
import com.fyp.cgp.nodes.InputNode;
import com.fyp.cgp.nodes.OutputNode;

import java.util.Random;

public class Individual {
    private int columns;
    private int rows;
    private int levelsBack;
    private int numberOfInputs;
    private int numberOfOutputs;
    private double fitness;
    private GenericNode[] entireGrid;
    private int[] nodesUsed;
    private GenericFunctions functionList;
    private int numberOfNodesUsed;
    //TODO: these 3 variables are not generic
    private double[] inputData;
    private double[] outputData;
    private double[] predictedOutput;
    private Random rand;

    Individual(int columns, int rows, int levelsBack, int inputs, int outputs, GenericFunctions functionList, double[] inputData, double[] outputData, boolean initFlag){
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
        this.inputData = inputData;
        this.outputData = outputData;
        rand = new Random();
        if(initFlag){
			initialise();
		}
    }

    //this constructor takes a pre-existing grid essentially copying an individual, used for offspring creation
    Individual(int columns, int rows, int levelsBack, int inputs, int outputs, GenericFunctions functionList, double[] inputData, double[] outputData, GenericNode[] gridToCopy){
        this(columns,rows,levelsBack,inputs,outputs,functionList,inputData,outputData, false);
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
        for(int col = 0; col < columns; col++ ){
            for (int row = 0; row < rows; row++){
                entireGrid[index] = newValidConnectionNode(index);
                index++;
            }
        }

        //initialise outputs
        for(int output = 0; output < numberOfOutputs; output++){
            entireGrid[index] = new OutputNode(index, rand.nextInt(numberOfInputs + columns*rows));
            index++;
        }
    }

    //coded and efficient
    private ConnectionNode newValidConnectionNode(int index){
        int maxAllowedIndex = calculateMaxAllowedIndex(index);
        int minAllowedIndex = calculateMinAllowedIndex(index);
        int function = rand.nextInt(functionList.getNumberOfFunctions());
        int arityOfFunction = functionList.getFunctionArity(function);
        int[] connections = new int[arityOfFunction];
        for(int i = 0; i < arityOfFunction; i++) {
            int connection = rand.nextInt((maxAllowedIndex - minAllowedIndex) + 1)+minAllowedIndex;
            connections[i] = connection;
        }
        return new ConnectionNode(index, function, connections);
    }

    //coded and efficient
    private int calculateMaxAllowedIndex(int index){
        //take number of inputs off to find out which -connection- grid spot it's in
        int connectionGridIndex = index - numberOfInputs;
        //finding which connection grid column it's in
        int column = connectionGridIndex/rows;
        //this column's first index-1 i.e. the last index of the previous column, then add back on the inputs
        return column*rows-1+numberOfInputs;
    }

	//coded and efficient
    private int calculateMinAllowedIndex(int index){
        //if the levels back and columns are the same then node can connect to anything (zero is the first input index)
    	if(columns==levelsBack){
			return 0;
		}
        int connectionGridIndex = index - numberOfInputs;
        //finding which connection grid column it's in
        int column = connectionGridIndex/rows;
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
        while (!activeGeneModified){
            int nodeIndex = rand.nextInt((entireGrid.length - numberOfInputs))+numberOfInputs;
            int connectionOrFunction = rand.nextInt(2);
            if(entireGrid[nodeIndex] instanceof ConnectionNode){
                ConnectionNode node = (ConnectionNode) entireGrid[nodeIndex];
                int connectionNumber = rand.nextInt(node.getConnections().length);
                if(connectionOrFunction == 0){
                    node.getConnections()[connectionNumber] = rand.nextInt((calculateMaxAllowedIndex(nodeIndex) - calculateMinAllowedIndex(nodeIndex)) + 1)+calculateMinAllowedIndex(nodeIndex);
                }
                else{
                    int arityOld = functionList.getFunctionArity(node.getFunction());
                    node.setFunction(rand.nextInt(functionList.getNumberOfFunctions()));
                    int arityNew = functionList.getFunctionArity(node.getFunction());
                    if(arityNew != arityOld){
                        int[] connections = new int[arityNew];
                        for(int i = 0; i < arityNew; i++) {
                            int connection;
                            connection = rand.nextInt((calculateMaxAllowedIndex(nodeIndex) - calculateMinAllowedIndex(nodeIndex)) + 1)+calculateMinAllowedIndex(nodeIndex);
                            connections[i] = connection;
                        }
                        node.setConnections(connections);
                    }
                }
                if(node.isActive()){
					activeGeneModified = true;
				}
            }
            else {
                OutputNode node = (OutputNode) entireGrid[nodeIndex];
                node.setConnection(entireGrid.length - numberOfOutputs - 1);
                if (node.isActive()){
					activeGeneModified = true;
				}
            }
        }
    }

    //coded
	//TODO review - look at improvements
    void decodeGenotype(){
        boolean[] toEvaluate = new boolean[entireGrid.length];
        for (int i = 0; i < numberOfOutputs; i++) {
            toEvaluate[toEvaluate.length-i-1] = true;
        }
        for (int i = entireGrid.length-1; i >= numberOfInputs; i--) {
            if (toEvaluate[i]) {
                if (entireGrid[i] instanceof OutputNode) {
                    OutputNode node = (OutputNode) entireGrid[i];
                    toEvaluate[node.getConnection()] = true;
                } else if (entireGrid[i] instanceof ConnectionNode) {
                    ConnectionNode node = (ConnectionNode) entireGrid[i];
                    for (int j = 0; j < node.getConnections().length; j++) {
                        toEvaluate[node.getConnections()[j]] = true;
                    }
                }
            }
        }
        numberOfNodesUsed = 0;
        for (int i = numberOfInputs; i < entireGrid.length; i++) {
            if(toEvaluate[i]){
                entireGrid[i].setActive();
                nodesUsed[numberOfNodesUsed] = i;
                numberOfNodesUsed++;
            }
        }
        predictedOutput = new double[inputData.length];
        for(int inputIndex = 0; inputIndex < inputData.length; inputIndex ++) {
            double[] runningTotals = new double[entireGrid.length];
            runningTotals[0] = inputData[inputIndex];
            for (int i = 0; i < numberOfNodesUsed; i++) {
                if (entireGrid[nodesUsed[i]] instanceof ConnectionNode) {
                    ConnectionNode node = (ConnectionNode) entireGrid[nodesUsed[i]];
                    Object[] inputs = new Object[node.getConnections().length];
                    for (int j = 0; j < node.getConnections().length; j++){
                        inputs[j] = runningTotals[node.getConnections()[j]];
                    }
                    runningTotals[node.getIndex()] = (double)functionList.callFunction(inputs,node.getFunction());
                } else {
                    OutputNode node = (OutputNode) entireGrid[nodesUsed[i]];
                    runningTotals[node.getIndex()] = runningTotals[node.getConnection()];
                }
            }
            predictedOutput[inputIndex] = runningTotals[entireGrid.length-1];
        }
        fitness = calculateFitness();
    }

    //coded and efficient
	//index left in, incase of use for average
    private double calculateFitness(){
        double runningTotalFitness = 0.0;
        for (int i = 0; i < inputData.length; i++) {
            runningTotalFitness -= Math.pow(outputData[i] - predictedOutput[i], 2);
        }
        return runningTotalFitness - numberOfNodesUsed;
    }

	//coded
	//TODO review - look at improvements
    String getExpressions(){
        String[] expressions = new String[entireGrid.length];
        for (int i = 0; i < numberOfInputs; i++) {
            expressions[i] = "x" + i;
        }
        for (int i = 0; i < numberOfNodesUsed; i++) {
            if(entireGrid[nodesUsed[i]] instanceof ConnectionNode){
                ConnectionNode node = (ConnectionNode) entireGrid[nodesUsed[i]];
                if(functionList.getFunctionArity(node.getFunction()) == 1){
                    expressions[node.getIndex()] = "(" + functionList.getFunctionName(node.getFunction()) + "(" + expressions[node.getConnections()[0]] + "))";
                }
                else {
                    StringBuilder fullExpression = new StringBuilder();
                    for(int j = 0; j < functionList.getFunctionArity(node.getFunction()); j++){
                        if(j != functionList.getFunctionArity(node.getFunction())-1){
                            fullExpression.append("(" + expressions[node.getConnections()[j]]);
							fullExpression.append(functionList.getFunctionName(node.getFunction()));
						}
                        else{
							fullExpression.append(expressions[node.getConnections()[j]]+")");
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
        for (int i = entireGrid.length-numberOfOutputs; i < entireGrid.length; i++) {
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
	//TODO: remove if not needed
    public GenericNode[] getEntireGrid(){
        return entireGrid;
    }

    //coded
	//TODO: review - look at improvements - maybe serialise and deserialise?
    GenericNode[] getGridCopy(){
        GenericNode[] copy = new GenericNode[entireGrid.length];
        for (int i = 0; i < entireGrid.length; i++) {
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

    //coded and efficient
	//TODO: will probably factor this out and put elsewhere, probably unnecessary, delete if so
    void printStuff(){
        for (double d :
                predictedOutput) {
            System.out.print(d + " ");
        }
    }
}
