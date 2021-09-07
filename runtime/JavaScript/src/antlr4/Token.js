/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

/**
 * A token has properties: text, type, line, character position in the line
 * (so we can ignore tabs), token channel, index, and source from which
 * we obtained this token.
 */
class Token {
	constructor() {
		this.source = null;
		this.type = null; // token type of the token
		this.channel = null; // The parser ignores everything not on DEFAULT_CHANNEL
		this.start = null; // optional; return -1 if not implemented.
		this.stop = null; // optional; return -1 if not implemented.
		this.tokenIndex = null; // from 0..n-1 of the token object in the input stream
		this.line = null; // line=1..n of the 1st character
		this.column = null; // beginning of the line at which it occurs, 0..n-1
		this._text = null; // text of the token.
	}

	getTokenSource() {
		return this.source[0];
	}

	getInputStream() {
		return this.source[1];
	}

	get text(){
		return this._text;
	}

	set text(text) {
		this._text = text;
	}
}

Token.INVALID_TYPE = 0;

/**
 * During lookahead operations, this "token" signifies we hit rule end ATN state
 * and did not follow it despite needing to.
 */
Token.EPSILON = -2;

Token.MIN_USER_TOKEN_TYPE = 1;

Token.EOF = -1;

/**
 * All tokens go to the parser (unless skip() is called in that rule)
 * on a particular "channel". The parser tunes to a particular channel
 * so that whitespace etc... can go to the parser on a "hidden" channel.
 */
Token.DEFAULT_CHANNEL = 0;

/**
 * Anything on different channel than DEFAULT_CHANNEL is not parsed
 * by parser.
 */
Token.HIDDEN_CHANNEL = 1;


class CommonToken extends Token {
	constructor(source, type, channel, start, stop) {
		super();
		this.source = source !== undefined ? source : CommonToken.EMPTY_SOURCE;
		this.type = type !== undefined ? type : null;
		this.channel = channel !== undefined ? channel : Token.DEFAULT_CHANNEL;
		this.start = start !== undefined ? start : -1;
		this.stop = stop !== undefined ? stop : -1;
		this.tokenIndex = -1;
		if (this.source[0] !== null) {
			this.line = source[0].line;
			this.column = source[0].column;
		} else {
			this.column = -1;
		}
	}

	/**
	 * Constructs a new {@link CommonToken} as a copy of another {@link Token}.
	 *
	 * <p>
	 * If {@code oldToken} is also a {@link CommonToken} instance, the newly
	 * constructed token will share a reference to the {@link //text} field and
	 * the {@link Pair} stored in {@link //source}. Otherwise, {@link //text} will
	 * be assigned the result of calling {@link //getText}, and {@link //source}
	 * will be constructed from the result of {@link Token//getTokenSource} and
	 * {@link Token//getInputStream}.</p>
	 *
	 * @param oldToken The token to copy.
	 */
	clone() {
		const t = new CommonToken(this.source, this.type, this.channel, this.start, this.stop);
		t.tokenIndex = this.tokenIndex;
		t.line = this.line;
		t.column = this.column;
		t.text = this.text;
		return t;
	}

	toString() {
		let txt = this.text;
		if (txt !== null) {
			txt = txt.replace(/\n/g, "\\n").replace(/\r/g, "\\r").replace(/\t/g, "\\t");
		} else {
			txt = "<no text>";
		}
		return "[@" + this.tokenIndex + "," + this.start + ":" + this.stop + "='" +
				txt + "',<" + this.type + ">" +
				(this.channel > 0 ? ",channel=" + this.channel : "") + "," +
				this.line + ":" + this.column + "]";
	}

	get text(){
		if (this._text !== null) {
			return this._text;
		}
		const input = this.getInputStream();
		if (input === null) {
			return null;
		}
		const n = input.size;
		if (this.start < n && this.stop < n) {
			return input.getText(this.start, this.stop);
		} else {
			return "<EOF>";
		}
	}

	set text(text) {
		this._text = text;
	}
}

/**
 * An empty {@link Pair} which is used as the default value of
 * {@link //source} for tokens that do not have a source.
 */
CommonToken.EMPTY_SOURCE = [ null, null ];

module.exports = {
	Token,
	CommonToken
}
