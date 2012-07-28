package org.jzy3d.demos.surface;

import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.ui.ChartLauncher;

public class Test3D {
	public static void main(String[] args){
		int size = 50;
		float x;
		float y;
		float z;
		
		Coord3d[] points = new Coord3d[size];

		for(int i=0; i<size; i++){
			x = (float)Math.random() - 0.5f;
			y = (float)Math.random() - 0.5f;
			z = (float)Math.random() - 0.5f;
			points[i] = new Coord3d(x, y, z);
		}       

		Scatter scatter = new Scatter(points);
		Chart chart = new Chart();
		chart.getAxeLayout().setMainColor(org.jzy3d.colors.Color.RED);
		chart.getView().setBackgroundColor(org.jzy3d.colors.Color.WHITE);
		chart.getScene().add(scatter);

		ChartLauncher.openChart(chart);
	}
}
