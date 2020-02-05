package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import model.Individual;
import model.Model;
import model.Route;

public class Algorithm {

    private Random random = new Random();

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
            double totalEuclidianDistance = 0;
            for(ArrayList<Route> phenotype: individual.getPhenotype()){
                for(Route route: phenotype){
                    totalEuclidianDistance = totalEuclidianDistance + route.getDistance();
                }
            }
            individual.addFitness(totalEuclidianDistance);
        }

    }

    public Individual getBestIndividual(ArrayList<Individual> population){
        int index = 0;
        double currentBestFitness = population.get(0).getFitness();

        for (int i=1; i<population.size(); i++){
            if(population.get(i).getFitness() < currentBestFitness){
                index = i;
                currentBestFitness = population.get(i).getFitness();
            }
        }
        return population.get(index);
    }

    public ArrayList<Individual> getElites(ArrayList<Individual> population, int number){

        //Sort the population based on fitness value
        Collections.sort(population, Comparator.comparingDouble(Individual ::getFitness));

        return new ArrayList<Individual>(population.subList(0, number));
    }

    public Individual tournamentSelection(ArrayList<Individual> population, double pressure, int tournamentSize){

        ArrayList<Individual> candidates = new ArrayList<Individual>();
        ArrayList<Double> pValues = new ArrayList<Double>();

        //Randomly select tournamentSize number of candidates from the population
        for (int i=0; i<tournamentSize; i++){
            candidates.add(population.get(this.random.nextInt(population.size())));
            //Compute pvalues for the later selection NB: the candidate-list is not ordered
            pValues.add(pressure*Math.pow(1-pressure, i));
        }
        Collections.sort(candidates, Comparator.comparingDouble(Individual ::getFitness));

        //Generate a random number between 0-1
        double selection = Math.random();

        //iterate through the p-values list, and if the selection variable is smaller, return the given candidate
        for (int j=0; j<candidates.size(); j++){
            if (selection < pValues.get(j)){
                return candidates.get(j);
            }
        }
        //If none of the cancidates are returned, return the last candidate (Because there is a theoretical chance the above loop wont trigger, since the p-values doesnt sum to exaclty 1)
        return candidates.get(candidates.size()-1);
    }

    // Order 1 crossover
    public ArrayList<Individual> crossover(Model model, Individual parent1, Individual parent2){

        ArrayList<Individual> children = new ArrayList<Individual>();

        // Randomly choose depot to undergo reproduction
        int depotIndex = this.random.nextInt(model.depotList.size());

        // Get the Integer-representation of the routes for selected depot, for each parent
        ArrayList<Integer> subChromosome1 = parent1.getChromosome().get(depotIndex);
        ArrayList<Integer> subChromosome2 = parent2.getChromosome().get(depotIndex);

        // get indexes for cutting of chromosomes
        int cutIndex1 = this.random.nextInt(subChromosome1.size());
        int cutIndex2 = this.random.nextInt(subChromosome1.size());
        

        //Recombination for child 1
        ArrayList<Integer> child1SubChromosome = new ArrayList<Integer>(subChromosome1.subList(Math.min(cutIndex1, cutIndex2), Math.max(cutIndex1, cutIndex2)));

        for(Integer customerIndex: subChromosome2){
            if (!child1SubChromosome.contains(customerIndex)){
                child1SubChromosome.add(customerIndex);
            }
        }

        ArrayList<ArrayList<Integer>> child1Chromosome = new ArrayList<ArrayList<Integer>>(parent1.getChromosome());
        child1Chromosome.set(depotIndex, child1SubChromosome);
        
        // create new Individual from chromosome
        Individual child1 = new Individual();
        child1.addChromosome(child1Chromosome);
        children.add(child1);

        //Recombination for child 2
        ArrayList<Integer> child2SubChromosome = new ArrayList<Integer>(subChromosome2.subList(Math.min(cutIndex1, cutIndex2), Math.max(cutIndex1, cutIndex2)));

        for(Integer customerIndex: subChromosome1){
            if (!child2SubChromosome.contains(customerIndex)){
                child2SubChromosome.add(customerIndex);
            }
        }

        ArrayList<ArrayList<Integer>> child2Chromosome = new ArrayList<ArrayList<Integer>>(parent2.getChromosome());
        child2Chromosome.set(depotIndex, child2SubChromosome);

        // create new Individual from chromosome
        Individual child2 = new Individual();
        child2.addChromosome(child2Chromosome);
        children.add(child2);

        // System.out.println("customers parent 1: " +routeListParent1);
        // System.out.println("customers parent 2: " +routeListParent2);
        // System.out.println("Cutting indexes: " + cutIndex1 + ":" + cutIndex2);

        // System.out.println("child 1: " + child1);
        // System.out.println("child 2: " +child2);


        return children;
    }

    // Swap mutation, performs swap on a random depot
    public Individual swapMutation(Individual individual, Model model){

        ArrayList<ArrayList<Integer>> chromosome = individual.cloneChromosome();

        // Randomly choose depot to undergo mutation
        int depotIndex = this.random.nextInt(model.depotList.size());
        ArrayList<Integer> subChromosome = chromosome.get(depotIndex);

        // Randomly choose which integers to swap
        int index1 = this.random.nextInt(subChromosome.size());
        int index2 = this.random.nextInt(subChromosome.size());

        Collections.swap(subChromosome, index1, index2);
        chromosome.set(depotIndex, subChromosome);

        Individual mutatedIndividual = new Individual();
        mutatedIndividual.addChromosome(chromosome);

        return mutatedIndividual;
    }

    // insert mutation
    public Individual insertMutation(Individual individual, Model model){

        ArrayList<ArrayList<Integer>> chromosome = individual.cloneChromosome();

        // Randomly choose depot to undergo mutation
        int depotIndex = this.random.nextInt(model.depotList.size());
        ArrayList<Integer> subChromosome = chromosome.get(depotIndex);

        //index to move
        int index2 = this.random.nextInt(subChromosome.size());

        int element = subChromosome.get(index2);
        subChromosome.remove(index2);

        //Index to mark
        int index1 = this.random.nextInt(subChromosome.size());

        if(index1 == subChromosome.size()-1){
            subChromosome.add(0, element);
        }
        else {
            subChromosome.add(index1+1, element);
        }
        chromosome.set(depotIndex, subChromosome);

        Individual mutatedIndividual = new Individual();
        mutatedIndividual.addChromosome(chromosome);

        return mutatedIndividual;
    }

    // Scramble mutation, performs scramble on a slice of subchromosome
    public Individual scrambleMutation(Individual individual, Model model){

        ArrayList<ArrayList<Integer>> chromosome = individual.cloneChromosome();

        // Randomly choose depot to undergo mutation
        int depotIndex = this.random.nextInt(model.depotList.size());
        ArrayList<Integer> subChromosome = chromosome.get(depotIndex);

        // Randomly choose which integer-range to shuffle
        int index1 = this.random.nextInt(subChromosome.size());
        int index2 = this.random.nextInt(subChromosome.size());

        Collections.shuffle(subChromosome.subList(Math.min(index1, index2), Math.max(index1, index2)));
        chromosome.set(depotIndex, subChromosome);

        Individual mutatedIndividual = new Individual();
        mutatedIndividual.addChromosome(chromosome);

        return mutatedIndividual;
    }

    
}