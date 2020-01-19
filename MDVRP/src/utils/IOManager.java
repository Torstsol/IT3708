package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

import model.Model;
import model.Depot;
import model.Customer;

public class IOManager{

    public void parseFile(Model model) throws FileNotFoundException {

        Scanner scan = new Scanner(new File("/home/torstein/Documents/ntnu/it3708/project1/Testing_Data/Data_Files/p01"));

        //Scan the first line of the input file to obtain the number of vehicles, customers and depots
        String[] list = scan.nextLine().split(" ");
        model.setVehicles(Integer.parseInt(list[0]));

        for(int i=0; i<Integer.parseInt(list[2]); i++){  
            //initialize depot_capacity
            model.addDepot(new Depot(scan.nextInt(), scan.nextInt()));
        }
        for(int i=0; i<Integer.parseInt(list[1]); i++){  
            //initialize customers
            model.addCustomer(new Customer(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt()));
            //should be replaced by some regex and scan.skip(Pattern pattern)
            scan.nextInt();
            scan.nextInt();
            scan.nextInt();
            scan.nextInt();
            scan.nextInt();
            scan.nextInt();
        }
        for(int i=0; i<Integer.parseInt(list[2]); i++){ 
            scan.nextInt(); 
            model.depotList.get(i).setPosition(scan.nextInt(), scan.nextInt());
            //should be replaced by some regex and scan.skip(Pattern pattern)
            scan.nextInt();
            scan.nextInt();
            scan.nextInt();
            scan.nextInt();
        }
        scan.close();
    }

    public void assignCustomersToDepots(ArrayList<Depot> depots, ArrayList<Customer> customers){
        for (Customer customer: customers){
            double lowestDistance = 1000.0;
            int depotID = -1;
            for (int i = 0; i < depots.size(); i++) {
                double currentDistance = this.euclidianDistance(customer.xCoordinate, customer.yCoordinate, depots.get(i).xCoordinate, depots.get(i).yCoordinate);
                if (currentDistance < lowestDistance){
                    lowestDistance = currentDistance;
                    depotID = i;
                }
            }
            System.out.println("CustomerNumber: " + customer.customerID + " Assigned Depot: " + depotID + " Shortest Euclidian distance: " + lowestDistance);
        }
    }

    public double euclidianDistance(int x1, int y1, int x2, int y2){
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        double result = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return result; 
    }

}