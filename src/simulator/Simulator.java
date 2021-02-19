/* Copyright (c) 2007-2009, Computer Engineering and Networks Laboratory (TIK), ETH Zurich.
*  All rights reserved.
*
*  Redistribution and use in source and binary forms, with or without
*  modification, are permitted provided that the following conditions
*  are met:
*
*  1. Redistributions of source code must retain the above copyright
*     notice, this list of conditions and the following disclaimer.
*  2. Redistributions in binary form must reproduce the above copyright
*     notice, this list of conditions and the following disclaimer in the
*     documentation and/or other materials provided with the distribution.
*  3. Neither the name of the copyright holders nor the names of
*     contributors may be used to endorse or promote products derived
*     from this software without specific prior written permission.
*
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS `AS IS'
*  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
*  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
*  ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS
*  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
*  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, LOSS OF USE, DATA,
*  OR PROFITS) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
*  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
*  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
*  THE POSSIBILITY OF SUCH DAMAGE.
*
*  @author Philipp Sommer <phsommer@users.sourceforge.net>
* 
*/


package simulator;


import java.util.*;
import java.util.concurrent.TimeUnit;

import event.Event;
import gui.GUI;
import output.*;
import mobility.*;
import mobility.gis.GISModel;
import mobility.manhattan.ManhattanModel;
import mobility.mmts.MMTSModel;
import mobility.rwp.RandomWaypointModel;
import mobility.fixed.FixedModel;
import model.*;


public class Simulator {

	
	/** Mobility model used for the simulation of node mobility */
	public static MobilityModel mobilityModel = null;
	
	/** list modules attached to the simulator */
	static LinkedList<Module> modules = new LinkedList<Module>();
	
	/** random number generator */
	public static Random rng = new Random();
	
	/** simulation parameters */
	public static Properties parameters = new Properties();
	
	/** directory for input files */
	public static String inputDirectory = "";
	/** directory for output files */
	public static String outputDirectory = "";
	
	/** simulation duration */
	public static double duration = 0;
	/** current simulation time */
	public static double time = 0;
	/** size of a sample step */
	public static double step = 1.0;
	/** number of samples */
	public static int samples = 0;
	
	/** number of (unique) nodes in the simulation */
	public static int uniqueNodes = 0;
	/** average number of nodes */
	public static double avgNodes = 0;
	/** average node participation time */
	public static double avgNodeTime = 0;
	/** number of nodes joined the simulation area */
	public static int nodeJoins = 0;
	
	/** nodes participating in the simulation */
	public static ArrayList<MobileNode> nodes = new ArrayList<MobileNode>();
	/** node events */
	public static LinkedList<Event> events = new LinkedList<Event>();
	/** graph representing the neighborhood relations between nodes */
	public static Graph neighborhoodGraph = new Graph();
	
	/** seed for the random number generators */
	public static long seed = 0;
	
	
	/** size of the simulation square area */
	public static int size = 0;
	
	
	
