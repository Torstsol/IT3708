package model;

import java.util.ArrayList;
import model.Route;

public class Individual {

    ArrayList<ArrayList<Integer>> chromosome = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Route>> phenotype = new ArrayList<ArrayList<Route>>();
    double fitness;

    public void addChromosome(ArrayList<ArrayList<Integer>> chromosome){
        this.chromosome = chromosome;
    }

    public ArrayList<ArrayList<Integer>> getChromosome(){
        return this.chromosome;
    }

    public ArrayList<ArrayList<Route>> getPhenotype(){
        return this.phenotype;
    }

    public void addPhenotype(ArrayList<ArrayList<Route>> phenotype){
        this.phenotype = phenotype;
    }

    public void addFitness(double fitness){
        this.fitness = fitness;
    }

    public double getFitness(){
        return this.fitness;
    }
}