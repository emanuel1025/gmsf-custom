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

package simulator;

import mobility.MobileNode;

/**
 * Interface for pluggable Modules for the Simulation Framework.
 * @author psommer
 *
 */
public abstract class Module {

	/** Module name */
	public String name = "";
	
	/**
	 * Initializes the module
	 */
	public abstract void init();
	
	/**
	 * Lets the module execute the next time step in the simulation.
	 */
	public abstract void next();
	
	/**
	 * Finishes the module after a completed simulation run.
	 */
	public abstract void finish();
	
	/**
	 * Notifies the module that a new node has joined the simulation
	 * @param time time when node joins the simulation
	 * @param node Node which joins the simulation
	 */
	public abstract void addNode(double time, MobileNode node);
	
	/**
	 * Notifies the module that a node has left the simulation
	 * @param time time when node leaves the simuation
	 * @param node Node which leaves the simulation
	 */
	public abstract void removeNode(double time, MobileNode node);
	
	
}
