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

/** 
 * Distribution is an interface for the probability distribution of a random variable.
 */
public interface Distribution {

	/**
	 * Returns the minimum possible value for the random variable X
	 * @return minimum value of X
	 */
	public abstract double getMin();
	
	/**
	 * Returns the maximum possible value for the random variable X
	 * @return maximum value of X
	 */
	public abstract double getMax();
	
	/**
	 * Returns the mean value of the random variable X
	 * @return mean value of X
	 */
	public abstract double getMean();
	
	/**
	 * Returns the value of the probability density function (PDF) for the given argument
	 * @param x argument
	 * @return f(x)
	 */
	public abstract double getPDF(double x);
	
	/**
	 * Returns the value of the cumulative distribution function (CDF) for the given argument
	 * @param x argument
	 * @return F(x)
	 */
	public abstract double getCDF(double x);
	
	/**
	 * Returns the next random variable
	 * @return value of random variable x
	 */
	public abstract double nextValue();
	
}
