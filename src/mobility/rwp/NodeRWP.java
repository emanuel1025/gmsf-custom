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

package mobility.rwp;

import simulator.*;
import mobility.MobileNode;
import event.*;


/**
 * Node which implements the Random Waypoint mobility model
 * @author psommer
 *
 */
public class NodeRWP extends MobileNode {
	
	/**
	 * Creates a new node implementing the random waypoint mobility model
	 * @param id unique node identifier
	 */
	public NodeRWP(int id) {
		super(id);
	}
	
	public void init() {
		
		if (Simulator.rng.nextDouble()<RandomWaypointModel.probabilityPause) {
			// node starts paused
			
			// determine length of initial pause
			double initWaitTime = 0;
			
			// u ~ uniform (0,1)
			double u = Simulator.rng.nextDouble();
			double threshold = RandomWaypointModel.waitTimeDistribution.getMin()/RandomWaypointModel.waitTimeDistribution.getMean();
			
			if (u<threshold) initWaitTime = u*RandomWaypointModel.waitTimeDistribution.getMean();
			else initWaitTime = RandomWaypointModel.waitTimeDistribution.getMax()-Math.sqrt((1-u)*(RandomWaypointModel.waitTimeDistribution.getMax()*RandomWaypointModel.waitTimeDistribution.getMax() - RandomWaypointModel.waitTimeDistribution.getMin()*RandomWaypointModel.waitTimeDistribution.getMin()));
			
			// initial position
			Event join = new Join(this, 0.0, Simulator.rng.nextDouble()*Simulator.size, Simulator.rng.nextDouble()*Simulator.size);
			addEvent(join);
			
			Pause pause = new Pause(this, 0.0, initWaitTime, join.x, join.y);
			addEvent(pause);
			
			
		} else {
			// node starts moving
			
			// rejection sampling of initial position
			
			boolean reject = true;
			
			while (reject) {
				
				double x1 = Simulator.rng.nextDouble();
				double x2 = Simulator.rng.nextDouble();
				double y1 = Simulator.rng.nextDouble();
				double y2 = Simulator.rng.nextDouble();
				
				
				double r = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))/RandomWaypointModel.maxDistanceNormalized; 
				double u = Simulator.rng.nextDouble();
				
				
				if (u<r) {
					// accept initial positions
					reject = false;
					
					u = Simulator.rng.nextDouble();
					// select a random position on the line between (x1,y1) and (x2,y2)
					double initX = u*x1 + (1-u)*x2;
					double initY = u*y1 + (1-u)*y2;
					
					// initial speed
					u = Simulator.rng.nextDouble();
					double initSpeed = Math.pow(RandomWaypointModel.velocityDistribution.getMax(), u)/Math.pow(RandomWaypointModel.velocityDistribution.getMin(), u-1);
					
					// node joins the simulation
					Join join = new Join(this, 0.0, Simulator.size*initX, Simulator.size*initY);
					addEvent(join);
					

//					Move move = new Move(this, 0.0, join.x, join.y, Simulator.size*x2, Simulator.size*y2, initSpeed);
//					addEvent(move);

					// node movement
					double curTime = 0;
					double startX = join.x;
					double startY = join.y;
					double endX = Simulator.size*x2;
					double endY = Simulator.size*y2;

					double curX;
					double curY;
					double prevX = startX;
					double prevY = startY;

					while (curTime < Simulator.duration) {
						curX = startX + (endX - startX)*(curTime/Simulator.duration);
						curY = startY + (endY - startY)*(curTime/Simulator.duration);
						Move move = new Move(this, 0.0, prevX, prevY, curX, curY, initSpeed);
						addEvent(move);
						prevX = curX;
						prevY = curY;
						curTime++;
					}
				}
									
			}
							
		}
		
		
	}
	
	public void prepare() {
		
		// generate new events until the current simulation time (if necessary)
		while (lastEventEndTime<=Simulator.time) {
			
			// generate the next event
			if (lastEvent!=null && lastEvent.type==Event.MOVE) {
				
				// currently moving -> generate a new PAUSE event
				Move move  = (Move)lastEvent;
				Pause pause = new Pause(this, lastEventEndTime, RandomWaypointModel.waitTimeDistribution.nextValue(), move.moveToX, move.moveToY);
				addEvent(pause);
				
			} else if (lastEvent!=null && lastEvent.type==Event.PAUSE) {
				
				// generate a new MOVEMENT event
				Move move = new Move(this, lastEventEndTime, lastEvent.x, lastEvent.y, Simulator.size*Simulator.rng.nextDouble(), Simulator.size*Simulator.rng.nextDouble(), RandomWaypointModel.velocityDistribution.nextValue());
				addEvent(move);
				
			}
			
		}
		
	}
	
	public boolean next() {
		return super.next();
	}
	
	public void finish() {
		addEvent(new Leave(this, Simulator.duration, x, y));
		Simulator.removeNode(Simulator.duration, this);
		super.finish();
	}
	
}
