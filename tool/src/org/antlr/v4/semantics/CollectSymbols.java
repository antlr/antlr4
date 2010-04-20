// $ANTLR ${project.version} ${buildNumber} CollectSymbols.g 2010-04-19 17:33:29

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
import org.antlr.v4.parse.ScopeParser;
import org.antlr.v4.tool.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Collects rules, terminals, strings, actions, scopes etc... from AST
 *  Side-effects: None
 */
public class CollectSymbols extends org.antlr.v4.runtime.tree.TreeFilter {
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


        public CollectSymbols(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public CollectSymbols(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return CollectSymbols.tokenNames; }
    public String getGrammarFileName() { return "CollectSymbols.g"; }


    Rule currentRule;
    String currentMode = LexerGrammar.DEFAULT_MODE_NAME;
    int currentAlt = 1; // 1..n
    public List<Rule> rules = new ArrayList<Rule>();
    public List<GrammarAST> rulerefs = new ArrayList<GrammarAST>();
    public List<GrammarAST> qualifiedRulerefs = new ArrayList<GrammarAST>();
    public List<GrammarAST> terminals = new ArrayList<GrammarAST>();
    public List<GrammarAST> tokenIDRefs = new ArrayList<GrammarAST>();
    public Set<String> strings = new HashSet<String>();
    public List<GrammarAST> tokensDefs = new ArrayList<GrammarAST>();
    public List<AttributeDict> scopes = new ArrayList<AttributeDict>();
    public List<GrammarAST> actions = new ArrayList<GrammarAST>();
    Grammar g; // which grammar are we checking
    public CollectSymbols(TreeNodeStream input, Grammar g) {
    	this(input);
    	this.g = g;
    }



    // $ANTLR start "topdown"
    // CollectSymbols.g:94:1: topdown : ( globalScope | globalNamedAction | tokensSection | mode | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | finallyClause | exceptionHandler );
    public final void topdown() throws RecognitionException {
        try {
            // CollectSymbols.g:96:5: ( globalScope | globalNamedAction | tokensSection | mode | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | finallyClause | exceptionHandler )
            int alt1=17;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // CollectSymbols.g:96:7: globalScope
                    {
                    pushFollow(FOLLOW_globalScope_in_topdown97);
                    globalScope();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:97:7: globalNamedAction
                    {
                    pushFollow(FOLLOW_globalNamedAction_in_topdown105);
                    globalNamedAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // CollectSymbols.g:98:7: tokensSection
                    {
                    pushFollow(FOLLOW_tokensSection_in_topdown113);
                    tokensSection();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // CollectSymbols.g:99:7: mode
                    {
                    pushFollow(FOLLOW_mode_in_topdown121);
                    mode();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // CollectSymbols.g:100:7: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown129);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // CollectSymbols.g:101:7: ruleArg
                    {
                    pushFollow(FOLLOW_ruleArg_in_topdown137);
                    ruleArg();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // CollectSymbols.g:102:7: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_topdown145);
                    ruleReturns();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // CollectSymbols.g:103:7: ruleNamedAction
                    {
                    pushFollow(FOLLOW_ruleNamedAction_in_topdown153);
                    ruleNamedAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // CollectSymbols.g:104:7: ruleScopeSpec
                    {
                    pushFollow(FOLLOW_ruleScopeSpec_in_topdown161);
                    ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // CollectSymbols.g:105:7: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown169);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // CollectSymbols.g:106:7: rewriteElement
                    {
                    pushFollow(FOLLOW_rewriteElement_in_topdown177);
                    rewriteElement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // CollectSymbols.g:108:7: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_topdown198);
                    terminal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 13 :
                    // CollectSymbols.g:109:7: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_topdown206);
                    labeledElement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 14 :
                    // CollectSymbols.g:110:7: setAlt
                    {
                    pushFollow(FOLLOW_setAlt_in_topdown214);
                    setAlt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 15 :
                    // CollectSymbols.g:111:7: ruleAction
                    {
                    pushFollow(FOLLOW_ruleAction_in_topdown222);
                    ruleAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 16 :
                    // CollectSymbols.g:112:7: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_topdown230);
                    finallyClause();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 17 :
                    // CollectSymbols.g:113:7: exceptionHandler
                    {
                    pushFollow(FOLLOW_exceptionHandler_in_topdown238);
                    exceptionHandler();

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
    // CollectSymbols.g:116:1: bottomup : finishRule ;
    public final void bottomup() throws RecognitionException {
        try {
            // CollectSymbols.g:117:2: ( finishRule )
            // CollectSymbols.g:117:4: finishRule
            {
            pushFollow(FOLLOW_finishRule_in_bottomup249);
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
    // CollectSymbols.g:120:1: globalScope : {...}? ^( SCOPE ID ACTION ) ;
    public final void globalScope() throws RecognitionException {
        GrammarAST ACTION1=null;
        GrammarAST ID2=null;

        try {
            // CollectSymbols.g:121:2: ({...}? ^( SCOPE ID ACTION ) )
            // CollectSymbols.g:121:4: {...}? ^( SCOPE ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "globalScope", "inContext(\"GRAMMAR\")");
            }
            match(input,SCOPE,FOLLOW_SCOPE_in_globalScope263); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID2=(GrammarAST)match(input,ID,FOLLOW_ID_in_globalScope265); if (state.failed) return ;
            ACTION1=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_globalScope267); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		AttributeDict s = ScopeParser.parseDynamicScope((ACTION1!=null?ACTION1.getText():null));
              		s.name = (ID2!=null?ID2.getText():null);
              		s.ast = ACTION1;
              		scopes.add(s);
              		
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


    // $ANTLR start "globalNamedAction"
    // CollectSymbols.g:130:1: globalNamedAction : {...}? ^( AT ( ID )? ID ACTION ) ;
    public final void globalNamedAction() throws RecognitionException {
        GrammarAST AT3=null;
        GrammarAST ACTION4=null;

        try {
            // CollectSymbols.g:131:2: ({...}? ^( AT ( ID )? ID ACTION ) )
            // CollectSymbols.g:131:4: {...}? ^( AT ( ID )? ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "globalNamedAction", "inContext(\"GRAMMAR\")");
            }
            AT3=(GrammarAST)match(input,AT,FOLLOW_AT_in_globalNamedAction286); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // CollectSymbols.g:131:33: ( ID )?
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
                    // CollectSymbols.g:131:33: ID
                    {
                    match(input,ID,FOLLOW_ID_in_globalNamedAction288); if (state.failed) return ;

                    }
                    break;

            }

            match(input,ID,FOLLOW_ID_in_globalNamedAction291); if (state.failed) return ;
            ACTION4=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_globalNamedAction293); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              actions.add(AT3); ((ActionAST)ACTION4).resolver = g;
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
    // $ANTLR end "globalNamedAction"


    // $ANTLR start "tokensSection"
    // CollectSymbols.g:135:1: tokensSection : {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) ;
    public final void tokensSection() throws RecognitionException {
        GrammarAST t=null;
        GrammarAST ASSIGN5=null;
        GrammarAST STRING_LITERAL6=null;

        try {
            // CollectSymbols.g:136:2: ({...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) )
            // CollectSymbols.g:136:4: {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokensSection", "inContext(\"TOKENS\")");
            }
            // CollectSymbols.g:137:3: ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
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
                    // CollectSymbols.g:137:5: ^( ASSIGN t= ID STRING_LITERAL )
                    {
                    ASSIGN5=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokensSection316); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection320); if (state.failed) return ;
                    STRING_LITERAL6=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokensSection322); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      terminals.add(t); tokenIDRefs.add(t);
                      			 tokensDefs.add(ASSIGN5); strings.add((STRING_LITERAL6!=null?STRING_LITERAL6.getText():null));
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:140:5: t= ID
                    {
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection336); if (state.failed) return ;
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


    // $ANTLR start "mode"
    // CollectSymbols.g:145:1: mode : ^( MODE ID ( . )+ ) ;
    public final void mode() throws RecognitionException {
        GrammarAST ID7=null;

        try {
            // CollectSymbols.g:145:5: ( ^( MODE ID ( . )+ ) )
            // CollectSymbols.g:145:7: ^( MODE ID ( . )+ )
            {
            match(input,MODE,FOLLOW_MODE_in_mode355); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID7=(GrammarAST)match(input,ID,FOLLOW_ID_in_mode357); if (state.failed) return ;
            // CollectSymbols.g:145:17: ( . )+
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
            	    // CollectSymbols.g:145:17: .
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
              currentMode = (ID7!=null?ID7.getText():null);
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
    // $ANTLR end "mode"


    // $ANTLR start "rule"
    // CollectSymbols.g:147:1: rule : ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ^( BLOCK ( . )+ ) ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST name=null;
        GrammarAST RULE8=null;
        GrammarAST m=null;

        List<GrammarAST> modifiers = new ArrayList<GrammarAST>();
        try {
            // CollectSymbols.g:149:2: ( ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ^( BLOCK ( . )+ ) ( . )* ) )
            // CollectSymbols.g:149:6: ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ^( BLOCK ( . )+ ) ( . )* )
            {
            RULE8=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_rule382); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            name=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule394); if (state.failed) return ;
            // CollectSymbols.g:150:17: ( options {greedy=false; } : . )*
            loop5:
            do {
                int alt5=2;
                alt5 = dfa5.predict(input);
                switch (alt5) {
            	case 1 :
            	    // CollectSymbols.g:150:42: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // CollectSymbols.g:151:9: ( ^( RULEMODIFIERS (m= . )+ ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULEMODIFIERS) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // CollectSymbols.g:151:10: ^( RULEMODIFIERS (m= . )+ )
                    {
                    match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_rule418); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    // CollectSymbols.g:151:26: (m= . )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>=SEMPRED && LA6_0<=ALT_REWRITE)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // CollectSymbols.g:151:27: m= .
                    	    {
                    	    m=(GrammarAST)input.LT(1);
                    	    matchAny(input); if (state.failed) return ;
                    	    if ( state.backtracking==1 ) {
                    	      modifiers.add(m);
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return ;

                    }
                    break;

            }

            match(input,BLOCK,FOLLOW_BLOCK_in_rule441); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // CollectSymbols.g:152:17: ( . )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0>=SEMPRED && LA8_0<=ALT_REWRITE)) ) {
                    alt8=1;
                }
                else if ( (LA8_0==UP) ) {
                    alt8=2;
                }


                switch (alt8) {
            	case 1 :
            	    // CollectSymbols.g:152:17: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            // CollectSymbols.g:153:9: ( . )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=SEMPRED && LA9_0<=ALT_REWRITE)) ) {
                    alt9=1;
                }
                else if ( (LA9_0==UP) ) {
                    alt9=2;
                }


                switch (alt9) {
            	case 1 :
            	    // CollectSymbols.g:153:9: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		int numAlts = RULE8.getFirstChildWithType(BLOCK).getChildCount();
              		Rule r = new Rule(g, (name!=null?name.getText():null), (GrammarASTWithOptions)RULE8, numAlts);
              		if ( g.isLexer() ) r.mode = currentMode;
              		else r.mode = LexerGrammar.DEFAULT_MODE_NAME;
              		if ( modifiers.size()>0 ) r.modifiers = modifiers;
              		rules.add(r);
              		currentRule = r;
              		currentAlt = 1;
              		
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

    public static class setAlt_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "setAlt"
    // CollectSymbols.g:167:1: setAlt : {...}? ( ALT | ALT_REWRITE ) ;
    public final CollectSymbols.setAlt_return setAlt() throws RecognitionException {
        CollectSymbols.setAlt_return retval = new CollectSymbols.setAlt_return();
        retval.start = input.LT(1);

        try {
            // CollectSymbols.g:168:2: ({...}? ( ALT | ALT_REWRITE ) )
            // CollectSymbols.g:168:4: {...}? ( ALT | ALT_REWRITE )
            {
            if ( !((inContext("RULE BLOCK"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "setAlt", "inContext(\"RULE BLOCK\")");
            }
            if ( input.LA(1)==ALT||input.LA(1)==ALT_REWRITE ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            if ( state.backtracking==1 ) {
              currentAlt = ((GrammarAST)retval.start).getChildIndex()+1;
            }

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
    // $ANTLR end "setAlt"


    // $ANTLR start "finishRule"
    // CollectSymbols.g:172:1: finishRule : RULE ;
    public final void finishRule() throws RecognitionException {
        try {
            // CollectSymbols.g:173:2: ( RULE )
            // CollectSymbols.g:173:4: RULE
            {
            match(input,RULE,FOLLOW_RULE_in_finishRule508); if (state.failed) return ;
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


    // $ANTLR start "ruleNamedAction"
    // CollectSymbols.g:176:1: ruleNamedAction : {...}? ^( AT ID ACTION ) ;
    public final void ruleNamedAction() throws RecognitionException {
        GrammarAST ID9=null;
        GrammarAST ACTION10=null;

        try {
            // CollectSymbols.g:177:2: ({...}? ^( AT ID ACTION ) )
            // CollectSymbols.g:177:4: {...}? ^( AT ID ACTION )
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleNamedAction", "inContext(\"RULE\")");
            }
            match(input,AT,FOLLOW_AT_in_ruleNamedAction524); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID9=(GrammarAST)match(input,ID,FOLLOW_ID_in_ruleNamedAction526); if (state.failed) return ;
            ACTION10=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleNamedAction528); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.namedActions.put((ID9!=null?ID9.getText():null),(ActionAST)ACTION10);
              		((ActionAST)ACTION10).resolver = currentRule;
              		
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
    // $ANTLR end "ruleNamedAction"


    // $ANTLR start "ruleAction"
    // CollectSymbols.g:184:1: ruleAction : {...}? ACTION ;
    public final void ruleAction() throws RecognitionException {
        GrammarAST ACTION11=null;

        try {
            // CollectSymbols.g:185:2: ({...}? ACTION )
            // CollectSymbols.g:185:4: {...}? ACTION
            {
            if ( !((inContext("RULE ...")&&!inContext("SCOPE")&&
            		 !inContext("CATCH")&&!inContext("FINALLY")&&!inContext("AT"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleAction", "inContext(\"RULE ...\")&&!inContext(\"SCOPE\")&&\n\t\t !inContext(\"CATCH\")&&!inContext(\"FINALLY\")&&!inContext(\"AT\")");
            }
            ACTION11=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleAction548); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.alt[currentAlt].actions.add((ActionAST)ACTION11);
              		((ActionAST)ACTION11).resolver = currentRule.alt[currentAlt];
              		
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
    // $ANTLR end "ruleAction"


    // $ANTLR start "exceptionHandler"
    // CollectSymbols.g:194:1: exceptionHandler : ^( CATCH ARG_ACTION ACTION ) ;
    public final void exceptionHandler() throws RecognitionException {
        GrammarAST ACTION12=null;

        try {
            // CollectSymbols.g:195:2: ( ^( CATCH ARG_ACTION ACTION ) )
            // CollectSymbols.g:195:4: ^( CATCH ARG_ACTION ACTION )
            {
            match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler564); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler566); if (state.failed) return ;
            ACTION12=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler568); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.exceptionActions.add((ActionAST)ACTION12);
              		((ActionAST)ACTION12).resolver = currentRule;
              		
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
    // $ANTLR end "exceptionHandler"


    // $ANTLR start "finallyClause"
    // CollectSymbols.g:202:1: finallyClause : ^( FINALLY ACTION ) ;
    public final void finallyClause() throws RecognitionException {
        GrammarAST ACTION13=null;

        try {
            // CollectSymbols.g:203:2: ( ^( FINALLY ACTION ) )
            // CollectSymbols.g:203:4: ^( FINALLY ACTION )
            {
            match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause585); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ACTION13=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_finallyClause587); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.exceptionActions.add((ActionAST)ACTION13);
              		((ActionAST)ACTION13).resolver = currentRule;
              		
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
    // $ANTLR end "finallyClause"


    // $ANTLR start "ruleArg"
    // CollectSymbols.g:210:1: ruleArg : {...}? ARG_ACTION ;
    public final void ruleArg() throws RecognitionException {
        GrammarAST ARG_ACTION14=null;

        try {
            // CollectSymbols.g:211:2: ({...}? ARG_ACTION )
            // CollectSymbols.g:211:4: {...}? ARG_ACTION
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleArg", "inContext(\"RULE\")");
            }
            ARG_ACTION14=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleArg607); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.args = ScopeParser.parseTypeList((ARG_ACTION14!=null?ARG_ACTION14.getText():null));
              		currentRule.args.ast = ARG_ACTION14;
              		
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
    // CollectSymbols.g:218:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
    public final void ruleReturns() throws RecognitionException {
        GrammarAST ARG_ACTION15=null;

        try {
            // CollectSymbols.g:219:2: ( ^( RETURNS ARG_ACTION ) )
            // CollectSymbols.g:219:4: ^( RETURNS ARG_ACTION )
            {
            match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns624); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ARG_ACTION15=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns626); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.retvals = ScopeParser.parseTypeList((ARG_ACTION15!=null?ARG_ACTION15.getText():null));
              		currentRule.retvals.ast = ARG_ACTION15;
              		
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
    // CollectSymbols.g:226:1: ruleScopeSpec : {...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) ) ;
    public final void ruleScopeSpec() throws RecognitionException {
        GrammarAST ACTION16=null;
        GrammarAST ids=null;
        List list_ids=null;

        try {
            // CollectSymbols.g:227:2: ({...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) ) )
            // CollectSymbols.g:227:4: {...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) )
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleScopeSpec", "inContext(\"RULE\")");
            }
            // CollectSymbols.g:228:3: ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==SCOPE) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==DOWN) ) {
                    int LA11_2 = input.LA(3);

                    if ( (LA11_2==ACTION) ) {
                        alt11=1;
                    }
                    else if ( (LA11_2==ID) ) {
                        alt11=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 11, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // CollectSymbols.g:228:5: ^( SCOPE ACTION )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec649); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    ACTION16=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec651); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {

                      			currentRule.scope = ScopeParser.parseDynamicScope((ACTION16!=null?ACTION16.getText():null));
                      			currentRule.scope.name = currentRule.name;
                      			currentRule.scope.ast = ACTION16;
                      			
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:234:5: ^( SCOPE (ids+= ID )+ )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec664); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    // CollectSymbols.g:234:16: (ids+= ID )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( (LA10_0==ID) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // CollectSymbols.g:234:16: ids+= ID
                    	    {
                    	    ids=(GrammarAST)match(input,ID,FOLLOW_ID_in_ruleScopeSpec668); if (state.failed) return ;
                    	    if (list_ids==null) list_ids=new ArrayList();
                    	    list_ids.add(ids);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);


                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      currentRule.useScopes = list_ids;
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
    // $ANTLR end "ruleScopeSpec"

    public static class rewriteElement_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "rewriteElement"
    // CollectSymbols.g:238:1: rewriteElement : {...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL ) ;
    public final CollectSymbols.rewriteElement_return rewriteElement() throws RecognitionException {
        CollectSymbols.rewriteElement_return retval = new CollectSymbols.rewriteElement_return();
        retval.start = input.LT(1);

        try {
            // CollectSymbols.g:240:2: ({...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL ) )
            // CollectSymbols.g:241:6: {...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL )
            {
            if ( !((inContext("RESULT ..."))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "rewriteElement", "inContext(\"RESULT ...\")");
            }
            if ( (input.LA(1)>=TOKEN_REF && input.LA(1)<=RULE_REF)||input.LA(1)==STRING_LITERAL||input.LA(1)==LABEL ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            if ( state.backtracking==1 ) {
              currentRule.alt[currentAlt].rewriteElements.add(((GrammarAST)retval.start));
            }

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
    // $ANTLR end "rewriteElement"

    public static class labeledElement_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "labeledElement"
    // CollectSymbols.g:245:1: labeledElement : {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) ;
    public final CollectSymbols.labeledElement_return labeledElement() throws RecognitionException {
        CollectSymbols.labeledElement_return retval = new CollectSymbols.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST id=null;
        GrammarAST e=null;

        try {
            // CollectSymbols.g:251:2: ({...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) )
            // CollectSymbols.g:251:4: {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            {
            if ( !((inContext("RULE ..."))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "labeledElement", "inContext(\"RULE ...\")");
            }
            // CollectSymbols.g:252:3: ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==ASSIGN) ) {
                alt12=1;
            }
            else if ( (LA12_0==PLUS_ASSIGN) ) {
                alt12=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // CollectSymbols.g:252:5: ^( ASSIGN id= ID e= . )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement732); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement736); if (state.failed) return retval;
                    e=(GrammarAST)input.LT(1);
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:253:5: ^( PLUS_ASSIGN id= ID e= . )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement748); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement752); if (state.failed) return retval;
                    e=(GrammarAST)input.LT(1);
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;

            }


            }

            if ( state.backtracking==1 ) {

              LabelElementPair lp = new LabelElementPair(g, id, e, ((GrammarAST)retval.start).getType());
              //currentRule.labelDefs.map((id!=null?id.getText():null), lp);
              currentRule.alt[currentAlt].labelDefs.map((id!=null?id.getText():null), lp);

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
    // CollectSymbols.g:257:1: terminal : ({...}? STRING_LITERAL | TOKEN_REF );
    public final CollectSymbols.terminal_return terminal() throws RecognitionException {
        CollectSymbols.terminal_return retval = new CollectSymbols.terminal_return();
        retval.start = input.LT(1);

        GrammarAST STRING_LITERAL17=null;
        GrammarAST TOKEN_REF18=null;

        try {
            // CollectSymbols.g:258:5: ({...}? STRING_LITERAL | TOKEN_REF )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==STRING_LITERAL) ) {
                alt13=1;
            }
            else if ( (LA13_0==TOKEN_REF) ) {
                alt13=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // CollectSymbols.g:258:7: {...}? STRING_LITERAL
                    {
                    if ( !((!inContext("TOKENS ASSIGN"))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "terminal", "!inContext(\"TOKENS ASSIGN\")");
                    }
                    STRING_LITERAL17=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal778); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {

                          	terminals.add(((GrammarAST)retval.start));
                          	strings.add((STRING_LITERAL17!=null?STRING_LITERAL17.getText():null));
                          	if ( currentRule!=null ) {
                          		currentRule.alt[currentAlt].tokenRefs.map((STRING_LITERAL17!=null?STRING_LITERAL17.getText():null), STRING_LITERAL17);
                          	}
                          	
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:266:7: TOKEN_REF
                    {
                    TOKEN_REF18=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal793); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {

                          	terminals.add(TOKEN_REF18);
                          	tokenIDRefs.add(TOKEN_REF18);
                          	if ( currentRule!=null ) {
                          		currentRule.alt[currentAlt].tokenRefs.map((TOKEN_REF18!=null?TOKEN_REF18.getText():null), TOKEN_REF18);
                          	}
                          	
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
    // CollectSymbols.g:276:1: ruleref : ({...}?r= RULE_REF | r= RULE_REF ) ;
    public final void ruleref() throws RecognitionException {
        GrammarAST r=null;

        try {
            // CollectSymbols.g:278:5: ( ({...}?r= RULE_REF | r= RULE_REF ) )
            // CollectSymbols.g:278:7: ({...}?r= RULE_REF | r= RULE_REF )
            {
            // CollectSymbols.g:278:7: ({...}?r= RULE_REF | r= RULE_REF )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_REF) ) {
                int LA14_1 = input.LA(2);

                if ( ((inContext("DOT ..."))) ) {
                    alt14=1;
                }
                else if ( (true) ) {
                    alt14=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // CollectSymbols.g:278:9: {...}?r= RULE_REF
                    {
                    if ( !((inContext("DOT ..."))) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "ruleref", "inContext(\"DOT ...\")");
                    }
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref830); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      qualifiedRulerefs.add((GrammarAST)r.getParent());
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:280:8: r= RULE_REF
                    {
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref843); if (state.failed) return ;

                    }
                    break;

            }

            if ( state.backtracking==1 ) {

                  	rulerefs.add(r);
                  	if ( currentRule!=null ) {
                  		currentRule.alt[currentAlt].ruleRefs.map((r!=null?r.getText():null), r);
                  	}
                  	
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
    protected DFA5 dfa5 = new DFA5(this);
    static final String DFA1_eotS =
        "\42\uffff";
    static final String DFA1_eofS =
        "\42\uffff";
    static final String DFA1_minS =
        "\1\16\3\2\5\uffff\3\0\6\uffff\1\20\2\127\2\uffff\1\3\1\uffff\1\20"+
        "\1\4\1\uffff\1\3\1\uffff\1\2\2\0\1\uffff";
    static final String DFA1_maxS =
        "\1\146\3\2\5\uffff\3\0\6\uffff\3\127\2\uffff\1\127\1\uffff\1\127"+
        "\1\146\1\uffff\1\3\1\uffff\1\3\2\0\1\uffff";
    static final String DFA1_acceptS =
        "\4\uffff\1\3\1\4\1\5\1\6\1\7\3\uffff\1\13\1\15\1\16\1\17\1\20\1"+
        "\21\3\uffff\1\12\1\14\1\uffff\1\11\2\uffff\1\1\1\uffff\1\2\3\uffff"+
        "\1\10";
    static final String DFA1_specialS =
        "\11\uffff\1\3\1\4\1\1\23\uffff\1\2\1\0\1\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\7\1\uffff\1\17\4\uffff\1\1\11\uffff\1\10\1\uffff\1\21\1\20"+
            "\1\uffff\1\5\11\uffff\1\3\4\uffff\1\15\10\uffff\1\2\2\uffff"+
            "\1\13\1\11\3\uffff\1\12\4\uffff\1\6\13\uffff\1\16\1\uffff\1"+
            "\4\5\uffff\1\14\10\uffff\1\16",
            "\1\22",
            "\1\23",
            "\1\24",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\30\106\uffff\1\27",
            "\1\31",
            "\1\32",
            "",
            "",
            "\1\30\14\uffff\1\33\106\uffff\1\30",
            "",
            "\1\34\106\uffff\1\35",
            "\100\15\1\36\42\15",
            "",
            "\1\37",
            "",
            "\1\15\1\40",
            "\1\uffff",
            "\1\uffff",
            ""
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
            return "94:1: topdown : ( globalScope | globalNamedAction | tokensSection | mode | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | finallyClause | exceptionHandler );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TreeNodeStream input = (TreeNodeStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_32 = input.LA(1);

                         
                        int index1_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("TOKENS"))) ) {s = 4;}

                        else if ( ((inContext("RULE ..."))) ) {s = 13;}

                         
                        input.seek(index1_32);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_11 = input.LA(1);

                         
                        int index1_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("RESULT ..."))) ) {s = 12;}

                        else if ( (true) ) {s = 22;}

                         
                        input.seek(index1_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA1_31 = input.LA(1);

                         
                        int index1_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("GRAMMAR"))) ) {s = 29;}

                        else if ( ((inContext("RULE"))) ) {s = 33;}

                         
                        input.seek(index1_31);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA1_9 = input.LA(1);

                         
                        int index1_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(((inContext("RESULT ..."))))) ) {s = 21;}

                        else if ( ((inContext("RESULT ..."))) ) {s = 12;}

                         
                        input.seek(index1_9);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA1_10 = input.LA(1);

                         
                        int index1_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("RESULT ..."))) ) {s = 12;}

                        else if ( ((!inContext("TOKENS ASSIGN"))) ) {s = 22;}

                         
                        input.seek(index1_10);
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
    static final String DFA5_eotS =
        "\26\uffff";
    static final String DFA5_eofS =
        "\26\uffff";
    static final String DFA5_minS =
        "\1\4\2\2\1\uffff\2\4\2\2\1\uffff\1\3\3\2\3\4\2\2\2\3\1\uffff\1\3";
    static final String DFA5_maxS =
        "\3\146\1\uffff\4\146\1\uffff\13\146\1\uffff\1\146";
    static final String DFA5_acceptS =
        "\3\uffff\1\1\4\uffff\1\2\13\uffff\1\2\1\uffff";
    static final String DFA5_specialS =
        "\26\uffff}>";
    static final String[] DFA5_transitionS = {
            "\107\3\1\1\1\3\1\2\31\3",
            "\1\4\1\uffff\143\3",
            "\1\5\1\uffff\143\3",
            "",
            "\143\6",
            "\143\7",
            "\2\10\143\6",
            "\1\10\1\11\143\7",
            "",
            "\1\10\107\14\1\12\1\14\1\13\31\14",
            "\1\15\1\10\107\14\1\12\1\14\1\13\31\14",
            "\1\16\1\10\107\14\1\12\1\14\1\13\31\14",
            "\1\17\1\10\107\14\1\12\1\14\1\13\31\14",
            "\143\20",
            "\143\21",
            "\143\22",
            "\1\3\1\23\143\20",
            "\1\3\1\24\143\21",
            "\1\25\143\22",
            "\1\24\107\14\1\12\1\14\1\13\31\14",
            "",
            "\1\24\107\14\1\12\1\14\1\13\31\14"
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "()* loopback of 150:17: ( options {greedy=false; } : . )*";
        }
    }
 

    public static final BitSet FOLLOW_globalScope_in_topdown97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_globalNamedAction_in_topdown105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSection_in_topdown113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mode_in_topdown121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_topdown129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArg_in_topdown137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturns_in_topdown145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNamedAction_in_topdown153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_topdown161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_topdown169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteElement_in_topdown177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_topdown198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_labeledElement_in_topdown206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setAlt_in_topdown214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_topdown222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finallyClause_in_topdown230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exceptionHandler_in_topdown238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finishRule_in_bottomup249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_globalScope263 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_globalScope265 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_globalScope267 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_globalNamedAction286 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_globalNamedAction288 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_globalNamedAction291 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_globalNamedAction293 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_tokensSection316 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokensSection320 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokensSection322 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_tokensSection336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODE_in_mode355 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_mode357 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_RULE_in_rule382 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule394 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_RULEMODIFIERS_in_rule418 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BLOCK_in_rule441 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_setAlt484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_finishRule508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_ruleNamedAction524 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleNamedAction526 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleNamedAction528 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler564 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler566 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler568 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause585 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause587 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleArg607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns624 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns626 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec651 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec664 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec668 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_set_in_rewriteElement696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement732 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement736 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement748 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement752 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref843 = new BitSet(new long[]{0x0000000000000002L});

}