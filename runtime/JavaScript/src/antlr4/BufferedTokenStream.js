//
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
//  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
//  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
//  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
//  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
//  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
//  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
//  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
//  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

// This implementation of {@link TokenStream} loads tokens from a
// {@link TokenSource} on-demand, and places the tokens in a buffer to provide
// access to any previous token by index.
//
// <p>
// This token stream ignores the value of {@link Token//getChannel}. If your
// parser requires the token stream filter tokens to only those on a particular
// channel, such as {@link Token//DEFAULT_CHANNEL} or
// {@link Token//HIDDEN_CHANNEL}, use a filtering token stream such a
// {@link CommonTokenStream}.</p>

var Token = require('./Token').Token;
var Lexer = require('./Lexer').Lexer;
var Interval = require('./IntervalSet').Interval;

// this is just to keep meaningful parameter types to Parser
function TokenStream() {
	return this;
}

function BufferedTokenStream(tokenSource) {

	TokenStream.call(this);
	// The {@link TokenSource} from which tokens for this stream are fetched.
	this.tokenSource = tokenSource;

	// A collection of all tokens fetched from the token source. The list is
	// considered a complete view of the input once {@link //fetchedEOF} is set
	// to {@code true}.
	this.tokens = [];

	// The index into {@link //tokens} of the current token (next token to
	// {@link //consume}). {@link //tokens}{@code [}{@link //p}{@code ]} should
	// be
	// {@link //LT LT(1)}.
	//
	// <p>This field is set to -1 when the stream is first constructed or when
	// {@link //setTokenSource} is called, indicating that the first token has
	// not yet been fetched from the token source. For additional information,
	// see the documentation of {@link IntStream} for a description of
	// Initializing Methods.</p>
	this.index = -1;

	// Indicates whether the {@link Token//EOF} token has been fetched from
	// {@link //tokenSource} and added to {@link //tokens}. This field improves
	// performance for the following cases:
	//
	// <ul>
	// <li>{@link //consume}: The lookahead check in {@link //consume} to
	// prevent
	// consuming the EOF symbol is optimized by checking the values of
	// {@link //fetchedEOF} and {@link //p} instead of calling {@link
	// //LA}.</li>
	// <li>{@link //fetch}: The check to prevent adding multiple EOF symbols
	// into
	// {@link //tokens} is trivial with this field.</li>
	// <ul>
	this.fetchedEOF = false;
	return this;
}

BufferedTokenStream.prototype = Object.create(TokenStream.prototype);
BufferedTokenStream.prototype.constructor = BufferedTokenStream;

BufferedTokenStream.prototype.mark = function() {
	return 0;
};

BufferedTokenStream.prototype.release = function(marker) {
	// no resources to release
};

BufferedTokenStream.prototype.reset = function() {
	this.seek(0);
};

BufferedTokenStream.prototype.seek = function(index) {
	this.lazyInit();
	this.index = this.adjustSeekIndex(index);
};

BufferedTokenStream.prototype.get = function(index) {
	this.lazyInit();
	return this.tokens[index];
};

BufferedTokenStream.prototype.consume = function() {
	var skipEofCheck = false;
	if (this.index >= 0) {
		if (this.fetchedEOF) {
			// the last token in tokens is EOF. skip check if p indexes any
			// fetched token except the last.
			skipEofCheck = this.index < this.tokens.length - 1;
		} else {
			// no EOF token in tokens. skip check if p indexes a fetched token.
			skipEofCheck = this.index < this.tokens.length;
		}
	} else {
		// not yet initialized
		skipEofCheck = false;
	}
	if (!skipEofCheck && this.LA(1) === Token.EOF) {
		throw "cannot consume EOF";
	}
	if (this.sync(this.index + 1)) {
		this.index = this.adjustSeekIndex(this.index + 1);
	}
};

// Make sure index {@code i} in tokens has a token.
//
// @return {@code true} if a token is located at index {@code i}, otherwise
// {@code false}.
// @see //get(int i)
// /
BufferedTokenStream.prototype.sync = function(i) {
	var n = i - this.tokens.length + 1; // how many more elements we need?
	if (n > 0) {
		var fetched = this.fetch(n);
		return fetched >= n;
	}
	return true;
};

// Add {@code n} elements to buffer.
//
// @return The actual number of elements added to the buffer.
// /
BufferedTokenStream.prototype.fetch = function(n) {
	if (this.fetchedEOF) {
		return 0;
	}
	for (var i = 0; i < n; i++) {
		var t = this.tokenSource.nextToken();
		t.tokenIndex = this.tokens.length;
		this.tokens.push(t);
		if (t.type === Token.EOF) {
			this.fetchedEOF = true;
			return i + 1;
		}
	}
	return n;
};

// Get all tokens from start..stop inclusively///
BufferedTokenStream.prototype.getTokens = function(start, stop, types) {
	if (types === undefined) {
		types = null;
	}
	if (start < 0 || stop < 0) {
		return null;
	}
	this.lazyInit();
	var subset = [];
	if (stop >= this.tokens.length) {
		stop = this.tokens.length - 1;
	}
	for (var i = start; i < stop; i++) {
		var t = this.tokens[i];
		if (t.type === Token.EOF) {
			break;
		}
		if (types === null || types.contains(t.type)) {
			subset.push(t);
		}
	}
	return subset;
};

BufferedTokenStream.prototype.LA = function(i) {
	return this.LT(i).type;
};

BufferedTokenStream.prototype.LB = function(k) {
	if (this.index - k < 0) {
		return null;
	}
	return this.tokens[this.index - k];
};

