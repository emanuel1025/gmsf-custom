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

package mobility.gis;


import java.util.*;
import java.io.*;

import model.*;

/**
 * LandscapeModelFactory populates a landscape model with geographical 
 * data based on the selected real-world scenario.
 * @author psommer
 *
 */
public final class LandscapeModelFactory {

	/**
	 * Adds roads to the landscape model
	 * @param file File with road information
	 * @param model Landscape model
	 * @return Returns true if the method completed successfully
	 */
	public static boolean addRoads(String file, LandscapeModel model) {
		
		/** hashtable mapping Strings to RoadNodes */
		Hashtable<String,RoadNode> nodeTable = new Hashtable<String,RoadNode>();
		
		/** collection of intersection (nodes) */
		Collection<RoadNode> nodes = new ArrayList<RoadNode>();
		/** collection of roads (edges) */
		Collection<RoadEdge> edges = new ArrayList<RoadEdge>();
		
		/** list of lines following the course of the road */
		LinkedList<Line> segments = new LinkedList<Line>();
		/** list of lines following the course of the road in the opposite direction */
		LinkedList<Line> segmentsOpposite = new LinkedList<Line>();
		
		int type = 0;
    	int identifier = 0;
    	
		double length = 0;
		
		try {
	        BufferedReader in = new BufferedReader(new FileReader(file));
	        String str;
	        while ((str = in.readLine()) != null) {
	           

	        	if (str.equals("<Road>")) {
	        		// start of new road
	        		segments.clear();
	        		segmentsOpposite.clear();
	        		length = 0;
	        		
	        	} else if (str.equals("</Road>")) {
	        		// end of road
	        		
	        		// start point
	        		double x1 = segments.get(0).x1;
	        		double y1 = segments.get(0).y1;
	        		
	        		
	        		// end point
	        		double x2 = segments.get(segments.size()-1).x2;
	        		double y2 = segments.get(segments.size()-1).y2;
	        		
	        		
	        		// generate node keys
	        		String key1 = x1 + "_" + y1;
					String key2 = x2 + "_" + y2;
					
					RoadNode node1 = null, node2 = null;
					
					
					// lookup in table if this node already exists
					if (nodeTable.containsKey(key1)) {
						node1 = nodeTable.get(key1);
					} else {
						node1 = new RoadNode(x1, y1);
						nodeTable.put(key1, node1);
						nodes.add(node1);
					}
					
					if (nodeTable.containsKey(key2)) {
						node2 = nodeTable.get(key2);
					} else {
						node2 = new RoadNode(x2, y2);
						nodeTable.put(key2, node2);
						nodes.add(node2);
					}
	        		
					// add directed edge
					RoadEdge edge = new RoadEdge(node1, node2, segments, length);
					edge.identifier = identifier;
					edge.type = type;
					edges.add(edge);
					// add directed edge for the opposite direction
					RoadEdge edge2 = new RoadEdge(node2, node1, segmentsOpposite, length);
					edge2.identifier=identifier;
					edge2.type = type;
					edges.add(edge2);
					
					
					if (type<=4) {
						edge.maxSpeed = 120/3.6;	// 120 km/h
						edge.priority = 4;
						edge.weight = edge.length/edge.maxSpeed;
						edge2.maxSpeed = edge.maxSpeed;
						edge2.priority = edge.priority;
						edge2.weight = edge2.length/edge2.maxSpeed;
						
					}
					else if (type==5 || type==6 || type==7) {
						edge.maxSpeed = 60/3.6;		// 60 km/h
						edge.priority = 3;
						edge.weight = edge.length/edge.maxSpeed;
						edge2.maxSpeed = edge.maxSpeed;
						edge2.priority = edge.priority;
						edge2.weight = edge2.length/edge2.maxSpeed;
					}
					else if (type==8 || type==9) {
						edge.maxSpeed = 50/3.6;		// 50 km/h
						edge.priority = 2;
						edge.weight = edge.length/edge.maxSpeed;
						edge2.maxSpeed = edge.maxSpeed;
						edge2.priority = edge.priority;
						edge2.weight = edge2.length/edge2.maxSpeed;
					}
					else {
						edge.maxSpeed = 30/3.6;								// 30 km/h
						edge.priority = 1;
						edge.weight = edge.length/edge.maxSpeed;
						edge2.maxSpeed = edge.maxSpeed;
						edge2.priority = edge.priority;
						edge2.weight = edge2.length/edge2.maxSpeed;
					}
	        		
	        		
	        		
	        	} else if (str.length()==0) {
	        		// empty line
	        	} else {
	        	
	        		// parse input
	        		String[] column = str.split(" ");
		        	identifier = Integer.parseInt(column[0]);
		        	double x1 = Double.parseDouble(column[2]);
					double y1 = Double.parseDouble(column[3]);
					double x2 = Double.parseDouble(column[4]);
					double y2 = Double.parseDouble(column[5]);
					
					type = Integer.parseInt(column[1]);
					Line line = new Line(x1,y1,x2,y2);
					length+=line.length;
					// add road segment
					segments.addLast(line);
					segmentsOpposite.addFirst(new Line(x2,y2,x1,y1));
					
				}
				
			}	
				
	        // close input file
	        in.close();
	        
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		// create a new road network
		model.roadNetwork = new RoadNetwork(nodes, edges);
		try {
			FileWriter writer = new FileWriter("points.wkt");
			writer.append("MULTIPOINT(");
			boolean first = true;
			for(RoadNode r:nodes) {
				if(!first) {
					writer.append(",");
				}else {
					first = false;
				}
				writer.append("("+r.x+" "+r.y+")");
			}
			writer.append(")\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

				
		System.out.println("Edges: " + model.roadNetwork.getEdges().size());
		System.out.println("Nodes: " + model.roadNetwork.getNodes().size());
		
		
		// post-processing of the graph
		// check if each node is reachable from all other nodes in the graph
		// (which is typically for a road network)
		Iterator<RoadNode> nodeIterator = model.roadNetwork.getNodes().iterator();
		
		int reachableNodes[] = new int[model.roadNetwork.getNodes().size()];
		
		int position = 0;
		int maxValue = 0;
		
		while (nodeIterator.hasNext()) {
			
			RoadNode start = nodeIterator.next();
			
			HashSet<RoadNode> visitedNodes = new HashSet<RoadNode>();
			LinkedList<RoadNode> nextNodes = new LinkedList<RoadNode>();
			
			nextNodes.add(start);
			
			RoadNode current = null;
			while ((current=nextNodes.poll())!=null) {
				
				Iterator<RoadEdge> it = current.getOutEdges().iterator();
				while (it.hasNext()) {
					RoadEdge edge = it.next();
					RoadNode next = edge.getOtherNode(current);
					
					if (!visitedNodes.contains(next) && !nextNodes.contains(next)) {
						nextNodes.add(next);
					}
					
				}
				
				visitedNodes.add(current);
			}
			
			reachableNodes[position] = visitedNodes.size();
			if (reachableNodes[position]>maxValue) maxValue = reachableNodes[position];
			System.out.println(position+" "+reachableNodes[position]+" "+maxValue);
			break;
			//position++;
		}
		
		
		
		System.out.println("Maximum number of reachable nodes: " + maxValue);
		
		nodeIterator = model.roadNetwork.getNodes().iterator();
		
		position = 0;
		
		while (nodeIterator.hasNext()) {
			
			RoadNode node = nodeIterator.next();
			
			if (reachableNodes[position]<maxValue) {
				
				// delete node and related edges
				
				Iterator<RoadEdge> edgeIterator = node.getInEdges().iterator();
				while (edgeIterator.hasNext()) {
					
					RoadEdge edge = edgeIterator.next();
					System.out.println("removing edge: (" + edge.getStartNode().x + "," +  edge.getStartNode().y + ") <-> (" + edge.getEndNode().x + "," +  edge.getEndNode().y + ")");
					model.roadNetwork.getEdges().remove(edge);
				}
				node.getInEdges().clear();
				
				edgeIterator = node.getOutEdges().iterator();
				while (edgeIterator.hasNext()) {
					model.roadNetwork.getEdges().remove(edgeIterator.next());
				}
				node.getOutEdges().clear();
				
				// remove node
				nodeIterator.remove();
				
				
				System.out.println("Removed node: " + node.id);
			}
			break;

			//position++;
		}
		
		// statistics
		/** total length of roads*/
		double totalRoadLength = 0;
		/** total weight of roads*/
		double totalRoadWeight = 0;
		
		Iterator<RoadEdge> edgeIterator = model.roadNetwork.getEdges().iterator();
		while (edgeIterator.hasNext()) {
			
			RoadEdge edge = edgeIterator.next();
			
			// update statistics
			totalRoadLength+=edge.length;
			totalRoadWeight+=edge.weight;
			
		}
		
		System.out.println("Total length of roads: " + totalRoadLength + " (m)");
		System.out.println("Average speed: " + totalRoadLength/totalRoadWeight + " (m/s)");
		
		// initialize traffic lights
		
		nodeIterator = model.roadNetwork.getNodes().iterator();
		while (nodeIterator.hasNext()) {
			
			RoadNode intersection = nodeIterator.next();

			// determine the number of incoming streets
			int incomingRoads = intersection.getInEdges().size();
			
			// no traffic lights for intersection with less than 4 incoming roads
			if (incomingRoads<4) intersection.trafficLight = false;
			else intersection.trafficLight = true;
		/*
			
			if (incomingRoads==3) {
				
				boolean samePriorityRoads = true;
				
				Iterator<RoadEdge> it = intersection.getInEdges().iterator();
				int lastPriority = -1;
				
				while (it.hasNext()) {
					
					RoadEdge road = it.next();
					if (lastPriority==-1) lastPriority = road.priority;
					else {
						if (road.priority!=lastPriority) samePriorityRoads = false;
						break;
					}
				}
				intersection.trafficLight = !samePriorityRoads;
			}
			
			
			if (incomingRoads>3) {
				
				intersection.trafficLight = true;
				
			}
	
			*/
			
			if (intersection.trafficLight) {
			
				// set priorities according to the street class
				int sliceTime = 120;
				double [] slices = new double[incomingRoads];
				
				Iterator<RoadEdge> it = intersection.getInEdges().iterator();
				
				int index = 0;
				int sum = 0;
				
				while (it.hasNext()) {
					
					RoadEdge road = it.next();
					slices[index] = road.priority;
					sum+=road.priority;
					index++;
				}
				
				intersection.slices = new int[incomingRoads];
				
				// normalize priorities
				for (index=0; index<slices.length;index++) {
					intersection.slices[index] = (int)Math.round(slices[index]/sum*sliceTime);
					//System.out.println("Road slice: " + intersection.slices[index]);
				}
/*
				// randomly select the green light in the opposite direction
				intersection.greenLight = Simulator.rng.nextInt(incomingRoads);
				intersection.greenLightOpposite = intersection.greenLight + 2;
				if (intersection.greenLightOpposite>=incomingRoads) intersection.greenLightOpposite = intersection.greenLightOpposite-incomingRoads;
	*/			
			}
			
			
			
		}
		
		
		
		return true;
	}
	
	
	
	public static boolean addPointOfInterests(String file, LandscapeModel model) {
		
		
		try {
	        BufferedReader in = new BufferedReader(new FileReader(file));
	        String str;
	        
	        int poiCounter = 0;
	        
	        while ((str = in.readLine()) != null) {
	           
	        	// parse input
        		String[] column = str.split(" ");
	        	
	        	double x = Double.parseDouble(column[0]);
				double y = Double.parseDouble(column[1]);
				int weight = Integer.valueOf(column[2]);
				
				
				
				// search road intersection with the smallest distance to the point of interest
				double minDistance = Double.MAX_VALUE;
				RoadNode closestIntersection = null;
				
				Iterator<RoadNode> it = model.roadNetwork.getNodes().iterator();
				double distance = 0;
				while (it.hasNext()) {
					RoadNode intersection = it.next();
					distance = Math.sqrt((x-intersection.x)*(x-intersection.x) + (y-intersection.y)*(y-intersection.y));
					if (distance<minDistance) {
						minDistance = distance;
						closestIntersection = intersection;
					}
				}
				
				System.out.println("minDistance" + minDistance);
				
				if (closestIntersection!=null) {
					
					System.out.println("Mapping POI (" + x + "," + y + ") to intersection " + closestIntersection.id + " (" + closestIntersection.x + "," + closestIntersection.y + ")" );
					for (int i=0; i<weight;i++) {
						model.destinations.add(closestIntersection);
						poiCounter++;
					}
				} else {
					System.out.println("Point of Interest (" + x + "," + y + ") has no road intersection in its proximity.");
				}
				
				
				
			}	
				
	        System.out.println("Added " + poiCounter + " points of interest");
	        
	        // close input file
	        in.close();
	        
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		
		
		return true;
	}
	
	
	public static boolean addPointOfInterests(LandscapeModel model) {
		
		Iterator<RoadNode> it = model.roadNetwork.getNodes().iterator();
		while (it.hasNext()) {
				model.destinations.add(it.next());
		}
		return true;
	}
	
	

/*	public static boolean addBuildings(LandscapeModel model, Simulation simulation) {
		
	
		int buildings = 0;
		int lastId = -1;
		Building building = null;
		MapArea area = model.area;
		
		try {
	        BufferedReader in = new BufferedReader(new FileReader("/home/psommer/ETHZ/MA/Resources/GIS/swissGIS/DBimport/Scenarios/luzern/buildings.dat"));
	        String str;
	        while ((str = in.readLine()) != null) {
	           
	        	String[] column = str.split(" ");
	        	
	        	double x1 = Double.parseDouble(column[1]);
				double y1 = Double.parseDouble(column[2]);
				double x2 = Double.parseDouble(column[3]);
				double y2 = Double.parseDouble(column[4]);
				int id = Integer.parseInt(column[0]);
	        	
				if (id!=lastId) {
					
					building = new Building();
					building.id = id;
					model.buildings.add(building);
					System.out.println("");
					
					buildings++;
					lastId = id;
				}
				
				// scale coordinates
				x1 = (x1 - area.xMin)/(area.xMax-area.xMin)*simulation.size;
				y1 = (y1 - area.yMin)/(area.yMax-area.yMin)*simulation.size;
				x2 = (x2 - area.xMin)/(area.xMax-area.xMin)*simulation.size;
				y2 = (y2 - area.yMin)/(area.yMax-area.yMin)*simulation.size;
				
				Line line = new Line(x1, y1, x2, y2);
				
				System.out.println("Line from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")");
				building.outline.add(line);
				
			}	
				
	        
	        in.close();
	        
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		
		System.out.println("Buildings: " + model.buildings.size());
		
	
		return true;
	}
	
	
	
*/
	
	
}
