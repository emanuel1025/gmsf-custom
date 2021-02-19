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

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import event.Event;
import event.Move;
import event.Pause;
import simulator.*;

/**
 * XMLFormatter generates a XML file with mobility traces.
 * @author psommer
 *
 */
public class XMLFormatter extends TraceFormatter {

	private final String fileName = "/trace-" + System.currentTimeMillis() + ".xml";
	private final Logger logger =
			Logger.getLogger(this.getClass().getSimpleName());

	public void finish() {

		// sort all events by node identifier and start time
		Collections.sort(Simulator.events, new EventComparatorByNodeIdByStartTime());

		try {

//			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(Simulator.outputDirectory + fileName)));
			LittleEndianDataOutputStream dataOutput = new LittleEndianDataOutputStream(new FileOutputStream(Simulator.outputDirectory + fileName));

			//writing hyperparameters
//			dataOutput.writeChars(Integer.toBinaryString(Simulator.uniqueNodes));
//			dataOutput.writeChars(Integer.toBinaryString((int) Simulator.duration));
			dataOutput.writeInt(Simulator.uniqueNodes);
			dataOutput.writeInt((int) Simulator.duration);

			//writing mbr
//			dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(0.0)));
//			dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(0.0)));
//			dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(Simulator.size)));
//			dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(Simulator.size)));
			dataOutput.writeDouble(0.0);
			dataOutput.writeDouble(0.0);
			dataOutput.writeDouble(Simulator.size);
			dataOutput.writeDouble(Simulator.size);

//			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//			writer.write("<traces xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"traces.xsd\">\n");
//
			// output paths of all nodes
			Iterator<Event> it = Simulator.events.iterator();
			int lastNodeId = -1;

			ArrayList<Double> paths = new ArrayList<>();

			while (it.hasNext()) {

				Event event = it.next();

				if (event.node.id!=lastNodeId) {

//					if (lastNodeId!=-1) {
//						writer.write("    </events>\n");
//						writer.write("  </node>\n");
//					}
//
//					// next node
//					writer.write("  <node id=\"" + event.node.id + "\">\n");
//					writer.write("    <events>\n");

					lastNodeId = event.node.id;
				}

				if (event.type==Event.MOVE) {
					Move temp = (Move) event;

//					writer.write("      <move>\n");
//					writer.write("        <start>\n");
//					writer.write("          <time>" + String.format("%.2f",temp.time) + "</time>\n");
//					writer.write("          <x>" + String.format("%.2f",temp.x) + "</x>\n");
//					writer.write("          <y>" + String.format("%.2f",temp.y) + "</y>\n");
//					writer.write("        </start>\n");
//					writer.write("        <stop>\n");
//					writer.write("        g  <time>" + String.format("%.2f", temp.time + temp.duration) + "</time>\n");
//					writer.write("          <x>" + String.format("%.2f", temp.moveToX) + "</x>\n");
//					writer.write("          <y>" + String.format("%.2f",temp.moveToY) + "</y>\n");
//					writer.write("        </stop>\n");
//					writer.write("      </move>\n");
//					writer.write(String.format("%.2f %.2f", temp.moveToX, temp.moveToY)+ "\n");
//					dataOutput.writeDouble(temp.moveToX);
//					dataOutput.writeDouble(temp.moveToY);
//					dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(temp.moveToX)));
//
//					dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(temp.moveToY)));
					paths.add(temp.moveToX);
					paths.add(temp.moveToY);
//					System.out.println(temp.moveToX + " " + temp.moveToY);
//					System.out.println(temp.node.id);

				} else if (event.type==Event.PAUSE) {
					Pause temp = (Pause) event;

//					writer.write("      <pause>\n");
//					writer.write("        <time>" + String.format("%.2f", temp.time) + "</time>\n");
//					writer.write("        <x>" + String.format("%.2f", temp.x) + "</x>\n");
//					writer.write("        <y>" + String.format("%.2f", temp.y) + "</y>\n");
//					writer.write("        <duration>" + String.format("%.2f", temp.duration)  + "</duration>\n");
//					writer.write("      </pause>\n");
//					writer.write(String.format("%.2f %.2f", temp.x, temp.y)+ "\n");
//					dataOutput.writeDouble(temp.x);
//					dataOutput.writeDouble(temp.y);

					paths.add(temp.x);
					paths.add(temp.y);
//					dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(temp.x)));
//					dataOutput.writeChars(Long.toBinaryString(Double.doubleToRawLongBits(temp.y)));
//					System.out.println(temp.x + " " + temp.y);
//					System.out.println(temp.node.id);

				}  else if (event.type==Event.JOIN) {

//					writer.write("      <join>\n");
//					writer.write("        <time>" + String.format("%.2f", event.time) + "</time>\n");
//					writer.write("        <x>" + String.format("%.2f", event.x) + "</x>\n");
//					writer.write("        <y>" + String.format("%.2f", event.y) + "</y>\n");
//					writer.write("      </join>\n");
//					writer.write(String.format("%.2f %.2f JOIN %s", event.x, event.y, event.node.id)+ "\n");

				}  else if (event.type==Event.LEAVE) {

//					writer.write("      <leave>\n");
//					writer.write("        <time>" + String.format("%.2f", event.time) + "</time>\n");
//					writer.write("        <x>" + String.format("%.2f", event.x) + "</x>\n");
//					writer.write("        <y>" + String.format("%.2f", event.y) + "</y>\n");
//					writer.write("      </leave>\n");
//					writer.write(String.format("%.2f %.2f", event.x, event.y, event.node.id)+ "\n");
				}

			}

//			if (lastNodeId!=-1) {
//				writer.write("    </events>\n");
//				writer.write("  </node>\n");
//			}
//
//			writer.write("</traces>\n");
			logger.info(String.format("Number of nodes: %d", Simulator.uniqueNodes));
			logger.info(String.format("Duration of simulation: %d", (int) Simulator.duration));

			int nodes = Simulator.uniqueNodes;
			int duration = (int) Simulator.duration;

			for (int i = 0; i < duration; i++) {
				for (int j = 0; j < nodes; j++) {
//					System.out.println(paths.get((2 * j * duration) + (2*i)));
//					System.out.println(paths.get((2 * j * duration) + (2*i) + 1));
					dataOutput.writeDouble(paths.get((2 * j * duration) + (2*i)));
					dataOutput.writeDouble(paths.get((2 * j * duration) + (2*i) + 1));
				}
			}

			//Dejun's method
			dataOutput.close();
			logger.info(String.format("dumped to %s", fileName));

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		try {
			LittleEndianDataInputStream dataInput = new LittleEndianDataInputStream(new FileInputStream(Simulator.outputDirectory + fileName));
			// Count the total bytes
			// form the input stream
//			int count = dataInput.available();

			int x = dataInput.readInt();
			System.out.println(x);
			int y = dataInput.readInt();
			System.out.println(y);




//			for (byte by : b) {
//				// Print the character
//				System.out.print((char)by);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	void dumpTo(String path) {
//		ofstream wf (path, ios::out|ios::binary|ios::trunc);
//		wf.write((char *)&config.num_objects, sizeof(config.num_objects));
//		wf.write((char *)&config.duration, sizeof(config.duration));
//		wf.write((char *)&mbr, sizeof(mbr));
//		size_t num_points = config.duration*config.num_objects;
//		wf.write((char *)trace, sizeof (Point) *num_points);
//		wf.close();
//		logger.info("dumped to {}", path);
	}


}
