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
 *
 * @author psommer
 */
public class XMLFormatter extends TraceFormatter {

    private final String fileName = "/trace-" + Thread.currentThread().getName() + ".xml";

    private final Logger logger =
            Logger.getLogger(this.getClass().getSimpleName());
    Simulator curSimulation;

    public XMLFormatter(Simulator simulator) {
        System.out.println("Init XMLFORMATTER");
        this.curSimulation = simulator;
    }

    public void finish() {

        // sort all events by node identifier and start time
        Collections.sort(curSimulation.events, new EventComparatorByNodeIdByStartTime());

        try {
            LittleEndianDataOutputStream dataOutput = new LittleEndianDataOutputStream(new FileOutputStream(curSimulation.outputDirectory + fileName));

            //writing hyperparameters
            int nodes = curSimulation.uniqueNodes;
            int duration = (int) curSimulation.duration;
            dataOutput.writeInt(nodes);
            dataOutput.writeInt(duration);

            //writing mbr
            dataOutput.writeDouble(0.0);
            dataOutput.writeDouble(0.0);
            dataOutput.writeDouble(curSimulation.size);
            dataOutput.writeDouble(curSimulation.size);

            // output paths of all nodes
            ArrayList<Double> paths = new ArrayList<>();
            System.out.println("Event Size " + curSimulation.events.size());
            int numFound = 0;
            for (int i = 0; i < duration; i++) {
//                System.out.println("Current Iteration " + i);
//                System.out.println("Current numFound " + numFound);
                int j = 0;
                int lookfor = i;
                //passing through all events
                for (Event event : curSimulation.events) {
                    if (j == lookfor) {
                        numFound += 1;
                        if (event.type == Event.MOVE) {
                            Move temp = (Move) event;
//                            System.out.println(temp.moveToX);
                            dataOutput.writeDouble(temp.moveToX);
                            dataOutput.writeDouble(temp.moveToY);
                        }
//                        else if (event.type == Event.PAUSE) {
//                            Pause temp = (Pause) event;
//                            dataOutput.writeDouble(temp.x);
//                            dataOutput.writeDouble(temp.y);
//                        }
                        lookfor += duration;
//                        System.out.println("Looking for " + lookfor);
                    }
                    j++;
                }
                //modified to dump by epoch
                //two options 1) distributed system spark job to combine all the files
                //manual job run and combine all the files later
            }

            logger.info(String.format("Number of nodes: %d", curSimulation.uniqueNodes));
            logger.info(String.format("Duration of simulation: %d", (int) curSimulation.duration));
            logger.info(String.format("NumFound: %d", numFound));
//            for (int i = 0; i < duration; i++) {
//                for (int j = 0; j < nodes; j++) {
//                    dataOutput.writeDouble(paths.get((2 * j * duration) + (2 * i)));
//                    dataOutput.writeDouble(paths.get((2 * j * duration) + (2 * i) + 1));
//                }
//            }

            //Dejun's method
            dataOutput.close();
            logger.info(String.format("dumped to %s", fileName));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            LittleEndianDataInputStream dataInput = new LittleEndianDataInputStream(new FileInputStream(curSimulation.outputDirectory + fileName));
            // Count the total byte form the input stream
            int count = dataInput.available();
            System.out.println(count);
            int x = dataInput.readInt();
            System.out.println(x);
            int y = dataInput.readInt();
            System.out.println(y);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
