package utils;

import java.util.ArrayList;
import model.Route;
import model.Depot;
import model.Customer;
import model.Model;

public class RouteScheduler{

    public ArrayList<ArrayList<Route>> generatePopulationRoutes(ArrayList<ArrayList<ArrayList<Integer>>> population, Model model){

        ArrayList<ArrayList<Route>> solutionList = new ArrayList<ArrayList<Route>>();

        //For each chromosome in population
        for(int i=0; i<population.size(); i++){
            //get each depot pr. chromosome

            for(int j=0; j<population.get(i).size(); j++){
                Depot depot = model.depotList.get(j);
                //Iterate through each customer pr. depot
                ArrayList<Customer> customers = new ArrayList<Customer>();

                for (int k=0; k<population.get(i).get(j).size(); k++){
                    customers.add(model.customerList.get(population.get(i).get(j).get(k).intValue()-1));
                }
                // System.out.println(customers);
                solutionList.add(generateDepotRoutes(depot, customers));
            }
        }

        return solutionList;

    }

    public ArrayList<Route> generateDepotRoutes(Depot depot, ArrayList<Customer> customers){

        int depotX = depot.xCoordinate;
        int depotY = depot.yCoordinate;

        int vehicles = depot.getVehicles();
        int maxDuration = depot.getMaxDuration();
        int maxLoad = depot.getMaxLoad();

        System.out.println(maxDuration);

        ArrayList<Route> routes = new ArrayList<Route>();
        
        //instantiate the first route outside the loop
        Route route = new Route(depot);

        // phase 1
        for(Customer customer: customers){
            if(routes.size() == 0){
                route = addFirstCustomerToNewRoute(depot, customer);
                routes.add(route);
            }
            else{
                if (route.getLoad() + customer.demand < maxLoad){
                    double subDistance = IOManager.euclidianDistance(route.geCustomers().get(route.geCustomers().size()-1).xCoordinate, route.geCustomers().get(route.geCustomers().size()-1).yCoordinate, customer.xCoordinate, customer.yCoordinate);
                    double depotDistance = IOManager.euclidianDistance(depotX, depotY, customer.xCoordinate, customer.yCoordinate);
                    if (route.getDistance() + subDistance + depotDistance < maxDuration || maxDuration == 0 ){
                        route.addDistance(subDistance);
                        route.addCustomer(customer);
                        route.addLoad(customer.demand);
                    }
                    else{
                        System.out.println("max duration reached");
                        routes.add(route);
                        route = addFirstCustomerToNewRoute(depot, customer);
                    }
                }
                else{
                    System.out.println("max load reached");
                    routes.add(route);
                    route = addFirstCustomerToNewRoute(depot, customer);
                }
            }

        }

        return null;
    }


    public Route addFirstCustomerToNewRoute(Depot depot, Customer customer){
        int depotX = depot.xCoordinate;
        int depotY = depot.yCoordinate;

        Route route = new Route(depot);

        double depotDistance = IOManager.euclidianDistance(depotX, depotY, customer.xCoordinate, customer.yCoordinate);
        route.addCustomer(customer);
        route.addDistance(depotDistance);
        route.addLoad(customer.demand);

        return route;
    }
}