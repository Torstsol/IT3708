package app;

import utils.IOManager;
import model.Model;
import model.Depot;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws Exception {

        //Initialize the customers and depots from file
        IOManager manager = new IOManager();
        Model model = new Model();
        manager.parseFile(model);

        // Assign customers to depots based on shortest euclidian distance
        manager.assignCustomersToDepots(model.depotList, model.customerList);

        // Generate lists that can permutate to chromosomes
        System.out.println(manager.generateDepotCustomerIntegerList(model.depotList));
    }
}