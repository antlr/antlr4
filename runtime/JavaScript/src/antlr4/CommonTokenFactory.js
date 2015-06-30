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
//

//
// This default implementation of {@link TokenFactory} creates
// {@link CommonToken} objects.
//

var CommonToken = require('./Token').CommonToken;

function TokenFactory() {
	return this;
}

function CommonTokenFactory(copyText) {
	TokenFactory.call(this);
    // Indicates whether {@link CommonToken//setText} should be called after
    // constructing tokens to explicitly set the text. This is useful for cases
    // where the input stream might not be able to provide arbitrary substrings
    // of text from the input after the lexer creates a token (e.g. the
    // implementation of {@link CharStream//getText} in
    // {@link UnbufferedCharStream} throws an
    // {@link UnsupportedOperationException}). Explicitly setting the token text
    // allows {@link Token//getText} to be called at any time regardless of the
    // input stream implementation.
    //
    // <p>
    // The default value is {@code false} to avoid the performance and memory
    // overhead of copying text for every token unless explicitly requested.</p>
    //
    this.copyText = copyText===undefined ? false : copyText;
	return this;
}

CommonTokenFactory.prototype = Object.create(TokenFactory.prototype);
CommonTokenFactory.prototype.constructor = CommonTokenFactory;

//
// The default {@link CommonTokenFactory} instance.
//
// <p>
// This token factory does not explicitly copy token text when constructing
// tokens.</p>
//
CommonTokenFactory.DEFAULT = new CommonTokenFactory();

CommonTokenFactory.prototype.create = function(source, type, text, channel, start, stop, line, column) {
    var t = new CommonToken(source, type, channel, start, stop);
    t.line = line;
    t.column = column;
    if (text !==null) {
        t.text = text;
    } else if (this.copyText && source[1] !==null) {
        t.text = source[1].getText(start,stop);
    }
    return t;
};

CommonTokenFactory.prototype.createThin = function(type, text) {
    var t = new CommonToken(null, type);
    t.text = text;
    return t;
};

exports.CommonTokenFactory = CommonTokenFactory;
