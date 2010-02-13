// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ActionSplitter.g 2010-02-12 16:46:37

package org.antlr.v4.parse;

import org.antlr.runtime.*;
import org.antlr.v4.runtime.CommonToken;

import java.util.ArrayList;
import java.util.List;
public class ActionSplitter extends Lexer {
    public static final int INDIRECT_TEMPLATE_INSTANCE=23;
    public static final int LINE_COMMENT=5;
    public static final int DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR=15;
    public static final int SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR=14;
    public static final int SET_ATTRIBUTE=25;
    public static final int TEMPLATE_EXPR=26;
    public static final int TEXT=28;
    public static final int ID=6;
    public static final int QUALIFIED_ATTR=10;
    public static final int EOF=-1;
    public static final int ACTION=22;
    public static final int UNKNOWN_SYNTAX=27;
    public static final int SET_QUALIFIED_ATTR=9;
    public static final int SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR=16;
    public static final int WS=7;
    public static final int ARG=20;
    public static final int TEMPLATE_INSTANCE=21;
    public static final int SET_EXPR_ATTRIBUTE=24;
    public static final int ATTR_VALUE_EXPR=8;
    public static final int SET_DYNAMIC_SCOPE_ATTR=11;
    public static final int SCOPE_INDEX_EXPR=13;
    public static final int DYNAMIC_SCOPE_ATTR=12;
    public static final int SET_ATTR=18;
    public static final int COMMENT=4;
    public static final int ATTR=19;
    public static final int DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR=17;

    ActionSplitterListener delegate;

    public ActionSplitter(CharStream input, ActionSplitterListener delegate) {
        this(input, new RecognizerSharedState());
        this.delegate = delegate;
    }

    public void emit(Token token) {
    	super.emit(token);
    	
    }

    /** force filtering (and return tokens). triggers all above actions. */
    public List<Token> getActionChunks() { 
        List<Token> chunks = new ArrayList<Token>();
        Token t = nextToken();
        while ( t.getType()!=Token.EOF ) {
            chunks.add(t);
            t = nextToken();
        }
        return chunks;
    }


    // delegates
    // delegators

