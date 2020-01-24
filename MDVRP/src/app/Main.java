package app;

import utils.IOManager;
import utils.RouteScheduler;
import model.Model;
import algorithm.Algorithm;


public class Main {

    public static void main(String[] args) throws Exception {

        //Parameters
        int populationSize = 10;

        //Initialize the customers and depots from file
        IOManager manager = new IOManager();
        Model model = new Model();
        Algorithm algorithm = new Algorithm();
        RouteScheduler scheduler = new RouteScheduler();

        manager.parseFile(model);

        // Assign customers to depots based on shortest euclidian distance
        manager.assignCustomersToDepots(model.depotList, model.customerList);

        // Generate pseudochromosome
        model.addPseudoChromosome(manager.generateDepotCustomerIntegerList(model.depotList));

        //seed population based on seudochromosome
        model.addPopulation(algorithm.seedPopulation(model.getPseudoChromosome(), populationSize));
        
        //generate solutions from population
        scheduler.generatePopulationRoutes(model.getPopulation(), model);

    }
}