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
///

//
// This class extends {@link BufferedTokenStream} with functionality to filter
// token streams to tokens on a particular channel (tokens where
// {@link Token//getChannel} returns a particular value).
//
// <p>
// This token stream provides access to all tokens by index or when calling
// methods like {@link //getText}. The channel filtering is only used for code
// accessing tokens via the lookahead methods {@link //LA}, {@link //LT}, and
// {@link //LB}.</p>
//
// <p>
// By default, tokens are placed on the default channel
// ({@link Token//DEFAULT_CHANNEL}), but may be reassigned by using the
// {@code ->channel(HIDDEN)} lexer command, or by using an embedded action to
// call {@link Lexer//setChannel}.
// </p>
//
// <p>
// Note: lexer rules which use the {@code ->skip} lexer command or call
// {@link Lexer//skip} do not produce tokens at all, so input text matched by
// such a rule will not be available as part of the token stream, regardless of
// channel.</p>
///

var Token = require('./Token').Token;
var BufferedTokenStream = require('./BufferedTokenStream').BufferedTokenStream;

function CommonTokenStream(lexer, channel) {
	BufferedTokenStream.call(this, lexer);
    this.channel = channel===undefined ? Token.DEFAULT_CHANNEL : channel;
    return this;
}

CommonTokenStream.prototype = Object.create(BufferedTokenStream.prototype);
CommonTokenStream.prototype.constructor = CommonTokenStream;

CommonTokenStream.prototype.adjustSeekIndex = function(i) {
    return this.nextTokenOnChannel(i, this.channel);
};

CommonTokenStream.prototype.LB = function(k) {
    if (k===0 || this.index-k<0) {
        return null;
    }
    var i = this.index;
    var n = 1;
    // find k good tokens looking backwards
    while (n <= k) {
        // skip off-channel tokens
        i = this.previousTokenOnChannel(i - 1, this.channel);
        n += 1;
    }
    if (i < 0) {
        return null;
    }
    return this.tokens[i];
};

CommonTokenStream.prototype.LT = function(k) {
    this.lazyInit();
    if (k === 0) {
        return null;
    }
    if (k < 0) {
        return this.LB(-k);
    }
    var i = this.index;
    var n = 1; // we know tokens[pos] is a good one
    // find k good tokens
    while (n < k) {
        // skip off-channel tokens, but make sure to not look past EOF
        if (this.sync(i + 1)) {
            i = this.nextTokenOnChannel(i + 1, this.channel);
        }
        n += 1;
    }
    return this.tokens[i];
};

// Count EOF just once.///
CommonTokenStream.prototype.getNumberOfOnChannelTokens = function() {
    var n = 0;
    this.fill();
    for (var i =0; i< this.tokens.length;i++) {
        var t = this.tokens[i];
        if( t.channel===this.channel) {
            n += 1;
        }
        if( t.type===Token.EOF) {
            break;
        }
    }
    return n;
};

exports.CommonTokenStream = CommonTokenStream;