import java.awt.*;
import java.io.FileReader;

public class Edge implements Comparable<Edge>{
    private int from;
    private int to;

    private double distance;

    public Edge(Pixel from, Pixel to) {
        this.from = from.getPixelIdx();
        this.to = to.getPixelIdx();
        this.distance = dist(from, to);
    }

    // used to save some calculations when not needing the distance
    public Edge(int from, int to){
        this.from = from;
        this.to = to;
    }

    //Calculates the Euclidean distance by using RGB
    public static double dist(Pixel FromPixel, Pixel toPixel) {
        double deltaRed = FromPixel.getRed() - toPixel.getRed();
        double deltaGreen = FromPixel.getGreen() - toPixel.getGreen();
        double deltaBlue = FromPixel.getBlue() - toPixel.getBlue();
        return Math.sqrt((Math.pow(deltaRed, 2)) + (Math.pow(deltaGreen, 2)) + (Math.pow(deltaBlue, 2)));
    }

    //Used when calculating overall deviation since one is a Color object
    public static double distColor(Pixel FromPixel, Color centroidColor) {
        double deltaRed = FromPixel.getRed() - centroidColor.getRed();
        double deltaGreen = FromPixel.getGreen() - centroidColor.getGreen();
        double deltaBlue = FromPixel.getBlue() - centroidColor.getBlue();
        return Math.sqrt((Math.pow(deltaRed, 2)) + (Math.pow(deltaGreen, 2)) + (Math.pow(deltaBlue, 2)));
    }


    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Edge o) {
        if (this.getDistance() > o.getDistance()) return 1;
        if (this.getDistance() < o.getDistance()) return -1;
        return 0;
    }
}
