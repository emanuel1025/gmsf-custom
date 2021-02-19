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

package output;

import java.awt.Color;
import java.io.*;
import java.util.*;

import simulator.*;
import output.pdf.*;
import event.*;
import model.*;

/**
 * PDFFormatter outputs mobility traces as lines on a page for each node in a PDF file.
 * @author psommer
 *
 */
public class PDFFormatter extends TraceFormatter {

	
	public void finish() {
		
		
		// create a PDFOutput instance
		PDFOutput pdf = new PDFOutput(new File(Simulator.outputDirectory + "/trace.pdf"), Simulator.size);
		
		// sort all events by node identifier and event start time
		Collections.sort(Simulator.events, new EventComparatorByNodeIdByStartTime());
		
		// draw paths of all nodes
		Iterator<Event> it = Simulator.events.iterator();
		// identifier of the previous node
		int previousNodeId = -1;
		
		while (it.hasNext()) {
			
			Event event = it.next();
			
			if (event.node.id!=previousNodeId) {
				// next node
				if (previousNodeId!=-1) pdf.newPage();
				pdf.drawBorder(2, 2);
				pdf.drawText(0, Simulator.size + 20, "Node: " + event.node.id, Color.BLACK, 12f);
				previousNodeId = event.node.id;
			}
			
			if (event.type==Event.MOVE) {
				Move temp = (Move) event;
				Line line = new Line(temp.x, temp.y, temp.moveToX, temp.moveToY);
				pdf.drawLine(line, Color.BLACK, 1f);
			} else if (event.type==Event.JOIN) {
				pdf.drawCircle(event.x, event.y, Color.BLACK, 1f);
			} else if (event.type==Event.LEAVE) {
				pdf.drawCircle(event.x, event.y, Color.BLACK, 1f);
			}
			
		}
		
		// close pdf
		pdf.close();
		
	}
	
}
