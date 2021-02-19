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

package mobility.mmts;

import simulator.*;
import mobility.MobileNode;
import event.*;


public class NodeMMTS extends MobileNode {
	
	public NodeMMTS(int id) {
		super(id);
	}
	
	@Override
	public void init() {}
	
	public void prepare() {}

	@Override
	public boolean next() {
		
		// node leaves the simulation if it is more than 2 seconds paused
		
		if (currentEvent!=null && lastEvent.type!=Event.LEAVE && lastEventEndTime<(Simulator.time + 5)) {
			Move lastMoveEvent = (Move) lastEvent;
			addEvent(new Leave(this, lastEventEndTime, lastMoveEvent.moveToX, lastMoveEvent.moveToY));
			MMTSModel.poiSet.add(String.format("%.0f %.0f %d", lastMoveEvent.moveToX, lastMoveEvent.moveToY, 1));
		}
		
        return super.next();
	}
	
	public void finish() {
		super.finish();
	}

}
