/*
 * @(#)SerializationTester.java 1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerSharedState;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import org.antlr.runtime.*;

public class T extends Lexer {
    public static final int
        EOR=1, STRING_START=4, WS=5, STRING=6, ANY=7;
    public static final int DEFAULT_MODE = 0;
    public static final int STRING_MODE = 1;

    public static final String[] tokenNames = {
        "<INVALID>", "<INVALID>", "<INVALID>",
        "EOR", "STRING_START", "WS", "STRING", "ANY"
    };
    public static final String[] ruleNames = {
        "<INVALID>",
        "STRING_START", "WS", "STRING", "ANY"
    };


    public T(CharStream input) {
        this(input, new LexerSharedState());
    }
    public T(CharStream input, LexerSharedState state) {
        super(input,state);
		_interp = new LexerInterpreter(this,_ATN);
    }

    public String getGrammarFileName() { return "T.java"; }
    @Override
    public String[] getTokenNames() { return tokenNames; }
    @Override
    public String[] getRuleNames() { return ruleNames; }
	@Override
	public ATN getATN() { return _ATN; }


    	public void action(int ruleIndex, int actionIndex) {
    		switch ( actionIndex ) {
    		    case 1 : pushMode(STRING_MODE); more(); break;
    		    case 2 : skip(); break;
    		    case 3 : popMode(); break;
    		    case 4 : more(); break;
        	}
    	}

    public static final String _serializedATN =
    	"\030\012\032\06\00\06\00\02\01\07\01\02\02\07\02\02\03\07\03\02\04"+
      "\07\04\01\01\01\01\01\01\01\02\01\02\01\02\01\02\01\02\03\02\010\02"+
      "\01\03\01\03\01\03\01\04\01\04\01\04\04\02\04\01\04\05\02\06\06\03"+
      "\010\07\04\02\00\01\00\031\00\02\01\00\00\00\04\01\00\00\01\06\01"+
      "\00\00\01\010\01\00\00\02\012\01\00\00\04\022\01\00\00\06\024\01\00"+
      "\00\010\027\01\00\00\012\013\05\042\00\013\014\01\00\00\014\03\01"+
      "\00\00\015\016\05\040\00\016\023\01\00\00\017\020\05\012\00\020\021"+
      "\01\00\00\021\023\01\00\00\022\015\01\00\00\022\017\01\00\00\023\05"+
      "\01\00\00\024\025\05\042\00\025\026\01\00\00\026\07\01\00\00\027\030"+
      "\013\00\00\030\031\01\00\00\031\011\01\00\00\03\00\01\022";
    public static final ATN _ATN =
        ATNInterpreter.deserialize(_serializedATN.toCharArray());
    static {
        org.antlr.v4.tool.DOTGenerator dot = new org.antlr.v4.tool.DOTGenerator(null);
    	//System.out.println(dot.getDOT(_ATN.decisionToATNState.get(0)));
    }
}
