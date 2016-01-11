// Generated from T.g4 by ANTLR 4.5.1
// jshint ignore: start
var antlr4 = require('antlr4/index');


var serializedATN = ["\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd",
    "\u0002\u0007\u001f\b\u0001\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004",
    "\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0003\u0002\u0003",
    "\u0002\u0003\u0003\u0003\u0003\u0003\u0004\u0006\u0004\u0013\n\u0004",
    "\r\u0004\u000e\u0004\u0014\u0003\u0005\u0006\u0005\u0018\n\u0005\r\u0005",
    "\u000e\u0005\u0019\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0002",
    "\u0002\u0007\u0003\u0003\u0005\u0004\u0007\u0005\t\u0006\u000b\u0007",
    "\u0003\u0002\u0003\u0004\u0002\f\f\"\" \u0002\u0003\u0003\u0002\u0002",
    "\u0002\u0002\u0005\u0003\u0002\u0002\u0002\u0002\u0007\u0003\u0002\u0002",
    "\u0002\u0002\t\u0003\u0002\u0002\u0002\u0002\u000b\u0003\u0002\u0002",
    "\u0002\u0003\r\u0003\u0002\u0002\u0002\u0005\u000f\u0003\u0002\u0002",
    "\u0002\u0007\u0012\u0003\u0002\u0002\u0002\t\u0017\u0003\u0002\u0002",
    "\u0002\u000b\u001b\u0003\u0002\u0002\u0002\r\u000e\u0007?\u0002\u0002",
    "\u000e\u0004\u0003\u0002\u0002\u0002\u000f\u0010\u0007-\u0002\u0002",
    "\u0010\u0006\u0003\u0002\u0002\u0002\u0011\u0013\u0004c|\u0002\u0012",
    "\u0011\u0003\u0002\u0002\u0002\u0013\u0014\u0003\u0002\u0002\u0002\u0014",
    "\u0012\u0003\u0002\u0002\u0002\u0014\u0015\u0003\u0002\u0002\u0002\u0015",
    "\b\u0003\u0002\u0002\u0002\u0016\u0018\u00042;\u0002\u0017\u0016\u0003",
    "\u0002\u0002\u0002\u0018\u0019\u0003\u0002\u0002\u0002\u0019\u0017\u0003",
    "\u0002\u0002\u0002\u0019\u001a\u0003\u0002\u0002\u0002\u001a\n\u0003",
    "\u0002\u0002\u0002\u001b\u001c\t\u0002\u0002\u0002\u001c\u001d\u0003",
    "\u0002\u0002\u0002\u001d\u001e\b\u0006\u0002\u0002\u001e\f\u0003\u0002",
    "\u0002\u0002\u0005\u0002\u0014\u0019\u0003\b\u0002\u0002"].join("");


var atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

var decisionsToDFA = atn.decisionToState.map( function(ds, index) { return new antlr4.dfa.DFA(ds, index); });

function TLexer(input) {
	antlr4.Lexer.call(this, input);
    this._interp = new antlr4.atn.LexerATNSimulator(this, atn, decisionsToDFA, new antlr4.PredictionContextCache());
    return this;
}

TLexer.prototype = Object.create(antlr4.Lexer.prototype);
TLexer.prototype.constructor = TLexer;

TLexer.EOF = antlr4.Token.EOF;
TLexer.T__0 = 1;
TLexer.T__1 = 2;
TLexer.ID = 3;
TLexer.INT = 4;
TLexer.WS = 5;


TLexer.prototype.modeNames = [ "DEFAULT_MODE" ];

TLexer.prototype.literalNames = [ null, "'='", "'+'" ];

TLexer.prototype.symbolicNames = [ null, null, null, "ID", "INT", "WS" ];

TLexer.prototype.ruleNames = [ "T__0", "T__1", "ID", "INT", "WS" ];

TLexer.prototype.grammarFileName = "T.g4";



exports.TLexer = TLexer;