    public ActionSplitter() {;} 
    public ActionSplitter(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ActionSplitter(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "ActionSplitter.g"; }

    public Token nextToken() {
        while (true) {
            if ( input.LA(1)==CharStream.EOF ) {
                Token eof = new CommonToken((CharStream)input,Token.EOF,
                                            Token.DEFAULT_CHANNEL,
                                            input.index(),input.index());
                eof.setLine(getLine());
                eof.setCharPositionInLine(getCharPositionInLine());
                return eof;
            }
            state.token = null;
    	state.channel = Token.DEFAULT_CHANNEL;
            state.tokenStartCharIndex = input.index();
            state.tokenStartCharPositionInLine = input.getCharPositionInLine();
            state.tokenStartLine = input.getLine();
    	state.text = null;
            try {
                int m = input.mark();
                state.backtracking=1; 
                state.failed=false;
                mTokens();
                state.backtracking=0;

                if ( state.failed ) {
                    input.rewind(m);
                    input.consume(); 
                }
                else {
                    emit();
                    return state.token;
                }
            }
            catch (RecognitionException re) {
                // shouldn't happen in backtracking mode, but...
                reportError(re);
                recover(re);
            }
        }
    }

    public void memoize(IntStream input,
    		int ruleIndex,
    		int ruleStartIndex)
    {
    if ( state.backtracking>1 ) super.memoize(input, ruleIndex, ruleStartIndex);
    }

    public boolean alreadyParsedRule(IntStream input, int ruleIndex) {
    if ( state.backtracking>1 ) return super.alreadyParsedRule(input, ruleIndex);
    return false;
    }// $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ActionSplitter.g:39:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // ActionSplitter.g:39:9: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); if (state.failed) return ;

            // ActionSplitter.g:39:14: ( options {greedy=false; } : . )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0=='*') ) {
                    int LA1_1 = input.LA(2);

                    if ( (LA1_1=='/') ) {
                        alt1=2;
                    }
                    else if ( ((LA1_1>='\u0000' && LA1_1<='.')||(LA1_1>='0' && LA1_1<='\uFFFF')) ) {
                        alt1=1;
                    }


                }
                else if ( ((LA1_0>='\u0000' && LA1_0<=')')||(LA1_0>='+' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ActionSplitter.g:39:42: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match("*/"); if (state.failed) return ;

            if ( state.backtracking==1 ) {
              delegate.text(getText());
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "LINE_COMMENT"
    public final void mLINE_COMMENT() throws RecognitionException {
        try {
            int _type = LINE_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ActionSplitter.g:43:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // ActionSplitter.g:43:7: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); if (state.failed) return ;

            // ActionSplitter.g:43:12: (~ ( '\\n' | '\\r' ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='\u0000' && LA2_0<='\t')||(LA2_0>='\u000B' && LA2_0<='\f')||(LA2_0>='\u000E' && LA2_0<='\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ActionSplitter.g:43:12: ~ ( '\\n' | '\\r' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // ActionSplitter.g:43:26: ( '\\r' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='\r') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ActionSplitter.g:43:26: '\\r'
                    {
                    match('\r'); if (state.failed) return ;

                    }
                    break;

            }

            match('\n'); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              delegate.text(getText());
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LINE_COMMENT"

    // $ANTLR start "SET_QUALIFIED_ATTR"
    public final void mSET_QUALIFIED_ATTR() throws RecognitionException {
        try {
            int _type = SET_QUALIFIED_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken y=null;
            CommonToken expr=null;

            // ActionSplitter.g:47:2: ( '$' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:47:4: '$' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('$'); if (state.failed) return ;
            int xStart112 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart112, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            int yStart118 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart118, getCharIndex()-1);
            // ActionSplitter.g:47:22: ( WS )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>='\t' && LA4_0<='\n')||LA4_0=='\r'||LA4_0==' ') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ActionSplitter.g:47:22: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart127 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart127, getCharIndex()-1);
            match(';'); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              delegate.setQualifiedAttr(getText(), x, y, expr);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET_QUALIFIED_ATTR"

    // $ANTLR start "QUALIFIED_ATTR"
    public final void mQUALIFIED_ATTR() throws RecognitionException {
        try {
            int _type = QUALIFIED_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken y=null;

            // ActionSplitter.g:52:2: ( '$' x= ID '.' y= ID {...}?)
            // ActionSplitter.g:52:4: '$' x= ID '.' y= ID {...}?
            {
            match('$'); if (state.failed) return ;
            int xStart148 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart148, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            int yStart154 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart154, getCharIndex()-1);
            if ( !((input.LA(1)!='(')) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "QUALIFIED_ATTR", "input.LA(1)!='('");
            }
            if ( state.backtracking==1 ) {
              delegate.qualifiedAttr(getText(), x, y);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUALIFIED_ATTR"

    // $ANTLR start "SET_DYNAMIC_SCOPE_ATTR"
    public final void mSET_DYNAMIC_SCOPE_ATTR() throws RecognitionException {
        try {
            int _type = SET_DYNAMIC_SCOPE_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken y=null;
            CommonToken expr=null;

            // ActionSplitter.g:56:2: ( '$' x= ID '::' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:56:4: '$' x= ID '::' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('$'); if (state.failed) return ;
            int xStart173 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart173, getCharIndex()-1);
            match("::"); if (state.failed) return ;

            int yStart179 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart179, getCharIndex()-1);
            // ActionSplitter.g:56:23: ( WS )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>='\t' && LA5_0<='\n')||LA5_0=='\r'||LA5_0==' ') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ActionSplitter.g:56:23: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart188 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart188, getCharIndex()-1);
            match(';'); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              delegate.setDynamicScopeAttr(getText(), x, y, expr);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET_DYNAMIC_SCOPE_ATTR"

    // $ANTLR start "DYNAMIC_SCOPE_ATTR"
    public final void mDYNAMIC_SCOPE_ATTR() throws RecognitionException {
        try {
            int _type = DYNAMIC_SCOPE_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken y=null;

            // ActionSplitter.g:61:2: ( '$' x= ID '::' y= ID )
            // ActionSplitter.g:61:4: '$' x= ID '::' y= ID
            {
            match('$'); if (state.failed) return ;
            int xStart209 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart209, getCharIndex()-1);
            match("::"); if (state.failed) return ;

            int yStart215 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart215, getCharIndex()-1);
            if ( state.backtracking==1 ) {
              delegate.dynamicScopeAttr(getText(), x, y);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DYNAMIC_SCOPE_ATTR"

    // $ANTLR start "SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR"
    public final void mSET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR() throws RecognitionException {
        try {
            int _type = SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken index=null;
            CommonToken y=null;
            CommonToken expr=null;

            // ActionSplitter.g:73:2: ( '$' x= ID '[' '-' index= SCOPE_INDEX_EXPR ']' '::' y= ID ( WS )? ( '=' expr= ATTR_VALUE_EXPR ';' )? )
            // ActionSplitter.g:73:4: '$' x= ID '[' '-' index= SCOPE_INDEX_EXPR ']' '::' y= ID ( WS )? ( '=' expr= ATTR_VALUE_EXPR ';' )?
            {
            match('$'); if (state.failed) return ;
            int xStart234 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart234, getCharIndex()-1);
            match('['); if (state.failed) return ;
            match('-'); if (state.failed) return ;
            int indexStart242 = getCharIndex();
            mSCOPE_INDEX_EXPR(); if (state.failed) return ;
            index = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, indexStart242, getCharIndex()-1);
            match(']'); if (state.failed) return ;
            match("::"); if (state.failed) return ;

            int yStart250 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart250, getCharIndex()-1);
            // ActionSplitter.g:74:3: ( WS )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( ((LA6_0>='\t' && LA6_0<='\n')||LA6_0=='\r'||LA6_0==' ') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ActionSplitter.g:74:3: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            // ActionSplitter.g:74:7: ( '=' expr= ATTR_VALUE_EXPR ';' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='=') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ActionSplitter.g:74:8: '=' expr= ATTR_VALUE_EXPR ';'
                    {
                    match('='); if (state.failed) return ;
                    int exprStart262 = getCharIndex();
                    mATTR_VALUE_EXPR(); if (state.failed) return ;
                    expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart262, getCharIndex()-1);
                    match(';'); if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==1 ) {
              delegate.setDynamicNegativeIndexedScopeAttr(getText(), x, y, index, expr);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR"

    // $ANTLR start "DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR"
    public final void mDYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR() throws RecognitionException {
        try {
            int _type = DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken index=null;
            CommonToken y=null;

            // ActionSplitter.g:79:2: ( '$' x= ID '[' '-' index= SCOPE_INDEX_EXPR ']' '::' y= ID )
            // ActionSplitter.g:79:4: '$' x= ID '[' '-' index= SCOPE_INDEX_EXPR ']' '::' y= ID
            {
            match('$'); if (state.failed) return ;
            int xStart285 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart285, getCharIndex()-1);
            match('['); if (state.failed) return ;
            match('-'); if (state.failed) return ;
            int indexStart293 = getCharIndex();
            mSCOPE_INDEX_EXPR(); if (state.failed) return ;
            index = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, indexStart293, getCharIndex()-1);
            match(']'); if (state.failed) return ;
            match("::"); if (state.failed) return ;

            int yStart301 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart301, getCharIndex()-1);
            if ( state.backtracking==1 ) {
              delegate.dynamicNegativeIndexedScopeAttr(getText(), x, y, index);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR"

    // $ANTLR start "SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR"
    public final void mSET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR() throws RecognitionException {
        try {
            int _type = SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken index=null;
            CommonToken y=null;
            CommonToken expr=null;

            // ActionSplitter.g:84:2: ( '$' x= ID '[' index= SCOPE_INDEX_EXPR ']' '::' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:84:4: '$' x= ID '[' index= SCOPE_INDEX_EXPR ']' '::' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('$'); if (state.failed) return ;
            int xStart320 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart320, getCharIndex()-1);
            match('['); if (state.failed) return ;
            int indexStart326 = getCharIndex();
            mSCOPE_INDEX_EXPR(); if (state.failed) return ;
            index = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, indexStart326, getCharIndex()-1);
            match(']'); if (state.failed) return ;
            match("::"); if (state.failed) return ;

            int yStart334 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart334, getCharIndex()-1);
            // ActionSplitter.g:85:3: ( WS )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( ((LA8_0>='\t' && LA8_0<='\n')||LA8_0=='\r'||LA8_0==' ') ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ActionSplitter.g:85:3: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart345 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart345, getCharIndex()-1);
            match(';'); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              delegate.setDynamicAbsoluteIndexedScopeAttr(getText(), x, y, index, expr);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR"

    // $ANTLR start "DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR"
    public final void mDYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR() throws RecognitionException {
        try {
            int _type = DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken index=null;
            CommonToken y=null;

            // ActionSplitter.g:90:2: ( '$' x= ID '[' index= SCOPE_INDEX_EXPR ']' '::' y= ID )
            // ActionSplitter.g:90:4: '$' x= ID '[' index= SCOPE_INDEX_EXPR ']' '::' y= ID
            {
            match('$'); if (state.failed) return ;
            int xStart366 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart366, getCharIndex()-1);
            match('['); if (state.failed) return ;
            int indexStart372 = getCharIndex();
            mSCOPE_INDEX_EXPR(); if (state.failed) return ;
            index = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, indexStart372, getCharIndex()-1);
            match(']'); if (state.failed) return ;
            match("::"); if (state.failed) return ;

            int yStart380 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart380, getCharIndex()-1);
            if ( state.backtracking==1 ) {
              delegate.dynamicAbsoluteIndexedScopeAttr(getText(), x, y, index);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR"

    // $ANTLR start "SET_ATTR"
    public final void mSET_ATTR() throws RecognitionException {
        try {
            int _type = SET_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken expr=null;

            // ActionSplitter.g:95:2: ( '$' x= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:95:4: '$' x= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('$'); if (state.failed) return ;
            int xStart399 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart399, getCharIndex()-1);
            // ActionSplitter.g:95:13: ( WS )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( ((LA9_0>='\t' && LA9_0<='\n')||LA9_0=='\r'||LA9_0==' ') ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // ActionSplitter.g:95:13: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart408 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart408, getCharIndex()-1);
            match(';'); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              delegate.setAttr(getText(), x, expr);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET_ATTR"

    // $ANTLR start "ATTR"
    public final void mATTR() throws RecognitionException {
        try {
            int _type = ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;

            // ActionSplitter.g:99:2: ( '$' x= ID )
            // ActionSplitter.g:99:4: '$' x= ID
            {
            match('$'); if (state.failed) return ;
            int xStart427 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart427, getCharIndex()-1);
            if ( state.backtracking==1 ) {
              delegate.attr(getText(), x);
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ATTR"

    // $ANTLR start "TEMPLATE_INSTANCE"
    public final void mTEMPLATE_INSTANCE() throws RecognitionException {
        try {
            int _type = TEMPLATE_INSTANCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ActionSplitter.g:104:2: ( '%' ID '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')' )
            // ActionSplitter.g:104:4: '%' ID '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')'
            {
            match('%'); if (state.failed) return ;
            mID(); if (state.failed) return ;
            match('('); if (state.failed) return ;
            // ActionSplitter.g:104:15: ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>='\t' && LA14_0<='\n')||LA14_0=='\r'||LA14_0==' '||(LA14_0>='A' && LA14_0<='Z')||LA14_0=='_'||(LA14_0>='a' && LA14_0<='z')) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ActionSplitter.g:104:17: ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )?
                    {
                    // ActionSplitter.g:104:17: ( WS )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( ((LA10_0>='\t' && LA10_0<='\n')||LA10_0=='\r'||LA10_0==' ') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // ActionSplitter.g:104:17: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    mARG(); if (state.failed) return ;
                    // ActionSplitter.g:104:25: ( ',' ( WS )? ARG )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==',') ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // ActionSplitter.g:104:26: ',' ( WS )? ARG
                    	    {
                    	    match(','); if (state.failed) return ;
                    	    // ActionSplitter.g:104:30: ( WS )?
                    	    int alt11=2;
                    	    int LA11_0 = input.LA(1);

                    	    if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {
                    	        alt11=1;
                    	    }
                    	    switch (alt11) {
                    	        case 1 :
                    	            // ActionSplitter.g:104:30: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    mARG(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop12;
                        }
                    } while (true);

                    // ActionSplitter.g:104:40: ( WS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( ((LA13_0>='\t' && LA13_0<='\n')||LA13_0=='\r'||LA13_0==' ') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // ActionSplitter.g:104:40: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(')'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TEMPLATE_INSTANCE"

    // $ANTLR start "INDIRECT_TEMPLATE_INSTANCE"
    public final void mINDIRECT_TEMPLATE_INSTANCE() throws RecognitionException {
        try {
            int _type = INDIRECT_TEMPLATE_INSTANCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ActionSplitter.g:109:2: ( '%' '(' ACTION ')' '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')' )
            // ActionSplitter.g:109:4: '%' '(' ACTION ')' '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')'
            {
            match('%'); if (state.failed) return ;
            match('('); if (state.failed) return ;
            mACTION(); if (state.failed) return ;
            match(')'); if (state.failed) return ;
            match('('); if (state.failed) return ;
            // ActionSplitter.g:109:27: ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>='\t' && LA19_0<='\n')||LA19_0=='\r'||LA19_0==' '||(LA19_0>='A' && LA19_0<='Z')||LA19_0=='_'||(LA19_0>='a' && LA19_0<='z')) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ActionSplitter.g:109:29: ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )?
                    {
                    // ActionSplitter.g:109:29: ( WS )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( ((LA15_0>='\t' && LA15_0<='\n')||LA15_0=='\r'||LA15_0==' ') ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // ActionSplitter.g:109:29: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    mARG(); if (state.failed) return ;
                    // ActionSplitter.g:109:37: ( ',' ( WS )? ARG )*
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( (LA17_0==',') ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // ActionSplitter.g:109:38: ',' ( WS )? ARG
                    	    {
                    	    match(','); if (state.failed) return ;
                    	    // ActionSplitter.g:109:42: ( WS )?
                    	    int alt16=2;
                    	    int LA16_0 = input.LA(1);

                    	    if ( ((LA16_0>='\t' && LA16_0<='\n')||LA16_0=='\r'||LA16_0==' ') ) {
                    	        alt16=1;
                    	    }
                    	    switch (alt16) {
                    	        case 1 :
                    	            // ActionSplitter.g:109:42: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    mARG(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop17;
                        }
                    } while (true);

                    // ActionSplitter.g:109:52: ( WS )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( ((LA18_0>='\t' && LA18_0<='\n')||LA18_0=='\r'||LA18_0==' ') ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // ActionSplitter.g:109:52: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(')'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INDIRECT_TEMPLATE_INSTANCE"

    // $ANTLR start "SET_EXPR_ATTRIBUTE"
    public final void mSET_EXPR_ATTRIBUTE() throws RecognitionException {
        try {
            int _type = SET_EXPR_ATTRIBUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken a=null;
            CommonToken expr=null;

            // ActionSplitter.g:114:2: ( '%' a= ACTION '.' ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:114:4: '%' a= ACTION '.' ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('%'); if (state.failed) return ;
            int aStart534 = getCharIndex();
            mACTION(); if (state.failed) return ;
            a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart534, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            mID(); if (state.failed) return ;
            // ActionSplitter.g:114:24: ( WS )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( ((LA20_0>='\t' && LA20_0<='\n')||LA20_0=='\r'||LA20_0==' ') ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ActionSplitter.g:114:24: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart547 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart547, getCharIndex()-1);
            match(';'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET_EXPR_ATTRIBUTE"

    // $ANTLR start "SET_ATTRIBUTE"
    public final void mSET_ATTRIBUTE() throws RecognitionException {
        try {
            int _type = SET_ATTRIBUTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken y=null;
            CommonToken expr=null;

            // ActionSplitter.g:122:2: ( '%' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:122:4: '%' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('%'); if (state.failed) return ;
            int xStart567 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart567, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            int yStart573 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart573, getCharIndex()-1);
            // ActionSplitter.g:122:22: ( WS )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( ((LA21_0>='\t' && LA21_0<='\n')||LA21_0=='\r'||LA21_0==' ') ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ActionSplitter.g:122:22: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart582 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart582, getCharIndex()-1);
            match(';'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET_ATTRIBUTE"

    // $ANTLR start "TEMPLATE_EXPR"
    public final void mTEMPLATE_EXPR() throws RecognitionException {
        try {
            int _type = TEMPLATE_EXPR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken a=null;

            // ActionSplitter.g:127:2: ( '%' a= ACTION )
            // ActionSplitter.g:127:4: '%' a= ACTION
            {
            match('%'); if (state.failed) return ;
            int aStart601 = getCharIndex();
            mACTION(); if (state.failed) return ;
            a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart601, getCharIndex()-1);

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TEMPLATE_EXPR"

    // $ANTLR start "UNKNOWN_SYNTAX"
    public final void mUNKNOWN_SYNTAX() throws RecognitionException {
        try {
            int _type = UNKNOWN_SYNTAX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ActionSplitter.g:132:2: ( '%' ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )* )
            // ActionSplitter.g:132:4: '%' ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )*
            {
            match('%'); if (state.failed) return ;
            // ActionSplitter.g:132:8: ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )*
            loop22:
            do {
                int alt22=9;
                alt22 = dfa22.predict(input);
                switch (alt22) {
            	case 1 :
            	    // ActionSplitter.g:132:9: ID
            	    {
            	    mID(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // ActionSplitter.g:132:12: '.'
            	    {
            	    match('.'); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // ActionSplitter.g:132:16: '('
            	    {
            	    match('('); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // ActionSplitter.g:132:20: ')'
            	    {
            	    match(')'); if (state.failed) return ;

            	    }
            	    break;
            	case 5 :
            	    // ActionSplitter.g:132:24: ','
            	    {
            	    match(','); if (state.failed) return ;

            	    }
            	    break;
            	case 6 :
            	    // ActionSplitter.g:132:28: '{'
            	    {
            	    match('{'); if (state.failed) return ;

            	    }
            	    break;
            	case 7 :
            	    // ActionSplitter.g:132:32: '}'
            	    {
            	    match('}'); if (state.failed) return ;

            	    }
            	    break;
            	case 8 :
            	    // ActionSplitter.g:132:36: '\"'
            	    {
            	    match('\"'); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop22;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
            if ( state.backtracking==1 ) {
              delegate.unknownSyntax(getText());
            }    }
        finally {
        }
    }
    // $ANTLR end "UNKNOWN_SYNTAX"

    // $ANTLR start "TEXT"
    public final void mTEXT() throws RecognitionException {
        try {
            int _type = TEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ActionSplitter.g:138:2: ( ( '\\\\$' | '\\\\%' )+ )
            // ActionSplitter.g:138:4: ( '\\\\$' | '\\\\%' )+
            {
            // ActionSplitter.g:138:4: ( '\\\\$' | '\\\\%' )+
            int cnt23=0;
            loop23:
            do {
                int alt23=3;
                int LA23_0 = input.LA(1);

                if ( (LA23_0=='\\') ) {
                    int LA23_2 = input.LA(2);

                    if ( (LA23_2=='$') ) {
                        alt23=1;
                    }
                    else if ( (LA23_2=='%') ) {
                        alt23=2;
                    }


                }


                switch (alt23) {
            	case 1 :
            	    // ActionSplitter.g:138:6: '\\\\$'
            	    {
            	    match("\\$"); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // ActionSplitter.g:139:5: '\\\\%'
            	    {
            	    match("\\%"); if (state.failed) return ;


            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
            if ( state.backtracking==1 ) {
              delegate.text(getText());
            }    }
        finally {
        }
    }
    // $ANTLR end "TEXT"

    // $ANTLR start "ACTION"
    public final void mACTION() throws RecognitionException {
        try {
            // ActionSplitter.g:145:2: ( '{' ( '\\\\}' | ~ '}' )* '}' )
            // ActionSplitter.g:145:4: '{' ( '\\\\}' | ~ '}' )* '}'
            {
            match('{'); if (state.failed) return ;
            // ActionSplitter.g:145:8: ( '\\\\}' | ~ '}' )*
            loop24:
            do {
                int alt24=3;
                int LA24_0 = input.LA(1);

                if ( (LA24_0=='\\') ) {
                    int LA24_2 = input.LA(2);

                    if ( (LA24_2=='}') ) {
                        int LA24_4 = input.LA(3);

                        if ( ((LA24_4>='\u0000' && LA24_4<='\uFFFF')) ) {
                            alt24=1;
                        }

                        else {
                            alt24=2;
                        }

                    }
                    else if ( ((LA24_2>='\u0000' && LA24_2<='|')||(LA24_2>='~' && LA24_2<='\uFFFF')) ) {
                        alt24=2;
                    }


                }
                else if ( ((LA24_0>='\u0000' && LA24_0<='[')||(LA24_0>=']' && LA24_0<='|')||(LA24_0>='~' && LA24_0<='\uFFFF')) ) {
                    alt24=2;
                }


                switch (alt24) {
            	case 1 :
            	    // ActionSplitter.g:145:9: '\\\\}'
            	    {
            	    match("\\}"); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // ActionSplitter.g:145:15: ~ '}'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='|')||(input.LA(1)>='~' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

            match('}'); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "ACTION"

    // $ANTLR start "ARG"
    public final void mARG() throws RecognitionException {
        try {
            // ActionSplitter.g:149:5: ( ID '=' ACTION )
            // ActionSplitter.g:149:7: ID '=' ACTION
            {
            mID(); if (state.failed) return ;
            match('='); if (state.failed) return ;
            mACTION(); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "ARG"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            // ActionSplitter.g:153:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // ActionSplitter.g:153:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ActionSplitter.g:153:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>='0' && LA25_0<='9')||(LA25_0>='A' && LA25_0<='Z')||LA25_0=='_'||(LA25_0>='a' && LA25_0<='z')) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ActionSplitter.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "ATTR_VALUE_EXPR"
    public final void mATTR_VALUE_EXPR() throws RecognitionException {
        try {
            // ActionSplitter.g:159:2: (~ '=' (~ ';' )* )
            // ActionSplitter.g:159:4: ~ '=' (~ ';' )*
            {
            if ( (input.LA(1)>='\u0000' && input.LA(1)<='<')||(input.LA(1)>='>' && input.LA(1)<='\uFFFF') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // ActionSplitter.g:159:9: (~ ';' )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>='\u0000' && LA26_0<=':')||(LA26_0>='<' && LA26_0<='\uFFFF')) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ActionSplitter.g:159:10: ~ ';'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<=':')||(input.LA(1)>='<' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "ATTR_VALUE_EXPR"

    // $ANTLR start "SCOPE_INDEX_EXPR"
    public final void mSCOPE_INDEX_EXPR() throws RecognitionException {
        try {
            // ActionSplitter.g:164:2: ( ( '\\\\]' | ~ ']' )+ )
            // ActionSplitter.g:164:4: ( '\\\\]' | ~ ']' )+
            {
            // ActionSplitter.g:164:4: ( '\\\\]' | ~ ']' )+
            int cnt27=0;
            loop27:
            do {
                int alt27=3;
                int LA27_0 = input.LA(1);

                if ( (LA27_0=='\\') ) {
                    int LA27_2 = input.LA(2);

                    if ( (LA27_2==']') ) {
                        alt27=1;
                    }

                    else {
                        alt27=2;
                    }

                }
                else if ( ((LA27_0>='\u0000' && LA27_0<='[')||(LA27_0>='^' && LA27_0<='\uFFFF')) ) {
                    alt27=2;
                }


                switch (alt27) {
            	case 1 :
            	    // ActionSplitter.g:164:5: '\\\\]'
            	    {
            	    match("\\]"); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // ActionSplitter.g:164:11: ~ ']'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\\')||(input.LA(1)>='^' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "SCOPE_INDEX_EXPR"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            // ActionSplitter.g:168:4: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // ActionSplitter.g:168:6: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // ActionSplitter.g:168:6: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt28=0;
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( ((LA28_0>='\t' && LA28_0<='\n')||LA28_0=='\r'||LA28_0==' ') ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ActionSplitter.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    if ( cnt28 >= 1 ) break loop28;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(28, input);
                        throw eee;
                }
                cnt28++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // ActionSplitter.g:1:39: ( COMMENT | LINE_COMMENT | SET_QUALIFIED_ATTR | QUALIFIED_ATTR | SET_DYNAMIC_SCOPE_ATTR | DYNAMIC_SCOPE_ATTR | SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | SET_ATTR | ATTR | TEMPLATE_INSTANCE | INDIRECT_TEMPLATE_INSTANCE | SET_EXPR_ATTRIBUTE | SET_ATTRIBUTE | TEMPLATE_EXPR | UNKNOWN_SYNTAX | TEXT )
        int alt29=19;
        alt29 = dfa29.predict(input);
        switch (alt29) {
            case 1 :
                // ActionSplitter.g:1:41: COMMENT
                {
                mCOMMENT(); if (state.failed) return ;

                }
                break;
            case 2 :
                // ActionSplitter.g:1:49: LINE_COMMENT
                {
                mLINE_COMMENT(); if (state.failed) return ;

                }
                break;
            case 3 :
                // ActionSplitter.g:1:62: SET_QUALIFIED_ATTR
                {
                mSET_QUALIFIED_ATTR(); if (state.failed) return ;

                }
                break;
            case 4 :
                // ActionSplitter.g:1:81: QUALIFIED_ATTR
                {
                mQUALIFIED_ATTR(); if (state.failed) return ;

                }
                break;
            case 5 :
                // ActionSplitter.g:1:96: SET_DYNAMIC_SCOPE_ATTR
                {
                mSET_DYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 6 :
                // ActionSplitter.g:1:119: DYNAMIC_SCOPE_ATTR
                {
                mDYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 7 :
                // ActionSplitter.g:1:138: SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
                {
                mSET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 8 :
                // ActionSplitter.g:1:178: DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
                {
                mDYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 9 :
                // ActionSplitter.g:1:214: SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
                {
                mSET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 10 :
                // ActionSplitter.g:1:254: DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
                {
                mDYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 11 :
                // ActionSplitter.g:1:290: SET_ATTR
                {
                mSET_ATTR(); if (state.failed) return ;

                }
                break;
            case 12 :
                // ActionSplitter.g:1:299: ATTR
                {
                mATTR(); if (state.failed) return ;

                }
                break;
            case 13 :
                // ActionSplitter.g:1:304: TEMPLATE_INSTANCE
                {
                mTEMPLATE_INSTANCE(); if (state.failed) return ;

                }
                break;
            case 14 :
                // ActionSplitter.g:1:322: INDIRECT_TEMPLATE_INSTANCE
                {
                mINDIRECT_TEMPLATE_INSTANCE(); if (state.failed) return ;

                }
                break;
            case 15 :
                // ActionSplitter.g:1:349: SET_EXPR_ATTRIBUTE
                {
                mSET_EXPR_ATTRIBUTE(); if (state.failed) return ;

                }
                break;
            case 16 :
                // ActionSplitter.g:1:368: SET_ATTRIBUTE
                {
                mSET_ATTRIBUTE(); if (state.failed) return ;

                }
                break;
            case 17 :
                // ActionSplitter.g:1:382: TEMPLATE_EXPR
                {
                mTEMPLATE_EXPR(); if (state.failed) return ;

                }
                break;
            case 18 :
                // ActionSplitter.g:1:396: UNKNOWN_SYNTAX
                {
                mUNKNOWN_SYNTAX(); if (state.failed) return ;

                }
                break;
            case 19 :
                // ActionSplitter.g:1:411: TEXT
                {
                mTEXT(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_ActionSplitter
    public final void synpred1_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:41: ( COMMENT )
        // ActionSplitter.g:1:41: COMMENT
        {
        mCOMMENT(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_ActionSplitter

    // $ANTLR start synpred2_ActionSplitter
    public final void synpred2_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:49: ( LINE_COMMENT )
        // ActionSplitter.g:1:49: LINE_COMMENT
        {
        mLINE_COMMENT(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_ActionSplitter

    // $ANTLR start synpred3_ActionSplitter
    public final void synpred3_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:62: ( SET_QUALIFIED_ATTR )
        // ActionSplitter.g:1:62: SET_QUALIFIED_ATTR
        {
        mSET_QUALIFIED_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_ActionSplitter

    // $ANTLR start synpred4_ActionSplitter
    public final void synpred4_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:81: ( QUALIFIED_ATTR )
        // ActionSplitter.g:1:81: QUALIFIED_ATTR
        {
        mQUALIFIED_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_ActionSplitter

    // $ANTLR start synpred5_ActionSplitter
    public final void synpred5_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:96: ( SET_DYNAMIC_SCOPE_ATTR )
        // ActionSplitter.g:1:96: SET_DYNAMIC_SCOPE_ATTR
        {
        mSET_DYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_ActionSplitter

    // $ANTLR start synpred6_ActionSplitter
    public final void synpred6_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:119: ( DYNAMIC_SCOPE_ATTR )
        // ActionSplitter.g:1:119: DYNAMIC_SCOPE_ATTR
        {
        mDYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_ActionSplitter

    // $ANTLR start synpred7_ActionSplitter
    public final void synpred7_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:138: ( SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:138: SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
        {
        mSET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_ActionSplitter

    // $ANTLR start synpred8_ActionSplitter
    public final void synpred8_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:178: ( DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:178: DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
        {
        mDYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_ActionSplitter

    // $ANTLR start synpred9_ActionSplitter
    public final void synpred9_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:214: ( SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:214: SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
        {
        mSET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_ActionSplitter

    // $ANTLR start synpred10_ActionSplitter
    public final void synpred10_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:254: ( DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:254: DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
        {
        mDYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_ActionSplitter

    // $ANTLR start synpred11_ActionSplitter
    public final void synpred11_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:290: ( SET_ATTR )
        // ActionSplitter.g:1:290: SET_ATTR
        {
        mSET_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_ActionSplitter

    // $ANTLR start synpred12_ActionSplitter
    public final void synpred12_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:299: ( ATTR )
        // ActionSplitter.g:1:299: ATTR
        {
        mATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_ActionSplitter

    // $ANTLR start synpred13_ActionSplitter
    public final void synpred13_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:304: ( TEMPLATE_INSTANCE )
        // ActionSplitter.g:1:304: TEMPLATE_INSTANCE
        {
        mTEMPLATE_INSTANCE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_ActionSplitter

    // $ANTLR start synpred14_ActionSplitter
    public final void synpred14_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:322: ( INDIRECT_TEMPLATE_INSTANCE )
        // ActionSplitter.g:1:322: INDIRECT_TEMPLATE_INSTANCE
        {
        mINDIRECT_TEMPLATE_INSTANCE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_ActionSplitter

    // $ANTLR start synpred15_ActionSplitter
    public final void synpred15_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:349: ( SET_EXPR_ATTRIBUTE )
        // ActionSplitter.g:1:349: SET_EXPR_ATTRIBUTE
        {
        mSET_EXPR_ATTRIBUTE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_ActionSplitter

    // $ANTLR start synpred16_ActionSplitter
    public final void synpred16_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:368: ( SET_ATTRIBUTE )
        // ActionSplitter.g:1:368: SET_ATTRIBUTE
        {
        mSET_ATTRIBUTE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_ActionSplitter

    // $ANTLR start synpred17_ActionSplitter
    public final void synpred17_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:382: ( TEMPLATE_EXPR )
        // ActionSplitter.g:1:382: TEMPLATE_EXPR
        {
        mTEMPLATE_EXPR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred17_ActionSplitter

    // $ANTLR start synpred18_ActionSplitter
    public final void synpred18_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:396: ( UNKNOWN_SYNTAX )
        // ActionSplitter.g:1:396: UNKNOWN_SYNTAX
        {
        mUNKNOWN_SYNTAX(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred18_ActionSplitter

    public final boolean synpred10_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred17_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred18_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_ActionSplitter() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_ActionSplitter_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA22 dfa22 = new DFA22(this);
    protected DFA29 dfa29 = new DFA29(this);
    static final String DFA22_eotS =
        "\1\1\11\uffff";
    static final String DFA22_eofS =
        "\12\uffff";
    static final String DFA22_minS =
        "\1\42\11\uffff";
    static final String DFA22_maxS =
        "\1\175\11\uffff";
    static final String DFA22_acceptS =
        "\1\uffff\1\11\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA22_specialS =
        "\12\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\11\5\uffff\1\4\1\5\2\uffff\1\6\1\uffff\1\3\22\uffff\32\2"+
            "\4\uffff\1\2\1\uffff\32\2\1\7\1\uffff\1\10",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "()* loopback of 132:8: ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )*";
        }
    }
    static final String DFA29_eotS =
        "\27\uffff";
    static final String DFA29_eofS =
        "\27\uffff";
    static final String DFA29_minS =
        "\1\44\1\0\2\uffff\1\0\7\uffff\1\0\12\uffff";
    static final String DFA29_maxS =
        "\1\134\1\0\2\uffff\1\0\7\uffff\1\0\12\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\1\1\1\2\1\uffff\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\uffff"+
        "\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14";
    static final String DFA29_specialS =
        "\1\uffff\1\0\2\uffff\1\1\7\uffff\1\2\12\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\14\1\4\11\uffff\1\1\54\uffff\1\13",
            "\1\uffff",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "1:1: Tokens options {k=1; backtrack=true; } : ( COMMENT | LINE_COMMENT | SET_QUALIFIED_ATTR | QUALIFIED_ATTR | SET_DYNAMIC_SCOPE_ATTR | DYNAMIC_SCOPE_ATTR | SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | SET_ATTR | ATTR | TEMPLATE_INSTANCE | INDIRECT_TEMPLATE_INSTANCE | SET_EXPR_ATTRIBUTE | SET_ATTRIBUTE | TEMPLATE_EXPR | UNKNOWN_SYNTAX | TEXT );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_1 = input.LA(1);

                         
                        int index29_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_ActionSplitter()) ) {s = 2;}

                        else if ( (synpred2_ActionSplitter()) ) {s = 3;}

                         
                        input.seek(index29_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA29_4 = input.LA(1);

                         
                        int index29_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred13_ActionSplitter()) ) {s = 5;}

                        else if ( (synpred14_ActionSplitter()) ) {s = 6;}

                        else if ( (synpred15_ActionSplitter()) ) {s = 7;}

                        else if ( (synpred16_ActionSplitter()) ) {s = 8;}

                        else if ( (synpred17_ActionSplitter()) ) {s = 9;}

                        else if ( (synpred18_ActionSplitter()) ) {s = 10;}

                         
                        input.seek(index29_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA29_12 = input.LA(1);

                         
                        int index29_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_ActionSplitter()) ) {s = 13;}

                        else if ( (synpred4_ActionSplitter()) ) {s = 14;}

                        else if ( (synpred5_ActionSplitter()) ) {s = 15;}

                        else if ( (synpred6_ActionSplitter()) ) {s = 16;}

                        else if ( (synpred7_ActionSplitter()) ) {s = 17;}

                        else if ( (synpred8_ActionSplitter()) ) {s = 18;}

                        else if ( (synpred9_ActionSplitter()) ) {s = 19;}

                        else if ( (synpred10_ActionSplitter()) ) {s = 20;}

                        else if ( (synpred11_ActionSplitter()) ) {s = 21;}

                        else if ( (synpred12_ActionSplitter()) ) {s = 22;}

                         
                        input.seek(index29_12);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 29, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}