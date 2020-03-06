import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    double overallConnectivity;
    private double crowding_distance;

    private int lastMergeSize = -1;

    List<List<Integer>> segmentMatrix;
    private int rank;

    public ChromosomeNew(ImageMatrix imageMatrix, int segments) {
        this.segments = segments;
        this.imageMatrix = imageMatrix;
        chromosome = new int[imageMatrix.getRowLength() * imageMatrix.getColLength()];
        initPrimMST();
        this.segmentMatrix = findSegments();
        this.deviation = overallDeviation();
        this.overallConnectivity = overallConnectivity();
    }

    // crossover constructor
    public ChromosomeNew(ChromosomeNew father, ChromosomeNew mother, double mutationRate, ImageMatrix imageMatrix) {
        this.imageMatrix = imageMatrix;
        chromosome = new int[imageMatrix.getRowLength() * imageMatrix.getColLength()];
        // integer for index to take genes from mother instead of father.
        for (int i = 0; i < chromosome.length; i++) {
            if (new SplittableRandom().nextInt(0, 2) == 0) {
                chromosome[i] = father.chromosome[i];
            } else {
                chromosome[i] = mother.chromosome[i];
            }
        }
        mutate(mutationRate);
        findSegments();
        this.deviation = overallDeviation();
        this.overallConnectivity = overallConnectivity();
    }

    public void mutate(double mutationRate) {
        if (segmentDivision == null)
            findSegments();
        if (new SplittableRandom().nextInt(0, 100) < 10) {
            mutateMergeTwoRandomSegments();
        } else if (new SplittableRandom().nextInt(0, 100) < 70) {
            mutateMergeTwoClosestSegments();
        } else {
            for (int pixelId : this.chromosome) {
                if (new SplittableRandom().nextInt(0, 100) < mutationRate * 100) {
                    if (new SplittableRandom().nextInt(0, 100) < 50) {
                        mutateRandomEdge(pixelId);
                    } else {
                        mutateBestEdge(pixelId);
                    }
                }
            }
        }
    }

    public void mutateRandomEdge(int pixelIndex) {
        List<Integer> neigbours = getNeighbours(pixelIndex);
        // change edge to a random possible edge for the pixel
        chromosome[pixelIndex] = neigbours.get(new SplittableRandom().nextInt(0, neigbours.size()));
    }

    public void mutateBestEdge(int pixelIndex) {
        double bestDist = Double.MAX_VALUE;
        Edge bestEdge = new Edge(0, 0);

        for (int neighbourIndex : getNeighbours(pixelIndex)) {
            if (segmentDivision[pixelIndex] != segmentDivision[neighbourIndex]) {
                Edge currentEdge = new Edge(pixelIndex, neighbourIndex);
                if (currentEdge.getDistance() < bestDist) {
                    bestDist = currentEdge.getDistance();
                    bestEdge = currentEdge;
                }
            }
        }
        chromosome[bestEdge.getFrom()] = bestEdge.getTo();
    }

    public void mutateMergeTwoRandomSegments() {
        List<Edge> connectingEdges = new ArrayList<>();
        for (int pixel = 0; pixel < chromosome.length; pixel++) {
            for (int neighB : getNeighbours(pixel)) {
                if (segmentDivision[pixel] != segmentDivision[neighB]) {
                    connectingEdges.add(new Edge(pixel, neighB));
                }
            }
        }
        if (connectingEdges.size() > 0) {
            Edge toConnect = connectingEdges.get(new SplittableRandom().nextInt(0, connectingEdges.size()));
            chromosome[toConnect.getFrom()] = toConnect.getTo();
        }
    }

    public void mutateMergeTwoClosestSegments() {
        if (segmentDivision == null)
            findSegments();
        List<Edge> connectingEdges = new ArrayList<>();
        for (int pixel = 0; pixel < chromosome.length; pixel++) {
            for (int neighB : getNeighbours(pixel)) {
                if (segmentDivision[pixel] != segmentDivision[neighB]) {
                    connectingEdges.add(new Edge(pixel, neighB));
                }
            }
        }

        if (connectingEdges.size() > 0) {
            Color[] centroids = new Color[this.segments];
            // create segmentMatrix
            List<List<Integer>> segmentMatrix = new ArrayList<>();
            for (int i = 0; i < segments; i++) {
                segmentMatrix.add(new ArrayList<>());
            }
            for (int i = 0; i < segmentDivision.length; i++) {
                segmentMatrix.get(segmentDivision[i]).add(i);
            }
            for (int i = 0; i < segments; i++) {
                centroids[i] = getSegmentCentroid(segmentMatrix.get(i));
            }

            Edge bestEdge = connectingEdges.get(0);
            // dist between centroids of the the segments connected by the edge
            double bestDist = distColor(centroids[segmentDivision[bestEdge.getFrom()]].getRGB(),
                    centroids[segmentDivision[bestEdge.getTo()]]);
            for (int i = 1; i < connectingEdges.size(); i++) {
                Edge currentEdge = connectingEdges.get(i);
                double currentDist = distColor(centroids[segmentDivision[currentEdge.getFrom()]].getRGB(),
                        centroids[segmentDivision[currentEdge.getTo()]]);
                if (currentDist < bestDist) {
                    bestDist = currentDist;
                    bestEdge = currentEdge;
                }
            }
            chromosome[bestEdge.getFrom()] = bestEdge.getTo();
        }
    }

    public List<List<Integer>> findSegments() {
        // Pixels pointing to themselves are representing the root of each segment
        this.segmentDivision = new int[chromosome.length];
        Arrays.fill(segmentDivision, -1);
        int currentSegmentID = 0;
        List<Integer> currentSegment;

        for (int i = 0; i < chromosome.length; i++) {
            // Skip if pixel-index is already assigned
            if (segmentDivision[i] != -1)
                continue;
            currentSegment = new ArrayList<>();
            // Add first non-assigned pixel to new segment
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
                // check new pixel
                pixel = chromosome[pixel];
            }
            // If connected to another segment "merges" them together, will not fire if it
            // points at itself
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
        List<EdgeNew> createdEdges = new ArrayList<>();
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
                createdEdges.add(edge);
                // adds the n worst edges, to remove them and make segments.
            }
            current = edge.getTo();
        }

        Collections.sort(createdEdges);
        // Get the "worst edges in order"
        Collections.reverse(createdEdges);

        // Removes worst edges
        for (int i = 0; i < segments - 1; i++) {
            EdgeNew removeEdge = createdEdges.get(i);
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
        for (List<Integer> segment : getSegmentMatrix()) {
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
        for (List<Integer> segment : getSegmentMatrix()) {
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

    public boolean isEdge(int pixelIndex) {
        for (int neighbor : getNeighboursForPrinting(pixelIndex)) {
            if (segmentDivision[pixelIndex] != segmentDivision[neighbor])
                return true;
        }
        return false;
    }

    public List<Integer> getNeighboursForPrinting(int pixelIndex) {
        List<Integer> neighbours = new ArrayList<>();
        int row = getRowOnIndex(pixelIndex);
        int col = getColOnIndex(pixelIndex);

        // add left neighbour
        if (col > 0) {
            neighbours.add(getIndexOnRowCol(row, col - 1));
        }
        // add down neighbour
        if (row > 0) {
            neighbours.add(getIndexOnRowCol(row - 1, col));
        }
        return neighbours;
    }

    public List<Integer> getNeighbours(int currentIndex) {
        List<Integer> neighbours = new ArrayList<>();
        int row = getRowOnIndex(currentIndex);
        int col = getColOnIndex(currentIndex);

        // add left neighbours
        if (col > 0) {
            neighbours.add(getIndexOnRowCol(row, col - 1));
            if (row > 0) {
                neighbours.add(getIndexOnRowCol(row - 1, col - 1));
            }
            if (row + 1 < this.imageMatrix.getRowLength()) {
                neighbours.add(getIndexOnRowCol(row + 1, col - 1));
            }
        }
        // add right neighbours
        if (col + 1 < this.imageMatrix.getColLength()) {
            neighbours.add(getIndexOnRowCol(row, col + 1));
            if (row > 0) {
                neighbours.add(getIndexOnRowCol(row - 1, col + 1));
            }
            if (row + 1 < this.imageMatrix.getRowLength()) {
                neighbours.add(getIndexOnRowCol(row + 1, col + 1));
            }
        }
        // add up and down
        if (row > 0) {
            neighbours.add(getIndexOnRowCol(row - 1, col));
        }
        if (row + 1 < this.imageMatrix.getRowLength()) {
            neighbours.add(getIndexOnRowCol(row + 1, col));
        }

        return neighbours;
    }

    public double getConnectivity() {
        return this.overallConnectivity;
    }

    public double getDeviation() {
        return this.deviation;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return this.rank;
    }

    public double getCrowding_distance() {
        return this.crowding_distance;
    }

    // Return 1 if object 2 should be before object 1
    static Comparator<ChromosomeNew> nonDominatedCrowdingComparator() {
        return ((o1, o2) -> {
            if (o1.getRank() < o2.getRank())
                return -1;
            if (o1.getRank() > o2.getRank())
                return 1;
            if (o1.getCrowding_distance() > o2.getCrowding_distance())
                return -1;
            if (o1.getCrowding_distance() < o2.getCrowding_distance())
                return 1;
            return 0;
        });
    }

    public void setCrowding_distance(double crowding_distance) {
        this.crowding_distance = crowding_distance;
    }

    static Comparator<ChromosomeNew> deviationComparator() {
        return Comparator.comparingDouble(ChromosomeNew::getDeviation);
    }

    static Comparator<ChromosomeNew> connectivityComparator() {
        return Comparator.comparingDouble(ChromosomeNew::getConnectivity);
    }

    List<List<Integer>> getSegmentMatrix() {

        List<List<Integer>> segmentMatrix = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            segmentMatrix.add(new ArrayList<>());
        }
        for (int i = 0; i < segmentDivision.length; i++) {
            segmentMatrix.get(segmentDivision[i]).add(i);
        }

        return segmentMatrix;

    }

    public void mergeAllSmallerThanN(int n, int counter) {
        int[] segmentcount = new int[segments];
        for (int segId : segmentDivision) {
            segmentcount[segId]++;
        }

        List<Integer> toMerge = new ArrayList<>();
        for (int i = 0; i < segmentcount.length; i++) {
            if (segmentcount[i] < n) {
                toMerge.add(i);
            }
        }
        //System.out.println(toMerge.size());
        if (lastMergeSize == toMerge.size()) counter++;
        if (toMerge.size() == 0 || counter > 20) return;
        for (int segId : toMerge) {
            EdgeNew bestEdge = findBestEdgeFromSegment(segId);
            chromosome[bestEdge.getFrom()] = bestEdge.getTo();
        }
        lastMergeSize = toMerge.size();
        findSegments();
        mergeAllSmallerThanN(n, counter);
    }

    public EdgeNew findBestEdgeFromSegment(int segIdx) {
        List<Integer> seg = getSegmentMatrix().get(segIdx);
        double bestDist = Double.MAX_VALUE;
        EdgeNew bestEdge = new EdgeNew(0, 0);
        for (int pixelIndex : seg) {
            for (int neighbourIndex : getNeighbours(pixelIndex)) {
                if (segmentDivision[pixelIndex] != segmentDivision[neighbourIndex]) {
                    EdgeNew currentEdge = new EdgeNew(pixelIndex, neighbourIndex);
                    if (currentEdge.getDistance() < bestDist) {
                        bestDist = currentEdge.getDistance();
                        bestEdge = currentEdge;
                    }
                }
            }
        }
        return bestEdge;
    }
}