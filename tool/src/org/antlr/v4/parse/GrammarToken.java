/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
