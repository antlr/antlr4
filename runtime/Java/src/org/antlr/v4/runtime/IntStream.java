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

/** A simple stream of integers used when all I care about is the char
 *  or token type sequence (such as interpretation).
 *
 *  Do like Java IO and have new BufferedStream(new TokenStream) rather than
 *  using inheritance?
 */
public interface IntStream<Symbol> {
	void consume();

	/** Get int at current input pointer + i ahead where i=1 is next int.
	 *  Negative indexes are allowed.  LA(-1) is previous token (token
	 *  just matched).  LA(-i) where i is before first token should
	 *  yield -1, invalid char / EOF.
	 */
	int LA(int i);

	/** Tell the stream to start buffering if it hasn't already.  Return
     *  a marker, usually current input position, index().
	 *  Calling release(mark()) should not affect the input cursor.
	 *  Can seek to any index between where we were when mark() was called
	 *  and current index() until we release this marker.
     */
	int mark();

	/** Release requirement that stream holds tokens from marked location
	 *  to current index().  Must release in reverse order (like stack)
     *  of mark() otherwise undefined behavior.
	 */
	void release(int marker);

	/** Return the current input symbol index 0..n where n indicates the
     *  last symbol has been read.  The index is the symbol about to be
	 *  read not the most recently read symbol.
     */
	int index();

	/** Set the input cursor to the position indicated by index.  This is
	 *  normally used to rewind the input stream but can move forward as well.
	 *  It's up to the stream implementation to make sure that symbols are
	 *  buffered as necessary to make seek land on a valid symbol.
	 *  Or, they should avoid moving the input cursor.
	 *
	 *  The index is 0..n-1.  A seek to position i means that LA(1) will
	 *  return the ith symbol.  So, seeking to 0 means LA(1) will return the
	 *  first element in the stream.
     *
     *  For unbuffered streams, index i might not be in buffer. That throws
     *  index exception
	 */
	void seek(int index);

	/** Only makes sense for streams that buffer everything up probably, but
	 *  might be useful to display the entire stream or for testing.  This
	 *  value includes a single EOF.
	 */
	int size();

	/** Where are you getting symbols from?  Normally, implementations will
	 *  pass the buck all the way to the lexer who can ask its input stream
	 *  for the file name or whatever.
	 */
	public String getSourceName();
}
