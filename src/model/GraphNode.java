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
 * GraphNode models a node in a neighborhood graph.
 * @author psommer
 *
 */
public class GraphNode extends Position {

	/** list of edges containing this node */
	List<GraphEdge> edges = new LinkedList<GraphEdge>();
	
	/**
	 * Adds a new edge to this node's list of edges
	 * @param edge Edge which is added to this node
	 */
	public void add(GraphEdge edge) {
		edges.add(edge);
	}
	
	/**
	 * Removes the given edge from this node's list of edges
	 * @param edge
	 */
	public void remove(GraphEdge edge) {
		edges.remove(edge);
	}
	
	/**
	 * Returns the node degree (number of edges)
	 * @return node degree
	 */
	public int getDegree() {
		return edges.size();
	}
	
	/**
	 * Returns the edge connecting this node with the given GraphNode
	 * @param node GraphNode which is related to the edge
	 * @return Edge between this node and the given node or null if no such edge exists
	 */
	public GraphEdge getEdge(GraphNode node) {
		GraphEdge edge = null;
		// iterator over all edges
		Iterator<GraphEdge> it = edges.iterator();
		while (it.hasNext()) {
			GraphEdge temp = it.next();
			if ((temp.nodeA==this && temp.nodeB==node) || (temp.nodeB==this && temp.nodeA==node)) {
				edge = temp;
				break;
			}
		}
		return edge;
	}
	
	
}
