package app;

import utils.IOManager;
import model.Model;


public class Main {
    public static void main(String[] args) throws Exception {

        //Initialize the models from file
        IOManager manager = new IOManager();
        Model model = new Model();
        manager.parseFile(model);
        manager.assignCustomersToDepots(model.depotList, model.customerList);

    }
}