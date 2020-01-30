package algorithm;

import java.util.ArrayList;
import java.util.Collections;

import model.Individual;
import model.Route;

public class Algorithm {

    public ArrayList<Individual> seedPopulation(ArrayList<ArrayList<Integer>> pseudoChromosome, int populationSize){

        ArrayList<Individual> population = new ArrayList<Individual>();

        for(int i = 0; i < populationSize; i++){
            ArrayList<ArrayList<Integer>> chromosome = new ArrayList<ArrayList<Integer>>();
            for (ArrayList<Integer> subPseudoChromosome: pseudoChromosome){
                // copy the array to avoid reference bug
                ArrayList<Integer> copy = new ArrayList<Integer>(subPseudoChromosome);
                Collections.shuffle(copy);
                chromosome.add(copy);
            }
            Individual individual = new Individual();
            individual.addChromosome(chromosome);
            population.add(individual);
        }
        return population;
    }

    public void evaluateFitness(ArrayList<Individual> population){
        
        for(Individual individual: population){
            for(ArrayList<Route> phenotype: individual.getPhenotype()){

            }
        }

    }
}