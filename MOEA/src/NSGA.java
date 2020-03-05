import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class NSGA {

     //Variables
     String imageString = "Training/86016/Test image.jpg";
     private List<ChromosomeNew> population = new ArrayList<>();
     int populationSize = 10;
    public static void main(String[] args){

        NSGA nsga = new NSGA();
        nsga.mainLoop();
    }

    private void mainLoop(){
        ImageMatrix imageMatrix = new ImageMatrix(this.imageString);
        createPopulation(imageMatrix);
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

    } 
}
