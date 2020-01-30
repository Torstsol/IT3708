package visualization;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.List;
import javax.swing.*;

import model.*;

public class Visualizer extends JFrame {

    private MyCanvas canvas = new MyCanvas();

    public Visualizer(ArrayList<Depot> depotList, ArrayList<Customer> customerList, double maxCoordinate, double minCoordinate) {
        //canvas.customer_dict = customer_dict;
        //canvas.depot_dict = depot_dict;
        //canvas.vehicle_dict = vehicle_dict;
        //canvas.DNAString = DNAString;
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

    public void changeDNA(List<List<Integer>> DNAString){
        canvas.setDNA(DNAString);
    }

    //TODO Make dynamic
    private class MyCanvas extends Canvas {

        ArrayList<Depot> depotList;
        ArrayList<Customer> customerList;
        //private Map<Integer, Vehicle> vehicle_dict;
        private List<List<Integer>> DNAString;
        private double maxCoordinate;
        private double minCoordinate;
        private double border = 5;
        List<Color> depotColours = new ArrayList<Color>(Arrays.asList(
                Color.blue, Color.red, Color.green, Color.black, Color.yellow, Color.pink, Color.cyan, Color.magenta, Color.orange, Color.lightGray));


        public void setDNA(List<List<Integer>> dna) {
            this.DNAString = dna;
        }

        private void drawPoint(Graphics g, int x, int y, Color color, int size) {
            g.setColor(color);

            // transform from min-max point coordinates to screen coordinates
            // - size/2 to get the center of the point at the coordinate
            g.fillOval((x + (int)(-minCoordinate+border)) * this.getWidth() / (int)(maxCoordinate-minCoordinate+2*border)-size/2,
                    this.getHeight() - (y + (int)(-minCoordinate+border)) * this.getHeight() / (int)(maxCoordinate-minCoordinate+2*border) - size/2,
                    size,
                    size);
        }

        private void drawLine(Graphics g, int x1, int y1, int x2, int y2, Color color) {
            g.setColor(color);

            // x*this.getWidth()/100 and this.getHeight()-y*this.getHeight()/100
            // to transform from 0-100 coordinates to screen coordinates
            g.drawLine((x1 + (int)(-minCoordinate+border)) * this.getWidth() / (int)(maxCoordinate-minCoordinate+2*border),
                    this.getHeight() - (y1 + (int)(-minCoordinate+border)) * this.getHeight() / (int)(maxCoordinate-minCoordinate+2*border),
                    (x2 + (int)(-minCoordinate+border)) * this.getWidth() / (int)(maxCoordinate-minCoordinate+2*border),
                    this.getHeight() - (y2 + (int)(-minCoordinate+border)) * this.getHeight() / (int)(maxCoordinate-minCoordinate+2*border));
        }

        @Override
        public void paint(Graphics g) {
            for (Depot depot : depotList) {
                drawPoint(g, depot.xCoordinate, depot.yCoordinate, Color.black, 10);
            }
            for (Customer customer : customerList) {
                drawPoint(g, customer.xCoordinate, customer.yCoordinate, Color.red, 5);
            }
        }
      //       for (int i = 0; i < this.DNAString.size(); i++){
      //           Vehicle currentVehicle = vehicle_dict.get(i);
      //           List<Integer> currentRoute = this.DNAString.get(i);

      //           int startDepotId = currentVehicle.getDepotID();
      //           Color currentColor = depotColours.get(startDepotId);
      //           Depot startDepot = depot_dict.get(startDepotId);
      //           Depot endDepot = depot_dict.get(currentRoute.get(currentRoute.size()-1));
      //           if(currentRoute.size()>1) {
      //               Customer nextCustomer = customer_dict.get(currentRoute.get(0));
      //               Customer lastCustomer;
      //               drawLine(g, startDepot.getX(), startDepot.getY(), nextCustomer.getX(), nextCustomer.getY(), currentColor);
      //               drawPoint(g, nextCustomer.getX(), nextCustomer.getY(), currentColor, 10);
      //               for(int j = 1; j < currentRoute.size()-2; j++){
      //                   lastCustomer = nextCustomer;
      //                   nextCustomer = customer_dict.get(currentRoute.get(j));
      //                   drawLine(g, lastCustomer.getX(), lastCustomer.getY(), nextCustomer.getX(), nextCustomer.getY(), currentColor);
      //                   drawPoint(g, nextCustomer.getX(), nextCustomer.getY(), currentColor, 10);
      //               }
      //               lastCustomer = nextCustomer;
      //               drawLine(g, lastCustomer.getX(), lastCustomer.getY(), endDepot.getX(), endDepot.getY(), currentColor);
      //           }
      //       }
      //   }

        public void changeDNA(List<List<Integer>> DNAString) {
            this.DNAString = DNAString;
            repaint();
        }
    }

    public static void main(String[] args){
       //Visualizer visualizer = new Visualizer();
    }
}

