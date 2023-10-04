// Generated from calc.g4 by ANTLR 4.12.0
// jshint ignore: start
import antlr4 from '../../../src/antlr4/index.node.js';


const serializedATN = [4,0,7,38,6,-1,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,
7,4,2,5,7,5,2,6,7,6,1,0,4,0,17,8,0,11,0,12,0,18,1,1,4,1,22,8,1,11,1,12,1,
23,1,2,1,2,1,3,1,3,1,4,1,4,1,5,1,5,1,6,4,6,35,8,6,11,6,12,6,36,0,0,7,1,1,
3,2,5,3,7,4,9,5,11,6,13,7,1,0,0,40,0,1,1,0,0,0,0,3,1,0,0,0,0,5,1,0,0,0,0,
7,1,0,0,0,0,9,1,0,0,0,0,11,1,0,0,0,0,13,1,0,0,0,1,16,1,0,0,0,3,21,1,0,0,
0,5,25,1,0,0,0,7,27,1,0,0,0,9,29,1,0,0,0,11,31,1,0,0,0,13,34,1,0,0,0,15,
17,2,97,122,0,16,15,1,0,0,0,17,18,1,0,0,0,18,16,1,0,0,0,18,19,1,0,0,0,19,
2,1,0,0,0,20,22,2,48,57,0,21,20,1,0,0,0,22,23,1,0,0,0,23,21,1,0,0,0,23,24,
1,0,0,0,24,4,1,0,0,0,25,26,5,59,0,0,26,6,1,0,0,0,27,28,5,43,0,0,28,8,1,0,
0,0,29,30,5,42,0,0,30,10,1,0,0,0,31,32,5,61,0,0,32,12,1,0,0,0,33,35,5,32,
0,0,34,33,1,0,0,0,35,36,1,0,0,0,36,34,1,0,0,0,36,37,1,0,0,0,37,14,1,0,0,
0,4,0,18,23,36,0];


const atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

const decisionsToDFA = atn.decisionToState.map( (ds, index) => new antlr4.dfa.DFA(ds, index) );

export default class calc extends antlr4.Lexer {

    static grammarFileName = "calc.g4";
    static channelNames = [ "DEFAULT_TOKEN_CHANNEL", "HIDDEN" ];
	static modeNames = [ "DEFAULT_MODE" ];
	static literalNames = [ null, null, null, "';'", "'+'", "'*'", "'='" ];
	static symbolicNames = [ null, "ID", "INT", "SEMI", "PLUS", "MUL", "ASSIGN", 
                          "WS" ];
	static ruleNames = [ "ID", "INT", "SEMI", "PLUS", "MUL", "ASSIGN", "WS" ];

    constructor(input) {
        super(input)
        this._interp = new antlr4.atn.LexerATNSimulator(this, atn, decisionsToDFA, new antlr4.atn.PredictionContextCache());
    }
}

calc.EOF = antlr4.Token.EOF;
calc.ID = 1;
calc.INT = 2;
calc.SEMI = 3;
calc.PLUS = 4;
calc.MUL = 5;
calc.ASSIGN = 6;
calc.WS = 7;



