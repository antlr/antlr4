//
/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

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

