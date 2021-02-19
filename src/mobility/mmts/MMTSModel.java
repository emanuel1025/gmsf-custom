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

package mobility.mmts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import mobility.*;

import event.*;
import simulator.*;



/**
 * Implementation of a mobility model based on traces of MMTS (Microscopic Mulit-agent traffic simulator)
 * For more information about MMTS, see: http://www.lst.inf.ethz.ch/research/ad-hoc/car-traces/
 * @author psommer
 *
 */
public class MMTSModel extends MobilityModel {

	/** reader for the traces file */
	BufferedReader input = null;
	/** last read input line */
	String line = null;
	/** unique node id counter */
	int nodeUniqueId = 0;
	/** specifies the number of second events are read in advance */
	double readahead = 10;
	
	/** current event properties */
	double t1 = 0, x1 = 0, y1 = 0, x2 = 0, y2 = 0, t2 = 0, dt = 0;
	int id = 0;
	
	public static HashSet<String> poiSet = new HashSet<String>();
	
	
	
	
	public void init() {

		
		// open input reader for the traces file
		try {
	        input = new BufferedReader(new FileReader(Simulator.inputDirectory + "/mmts.dat"));
	        // read the first line in the file
	    } catch (Exception e) {
			System.err.println("MMTS trace file not found: " + e.getMessage());
			System.exit(0);
		}
	    
	    // read the first event
	    readEvent();
	    
	}

	
	private boolean readEvent() {
		
		try {
			line = input.readLine();
			//if (line!=null) System.out.println("Read event: " + line);
		} catch (Exception e) {
			return false;
		}
		if (line==null) return false;
		
		// split into columns 	
	    String[] column = line.split(" ");
	        
	    
	    // event start time
	    t1 = Double.parseDouble(column[0]);
	    // node id
	    id = Integer.parseInt(column[1]);
	    // start position
	    x1 = Double.parseDouble(column[2]);
		y1 = Double.parseDouble(column[3]);
		// end position
		x2 = Double.parseDouble(column[4]);
		y2 = Double.parseDouble(column[5]);
		// duration
		dt = Double.parseDouble(column[6]);
		// event end time
		t2 = t1 + dt;
		return true;
	}
	
	/**
	 * Updates the model for the next sample time
	 * 
	 */
	public void next() {
		
		// current node
		MobileNode currentNode = null;
			
		
		while (line!=null) {
			
			if (t1<=(Simulator.time + readahead)) {
				
				// process event
				
				if (id>nodeUniqueId) {
					// create a new node
					currentNode = new NodeMMTS(id);
						
					nodes.add(currentNode);
					currentNode.init();
					Simulator.uniqueNodes++;
					nodeUniqueId++;
					
					// generate simulation join event
					currentNode.addEvent(new Join(currentNode, t1, x1, y1));
											
				} else {
					// get node from list of nodes
					currentNode = nodes.get(id-1);
					
					// check if node enters the simulation area (again)
					if(x1==0.0 || x1==Simulator.size || y1==0.0 || y1==Simulator.size) {
						currentNode.addEvent(new Join(currentNode, t1, x1, y1));
						//System.out.println("Node enters simulation area again!");
					}
					
				}
					
				// add node movement
				double velocity = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2))/dt;
				Move move = new Move(currentNode, t1, x1, y1, x2, y2, velocity);
				currentNode.addEvent(move);
				
				// read the next event in the file
				if (!readEvent()) break;
				
			} else {
			
				// stop processing
				break;
			}
				
		}	
			
						
		// let nodes execute their next events
		Iterator<MobileNode> it = nodes.iterator();
		while (it.hasNext()) {
			MobileNode current = it.next();
			current.next();
		}
		
		
	}
	
	
	public void finish() {
		// close traces file
		try {
	        input.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	
		/*
		Iterator<String> it = poiSet.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		*/
	}

}
