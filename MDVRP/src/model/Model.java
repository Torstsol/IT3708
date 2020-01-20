package model;

import java.util.ArrayList;

public class Model {

    //Real-world related model
    private int vehicles;
    public ArrayList<Depot> depotList = new ArrayList<Depot>();
    public ArrayList<Customer> customerList = new ArrayList<Customer>();


    //GA-related model
    ArrayList<ArrayList<Integer>> pseudoChromosome = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<ArrayList<Integer>>> population = new ArrayList<ArrayList<ArrayList<Integer>>>();

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
}