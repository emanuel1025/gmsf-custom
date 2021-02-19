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

import java.util.*;
import mobility.*;

/**
 * RoadNode models an intersection between roads.
 * @author psommer
 *
 */
public class RoadNode extends Position {

	/** unique identifier for this intersection */
	public int id = 0;
	
	/** list of outgoing roads */
	List<RoadEdge> outEdges = new ArrayList<RoadEdge>();
	/** list of incoming roads */
	List<RoadEdge> inEdges = new ArrayList<RoadEdge>();
	
	/** indicates that node has already been visited in the Dijkstra's algorithm */
	boolean dijkstraVisited = false;
	/** current cost of this node in the Dijkstra's algorithm */
	double dijkstraCost = Double.MAX_VALUE;
	/** edge to parent node in the Dijkstra's algorithm */
	RoadEdge dijkstraEdgeParent = null;
	
	/** defines if this intersection is controlled by a traffic light */
	public boolean trafficLight = false;
	/** defines the index of the incoming road which has currently a green traffic light */
	public int greenLight = 0;
	/** defines the index of the incoming road (opposite direction) which has currently a green traffic light */
	public int greenLightOpposite = 0;
	/** counter variable for traffic light scheduler */
	int counter = 0;
	/** duration of the current traffic light time slice */
	int currentSlice = 0;
	/** time slices (duration of green phase) for all traffic lights */
	public int slices[] = null;
	
	
	
	/**
	 * Creates a road intersection at the specified position.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public RoadNode(double x, double y) {
		this.x = x;
		this.y = y;
		id = RoadNetwork.uniqueId++;
	}
	
	/**
	 * Adds an incoming road to this intersection
	 * @param edge Road
	 */
	public void addIn(RoadEdge edge) {
		inEdges.add(edge);
	}
	
	/**
	 * Adds an outgoing road to this intersection
	 * @param edge Road
	 */
	public void addOut(RoadEdge edge) {
		outEdges.add(edge);
	}
	
	/** Returns a list of outgoing roads */
	public List<RoadEdge> getOutEdges() {
		return outEdges;
	}
	
	/** Returns a list of incoming roads */
	public List<RoadEdge> getInEdges() {
		return inEdges;
	}
	
	/**
	 * Returns the incoming road which starts at the specified intersection and ends at this intersection
	 * @param node Intersection where the road starts
	 * @return Road which starts at the specified intersection and ends at this intersection or null if no such road exists.
	 */
	public RoadEdge getInEdge(RoadNode node) {
		RoadEdge edge = null;
		
		Iterator<RoadEdge> it = inEdges.iterator();
		while (it.hasNext()) {
			RoadEdge temp = it.next();
			if ((temp.nodeA==this && temp.nodeB==node) || (temp.nodeB==this && temp.nodeA==node)) {
				edge = temp;
				break;
			}
		}
		return edge;
	}
	
	/**
	 * Returns the outgoing road which starts here and ends at the specified intersection
	 * @param node Intersection where the road ends
	 * @return Road which starts at this intersection and ends at the specified intersection or null if no such road exists.
	 */
	public RoadEdge getOutEdge(RoadNode node) {
		RoadEdge edge = null;
		
		Iterator<RoadEdge> it = outEdges.iterator();
		while (it.hasNext()) {
			RoadEdge temp = it.next();
			if ((temp.nodeA==this && temp.nodeB==node) || (temp.nodeB==this && temp.nodeA==node)) {
				edge = temp;
				break;
			}
		}
		return edge;
	}
	
	/**
	 * Initializes the intersection for the simulation. 
	 * Traffic lights at this intersection are initialized (if applicable).
	 */
	public void init() {
		
		if (trafficLight) {
			// set all traffic lights to red
			Iterator<RoadEdge> it = getInEdges().iterator();
			while (it.hasNext()) {
				RoadEdge road = it.next();
				// set traffic lights to red
				road.setTrafficLight(true);
			}
			
			RoadEdge greenRoad = getInEdges().get(greenLight);
			greenRoad.setTrafficLight(false);
			currentSlice = slices[greenLight];
			counter = 0;
		}
		
			
	}
	
	/**
	 * Updates the traffic light for the next simulation step.
	 * This method toggles traffic lights if necessary.
	 */
	public void next() {

		// check if this intersection is controlled by traffic lights
		if (trafficLight) {
			
			if (counter<currentSlice) {
				// increase counter
				counter++;
			} else {
				
				// set the traffic light of the current active street to red
				getInEdges().get(greenLight).setTrafficLight(true);
				
				// get next traffic light in a round robin way
				greenLight++;
				if (greenLight==getInEdges().size()) greenLight = 0;
				
				// set the duration of the green phase for the current traffic light
				currentSlice = (int)Math.round(slices[greenLight]);
				// reset counter
				counter = 0;
				// set traffic light to green
				getInEdges().get(greenLight).setTrafficLight(false);	
				
				
				// traffic light in opposite direction (only for more than 4 roads)
		/*
				// set the traffic light of the current active street to red
				getInEdges().get(greenLightOpposite).setTrafficLight(true);
				
				// get next traffic light in a round robin way
				greenLightOpposite++;
				if (greenLightOpposite==getInEdges().size()) greenLightOpposite = 0;
			
				// set traffic light to green
				getInEdges().get(greenLightOpposite).setTrafficLight(false);	
				
				System.out.println("Green lights: " + greenLight + " " + greenLightOpposite);
				
		*/
			}
			
		} else {
			// first come, first served
			
			double minDistance = Double.MAX_VALUE;
			double vehicleDistance = 0;
			int roadIndex = -1;
			
			int index = 0;
			Iterator<RoadEdge> it = getInEdges().iterator();
			while (it.hasNext()) {
			
				RoadEdge road = it.next();
				MobileNode vehicle = road.getForemostVehicle();
				if (vehicle!=null)  {
					vehicleDistance = road.length - vehicle.position;
					if (vehicleDistance<minDistance) {
						minDistance = vehicleDistance;
						roadIndex = index; 
					}
				}
				index++;
				
				// switch traffic light to red
				road.setTrafficLight(true);
				
			}
			
			// switch traffic light to green
			if (roadIndex!=-1) getInEdges().get(roadIndex).setTrafficLight(false);
			
		}
		
		
		
		
	}
	

}
