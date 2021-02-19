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


import java.util.*;
import mobility.*;
import model.*;
import simulator.*;


/**
 * Implementation of the Manhattan mobility model.
 * The model is initialized using a special steady-state distribution.
 * @author psommer
 *
 */
public class ManhattanModel extends MobilityModel {


	/** road graph */
	static RoadNetwork roadNetwork = null;
	/** number of grid segments */
	int segments = 15;
	/** graph nodes */
	ArrayList<RoadNode> graphNodes = new ArrayList<RoadNode>();
	/** graph edges */
	ArrayList<RoadEdge> graphEdges = new ArrayList<RoadEdge>();
	/** indicates if the model is in the warmup phase */
	static boolean warmupPhase = true;
	/** maximum acceleration/deceleration */
	static double acceleration = 0.1;
	/** decelerate if distance to front vehicle is below this value */
	static double securityDistance = 25;
	/** minimum speed */
	static double speedMin = 10;
	/** maximum speed */
	static double speedMax = 14;
	

	/**
	 * Initializes the mobility model with the parameters defined by the Simulator.
	 */
	public void init() {
		
		// create the road network for the Manhattan mobility model
		
		
		// calculate the block length
		int blockLength = 0;
		
		if (Simulator.parameters.containsKey("BLOCKS")) {
			try {
				int blocks = Integer.valueOf(Simulator.parameters.getProperty("BLOCKS"));
				blockLength = (int)Math.floor(Simulator.size/blocks);
				
			} catch (Exception e) {
				System.err.println("Error parsing parameter BLOCKS: " + e.getMessage());
			}
		} else {
			System.err.println("Number of blocks for Manhattan model not specified. Use the BLOCKS parameter to specify the number of blocks in one dimension.");
			System.exit(0);
			return;
		}

		
		// create nodes
		for (int i=0; i<=segments; i++) {
			for (int k=0; k<=segments; k++) {
				RoadNode node = new RoadNode(k*blockLength,i*blockLength);
				graphNodes.add(node);
			}
		}
		
		// create horizontal edges

		for (int i=1; i<segments; i++) {
			for (int k=0; k<segments; k++) {
					graphEdges.add(new RoadEdge(graphNodes.get(i*(segments+1) + k), graphNodes.get(i*(segments+1) + k+1)));
					graphEdges.add(new RoadEdge(graphNodes.get(i*(segments+1) + k+1),graphNodes.get(i*(segments+1) + k)));
			}
		}
		
		// create vertical edges
		for (int i=0; i<segments; i++) {
			for (int k=1; k<segments; k++) {
				graphEdges.add(new RoadEdge(graphNodes.get(i*(segments+1) + k), graphNodes.get((i+1)*(segments+1) + k)));
				graphEdges.add(new RoadEdge(graphNodes.get((i+1)*(segments+1) + k), graphNodes.get(i*(segments+1) + k)));
			}
		}
		
		
		// remove nodes with no outgoing edges
		Iterator<RoadNode> it = graphNodes.iterator();
		while (it.hasNext()) {
			RoadNode node = it.next();
			if (node.getOutEdges().size()==0) {
				it.remove();
			}
		}
		
		// construct the road graph
		roadNetwork = new RoadNetwork(graphNodes, graphEdges);
		
		System.out.println("Nodes: " + graphNodes.size() + ", Edges: " + graphEdges.size());
		
		
		if (Simulator.parameters.containsKey("NODES")) {
			int nodesNumber = 0;
			try {
				nodesNumber = Integer.valueOf(Simulator.parameters.getProperty("NODES"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter NODES: " + e.getMessage());
			}
			
			// intialize nodes
			System.out.println("Initialization of Manhattan model");
			for (int i=1; i<=nodesNumber; i++) {
				NodeManhattan node = new NodeManhattan(i);
				nodes.add(node);
				node.warmup();
				Simulator.uniqueNodes++;
			}
			
		} else {
			System.err.println("Number of nodes not specified. Use the NODES parameter to specify the node number.");
			return;
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
		
		// warm-up phase
		
		for (int i=1; i<5000; i++) {
			next();
		}
		
		warmupPhase = false;
		
		// nodes
		Iterator<MobileNode> nodeIt = nodes.iterator(); 
		
		while (nodeIt.hasNext()) {
			MobileNode node = nodeIt.next();
			node.init();
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
		}
		
		Iterator<MobileNode> it2 = nodes.iterator();
		while (it2.hasNext()) {
			MobileNode node = it2.next();
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
