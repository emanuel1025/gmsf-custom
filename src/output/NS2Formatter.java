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
import java.util.Collections;
import java.util.Iterator;

import simulator.*;
import event.*;


/**
 * NS2Formatter generates mobility traces for the NS-2 simulator.
 * @author psommer
 *
 */
public class NS2Formatter extends TraceFormatter{

	/** maximum node identifier value */
	int maxNodeId = 0;
	
	
	public void finish() {
		
		/** buffered output writer */
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(new File(Simulator.outputDirectory + "/trace.mov")));
	    } catch (Exception e) {
	    	System.err.println(e.getLocalizedMessage());
	    }
		
	    // sort events by start time
		Collections.sort(Simulator.events, new EventComparatorByStartTime());
	    
	    // output node initialization section (t=0)
	 	Iterator<Event> it = Simulator.events.iterator();
		while (it.hasNext()) {
			
			Event event = it.next();
			
			if (event.time>0) break;
			
			if (event.type==Event.JOIN) {
				try {
					writer.write("$node_(" + event.node.getId() + ") set X_ " + event.x + "\n$node_(" + event.node.getId() + ") set Y_ " + event.y + "\n$node_(" + event.node.getId() + ") set Z_ 0.0\n");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			
		}
	    
		
		
		// iteration over all events
		it = Simulator.events.iterator();
		while (it.hasNext()) {
			
			Event event = it.next();
			
			if (event.type==Event.MOVE) {
				Move move = (Move) event;
				try {
					writer.write("$ns_ at " + move.time + " \"$node_(" + (move.node.getId()-1) + ") setdest  " + move.moveToX + " " + move.moveToY + " " + move.velocity + "\"\n");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			} else if (event.type==Event.JOIN) {
				try {
					writer.write("$ns_ at " + event.time + " \"$node_(" + (event.node.getId()-1) + ") on\"\n");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			} else if (event.type==Event.LEAVE) {
				try {
					writer.write("$ns_ at " + event.time + " \"$node_(" + (event.node.getId()-1) + ") off\"\n");
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

	
