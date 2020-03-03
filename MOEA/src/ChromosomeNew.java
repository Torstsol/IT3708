import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.SplittableRandom;

public class ChromosomeNew {
    
    int segments;
    int[] chromosome;
    ImageMatrix imageMatrix;
    
    public ChromosomeNew(ImageMatrix imageMatrix, int segments){
        this.segments = segments;
        this.imageMatrix = imageMatrix;
        chromosome = new int[imageMatrix.getRowLength()*imageMatrix.getColLength()];
        initPrimMST();
    }


    private void initPrimMST() {
        for (int i = 0; i < chromosome.length; i++) chromosome[i] = i;

        //Set that contains visited nodes
        HashSet<Integer> visited = new HashSet<>();

        // Edges sorted after color distance in priorityQueue
        PriorityQueue<EdgeNew> priorityQueue = new PriorityQueue<>();
        //TODO: Understand this
        List<EdgeNew> worstEdges = new ArrayList<>();
        // Random starting point for the prims algorithm, using splittable random because multithreaded
        int current = new SplittableRandom().nextInt(0, chromosome.length - 1);
        while (visited.size() < chromosome.length) {
            if (!visited.contains(current)) {
                visited.add(current);
                // add all possible edges from current pixel to all neighbours.
                addEdges(priorityQueue, current);
            }
            EdgeNew edge = priorityQueue.poll();
            // add the best scoring edge to the MST if the "to node" is not visited.

            //TODO: Understand what happens here
            if (!visited.contains(edge.getTo())) {
                chromosome[edge.getTo()] = edge.getFrom();
                worstEdges.add(edge);
                // adds the n worst edges, to remove them and make segments.
            }
            current = edge.getTo();
        }

        Collections.sort(worstEdges);
        Collections.reverse(worstEdges);

        //Removes worst edges somehow?
        for (int i = 0; i < segments - 1; i++) {
            EdgeNew removeEdge = worstEdges.get(i);
            this.chromosome[removeEdge.getFrom()] = removeEdge.getFrom();
        }
    }

    //Calculates index in chromosome from row and col
    private int getIndexOnRowCol(int row, int col){
        return row*this.imageMatrix.getColLength() + col;
    }

    private int getRowOnIndex(int pixelNumber) {
        return pixelNumber / this.imageMatrix.getColLength();
    }
    private int getColOnIndex(int pixelNumber) {
        return pixelNumber % this.imageMatrix.getColLength();
    }

    private void addEdges(PriorityQueue<EdgeNew> candidateEdges, int currentIndex) {

        int row = getRowOnIndex(currentIndex);
        int col = getColOnIndex(currentIndex);

        //add left neighbours
        if (col > 0) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row, col - 1)));
            if (row > 0) {
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row - 1, col - 1)));
            }
            if (row + 1 < this.imageMatrix.getRowLength()) {
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row + 1, col - 1)));
            }
        }
        //add right neighbours
        if (col + 1 < this.imageMatrix.getColLength()) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row, col + 1)));
            if (row > 0) {
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row - 1, col + 1)));
            }
            if (row + 1 < this.imageMatrix.getRowLength()){
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row + 1, col + 1)));
            }
        }
        //add up and down
        if (row > 0) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row - 1, col)));
        }
        if (row + 1 < this.imageMatrix.getRowLength()) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row + 1, col)));
        }
    }



}