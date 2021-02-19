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

package output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import simulator.*;
import event.*;
import model.Position;


/**
 * QualnetFormatter generates mobility traces in the Qualnet simulator format.
 * Additionally, a file defining network interface failures is created.
 * The network interface of a node is set to failed during these time 
 * periods the mobile node is not inside the simulation area.
 * @author psommer
 *
 */
public class QualnetFormatter extends TraceFormatter {

	/**
	 * Waypoint describes the position of a mobile node at a certain time during the Simulator.
	 * @author psommer
	 *
	 */
	private class Waypoint extends Position implements Comparable<Waypoint> {
		/** node identifier */
		int id = 0;
		/** time when the node is at this point */
		double time = 0;
		
		/**
		 * Creates a Waypoint instance
		 * @param id Node identifier
		 * @param time Time
		 * @param x x position
		 * @param y y position
		 */
		public Waypoint(int id, double time, double x, double y) {
			this.id = id;
			this.time = time;
			this.x = x;
			this.y = y;
		}
		/**
		 * Compares this Waypoint with another Waypoint.
		 * First, the node identifier is compared and then the time is compared in a second step (if necessary).
		 */
		public int compareTo(Waypoint other) {
			// sort by node id and time
			if (id==other.id) {
				return Double.compare(time, other.time);
			} else return id-other.id;
		}
	}
	
	
	/** list of waypoints */
	List<Waypoint> waypoints = new LinkedList<Waypoint>();
	
	/**
	 * Converts a node identifier to an IPv4 address.
	 * @param id Node identifier
	 * @return IPv4 address corresponding to the value of the node identifier
	 */
	public String getIPAddress(int id) {
		int part1 = id & 255;
		int part2 = (id & (255<<8))>>8;
		int part3 = (id & (255<<16))>>16;
		int part4 = (id & (255<<24))>>24;
		return part4 + "." + part3 + "." + part2 + "." + part1;
	}
		
	
	public void finish() {
		
		
		/** buffered output writer */
		BufferedWriter traceWriter = null, failureWriter = null;
		
		LinkedList<Waypoint> reusableNodes = new LinkedList<Waypoint>();
		Hashtable<Integer, Integer> nodeMapping = new Hashtable<Integer, Integer>();
		
		double reuseAfterTime = 30;
		int uniqueNodes = 0;
		
		try {
			File dir = new File(Simulator.outputDirectory);
			dir.mkdirs();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		// output node positions
		try {
			traceWriter = new BufferedWriter(new FileWriter(new File(Simulator.outputDirectory + "/trace.mobility")));
	    } catch (Exception e) {
	    	System.err.println(e.getLocalizedMessage());
	    }
	    
	    
	    // output network interface failures
		try {
			failureWriter = new BufferedWriter(new FileWriter(new File(Simulator.outputDirectory + "/interface.fault")));
	    } catch (Exception e) {
	    	System.err.println(e.getLocalizedMessage());
	    }
	    
	    
	    // sort events by node id and time
	    Collections.sort(Simulator.events, new EventComparatorByStartTime());
	    
	    int nodeIdTemp = 0;
	    
	    // iteration over all events
	    Iterator<Event> it = Simulator.events.iterator();
	    while (it.hasNext()) {
	    	
	    	Event event = it.next();
	    	
	    	if (event.type==Event.MOVE) {
				Move move = (Move) event;
				nodeIdTemp = nodeMapping.get(event.node.id);
				waypoints.add(new Waypoint(nodeIdTemp, move.time, move.x, move.y));
				waypoints.add(new Waypoint(nodeIdTemp, (move.time + move.duration), move.moveToX, move.moveToY));
			
	    	} else if (event.type==Event.JOIN) {
				
				if (!reusableNodes.isEmpty() && (reusableNodes.peek().time + reuseAfterTime)<event.time) {
					
					// reuse node
					
					// add mapping
					Waypoint leavePoint = reusableNodes.poll();
					
					
					nodeIdTemp = leavePoint.id;
					nodeMapping.put(event.node.id, nodeIdTemp);
					
					//System.out.println(event.node.id + " maps to qualnet node: " + nodeIdTemp);
					//System.out.println("Reuse qualnet node " + nodeIdTemp + " at: " + event.time);
					
					waypoints.add(new Waypoint(nodeIdTemp, event.time, event.x, event.y));
					
					try {
						failureWriter.write("INTERFACE-FAULT " + getIPAddress(nodeIdTemp) + " " + String.format("%.2f", leavePoint.time) + "S " + String.format("%.2f", event.time) + "S\n");
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
					
					
				} else {
					uniqueNodes++;
					nodeIdTemp = uniqueNodes;
					// add new node
					nodeMapping.put(event.node.id, nodeIdTemp);
					
					if (event.time>0) {
						try {
							failureWriter.write("INTERFACE-FAULT " + getIPAddress(nodeIdTemp) + " 0S " + String.format("%.2f", event.time) + "S\n");
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
					
					
				}
				
				waypoints.add(new Waypoint(nodeIdTemp, event.time, event.x, event.y));
				
			} else if (event.type==Event.LEAVE) {
				nodeIdTemp = nodeMapping.get(event.node.id);
				// node leaves the simulation area -> re-use node in qualnet to keep the total number of nodes low
				
				// add point and time where this node left the simulation area
				reusableNodes.addLast(new Waypoint(nodeIdTemp, event.time, event.x, event.y));
			}
	    	
	    }
	    
	    Iterator<Waypoint> itWay = reusableNodes.iterator();
	    
	    while (itWay.hasNext()) {
	    	
	    	Waypoint exitPoint = itWay.next();
	    	
	    	if (exitPoint.time<Simulator.duration) {
				try {
					failureWriter.write("INTERFACE-FAULT " + getIPAddress(exitPoint.id) + " " + String.format("%.2f", exitPoint.time) + "S " + String.format("%.2f", Simulator.duration) + "S\n");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
	    	
	    }
	    
	    try {
			failureWriter.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	    
	    
	    
	    
	    // sort waypoints by node id and time
		Collections.sort(waypoints);
		
		// iteration over all waypoints
		Iterator<Waypoint> it2 = waypoints.iterator();
		Waypoint previous = null;
		
		while (it2.hasNext()) {
			
			Waypoint current = it2.next();
			if (previous==null || !(previous.id==current.id && Math.abs(previous.time-current.time)<0.01)) {
				
				if (previous==null || (previous.id!=current.id)) {
					// first waypoint of a new node
					try {
						// set initial position to the first postion in the simulation area
						if (current.time>0) traceWriter.write(String.format("%d %.2fS (%.2f, %.2f, %.2f)\n", current.id, 0.0, current.x, current.y , 0.0000));
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}
				}
				
				try {
					traceWriter.write(String.format("%d %.2fS (%.2f, %.2f, %.2f)\n", current.id, current.time, current.x, current.y , 0.0000));
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			} 
			previous = current;
		}
		
		try {
			traceWriter.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		
	    
	}
		
}
