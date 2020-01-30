package algorithm;

import java.util.ArrayList;
import java.util.Collections;

public class Algorithm {

    public ArrayList<ArrayList<ArrayList<Integer>>> seedPopulation(ArrayList<ArrayList<Integer>> pseudoChromosome, int populationSize){

        ArrayList<ArrayList<ArrayList<Integer>>> population = new ArrayList<ArrayList<ArrayList<Integer>>>();

        for(int i = 0; i < populationSize; i++){
            ArrayList<ArrayList<Integer>> chromosome = new ArrayList<ArrayList<Integer>>();
            for (ArrayList<Integer> subPseudoChromosome: pseudoChromosome){
                // copy the array to avoid reference bug
                ArrayList<Integer> copy = new ArrayList<Integer>(subPseudoChromosome);
                Collections.shuffle(copy);
                chromosome.add(copy);
            }
            population.add(chromosome);
        }
        return population;
    }

    public double getBestFitness(){
        return 100.0;
    }
}