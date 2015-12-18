package antlr4

import (
    "fmt"
    "strings"
            )

type Recognizer struct {
    _listeners []ParseTreeListener
    _interp *ATNSimulator
    state int
}

func NewRecognizer() *Recognizer {
    rec := new(Recognizer)
    rec.initRecognizer()
    return rec
}

func (rec *Recognizer) initRecognizer() {
    rec._listeners = []ParseTreeListener{ ConsoleErrorListenerINSTANCE }
    rec._interp = nil
    rec.state = -1
}

var tokenTypeMapCache = make(map[[]string]int)
var ruleIndexMapCache = make(map[[]string]int)

func (this *Recognizer) checkVersion(toolVersion string) {
    var runtimeVersion = "4.5.2"
    if (runtimeVersion!=toolVersion) {
        fmt.Println("ANTLR runtime and generated code versions disagree: "+runtimeVersion+"!="+toolVersion)
    }
}

func (this *Recognizer) addErrorListener(listener *ParseTreeListener) {
    append(this._listeners, listener)
}

func (this *Recognizer) removeErrorListeners() {
    this._listeners = make([]ParseTreeListener, 0)
}

func (this *Recognizer) getRuleNames() []string {
    return nil
}

func (this *Recognizer) getTokenNames() []string {
    return nil
}

func (this *Recognizer) getATN() *ATN {
    return this._interp.atn
}

//func (this *Recognizer) getTokenTypeMap() {
//    var tokenNames = this.getTokenNames()
//    if (tokenNames==nil) {
//        panic("The current recognizer does not provide a list of token names.")
//    }
//    var result = tokenTypeMapCache[tokenNames]
//    if(result==nil) {
//        result = tokenNames.reduce(function(o, k, i) { o[k] = i })
//        result.EOF = TokenEOF
//        tokenTypeMapCache[tokenNames] = result
//    }
//    return result
//}

// Get a map from rule names to rule indexes.
//
// <p>Used for XPath and tree pattern compilation.</p>
//
func (this *Recognizer) getRuleIndexMap() {
    panic("Method not defined!")
//    var ruleNames = this.getRuleNames()
//    if (ruleNames==nil) {
//        panic("The current recognizer does not provide a list of rule names.")
//    }
//
//    var result = ruleIndexMapCache[ruleNames]
//    if(result==nil) {
//        result = ruleNames.reduce(function(o, k, i) { o[k] = i })
//        ruleIndexMapCache[ruleNames] = result
//    }
//    return result
}

func (this *Recognizer) getTokenType(tokenName string) int {
    panic("Method not defined!")
//    var ttype = this.getTokenTypeMap()[tokenName]
//    if (ttype !=nil) {
//        return ttype
//    } else {
//        return TokenInvalidType
//    }
}

//func (this *Recognizer) getTokenTypeMap() map[string]int {
//    Vocabulary vocabulary = getVocabulary();
//
//    synchronized (tokenTypeMapCache) {
//        Map<String, Integer> result = tokenTypeMapCache.get(vocabulary);
//        if (result == null) {
//            result = new HashMap<String, Integer>();
//            for (int i = 0; i < getATN().maxTokenType; i++) {
//                String literalName = vocabulary.getLiteralName(i);
//                if (literalName != null) {
//                    result.put(literalName, i);
//                }
//
//                String symbolicName = vocabulary.getSymbolicName(i);
//                if (symbolicName != null) {
//                    result.put(symbolicName, i);
//                }
//            }
//
//            result.put("EOF", Token.EOF);
//            result = Collections.unmodifiableMap(result);
//            tokenTypeMapCache.put(vocabulary, result);
//        }
//
//        return result;
//    }
//}

// What is the error header, normally line/character position information?//
func (this *Recognizer) getErrorHeader(e error) string {
    panic("Method not defined!")
//    var line = e.getOffendingToken().line
//    var column = e.getOffendingToken().column
//    return "line " + line + ":" + column
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
    var s = t.text()
    if s==nil {
        if (t.tokenType==TokenEOF) {
            s = "<EOF>"
        } else {
            s = "<" + t.tokenType + ">"
        }
    }
    s = strings.Replace(s,"\t","\\t", -1)
    s = strings.Replace(s,"\n","\\n", -1)
    s = strings.Replace(s,"\r","\\r", -1)

    return "'" + s + "'"
}

func (this *Recognizer) getErrorListenerDispatch() *ErrorListener {
    return NewProxyErrorListener(this._listeners)
}

// subclass needs to override these if there are sempreds or actions
// that the ATN interp needs to execute
func (this *Recognizer) sempred(localctx *RuleContext, ruleIndex int, actionIndex int) {
    return true
}

func (this *Recognizer) precpred(localctx *RuleContext, precedence int) bool {
    return true
}

//Indicate that the recognizer has changed internal state that is
//consistent with the ATN state passed in.  This way we always know
//where we are in the ATN as the parser goes along. The rule
//context objects form a stack that lets us see the stack of
//invoking rules. Combine this and we have complete ATN
//configuration information.


