package app;

import utils.IOManager;
import utils.Randomizer;
import utils.RouteScheduler;
import visualization.Visualizer;
import model.Model;

import java.util.ArrayList;

import algorithm.Algorithm;
import model.Individual;
import utils.Validator;

public class Main {

    public static void main(String[] args) throws Exception {

        // Parameters
        int populationSize = 100;
        int numberOfElites = 10;
        int numberOfGenerations = 20000;
        double crossoverChance = 0.80;
        double swapMutationChance = 0.10;
        double insertMutationChance = 0.10;
        double scrambleMutationChance = 0.10;

        // Tournament selection
        int tournamentSize = 2;
        double pressure = 0.65;

        // Testing data
        String fileName = "Testing_Data/Data_Files/p01";

        // Initialize the customers and depots from file
        IOManager manager = new IOManager();
        Model model = new Model();
        Algorithm algorithm = new Algorithm();
        RouteScheduler scheduler = new RouteScheduler();

        manager.parseFile(model, fileName);

        // Assign customers to depots based on shortest euclidian distance
        manager.assignCustomersToDepots(model.depotList, model.customerList);

        // Generate pseudochromosome
        model.addPseudoChromosome(manager.generateDepotCustomerIntegerList(model.depotList));

        // seed population based on pseudochromosome
        model.addPopulation(algorithm.seedPopulation(model.getPseudoChromosome(), populationSize));

        // Main loop
        Individual bestIndividual = new Individual();
        int generation = 0;
        while (generation < numberOfGenerations) {
            // Generate schedules for each individual in population
            scheduler.generatePopulationRoutes(model.getPopulation(), model);

            // Evaluate fitness for all individuals
            algorithm.evaluateFitness(model.getPopulation());

            // Get the n best solutions from population
            ArrayList<Individual> elites = algorithm.getElites(model.getPopulation(), numberOfElites);

            // get best individual and print generation
            bestIndividual = elites.get(0);
            double avg = model.getPopulation().stream().map(Individual::getFitness).reduce(Double::sum).get() / model.getPopulation().size();

            if(generation%10 == 0 || generation == numberOfGenerations-1){
                System.out.println("Generation: " + generation + " Fitness of best solution: " + bestIndividual.getFitness() + " Avg: fitness: " + avg);
            }
    

            // Initialize new population
            ArrayList<Individual> newPopulation = new ArrayList<Individual>();

            // add elites to new population
            newPopulation.addAll(elites);

            while (newPopulation.size() < populationSize){

                ArrayList<Individual> pair = new ArrayList<Individual>();

                // Tournament selection
                pair.add(algorithm.tournamentSelection(model.getPopulation(), pressure, tournamentSize));
                pair.add(algorithm.tournamentSelection(model.getPopulation(), pressure, tournamentSize));
                
                if(Randomizer.check(crossoverChance)){
                    ArrayList<Individual> children = algorithm.crossover(model, pair.get(0), pair.get(1));
                    pair.set(0, children.get(0));
                    pair.set(1, children.get(1));
                }
                if(Randomizer.check(swapMutationChance)){
                    pair.set(0, algorithm.swapMutation(pair.get(0), model));
                    pair.set(1, algorithm.swapMutation(pair.get(1), model));
                }
                if(Randomizer.check(insertMutationChance)){
                    pair.set(0, algorithm.insertMutation(pair.get(0), model));
                    pair.set(1, algorithm.insertMutation(pair.get(1), model));
                }
                if(Randomizer.check(scrambleMutationChance)){
                    pair.set(0, algorithm.scrambleMutation(pair.get(0), model));
                    pair.set(1, algorithm.scrambleMutation(pair.get(1), model));
                }

                newPopulation.addAll(pair);
            }
            model.addPopulation(newPopulation);

            generation = generation+1;
        }

        // generate Answerfile from Individual
        manager.generateAnswerFile(bestIndividual);

        // visualize the Individual-solution
        Visualizer visualizer = new Visualizer(model.depotList, model.customerList, model.maxCoordinate,
                model.minCoordinate, bestIndividual.getPhenotype());

        // evaluate the solution
        double epsilon = 1E-2;
        Validator validator = new Validator(epsilon);
        validator.evaluateFromFile("solutions/solution.txt", fileName);

    }
}