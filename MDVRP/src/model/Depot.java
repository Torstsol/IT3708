package model;

public class Depot{

    int maxDuration;
    int maxLoad;

    int xCoordinate;
    int yCoordinate;

    public Depot (int maxDuration, int maxLoad){
        this.maxDuration = maxDuration;
        this.maxLoad = maxLoad;
    }

    public void setPosition(int x, int y){
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    @Override
    public String toString(){
        return "maxDuration: " + maxDuration + " maxLoad: " + maxLoad + " Coordinates: " + xCoordinate + "." + yCoordinate; 
    }

}