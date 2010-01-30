// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ANTLRLexer.g 2010-01-30 14:28:45

/*
 [The "BSD licence"]
 Copyright (c) 2005-2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.antlr.v4.parse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/** Read in an ANTLR grammar and build an AST.  Try not to do
 *  any actions, just build the tree.
 *
 *  The phases are:
 *
 *		A3Lexer.g (this file)
 *              A3Parser.g
 *              A3Verify.g (derived from A3Walker.g)
 *		assign.types.g
 *		define.g
 *		buildnfa.g
 *		antlr.print.g (optional)
 *		codegen.g
 *
 *  Terence Parr
 *  University of San Francisco
 *  2005
 *  Jim Idle (this v3 grammar)
 *  Temporal Wave LLC
 *  2009
 */
public class ANTLRLexer extends Lexer {
    public static final int DOLLAR=53;
    public static final int PROTECTED=28;
    public static final int LT=43;
    public static final int STAR=48;
    public static final int NESTED_ACTION=15;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int FORCED_ACTION=5;
    public static final int FRAGMENT=23;
    public static final int NOT=60;
    public static final int TREE_BEGIN=58;
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int ACTION=16;
    public static final int TOKEN_REF=62;
    public static final int LPAREN=40;
    public static final int AT=59;
    public static final int RPAREN=41;
    public static final int IMPORT=22;
    public static final int ESC_SEQ=67;
    public static final int STRING_LITERAL=66;
    public static final int SCOPE=21;
    public static final int TREE=26;
    public static final int ETC=56;
    public static final int COMMA=38;
    public static final int WILDCARD=54;
    public static final int ARG_ACTION=14;
    public static final int DOC_COMMENT=6;
    public static final int PLUS=49;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=10;
    public static final int COMMENT=9;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int GRAMMAR=27;
    public static final int WSCHARS=65;
    public static final int RETURNS=31;
    public static final int IMPLIES=42;
    public static final int RBRACE=61;
    public static final int ACTION_ESC=17;
    public static final int PRIVATE=30;
    public static final int UNICODE_ESC=70;
    public static final int RARROW=57;
    public static final int SRC=7;
    public static final int HEX_DIGIT=69;
    public static final int RANGE=55;
    public static final int TOKENS=20;
    public static final int THROWS=32;
    public static final int INT=64;
    public static final int BANG=47;
    public static final int ACTION_STRING_LITERAL=12;
    public static final int ROOT=52;
    public static final int SEMI=39;
    public static final int RULE_REF=63;
    public static final int NLCHARS=8;
    public static final int COLON=36;
    public static final int COLONCOLON=37;
    public static final int WSNLCHARS=18;
    public static final int WS=71;
    public static final int QUESTION=46;
    public static final int CHAR_LITERAL=68;
    public static final int FINALLY=34;
    public static final int TEMPLATE=35;
    public static final int LEXER=24;
    public static final int ERRCHAR=72;
    public static final int OR=51;
    public static final int PLUS_ASSIGN=50;
    public static final int ASSIGN=45;
    public static final int GT=44;
    public static final int CATCH=33;
    public static final int PARSER=25;
    public static final int PUBLIC=29;
    public static final int OPTIONS=19;

    // delegates
    // delegators

