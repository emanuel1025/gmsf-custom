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
package mobility.manhattan;

import event.*;
import simulator.*;
import mobility.MobileNode;
import model.*;
import java.util.*;

/**
 * Implementation of a node in the manhattan mobility model
 * @author      psommer
 */
public class NodeManhattan extends MobileNode {
	
	Position lastPositionXY = null;
	double dv = 0;
	
	/**
	 * Creates a new node implementing the Manhattan mobility model
	 * @param id unique node identifier
	 */
	public NodeManhattan(int id) {
		super(id);
	}
	
	
	public void warmup() {
		
				
		// select a start node
		road = ManhattanModel.roadNetwork.getEdges().get(Simulator.rng.nextInt(ManhattanModel.roadNetwork.getEdges().size()));
		position = Simulator.rng.nextDouble()*road.length;
		Position posXY= road.getPosition(this);
		
		lastPositionXY = posXY;
		speed = (ManhattanModel.speedMax + ManhattanModel.speedMin)/2;
		
		road.update(this);
		
	}
	
	
	public void init() {
		// generate simulation join event
		addEvent(new Join(this, 0.0, x, y));
	}
	
	public void prepare() {
			// update node velocity
			dv = (2*Simulator.rng.nextDouble() - 1)*ManhattanModel.acceleration;
			
			// find preceeding driver
			MobileNode preceedingDriver = road.getFrontVehicle(this);
			if (preceedingDriver!=null && (preceedingDriver.position - position)<=ManhattanModel.securityDistance) {
				// limit speed
				if (preceedingDriver.speed<(speed+dv)) dv = preceedingDriver.speed - speed;		
			} 
			
	}
	
	
	/**
	 * Updates the node 
	 */
	public boolean next() {
		
		speed+=dv;
		if (speed<ManhattanModel.speedMin) speed = ManhattanModel.speedMin;
		if (speed>ManhattanModel.speedMax) speed = ManhattanModel.speedMax;
		
		position+=speed*Simulator.step;
		
		//System.out.println("velocity: " + velocity);
		
		if (position>=road.length) {
			// move into next road
			
			RoadEdge nextRoad = null;
			
			Iterator<RoadEdge> it = road.getEndNode().getOutEdges().iterator();
			
			int direction = Simulator.rng.nextInt(4);
			
			while (it.hasNext()) {
			
				RoadEdge outgoingRoad = it.next();
				
				if (outgoingRoad.getEndNode()==road.getStartNode()) {
					// street in the opposite direction
					if (road.getEndNode().getOutEdges().size()==1) {
						nextRoad = outgoingRoad;
						break;
					}
				} else {
					
					if (direction<2) {
						// go straight ahead
						if (outgoingRoad.getEndNode().x==road.getStartNode().x || outgoingRoad.getEndNode().y==road.getStartNode().y) {
							nextRoad = outgoingRoad;
							break;
						}
						
					} else if (direction==2) {
						// turn left
						
						if (road.getStartNode().x==road.getEndNode().x) {
							// currently on a vertical street
							if (road.getEndNode().x>outgoingRoad.getEndNode().x) {
								nextRoad = outgoingRoad;
								break;
							}
						} else {
							// currently on a horizontal street
							if (road.getEndNode().y>outgoingRoad.getEndNode().y) {
								nextRoad = outgoingRoad;
								break;
							}
						}
						
					} else {
						// turn right
						
						if (road.getStartNode().x==road.getEndNode().x) {
							// currently on a vertical street
							if (road.getEndNode().x<outgoingRoad.getEndNode().x) {
								nextRoad = outgoingRoad;
								break;
							}
						} else {
							// currently on a horizontal street
							if (road.getEndNode().y<outgoingRoad.getEndNode().y) {
								nextRoad = outgoingRoad;
								break;
							}
						}
						
					}
					
				}
				
				
				
				
			} // while
			
			// remove car from current road
			road.remove(this);
			position = position - road.length;
			
			road = nextRoad;
			road.update(this);
			
			
			
		
		}
	
		Position posXY = road.getPosition(this);
		
		
		// generate a new MOVE event
		if (!ManhattanModel.warmupPhase && Simulator.time<Simulator.duration) addEvent(new Move(this, Simulator.time, lastPositionXY.x, lastPositionXY.y, posXY.x, posXY.y, speed));
		
		lastPositionXY = posXY;
		
		return super.next();
	}	
	
	/** 
	 * Clean-up after the end of the simulation 
	 **/
	public void finish() {
		addEvent(new Leave(this, Simulator.duration, x, y));
		Simulator.removeNode(Simulator.duration, this);
		super.finish();
	}
	
}
