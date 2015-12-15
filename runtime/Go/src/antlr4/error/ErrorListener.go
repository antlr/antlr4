package error

// Provides an empty default implementation of {@link ANTLRErrorListener}. The
// default implementation of each method does nothing, but can be overridden as
// necessary.

type ErrorListener struct {
	return this
}

func (this *ErrorListener) syntaxError(recognizer, offendingSymbol, line, column, msg, e) {
}

func (this *ErrorListener) reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) {
}

func (this *ErrorListener) reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
}

func (this *ErrorListener) reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs) {
}

type ConsoleErrorListener struct {
	ErrorListener.call(this)
	return this
}

ConsoleErrorListener.prototype = Object.create(ErrorListener.prototype)
ConsoleErrorListener.prototype.constructor = ConsoleErrorListener

//
// Provides a default instance of {@link ConsoleErrorListener}.
//
ConsoleErrorListener.INSTANCE = new ConsoleErrorListener()

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
func (this *ConsoleErrorListener) syntaxError(recognizer, offendingSymbol, line, column, msg, e) {
    console.error("line " + line + ":" + column + " " + msg)
}

func ProxyErrorListener(delegates) {
	ErrorListener.call(this)
    if (delegates==nil) {
        throw "delegates"
    }
    this.delegates = delegates
	return this
}

ProxyErrorListener.prototype = Object.create(ErrorListener.prototype)
ProxyErrorListener.prototype.constructor = ProxyErrorListener

func (this *ProxyErrorListener) syntaxError(recognizer, offendingSymbol, line, column, msg, e) {
    this.delegates.map(function(d) { d.syntaxError(recognizer, offendingSymbol, line, column, msg, e) })
}

func (this *ProxyErrorListener) reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) {
    this.delegates.map(function(d) { d.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) })
}

func (this *ProxyErrorListener) reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
	this.delegates.map(function(d) { d.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) })
}

func (this *ProxyErrorListener) reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs) {
	this.delegates.map(function(d) { d.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs) })
}





