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

package probability;

import java.util.*;

/**
 * UniformDistribution generates samples of a uniform distributed random variable X.
 * The Java Random number generator (java.util.Random) is used to generate the sample values.
 * @author psommer
 *
 */
public class UniformDistribution implements Distribution {

	/** Random number generator */
	Random rng = null;
	/** minimum value */
	double min = 0;
	/** maximum value */
	double max = 0;
	
	/**
	 * Creates a UniformDistribution. Sample values are uniformly distributed between the minimum and the maximum value.
	 * @param min minimum value
	 * @param max maximum value
	 * @param seed seed value for the underlying random number generator
	 */
	public UniformDistribution(double min, double max, long seed) {
		// initialize random number generator
		rng = new Random(seed);
		// set min value
		this.min = min;
		// set max value
		this.max = max;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public double nextValue() {
		return min + rng.nextDouble()*(max-min);
	}
	
	public double getMean() {
		return (min + max)/2;
	}
	
	public double getPDF(double x) {
		return 1/(max-min);
	}
	
	public double getCDF(double x) {
		return (x-min)/(max-min);
	}
	
}
