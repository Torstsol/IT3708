import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class ScatterPlot extends JFrame {

    public ScatterPlot(ArrayList<Chromosome> paretoFront, String tittel) {

        //Create dataset
        XYDataset dataset = createDataset(paretoFront);

        //Create chart
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Scatter plot for pareto front " + tittel , "Connectivity",
                "Deviation", dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255, 255, 255));

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }


    private XYDataset createDataset(ArrayList<Chromosome> paretoFront) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        //Adds the deviation and connectivity for each member in the pareto front

        XYSeries series1 = new XYSeries("Rank 1 Pareto");
        for (Chromosome member : paretoFront) {
            series1.add(member.getConnectivity(), member.getDeviation());
        }

        dataset.addSeries(series1);

        return dataset;
    }
}
