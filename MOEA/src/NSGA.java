import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class NSGA {

     //Variables
     String imageString = "Training/86016/Test image.jpg";
     int populationSize = 25;
     int generationNumber = 100;
     double mutationRate = 0.2;
     private int minSegmentSize = 400;
     private List<ChromosomeNew> population = new ArrayList<>();
     private ArrayList<ArrayList<ChromosomeNew>> rankedPopulation = new ArrayList<>();
    public static void main(String[] args){

        NSGA nsga = new NSGA();
        nsga.mainLoop();
    }

    private void mainLoop(){
        ImageMatrix imageMatrix = new ImageMatrix(this.imageString);
        createPopulation(imageMatrix);
        rankPopulation();

        //createNewPopulationBasedOnRank();

        int generation = 1;

        while (generation < this.generationNumber) {
            //Print status
            printStatus(generation);
            //Create offsprings
            List<ChromosomeNew> children = createChildren(false, imageMatrix);
            population.addAll(children);
            rankPopulation();
            createNewPopulationBasedOnRank();
            //Should we use selection, crossover and mutation to create a new population of size N here?
            generation++;

        }

        System.out.println("Number of members in rank 1: " + rankedPopulation.get(0).size());
        System.out.println("Number of pareto fronts: " + rankedPopulation.size());

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (File file : Objects.requireNonNull(new File("Evaluator/Student_Segmentation_Files/").listFiles()))
            if (!file.isDirectory())
                file.delete();

        for (File file : Objects.requireNonNull(new File("Evaluator/Student_Segmentation_Files_Green/").listFiles()))
            if (!file.isDirectory())
                file.delete();

        for (int i = 0; i < rankedPopulation.get(0).size(); i++) {
            final int index = i;
            System.out.println("Merging for member: " + index);

            ChromosomeNew contestant = rankedPopulation.get(0).get(index);
            executorService.execute(() -> {
                contestant.mergeAllSmallerThanN(this.minSegmentSize, 0);
                imageMatrix.saveAsBlackAndWhite("Evaluator/Student_Segmentation_Files/testimage" + index +
                        "Deviation" + contestant.getDeviation() + "Connectivity" + contestant.getConnectivity(), contestant);
                imageMatrix.saveAsGreen("Evaluator/Student_Segmentation_Files_Green/testimage" + index +
                        "Deviation" + contestant.getDeviation() + "Connectivity" + contestant.getConnectivity(), contestant);
                contestant.findSegments();
                contestant.deviation = contestant.overallDeviation();
                contestant.overallConnectivity = contestant.overallConnectivity();
                System.out.println("Finished writing number: " + index);

            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated());

    }

    private void createPopulation(ImageMatrix imageMatrix){

        //Wraps a list to create a synchronized list, for exploitation of multithreaded initialisation
        List<ChromosomeNew> populationInProgress = Collections.synchronizedList(new ArrayList<>(populationSize * 2));

        //Creates N-threads, where N is available processors
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < this.populationSize * 2; i++) {
            final int index = i+1;
            System.out.println("Progress: "+index+"/"+this.populationSize*2);
            executorService.execute(() -> {
                ChromosomeNew temp = new ChromosomeNew(imageMatrix, ThreadLocalRandom.current().nextInt(5, 30));
                //temp.mergeAllSmallerThanN(this.runMinSegmentSize, 0);
                populationInProgress.add(temp);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        this.population.addAll(populationInProgress);

        //Testing
        for(ChromosomeNew chromosome: this.population){
            imageMatrix.saveAsBlackAndWhite("imagesNew/"+chromosome.hashCode(), chromosome);
        }
    } 

    private void rankPopulation() {
        rankedPopulation.clear();
        ArrayList<ChromosomeNew> rankList;
        int rank = 1;
        while (this.population.size() > 0) {
            rankList = fastNondominatedSort(this.population);
            for (ChromosomeNew x : rankList) {
                x.setRank(rank);
            }
            rankedPopulation.add(rankList);
            population.removeAll(rankList);
            rank++;
        }
        for (List<ChromosomeNew> list : rankedPopulation) {
            population.addAll(list);
        }
    }

    //TODO: Check for bugs. Has not been tested with solutions dominating each other
    private ArrayList<ChromosomeNew> fastNondominatedSort(List<ChromosomeNew> population) {
        Set<ChromosomeNew> non_dominated_set = new HashSet<>();
        //Include first member in P'
        non_dominated_set.add(population.get(0));
        List<ChromosomeNew> isDominated = new ArrayList<>();

        for (ChromosomeNew individual : population) {
            if (isDominated.contains(individual)) {
                continue;
            }
            //Include p in P' temporarily
            non_dominated_set.add(individual);
            //Compare p with other members of P'
            for (ChromosomeNew non_dominated : non_dominated_set) {
                if (isDominated.contains(individual)) {
                    continue;
                }
                if (non_dominated == individual) {
                    continue;
                }
                //If p dominates a member of P', delete it
                if (individual.getConnectivity() < non_dominated.getConnectivity() && individual.getDeviation() < non_dominated.getDeviation()) {
                    isDominated.add(non_dominated);
                    //if p is dominated by other members of P', do not include p in P'
                } else if (individual.getConnectivity() > non_dominated.getConnectivity() && individual.getDeviation() > non_dominated.getDeviation()) {
                    isDominated.add(individual);
                }
            }
        }
        non_dominated_set.removeAll(isDominated);
        return new ArrayList<>(non_dominated_set);
    }

    private void printStatus(int generation) {
        System.out.println("---------------------");
        System.out.println("This is generation: " + generation);
        System.out.println("Size of population " + this.population.size());
        System.out.println("The number of pareto fronts are: " + this.rankedPopulation.size());
        System.out.println("The number of members in the best front is: " + this.rankedPopulation.get(0).size());
    }

    private List<ChromosomeNew> createChildren(boolean generationZero, ImageMatrix imageMatrix) {
        int multiplier = (generationZero) ? 2 : 1;
        List<ChromosomeNew> children = Collections.synchronizedList(new ArrayList<>(this.populationSize));
        //List<Chromosome> children = new ArrayList<>(this.childPopulationNumber);

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < this.populationSize * multiplier; i++) {
            final int index = i;
            executorService.execute(() -> {
                ChromosomeNew father = selectParent();
                ChromosomeNew mother = selectParent();
                ChromosomeNew child = new ChromosomeNew(father, mother, mutationRate, imageMatrix);
                //child.mergeAllSmallerThanN(this.runMinSegmentSize, 0);
                children.add(child);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        return children;
    }

    private ChromosomeNew selectParent() {
        //Binary tournament selection
        int indx1;
        int indx2;
        indx1 = new SplittableRandom().nextInt(0, population.size());
        indx2 = new SplittableRandom().nextInt(0, population.size());
        while (indx1 == indx2) {
            indx2 = new SplittableRandom().nextInt(0, population.size());
        }
        ChromosomeNew p1 = population.get(indx1);
        ChromosomeNew p2 = population.get(indx2);
        if (ChromosomeNew.nonDominatedCrowdingComparator().compare(p1, p2) < 0) return p1;
        return p2;
    }

    private void createNewPopulationBasedOnRank() {
        population.clear();
        for (List<ChromosomeNew> pareto_front : rankedPopulation) {
            crowdingDistanceAssignment(pareto_front);
            //Add the pareto-front to the population if space
            if (pareto_front.size() <= this.populationSize - population.size()) {
                population.addAll(pareto_front);
            } else {
                ArrayList<ChromosomeNew> pareto_front_copy = new ArrayList<>(pareto_front);
                pareto_front_copy.sort(ChromosomeNew.nonDominatedCrowdingComparator());
                while (population.size() < this.populationSize) {
                    population.add(pareto_front_copy.remove(0));
                }
            }
        }

    }

    private void crowdingDistanceAssignment(List<ChromosomeNew> pareto_front) {
        //initialize distance to 0
        for (ChromosomeNew chromosome : pareto_front) chromosome.setCrowding_distance(0);
        crowdingDistanceAssignmentPerObjective(pareto_front, 0);
        crowdingDistanceAssignmentPerObjective(pareto_front, 1);
    }

    private void crowdingDistanceAssignmentPerObjective(List<ChromosomeNew> pareto_front, int sortingObjectiveIndex) {
        if (sortingObjectiveIndex == 0) pareto_front.sort(ChromosomeNew.deviationComparator());
        if (sortingObjectiveIndex == 1) pareto_front.sort(ChromosomeNew.connectivityComparator());
        //Set boundries so they are always detected
        pareto_front.get(0).setCrowding_distance(Double.POSITIVE_INFINITY);
        pareto_front.get(pareto_front.size() - 1).setCrowding_distance(Double.POSITIVE_INFINITY);

        for (int i = 1; i < pareto_front.size() - 1; i++) {
            if (sortingObjectiveIndex == 0) {
                pareto_front.get(i).setCrowding_distance(pareto_front.get(i).getCrowding_distance() + (pareto_front.get(i + 1).getDeviation() - pareto_front.get(i - 1).getDeviation()));
            } else {
                pareto_front.get(i).setCrowding_distance(pareto_front.get(i).getCrowding_distance() + (pareto_front.get(i + 1).getConnectivity() - pareto_front.get(i - 1).getConnectivity()));
            }
        }

    }
    
}
