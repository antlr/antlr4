// $ANTLR ${project.version} ${buildNumber} NFABuilder.g 2010-03-06 14:37:58

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
    // NFABuilder.g:81:1: block returns [NFAFactory.Handle p] : ^( BLOCK ( ^( OPTIONS ( . )+ ) )? (a= alternative )+ ) ;
    public final NFAFactory.Handle block() throws RecognitionException {
        NFAFactory.Handle p = null;

        GrammarAST BLOCK3=null;
        NFAFactory.Handle a = null;


        List<NFAFactory.Handle> alts = new ArrayList<NFAFactory.Handle>();
        try {
            // NFABuilder.g:83:5: ( ^( BLOCK ( ^( OPTIONS ( . )+ ) )? (a= alternative )+ ) )
            // NFABuilder.g:83:7: ^( BLOCK ( ^( OPTIONS ( . )+ ) )? (a= alternative )+ )
            {
            BLOCK3=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_block112); 

            match(input, Token.DOWN, null); 
            // NFABuilder.g:83:15: ( ^( OPTIONS ( . )+ ) )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==OPTIONS) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // NFABuilder.g:83:16: ^( OPTIONS ( . )+ )
                    {
                    match(input,OPTIONS,FOLLOW_OPTIONS_in_block116); 

                    match(input, Token.DOWN, null); 
                    // NFABuilder.g:83:26: ( . )+
                    int cnt2=0;
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>=SEMPRED && LA2_0<=ALT_REWRITE)) ) {
                            alt2=1;
                        }
                        else if ( (LA2_0==UP) ) {
                            alt2=2;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // NFABuilder.g:83:26: .
                    	    {
                    	    matchAny(input); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt2 >= 1 ) break loop2;
                                EarlyExitException eee =
                                    new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);


                    match(input, Token.UP, null); 

                    }
                    break;

            }

            // NFABuilder.g:83:32: (a= alternative )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ALT||LA4_0==ALT_REWRITE) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // NFABuilder.g:83:33: a= alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_block127);
            	    a=alternative();

            	    state._fsp--;

            	    alts.add(a);

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
            int alt6=3;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==ALT_REWRITE) ) {
                alt6=1;
            }
            else if ( (LA6_0==ALT) ) {
                int LA6_2 = input.LA(2);

                if ( (LA6_2==DOWN) ) {
                    int LA6_3 = input.LA(3);

                    if ( (LA6_3==EPSILON) ) {
                        alt6=2;
                    }
                    else if ( (LA6_3==SEMPRED||LA6_3==ACTION||LA6_3==IMPLIES||LA6_3==ASSIGN||LA6_3==BANG||LA6_3==PLUS_ASSIGN||LA6_3==ROOT||(LA6_3>=DOT && LA6_3<=RANGE)||LA6_3==TREE_BEGIN||(LA6_3>=TOKEN_REF && LA6_3<=RULE_REF)||LA6_3==STRING_LITERAL||LA6_3==BLOCK||(LA6_3>=OPTIONAL && LA6_3<=POSITIVE_CLOSURE)||LA6_3==GATED_SEMPRED||LA6_3==WILDCARD) ) {
                        alt6=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // NFABuilder.g:89:7: ^( ALT_REWRITE a= alternative . )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative166); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative170);
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
                    match(input,ALT,FOLLOW_ALT_in_alternative184); 

                    match(input, Token.DOWN, null); 
                    EPSILON4=(GrammarAST)match(input,EPSILON,FOLLOW_EPSILON_in_alternative186); 

                    match(input, Token.UP, null); 
                    p = factory.epsilon(EPSILON4);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:91:9: ^( ALT (e= element )+ )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative204); 

                    match(input, Token.DOWN, null); 
                    // NFABuilder.g:91:15: (e= element )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==SEMPRED||LA5_0==ACTION||LA5_0==IMPLIES||LA5_0==ASSIGN||LA5_0==BANG||LA5_0==PLUS_ASSIGN||LA5_0==ROOT||(LA5_0>=DOT && LA5_0<=RANGE)||LA5_0==TREE_BEGIN||(LA5_0>=TOKEN_REF && LA5_0<=RULE_REF)||LA5_0==STRING_LITERAL||LA5_0==BLOCK||(LA5_0>=OPTIONAL && LA5_0<=POSITIVE_CLOSURE)||LA5_0==GATED_SEMPRED||LA5_0==WILDCARD) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // NFABuilder.g:91:16: e= element
                    	    {
                    	    pushFollow(FOLLOW_element_in_alternative209);
                    	    e=element();

                    	    state._fsp--;

                    	    els.add(e);

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
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
            int alt7=7;
            alt7 = dfa7.predict(input);
            switch (alt7) {
                case 1 :
                    // NFABuilder.g:96:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element247);
                    labeledElement5=labeledElement();

                    state._fsp--;

                    p = labeledElement5;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:97:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element257);
                    atom6=atom();

                    state._fsp--;

                    p = atom6;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:98:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element269);
                    ebnf7=ebnf();

                    state._fsp--;

                    p = (ebnf7!=null?ebnf7.p:null);

                    }
                    break;
                case 4 :
                    // NFABuilder.g:99:6: ACTION
                    {
                    ACTION8=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element283); 
                    p = factory.action(ACTION8);

                    }
                    break;
                case 5 :
                    // NFABuilder.g:100:6: SEMPRED
                    {
                    SEMPRED9=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element297); 
                    p = factory.sempred(SEMPRED9);

                    }
                    break;
                case 6 :
                    // NFABuilder.g:101:4: GATED_SEMPRED
                    {
                    GATED_SEMPRED10=(GrammarAST)match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element309); 
                    p = factory.gated_sempred(GATED_SEMPRED10);

                    }
                    break;
                case 7 :
                    // NFABuilder.g:102:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element319);
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
            int alt8=4;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // NFABuilder.g:106:4: ^( ASSIGN ID atom )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement342); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement344); 
                    pushFollow(FOLLOW_atom_in_labeledElement346);
                    atom12=atom();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = atom12;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:107:4: ^( ASSIGN ID block )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement357); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement359); 
                    pushFollow(FOLLOW_block_in_labeledElement361);
                    block13=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = block13;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:108:4: ^( PLUS_ASSIGN ID atom )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement372); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement374); 
                    pushFollow(FOLLOW_atom_in_labeledElement376);
                    atom14=atom();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = atom14;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:109:4: ^( PLUS_ASSIGN ID block )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement386); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement388); 
                    pushFollow(FOLLOW_block_in_labeledElement390);
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
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec418); 

            match(input, Token.DOWN, null); 
            // NFABuilder.g:114:21: (e= element )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==SEMPRED||LA9_0==ACTION||LA9_0==IMPLIES||LA9_0==ASSIGN||LA9_0==BANG||LA9_0==PLUS_ASSIGN||LA9_0==ROOT||(LA9_0>=DOT && LA9_0<=RANGE)||LA9_0==TREE_BEGIN||(LA9_0>=TOKEN_REF && LA9_0<=RULE_REF)||LA9_0==STRING_LITERAL||LA9_0==BLOCK||(LA9_0>=OPTIONAL && LA9_0<=POSITIVE_CLOSURE)||LA9_0==GATED_SEMPRED||LA9_0==WILDCARD) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // NFABuilder.g:114:22: e= element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec424);
            	    e=element();

            	    state._fsp--;

            	    els.add(e);

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
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
            int alt10=5;
            switch ( input.LA(1) ) {
            case IMPLIES:
            case BANG:
            case ROOT:
                {
                alt10=1;
                }
                break;
            case OPTIONAL:
                {
                alt10=2;
                }
                break;
            case CLOSURE:
                {
                alt10=3;
                }
                break;
            case POSITIVE_CLOSURE:
                {
                alt10=4;
                }
                break;
            case BLOCK:
                {
                alt10=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // NFABuilder.g:118:4: ^( astBlockSuffix block )
                    {
                    pushFollow(FOLLOW_astBlockSuffix_in_ebnf450);
                    astBlockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf452);
                    block16=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = block16;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:119:4: ^( OPTIONAL block )
                    {
                    match(input,OPTIONAL,FOLLOW_OPTIONAL_in_ebnf462); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf464);
                    block17=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.optional(((GrammarAST)retval.start), block17);

                    }
                    break;
                case 3 :
                    // NFABuilder.g:120:4: ^( CLOSURE block )
                    {
                    match(input,CLOSURE,FOLLOW_CLOSURE_in_ebnf475); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf477);
                    block18=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.star(((GrammarAST)retval.start), block18);

                    }
                    break;
                case 4 :
                    // NFABuilder.g:121:4: ^( POSITIVE_CLOSURE block )
                    {
                    match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_ebnf488); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf490);
                    block19=block();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = factory.plus(((GrammarAST)retval.start), block19);

                    }
                    break;
                case 5 :
                    // NFABuilder.g:122:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf499);
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
            int alt11=9;
            alt11 = dfa11.predict(input);
            switch (alt11) {
                case 1 :
                    // NFABuilder.g:132:4: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom559); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom561);
                    range21=range();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = range21;

                    }
                    break;
                case 2 :
                    // NFABuilder.g:133:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom572); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom574);
                    range22=range();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = range22;

                    }
                    break;
                case 3 :
                    // NFABuilder.g:134:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom585); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom587);
                    notSet23=notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = notSet23;

                    }
                    break;
                case 4 :
                    // NFABuilder.g:135:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom598); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom600);
                    notSet24=notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = notSet24;

                    }
                    break;
                case 5 :
                    // NFABuilder.g:136:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom610);
                    range25=range();

                    state._fsp--;

                    p = range25;

                    }
                    break;
                case 6 :
                    // NFABuilder.g:137:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom622); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom624); 
                    pushFollow(FOLLOW_terminal_in_atom626);
                    terminal26=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = (terminal26!=null?terminal26.p:null);

                    }
                    break;
                case 7 :
                    // NFABuilder.g:138:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom636); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom638); 
                    pushFollow(FOLLOW_ruleref_in_atom640);
                    ruleref27=ruleref();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = ruleref27;

                    }
                    break;
                case 8 :
                    // NFABuilder.g:139:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom654);
                    terminal28=terminal();

                    state._fsp--;

                    p = (terminal28!=null?terminal28.p:null);

                    }
                    break;
                case 9 :
                    // NFABuilder.g:140:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom669);
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
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==NOT) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==DOWN) ) {
                    int LA12_2 = input.LA(3);

                    if ( (LA12_2==TOKEN_REF||LA12_2==STRING_LITERAL) ) {
                        alt12=1;
                    }
                    else if ( (LA12_2==BLOCK) ) {
                        alt12=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // NFABuilder.g:144:7: ^( NOT notTerminal )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet697); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notTerminal_in_notSet699);
                    notTerminal30=notTerminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    p = factory.not((notTerminal30!=null?notTerminal30.p:null));

                    }
                    break;
                case 2 :
                    // NFABuilder.g:145:7: ^( NOT block )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet711); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet713);
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
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==TOKEN_REF) ) {
                alt13=1;
            }
            else if ( (LA13_0==STRING_LITERAL) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // NFABuilder.g:149:7: TOKEN_REF
                    {
                    TOKEN_REF32=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_notTerminal739); 
                    retval.p = factory.tokenRef((TerminalAST)TOKEN_REF32);

                    }
                    break;
                case 2 :
                    // NFABuilder.g:150:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_notTerminal752); 
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
            int alt17=3;
            switch ( input.LA(1) ) {
            case ROOT:
                {
                alt17=1;
                }
                break;
            case BANG:
                {
                alt17=2;
                }
                break;
            case RULE_REF:
                {
                alt17=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // NFABuilder.g:154:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref777); 

                    match(input, Token.DOWN, null); 
                    RULE_REF33=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref780); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:154:25: ( ARG_ACTION )?
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==ARG_ACTION) ) {
                            alt14=1;
                        }
                        switch (alt14) {
                            case 1 :
                                // NFABuilder.g:154:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref782); 

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
                    match(input,BANG,FOLLOW_BANG_in_ruleref796); 

                    match(input, Token.DOWN, null); 
                    RULE_REF34=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref799); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:155:25: ( ARG_ACTION )?
                        int alt15=2;
                        int LA15_0 = input.LA(1);

                        if ( (LA15_0==ARG_ACTION) ) {
                            alt15=1;
                        }
                        switch (alt15) {
                            case 1 :
                                // NFABuilder.g:155:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref801); 

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
                    RULE_REF35=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref815); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // NFABuilder.g:156:18: ( ARG_ACTION )?
                        int alt16=2;
                        int LA16_0 = input.LA(1);

                        if ( (LA16_0==ARG_ACTION) ) {
                            alt16=1;
                        }
                        switch (alt16) {
                            case 1 :
                                // NFABuilder.g:156:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref817); 

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
            match(input,RANGE,FOLLOW_RANGE_in_range845); 

            match(input, Token.DOWN, null); 
            a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range849); 
            b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range853); 

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
            int alt18=9;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // NFABuilder.g:164:8: ^( STRING_LITERAL . )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal879); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 2 :
                    // NFABuilder.g:165:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal894); 
                    retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 3 :
                    // NFABuilder.g:166:7: ^( TOKEN_REF ARG_ACTION . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal908); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal910); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 4 :
                    // NFABuilder.g:167:7: ^( TOKEN_REF . )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal924); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 5 :
                    // NFABuilder.g:168:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal940); 
                    retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));

                    }
                    break;
                case 6 :
                    // NFABuilder.g:169:7: ^( WILDCARD . )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal955); 

                    match(input, Token.DOWN, null); 
                    matchAny(input); 

                    match(input, Token.UP, null); 
                    retval.p = factory.wildcard(((GrammarAST)retval.start));

                    }
                    break;
                case 7 :
                    // NFABuilder.g:170:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal971); 
                    retval.p = factory.wildcard(((GrammarAST)retval.start));

                    }
                    break;
                case 8 :
                    // NFABuilder.g:171:7: ^( ROOT t= terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal986); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal990);
                    t=terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 
                    retval.p = (t!=null?t.p:null);

                    }
                    break;
                case 9 :
                    // NFABuilder.g:172:7: ^( BANG t= terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal1004); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal1008);
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


    protected DFA7 dfa7 = new DFA7(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA11 dfa11 = new DFA11(this);
    protected DFA18 dfa18 = new DFA18(this);
    static final String DFA7_eotS =
        "\14\uffff";
    static final String DFA7_eofS =
        "\14\uffff";
    static final String DFA7_minS =
        "\1\4\1\uffff\2\2\6\uffff\2\57";
    static final String DFA7_maxS =
        "\1\140\1\uffff\2\2\6\uffff\2\140";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA7_specialS =
        "\14\uffff}>";
    static final String[] DFA7_transitionS = {
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
            return "95:1: element returns [NFAFactory.Handle p] : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
    }
    static final String DFA8_eotS =
        "\13\uffff";
    static final String DFA8_eofS =
        "\13\uffff";
    static final String DFA8_minS =
        "\1\55\2\2\2\126\2\57\4\uffff";
    static final String DFA8_maxS =
        "\1\62\2\2\2\126\2\140\4\uffff";
    static final String DFA8_acceptS =
        "\7\uffff\1\2\1\1\1\3\1\4";
    static final String DFA8_specialS =
        "\13\uffff}>";
    static final String[] DFA8_transitionS = {
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

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "105:1: labeledElement returns [NFAFactory.Handle p] : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );";
        }
    }
    static final String DFA11_eotS =
        "\25\uffff";
    static final String DFA11_eofS =
        "\25\uffff";
    static final String DFA11_minS =
        "\1\57\2\2\1\uffff\1\2\2\uffff\2\57\1\126\4\uffff\1\57\1\uffff\2"+
        "\2\1\uffff\2\57";
    static final String DFA11_maxS =
        "\1\140\2\2\1\uffff\1\2\2\uffff\2\140\1\126\4\uffff\1\140\1\uffff"+
        "\2\2\1\uffff\2\140";
    static final String DFA11_acceptS =
        "\3\uffff\1\5\1\uffff\1\10\1\11\3\uffff\1\3\1\1\1\4\1\2\1\uffff\1"+
        "\6\2\uffff\1\7\2\uffff";
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
            "\1\5\4\uffff\1\5\2\uffff\1\13\4\uffff\1\12\1\uffff\1\5\1\6"+
            "\3\uffff\1\5\34\uffff\1\5",
            "\1\5\4\uffff\1\5\2\uffff\1\15\4\uffff\1\14\1\uffff\1\5\1\6"+
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
            return "131:1: atom returns [NFAFactory.Handle p] : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );";
        }
    }
    static final String DFA18_eotS =
        "\17\uffff";
    static final String DFA18_eofS =
        "\17\uffff";
    static final String DFA18_minS =
        "\1\57\3\2\4\uffff\1\4\3\uffff\1\2\2\uffff";
    static final String DFA18_maxS =
        "\4\140\4\uffff\1\145\3\uffff\1\145\2\uffff";
    static final String DFA18_acceptS =
        "\4\uffff\1\10\1\11\1\1\1\2\1\uffff\1\5\1\6\1\7\1\uffff\1\4\1\3";
    static final String DFA18_specialS =
        "\17\uffff}>";
    static final String[] DFA18_transitionS = {
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
            return "163:1: terminal returns [NFAFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF | ^( WILDCARD . ) | WILDCARD | ^( ROOT t= terminal ) | ^( BANG t= terminal ) );";
        }
    }
 

    public static final BitSet FOLLOW_RULE_in_rule71 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule75 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_rule77 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_block_in_rule83 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block112 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_OPTIONS_in_block116 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_block127 = new BitSet(new long[]{0x0000000000000008L,0x0000002000100000L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative166 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative170 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_ALT_in_alternative184 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative186 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative204 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_alternative209 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_labeledElement_in_element247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement342 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement344 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement357 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement359 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_block_in_labeledElement361 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement372 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement374 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement376 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement386 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement388 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_block_in_labeledElement390 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec418 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec424 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_astBlockSuffix_in_ebnf450 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf452 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_OPTIONAL_in_ebnf462 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf464 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CLOSURE_in_ebnf475 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf477 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_ebnf488 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf490 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_astBlockSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom559 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom561 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom572 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom574 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom585 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom587 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom598 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom600 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_range_in_atom610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom622 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom624 = new BitSet(new long[]{0x4010800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_terminal_in_atom626 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom636 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom638 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000100000008L});
    public static final BitSet FOLLOW_ruleref_in_atom640 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_terminal_in_atom654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet697 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notTerminal_in_notSet699 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet711 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet713 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_notTerminal739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_notTerminal752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_ruleref777 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref780 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref782 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref796 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref799 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref801 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref815 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref817 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range845 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range849 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range853 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal879 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal908 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal910 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal924 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal955 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal986 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal990 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal1004 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal1008 = new BitSet(new long[]{0x0000000000000008L});

}