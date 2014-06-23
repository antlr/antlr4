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

// Provides an empty default implementation of {@link ANTLRErrorListener}. The
// default implementation of each method does nothing, but can be overridden as
// necessary.

function ErrorListener() {
	return this;
}

ErrorListener.prototype.syntaxError = function(recognizer, offendingSymbol, line, column, msg, e) {
};

ErrorListener.prototype.reportAmbiguity = function(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) {
};

ErrorListener.prototype.reportAttemptingFullContext = function(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
};

ErrorListener.prototype.reportContextSensitivity = function(recognizer, dfa, startIndex, stopIndex, prediction, configs) {
};

function ConsoleErrorListener() {
	ErrorListener.call(this);
	return this;
}

ConsoleErrorListener.prototype = Object.create(ErrorListener.prototype);
ConsoleErrorListener.prototype.constructor = ConsoleErrorListener;

//
// Provides a default instance of {@link ConsoleErrorListener}.
//
ConsoleErrorListener.INSTANCE = new ConsoleErrorListener();

//
// {@inheritDoc}
//
// <p>
// This implementation prints messages to {@link System//err} containing the
// values of {@code line}, {@code charPositionInLine}, and {@code msg} using
// the following format.</p>
//
// <pre>
// line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
// </pre>
//
ConsoleErrorListener.prototype.syntaxError = function(recognizer, offendingSymbol, line, column, msg, e) {
    console.error("line " + line + ":" + column + " " + msg);
};

function ProxyErrorListener(delegates) {
	ErrorListener.call(this);
    if (delegates===null) {
        throw "delegates";
    }
    this.delegates = delegates;
	return this;
}

ProxyErrorListener.prototype = Object.create(ErrorListener.prototype);
ProxyErrorListener.prototype.constructor = ProxyErrorListener;

ProxyErrorListener.prototype.syntaxError = function(recognizer, offendingSymbol, line, column, msg, e) {
    this.delegates.map(function(d) { d.syntaxError(recognizer, offendingSymbol, line, column, msg, e); });
};

ProxyErrorListener.prototype.reportAmbiguity = function(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) {
    this.delegates.map(function(d) { d.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs); });
};

ProxyErrorListener.prototype.reportAttemptingFullContext = function(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
	this.delegates.map(function(d) { d.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs); });
};

ProxyErrorListener.prototype.reportContextSensitivity = function(recognizer, dfa, startIndex, stopIndex, prediction, configs) {
	this.delegates.map(function(d) { d.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs); });
};

exports.ErrorListener = ErrorListener;
exports.ConsoleErrorListener = ConsoleErrorListener;
exports.ProxyErrorListener = ProxyErrorListener;

