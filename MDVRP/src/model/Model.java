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
    ArrayList<Individual> population = new ArrayList<Individual>();

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

    public ArrayList<ArrayList<Integer>> getPseudoChromosome(){
        return this.pseudoChromosome;
    }

    public void addPopulation(ArrayList<Individual> population){
        this.population = population;
    }

    public ArrayList<Individual> getPopulation(){
        return this.population;
    }
}