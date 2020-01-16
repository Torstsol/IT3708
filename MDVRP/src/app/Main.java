package app;

import utils.IOManager;
import model.Model;

public class Main {
    public static void main(String[] args) throws Exception {
        IOManager manager = new IOManager();
        Model model = new Model();
        manager.parseFile(model);
        System.out.println("hello");
        System.out.println(model.depotList.toString());
        System.out.println(model.customerList.toString());

    }
}