	public static void main(String[] args) {

		
		System.out.println("*************************************************");
		System.out.println("*  Generic Mobility Simulation Framework (GMSF) *");
		System.out.println("*  Philipp Sommer, Computer Engineering and     *");
		System.out.println("*  Networks Laboratory (TIK), ETH Zurich        *");
		System.out.println("*************************************************");

		long start = System.nanoTime();
		// parse input parameters
		if (args.length<1) {
			System.err.println("No parameters specifed.");
			return;
		}
		
		String[] pairs = args[0].split(",");
		for (int i=0; i<pairs.length;i++) {
			String[] parts = pairs[i].split("=");
			parameters.setProperty(parts[0], parts[1]);
		}
		
		
		// input directory
		if (parameters.containsKey("INPUT_DIRECTORY")) {
			try {
				 inputDirectory = parameters.getProperty("INPUT_DIRECTORY");
			} catch (Exception e) {
				System.err.println("Error parsing parameter INPUT_DIRECTORY: " + e.getMessage());
			}
		}
		// output directory
		if (parameters.containsKey("OUTPUT_DIRECTORY")) {
			try {
				 outputDirectory = parameters.getProperty("OUTPUT_DIRECTORY");
			} catch (Exception e) {
				System.err.println("Error parsing parameter OUTPUT_DIRECTORY: " + e.getMessage());
			}
		} else {
			outputDirectory = System.getProperty("user.dir");
		}
		
			
		if (parameters.containsKey("SIMULATION_SIZE")) {
			try {
				size = Integer.valueOf(parameters.getProperty("SIMULATION_SIZE"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter SIMULATION_SIZE: " + e.getMessage());
			}
		}
		
		
		if (parameters.containsKey("TIME")) {
			try {
				duration = Double.valueOf(parameters.getProperty("TIME"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter TIME: " + e.getMessage());
			}
		}

		
		if (parameters.containsKey("SEED")) {
			try {
				seed = Long.valueOf(parameters.getProperty("SEED"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter SEED: " + e.getMessage());
			}
		}
		
	
		// initialize random number generator
		rng = new Random(seed);
		
		// initialize mobility model
		if (parameters.containsKey("MODEL")) {
			try {
				String model = parameters.getProperty("MODEL");
				if (model.equals("RWP")) {
					mobilityModel = new RandomWaypointModel();
				} else if (model.equals("MN")) {
					mobilityModel = new ManhattanModel();
		 		} else if (model.equals("GIS")) {
		 			mobilityModel = new GISModel();
				} else if (model.equals("MMTS")) {
					mobilityModel = new MMTSModel();
				} else if (model.equals("FIXED")) {
					mobilityModel = new FixedModel();
				}
			} catch (Exception e) {
				System.err.println("Error parsing parameter MODEL: " + e.getMessage());
			}
		} else {
			System.err.println("No mobility model specified. Use the MODEL parameter to specify a mobility model.");
		}
		
		
		// initialize traces output module
		
		if (parameters.containsKey("FORMAT")) {
			try {
				String output = parameters.getProperty("FORMAT");
				if (output.equals("QUALNET")) modules.add(new QualnetFormatter());
				else if (output.equals("NAM")) modules.add(new NAMFormatter());
				else if (output.equals("NS-2")) modules.add(new NS2Formatter());
				else if (output.equals("XML")) modules.add(new XMLFormatter());
				else if (output.equals("PDF")) modules.add(new PDFFormatter());
			} catch (Exception e) {
				System.err.println("Error parsing parameter FORMAT: " + e.getMessage());
			}
		}
		
		
		// graphical user interface
		if (parameters.containsKey("GUI")) {
			try {
				boolean enableGUI = (1==Integer.valueOf(parameters.getProperty("GUI")));
				// initialize graphical user interface (GUI)
				if (enableGUI) modules.add(new GUI());
			} catch (Exception e) {
				System.err.println("Error parsing parameter GUI: " + e.getMessage());
			}
		}
		
		
		
		// simulation time settings
		time = 0;
		samples = (int)Math.floor((duration)/step)+1;
		
				
		// initialize simulation
		mobilityModel.init();
		
		// initialize all modules
		Iterator<Module> moduleIterator = modules.iterator();
		while (moduleIterator.hasNext()){
			Module module = moduleIterator.next();
//			System.out.println("Initializing module: " + module.name);
			module.init();
		}
		
		
		// perform simulation
		for (int sample=0; sample<samples; sample++) {
			System.out.println("Sample point: " + sample + "/" + samples + " time=" + time);
			
			// update node positions
			mobilityModel.next();
			
			// modules
			moduleIterator = modules.iterator();
			while (moduleIterator.hasNext()){
				Module module = moduleIterator.next();
				module.next();
			}
					
			// count average number of nodes
			avgNodes += nodes.size();

			// update simulation time
			time+=step;
			
		}
		
		
		// simulation finished
		mobilityModel.finish();

		System.out.println("Everything's done!");
		

		// calculate average time node is in the simulation area
		avgNodeTime=1.00*avgNodeTime/nodeJoins;
		avgNodes = 1.00*avgNodes/samples;
		
		
		// finish all modules
		moduleIterator = modules.iterator();
		while (moduleIterator.hasNext()){
			Module module = moduleIterator.next();
			//System.out.println("Finish module: " + module.name);
			module.finish();
		}

		// some time passes
		long end = System.nanoTime();

		long elapsedTime = end - start;
		long timeTaken = TimeUnit.SECONDS.convert(elapsedTime, TimeUnit.NANOSECONDS);
		System.out.println("Time taken in seconds: " + timeTaken);
	}

	
	public static void addNode(double time, MobileNode node) {
		// add a new node to the simulation
		nodes.add(node);

		// update all modules
		Iterator<Module> moduleIterator = modules.iterator();
		while (moduleIterator.hasNext()){
			Module module = moduleIterator.next();
			module.addNode(time, node);
		}
		nodeJoins++;		
		
	}
	
	public static void removeNode(double time, MobileNode node) {
		// remove node from the simulation
		nodes.remove(node);
		
		// update all modules
		Iterator<Module> moduleIterator = modules.iterator();
		while (moduleIterator.hasNext()){
			Module module = moduleIterator.next();
			module.removeNode(time, node);
		}
		
		// update average time node is in simulation area
		avgNodeTime+=node.leaveTime-node.joinTime;
	}
	
	public static void addEvent(Event event) {
		events.add(event);
	}
	
	
}
