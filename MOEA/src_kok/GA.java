
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GA {
    //Settings
    private int populationSize = 15;
    private double crossoverRate = 0.6;
    private double mutationRate = 0.005;
    private int maxGenerationNumber = 150;
    private int elites = 8;
    private int tournamentSize = 5;
    private int generationNumber = 0;
    private int startingSegments = 500;
    private int mergeSmallerThan = 20;

    private List<Chromosome> population = new ArrayList<>(this.populationSize);

    private void createInitialPopulation(ImageMat loadImg) {
        Chromosome.img = loadImg;
        for (int i = 0; i < this.populationSize; i++) {
            System.out.println("Created individual numbered: " + i);
            Chromosome populationMember = new Chromosome(this.startingSegments);
            populationMember.mergeAllSmallerThanN(mergeSmallerThan, 0);
            populationMember.setWeightedSum();
            this.population.add(populationMember);
        }

    }

    private void printStatus() {
        Chromosome temp = this.population.get(0);
        System.out.println("---------------------");
        System.out.println("This is generation: " + this.generationNumber);
        System.out.println("Best weighted sum is: " + temp.getWeightedSum());
        System.out.println("With an individual deviation of: " + temp.getDeviation());
        System.out.println("And a connectivity of: " + temp.getConnectivity());
        System.out.println("---------------------");
    }

    private List<Chromosome> playTournamentWithCrossoverAndMutation(int numberOfKids) {
        List<Chromosome> children = new ArrayList<>(numberOfKids);
        for (int i = 0; i < numberOfKids; i++) {
            double crossoverRateCheck = new Random().nextDouble();
            Chromosome father = tournamentWinner(this.tournamentSize);
            Chromosome mother = tournamentWinner(this.tournamentSize);
            if (crossoverRateCheck < this.crossoverRate) {
                Chromosome child = new Chromosome(father, mother, this.mutationRate);
                //child.mergeAllSmallerThanN(mergeSmallerThan, 0);
                child.setWeightedSum();
                children.add(child);
            } else {
                int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
                if (randomNum == 1) {
                    Chromosome child = new Chromosome(father, this.mutationRate);
                    //child.mergeAllSmallerThanN(mergeSmallerThan, 0);
                    child.setWeightedSum();
                    children.add(child);
                } else {
                    Chromosome child = new Chromosome(mother, this.mutationRate);
                    //child.mergeAllSmallerThanN(mergeSmallerThan, 0);
                    child.setWeightedSum();
                    children.add(child);
                }
            }
        }
        return children;
    }

    private Chromosome tournamentWinner(int tournamentSize) {
        Collections.shuffle(this.population);
        List<Chromosome> participants = new ArrayList<>(tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            participants.add(population.get(i));
        }
        participants.sort(Chromosome.weightedSumComparator());
        return participants.get(0);
    }

    private void geneticAlgorithm(String imageFile) {
        ImageMat loadImg = new ImageMat(imageFile);
        createInitialPopulation(loadImg);
        while (this.generationNumber < this.maxGenerationNumber) {
            List<Chromosome> newGenerationPopulation = new ArrayList<>();
            this.population.sort(Chromosome.weightedSumComparator());
            printStatus();

            //Saving X best from last generation. Elitism.
            for (int i = 0; i < this.elites; i++) {
                newGenerationPopulation.add(this.population.get(i));
            }

            //Playing k-tournament selection for deciding parents
            List<Chromosome> kidsToFillUpGeneration = playTournamentWithCrossoverAndMutation(this.population.size() - newGenerationPopulation.size());
            newGenerationPopulation.addAll(kidsToFillUpGeneration);
            this.population = newGenerationPopulation;
            this.generationNumber += 1;
        }

    }

    public static void main(String[] args) {
        GA run = new GA();
        run.geneticAlgorithm("Training/216066/Test image.jpg");
        run.population.sort(Chromosome.weightedSumComparator());
        int i = 0;
        for(Chromosome c: run.population){
            Chromosome.img.saveAsBlackAndWhite("bnw"+i, run.population.get(i++));
        }
    }
}
