package utils;

import java.util.ArrayList;
import model.Route;
import model.Depot;
import model.Individual;
import model.Customer;
import model.Model;

public class RouteScheduler {

    public void generatePopulationRoutes(ArrayList<Individual> population, Model model) {

        ArrayList<ArrayList<ArrayList<Route>>> solutionList = new ArrayList<ArrayList<ArrayList<Route>>>();

        // For each chromosome in population
        for (int i = 0; i < population.size(); i++) {
            ArrayList<ArrayList<Route>> solution = new ArrayList<ArrayList<Route>>();

            // get each depot pr. chromosome
            for (int j = 0; j < population.get(i).getChromosome().size(); j++) {
                Depot depot = model.depotList.get(j);
                // Iterate through each customer pr. depot
                ArrayList<Customer> customers = new ArrayList<Customer>();

                for (int k = 0; k < population.get(i).getChromosome().get(j).size(); k++) {
                    customers.add(model.customerList.get(population.get(i).getChromosome().get(j).get(k).intValue() - 1));
                }
                solution.add(generateDepotRoutes(depot, customers));
            }
            population.get(i).addPhenotype(solution);

        }
    }

    public ArrayList<Route> generateDepotRoutes(Depot depot, ArrayList<Customer> customers) {

        int depotX = depot.xCoordinate;
        int depotY = depot.yCoordinate;

        int vehicles = depot.getVehicles();
        int maxDuration = depot.getMaxDuration();
        int maxLoad = depot.getMaxLoad();

        ArrayList<Route> routes = new ArrayList<Route>();

        // instantiate the first route outside the loop
        Route route = new Route(depot);
        routes.add(route);

        // phase 1
        for (Customer customer : customers) {
            double subDistance = 0;
            if (route.getLoad() + customer.demand <= maxLoad) {
                if (route.geCustomers().size() != 0) {
                    subDistance = IOManager.euclidianDistance(
                            route.geCustomers().get(route.geCustomers().size() - 1).xCoordinate,
                            route.geCustomers().get(route.geCustomers().size() - 1).yCoordinate, customer.xCoordinate,
                            customer.yCoordinate);
                }
                double depotDistance = IOManager.euclidianDistance(depotX, depotY, customer.xCoordinate,
                        customer.yCoordinate);
                if (route.getDistance() + subDistance + depotDistance < maxDuration || maxDuration == 0) {
                    route.addDistance(subDistance);
                    route.addCustomer(customer);
                    route.addLoad(customer.demand);
                } else {
                    // Max duration reached
                    route = addFirstCustomerToNewRoute(depot, customer);
                    routes.add(route);
                }
            } else {
                // Max load reached
                route = addFirstCustomerToNewRoute(depot, customer);
                routes.add(route);
            }

        }

        return routes;
    }

    public Route addFirstCustomerToNewRoute(Depot depot, Customer customer) {
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