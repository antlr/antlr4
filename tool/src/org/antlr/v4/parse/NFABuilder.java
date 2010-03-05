// $ANTLR ${project.version} ${buildNumber} NFABuilder.g 2010-03-04 18:14:51

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
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;
import org.antlr.v4.automata.NFAFactory;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.TerminalAST;

import java.util.ArrayList;
import java.util.List;

public class NFABuilder extends TreeParser {
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



    // $ANTLR start "rule"
    // NFABuilder.g:76:1: rule returns [NFAFactory.Handle p] : ^( RULE name= ID (~ BLOCK )* block ) ;
    public final NFAFactory.Handle rule() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST name=null;
        GrammarAST RULE1=null;
        NFAFactory.Handle block2 = null;


        try {
            // NFABuilder.g:77:2: ( ^( RULE name= ID (~ BLOCK )* block ) )
            // NFABuilder.g:77:6: ^( RULE name= ID (~ BLOCK )* block )
            {
            RULE1=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_rule71); 

            match(input, Token.DOWN, null); 
            name=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule75); 
            // NFABuilder.g:77:21: (~ BLOCK )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=SEMPRED && LA1_0<=RULEACTIONS)||(LA1_0>=REWRITE_BLOCK && LA1_0<=ALT_REWRITE)) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // NFABuilder.g:77:21: ~ BLOCK
            	    {
            	    if ( (input.LA(1)>=SEMPRED && input.LA(1)<=RULEACTIONS)||(input.LA(1)>=REWRITE_BLOCK && input.LA(1)<=ALT_REWRITE) ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            factory.setCurrentRuleName((name!=null?name.getText():null));
            pushFollow(FOLLOW_block_in_rule83);
            block2=block();

            state._fsp--;


            match(input, Token.UP, null); 
            p = factory.rule(RULE1, (name!=null?name.getText():null), block2);

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
    // NFABuilder.g:81:1: block returns [NFAFactory.Handle p] : ^( BLOCK (~ ALT )* (a= alternative )+ ) ;
    public final NFAFactory.Handle block() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST BLOCK3=null;
        NFAFactory.Handle a = null;


        List<NFAFactory.Handle> alts = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:83:5: ( ^( BLOCK (~ ALT )* (a= alternative )+ ) )
            // NFABuilder.g:83:7: ^( BLOCK (~ ALT )* (a= alternative )+ )
            {
            BLOCK3=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_block112); 

            match(input, Token.DOWN, null); 
            // NFABuilder.g:83:15: (~ ALT )*
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
            	    // NFABuilder.g:83:15: ~ ALT
            	    {
            	    if ( (input.LA(1)>=SEMPRED && input.LA(1)<=EPSILON)||(input.LA(1)>=ALTLIST && input.LA(1)<=ALT_REWRITE) ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // NFABuilder.g:83:21: (a= alternative )+
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
            	    // NFABuilder.g:83:22: a= alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_block121);
            	    a=alternative();

            	    state._fsp--;

            	    alts.add(a);

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            match(input, Token.UP, null); 
            p = factory.block(BLOCK3, alts);

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
    // NFABuilder.g:87:1: alternative returns [NFAFactory.Handle p] : ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT (e= element )+ ) );
    public final NFAFactory.Handle alternative() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST EPSILON4=null;
        NFAFactory.Handle a = null;

        NFAFactory.Handle e = null;


        List<NFAFactory.Handle> els = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:89:5: ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT (e= element )+ ) )
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
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 5, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // NFABuilder.g:89:7: ^( ALT_REWRITE a= alternative . )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative160); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative164);
                    a=alternative();

                    state._fsp--;

                    matchAny(input); 

                    match(input, Token.UP, null); 
                    p = a;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:90:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative178); 

                    match(input, Token.DOWN, null); 
                    EPSILON4=(GrammarAST)match(input,EPSILON,FOLLOW_EPSILON_in_alternative180); 

                    match(input, Token.UP, null); 
                    p = factory.epsilon(EPSILON4);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:91:9: ^( ALT (e= element )+ )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative198); 

