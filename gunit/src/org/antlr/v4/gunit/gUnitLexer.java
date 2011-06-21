// $ANTLR 3.4 gUnit.g 2011-06-20 18:31:50

package org.antlr.v4.gunit;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class gUnitLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int T__33=33;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int ACTION=4;
    public static final int DOC_COMMENT=5;
    public static final int FILENAME=6;
    public static final int ID=7;
    public static final int ID_=8;
    public static final int ML_COMMENT=9;
    public static final int ML_STRING=10;
    public static final int NESTED_AST=11;
    public static final int NESTED_RETVAL=12;
    public static final int OPTIONS=13;
    public static final int RETVAL=14;
    public static final int SL_COMMENT=15;
    public static final int STRING=16;
    public static final int STRING_=17;
    public static final int SUITE=18;
    public static final int TEST_ACTION=19;
    public static final int TEST_FAIL=20;
    public static final int TEST_OK=21;
    public static final int TEST_RETVAL=22;
    public static final int TEST_STDOUT=23;
    public static final int TEST_TREE=24;
    public static final int TREE=25;
    public static final int WS=26;
    public static final int XDIGIT=27;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public gUnitLexer() {} 
    public gUnitLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public gUnitLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "gUnit.g"; }

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:6:7: ( '->' )
            // gUnit.g:6:9: '->'
            {
            match("->"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:7:7: ( ':' )
            // gUnit.g:7:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:8:7: ( ';' )
            // gUnit.g:8:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:9:7: ( '=' )
            // gUnit.g:9:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:10:7: ( '@header' )
            // gUnit.g:10:9: '@header'
            {
            match("@header"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:11:7: ( 'FAIL' )
            // gUnit.g:11:9: 'FAIL'
            {
            match("FAIL"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:12:7: ( 'OK' )
            // gUnit.g:12:9: 'OK'
            {
            match("OK"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:13:7: ( 'gunit' )
            // gUnit.g:13:9: 'gunit'
            {
            match("gunit"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:14:7: ( 'returns' )
            // gUnit.g:14:9: 'returns'
            {
            match("returns"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:15:7: ( 'walks' )
            // gUnit.g:15:9: 'walks'
            {
            match("walks"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:16:7: ( '}' )
            // gUnit.g:16:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "ACTION"
    public final void mACTION() throws RecognitionException {
        try {
            int _type = ACTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:59:2: ( '{' ( '\\\\}' | '\\\\' ~ '}' |~ ( '\\\\' | '}' ) )* '}' )
            // gUnit.g:59:4: '{' ( '\\\\}' | '\\\\' ~ '}' |~ ( '\\\\' | '}' ) )* '}'
            {
            match('{'); 

            // gUnit.g:59:8: ( '\\\\}' | '\\\\' ~ '}' |~ ( '\\\\' | '}' ) )*
            loop1:
            do {
                int alt1=4;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='\\') ) {
                    int LA1_2 = input.LA(2);

                    if ( (LA1_2=='}') ) {
                        alt1=1;
                    }
                    else if ( ((LA1_2 >= '\u0000' && LA1_2 <= '|')||(LA1_2 >= '~' && LA1_2 <= '\uFFFF')) ) {
                        alt1=2;
                    }


                }
                else if ( ((LA1_0 >= '\u0000' && LA1_0 <= '[')||(LA1_0 >= ']' && LA1_0 <= '|')||(LA1_0 >= '~' && LA1_0 <= '\uFFFF')) ) {
                    alt1=3;
                }


                switch (alt1) {
            	case 1 :
            	    // gUnit.g:59:9: '\\\\}'
            	    {
            	    match("\\}"); 



            	    }
            	    break;
            	case 2 :
            	    // gUnit.g:59:15: '\\\\' ~ '}'
            	    {
            	    match('\\'); 

            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '|')||(input.LA(1) >= '~' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;
            	case 3 :
            	    // gUnit.g:59:25: ~ ( '\\\\' | '}' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '|')||(input.LA(1) >= '~' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            match('}'); 

            setText(getText().substring(1, getText().length()-1));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ACTION"

    // $ANTLR start "RETVAL"
    public final void mRETVAL() throws RecognitionException {
        try {
            int _type = RETVAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:63:2: ( NESTED_RETVAL )
            // gUnit.g:63:4: NESTED_RETVAL
            {
            mNESTED_RETVAL(); 


            setText(getText().substring(1, getText().length()-1));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RETVAL"

    // $ANTLR start "NESTED_RETVAL"
    public final void mNESTED_RETVAL() throws RecognitionException {
        try {
            // gUnit.g:68:15: ( '[' ( options {greedy=false; } : NESTED_RETVAL | . )* ']' )
            // gUnit.g:69:2: '[' ( options {greedy=false; } : NESTED_RETVAL | . )* ']'
            {
            match('['); 

            // gUnit.g:70:2: ( options {greedy=false; } : NESTED_RETVAL | . )*
            loop2:
            do {
                int alt2=3;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==']') ) {
                    alt2=3;
                }
                else if ( (LA2_0=='[') ) {
                    alt2=1;
                }
                else if ( ((LA2_0 >= '\u0000' && LA2_0 <= 'Z')||LA2_0=='\\'||(LA2_0 >= '^' && LA2_0 <= '\uFFFF')) ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // gUnit.g:71:4: NESTED_RETVAL
            	    {
            	    mNESTED_RETVAL(); 


            	    }
            	    break;
            	case 2 :
            	    // gUnit.g:72:4: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            match(']'); 

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NESTED_RETVAL"

    // $ANTLR start "TREE"
    public final void mTREE() throws RecognitionException {
        try {
            int _type = TREE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:76:6: ( NESTED_AST ( ( ' ' )? NESTED_AST )* )
            // gUnit.g:76:8: NESTED_AST ( ( ' ' )? NESTED_AST )*
            {
            mNESTED_AST(); 


            // gUnit.g:76:19: ( ( ' ' )? NESTED_AST )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==' '||LA4_0=='(') ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // gUnit.g:76:20: ( ' ' )? NESTED_AST
            	    {
            	    // gUnit.g:76:20: ( ' ' )?
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0==' ') ) {
            	        alt3=1;
            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // gUnit.g:76:20: ' '
            	            {
            	            match(' '); 

            	            }
            	            break;

            	    }


            	    mNESTED_AST(); 


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TREE"

    // $ANTLR start "NESTED_AST"
    public final void mNESTED_AST() throws RecognitionException {
        try {
            // gUnit.g:81:2: ( '(' ( NESTED_AST | STRING_ |~ ( '(' | ')' | '\"' ) )* ')' )
            // gUnit.g:81:4: '(' ( NESTED_AST | STRING_ |~ ( '(' | ')' | '\"' ) )* ')'
            {
            match('('); 

            // gUnit.g:82:3: ( NESTED_AST | STRING_ |~ ( '(' | ')' | '\"' ) )*
            loop5:
            do {
                int alt5=4;
                int LA5_0 = input.LA(1);

                if ( (LA5_0=='(') ) {
                    alt5=1;
                }
                else if ( (LA5_0=='\"') ) {
                    alt5=2;
                }
                else if ( ((LA5_0 >= '\u0000' && LA5_0 <= '!')||(LA5_0 >= '#' && LA5_0 <= '\'')||(LA5_0 >= '*' && LA5_0 <= '\uFFFF')) ) {
                    alt5=3;
                }


                switch (alt5) {
            	case 1 :
            	    // gUnit.g:82:5: NESTED_AST
            	    {
            	    mNESTED_AST(); 


            	    }
            	    break;
            	case 2 :
            	    // gUnit.g:83:7: STRING_
            	    {
            	    mSTRING_(); 


            	    }
            	    break;
            	case 3 :
            	    // gUnit.g:84:5: ~ ( '(' | ')' | '\"' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\'')||(input.LA(1) >= '*' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            match(')'); 

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NESTED_AST"

    // $ANTLR start "OPTIONS"
    public final void mOPTIONS() throws RecognitionException {
        try {
            int _type = OPTIONS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:88:9: ( 'options' ( WS )* '{' )
            // gUnit.g:88:11: 'options' ( WS )* '{'
            {
            match("options"); 



            // gUnit.g:88:21: ( WS )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0 >= '\t' && LA6_0 <= '\n')||LA6_0=='\r'||LA6_0==' ') ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // gUnit.g:88:21: WS
            	    {
            	    mWS(); 


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OPTIONS"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:90:4: ( ID_ ( '.' ID_ )* )
            // gUnit.g:90:6: ID_ ( '.' ID_ )*
            {
            mID_(); 


            // gUnit.g:90:10: ( '.' ID_ )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0=='.') ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // gUnit.g:90:11: '.' ID_
            	    {
            	    match('.'); 

            	    mID_(); 


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "ID_"
    public final void mID_() throws RecognitionException {
        try {
            // gUnit.g:94:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // gUnit.g:94:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // gUnit.g:94:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0 >= '0' && LA8_0 <= '9')||(LA8_0 >= 'A' && LA8_0 <= 'Z')||LA8_0=='_'||(LA8_0 >= 'a' && LA8_0 <= 'z')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // gUnit.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID_"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:96:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // gUnit.g:96:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:104:3: ( '//' (~ ( '\\r' | '\\n' ) )* ( '\\r' )? '\\n' )
            // gUnit.g:104:5: '//' (~ ( '\\r' | '\\n' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 



            // gUnit.g:104:10: (~ ( '\\r' | '\\n' ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0 >= '\u0000' && LA9_0 <= '\t')||(LA9_0 >= '\u000B' && LA9_0 <= '\f')||(LA9_0 >= '\u000E' && LA9_0 <= '\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // gUnit.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            // gUnit.g:104:24: ( '\\r' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\r') ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // gUnit.g:104:24: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SL_COMMENT"

    // $ANTLR start "DOC_COMMENT"
    public final void mDOC_COMMENT() throws RecognitionException {
        try {
            int _type = DOC_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:108:2: ( '/**' ( options {greedy=false; } : . )* '*/' )
            // gUnit.g:108:4: '/**' ( options {greedy=false; } : . )* '*/'
            {
            match("/**"); 



            // gUnit.g:108:10: ( options {greedy=false; } : . )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0=='*') ) {
                    int LA11_1 = input.LA(2);

                    if ( (LA11_1=='/') ) {
                        alt11=2;
                    }
                    else if ( ((LA11_1 >= '\u0000' && LA11_1 <= '.')||(LA11_1 >= '0' && LA11_1 <= '\uFFFF')) ) {
                        alt11=1;
                    }


                }
                else if ( ((LA11_0 >= '\u0000' && LA11_0 <= ')')||(LA11_0 >= '+' && LA11_0 <= '\uFFFF')) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // gUnit.g:108:35: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            match("*/"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOC_COMMENT"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:112:2: ( '/*' ~ '*' ( options {greedy=false; } : . )* '*/' )
            // gUnit.g:112:4: '/*' ~ '*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            if ( (input.LA(1) >= '\u0000' && input.LA(1) <= ')')||(input.LA(1) >= '+' && input.LA(1) <= '\uFFFF') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // gUnit.g:112:14: ( options {greedy=false; } : . )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0=='*') ) {
                    int LA12_1 = input.LA(2);

                    if ( (LA12_1=='/') ) {
                        alt12=2;
                    }
                    else if ( ((LA12_1 >= '\u0000' && LA12_1 <= '.')||(LA12_1 >= '0' && LA12_1 <= '\uFFFF')) ) {
                        alt12=1;
                    }


                }
                else if ( ((LA12_0 >= '\u0000' && LA12_0 <= ')')||(LA12_0 >= '+' && LA12_0 <= '\uFFFF')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // gUnit.g:112:39: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            match("*/"); 



            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ML_COMMENT"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:115:8: ( STRING_ )
            // gUnit.g:115:10: STRING_
            {
            mSTRING_(); 


            setText(getText().substring(1, getText().length()-1));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "STRING_"
    public final void mSTRING_() throws RecognitionException {
        try {
            // gUnit.g:120:2: ( '\"' ( '\\\\\"' | '\\\\' ~ '\"' |~ ( '\\\\' | '\"' ) )+ '\"' )
            // gUnit.g:120:4: '\"' ( '\\\\\"' | '\\\\' ~ '\"' |~ ( '\\\\' | '\"' ) )+ '\"'
            {
            match('\"'); 

            // gUnit.g:120:8: ( '\\\\\"' | '\\\\' ~ '\"' |~ ( '\\\\' | '\"' ) )+
            int cnt13=0;
            loop13:
            do {
                int alt13=4;
                int LA13_0 = input.LA(1);

                if ( (LA13_0=='\\') ) {
                    int LA13_2 = input.LA(2);

                    if ( (LA13_2=='\"') ) {
                        alt13=1;
                    }
                    else if ( ((LA13_2 >= '\u0000' && LA13_2 <= '!')||(LA13_2 >= '#' && LA13_2 <= '\uFFFF')) ) {
                        alt13=2;
                    }


                }
                else if ( ((LA13_0 >= '\u0000' && LA13_0 <= '!')||(LA13_0 >= '#' && LA13_0 <= '[')||(LA13_0 >= ']' && LA13_0 <= '\uFFFF')) ) {
                    alt13=3;
                }


                switch (alt13) {
            	case 1 :
            	    // gUnit.g:120:9: '\\\\\"'
            	    {
            	    match("\\\""); 



            	    }
            	    break;
            	case 2 :
            	    // gUnit.g:120:15: '\\\\' ~ '\"'
            	    {
            	    match('\\'); 

            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;
            	case 3 :
            	    // gUnit.g:120:25: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


            match('\"'); 

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING_"

    // $ANTLR start "ML_STRING"
    public final void mML_STRING() throws RecognitionException {
        try {
            int _type = ML_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:123:2: ( '<<' ( . )* '>>' )
            // gUnit.g:123:4: '<<' ( . )* '>>'
            {
            match("<<"); 



            // gUnit.g:123:9: ( . )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='>') ) {
                    int LA14_1 = input.LA(2);

                    if ( (LA14_1=='>') ) {
                        alt14=2;
                    }
                    else if ( ((LA14_1 >= '\u0000' && LA14_1 <= '=')||(LA14_1 >= '?' && LA14_1 <= '\uFFFF')) ) {
                        alt14=1;
                    }


                }
                else if ( ((LA14_0 >= '\u0000' && LA14_0 <= '=')||(LA14_0 >= '?' && LA14_0 <= '\uFFFF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // gUnit.g:123:9: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            match(">>"); 



            setText(getText().substring(2, getText().length()-2));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ML_STRING"

    // $ANTLR start "FILENAME"
    public final void mFILENAME() throws RecognitionException {
        try {
            int _type = FILENAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // gUnit.g:127:2: ( '/' ID ( '/' ID )* | ID ( '/' ID )+ )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0=='/') ) {
                alt17=1;
            }
            else if ( ((LA17_0 >= 'A' && LA17_0 <= 'Z')||LA17_0=='_'||(LA17_0 >= 'a' && LA17_0 <= 'z')) ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;

            }
            switch (alt17) {
                case 1 :
                    // gUnit.g:127:4: '/' ID ( '/' ID )*
                    {
                    match('/'); 

                    mID(); 


                    // gUnit.g:127:11: ( '/' ID )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0=='/') ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // gUnit.g:127:12: '/' ID
                    	    {
                    	    match('/'); 

                    	    mID(); 


                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // gUnit.g:128:4: ID ( '/' ID )+
                    {
                    mID(); 


                    // gUnit.g:128:7: ( '/' ID )+
                    int cnt16=0;
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0=='/') ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // gUnit.g:128:8: '/' ID
                    	    {
                    	    match('/'); 

                    	    mID(); 


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt16 >= 1 ) break loop16;
                                EarlyExitException eee =
                                    new EarlyExitException(16, input);
                                throw eee;
                        }
                        cnt16++;
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FILENAME"

    // $ANTLR start "XDIGIT"
    public final void mXDIGIT() throws RecognitionException {
        try {
            // gUnit.g:151:8: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            // gUnit.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "XDIGIT"

    public void mTokens() throws RecognitionException {
        // gUnit.g:1:8: ( T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | ACTION | RETVAL | TREE | OPTIONS | ID | WS | SL_COMMENT | DOC_COMMENT | ML_COMMENT | STRING | ML_STRING | FILENAME )
        int alt18=23;
        alt18 = dfa18.predict(input);
        switch (alt18) {
            case 1 :
                // gUnit.g:1:10: T__28
                {
                mT__28(); 


                }
                break;
            case 2 :
                // gUnit.g:1:16: T__29
                {
                mT__29(); 


                }
                break;
            case 3 :
                // gUnit.g:1:22: T__30
                {
                mT__30(); 


                }
                break;
            case 4 :
                // gUnit.g:1:28: T__31
                {
                mT__31(); 


                }
                break;
            case 5 :
                // gUnit.g:1:34: T__32
                {
                mT__32(); 


                }
                break;
            case 6 :
                // gUnit.g:1:40: T__33
                {
                mT__33(); 


                }
                break;
            case 7 :
                // gUnit.g:1:46: T__34
                {
                mT__34(); 


                }
                break;
            case 8 :
                // gUnit.g:1:52: T__35
                {
                mT__35(); 


                }
                break;
            case 9 :
                // gUnit.g:1:58: T__36
                {
                mT__36(); 


                }
                break;
            case 10 :
                // gUnit.g:1:64: T__37
                {
                mT__37(); 


                }
                break;
            case 11 :
                // gUnit.g:1:70: T__38
                {
                mT__38(); 


                }
                break;
            case 12 :
                // gUnit.g:1:76: ACTION
                {
                mACTION(); 


                }
                break;
            case 13 :
                // gUnit.g:1:83: RETVAL
                {
                mRETVAL(); 


                }
                break;
            case 14 :
                // gUnit.g:1:90: TREE
                {
                mTREE(); 


                }
                break;
            case 15 :
                // gUnit.g:1:95: OPTIONS
                {
                mOPTIONS(); 


                }
                break;
            case 16 :
                // gUnit.g:1:103: ID
                {
                mID(); 


                }
                break;
            case 17 :
                // gUnit.g:1:106: WS
                {
                mWS(); 


                }
                break;
            case 18 :
                // gUnit.g:1:109: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;
            case 19 :
                // gUnit.g:1:120: DOC_COMMENT
                {
                mDOC_COMMENT(); 


                }
                break;
            case 20 :
                // gUnit.g:1:132: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 21 :
                // gUnit.g:1:143: STRING
                {
                mSTRING(); 


                }
                break;
            case 22 :
                // gUnit.g:1:150: ML_STRING
                {
                mML_STRING(); 


                }
                break;
            case 23 :
                // gUnit.g:1:160: FILENAME
                {
                mFILENAME(); 


                }
                break;

        }

    }


    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA18_eotS =
        "\6\uffff\5\27\4\uffff\2\27\4\uffff\2\27\3\uffff\1\43\4\27\2\uffff"+
        "\2\27\1\uffff\4\27\2\uffff\1\60\5\27\1\uffff\1\65\1\27\1\67\1\27"+
        "\1\uffff\1\27\1\uffff\1\27\1\73\1\27\2\uffff";
    static final String DFA18_eofS =
        "\75\uffff";
    static final String DFA18_minS =
        "\1\11\5\uffff\5\56\4\uffff\2\56\1\uffff\1\52\2\uffff\2\56\1\uffff"+
        "\1\101\1\uffff\5\56\1\uffff\1\0\2\56\1\uffff\4\56\2\uffff\6\56\1"+
        "\uffff\4\56\1\uffff\1\56\1\uffff\2\56\1\11\2\uffff";
    static final String DFA18_maxS =
        "\1\175\5\uffff\5\172\4\uffff\2\172\1\uffff\1\172\2\uffff\2\172\1"+
        "\uffff\1\172\1\uffff\5\172\1\uffff\1\uffff\2\172\1\uffff\4\172\2"+
        "\uffff\6\172\1\uffff\4\172\1\uffff\1\172\1\uffff\2\172\1\173\2\uffff";
    static final String DFA18_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\5\uffff\1\13\1\14\1\15\1\16\2\uffff"+
        "\1\21\1\uffff\1\25\1\26\2\uffff\1\20\1\uffff\1\27\5\uffff\1\22\3"+
        "\uffff\1\7\4\uffff\1\23\1\24\6\uffff\1\6\4\uffff\1\10\1\uffff\1"+
        "\12\3\uffff\1\11\1\17";
    static final String DFA18_specialS =
        "\40\uffff\1\0\34\uffff}>";
    static final String[] DFA18_transitionS = {
            "\2\21\2\uffff\1\21\22\uffff\1\21\1\uffff\1\23\5\uffff\1\16\4"+
            "\uffff\1\1\1\uffff\1\22\12\uffff\1\2\1\3\1\24\1\4\2\uffff\1"+
            "\5\5\20\1\6\10\20\1\7\13\20\1\15\3\uffff\1\20\1\uffff\6\20\1"+
            "\10\7\20\1\17\2\20\1\11\4\20\1\12\3\20\1\14\1\uffff\1\13",
            "",
            "",
            "",
            "",
            "",
            "\1\30\1\31\12\26\7\uffff\1\25\31\26\4\uffff\1\26\1\uffff\32"+
            "\26",
            "\1\30\1\31\12\26\7\uffff\12\26\1\32\17\26\4\uffff\1\26\1\uffff"+
            "\32\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\24\26\1"+
            "\33\5\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\4\26\1"+
            "\34\25\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\1\35\31"+
            "\26",
            "",
            "",
            "",
            "",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\17\26\1"+
            "\36\12\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
            "",
            "\1\40\4\uffff\1\37\21\uffff\32\31\4\uffff\1\31\1\uffff\32\31",
            "",
            "",
            "\1\30\1\31\12\26\7\uffff\10\26\1\41\21\26\4\uffff\1\26\1\uffff"+
            "\32\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
            "",
            "\32\42\4\uffff\1\42\1\uffff\32\42",
            "",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\15\26\1"+
            "\44\14\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\23\26\1"+
            "\45\6\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\13\26\1"+
            "\46\16\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\23\26\1"+
            "\47\6\26",
            "",
            "\52\51\1\50\uffd5\51",
            "\1\30\1\31\12\26\7\uffff\13\26\1\52\16\26\4\uffff\1\26\1\uffff"+
            "\32\26",
            "\1\30\1\31\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
            "",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\10\26\1"+
            "\54\21\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\24\26\1"+
            "\55\5\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\12\26\1"+
            "\56\17\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\10\26\1"+
            "\57\21\26",
            "",
            "",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
            "\1\30\1\31\12\53\7\uffff\32\53\4\uffff\1\53\1\uffff\32\53",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\23\26\1"+
            "\61\6\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\21\26\1"+
            "\62\10\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\22\26\1"+
            "\63\7\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\16\26\1"+
            "\64\13\26",
            "",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\15\26\1"+
            "\66\14\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\15\26\1"+
            "\70\14\26",
            "",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\22\26\1"+
            "\71\7\26",
            "",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\22\26\1"+
            "\72\7\26",
            "\1\30\1\31\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
            "\2\74\2\uffff\1\74\22\uffff\1\74\15\uffff\1\30\1\31\12\26\7"+
            "\uffff\32\26\4\uffff\1\26\1\uffff\32\26\1\74",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | ACTION | RETVAL | TREE | OPTIONS | ID | WS | SL_COMMENT | DOC_COMMENT | ML_COMMENT | STRING | ML_STRING | FILENAME );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA18_32 = input.LA(1);

                        s = -1;
                        if ( (LA18_32=='*') ) {s = 40;}

                        else if ( ((LA18_32 >= '\u0000' && LA18_32 <= ')')||(LA18_32 >= '+' && LA18_32 <= '\uFFFF')) ) {s = 41;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 18, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}