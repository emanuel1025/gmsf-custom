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
import mobility.*;
import probability.*;
import java.util.*;



/**
 * Implementation of the Random waypoint mobility model.
 * The model is initialized using a special steady-state distribution.
 * @author psommer
 *
 */
public final class RandomWaypointModel extends MobilityModel {

	/** distribution of the node velocity */
	static Distribution velocityDistribution = null;
	/** distribution of the pause time */
	static Distribution waitTimeDistribution = null;
	
	/** probability that node starts being paused */
	static double probabilityPause = 0;
	/** maximum (normalized) distance between two nodes in the simulation */
	static double maxDistanceNormalized = Math.sqrt(2);
	
	
	/**
	 * Initializes the random waypoint mobility model with the parameters defined by the simulation.
	 */
	public void init() {
		
		// set speed and pause parameters
		double speedMin = 0, speedMax = 0, pauseMin = 0, pauseMax = 0;
		
		
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
		
		if (Simulator.parameters.containsKey("PAUSE_MIN")) {
			try {
				 pauseMin = Double.valueOf(Simulator.parameters.getProperty("PAUSE_MIN"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter PAUSE_MIN: " + e.getMessage());
			}
		}
		
		if (Simulator.parameters.containsKey("PAUSE_MAX")) {
			try {
				 pauseMax = Double.valueOf(Simulator.parameters.getProperty("PAUSE_MAX"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter PAUSE_MAX: " + e.getMessage());
			}
		}
			
		velocityDistribution = new UniformDistribution(speedMin, speedMax, Simulator.seed);
		waitTimeDistribution = new UniformDistribution(pauseMin, pauseMax, Simulator.seed);
		
		
		// steady-state distribution initialization
		int numberOfBin = 10000;
		double delta = (velocityDistribution.getMax()-velocityDistribution.getMin())/numberOfBin;
		
		double meanInverseVelocity = 0;
		double v = velocityDistribution.getMin();
		for (int i=0;i<numberOfBin;i++) {
			meanInverseVelocity += 1.0/v*velocityDistribution.getPDF(v)*delta; 
			v+=delta;
		}
		
		//System.out.println("delta: " + delta);
		//System.out.println("PDF(mean(v)): " + model.velocityDistribution.getPDF(model.velocityDistribution.getMean()));
		//System.out.println("Mean of 1/v: " + meanInverseVelocity);
		
		probabilityPause = waitTimeDistribution.getMean()/(waitTimeDistribution.getMean() + 0.521405*Simulator.size*meanInverseVelocity);
		
		//System.out.println("Probability of node being paused: " + probabilityPause);
		
		
		if (Simulator.parameters.containsKey("NODES")) {
			int nodesNumber = 0;
			try {
				nodesNumber = Integer.valueOf(Simulator.parameters.getProperty("NODES"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter NODES: " + e.getMessage());
			}
			
			// intialize nodes
			System.out.println("Initialization of Random Waypoint model");
			Simulator.uniqueNodes = 0;
			for (int i=1; i<=nodesNumber; i++) {
				NodeRWP node = new NodeRWP(i);
				node.init();
				nodes.add(node);
				Simulator.uniqueNodes++;
			}
			
			
		} else {
			System.err.println("Number of nodes not specified. Use the NODES parameter to specify the node number.");
			return;
		}
	}

	/**
	 * Updates the model for the next sample point
	 */
	public void next() {
		// prepare and update all nodes
		Iterator<MobileNode> it = nodes.iterator(); 
		while (it.hasNext()) {
			MobileNode node = it.next();
			node.prepare();
			node.next();
		}
	}

	
	/**
	 * Called after the simulation to clean-up
	 */
	public void finish() {
		Iterator<MobileNode> it = nodes.iterator(); 
		while (it.hasNext()) {
			MobileNode node = it.next();
			node.finish();
		}
	}
}
