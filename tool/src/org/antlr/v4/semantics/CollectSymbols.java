// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 CollectSymbols.g 2010-02-07 16:55:18

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
package org.antlr.v4.semantics;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeRuleReturnScope;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.List;
/** Collects rules, terminals, strings, actions, scopes etc... from AST
 *  Side-effects: None
 */
public class CollectSymbols extends org.antlr.v4.runtime.tree.TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "RESULT", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "ALT_REWRITE"
    };
    public static final int COMBINED=91;
    public static final int LT=43;
    public static final int STAR=48;
    public static final int BACKTRACK_SEMPRED=96;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int FORCED_ACTION=5;
    public static final int ARGLIST=89;
    public static final int ALTLIST=85;
    public static final int NOT=60;
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int ACTION=16;
    public static final int TOKEN_REF=62;
    public static final int RULEMODIFIERS=74;
    public static final int ST_RESULT=100;
    public static final int RPAREN=41;
    public static final int RET=90;
    public static final int IMPORT=22;
    public static final int STRING_LITERAL=67;
    public static final int ARG=88;
    public static final int ARG_ACTION=14;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=10;
    public static final int COMMENT=9;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int GRAMMAR=27;
    public static final int RULEACTIONS=75;
    public static final int WSCHARS=65;
    public static final int INITACTION=92;
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
    public static final int LIST=98;
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
    public static final int ELEMENT_OPTIONS=99;
    public static final int NESTED_ACTION=15;
    public static final int FRAGMENT=23;
    public static final int ID=87;
    public static final int TREE_BEGIN=58;
    public static final int LPAREN=40;
    public static final int AT=59;
    public static final int ESC_SEQ=66;
    public static final int ALT=84;
    public static final int TREE=26;
    public static final int SCOPE=21;
    public static final int ETC=56;
    public static final int COMMA=38;
    public static final int WILDCARD=97;
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
    public static final int GATED_SEMPRED=94;
    public static final int RESULT=86;
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
    public static final int LABEL=93;
    public static final int SYN_SEMPRED=95;
    public static final int ERRCHAR=71;
    public static final int BLOCK=76;
    public static final int ASSIGN=45;
    public static final int PLUS_ASSIGN=50;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=80;
    public static final int OPTIONS=19;

    // delegates
    // delegators


        public CollectSymbols(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public CollectSymbols(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return CollectSymbols.tokenNames; }
    public String getGrammarFileName() { return "CollectSymbols.g"; }


    Rule currentRule = null;
    public List<Rule> rules = new ArrayList<Rule>();
    public List<GrammarAST> rulerefs = new ArrayList<GrammarAST>();
    public List<GrammarAST> terminals = new ArrayList<GrammarAST>();
    public List<GrammarAST> tokenIDRefs = new ArrayList<GrammarAST>();
    public List<GrammarAST> tokenNameRefsInRewrite = new ArrayList<GrammarAST>();
    public List<GrammarAST> strings = new ArrayList<GrammarAST>();
    public List<GrammarAST> tokensDefs = new ArrayList<GrammarAST>();
    public List<GrammarAST> scopes = new ArrayList<GrammarAST>();
    public List<GrammarAST> actions = new ArrayList<GrammarAST>();
    public MultiMap<String, LabelElementPair> ruleToLabelSpace =
    	new MultiMap<String, LabelElementPair>();
    Grammar g; // which grammar are we checking
    public CollectSymbols(TreeNodeStream input, Grammar g) {
    	this(input);
    	this.g = g;
    }



    // $ANTLR start "topdown"
    // CollectSymbols.g:93:1: topdown : ( globalScope | action | tokensSection | rule | ruleArg | ruleReturns | ruleScopeSpec | ruleref | terminal | labeledElement | tokenRefInRewrite );
    public final void topdown() throws RecognitionException {
        try {
            // CollectSymbols.g:94:5: ( globalScope | action | tokensSection | rule | ruleArg | ruleReturns | ruleScopeSpec | ruleref | terminal | labeledElement | tokenRefInRewrite )
            int alt1=11;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // CollectSymbols.g:94:7: globalScope
                    {
                    pushFollow(FOLLOW_globalScope_in_topdown96);
                    globalScope();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:95:7: action
                    {
                    pushFollow(FOLLOW_action_in_topdown104);
                    action();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // CollectSymbols.g:96:7: tokensSection
                    {
                    pushFollow(FOLLOW_tokensSection_in_topdown112);
                    tokensSection();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // CollectSymbols.g:97:7: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown120);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // CollectSymbols.g:98:7: ruleArg
                    {
                    pushFollow(FOLLOW_ruleArg_in_topdown128);
                    ruleArg();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // CollectSymbols.g:99:7: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_topdown136);
                    ruleReturns();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // CollectSymbols.g:100:7: ruleScopeSpec
                    {
                    pushFollow(FOLLOW_ruleScopeSpec_in_topdown144);
                    ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // CollectSymbols.g:101:7: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown152);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // CollectSymbols.g:102:7: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_topdown160);
                    terminal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // CollectSymbols.g:103:7: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_topdown168);
                    labeledElement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // CollectSymbols.g:104:7: tokenRefInRewrite
                    {
                    pushFollow(FOLLOW_tokenRefInRewrite_in_topdown176);
                    tokenRefInRewrite();

                    state._fsp--;
                    if (state.failed) return ;

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
        return ;
    }
    // $ANTLR end "topdown"


    // $ANTLR start "bottomup"
    // CollectSymbols.g:107:1: bottomup : finishRule ;
    public final void bottomup() throws RecognitionException {
        try {
            // CollectSymbols.g:108:2: ( finishRule )
            // CollectSymbols.g:108:4: finishRule
            {
            pushFollow(FOLLOW_finishRule_in_bottomup187);
            finishRule();

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


    // $ANTLR start "globalScope"
    // CollectSymbols.g:111:1: globalScope : {...}? ^( SCOPE ID ACTION ) ;
    public final void globalScope() throws RecognitionException {
        GrammarAST SCOPE1=null;

        try {
            // CollectSymbols.g:112:2: ({...}? ^( SCOPE ID ACTION ) )
            // CollectSymbols.g:112:4: {...}? ^( SCOPE ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "globalScope", "inContext(\"GRAMMAR\")");
            }
            SCOPE1=(GrammarAST)match(input,SCOPE,FOLLOW_SCOPE_in_globalScope201); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ID,FOLLOW_ID_in_globalScope203); if (state.failed) return ;
            match(input,ACTION,FOLLOW_ACTION_in_globalScope205); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              scopes.add(SCOPE1);
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
    // $ANTLR end "globalScope"


    // $ANTLR start "action"
    // CollectSymbols.g:115:1: action : {...}? ^( AT ( ID )? ID ACTION ) ;
    public final void action() throws RecognitionException {
        GrammarAST AT2=null;

        try {
            // CollectSymbols.g:116:2: ({...}? ^( AT ( ID )? ID ACTION ) )
            // CollectSymbols.g:116:4: {...}? ^( AT ( ID )? ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "action", "inContext(\"GRAMMAR\")");
            }
            AT2=(GrammarAST)match(input,AT,FOLLOW_AT_in_action222); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // CollectSymbols.g:116:33: ( ID )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==ID) ) {
                int LA2_1 = input.LA(2);

                if ( (LA2_1==ID) ) {
                    alt2=1;
                }
            }
            switch (alt2) {
                case 1 :
                    // CollectSymbols.g:116:33: ID
                    {
                    match(input,ID,FOLLOW_ID_in_action224); if (state.failed) return ;

                    }
                    break;

            }

            match(input,ID,FOLLOW_ID_in_action227); if (state.failed) return ;
            match(input,ACTION,FOLLOW_ACTION_in_action229); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              actions.add(AT2);
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
    // $ANTLR end "action"


    // $ANTLR start "tokensSection"
    // CollectSymbols.g:120:1: tokensSection : {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) ;
    public final void tokensSection() throws RecognitionException {
        GrammarAST t=null;
        GrammarAST ASSIGN3=null;
        GrammarAST STRING_LITERAL4=null;

        try {
            // CollectSymbols.g:121:2: ({...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) )
            // CollectSymbols.g:121:4: {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokensSection", "inContext(\"TOKENS\")");
            }
            // CollectSymbols.g:122:3: ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==ASSIGN) ) {
                alt3=1;
            }
            else if ( (LA3_0==ID) ) {
                alt3=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // CollectSymbols.g:122:5: ^( ASSIGN t= ID STRING_LITERAL )
                    {
                    ASSIGN3=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokensSection252); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection256); if (state.failed) return ;
                    STRING_LITERAL4=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokensSection258); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      terminals.add(t); tokenIDRefs.add(t);
                      			 tokensDefs.add(ASSIGN3); strings.add(STRING_LITERAL4);
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:125:5: t= ID
                    {
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection272); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      terminals.add(t); tokenIDRefs.add(t); tokensDefs.add(t);
                    }

                    }
                    break;

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
    // $ANTLR end "tokensSection"


    // $ANTLR start "rule"
    // CollectSymbols.g:130:1: rule : ^( RULE name= ID ( . )+ ) ;
    public final void rule() throws RecognitionException {
        GrammarAST name=null;
        GrammarAST RULE5=null;

        try {
            // CollectSymbols.g:130:5: ( ^( RULE name= ID ( . )+ ) )
            // CollectSymbols.g:130:9: ^( RULE name= ID ( . )+ )
            {
            RULE5=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_rule294); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            name=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule298); if (state.failed) return ;
            // CollectSymbols.g:130:25: ( . )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=SEMPRED && LA4_0<=ALT_REWRITE)) ) {
                    alt4=1;
                }
                else if ( (LA4_0==UP) ) {
                    alt4=2;
                }


                switch (alt4) {
            	case 1 :
            	    // CollectSymbols.g:130:25: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		Rule r = new Rule((name!=null?name.getText():null), (GrammarASTWithOptions)RULE5);
              		rules.add(r);
              		currentRule = r;
              		
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
    // $ANTLR end "rule"


    // $ANTLR start "finishRule"
    // CollectSymbols.g:138:1: finishRule : RULE ;
    public final void finishRule() throws RecognitionException {
        try {
            // CollectSymbols.g:139:2: ( RULE )
            // CollectSymbols.g:139:4: RULE
            {
            match(input,RULE,FOLLOW_RULE_in_finishRule320); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              currentRule = null;
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
    // $ANTLR end "finishRule"


    // $ANTLR start "ruleArg"
    // CollectSymbols.g:142:1: ruleArg : {...}? ARG_ACTION ;
    public final void ruleArg() throws RecognitionException {
        GrammarAST ARG_ACTION6=null;

        try {
            // CollectSymbols.g:143:2: ({...}? ARG_ACTION )
            // CollectSymbols.g:143:4: {...}? ARG_ACTION
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleArg", "inContext(\"RULE\")");
            }
            ARG_ACTION6=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleArg335); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              currentRule.arg = ARG_ACTION6;
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
    // $ANTLR end "ruleArg"


    // $ANTLR start "ruleReturns"
    // CollectSymbols.g:146:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
    public final void ruleReturns() throws RecognitionException {
        GrammarAST ARG_ACTION7=null;

        try {
            // CollectSymbols.g:147:2: ( ^( RETURNS ARG_ACTION ) )
            // CollectSymbols.g:147:4: ^( RETURNS ARG_ACTION )
            {
            match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns350); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ARG_ACTION7=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns352); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              currentRule.ret = ARG_ACTION7;
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
    // $ANTLR end "ruleReturns"


    // $ANTLR start "ruleScopeSpec"
    // CollectSymbols.g:150:1: ruleScopeSpec : {...}? ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) ) ;
    public final void ruleScopeSpec() throws RecognitionException {
        try {
            // CollectSymbols.g:151:2: ({...}? ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) ) )
            // CollectSymbols.g:151:4: {...}? ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) )
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleScopeSpec", "inContext(\"RULE\")");
            }
            // CollectSymbols.g:152:3: ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==SCOPE) ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==DOWN) ) {
                    int LA6_2 = input.LA(3);

                    if ( (LA6_2==ACTION) ) {
                        alt6=1;
                    }
                    else if ( (LA6_2==ID) ) {
                        alt6=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 6, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // CollectSymbols.g:152:5: ^( SCOPE ACTION )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec373); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec375); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:153:5: ^( SCOPE ( ID )+ )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec383); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    // CollectSymbols.g:153:13: ( ID )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==ID) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // CollectSymbols.g:153:13: ID
                    	    {
                    	    match(input,ID,FOLLOW_ID_in_ruleScopeSpec385); if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return ;

                    }
                    break;

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
    // $ANTLR end "ruleScopeSpec"


    // $ANTLR start "tokenRefInRewrite"
    // CollectSymbols.g:157:1: tokenRefInRewrite : {...}? TOKEN_REF ;
    public final void tokenRefInRewrite() throws RecognitionException {
        GrammarAST TOKEN_REF8=null;

        try {
            // CollectSymbols.g:158:2: ({...}? TOKEN_REF )
            // CollectSymbols.g:158:4: {...}? TOKEN_REF
            {
            if ( !((inContext("RESULT ..."))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokenRefInRewrite", "inContext(\"RESULT ...\")");
            }
            TOKEN_REF8=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenRefInRewrite404); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              tokenNameRefsInRewrite.add(TOKEN_REF8);
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
    // $ANTLR end "tokenRefInRewrite"

    public static class labeledElement_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "labeledElement"
    // CollectSymbols.g:161:1: labeledElement : {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) ;
    public final CollectSymbols.labeledElement_return labeledElement() throws RecognitionException {
        CollectSymbols.labeledElement_return retval = new CollectSymbols.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST id=null;
        GrammarAST e=null;

        try {
            // CollectSymbols.g:166:2: ({...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) )
            // CollectSymbols.g:166:4: {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            {
            if ( !((inContext("RULE ..."))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "labeledElement", "inContext(\"RULE ...\")");
            }
            // CollectSymbols.g:167:3: ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==ASSIGN) ) {
                alt7=1;
            }
            else if ( (LA7_0==PLUS_ASSIGN) ) {
                alt7=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // CollectSymbols.g:167:5: ^( ASSIGN id= ID e= . )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement430); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement434); if (state.failed) return retval;
                    e=(GrammarAST)input.LT(1);
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:168:5: ^( PLUS_ASSIGN id= ID e= . )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement446); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement450); if (state.failed) return retval;
                    e=(GrammarAST)input.LT(1);
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;

            }


            }

            if ( state.backtracking==1 ) {

              LabelElementPair lp = new LabelElementPair(g, id, e, ((GrammarAST)retval.start).getType());
              ruleToLabelSpace.map(currentRule.name, lp);

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
    // $ANTLR end "labeledElement"

    public static class terminal_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "terminal"
    // CollectSymbols.g:172:1: terminal : ({...}? STRING_LITERAL | TOKEN_REF );
    public final CollectSymbols.terminal_return terminal() throws RecognitionException {
        CollectSymbols.terminal_return retval = new CollectSymbols.terminal_return();
        retval.start = input.LT(1);

        GrammarAST STRING_LITERAL9=null;
        GrammarAST TOKEN_REF10=null;

        try {
            // CollectSymbols.g:173:5: ({...}? STRING_LITERAL | TOKEN_REF )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==STRING_LITERAL) ) {
                alt8=1;
            }
            else if ( (LA8_0==TOKEN_REF) ) {
                alt8=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // CollectSymbols.g:173:7: {...}? STRING_LITERAL
                    {
                    if ( !((!inContext("TOKENS ASSIGN"))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "terminal", "!inContext(\"TOKENS ASSIGN\")");
                    }
                    STRING_LITERAL9=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal476); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      terminals.add(((GrammarAST)retval.start));
                          												     strings.add(STRING_LITERAL9);
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:175:7: TOKEN_REF
                    {
                    TOKEN_REF10=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal486); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {
                      terminals.add(((GrammarAST)retval.start)); tokenIDRefs.add(TOKEN_REF10);
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
        return retval;
    }
    // $ANTLR end "terminal"


    // $ANTLR start "ruleref"
    // CollectSymbols.g:178:1: ruleref : ^( RULE_REF ( ARG_ACTION )? ) ;
    public final void ruleref() throws RecognitionException {
        GrammarAST RULE_REF11=null;

        try {
            // CollectSymbols.g:179:5: ( ^( RULE_REF ( ARG_ACTION )? ) )
            // CollectSymbols.g:179:7: ^( RULE_REF ( ARG_ACTION )? )
            {
            RULE_REF11=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref506); if (state.failed) return ;

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); if (state.failed) return ;
                // CollectSymbols.g:179:18: ( ARG_ACTION )?
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==ARG_ACTION) ) {
                    alt9=1;
                }
                switch (alt9) {
                    case 1 :
                        // CollectSymbols.g:179:18: ARG_ACTION
                        {
                        match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref508); if (state.failed) return ;

                        }
                        break;

                }


                match(input, Token.UP, null); if (state.failed) return ;
            }
            if ( state.backtracking==1 ) {
              rulerefs.add(RULE_REF11);
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
    // $ANTLR end "ruleref"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\25\uffff";
    static final String DFA1_eofS =
        "\25\uffff";
    static final String DFA1_minS =
        "\1\16\1\2\1\uffff\1\2\6\uffff\1\0\1\uffff\1\20\1\127\1\uffff\1\3"+
        "\1\uffff\1\4\1\uffff\1\2\1\0";
    static final String DFA1_maxS =
        "\1\127\1\2\1\uffff\1\2\6\uffff\1\0\1\uffff\2\127\1\uffff\1\127\1"+
        "\uffff\1\145\1\uffff\1\3\1\0";
    static final String DFA1_acceptS =
        "\2\uffff\1\2\1\uffff\1\3\1\4\1\5\1\6\1\10\1\11\1\uffff\1\12\2\uffff"+
        "\1\13\1\uffff\1\7\1\uffff\1\1\2\uffff";
    static final String DFA1_specialS =
        "\12\uffff\1\0\11\uffff\1\1}>";
    static final String[] DFA1_transitionS = {
            "\1\6\6\uffff\1\1\11\uffff\1\7\15\uffff\1\3\4\uffff\1\13\10\uffff"+
            "\1\2\2\uffff\1\12\1\10\3\uffff\1\11\4\uffff\1\5\16\uffff\1\4",
            "\1\14",
            "",
            "\1\15",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "\1\20\106\uffff\1\17",
            "\1\21",
            "",
            "\1\20\14\uffff\1\22\106\uffff\1\20",
            "",
            "\77\13\1\23\42\13",
            "",
            "\1\13\1\24",
            "\1\uffff"
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "93:1: topdown : ( globalScope | action | tokensSection | rule | ruleArg | ruleReturns | ruleScopeSpec | ruleref | terminal | labeledElement | tokenRefInRewrite );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TreeNodeStream input = (TreeNodeStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_10 = input.LA(1);

                         
                        int index1_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(((inContext("RESULT ..."))))) ) {s = 9;}

                        else if ( ((inContext("RESULT ..."))) ) {s = 14;}

                         
                        input.seek(index1_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_20 = input.LA(1);

                         
                        int index1_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("TOKENS"))) ) {s = 4;}

                        else if ( ((inContext("RULE ..."))) ) {s = 11;}

                         
                        input.seek(index1_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 1, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_globalScope_in_topdown96 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_topdown104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSection_in_topdown112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_topdown120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArg_in_topdown128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturns_in_topdown136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_topdown144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_topdown152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_topdown160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_labeledElement_in_topdown168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokenRefInRewrite_in_topdown176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finishRule_in_bottomup187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_globalScope201 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_globalScope203 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_globalScope205 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_action222 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action224 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_action227 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action229 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_tokensSection252 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokensSection256 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokensSection258 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_tokensSection272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule294 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule298 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_RULE_in_finishRule320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleArg335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns350 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns352 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec373 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec375 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec383 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec385 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenRefInRewrite404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement430 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement434 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement446 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement450 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref506 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref508 = new BitSet(new long[]{0x0000000000000008L});

}