BufferedTokenStream.prototype.LT = function(k) {
	this.lazyInit();
	if (k === 0) {
		return null;
	}
	if (k < 0) {
		return this.LB(-k);
	}
	var i = this.index + k - 1;
	this.sync(i);
	if (i >= this.tokens.length) { // return EOF token
		// EOF must be last token
		return this.tokens[this.tokens.length - 1];
	}
	return this.tokens[i];
};

// Allowed derived classes to modify the behavior of operations which change
// the current stream position by adjusting the target token index of a seek
// operation. The default implementation simply returns {@code i}. If an
// exception is thrown in this method, the current stream index should not be
// changed.
//
// <p>For example, {@link CommonTokenStream} overrides this method to ensure
// that
// the seek target is always an on-channel token.</p>
//
// @param i The target token index.
// @return The adjusted target token index.

BufferedTokenStream.prototype.adjustSeekIndex = function(i) {
	return i;
};

BufferedTokenStream.prototype.lazyInit = function() {
	if (this.index === -1) {
		this.setup();
	}
};

BufferedTokenStream.prototype.setup = function() {
	this.sync(0);
	this.index = this.adjustSeekIndex(0);
};

// Reset this token stream by setting its token source.///
BufferedTokenStream.prototype.setTokenSource = function(tokenSource) {
	this.tokenSource = tokenSource;
	this.tokens = [];
	this.index = -1;
};

// Given a starting index, return the index of the next token on channel.
// Return i if tokens[i] is on channel. Return -1 if there are no tokens
// on channel between i and EOF.
// /
BufferedTokenStream.prototype.nextTokenOnChannel = function(i, channel) {
	this.sync(i);
	if (i >= this.tokens.length) {
		return -1;
	}
	var token = this.tokens[i];
	while (token.channel !== this.channel) {
		if (token.type === Token.EOF) {
			return -1;
		}
		i += 1;
		this.sync(i);
		token = this.tokens[i];
	}
	return i;
};

// Given a starting index, return the index of the previous token on channel.
// Return i if tokens[i] is on channel. Return -1 if there are no tokens
// on channel between i and 0.
BufferedTokenStream.prototype.previousTokenOnChannel = function(i, channel) {
	while (i >= 0 && this.tokens[i].channel !== channel) {
		i -= 1;
	}
	return i;
};

// Collect all tokens on specified channel to the right of
// the current token up until we see a token on DEFAULT_TOKEN_CHANNEL or
// EOF. If channel is -1, find any non default channel token.
BufferedTokenStream.prototype.getHiddenTokensToRight = function(tokenIndex,
		channel) {
	if (channel === undefined) {
		channel = -1;
	}
	this.lazyInit();
	if (this.tokenIndex < 0 || tokenIndex >= this.tokens.length) {
		throw "" + tokenIndex + " not in 0.." + this.tokens.length - 1;
	}
	var nextOnChannel = this.nextTokenOnChannel(tokenIndex + 1,
			Lexer.DEFAULT_TOKEN_CHANNEL);
	var from_ = tokenIndex + 1;
	// if none onchannel to right, nextOnChannel=-1 so set to = last token
	var to = nextOnChannel === -1 ? this.tokens.length - 1 : nextOnChannel;
	return this.filterForChannel(from_, to, channel);
};

// Collect all tokens on specified channel to the left of
// the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
// If channel is -1, find any non default channel token.
BufferedTokenStream.prototype.getHiddenTokensToLeft = function(tokenIndex,
		channel) {
	if (channel === undefined) {
		channel = -1;
	}
	this.lazyInit();
	if (tokenIndex < 0 || tokenIndex >= this.tokens.length) {
		throw "" + tokenIndex + " not in 0.." + this.tokens.length - 1;
	}
	var prevOnChannel = this.previousTokenOnChannel(tokenIndex - 1,
			Lexer.DEFAULT_TOKEN_CHANNEL);
	if (prevOnChannel === tokenIndex - 1) {
		return null;
	}
	// if none on channel to left, prevOnChannel=-1 then from=0
	var from_ = prevOnChannel + 1;
	var to = tokenIndex - 1;
	return this.filterForChannel(from_, to, channel);
};

BufferedTokenStream.prototype.filterForChannel = function(left, right, channel) {
	var hidden = [];
	for (var i = left; i < right + 1; i++) {
		var t = this.tokens[i];
		if (channel === -1) {
			if (t.channel !== Lexer.DEFAULT_TOKEN_CHANNEL) {
				hidden.push(t);
			}
		} else if (t.channel === channel) {
			hidden.push(t);
		}
	}
	if (hidden.length === 0) {
		return null;
	}
	return hidden;
};

BufferedTokenStream.prototype.getSourceName = function() {
	return this.tokenSource.getSourceName();
};

// Get the text of all tokens in this buffer.///
BufferedTokenStream.prototype.getText = function(interval) {
	this.lazyInit();
	this.fill();
	if (interval === undefined || interval === null) {
		interval = new Interval(0, this.tokens.length - 1);
	}
	var start = interval.start;
	if (start instanceof Token) {
		start = start.tokenIndex;
	}
	var stop = interval.stop;
	if (stop instanceof Token) {
		stop = stop.tokenIndex;
	}
	if (start === null || stop === null || start < 0 || stop < 0) {
		return "";
	}
	if (stop >= this.tokens.length) {
		stop = this.tokens.length - 1;
	}
	var s = "";
	for (var i = start; i < stop + 1; i++) {
		var t = this.tokens[i];
		if (t.type === Token.EOF) {
			break;
		}
		s = s + t.text;
	}
	return s;
};

// Get all tokens from lexer until EOF///
BufferedTokenStream.prototype.fill = function() {
	this.lazyInit();
	while (this.fetch(1000) === 1000) {
		continue;
	}
};

exports.BufferedTokenStream = BufferedTokenStream;
