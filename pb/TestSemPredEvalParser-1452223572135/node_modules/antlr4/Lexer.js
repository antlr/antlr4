// [The "BSD license"]
//  Copyright (c) 2012 Terence Parr
//  Copyright (c) 2012 Sam Harwell
//  Copyright (c) 2014 Eric Vergnaud
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions
//  are met:
//
//  1. Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//  2. Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in the
//     documentation and/or other materials provided with the distribution.
//  3. The name of the author may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
//  this SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
//  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
//  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
//  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
//  this SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
///

// A lexer is recognizer that draws input symbols from a character stream.
//  lexer grammars result in a subclass of this object. A Lexer object
//  uses simplified match() and error recovery mechanisms in the interest
//  of speed.
///

var Token = require('./Token').Token;
var Recognizer = require('./Recognizer').Recognizer;
var CommonTokenFactory = require('./CommonTokenFactory').CommonTokenFactory;
var LexerNoViableAltException = require('./error/Errors').LexerNoViableAltException;

function TokenSource() {
	return this;
}

function Lexer(input) {
	Recognizer.call(this);
	this._input = input;
	this._factory = CommonTokenFactory.DEFAULT;
	this._tokenFactorySourcePair = [ this, input ];

	this._interp = null; // child classes must populate this

	// The goal of all lexer rules/methods is to create a token object.
	// this is an instance variable as multiple rules may collaborate to
	// create a single token. nextToken will return this object after
	// matching lexer rule(s). If you subclass to allow multiple token
	// emissions, then set this to the last token to be matched or
	// something nonnull so that the auto token emit mechanism will not
	// emit another token.
	this._token = null;

	// What character index in the stream did the current token start at?
	// Needed, for example, to get the text for current token. Set at
	// the start of nextToken.
	this._tokenStartCharIndex = -1;

	// The line on which the first character of the token resides///
	this._tokenStartLine = -1;

	// The character position of first character within the line///
	this._tokenStartColumn = -1;

	// Once we see EOF on char stream, next token will be EOF.
	// If you have DONE : EOF ; then you see DONE EOF.
	this._hitEOF = false;

	// The channel number for the current token///
	this._channel = Token.DEFAULT_CHANNEL;

	// The token type for the current token///
	this._type = Token.INVALID_TYPE;

	this._modeStack = [];
	this._mode = Lexer.DEFAULT_MODE;

	// You can set the text for the current token to override what is in
	// the input char buffer. Use setText() or can set this instance var.
	// /
	this._text = null;

	return this;
}

Lexer.prototype = Object.create(Recognizer.prototype);
Lexer.prototype.constructor = Lexer;

Lexer.DEFAULT_MODE = 0;
Lexer.MORE = -2;
Lexer.SKIP = -3;

Lexer.DEFAULT_TOKEN_CHANNEL = Token.DEFAULT_CHANNEL;
Lexer.HIDDEN = Token.HIDDEN_CHANNEL;
Lexer.MIN_CHAR_VALUE = '\u0000';
Lexer.MAX_CHAR_VALUE = '\uFFFE';

Lexer.prototype.reset = function() {
	// wack Lexer state variables
	if (this._input !== null) {
		this._input.seek(0); // rewind the input
	}
	this._token = null;
	this._type = Token.INVALID_TYPE;
	this._channel = Token.DEFAULT_CHANNEL;
	this._tokenStartCharIndex = -1;
	this._tokenStartColumn = -1;
	this._tokenStartLine = -1;
	this._text = null;

	this._hitEOF = false;
	this._mode = Lexer.DEFAULT_MODE;
	this._modeStack = [];

	this._interp.reset();
};

// Return a token from this source; i.e., match a token on the char stream.
Lexer.prototype.nextToken = function() {
	if (this._input === null) {
		throw "nextToken requires a non-null input stream.";
	}

	// Mark start location in char stream so unbuffered streams are
	// guaranteed at least have text of current token
	var tokenStartMarker = this._input.mark();
	try {
		while (true) {
			if (this._hitEOF) {
				this.emitEOF();
				return this._token;
			}
			this._token = null;
			this._channel = Token.DEFAULT_CHANNEL;
			this._tokenStartCharIndex = this._input.index;
			this._tokenStartColumn = this._interp.column;
			this._tokenStartLine = this._interp.line;
			this._text = null;
			var continueOuter = false;
			while (true) {
				this._type = Token.INVALID_TYPE;
				var ttype = Lexer.SKIP;
				try {
					ttype = this._interp.match(this._input, this._mode);
				} catch (e) {
					this.notifyListeners(e); // report error
					this.recover(e);
				}
				if (this._input.LA(1) === Token.EOF) {
					this._hitEOF = true;
				}
				if (this._type === Token.INVALID_TYPE) {
					this._type = ttype;
				}
				if (this._type === Lexer.SKIP) {
					continueOuter = true;
					break;
				}
				if (this._type !== Lexer.MORE) {
					break;
				}
			}
			if (continueOuter) {
				continue;
			}
			if (this._token === null) {
				this.emit();
			}
			return this._token;
		}
	} finally {
		// make sure we release marker after match or
		// unbuffered char stream will keep buffering
		this._input.release(tokenStartMarker);
	}
};

// Instruct the lexer to skip creating a token for current lexer rule
// and look for another token. nextToken() knows to keep looking when
// a lexer rule finishes with token set to SKIP_TOKEN. Recall that
// if token==null at end of any token rule, it creates one for you
// and emits it.
// /
Lexer.prototype.skip = function() {
	this._type = Lexer.SKIP;
};

