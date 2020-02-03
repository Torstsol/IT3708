package visualization;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

import model.*;

public class Visualizer extends JFrame {

    private MyCanvas canvas = new MyCanvas();

    public Visualizer(ArrayList<Depot> depotList, ArrayList<Customer> customerList, double maxCoordinate,
            double minCoordinate, ArrayList<ArrayList<Route>> schedule) {
        // canvas.customer_dict = customer_dict;
        // canvas.depot_dict = depot_dict;
        // canvas.vehicle_dict = vehicle_dict;
        canvas.schedule = schedule;
        canvas.maxCoordinate = maxCoordinate;
        canvas.minCoordinate = minCoordinate;
        canvas.depotList = depotList;
        canvas.customerList = customerList;
        setLayout(new BorderLayout());
        setSize(720, 720);
        setTitle("Visualization");
        add("Center", canvas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void changeSchedule(ArrayList<ArrayList<Route>> schedule) {
        canvas.setSchedule(schedule);
    }

    // TODO Make dynamic
    private class MyCanvas extends Canvas {

        ArrayList<Depot> depotList;
        ArrayList<Customer> customerList;
        // private Map<Integer, Vehicle> vehicle_dict;
        private ArrayList<ArrayList<Route>> schedule;
        private double maxCoordinate;
        private double minCoordinate;
        private double border = 5;
        List<Color> depotColours = new ArrayList<Color>(Arrays.asList(Color.blue, Color.red, Color.green, Color.black,
                Color.yellow, Color.pink, Color.cyan, Color.magenta, Color.orange, Color.lightGray));

        public void setSchedule(ArrayList<ArrayList<Route>> schedule) {
            this.schedule = schedule;
        }

        private void drawPoint(Graphics g, int x, int y, Color color, int size) {
            g.setColor(color);

            // transform from min-max point coordinates to screen coordinates
            // - size/2 to get the center of the point at the coordinate
            g.fillOval(
                    (x + (int) (-minCoordinate + border)) * this.getWidth()
                            / (int) (maxCoordinate - minCoordinate + 2 * border) - size / 2,
                    this.getHeight() - (y + (int) (-minCoordinate + border)) * this.getHeight()
                            / (int) (maxCoordinate - minCoordinate + 2 * border) - size / 2,
                    size, size);
        }

        private void drawLine(Graphics g, int x1, int y1, int x2, int y2, Color color) {
            g.setColor(color);

            // x*this.getWidth()/100 and this.getHeight()-y*this.getHeight()/100
            // to transform from 0-100 coordinates to screen coordinates
            g.drawLine(
                    (x1 + (int) (-minCoordinate + border)) * this.getWidth()
                            / (int) (maxCoordinate - minCoordinate + 2 * border),
                    this.getHeight() - (y1 + (int) (-minCoordinate + border)) * this.getHeight()
                            / (int) (maxCoordinate - minCoordinate + 2 * border),
                    (x2 + (int) (-minCoordinate + border)) * this.getWidth()
                            / (int) (maxCoordinate - minCoordinate + 2 * border),
                    this.getHeight() - (y2 + (int) (-minCoordinate + border)) * this.getHeight()
                            / (int) (maxCoordinate - minCoordinate + 2 * border));
        }

        @Override
        public void paint(Graphics g) {
            for (Depot depot : depotList) {
                drawPoint(g, depot.xCoordinate, depot.yCoordinate, Color.black, 10);
            }
            for (Customer customer : customerList) {
                drawPoint(g, customer.xCoordinate, customer.yCoordinate, Color.red, 5);
            }

            // for each depot
            for (int i = 0; i < schedule.size(); i++) {
                // for each route in depot
                for (int j = 0; j < schedule.get(i).size(); j++) {
                    // for each customer in route
                    for (int k = 0; k < schedule.get(i).get(j).geCustomers().size(); k++) {
                        if (k == schedule.get(i).get(j).geCustomers().size() - 1) {
                            if (k != 0) {
                                drawLine(g, schedule.get(i).get(j).geCustomers().get(k - 1).xCoordinate,
                                        schedule.get(i).get(j).geCustomers().get(k - 1).yCoordinate,
                                        schedule.get(i).get(j).geCustomers().get(k).xCoordinate,
                                        schedule.get(i).get(j).geCustomers().get(k).yCoordinate, Color.blue);
                            }
                            drawLine(g, schedule.get(i).get(j).geCustomers().get(k).xCoordinate,
                                    schedule.get(i).get(j).geCustomers().get(k).yCoordinate,
                                    depotList.get(i).xCoordinate, depotList.get(i).yCoordinate, Color.black);
                        } else if (k == 0) {
                            drawLine(g, schedule.get(i).get(j).geCustomers().get(k).xCoordinate,
                                    schedule.get(i).get(j).geCustomers().get(k).yCoordinate,
                                    depotList.get(i).xCoordinate, depotList.get(i).yCoordinate, Color.red);
                        } else {
                            drawLine(g, schedule.get(i).get(j).geCustomers().get(k - 1).xCoordinate,
                                    schedule.get(i).get(j).geCustomers().get(k - 1).yCoordinate,
                                    schedule.get(i).get(j).geCustomers().get(k).xCoordinate,
                                    schedule.get(i).get(j).geCustomers().get(k).yCoordinate, Color.blue);
                        }

                    }
                }
            }

        }
    }

}
