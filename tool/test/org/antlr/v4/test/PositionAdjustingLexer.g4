lexer grammar PositionAdjustingLexer;

@members {
	@Override
	public Token nextToken() {
		if (!(_interp instanceof PositionAdjustingLexerATNSimulator)) {
			_interp = new PositionAdjustingLexerATNSimulator(this, _ATN);
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

		public PositionAdjustingLexerATNSimulator(Lexer recog, ATN atn) {
			super(recog, atn);
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
