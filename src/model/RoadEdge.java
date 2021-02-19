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
package model;


import mobility.*;
import java.util.*;

/**
 * RoadEdge models a (directed) road in a road network.
 * The road starts at nodeA and ends at nodeB.
 * @author psommer
 *
 */
public class RoadEdge {

	/** start node (nodeA) */
	RoadNode nodeA = null;
	/** end node (nodeB) */
	RoadNode nodeB = null;
	
	/** unique road id */
	public int id = 0;
	/** identifier for this road in the GIS model */
	public int identifier = 0;
	
	/** road type */
	public int type = 0;
	/** road priority */
	public int priority = 0;
	/** weight of this route in the shortest path algorithm */
	public double weight = 0;
	/** length of this road */
	public double length = 0;
	/** speed limit on this road */
	public double maxSpeed = 0;
	/** status of the traffic light at the end of the road. True=red, false=green */
	boolean trafficLightRed = false;
	/** list of line segments following the course of the road */
	public List<Line> segments = null;
	/** list of vehicles on this road */
	ArrayList<MobileNode> vehicles = new ArrayList<MobileNode>(); 
	
	/**
	 * This comparator compares two mobile nodes based on the position relative
	 * to the current street
	 * @author psommer
	 *
	 */
	class CarOrderComparator implements Comparator<MobileNode> {
		public int compare(MobileNode nodeA, MobileNode nodeB) {
			return Double.compare(nodeA.position, nodeB.position);
		}
	}
	
	/** compares the order of two cars on the same road */
	CarOrderComparator comparator = new CarOrderComparator();
	
	/**
	 * Constructs a road between the two given intersections. 
	 * The road is directed. It starts at intersection nodeA and ends at intersection nodeB.
	 * @param nodeA Intersection where the road starts
	 * @param nodeB Intersection where the road ends
	 */
	public RoadEdge(RoadNode nodeA, RoadNode nodeB) {
		// set unique road identifier
		id = RoadNetwork.uniqueId++;
		// set nodeA
		this.nodeA = nodeA;
		// add the road as an outgoing edge
		this.nodeA.addOut(this);
		// set nodeB
		this.nodeB = nodeB;
		// add the road as an incoming edge
		this.nodeB.addIn(this);
		// calculate the length of this road
		length = Math.sqrt((nodeA.x-nodeB.x)*(nodeA.x-nodeB.x) + (nodeA.y-nodeB.y)*(nodeA.y-nodeB.y));
	}
	
	
	/**
	 * Constructs a road between the two given intersections defined by a intermediate road segments. 
	 * The road is directed. It starts at intersection nodeA and ends at intersection nodeB.
	 * @param nodeA Intersection where the road starts
	 * @param nodeB Intersection where the road ends
	 * @param segments Line segments following the course of the road
	 * @param length Road length
	 */
	public RoadEdge(RoadNode nodeA, RoadNode nodeB, List<Line> segments, double length) {
		// set unique road identifier
		id = RoadNetwork.uniqueId++;
		// set nodeA
		this.nodeA = nodeA;
		// add the road as an outgoing edge
		this.nodeA.addOut(this);
		// set nodeB
		this.nodeB = nodeB;
		// add the road as an incoming edge
		this.nodeB.addIn(this);
		this.segments = new ArrayList<Line>(segments);
		// set road length
		this.length = length;
	}
	
	/**
	 * Returns the start node of this road
	 * @return Intersection where this road starts
	 */
	public RoadNode getStartNode() {
		return nodeA;
	}
	
	/**
	 * Returns the end node of this road
	 * @return Intersection where this road ends
	 */
	public RoadNode getEndNode() {
		return nodeB;
	}
	
	/**
	 * Returns the other node related to the given node of this edge
	 * @param node Opposite node on the same edge of the node to return
	 * @return Returns the start node if the end node is specified and vice versa. Null is returned if the specified node belongs not to this edge.
	 */
	public RoadNode getOtherNode(RoadNode node) {
		if (nodeA==node) return nodeB;
		else if (nodeB==node) return nodeA;
		else return null;
	}
	
