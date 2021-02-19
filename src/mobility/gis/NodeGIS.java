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


import event.*;
import simulator.*;
import mobility.MobileNode;
import model.*;

/**
 * Node which implements a GIS-based mobility model
 * @author psommer
 *
 */
public class NodeGIS extends MobileNode {
	
	
	Path route = null;
	RoadNode destination = null;
	
	/** current change in speed */
	double dv = 0;
	/** target speed */
	double v_max = 0;
	/** speed difference */
	double v_delta = 0;
	/** driver reaction time */
	double t_react = 1.0;
	/** vehicle length */
	double length = 4;
	/** minimal desired gap between cars */
	double s_0 = 1;
	/** minimal desired gap to traffic light */
	double s_t = 2;
	/** distance to front vehicle */
	double s = Double.MAX_VALUE;
	/** comfortable acceleration */
	double a = 0.6;
	/** comfortable deceleration */
	double b = 0.9;
	/** breaking constant */
	double k = 5;
	
	
	// position at last sampling point
	Position lastPositionXY = new Position();
	

	/**
	 * Creates a new mobile node implementing the GIS based mobility model
	 * @param id Node identifier
	 */
	public NodeGIS(int id) {
		super(id);
	}
	
	
	/**
	 * Initialization of the node
	 */
	public void warmup() {
		
		// select a start node
		RoadNode start = GISModel.landscape.getNextDestination();
		
		lastPositionXY.x = start.x;
		lastPositionXY.y = start.y;
		
		destination = start;
		
		while (route==null) {
			// find a destination node
			while (destination==start) {
				destination = GISModel.landscape.getNextDestination();
			}
			
			// find the shortest path between source and destination
			route = GISModel.landscape.roadNetwork.getPath(start, destination);
		}
		
		// set the current road
		road = route.getNextRoad();
		
		// start position
		position = road.length*Simulator.rng.nextDouble();
		speed = 0;
		// enter the current road
		road.update(this);
		// set desired speed
		v_max = road.maxSpeed*(Simulator.rng.nextDouble()*0.05 + 0.95);
		
		
	}
	
	
	public void init() {
		// generate simulation join event
		addEvent(new Join(this, 0.0, x, y));
	}
	
	/**
	 * Prepare the next movement for this node. This method calculates the change in the speed (dv) which is
	 * then applied by the next() method.
	 */
	public void prepare() {

		//if (id==1) System.out.println("Node: " + id  + " - Current Road: " + road.identifier + " at position: " + position + ", speed: " + speed);
		
		
		s = Double.POSITIVE_INFINITY;
		
		if (GISModel.enableCarFollowing) {
			
			MobileNode frontVehicle = null;
			
			// check for other vehicles in front of this vehicle on the same street or on other streets on the route
			
			if (GISModel.enableTrafficLights) {
				
				double trafficLightDistance = Double.POSITIVE_INFINITY;
				
				// traffic lights
				if (road.getTrafficLight()) {
					trafficLightDistance = road.length - s_t - position;
				}
				
				// calculate breaking distance
				double s_breaking = speed*speed/(2*k*b);
				
				if (trafficLightDistance>s_breaking) {
					// still possible to break before the traffic light
					s = trafficLightDistance;
					v_delta = speed;
				} else {
					// don't brake before the traffic light
				}
				
			}
			
			
			// check if we are the foremost car on this lane
			if (road.getForemostVehicle()!=this) {
			
				// this node is not the foremost car on the current road
				
				frontVehicle = road.getFrontVehicle(this);
				double frontVehicleDistance = frontVehicle.position - length - position;
				s = frontVehicleDistance;
				v_delta = speed - frontVehicle.speed;
				//if (frontVehicleDistance<0.9) System.out.println("distance to front vehicle: " + frontVehicleDistance);
				
				//if (id==1) System.out.println("Distance to front driver (" + frontVehicle.id + "): " + frontVehicleDistance);
				
			}
			
		} 	
		
		if (s<Double.POSITIVE_INFINITY) {
			
			double s_star = s_0 + (speed*t_react + speed*v_delta/(2*Math.sqrt(a*b)));
			dv = a * (1 - Math.pow(speed/v_max, 4) - Math.pow(s_star/s, 2));
		} else {
			dv = a * (1 - Math.pow(speed/v_max, 4));
		}
		
		
	}
	
	
	public boolean next() {
			
		// update the current speed
		speed += dv*Simulator.step;
		
		if (speed<0.01) speed=0;
		
		// update the current position
		position += speed*Simulator.step;
		
		// check if the node is still within this street
		while (position>=road.length) {
			
			// update position
			position-=road.length;
			
			// remove car from the current street
			road.remove(this);
			
			// find next street on the route 
			
			if (route.hasNextRoad()) {
				
				road = route.getNextRoad();
				// set desired speed
				v_max = road.maxSpeed*(Simulator.rng.nextDouble()*0.05 + 0.95);
				
			} else {
				
				
				// destination reached, calculate a new route
				RoadNode start = destination;
				
				// issue join event
				
				destination = start;
				// unset route
				route=null;
				
				while (route==null) {
					// find a destination node
					while (destination==start) {
						destination = GISModel.landscape.getNextDestination();
					}
					// find the shortest path between source and destination
					route = GISModel.landscape.roadNetwork.getPath(start, destination);
				}
				
				road = route.getNextRoad();
				// set desired speed
				v_max = road.maxSpeed*(Simulator.rng.nextDouble()*0.05 + 0.95);
				
			}
			
		}
		
		road.update(this);

		
		// determine the current position
		
		// transform the position relative to the street into a position on the map
		Position positionXY = road.getPosition(this);
		x = positionXY.x;
		y = positionXY.y;
		
		// generate the next event
		if (!GISModel.warmupPhase && Simulator.time<Simulator.duration) {
			
			if (speed>0) addEvent(new Move(this, Simulator.time, lastPositionXY.x, lastPositionXY.y, positionXY.x, positionXY.y, speed, Simulator.step));
			else addEvent(new Pause(this, Simulator.time, Simulator.step, positionXY.x, positionXY.y));
		}
		
	
		lastPositionXY = positionXY;
		
		
		return super.next();
		
	}
	
	
	public void finish() {
		addEvent(new Leave(this, Simulator.duration, x, y));
		Simulator.removeNode(Simulator.duration, this);
		super.finish();
	}
	
	
}
