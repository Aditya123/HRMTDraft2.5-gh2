package org.jzy3d.demos.surface;


import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.ui.ChartLauncher;
import org.jzy3d.plot3d.primitives.Polygon;


public class OpenPolygon implements Runnable{
    
    Coord3d[] points = null;
    public OpenPolygon(Coord3d[] temp){
        points = temp;
    }
    
    public void run(){

    	Polygon poly = new Polygon();
    	System.out.println("Inside the Thread");
    	for(int i = 0; i < points.length; i++){
    		poly.add(new Point(points[i]));
    		//System.out.println("Point #" + i + "out of " + points.length);
    	}
    	poly.setColor(org.jzy3d.colors.Color.BLUE);
    	System.out.println("Polygon Size:" + poly.size());
        System.out.println("Length of Points: " + points.length);
        Chart chart = new Chart();
        chart.getAxeLayout().setMainColor(org.jzy3d.colors.Color.RED);
        chart.getView().setBackgroundColor(org.jzy3d.colors.Color.WHITE);
        chart.getScene().getGraph().add(poly);
        
        ChartLauncher.openChart(chart);
    }
}