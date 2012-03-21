/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime;

/**  A stream of either tokens or tree nodes */
public interface SymbolStream<T> extends IntStream {
	/** Get the symbol at absolute index i; 0..n-1.
	 *  This is only valid if the underlying stream implementation buffers
	 *  all of the incoming objects.
	 *
	 *  @throws UnsupportedOperationException if the index {@code i} is outside
	 *    the marked region and the stream does not support accessing symbols by
	 *    index outside of marked regions.
	 */
	public T get(int i);

	/** Get symbol at current input pointer + {@code k} ahead where {@code k=1}
	 *  is next symbol. k&lt;0 indicates objects in the past.  So -1 is previous
	 *  Object and -2 is two Objects ago. {@code LT(0)} is undefined.  For i>=n,
	 *  return an object representing EOF. Return {@code null} for {@code LT(0)}
	 *  and any index that results in an absolute index that is negative.
	 */
	T LT(int k);
}
