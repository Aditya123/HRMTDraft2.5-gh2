import java.awt.image.BufferedImage;
import java.io.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.geom.Point2D;

import org.jzy3d.demos.surface.OpenPolygon;
import org.jzy3d.maths.Coord3d;

/**
 * Written By Aditya Gande, intern at Stanford University & Cupertino High School Student
 * Written in Summer 2012
 * A re-implementation and improvement of the HRMT method first used by Itai Cohen
 * Lab group at Cornell University. Originally created in MATLAB programming language,
 * this version is written using JAVA.
 *
 * Draft 1 Goals:
 * Read in an image and store their RGB pixel values into an array
 * Start making basic stub methods for the more complex methods involving trig
 * Use Imagelab code for polychrome/monochrome an image to suit our needs
 */

public class HRMTDraft2 {
	BufferedImage[] imageView = new BufferedImage[3]; // location 0 = topView, location 1 = lateralView, location 2 = frontViews

	//GUI related stuff
	JFrame frame = new JFrame("Hull Reconstruction Method Utility");
	JPanel container = new JPanel();
	JMenuBar bar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenu imageMenu = new JMenu("Image");
	JMenu mathMenu = new JMenu("Math");
	JMenu helpMenu = new JMenu("Help");
	JMenu getView = new JMenu("Grab View Images");
	InfoPanel info = new InfoPanel();
	MediaPanel media = new MediaPanel();
	JMenuItem addView, edgeDetection, threshold, angle, frontView, lateralView, topView, figureVoxels, help, about, testAll3D;
	final JFileChooser fileChooser = new JFileChooser();
	Border infoBorder = BorderFactory.createTitledBorder("HRMT Info");
	Border mediaBorder = BorderFactory.createTitledBorder("Image");
	int screenWidth  = 0;
	int screenHeight = 0;

	//Three Views stored in String form
	String topViewString, lateralViewString, frontViewString;

	//Three Arrays used for image processing
	int[][] topViewArray = null;
	int[][] lateralViewArray = null;
	int[][] frontViewArray = null;

	//Three ArrayLists used for finding coordinates of the body and wings
	ArrayList<Point2D.Double> topViewCoordinates = new ArrayList<Point2D.Double>();
	ArrayList<Point2D.Double> lateralViewCoordinates = new ArrayList<Point2D.Double>();
	ArrayList<Point2D.Double> frontViewCoordinates = new ArrayList<Point2D.Double>();

	//Two ArrayLists that contain 3D coordinates
	ArrayList<Coord3d> allCoordinates = new ArrayList<Coord3d>();
	ArrayList<Coord3d> sampleCoordinates = new ArrayList<Coord3d>();
	ArrayList<Coord3d> commonCoordinates = new ArrayList<Coord3d>();

	ArrayList<Coord3d> topView3DCoordinates = new ArrayList<Coord3d>();
	ArrayList<Coord3d> lateralView3DCoordinates = new ArrayList<Coord3d>();
	ArrayList<Coord3d> frontView3DCoordinates = new ArrayList<Coord3d>();

	//Thread
	Thread t1 = null;

	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Calendar cal = Calendar.getInstance();
	String start = null;


	/* Enter the file name to use this program with any file"*/
	public HRMTDraft2() {

		setupFrame();
		frame.setJMenuBar(bar);
		frame.setResizable(false);
		info.setBorder(infoBorder);
		media.setBorder(mediaBorder);
		frame.setBackground(Color.BLUE);
		frame.add(container);
	}

	//************Front end Code starts here**********//
	public void setupFrame(){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		screenWidth = dim.width;
		screenHeight = dim.height;

		frame.setBounds(screenWidth/2 - 150, screenHeight/12,300,600);
		frame.setVisible(true);

		fileMenu.add(getView);
		setupMenu();
		bar.add(fileMenu);
		bar.add(imageMenu);
		bar.add(mathMenu);
		bar.add(helpMenu);

		info.setMaximumSize(new Dimension(300, frame.getHeight()/2));
		media.setMaximumSize(new Dimension(300,frame.getHeight()/2));
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.add(info);
		container.add(media);
		container.setBackground(Color.ORANGE);
	}

