// Generated from abc.g4 by ANTLR 4.12.0
// jshint ignore: start
import antlr4 from '../../../src/antlr4/index.node.js';


const serializedATN = [4,0,3,13,6,-1,2,0,7,0,2,1,7,1,2,2,7,2,1,0,1,0,1,1,
1,1,1,2,1,2,0,0,3,1,1,3,2,5,3,1,0,0,12,0,1,1,0,0,0,0,3,1,0,0,0,0,5,1,0,0,
0,1,7,1,0,0,0,3,9,1,0,0,0,5,11,1,0,0,0,7,8,5,97,0,0,8,2,1,0,0,0,9,10,5,98,
0,0,10,4,1,0,0,0,11,12,5,99,0,0,12,6,1,0,0,0,1,0,0];


const atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

const decisionsToDFA = atn.decisionToState.map( (ds, index) => new antlr4.dfa.DFA(ds, index) );

export default class abc extends antlr4.Lexer {

    static grammarFileName = "abc.g4";
    static channelNames = [ "DEFAULT_TOKEN_CHANNEL", "HIDDEN" ];
	static modeNames = [ "DEFAULT_MODE" ];
	static literalNames = [ null, "'a'", "'b'", "'c'" ];
	static symbolicNames = [ null, "A", "B", "C" ];
	static ruleNames = [ "A", "B", "C" ];

    constructor(input) {
        super(input)
        this._interp = new antlr4.atn.LexerATNSimulator(this, atn, decisionsToDFA, new antlr4.atn.PredictionContextCache());
    }
}

abc.EOF = antlr4.Token.EOF;
abc.A = 1;
abc.B = 2;
abc.C = 3;



