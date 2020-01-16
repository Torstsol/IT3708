package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
}