	/**
	 * Returns the current position of the mobile node on this road.
	 * @param node Mobile node
	 * @return Position of the mobile node
	 */
	public Position getPosition(MobileNode node) {
		
		
		if (segments!=null) {
			
			double distance = 0;
			Iterator<Line> it = segments.iterator();
			while (it.hasNext()) {
				Line segment = it.next();
				distance+=segment.length;
				
				if (node.position<=distance) {
					double fraction = 1 - (distance - node.position)/segment.length;
					return new Position(segment.x1 + fraction*(segment.x2-segment.x1), segment.y1 + fraction*(segment.y2-segment.y1));
				}
			}
			
			System.out.println("Position: " + node.position + " length: " + length);
			
			return null;
			
		} else {
			double fraction = node.position/length;
			return new Position(nodeA.x + fraction*(nodeB.x-nodeA.x), nodeA.y + fraction*(nodeB.y-nodeA.y));
		}
		
		
		
	}
	
	/**
	 * Set the traffic light at the end of this road to the given status
	 * @param status Traffic light status (true=red, false=green)
	 */
	public void setTrafficLight(boolean status) {
		trafficLightRed = status;
	}
	
	/**
	 * Returns the current status of the traffic light at the end of this road
	 * @return Traffic light status (true=red, false=green)
	 */
	public boolean getTrafficLight() {
		return trafficLightRed;
	}
	

	/**
	 * Updates the current position of a vehicle on this street
	 * @param car Vehicle which updated its position
	 */
	public void update(MobileNode car) {
		
		
		// remove vehicle
		vehicles.remove(car);
		
		// find the correct insert position for this vehicle
	    int index = Collections.binarySearch(vehicles, car, comparator);
	
	    // re-add the vehicle at the correct position
	    if (index < 0) {
	        vehicles.add(-index-1, car);
	    } else {
	    	vehicles.add(index, car);
	    }

	    // set this street as the current street
		car.road = this;
		
		/*
		double lastPosition = 0;
		int lastId = -1;
		
		for (int i=0; i<vehicles.size();i++) {
			
			MobileNode current = vehicles.get(i);
			
			if (current.position<0 || current.position>length) System.out.println("Car not inside road bounds");
			
			if (current.position<lastPosition) {
				System.out.println("error in car order");
			}
			
			//if (current.position-lastPosition<1.0 && lastId!=-1) System.out.println("Road " + id + " (" + vehicles.size() + " vehicles): gap to close: " + (current.position - lastPosition) + " first: (" + lastId + ") " + lastPosition + " next (" + current.id + ") " + current.position);
			lastPosition = current.position;
			lastId = current.id;
		}
		*/
		
		
		
	}
	
	
	/**
	 * Removes the car from the list of vehicle on this Street
	 * @param car Vehicle to remove from this street
	 */
	public void remove(MobileNode car) {
		vehicles.remove(car);
	}
	
	/**
	 * Gets the rearmost vehicle on this road
	 * @return Vehicle in the rearmost position on this road or null if there is no vehicle on this road.
	 */
	public MobileNode getRearmostVehicle() {
		try {
			return vehicles.get(0);
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Gets the foremost vehicle on this road
	 * @return Vehicle in the foremost position on this road or null if there is no vehicle on this road.
	 */
	public MobileNode getForemostVehicle() {
		try {
			return vehicles.get(vehicles.size()-1);
		} catch (Exception e){
			return null;
		}
	}
	
	
	/**
	 * Gets the vehicle in front of the specified vehicle
	 * @param currentVehicle Vehicle from which we want to have the vehicle in front
	 * @return Vehicle in front of the specified vehicle or null if no such vehicle exisits 
	 */
	public MobileNode getFrontVehicle(MobileNode currentVehicle) {
		
		int index = vehicles.indexOf(currentVehicle);
		if (index>-1 && index<vehicles.size()-1) return vehicles.get(index+1);
		return null;
	}
	
	/**
	 * Lists vehicles on this road
	 */
	public void listCars() {
		
		Iterator<MobileNode> it = vehicles.iterator();
		System.out.println("Cars on street (length=" + length + "): " + getEndNode().id + "->" + getStartNode().id);

		while (it.hasNext()) {
			MobileNode node = it.next();
			System.out.println("\tNode " + node.id + " position=" + node.position + " speed=" + node.speed);
		}
	}

	
}
