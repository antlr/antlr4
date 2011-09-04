/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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
package org.antlr.v4.runtime.debug;

import org.antlr.v4.runtime.*;

public class DebugTokenStream implements TokenStream {
	protected DebugEventListener dbg;
	public TokenStream input;
	protected boolean initialStreamState = true;

	/** Track the last mark() call result value for use in rewind(). */
	protected int lastMarker;

	public DebugTokenStream(TokenStream input, DebugEventListener dbg) {
		this.input = input;
		setDebugListener(dbg);
		// force TokenStream to get at least first valid token
		// so we know if there are any hidden tokens first in the stream
		input.LT(1);
	}

	public void setDebugListener(DebugEventListener dbg) {
		this.dbg = dbg;
	}

	public void consume() {
		if ( initialStreamState ) {
			consumeInitialHiddenTokens();
		}
		int a = input.index();
		Token t = input.LT(1);
		input.consume();
		int b = input.index();
		dbg.consumeToken(t);
		if ( b>a+1 ) {
			// then we consumed more than one token; must be off channel tokens
			for (int i=a+1; i<b; i++) {
				dbg.consumeHiddenToken(input.get(i));
			}
		}
	}

	/* consume all initial off-channel tokens */
	protected void consumeInitialHiddenTokens() {
		int firstOnChannelTokenIndex = input.index();
		for (int i=0; i<firstOnChannelTokenIndex; i++) {
			dbg.consumeHiddenToken(input.get(i));
		}
		initialStreamState = false;
	}

	public Token LT(int i) {
		if ( initialStreamState ) {
			consumeInitialHiddenTokens();
		}
		dbg.LT(i, input.LT(i));
		return input.LT(i);
	}

	public int LA(int i) {
		if ( initialStreamState ) {
			consumeInitialHiddenTokens();
		}
		dbg.LT(i, input.LT(i));
		return input.LA(i);
	}

	public Token get(int i) {
		return input.get(i);
	}

	public int mark() {
		lastMarker = input.mark();
		dbg.mark(lastMarker);
		return lastMarker;
	}

	public int index() {
		return input.index();
	}

	public int range() {
		return input.range();
	}

	public void release(int index) {
		dbg.release(index);
		input.release(index);
	}

	public void seek(int index) {
		// TODO: implement seek in dbg interface
		// db.seek(index);
		input.seek(index);
	}

	public int size() {
		return input.size();
	}

	public TokenSource getTokenSource() {
		return input.getTokenSource();
	}

	public String getSourceName() {
		return getTokenSource().getSourceName();
	}

	public String toString() {
		return input.toString();
	}

	public String toString(int start, int stop) {
		return input.toString(start,stop);
	}

	public String toString(Token start, Token stop) {
		return input.toString(start,stop);
	}
}
