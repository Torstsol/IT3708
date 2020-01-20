package model;

import model.Customer;
import java.util.ArrayList;

public class Depot{

    int maxDuration;
    int maxLoad;

    public int xCoordinate;
    public int yCoordinate;

    public ArrayList<Customer> assignedCustomers = new ArrayList<Customer>();

    public Depot (int maxDuration, int maxLoad){
        this.maxDuration = maxDuration;
        this.maxLoad = maxLoad;
    }

    public void setPosition(int x, int y){
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public void addCustomer(Customer customer){
        this.assignedCustomers.add(customer);
    }

    public ArrayList<Customer> getCustomers(){
        return this.assignedCustomers;
    } 

    @Override
    public String toString(){
        return "maxDuration: " + maxDuration + " maxLoad: " + maxLoad + " Coordinates: " + xCoordinate + "." + yCoordinate; 
    }

}