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

/**
 * GraphEdge models an undirected edge between two nodes in a neighborhood graph.
 * @author psommer
 *
 */
public class GraphEdge {

	/** node A */
	GraphNode nodeA = null;
	/** node B */
	GraphNode nodeB = null;

	/**
	 * Constructs a new (undirected) GraphEdge between the two given GraphNodes
	 * @param nodeA first graph node
	 * @param nodeB second graph node
	 */
	public GraphEdge(GraphNode nodeA, GraphNode nodeB) {
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		// add the edge to nodeA
		nodeA.add(this);
		// add the edge to nodeB
		nodeB.add(this);
	}
	
	/**
	 * Gets the first node of this (undirected) edge
	 * @return NodeA
	 */
	public GraphNode getNodeA() {
		return nodeA;
	}
	
	/**
	 * Gets the second node of this (undirected) edge
	 * @return NodeB
	 */
	public GraphNode getNodeB() {
		return nodeB;
	}
	
}
