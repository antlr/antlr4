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
 *  TODO: Notes from Oli: index, seek, LA, LT, consume, getSourceName needed
 *  Do like Java IO and have new BufferedStream(new TokenStream) rather than
 *  using inheritance. line/col go into lexer
 */
public interface IntStream {
	void consume();

	/** Get int at current input pointer + i ahead where i=1 is next int.
	 *  Negative indexes are allowed.  LA(-1) is previous token (token
	 *  just matched).  LA(-i) where i is before first token should
	 *  yield -1, invalid char / EOF.
	 */
	int LA(int i);

	/** Tell the stream to start buffering if it hasn't already.  Return
     *  current input position, index(). seek(mark()) should not
	 *  affect the input cursor.
     */
	int mark();

	/** Reset the stream so that next call to index() would return index arg. */
	void rewind(int index);

	/** Return the current input symbol index 0..n where n indicates the
     *  last symbol has been read.  The index is the symbol about to be
	 *  read not the most recently read symbol.
     */
	int index();

	/** Set the input cursor to the position indicated by index.  This is
	 *  normally used to seek ahead in the input stream.  No buffering is
	 *  required to do this unless you know your stream will use seek to
	 *  move backwards such as when backtracking.
	 *
	 *  This is different from rewind in its multi-directional
	 *  requirement and in that its argument is strictly an input cursor (index).
	 *
	 *  For char streams, seeking forward must update the stream state such
	 *  as line number.  For seeking backwards, you will be presumably
	 *  backtracking using the mark/rewind mechanism that restores state and
	 *  so this method does not need to update state when seeking backwards.
	 *
	 *  Currently, this method is only used for efficient backtracking using
	 *  memoization, but in the future it may be used for incremental parsing.
	 *
	 *  The index is 0..n-1.  A seek to position i means that LA(1) will
	 *  return the ith symbol.  So, seeking to 0 means LA(1) will return the
	 *  first element in the stream.
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
