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
import org.antlr.v4.runtime.misc.Pair;

import java.io.Serializable;

public class CommonToken implements WritableToken, Serializable {
	/**
	 * An empty {@link Pair} which is used as the default value of
	 * {@link #source} for tokens that do not have a source.
	 */
	protected static final Pair<TokenSource, CharStream> EMPTY_SOURCE =
		new Pair<TokenSource, CharStream>(null, null);

	/**
	 * This is the backing field for {@link #getType} and {@link #setType}.
	 */
	protected int type;

	/**
	 * This is the backing field for {@link #getLine} and {@link #setLine}.
	 */
	protected int line;

	/**
	 * This is the backing field for {@link #getCharPositionInLine} and
	 * {@link #setCharPositionInLine}.
	 */
	protected int charPositionInLine = -1; // set to invalid position

	/**
	 * This is the backing field for {@link #getChannel} and
	 * {@link #setChannel}.
	 */
	protected int channel=DEFAULT_CHANNEL;

	/**
	 * This is the backing field for {@link #getTokenSource} and
	 * {@link #getInputStream}.
	 *
	 * <p>
	 * These properties share a field to reduce the memory footprint of
	 * {@link CommonToken}. Tokens created by a {@link CommonTokenFactory} from
	 * the same source and input stream share a reference to the same
	 * {@link Pair} containing these values.</p>
	 */

	protected Pair<TokenSource, CharStream> source;

	/**
	 * This is the backing field for {@link #getText} when the token text is
	 * explicitly set in the constructor or via {@link #setText}.
	 *
	 * @see #getText()
	 */
	protected String text;

	/**
	 * This is the backing field for {@link #getTokenIndex} and
	 * {@link #setTokenIndex}.
	 */
	protected int index = -1;

	/**
	 * This is the backing field for {@link #getStartIndex} and
	 * {@link #setStartIndex}.
	 */
	protected int start;

	/**
	 * This is the backing field for {@link #getStopIndex} and
	 * {@link #setStopIndex}.
	 */
	protected int stop;

	/**
	 * Constructs a new {@link CommonToken} with the specified token type.
	 *
	 * @param type The token type.
	 */
	public CommonToken(int type) {
		this.type = type;
		this.source = EMPTY_SOURCE;
	}

	public CommonToken(Pair<TokenSource, CharStream> source, int type, int channel, int start, int stop) {
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

	/**
	 * Constructs a new {@link CommonToken} with the specified token type and
	 * text.
	 *
	 * @param type The token type.
	 * @param text The text of the token.
	 */
	public CommonToken(int type, String text) {
		this.type = type;
		this.channel = DEFAULT_CHANNEL;
		this.text = text;
		this.source = EMPTY_SOURCE;
	}

	/**
	 * Constructs a new {@link CommonToken} as a copy of another {@link Token}.
	 *
	 * <p>
	 * If {@code oldToken} is also a {@link CommonToken} instance, the newly
	 * constructed token will share a reference to the {@link #text} field and
	 * the {@link Pair} stored in {@link #source}. Otherwise, {@link #text} will
	 * be assigned the result of calling {@link #getText}, and {@link #source}
	 * will be constructed from the result of {@link Token#getTokenSource} and
	 * {@link Token#getInputStream}.</p>
	 *
	 * @param oldToken The token to copy.
	 */
	public CommonToken(Token oldToken) {
		type = oldToken.getType();
		line = oldToken.getLine();
		index = oldToken.getTokenIndex();
		charPositionInLine = oldToken.getCharPositionInLine();
		channel = oldToken.getChannel();
		start = oldToken.getStartIndex();
		stop = oldToken.getStopIndex();

		if (oldToken instanceof CommonToken) {
			text = ((CommonToken)oldToken).text;
			source = ((CommonToken)oldToken).source;
		}
		else {
			text = oldToken.getText();
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

	/**
	 * Explicitly set the text for this token. If {code text} is not
	 * {@code null}, then {@link #getText} will return this value rather than
	 * extracting the text from the input.
	 *
	 * @param text The explicit text of the token, or {@code null} if the text
	 * should be obtained from the input along with the start and stop indexes
	 * of the token.
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
