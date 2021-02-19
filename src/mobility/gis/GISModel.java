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

package mobility.gis;


import java.util.*;

import simulator.*;
import mobility.*;
import model.RoadNode;


/**
 * Mobility model which is based on GIS data to model realistic node movements.
 * @author psommer
 *
 */
public class GISModel extends MobilityModel {

	
	
	// landscape model (GIS data)
	static LandscapeModel landscape = null;
	static boolean enableTrafficLights = false;
	static boolean enableCarFollowing = false;

	static boolean warmupPhase = true;
	
	public void init() {
		
		
		landscape = new LandscapeModel();
		LandscapeModelFactory.addRoads(Simulator.inputDirectory + "/roads.dat", landscape);
		//LandscapeModelFactory.addPointOfInterests(Simulator.inputDirectory + "/points.dat", landscape);
		LandscapeModelFactory.addPointOfInterests(landscape);
		
		if (Simulator.parameters.containsKey("CAR_FOLLOWING")) {
			try {
				 enableCarFollowing = (1==Integer.valueOf(Simulator.parameters.getProperty("CAR_FOLLOWING")));
			} catch (Exception e) {
				System.err.println("Error parsing parameter CAR_FOLLOWING: " + e.getMessage());
			}
		}
		
		if (Simulator.parameters.containsKey("TRAFFIC_LIGHTS")) {
			try {
				 enableTrafficLights = (1==Integer.valueOf(Simulator.parameters.getProperty("TRAFFIC_LIGHTS")));
			} catch (Exception e) {
				System.err.println("Error parsing parameter TRAFFIC_LIGHTS: " + e.getMessage());
			}
		}
		
		
		// initialize traffic lights
		if (enableTrafficLights) {
			Iterator<RoadNode> it = landscape.roadNetwork.getNodes().iterator();
			while (it.hasNext()) {
				RoadNode intersection = it.next();
				intersection.init();
			}
		}
		
		
		
		if (Simulator.parameters.containsKey("NODES")) {
			int nodesNumber = 0;
			try {
				nodesNumber = Integer.valueOf(Simulator.parameters.getProperty("NODES"));
			} catch (Exception e) {
				System.err.println("Error parsing parameter NODES: " + e.getMessage());
			}
			
			// intialize nodes
			System.out.println("Initialization of GIS model");
			for (int i=1; i<=nodesNumber; i++) {
				NodeGIS node = new NodeGIS(i);
				nodes.add(node);
				node.warmup();
				Simulator.uniqueNodes++;
			}
			System.out.println("GIS model initialized");
		} else {
			System.err.println("Number of nodes not specified. Use the NODES parameter to specify the node number.");
			return;
		}
		
		
		System.out.println("warm-uping");

		// warm-up phase (5000 seconds)
		for (int i=1; i<5000; i++) {
			next();
		}
		System.out.println("warm-upped");

		warmupPhase = false;
		
		// nodes
		Iterator<MobileNode> it = nodes.iterator(); 
		
		while (it.hasNext()) {
			MobileNode node = it.next();
			node.init();
		}
		
		
	}

	public void next() {

		
		
		if (enableTrafficLights) {
			// traffic lights
			Iterator<RoadNode> it = landscape.roadNetwork.getNodes().iterator();
			while (it.hasNext()) {
				RoadNode intersection = it.next();
				intersection.next();
			}
		}
		
		
		// nodes
		Iterator<MobileNode> it2 = nodes.iterator(); 
		
		while (it2.hasNext()) {
			MobileNode node = it2.next();
			node.prepare();
		}
		
		Iterator<MobileNode> it3 = nodes.iterator(); 
		
		while (it3.hasNext()) {
			MobileNode node = it3.next();
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
