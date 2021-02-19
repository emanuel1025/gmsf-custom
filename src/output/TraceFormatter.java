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


import java.util.Comparator;

import event.Event;
import simulator.Module;
import mobility.MobileNode;

/**
 * TraceFormatter is the abstract base class for all trace formatter classes which
 * generate mobility traces in various ouput formats. 
 * TraceFormatter implements the Module interface.
 * 
 * @author psommer
 *
 */
public abstract class TraceFormatter extends Module {

	/**
	 * Comparator for events based on the start time of the events.
	 * @author psommer
	 *
	 */
	public class EventComparatorByStartTime implements Comparator<Event> {
		public int compare(Event event1, Event event2) {
			return Double.compare(event1.time, event2.time);
		}
	}
	
	/**
	 * Comparator for events based on the end time of the events.
	 * @author psommer
	 *
	 */
	public class EventComparatorByEndTime implements Comparator<Event> {
		public int compare(Event event1, Event event2) {
			return Double.compare(event1.time + event1.duration, event2.time + event2.duration);
		}
	}
	
	/**
	 * Comparator for events. Events are compared first based on the node identifier and then based
	 * on the start time of the event.
	 * @author psommer
	 *
	 */
	public class EventComparatorByNodeIdByStartTime implements Comparator<Event> {
		public int compare(Event event1, Event event2) {
			// compare by node id
			if (event1.node.id==event2.node.id) {
				// compare by event start time
				return Double.compare(event1.time, event2.time);
			} else {
				return event1.node.id - event2.node.id;
			}
		}
	}
	
	
	public TraceFormatter() {
		name = "Output module";
	}
	
	public void init() {
		// nothing to do
	}
	public void next() {
		// nothing to do
	};
	
	public void addNode(double time, MobileNode node) {
		// nothing to do
	};
	public void removeNode(double time, MobileNode node) {
		// nothing to do
	};
	
	public abstract void finish();
	
}
