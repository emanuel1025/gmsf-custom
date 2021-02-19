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
 * Path contains a list of type RoadEdge which define a path between
 * two RoadNode objects.
 * @author psommer
 *
 */
public class Path {

	/** List of type RoadEdge containing all roads belonging to this path */
	LinkedList<RoadEdge> roads = new LinkedList<RoadEdge>();
	
	/** total path length */
	double length = 0;
	
	/**
	 * Constructs a Path
	 * @param path List with roads
	 */
	public Path(List<RoadEdge> path) {
		Iterator<RoadEdge> it = path.iterator();
		while (it.hasNext()) {
			RoadEdge road = it.next();
			roads.add(road);
			length+=road.length;
		}
	}
	
	/**
	 * Polls the next road from this path 
	 * @return Next road on the path
	 */
	public RoadEdge getNextRoad() {
		return roads.poll();
	}
	
	
	/**
	 * Checks if this path has a next road
	 * @return True if path has a next road, false if not
	 */
	public boolean hasNextRoad() {
		return roads.size()>0;
	}
	
	/**
	 * Number of roads on this path
	 * @return number of roads
	 */public int size() {
		return roads.size();
	}
	
	/**
	 * Iterator for this path
	 * @return ListIterator for roads on this path
	 */ 
	public ListIterator<RoadEdge> getPathIterator() {
		return roads.listIterator();
	}
	
}
