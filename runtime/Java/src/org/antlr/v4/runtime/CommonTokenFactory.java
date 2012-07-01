/*
 [The "BSD license"]
 Copyright (c) 2012 Terence Parr
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

import org.antlr.v4.runtime.misc.Interval;

public class CommonTokenFactory implements TokenFactory<CommonToken> {
	public static final TokenFactory<CommonToken> DEFAULT = new CommonTokenFactory();

	/** Copy text for token out of input char stream. Useful when input
	 *  stream is unbuffered.
	 *  @see UnbufferedCharStream
 	 */
	protected final boolean copyText;

	/** Create factory and indicate whether or not the factory copy
	 *  text out of the char stream.
	 */
	public CommonTokenFactory(boolean copyText) { this.copyText = copyText; }

	public CommonTokenFactory() { this(false); }

	@Override
	public CommonToken create(TokenSource source, int type, String text,
							  int channel, int start, int stop,
							  int line, int charPositionInLine)
	{
		CommonToken t = new CommonToken(source, type, channel, start, stop);
		t.setLine(line);
		t.setCharPositionInLine(charPositionInLine);
		if ( text!=null ) {
			t.setText(text);
		}
		else {
			if ( copyText ) {
				CharStream input = source.getInputStream();
				t.setText(input.getText(Interval.of(start,stop)));
			}
		}
		return t;
	}

	@Override
	public CommonToken create(int type, String text) {
		return new CommonToken(type, text);
	}
}