	public void setupMenu(){

		edgeDetection = new JMenuItem("Edge Detector");
		angle = new JMenuItem("Determine Angle");
		threshold = new JMenuItem("Threshold");
		topView = new JMenuItem("Add Top View");
		lateralView = new JMenuItem("Add Lateral View");
		frontView = new JMenuItem("Add Front View");
		figureVoxels = new JMenuItem("Figure Voxels");
		help = new JMenuItem("Help");
		about = new JMenuItem("About");
		testAll3D = new JMenuItem("Testing 3D chart");

		//JMenuItem Event Code starts here
		topView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int ret = fileChooser.showOpenDialog(null);
				if(ret == JFileChooser.APPROVE_OPTION){
					File fileOne = fileChooser.getSelectedFile();
					topViewString = fileOne.getPath();
					try{
						imageView[0] = ImageIO.read(fileOne);
						topViewArray = convertTo2D(imageView[0]);
						filterBody(topViewArray, 1);
						System.out.println("topView: " + topViewCoordinates.size());
					}
					catch(IOException i){System.out.println(i);}
				}
			}
		});

		lateralView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int ret = fileChooser.showOpenDialog(null);
				if(ret == JFileChooser.APPROVE_OPTION){
					File fileTwo = fileChooser.getSelectedFile();
					lateralViewString = fileTwo.getPath();
					try{
						imageView[1] = ImageIO.read(fileTwo);
						lateralViewArray = convertTo2D(imageView[1]);
						filterBody(lateralViewArray, 2);
						System.out.println("lateralView: " + lateralViewCoordinates.size());
					}catch(IOException i){System.out.println(i);}
				}
			}
		});

		frontView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ret = fileChooser.showOpenDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File fileThree = fileChooser.getSelectedFile();
					frontViewString = fileThree.getPath();
					try {
						imageView[2] = ImageIO.read(fileThree);
						frontViewArray = convertTo2D(imageView[2]);
						filterBody(frontViewArray, 3);
						System.out.println("frontView: "
							+ frontViewCoordinates.size());
					} catch (IOException i) {
						System.out.println(i);
					}
				}
			}
		});

		figureVoxels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				addAllCoordinates();
				//System.out.println(allCoordinates.size());
				figureVoxels();
				System.out.println("Common Coordinates Size" + commonCoordinates.size());
			}
		});

		threshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{

			}});

		testAll3D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{

				System.out.println("Adding All Coordinates...");
				addAllCoordinates();
				t1 = new Thread(new OpenChart(returnCommonCoordinates()));
				System.out.println("Starting Thread");
				t1.start();

				/*
				 * Coord3d[] testy = new Coord3d[30000]; for(int i = 0; i <
				 * testy.length; i++){ testy[i] = new Coord3d(500, i + 60, 30 +
				 * i); } t1 = new Thread(new Testing(testy)); t1.start();
				 */
			}
		});

		getView.add(topView);
		getView.add(lateralView);
		getView.add(frontView);
		fileMenu.add(figureVoxels);
		fileMenu.add(testAll3D);

		//imageMenu.add(edgeDetection);
		imageMenu.add(threshold);
		mathMenu.add(angle);

		helpMenu.add(help);
		helpMenu.add(about);
	}

	//**************Backend code starts here************////

	//filter the body and the wing coordinates from the picture
	public void filterBody(int[][] image, int n) {
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[0].length; j++) {
				int red = (image[i][j] >> 16) & 0xff;
				int blue = (image[i][j] >> 8) & 0xff;
				int green = (image[i][j]) & 0xff;
				if (red == 0 && blue == 0 && green == 0) {
					if (n == 1)
						topViewCoordinates.add(new Point2D.Double(j, i));
					else if (n == 2)
						lateralViewCoordinates.add(new Point2D.Double(j, i));
					else if (n == 3)
						frontViewCoordinates.add(new Point2D.Double(j, i));
				}
			}
	}

	//Determine the intersecting voxels of the three views
	public void figureVoxels() {
		start = dateFormat.format(cal.getTime());
		long commonCounter = 0;
		for (int i = 0; i < topView3DCoordinates.size(); i++) {
			Coord3d sample = topView3DCoordinates.get(i);
			if (foundLateral(sample) == true && foundFront(sample) == true) {
				commonCounter++;
				System.out.println("Common Coordinate #" + commonCounter
					+ " found");
				commonCoordinates.add(sample);
			}
		}
		System.out.println("topView3DCoordinates: "
			+ topView3DCoordinates.size());
		System.out.println("lateralView3DCoordinates: "
			+ lateralView3DCoordinates.size());
		System.out.println("Figured out Voxels");
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		cal = Calendar.getInstance();
		System.out.println(start);
		System.out.println(dateFormat.format(cal.getTime()));
	}

	/*Load all the coordinates into the arrayList. Refer to notebook for more information
	regarding the way I am doing this*/
	public void addAllCoordinates(){
		for(int i = 0; i < topViewCoordinates.size(); i++){
			double x = 73 - topViewCoordinates.get(i).getX();
			double y = topViewCoordinates.get(i).getY();
			for(int z = 0; z < frontViewArray.length;z++){
				topView3DCoordinates.add(new Coord3d((float)x, (float)y, (float)z));
				//sampleCoordinates.add(new Coord3d((float)x, (float)y, (float)z));
			}
		}
		for(int i = 0; i < lateralViewCoordinates.size(); i++){
			double y = 73 - lateralViewCoordinates.get(i).getX();
			double z = 73 - lateralViewCoordinates.get(i).getY();
			for(int x = 0; x < frontViewArray[0].length;x++){
				lateralView3DCoordinates.add(new Coord3d((float)x,(float)y, (float)z));
				//sampleCoordinates.add(new Coord3d((float)x, (float)y, (float)z));
			}
		}
		for(int i = 0; i < frontViewCoordinates.size(); i++){
			double x = 73 - frontViewCoordinates.get(i).getX();
			double z = 73 - frontViewCoordinates.get(i).getY();
			for(int y = 0; y < topViewArray.length; y++){
				frontView3DCoordinates.add(new Coord3d((float)x, (float)y, (float)z));
				//sampleCoordinates.add(new Coord3d((float)x, (float)y, (float)z));
			}
		}
		System.out.println("topView3DCoordinates: " + topView3DCoordinates.size());
		System.out.println("lateralView3DCoordinates: " + lateralView3DCoordinates.size());
		System.out.println("frontView3DCoordinates: " + frontView3DCoordinates.size());

	}

	public boolean foundLateral(Coord3d coord) {
		for (int j = 0; j < lateralView3DCoordinates.size(); j++) {
			if (coord.equals(lateralView3DCoordinates.get(j))) {
				return true;
			}
		}
		return false;
	}

	public boolean foundFront(Coord3d coord) {
		for (int j = 0; j < frontView3DCoordinates.size(); j++) {
			if (coord.equals(frontView3DCoordinates.get(j))) {
				return true;
			}
		}
		return false;
	}

	//Got code for this from Internet
	private static int[][] convertTo2D(BufferedImage image) {

		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		final int width = image.getWidth();
		final int height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] result = new int[height][width];
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
			argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
			argb += ((int) pixels[pixel + 1] & 0xff); // blue
			argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
			argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
			result[row][col] = argb;
			col++;
			if (col == width) {
				col = 0;
				row++;
			}
		}
	} else {
		final int pixelLength = 3;
		for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
			int argb = 0;
			argb += -16777216; // 255 alpha
			argb += ((int) pixels[pixel] & 0xff); // blue
			argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
			argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
			result[row][col] = argb;
			col++;
			if (col == width) {
				col = 0;
				row++;
			}
		}
	}

	return result;
}

