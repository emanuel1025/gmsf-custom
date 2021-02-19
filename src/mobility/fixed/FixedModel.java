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


import java.util.*;

import simulator.*;
import mobility.*;

/**
 * Mobility model implementation with nodes at fixed positions (no mobility).
 * @author psommer
 *
 */
public class FixedModel extends MobilityModel {

	
	
	/** minimum speed parameter (used for steady-state initialization) */
	public static double speedMin = 0;
	/** maximum speed parameter (used for steady-state initialization) */
	public static double speedMax = 0;
	
	public void init() {
		
		
		if (Simulator.parameters.containsKey("NODES")) {
			int nodesNumber = 0;
			try {
				nodesNumber = Integer.valueOf(Simulator.parameters.getProperty("NODES"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter NODES: " + e.getMessage());
			}
			
			
			
			if (Simulator.parameters.containsKey("SPEED_MIN")) {
				try {
					 speedMin = Double.valueOf(Simulator.parameters.getProperty("SPEED_MIN"));
				} catch (Exception e) {
					System.err.println("Error parsing parameter SPEED_MIN: " + e.getMessage());
				}
			}
			
			if (Simulator.parameters.containsKey("SPEED_MAX")) {
				try {
					 speedMax = Double.valueOf(Simulator.parameters.getProperty("SPEED_MAX"));
				} catch (Exception e) {
					System.err.println("Error parsing parameter SPEED_MAX: " + e.getMessage());
				}
			}
			
			// intialize nodes
			System.out.println("Initialization of dummy model (no mobility)");
			for (int i=1; i<=nodesNumber; i++) {
				MobileNode node = new NodeFixed(i);
				nodes.add(node);
				node.init();
				Simulator.uniqueNodes++;
			}
			
		} else {
			System.err.println("Number of nodes not specified. Use the NODES parameter to specify the node number.");
			return;
		}
		
		
	}

	public void next() {
		
		Iterator<MobileNode> it = nodes.iterator(); 
		
		while (it.hasNext()) {
			MobileNode node = it.next();
			node.next();
		}
		
	}

	
	public void finish() {
		Iterator<MobileNode> it = nodes.iterator(); 
		while (it.hasNext()) {
			MobileNode node = it.next();
			node.finish();
		}
		
	}

}
