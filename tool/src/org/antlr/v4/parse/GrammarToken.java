/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.parse;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.v4.tool.Grammar;

/** A CommonToken that can also track it's original location,
 *  derived from options on the element ref like BEGIN&lt;line=34,...&gt;.
 */
public class GrammarToken extends CommonToken {
	public Grammar g;
	public int originalTokenIndex = -1;

	public GrammarToken(Grammar g, Token oldToken) {
		super(oldToken);
		this.g = g;
	}

	@Override
	public int getCharPositionInLine() {
		if ( originalTokenIndex>=0 ) return g.originalTokenStream.get(originalTokenIndex).getCharPositionInLine();
		return super.getCharPositionInLine();
	}

	@Override
	public int getLine() {
		if ( originalTokenIndex>=0 ) return g.originalTokenStream.get(originalTokenIndex).getLine();
		return super.getLine();
	}

	@Override
	public int getTokenIndex() {
		return originalTokenIndex;
	}

	@Override
	public int getStartIndex() {
		if ( originalTokenIndex>=0 ) {
			return ((CommonToken)g.originalTokenStream.get(originalTokenIndex)).getStartIndex();
		}
		return super.getStartIndex();
	}

	@Override
	public int getStopIndex() {
		int n = super.getStopIndex() - super.getStartIndex() + 1;
		return getStartIndex() + n - 1;
	}

	@Override
	public String toString() {
		String channelStr = "";
		if ( channel>0 ) {
			channelStr=",channel="+channel;
		}
		String txt = getText();
		if ( txt!=null ) {
			txt = txt.replaceAll("\n","\\\\n");
			txt = txt.replaceAll("\r","\\\\r");
			txt = txt.replaceAll("\t","\\\\t");
		}
		else {
			txt = "<no text>";
		}
		return "[@"+getTokenIndex()+","+getStartIndex()+":"+getStopIndex()+
			   "='"+txt+"',<"+getType()+">"+channelStr+","+getLine()+":"+getCharPositionInLine()+"]";
	}
}