    public ANTLRLexer() {;} 
    public ANTLRLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public ANTLRLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "ANTLRLexer.g"; }

    // $ANTLR start "DOC_COMMENT"
    public final void mDOC_COMMENT() throws RecognitionException {
        try {
            // ANTLRLexer.g:135:22: ()
            // ANTLRLexer.g:135:24: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "DOC_COMMENT"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;


            	// Record the start line and offsets as if we need to report an
            	// unterminated comment, then we want to show the start of the comment
            	// we think is broken, not the end, where people will have to try and work
            	// it out themselves. 
            	//
            	int startLine = state.tokenStartLine;
            	int offset    = getCharPositionInLine();

            // ANTLRLexer.g:147:5: ( '/' ( '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* ) | '*' ({...}? => '*' | {...}? =>) ({...}? . )* ( '*/' | ) | ) )
            // ANTLRLexer.g:150:7: '/' ( '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* ) | '*' ({...}? => '*' | {...}? =>) ({...}? . )* ( '*/' | ) | )
            {
            match('/'); if (state.failed) return ;
            // ANTLRLexer.g:152:7: ( '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* ) | '*' ({...}? => '*' | {...}? =>) ({...}? . )* ( '*/' | ) | )
            int alt6=3;
            switch ( input.LA(1) ) {
            case '/':
                {
                alt6=1;
                }
                break;
            case '*':
                {
                alt6=2;
                }
                break;
            default:
                alt6=3;}

            switch (alt6) {
                case 1 :
                    // ANTLRLexer.g:158:11: '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* )
                    {
                    match('/'); if (state.failed) return ;
                    // ANTLRLexer.g:159:13: ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* )
                    int alt2=2;
                    alt2 = dfa2.predict(input);
                    switch (alt2) {
                        case 1 :
                            // ANTLRLexer.g:160:17: ( ' $ANTLR' )=> ' $ANTLR' SRC
                            {
                            match(" $ANTLR"); if (state.failed) return ;

                            mSRC(); if (state.failed) return ;

                            }
                            break;
                        case 2 :
                            // ANTLRLexer.g:161:17: (~ ( NLCHARS ) )*
                            {
                            // ANTLRLexer.g:161:17: (~ ( NLCHARS ) )*
                            loop1:
                            do {
                                int alt1=2;
                                int LA1_0 = input.LA(1);

                                if ( ((LA1_0>='\u0000' && LA1_0<='\t')||(LA1_0>='\u000B' && LA1_0<='\f')||(LA1_0>='\u000E' && LA1_0<='\uFFFF')) ) {
                                    alt1=1;
                                }


                                switch (alt1) {
                            	case 1 :
                            	    // ANTLRLexer.g:161:17: ~ ( NLCHARS )
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
                            	    break loop1;
                                }
                            } while (true);


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // ANTLRLexer.g:168:12: '*' ({...}? => '*' | {...}? =>) ({...}? . )* ( '*/' | )
                    {
                    match('*'); if (state.failed) return ;
                    // ANTLRLexer.g:168:16: ({...}? => '*' | {...}? =>)
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='*') && ((( input.LA(2) != '/')||( true )))) {
                        int LA3_1 = input.LA(2);

                        if ( (( input.LA(2) != '/')) ) {
                            alt3=1;
                        }
                        else if ( (((( true )&&(    !(input.LA(1) == '*' && input.LA(2) == '/') ))||( true ))) ) {
                            alt3=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return ;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 3, 1, input);

                            throw nvae;
                        }
                    }
                    else {
                        alt3=2;}
                    switch (alt3) {
                        case 1 :
                            // ANTLRLexer.g:169:17: {...}? => '*'
                            {
                            if ( !(( input.LA(2) != '/')) ) {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                throw new FailedPredicateException(input, "COMMENT", " input.LA(2) != '/'");
                            }
                            match('*'); if (state.failed) return ;
                            if ( state.backtracking==0 ) {
                               _type = DOC_COMMENT; 
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRLexer.g:170:17: {...}? =>
                            {
                            if ( !(( true )) ) {
                                if (state.backtracking>0) {state.failed=true; return ;}
                                throw new FailedPredicateException(input, "COMMENT", " true ");
                            }

                            }
                            break;

                    }

                    // ANTLRLexer.g:175:16: ({...}? . )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0=='*') ) {
                            int LA4_1 = input.LA(2);

                            if ( (LA4_1=='/') ) {
                                int LA4_4 = input.LA(3);

                                if ( ((    !(input.LA(1) == '*' && input.LA(2) == '/') )) ) {
                                    alt4=1;
                                }


                            }

                            else {
                                alt4=1;
                            }

                        }
                        else if ( ((LA4_0>='\u0000' && LA4_0<=')')||(LA4_0>='+' && LA4_0<='\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // ANTLRLexer.g:179:20: {...}? .
                    	    {
                    	    if ( !((    !(input.LA(1) == '*' && input.LA(2) == '/') )) ) {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        throw new FailedPredicateException(input, "COMMENT", "    !(input.LA(1) == '*' && input.LA(2) == '/') ");
                    	    }
                    	    matchAny(); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);

                    // ANTLRLexer.g:186:13: ( '*/' | )
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='*') ) {
                        alt5=1;
                    }
                    else {
                        alt5=2;}
                    switch (alt5) {
                        case 1 :
                            // ANTLRLexer.g:192:18: '*/'
                            {
                            match("*/"); if (state.failed) return ;


                            }
                            break;
                        case 2 :
                            // ANTLRLexer.g:196:18: 
                            {
                            if ( state.backtracking==0 ) {

                                                 // ErrorManager.msg(Msg.UNTERMINATED_DOC_COMMENT, startLine, offset, state.tokenStartCharPositionInLine, startLine, offset, state.tokenStartCharPositionInLine, (Object)null);
                                               
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // ANTLRLexer.g:204:12: 
                    {
                    if ( state.backtracking==0 ) {

                                 	 // TODO: Insert error message relative to comment start
                                   //
                                 
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {

                       // Unless we had a documentation comment, then we do not wish to
                       // pass the comments in to the parser. If you are writing a formatter
                       // then you will want to preserve the comments off channel, but could
                       // just skip and save token space if not.
                       //
                       if (_type != DOC_COMMENT) {
                        
                           _channel=2;  // Comments are on channel 2
                       }
                     
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "DOUBLE_QUOTE_STRING_LITERAL"
    public final void mDOUBLE_QUOTE_STRING_LITERAL() throws RecognitionException {
        try {
            int _type = DOUBLE_QUOTE_STRING_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:224:2: ( '\"' ( ( '\\\\' )=> '\\\\' . | ~ '\"' )* '\"' )
            // ANTLRLexer.g:224:4: '\"' ( ( '\\\\' )=> '\\\\' . | ~ '\"' )* '\"'
            {
            match('\"'); if (state.failed) return ;
            // ANTLRLexer.g:224:8: ( ( '\\\\' )=> '\\\\' . | ~ '\"' )*
            loop7:
            do {
                int alt7=3;
                alt7 = dfa7.predict(input);
                switch (alt7) {
            	case 1 :
            	    // ANTLRLexer.g:224:9: ( '\\\\' )=> '\\\\' .
            	    {
            	    match('\\'); if (state.failed) return ;
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // ANTLRLexer.g:224:26: ~ '\"'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
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
            	    break loop7;
                }
            } while (true);

            match('\"'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_QUOTE_STRING_LITERAL"

    // $ANTLR start "DOUBLE_ANGLE_STRING_LITERAL"
    public final void mDOUBLE_ANGLE_STRING_LITERAL() throws RecognitionException {
        try {
            int _type = DOUBLE_ANGLE_STRING_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:228:2: ( '<<' ( options {greedy=false; } : . )* '>>' )
            // ANTLRLexer.g:228:4: '<<' ( options {greedy=false; } : . )* '>>'
            {
            match("<<"); if (state.failed) return ;

            // ANTLRLexer.g:228:9: ( options {greedy=false; } : . )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='>') ) {
                    int LA8_1 = input.LA(2);

                    if ( (LA8_1=='>') ) {
                        alt8=2;
                    }
                    else if ( ((LA8_1>='\u0000' && LA8_1<='=')||(LA8_1>='?' && LA8_1<='\uFFFF')) ) {
                        alt8=1;
                    }


                }
                else if ( ((LA8_0>='\u0000' && LA8_0<='=')||(LA8_0>='?' && LA8_0<='\uFFFF')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ANTLRLexer.g:228:36: .
            	    {
            	    matchAny(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            match(">>"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_ANGLE_STRING_LITERAL"

    // $ANTLR start "ARG_ACTION"
    public final void mARG_ACTION() throws RecognitionException {
        try {
            int _type = ARG_ACTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            CommonToken as=null;
            CommonToken ac=null;
            int c;


            	StringBuffer theText = new StringBuffer();

            // ANTLRLexer.g:244:2: ( '[' ( ( '\\\\' )=> '\\\\' ( ( ']' )=> ']' | c= . ) | ( '\"' )=>as= ACTION_STRING_LITERAL | ( '\\'' )=>ac= ACTION_CHAR_LITERAL | c=~ ']' )* ']' )
            // ANTLRLexer.g:244:4: '[' ( ( '\\\\' )=> '\\\\' ( ( ']' )=> ']' | c= . ) | ( '\"' )=>as= ACTION_STRING_LITERAL | ( '\\'' )=>ac= ACTION_CHAR_LITERAL | c=~ ']' )* ']'
            {
            match('['); if (state.failed) return ;
            // ANTLRLexer.g:245:10: ( ( '\\\\' )=> '\\\\' ( ( ']' )=> ']' | c= . ) | ( '\"' )=>as= ACTION_STRING_LITERAL | ( '\\'' )=>ac= ACTION_CHAR_LITERAL | c=~ ']' )*
            loop10:
            do {
                int alt10=5;
                alt10 = dfa10.predict(input);
                switch (alt10) {
            	case 1 :
            	    // ANTLRLexer.g:246:14: ( '\\\\' )=> '\\\\' ( ( ']' )=> ']' | c= . )
            	    {
            	    match('\\'); if (state.failed) return ;
            	    // ANTLRLexer.g:247:18: ( ( ']' )=> ']' | c= . )
            	    int alt9=2;
            	    int LA9_0 = input.LA(1);

            	    if ( (LA9_0==']') ) {
            	        int LA9_1 = input.LA(2);

            	        if ( (synpred4_ANTLRLexer()) ) {
            	            alt9=1;
            	        }
            	        else if ( (true) ) {
            	            alt9=2;
            	        }
            	        else {
            	            if (state.backtracking>0) {state.failed=true; return ;}
            	            NoViableAltException nvae =
            	                new NoViableAltException("", 9, 1, input);

            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA9_0>='\u0000' && LA9_0<='\\')||(LA9_0>='^' && LA9_0<='\uFFFF')) ) {
            	        alt9=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 9, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt9) {
            	        case 1 :
            	            // ANTLRLexer.g:248:22: ( ']' )=> ']'
            	            {
            	            match(']'); if (state.failed) return ;
            	            if ( state.backtracking==0 ) {

            	                                         // We do not include the \ character itself when picking up an escaped ] 
            	                                         //
            	                                         theText.append(']'); 
            	                                     
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // ANTLRLexer.g:254:22: c= .
            	            {
            	            c = input.LA(1);
            	            matchAny(); if (state.failed) return ;
            	            if ( state.backtracking==0 ) {

            	                                         // We DO include the \ character when finding any other escape
            	                                         //
            	                                         theText.append('\\');
            	                                         theText.append((char)c);
            	                                     
            	            }

            	            }
            	            break;

            	    }


            	    }
            	    break;
            	case 2 :
            	    // ANTLRLexer.g:263:14: ( '\"' )=>as= ACTION_STRING_LITERAL
            	    {
            	    int asStart1319 = getCharIndex();
            	    mACTION_STRING_LITERAL(); if (state.failed) return ;
            	    as = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, asStart1319, getCharIndex()-1);
            	    if ( state.backtracking==0 ) {

            	                          // Append the embedded string literal test
            	                          //
            	                          theText.append((as!=null?as.getText():null));
            	                      
            	    }

            	    }
            	    break;
            	case 3 :
            	    // ANTLRLexer.g:270:14: ( '\\'' )=>ac= ACTION_CHAR_LITERAL
            	    {
            	    int acStart1370 = getCharIndex();
            	    mACTION_CHAR_LITERAL(); if (state.failed) return ;
            	    ac = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, acStart1370, getCharIndex()-1);
            	    if ( state.backtracking==0 ) {

            	                          // Append the embedded chracter literal text
            	                          //
            	                          theText.append((ac!=null?ac.getText():null));
            	                      
            	    }

            	    }
            	    break;
            	case 4 :
            	    // ANTLRLexer.g:277:14: c=~ ']'
            	    {
            	    c= input.LA(1);
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\\')||(input.LA(1)>='^' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();
            	    state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}

            	    if ( state.backtracking==0 ) {

            	                          // Whatever else we found in the scan
            	                          //
            	                          theText.append((char)c);
            	                      
            	    }

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            match(']'); if (state.failed) return ;
            if ( state.backtracking==0 ) {

                         // Set the token text to our gathered string
                         //
                         setText(theText.toString());
                     
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARG_ACTION"

    // $ANTLR start "ACTION"
    public final void mACTION() throws RecognitionException {
        try {
            int _type = ACTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:308:2: ( NESTED_ACTION ( '?' )? )
            // ANTLRLexer.g:308:4: NESTED_ACTION ( '?' )?
            {
            mNESTED_ACTION(); if (state.failed) return ;
            // ANTLRLexer.g:308:18: ( '?' )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0=='?') ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ANTLRLexer.g:308:19: '?'
                    {
                    match('?'); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                      _type = SEMPRED;
                    }

                    }
                    break;

            }

            if ( state.backtracking==0 ) {

                          // Note that because of the sempred detection above, we
                          // will not see {{ action }}? as a forced action, but as a semantic
                          // predicate.
                          if ( getText().startsWith("{{") && getText().endsWith("}}") ) {            
                              // Switch types to a forced action
                              _type = FORCED_ACTION;
                          }          
              		
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ACTION"

    // $ANTLR start "NESTED_ACTION"
    public final void mNESTED_ACTION() throws RecognitionException {
        try {


            	// Record the start line and offsets as if we need to report an
            	// unterminated block, then we want to show the start of the comment
            	// we think is broken, not the end, where people will have to try and work
            	// it out themselves. 
            	//
            	int startLine = getLine();
            	int offset    = getCharPositionInLine();

            // ANTLRLexer.g:344:5: ( '{' ( NESTED_ACTION | ACTION_CHAR_LITERAL | COMMENT | ACTION_STRING_LITERAL | ACTION_ESC | ~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' ) )* ( '}' | ) )
            // ANTLRLexer.g:346:4: '{' ( NESTED_ACTION | ACTION_CHAR_LITERAL | COMMENT | ACTION_STRING_LITERAL | ACTION_ESC | ~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' ) )* ( '}' | )
            {
            match('{'); if (state.failed) return ;
            // ANTLRLexer.g:347:7: ( NESTED_ACTION | ACTION_CHAR_LITERAL | COMMENT | ACTION_STRING_LITERAL | ACTION_ESC | ~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' ) )*
            loop12:
            do {
                int alt12=7;
                int LA12_0 = input.LA(1);

                if ( (LA12_0=='{') ) {
                    alt12=1;
                }
                else if ( (LA12_0=='\'') ) {
                    alt12=2;
                }
                else if ( (LA12_0=='/') ) {
                    alt12=3;
                }
                else if ( (LA12_0=='\"') ) {
                    alt12=4;
                }
                else if ( (LA12_0=='\\') ) {
                    alt12=5;
                }
                else if ( ((LA12_0>='\u0000' && LA12_0<='!')||(LA12_0>='#' && LA12_0<='&')||(LA12_0>='(' && LA12_0<='.')||(LA12_0>='0' && LA12_0<='[')||(LA12_0>=']' && LA12_0<='z')||LA12_0=='|'||(LA12_0>='~' && LA12_0<='\uFFFF')) ) {
                    alt12=6;
                }


                switch (alt12) {
            	case 1 :
            	    // ANTLRLexer.g:362:8: NESTED_ACTION
            	    {
            	    mNESTED_ACTION(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // ANTLRLexer.g:366:11: ACTION_CHAR_LITERAL
            	    {
            	    mACTION_CHAR_LITERAL(); if (state.failed) return ;

            	    }
            	    break;
            	case 3 :
            	    // ANTLRLexer.g:371:11: COMMENT
            	    {
            	    mCOMMENT(); if (state.failed) return ;

            	    }
            	    break;
            	case 4 :
            	    // ANTLRLexer.g:375:11: ACTION_STRING_LITERAL
            	    {
            	    mACTION_STRING_LITERAL(); if (state.failed) return ;

            	    }
            	    break;
            	case 5 :
            	    // ANTLRLexer.g:379:8: ACTION_ESC
            	    {
            	    mACTION_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 6 :
            	    // ANTLRLexer.g:384:8: ~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='.')||(input.LA(1)>='0' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='z')||input.LA(1)=='|'||(input.LA(1)>='~' && input.LA(1)<='\uFFFF') ) {
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
            	    break loop12;
                }
            } while (true);

            // ANTLRLexer.g:388:2: ( '}' | )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='}') ) {
                alt13=1;
            }
            else {
                alt13=2;}
            switch (alt13) {
                case 1 :
                    // ANTLRLexer.g:391:6: '}'
                    {
                    match('}'); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // ANTLRLexer.g:396:6: 
                    {
                    if ( state.backtracking==0 ) {

                      	        // TODO: Report imbalanced {}
                      	        System.out.println("Block starting  at line " + startLine + " offset " + (offset+1) + " contains imbalanced {} or is missing a }");
                      	    
                    }

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "NESTED_ACTION"

    // $ANTLR start "OPTIONS"
    public final void mOPTIONS() throws RecognitionException {
        try {
            int _type = OPTIONS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:413:14: ( 'options' ( WSNLCHARS )* '{' )
            // ANTLRLexer.g:413:16: 'options' ( WSNLCHARS )* '{'
            {
            match("options"); if (state.failed) return ;

            // ANTLRLexer.g:413:26: ( WSNLCHARS )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( ((LA14_0>='\t' && LA14_0<='\n')||(LA14_0>='\f' && LA14_0<='\r')||LA14_0==' ') ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ANTLRLexer.g:413:26: WSNLCHARS
            	    {
            	    mWSNLCHARS(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            match('{'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPTIONS"

    // $ANTLR start "TOKENS"
    public final void mTOKENS() throws RecognitionException {
        try {
            int _type = TOKENS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:414:14: ( 'tokens' ( WSNLCHARS )* '{' )
            // ANTLRLexer.g:414:16: 'tokens' ( WSNLCHARS )* '{'
            {
            match("tokens"); if (state.failed) return ;

            // ANTLRLexer.g:414:26: ( WSNLCHARS )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0>='\t' && LA15_0<='\n')||(LA15_0>='\f' && LA15_0<='\r')||LA15_0==' ') ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ANTLRLexer.g:414:26: WSNLCHARS
            	    {
            	    mWSNLCHARS(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            match('{'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TOKENS"

    // $ANTLR start "SCOPE"
    public final void mSCOPE() throws RecognitionException {
        try {
            int _type = SCOPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:416:14: ( 'scope' )
            // ANTLRLexer.g:416:16: 'scope'
            {
            match("scope"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SCOPE"

    // $ANTLR start "IMPORT"
    public final void mIMPORT() throws RecognitionException {
        try {
            int _type = IMPORT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:417:14: ( 'import' )
            // ANTLRLexer.g:417:16: 'import'
            {
            match("import"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IMPORT"

    // $ANTLR start "FRAGMENT"
    public final void mFRAGMENT() throws RecognitionException {
        try {
            int _type = FRAGMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:418:14: ( 'fragment' )
            // ANTLRLexer.g:418:16: 'fragment'
            {
            match("fragment"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FRAGMENT"

    // $ANTLR start "LEXER"
    public final void mLEXER() throws RecognitionException {
        try {
            int _type = LEXER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:419:14: ( 'lexer' )
            // ANTLRLexer.g:419:16: 'lexer'
            {
            match("lexer"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEXER"

    // $ANTLR start "PARSER"
    public final void mPARSER() throws RecognitionException {
        try {
            int _type = PARSER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:420:14: ( 'parser' )
            // ANTLRLexer.g:420:16: 'parser'
            {
            match("parser"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PARSER"

    // $ANTLR start "TREE"
    public final void mTREE() throws RecognitionException {
        try {
            int _type = TREE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:421:14: ( 'tree' )
            // ANTLRLexer.g:421:16: 'tree'
            {
            match("tree"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TREE"

    // $ANTLR start "GRAMMAR"
    public final void mGRAMMAR() throws RecognitionException {
        try {
            int _type = GRAMMAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:422:14: ( 'grammar' )
            // ANTLRLexer.g:422:16: 'grammar'
            {
            match("grammar"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GRAMMAR"

    // $ANTLR start "PROTECTED"
    public final void mPROTECTED() throws RecognitionException {
        try {
            int _type = PROTECTED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:423:14: ( 'protected' )
            // ANTLRLexer.g:423:16: 'protected'
            {
            match("protected"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PROTECTED"

    // $ANTLR start "PUBLIC"
    public final void mPUBLIC() throws RecognitionException {
        try {
            int _type = PUBLIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:424:14: ( 'public' )
            // ANTLRLexer.g:424:16: 'public'
            {
            match("public"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PUBLIC"

    // $ANTLR start "PRIVATE"
    public final void mPRIVATE() throws RecognitionException {
        try {
            int _type = PRIVATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:425:14: ( 'private' )
            // ANTLRLexer.g:425:16: 'private'
            {
            match("private"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PRIVATE"

    // $ANTLR start "RETURNS"
    public final void mRETURNS() throws RecognitionException {
        try {
            int _type = RETURNS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:426:14: ( 'returns' )
            // ANTLRLexer.g:426:16: 'returns'
            {
            match("returns"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RETURNS"

    // $ANTLR start "THROWS"
    public final void mTHROWS() throws RecognitionException {
        try {
            int _type = THROWS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:427:14: ( 'throws' )
            // ANTLRLexer.g:427:16: 'throws'
            {
            match("throws"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THROWS"

    // $ANTLR start "CATCH"
    public final void mCATCH() throws RecognitionException {
        try {
            int _type = CATCH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:428:14: ( 'catch' )
            // ANTLRLexer.g:428:16: 'catch'
            {
            match("catch"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CATCH"

    // $ANTLR start "FINALLY"
    public final void mFINALLY() throws RecognitionException {
        try {
            int _type = FINALLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:429:14: ( 'finally' )
            // ANTLRLexer.g:429:16: 'finally'
            {
            match("finally"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FINALLY"

    // $ANTLR start "TEMPLATE"
    public final void mTEMPLATE() throws RecognitionException {
        try {
            int _type = TEMPLATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:430:14: ( 'template' )
            // ANTLRLexer.g:430:16: 'template'
            {
            match("template"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TEMPLATE"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:437:14: ( ':' )
            // ANTLRLexer.g:437:16: ':'
            {
            match(':'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "COLONCOLON"
    public final void mCOLONCOLON() throws RecognitionException {
        try {
            int _type = COLONCOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:438:14: ( '::' )
            // ANTLRLexer.g:438:16: '::'
            {
            match("::"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLONCOLON"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:439:14: ( ',' )
            // ANTLRLexer.g:439:16: ','
            {
            match(','); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "SEMI"
    public final void mSEMI() throws RecognitionException {
        try {
            int _type = SEMI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:440:14: ( ';' )
            // ANTLRLexer.g:440:16: ';'
            {
            match(';'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMI"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:441:14: ( '(' )
            // ANTLRLexer.g:441:16: '('
            {
            match('('); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:442:14: ( ')' )
            // ANTLRLexer.g:442:16: ')'
            {
            match(')'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "IMPLIES"
    public final void mIMPLIES() throws RecognitionException {
        try {
            int _type = IMPLIES;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:443:14: ( '=>' )
            // ANTLRLexer.g:443:16: '=>'
            {
            match("=>"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IMPLIES"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:444:14: ( '<' )
            // ANTLRLexer.g:444:16: '<'
            {
            match('<'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:445:14: ( '>' )
            // ANTLRLexer.g:445:16: '>'
            {
            match('>'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:446:14: ( '=' )
            // ANTLRLexer.g:446:16: '='
            {
            match('='); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASSIGN"

    // $ANTLR start "QUESTION"
    public final void mQUESTION() throws RecognitionException {
        try {
            int _type = QUESTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:447:14: ( '?' )
            // ANTLRLexer.g:447:16: '?'
            {
            match('?'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "QUESTION"

    // $ANTLR start "BANG"
    public final void mBANG() throws RecognitionException {
        try {
            int _type = BANG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:448:14: ( '!' )
            // ANTLRLexer.g:448:16: '!'
            {
            match('!'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BANG"

    // $ANTLR start "STAR"
    public final void mSTAR() throws RecognitionException {
        try {
            int _type = STAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:449:14: ( '*' )
            // ANTLRLexer.g:449:16: '*'
            {
            match('*'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STAR"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:450:14: ( '+' )
            // ANTLRLexer.g:450:16: '+'
            {
            match('+'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "PLUS_ASSIGN"
    public final void mPLUS_ASSIGN() throws RecognitionException {
        try {
            int _type = PLUS_ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:451:14: ( '+=' )
            // ANTLRLexer.g:451:16: '+='
            {
            match("+="); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS_ASSIGN"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:452:14: ( '|' )
            // ANTLRLexer.g:452:16: '|'
            {
            match('|'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "ROOT"
    public final void mROOT() throws RecognitionException {
        try {
            int _type = ROOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:453:14: ( '^' )
            // ANTLRLexer.g:453:16: '^'
            {
            match('^'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ROOT"

    // $ANTLR start "DOLLAR"
    public final void mDOLLAR() throws RecognitionException {
        try {
            int _type = DOLLAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:454:14: ( '$' )
            // ANTLRLexer.g:454:16: '$'
            {
            match('$'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOLLAR"

    // $ANTLR start "WILDCARD"
    public final void mWILDCARD() throws RecognitionException {
        try {
            int _type = WILDCARD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:455:14: ( '.' )
            // ANTLRLexer.g:455:16: '.'
            {
            match('.'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WILDCARD"

    // $ANTLR start "RANGE"
    public final void mRANGE() throws RecognitionException {
        try {
            int _type = RANGE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:456:14: ( '..' )
            // ANTLRLexer.g:456:16: '..'
            {
            match(".."); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RANGE"

    // $ANTLR start "ETC"
    public final void mETC() throws RecognitionException {
        try {
            int _type = ETC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:457:14: ( '...' )
            // ANTLRLexer.g:457:16: '...'
            {
            match("..."); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ETC"

    // $ANTLR start "RARROW"
    public final void mRARROW() throws RecognitionException {
        try {
            int _type = RARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:458:14: ( '->' )
            // ANTLRLexer.g:458:16: '->'
            {
            match("->"); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RARROW"

    // $ANTLR start "TREE_BEGIN"
    public final void mTREE_BEGIN() throws RecognitionException {
        try {
            int _type = TREE_BEGIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:459:14: ( '^(' )
            // ANTLRLexer.g:459:16: '^('
            {
            match("^("); if (state.failed) return ;


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TREE_BEGIN"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:460:14: ( '@' )
            // ANTLRLexer.g:460:16: '@'
            {
            match('@'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:461:14: ( '~' )
            // ANTLRLexer.g:461:16: '~'
            {
            match('~'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "RBRACE"
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:462:14: ( '}' )
            // ANTLRLexer.g:462:16: '}'
            {
            match('}'); if (state.failed) return ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACE"

    // $ANTLR start "TOKEN_REF"
    public final void mTOKEN_REF() throws RecognitionException {
        try {
            int _type = TOKEN_REF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:471:5: ( ( 'A' .. 'Z' ) ( 'A' .. 'Z' | 'a' .. 'z' | '0' .. '9' | '_' )* )
            // ANTLRLexer.g:471:7: ( 'A' .. 'Z' ) ( 'A' .. 'Z' | 'a' .. 'z' | '0' .. '9' | '_' )*
            {
            // ANTLRLexer.g:471:7: ( 'A' .. 'Z' )
            // ANTLRLexer.g:471:8: 'A' .. 'Z'
            {
            matchRange('A','Z'); if (state.failed) return ;

            }

            // ANTLRLexer.g:471:18: ( 'A' .. 'Z' | 'a' .. 'z' | '0' .. '9' | '_' )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='0' && LA16_0<='9')||(LA16_0>='A' && LA16_0<='Z')||LA16_0=='_'||(LA16_0>='a' && LA16_0<='z')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ANTLRLexer.g:
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
            	    break loop16;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TOKEN_REF"

    // $ANTLR start "RULE_REF"
    public final void mRULE_REF() throws RecognitionException {
        try {
            int _type = RULE_REF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:482:5: ( ( 'a' .. 'z' ) ( 'A' .. 'Z' | 'a' .. 'z' | '0' .. '9' | '_' )* )
            // ANTLRLexer.g:482:7: ( 'a' .. 'z' ) ( 'A' .. 'Z' | 'a' .. 'z' | '0' .. '9' | '_' )*
            {
            // ANTLRLexer.g:482:7: ( 'a' .. 'z' )
            // ANTLRLexer.g:482:8: 'a' .. 'z'
            {
            matchRange('a','z'); if (state.failed) return ;

            }

            // ANTLRLexer.g:482:18: ( 'A' .. 'Z' | 'a' .. 'z' | '0' .. '9' | '_' )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0>='0' && LA17_0<='9')||(LA17_0>='A' && LA17_0<='Z')||LA17_0=='_'||(LA17_0>='a' && LA17_0<='z')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ANTLRLexer.g:
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
            	    break loop17;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RULE_REF"

    // $ANTLR start "ACTION_CHAR_LITERAL"
    public final void mACTION_CHAR_LITERAL() throws RecognitionException {
        try {
            // ANTLRLexer.g:505:2: ( '\\'' ( ( '\\\\' )=> ACTION_ESC | ~ '\\'' )* '\\'' )
            // ANTLRLexer.g:505:4: '\\'' ( ( '\\\\' )=> ACTION_ESC | ~ '\\'' )* '\\''
            {
            match('\''); if (state.failed) return ;
            // ANTLRLexer.g:505:9: ( ( '\\\\' )=> ACTION_ESC | ~ '\\'' )*
            loop18:
            do {
                int alt18=3;
                alt18 = dfa18.predict(input);
                switch (alt18) {
            	case 1 :
            	    // ANTLRLexer.g:505:10: ( '\\\\' )=> ACTION_ESC
            	    {
            	    mACTION_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // ANTLRLexer.g:505:31: ~ '\\''
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFF') ) {
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
            	    break loop18;
                }
            } while (true);

            match('\''); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "ACTION_CHAR_LITERAL"

    // $ANTLR start "ACTION_STRING_LITERAL"
    public final void mACTION_STRING_LITERAL() throws RecognitionException {
        try {
            // ANTLRLexer.g:515:2: ( '\"' ( ( '\\\\' )=> ACTION_ESC | ~ '\"' )* '\"' )
            // ANTLRLexer.g:515:4: '\"' ( ( '\\\\' )=> ACTION_ESC | ~ '\"' )* '\"'
            {
            match('\"'); if (state.failed) return ;
            // ANTLRLexer.g:515:8: ( ( '\\\\' )=> ACTION_ESC | ~ '\"' )*
            loop19:
            do {
                int alt19=3;
                alt19 = dfa19.predict(input);
                switch (alt19) {
            	case 1 :
            	    // ANTLRLexer.g:515:9: ( '\\\\' )=> ACTION_ESC
            	    {
            	    mACTION_ESC(); if (state.failed) return ;

            	    }
            	    break;
            	case 2 :
            	    // ANTLRLexer.g:515:30: ~ '\"'
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
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
            	    break loop19;
                }
            } while (true);

            match('\"'); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "ACTION_STRING_LITERAL"

    // $ANTLR start "ACTION_ESC"
    public final void mACTION_ESC() throws RecognitionException {
        try {
            // ANTLRLexer.g:525:2: ( '\\\\' . )
            // ANTLRLexer.g:525:4: '\\\\' .
            {
            match('\\'); if (state.failed) return ;
            matchAny(); if (state.failed) return ;

            }

        }
        finally {
        }
    }
    // $ANTLR end "ACTION_ESC"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:533:5: ( ( '0' .. '9' )+ )
            // ANTLRLexer.g:533:7: ( '0' .. '9' )+
            {
            // ANTLRLexer.g:533:7: ( '0' .. '9' )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ANTLRLexer.g:533:8: '0' .. '9'
            	    {
            	    matchRange('0','9'); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "SRC"
    public final void mSRC() throws RecognitionException {
        try {
            CommonToken file=null;
            CommonToken line=null;

            // ANTLRLexer.g:545:5: ( 'src' ( WSCHARS )+ file= ACTION_STRING_LITERAL ( WSCHARS )+ line= INT )
            // ANTLRLexer.g:545:7: 'src' ( WSCHARS )+ file= ACTION_STRING_LITERAL ( WSCHARS )+ line= INT
            {
            match("src"); if (state.failed) return ;

            // ANTLRLexer.g:545:13: ( WSCHARS )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0=='\t'||LA21_0=='\f'||LA21_0==' ') ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ANTLRLexer.g:545:13: WSCHARS
            	    {
            	    mWSCHARS(); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);

            int fileStart3565 = getCharIndex();
            mACTION_STRING_LITERAL(); if (state.failed) return ;
            file = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, fileStart3565, getCharIndex()-1);
            // ANTLRLexer.g:545:49: ( WSCHARS )+
            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0=='\t'||LA22_0=='\f'||LA22_0==' ') ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // ANTLRLexer.g:545:49: WSCHARS
            	    {
            	    mWSCHARS(); if (state.failed) return ;

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

            int lineStart3572 = getCharIndex();
            mINT(); if (state.failed) return ;
            line = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, lineStart3572, getCharIndex()-1);
            if ( state.backtracking==0 ) {

                       // TODO: Add target specific code to change the source file name and current line number
                       //     
                    
            }

            }

        }
        finally {
        }
    }
    // $ANTLR end "SRC"

    // $ANTLR start "STRING_LITERAL"
    public final void mSTRING_LITERAL() throws RecognitionException {
        try {
            // ANTLRLexer.g:562:25: ()
            // ANTLRLexer.g:562:27: 
            {
            }

        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL"

    // $ANTLR start "CHAR_LITERAL"
    public final void mCHAR_LITERAL() throws RecognitionException {
        try {
            int _type = CHAR_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;

               int len = 0;

            // ANTLRLexer.g:567:5: ( '\\'' ( ( ESC_SEQ | ~ ( '\\\\' | '\\'' ) ) )* '\\'' )
            // ANTLRLexer.g:567:8: '\\'' ( ( ESC_SEQ | ~ ( '\\\\' | '\\'' ) ) )* '\\''
            {
            match('\''); if (state.failed) return ;
            // ANTLRLexer.g:567:13: ( ( ESC_SEQ | ~ ( '\\\\' | '\\'' ) ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( ((LA24_0>='\u0000' && LA24_0<='&')||(LA24_0>='(' && LA24_0<='\uFFFF')) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ANTLRLexer.g:567:15: ( ESC_SEQ | ~ ( '\\\\' | '\\'' ) )
            	    {
            	    // ANTLRLexer.g:567:15: ( ESC_SEQ | ~ ( '\\\\' | '\\'' ) )
            	    int alt23=2;
            	    int LA23_0 = input.LA(1);

            	    if ( (LA23_0=='\\') ) {
            	        alt23=1;
            	    }
            	    else if ( ((LA23_0>='\u0000' && LA23_0<='&')||(LA23_0>='(' && LA23_0<='[')||(LA23_0>=']' && LA23_0<='\uFFFF')) ) {
            	        alt23=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 23, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt23) {
            	        case 1 :
            	            // ANTLRLexer.g:567:17: ESC_SEQ
            	            {
            	            mESC_SEQ(); if (state.failed) return ;

            	            }
            	            break;
            	        case 2 :
            	            // ANTLRLexer.g:567:27: ~ ( '\\\\' | '\\'' )
            	            {
            	            if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
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

            	    }

            	    if ( state.backtracking==0 ) {
            	      len++;
            	    }

            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

            match('\''); if (state.failed) return ;
            if ( state.backtracking==0 ) {

                  	   // Change the token type if we have more than one character
                  	   //
                  	   if (len > 1) {
                  	   
                  	       _type = STRING_LITERAL;
                  	   }
                  	
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR_LITERAL"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // ANTLRLexer.g:582:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // ANTLRLexer.g:582:13: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // ANTLRLexer.g:589:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | UNICODE_ESC | ) )
            // ANTLRLexer.g:589:7: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | UNICODE_ESC | )
            {
            match('\\'); if (state.failed) return ;
            // ANTLRLexer.g:590:9: ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | UNICODE_ESC | )
            int alt25=10;
            alt25 = dfa25.predict(input);
            switch (alt25) {
                case 1 :
                    // ANTLRLexer.g:594:9: 'b'
                    {
                    match('b'); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // ANTLRLexer.g:594:13: 't'
                    {
                    match('t'); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // ANTLRLexer.g:594:17: 'n'
                    {
                    match('n'); if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // ANTLRLexer.g:594:21: 'f'
                    {
                    match('f'); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // ANTLRLexer.g:594:25: 'r'
                    {
                    match('r'); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // ANTLRLexer.g:594:29: '\\\"'
                    {
                    match('\"'); if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // ANTLRLexer.g:594:34: '\\''
                    {
                    match('\''); if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // ANTLRLexer.g:594:39: '\\\\'
                    {
                    match('\\'); if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // ANTLRLexer.g:598:12: UNICODE_ESC
                    {
                    mUNICODE_ESC(); if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // ANTLRLexer.g:602:12: 
                    {
                    if ( state.backtracking==0 ) {

                          	      	// TODO: Issue error message
                          	      	//
                          	      
                    }

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {


            	// Flag to tell us whether we have a valid number of
            	// hex digits in the escape sequence
            	//
            	int	hCount = 0;

            // ANTLRLexer.g:619:5: ( 'u' ( ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | ) | ) ) | ) )
            // ANTLRLexer.g:619:9: 'u' ( ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | ) | ) ) | )
            {
            match('u'); if (state.failed) return ;
            // ANTLRLexer.g:628:6: ( ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | ) | ) ) | )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( ((LA29_0>='0' && LA29_0<='9')||(LA29_0>='A' && LA29_0<='F')||(LA29_0>='a' && LA29_0<='f')) ) {
                alt29=1;
            }
            else {
                alt29=2;}
            switch (alt29) {
                case 1 :
                    // ANTLRLexer.g:629:9: ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | ) | ) )
                    {
                    // ANTLRLexer.g:629:9: ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | ) | ) )
                    // ANTLRLexer.g:630:12: HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | ) | )
                    {
                    mHEX_DIGIT(); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       hCount++; 
                    }
                    // ANTLRLexer.g:631:14: ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | ) | )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( ((LA28_0>='0' && LA28_0<='9')||(LA28_0>='A' && LA28_0<='F')||(LA28_0>='a' && LA28_0<='f')) ) {
                        alt28=1;
                    }
                    else {
                        alt28=2;}
                    switch (alt28) {
                        case 1 :
                            // ANTLRLexer.g:632:19: HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT | ) | )
                            {
                            mHEX_DIGIT(); if (state.failed) return ;
                            if ( state.backtracking==0 ) {
                               hCount++; 
                            }
                            // ANTLRLexer.g:633:16: ( HEX_DIGIT ( HEX_DIGIT | ) | )
                            int alt27=2;
                            int LA27_0 = input.LA(1);

                            if ( ((LA27_0>='0' && LA27_0<='9')||(LA27_0>='A' && LA27_0<='F')||(LA27_0>='a' && LA27_0<='f')) ) {
                                alt27=1;
                            }
                            else {
                                alt27=2;}
                            switch (alt27) {
                                case 1 :
                                    // ANTLRLexer.g:634:21: HEX_DIGIT ( HEX_DIGIT | )
                                    {
                                    mHEX_DIGIT(); if (state.failed) return ;
                                    if ( state.backtracking==0 ) {
                                       hCount++; 
                                    }
                                    // ANTLRLexer.g:635:21: ( HEX_DIGIT | )
                                    int alt26=2;
                                    int LA26_0 = input.LA(1);

                                    if ( ((LA26_0>='0' && LA26_0<='9')||(LA26_0>='A' && LA26_0<='F')||(LA26_0>='a' && LA26_0<='f')) ) {
                                        alt26=1;
                                    }
                                    else {
                                        alt26=2;}
                                    switch (alt26) {
                                        case 1 :
                                            // ANTLRLexer.g:638:25: HEX_DIGIT
                                            {
                                            mHEX_DIGIT(); if (state.failed) return ;
                                            if ( state.backtracking==0 ) {
                                               hCount++; 
                                            }

                                            }
                                            break;
                                        case 2 :
                                            // ANTLRLexer.g:641:21: 
                                            {
                                            }
                                            break;

                                    }


                                    }
                                    break;
                                case 2 :
                                    // ANTLRLexer.g:644:17: 
                                    {
                                    }
                                    break;

                            }


                            }
                            break;
                        case 2 :
                            // ANTLRLexer.g:647:11: 
                            {
                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // ANTLRLexer.g:650:6: 
                    {
                    }
                    break;

            }

            if ( state.backtracking==0 ) {

                  		if	(hCount != 4) {
                  		
                  			// TODO: Issue error message
                  		}
                  	
            }

            }

        }
        finally {
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:670:5: ( ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+ )
            // ANTLRLexer.g:670:7: ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+
            {
            // ANTLRLexer.g:670:7: ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( ((LA30_0>='\t' && LA30_0<='\n')||(LA30_0>='\f' && LA30_0<='\r')||LA30_0==' ') ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // ANTLRLexer.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
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
            	    if ( cnt30 >= 1 ) break loop30;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);

            if ( state.backtracking==0 ) {

                    
              	_channel=2;
                    
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "NLCHARS"
    public final void mNLCHARS() throws RecognitionException {
        try {
            // ANTLRLexer.g:688:5: ( '\\n' | '\\r' )
            // ANTLRLexer.g:
            {
            if ( input.LA(1)=='\n'||input.LA(1)=='\r' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "NLCHARS"

    // $ANTLR start "WSCHARS"
    public final void mWSCHARS() throws RecognitionException {
        try {
            // ANTLRLexer.g:696:5: ( ' ' | '\\t' | '\\f' )
            // ANTLRLexer.g:
            {
            if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "WSCHARS"

    // $ANTLR start "WSNLCHARS"
    public final void mWSNLCHARS() throws RecognitionException {
        try {
            // ANTLRLexer.g:705:5: ( ' ' | '\\t' | '\\f' | '\\n' | '\\r' )
            // ANTLRLexer.g:
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||(input.LA(1)>='\f' && input.LA(1)<='\r')||input.LA(1)==' ' ) {
                input.consume();
            state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "WSNLCHARS"

    // $ANTLR start "ERRCHAR"
    public final void mERRCHAR() throws RecognitionException {
        try {
            int _type = ERRCHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // ANTLRLexer.g:720:5: ( . )
            // ANTLRLexer.g:720:7: .
            {
            matchAny(); if (state.failed) return ;
            if ( state.backtracking==0 ) {

                       // TODO: Issue error message
                       //
                       skip();
                    
            }

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ERRCHAR"

    public void mTokens() throws RecognitionException {
        // ANTLRLexer.g:1:8: ( COMMENT | DOUBLE_QUOTE_STRING_LITERAL | DOUBLE_ANGLE_STRING_LITERAL | ARG_ACTION | ACTION | OPTIONS | TOKENS | SCOPE | IMPORT | FRAGMENT | LEXER | PARSER | TREE | GRAMMAR | PROTECTED | PUBLIC | PRIVATE | RETURNS | THROWS | CATCH | FINALLY | TEMPLATE | COLON | COLONCOLON | COMMA | SEMI | LPAREN | RPAREN | IMPLIES | LT | GT | ASSIGN | QUESTION | BANG | STAR | PLUS | PLUS_ASSIGN | OR | ROOT | DOLLAR | WILDCARD | RANGE | ETC | RARROW | TREE_BEGIN | AT | NOT | RBRACE | TOKEN_REF | RULE_REF | INT | CHAR_LITERAL | WS | ERRCHAR )
        int alt31=54;
        alt31 = dfa31.predict(input);
        switch (alt31) {
            case 1 :
                // ANTLRLexer.g:1:10: COMMENT
                {
                mCOMMENT(); if (state.failed) return ;

                }
                break;
            case 2 :
                // ANTLRLexer.g:1:18: DOUBLE_QUOTE_STRING_LITERAL
                {
                mDOUBLE_QUOTE_STRING_LITERAL(); if (state.failed) return ;

                }
                break;
            case 3 :
                // ANTLRLexer.g:1:46: DOUBLE_ANGLE_STRING_LITERAL
                {
                mDOUBLE_ANGLE_STRING_LITERAL(); if (state.failed) return ;

                }
                break;
            case 4 :
                // ANTLRLexer.g:1:74: ARG_ACTION
                {
                mARG_ACTION(); if (state.failed) return ;

                }
                break;
            case 5 :
                // ANTLRLexer.g:1:85: ACTION
                {
                mACTION(); if (state.failed) return ;

                }
                break;
            case 6 :
                // ANTLRLexer.g:1:92: OPTIONS
                {
                mOPTIONS(); if (state.failed) return ;

                }
                break;
            case 7 :
                // ANTLRLexer.g:1:100: TOKENS
                {
                mTOKENS(); if (state.failed) return ;

                }
                break;
            case 8 :
                // ANTLRLexer.g:1:107: SCOPE
                {
                mSCOPE(); if (state.failed) return ;

                }
                break;
            case 9 :
                // ANTLRLexer.g:1:113: IMPORT
                {
                mIMPORT(); if (state.failed) return ;

                }
                break;
            case 10 :
                // ANTLRLexer.g:1:120: FRAGMENT
                {
                mFRAGMENT(); if (state.failed) return ;

                }
                break;
            case 11 :
                // ANTLRLexer.g:1:129: LEXER
                {
                mLEXER(); if (state.failed) return ;

                }
                break;
            case 12 :
                // ANTLRLexer.g:1:135: PARSER
                {
                mPARSER(); if (state.failed) return ;

                }
                break;
            case 13 :
                // ANTLRLexer.g:1:142: TREE
                {
                mTREE(); if (state.failed) return ;

                }
                break;
            case 14 :
                // ANTLRLexer.g:1:147: GRAMMAR
                {
                mGRAMMAR(); if (state.failed) return ;

                }
                break;
            case 15 :
                // ANTLRLexer.g:1:155: PROTECTED
                {
                mPROTECTED(); if (state.failed) return ;

                }
                break;
            case 16 :
                // ANTLRLexer.g:1:165: PUBLIC
                {
                mPUBLIC(); if (state.failed) return ;

                }
                break;
            case 17 :
                // ANTLRLexer.g:1:172: PRIVATE
                {
                mPRIVATE(); if (state.failed) return ;

                }
                break;
            case 18 :
                // ANTLRLexer.g:1:180: RETURNS
                {
                mRETURNS(); if (state.failed) return ;

                }
                break;
            case 19 :
                // ANTLRLexer.g:1:188: THROWS
                {
                mTHROWS(); if (state.failed) return ;

                }
                break;
            case 20 :
                // ANTLRLexer.g:1:195: CATCH
                {
                mCATCH(); if (state.failed) return ;

                }
                break;
            case 21 :
                // ANTLRLexer.g:1:201: FINALLY
                {
                mFINALLY(); if (state.failed) return ;

                }
                break;
            case 22 :
                // ANTLRLexer.g:1:209: TEMPLATE
                {
                mTEMPLATE(); if (state.failed) return ;

                }
                break;
            case 23 :
                // ANTLRLexer.g:1:218: COLON
                {
                mCOLON(); if (state.failed) return ;

                }
                break;
            case 24 :
                // ANTLRLexer.g:1:224: COLONCOLON
                {
                mCOLONCOLON(); if (state.failed) return ;

                }
                break;
            case 25 :
                // ANTLRLexer.g:1:235: COMMA
                {
                mCOMMA(); if (state.failed) return ;

                }
                break;
            case 26 :
                // ANTLRLexer.g:1:241: SEMI
                {
                mSEMI(); if (state.failed) return ;

                }
                break;
            case 27 :
                // ANTLRLexer.g:1:246: LPAREN
                {
                mLPAREN(); if (state.failed) return ;

                }
                break;
            case 28 :
                // ANTLRLexer.g:1:253: RPAREN
                {
                mRPAREN(); if (state.failed) return ;

                }
                break;
            case 29 :
                // ANTLRLexer.g:1:260: IMPLIES
                {
                mIMPLIES(); if (state.failed) return ;

                }
                break;
            case 30 :
                // ANTLRLexer.g:1:268: LT
                {
                mLT(); if (state.failed) return ;

                }
                break;
            case 31 :
                // ANTLRLexer.g:1:271: GT
                {
                mGT(); if (state.failed) return ;

                }
                break;
            case 32 :
                // ANTLRLexer.g:1:274: ASSIGN
                {
                mASSIGN(); if (state.failed) return ;

                }
                break;
            case 33 :
                // ANTLRLexer.g:1:281: QUESTION
                {
                mQUESTION(); if (state.failed) return ;

                }
                break;
            case 34 :
                // ANTLRLexer.g:1:290: BANG
                {
                mBANG(); if (state.failed) return ;

                }
                break;
            case 35 :
                // ANTLRLexer.g:1:295: STAR
                {
                mSTAR(); if (state.failed) return ;

                }
                break;
            case 36 :
                // ANTLRLexer.g:1:300: PLUS
                {
                mPLUS(); if (state.failed) return ;

                }
                break;
            case 37 :
                // ANTLRLexer.g:1:305: PLUS_ASSIGN
                {
                mPLUS_ASSIGN(); if (state.failed) return ;

                }
                break;
            case 38 :
                // ANTLRLexer.g:1:317: OR
                {
                mOR(); if (state.failed) return ;

                }
                break;
            case 39 :
                // ANTLRLexer.g:1:320: ROOT
                {
                mROOT(); if (state.failed) return ;

                }
                break;
            case 40 :
                // ANTLRLexer.g:1:325: DOLLAR
                {
                mDOLLAR(); if (state.failed) return ;

                }
                break;
            case 41 :
                // ANTLRLexer.g:1:332: WILDCARD
                {
                mWILDCARD(); if (state.failed) return ;

                }
                break;
            case 42 :
                // ANTLRLexer.g:1:341: RANGE
                {
                mRANGE(); if (state.failed) return ;

                }
                break;
            case 43 :
                // ANTLRLexer.g:1:347: ETC
                {
                mETC(); if (state.failed) return ;

                }
                break;
            case 44 :
                // ANTLRLexer.g:1:351: RARROW
                {
                mRARROW(); if (state.failed) return ;

                }
                break;
            case 45 :
                // ANTLRLexer.g:1:358: TREE_BEGIN
                {
                mTREE_BEGIN(); if (state.failed) return ;

                }
                break;
            case 46 :
                // ANTLRLexer.g:1:369: AT
                {
                mAT(); if (state.failed) return ;

                }
                break;
            case 47 :
                // ANTLRLexer.g:1:372: NOT
                {
                mNOT(); if (state.failed) return ;

                }
                break;
            case 48 :
                // ANTLRLexer.g:1:376: RBRACE
                {
                mRBRACE(); if (state.failed) return ;

                }
                break;
            case 49 :
                // ANTLRLexer.g:1:383: TOKEN_REF
                {
                mTOKEN_REF(); if (state.failed) return ;

                }
                break;
            case 50 :
                // ANTLRLexer.g:1:393: RULE_REF
                {
                mRULE_REF(); if (state.failed) return ;

                }
                break;
            case 51 :
                // ANTLRLexer.g:1:402: INT
                {
                mINT(); if (state.failed) return ;

                }
                break;
            case 52 :
                // ANTLRLexer.g:1:406: CHAR_LITERAL
                {
                mCHAR_LITERAL(); if (state.failed) return ;

                }
                break;
            case 53 :
                // ANTLRLexer.g:1:419: WS
                {
                mWS(); if (state.failed) return ;

                }
                break;
            case 54 :
                // ANTLRLexer.g:1:422: ERRCHAR
                {
                mERRCHAR(); if (state.failed) return ;

                }
                break;

        }

    }

    // $ANTLR start synpred1_ANTLRLexer
    public final void synpred1_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:160:17: ( ' $ANTLR' )
        // ANTLRLexer.g:160:18: ' $ANTLR'
        {
        match(" $ANTLR"); if (state.failed) return ;


        }
    }
    // $ANTLR end synpred1_ANTLRLexer

    // $ANTLR start synpred2_ANTLRLexer
    public final void synpred2_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:224:9: ( '\\\\' )
        // ANTLRLexer.g:224:10: '\\\\'
        {
        match('\\'); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_ANTLRLexer

    // $ANTLR start synpred3_ANTLRLexer
    public final void synpred3_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:246:14: ( '\\\\' )
        // ANTLRLexer.g:246:15: '\\\\'
        {
        match('\\'); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_ANTLRLexer

    // $ANTLR start synpred4_ANTLRLexer
    public final void synpred4_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:248:22: ( ']' )
        // ANTLRLexer.g:248:23: ']'
        {
        match(']'); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_ANTLRLexer

    // $ANTLR start synpred5_ANTLRLexer
    public final void synpred5_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:263:14: ( '\"' )
        // ANTLRLexer.g:263:15: '\"'
        {
        match('\"'); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_ANTLRLexer

    // $ANTLR start synpred6_ANTLRLexer
    public final void synpred6_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:270:14: ( '\\'' )
        // ANTLRLexer.g:270:15: '\\''
        {
        match('\''); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_ANTLRLexer

    // $ANTLR start synpred7_ANTLRLexer
    public final void synpred7_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:505:10: ( '\\\\' )
        // ANTLRLexer.g:505:11: '\\\\'
        {
        match('\\'); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_ANTLRLexer

    // $ANTLR start synpred8_ANTLRLexer
    public final void synpred8_ANTLRLexer_fragment() throws RecognitionException {   
        // ANTLRLexer.g:515:9: ( '\\\\' )
        // ANTLRLexer.g:515:10: '\\\\'
        {
        match('\\'); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_ANTLRLexer

    public final boolean synpred8_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_ANTLRLexer() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_ANTLRLexer_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA2 dfa2 = new DFA2(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA10 dfa10 = new DFA10(this);
    protected DFA18 dfa18 = new DFA18(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA25 dfa25 = new DFA25(this);
    protected DFA31 dfa31 = new DFA31(this);
    static final String DFA2_eotS =
        "\2\2\1\uffff\16\2\1\uffff\1\2\1\uffff\4\2\2\uffff";
    static final String DFA2_eofS =
        "\32\uffff";
    static final String DFA2_minS =
        "\1\40\1\44\1\uffff\1\101\1\116\1\124\1\114\1\122\1\163\1\162\1\143"+
        "\2\11\3\0\1\11\1\uffff\1\0\1\uffff\2\0\1\11\3\0";
    static final String DFA2_maxS =
        "\1\40\1\44\1\uffff\1\101\1\116\1\124\1\114\1\122\1\163\1\162\1\143"+
        "\1\40\1\42\3\uffff\1\40\1\uffff\1\uffff\1\uffff\2\uffff\1\71\1\uffff"+
        "\2\0";
    static final String DFA2_acceptS =
        "\2\uffff\1\2\16\uffff\1\1\1\uffff\1\1\6\uffff";
    static final String DFA2_specialS =
        "\15\uffff\1\6\1\2\1\1\2\uffff\1\10\1\uffff\1\7\1\3\1\uffff\1\5\1"+
        "\4\1\0}>";
    static final String[] DFA2_transitionS = {
            "\1\1",
            "\1\3",
            "",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\7",
            "\1\10",
            "\1\11",
            "\1\12",
            "\1\13",
            "\1\14\2\uffff\1\14\23\uffff\1\14",
            "\1\14\2\uffff\1\14\23\uffff\1\14\1\uffff\1\15",
            "\12\17\1\21\2\17\1\21\24\17\1\20\71\17\1\16\uffa3\17",
            "\12\25\1\23\2\25\1\23\24\25\1\22\71\25\1\24\uffa3\25",
            "\12\17\1\21\2\17\1\21\24\17\1\20\71\17\1\16\uffa3\17",
            "\1\26\2\uffff\1\26\23\uffff\1\26",
            "",
            "\11\17\1\27\1\21\1\17\1\27\1\21\22\17\1\27\1\17\1\20\71\17"+
            "\1\16\uffa3\17",
            "",
            "\12\25\1\23\2\25\1\23\24\25\1\22\71\25\1\24\uffa3\25",
            "\12\17\1\21\2\17\1\21\24\17\1\20\71\17\1\16\uffa3\17",
            "\1\26\2\uffff\1\26\23\uffff\1\26\17\uffff\12\30",
            "\11\17\1\27\1\21\1\17\1\27\1\21\22\17\1\27\1\17\1\20\15\17"+
            "\12\31\42\17\1\16\uffa3\17",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
    static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
    static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
    static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
    static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
    static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
    static final short[][] DFA2_transition;

    static {
        int numStates = DFA2_transitionS.length;
        DFA2_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
        }
    }

    class DFA2 extends DFA {

        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA2_eot;
            this.eof = DFA2_eof;
            this.min = DFA2_min;
            this.max = DFA2_max;
            this.accept = DFA2_accept;
            this.special = DFA2_special;
            this.transition = DFA2_transition;
        }
        public String getDescription() {
            return "159:13: ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA2_25 = input.LA(1);

                         
                        int index2_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_ANTLRLexer()) ) {s = 19;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index2_25);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA2_15 = input.LA(1);

                         
                        int index2_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA2_15=='\"') ) {s = 16;}

                        else if ( (LA2_15=='\\') ) {s = 14;}

                        else if ( ((LA2_15>='\u0000' && LA2_15<='\t')||(LA2_15>='\u000B' && LA2_15<='\f')||(LA2_15>='\u000E' && LA2_15<='!')||(LA2_15>='#' && LA2_15<='[')||(LA2_15>=']' && LA2_15<='\uFFFF')) ) {s = 15;}

                        else if ( (LA2_15=='\n'||LA2_15=='\r') && (synpred1_ANTLRLexer())) {s = 17;}

                        else s = 2;

                         
                        input.seek(index2_15);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA2_14 = input.LA(1);

                         
                        int index2_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA2_14=='\"') ) {s = 18;}

                        else if ( (LA2_14=='\n'||LA2_14=='\r') && (synpred1_ANTLRLexer())) {s = 19;}

                        else if ( (LA2_14=='\\') ) {s = 20;}

                        else if ( ((LA2_14>='\u0000' && LA2_14<='\t')||(LA2_14>='\u000B' && LA2_14<='\f')||(LA2_14>='\u000E' && LA2_14<='!')||(LA2_14>='#' && LA2_14<='[')||(LA2_14>=']' && LA2_14<='\uFFFF')) ) {s = 21;}

                        else s = 2;

                         
                        input.seek(index2_14);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA2_21 = input.LA(1);

                         
                        int index2_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA2_21=='\"') ) {s = 16;}

                        else if ( (LA2_21=='\\') ) {s = 14;}

                        else if ( ((LA2_21>='\u0000' && LA2_21<='\t')||(LA2_21>='\u000B' && LA2_21<='\f')||(LA2_21>='\u000E' && LA2_21<='!')||(LA2_21>='#' && LA2_21<='[')||(LA2_21>=']' && LA2_21<='\uFFFF')) ) {s = 15;}

                        else if ( (LA2_21=='\n'||LA2_21=='\r') && (synpred1_ANTLRLexer())) {s = 17;}

                        else s = 2;

                         
                        input.seek(index2_21);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA2_24 = input.LA(1);

                         
                        int index2_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_ANTLRLexer()) ) {s = 19;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index2_24);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA2_23 = input.LA(1);

                         
                        int index2_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA2_23>='0' && LA2_23<='9')) ) {s = 25;}

                        else if ( (LA2_23=='\t'||LA2_23=='\f'||LA2_23==' ') ) {s = 23;}

                        else if ( (LA2_23=='\"') ) {s = 16;}

                        else if ( (LA2_23=='\\') ) {s = 14;}

                        else if ( ((LA2_23>='\u0000' && LA2_23<='\b')||LA2_23=='\u000B'||(LA2_23>='\u000E' && LA2_23<='\u001F')||LA2_23=='!'||(LA2_23>='#' && LA2_23<='/')||(LA2_23>=':' && LA2_23<='[')||(LA2_23>=']' && LA2_23<='\uFFFF')) ) {s = 15;}

                        else if ( (LA2_23=='\n'||LA2_23=='\r') && (synpred1_ANTLRLexer())) {s = 17;}

                        else s = 2;

                         
                        input.seek(index2_23);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA2_13 = input.LA(1);

                         
                        int index2_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA2_13=='\\') ) {s = 14;}

                        else if ( ((LA2_13>='\u0000' && LA2_13<='\t')||(LA2_13>='\u000B' && LA2_13<='\f')||(LA2_13>='\u000E' && LA2_13<='!')||(LA2_13>='#' && LA2_13<='[')||(LA2_13>=']' && LA2_13<='\uFFFF')) ) {s = 15;}

                        else if ( (LA2_13=='\"') ) {s = 16;}

                        else if ( (LA2_13=='\n'||LA2_13=='\r') && (synpred1_ANTLRLexer())) {s = 17;}

                        else s = 2;

                         
                        input.seek(index2_13);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA2_20 = input.LA(1);

                         
                        int index2_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA2_20=='\"') ) {s = 18;}

                        else if ( (LA2_20=='\\') ) {s = 20;}

                        else if ( ((LA2_20>='\u0000' && LA2_20<='\t')||(LA2_20>='\u000B' && LA2_20<='\f')||(LA2_20>='\u000E' && LA2_20<='!')||(LA2_20>='#' && LA2_20<='[')||(LA2_20>=']' && LA2_20<='\uFFFF')) ) {s = 21;}

                        else if ( (LA2_20=='\n'||LA2_20=='\r') && (synpred1_ANTLRLexer())) {s = 19;}

                        else s = 2;

                         
                        input.seek(index2_20);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA2_18 = input.LA(1);

                         
                        int index2_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA2_18=='\t'||LA2_18=='\f'||LA2_18==' ') ) {s = 23;}

                        else if ( (LA2_18=='\"') ) {s = 16;}

                        else if ( (LA2_18=='\\') ) {s = 14;}

                        else if ( ((LA2_18>='\u0000' && LA2_18<='\b')||LA2_18=='\u000B'||(LA2_18>='\u000E' && LA2_18<='\u001F')||LA2_18=='!'||(LA2_18>='#' && LA2_18<='[')||(LA2_18>=']' && LA2_18<='\uFFFF')) ) {s = 15;}

                        else if ( (LA2_18=='\n'||LA2_18=='\r') && (synpred1_ANTLRLexer())) {s = 17;}

                        else s = 2;

                         
                        input.seek(index2_18);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 2, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA7_eotS =
        "\4\uffff\1\3\5\uffff";
    static final String DFA7_eofS =
        "\12\uffff";
    static final String DFA7_minS =
        "\1\0\1\uffff\1\0\1\uffff\3\0\3\uffff";
    static final String DFA7_maxS =
        "\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\2\0\3\uffff";
    static final String DFA7_acceptS =
        "\1\uffff\1\3\1\uffff\1\2\3\uffff\3\1";
    static final String DFA7_specialS =
        "\1\3\1\uffff\1\2\1\uffff\1\0\1\4\1\1\3\uffff}>";
    static final String[] DFA7_transitionS = {
            "\42\3\1\1\71\3\1\2\uffa3\3",
            "",
            "\42\6\1\4\71\6\1\5\uffa3\6",
            "",
            "\42\11\1\7\71\11\1\10\uffa3\11",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "()* loopback of 224:8: ( ( '\\\\' )=> '\\\\' . | ~ '\"' )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA7_4 = input.LA(1);

                         
                        int index7_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA7_4=='\"') && (synpred2_ANTLRLexer())) {s = 7;}

                        else if ( (LA7_4=='\\') && (synpred2_ANTLRLexer())) {s = 8;}

                        else if ( ((LA7_4>='\u0000' && LA7_4<='!')||(LA7_4>='#' && LA7_4<='[')||(LA7_4>=']' && LA7_4<='\uFFFF')) && (synpred2_ANTLRLexer())) {s = 9;}

                        else s = 3;

                         
                        input.seek(index7_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA7_6 = input.LA(1);

                         
                        int index7_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_ANTLRLexer()) ) {s = 9;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index7_6);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA7_2 = input.LA(1);

                        s = -1;
                        if ( (LA7_2=='\"') ) {s = 4;}

                        else if ( (LA7_2=='\\') ) {s = 5;}

                        else if ( ((LA7_2>='\u0000' && LA7_2<='!')||(LA7_2>='#' && LA7_2<='[')||(LA7_2>=']' && LA7_2<='\uFFFF')) ) {s = 6;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA7_0 = input.LA(1);

                        s = -1;
                        if ( (LA7_0=='\"') ) {s = 1;}

                        else if ( (LA7_0=='\\') ) {s = 2;}

                        else if ( ((LA7_0>='\u0000' && LA7_0<='!')||(LA7_0>='#' && LA7_0<='[')||(LA7_0>=']' && LA7_0<='\uFFFF')) ) {s = 3;}

                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA7_5 = input.LA(1);

                         
                        int index7_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_ANTLRLexer()) ) {s = 9;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index7_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA10_eotS =
        "\6\uffff\1\5\4\uffff\1\5\4\uffff\1\5\14\uffff\1\5\4\uffff\1\5\7"+
        "\uffff\1\5\4\uffff\1\5\10\uffff\1\5\12\uffff\1\5\4\uffff\1\135\14"+
        "\uffff\1\155\2\uffff\1\135\10\uffff\1\135\3\uffff\1\155\4\uffff"+
        "\1\155\3\uffff\1\135\6\uffff\1\155\2\uffff";
    static final String DFA10_eofS =
        "\170\uffff";
    static final String DFA10_minS =
        "\1\0\1\uffff\3\0\1\uffff\17\0\10\uffff\12\0\3\uffff\63\0\1\uffff"+
        "\17\0\1\uffff\12\0";
    static final String DFA10_maxS =
        "\1\uffff\1\uffff\3\uffff\1\uffff\1\uffff\4\0\2\uffff\1\0\5\uffff"+
        "\1\0\1\uffff\10\uffff\2\uffff\1\0\4\uffff\1\0\2\uffff\3\uffff\3"+
        "\uffff\1\0\4\uffff\1\0\7\uffff\1\0\11\uffff\1\0\1\uffff\1\0\14\uffff"+
        "\1\0\5\uffff\1\0\3\uffff\1\uffff\1\uffff\1\0\3\uffff\1\0\4\uffff"+
        "\1\0\4\uffff\1\uffff\3\uffff\1\0\1\uffff\1\0\4\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\5\3\uffff\1\4\17\uffff\5\1\3\2\12\uffff\3\3\63\uffff"+
        "\1\2\17\uffff\1\3\12\uffff";
    static final String DFA10_specialS =
        "\1\44\1\uffff\1\122\1\41\1\104\1\uffff\1\25\1\26\1\120\1\31\1\30"+
        "\1\37\1\20\1\63\1\60\1\133\1\125\1\110\1\136\1\0\1\45\10\uffff\1"+
        "\7\1\112\1\36\1\144\1\121\1\141\1\22\1\43\1\102\1\147\3\uffff\1"+
        "\73\1\52\1\132\1\14\1\50\1\142\1\24\1\66\1\42\1\62\1\6\1\51\1\106"+
        "\1\11\1\72\1\76\1\27\1\33\1\117\1\21\1\67\1\61\1\16\1\46\1\64\1"+
        "\34\1\140\1\126\1\32\1\40\1\127\1\107\1\1\1\137\1\143\1\10\1\70"+
        "\1\17\1\115\1\113\1\130\1\56\1\15\1\47\1\57\1\131\1\55\1\3\1\101"+
        "\1\74\1\2\1\uffff\1\134\1\4\1\123\1\135\1\124\1\35\1\13\1\145\1"+
        "\116\1\146\1\75\1\111\1\5\1\150\1\114\1\uffff\1\100\1\23\1\54\1"+
        "\77\1\71\1\53\1\103\1\65\1\12\1\105}>";
    static final String[] DFA10_transitionS = {
            "\42\5\1\3\4\5\1\4\64\5\1\2\1\1\uffa2\5",
            "",
            "\42\12\1\10\4\12\1\11\64\12\1\7\1\6\uffa2\12",
            "\42\17\1\15\4\17\1\16\64\17\1\14\1\13\uffa2\17",
            "\42\24\1\22\4\24\1\23\64\24\1\21\1\20\uffa2\24",
            "",
            "\42\31\1\27\4\31\1\30\64\31\1\26\1\25\uffa2\31",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\42\34\1\32\71\34\1\33\uffa3\34",
            "\42\41\1\37\4\41\1\40\64\41\1\36\1\35\uffa2\41",
            "\1\uffff",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "\42\17\1\15\4\17\1\16\64\17\1\14\1\13\uffa2\17",
            "\47\51\1\47\64\51\1\50\uffa3\51",
            "\42\56\1\54\4\56\1\55\64\56\1\53\1\52\uffa2\56",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\1\uffff",
            "\42\24\1\22\4\24\1\23\64\24\1\21\1\20\uffa2\24",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\42\17\1\15\4\17\1\16\64\17\1\14\1\13\uffa2\17",
            "\42\41\1\37\4\41\1\40\64\41\1\36\1\35\uffa2\41",
            "\1\uffff",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "\42\17\1\15\4\17\1\16\64\17\1\14\1\13\uffa2\17",
            "\42\67\1\64\4\67\1\66\64\67\1\65\uffa3\67",
            "\42\74\1\72\4\74\1\73\64\74\1\71\1\70\uffa2\74",
            "\1\uffff",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "",
            "",
            "",
            "\42\24\1\22\4\24\1\23\64\24\1\21\1\20\uffa2\24",
            "\42\56\1\54\4\56\1\55\64\56\1\53\1\52\uffa2\56",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\1\uffff",
            "\42\24\1\22\4\24\1\23\64\24\1\21\1\20\uffa2\24",
            "\42\100\1\75\4\100\1\77\64\100\1\76\uffa3\100",
            "\42\105\1\101\4\105\1\104\64\105\1\102\1\103\uffa2\105",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\1\uffff",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\42\112\1\111\4\112\1\106\64\112\1\107\1\110\uffa2\112",
            "\42\116\1\113\4\116\1\115\64\116\1\114\uffa3\116",
            "\42\17\1\15\4\17\1\16\64\17\1\14\1\13\uffa2\17",
            "\42\67\1\64\4\67\1\66\64\67\1\65\uffa3\67",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "\42\74\1\72\4\74\1\73\64\74\1\71\1\70\uffa2\74",
            "\1\uffff",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "\42\24\1\22\4\24\1\23\64\24\1\21\1\20\uffa2\24",
            "\42\122\1\117\4\122\1\121\64\122\1\120\uffa3\122",
            "\42\127\1\123\4\127\1\126\64\127\1\124\1\125\uffa2\127",
            "\42\100\1\75\4\100\1\77\64\100\1\76\uffa3\100",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\42\105\1\101\4\105\1\104\64\105\1\102\1\103\uffa2\105",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\1\uffff",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\1\uffff",
            "\42\134\1\133\4\134\1\131\64\134\1\132\1\130\uffa2\134",
            "\0\5",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\42\112\1\111\4\112\1\106\64\112\1\107\1\110\uffa2\112",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\42\116\1\113\4\116\1\115\64\116\1\114\uffa3\116",
            "\42\46\1\44\4\46\1\45\64\46\1\43\1\42\uffa2\46",
            "\42\67\1\64\4\67\1\66\64\67\1\65\uffa3\67",
            "\42\63\1\61\4\63\1\62\64\63\1\60\1\57\uffa2\63",
            "\42\122\1\117\4\122\1\121\64\122\1\120\uffa3\122",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147",
            "\42\100\1\75\4\100\1\77\64\100\1\76\uffa3\100",
            "\1\uffff",
            "\42\154\1\150\4\154\1\153\64\154\1\151\1\152\uffa2\154",
            "\0\5",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147",
            "\42\127\1\123\4\127\1\126\64\127\1\124\1\125\uffa2\127",
            "\42\112\1\111\4\112\1\106\64\112\1\107\1\110\uffa2\112",
            "\1\uffff",
            "\42\134\1\133\4\134\1\131\64\134\1\132\1\130\uffa2\134",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\42\112\1\111\4\112\1\106\64\112\1\107\1\110\uffa2\112",
            "",
            "\42\162\1\157\4\162\1\161\64\162\1\160\1\156\uffa2\162",
            "\1\uffff",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\42\67\1\64\4\67\1\66\64\67\1\65\uffa3\67",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\1\uffff",
            "\42\167\1\163\4\167\1\166\64\167\1\164\1\165\uffa2\167",
            "\42\100\1\75\4\100\1\77\64\100\1\76\uffa3\100",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147",
            "\1\uffff",
            "\42\154\1\150\4\154\1\153\64\154\1\151\1\152\uffa2\154",
            "\42\127\1\123\4\127\1\126\64\127\1\124\1\125\uffa2\127",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147",
            "\42\127\1\123\4\127\1\126\64\127\1\124\1\125\uffa2\127",
            "",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\42\162\1\157\4\162\1\161\64\162\1\160\1\156\uffa2\162",
            "\1\uffff",
            "\42\142\1\140\4\142\1\137\64\142\1\136\1\141\uffa2\142",
            "\1\uffff",
            "\42\167\1\163\4\167\1\166\64\167\1\164\1\165\uffa2\167",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147",
            "\42\147\1\143\4\147\1\146\64\147\1\144\1\145\uffa2\147"
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "()* loopback of 245:10: ( ( '\\\\' )=> '\\\\' ( ( ']' )=> ']' | c= . ) | ( '\"' )=>as= ACTION_STRING_LITERAL | ( '\\'' )=>ac= ACTION_CHAR_LITERAL | c=~ ']' )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA10_19 = input.LA(1);

                         
                        int index10_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 41;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_19);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA10_74 = input.LA(1);

                        s = -1;
                        if ( (LA10_74=='\'') ) {s = 70;}

                        else if ( (LA10_74=='\\') ) {s = 71;}

                        else if ( (LA10_74==']') ) {s = 72;}

                        else if ( (LA10_74=='\"') ) {s = 73;}

                        else if ( ((LA10_74>='\u0000' && LA10_74<='!')||(LA10_74>='#' && LA10_74<='&')||(LA10_74>='(' && LA10_74<='[')||(LA10_74>='^' && LA10_74<='\uFFFF')) ) {s = 74;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA10_92 = input.LA(1);

                        s = -1;
                        if ( (LA10_92=='\'') ) {s = 70;}

                        else if ( (LA10_92=='\\') ) {s = 71;}

                        else if ( (LA10_92==']') ) {s = 72;}

                        else if ( (LA10_92=='\"') ) {s = 73;}

                        else if ( ((LA10_92>='\u0000' && LA10_92<='!')||(LA10_92>='#' && LA10_92<='&')||(LA10_92>='(' && LA10_92<='[')||(LA10_92>='^' && LA10_92<='\uFFFF')) ) {s = 74;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA10_89 = input.LA(1);

                         
                        int index10_89 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 93;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_89);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA10_95 = input.LA(1);

                         
                        int index10_95 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 93;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_95);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA10_106 = input.LA(1);

                         
                        int index10_106 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_106=='\"') ) {s = 83;}

                        else if ( (LA10_106=='\\') ) {s = 84;}

                        else if ( (LA10_106==']') ) {s = 85;}

                        else if ( (LA10_106=='\'') ) {s = 86;}

                        else if ( ((LA10_106>='\u0000' && LA10_106<='!')||(LA10_106>='#' && LA10_106<='&')||(LA10_106>='(' && LA10_106<='[')||(LA10_106>='^' && LA10_106<='\uFFFF')) ) {s = 87;}

                        else s = 109;

                         
                        input.seek(index10_106);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA10_52 = input.LA(1);

                        s = -1;
                        if ( (LA10_52=='\'') ) {s = 70;}

                        else if ( (LA10_52=='\\') ) {s = 71;}

                        else if ( (LA10_52==']') ) {s = 72;}

                        else if ( (LA10_52=='\"') ) {s = 73;}

                        else if ( ((LA10_52>='\u0000' && LA10_52<='!')||(LA10_52>='#' && LA10_52<='&')||(LA10_52>='(' && LA10_52<='[')||(LA10_52>='^' && LA10_52<='\uFFFF')) ) {s = 74;}

                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA10_29 = input.LA(1);

                        s = -1;
                        if ( (LA10_29=='\"') ) {s = 13;}

                        else if ( (LA10_29=='\\') ) {s = 12;}

                        else if ( (LA10_29==']') ) {s = 11;}

                        else if ( (LA10_29=='\'') ) {s = 14;}

                        else if ( ((LA10_29>='\u0000' && LA10_29<='!')||(LA10_29>='#' && LA10_29<='&')||(LA10_29>='(' && LA10_29<='[')||(LA10_29>='^' && LA10_29<='\uFFFF')) ) {s = 15;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA10_77 = input.LA(1);

                        s = -1;
                        if ( (LA10_77=='\"') ) {s = 36;}

                        else if ( (LA10_77=='\\') ) {s = 35;}

                        else if ( (LA10_77==']') ) {s = 34;}

                        else if ( (LA10_77=='\'') ) {s = 37;}

                        else if ( ((LA10_77>='\u0000' && LA10_77<='!')||(LA10_77>='#' && LA10_77<='&')||(LA10_77>='(' && LA10_77<='[')||(LA10_77>='^' && LA10_77<='\uFFFF')) ) {s = 38;}

                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA10_55 = input.LA(1);

                        s = -1;
                        if ( (LA10_55=='\"') ) {s = 52;}

                        else if ( (LA10_55=='\\') ) {s = 53;}

                        else if ( (LA10_55=='\'') ) {s = 54;}

                        else if ( ((LA10_55>='\u0000' && LA10_55<='!')||(LA10_55>='#' && LA10_55<='&')||(LA10_55>='(' && LA10_55<='[')||(LA10_55>=']' && LA10_55<='\uFFFF')) ) {s = 55;}

                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA10_118 = input.LA(1);

                        s = -1;
                        if ( (LA10_118=='\"') ) {s = 99;}

                        else if ( (LA10_118=='\\') ) {s = 100;}

                        else if ( (LA10_118==']') ) {s = 101;}

                        else if ( (LA10_118=='\'') ) {s = 102;}

                        else if ( ((LA10_118>='\u0000' && LA10_118<='!')||(LA10_118>='#' && LA10_118<='&')||(LA10_118>='(' && LA10_118<='[')||(LA10_118>='^' && LA10_118<='\uFFFF')) ) {s = 103;}

                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA10_100 = input.LA(1);

                        s = -1;
                        if ( (LA10_100=='\"') ) {s = 115;}

                        else if ( (LA10_100=='\\') ) {s = 116;}

                        else if ( (LA10_100==']') ) {s = 117;}

                        else if ( (LA10_100=='\'') ) {s = 118;}

                        else if ( ((LA10_100>='\u0000' && LA10_100<='!')||(LA10_100>='#' && LA10_100<='&')||(LA10_100>='(' && LA10_100<='[')||(LA10_100>='^' && LA10_100<='\uFFFF')) ) {s = 119;}

                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA10_45 = input.LA(1);

                         
                        int index10_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 41;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_45);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA10_84 = input.LA(1);

                        s = -1;
                        if ( (LA10_84=='\"') ) {s = 104;}

                        else if ( (LA10_84=='\\') ) {s = 105;}

                        else if ( (LA10_84==']') ) {s = 106;}

                        else if ( (LA10_84=='\'') ) {s = 107;}

                        else if ( ((LA10_84>='\u0000' && LA10_84<='!')||(LA10_84>='#' && LA10_84<='&')||(LA10_84>='(' && LA10_84<='[')||(LA10_84>='^' && LA10_84<='\uFFFF')) ) {s = 108;}

                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA10_64 = input.LA(1);

                        s = -1;
                        if ( (LA10_64=='\"') ) {s = 61;}

                        else if ( (LA10_64=='\\') ) {s = 62;}

                        else if ( (LA10_64=='\'') ) {s = 63;}

                        else if ( ((LA10_64>='\u0000' && LA10_64<='!')||(LA10_64>='#' && LA10_64<='&')||(LA10_64>='(' && LA10_64<='[')||(LA10_64>=']' && LA10_64<='\uFFFF')) ) {s = 64;}

                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA10_79 = input.LA(1);

                        s = -1;
                        if ( (LA10_79==']') ) {s = 47;}

                        else if ( (LA10_79=='\\') ) {s = 48;}

                        else if ( (LA10_79=='\"') ) {s = 49;}

                        else if ( (LA10_79=='\'') ) {s = 50;}

                        else if ( ((LA10_79>='\u0000' && LA10_79<='!')||(LA10_79>='#' && LA10_79<='&')||(LA10_79>='(' && LA10_79<='[')||(LA10_79>='^' && LA10_79<='\uFFFF')) ) {s = 51;}

                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA10_12 = input.LA(1);

                        s = -1;
                        if ( (LA10_12==']') ) {s = 29;}

                        else if ( (LA10_12=='\\') ) {s = 30;}

                        else if ( (LA10_12=='\"') ) {s = 31;}

                        else if ( (LA10_12=='\'') ) {s = 32;}

                        else if ( ((LA10_12>='\u0000' && LA10_12<='!')||(LA10_12>='#' && LA10_12<='&')||(LA10_12>='(' && LA10_12<='[')||(LA10_12>='^' && LA10_12<='\uFFFF')) ) {s = 33;}

                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA10_61 = input.LA(1);

                        s = -1;
                        if ( (LA10_61==']') ) {s = 16;}

                        else if ( (LA10_61=='\\') ) {s = 17;}

                        else if ( (LA10_61=='\"') ) {s = 18;}

                        else if ( (LA10_61=='\'') ) {s = 19;}

                        else if ( ((LA10_61>='\u0000' && LA10_61<='!')||(LA10_61>='#' && LA10_61<='&')||(LA10_61>='(' && LA10_61<='[')||(LA10_61>='^' && LA10_61<='\uFFFF')) ) {s = 20;}

                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA10_35 = input.LA(1);

                        s = -1;
                        if ( (LA10_35==']') ) {s = 56;}

                        else if ( (LA10_35=='\\') ) {s = 57;}

                        else if ( (LA10_35=='\"') ) {s = 58;}

                        else if ( (LA10_35=='\'') ) {s = 59;}

                        else if ( ((LA10_35>='\u0000' && LA10_35<='!')||(LA10_35>='#' && LA10_35<='&')||(LA10_35>='(' && LA10_35<='[')||(LA10_35>='^' && LA10_35<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA10_111 = input.LA(1);

                        s = -1;
                        if ( (LA10_111=='\'') ) {s = 95;}

                        else if ( (LA10_111=='\\') ) {s = 94;}

                        else if ( (LA10_111==']') ) {s = 97;}

                        else if ( (LA10_111=='\"') ) {s = 96;}

                        else if ( ((LA10_111>='\u0000' && LA10_111<='!')||(LA10_111>='#' && LA10_111<='&')||(LA10_111>='(' && LA10_111<='[')||(LA10_111>='^' && LA10_111<='\uFFFF')) ) {s = 98;}

                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA10_48 = input.LA(1);

                        s = -1;
                        if ( (LA10_48=='\"') ) {s = 65;}

                        else if ( (LA10_48=='\\') ) {s = 66;}

                        else if ( (LA10_48==']') ) {s = 67;}

                        else if ( (LA10_48=='\'') ) {s = 68;}

                        else if ( ((LA10_48>='\u0000' && LA10_48<='!')||(LA10_48>='#' && LA10_48<='&')||(LA10_48>='(' && LA10_48<='[')||(LA10_48>='^' && LA10_48<='\uFFFF')) ) {s = 69;}

                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA10_6 = input.LA(1);

                         
                        int index10_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_6==']') && (synpred3_ANTLRLexer())) {s = 21;}

                        else if ( (LA10_6=='\\') && (synpred3_ANTLRLexer())) {s = 22;}

                        else if ( (LA10_6=='\"') && (synpred3_ANTLRLexer())) {s = 23;}

                        else if ( (LA10_6=='\'') && (synpred3_ANTLRLexer())) {s = 24;}

                        else if ( ((LA10_6>='\u0000' && LA10_6<='!')||(LA10_6>='#' && LA10_6<='&')||(LA10_6>='(' && LA10_6<='[')||(LA10_6>='^' && LA10_6<='\uFFFF')) && (synpred3_ANTLRLexer())) {s = 25;}

                        else s = 5;

                         
                        input.seek(index10_6);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA10_7 = input.LA(1);

                         
                        int index10_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_ANTLRLexer()) ) {s = 25;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_7);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA10_58 = input.LA(1);

                         
                        int index10_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 28;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_58);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA10_10 = input.LA(1);

                         
                        int index10_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_ANTLRLexer()) ) {s = 25;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_10);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA10_9 = input.LA(1);

                         
                        int index10_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_ANTLRLexer()) ) {s = 25;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_9);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA10_70 = input.LA(1);

                         
                        int index10_70 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 28;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_70);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA10_59 = input.LA(1);

                        s = -1;
                        if ( (LA10_59==']') ) {s = 34;}

                        else if ( (LA10_59=='\\') ) {s = 35;}

                        else if ( (LA10_59=='\"') ) {s = 36;}

                        else if ( (LA10_59=='\'') ) {s = 37;}

                        else if ( ((LA10_59>='\u0000' && LA10_59<='!')||(LA10_59>='#' && LA10_59<='&')||(LA10_59>='(' && LA10_59<='[')||(LA10_59>='^' && LA10_59<='\uFFFF')) ) {s = 38;}

                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA10_67 = input.LA(1);

                        s = -1;
                        if ( (LA10_67=='\"') ) {s = 49;}

                        else if ( (LA10_67=='\\') ) {s = 48;}

                        else if ( (LA10_67==']') ) {s = 47;}

                        else if ( (LA10_67=='\'') ) {s = 50;}

                        else if ( ((LA10_67>='\u0000' && LA10_67<='!')||(LA10_67>='#' && LA10_67<='&')||(LA10_67>='(' && LA10_67<='[')||(LA10_67>='^' && LA10_67<='\uFFFF')) ) {s = 51;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA10_99 = input.LA(1);

                         
                        int index10_99 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 109;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_99);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA10_31 = input.LA(1);

                         
                        int index10_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 28;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_31);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA10_11 = input.LA(1);

                         
                        int index10_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_11=='\"') && (synpred5_ANTLRLexer())) {s = 26;}

                        else if ( (LA10_11=='\\') && (synpred5_ANTLRLexer())) {s = 27;}

                        else if ( ((LA10_11>='\u0000' && LA10_11<='!')||(LA10_11>='#' && LA10_11<='[')||(LA10_11>=']' && LA10_11<='\uFFFF')) && (synpred5_ANTLRLexer())) {s = 28;}

                        else s = 5;

                         
                        input.seek(index10_11);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA10_71 = input.LA(1);

                        s = -1;
                        if ( (LA10_71==']') ) {s = 88;}

                        else if ( (LA10_71=='\'') ) {s = 89;}

                        else if ( (LA10_71=='\\') ) {s = 90;}

                        else if ( (LA10_71=='\"') ) {s = 91;}

                        else if ( ((LA10_71>='\u0000' && LA10_71<='!')||(LA10_71>='#' && LA10_71<='&')||(LA10_71>='(' && LA10_71<='[')||(LA10_71>='^' && LA10_71<='\uFFFF')) ) {s = 92;}

                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA10_3 = input.LA(1);

                        s = -1;
                        if ( (LA10_3==']') ) {s = 11;}

                        else if ( (LA10_3=='\\') ) {s = 12;}

                        else if ( (LA10_3=='\"') ) {s = 13;}

                        else if ( (LA10_3=='\'') ) {s = 14;}

                        else if ( ((LA10_3>='\u0000' && LA10_3<='!')||(LA10_3>='#' && LA10_3<='&')||(LA10_3>='(' && LA10_3<='[')||(LA10_3>='^' && LA10_3<='\uFFFF')) ) {s = 15;}

                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA10_50 = input.LA(1);

                         
                        int index10_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 41;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_50);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA10_36 = input.LA(1);

                         
                        int index10_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 28;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_36);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA10_0 = input.LA(1);

                        s = -1;
                        if ( (LA10_0==']') ) {s = 1;}

                        else if ( (LA10_0=='\\') ) {s = 2;}

                        else if ( (LA10_0=='\"') ) {s = 3;}

                        else if ( (LA10_0=='\'') ) {s = 4;}

                        else if ( ((LA10_0>='\u0000' && LA10_0<='!')||(LA10_0>='#' && LA10_0<='&')||(LA10_0>='(' && LA10_0<='[')||(LA10_0>='^' && LA10_0<='\uFFFF')) ) {s = 5;}

                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA10_20 = input.LA(1);

                        s = -1;
                        if ( (LA10_20==']') ) {s = 16;}

                        else if ( (LA10_20=='\\') ) {s = 17;}

                        else if ( (LA10_20=='\"') ) {s = 18;}

                        else if ( (LA10_20=='\'') ) {s = 19;}

                        else if ( ((LA10_20>='\u0000' && LA10_20<='!')||(LA10_20>='#' && LA10_20<='&')||(LA10_20>='(' && LA10_20<='[')||(LA10_20>='^' && LA10_20<='\uFFFF')) ) {s = 20;}

                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA10_65 = input.LA(1);

                        s = -1;
                        if ( (LA10_65==']') ) {s = 47;}

                        else if ( (LA10_65=='\\') ) {s = 48;}

                        else if ( (LA10_65=='\"') ) {s = 49;}

                        else if ( (LA10_65=='\'') ) {s = 50;}

                        else if ( ((LA10_65>='\u0000' && LA10_65<='!')||(LA10_65>='#' && LA10_65<='&')||(LA10_65>='(' && LA10_65<='[')||(LA10_65>='^' && LA10_65<='\uFFFF')) ) {s = 51;}

                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA10_85 = input.LA(1);

                         
                        int index10_85 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA10_85>='\u0000' && LA10_85<='\uFFFF')) ) {s = 5;}

                        else s = 109;

                         
                        input.seek(index10_85);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA10_46 = input.LA(1);

                        s = -1;
                        if ( (LA10_46==']') ) {s = 16;}

                        else if ( (LA10_46=='\\') ) {s = 17;}

                        else if ( (LA10_46=='\"') ) {s = 18;}

                        else if ( (LA10_46=='\'') ) {s = 19;}

                        else if ( ((LA10_46>='\u0000' && LA10_46<='!')||(LA10_46>='#' && LA10_46<='&')||(LA10_46>='(' && LA10_46<='[')||(LA10_46>='^' && LA10_46<='\uFFFF')) ) {s = 20;}

                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA10_53 = input.LA(1);

                        s = -1;
                        if ( (LA10_53=='\"') ) {s = 75;}

                        else if ( (LA10_53=='\\') ) {s = 76;}

                        else if ( (LA10_53=='\'') ) {s = 77;}

                        else if ( ((LA10_53>='\u0000' && LA10_53<='!')||(LA10_53>='#' && LA10_53<='&')||(LA10_53>='(' && LA10_53<='[')||(LA10_53>=']' && LA10_53<='\uFFFF')) ) {s = 78;}

                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA10_43 = input.LA(1);

                        s = -1;
                        if ( (LA10_43==']') ) {s = 42;}

                        else if ( (LA10_43=='\\') ) {s = 43;}

                        else if ( (LA10_43=='\"') ) {s = 44;}

                        else if ( (LA10_43=='\'') ) {s = 45;}

                        else if ( ((LA10_43>='\u0000' && LA10_43<='!')||(LA10_43>='#' && LA10_43<='&')||(LA10_43>='(' && LA10_43<='[')||(LA10_43>='^' && LA10_43<='\uFFFF')) ) {s = 46;}

                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA10_115 = input.LA(1);

                         
                        int index10_115 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 109;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_115);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA10_112 = input.LA(1);

                        s = -1;
                        if ( (LA10_112==']') ) {s = 110;}

                        else if ( (LA10_112=='\"') ) {s = 111;}

                        else if ( (LA10_112=='\\') ) {s = 112;}

                        else if ( (LA10_112=='\'') ) {s = 113;}

                        else if ( ((LA10_112>='\u0000' && LA10_112<='!')||(LA10_112>='#' && LA10_112<='&')||(LA10_112>='(' && LA10_112<='[')||(LA10_112>='^' && LA10_112<='\uFFFF')) ) {s = 114;}

                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA10_88 = input.LA(1);

                         
                        int index10_88 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_88=='\'') ) {s = 70;}

                        else if ( (LA10_88=='\\') ) {s = 71;}

                        else if ( (LA10_88==']') ) {s = 72;}

                        else if ( (LA10_88=='\"') ) {s = 73;}

                        else if ( ((LA10_88>='\u0000' && LA10_88<='!')||(LA10_88>='#' && LA10_88<='&')||(LA10_88>='(' && LA10_88<='[')||(LA10_88>='^' && LA10_88<='\uFFFF')) ) {s = 74;}

                        else s = 93;

                         
                        input.seek(index10_88);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA10_83 = input.LA(1);

                         
                        int index10_83 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 41;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_83);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA10_86 = input.LA(1);

                        s = -1;
                        if ( (LA10_86=='\"') ) {s = 99;}

                        else if ( (LA10_86=='\\') ) {s = 100;}

                        else if ( (LA10_86==']') ) {s = 101;}

                        else if ( (LA10_86=='\'') ) {s = 102;}

                        else if ( ((LA10_86>='\u0000' && LA10_86<='!')||(LA10_86>='#' && LA10_86<='&')||(LA10_86>='(' && LA10_86<='[')||(LA10_86>='^' && LA10_86<='\uFFFF')) ) {s = 103;}

                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA10_14 = input.LA(1);

                        s = -1;
                        if ( (LA10_14==']') ) {s = 34;}

                        else if ( (LA10_14=='\\') ) {s = 35;}

                        else if ( (LA10_14=='\"') ) {s = 36;}

                        else if ( (LA10_14=='\'') ) {s = 37;}

                        else if ( ((LA10_14>='\u0000' && LA10_14<='!')||(LA10_14>='#' && LA10_14<='&')||(LA10_14>='(' && LA10_14<='[')||(LA10_14>='^' && LA10_14<='\uFFFF')) ) {s = 38;}

                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA10_63 = input.LA(1);

                        s = -1;
                        if ( (LA10_63=='\"') ) {s = 83;}

                        else if ( (LA10_63=='\\') ) {s = 84;}

                        else if ( (LA10_63==']') ) {s = 85;}

                        else if ( (LA10_63=='\'') ) {s = 86;}

                        else if ( ((LA10_63>='\u0000' && LA10_63<='!')||(LA10_63>='#' && LA10_63<='&')||(LA10_63>='(' && LA10_63<='[')||(LA10_63>='^' && LA10_63<='\uFFFF')) ) {s = 87;}

                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA10_51 = input.LA(1);

                        s = -1;
                        if ( (LA10_51=='\"') ) {s = 49;}

                        else if ( (LA10_51=='\\') ) {s = 48;}

                        else if ( (LA10_51==']') ) {s = 47;}

                        else if ( (LA10_51=='\'') ) {s = 50;}

                        else if ( ((LA10_51>='\u0000' && LA10_51<='!')||(LA10_51>='#' && LA10_51<='&')||(LA10_51>='(' && LA10_51<='[')||(LA10_51>='^' && LA10_51<='\uFFFF')) ) {s = 51;}

                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA10_13 = input.LA(1);

                         
                        int index10_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 28;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_13);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA10_66 = input.LA(1);

                        s = -1;
                        if ( (LA10_66==']') ) {s = 67;}

                        else if ( (LA10_66=='\\') ) {s = 66;}

                        else if ( (LA10_66=='\"') ) {s = 65;}

                        else if ( (LA10_66=='\'') ) {s = 68;}

                        else if ( ((LA10_66>='\u0000' && LA10_66<='!')||(LA10_66>='#' && LA10_66<='&')||(LA10_66>='(' && LA10_66<='[')||(LA10_66>='^' && LA10_66<='\uFFFF')) ) {s = 69;}

                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA10_117 = input.LA(1);

                         
                        int index10_117 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_117=='\"') ) {s = 99;}

                        else if ( (LA10_117=='\\') ) {s = 100;}

                        else if ( (LA10_117==']') ) {s = 101;}

                        else if ( (LA10_117=='\'') ) {s = 102;}

                        else if ( ((LA10_117>='\u0000' && LA10_117<='!')||(LA10_117>='#' && LA10_117<='&')||(LA10_117>='(' && LA10_117<='[')||(LA10_117>='^' && LA10_117<='\uFFFF')) ) {s = 103;}

                        else s = 109;

                         
                        input.seek(index10_117);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA10_49 = input.LA(1);

                        s = -1;
                        if ( (LA10_49==']') ) {s = 47;}

                        else if ( (LA10_49=='\\') ) {s = 48;}

                        else if ( (LA10_49=='\"') ) {s = 49;}

                        else if ( (LA10_49=='\'') ) {s = 50;}

                        else if ( ((LA10_49>='\u0000' && LA10_49<='!')||(LA10_49>='#' && LA10_49<='&')||(LA10_49>='(' && LA10_49<='[')||(LA10_49>='^' && LA10_49<='\uFFFF')) ) {s = 51;}

                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA10_62 = input.LA(1);

                        s = -1;
                        if ( (LA10_62=='\"') ) {s = 79;}

                        else if ( (LA10_62=='\\') ) {s = 80;}

                        else if ( (LA10_62=='\'') ) {s = 81;}

                        else if ( ((LA10_62>='\u0000' && LA10_62<='!')||(LA10_62>='#' && LA10_62<='&')||(LA10_62>='(' && LA10_62<='[')||(LA10_62>=']' && LA10_62<='\uFFFF')) ) {s = 82;}

                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA10_78 = input.LA(1);

                        s = -1;
                        if ( (LA10_78=='\"') ) {s = 52;}

                        else if ( (LA10_78=='\\') ) {s = 53;}

                        else if ( (LA10_78=='\'') ) {s = 54;}

                        else if ( ((LA10_78>='\u0000' && LA10_78<='!')||(LA10_78>='#' && LA10_78<='&')||(LA10_78>='(' && LA10_78<='[')||(LA10_78>=']' && LA10_78<='\uFFFF')) ) {s = 55;}

                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA10_114 = input.LA(1);

                        s = -1;
                        if ( (LA10_114=='\"') ) {s = 96;}

                        else if ( (LA10_114=='\\') ) {s = 94;}

                        else if ( (LA10_114=='\'') ) {s = 95;}

                        else if ( (LA10_114==']') ) {s = 97;}

                        else if ( ((LA10_114>='\u0000' && LA10_114<='!')||(LA10_114>='#' && LA10_114<='&')||(LA10_114>='(' && LA10_114<='[')||(LA10_114>='^' && LA10_114<='\uFFFF')) ) {s = 98;}

                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA10_56 = input.LA(1);

                        s = -1;
                        if ( (LA10_56=='\"') ) {s = 36;}

                        else if ( (LA10_56=='\\') ) {s = 35;}

                        else if ( (LA10_56=='\'') ) {s = 37;}

                        else if ( (LA10_56==']') ) {s = 34;}

                        else if ( ((LA10_56>='\u0000' && LA10_56<='!')||(LA10_56>='#' && LA10_56<='&')||(LA10_56>='(' && LA10_56<='[')||(LA10_56>='^' && LA10_56<='\uFFFF')) ) {s = 38;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA10_42 = input.LA(1);

                        s = -1;
                        if ( (LA10_42==']') ) {s = 16;}

                        else if ( (LA10_42=='\\') ) {s = 17;}

                        else if ( (LA10_42=='\"') ) {s = 18;}

                        else if ( (LA10_42=='\'') ) {s = 19;}

                        else if ( ((LA10_42>='\u0000' && LA10_42<='!')||(LA10_42>='#' && LA10_42<='&')||(LA10_42>='(' && LA10_42<='[')||(LA10_42>='^' && LA10_42<='\uFFFF')) ) {s = 20;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA10_91 = input.LA(1);

                        s = -1;
                        if ( (LA10_91=='\'') ) {s = 95;}

                        else if ( (LA10_91=='\\') ) {s = 94;}

                        else if ( (LA10_91==']') ) {s = 97;}

                        else if ( (LA10_91=='\"') ) {s = 96;}

                        else if ( ((LA10_91>='\u0000' && LA10_91<='!')||(LA10_91>='#' && LA10_91<='&')||(LA10_91>='(' && LA10_91<='[')||(LA10_91>='^' && LA10_91<='\uFFFF')) ) {s = 98;}

                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA10_104 = input.LA(1);

                         
                        int index10_104 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 109;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_104);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA10_57 = input.LA(1);

                        s = -1;
                        if ( (LA10_57==']') ) {s = 56;}

                        else if ( (LA10_57=='\\') ) {s = 57;}

                        else if ( (LA10_57=='\"') ) {s = 58;}

                        else if ( (LA10_57=='\'') ) {s = 59;}

                        else if ( ((LA10_57>='\u0000' && LA10_57<='!')||(LA10_57>='#' && LA10_57<='&')||(LA10_57>='(' && LA10_57<='[')||(LA10_57>='^' && LA10_57<='\uFFFF')) ) {s = 60;}

                        if ( s>=0 ) return s;
                        break;
                    case 63 : 
                        int LA10_113 = input.LA(1);

                         
                        int index10_113 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_ANTLRLexer()) ) {s = 93;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_113);
                        if ( s>=0 ) return s;
                        break;
                    case 64 : 
                        int LA10_110 = input.LA(1);

                         
                        int index10_110 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_110=='\"') ) {s = 96;}

                        else if ( (LA10_110=='\\') ) {s = 94;}

                        else if ( (LA10_110=='\'') ) {s = 95;}

                        else if ( (LA10_110==']') ) {s = 97;}

                        else if ( ((LA10_110>='\u0000' && LA10_110<='!')||(LA10_110>='#' && LA10_110<='&')||(LA10_110>='(' && LA10_110<='[')||(LA10_110>='^' && LA10_110<='\uFFFF')) ) {s = 98;}

                        else s = 93;

                         
                        input.seek(index10_110);
                        if ( s>=0 ) return s;
                        break;
                    case 65 : 
                        int LA10_90 = input.LA(1);

                        s = -1;
                        if ( (LA10_90==']') ) {s = 88;}

                        else if ( (LA10_90=='\'') ) {s = 89;}

                        else if ( (LA10_90=='\\') ) {s = 90;}

                        else if ( (LA10_90=='\"') ) {s = 91;}

                        else if ( ((LA10_90>='\u0000' && LA10_90<='!')||(LA10_90>='#' && LA10_90<='&')||(LA10_90>='(' && LA10_90<='[')||(LA10_90>='^' && LA10_90<='\uFFFF')) ) {s = 92;}

                        if ( s>=0 ) return s;
                        break;
                    case 66 : 
                        int LA10_37 = input.LA(1);

                        s = -1;
                        if ( (LA10_37==']') ) {s = 34;}

                        else if ( (LA10_37=='\\') ) {s = 35;}

                        else if ( (LA10_37=='\"') ) {s = 36;}

                        else if ( (LA10_37=='\'') ) {s = 37;}

                        else if ( ((LA10_37>='\u0000' && LA10_37<='!')||(LA10_37>='#' && LA10_37<='&')||(LA10_37>='(' && LA10_37<='[')||(LA10_37>='^' && LA10_37<='\uFFFF')) ) {s = 38;}

                        if ( s>=0 ) return s;
                        break;
                    case 67 : 
                        int LA10_116 = input.LA(1);

                        s = -1;
                        if ( (LA10_116=='\"') ) {s = 115;}

                        else if ( (LA10_116=='\\') ) {s = 116;}

                        else if ( (LA10_116==']') ) {s = 117;}

                        else if ( (LA10_116=='\'') ) {s = 118;}

                        else if ( ((LA10_116>='\u0000' && LA10_116<='!')||(LA10_116>='#' && LA10_116<='&')||(LA10_116>='(' && LA10_116<='[')||(LA10_116>='^' && LA10_116<='\uFFFF')) ) {s = 119;}

                        if ( s>=0 ) return s;
                        break;
                    case 68 : 
                        int LA10_4 = input.LA(1);

                        s = -1;
                        if ( (LA10_4==']') ) {s = 16;}

                        else if ( (LA10_4=='\\') ) {s = 17;}

                        else if ( (LA10_4=='\"') ) {s = 18;}

                        else if ( (LA10_4=='\'') ) {s = 19;}

                        else if ( ((LA10_4>='\u0000' && LA10_4<='!')||(LA10_4>='#' && LA10_4<='&')||(LA10_4>='(' && LA10_4<='[')||(LA10_4>='^' && LA10_4<='\uFFFF')) ) {s = 20;}

                        if ( s>=0 ) return s;
                        break;
                    case 69 : 
                        int LA10_119 = input.LA(1);

                        s = -1;
                        if ( (LA10_119=='\"') ) {s = 99;}

                        else if ( (LA10_119=='\\') ) {s = 100;}

                        else if ( (LA10_119=='\'') ) {s = 102;}

                        else if ( (LA10_119==']') ) {s = 101;}

                        else if ( ((LA10_119>='\u0000' && LA10_119<='!')||(LA10_119>='#' && LA10_119<='&')||(LA10_119>='(' && LA10_119<='[')||(LA10_119>='^' && LA10_119<='\uFFFF')) ) {s = 103;}

                        if ( s>=0 ) return s;
                        break;
                    case 70 : 
                        int LA10_54 = input.LA(1);

                        s = -1;
                        if ( (LA10_54=='\"') ) {s = 13;}

                        else if ( (LA10_54=='\\') ) {s = 12;}

                        else if ( (LA10_54==']') ) {s = 11;}

                        else if ( (LA10_54=='\'') ) {s = 14;}

                        else if ( ((LA10_54>='\u0000' && LA10_54<='!')||(LA10_54>='#' && LA10_54<='&')||(LA10_54>='(' && LA10_54<='[')||(LA10_54>='^' && LA10_54<='\uFFFF')) ) {s = 15;}

                        if ( s>=0 ) return s;
                        break;
                    case 71 : 
                        int LA10_73 = input.LA(1);

                        s = -1;
                        if ( (LA10_73=='\\') ) {s = 94;}

                        else if ( (LA10_73=='\'') ) {s = 95;}

                        else if ( (LA10_73=='\"') ) {s = 96;}

                        else if ( (LA10_73==']') ) {s = 97;}

                        else if ( ((LA10_73>='\u0000' && LA10_73<='!')||(LA10_73>='#' && LA10_73<='&')||(LA10_73>='(' && LA10_73<='[')||(LA10_73>='^' && LA10_73<='\uFFFF')) ) {s = 98;}

                        if ( s>=0 ) return s;
                        break;
                    case 72 : 
                        int LA10_17 = input.LA(1);

                        s = -1;
                        if ( (LA10_17==']') ) {s = 42;}

                        else if ( (LA10_17=='\\') ) {s = 43;}

                        else if ( (LA10_17=='\"') ) {s = 44;}

                        else if ( (LA10_17=='\'') ) {s = 45;}

                        else if ( ((LA10_17>='\u0000' && LA10_17<='!')||(LA10_17>='#' && LA10_17<='&')||(LA10_17>='(' && LA10_17<='[')||(LA10_17>='^' && LA10_17<='\uFFFF')) ) {s = 46;}

                        if ( s>=0 ) return s;
                        break;
                    case 73 : 
                        int LA10_105 = input.LA(1);

                        s = -1;
                        if ( (LA10_105=='\"') ) {s = 104;}

                        else if ( (LA10_105=='\\') ) {s = 105;}

                        else if ( (LA10_105==']') ) {s = 106;}

                        else if ( (LA10_105=='\'') ) {s = 107;}

                        else if ( ((LA10_105>='\u0000' && LA10_105<='!')||(LA10_105>='#' && LA10_105<='&')||(LA10_105>='(' && LA10_105<='[')||(LA10_105>='^' && LA10_105<='\uFFFF')) ) {s = 108;}

                        if ( s>=0 ) return s;
                        break;
                    case 74 : 
                        int LA10_30 = input.LA(1);

                        s = -1;
                        if ( (LA10_30==']') ) {s = 29;}

                        else if ( (LA10_30=='\\') ) {s = 30;}

                        else if ( (LA10_30=='\"') ) {s = 31;}

                        else if ( (LA10_30=='\'') ) {s = 32;}

                        else if ( ((LA10_30>='\u0000' && LA10_30<='!')||(LA10_30>='#' && LA10_30<='&')||(LA10_30>='(' && LA10_30<='[')||(LA10_30>='^' && LA10_30<='\uFFFF')) ) {s = 33;}

                        if ( s>=0 ) return s;
                        break;
                    case 75 : 
                        int LA10_81 = input.LA(1);

                        s = -1;
                        if ( (LA10_81=='\"') ) {s = 99;}

                        else if ( (LA10_81=='\\') ) {s = 100;}

                        else if ( (LA10_81==']') ) {s = 101;}

                        else if ( (LA10_81=='\'') ) {s = 102;}

                        else if ( ((LA10_81>='\u0000' && LA10_81<='!')||(LA10_81>='#' && LA10_81<='&')||(LA10_81>='(' && LA10_81<='[')||(LA10_81>='^' && LA10_81<='\uFFFF')) ) {s = 103;}

                        if ( s>=0 ) return s;
                        break;
                    case 76 : 
                        int LA10_108 = input.LA(1);

                        s = -1;
                        if ( (LA10_108=='\"') ) {s = 83;}

                        else if ( (LA10_108=='\\') ) {s = 84;}

                        else if ( (LA10_108==']') ) {s = 85;}

                        else if ( (LA10_108=='\'') ) {s = 86;}

                        else if ( ((LA10_108>='\u0000' && LA10_108<='!')||(LA10_108>='#' && LA10_108<='&')||(LA10_108>='(' && LA10_108<='[')||(LA10_108>='^' && LA10_108<='\uFFFF')) ) {s = 87;}

                        if ( s>=0 ) return s;
                        break;
                    case 77 : 
                        int LA10_80 = input.LA(1);

                        s = -1;
                        if ( (LA10_80=='\"') ) {s = 79;}

                        else if ( (LA10_80=='\\') ) {s = 80;}

                        else if ( (LA10_80=='\'') ) {s = 81;}

                        else if ( ((LA10_80>='\u0000' && LA10_80<='!')||(LA10_80>='#' && LA10_80<='&')||(LA10_80>='(' && LA10_80<='[')||(LA10_80>=']' && LA10_80<='\uFFFF')) ) {s = 82;}

                        if ( s>=0 ) return s;
                        break;
                    case 78 : 
                        int LA10_102 = input.LA(1);

                        s = -1;
                        if ( (LA10_102=='\"') ) {s = 99;}

                        else if ( (LA10_102=='\\') ) {s = 100;}

                        else if ( (LA10_102==']') ) {s = 101;}

                        else if ( (LA10_102=='\'') ) {s = 102;}

                        else if ( ((LA10_102>='\u0000' && LA10_102<='!')||(LA10_102>='#' && LA10_102<='&')||(LA10_102>='(' && LA10_102<='[')||(LA10_102>='^' && LA10_102<='\uFFFF')) ) {s = 103;}

                        if ( s>=0 ) return s;
                        break;
                    case 79 : 
                        int LA10_60 = input.LA(1);

                        s = -1;
                        if ( (LA10_60==']') ) {s = 34;}

                        else if ( (LA10_60=='\\') ) {s = 35;}

                        else if ( (LA10_60=='\"') ) {s = 36;}

                        else if ( (LA10_60=='\'') ) {s = 37;}

                        else if ( ((LA10_60>='\u0000' && LA10_60<='!')||(LA10_60>='#' && LA10_60<='&')||(LA10_60>='(' && LA10_60<='[')||(LA10_60>='^' && LA10_60<='\uFFFF')) ) {s = 38;}

                        if ( s>=0 ) return s;
                        break;
                    case 80 : 
                        int LA10_8 = input.LA(1);

                         
                        int index10_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_ANTLRLexer()) ) {s = 25;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_8);
                        if ( s>=0 ) return s;
                        break;
                    case 81 : 
                        int LA10_33 = input.LA(1);

                        s = -1;
                        if ( (LA10_33==']') ) {s = 11;}

                        else if ( (LA10_33=='\\') ) {s = 12;}

                        else if ( (LA10_33=='\"') ) {s = 13;}

                        else if ( (LA10_33=='\'') ) {s = 14;}

                        else if ( ((LA10_33>='\u0000' && LA10_33<='!')||(LA10_33>='#' && LA10_33<='&')||(LA10_33>='(' && LA10_33<='[')||(LA10_33>='^' && LA10_33<='\uFFFF')) ) {s = 15;}

                        if ( s>=0 ) return s;
                        break;
                    case 82 : 
                        int LA10_2 = input.LA(1);

                        s = -1;
                        if ( (LA10_2==']') ) {s = 6;}

                        else if ( (LA10_2=='\\') ) {s = 7;}

                        else if ( (LA10_2=='\"') ) {s = 8;}

                        else if ( (LA10_2=='\'') ) {s = 9;}

                        else if ( ((LA10_2>='\u0000' && LA10_2<='!')||(LA10_2>='#' && LA10_2<='&')||(LA10_2>='(' && LA10_2<='[')||(LA10_2>='^' && LA10_2<='\uFFFF')) ) {s = 10;}

                        if ( s>=0 ) return s;
                        break;
                    case 83 : 
                        int LA10_96 = input.LA(1);

                        s = -1;
                        if ( (LA10_96=='\\') ) {s = 94;}

                        else if ( (LA10_96=='\'') ) {s = 95;}

                        else if ( (LA10_96=='\"') ) {s = 96;}

                        else if ( (LA10_96==']') ) {s = 97;}

                        else if ( ((LA10_96>='\u0000' && LA10_96<='!')||(LA10_96>='#' && LA10_96<='&')||(LA10_96>='(' && LA10_96<='[')||(LA10_96>='^' && LA10_96<='\uFFFF')) ) {s = 98;}

                        if ( s>=0 ) return s;
                        break;
                    case 84 : 
                        int LA10_98 = input.LA(1);

                        s = -1;
                        if ( (LA10_98=='\"') ) {s = 96;}

                        else if ( (LA10_98=='\\') ) {s = 94;}

                        else if ( (LA10_98=='\'') ) {s = 95;}

                        else if ( (LA10_98==']') ) {s = 97;}

                        else if ( ((LA10_98>='\u0000' && LA10_98<='!')||(LA10_98>='#' && LA10_98<='&')||(LA10_98>='(' && LA10_98<='[')||(LA10_98>='^' && LA10_98<='\uFFFF')) ) {s = 98;}

                        if ( s>=0 ) return s;
                        break;
                    case 85 : 
                        int LA10_16 = input.LA(1);

                         
                        int index10_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_16=='\'') && (synpred6_ANTLRLexer())) {s = 39;}

                        else if ( (LA10_16=='\\') && (synpred6_ANTLRLexer())) {s = 40;}

                        else if ( ((LA10_16>='\u0000' && LA10_16<='&')||(LA10_16>='(' && LA10_16<='[')||(LA10_16>=']' && LA10_16<='\uFFFF')) && (synpred6_ANTLRLexer())) {s = 41;}

                        else s = 5;

                         
                        input.seek(index10_16);
                        if ( s>=0 ) return s;
                        break;
                    case 86 : 
                        int LA10_69 = input.LA(1);

                        s = -1;
                        if ( (LA10_69==']') ) {s = 47;}

                        else if ( (LA10_69=='\\') ) {s = 48;}

                        else if ( (LA10_69=='\"') ) {s = 49;}

                        else if ( (LA10_69=='\'') ) {s = 50;}

                        else if ( ((LA10_69>='\u0000' && LA10_69<='!')||(LA10_69>='#' && LA10_69<='&')||(LA10_69>='(' && LA10_69<='[')||(LA10_69>='^' && LA10_69<='\uFFFF')) ) {s = 51;}

                        if ( s>=0 ) return s;
                        break;
                    case 87 : 
                        int LA10_72 = input.LA(1);

                         
                        int index10_72 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA10_72>='\u0000' && LA10_72<='\uFFFF')) ) {s = 5;}

                        else s = 93;

                         
                        input.seek(index10_72);
                        if ( s>=0 ) return s;
                        break;
                    case 88 : 
                        int LA10_82 = input.LA(1);

                        s = -1;
                        if ( (LA10_82=='\"') ) {s = 61;}

                        else if ( (LA10_82=='\\') ) {s = 62;}

                        else if ( (LA10_82=='\'') ) {s = 63;}

                        else if ( ((LA10_82>='\u0000' && LA10_82<='!')||(LA10_82>='#' && LA10_82<='&')||(LA10_82>='(' && LA10_82<='[')||(LA10_82>=']' && LA10_82<='\uFFFF')) ) {s = 64;}

                        if ( s>=0 ) return s;
                        break;
                    case 89 : 
                        int LA10_87 = input.LA(1);

                        s = -1;
                        if ( (LA10_87=='\"') ) {s = 83;}

                        else if ( (LA10_87=='\\') ) {s = 84;}

                        else if ( (LA10_87==']') ) {s = 85;}

                        else if ( (LA10_87=='\'') ) {s = 86;}

                        else if ( ((LA10_87>='\u0000' && LA10_87<='!')||(LA10_87>='#' && LA10_87<='&')||(LA10_87>='(' && LA10_87<='[')||(LA10_87>='^' && LA10_87<='\uFFFF')) ) {s = 87;}

                        if ( s>=0 ) return s;
                        break;
                    case 90 : 
                        int LA10_44 = input.LA(1);

                        s = -1;
                        if ( (LA10_44==']') ) {s = 47;}

                        else if ( (LA10_44=='\\') ) {s = 48;}

                        else if ( (LA10_44=='\"') ) {s = 49;}

                        else if ( (LA10_44=='\'') ) {s = 50;}

                        else if ( ((LA10_44>='\u0000' && LA10_44<='!')||(LA10_44>='#' && LA10_44<='&')||(LA10_44>='(' && LA10_44<='[')||(LA10_44>='^' && LA10_44<='\uFFFF')) ) {s = 51;}

                        if ( s>=0 ) return s;
                        break;
                    case 91 : 
                        int LA10_15 = input.LA(1);

                        s = -1;
                        if ( (LA10_15==']') ) {s = 11;}

                        else if ( (LA10_15=='\\') ) {s = 12;}

                        else if ( (LA10_15=='\"') ) {s = 13;}

                        else if ( (LA10_15=='\'') ) {s = 14;}

                        else if ( ((LA10_15>='\u0000' && LA10_15<='!')||(LA10_15>='#' && LA10_15<='&')||(LA10_15>='(' && LA10_15<='[')||(LA10_15>='^' && LA10_15<='\uFFFF')) ) {s = 15;}

                        if ( s>=0 ) return s;
                        break;
                    case 92 : 
                        int LA10_94 = input.LA(1);

                        s = -1;
                        if ( (LA10_94==']') ) {s = 110;}

                        else if ( (LA10_94=='\"') ) {s = 111;}

                        else if ( (LA10_94=='\\') ) {s = 112;}

                        else if ( (LA10_94=='\'') ) {s = 113;}

                        else if ( ((LA10_94>='\u0000' && LA10_94<='!')||(LA10_94>='#' && LA10_94<='&')||(LA10_94>='(' && LA10_94<='[')||(LA10_94>='^' && LA10_94<='\uFFFF')) ) {s = 114;}

                        if ( s>=0 ) return s;
                        break;
                    case 93 : 
                        int LA10_97 = input.LA(1);

                         
                        int index10_97 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_97=='\"') ) {s = 52;}

                        else if ( (LA10_97=='\\') ) {s = 53;}

                        else if ( (LA10_97=='\'') ) {s = 54;}

                        else if ( ((LA10_97>='\u0000' && LA10_97<='!')||(LA10_97>='#' && LA10_97<='&')||(LA10_97>='(' && LA10_97<='[')||(LA10_97>=']' && LA10_97<='\uFFFF')) ) {s = 55;}

                        else s = 93;

                         
                        input.seek(index10_97);
                        if ( s>=0 ) return s;
                        break;
                    case 94 : 
                        int LA10_18 = input.LA(1);

                        s = -1;
                        if ( (LA10_18==']') ) {s = 47;}

                        else if ( (LA10_18=='\\') ) {s = 48;}

                        else if ( (LA10_18=='\"') ) {s = 49;}

                        else if ( (LA10_18=='\'') ) {s = 50;}

                        else if ( ((LA10_18>='\u0000' && LA10_18<='!')||(LA10_18>='#' && LA10_18<='&')||(LA10_18>='(' && LA10_18<='[')||(LA10_18>='^' && LA10_18<='\uFFFF')) ) {s = 51;}

                        if ( s>=0 ) return s;
                        break;
                    case 95 : 
                        int LA10_75 = input.LA(1);

                        s = -1;
                        if ( (LA10_75=='\'') ) {s = 95;}

                        else if ( (LA10_75=='\\') ) {s = 94;}

                        else if ( (LA10_75==']') ) {s = 97;}

                        else if ( (LA10_75=='\"') ) {s = 96;}

                        else if ( ((LA10_75>='\u0000' && LA10_75<='!')||(LA10_75>='#' && LA10_75<='&')||(LA10_75>='(' && LA10_75<='[')||(LA10_75>='^' && LA10_75<='\uFFFF')) ) {s = 98;}

                        if ( s>=0 ) return s;
                        break;
                    case 96 : 
                        int LA10_68 = input.LA(1);

                         
                        int index10_68 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred6_ANTLRLexer()) ) {s = 41;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index10_68);
                        if ( s>=0 ) return s;
                        break;
                    case 97 : 
                        int LA10_34 = input.LA(1);

                        s = -1;
                        if ( (LA10_34=='\"') ) {s = 52;}

                        else if ( (LA10_34=='\\') ) {s = 53;}

                        else if ( (LA10_34=='\'') ) {s = 54;}

                        else if ( ((LA10_34>='\u0000' && LA10_34<='!')||(LA10_34>='#' && LA10_34<='&')||(LA10_34>='(' && LA10_34<='[')||(LA10_34>=']' && LA10_34<='\uFFFF')) ) {s = 55;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
                    case 98 : 
                        int LA10_47 = input.LA(1);

                        s = -1;
                        if ( (LA10_47=='\"') ) {s = 61;}

                        else if ( (LA10_47=='\\') ) {s = 62;}

                        else if ( (LA10_47=='\'') ) {s = 63;}

                        else if ( ((LA10_47>='\u0000' && LA10_47<='!')||(LA10_47>='#' && LA10_47<='&')||(LA10_47>='(' && LA10_47<='[')||(LA10_47>=']' && LA10_47<='\uFFFF')) ) {s = 64;}

                        else s = 5;

                        if ( s>=0 ) return s;
                        break;
                    case 99 : 
                        int LA10_76 = input.LA(1);

                        s = -1;
                        if ( (LA10_76=='\"') ) {s = 75;}

                        else if ( (LA10_76=='\\') ) {s = 76;}

                        else if ( (LA10_76=='\'') ) {s = 77;}

                        else if ( ((LA10_76>='\u0000' && LA10_76<='!')||(LA10_76>='#' && LA10_76<='&')||(LA10_76>='(' && LA10_76<='[')||(LA10_76>=']' && LA10_76<='\uFFFF')) ) {s = 78;}

                        if ( s>=0 ) return s;
                        break;
                    case 100 : 
                        int LA10_32 = input.LA(1);

                        s = -1;
                        if ( (LA10_32==']') ) {s = 34;}

                        else if ( (LA10_32=='\\') ) {s = 35;}

                        else if ( (LA10_32=='\"') ) {s = 36;}

                        else if ( (LA10_32=='\'') ) {s = 37;}

                        else if ( ((LA10_32>='\u0000' && LA10_32<='!')||(LA10_32>='#' && LA10_32<='&')||(LA10_32>='(' && LA10_32<='[')||(LA10_32>='^' && LA10_32<='\uFFFF')) ) {s = 38;}

                        if ( s>=0 ) return s;
                        break;
                    case 101 : 
                        int LA10_101 = input.LA(1);

                         
                        int index10_101 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA10_101=='\"') ) {s = 61;}

                        else if ( (LA10_101=='\\') ) {s = 62;}

                        else if ( (LA10_101=='\'') ) {s = 63;}

                        else if ( ((LA10_101>='\u0000' && LA10_101<='!')||(LA10_101>='#' && LA10_101<='&')||(LA10_101>='(' && LA10_101<='[')||(LA10_101>=']' && LA10_101<='\uFFFF')) ) {s = 64;}

                        else s = 109;

                         
                        input.seek(index10_101);
                        if ( s>=0 ) return s;
                        break;
                    case 102 : 
                        int LA10_103 = input.LA(1);

                        s = -1;
                        if ( (LA10_103=='\"') ) {s = 99;}

                        else if ( (LA10_103=='\\') ) {s = 100;}

                        else if ( (LA10_103=='\'') ) {s = 102;}

                        else if ( (LA10_103==']') ) {s = 101;}

                        else if ( ((LA10_103>='\u0000' && LA10_103<='!')||(LA10_103>='#' && LA10_103<='&')||(LA10_103>='(' && LA10_103<='[')||(LA10_103>='^' && LA10_103<='\uFFFF')) ) {s = 103;}

                        if ( s>=0 ) return s;
                        break;
                    case 103 : 
                        int LA10_38 = input.LA(1);

                        s = -1;
                        if ( (LA10_38==']') ) {s = 34;}

                        else if ( (LA10_38=='\\') ) {s = 35;}

                        else if ( (LA10_38=='\"') ) {s = 36;}

                        else if ( (LA10_38=='\'') ) {s = 37;}

                        else if ( ((LA10_38>='\u0000' && LA10_38<='!')||(LA10_38>='#' && LA10_38<='&')||(LA10_38>='(' && LA10_38<='[')||(LA10_38>='^' && LA10_38<='\uFFFF')) ) {s = 38;}

                        if ( s>=0 ) return s;
                        break;
                    case 104 : 
                        int LA10_107 = input.LA(1);

                        s = -1;
                        if ( (LA10_107=='\"') ) {s = 99;}

                        else if ( (LA10_107=='\\') ) {s = 100;}

                        else if ( (LA10_107==']') ) {s = 101;}

                        else if ( (LA10_107=='\'') ) {s = 102;}

                        else if ( ((LA10_107>='\u0000' && LA10_107<='!')||(LA10_107>='#' && LA10_107<='&')||(LA10_107>='(' && LA10_107<='[')||(LA10_107>='^' && LA10_107<='\uFFFF')) ) {s = 103;}

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 10, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA18_eotS =
        "\4\uffff\1\3\5\uffff";
    static final String DFA18_eofS =
        "\12\uffff";
    static final String DFA18_minS =
        "\1\0\1\uffff\1\0\1\uffff\3\0\3\uffff";
    static final String DFA18_maxS =
        "\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\2\0\3\uffff";
    static final String DFA18_acceptS =
        "\1\uffff\1\3\1\uffff\1\2\3\uffff\3\1";
    static final String DFA18_specialS =
        "\1\0\1\uffff\1\2\1\uffff\1\3\1\1\1\4\3\uffff}>";
    static final String[] DFA18_transitionS = {
            "\47\3\1\1\64\3\1\2\uffa3\3",
            "",
            "\47\6\1\4\64\6\1\5\uffa3\6",
            "",
            "\47\11\1\7\64\11\1\10\uffa3\11",
            "\1\uffff",
            "\1\uffff",
            "",
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
            return "()* loopback of 505:9: ( ( '\\\\' )=> ACTION_ESC | ~ '\\'' )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA18_0 = input.LA(1);

                        s = -1;
                        if ( (LA18_0=='\'') ) {s = 1;}

                        else if ( (LA18_0=='\\') ) {s = 2;}

                        else if ( ((LA18_0>='\u0000' && LA18_0<='&')||(LA18_0>='(' && LA18_0<='[')||(LA18_0>=']' && LA18_0<='\uFFFF')) ) {s = 3;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA18_5 = input.LA(1);

                         
                        int index18_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_ANTLRLexer()) ) {s = 9;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index18_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA18_2 = input.LA(1);

                        s = -1;
                        if ( (LA18_2=='\'') ) {s = 4;}

                        else if ( (LA18_2=='\\') ) {s = 5;}

                        else if ( ((LA18_2>='\u0000' && LA18_2<='&')||(LA18_2>='(' && LA18_2<='[')||(LA18_2>=']' && LA18_2<='\uFFFF')) ) {s = 6;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA18_4 = input.LA(1);

                         
                        int index18_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA18_4=='\'') && (synpred7_ANTLRLexer())) {s = 7;}

                        else if ( (LA18_4=='\\') && (synpred7_ANTLRLexer())) {s = 8;}

                        else if ( ((LA18_4>='\u0000' && LA18_4<='&')||(LA18_4>='(' && LA18_4<='[')||(LA18_4>=']' && LA18_4<='\uFFFF')) && (synpred7_ANTLRLexer())) {s = 9;}

                        else s = 3;

                         
                        input.seek(index18_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA18_6 = input.LA(1);

                         
                        int index18_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred7_ANTLRLexer()) ) {s = 9;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index18_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 18, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA19_eotS =
        "\4\uffff\1\3\5\uffff";
    static final String DFA19_eofS =
        "\12\uffff";
    static final String DFA19_minS =
        "\1\0\1\uffff\1\0\1\uffff\3\0\3\uffff";
    static final String DFA19_maxS =
        "\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\2\0\3\uffff";
    static final String DFA19_acceptS =
        "\1\uffff\1\3\1\uffff\1\2\3\uffff\3\1";
    static final String DFA19_specialS =
        "\1\1\1\uffff\1\2\1\uffff\1\4\1\0\1\3\3\uffff}>";
    static final String[] DFA19_transitionS = {
            "\42\3\1\1\71\3\1\2\uffa3\3",
            "",
            "\42\6\1\4\71\6\1\5\uffa3\6",
            "",
            "\42\11\1\7\71\11\1\10\uffa3\11",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "()* loopback of 515:8: ( ( '\\\\' )=> ACTION_ESC | ~ '\"' )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA19_5 = input.LA(1);

                         
                        int index19_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_ANTLRLexer()) ) {s = 9;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index19_5);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA19_0 = input.LA(1);

                        s = -1;
                        if ( (LA19_0=='\"') ) {s = 1;}

                        else if ( (LA19_0=='\\') ) {s = 2;}

                        else if ( ((LA19_0>='\u0000' && LA19_0<='!')||(LA19_0>='#' && LA19_0<='[')||(LA19_0>=']' && LA19_0<='\uFFFF')) ) {s = 3;}

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA19_2 = input.LA(1);

                        s = -1;
                        if ( (LA19_2=='\"') ) {s = 4;}

                        else if ( (LA19_2=='\\') ) {s = 5;}

                        else if ( ((LA19_2>='\u0000' && LA19_2<='!')||(LA19_2>='#' && LA19_2<='[')||(LA19_2>=']' && LA19_2<='\uFFFF')) ) {s = 6;}

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA19_6 = input.LA(1);

                         
                        int index19_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred8_ANTLRLexer()) ) {s = 9;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index19_6);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA19_4 = input.LA(1);

                         
                        int index19_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA19_4=='\"') && (synpred8_ANTLRLexer())) {s = 7;}

                        else if ( (LA19_4=='\\') && (synpred8_ANTLRLexer())) {s = 8;}

                        else if ( ((LA19_4>='\u0000' && LA19_4<='!')||(LA19_4>='#' && LA19_4<='[')||(LA19_4>=']' && LA19_4<='\uFFFF')) && (synpred8_ANTLRLexer())) {s = 9;}

                        else s = 3;

                         
                        input.seek(index19_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA25_eotS =
        "\1\12\12\uffff";
    static final String DFA25_eofS =
        "\13\uffff";
    static final String DFA25_minS =
        "\1\42\12\uffff";
    static final String DFA25_maxS =
        "\1\165\12\uffff";
    static final String DFA25_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12";
    static final String DFA25_specialS =
        "\13\uffff}>";
    static final String[] DFA25_transitionS = {
            "\1\6\4\uffff\1\7\64\uffff\1\10\5\uffff\1\1\3\uffff\1\4\7\uffff"+
            "\1\3\3\uffff\1\5\1\uffff\1\2\1\11",
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

    static final short[] DFA25_eot = DFA.unpackEncodedString(DFA25_eotS);
    static final short[] DFA25_eof = DFA.unpackEncodedString(DFA25_eofS);
    static final char[] DFA25_min = DFA.unpackEncodedStringToUnsignedChars(DFA25_minS);
    static final char[] DFA25_max = DFA.unpackEncodedStringToUnsignedChars(DFA25_maxS);
    static final short[] DFA25_accept = DFA.unpackEncodedString(DFA25_acceptS);
    static final short[] DFA25_special = DFA.unpackEncodedString(DFA25_specialS);
    static final short[][] DFA25_transition;

    static {
        int numStates = DFA25_transitionS.length;
        DFA25_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA25_transition[i] = DFA.unpackEncodedString(DFA25_transitionS[i]);
        }
    }

    class DFA25 extends DFA {

        public DFA25(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 25;
            this.eot = DFA25_eot;
            this.eof = DFA25_eof;
            this.min = DFA25_min;
            this.max = DFA25_max;
            this.accept = DFA25_accept;
            this.special = DFA25_special;
            this.transition = DFA25_transition;
        }
        public String getDescription() {
            return "590:9: ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | UNICODE_ESC | )";
        }
    }
    static final String DFA31_eotS =
        "\2\uffff\1\50\1\54\1\50\1\uffff\12\60\1\101\4\uffff\1\107\4\uffff"+
        "\1\115\1\uffff\1\120\1\uffff\1\123\1\50\6\uffff\1\50\10\uffff\1"+
        "\60\1\uffff\17\60\22\uffff\1\156\11\uffff\21\60\2\uffff\2\60\1\u0082"+
        "\20\60\1\uffff\2\60\1\u0095\3\60\1\u0099\6\60\1\u00a0\2\60\1\u00a3"+
        "\1\60\1\uffff\1\u00a5\2\60\1\uffff\1\u00a8\2\60\1\u00ab\2\60\1\uffff"+
        "\1\60\2\uffff\1\60\1\uffff\1\60\1\u00b1\1\uffff\1\60\1\u00b3\1\uffff"+
        "\1\u00b4\1\u00b5\1\uffff\1\u00b6\1\u00b7\1\uffff\1\60\5\uffff\1"+
        "\u00b9\1\uffff";
    static final String DFA31_eofS =
        "\u00ba\uffff";
    static final String DFA31_minS =
        "\1\0\1\uffff\1\0\1\74\1\0\1\uffff\1\160\1\145\1\143\1\155\1\151"+
        "\1\145\1\141\1\162\1\145\1\141\1\72\4\uffff\1\76\4\uffff\1\75\1"+
        "\uffff\1\50\1\uffff\1\56\1\76\6\uffff\1\0\10\uffff\1\164\1\uffff"+
        "\1\153\1\145\1\162\1\155\1\157\1\160\1\141\1\156\1\170\1\162\1\151"+
        "\1\142\1\141\2\164\22\uffff\1\56\11\uffff\1\151\2\145\1\157\2\160"+
        "\1\157\1\147\1\141\1\145\1\163\1\164\1\166\1\154\1\155\1\165\1\143"+
        "\2\uffff\1\157\1\156\1\60\1\167\1\154\1\145\1\162\1\155\1\154\1"+
        "\162\2\145\1\141\1\151\1\155\1\162\1\150\1\156\1\163\1\uffff\1\163"+
        "\1\141\1\60\1\164\1\145\1\154\1\60\1\162\1\143\1\164\1\143\1\141"+
        "\1\156\1\60\1\163\1\11\1\60\1\164\1\uffff\1\60\1\156\1\171\1\uffff"+
        "\1\60\1\164\1\145\1\60\1\162\1\163\1\uffff\1\11\2\uffff\1\145\1"+
        "\uffff\1\164\1\60\1\uffff\1\145\1\60\1\uffff\2\60\1\uffff\2\60\1"+
        "\uffff\1\144\5\uffff\1\60\1\uffff";
    static final String DFA31_maxS =
        "\1\uffff\1\uffff\1\uffff\1\74\1\uffff\1\uffff\1\160\1\162\1\143"+
        "\1\155\1\162\1\145\1\165\1\162\1\145\1\141\1\72\4\uffff\1\76\4\uffff"+
        "\1\75\1\uffff\1\50\1\uffff\1\56\1\76\6\uffff\1\uffff\10\uffff\1"+
        "\164\1\uffff\1\153\1\145\1\162\1\155\1\157\1\160\1\141\1\156\1\170"+
        "\1\162\1\157\1\142\1\141\2\164\22\uffff\1\56\11\uffff\1\151\2\145"+
        "\1\157\2\160\1\157\1\147\1\141\1\145\1\163\1\164\1\166\1\154\1\155"+
        "\1\165\1\143\2\uffff\1\157\1\156\1\172\1\167\1\154\1\145\1\162\1"+
        "\155\1\154\1\162\2\145\1\141\1\151\1\155\1\162\1\150\1\156\1\163"+
        "\1\uffff\1\163\1\141\1\172\1\164\1\145\1\154\1\172\1\162\1\143\1"+
        "\164\1\143\1\141\1\156\1\172\1\163\1\173\1\172\1\164\1\uffff\1\172"+
        "\1\156\1\171\1\uffff\1\172\1\164\1\145\1\172\1\162\1\163\1\uffff"+
        "\1\173\2\uffff\1\145\1\uffff\1\164\1\172\1\uffff\1\145\1\172\1\uffff"+
        "\2\172\1\uffff\2\172\1\uffff\1\144\5\uffff\1\172\1\uffff";
    static final String DFA31_acceptS =
        "\1\uffff\1\1\3\uffff\1\5\13\uffff\1\31\1\32\1\33\1\34\1\uffff\1"+
        "\37\1\41\1\42\1\43\1\uffff\1\46\1\uffff\1\50\2\uffff\1\56\1\57\1"+
        "\60\1\61\1\62\1\63\1\uffff\1\65\1\66\1\1\1\2\1\3\1\36\1\4\1\5\1"+
        "\uffff\1\62\17\uffff\1\30\1\27\1\31\1\32\1\33\1\34\1\35\1\40\1\37"+
        "\1\41\1\42\1\43\1\45\1\44\1\46\1\55\1\47\1\50\1\uffff\1\51\1\54"+
        "\1\56\1\57\1\60\1\61\1\63\1\64\1\65\21\uffff\1\53\1\52\23\uffff"+
        "\1\15\22\uffff\1\10\3\uffff\1\13\6\uffff\1\24\1\uffff\1\7\1\23\1"+
        "\uffff\1\11\2\uffff\1\14\2\uffff\1\20\2\uffff\1\6\2\uffff\1\25\1"+
        "\uffff\1\21\1\16\1\22\1\26\1\12\1\uffff\1\17";
    static final String DFA31_specialS =
        "\1\0\1\uffff\1\3\1\uffff\1\1\41\uffff\1\2\u0093\uffff}>";
    static final String[] DFA31_transitionS = {
            "\11\50\2\47\1\50\2\47\22\50\1\47\1\30\1\2\1\50\1\35\2\50\1\46"+
            "\1\23\1\24\1\31\1\32\1\21\1\37\1\36\1\1\12\45\1\20\1\22\1\3"+
            "\1\25\1\26\1\27\1\40\32\43\1\4\2\50\1\34\2\50\2\44\1\17\2\44"+
            "\1\12\1\15\1\44\1\11\2\44\1\13\2\44\1\6\1\14\1\44\1\16\1\10"+
            "\1\7\6\44\1\5\1\33\1\42\1\41\uff81\50",
            "",
            "\0\52",
            "\1\53",
            "\0\55",
            "",
            "\1\57",
            "\1\64\2\uffff\1\63\6\uffff\1\61\2\uffff\1\62",
            "\1\65",
            "\1\66",
            "\1\70\10\uffff\1\67",
            "\1\71",
            "\1\72\20\uffff\1\73\2\uffff\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "",
            "",
            "",
            "",
            "\1\106",
            "",
            "",
            "",
            "",
            "\1\114",
            "",
            "\1\117",
            "",
            "\1\122",
            "\1\124",
            "",
            "",
            "",
            "",
            "",
            "",
            "\0\132",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\134",
            "",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\141",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\150\5\uffff\1\147",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\154",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\155",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "\1\176",
            "\1\177",
            "",
            "",
            "\1\u0080",
            "\1\u0081",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0083",
            "\1\u0084",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "",
            "\1\u0093",
            "\1\u0094",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c",
            "\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00a1",
            "\2\u00a2\1\uffff\2\u00a2\22\uffff\1\u00a2\132\uffff\1\u00a2",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00a4",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00a6",
            "\1\u00a7",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00a9",
            "\1\u00aa",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\1\u00ac",
            "\1\u00ad",
            "",
            "\2\u00ae\1\uffff\2\u00ae\22\uffff\1\u00ae\132\uffff\1\u00ae",
            "",
            "",
            "\1\u00af",
            "",
            "\1\u00b0",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "\1\u00b2",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            "",
            "\1\u00b8",
            "",
            "",
            "",
            "",
            "",
            "\12\60\7\uffff\32\60\4\uffff\1\60\1\uffff\32\60",
            ""
    };

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( COMMENT | DOUBLE_QUOTE_STRING_LITERAL | DOUBLE_ANGLE_STRING_LITERAL | ARG_ACTION | ACTION | OPTIONS | TOKENS | SCOPE | IMPORT | FRAGMENT | LEXER | PARSER | TREE | GRAMMAR | PROTECTED | PUBLIC | PRIVATE | RETURNS | THROWS | CATCH | FINALLY | TEMPLATE | COLON | COLONCOLON | COMMA | SEMI | LPAREN | RPAREN | IMPLIES | LT | GT | ASSIGN | QUESTION | BANG | STAR | PLUS | PLUS_ASSIGN | OR | ROOT | DOLLAR | WILDCARD | RANGE | ETC | RARROW | TREE_BEGIN | AT | NOT | RBRACE | TOKEN_REF | RULE_REF | INT | CHAR_LITERAL | WS | ERRCHAR );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_0 = input.LA(1);

                        s = -1;
                        if ( (LA31_0=='/') ) {s = 1;}

                        else if ( (LA31_0=='\"') ) {s = 2;}

                        else if ( (LA31_0=='<') ) {s = 3;}

                        else if ( (LA31_0=='[') ) {s = 4;}

                        else if ( (LA31_0=='{') ) {s = 5;}

                        else if ( (LA31_0=='o') ) {s = 6;}

                        else if ( (LA31_0=='t') ) {s = 7;}

                        else if ( (LA31_0=='s') ) {s = 8;}

                        else if ( (LA31_0=='i') ) {s = 9;}

                        else if ( (LA31_0=='f') ) {s = 10;}

                        else if ( (LA31_0=='l') ) {s = 11;}

                        else if ( (LA31_0=='p') ) {s = 12;}

                        else if ( (LA31_0=='g') ) {s = 13;}

                        else if ( (LA31_0=='r') ) {s = 14;}

                        else if ( (LA31_0=='c') ) {s = 15;}

                        else if ( (LA31_0==':') ) {s = 16;}

                        else if ( (LA31_0==',') ) {s = 17;}

                        else if ( (LA31_0==';') ) {s = 18;}

                        else if ( (LA31_0=='(') ) {s = 19;}

                        else if ( (LA31_0==')') ) {s = 20;}

                        else if ( (LA31_0=='=') ) {s = 21;}

                        else if ( (LA31_0=='>') ) {s = 22;}

                        else if ( (LA31_0=='?') ) {s = 23;}

                        else if ( (LA31_0=='!') ) {s = 24;}

                        else if ( (LA31_0=='*') ) {s = 25;}

                        else if ( (LA31_0=='+') ) {s = 26;}

                        else if ( (LA31_0=='|') ) {s = 27;}

                        else if ( (LA31_0=='^') ) {s = 28;}

                        else if ( (LA31_0=='$') ) {s = 29;}

                        else if ( (LA31_0=='.') ) {s = 30;}

                        else if ( (LA31_0=='-') ) {s = 31;}

                        else if ( (LA31_0=='@') ) {s = 32;}

                        else if ( (LA31_0=='~') ) {s = 33;}

                        else if ( (LA31_0=='}') ) {s = 34;}

                        else if ( ((LA31_0>='A' && LA31_0<='Z')) ) {s = 35;}

                        else if ( ((LA31_0>='a' && LA31_0<='b')||(LA31_0>='d' && LA31_0<='e')||LA31_0=='h'||(LA31_0>='j' && LA31_0<='k')||(LA31_0>='m' && LA31_0<='n')||LA31_0=='q'||(LA31_0>='u' && LA31_0<='z')) ) {s = 36;}

                        else if ( ((LA31_0>='0' && LA31_0<='9')) ) {s = 37;}

                        else if ( (LA31_0=='\'') ) {s = 38;}

                        else if ( ((LA31_0>='\t' && LA31_0<='\n')||(LA31_0>='\f' && LA31_0<='\r')||LA31_0==' ') ) {s = 39;}

                        else if ( ((LA31_0>='\u0000' && LA31_0<='\b')||LA31_0=='\u000B'||(LA31_0>='\u000E' && LA31_0<='\u001F')||LA31_0=='#'||(LA31_0>='%' && LA31_0<='&')||(LA31_0>='\\' && LA31_0<=']')||(LA31_0>='_' && LA31_0<='`')||(LA31_0>='\u007F' && LA31_0<='\uFFFF')) ) {s = 40;}

                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA31_4 = input.LA(1);

                        s = -1;
                        if ( ((LA31_4>='\u0000' && LA31_4<='\uFFFF')) ) {s = 45;}

                        else s = 40;

                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA31_38 = input.LA(1);

                        s = -1;
                        if ( ((LA31_38>='\u0000' && LA31_38<='\uFFFF')) ) {s = 90;}

                        else s = 40;

                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA31_2 = input.LA(1);

                        s = -1;
                        if ( ((LA31_2>='\u0000' && LA31_2<='\uFFFF')) ) {s = 42;}

                        else s = 40;

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 31, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

}