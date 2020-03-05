import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.SplittableRandom;
import java.awt.*;

public class ChromosomeNew {

    int segments;
    int[] chromosome;
    ImageMatrix imageMatrix;
    public int[] segmentDivision;
    double deviation;
    List<List<Integer>> segmentMatrix;

    public ChromosomeNew(ImageMatrix imageMatrix, int segments) {
        this.segments = segments;
        this.imageMatrix = imageMatrix;
        chromosome = new int[imageMatrix.getRowLength() * imageMatrix.getColLength()];
        initPrimMST();
        this.segmentMatrix = findSegments();
        this.deviation = overallDeviation();
    }

    public List<List<Integer>> findSegments() {
        // Pixels pointing to themselves are representing the root of each segment
        this.segmentDivision = new int[chromosome.length];
        Arrays.fill(segmentDivision, -1);
        int currentSegmentID = 0;
        List<Integer> currentSegment;

        for (int i = 0; i < chromosome.length; i++) {
            if (segmentDivision[i] != -1)
                continue;
            currentSegment = new ArrayList<>();
            currentSegment.add(i);
            segmentDivision[i] = currentSegmentID;
            // Sets next pixel to pointer in chromosome. See chromosome representation. Will
            // be one of the neighbours
            int pixel = chromosome[i];
            // As long as the neighbour does not belong to a segment
            while (segmentDivision[pixel] == -1) {
                // Loops and adds pixel to segment. Updates segmentDivision-list.
                currentSegment.add(pixel);
                segmentDivision[pixel] = currentSegmentID;
                pixel = chromosome[pixel];
            }
            // If connected to another segment "merges" them together
            if (segmentDivision[i] != segmentDivision[pixel]) {
                // Sets segment to the parent segment
                int setSegment = segmentDivision[pixel];
                for (int pixelIndex : currentSegment) {
                    segmentDivision[pixelIndex] = setSegment;
                }
            } else {
                currentSegmentID++;
            }

        }
        segments = currentSegmentID;

        // create segmentMatrix
        List<List<Integer>> segmentMatrix = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            segmentMatrix.add(new ArrayList<>());
        }
        for (int i = 0; i < segmentDivision.length; i++) {
            segmentMatrix.get(segmentDivision[i]).add(i);
        }
        return segmentMatrix;
    }

    private void initPrimMST() {
        for (int i = 0; i < chromosome.length; i++)
            chromosome[i] = i;

        // Set that contains visited nodes
        HashSet<Integer> visited = new HashSet<>();

        // Edges sorted after color distance in priorityQueue
        PriorityQueue<EdgeNew> priorityQueue = new PriorityQueue<>();
        // TODO: Understand this
        List<EdgeNew> worstEdges = new ArrayList<>();
        // Random starting point for the prims algorithm, using splittable random
        // because multithreaded
        int current = new SplittableRandom().nextInt(0, chromosome.length - 1);
        while (visited.size() < chromosome.length) {
            if (!visited.contains(current)) {
                visited.add(current);
                // add all possible edges from current pixel to all neighbours.
                addEdges(priorityQueue, current);
            }
            EdgeNew edge = priorityQueue.poll();

            // add the best scoring edge to the MST if the "to node" is not visited.
            // TODO: Understand what happens here
            if (!visited.contains(edge.getTo())) {
                chromosome[edge.getTo()] = edge.getFrom();
                worstEdges.add(edge);
                // adds the n worst edges, to remove them and make segments.
            }
            current = edge.getTo();
        }

        Collections.sort(worstEdges);
        Collections.reverse(worstEdges);

        // Removes worst edges somehow?
        for (int i = 0; i < segments - 1; i++) {
            EdgeNew removeEdge = worstEdges.get(i);
            this.chromosome[removeEdge.getFrom()] = removeEdge.getFrom();
        }
    }

    // Calculates index in chromosome from row and col
    private int getIndexOnRowCol(int row, int col) {
        return row * this.imageMatrix.getColLength() + col;
    }

    private int getRowOnIndex(int pixelNumber) {
        return pixelNumber / this.imageMatrix.getColLength();
    }

    private int getColOnIndex(int pixelNumber) {
        return pixelNumber % this.imageMatrix.getColLength();
    }

    private int getPixelonIndex(int pixelNumber) {
        return this.imageMatrix.getIntMatrix()[getRowOnIndex(pixelNumber)][getColOnIndex(pixelNumber)];
    }

    private void addEdges(PriorityQueue<EdgeNew> candidateEdges, int currentIndex) {

        int row = getRowOnIndex(currentIndex);
        int col = getColOnIndex(currentIndex);

        // add left neighbours
        if (col > 0) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row, col - 1)));
            if (row > 0) {
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row - 1, col - 1)));
            }
            if (row + 1 < this.imageMatrix.getRowLength()) {
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row + 1, col - 1)));
            }
        }
        // add right neighbours
        if (col + 1 < this.imageMatrix.getColLength()) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row, col + 1)));
            if (row > 0) {
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row - 1, col + 1)));
            }
            if (row + 1 < this.imageMatrix.getRowLength()) {
                candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row + 1, col + 1)));
            }
        }
        // add up and down
        if (row > 0) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row - 1, col)));
        }
        if (row + 1 < this.imageMatrix.getRowLength()) {
            candidateEdges.add(new EdgeNew(currentIndex, getIndexOnRowCol(row + 1, col)));
        }
    }

    // measure of the ‘similarity’ (homogeneity) of pixels in the same segment
    // Assumes a 2D list in the form of [[1,52,23]] where the numbers are
    // pixelnumbers
    public double overallDeviation() {
        double deviation = 0;
        // Change when we have 2d list
        for (List<Integer> segment : this.segmentMatrix) {
            // Find segment center
            Color centroidColor = getSegmentCentroid(segment);
            // List<Integer> centerPos = getSegmentCenter(segment);
            // Pixel centerPixel = imageMat[centerPos.get(0)][centerPos.get(1)];
            for (Integer integer : segment) {
                int pixel = getPixelonIndex(integer);
                deviation += distColor(pixel, centroidColor);
            }
        }
        return deviation;
    }

    // Used when calculating overall deviation since one is a Color object
    public static double distColor(int pixel, Color centroidColor) {
        double deltaRed = ((pixel & 0xff0000) >> 16) - centroidColor.getRed();
        double deltaGreen = ((pixel & 0xff00) >> 8) - centroidColor.getGreen();
        double deltaBlue = (pixel & 0xff) - centroidColor.getBlue();
        return Math.sqrt((Math.pow(deltaRed, 2)) + (Math.pow(deltaGreen, 2)) + (Math.pow(deltaBlue, 2)));
    }

    // The centroid is the average color of all the pixels in one segment.
    private Color getSegmentCentroid(List<Integer> segment) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int numberOfPixelsInSegment = segment.size();
        for (Integer integer : segment) {
            int temp = getPixelonIndex(integer);
            blue = temp & 0xff;
            green = (temp & 0xff00) >> 8;
            red = (temp & 0xff0000) >> 16;
        }
        red = red / numberOfPixelsInSegment;
        green = green / numberOfPixelsInSegment;
        blue = blue / numberOfPixelsInSegment;

        Color centroid = new Color(red, green, blue);
        return centroid;
    }

    // Evaluates the degree to which neighbouring pixels have been placed in the
    // same segment
    public double overallConnectivity() {
        double connectiviy = 0;
        for (List<Integer> segment : this.segmentMatrix) {
            for (int pixelIndex : segment) {
                int row = getRowOnIndex(pixelIndex);
                int col = getColOnIndex(pixelIndex);

                // add left neighbours
                if (col > 0) {
                    connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row, col - 1));
                    if (row > 0) {
                        connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row - 1, col - 1));
                    }
                    if (row + 1 < this.imageMatrix.getRowLength()) {
                        connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row + 1, col - 1));
                    }
                }
                // add right neighbours
                if (col + 1 < this.imageMatrix.getColLength()) {
                    connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row, col + 1));
                    if (row > 0) {
                        connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row - 1, col + 1));
                    }
                    if (row + 1 < this.imageMatrix.getRowLength()) {
                        connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row + 1, col + 1));
                    }
                }

                // add up and down
                if (row > 0) {
                    connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row - 1, col));
                }
                if (row + 1 < this.imageMatrix.getRowLength()) {
                    connectiviy += checkNeighbour(pixelIndex, getIndexOnRowCol(row + 1, col));
                }

            }
        }
        return connectiviy;
    }

    private double checkNeighbour(int current, int target) {
        if (segmentDivision[current] == segmentDivision[target]) {
            return 0;
        } else {
            return 0.125;
        }
    }

}