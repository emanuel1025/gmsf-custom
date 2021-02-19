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

package output.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;


import java.awt.Color;
import java.io.*;
import java.util.*;

import model.*;


/**
 * PDFOutput is a helper class providing methods to create PDF documents.
 * @author psommer
 *
 */
public class PDFOutput {

	/** PDF document object */
	Document document = null;
	/** PDF writer object */
	PdfWriter writer = null;
	/** Buffered output stream */
	BufferedOutputStream stream = null;
	/** pdf content byte object */
	PdfContentByte cb = null;
	
	/** y coordinate of the lower-left corner */
	final int yMin = 156;
	/** x coordinate of the lower-left corner */
	final int xMin = 48;
	/** x coordinate of the upper-right corner */
	final int xMax = 548; 
	/** y coordinate of the upper-right corner */
	final int yMax = 656;
	
	/** scale factor */
	double scale = 1;
	/** area size */
	double size = 0;
	
	
	public PDFOutput(File file, double size) {
		
		this.size = size;
		
		try {
			stream = new BufferedOutputStream(new FileOutputStream(file));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return;
		}
		
		document = new Document(PageSize.A4);

		try {
			writer = PdfWriter.getInstance(document, stream);
	    
			// open document
	    	document.open();
	    	
	    	// content byte
	    	cb = writer.getDirectContent();
	        
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		// calculate scale factor
		scale = size / (xMax -xMin);
	    
	}
	
	/**
	 * Sets a background image for the pdf document
	 * @param image Background image file
	 */
	public void setBackgroundImage(String image) {
		
		try {
			Image background = Image.getInstance(image);
	    	background.setAbsolutePosition(xMin, yMin);
	    	background.scaleAbsolute(xMax-xMin, yMax-yMin);
	    	document.add(background);
	    } catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Creates a new page in the PDF document
	 */
	public void newPage() {
		try {
			document.add(Chunk.NEXTPAGE);
		} catch (Exception e) {}
	}
	
	/**
	 * Draws text at the specified position (x,y) with the specified color and size
	 * @param x x-position
	 * @param y y-position
	 * @param text Text 
	 * @param color Font color
	 * @param size Font size
	 */
	public void drawText(double x, double y, String text, Color color, float size) {
		
		BaseFont footnote = null;
	    try {
	    	footnote = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
	 	} catch (Exception e) {
	 		System.err.print(e.getMessage());
	 	}
	     
		// output text
		cb.setColorStroke(color);
    	cb.setColorFill(color);
        cb.setFontAndSize(footnote, size);
	    cb.beginText();
    	cb.setTextMatrix((float) (x/scale + xMin), (float) (y/scale + yMin));
    	cb.showText(text);
    	cb.endText();
	}
	
	/**
	 * Draws a circle with the specified position, color and size
	 * @param x x-position of the center
	 * @param y y-position of the center
	 * @param color Line color
	 * @param size Circle radius
	 */
	public void drawCircle(double x, double y, Color color, float size) {
    	cb.setLineWidth(1f);
    	cb.setColorStroke(color);
    	cb.circle((float) (x/scale + xMin), (float) (y/scale) + yMin , size);   	
     	cb.stroke();
	}
	
	/**
	 * Draws a line with the specified line width and color
	 * @param line Line
	 * @param color Line color
	 * @param size Line width
	 */
	public void drawLine(Line line, Color color, float size) {
		try {
	        // draw line
	        cb.moveTo((float) (line.x1/scale + xMin), (float) (line.y1/scale) + yMin);
	        cb.setLineWidth(size);
	        cb.setColorStroke(color);
	    	cb.lineTo((float) (line.x2/scale + xMin), (float) (line.y2/scale) + yMin);
	        cb.stroke();
	    } catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	/**
	 * Draws a path with the specified line width and color
	 * @param path Path
	 * @param color Line color
	 * @param size Line width
	 */
	public void drawPath(Path path, Color color, float size) {
		try {
			
			Iterator<RoadEdge> it = path.getPathIterator();
			while (it.hasNext()) {
				RoadEdge edge = it.next();
				RoadNode nodeA = edge.getStartNode();
				RoadNode nodeB = edge.getEndNode();
				
				if (edge.segments==null) {
					
					drawLine(new Line(nodeA.x, nodeA.y, nodeB.x, nodeB.y), color, 0.5f);
					
				} else {
					Iterator<Line> it2 = edge.segments.iterator();
					while (it2.hasNext()) {
						drawLine(it2.next(), color, 0.5f);
					}
					
				}
			}
	        
	    } catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	/**
	 * Draws a rectangle with the specified coordinates and colors 
	 * @param x1 x-coordinate of the lower-left corner
	 * @param y1 y-coordinate of the lower-left corner
	 * @param x2 x-coordinate of the upper-right corner
	 * @param y2 y-coordinate of the upper-right corner
	 * @param lineColor Line color
	 * @param fillColor Fill color
	 * @param border Border widht
	 */
	public void drawRectangle(double x1, double y1, double x2, double y2, Color lineColor, Color fillColor, float border) {
		try {
	        cb.setColorFill(fillColor);
	        cb.setColorStroke(lineColor);
	        cb.setLineWidth(0.1f);
	        cb.rectangle((float) (x1/scale + xMin), (float) (y1/scale + yMin), (float) ((x2-x1)/scale), (float) ((y2-y1)/scale));
	        cb.closePathFillStroke();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Draws a polygon from a list of lines and with the specified colors
	 * @param lines List with polygon lines
	 * @param lineColor Line color
	 * @param fillColor Fill color
	 */
	public void drawPolygon(java.util.List<Line> lines, Color lineColor, Color fillColor) {
		
		cb.setRGBColorFill(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue());
        cb.setRGBColorStroke(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue());
        cb.setLineWidth(0);
        
		Iterator<Line> it = lines.iterator();
		
		Line line = it.next();
		cb.moveTo((float) (line.x1/scale + xMin), (float) (line.y1/scale) + yMin);
		cb.lineTo((float) (line.x2/scale + xMin), (float) (line.y2/scale) + yMin);
	     
		double startX = line.x1;
	    double startY = line.y1;
		
	    double lastX = startX;
	    double lastY = startY;
	    
		while (it.hasNext()) {
			
			 line = it.next();
			 if (lastX!=line.x1 || lastY!=line.y1) {
				 cb.moveTo((float) (line.x1/scale + xMin), (float) (line.y1/scale) + yMin);
			 }
		     cb.lineTo((float) (line.x2/scale + xMin), (float) (line.y2/scale) + yMin);
		     
		     lastX = line.x2;
		     lastY = line.y2;
		}
	
		cb.stroke();

	}

	
	/**
	 * Draws a border around the simulation area with coordinate ticks.
	 * @param xticks Number of ticks on the x-coordinate
	 * @param yticks Number of ticks on the y-coordinate
	 */
	public void drawBorder(int xticks, int yticks) {
		// draw border around page
		
    	cb.setColorStroke(Color.BLACK);
    	cb.setColorFill(Color.BLACK);
    	
        cb.moveTo(xMin, yMin);
        cb.lineTo(xMin, yMax);
        cb.lineTo(xMax, yMax);
        cb.lineTo(xMax, yMin);
        cb.lineTo(xMin, yMin);
        cb.stroke();
        
        try {
         	BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            cb.setLineWidth(1f);
        	
            float xOffset = (float)size/xticks;
        	for (int i=0; i<xticks+1; i++) {
         		float xPos = xMin + convert(i*xOffset);
        		cb.moveTo(xPos, yMin);
                cb.lineTo(xPos, yMin-5);
                cb.stroke();
        		
                cb.beginText();
                cb.setFontAndSize(bf, 9);
                cb.showTextAligned(PdfContentByte.ALIGN_CENTER, Integer.toString((int)(i*xOffset)), (float)xPos, (float)yMin - 20, 0);
                cb.endText();
         	}
        	
            float yOffset = (float)size/yticks;
        	for (int i=0; i<yticks+1; i++) {
         		float yPos = yMin + convert(i*yOffset);
        		cb.moveTo(xMin, yPos);
                cb.lineTo(xMin-5, yPos);
                cb.stroke();
        		
                cb.beginText();
                cb.setFontAndSize(bf, 9);
                cb.showTextAligned(PdfContentByte.ALIGN_CENTER, Integer.toString((int)(i*xOffset)), (float)xMin - 10, (float)yPos,  90);
                cb.endText();
        	}
         	
        } catch (Exception e) {
        	System.err.println(e.getLocalizedMessage());
        }
	}
	
	
	/**
	 * Draws the given road network
	 * @param roadNetwork Road network
	 */
	public void drawRoadNetwork(RoadNetwork roadNetwork) {
		
		Iterator<RoadEdge> edgeIterator = roadNetwork.getEdges().iterator();
		
		while (edgeIterator.hasNext()) {
			
			RoadEdge edge = edgeIterator.next();
			RoadNode nodeA = edge.getStartNode();
			RoadNode nodeB = edge.getEndNode();
			
			
			Color color = Color.BLACK;
			
			if (nodeA.getOutEdges().size()==5) {
				drawCircle(nodeA.x, nodeA.y, color, 5f);
				System.out.println("Node id: " + nodeA.id + " " + nodeA.x + " " + nodeA.y);
			}
			
			
			if (edge.segments==null) {
				
				drawLine(new Line(nodeA.x, nodeA.y, nodeB.x, nodeB.y), color, 0.5f);
				
			} else {
				Iterator<Line> it = edge.segments.iterator();
				while (it.hasNext()) {
					drawLine(it.next(), color, 0.5f);
				}
				
			}
			
			
			
			//drawCircle(nodeA.x, nodeA.y, Color.BLACK, 1f);
			//drawCircle(nodeB.x, nodeB.y, Color.BLACK, 1f);
		}
		
	}
	
	/**
	 * Draws a graph
	 * @param graph Graph
	 */
	public void drawGraph(Graph graph) {
		
		Iterator<GraphEdge> edgeIterator = graph.getEdges().iterator();
		
		while (edgeIterator.hasNext()) {
			
			GraphEdge edge = edgeIterator.next();
			
			GraphNode nodeA = edge.getNodeA();
			GraphNode nodeB = edge.getNodeB();
			
			drawLine(new Line(nodeA.x, nodeA.y, nodeB.x, nodeB.y), Color.BLACK, 0.5f);
		}
		
		Iterator<GraphNode> nodeIterator = graph.getNodes().iterator();
		
		while (nodeIterator.hasNext()) {
			GraphNode node = nodeIterator.next();
			drawCircle(node.x, node.y, Color.BLACK, 2f);
		}
		
	}
	
	/**
	 * Closes the pdf output file
	 */
	public void close() {

		try {
			// close document
	    	document.close();
	    	// close output stream
	    	stream.flush();
	    	stream.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Converts coordinates from simulation space to page space
	 * @param value Coordinate in simulation space
	 * @return Coordinate in page space
	 */
	float convert(float value) {
		return (float)(value/scale);
	}

	
}
