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

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.*;

/** A node representing erroneous token range in token stream */
public class CommonErrorNode extends CommonTree {
	public IntStream input;
	public Token start;
	public Token stop;
	public RecognitionException trappedException;

	public CommonErrorNode(TokenStream input, Token start, Token stop,
						   RecognitionException e)
	{
		//System.out.println("start: "+start+", stop: "+stop);
		if ( stop==null ||
			 (stop.getTokenIndex() < start.getTokenIndex() &&
			  stop.getType()!=Token.EOF) )
		{
			// sometimes resync does not consume a token (when LT(1) is
			// in follow set.  So, stop will be 1 to left to start. adjust.
			// Also handle case where start is the first token and no token
			// is consumed during recovery; LT(-1) will return null.
			stop = start;
		}
		this.input = input;
		this.start = start;
		this.stop = stop;
		this.trappedException = e;
	}

	public boolean isNil() {
		return false;
	}

	public int getType() {
		return Token.INVALID_TYPE;
	}

	public String getText() {
		String badText = null;
		if ( start instanceof Token ) {
			int i = ((Token)start).getTokenIndex();
			int j = ((Token)stop).getTokenIndex();
			if ( ((Token)stop).getType() == Token.EOF ) {
				j = ((TokenStream)input).size();
			}
			badText = ((TokenStream)input).toString(i, j);
		}
		else if ( start instanceof Tree ) {
			badText = ((TreeNodeStream)input).toString(start, stop);
		}
		else {
			// people should subclass if they alter the tree type so this
			// next one is for sure correct.
			badText = "<unknown>";
		}
		return badText;
	}

	public String toString() {
		if ( trappedException instanceof MissingTokenException ) {
			return "<missing type: "+
				   ((MissingTokenException)trappedException).getMissingType()+
				   ">";
		}
		else if ( trappedException instanceof UnwantedTokenException ) {
			return "<extraneous: "+
				   ((UnwantedTokenException)trappedException).getUnexpectedToken()+
				   ", resync="+getText()+">";
		}
		else if ( trappedException instanceof MismatchedTokenException ) {
			return "<mismatched token: "+trappedException.token+", resync="+getText()+">";
		}
		else if ( trappedException instanceof NoViableAltException ) {
			return "<unexpected: "+trappedException.token+
				   ", resync="+getText()+">";
		}
		return "<error: "+getText()+">";
	}
}
