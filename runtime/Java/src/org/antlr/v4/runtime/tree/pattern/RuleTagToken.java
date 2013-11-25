/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

/** A token object representing an entire subtree matched by a rule; e.g., <expr> */
public class RuleTagToken implements Token {
	protected String ruleName;
	protected int ruleImaginaryTokenType;
	protected String label;

	public RuleTagToken(String ruleName, int ruleImaginaryTokenType) {
		this.ruleName = ruleName;
		this.ruleImaginaryTokenType = ruleImaginaryTokenType;
	}

	public RuleTagToken(String ruleName, int ruleImaginaryTokenType, String label) {
		this(ruleName, ruleImaginaryTokenType);
		this.label = label;
	}

	@Override
	public int getChannel() {
		return 0;
	}

	@Override
	public String getText() {
		return "<"+ruleName+">";
	}

	@Override
	public int getType() {
		return ruleImaginaryTokenType;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int getCharPositionInLine() {
		return 0;
	}

	@Override
	public int getTokenIndex() {
		return 0;
	}

	@Override
	public int getStartIndex() {
		return 0;
	}

	@Override
	public int getStopIndex() {
		return 0;
	}

	@Override
	public TokenSource getTokenSource() {
		return null;
	}

	@Override
	public CharStream getInputStream() {
		return null;
	}

	@Override
	public String toString() {
		return ruleName+":"+ ruleImaginaryTokenType;
	}
}
