package antlr4

import (
    "fmt"
    "antlr4/tree"
    "antlr4/error"
)

//var Token = require('./Token').Token
//var ConsoleErrorListener = require('./error/ErrorListener').ConsoleErrorListener
//var ProxyErrorListener = require('./error/ErrorListener').ProxyErrorListener

type Recognizer struct {
    _listeners []tree.ParseTreeListener
    _interp *Parser
    state int
}

func NewRecognizer() *Recognizer {
    rec := new(Recognizer)
    rec._listeners = []tree.ParseTreeListener{ error.ConsoleErrorListenerINSTANCE }
    rec._interp = nil
    rec.state = -1
    return rec
}

var tokenTypeMapCache = make(map[string]int)
var ruleIndexMapCache = make(map[string]int)

func (this *Recognizer) checkVersion(toolVersion string) {
    var runtimeVersion = "4.5.1"
    if (runtimeVersion!=toolVersion) {
        fmt.Println("ANTLR runtime and generated code versions disagree: "+runtimeVersion+"!="+toolVersion)
    }
}

func (this *Recognizer) addErrorListener(listener *tree.ParseTreeListener) {
    append(this._listeners, listener)
}

func (this *Recognizer) removeErrorListeners() {
    this._listeners = make([]tree.ParseTreeListener, 1)
}

func (this *Recognizer) getTokenTypeMap() {
    var tokenNames = this.getTokenNames()
    if (tokenNames==nil) {
        panic("The current recognizer does not provide a list of token names.")
    }
    var result = tokenTypeMapCache[tokenNames]
    if(result==undefined) {
        result = tokenNames.reduce(function(o, k, i) { o[k] = i })
        result.EOF = TokenEOF
        tokenTypeMapCache[tokenNames] = result
    }
    return result
}

// Get a map from rule names to rule indexes.
//
// <p>Used for XPath and tree pattern compilation.</p>
//
func (this *Recognizer) getRuleIndexMap() {
    var ruleNames = this.getRuleNames()
    if (ruleNames==nil) {
        panic("The current recognizer does not provide a list of rule names.")
    }
    var result = ruleIndexMapCache[ruleNames]
    if(result==undefined) {
        result = ruleNames.reduce(function(o, k, i) { o[k] = i })
        ruleIndexMapCache[ruleNames] = result
    }
    return result
}

func (this *Recognizer) getTokenType(tokenName string) int {
    var ttype = this.getTokenTypeMap()[tokenName]
    if (ttype !=undefined) {
        return ttype
    } else {
        return TokenInvalidType
    }
}


// What is the error header, normally line/character position information?//
func (this *Recognizer) getErrorHeader(e) {
    var line = e.getOffendingToken().line
    var column = e.getOffendingToken().column
    return "line " + line + ":" + column
}


// How should a token be displayed in an error message? The default
//  is to display just the text, but during development you might
//  want to have a lot of information spit out.  Override in that case
//  to use t.toString() (which, for CommonToken, dumps everything about
//  the token). This is better than forcing you to override a method in
//  your token objects because you don't have to go modify your lexer
//  so that it creates a NewJava type.
//
// @deprecated This method is not called by the ANTLR 4 Runtime. Specific
// implementations of {@link ANTLRErrorStrategy} may provide a similar
// feature when necessary. For example, see
// {@link DefaultErrorStrategy//getTokenErrorDisplay}.
//
func (this *Recognizer) getTokenErrorDisplay(t *Token) string {
    if (t==nil) {
        return "<no token>"
    }
    var s = t.text
    if s==nil {
        if (t.tokenType==TokenEOF) {
            s = "<EOF>"
        } else {
            s = "<" + t.tokenType + ">"
        }
    }
    s = s.replace("\n","\\n").replace("\r","\\r").replace("\t","\\t")
    return "'" + s + "'"
}

func (this *Recognizer) getErrorListenerDispatch() {
    return error.NewProxyErrorListener(this._listeners)
}

// subclass needs to override these if there are sempreds or actions
// that the ATN interp needs to execute
func (this *Recognizer) sempred(localctx *RuleContext, ruleIndex int, actionIndex int) {
    return true
}

func (this *Recognizer) precpred(localctx *RuleContext, precedence) {
    return true
}

//Indicate that the recognizer has changed internal state that is
//consistent with the ATN state passed in.  This way we always know
//where we are in the ATN as the parser goes along. The rule
//context objects form a stack that lets us see the stack of
//invoking rules. Combine this and we have complete ATN
//configuration information.

//Object.defineProperty(Recognizer.prototype, "state", {
//	get : function() {
//		return this._stateNumber
//	},
//	set : function(state) {
//		this._stateNumber = state
//	}
//})



