// Generated from Arithmetic.g4 by ANTLR 4.5.1
// jshint ignore: start
var antlr4 = require('./antlr4/index');


var serializedATN = ["\u0003\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd",
    "\u0002\u0011E\b\u0001\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004",
    "\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t",
    "\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004",
    "\f\t\f\u0004\r\t\r\u0004\u000e\t\u000e\u0004\u000f\t\u000f\u0004\u0010",
    "\t\u0010\u0003\u0002\u0003\u0002\u0003\u0003\u0003\u0003\u0003\u0004",
    "\u0003\u0004\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0007",
    "\u0003\u0007\u0003\b\u0003\b\u0003\t\u0003\t\u0003\n\u0003\n\u0003\u000b",
    "\u0003\u000b\u0003\f\u0003\f\u0003\r\u0003\r\u0003\u000e\u0005\u000e",
    ";\n\u000e\u0003\u000f\u0003\u000f\u0003\u0010\u0006\u0010@\n\u0010\r",
    "\u0010\u000e\u0010A\u0003\u0010\u0003\u0010\u0002\u0002\u0011\u0003",
    "\u0003\u0005\u0004\u0007\u0005\t\u0006\u000b\u0007\r\b\u000f\t\u0011",
    "\n\u0013\u000b\u0015\f\u0017\r\u0019\u000e\u001b\u000f\u001d\u0010\u001f",
    "\u0011\u0003\u0002\u0005\u0004\u0002GGgg\u0004\u0002C\\c|\u0005\u0002",
    "\u000b\f\u000f\u000f\"\"E\u0002\u0003\u0003\u0002\u0002\u0002\u0002",
    "\u0005\u0003\u0002\u0002\u0002\u0002\u0007\u0003\u0002\u0002\u0002\u0002",
    "\t\u0003\u0002\u0002\u0002\u0002\u000b\u0003\u0002\u0002\u0002\u0002",
    "\r\u0003\u0002\u0002\u0002\u0002\u000f\u0003\u0002\u0002\u0002\u0002",
    "\u0011\u0003\u0002\u0002\u0002\u0002\u0013\u0003\u0002\u0002\u0002\u0002",
    "\u0015\u0003\u0002\u0002\u0002\u0002\u0017\u0003\u0002\u0002\u0002\u0002",
    "\u0019\u0003\u0002\u0002\u0002\u0002\u001b\u0003\u0002\u0002\u0002\u0002",
    "\u001d\u0003\u0002\u0002\u0002\u0002\u001f\u0003\u0002\u0002\u0002\u0003",
    "!\u0003\u0002\u0002\u0002\u0005#\u0003\u0002\u0002\u0002\u0007%\u0003",
    "\u0002\u0002\u0002\t\'\u0003\u0002\u0002\u0002\u000b)\u0003\u0002\u0002",
    "\u0002\r+\u0003\u0002\u0002\u0002\u000f-\u0003\u0002\u0002\u0002\u0011",
    "/\u0003\u0002\u0002\u0002\u00131\u0003\u0002\u0002\u0002\u00153\u0003",
    "\u0002\u0002\u0002\u00175\u0003\u0002\u0002\u0002\u00197\u0003\u0002",
    "\u0002\u0002\u001b:\u0003\u0002\u0002\u0002\u001d<\u0003\u0002\u0002",
    "\u0002\u001f?\u0003\u0002\u0002\u0002!\"\u0007*\u0002\u0002\"\u0004",
    "\u0003\u0002\u0002\u0002#$\u0007+\u0002\u0002$\u0006\u0003\u0002\u0002",
    "\u0002%&\u0007-\u0002\u0002&\b\u0003\u0002\u0002\u0002\'(\u0007/\u0002",
    "\u0002(\n\u0003\u0002\u0002\u0002)*\u0007,\u0002\u0002*\f\u0003\u0002",
    "\u0002\u0002+,\u00071\u0002\u0002,\u000e\u0003\u0002\u0002\u0002-.\u0007",
    "@\u0002\u0002.\u0010\u0003\u0002\u0002\u0002/0\u0007>\u0002\u00020\u0012",
    "\u0003\u0002\u0002\u000212\u0007?\u0002\u00022\u0014\u0003\u0002\u0002",
    "\u000234\u00070\u0002\u00024\u0016\u0003\u0002\u0002\u000256\t\u0002",
    "\u0002\u00026\u0018\u0003\u0002\u0002\u000278\u0007`\u0002\u00028\u001a",
    "\u0003\u0002\u0002\u00029;\t\u0003\u0002\u0002:9\u0003\u0002\u0002\u0002",
    ";\u001c\u0003\u0002\u0002\u0002<=\u00042;\u0002=\u001e\u0003\u0002\u0002",
    "\u0002>@\t\u0004\u0002\u0002?>\u0003\u0002\u0002\u0002@A\u0003\u0002",
    "\u0002\u0002A?\u0003\u0002\u0002\u0002AB\u0003\u0002\u0002\u0002BC\u0003",
    "\u0002\u0002\u0002CD\b\u0010\u0002\u0002D \u0003\u0002\u0002\u0002\u0005",
    "\u0002:A\u0003\u0002\u0003\u0002"].join("");


var atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

var decisionsToDFA = atn.decisionToState.map( function(ds, index) { return new antlr4.dfa.DFA(ds, index); });

function ArithmeticLexer(input) {
	antlr4.Lexer.call(this, input);
    this._interp = new antlr4.atn.LexerATNSimulator(this, atn, decisionsToDFA, new antlr4.PredictionContextCache());
    return this;
}

ArithmeticLexer.prototype = Object.create(antlr4.Lexer.prototype);
ArithmeticLexer.prototype.constructor = ArithmeticLexer;

ArithmeticLexer.EOF = antlr4.Token.EOF;
ArithmeticLexer.LPAREN = 1;
ArithmeticLexer.RPAREN = 2;
ArithmeticLexer.PLUS = 3;
ArithmeticLexer.MINUS = 4;
ArithmeticLexer.TIMES = 5;
ArithmeticLexer.DIV = 6;
ArithmeticLexer.GT = 7;
ArithmeticLexer.LT = 8;
ArithmeticLexer.EQ = 9;
ArithmeticLexer.POINT = 10;
ArithmeticLexer.E = 11;
ArithmeticLexer.POW = 12;
ArithmeticLexer.LETTER = 13;
ArithmeticLexer.DIGIT = 14;
ArithmeticLexer.WS = 15;


ArithmeticLexer.modeNames = [ "DEFAULT_MODE" ];

ArithmeticLexer.literalNames = [ null, "'('", "')'", "'+'", "'-'", "'*'", 
                                 "'/'", "'>'", "'<'", "'='", "'.'", null, 
                                 "'^'" ];

ArithmeticLexer.symbolicNames = [ null, "LPAREN", "RPAREN", "PLUS", "MINUS", 
                                  "TIMES", "DIV", "GT", "LT", "EQ", "POINT", 
                                  "E", "POW", "LETTER", "DIGIT", "WS" ];

ArithmeticLexer.ruleNames = [ "LPAREN", "RPAREN", "PLUS", "MINUS", "TIMES", 
                              "DIV", "GT", "LT", "EQ", "POINT", "E", "POW", 
                              "LETTER", "DIGIT", "WS" ];

ArithmeticLexer.grammarFileName = "Arithmetic.g4";



exports.ArithmeticLexer = ArithmeticLexer;

