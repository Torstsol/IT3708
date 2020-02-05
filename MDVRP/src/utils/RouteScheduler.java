package utils;

import java.util.ArrayList;
import model.Route;
import model.Depot;
import model.Individual;
import model.Customer;
import model.Model;

public class RouteScheduler {

    public void generatePopulationRoutes(ArrayList<Individual> population, Model model) {

        // For each chromosome in population
        for (int i = 0; i < population.size(); i++) {
            ArrayList<ArrayList<Route>> solution = new ArrayList<ArrayList<Route>>();

            // get each depot pr. chromosome
            for (int j = 0; j < population.get(i).getChromosome().size(); j++) {
                Depot depot = model.depotList.get(j);
                // Iterate through each customer pr. depot
                ArrayList<Customer> customers = new ArrayList<Customer>();

                for (int k = 0; k < population.get(i).getChromosome().get(j).size(); k++) {
                    customers.add(
                            model.customerList.get(population.get(i).getChromosome().get(j).get(k).intValue() - 1));
                }
                ArrayList<Route> routes = generateDepotRoutes(depot, customers);
                optimizeRoutes(routes, depot);
                solution.add(routes);
            }
            population.get(i).addPhenotype(solution);

        }
    }

    public ArrayList<Route> generateDepotRoutes(Depot depot, ArrayList<Customer> customers) {

        int depotX = depot.xCoordinate;
        int depotY = depot.yCoordinate;

        int maxDuration = depot.getMaxDuration();
        int maxLoad = depot.getMaxLoad();

        ArrayList<Route> routes = new ArrayList<Route>();

        // instantiate the first route outside the loop
        Route route = new Route(depot);
        routes.add(route);

        // phase 1
        for (int i = 0; i < customers.size(); i++) {

            // calculate distance from current customer to depot
            double depotDistance = IOManager.euclidianDistance(depotX, depotY, customers.get(i).xCoordinate,
                    customers.get(i).yCoordinate);

            double subDistance = 0;
            // if not first customer in route, calculate inter-customer distance
            if (route.geCustomers().size() != 0) {
                subDistance = IOManager.euclidianDistance(
                        route.geCustomers().get(route.geCustomers().size() - 1).xCoordinate,
                        route.geCustomers().get(route.geCustomers().size() - 1).yCoordinate,
                        customers.get(i).xCoordinate, customers.get(i).yCoordinate);
            }
            // if feasible with regards to load and duration
            if (route.getLoad() + customers.get(i).demand <= maxLoad
                    && (route.getDistance() + subDistance + depotDistance < maxDuration || maxDuration == 0)) {

                // Add the depotDistance for first customer of the depot (not covered by
                // addFirstCustomerToDepot)
                if (route.geCustomers().size() == 0) {
                    route.addDistance(depotDistance);
                }
                route.addDistance(subDistance);
                route.addCustomer(customers.get(i));
                route.addLoad(customers.get(i).demand);

                // if last customer in loop, add depotdistance
                if (i == customers.size() - 1) {
                    route.addDistance(IOManager.euclidianDistance(depotX, depotY, customers.get(i).xCoordinate,
                            customers.get(i).yCoordinate));
                    continue;
                }
            } else {
                // Duration- or load-constraints reached

                // Add distance back to depot from previous customer
                depotDistance = IOManager.euclidianDistance(depotX, depotY, customers.get(i - 1).xCoordinate,
                        customers.get(i - 1).yCoordinate);
                route.addDistance(depotDistance);
                route = addFirstCustomerToNewRoute(depot, customers.get(i), i, customers.size());

                // add the new route to the list of routes
                routes.add(route);
            }
        }
        return routes;
    }

    public Route addFirstCustomerToNewRoute(Depot depot, Customer customer, int i, int customerSize) {
        int depotX = depot.xCoordinate;
        int depotY = depot.yCoordinate;

        Route route = new Route(depot);
        double depotDistance = 0;

        // if there is only one customer left for the depot
        if (i == customerSize - 1) {
            depotDistance = IOManager.euclidianDistance(depotX, depotY, customer.xCoordinate, customer.yCoordinate);
        }

        depotDistance = depotDistance
                + IOManager.euclidianDistance(depotX, depotY, customer.xCoordinate, customer.yCoordinate);
        route.addCustomer(customer);
        route.addDistance(depotDistance);
        route.addLoad(customer.demand);

        return route;
    }

    public void optimizeRoutes(ArrayList<Route> routes, Depot depot){

        for (int i=0; i<routes.size()-1; i++){

            Route routeA = routes.get(i);
            Route routeB = routes.get(i+1);

            ArrayList<Customer> customersA = routeA.copyCustomers();
            ArrayList<Customer> customersB = routeB.copyCustomers();

            double currentFitness = routeA.getDistance() + routeB.getDistance();;

            //Get the last customer in route A
            Customer customer = customersA.get(routes.get(i).geCustomers().size()-1);
            
            //Create new Routes A 
            Route newRouteA = new Route(depot);
            newRouteA.addDistance(0.00);
            customersA.remove(customersA.size()-1);
            if (customersA.size() != 0){
                newRouteA.addCustomerList(customersA);
                newRouteA.addDistance(getDistanceOfRoute(newRouteA, depot));
            }

            //and B with last customer of A swapped to B
            Route newRouteB = new Route(depot);
            customersB.add(0, customer);
            newRouteB.addCustomerList(customersB);
            newRouteB.addDistance(getDistanceOfRoute(newRouteB, depot));

            double newFitness = newRouteA.getDistance() + newRouteB.getDistance();

            if (newFitness < currentFitness){
                if(newRouteA.geCustomers().size() == 0){
                    if (isRouteFeasible(newRouteB, depot)){
                        routes.set(i+1, newRouteB);
                        routes.remove(i);
                        i = 0;
                    }

                }
                else if (isRouteFeasible(newRouteA, depot) && isRouteFeasible(newRouteB, depot)){
                    routes.set(i, newRouteA);
                    routes.set(i+1, newRouteB);
                    i = 0;
                }
            }
        }
    }

    public double getDistanceOfRoute(Route route, Depot depot) {

        double subDistance = 0;
        double depotDistance = 0;

        depotDistance = depotDistance + IOManager.euclidianDistance(depot.xCoordinate, depot.yCoordinate,
                route.geCustomers().get(0).xCoordinate, route.geCustomers().get(0).yCoordinate);

        for (int i = 0; i < route.geCustomers().size() - 1; i++) {
            subDistance = subDistance + IOManager.euclidianDistance(route.geCustomers().get(i).xCoordinate,
                    route.geCustomers().get(i).yCoordinate, route.geCustomers().get(i + 1).xCoordinate,
                    route.geCustomers().get(i + 1).yCoordinate);
        }

        depotDistance = depotDistance + IOManager.euclidianDistance(depot.xCoordinate, depot.yCoordinate,
                route.geCustomers().get(route.geCustomers().size() - 1).xCoordinate,
                route.geCustomers().get(route.geCustomers().size() - 1).yCoordinate);

        return subDistance + depotDistance;

    }
    //Feasibility measure with regards to duration and load
    public boolean isRouteFeasible(Route route, Depot depot){

        double maxDuration = depot.getMaxDuration();
        double maxLoad = depot.getMaxLoad();

        if ((route.getDistance() <= maxDuration || maxDuration == 0.00) && route.getLoad() <= maxLoad){
            return true;
        }
        return false;

    }
}