Lexer.prototype.more = function() {
	this._type = Lexer.MORE;
};

Lexer.prototype.mode = function(m) {
	this._mode = m;
};

Lexer.prototype.pushMode = function(m) {
	if (this._interp.debug) {
		console.log("pushMode " + m);
	}
	this._modeStack.push(this._mode);
	this.mode(m);
};

Lexer.prototype.popMode = function() {
	if (this._modeStack.length === 0) {
		throw "Empty Stack";
	}
	if (this._interp.debug) {
		console.log("popMode back to " + this._modeStack.slice(0, -1));
	}
	this.mode(this._modeStack.pop());
	return this._mode;
};

// Set the char stream and reset the lexer
Object.defineProperty(Lexer.prototype, "inputStream", {
	get : function() {
		return this._input;
	},
	set : function(input) {
		this._input = null;
		this._tokenFactorySourcePair = [ this, this._input ];
		this.reset();
		this._input = input;
		this._tokenFactorySourcePair = [ this, this._input ];
	}
});

Object.defineProperty(Lexer.prototype, "sourceName", {
	get : function sourceName() {
		return this._input.sourceName;
	}
});

// By default does not support multiple emits per nextToken invocation
// for efficiency reasons. Subclass and override this method, nextToken,
// and getToken (to push tokens into a list and pull from that list
// rather than a single variable as this implementation does).
// /
Lexer.prototype.emitToken = function(token) {
	this._token = token;
};

// The standard method called to automatically emit a token at the
// outermost lexical rule. The token object should point into the
// char buffer start..stop. If there is a text override in 'text',
// use that to set the token's text. Override this method to emit
// custom Token objects or provide a new factory.
// /
Lexer.prototype.emit = function() {
	var t = this._factory.create(this._tokenFactorySourcePair, this._type,
			this._text, this._channel, this._tokenStartCharIndex, this
					.getCharIndex() - 1, this._tokenStartLine,
			this._tokenStartColumn);
	this.emitToken(t);
	return t;
};

Lexer.prototype.emitEOF = function() {
	var cpos = this.column;
	var lpos = this.line;
	var eof = this._factory.create(this._tokenFactorySourcePair, Token.EOF,
			null, Token.DEFAULT_CHANNEL, this._input.index,
			this._input.index - 1, lpos, cpos);
	this.emitToken(eof);
	return eof;
};

Object.defineProperty(Lexer.prototype, "type", {
	get : function() {
		return this.type;
	},
	set : function(type) {
		this._type = type;
	}
});

Object.defineProperty(Lexer.prototype, "line", {
	get : function() {
		return this._interp.line;
	},
	set : function(line) {
		this._interp.line = line;
	}
});

Object.defineProperty(Lexer.prototype, "column", {
	get : function() {
		return this._interp.column;
	},
	set : function(column) {
		this._interp.column = column;
	}
});


// What is the index of the current character of lookahead?///
Lexer.prototype.getCharIndex = function() {
	return this._input.index;
};

// Return the text matched so far for the current token or any text override.
//Set the complete text of this token; it wipes any previous changes to the text.
Object.defineProperty(Lexer.prototype, "text", {
	get : function() {
		if (this._text !== null) {
			return this._text;
		} else {
			return this._interp.getText(this._input);
		}
	},
	set : function(text) {
		this._text = text;
	}
});
// Return a list of all Token objects in input char stream.
// Forces load of all tokens. Does not include EOF token.
// /
Lexer.prototype.getAllTokens = function() {
	var tokens = [];
	var t = this.nextToken();
	while (t.type !== Token.EOF) {
		tokens.push(t);
		t = this.nextToken();
	}
	return tokens;
};

Lexer.prototype.notifyListeners = function(e) {
	var start = this._tokenStartCharIndex;
	var stop = this._input.index;
	var text = this._input.getText(start, stop);
	var msg = "token recognition error at: '" + this.getErrorDisplay(text) + "'";
	var listener = this.getErrorListenerDispatch();
	listener.syntaxError(this, null, this._tokenStartLine,
			this._tokenStartColumn, msg, e);
};

Lexer.prototype.getErrorDisplay = function(s) {
	var d = [];
	for (var i = 0; i < s.length; i++) {
		d.push(s[i]);
	}
	return d.join('');
};

Lexer.prototype.getErrorDisplayForChar = function(c) {
	if (c.charCodeAt(0) === Token.EOF) {
		return "<EOF>";
	} else if (c === '\n') {
		return "\\n";
	} else if (c === '\t') {
		return "\\t";
	} else if (c === '\r') {
		return "\\r";
	} else {
		return c;
	}
};

Lexer.prototype.getCharErrorDisplay = function(c) {
	return "'" + this.getErrorDisplayForChar(c) + "'";
};

// Lexers can normally match any char in it's vocabulary after matching
// a token, so do the easy thing and just kill a character and hope
// it all works out. You can instead use the rule invocation stack
// to do sophisticated error recovery if you are in a fragment rule.
// /
Lexer.prototype.recover = function(re) {
	if (this._input.LA(1) !== Token.EOF) {
		if (re instanceof LexerNoViableAltException) {
			// skip a char and try again
			this._interp.consume(this._input);
		} else {
			// TODO: Do we lose character or line position information?
			this._input.consume();
		}
	}
};

exports.Lexer = Lexer;
