//package com.fyp.cgp.genes;
//
//import com.fyp.cgp.functions.GenericFunctions;
//
//import java.io.PrintWriter;
//
//public class RegressionGeneration extends GenericGeneration {
//    private int columns;
//    private int rows;
//    private int levelsBack;
//    private int numberOfInputs;
//    private int numberOfOutputs;
//    private GenericFunctions functionList;
//    private double[] inputData;
//    private double[] outputData;
//
//    public RegressionGeneration(int columns, int rows, int levelsBack, int numberOfInputs, int numberOfOutputs, GenericFunctions functionList, double[] inputData, double[] outputData) {
//        this.columns = columns;
//        this.rows = rows;
//        this.levelsBack = levelsBack;
//        this.numberOfInputs = numberOfInputs;
//        this.numberOfOutputs = numberOfOutputs;
//        this.functionList = functionList;
//        this.inputData = inputData;
//        this.outputData = outputData;
//        initialise();
//    }
//
//    protected void initialise(){
//        for (int i = 0; i < 5; i++){
//            addIndividualToGeneration(new Individual(columns, rows, levelsBack, numberOfInputs, numberOfOutputs, functionList, inputData, outputData, true));
//        }
//    }
//
//    public String run(){
//    	double time = System.currentTimeMillis();
//        for (Individual individual:getGeneration()) {
//            individual.decodeGenotype();
//        }
//        setBestIndividual();
//        int generation = 0;
//        //TODO stop conditions
//        while(getBestIndividual().getFitness() < -16) {
//            getGeneration().clear();
//            getGeneration().add(getBestIndividual());
//            for (int i = 1; i < 5; i++) {
//                Individual offSpring = new Individual(columns, rows, levelsBack, numberOfInputs, numberOfOutputs, functionList, inputData, outputData, getBestIndividual().getGridCopy());
//                offSpring.mutate();
//                offSpring.decodeGenotype();
//                getGeneration().add(offSpring);
//            }
//            setBestIndividual();
////            if (generation % 10000 == 0){
////                System.out.println(generation);
////                System.out.println(getBestIndividual().getExpressions() + " " + getBestIndividual().getFitness());
////                getBestIndividual().printStuff();
////                System.out.println();
////
////            }
//            generation++;
//        }
//		time = System.currentTimeMillis() - time;
//		System.out.println("Single run complete");
////		System.out.println(generation);
////		System.out.println(time);
//		System.out.println(getBestIndividual().getExpressions() + " " + getBestIndividual().getFitness());
////		getBestIndividual().printStuff();
//		return generation + "," + time;
//    }
//
//}
