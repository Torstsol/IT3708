package model;

import java.util.ArrayList;

public class Route {

    double distance = 0.0;
    int load = 0;
    ArrayList<Customer> customers = new ArrayList<Customer>();
    Depot depot;

    public Route(Depot depot){
        this.depot = depot;
    }

    public ArrayList<Customer> geCustomers(){
        return this.customers;
    }

    public void addCustomer(Customer customer){
        this.customers.add(customer);
    }

    public void addDistance(double distance){
        this.distance = this.distance + distance;
    }

    public void addLoad(int load){
        this.load = this.load + load;
    }

    public int getLoad(){
        return this.load;
    }

    public double getDistance(){
        return this.distance;
    }

    public void addCustomerList(ArrayList<Customer> customers){
        this.customers = customers;
        this.load = customers.stream().map(Customer::getDemand).reduce(Integer::sum).get();
    }

    public ArrayList<Customer> copyCustomers(){
        return new ArrayList<Customer>(this.customers);
    }



}