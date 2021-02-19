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

package mobility.fixed;

import simulator.*;
import mobility.MobileNode;
import event.*;


/**
 * Node which implements a fixed node without mobility
 * @author psommer
 *
 */
public class NodeFixed extends MobileNode {
	
	/**
	 * Creates a new fixed node without any mobility
	 * @param id unique node identifier
	 */
	public NodeFixed(int id) {
		super(id);
	}
	
	public void init() {
		
		// node starts moving
		
		// rejection sampling of initial position
		
		boolean reject = true;
		
		while (reject) {
			
			double x1 = Simulator.rng.nextDouble();
			double x2 = Simulator.rng.nextDouble();
			double y1 = Simulator.rng.nextDouble();
			double y2 = Simulator.rng.nextDouble();
			
			
			double r = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))/Math.sqrt(2); 
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
				
				// node joins the simulation
				Join join = new Join(this, 0.0, Simulator.size*initX, Simulator.size*initY);
				addEvent(join);
				
				// pause node during the whole simulation period
				addEvent(new Pause(this, 0.0, Simulator.duration,  join.x, join.y));
				
			}
								
		}
						
	
		
			
		
	}
	
	public void prepare() {
		
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
