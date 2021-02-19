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

package model;


import java.util.*;

/**
 * RoadNetwork models a network of roads and intersections.
 * The road network is represented as a directed graph.
 * Intersections are nodes and roads are directed edges of the graph.
 * Dijkstra's shortest path algorithm is used to search shortest paths between two intersections in the network.
 * @author psommer
 *
 */
public class RoadNetwork  {

	/** unique identifier for roads and intersections */
	static int uniqueId = 0;
	
	/** compares two nodes used by the iteration by comparing Dijkstra costs **/
	private Comparator<RoadNode> comparator = new Comparator<RoadNode>() {
		public int compare(RoadNode node1, RoadNode node2) {
	     	return Double.compare(node1.dijkstraCost, node2.dijkstraCost);
	    }
	};
	
	/** list of intersections */
	List<RoadNode> nodes = null;
	/** list of roads */
	List<RoadEdge> edges = null;
	
	/** priority queue to manage active nodes (Dijkstra's algorithm)**/
	private PriorityQueue<RoadNode> queue = null;
	
	/**
	 * Creates a RoadNetwork out of the specified intersections and roads
	 * @param nodes Intersections of the road network
	 * @param edges Roads of the road network
	 */
	public RoadNetwork(Collection<RoadNode> nodes, Collection <RoadEdge> edges) {
		this.nodes = new ArrayList<RoadNode>(nodes);
		this.edges = new ArrayList<RoadEdge>(edges);
		// initialize priority queue for the dijkstra's algorithm
		queue = new PriorityQueue<RoadNode>(nodes.size(), comparator);
	}
	
	/**
	 * Sets the intersections (nodes) of the road network
	 * @param nodes Intersections of the road network
	 */
	public void setNodes(Collection<RoadNode> nodes) {
		this.nodes = new ArrayList<RoadNode>(nodes);
		queue = new PriorityQueue<RoadNode>(nodes.size(), comparator);
	}
	
	/**
	 * Sets the roads (edges) of the road network
	 * @param edges Roads of the road network
	 */
	public void setEdges(Collection<RoadEdge> edges) {
		this.edges = new ArrayList<RoadEdge>(edges);
	}
	
	/**
	 * Returns a list of Intersections (nodes)
	 * @return List of intersections
	 */
	public List<RoadNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Returns a list of Roads (edges)
	 * @return List of edges
	 */
	public List<RoadEdge> getEdges() {
		return edges;
	}
	
	/**
	 * Dijkstra's shortest path algorithm.
	 * Calculates the shortest path between source and destination.
	 * @param source Start intersection of the path
	 * @param destination End intersection of the path
	 * @return Shortest path between source and destination or null if no such path exists
	 */
	public Path getPath(RoadNode source, RoadNode destination) {
		   
		  // initialize priority queue
		  queue.clear();
		  
		  Iterator<RoadNode> it = nodes.iterator();
		  while (it.hasNext()) {
			  RoadNode node = it.next();
			  if (node==source) node.dijkstraCost = 0;
			  else node.dijkstraCost = Double.POSITIVE_INFINITY;
	          node.dijkstraVisited = false;
	          node.dijkstraEdgeParent = null;
	          queue.add(node);
	      }
		  
		  
		  RoadNode current = null;
		  
		  while (true) {
			 
			  // poll first node from queue
			  current = queue.poll();
			  
			  if (current==null || current==destination || current.dijkstraCost == Double.POSITIVE_INFINITY) break;
			  
			  current.dijkstraVisited = true;
			   
			  for (Iterator<RoadEdge> itr = current.getOutEdges().iterator(); itr.hasNext();) {
			      
				  RoadEdge outgoingEdge = itr.next();
				  RoadNode relatedNode = outgoingEdge.getEndNode();
				  
			      //if cost less than current cost of related node, update 
			      if (!relatedNode.dijkstraVisited && relatedNode.dijkstraCost > (current.dijkstraCost + outgoingEdge.weight)) {
			    	  
			    	  relatedNode.dijkstraCost = current.dijkstraCost + outgoingEdge.weight;
			    	  relatedNode.dijkstraEdgeParent = outgoingEdge;
			          // update cost in the priority queue
			    	  queue.remove(relatedNode);
			    	  queue.add(relatedNode);  
			      }  
			  }
			 
		  }
		  
		  //finished
		  if (current==null || current.dijkstraCost == Double.POSITIVE_INFINITY) {
			  // no path found
			  //System.out.println("no path found");
		 } else if (current==destination) {
			  // path found
			  LinkedList<RoadEdge> path = new LinkedList<RoadEdge>();
			  while (current!=source) {
				  path.addFirst(current.dijkstraEdgeParent);
				  current = current.dijkstraEdgeParent.getStartNode();
			  }
			  
			  return new Path(path);
		  }
		  return null;
	}
	
}
