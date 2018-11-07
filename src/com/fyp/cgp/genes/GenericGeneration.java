package com.fyp.cgp.genes;

import java.util.ArrayList;

public abstract class GenericGeneration {
    ArrayList<VCPIndividual> generation = new ArrayList<>();
    VCPIndividual bestIndividual;
    double bestScoreOverall;


    public ArrayList<VCPIndividual> getGeneration(){
        return generation;
    }
    public void addIndividualToGeneration(VCPIndividual individual){
        generation.add(individual);
    }
    public void setBestIndividual(){
        double bestScore = generation.get(0).getFitness();
        int bestIndividualIndex = 0;
        for (int i = 0; i < generation.size(); i++) {
            if(generation.get(i).getFitness() >= bestScore){
                bestScore = generation.get(i).getFitness();
                bestScoreOverall = generation.get(i).getFitness();
                bestIndividualIndex = i;
            }
        }
        bestIndividual = generation.get(bestIndividualIndex);
    }
    public VCPIndividual getBestIndividual(){
        return bestIndividual;
    }

    public double getBestScoreOverall() {
        return bestScoreOverall;
    }

    protected abstract void initialise();

    public abstract String run();

}
