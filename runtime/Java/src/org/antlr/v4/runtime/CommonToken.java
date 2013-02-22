/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Pair;

import java.io.Serializable;

public class CommonToken implements WritableToken, Serializable {
	protected static final Pair<TokenSource, CharStream> EMPTY_SOURCE =
		new Pair<TokenSource, CharStream>(null, null);

	protected int type;
	protected int line;
	protected int charPositionInLine = -1; // set to invalid position
	protected int channel=DEFAULT_CHANNEL;
	protected Pair<TokenSource, CharStream> source;

	/** We need to be able to change the text once in a while.  If
	 *  this is non-null, then getText should return this.  Note that
	 *  start/stop are not affected by changing this.
	  */
	// TODO: can store these in map in token stream rather than as field here
	protected String text;

	/** What token number is this from 0..n-1 tokens; < 0 implies invalid index */
	protected int index = -1;

	/** The char position into the input buffer where this token starts */
	protected int start;

	/** The char position into the input buffer where this token stops */
	protected int stop;

	public CommonToken(int type) {
		this.type = type;
	}

	public CommonToken(@NotNull Pair<TokenSource, CharStream> source, int type, int channel, int start, int stop) {
		this.source = source;
		this.type = type;
		this.channel = channel;
		this.start = start;
		this.stop = stop;
		if (source.a != null) {
			this.line = source.a.getLine();
			this.charPositionInLine = source.a.getCharPositionInLine();
		}
	}

	public CommonToken(int type, String text) {
		this.type = type;
		this.channel = DEFAULT_CHANNEL;
		this.text = text;
		this.source = EMPTY_SOURCE;
	}

	public CommonToken(Token oldToken) {
		text = oldToken.getText();
		type = oldToken.getType();
		line = oldToken.getLine();
		index = oldToken.getTokenIndex();
		charPositionInLine = oldToken.getCharPositionInLine();
		channel = oldToken.getChannel();
		start = oldToken.getStartIndex();
		stop = oldToken.getStopIndex();

		if (oldToken instanceof CommonToken) {
			source = ((CommonToken)oldToken).source;
		}
		else {
			source = new Pair<TokenSource, CharStream>(oldToken.getTokenSource(), oldToken.getInputStream());
		}
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public String getText() {
		if ( text!=null ) {
			return text;
		}

		CharStream input = getInputStream();
		if ( input==null ) return null;
		int n = input.size();
		if ( start<n && stop<n) {
			return input.getText(Interval.of(start,stop));
		}
		else {
			return "<EOF>";
		}
	}

	/** Override the text for this token.  getText() will return this text
	 *  rather than pulling from the buffer.  Note that this does not mean
	 *  that start/stop indexes are not valid.  It means that that input
	 *  was converted to a new string in the token object.
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	@Override
	public void setCharPositionInLine(int charPositionInLine) {
		this.charPositionInLine = charPositionInLine;
	}

	@Override
	public int getChannel() {
		return channel;
	}

	@Override
	public void setChannel(int channel) {
		this.channel = channel;
	}

	@Override
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getStartIndex() {
		return start;
	}

	public void setStartIndex(int start) {
		this.start = start;
	}

	@Override
	public int getStopIndex() {
		return stop;
	}

	public void setStopIndex(int stop) {
		this.stop = stop;
	}

	@Override
	public int getTokenIndex() {
		return index;
	}

	@Override
	public void setTokenIndex(int index) {
		this.index = index;
	}

	@Override
	public TokenSource getTokenSource() {
		return source.a;
	}

	@Override
	public CharStream getInputStream() {
		return source.b;
	}

	@Override
	public String toString() {
		String channelStr = "";
		if ( channel>0 ) {
			channelStr=",channel="+channel;
		}
		String txt = getText();
		if ( txt!=null ) {
			txt = txt.replace("\n","\\n");
			txt = txt.replace("\r","\\r");
			txt = txt.replace("\t","\\t");
		}
		else {
			txt = "<no text>";
		}
		return "[@"+getTokenIndex()+","+start+":"+stop+"='"+txt+"',<"+type+">"+channelStr+","+line+":"+getCharPositionInLine()+"]";
	}
}
