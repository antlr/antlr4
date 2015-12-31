package antlr4

import (
	"fmt"
	"strings"

	"strconv"
)

type IRecognizer interface {

	GetLiteralNames() []string
	GetSymbolicNames() []string
	GetRuleNames() []string
	Sempred(localctx IRuleContext, ruleIndex int, actionIndex int) bool
	Precpred(localctx IRuleContext, precedence int) bool

	GetState() int
	SetState(int)
	Action(_localctx IRuleContext, ruleIndex, actionIndex int)
	GetATN() *ATN
	getErrorListenerDispatch() IErrorListener

}

type Recognizer struct {
	_listeners []IErrorListener
	state      int

	RuleNames  []string
	LiteralNames  []string
	SymbolicNames []string
	GrammarFileName string
}

func NewRecognizer() *Recognizer {
	rec := new(Recognizer)
	rec._listeners = []IErrorListener{ConsoleErrorListenerINSTANCE}
	rec.state = -1
	return rec
}

var tokenTypeMapCache = make(map[string]int)
var ruleIndexMapCache = make(map[string]int)

func (this *Recognizer) checkVersion(toolVersion string) {
	var runtimeVersion = "4.5.2"
	if runtimeVersion != toolVersion {
		fmt.Println("ANTLR runtime and generated code versions disagree: " + runtimeVersion + "!=" + toolVersion)
	}
}

func (this *Recognizer) Action(context IRuleContext, ruleIndex, actionIndex int) {
	panic("action not implemented on Recognizer!")
}

func (this *Recognizer) addErrorListener(listener IErrorListener) {
	this._listeners = append(this._listeners, listener)
}

func (this *Recognizer) removeErrorListeners() {
	this._listeners = make([]IErrorListener, 0)
}

func (this *Recognizer) GetRuleNames() []string {
	return this.RuleNames
}

func (this *Recognizer) GetTokenNames() []string {
	return this.LiteralNames
}

func (this *Recognizer) GetSymbolicNames() []string {
	return this.LiteralNames
}

func (this *Recognizer) GetLiteralNames() []string {
	return this.LiteralNames
}

func (this *Recognizer) GetState() int {
	return this.state
}

func (this *Recognizer) SetState(v int) {
	if PortDebug {
		fmt.Println("SETTING STATE " + strconv.Itoa(v) + " from " +  strconv.Itoa(this.state))
	}

	this.state = v
}

//func (this *Recognizer) GetTokenTypeMap() {
//    var tokenNames = this.GetTokenNames()
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
func (this *Recognizer) getRuleIndexMap() map[string]int {
	panic("Method not defined!")
	//    var ruleNames = this.GetRuleNames()
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

func (this *Recognizer) GetTokenType(tokenName string) int {
	panic("Method not defined!")
	//    var ttype = this.GetTokenTypeMap()[tokenName]
	//    if (ttype !=nil) {
	//        return ttype
	//    } else {
	//        return TokenInvalidType
	//    }
}

//func (this *Recognizer) GetTokenTypeMap() map[string]int {
//    Vocabulary vocabulary = getVocabulary();
//
//    Synchronized (tokenTypeMapCache) {
//        Map<String, Integer> result = tokenTypeMapCache.Get(vocabulary);
//        if (result == null) {
//            result = new HashMap<String, Integer>();
//            for (int i = 0; i < GetATN().maxTokenType; i++) {
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
func (this *Recognizer) getErrorHeader(e IRecognitionException) string {
	var line = e.GetOffendingToken().GetLine()
	var column = e.GetOffendingToken().GetColumn()
	return "line " + strconv.Itoa(line) + ":" + strconv.Itoa(column)
}

// How should a token be displayed in an error message? The default
//  is to display just the text, but during development you might
//  want to have a lot of information spit out.  Override in that case
//  to use t.String() (which, for CommonToken, dumps everything about
//  the token). This is better than forcing you to override a method in
//  your token objects because you don't have to go modify your lexer
//  so that it creates a NewJava type.
//
// @deprecated This method is not called by the ANTLR 4 Runtime. Specific
// implementations of {@link ANTLRErrorStrategy} may provide a similar
// feature when necessary. For example, see
// {@link DefaultErrorStrategy//GetTokenErrorDisplay}.
//
func (this *Recognizer) GetTokenErrorDisplay(t IToken) string {
	if t == nil {
		return "<no token>"
	}
	var s = t.GetText()
	if s == "" {
		if t.GetTokenType() == TokenEOF {
			s = "<EOF>"
		} else {
			s = "<" + strconv.Itoa(t.GetTokenType()) + ">"
		}
	}
	s = strings.Replace(s, "\t", "\\t", -1)
	s = strings.Replace(s, "\n", "\\n", -1)
	s = strings.Replace(s, "\r", "\\r", -1)

	return "'" + s + "'"
}

func (this *Recognizer) getErrorListenerDispatch() IErrorListener {
	return NewProxyErrorListener(this._listeners)
}

// subclass needs to override these if there are sempreds or actions
// that the ATN interp needs to execute
func (this *Recognizer) Sempred(localctx IRuleContext, ruleIndex int, actionIndex int) bool {
	return true
}

func (this *Recognizer) Precpred(localctx IRuleContext, precedence int) bool {
	return true
}
