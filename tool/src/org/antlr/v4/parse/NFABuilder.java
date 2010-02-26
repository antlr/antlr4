// $ANTLR ${project.version} ${buildNumber} NFABuilder.g 2010-02-25 18:51:26

/*
 [The "BSD license"]
 Copyright (c) 2010 Terence Parr
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
import org.antlr.runtime.tree.TreeFilter;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeRuleReturnScope;
import org.antlr.v4.automata.NFAFactory;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.TerminalAST;

import java.util.ArrayList;
import java.util.List;
public class NFABuilder extends TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "RESULT", "ALT_REWRITE"
    };
    public static final int COMBINED=90;
    public static final int LT=43;
    public static final int STAR=48;
    public static final int BACKTRACK_SEMPRED=95;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int FORCED_ACTION=5;
    public static final int ARGLIST=88;
    public static final int ALTLIST=85;
    public static final int NOT=60;
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int ACTION=16;
    public static final int TOKEN_REF=62;
    public static final int RULEMODIFIERS=74;
    public static final int ST_RESULT=99;
    public static final int RPAREN=41;
    public static final int RET=89;
    public static final int IMPORT=22;
    public static final int STRING_LITERAL=67;
    public static final int ARG=87;
    public static final int ARG_ACTION=14;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=10;
    public static final int COMMENT=9;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int GRAMMAR=27;
    public static final int RULEACTIONS=75;
    public static final int WSCHARS=65;
    public static final int INITACTION=91;
    public static final int ALT_REWRITE=101;
    public static final int IMPLIES=42;
    public static final int RULE=72;
    public static final int RBRACE=61;
    public static final int ACTION_ESC=17;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int CHAR_RANGE=82;
    public static final int INT=64;
    public static final int EPSILON=83;
    public static final int LIST=97;
    public static final int COLONCOLON=37;
    public static final int WSNLCHARS=18;
    public static final int WS=70;
    public static final int LEXER=24;
    public static final int OR=51;
    public static final int GT=44;
    public static final int CATCH=33;
    public static final int CLOSURE=79;
    public static final int PARSER=25;
    public static final int DOLLAR=53;
    public static final int PROTECTED=28;
    public static final int ELEMENT_OPTIONS=98;
    public static final int NESTED_ACTION=15;
    public static final int FRAGMENT=23;
    public static final int ID=86;
    public static final int TREE_BEGIN=58;
    public static final int LPAREN=40;
    public static final int AT=59;
    public static final int ESC_SEQ=66;
    public static final int ALT=84;
    public static final int TREE=26;
    public static final int SCOPE=21;
    public static final int ETC=56;
    public static final int COMMA=38;
    public static final int WILDCARD=96;
    public static final int DOC_COMMENT=6;
    public static final int PLUS=49;
    public static final int REWRITE_BLOCK=77;
    public static final int DOT=54;
    public static final int RETURNS=31;
    public static final int RULES=73;
    public static final int RARROW=57;
    public static final int UNICODE_ESC=69;
    public static final int HEX_DIGIT=68;
    public static final int RANGE=55;
    public static final int TOKENS=20;
    public static final int RESULT=100;
    public static final int GATED_SEMPRED=93;
    public static final int BANG=47;
    public static final int ACTION_STRING_LITERAL=12;
    public static final int ROOT=52;
    public static final int SEMI=39;
    public static final int RULE_REF=63;
    public static final int NLCHARS=8;
    public static final int OPTIONAL=78;
    public static final int SYNPRED=81;
    public static final int COLON=36;
    public static final int QUESTION=46;
    public static final int FINALLY=34;
    public static final int TEMPLATE=35;
    public static final int LABEL=92;
    public static final int SYN_SEMPRED=94;
    public static final int ERRCHAR=71;
    public static final int BLOCK=76;
    public static final int ASSIGN=45;
    public static final int PLUS_ASSIGN=50;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=80;
    public static final int OPTIONS=19;

    // delegates
    // delegators


        public NFABuilder(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public NFABuilder(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return NFABuilder.tokenNames; }
    public String getGrammarFileName() { return "NFABuilder.g"; }


        NFAFactory factory;
        public NFABuilder(TreeNodeStream input, NFAFactory factory) {
        	this(input);
        	this.factory = factory;
        }



    // $ANTLR start "topdown"
    // NFABuilder.g:78:1: topdown : rule ;
    public final void topdown() throws RecognitionException {
        try {
            // NFABuilder.g:79:2: ( rule )
            // NFABuilder.g:79:4: rule
            {
            pushFollow(FOLLOW_rule_in_topdown79);
            rule();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "topdown"


    // $ANTLR start "bottomup"
    // NFABuilder.g:82:1: bottomup : block ;
    public final void bottomup() throws RecognitionException {
        try {
            // NFABuilder.g:83:2: ( block )
            // NFABuilder.g:83:4: block
            {
            pushFollow(FOLLOW_block_in_bottomup90);
            block();

            state._fsp--;
            if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "bottomup"


    // $ANTLR start "rule"
    // NFABuilder.g:86:1: rule returns [NFAFactory.Handle p] : ^( RULE name= ID ( . )+ ) ;
    public final NFAFactory.Handle rule() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST name=null;

        try {
            // NFABuilder.g:87:2: ( ^( RULE name= ID ( . )+ ) )
            // NFABuilder.g:87:6: ^( RULE name= ID ( . )+ )
            {
            match(input,RULE,FOLLOW_RULE_in_rule109); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            name=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule113); if (state.failed) return p;
            // NFABuilder.g:87:21: ( . )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=SEMPRED && LA1_0<=ALT_REWRITE)) ) {
                    alt1=1;
                }
                else if ( (LA1_0==UP) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // NFABuilder.g:87:21: .
            	    {
            	    matchAny(input); if (state.failed) return p;

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
            	    if (state.backtracking>0) {state.failed=true; return p;}
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==1 ) {
              factory.setCurrentRuleName((name!=null?name.getText():null));
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "rule"


    // $ANTLR start "block"
    // NFABuilder.g:90:1: block returns [NFAFactory.Handle p] : ^( BLOCK (~ ALT )* (a= alternative )+ ) ;
    public final NFAFactory.Handle block() throws RecognitionException {
        NFAFactory.Handle p = null;

        NFAFactory.Handle a = null;


        List<NFAFactory.Handle> alts = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:92:5: ( ^( BLOCK (~ ALT )* (a= alternative )+ ) )
            // NFABuilder.g:92:7: ^( BLOCK (~ ALT )* (a= alternative )+ )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block143); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            // NFABuilder.g:92:15: (~ ALT )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==ALT_REWRITE) ) {
                    int LA2_1 = input.LA(2);

                    if ( ((LA2_1>=SEMPRED && LA2_1<=ALT_REWRITE)) ) {
                        alt2=1;
                    }


                }
                else if ( ((LA2_0>=SEMPRED && LA2_0<=EPSILON)||(LA2_0>=ALTLIST && LA2_0<=RESULT)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // NFABuilder.g:92:15: ~ ALT
            	    {
            	    if ( (input.LA(1)>=SEMPRED && input.LA(1)<=EPSILON)||(input.LA(1)>=ALTLIST && input.LA(1)<=ALT_REWRITE) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return p;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // NFABuilder.g:92:21: (a= alternative )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==ALT||LA3_0==ALT_REWRITE) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // NFABuilder.g:92:22: a= alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_block152);
            	    a=alternative();

            	    state._fsp--;
            	    if (state.failed) return p;
            	    if ( state.backtracking==1 ) {
            	      alts.add(a);
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
            	    if (state.backtracking>0) {state.failed=true; return p;}
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==1 ) {
              factory.block(alts);
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "block"


    // $ANTLR start "alternative"
    // NFABuilder.g:96:1: alternative returns [NFAFactory.Handle p] : ( ^( ALT_REWRITE alternative . ) | ^( ALT EPSILON ) | ^( ALT ( element )+ ) );
    public final NFAFactory.Handle alternative() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:97:5: ( ^( ALT_REWRITE alternative . ) | ^( ALT EPSILON ) | ^( ALT ( element )+ ) )
            int alt5=3;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==ALT_REWRITE) ) {
                alt5=1;
            }
            else if ( (LA5_0==ALT) ) {
                int LA5_2 = input.LA(2);

                if ( (LA5_2==DOWN) ) {
                    int LA5_3 = input.LA(3);

                    if ( (LA5_3==EPSILON) ) {
                        alt5=2;
                    }
                    else if ( (LA5_3==SEMPRED||LA5_3==ACTION||LA5_3==IMPLIES||LA5_3==ASSIGN||LA5_3==BANG||LA5_3==PLUS_ASSIGN||LA5_3==ROOT||(LA5_3>=DOT && LA5_3<=RANGE)||LA5_3==TREE_BEGIN||(LA5_3>=TOKEN_REF && LA5_3<=RULE_REF)||LA5_3==STRING_LITERAL||LA5_3==BLOCK||(LA5_3>=OPTIONAL && LA5_3<=POSITIVE_CLOSURE)||LA5_3==GATED_SEMPRED||LA5_3==WILDCARD) ) {
                        alt5=3;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return p;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return p;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // NFABuilder.g:97:7: ^( ALT_REWRITE alternative . )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative186); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_alternative_in_alternative188);
                    alternative();

                    state._fsp--;
                    if (state.failed) return p;
                    matchAny(input); if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:98:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative200); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative202); if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:99:9: ^( ALT ( element )+ )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative214); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    // NFABuilder.g:99:15: ( element )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==SEMPRED||LA4_0==ACTION||LA4_0==IMPLIES||LA4_0==ASSIGN||LA4_0==BANG||LA4_0==PLUS_ASSIGN||LA4_0==ROOT||(LA4_0>=DOT && LA4_0<=RANGE)||LA4_0==TREE_BEGIN||(LA4_0>=TOKEN_REF && LA4_0<=RULE_REF)||LA4_0==STRING_LITERAL||LA4_0==BLOCK||(LA4_0>=OPTIONAL && LA4_0<=POSITIVE_CLOSURE)||LA4_0==GATED_SEMPRED||LA4_0==WILDCARD) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // NFABuilder.g:99:15: element
                    	    {
                    	    pushFollow(FOLLOW_element_in_alternative216);
                    	    element();

                    	    state._fsp--;
                    	    if (state.failed) return p;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                    	    if (state.backtracking>0) {state.failed=true; return p;}
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "alternative"


    // $ANTLR start "element"
    // NFABuilder.g:102:1: element returns [NFAFactory.Handle p] : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final NFAFactory.Handle element() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:103:2: ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt6=7;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // NFABuilder.g:103:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element236);
                    labeledElement();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:104:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element241);
                    atom();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:105:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element246);
                    ebnf();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:106:6: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_element253); if (state.failed) return p;

                    }
                    break;
                case 5 :
                    // NFABuilder.g:107:6: SEMPRED
                    {
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_element260); if (state.failed) return p;

                    }
                    break;
                case 6 :
                    // NFABuilder.g:108:4: GATED_SEMPRED
                    {
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element265); if (state.failed) return p;

                    }
                    break;
                case 7 :
                    // NFABuilder.g:109:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element270);
                    treeSpec();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "element"


    // $ANTLR start "labeledElement"
    // NFABuilder.g:112:1: labeledElement returns [NFAFactory.Handle p] : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );
    public final NFAFactory.Handle labeledElement() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:113:2: ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) )
            int alt7=4;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // NFABuilder.g:113:4: ^( ASSIGN ID atom )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement287); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    match(input,ID,FOLLOW_ID_in_labeledElement289); if (state.failed) return p;
                    pushFollow(FOLLOW_atom_in_labeledElement291);
                    atom();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:114:4: ^( ASSIGN ID block )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement298); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    match(input,ID,FOLLOW_ID_in_labeledElement300); if (state.failed) return p;
                    pushFollow(FOLLOW_block_in_labeledElement302);
                    block();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:115:4: ^( PLUS_ASSIGN ID atom )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement309); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    match(input,ID,FOLLOW_ID_in_labeledElement311); if (state.failed) return p;
                    pushFollow(FOLLOW_atom_in_labeledElement313);
                    atom();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:116:4: ^( PLUS_ASSIGN ID block )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement320); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    match(input,ID,FOLLOW_ID_in_labeledElement322); if (state.failed) return p;
                    pushFollow(FOLLOW_block_in_labeledElement324);
                    block();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "labeledElement"


    // $ANTLR start "treeSpec"
    // NFABuilder.g:119:1: treeSpec returns [NFAFactory.Handle p] : ^( TREE_BEGIN ( element )+ ) ;
    public final NFAFactory.Handle treeSpec() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:120:5: ( ^( TREE_BEGIN ( element )+ ) )
            // NFABuilder.g:120:7: ^( TREE_BEGIN ( element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec344); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            // NFABuilder.g:120:20: ( element )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==SEMPRED||LA8_0==ACTION||LA8_0==IMPLIES||LA8_0==ASSIGN||LA8_0==BANG||LA8_0==PLUS_ASSIGN||LA8_0==ROOT||(LA8_0>=DOT && LA8_0<=RANGE)||LA8_0==TREE_BEGIN||(LA8_0>=TOKEN_REF && LA8_0<=RULE_REF)||LA8_0==STRING_LITERAL||LA8_0==BLOCK||(LA8_0>=OPTIONAL && LA8_0<=POSITIVE_CLOSURE)||LA8_0==GATED_SEMPRED||LA8_0==WILDCARD) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // NFABuilder.g:120:20: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec346);
            	    element();

            	    state._fsp--;
            	    if (state.failed) return p;

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (state.backtracking>0) {state.failed=true; return p;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return p;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "treeSpec"


    // $ANTLR start "ebnf"
    // NFABuilder.g:123:1: ebnf returns [NFAFactory.Handle p] : ( ^( blockSuffix block ) | block );
    public final NFAFactory.Handle ebnf() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:124:2: ( ^( blockSuffix block ) | block )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==IMPLIES||LA9_0==BANG||LA9_0==ROOT||(LA9_0>=OPTIONAL && LA9_0<=POSITIVE_CLOSURE)) ) {
                alt9=1;
            }
            else if ( (LA9_0==BLOCK) ) {
                alt9=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // NFABuilder.g:124:4: ^( blockSuffix block )
                    {
                    pushFollow(FOLLOW_blockSuffix_in_ebnf367);
                    blockSuffix();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_block_in_ebnf369);
                    block();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:125:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf376);
                    block();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "ebnf"


    // $ANTLR start "blockSuffix"
    // NFABuilder.g:128:1: blockSuffix returns [NFAFactory.Handle p] : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final NFAFactory.Handle blockSuffix() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:129:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
            int alt10=4;
            switch ( input.LA(1) ) {
            case OPTIONAL:
            case CLOSURE:
            case POSITIVE_CLOSURE:
                {
                alt10=1;
                }
                break;
            case ROOT:
                {
                alt10=2;
                }
                break;
            case IMPLIES:
                {
                alt10=3;
                }
                break;
            case BANG:
                {
                alt10=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // NFABuilder.g:129:7: ebnfSuffix
                    {
                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffix397);
                    ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:130:7: ROOT
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_blockSuffix405); if (state.failed) return p;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:131:7: IMPLIES
                    {
                    match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffix413); if (state.failed) return p;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:132:7: BANG
                    {
                    match(input,BANG,FOLLOW_BANG_in_blockSuffix421); if (state.failed) return p;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "blockSuffix"


    // $ANTLR start "ebnfSuffix"
    // NFABuilder.g:135:1: ebnfSuffix returns [NFAFactory.Handle p] : ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE );
    public final NFAFactory.Handle ebnfSuffix() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:136:2: ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE )
            // NFABuilder.g:
            {
            if ( (input.LA(1)>=OPTIONAL && input.LA(1)<=POSITIVE_CLOSURE) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return p;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "ebnfSuffix"


    // $ANTLR start "atom"
    // NFABuilder.g:141:1: atom returns [NFAFactory.Handle p] : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );
    public final NFAFactory.Handle atom() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:142:2: ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref )
            int alt11=9;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // NFABuilder.g:142:4: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom472); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_range_in_atom474);
                    range();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:143:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom481); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_range_in_atom483);
                    range();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:144:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom490); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_notSet_in_atom492);
                    notSet();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:145:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom499); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_notSet_in_atom501);
                    notSet();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 5 :
                    // NFABuilder.g:146:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom507);
                    range();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;
                case 6 :
                    // NFABuilder.g:147:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom513); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    match(input,ID,FOLLOW_ID_in_atom515); if (state.failed) return p;
                    pushFollow(FOLLOW_terminal_in_atom517);
                    terminal();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 7 :
                    // NFABuilder.g:148:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom524); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    match(input,ID,FOLLOW_ID_in_atom526); if (state.failed) return p;
                    pushFollow(FOLLOW_ruleref_in_atom528);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 8 :
                    // NFABuilder.g:149:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom539);
                    terminal();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;
                case 9 :
                    // NFABuilder.g:150:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom549);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return p;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "atom"


    // $ANTLR start "notSet"
    // NFABuilder.g:153:1: notSet returns [NFAFactory.Handle p] : ( ^( NOT notTerminal ) | ^( NOT block ) );
    public final NFAFactory.Handle notSet() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:154:5: ( ^( NOT notTerminal ) | ^( NOT block ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==NOT) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==DOWN) ) {
                    int LA12_2 = input.LA(3);

                    if ( (LA12_2==BLOCK) ) {
                        alt12=2;
                    }
                    else if ( (LA12_2==TOKEN_REF||LA12_2==STRING_LITERAL) ) {
                        alt12=1;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return p;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return p;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // NFABuilder.g:154:7: ^( NOT notTerminal )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet571); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_notTerminal_in_notSet573);
                    notTerminal();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:155:7: ^( NOT block )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet583); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    pushFollow(FOLLOW_block_in_notSet585);
                    block();

                    state._fsp--;
                    if (state.failed) return p;

                    match(input, Token.UP, null); if (state.failed) return p;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "notSet"


    // $ANTLR start "notTerminal"
    // NFABuilder.g:158:1: notTerminal returns [NFAFactory.Handle p] : ( TOKEN_REF | STRING_LITERAL );
    public final NFAFactory.Handle notTerminal() throws RecognitionException {
        NFAFactory.Handle p = null;

        try {
            // NFABuilder.g:159:5: ( TOKEN_REF | STRING_LITERAL )
            // NFABuilder.g:
            {
            if ( input.LA(1)==TOKEN_REF||input.LA(1)==STRING_LITERAL ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return p;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "notTerminal"


    // $ANTLR start "ruleref"
    // NFABuilder.g:163:1: ruleref returns [NFAFactory.Handle p] : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final NFAFactory.Handle ruleref() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST RULE_REF1=null;
        GrammarAST RULE_REF2=null;
        GrammarAST RULE_REF3=null;

        try {
            // NFABuilder.g:164:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
            int alt16=3;
            switch ( input.LA(1) ) {
            case ROOT:
                {
                alt16=1;
                }
                break;
            case BANG:
                {
                alt16=2;
                }
                break;
            case RULE_REF:
                {
                alt16=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return p;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // NFABuilder.g:164:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref637); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    RULE_REF1=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref640); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // NFABuilder.g:164:25: ( ARG_ACTION )?
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ARG_ACTION) ) {
                            alt13=1;
                        }
                        switch (alt13) {
                            case 1 :
                                // NFABuilder.g:164:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref642); if (state.failed) return p;

                                }
                                break;

                        }


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    match(input, Token.UP, null); if (state.failed) return p;
                    if ( state.backtracking==1 ) {
                      factory.ruleRef(RULE_REF1);
                    }

                    }
                    break;
                case 2 :
                    // NFABuilder.g:165:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref656); if (state.failed) return p;

                    match(input, Token.DOWN, null); if (state.failed) return p;
                    RULE_REF2=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref659); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // NFABuilder.g:165:25: ( ARG_ACTION )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG_ACTION) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // NFABuilder.g:165:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref661); if (state.failed) return p;

                                }
                                break;

                        }


                        match(input, Token.UP, null); if (state.failed) return p;
                    }

                    match(input, Token.UP, null); if (state.failed) return p;
                    if ( state.backtracking==1 ) {
                      factory.ruleRef(RULE_REF2);
                    }

                    }
                    break;
                case 3 :
                    // NFABuilder.g:166:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    RULE_REF3=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref675); if (state.failed) return p;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return p;
                        // NFABuilder.g:166:18: ( ARG_ACTION )?
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==ARG_ACTION) ) {
                            alt15=1;
                        }
                        switch (alt15) {
                            case 1 :
                                // NFABuilder.g:166:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref677); if (state.failed) return p;

                                }
                                break;

                        }


                        match(input, Token.UP, null); if (state.failed) return p;
                    }
                    if ( state.backtracking==1 ) {
                      factory.ruleRef(RULE_REF3);
                    }

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "ruleref"


    // $ANTLR start "range"
    // NFABuilder.g:169:1: range returns [NFAFactory.Handle p] : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
    public final NFAFactory.Handle range() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST a=null;
        GrammarAST b=null;

        try {
            // NFABuilder.g:170:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
            // NFABuilder.g:170:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range705); if (state.failed) return p;

            match(input, Token.DOWN, null); if (state.failed) return p;
            a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range709); if (state.failed) return p;
            b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range713); if (state.failed) return p;

            match(input, Token.UP, null); if (state.failed) return p;
            if ( state.backtracking==1 ) {
              factory.range(a,b);
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return p;
    }
    // $ANTLR end "range"

    public static class terminal_return extends TreeRuleReturnScope {
        public NFAFactory.Handle p;
    };

    // $ANTLR start "terminal"
    // NFABuilder.g:173:1: terminal returns [NFAFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( WILDCARD . ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) );
    public final NFABuilder.terminal_return terminal() throws RecognitionException {
        NFABuilder.terminal_return retval = new NFABuilder.terminal_return();
        retval.start = input.LT(1);

        try {
            // NFABuilder.g:174:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( WILDCARD . ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) )
            int alt17=9;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // NFABuilder.g:174:8: ^( STRING_LITERAL . )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal739); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      factory.stringLiteral(((GrammarAST)retval.start));
                    }

                    }
                    break;
                case 2 :
                    // NFABuilder.g:175:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal754); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      factory.stringLiteral(((GrammarAST)retval.start));
                    }

                    }
                    break;
                case 3 :
                    // NFABuilder.g:176:7: ^( TOKEN_REF ARG_ACTION . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal768); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal770); if (state.failed) return retval;
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      factory.tokenRef((TerminalAST)((GrammarAST)retval.start));
                    }

                    }
                    break;
                case 4 :
                    // NFABuilder.g:177:7: ^( TOKEN_REF . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal784); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      factory.tokenRef((TerminalAST)((GrammarAST)retval.start));
                    }

                    }
                    break;
                case 5 :
                    // NFABuilder.g:178:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal800); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      factory.tokenRef((TerminalAST)((GrammarAST)retval.start));
                    }

                    }
                    break;
                case 6 :
                    // NFABuilder.g:179:7: ^( WILDCARD . )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal815); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      factory.wildcard(((GrammarAST)retval.start));
                    }

                    }
                    break;
                case 7 :
                    // NFABuilder.g:180:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal831); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      factory.wildcard(((GrammarAST)retval.start));
                    }

                    }
                    break;
                case 8 :
                    // NFABuilder.g:181:7: ^( ROOT terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal846); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    pushFollow(FOLLOW_terminal_in_terminal848);
                    terminal();

                    state._fsp--;
                    if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 9 :
                    // NFABuilder.g:182:7: ^( BANG terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal858); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    pushFollow(FOLLOW_terminal_in_terminal860);
                    terminal();

                    state._fsp--;
                    if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "terminal"

    // Delegated rules


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA7 dfa7 = new DFA7(this);
    protected DFA11 dfa11 = new DFA11(this);
    protected DFA17 dfa17 = new DFA17(this);
    static final String DFA6_eotS =
        "\14\uffff";
    static final String DFA6_eofS =
        "\14\uffff";
    static final String DFA6_minS =
        "\1\4\1\uffff\2\2\6\uffff\2\57";
    static final String DFA6_maxS =
        "\1\140\1\uffff\2\2\6\uffff\2\140";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA6_specialS =
        "\14\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\7\13\uffff\1\6\31\uffff\1\5\2\uffff\1\1\1\uffff\1\3\2\uffff"+
            "\1\1\1\uffff\1\2\1\uffff\2\4\2\uffff\1\11\3\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5\1\uffff\3\5\14\uffff\1\10\2\uffff\1\4",
            "",
            "\1\12",
            "\1\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\4\uffff\1\4\2\uffff\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5\23\uffff\1\4",
            "\1\4\4\uffff\1\4\2\uffff\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5\23\uffff\1\4"
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "102:1: element returns [NFAFactory.Handle p] : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
    }
    static final String DFA7_eotS =
        "\13\uffff";
    static final String DFA7_eofS =
        "\13\uffff";
    static final String DFA7_minS =
        "\1\55\2\2\2\126\2\57\4\uffff";
    static final String DFA7_maxS =
        "\1\62\2\2\2\126\2\140\4\uffff";
    static final String DFA7_acceptS =
        "\7\uffff\1\2\1\1\1\3\1\4";
    static final String DFA7_specialS =
        "\13\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\4\uffff\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\10\4\uffff\1\10\1\uffff\2\10\6\uffff\2\10\3\uffff\1\10\10"+
            "\uffff\1\7\23\uffff\1\10",
            "\1\11\4\uffff\1\11\1\uffff\2\11\6\uffff\2\11\3\uffff\1\11\10"+
            "\uffff\1\12\23\uffff\1\11",
            "",
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
            return "112:1: labeledElement returns [NFAFactory.Handle p] : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );";
        }
    }
    static final String DFA11_eotS =
        "\25\uffff";
    static final String DFA11_eofS =
        "\25\uffff";
    static final String DFA11_minS =
        "\1\57\2\2\1\uffff\1\2\2\uffff\2\57\1\126\4\uffff\1\57\2\2\2\uffff"+
        "\2\57";
    static final String DFA11_maxS =
        "\1\140\2\2\1\uffff\1\2\2\uffff\2\140\1\126\4\uffff\1\140\2\2\2\uffff"+
        "\2\140";
    static final String DFA11_acceptS =
        "\3\uffff\1\5\1\uffff\1\10\1\11\3\uffff\1\1\1\3\1\2\1\4\3\uffff\1"+
        "\7\1\6\2\uffff";
    static final String DFA11_specialS =
        "\25\uffff}>";
    static final String[] DFA11_transitionS = {
            "\1\2\4\uffff\1\1\1\uffff\1\4\1\3\6\uffff\1\5\1\6\3\uffff\1\5"+
            "\34\uffff\1\5",
            "\1\7",
            "\1\10",
            "",
            "\1\11",
            "",
            "",
            "\1\5\4\uffff\1\5\2\uffff\1\12\4\uffff\1\13\1\uffff\1\5\1\6"+
            "\3\uffff\1\5\34\uffff\1\5",
            "\1\5\4\uffff\1\5\2\uffff\1\14\4\uffff\1\15\1\uffff\1\5\1\6"+
            "\3\uffff\1\5\34\uffff\1\5",
            "\1\16",
            "",
            "",
            "",
            "",
            "\1\20\4\uffff\1\17\11\uffff\1\22\1\21\3\uffff\1\22\34\uffff"+
            "\1\22",
            "\1\23",
            "\1\24",
            "",
            "",
            "\1\22\4\uffff\1\22\11\uffff\1\22\1\21\3\uffff\1\22\34\uffff"+
            "\1\22",
            "\1\22\4\uffff\1\22\11\uffff\1\22\1\21\3\uffff\1\22\34\uffff"+
            "\1\22"
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "141:1: atom returns [NFAFactory.Handle p] : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );";
        }
    }
    static final String DFA17_eotS =
        "\17\uffff";
    static final String DFA17_eofS =
        "\17\uffff";
    static final String DFA17_minS =
        "\1\57\3\2\4\uffff\1\4\3\uffff\1\2\2\uffff";
    static final String DFA17_maxS =
        "\4\140\4\uffff\1\145\3\uffff\1\145\2\uffff";
    static final String DFA17_acceptS =
        "\4\uffff\1\10\1\11\1\1\1\2\1\uffff\1\5\1\6\1\7\1\uffff\1\4\1\3";
    static final String DFA17_specialS =
        "\17\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\5\4\uffff\1\4\11\uffff\1\2\4\uffff\1\1\34\uffff\1\3",
            "\1\6\2\7\13\uffff\1\7\31\uffff\1\7\2\uffff\1\7\1\uffff\1\7"+
            "\2\uffff\1\7\1\uffff\1\7\1\uffff\2\7\2\uffff\1\7\3\uffff\2\7"+
            "\3\uffff\1\7\10\uffff\1\7\1\uffff\3\7\14\uffff\1\7\2\uffff\1"+
            "\7",
            "\1\10\2\11\13\uffff\1\11\31\uffff\1\11\2\uffff\1\11\1\uffff"+
            "\1\11\2\uffff\1\11\1\uffff\1\11\1\uffff\2\11\2\uffff\1\11\3"+
            "\uffff\2\11\3\uffff\1\11\10\uffff\1\11\1\uffff\3\11\14\uffff"+
            "\1\11\2\uffff\1\11",
            "\1\12\2\13\13\uffff\1\13\31\uffff\1\13\2\uffff\1\13\1\uffff"+
            "\1\13\2\uffff\1\13\1\uffff\1\13\1\uffff\2\13\2\uffff\1\13\3"+
            "\uffff\2\13\3\uffff\1\13\10\uffff\1\13\1\uffff\3\13\14\uffff"+
            "\1\13\2\uffff\1\13",
            "",
            "",
            "",
            "",
            "\12\15\1\14\127\15",
            "",
            "",
            "",
            "\2\15\142\16",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "173:1: terminal returns [NFAFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( WILDCARD . ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) );";
        }
    }
 

    public static final BitSet FOLLOW_rule_in_topdown79 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_bottomup90 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule109 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule113 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_BLOCK_in_block143 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_block145 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_alternative_in_block152 = new BitSet(new long[]{0x0000000000000008L,0x0000002000100000L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative186 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative188 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_ALT_in_alternative200 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative202 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative214 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative216 = new BitSet(new long[]{0xC4D4A40000010018L,0x000000012001D008L});
    public static final BitSet FOLLOW_labeledElement_in_element236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement287 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement289 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement291 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement298 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement300 = new BitSet(new long[]{0x0010840000000000L,0x000000000001D000L});
    public static final BitSet FOLLOW_block_in_labeledElement302 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement311 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement313 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement320 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement322 = new BitSet(new long[]{0x0010840000000000L,0x000000000001D000L});
    public static final BitSet FOLLOW_block_in_labeledElement324 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec344 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec346 = new BitSet(new long[]{0xC4D4A40000010018L,0x000000012001D008L});
    public static final BitSet FOLLOW_blockSuffix_in_ebnf367 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf369 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffix397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffix405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffix413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffix421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ebnfSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom472 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom474 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom481 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom483 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom490 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom492 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom499 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom501 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_range_in_atom507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom513 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom515 = new BitSet(new long[]{0x4010800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_terminal_in_atom517 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom524 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom526 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_ruleref_in_atom528 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_terminal_in_atom539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet571 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notTerminal_in_notSet573 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet583 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet585 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_notTerminal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_ruleref637 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref640 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref642 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref656 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref659 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref661 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref675 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref677 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range709 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range713 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal739 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal768 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal770 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal784 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal815 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal846 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal848 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal858 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal860 = new BitSet(new long[]{0x0000000000000008L});

}