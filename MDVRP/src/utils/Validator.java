package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Validator {
    private StringBuilder errors;
    private int depotNo;
    private int routeNo;
    private double epsilon = 1E-5;

    public static void main(String[] args) {
        List<Integer> l1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Integer> l2 = Arrays.asList(9, 3, 7, 8, 2, 6, 5, 1, 4);
        int start = 3;
        int stop = 7;
        ArrayList<Integer> l3 = new ArrayList<>();
        l3.addAll(l1.subList(start, stop));
        int index = stop - 1;
        while (l3.size() < l1.size()) {
            int num = l2.get(++index % l2.size());
            if (!l3.contains(num)) {
                l3.add(num);
            }
        }
        ArrayList<Integer> l4 = new ArrayList<>();
        l4.addAll(l3.subList(l3.size()-start, l3.size()));
        l4.addAll(l3.subList(0, l3.size()-start));
        System.out.println(l4);
    }

    public Validator() {
    }

    public Validator(double epsilon) {
        this.epsilon = epsilon;
    }

    public void evaluateFromFile(String solutionFile, String inputFile) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(solutionFile));
        this.evaluate(new String(encoded, StandardCharsets.US_ASCII), inputFile);
    }

    public void evaluate(String solution, String inputFile) throws FileNotFoundException {
        // takes a solution string in correct format
        // checks the following:
        // all customer are present exactly once
        // total route length is sum of calculated values
        // all route lengths match calculated value
        // all loads match calculated values
        // max duration is not exceeded
        // load capacity is not exceeded
        // vehicle limit per depot is not exceeded

        // STEP 1: read input file
        Scanner scanner = new Scanner(new File(inputFile));
        int vehicleCount = scanner.nextInt();
        int customerCount = scanner.nextInt();
        int depotCount = scanner.nextInt();
        // x, y, demand, depot, route
        int[][] customers = new int[customerCount][5];
        // x, y, max duration, max load, number of routes
        int[][] depots = new int[depotCount][5];
        for (int i = 0; i < depotCount; i++) {
            depots[i][2] = scanner.nextInt();
            depots[i][3] = scanner.nextInt();
        }
        for (int i = 0; i < customerCount; i++) {
            scanner.nextInt(); // skip id
            customers[i][0] = scanner.nextInt();
            customers[i][1] = scanner.nextInt();
            scanner.nextInt(); // skip service duration
            customers[i][2] = scanner.nextInt();
            scanner.nextLine();
        }
        for (int i = 0; i < depotCount; i++) {
            scanner.nextInt(); // skip id
            depots[i][0] = scanner.nextInt();
            depots[i][1] = scanner.nextInt();
            scanner.nextLine();
        }
        scanner.close();

        // STEP 2: evaluation
        this.errors = new StringBuilder();
        Scanner fileScanner = new Scanner(solution).useLocale(Locale.US);
        double statedTotalDuration = fileScanner.nextDouble();
        double sumOfStatedDurations = 0;
        double actualTotalDuration = 0;
        fileScanner.nextLine();
        while (fileScanner.hasNextLine()) {
            Scanner lineScanner = new Scanner(fileScanner.nextLine()).useLocale(Locale.US);
            this.depotNo = lineScanner.nextInt();
            int[] depot = depots[this.depotNo - 1];
            // keep track of routes per depot
            depot[4]++;
            this.routeNo = lineScanner.nextInt();
            double statedDuration = lineScanner.nextDouble();
            sumOfStatedDurations += statedDuration;
            int statedLoad = lineScanner.nextInt();

            // read actual route
            int prevNo = lineScanner.nextInt();
            this.testRoute(prevNo == 0, "Did not start with 0 (started with %d)", prevNo);
            int[] prev = depot;
            double actualDuration = 0;
            int actualLoad = 0;
            while (lineScanner.hasNext()) {
                int currentNo = lineScanner.nextInt();
                if (currentNo == 0) {
                    this.test(!lineScanner.hasNext(), "Had id 0 but not at end");
                } else {
                    int[] current = customers[currentNo - 1];
                    this.testRoute(current[3] == 0 && current[4] == 0,
                            "Customer %d has already appeared (in Depot %d route %d)",
                            currentNo, current[3], current[4]
                    );
                    current[3] = this.depotNo;
                    current[4] = this.routeNo;
                    actualDuration += this.euclidean(prev[0], prev[1], current[0], current[1]);
                    actualLoad += current[2];
                    prev = current;
                }
            }
            actualDuration += this.euclidean(prev[0], prev[1], depot[0], depot[1]);
            actualTotalDuration += actualDuration;
            this.testRoute(almostEq(actualDuration, statedDuration),
                    "Stated duration (%f) did not match actual duration (%f)",
                    statedDuration, actualDuration
            );
            this.testRoute(actualDuration <= depot[2] || depot[2] == 0,
                    "Duration (%f) exceeds limit for depot (%d)",
                    actualDuration, depot[2]
            );
            this.testRoute(actualLoad == statedLoad,
                    "Stated load (%d) did not match actual load (%d)",
                    statedLoad, actualLoad
            );
            this.testRoute(actualLoad <= depot[3],
                    "Load (%d) exceeds limit for depot (%d)",
                    actualLoad, depot[3]
            );
        }
        this.test(almostEq(statedTotalDuration, sumOfStatedDurations, this.epsilon * 10),
                "Stated total duration (%f) did not match sum of stated durations (%f)",
                statedTotalDuration, sumOfStatedDurations
        );
        this.test(almostEq(statedTotalDuration, actualTotalDuration, this.epsilon * 10),
                "Stated total duration (%f) did not match actual total duration (%f)",
                statedTotalDuration, actualTotalDuration
        );
        for (int i = 0; i < depots.length; i++) {
            int[] depot = depots[i];
            this.test(depot[4] <= vehicleCount,
                    "Depot %d: Route count (%d) exceeds allowed vehicle count (%d)",
                    i + 1, depot[4], vehicleCount
            );
        }
        for (int i = 0; i < customers.length; i++) {
            int[] customer = customers[i];
            this.test(customer[3] != 0 && customer[4] != 0,
                    "Customer %d did not appear in any route",
                    i + 1
            );
        }
        if (this.errors.length() == 0) {
            this.errors.append("No errors found - solution is valid and feasible");
        }
        System.out.println(this.errors.toString());
    }

    private boolean almostEq(double a, double b, double limit) {
        return Math.abs(a - b) < limit;
    }

    private boolean almostEq(double a, double b) {
        return almostEq(a, b, this.epsilon);
    }

    private void test(boolean assertion, String formatString, Object... args) {
        if (!assertion) {
            this.errors.append(String.format(Locale.US, formatString, args));
            this.errors.append("\n");
        }
    }

    private void testRoute(boolean assertion, String formatString, Object... args) {
        if (!assertion) {
            this.errors.append(String.format("Depot %d, route %d: ", this.depotNo, this.routeNo));
            this.test(false, formatString, args);
        }
    }

    private double euclidean(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