                    match(input, Token.DOWN, null); 
                    // NFABuilder.g:91:15: (e= element )+
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
                    	    // NFABuilder.g:91:16: e= element
                    	    {
                    	    pushFollow(FOLLOW_element_in_alternative203);
                    	    e=element();

                    	    state._fsp--;

                    	    els.add(e);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    match(input, Token.UP, null); 
                    p = factory.alt(els);

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
    // NFABuilder.g:95:1: element returns [NFAFactory.Handle p] : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final NFAFactory.Handle element() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST ACTION8=null;
        GrammarAST SEMPRED9=null;
        GrammarAST GATED_SEMPRED10=null;
        NFAFactory.Handle labeledElement5 = null;

        NFAFactory.Handle atom6 = null;

        NFABuilder.ebnf_return ebnf7 = null;

        NFAFactory.Handle treeSpec11 = null;


        try {
            // NFABuilder.g:96:2: ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt6=7;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // NFABuilder.g:96:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element241);
                    labeledElement5=labeledElement();

                    state._fsp--;

                    p = labeledElement5;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:97:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element251);
                    atom6=atom();

                    state._fsp--;

                    p = atom6;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:98:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element263);
                    ebnf7=ebnf();

                    state._fsp--;

                    p = (ebnf7!=null?ebnf7.p:null);

                    }
                    break;
                case 4 :
                    // NFABuilder.g:99:6: ACTION
                    {
                    ACTION8=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element277); 
                    p = factory.action(ACTION8);

                    }
                    break;
                case 5 :
                    // NFABuilder.g:100:6: SEMPRED
                    {
                    SEMPRED9=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element291); 
                    p = factory.sempred(SEMPRED9);

                    }
                    break;
                case 6 :
                    // NFABuilder.g:101:4: GATED_SEMPRED
                    {
                    GATED_SEMPRED10=(GrammarAST)match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element303); 
                    p = factory.gated_sempred(GATED_SEMPRED10);

                    }
                    break;
                case 7 :
                    // NFABuilder.g:102:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element313);
                    treeSpec11=treeSpec();

                    state._fsp--;

                    p = treeSpec11;

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
    // NFABuilder.g:105:1: labeledElement returns [NFAFactory.Handle p] : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );
    public final NFAFactory.Handle labeledElement() throws RecognitionException {
        NFAFactory.Handle p = null;

        NFAFactory.Handle atom12 = null;

        NFAFactory.Handle block13 = null;

        NFAFactory.Handle atom14 = null;

        NFAFactory.Handle block15 = null;


        try {
            // NFABuilder.g:106:2: ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) )
            int alt7=4;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // NFABuilder.g:106:4: ^( ASSIGN ID atom )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement336); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement338); 
                    pushFollow(FOLLOW_atom_in_labeledElement340);
                    atom12=atom();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = atom12;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:107:4: ^( ASSIGN ID block )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement351); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement353); 
                    pushFollow(FOLLOW_block_in_labeledElement355);
                    block13=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = block13;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:108:4: ^( PLUS_ASSIGN ID atom )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement366); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement368); 
                    pushFollow(FOLLOW_atom_in_labeledElement370);
                    atom14=atom();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = atom14;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:109:4: ^( PLUS_ASSIGN ID block )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement380); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement382); 
                    pushFollow(FOLLOW_block_in_labeledElement384);
                    block15=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = block15;

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
    // NFABuilder.g:112:1: treeSpec returns [NFAFactory.Handle p] : ^( TREE_BEGIN (e= element )+ ) ;
    public final NFAFactory.Handle treeSpec() throws RecognitionException {
        NFAFactory.Handle p = null;

        NFAFactory.Handle e = null;


        List<NFAFactory.Handle> els = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:114:5: ( ^( TREE_BEGIN (e= element )+ ) )
            // NFABuilder.g:114:7: ^( TREE_BEGIN (e= element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec412); 

            match(input, Token.DOWN, null); 
            // NFABuilder.g:114:21: (e= element )+
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
            	    // NFABuilder.g:114:22: e= element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec418);
            	    e=element();

            	    state._fsp--;

            	    els.add(e);

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            match(input, Token.UP, null); 
            p = factory.tree(els);

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

    public static class ebnf_return extends TreeRuleReturnScope {
        public NFAFactory.Handle p;
    };

    // $ANTLR start "ebnf"
    // NFABuilder.g:117:1: ebnf returns [NFAFactory.Handle p] : ( ^( astBlockSuffix block ) | ^( OPTIONAL block ) | ^( CLOSURE block ) | ^( POSITIVE_CLOSURE block ) | block );
    public final NFABuilder.ebnf_return ebnf() throws RecognitionException {
        NFABuilder.ebnf_return retval = new NFABuilder.ebnf_return();
        retval.start = input.LT(1);

        NFAFactory.Handle block16 = null;

        NFAFactory.Handle block17 = null;

        NFAFactory.Handle block18 = null;

        NFAFactory.Handle block19 = null;

        NFAFactory.Handle block20 = null;


        try {
            // NFABuilder.g:118:2: ( ^( astBlockSuffix block ) | ^( OPTIONAL block ) | ^( CLOSURE block ) | ^( POSITIVE_CLOSURE block ) | block )
            int alt9=5;
            switch ( input.LA(1) ) {
            case IMPLIES:
            case BANG:
            case ROOT:
                {
                alt9=1;
                }
                break;
            case OPTIONAL:
                {
                alt9=2;
                }
                break;
            case CLOSURE:
                {
                alt9=3;
                }
                break;
            case POSITIVE_CLOSURE:
                {
                alt9=4;
                }
                break;
            case BLOCK:
                {
                alt9=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // NFABuilder.g:118:4: ^( astBlockSuffix block )
                    {
                    pushFollow(FOLLOW_astBlockSuffix_in_ebnf444);
                    astBlockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf446);
                    block16=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = block16;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:119:4: ^( OPTIONAL block )
                    {
                    match(input,OPTIONAL,FOLLOW_OPTIONAL_in_ebnf456); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf458);
                    block17=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.optional(((GrammarAST)retval.start), block17);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:120:4: ^( CLOSURE block )
                    {
                    match(input,CLOSURE,FOLLOW_CLOSURE_in_ebnf469); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf471);
                    block18=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.star(((GrammarAST)retval.start), block18);

                    }
                    break;
                case 4 :
                    // NFABuilder.g:121:4: ^( POSITIVE_CLOSURE block )
                    {
                    match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_ebnf482); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf484);
                    block19=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.plus(((GrammarAST)retval.start), block19);

                    }
                    break;
                case 5 :
                    // NFABuilder.g:122:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf493);
                    block20=block();

                    state._fsp--;

                    retval.p = block20;

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
    // $ANTLR end "ebnf"


    // $ANTLR start "astBlockSuffix"
    // NFABuilder.g:125:1: astBlockSuffix : ( ROOT | IMPLIES | BANG );
    public final void astBlockSuffix() throws RecognitionException {
        try {
            // NFABuilder.g:126:5: ( ROOT | IMPLIES | BANG )
            // NFABuilder.g:
            {
            if ( input.LA(1)==IMPLIES||input.LA(1)==BANG||input.LA(1)==ROOT ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
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
        return ;
    }
    // $ANTLR end "astBlockSuffix"


    // $ANTLR start "atom"
    // NFABuilder.g:131:1: atom returns [NFAFactory.Handle p] : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );
    public final NFAFactory.Handle atom() throws RecognitionException {
        NFAFactory.Handle p = null;

        NFAFactory.Handle range21 = null;

        NFAFactory.Handle range22 = null;

        NFAFactory.Handle notSet23 = null;

        NFAFactory.Handle notSet24 = null;

        NFAFactory.Handle range25 = null;

        NFABuilder.terminal_return terminal26 = null;

        NFAFactory.Handle ruleref27 = null;

        NFABuilder.terminal_return terminal28 = null;

        NFAFactory.Handle ruleref29 = null;


        try {
            // NFABuilder.g:132:2: ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref )
            int alt10=9;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // NFABuilder.g:132:4: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom553); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom555);
                    range21=range();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = range21;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:133:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom566); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom568);
                    range22=range();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = range22;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:134:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom579); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom581);
                    notSet23=notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = notSet23;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:135:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom592); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom594);
                    notSet24=notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = notSet24;

                    }
                    break;
                case 5 :
                    // NFABuilder.g:136:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom604);
                    range25=range();

                    state._fsp--;

                    p = range25;

                    }
                    break;
                case 6 :
                    // NFABuilder.g:137:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom616); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom618); 
                    pushFollow(FOLLOW_terminal_in_atom620);
                    terminal26=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = (terminal26!=null?terminal26.p:null);

                    }
                    break;
                case 7 :
                    // NFABuilder.g:138:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom630); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom632); 
                    pushFollow(FOLLOW_ruleref_in_atom634);
                    ruleref27=ruleref();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = ruleref27;

                    }
                    break;
                case 8 :
                    // NFABuilder.g:139:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom648);
                    terminal28=terminal();

                    state._fsp--;

                    p = (terminal28!=null?terminal28.p:null);

                    }
                    break;
                case 9 :
                    // NFABuilder.g:140:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom663);
                    ruleref29=ruleref();

                    state._fsp--;

                    p = ruleref29;

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
    // NFABuilder.g:143:1: notSet returns [NFAFactory.Handle p] : ( ^( NOT notTerminal ) | ^( NOT block ) );
    public final NFAFactory.Handle notSet() throws RecognitionException {
        NFAFactory.Handle p = null;

        NFABuilder.notTerminal_return notTerminal30 = null;

        NFAFactory.Handle block31 = null;


        try {
            // NFABuilder.g:144:5: ( ^( NOT notTerminal ) | ^( NOT block ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==NOT) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==DOWN) ) {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==BLOCK) ) {
                        alt11=2;
                    }
                    else if ( (LA11_2==TOKEN_REF||LA11_2==STRING_LITERAL) ) {
                        alt11=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // NFABuilder.g:144:7: ^( NOT notTerminal )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet691); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notTerminal_in_notSet693);
                    notTerminal30=notTerminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = factory.not((notTerminal30!=null?notTerminal30.p:null));

                    }
                    break;
                case 2 :
                    // NFABuilder.g:145:7: ^( NOT block )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet705); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet707);
                    block31=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = factory.not(block31);

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

    public static class notTerminal_return extends TreeRuleReturnScope {
        public NFAFactory.Handle p;
    };

    // $ANTLR start "notTerminal"
    // NFABuilder.g:148:1: notTerminal returns [NFAFactory.Handle p] : ( TOKEN_REF | STRING_LITERAL );
    public final NFABuilder.notTerminal_return notTerminal() throws RecognitionException {
        NFABuilder.notTerminal_return retval = new NFABuilder.notTerminal_return();
        retval.start = input.LT(1);

        GrammarAST TOKEN_REF32=null;

        try {
            // NFABuilder.g:149:5: ( TOKEN_REF | STRING_LITERAL )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==TOKEN_REF) ) {
                alt12=1;
            }
            else if ( (LA12_0==STRING_LITERAL) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // NFABuilder.g:149:7: TOKEN_REF
                    {
                    TOKEN_REF32=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_notTerminal733); 
                    retval.p = factory.tokenRef((TerminalAST)TOKEN_REF32);

                    }
                    break;
                case 2 :
                    // NFABuilder.g:150:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_notTerminal746); 
                    retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));

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
    // $ANTLR end "notTerminal"


    // $ANTLR start "ruleref"
    // NFABuilder.g:153:1: ruleref returns [NFAFactory.Handle p] : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final NFAFactory.Handle ruleref() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST RULE_REF33=null;
        GrammarAST RULE_REF34=null;
        GrammarAST RULE_REF35=null;

        try {
            // NFABuilder.g:154:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
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
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // NFABuilder.g:154:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref771); 

                    match(input, Token.DOWN, null); 
                    RULE_REF33=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref774); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:154:25: ( ARG_ACTION )?
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ARG_ACTION) ) {
                            alt13=1;
                        }
                        switch (alt13) {
                            case 1 :
                                // NFABuilder.g:154:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref776); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 
                    p = factory.ruleRef(RULE_REF33);

                    }
                    break;
                case 2 :
                    // NFABuilder.g:155:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref790); 

                    match(input, Token.DOWN, null); 
                    RULE_REF34=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref793); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:155:25: ( ARG_ACTION )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG_ACTION) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // NFABuilder.g:155:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref795); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 
                    p = factory.ruleRef(RULE_REF34);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:156:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    RULE_REF35=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref809); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:156:18: ( ARG_ACTION )?
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==ARG_ACTION) ) {
                            alt15=1;
                        }
                        switch (alt15) {
                            case 1 :
                                // NFABuilder.g:156:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref811); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }
                    p = factory.ruleRef(RULE_REF35);

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
    // NFABuilder.g:159:1: range returns [NFAFactory.Handle p] : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
    public final NFAFactory.Handle range() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST a=null;
        GrammarAST b=null;

        try {
            // NFABuilder.g:160:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
            // NFABuilder.g:160:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range839); 

            match(input, Token.DOWN, null); 
            a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range843); 
            b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range847); 

            match(input, Token.UP, null); 
            p = factory.range(a,b);

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
    // NFABuilder.g:163:1: terminal returns [NFAFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( WILDCARD . ) | WILDCARD | ^( ROOT t= terminal ) | ^( BANG t= terminal ) );
    public final NFABuilder.terminal_return terminal() throws RecognitionException {
        NFABuilder.terminal_return retval = new NFABuilder.terminal_return();
        retval.start = input.LT(1);

        NFABuilder.terminal_return t = null;


        try {
            // NFABuilder.g:164:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( WILDCARD . ) | WILDCARD | ^( ROOT t= terminal ) | ^( BANG t= terminal ) )
            int alt17=9;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // NFABuilder.g:164:8: ^( STRING_LITERAL . )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal873); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 2 :
                    // NFABuilder.g:165:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal888); 
                    retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 3 :
                    // NFABuilder.g:166:7: ^( TOKEN_REF ARG_ACTION . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal902); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal904); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 4 :
                    // NFABuilder.g:167:7: ^( TOKEN_REF . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal918); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 5 :
                    // NFABuilder.g:168:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal934); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 6 :
                    // NFABuilder.g:169:7: ^( WILDCARD . )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal949); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.wildcard(((GrammarAST)retval.start));

                    }
                    break;
                case 7 :
                    // NFABuilder.g:170:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal965); 
                    retval.p = factory.wildcard(((GrammarAST)retval.start));

                    }
                    break;
                case 8 :
                    // NFABuilder.g:171:7: ^( ROOT t= terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal980); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal984);
                    t=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = (t!=null?t.p:null);

                    }
                    break;
                case 9 :
                    // NFABuilder.g:172:7: ^( BANG t= terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal998); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal1002);
                    t=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = (t!=null?t.p:null);

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
    protected DFA10 dfa10 = new DFA10(this);
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
            return "95:1: element returns [NFAFactory.Handle p] : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
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
        "\7\uffff\1\1\1\2\1\3\1\4";
    static final String DFA7_specialS =
        "\13\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\4\uffff\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\7\4\uffff\1\7\1\uffff\2\7\6\uffff\2\7\3\uffff\1\7\10\uffff"+
            "\1\10\23\uffff\1\7",
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
            return "105:1: labeledElement returns [NFAFactory.Handle p] : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );";
        }
    }
    static final String DFA10_eotS =
        "\25\uffff";
    static final String DFA10_eofS =
        "\25\uffff";
    static final String DFA10_minS =
        "\1\57\2\2\1\uffff\1\2\2\uffff\2\57\1\126\4\uffff\1\57\1\uffff\2"+
        "\2\1\uffff\2\57";
    static final String DFA10_maxS =
        "\1\140\2\2\1\uffff\1\2\2\uffff\2\140\1\126\4\uffff\1\140\1\uffff"+
        "\2\2\1\uffff\2\140";
    static final String DFA10_acceptS =
        "\3\uffff\1\5\1\uffff\1\10\1\11\3\uffff\1\3\1\1\1\2\1\4\1\uffff\1"+
        "\6\2\uffff\1\7\2\uffff";
    static final String DFA10_specialS =
        "\25\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\2\4\uffff\1\1\1\uffff\1\4\1\3\6\uffff\1\5\1\6\3\uffff\1\5"+
            "\34\uffff\1\5",
            "\1\7",
            "\1\10",
            "",
            "\1\11",
            "",
            "",
            "\1\5\4\uffff\1\5\2\uffff\1\13\4\uffff\1\12\1\uffff\1\5\1\6"+
            "\3\uffff\1\5\34\uffff\1\5",
            "\1\5\4\uffff\1\5\2\uffff\1\14\4\uffff\1\15\1\uffff\1\5\1\6"+
            "\3\uffff\1\5\34\uffff\1\5",
            "\1\16",
            "",
            "",
            "",
            "",
            "\1\21\4\uffff\1\20\11\uffff\1\17\1\22\3\uffff\1\17\34\uffff"+
            "\1\17",
            "",
            "\1\23",
            "\1\24",
            "",
            "\1\17\4\uffff\1\17\11\uffff\1\17\1\22\3\uffff\1\17\34\uffff"+
            "\1\17",
            "\1\17\4\uffff\1\17\11\uffff\1\17\1\22\3\uffff\1\17\34\uffff"+
            "\1\17"
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
            return "131:1: atom returns [NFAFactory.Handle p] : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );";
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
            return "163:1: terminal returns [NFAFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( WILDCARD . ) | WILDCARD | ^( ROOT t= terminal ) | ^( BANG t= terminal ) );";
        }
    }
 

    public static final BitSet FOLLOW_RULE_in_rule71 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule75 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_rule77 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_block_in_rule83 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block112 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_block114 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_alternative_in_block121 = new BitSet(new long[]{0x0000000000000008L,0x0000002000100000L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative160 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative164 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_ALT_in_alternative178 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative180 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative198 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative203 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_labeledElement_in_element241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement336 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement338 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement340 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement351 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement353 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_block_in_labeledElement355 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement366 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement368 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement370 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement380 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement382 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_block_in_labeledElement384 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec412 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec418 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_astBlockSuffix_in_ebnf444 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf446 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPTIONAL_in_ebnf456 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf458 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CLOSURE_in_ebnf469 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf471 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_ebnf482 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf484 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_astBlockSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom553 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom555 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom566 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom568 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom579 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom581 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom592 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom594 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_range_in_atom604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom616 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom618 = new BitSet(new long[]{0x4010800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_terminal_in_atom620 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom630 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom632 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_ruleref_in_atom634 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_terminal_in_atom648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet691 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notTerminal_in_notSet693 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet707 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_notTerminal733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_notTerminal746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_ruleref771 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref774 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref776 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref790 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref793 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref795 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref809 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref811 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range839 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range843 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range847 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal873 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal902 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal904 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal918 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal949 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal980 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal984 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal998 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal1002 = new BitSet(new long[]{0x0000000000000008L});

}