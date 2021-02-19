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

package gui;

import javax.swing.*;

import output.pdf.PDFOutput;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.*;
import mobility.*;
import simulator.*;
import simulator.Module;
import model.*;


/**
 * GUI is a graphical user interface to the simulator framework.
 * @author psommer
 *
 */
public class GUI extends Module {
	
	Object monitor = new Object();
	/** local list of mobile nodes in the simulation */
	List<MobileNode> nodes = new LinkedList<MobileNode>();
	/** local list of edges (connections) between nodes */
	List<GraphEdge> edges = new LinkedList<GraphEdge>();
	
	/** window size */
	int windowSize = 500;
	int windowOffset = 20;
	
	/** indicates if simulation is paused */
	boolean paused = false;
	
	/** start/next/pause button */
	JButton startButton = new JButton("Start");
	/** snapshot button */
	JButton snapshotButton = new JButton("Snapshot (PDF)");
	
	/** background image (map image) */
	Image background = null;
	/** window panel for the GUI */
	GUIPanel panel = null;
	
	private class GUIPanel extends JPanel {
		
		static final long serialVersionUID = 1;
		
		protected void paintComponent(Graphics g) {
			
			// paint super component
			super.paintComponent(g);
			
			// draw background image
			if (background!=null) g.drawImage(background, windowOffset, windowOffset, this);
			// draw rectangles around the simulation areas
			g.setColor(Color.BLACK);
			g.drawRect(windowOffset, windowOffset, windowSize, windowSize);
			
			synchronized(monitor) {
				
				// draw position of mobile nodes
				Iterator<MobileNode> it = nodes.iterator();
				while (it.hasNext()) {

					MobileNode node = it.next();
					int x = (int)Math.floor(node.x/Simulator.size*windowSize);
					int y = (int)Math.floor(node.y/Simulator.size*windowSize);
					
					if (x>=0 && x<=windowSize && y>=0 && y<=windowSize) {
						// draw node
						g.setColor(Color.RED);
						g.fillOval(windowOffset + x -4, windowOffset + windowSize- y - 4, 8, 8);
						g.drawString(Integer.toString(node.id), windowOffset + x + 10, windowOffset + windowSize - y + 10);
					} else {
						System.out.println("Outside the window: x=" + x + ", y=" + y);
					}
				}	
				
			}
			
	    }
	}
	
	public GUI() {
		name = "GUI";
	}
	
	
	public void init() {
		
		// create panel
		panel = new GUIPanel();
		// Create and set up the window.
        JFrame frame = new JFrame("Generic Mobility Simulation Framework (GMSF)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setLayout(new BorderLayout(1, 2));
        
        // add buttons
        frame.add(startButton, BorderLayout.NORTH);
        frame.add(snapshotButton, BorderLayout.SOUTH);
        
        // action listener for start button
        ActionListener al = new ActionListener() {
          public void actionPerformed( ActionEvent e ) {
            if (paused) {
            	startButton.setText("Pause");
            }
            else startButton.setText("Continue");
        	  paused = !paused;
          }
        };
        
        startButton.addActionListener(al);

        al = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
             // take snapshot
             PDFOutput pdf = new PDFOutput(new File(Simulator.outputDirectory + "/snapshot.pdf"), Simulator.size);
             Graph graph = new Graph();
             graph.setNodes(new LinkedList<GraphNode>(nodes));
             
             pdf.drawGraph(graph);
             pdf.drawLine(new Line(0, 0, 0, Simulator.size), Color.black, 1);
     		 pdf.drawLine(new Line(0, Simulator.size, Simulator.size, Simulator.size), Color.black, 1);
     		 pdf.drawLine(new Line(Simulator.size, Simulator.size, Simulator.size, 0), Color.black, 1);
     		 pdf.drawLine(new Line(Simulator.size, 0, 0, 0), Color.black, 1);
     		 pdf.close();
          };
        };
        
        snapshotButton.addActionListener(al);
        
        frame.pack();
        frame.setVisible(true);
		frame.add(panel);
        frame.setSize( windowSize + 3*windowOffset, windowSize + 6*windowOffset );
        
        // background image
        background = Toolkit.getDefaultToolkit().getImage(Simulator.inputDirectory + "/map.png");
		
        // start paused
        paused = true;
        if (paused) {
         	while (paused) {
        		try {
    				Thread.sleep(10);
    			} catch (Exception e) {}
        	}
		} 
        
	}
	
	
	public void next() {
		
		synchronized(monitor) {
			edges.clear();
			edges.addAll(Simulator.neighborhoodGraph.getEdges());
		}
		// paint panel
		panel.repaint();
		
		if (paused) {
         	while (paused) {
        		try {
    				Thread.sleep(10);
    			} catch (Exception e) {}
        	}
		}
		else {
			try {
				Thread.sleep(50);
			} catch (Exception e) {}
		}
		
	};
	
	public void addNode(double time, MobileNode node) {
		synchronized(monitor) {
			nodes.add(node);
		}
	}
	
	public void removeNode(double time, MobileNode node) {
		synchronized(monitor) {
			nodes.remove(node);
		}
	}

	public void finish() {};
	
	
}
