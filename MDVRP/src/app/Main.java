package app;

import utils.IOManager;
import utils.RouteScheduler;
import visualization.Visualizer;
import model.Model;
import algorithm.Algorithm;
import model.Individual;


public class Main {

    public static void main(String[] args) throws Exception {

        //Parameters
        int populationSize = 10000;

        //Ubuntu
        //String fileName = "/home/torstein/Documents/ntnu/it3708/project1/Testing_Data/Data_Files/p01";

        //Windows
        String fileName = "Testing_Data\\Data_Files\\p01";

        //Initialize the customers and depots from file
        IOManager manager = new IOManager();
        Model model = new Model();
        Algorithm algorithm = new Algorithm();
        RouteScheduler scheduler = new RouteScheduler();

        manager.parseFile(model, fileName);

        // Assign customers to depots based on shortest euclidian distance
        manager.assignCustomersToDepots(model.depotList, model.customerList);

        // Generate pseudochromosome
        model.addPseudoChromosome(manager.generateDepotCustomerIntegerList(model.depotList));

        //seed population based on seudochromosome
        model.addPopulation(algorithm.seedPopulation(model.getPseudoChromosome(), populationSize));

        //Generate schedules for each individual in population
        scheduler.generatePopulationRoutes(model.getPopulation(), model);

        //Evaluate fitness
        algorithm.evaluateFitness(model.getPopulation());

        //get best individual
        Individual individual = algorithm.getBestIndividual(model.getPopulation());
        System.out.println("Fitness of best solution: " + individual.getFitness());
        
        //generate Answerfile from Individual
        manager.generateAnswerFile(individual);

        //visualize the Individual-solution
        Visualizer visualizer = new Visualizer(model.depotList, model.customerList, model.maxCoordinate, model.minCoordinate, individual.getPhenotype());

    }
}