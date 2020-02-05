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

    public ArrayList<Individual> seedPopulation(ArrayList<ArrayList<Integer>> pseudoChromosome, int populationSize) {

        ArrayList<Individual> population = new ArrayList<Individual>();

        for (int i = 0; i < populationSize; i++) {
            ArrayList<ArrayList<Integer>> chromosome = new ArrayList<ArrayList<Integer>>();
            for (ArrayList<Integer> subPseudoChromosome : pseudoChromosome) {
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

    public void evaluateFitness(ArrayList<Individual> population) {

        for (Individual individual : population) {
            double totalEuclidianDistance = 0;
            for (ArrayList<Route> phenotype : individual.getPhenotype()) {
                for (Route route : phenotype) {
                    totalEuclidianDistance = totalEuclidianDistance + route.getDistance();
                }
            }
            individual.addFitness(totalEuclidianDistance);
        }

    }

    public Individual getBestIndividual(ArrayList<Individual> population) {
        int index = 0;
        double currentBestFitness = population.get(0).getFitness();

        for (int i = 1; i < population.size(); i++) {
            if (population.get(i).getFitness() < currentBestFitness) {
                index = i;
                currentBestFitness = population.get(i).getFitness();
            }
        }
        return population.get(index);
    }

    public ArrayList<Individual> getElites(ArrayList<Individual> population, int number) {

        // Sort the population based on fitness value
        Collections.sort(population, Comparator.comparingDouble(Individual::getFitness));

        return new ArrayList<Individual>(population.subList(0, number));
    }

    public Individual tournamentSelection(ArrayList<Individual> population, double pressure, int tournamentSize) {

        ArrayList<Individual> candidates = new ArrayList<Individual>();
        ArrayList<Double> pValues = new ArrayList<Double>();

        // Randomly select tournamentSize number of candidates from the population
        for (int i = 0; i < tournamentSize; i++) {
            candidates.add(population.get(this.random.nextInt(population.size())));
            // Compute pvalues for the later selection NB: the candidate-list is not ordered
            pValues.add(pressure * Math.pow(1 - pressure, i));
        }
        Collections.sort(candidates, Comparator.comparingDouble(Individual::getFitness));

        // Generate a random number between 0-1
        double selection = Math.random();

        // iterate through the p-values list, and if the selection variable is smaller,
        // return the given candidate
        for (int j = 0; j < candidates.size(); j++) {
            if (selection < pValues.get(j)) {
                return candidates.get(j);
            }
        }
        // If none of the cancidates are returned, return the last candidate (Because
        // there is a theoretical chance the above loop wont trigger, since the p-values
        // doesnt sum to exaclty 1)
        return candidates.get(candidates.size() - 1);
    }

    // Order 1 crossover
    public ArrayList<Individual> crossover(Model model, Individual parent1, Individual parent2) {

        ArrayList<Individual> children = new ArrayList<Individual>();

        // Randomly choose depot to undergo reproduction
        int depotIndex = this.random.nextInt(model.depotList.size());

        // Get the Integer-representation of the routes for selected depot, for each
        // parent
        ArrayList<Integer> subChromosome1 = parent1.getChromosome().get(depotIndex);
        ArrayList<Integer> subChromosome2 = parent2.getChromosome().get(depotIndex);

        // get indexes for cutting of A
        int cutA1 = this.random.nextInt(subChromosome1.size());
        int cutA2 = this.random.nextInt(subChromosome1.size());

        // get indexes for cutting of B
        int cutB1 = this.random.nextInt(subChromosome1.size());
        int cutB2 = this.random.nextInt(subChromosome1.size());

        // Recombination for child 1
        ArrayList<Integer> child1SubChromosome = new ArrayList<Integer>(
                subChromosome1.subList(Math.min(cutA1, cutA2), Math.max(cutA1, cutA2)));

        // list of introduced customers
        ArrayList<Integer> newCustomerIndexes = new ArrayList<Integer>();

        for (Integer customerIndex : subChromosome2) {
            if (!child1SubChromosome.contains(customerIndex)) {
                child1SubChromosome.add(customerIndex);
                // add customers introduced to the route in a list
                if (!subChromosome1.contains(customerIndex)) {
                    newCustomerIndexes.add(customerIndex);
                    System.out.println("added customerindex: " + customerIndex);
                }
            }
        }

        ArrayList<ArrayList<Integer>> child1Chromosome = parent1.cloneChromosome();

        if (newCustomerIndexes.size() != 0) {
            // Clean the chromosome for introduced customers
            child1Chromosome = cleanChromosome(child1Chromosome, newCustomerIndexes, depotIndex);
        }

        // Add customers that were skipped by the other parent
        for (Integer customerIndex : subChromosome1) {
            if (!child1SubChromosome.contains(customerIndex)) {
                child1SubChromosome.add(customerIndex);
                child1Chromosome.set(depotIndex, child1SubChromosome);
            }
        }
        // create new Individual from chromosome
        Individual child1 = new Individual();
        child1.addChromosome(child1Chromosome);
        children.add(child1);

        // // Recombination for child 2
        // ArrayList<Integer> child2SubChromosome = new ArrayList<Integer>(
        // subChromosome2.subList(Math.min(cutB1, cutB2), Math.max(cutB1, cutB2)));

        // // list of introduced customers
        // ArrayList<Integer> newCustomerIndexesB = new ArrayList<Integer>();
        // for (Integer customerIndex : subChromosome1) {
        // if (!child2SubChromosome.contains(customerIndex)) {
        // child2SubChromosome.add(customerIndex);
        // // add customers introduced to the route in a list
        // if (!subChromosome2.contains(customerIndex)) {
        // newCustomerIndexesB.add(customerIndex);
        // }
        // }
        // }

        // ArrayList<ArrayList<Integer>> child2Chromosome = parent2.cloneChromosome();

        // if (newCustomerIndexesB.size() != 0) {
        // // Clean the chromosome for introduced customers
        // cleanChromosome(child2Chromosome, newCustomerIndexesB, depotIndex);
        // }

        // // Add customers that were skipped by the other parent
        // for (Integer customerIndex : subChromosome2) {
        // if (!child2SubChromosome.contains(customerIndex)) {
        // child2SubChromosome.add(customerIndex);
        // child2Chromosome.set(depotIndex, child2SubChromosome);
        // }
        // }
        // // create new Individual from chromosome
        // Individual child2 = new Individual();
        // child2.addChromosome(child2Chromosome);
        children.add(parent2);

        return children;
    }

    public ArrayList<ArrayList<Integer>> cleanChromosome(ArrayList<ArrayList<Integer>> chromosome,
            ArrayList<Integer> faultyCustomer, int depotIndex) {

        for (int i = 0; i < chromosome.size(); i++) {
            if (i == depotIndex)
                continue;
            for (int j = 0; j < chromosome.get(i).size(); j++) {
                for (int k = 0; k < faultyCustomer.size(); k++) {
                    if (chromosome.get(i).get(j) == faultyCustomer.get(k)) {
                        chromosome.get(i).remove(j);
                    }
                }
            }
        }

        return chromosome;

    }

    // Swap mutation, performs swap on a random depot
    public Individual swapMutation(Individual individual, Model model) {

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
    public Individual insertMutation(Individual individual, Model model) {

        ArrayList<ArrayList<Integer>> chromosome = individual.cloneChromosome();

        // Randomly choose depot to undergo mutation
        int depotIndex = this.random.nextInt(model.depotList.size());
        ArrayList<Integer> subChromosome = chromosome.get(depotIndex);

        // index to move
        int index2 = this.random.nextInt(subChromosome.size());

        int element = subChromosome.get(index2);
        subChromosome.remove(index2);

        // Index to mark
        int index1 = this.random.nextInt(subChromosome.size());

        if (index1 == subChromosome.size() - 1) {
            subChromosome.add(0, element);
        } else {
            subChromosome.add(index1 + 1, element);
        }
        chromosome.set(depotIndex, subChromosome);

        Individual mutatedIndividual = new Individual();
        mutatedIndividual.addChromosome(chromosome);

        return mutatedIndividual;
    }

    // Scramble mutation, performs scramble on a slice of subchromosome
    public Individual scrambleMutation(Individual individual, Model model) {

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

    // moves a customer from a depot to another, placing it randomly in the other
    // list
    public Individual interDepotMutation(Individual individual, Model model) {

        ArrayList<ArrayList<Integer>> chromosome = individual.cloneChromosome();

        // Randomly choose depot to undergo mutation
        int originDepot = this.random.nextInt(model.depotList.size());
        ArrayList<Integer> subChromosome = chromosome.get(originDepot);

        // Randomly choose customer to move
        int customerIndex = this.random.nextInt(subChromosome.size());

        // Record customerID
        int customerID = subChromosome.get(customerIndex);

        // remove customer from depot
        subChromosome.remove(customerIndex);
        System.out.println("removed in mutation: " + customerIndex);
        chromosome.set(originDepot, subChromosome);

        int destinationDepot = this.random.nextInt(model.depotList.size());
        // To avoid adding to the same depot
        while (destinationDepot == originDepot) {
            destinationDepot = this.random.nextInt(model.depotList.size());
        }

        int placementIndex = this.random.nextInt(chromosome.get(destinationDepot).size());
        chromosome.get(destinationDepot).add(placementIndex, customerID);

        Individual mutatedIndividual = new Individual();
        mutatedIndividual.addChromosome(chromosome);

        return mutatedIndividual;
    }

}