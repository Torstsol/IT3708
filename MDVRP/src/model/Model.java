package model;

import java.util.ArrayList;

public class Model {

    //Real-world related model
    private int vehicles;
    public ArrayList<Depot> depotList = new ArrayList<Depot>();
    public ArrayList<Customer> customerList = new ArrayList<Customer>();

    //For visualization
    public double maxCoordinate = 0;
    public double minCoordinate = 0;


    //GA-related model
    ArrayList<ArrayList<Integer>> pseudoChromosome = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<ArrayList<Integer>>> population = new ArrayList<ArrayList<ArrayList<Integer>>>();

    //Generated schedules
    ArrayList<ArrayList<ArrayList<Route>>> populationSchedules = new ArrayList<ArrayList<ArrayList<Route>>>();
    ArrayList<ArrayList<Integer>> solution = new ArrayList<ArrayList<Integer>>();

    public void setVehicles(int vechicles){
        this.vehicles = vechicles;
    }

    public void addDepot(Depot depot){
        this.depotList.add(depot);
    }
    public void addCustomer(Customer customer){
        this.customerList.add(customer);
    }

    public void addPseudoChromosome(ArrayList<ArrayList<Integer>> pseudoChromosome){
        this.pseudoChromosome = pseudoChromosome;
    }

    public void addChromosomeToPopulation(ArrayList<ArrayList<Integer>> pseudoChromosome){
        this.population.add(pseudoChromosome);
    }

    public ArrayList<ArrayList<Integer>> getPseudoChromosome(){
        return this.pseudoChromosome;
    }

    public void addPopulation(ArrayList<ArrayList<ArrayList<Integer>>> population){
        this.population = population;
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> getPopulation(){
        return this.population;
    }

    public void addPopulationSchedules(ArrayList<ArrayList<ArrayList<Route>>> populationSchedules){
        this.populationSchedules = populationSchedules;
    }

    public ArrayList<ArrayList<ArrayList<Route>>> getPopulationSchedules(){
        return this.populationSchedules;
    }
}