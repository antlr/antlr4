// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ActionSplitter.g 2010-02-10 15:47:23

package org.antlr.v4.parse;
import org.antlr.v4.tool.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class ActionSplitter extends Lexer {
    public static final int INDIRECT_TEMPLATE_INSTANCE=21;
    public static final int DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR=11;
    public static final int SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR=12;
    public static final int SET_ATTRIBUTE=23;
    public static final int TEMPLATE_EXPR=24;
    public static final int TEXT=26;
    public static final int ID=4;
    public static final int QUALIFIED_ATTR=8;
    public static final int EOF=-1;
    public static final int ACTION=20;
    public static final int UNKNOWN_SYNTAX=25;
    public static final int SET_QUALIFIED_ATTR=7;
    public static final int SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR=15;
    public static final int WS=5;
    public static final int ARG=18;
    public static final int TEMPLATE_INSTANCE=19;
    public static final int SET_EXPR_ATTRIBUTE=22;
    public static final int ATTR_VALUE_EXPR=6;
    public static final int SET_DYNAMIC_SCOPE_ATTR=10;
    public static final int SCOPE_INDEX_EXPR=13;
    public static final int DYNAMIC_SCOPE_ATTR=9;
    public static final int SET_ATTR=17;
    public static final int ATTR=16;
    public static final int DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR=14;

    public void setQualifiedAttr() { }
    public void setDynamicScopeAttr() { }
    public void dynamicScopeAttr() { }
    public void setDynamicNegativeIndexedScopeAttr() { }
    public void dynamicNegativeIndexedScopeAttr() { }
    public void setDynamicAbsoluteIndexedScopeAttr() { }
    public void dynamicAbsoluteIndexedScopeAttr() { }
    public void setAttr() { }
    public void attr() { }
    public void templateInstance() { }
    public void indirectTemplateInstance() { }
    public void setExprAttribute() { }
    public void setAttribute() { }
    public void templateExpr() { }
    public void unknownSyntax() { }
    public void text() { }


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
    }// $ANTLR start "SET_QUALIFIED_ATTR"
    public final void mSET_QUALIFIED_ATTR() throws RecognitionException {
        try {
            int _type = SET_QUALIFIED_ATTR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken x=null;
            CommonToken y=null;
            CommonToken expr=null;

            // ActionSplitter.g:30:2: ( '$' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:30:4: '$' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('$'); if (state.failed) return ;
            int xStart39 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart39, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            int yStart45 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart45, getCharIndex()-1);
            // ActionSplitter.g:30:22: ( WS )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( ((LA1_0>='\t' && LA1_0<='\n')||LA1_0=='\r'||LA1_0==' ') ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ActionSplitter.g:30:22: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart54 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart54, getCharIndex()-1);
            match(';'); if (state.failed) return ;

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

            // ActionSplitter.g:34:2: ( '$' x= ID '.' y= ID {...}?)
            // ActionSplitter.g:34:4: '$' x= ID '.' y= ID {...}?
            {
            match('$'); if (state.failed) return ;
            int xStart71 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart71, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            int yStart77 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart77, getCharIndex()-1);
            if ( !((input.LA(1)!='(')) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "QUALIFIED_ATTR", "input.LA(1)!='('");
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
            CommonToken expr=null;

            // ActionSplitter.g:38:2: ( DYNAMIC_SCOPE_ATTR ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:38:4: DYNAMIC_SCOPE_ATTR ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            mDYNAMIC_SCOPE_ATTR(); if (state.failed) return ;
            // ActionSplitter.g:38:23: ( WS )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0>='\t' && LA2_0<='\n')||LA2_0=='\r'||LA2_0==' ') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ActionSplitter.g:38:23: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart99 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart99, getCharIndex()-1);
            match(';'); if (state.failed) return ;

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

            // ActionSplitter.g:42:2: ( '$' x= ID '::' y= ID )
            // ActionSplitter.g:42:4: '$' x= ID '::' y= ID
            {
            match('$'); if (state.failed) return ;
            int xStart116 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart116, getCharIndex()-1);
            match("::"); if (state.failed) return ;

            int yStart122 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart122, getCharIndex()-1);

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
            CommonToken expr=null;

            // ActionSplitter.g:54:2: ( DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR ( WS )? ( '=' expr= ATTR_VALUE_EXPR ';' )? )
            // ActionSplitter.g:54:4: DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR ( WS )? ( '=' expr= ATTR_VALUE_EXPR ';' )?
            {
            mDYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;
            // ActionSplitter.g:55:3: ( WS )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>='\t' && LA3_0<='\n')||LA3_0=='\r'||LA3_0==' ') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // ActionSplitter.g:55:3: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            // ActionSplitter.g:55:7: ( '=' expr= ATTR_VALUE_EXPR ';' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='=') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // ActionSplitter.g:55:8: '=' expr= ATTR_VALUE_EXPR ';'
                    {
                    match('='); if (state.failed) return ;
                    int exprStart147 = getCharIndex();
                    mATTR_VALUE_EXPR(); if (state.failed) return ;
                    expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart147, getCharIndex()-1);
                    match(';'); if (state.failed) return ;

                    }
                    break;

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
            CommonToken expr=null;
            CommonToken y=null;

            // ActionSplitter.g:59:2: ( '$' x= ID '[' '-' expr= SCOPE_INDEX_EXPR ']' '::' y= ID )
            // ActionSplitter.g:59:4: '$' x= ID '[' '-' expr= SCOPE_INDEX_EXPR ']' '::' y= ID
            {
            match('$'); if (state.failed) return ;
            int xStart166 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart166, getCharIndex()-1);
            match('['); if (state.failed) return ;
            match('-'); if (state.failed) return ;
            int exprStart174 = getCharIndex();
            mSCOPE_INDEX_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart174, getCharIndex()-1);
            match(']'); if (state.failed) return ;
            match("::"); if (state.failed) return ;

            int yStart182 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart182, getCharIndex()-1);

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
            CommonToken expr=null;

            // ActionSplitter.g:63:2: ( DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR ( WS )? ( '=' expr= ATTR_VALUE_EXPR ';' )? )
            // ActionSplitter.g:63:4: DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR ( WS )? ( '=' expr= ATTR_VALUE_EXPR ';' )?
            {
            mDYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;
            // ActionSplitter.g:64:3: ( WS )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( ((LA5_0>='\t' && LA5_0<='\n')||LA5_0=='\r'||LA5_0==' ') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // ActionSplitter.g:64:3: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            // ActionSplitter.g:64:7: ( '=' expr= ATTR_VALUE_EXPR ';' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='=') ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ActionSplitter.g:64:8: '=' expr= ATTR_VALUE_EXPR ';'
                    {
                    match('='); if (state.failed) return ;
                    int exprStart205 = getCharIndex();
                    mATTR_VALUE_EXPR(); if (state.failed) return ;
                    expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart205, getCharIndex()-1);
                    match(';'); if (state.failed) return ;

                    }
                    break;

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
            CommonToken expr=null;
            CommonToken y=null;

            // ActionSplitter.g:68:2: ( '$' x= ID '[' expr= SCOPE_INDEX_EXPR ']' '::' y= ID )
            // ActionSplitter.g:68:4: '$' x= ID '[' expr= SCOPE_INDEX_EXPR ']' '::' y= ID
            {
            match('$'); if (state.failed) return ;
            int xStart224 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart224, getCharIndex()-1);
            match('['); if (state.failed) return ;
            int exprStart230 = getCharIndex();
            mSCOPE_INDEX_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart230, getCharIndex()-1);
            match(']'); if (state.failed) return ;
            match("::"); if (state.failed) return ;

            int yStart238 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart238, getCharIndex()-1);

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
            CommonToken expr=null;

            // ActionSplitter.g:72:2: ( ATTR ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:72:4: ATTR ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            mATTR(); if (state.failed) return ;
            // ActionSplitter.g:72:9: ( WS )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( ((LA7_0>='\t' && LA7_0<='\n')||LA7_0=='\r'||LA7_0==' ') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ActionSplitter.g:72:9: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart258 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart258, getCharIndex()-1);
            match(';'); if (state.failed) return ;

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
            // ActionSplitter.g:76:2: ( '$' ID )
            // ActionSplitter.g:76:4: '$' ID
            {
            match('$'); if (state.failed) return ;
            mID(); if (state.failed) return ;

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
            // ActionSplitter.g:81:2: ( '%' ID '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')' )
            // ActionSplitter.g:81:4: '%' ID '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')'
            {
            match('%'); if (state.failed) return ;
            mID(); if (state.failed) return ;
            match('('); if (state.failed) return ;
            // ActionSplitter.g:81:15: ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( ((LA12_0>='\t' && LA12_0<='\n')||LA12_0=='\r'||LA12_0==' '||(LA12_0>='A' && LA12_0<='Z')||LA12_0=='_'||(LA12_0>='a' && LA12_0<='z')) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ActionSplitter.g:81:17: ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )?
                    {
                    // ActionSplitter.g:81:17: ( WS )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( ((LA8_0>='\t' && LA8_0<='\n')||LA8_0=='\r'||LA8_0==' ') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // ActionSplitter.g:81:17: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    mARG(); if (state.failed) return ;
                    // ActionSplitter.g:81:25: ( ',' ( WS )? ARG )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==',') ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // ActionSplitter.g:81:26: ',' ( WS )? ARG
                    	    {
                    	    match(','); if (state.failed) return ;
                    	    // ActionSplitter.g:81:30: ( WS )?
                    	    int alt9=2;
                    	    int LA9_0 = input.LA(1);

                    	    if ( ((LA9_0>='\t' && LA9_0<='\n')||LA9_0=='\r'||LA9_0==' ') ) {
                    	        alt9=1;
                    	    }
                    	    switch (alt9) {
                    	        case 1 :
                    	            // ActionSplitter.g:81:30: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    mARG(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);

                    // ActionSplitter.g:81:40: ( WS )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( ((LA11_0>='\t' && LA11_0<='\n')||LA11_0=='\r'||LA11_0==' ') ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // ActionSplitter.g:81:40: WS
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
            // ActionSplitter.g:86:2: ( '%' '(' ACTION ')' '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')' )
            // ActionSplitter.g:86:4: '%' '(' ACTION ')' '(' ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )? ')'
            {
            match('%'); if (state.failed) return ;
            match('('); if (state.failed) return ;
            mACTION(); if (state.failed) return ;
            match(')'); if (state.failed) return ;
            match('('); if (state.failed) return ;
            // ActionSplitter.g:86:27: ( ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )? )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>='\t' && LA17_0<='\n')||LA17_0=='\r'||LA17_0==' '||(LA17_0>='A' && LA17_0<='Z')||LA17_0=='_'||(LA17_0>='a' && LA17_0<='z')) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ActionSplitter.g:86:29: ( WS )? ARG ( ',' ( WS )? ARG )* ( WS )?
                    {
                    // ActionSplitter.g:86:29: ( WS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( ((LA13_0>='\t' && LA13_0<='\n')||LA13_0=='\r'||LA13_0==' ') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // ActionSplitter.g:86:29: WS
                            {
                            mWS(); if (state.failed) return ;

                            }
                            break;

                    }

                    mARG(); if (state.failed) return ;
                    // ActionSplitter.g:86:37: ( ',' ( WS )? ARG )*
                    loop15:
                    do {
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==',') ) {
                            alt15=1;
                        }


                        switch (alt15) {
                    	case 1 :
                    	    // ActionSplitter.g:86:38: ',' ( WS )? ARG
                    	    {
                    	    match(','); if (state.failed) return ;
                    	    // ActionSplitter.g:86:42: ( WS )?
                    	    int alt14=2;
                    	    int LA14_0 = input.LA(1);

                    	    if ( ((LA14_0>='\t' && LA14_0<='\n')||LA14_0=='\r'||LA14_0==' ') ) {
                    	        alt14=1;
                    	    }
                    	    switch (alt14) {
                    	        case 1 :
                    	            // ActionSplitter.g:86:42: WS
                    	            {
                    	            mWS(); if (state.failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    mARG(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);

                    // ActionSplitter.g:86:52: ( WS )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( ((LA16_0>='\t' && LA16_0<='\n')||LA16_0=='\r'||LA16_0==' ') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // ActionSplitter.g:86:52: WS
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

            // ActionSplitter.g:91:2: ( '%' a= ACTION '.' ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:91:4: '%' a= ACTION '.' ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('%'); if (state.failed) return ;
            int aStart378 = getCharIndex();
            mACTION(); if (state.failed) return ;
            a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart378, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            mID(); if (state.failed) return ;
            // ActionSplitter.g:91:24: ( WS )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( ((LA18_0>='\t' && LA18_0<='\n')||LA18_0=='\r'||LA18_0==' ') ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ActionSplitter.g:91:24: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart391 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart391, getCharIndex()-1);
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

            // ActionSplitter.g:99:2: ( '%' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';' )
            // ActionSplitter.g:99:4: '%' x= ID '.' y= ID ( WS )? '=' expr= ATTR_VALUE_EXPR ';'
            {
            match('%'); if (state.failed) return ;
            int xStart411 = getCharIndex();
            mID(); if (state.failed) return ;
            x = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, xStart411, getCharIndex()-1);
            match('.'); if (state.failed) return ;
            int yStart417 = getCharIndex();
            mID(); if (state.failed) return ;
            y = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, yStart417, getCharIndex()-1);
            // ActionSplitter.g:99:22: ( WS )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>='\t' && LA19_0<='\n')||LA19_0=='\r'||LA19_0==' ') ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ActionSplitter.g:99:22: WS
                    {
                    mWS(); if (state.failed) return ;

                    }
                    break;

            }

            match('='); if (state.failed) return ;
            int exprStart426 = getCharIndex();
            mATTR_VALUE_EXPR(); if (state.failed) return ;
            expr = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, exprStart426, getCharIndex()-1);
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

            // ActionSplitter.g:104:2: ( '%' a= ACTION )
            // ActionSplitter.g:104:4: '%' a= ACTION
            {
            match('%'); if (state.failed) return ;
            int aStart445 = getCharIndex();
            mACTION(); if (state.failed) return ;
            a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart445, getCharIndex()-1);

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
            // ActionSplitter.g:108:2: ( '$' | '%' ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )* )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0=='$') ) {
                alt21=1;
            }
            else if ( (LA21_0=='%') ) {
                alt21=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // ActionSplitter.g:108:4: '$'
                    {
                    match('$'); if (state.failed) return ;
                    if ( state.backtracking==1 ) {

                      //		chunks.add(getText());
                      		// shouldn't need an error here.  Just accept $ if it doesn't look like anything
                      		
                    }

                    }
                    break;
                case 2 :
                    // ActionSplitter.g:113:4: '%' ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )*
                    {
                    match('%'); if (state.failed) return ;
                    // ActionSplitter.g:113:8: ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )*
                    loop20:
                    do {
                        int alt20=9;
                        alt20 = dfa20.predict(input);
                        switch (alt20) {
                    	case 1 :
                    	    // ActionSplitter.g:113:9: ID
                    	    {
                    	    mID(); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 2 :
                    	    // ActionSplitter.g:113:12: '.'
                    	    {
                    	    match('.'); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 3 :
                    	    // ActionSplitter.g:113:16: '('
                    	    {
                    	    match('('); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 4 :
                    	    // ActionSplitter.g:113:20: ')'
                    	    {
                    	    match(')'); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 5 :
                    	    // ActionSplitter.g:113:24: ','
                    	    {
                    	    match(','); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 6 :
                    	    // ActionSplitter.g:113:28: '{'
                    	    {
                    	    match('{'); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 7 :
                    	    // ActionSplitter.g:113:32: '}'
                    	    {
                    	    match('}'); if (state.failed) return ;

                    	    }
                    	    break;
                    	case 8 :
                    	    // ActionSplitter.g:113:36: '\"'
                    	    {
                    	    match('\"'); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop20;
                        }
                    } while (true);

                    if ( state.backtracking==1 ) {

                      /*
                      		chunks.add(getText());
                      		ErrorManager.grammarError(ErrorManager.MSG_INVALID_TEMPLATE_ACTION,
                      								  grammar,
                      								  actionToken,
                      								  getText());
                      */
                      		
                    }

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNKNOWN_SYNTAX"

    // $ANTLR start "TEXT"
    public final void mTEXT() throws RecognitionException {
        try {
            int _type = TEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ActionSplitter.g:125:5: ( ( '\\\\$' | '\\\\%' | ~ ( '$' | '%' ) )+ )
            // ActionSplitter.g:125:7: ( '\\\\$' | '\\\\%' | ~ ( '$' | '%' ) )+
            {
            // ActionSplitter.g:125:7: ( '\\\\$' | '\\\\%' | ~ ( '$' | '%' ) )+
            int cnt22=0;
            loop22:
            do {
                int alt22=4;
                int LA22_0 = input.LA(1);

                if ( (LA22_0=='\\') ) {
                    switch ( input.LA(2) ) {
                    case '$':
                        {
                        alt22=1;
                        }
                        break;
                    case '%':
                        {
                        alt22=2;
                        }
                        break;

                    default:
                        alt22=3;
                        break;
                    }

                }
                else if ( ((LA22_0>='\u0000' && LA22_0<='#')||(LA22_0>='&' && LA22_0<='[')||(LA22_0>=']' && LA22_0<='\uFFFF')) ) {
                    alt22=3;
                }


                switch (alt22) {
            	case 1 :
            	    // ActionSplitter.g:125:9: '\\\\$'
            	    {
            	    match("\\$"); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // ActionSplitter.g:126:5: '\\\\%'
            	    {
            	    match("\\%"); if (state.failed) return ;


            	    }
            	    break;
            	case 3 :
            	    // ActionSplitter.g:127:5: ~ ( '$' | '%' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='#')||(input.LA(1)>='&' && input.LA(1)<='\uFFFF') ) {
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
            	    if ( cnt22 >= 1 ) break loop22;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TEXT"

    // $ANTLR start "ACTION"
    public final void mACTION() throws RecognitionException {
        try {
            // ActionSplitter.g:133:2: ( '{' ( '\\\\}' | ~ '}' )* '}' )
            // ActionSplitter.g:133:4: '{' ( '\\\\}' | ~ '}' )* '}'
            {
            match('{'); if (state.failed) return ;
            // ActionSplitter.g:133:8: ( '\\\\}' | ~ '}' )*
            loop23:
            do {
                int alt23=3;
                int LA23_0 = input.LA(1);

                if ( (LA23_0=='\\') ) {
                    int LA23_2 = input.LA(2);

                    if ( (LA23_2=='}') ) {
                        int LA23_4 = input.LA(3);

                        if ( ((LA23_4>='\u0000' && LA23_4<='\uFFFF')) ) {
                            alt23=1;
                        }

                        else {
                            alt23=2;
                        }

                    }
                    else if ( ((LA23_2>='\u0000' && LA23_2<='|')||(LA23_2>='~' && LA23_2<='\uFFFF')) ) {
                        alt23=2;
                    }


                }
                else if ( ((LA23_0>='\u0000' && LA23_0<='[')||(LA23_0>=']' && LA23_0<='|')||(LA23_0>='~' && LA23_0<='\uFFFF')) ) {
                    alt23=2;
                }


                switch (alt23) {
            	case 1 :
            	    // ActionSplitter.g:133:9: '\\\\}'
            	    {
            	    match("\\}"); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // ActionSplitter.g:133:15: ~ '}'
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
            	    break loop23;
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
            // ActionSplitter.g:137:5: ( ID '=' ACTION )
            // ActionSplitter.g:137:7: ID '=' ACTION
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
            // ActionSplitter.g:141:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // ActionSplitter.g:141:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
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

            // ActionSplitter.g:141:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>='0' && LA24_0<='9')||(LA24_0>='A' && LA24_0<='Z')||LA24_0=='_'||(LA24_0>='a' && LA24_0<='z')) ) {
                    alt24=1;
                }


                switch (alt24) {
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
            	    break loop24;
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
            // ActionSplitter.g:147:2: (~ '=' (~ ';' )* )
            // ActionSplitter.g:147:4: ~ '=' (~ ';' )*
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

            // ActionSplitter.g:147:9: (~ ';' )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>='\u0000' && LA25_0<=':')||(LA25_0>='<' && LA25_0<='\uFFFF')) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ActionSplitter.g:147:10: ~ ';'
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
            	    break loop25;
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
            // ActionSplitter.g:152:2: ( ( '\\\\]' | ~ ']' )+ )
            // ActionSplitter.g:152:4: ( '\\\\]' | ~ ']' )+
            {
            // ActionSplitter.g:152:4: ( '\\\\]' | ~ ']' )+
            int cnt26=0;
            loop26:
            do {
                int alt26=3;
                int LA26_0 = input.LA(1);

                if ( (LA26_0=='\\') ) {
                    int LA26_2 = input.LA(2);

                    if ( (LA26_2==']') ) {
                        alt26=1;
                    }

                    else {
                        alt26=2;
                    }

                }
                else if ( ((LA26_0>='\u0000' && LA26_0<='[')||(LA26_0>='^' && LA26_0<='\uFFFF')) ) {
                    alt26=2;
                }


                switch (alt26) {
            	case 1 :
            	    // ActionSplitter.g:152:5: '\\\\]'
            	    {
            	    match("\\]"); if (state.failed) return ;


            	    }
            	    break;
            	case 2 :
            	    // ActionSplitter.g:152:11: ~ ']'
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
            	    if ( cnt26 >= 1 ) break loop26;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(26, input);
                        throw eee;
                }
                cnt26++;
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
            // ActionSplitter.g:156:4: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // ActionSplitter.g:156:6: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // ActionSplitter.g:156:6: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0>='\t' && LA27_0<='\n')||LA27_0=='\r'||LA27_0==' ') ) {
                    alt27=1;
                }


                switch (alt27) {
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
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // ActionSplitter.g:1:39: ( SET_QUALIFIED_ATTR | QUALIFIED_ATTR | SET_DYNAMIC_SCOPE_ATTR | DYNAMIC_SCOPE_ATTR | SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | SET_ATTR | ATTR | TEMPLATE_INSTANCE | INDIRECT_TEMPLATE_INSTANCE | SET_EXPR_ATTRIBUTE | SET_ATTRIBUTE | TEMPLATE_EXPR | UNKNOWN_SYNTAX | TEXT )
        int alt28=17;
        alt28 = dfa28.predict(input);
        switch (alt28) {
            case 1 :
                // ActionSplitter.g:1:41: SET_QUALIFIED_ATTR
                {
                mSET_QUALIFIED_ATTR(); if (state.failed) return ;

                }
                break;
            case 2 :
                // ActionSplitter.g:1:60: QUALIFIED_ATTR
                {
                mQUALIFIED_ATTR(); if (state.failed) return ;

                }
                break;
            case 3 :
                // ActionSplitter.g:1:75: SET_DYNAMIC_SCOPE_ATTR
                {
                mSET_DYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 4 :
                // ActionSplitter.g:1:98: DYNAMIC_SCOPE_ATTR
                {
                mDYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 5 :
                // ActionSplitter.g:1:117: SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
                {
                mSET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 6 :
                // ActionSplitter.g:1:157: DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
                {
                mDYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 7 :
                // ActionSplitter.g:1:193: SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
                {
                mSET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 8 :
                // ActionSplitter.g:1:233: DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
                {
                mDYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

                }
                break;
            case 9 :
                // ActionSplitter.g:1:269: SET_ATTR
                {
                mSET_ATTR(); if (state.failed) return ;

                }
                break;
            case 10 :
                // ActionSplitter.g:1:278: ATTR
                {
                mATTR(); if (state.failed) return ;

                }
                break;
            case 11 :
                // ActionSplitter.g:1:283: TEMPLATE_INSTANCE
                {
                mTEMPLATE_INSTANCE(); if (state.failed) return ;

                }
                break;
            case 12 :
                // ActionSplitter.g:1:301: INDIRECT_TEMPLATE_INSTANCE
                {
                mINDIRECT_TEMPLATE_INSTANCE(); if (state.failed) return ;

                }
                break;
            case 13 :
                // ActionSplitter.g:1:328: SET_EXPR_ATTRIBUTE
                {
                mSET_EXPR_ATTRIBUTE(); if (state.failed) return ;

                }
                break;
            case 14 :
                // ActionSplitter.g:1:347: SET_ATTRIBUTE
                {
                mSET_ATTRIBUTE(); if (state.failed) return ;

                }
                break;
            case 15 :
                // ActionSplitter.g:1:361: TEMPLATE_EXPR
                {
                mTEMPLATE_EXPR(); if (state.failed) return ;

                }
                break;
            case 16 :
                // ActionSplitter.g:1:375: UNKNOWN_SYNTAX
                {
                mUNKNOWN_SYNTAX(); if (state.failed) return ;

                }
                break;
            case 17 :
                // ActionSplitter.g:1:390: TEXT
                {
                mTEXT(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_ActionSplitter
    public final void synpred1_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:41: ( SET_QUALIFIED_ATTR )
        // ActionSplitter.g:1:41: SET_QUALIFIED_ATTR
        {
        mSET_QUALIFIED_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_ActionSplitter

    // $ANTLR start synpred2_ActionSplitter
    public final void synpred2_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:60: ( QUALIFIED_ATTR )
        // ActionSplitter.g:1:60: QUALIFIED_ATTR
        {
        mQUALIFIED_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_ActionSplitter

    // $ANTLR start synpred3_ActionSplitter
    public final void synpred3_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:75: ( SET_DYNAMIC_SCOPE_ATTR )
        // ActionSplitter.g:1:75: SET_DYNAMIC_SCOPE_ATTR
        {
        mSET_DYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_ActionSplitter

    // $ANTLR start synpred4_ActionSplitter
    public final void synpred4_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:98: ( DYNAMIC_SCOPE_ATTR )
        // ActionSplitter.g:1:98: DYNAMIC_SCOPE_ATTR
        {
        mDYNAMIC_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_ActionSplitter

    // $ANTLR start synpred5_ActionSplitter
    public final void synpred5_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:117: ( SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:117: SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
        {
        mSET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_ActionSplitter

    // $ANTLR start synpred6_ActionSplitter
    public final void synpred6_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:157: ( DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:157: DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR
        {
        mDYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_ActionSplitter

    // $ANTLR start synpred7_ActionSplitter
    public final void synpred7_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:193: ( SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:193: SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
        {
        mSET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_ActionSplitter

    // $ANTLR start synpred8_ActionSplitter
    public final void synpred8_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:233: ( DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR )
        // ActionSplitter.g:1:233: DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR
        {
        mDYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_ActionSplitter

    // $ANTLR start synpred9_ActionSplitter
    public final void synpred9_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:269: ( SET_ATTR )
        // ActionSplitter.g:1:269: SET_ATTR
        {
        mSET_ATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_ActionSplitter

    // $ANTLR start synpred10_ActionSplitter
    public final void synpred10_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:278: ( ATTR )
        // ActionSplitter.g:1:278: ATTR
        {
        mATTR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_ActionSplitter

    // $ANTLR start synpred11_ActionSplitter
    public final void synpred11_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:283: ( TEMPLATE_INSTANCE )
        // ActionSplitter.g:1:283: TEMPLATE_INSTANCE
        {
        mTEMPLATE_INSTANCE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_ActionSplitter

    // $ANTLR start synpred12_ActionSplitter
    public final void synpred12_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:301: ( INDIRECT_TEMPLATE_INSTANCE )
        // ActionSplitter.g:1:301: INDIRECT_TEMPLATE_INSTANCE
        {
        mINDIRECT_TEMPLATE_INSTANCE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_ActionSplitter

    // $ANTLR start synpred13_ActionSplitter
    public final void synpred13_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:328: ( SET_EXPR_ATTRIBUTE )
        // ActionSplitter.g:1:328: SET_EXPR_ATTRIBUTE
        {
        mSET_EXPR_ATTRIBUTE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_ActionSplitter

    // $ANTLR start synpred14_ActionSplitter
    public final void synpred14_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:347: ( SET_ATTRIBUTE )
        // ActionSplitter.g:1:347: SET_ATTRIBUTE
        {
        mSET_ATTRIBUTE(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_ActionSplitter

    // $ANTLR start synpred15_ActionSplitter
    public final void synpred15_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:361: ( TEMPLATE_EXPR )
        // ActionSplitter.g:1:361: TEMPLATE_EXPR
        {
        mTEMPLATE_EXPR(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_ActionSplitter

    // $ANTLR start synpred16_ActionSplitter
    public final void synpred16_ActionSplitter_fragment() throws RecognitionException {   
        // ActionSplitter.g:1:375: ( UNKNOWN_SYNTAX )
        // ActionSplitter.g:1:375: UNKNOWN_SYNTAX
        {
        mUNKNOWN_SYNTAX(); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred16_ActionSplitter

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


    protected DFA20 dfa20 = new DFA20(this);
    protected DFA28 dfa28 = new DFA28(this);
    static final String DFA20_eotS =
        "\1\1\11\uffff";
    static final String DFA20_eofS =
        "\12\uffff";
    static final String DFA20_minS =
        "\1\42\11\uffff";
    static final String DFA20_maxS =
        "\1\175\11\uffff";
    static final String DFA20_acceptS =
        "\1\uffff\1\11\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA20_specialS =
        "\12\uffff}>";
    static final String[] DFA20_transitionS = {
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

    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;

    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }

    class DFA20 extends DFA {

        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "()* loopback of 113:8: ( ID | '.' | '(' | ')' | ',' | '{' | '}' | '\"' )*";
        }
    }
    static final String DFA28_eotS =
        "\24\uffff";
    static final String DFA28_eofS =
        "\24\uffff";
    static final String DFA28_minS =
        "\1\0\1\uffff\1\0\13\uffff\1\0\5\uffff";
    static final String DFA28_maxS =
        "\1\uffff\1\uffff\1\0\13\uffff\1\0\5\uffff";
    static final String DFA28_acceptS =
        "\1\uffff\1\21\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12"+
        "\1\20\1\uffff\1\13\1\14\1\15\1\16\1\17";
    static final String DFA28_specialS =
        "\1\0\1\uffff\1\1\13\uffff\1\2\5\uffff}>";
    static final String[] DFA28_transitionS = {
            "\44\1\1\2\1\16\uffda\1",
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
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "1:1: Tokens options {k=1; backtrack=true; } : ( SET_QUALIFIED_ATTR | QUALIFIED_ATTR | SET_DYNAMIC_SCOPE_ATTR | DYNAMIC_SCOPE_ATTR | SET_DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | DYNAMIC_NEGATIVE_INDEXED_SCOPE_ATTR | SET_DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | DYNAMIC_ABSOLUTE_INDEXED_SCOPE_ATTR | SET_ATTR | ATTR | TEMPLATE_INSTANCE | INDIRECT_TEMPLATE_INSTANCE | SET_EXPR_ATTRIBUTE | SET_ATTRIBUTE | TEMPLATE_EXPR | UNKNOWN_SYNTAX | TEXT );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA28_0 = input.LA(1);

                        s = -1;
                        if ( ((LA28_0>='\u0000' && LA28_0<='#')||(LA28_0>='&' && LA28_0<='\uFFFF')) ) {s = 1;}

                        else if ( (LA28_0=='$') ) {s = 2;}

                        else if ( (LA28_0=='%') ) {s = 14;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA28_2 = input.LA(1);

                         
                        int index28_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_ActionSplitter()) ) {s = 3;}

                        else if ( (synpred2_ActionSplitter()) ) {s = 4;}

                        else if ( (synpred3_ActionSplitter()) ) {s = 5;}

                        else if ( (synpred4_ActionSplitter()) ) {s = 6;}

                        else if ( (synpred5_ActionSplitter()) ) {s = 7;}

                        else if ( (synpred6_ActionSplitter()) ) {s = 8;}

                        else if ( (synpred7_ActionSplitter()) ) {s = 9;}

                        else if ( (synpred8_ActionSplitter()) ) {s = 10;}

                        else if ( (synpred9_ActionSplitter()) ) {s = 11;}

                        else if ( (synpred10_ActionSplitter()) ) {s = 12;}

                        else if ( (synpred16_ActionSplitter()) ) {s = 13;}

                         
                        input.seek(index28_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA28_14 = input.LA(1);

                         
                        int index28_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred11_ActionSplitter()) ) {s = 15;}

                        else if ( (synpred12_ActionSplitter()) ) {s = 16;}

                        else if ( (synpred13_ActionSplitter()) ) {s = 17;}

                        else if ( (synpred14_ActionSplitter()) ) {s = 18;}

                        else if ( (synpred15_ActionSplitter()) ) {s = 19;}

                        else if ( (synpred16_ActionSplitter()) ) {s = 13;}

                         
                        input.seek(index28_14);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 28, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}