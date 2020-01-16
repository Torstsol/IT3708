package model;

import java.util.ArrayList;

public class Model {

    private int vehicles;

    public ArrayList<Depot> depotList = new ArrayList<Depot>();
    public ArrayList<Customer> customerList = new ArrayList<Customer>();

    public void setVehicles(int vechicles){
        this.vehicles = vechicles;
    }

    public void addDepot(Depot depot){
        this.depotList.add(depot);
    }
    public void addCustomer(Customer customer){
        this.customerList.add(customer);
    }
}