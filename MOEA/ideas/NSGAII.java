import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class NSGAII {
    //Real number is 2x
    private int populationNumber = 50;
    private int childPopulationNumber = 50;
    private double mutationRate = 0.005;
    private int maxGenerationNumber = 10;
    private int minSegmentSize = 400;
    private int runMinSegmentSize = 30;
    private boolean toPlotOrNotToPlot = true;
    private List<Chromosome> population = new ArrayList<>();
    private ArrayList<ArrayList<Chromosome>> rankedPopulation = new ArrayList<>();


    //TODO: Check for bugs. Has not been tested with solutions dominating each other
    private ArrayList<Chromosome> fastNondominatedSort(List<Chromosome> population) {
        Set<Chromosome> non_dominated_set = new HashSet<>();
        //Include first member in P'
        non_dominated_set.add(population.get(0));
        List<Chromosome> isDominated = new ArrayList<>();

        for (Chromosome individual : population) {
            if (isDominated.contains(individual)) {
                continue;
            }
            //Include p in P' temporarily
            non_dominated_set.add(individual);
            //Compare p with other members of P'
            for (Chromosome non_dominated : non_dominated_set) {
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

    private void crowdingDistanceAssignment(List<Chromosome> pareto_front) {
        //initialize distance to 0
        for (Chromosome chromosome : pareto_front) chromosome.setCrowding_distance(0);
        if (pareto_front.get(0).isUseDeviation()) crowdingDistanceAssignmentPerObjective(pareto_front, 0);
        if (pareto_front.get(0).isUseConnectivity()) crowdingDistanceAssignmentPerObjective(pareto_front, 1);
    }

    private void crowdingDistanceAssignmentPerObjective(List<Chromosome> pareto_front, int sortingObjectiveIndex) {
        if (sortingObjectiveIndex == 0) pareto_front.sort(Chromosome.deviationComparator());
        if (sortingObjectiveIndex == 1) pareto_front.sort(Chromosome.connectivityComparator());
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

    private void initializePopulation(ImageMat loadImg) {

        Chromosome.img = loadImg;
        List<Chromosome> populationInProgress = Collections.synchronizedList(new ArrayList<>(this.populationNumber * 2));
        //List<Chromosome> populationInProgress = new ArrayList<>(this.populationNumber * 2);

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < this.populationNumber * 2; i++) {
            final int index = i;
            executorService.execute(() -> {
                System.out.println("Created individual numbered: " + index);
                Chromosome temp = new Chromosome(ThreadLocalRandom.current().nextInt(100, 1000));
                //temp.mergeAllSmallerThanN(this.runMinSegmentSize, 0);
                populationInProgress.add(temp);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        this.population.addAll(populationInProgress);
    }

    private void rankPopulation() {
        rankedPopulation.clear();
        ArrayList<Chromosome> rankList;
        int rank = 1;
        while (this.population.size() > 0) {
            rankList = fastNondominatedSort(this.population);
            for (Chromosome x : rankList) {
                x.setRank(rank);
            }
            rankedPopulation.add(rankList);
            population.removeAll(rankList);
            rank++;
        }
        for (List<Chromosome> list : rankedPopulation) {
            population.addAll(list);
        }
    }


    private List<Chromosome> createChildren(boolean generationZero) {
        int multiplier = (generationZero) ? 2 : 1;
        List<Chromosome> children = Collections.synchronizedList(new ArrayList<>(this.childPopulationNumber));
        //List<Chromosome> children = new ArrayList<>(this.childPopulationNumber);

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < this.childPopulationNumber * multiplier; i++) {
            final int index = i;
            executorService.execute(() -> {
                Chromosome father = selectParent();
                Chromosome mother = selectParent();
                Chromosome child = new Chromosome(father, mother, mutationRate);
                //child.mergeAllSmallerThanN(this.runMinSegmentSize, 0);
                children.add(child);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        return children;
    }

    private void createNewPopulationBasedOnRank() {
        population.clear();
        for (List<Chromosome> pareto_front : rankedPopulation) {
            crowdingDistanceAssignment(pareto_front);
            //Add the pareto-front to the population if space
            if (pareto_front.size() <= this.populationNumber - population.size()) {
                population.addAll(pareto_front);
            } else {
                ArrayList<Chromosome> pareto_front_copy = new ArrayList<>(pareto_front);
                pareto_front_copy.sort(Chromosome.nonDominatedCrowdingComparator());
                while (population.size() < this.populationNumber) {
                    population.add(pareto_front_copy.remove(0));
                }
            }
        }

    }

    private Chromosome selectParent() {
        //Binary tournament selection
        int indx1;
        int indx2;
        indx1 = new SplittableRandom().nextInt(0, population.size());
        indx2 = new SplittableRandom().nextInt(0, population.size());
        while (indx1 == indx2) {
            indx2 = new SplittableRandom().nextInt(0, population.size());
        }
        Chromosome p1 = population.get(indx1);
        Chromosome p2 = population.get(indx2);
        if (Chromosome.nonDominatedCrowdingComparator().compare(p1, p2) < 0) return p1;
        return p2;
    }

    private void printStatus(int generation) {
        System.out.println("---------------------");
        System.out.println("This is generation: " + generation);
        System.out.println("Size of population " + this.population.size());
        System.out.println("The number of pareto fronts are: " + this.rankedPopulation.size());
        System.out.println("The number of members in the best front is: " + this.rankedPopulation.get(0).size());
    }

    private void plottPareto(String tittelAddOn) {
        SwingUtilities.invokeLater(() -> {
            ScatterPlot plot = new ScatterPlot(rankedPopulation.get(0), tittelAddOn);
            plot.setSize(1000, 600);
            plot.setLocationRelativeTo(null);
            plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            plot.setVisible(true);
        });
    }

    private void runMainLoop(String imageFile) {
        ImageMat loadImg = new ImageMat(imageFile);
        initializePopulation(loadImg);
        rankPopulation();
        //Creates new population of size N based on initial population in generation 0
        //Not following the psudo-code correctly here, as I doubt it really matters. Easier to do it this way
        createNewPopulationBasedOnRank();
        //Following the psudo-code:
        //this.population = createChildren(true);

        int generation = 1;

        while (generation < this.maxGenerationNumber) {
            //Print status
            printStatus(generation);
            //Create offsprings
            List<Chromosome> children = createChildren(false);
            population.addAll(children);
            rankPopulation();
            createNewPopulationBasedOnRank();
            //Should we use selection, crossover and mutation to create a new population of size N here?
            generation++;

        }

        System.out.println("Number of members in rank 1: " + rankedPopulation.get(0).size());
        System.out.println("Number of pareto fronts: " + rankedPopulation.size());
        System.out.println("Plotting the pareto front");
        if (this.toPlotOrNotToPlot) {
            plottPareto("before merge");
        }

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

            Chromosome contestant = rankedPopulation.get(0).get(index);
            executorService.execute(() -> {
                contestant.mergeAllSmallerThanN(this.minSegmentSize, 0);
                loadImg.saveAsBlackAndWhite("Evaluator/Student_Segmentation_Files/testimage" + index +
                        "Deviation" + contestant.getDeviation() + "Connectivity" + contestant.getConnectivity(), contestant);
                loadImg.saveAsGreen("Evaluator/Student_Segmentation_Files_Green/testimage" + index +
                        "Deviation" + contestant.getDeviation() + "Connectivity" + contestant.getConnectivity(), contestant);
                contestant.findSegments();
                contestant.deviation = contestant.overallDeviation();
                contestant.connectivity = contestant.overallConnectivity();
                System.out.println("Finished writing number: " + index);

            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        if (this.toPlotOrNotToPlot) {
            plottPareto("after merge");
        }

    }

    public static void main(String[] args) {
        NSGAII run = new NSGAII();
        run.runMainLoop("Training/86016/Test image.jpg");
    }
}
