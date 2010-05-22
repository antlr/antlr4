// $ANTLR ${project.version} ${buildNumber} NFABuilder.g 2010-05-21 18:41:01

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "MODE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "RESULT", "ALT_REWRITE"
    };
    public static final int COMBINED=91;
    public static final int LT=44;
    public static final int STAR=49;
    public static final int BACKTRACK_SEMPRED=96;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int FORCED_ACTION=5;
    public static final int ARGLIST=89;
    public static final int ALTLIST=86;
    public static final int NOT=61;
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int ACTION=16;
    public static final int TOKEN_REF=63;
    public static final int RULEMODIFIERS=75;
    public static final int ST_RESULT=100;
    public static final int RPAREN=42;
    public static final int RET=90;
    public static final int IMPORT=22;
    public static final int STRING_LITERAL=68;
    public static final int ARG=88;
    public static final int ARG_ACTION=14;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=10;
    public static final int COMMENT=9;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int GRAMMAR=27;
    public static final int RULEACTIONS=76;
    public static final int WSCHARS=66;
    public static final int INITACTION=92;
    public static final int ALT_REWRITE=102;
    public static final int IMPLIES=43;
    public static final int RULE=73;
    public static final int RBRACE=62;
    public static final int ACTION_ESC=17;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int CHAR_RANGE=83;
    public static final int INT=65;
    public static final int EPSILON=84;
    public static final int LIST=98;
    public static final int COLONCOLON=38;
    public static final int WSNLCHARS=18;
    public static final int WS=71;
    public static final int LEXER=24;
    public static final int OR=52;
    public static final int GT=45;
    public static final int CATCH=33;
    public static final int CLOSURE=80;
    public static final int PARSER=25;
    public static final int DOLLAR=54;
    public static final int PROTECTED=28;
    public static final int ELEMENT_OPTIONS=99;
    public static final int NESTED_ACTION=15;
    public static final int FRAGMENT=23;
    public static final int ID=87;
    public static final int TREE_BEGIN=59;
    public static final int LPAREN=41;
    public static final int AT=60;
    public static final int ESC_SEQ=67;
    public static final int ALT=85;
    public static final int TREE=26;
    public static final int SCOPE=21;
    public static final int ETC=57;
    public static final int COMMA=39;
    public static final int WILDCARD=97;
    public static final int DOC_COMMENT=6;
    public static final int PLUS=50;
    public static final int REWRITE_BLOCK=78;
    public static final int DOT=55;
    public static final int MODE=36;
    public static final int RETURNS=31;
    public static final int RULES=74;
    public static final int RARROW=58;
    public static final int UNICODE_ESC=70;
    public static final int HEX_DIGIT=69;
    public static final int RANGE=56;
    public static final int TOKENS=20;
    public static final int RESULT=101;
    public static final int GATED_SEMPRED=94;
    public static final int BANG=48;
    public static final int ACTION_STRING_LITERAL=12;
    public static final int ROOT=53;
    public static final int SEMI=40;
    public static final int RULE_REF=64;
    public static final int NLCHARS=8;
    public static final int OPTIONAL=79;
    public static final int SYNPRED=82;
    public static final int COLON=37;
    public static final int QUESTION=47;
    public static final int FINALLY=34;
    public static final int TEMPLATE=35;
    public static final int LABEL=93;
    public static final int SYN_SEMPRED=95;
    public static final int ERRCHAR=72;
    public static final int BLOCK=77;
    public static final int ASSIGN=46;
    public static final int PLUS_ASSIGN=51;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=81;
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



    // $ANTLR start "block"
    // NFABuilder.g:76:1: block returns [NFAFactory.Handle p] : ^( BLOCK ( ^( OPTIONS ( . )+ ) )? (a= alternative )+ ) ;
    public final NFAFactory.Handle block() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST BLOCK1=null;
        NFAFactory.Handle a = null;


        List<NFAFactory.Handle> alts = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:78:5: ( ^( BLOCK ( ^( OPTIONS ( . )+ ) )? (a= alternative )+ ) )
            // NFABuilder.g:78:7: ^( BLOCK ( ^( OPTIONS ( . )+ ) )? (a= alternative )+ )
            {
            BLOCK1=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_block77); 

            match(input, Token.DOWN, null); 
            // NFABuilder.g:78:15: ( ^( OPTIONS ( . )+ ) )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==OPTIONS) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // NFABuilder.g:78:16: ^( OPTIONS ( . )+ )
                    {
                    match(input,OPTIONS,FOLLOW_OPTIONS_in_block81); 

                    match(input, Token.DOWN, null); 
                    // NFABuilder.g:78:26: ( . )+
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
                    	    // NFABuilder.g:78:26: .
                    	    {
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt1 >= 1 ) break loop1;
                                EarlyExitException eee =
                                    new EarlyExitException(1, input);
                                throw eee;
                        }
                        cnt1++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // NFABuilder.g:78:32: (a= alternative )+
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
            	    // NFABuilder.g:78:33: a= alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_block92);
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
            p = factory.block(BLOCK1, alts);

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
    // NFABuilder.g:82:1: alternative returns [NFAFactory.Handle p] : ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT (e= element )+ ) );
    public final NFAFactory.Handle alternative() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST EPSILON2=null;
        NFAFactory.Handle a = null;

        NFAFactory.Handle e = null;


        List<NFAFactory.Handle> els = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:84:5: ( ^( ALT_REWRITE a= alternative . ) | ^( ALT EPSILON ) | ^( ALT (e= element )+ ) )
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
                    else if ( (LA5_3==SEMPRED||LA5_3==ACTION||LA5_3==IMPLIES||LA5_3==ASSIGN||LA5_3==BANG||LA5_3==PLUS_ASSIGN||LA5_3==ROOT||(LA5_3>=DOT && LA5_3<=RANGE)||LA5_3==TREE_BEGIN||LA5_3==NOT||(LA5_3>=TOKEN_REF && LA5_3<=RULE_REF)||LA5_3==STRING_LITERAL||LA5_3==BLOCK||(LA5_3>=OPTIONAL && LA5_3<=POSITIVE_CLOSURE)||LA5_3==GATED_SEMPRED||LA5_3==WILDCARD) ) {
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
                    // NFABuilder.g:84:7: ^( ALT_REWRITE a= alternative . )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative131); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative135);
                    a=alternative();

                    state._fsp--;

                    matchAny(input); 

                    match(input, Token.UP, null); 
                    p = a;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:85:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative149); 

                    match(input, Token.DOWN, null); 
                    EPSILON2=(GrammarAST)match(input,EPSILON,FOLLOW_EPSILON_in_alternative151); 

                    match(input, Token.UP, null); 
                    p = factory.epsilon(EPSILON2);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:86:9: ^( ALT (e= element )+ )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative169); 

                    match(input, Token.DOWN, null); 
                    // NFABuilder.g:86:15: (e= element )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==SEMPRED||LA4_0==ACTION||LA4_0==IMPLIES||LA4_0==ASSIGN||LA4_0==BANG||LA4_0==PLUS_ASSIGN||LA4_0==ROOT||(LA4_0>=DOT && LA4_0<=RANGE)||LA4_0==TREE_BEGIN||LA4_0==NOT||(LA4_0>=TOKEN_REF && LA4_0<=RULE_REF)||LA4_0==STRING_LITERAL||LA4_0==BLOCK||(LA4_0>=OPTIONAL && LA4_0<=POSITIVE_CLOSURE)||LA4_0==GATED_SEMPRED||LA4_0==WILDCARD) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // NFABuilder.g:86:16: e= element
                    	    {
                    	    pushFollow(FOLLOW_element_in_alternative174);
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
    // NFABuilder.g:90:1: element returns [NFAFactory.Handle p] : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final NFAFactory.Handle element() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST ACTION6=null;
        GrammarAST SEMPRED7=null;
        GrammarAST GATED_SEMPRED8=null;
        NFAFactory.Handle labeledElement3 = null;

        NFABuilder.atom_return atom4 = null;

        NFABuilder.ebnf_return ebnf5 = null;

        NFAFactory.Handle treeSpec9 = null;


        try {
            // NFABuilder.g:91:2: ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt6=7;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // NFABuilder.g:91:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element212);
                    labeledElement3=labeledElement();

                    state._fsp--;

                    p = labeledElement3;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:92:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element222);
                    atom4=atom();

                    state._fsp--;

                    p = (atom4!=null?atom4.p:null);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:93:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element234);
                    ebnf5=ebnf();

                    state._fsp--;

                    p = (ebnf5!=null?ebnf5.p:null);

                    }
                    break;
                case 4 :
                    // NFABuilder.g:94:6: ACTION
                    {
                    ACTION6=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element248); 
                    p = factory.action(ACTION6);

                    }
                    break;
                case 5 :
                    // NFABuilder.g:95:6: SEMPRED
                    {
                    SEMPRED7=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element262); 
                    p = factory.sempred(SEMPRED7);

                    }
                    break;
                case 6 :
                    // NFABuilder.g:96:4: GATED_SEMPRED
                    {
                    GATED_SEMPRED8=(GrammarAST)match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element274); 
                    p = factory.gated_sempred(GATED_SEMPRED8);

                    }
                    break;
                case 7 :
                    // NFABuilder.g:97:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element284);
                    treeSpec9=treeSpec();

                    state._fsp--;

                    p = treeSpec9;

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
    // NFABuilder.g:100:1: labeledElement returns [NFAFactory.Handle p] : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );
    public final NFAFactory.Handle labeledElement() throws RecognitionException {
        NFAFactory.Handle p = null;

        NFABuilder.atom_return atom10 = null;

        NFAFactory.Handle block11 = null;

        NFABuilder.atom_return atom12 = null;

        NFAFactory.Handle block13 = null;


        try {
            // NFABuilder.g:101:2: ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) )
            int alt7=4;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // NFABuilder.g:101:4: ^( ASSIGN ID atom )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement307); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement309); 
                    pushFollow(FOLLOW_atom_in_labeledElement311);
                    atom10=atom();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = (atom10!=null?atom10.p:null);

                    }
                    break;
                case 2 :
                    // NFABuilder.g:102:4: ^( ASSIGN ID block )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement322); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement324); 
                    pushFollow(FOLLOW_block_in_labeledElement326);
                    block11=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = block11;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:103:4: ^( PLUS_ASSIGN ID atom )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement337); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement339); 
                    pushFollow(FOLLOW_atom_in_labeledElement341);
                    atom12=atom();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = (atom12!=null?atom12.p:null);

                    }
                    break;
                case 4 :
                    // NFABuilder.g:104:4: ^( PLUS_ASSIGN ID block )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement351); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement353); 
                    pushFollow(FOLLOW_block_in_labeledElement355);
                    block13=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = block13;

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
    // NFABuilder.g:107:1: treeSpec returns [NFAFactory.Handle p] : ^( TREE_BEGIN (e= element )+ ) ;
    public final NFAFactory.Handle treeSpec() throws RecognitionException {
        NFAFactory.Handle p = null;

        NFAFactory.Handle e = null;


        List<NFAFactory.Handle> els = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:109:5: ( ^( TREE_BEGIN (e= element )+ ) )
            // NFABuilder.g:109:7: ^( TREE_BEGIN (e= element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec383); 

            match(input, Token.DOWN, null); 
            // NFABuilder.g:109:21: (e= element )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==SEMPRED||LA8_0==ACTION||LA8_0==IMPLIES||LA8_0==ASSIGN||LA8_0==BANG||LA8_0==PLUS_ASSIGN||LA8_0==ROOT||(LA8_0>=DOT && LA8_0<=RANGE)||LA8_0==TREE_BEGIN||LA8_0==NOT||(LA8_0>=TOKEN_REF && LA8_0<=RULE_REF)||LA8_0==STRING_LITERAL||LA8_0==BLOCK||(LA8_0>=OPTIONAL && LA8_0<=POSITIVE_CLOSURE)||LA8_0==GATED_SEMPRED||LA8_0==WILDCARD) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // NFABuilder.g:109:22: e= element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec389);
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
    // NFABuilder.g:112:1: ebnf returns [NFAFactory.Handle p] : ( ^( astBlockSuffix block ) | ^( OPTIONAL block ) | ^( CLOSURE block ) | ^( POSITIVE_CLOSURE block ) | block );
    public final NFABuilder.ebnf_return ebnf() throws RecognitionException {
        NFABuilder.ebnf_return retval = new NFABuilder.ebnf_return();
        retval.start = input.LT(1);

        NFAFactory.Handle block14 = null;

        NFAFactory.Handle block15 = null;

        NFAFactory.Handle block16 = null;

        NFAFactory.Handle block17 = null;

        NFAFactory.Handle block18 = null;


        try {
            // NFABuilder.g:113:2: ( ^( astBlockSuffix block ) | ^( OPTIONAL block ) | ^( CLOSURE block ) | ^( POSITIVE_CLOSURE block ) | block )
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
                    // NFABuilder.g:113:4: ^( astBlockSuffix block )
                    {
                    pushFollow(FOLLOW_astBlockSuffix_in_ebnf415);
                    astBlockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf417);
                    block14=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = block14;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:114:4: ^( OPTIONAL block )
                    {
                    match(input,OPTIONAL,FOLLOW_OPTIONAL_in_ebnf427); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf429);
                    block15=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.optional(((GrammarAST)retval.start), block15);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:115:4: ^( CLOSURE block )
                    {
                    match(input,CLOSURE,FOLLOW_CLOSURE_in_ebnf440); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf442);
                    block16=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.star(((GrammarAST)retval.start), block16);

                    }
                    break;
                case 4 :
                    // NFABuilder.g:116:4: ^( POSITIVE_CLOSURE block )
                    {
                    match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_ebnf453); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf455);
                    block17=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.plus(((GrammarAST)retval.start), block17);

                    }
                    break;
                case 5 :
                    // NFABuilder.g:117:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf464);
                    block18=block();

                    state._fsp--;

                    retval.p = block18;

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
    // NFABuilder.g:120:1: astBlockSuffix : ( ROOT | IMPLIES | BANG );
    public final void astBlockSuffix() throws RecognitionException {
        try {
            // NFABuilder.g:121:5: ( ROOT | IMPLIES | BANG )
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

    public static class atom_return extends TreeRuleReturnScope {
        public NFAFactory.Handle p;
    };

    // $ANTLR start "atom"
    // NFABuilder.g:126:1: atom returns [NFAFactory.Handle p] : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | terminal | ruleref );
    public final NFABuilder.atom_return atom() throws RecognitionException {
        NFABuilder.atom_return retval = new NFABuilder.atom_return();
        retval.start = input.LT(1);

        NFAFactory.Handle range19 = null;

        NFAFactory.Handle range20 = null;

        NFAFactory.Handle notSet21 = null;

        NFAFactory.Handle notSet22 = null;

        NFAFactory.Handle notSet23 = null;

        NFAFactory.Handle range24 = null;

        NFABuilder.terminal_return terminal25 = null;

        NFAFactory.Handle ruleref26 = null;

        NFABuilder.terminal_return terminal27 = null;

        NFAFactory.Handle ruleref28 = null;


        try {
            // NFABuilder.g:127:2: ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | terminal | ruleref )
            int alt10=12;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // NFABuilder.g:127:4: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom524); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom526);
                    range19=range();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = range19;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:128:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom537); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom539);
                    range20=range();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = range20;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:129:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom550); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom552);
                    notSet21=notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = notSet21;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:130:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom563); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom565);
                    notSet22=notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = notSet22;

                    }
                    break;
                case 5 :
                    // NFABuilder.g:131:4: notSet
                    {
                    pushFollow(FOLLOW_notSet_in_atom575);
                    notSet23=notSet();

                    state._fsp--;

                    retval.p = notSet23;

                    }
                    break;
                case 6 :
                    // NFABuilder.g:132:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom586);
                    range24=range();

                    state._fsp--;

                    retval.p = range24;

                    }
                    break;
                case 7 :
                    // NFABuilder.g:133:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom598); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom600); 
                    pushFollow(FOLLOW_terminal_in_atom602);
                    terminal25=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = (terminal25!=null?terminal25.p:null);

                    }
                    break;
                case 8 :
                    // NFABuilder.g:134:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom612); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom614); 
                    pushFollow(FOLLOW_ruleref_in_atom616);
                    ruleref26=ruleref();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = ruleref26;

                    }
                    break;
                case 9 :
                    // NFABuilder.g:135:7: ^( WILDCARD . )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom629); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.wildcard(((GrammarAST)retval.start));

                    }
                    break;
                case 10 :
                    // NFABuilder.g:136:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom644); 
                    retval.p = factory.wildcard(((GrammarAST)retval.start));

                    }
                    break;
                case 11 :
                    // NFABuilder.g:137:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom659);
                    terminal27=terminal();

                    state._fsp--;

                    retval.p = (terminal27!=null?terminal27.p:null);

                    }
                    break;
                case 12 :
                    // NFABuilder.g:138:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom674);
                    ruleref28=ruleref();

                    state._fsp--;

                    retval.p = ruleref28;

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
    // $ANTLR end "atom"


    // $ANTLR start "notSet"
    // NFABuilder.g:141:1: notSet returns [NFAFactory.Handle p] : ( ^( NOT terminal ) | ^( NOT block ) );
    public final NFAFactory.Handle notSet() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST NOT29=null;
        GrammarAST NOT31=null;
        NFABuilder.terminal_return terminal30 = null;

        NFAFactory.Handle block32 = null;


        try {
            // NFABuilder.g:142:5: ( ^( NOT terminal ) | ^( NOT block ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==NOT) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==DOWN) ) {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==BLOCK) ) {
                        alt11=2;
                    }
                    else if ( (LA11_2==BANG||LA11_2==ROOT||LA11_2==TOKEN_REF||LA11_2==STRING_LITERAL) ) {
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
                    // NFABuilder.g:142:7: ^( NOT terminal )
                    {
                    NOT29=(GrammarAST)match(input,NOT,FOLLOW_NOT_in_notSet702); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_notSet704);
                    terminal30=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = factory.not(NOT29, (terminal30!=null?terminal30.p:null));

                    }
                    break;
                case 2 :
                    // NFABuilder.g:143:7: ^( NOT block )
                    {
                    NOT31=(GrammarAST)match(input,NOT,FOLLOW_NOT_in_notSet717); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet719);
                    block32=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = factory.not(NOT31, block32);

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


    // $ANTLR start "ruleref"
    // NFABuilder.g:146:1: ruleref returns [NFAFactory.Handle p] : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final NFAFactory.Handle ruleref() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST RULE_REF33=null;
        GrammarAST RULE_REF34=null;
        GrammarAST RULE_REF35=null;

        try {
            // NFABuilder.g:147:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
            int alt15=3;
            switch ( input.LA(1) ) {
            case ROOT:
                {
                alt15=1;
                }
                break;
            case BANG:
                {
                alt15=2;
                }
                break;
            case RULE_REF:
                {
                alt15=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // NFABuilder.g:147:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref746); 

                    match(input, Token.DOWN, null); 
                    RULE_REF33=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref749); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:147:25: ( ARG_ACTION )?
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ARG_ACTION) ) {
                            alt12=1;
                        }
                        switch (alt12) {
                            case 1 :
                                // NFABuilder.g:147:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref751); 

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
                    // NFABuilder.g:148:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref765); 

                    match(input, Token.DOWN, null); 
                    RULE_REF34=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref768); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:148:25: ( ARG_ACTION )?
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==ARG_ACTION) ) {
                            alt13=1;
                        }
                        switch (alt13) {
                            case 1 :
                                // NFABuilder.g:148:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref770); 

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
                    // NFABuilder.g:149:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    RULE_REF35=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref784); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:149:18: ( ARG_ACTION )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG_ACTION) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // NFABuilder.g:149:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref786); 

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
    // NFABuilder.g:152:1: range returns [NFAFactory.Handle p] : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
    public final NFAFactory.Handle range() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST a=null;
        GrammarAST b=null;

        try {
            // NFABuilder.g:153:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
            // NFABuilder.g:153:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range814); 

            match(input, Token.DOWN, null); 
            a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range818); 
            b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range822); 

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
    // NFABuilder.g:156:1: terminal returns [NFAFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT t= terminal ) | ^( BANG t= terminal ) );
    public final NFABuilder.terminal_return terminal() throws RecognitionException {
        NFABuilder.terminal_return retval = new NFABuilder.terminal_return();
        retval.start = input.LT(1);

        NFABuilder.terminal_return t = null;


        try {
            // NFABuilder.g:157:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT t= terminal ) | ^( BANG t= terminal ) )
            int alt16=7;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // NFABuilder.g:157:8: ^( STRING_LITERAL . )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal848); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 2 :
                    // NFABuilder.g:158:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal863); 
                    retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 3 :
                    // NFABuilder.g:159:7: ^( TOKEN_REF ARG_ACTION . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal877); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal879); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 4 :
                    // NFABuilder.g:160:7: ^( TOKEN_REF . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal893); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 5 :
                    // NFABuilder.g:161:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal909); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 6 :
                    // NFABuilder.g:162:7: ^( ROOT t= terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal924); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal928);
                    t=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = (t!=null?t.p:null);

                    }
                    break;
                case 7 :
                    // NFABuilder.g:163:7: ^( BANG t= terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal942); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal946);
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
    protected DFA16 dfa16 = new DFA16(this);
    static final String DFA6_eotS =
        "\14\uffff";
    static final String DFA6_eofS =
        "\14\uffff";
    static final String DFA6_minS =
        "\1\4\1\uffff\2\2\6\uffff\2\60";
    static final String DFA6_maxS =
        "\1\141\1\uffff\2\2\6\uffff\2\115";
    static final String DFA6_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA6_specialS =
        "\14\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\7\13\uffff\1\6\32\uffff\1\5\2\uffff\1\1\1\uffff\1\3\2\uffff"+
            "\1\1\1\uffff\1\2\1\uffff\2\4\2\uffff\1\11\1\uffff\1\4\1\uffff"+
            "\2\4\3\uffff\1\4\10\uffff\1\5\1\uffff\3\5\14\uffff\1\10\2\uffff"+
            "\1\4",
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
            "\1\4\10\uffff\1\5",
            "\1\4\4\uffff\1\4\2\uffff\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5"
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
            return "90:1: element returns [NFAFactory.Handle p] : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
    }
    static final String DFA7_eotS =
        "\13\uffff";
    static final String DFA7_eofS =
        "\13\uffff";
    static final String DFA7_minS =
        "\1\56\2\2\2\127\2\60\4\uffff";
    static final String DFA7_maxS =
        "\1\63\2\2\2\127\2\141\4\uffff";
    static final String DFA7_acceptS =
        "\7\uffff\1\2\1\1\1\4\1\3";
    static final String DFA7_specialS =
        "\13\uffff}>";
    static final String[] DFA7_transitionS = {
            "\1\1\4\uffff\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\10\4\uffff\1\10\1\uffff\2\10\4\uffff\1\10\1\uffff\2\10\3"+
            "\uffff\1\10\10\uffff\1\7\23\uffff\1\10",
            "\1\12\4\uffff\1\12\1\uffff\2\12\4\uffff\1\12\1\uffff\2\12\3"+
            "\uffff\1\12\10\uffff\1\11\23\uffff\1\12",
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
            return "100:1: labeledElement returns [NFAFactory.Handle p] : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );";
        }
    }
    static final String DFA10_eotS =
        "\31\uffff";
    static final String DFA10_eofS =
        "\31\uffff";
    static final String DFA10_minS =
        "\1\60\2\2\2\uffff\2\2\2\uffff\2\60\1\127\6\uffff\1\60\1\uffff\2"+
        "\2\1\uffff\2\60";
    static final String DFA10_maxS =
        "\1\141\2\2\2\uffff\1\2\1\141\2\uffff\2\104\1\127\6\uffff\1\104\1"+
        "\uffff\2\2\1\uffff\2\104";
    static final String DFA10_acceptS =
        "\3\uffff\1\5\1\6\2\uffff\1\13\1\14\3\uffff\1\11\1\12\1\1\1\3\1\4"+
        "\1\2\1\uffff\1\7\2\uffff\1\10\2\uffff";
    static final String DFA10_specialS =
        "\31\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\2\4\uffff\1\1\1\uffff\1\5\1\4\4\uffff\1\3\1\uffff\1\7\1\10"+
            "\3\uffff\1\7\34\uffff\1\6",
            "\1\11",
            "\1\12",
            "",
            "",
            "\1\13",
            "\1\14\2\15\13\uffff\1\15\32\uffff\1\15\2\uffff\1\15\1\uffff"+
            "\1\15\2\uffff\1\15\1\uffff\1\15\1\uffff\2\15\2\uffff\1\15\1"+
            "\uffff\1\15\1\uffff\2\15\3\uffff\1\15\10\uffff\1\15\1\uffff"+
            "\3\15\14\uffff\1\15\2\uffff\1\15",
            "",
            "",
            "\1\7\4\uffff\1\7\2\uffff\1\16\4\uffff\1\17\1\uffff\1\7\1\10"+
            "\3\uffff\1\7",
            "\1\7\4\uffff\1\7\2\uffff\1\21\4\uffff\1\20\1\uffff\1\7\1\10"+
            "\3\uffff\1\7",
            "\1\22",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\25\4\uffff\1\24\11\uffff\1\23\1\26\3\uffff\1\23",
            "",
            "\1\27",
            "\1\30",
            "",
            "\1\23\4\uffff\1\23\11\uffff\1\23\1\26\3\uffff\1\23",
            "\1\23\4\uffff\1\23\11\uffff\1\23\1\26\3\uffff\1\23"
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
            return "126:1: atom returns [NFAFactory.Handle p] : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | terminal | ruleref );";
        }
    }
    static final String DFA16_eotS =
        "\14\uffff";
    static final String DFA16_eofS =
        "\14\uffff";
    static final String DFA16_minS =
        "\1\60\2\2\4\uffff\1\4\1\uffff\1\2\2\uffff";
    static final String DFA16_maxS =
        "\1\104\2\141\4\uffff\1\146\1\uffff\1\146\2\uffff";
    static final String DFA16_acceptS =
        "\3\uffff\1\6\1\7\1\1\1\2\1\uffff\1\5\1\uffff\1\4\1\3";
    static final String DFA16_specialS =
        "\14\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\4\4\uffff\1\3\11\uffff\1\2\4\uffff\1\1",
            "\1\5\2\6\13\uffff\1\6\32\uffff\1\6\2\uffff\1\6\1\uffff\1\6"+
            "\2\uffff\1\6\1\uffff\1\6\1\uffff\2\6\2\uffff\1\6\1\uffff\1\6"+
            "\1\uffff\2\6\3\uffff\1\6\10\uffff\1\6\1\uffff\3\6\14\uffff\1"+
            "\6\2\uffff\1\6",
            "\1\7\2\10\13\uffff\1\10\32\uffff\1\10\2\uffff\1\10\1\uffff"+
            "\1\10\2\uffff\1\10\1\uffff\1\10\1\uffff\2\10\2\uffff\1\10\1"+
            "\uffff\1\10\1\uffff\2\10\3\uffff\1\10\10\uffff\1\10\1\uffff"+
            "\3\10\14\uffff\1\10\2\uffff\1\10",
            "",
            "",
            "",
            "",
            "\12\12\1\11\130\12",
            "",
            "\2\12\143\13",
            "",
            ""
    };

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "156:1: terminal returns [NFAFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( ROOT t= terminal ) | ^( BANG t= terminal ) );";
        }
    }
 

    public static final BitSet FOLLOW_BLOCK_in_block77 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPTIONS_in_block81 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_block92 = new BitSet(new long[]{0x0000000000000008L,0x0000004000200000L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative131 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative135 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_ALT_in_alternative149 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative151 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative169 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative174 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_labeledElement_in_element212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement307 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement309 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement311 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement322 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement324 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement326 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement337 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement339 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement341 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement351 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement353 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement355 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec383 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec389 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_astBlockSuffix_in_ebnf415 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf417 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPTIONAL_in_ebnf427 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf429 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CLOSURE_in_ebnf440 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf442 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_ebnf453 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_astBlockSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom524 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom526 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom537 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom539 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom550 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom552 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom563 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom565 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_notSet_in_atom575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom598 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom600 = new BitSet(new long[]{0x8021000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_terminal_in_atom602 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom612 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom614 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_ruleref_in_atom616 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_atom629 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_WILDCARD_in_atom644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet702 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_notSet704 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet717 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet719 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_ruleref746 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref749 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref751 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref765 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref768 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref770 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref784 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref786 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range814 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range822 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal848 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal863 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal877 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal879 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal893 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal924 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal928 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal942 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal946 = new BitSet(new long[]{0x0000000000000008L});

}