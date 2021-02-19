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

import java.io.*;
import java.util.*;

import simulator.*;
import event.*;


/**
 * NAMFormatter outputs mobility traces in the NAM (Network Animator) format.
 * @author psommer
 *
 */
public final class NAMFormatter extends TraceFormatter {

	/**
	 * NodeIdComparator is used to compare nodes based on the node id.
	 * @author psommer
	 *
	 */
	public class NodeIdComparator implements Comparator<Integer> {
		/**
		 * Compares two Integers.
		 * Returns:<br>
		 * 0 if both are equal,<br>
		 * 1 if the first number is bigger,<br>
		 *  -1 if the second number is bigger.
		 */
		public int compare(Integer id1, Integer id2) {
			if (id1==id2) return 0;
			else if (id1>id2) return 1;
			else return -1;
		}
	}
	
	
	public void finish() {
		
		/** BufferedWriter for output */
		BufferedWriter writer = null;
		
		try {
			// initialize output writer
			writer = new BufferedWriter(new FileWriter(new File(Simulator.outputDirectory + "/trace.nam")));
	    } catch (Exception e) {
	    	System.err.println(e.getLocalizedMessage());
	    }
		
	    /** node id set */
	    HashSet<Integer> nodes = new HashSet<Integer>();
		
	    // sort events by start time
	    Collections.sort(Simulator.events, new EventComparatorByStartTime());
	    
	    // iteration over all events
	    Iterator<Event> it = Simulator.events.iterator();
	    while (it.hasNext()) {
	    	Event event = it.next();
	    	// add new node id to the set
	    	if (!nodes.contains(event.node.id)) nodes.add(event.node.id);
	    }
		
	    // build sorted list with node identifiers
	    List<Integer> nodeIdList = new ArrayList<Integer>(nodes);
	    Collections.sort(nodeIdList, new NodeIdComparator());
	    
	    // output node initialization commands
	    Iterator<Integer> it2 = nodeIdList.iterator();
	    while (it2.hasNext()) {
	    	int nodeId = it2.next();
	    	
	    	try {
	    		writer.write("n -t * -s " + nodeId + " -x 0.000000 -y 0.000000 -Z 0 -z 20  -v circle -c black\n");
	    	} catch (Exception e) {
	    		System.err.println(e.getMessage());
	    	}
	    }

	    // output network settings
	    try {
			writer.write("V -t * -v 1.0a5 -a 0\nW -t * -x " + Simulator.size + " -y " + Simulator.size +"\nA -t * -n 1 -p 0 -o 0xffffffff -c 31 -a 1\nA -t * -h 1 -m 2147483647 -s 0\n");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		// output node mobility traces
		
		it = Simulator.events.iterator();
		while (it.hasNext()) {
			
			Event temp = it.next();
			if (temp.type==Event.MOVE) {
				Move move = (Move) temp;
				try {
					writer.write("n -t " + move.time + " -s " + move.node.getId() + " -x " + move.x + " -y " + move.y + " -U " + (move.moveToX-move.x)/move.duration + " -V " + (move.moveToY-move.y)/move.duration + " -T " + move.duration + "\n");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
		
		// close output file
		try {
			writer.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
}

	
