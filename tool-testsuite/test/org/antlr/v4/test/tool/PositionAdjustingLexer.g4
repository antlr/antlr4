/*
 * [The "BSD license"]
 *  Copyright (c) 2012-2016 Terence Parr
 *  Copyright (c) 2012-2016 Sam Harwell
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
lexer grammar PositionAdjustingLexer;

@members {
	@Override
	public Token nextToken() {
		if (!(_interp instanceof PositionAdjustingLexerATNSimulator)) {
			_interp = new PositionAdjustingLexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
		}

		return super.nextToken();
	}

	@Override
	public Token emit() {
		switch (_type) {
		case TOKENS:
			handleAcceptPositionForKeyword("tokens");
			break;

		case LABEL:
			handleAcceptPositionForIdentifier();
			break;

		default:
			break;
		}

		return super.emit();
	}

	private boolean handleAcceptPositionForIdentifier() {
		String tokenText = getText();
		int identifierLength = 0;
		while (identifierLength < tokenText.length() && isIdentifierChar(tokenText.charAt(identifierLength))) {
			identifierLength++;
		}

		if (getInputStream().index() > _tokenStartCharIndex + identifierLength) {
			int offset = identifierLength - 1;
			getInterpreter().resetAcceptPosition(getInputStream(), _tokenStartCharIndex + offset, _tokenStartLine, _tokenStartCharPositionInLine + offset);
			return true;
		}

		return false;
	}

	private boolean handleAcceptPositionForKeyword(String keyword) {
		if (getInputStream().index() > _tokenStartCharIndex + keyword.length()) {
			int offset = keyword.length() - 1;
			getInterpreter().resetAcceptPosition(getInputStream(), _tokenStartCharIndex + offset, _tokenStartLine, _tokenStartCharPositionInLine + offset);
			return true;
		}

		return false;
	}

	@Override
	public PositionAdjustingLexerATNSimulator getInterpreter() {
		return (PositionAdjustingLexerATNSimulator)super.getInterpreter();
	}

	private static boolean isIdentifierChar(char c) {
		return Character.isLetterOrDigit(c) || c == '_';
	}

	protected static class PositionAdjustingLexerATNSimulator extends LexerATNSimulator {

		public PositionAdjustingLexerATNSimulator(Lexer recog, ATN atn,
												  DFA[] decisionToDFA,
												  PredictionContextCache sharedContextCache)
		{
			super(recog, atn, decisionToDFA, sharedContextCache);
		}

		protected void resetAcceptPosition(CharStream input, int index, int line, int charPositionInLine) {
			input.seek(index);
			this.line = line;
			this.charPositionInLine = charPositionInLine;
			consume(input);
		}

	}
}

ASSIGN : '=' ;
PLUS_ASSIGN : '+=' ;
LCURLY:	'{';

// 'tokens' followed by '{'
TOKENS : 'tokens' IGNORED '{';

// IDENTIFIER followed by '+=' or '='
LABEL
	:	IDENTIFIER IGNORED '+'? '='
	;

IDENTIFIER
	:	[a-zA-Z_] [a-zA-Z0-9_]*
	;

fragment
IGNORED
	:	[ \t\r\n]*
	;

NEWLINE
	:	[\r\n]+ -> skip
	;

WS
	:	[ \t]+ -> skip
	;
