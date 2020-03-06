public class EdgeNew implements Comparable<EdgeNew>{
    private int from;
    private int to;

    private double distance;

    // used to save some calculations when not needing the distance
    public EdgeNew(int from, int to){
        this.from = from;
        this.to = to;
        this.distance = dist(from, to);
    }

    //Calculates the Euclidean distance by using RGB
    public static double dist(int fromPixel, int toPixel) {
        double deltaRed = ((fromPixel & 0xff0000) >> 16) - ((toPixel & 0xff0000) >> 16);
        double deltaGreen = ((fromPixel & 0xff00) >> 8) - ((toPixel & 0xff00) >> 8);
        double deltaBlue = (fromPixel & 0xff) - (toPixel & 0xff);
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
    public int compareTo(EdgeNew o) {
        if (this.getDistance() > o.getDistance()) return 1;
        if (this.getDistance() < o.getDistance()) return -1;
        return 0;
    }
}
