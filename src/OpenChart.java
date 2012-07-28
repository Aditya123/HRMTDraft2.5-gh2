import java.awt.Rectangle;

import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.ui.ChartLauncher;

public class OpenChart implements Runnable{
    
    Coord3d[] points = null;
    public OpenChart(Coord3d[] temp){
        points = temp;
    }
    
    public void run(){    
        Scatter scatter = new Scatter(points);
        System.out.println("Length of Points: " + points.length);
        Chart chart = new Chart();
        chart.getAxeLayout().setMainColor(org.jzy3d.colors.Color.RED);
        chart.getView().setBackgroundColor(org.jzy3d.colors.Color.WHITE);
        chart.getScene().add(scatter);
        
        ChartLauncher.openChart(chart);
    }
    
}