public Coord3d[] returnAllCoordinates(){
	Coord3d[] temp = new Coord3d[allCoordinates.size()];
	for(int i = 0; i < allCoordinates.size(); i++){
		temp[i] = allCoordinates.get(i);
	}
	return temp;
}

public Coord3d[] returnCommonCoordinates() {
	Coord3d[] temp = new Coord3d[commonCoordinates.size()];
	for (int i = 0; i < commonCoordinates.size(); i++) {
		temp[i] = commonCoordinates.get(i);
	}
	return temp;
}

public Coord3d[] returnSampleCoordinates() {
	Coord3d[] temp = new Coord3d[sampleCoordinates.size()];
	for (int i = 0; i < sampleCoordinates.size(); i++) {
		temp[i] = sampleCoordinates.get(i);
	}
	return temp;
}

public int surround(int y, int x, int[][] image){
	int counter = 0;
	for(int i = y - 1; i <= y+1; i++)
		for(int j = x -1; j <= x+1; j++){
			try{
				if(image[i][j] == -16777216)
					counter++;
			}catch(Exception e){;}
		}

		if(image[y][x] == -16777216)
			counter--;
		return counter;
	}


	//returns false is closer to white, returns true if closer to black
	public boolean distance(int y, int x, int[][] image){
		int red = (image[y][x] >> 16) & 0xff;
		int blue = (image[y][x] >> 8) & 0xff;
		int green = (image[y][x]) & 0xff;

		double dfb = Math.sqrt(Math.pow(red - 0, 2) + Math.pow(green - 0, 2) + Math.pow(blue - 0, 2));
		double dfw = Math.sqrt(Math.pow(red - 255, 2) + Math.pow(green - 255, 2) + Math.pow(blue - 255, 2));

		if(dfb > dfw){
			return false;
		}
		else{
			return true;
		}
	}


	public BufferedImage openImage (String ImageName) throws IOException {
		 // open image
		File imgPath = new File(ImageName);
		BufferedImage bufferedImage = ImageIO.read(imgPath);
		return bufferedImage;
	}

	/*Main Method for executing the sub-methods of this class*/
	public static void main(String[] args){
		HRMTDraft2 test = new HRMTDraft2();
	}

	private class InfoPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		public InfoPanel(){
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}
	}

	private class MediaPanel extends JPanel{
		private static final long serialVersionUID = 1L;

		public MediaPanel(){
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}
	}
}
