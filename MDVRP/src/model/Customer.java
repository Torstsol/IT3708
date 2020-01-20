package model;

public class Customer{

    public int customerID;
    public int xCoordinate;
    public int yCoordinate;
    public int serviceDuration;
    public int demand;

    public Customer (int customerID, int xCoordinate, int yCoordinate, int serviceDuration, int demand){
        this.customerID = customerID;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.serviceDuration = serviceDuration;
        this.demand = demand;
    }

    public String toString(){
        return "customerID: " + this.customerID; // + " Position: " + this.xCoordinate + "." + this.yCoordinate;
    }
}