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
lexer grammar PositionAdjustingLexer;

@members {

this.resetAcceptPosition = function(index, line, column) {
	this._input.seek(index);
	this.line = line;
	this.column = column;
	this._interp.consume(this._input);
};

this.nextToken = function() {
	if (!("resetAcceptPosition" in this._interp)) {
		var lexer = this;
		this._interp.resetAcceptPosition = function(index, line, column) { lexer.resetAcceptPosition(index, line, column); };
	}
	return antlr4.Lexer.prototype.nextToken.call(this);
};

this.emit = function() {
	switch(this._type) {
	case TOKENS:
		this.handleAcceptPositionForKeyword("tokens");
		break;
	case LABEL:
		this.handleAcceptPositionForIdentifier();
		break;
	}
	return antlr4.Lexer.prototype.emit.call(this);
};

this.handleAcceptPositionForIdentifier = function() {
	var tokenText = this.text;
	var identifierLength = 0;
	while (identifierLength < tokenText.length && 
		PositionAdjustingLexer.isIdentifierChar(tokenText[identifierLength])
	) {
		identifierLength += 1;
	}
	if (this._input.index > this._tokenStartCharIndex + identifierLength) {
		var offset = identifierLength - 1;
		this._interp.resetAcceptPosition(this._tokenStartCharIndex + offset, 
				this._tokenStartLine, this._tokenStartColumn + offset);
		return true;
	} else {
		return false;
	}
};

this.handleAcceptPositionForKeyword = function(keyword) {
	if (this._input.index > this._tokenStartCharIndex + keyword.length) {
		var offset = keyword.length - 1;
		this._interp.resetAcceptPosition(this._tokenStartCharIndex + offset, 
			this._tokenStartLine, this._tokenStartColumn + offset);
		return true;
	} else {
		return false;
	}
};

PositionAdjustingLexer.isIdentifierChar = function(c) {
	return c.match(/^[0-9a-zA-Z_]+$/);
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
