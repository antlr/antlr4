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

package org.antlr.v4.parse;

import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ErrorType;

/** Override error handling for use with ANTLR tool itself; leaves
 *  nothing in grammar associated with Tool so others can use in IDEs, ...
 */
public class ToolANTLRParser extends ANTLRParser {
	public Tool tool;

	public ToolANTLRParser(TokenStream input, Tool tool) {
		super(input);
		this.tool = tool;
	}

	@Override
	public void displayRecognitionError(String[] tokenNames,
										RecognitionException e)
	{
		String msg = getParserErrorMessage(this, e);
		if ( !paraphrases.isEmpty() ) {
			String paraphrase = paraphrases.peek();
			msg = msg+" while "+paraphrase;
		}
	//	List stack = getRuleInvocationStack(e, this.getClass().getName());
	//	msg += ", rule stack = "+stack;
		tool.errMgr.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e.token, e, msg);
	}

	public String getParserErrorMessage(Parser parser, RecognitionException e) {
		String msg;
		if ( e instanceof NoViableAltException) {
			String name = parser.getTokenErrorDisplay(e.token);
			msg = name+" came as a complete surprise to me";
		}
		else if ( e instanceof v4ParserException) {
			msg = ((v4ParserException)e).msg;
		}
		else {
			msg = parser.getErrorMessage(e, parser.getTokenNames());
		}
		return msg;
	}

	@Override
	public void grammarError(ErrorType etype, org.antlr.runtime.Token token, Object... args) {
		tool.errMgr.grammarError(etype, getSourceName(), token, args);
	}
}
