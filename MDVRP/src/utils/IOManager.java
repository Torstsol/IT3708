package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

import model.Model;
import model.Depot;
import model.Customer;
import model.Route;

public class IOManager{

    public void parseFile(Model model) throws FileNotFoundException {

        Scanner scan = new Scanner(new File("/home/torstein/Documents/ntnu/it3708/project1/Testing_Data/Data_Files/p01"));

        //Scan the first line of the input file to obtain the number of vehicles, customers and depots
        String[] list = scan.nextLine().split(" ");
        int vehicles = Integer.parseInt(list[0]);
        model.setVehicles(vehicles);

        for(int i=0; i<Integer.parseInt(list[2]); i++){  
            //initialize depot_capacity
            model.addDepot(new Depot(scan.nextInt(), scan.nextInt(), vehicles));
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
            depots.get(depotID).addCustomer(customer);
            // System.out.println("CustomerNumber: " + customer.customerID + " Assigned Depot: " + depotID + " Shortest Euclidian distance: " + lowestDistance);
        }
    }

    public ArrayList<ArrayList<Integer>> generateDepotCustomerIntegerList(ArrayList<Depot> depotList){
        ArrayList<ArrayList<Integer>> list = new  ArrayList<ArrayList<Integer>>();
        
        for(Depot depot: depotList){
            list.add(new ArrayList<Integer>());
            for (Customer customer: depot.getCustomers()){
                list.get(list.size()-1).add(customer.customerID);
            }
        }
        return list;

    }

    public static double euclidianDistance(int x1, int y1, int x2, int y2){
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        double result = Math.sqrt(deltaX*deltaX + deltaY*deltaY);
        return result; 
    }

    public void generateAnswerFile(ArrayList<ArrayList<Route>> answer){

        double solution = 100.0;

        File file = new File("solutions/solution.txt");
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);

            fr.write(solution+"\n");

            for (int i=0; i<answer.size(); i++){
                int depot = i+1;
                for (int j=0; j<answer.get(i).size(); j++){
                    int vehicle = j+1;

                    int routeLoad = answer.get(i).get(j).getLoad();
                    double distance = answer.get(i).get(j).getDistance();
                    double roundOffDistance = (double) Math.round(distance * 100) / 100;

                    fr.write(depot+"\t"+vehicle+"\t"+roundOffDistance+"\t"+routeLoad);

                    fr.write("\t"+0);

                    for(Customer customer: answer.get(i).get(j).geCustomers()){
                        fr.write(" "+customer.customerID);
                    }
                    fr.write(" "+0+"\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}