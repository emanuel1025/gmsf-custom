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
 * Graph models a graph representing neighborhood relations between mobile nodes.
 * An edge connecting two nodes indicates a radio communication link between these two nodes.
 * @author psommer
 *
 */
public class Graph  {

	/** nodes of this graph */
	List<GraphNode> nodes = new ArrayList<GraphNode>();
	/** edges of this graph */
	List<GraphEdge> edges = new ArrayList<GraphEdge>();
	
	/**
	 * Constructs an empty graph
	 */
	public Graph() {
	}
	
	/**
	 * Sets the nodes for this graph
	 * @param nodes Collection of nodes
	 */
	public void setNodes(Collection<GraphNode> nodes) {
		this.nodes = new ArrayList<GraphNode>(nodes);
	}
	
	/**
	 * Sets the edge for this graph 
	 * @param edges Collection of edges
	 */
	public void setEdges(Collection<GraphEdge> edges) {
		this.edges = new ArrayList<GraphEdge>(edges);
	}
	
	/**
	 * Gets the nodes of this graph
	 * @return List of nodes
	 */
	public List<GraphNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Gets the edges of this graph
	 * @return List of edges
	 */
	public List<GraphEdge> getEdges() {
		return edges;
	}
	
}
