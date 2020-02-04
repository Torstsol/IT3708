package app;

import utils.IOManager;
import utils.Randomizer;
import utils.RouteScheduler;
import visualization.Visualizer;
import model.Model;

import java.util.ArrayList;

import algorithm.Algorithm;
import model.Individual;

public class Main {

    public static void main(String[] args) throws Exception {

        // Parameters
        int populationSize = 50;
        int numberOfElites = 10;
        int numberOfGenerations = 20000;
        double crossoverChance = 0.8;

        // Tournament selection
        int tournamentSize = 2;
        double pressure = 0.7;

        // Testing data
        String fileName = "Testing_Data/Data_Files/p05";

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
                // Tournament selection
                Individual parent1 = algorithm.tournamentSelection(model.getPopulation(), pressure, tournamentSize);
                Individual parent2 = algorithm.tournamentSelection(model.getPopulation(), pressure, tournamentSize);
                
                if(Randomizer.check(crossoverChance)){
                    newPopulation.addAll(algorithm.crossover(model, parent1, parent2));
                }
                else{
                    newPopulation.add(parent1);
                    newPopulation.add(parent2);
                }
            }
            model.addPopulation(newPopulation);

            generation = generation+1;
        }

        // generate Answerfile from Individual
        manager.generateAnswerFile(bestIndividual);

        // visualize the Individual-solution
        Visualizer visualizer = new Visualizer(model.depotList, model.customerList, model.maxCoordinate,
                model.minCoordinate, bestIndividual.getPhenotype());

    }
}