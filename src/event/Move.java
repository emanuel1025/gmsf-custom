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

package event;

import mobility.MobileNode;

/**
 * Implementation of the MOVE event type.
 * A MOVE event is generated when a node moves to a new position
 * @author psommer
 *
 */

public class Move extends Event {

	/** x-coordinate of the end position of the movement */
	public double moveToX = 0;
	/** y-coordinate of the end position of the movement */
	public double moveToY = 0;
	/** velocity of the movement */
	public double velocity = 0;
	/** direction of the movement (rad) */
	public double direction = 0;
	/** distance of between start and end point */
	public double distance = 0;
	
	/**
	 * Constructs a MOVE event
	 * @param node Node related to this event
	 * @param time Event start time
	 * @param x1 X-coordinate of the start position of the movement
	 * @param y1 Y-coordinate of the start position of the movement
	 * @param x2 X-coordinate of the end position of the movement
	 * @param y2 Y-coordinate of the end position of the movement
	 */
	public Move(MobileNode node, double time, double x1, double y1, double x2, double y2, double velocity) {
		
		this.node = node;
		this.time = time;
		x = x1;
		y = y1;
		moveToX = x2;
		moveToY = y2;
		this.velocity = velocity;
		direction = Math.atan2(moveToY-y, moveToX-x);
		distance = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
		duration = distance/velocity;
		type = Event.MOVE;
	}
	
	/**
	 * Constructs a MOVE event
	 * @param node Node related to this event
	 * @param time Event start time
	 * @param x1 X-coordinate of the start position of the movement
	 * @param y1 Y-coordinate of the start position of the movement
	 * @param x2 X-coordinate of the end position of the movement
	 * @param y2 Y-coordinate of the end position of the movement
	 * @param duration Movement duration
	 */
	public Move(MobileNode node, double time, double x1, double y1, double x2, double y2, double velocity, double duration) {
		
		this.node = node;
		this.time = time;
		x = x1;
		y = y1;
		moveToX = x2;
		moveToY = y2;
		this.velocity = velocity;
		direction = Math.atan2(moveToY-y, moveToX-x);
		distance = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
		this.duration = duration;
		type = Event.MOVE;
	}
	
		
	public String toString() {
		return node.id + " time=" + time + ", duration=" + duration + " move from (" + x + "," + y + ") to (" + moveToX + "," + moveToY + ")";
	}
	
}
