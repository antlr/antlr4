// $ANTLR ${project.version} ${buildNumber} CollectSymbols.g 2010-04-02 12:57:47

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


        public CollectSymbols(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public CollectSymbols(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return CollectSymbols.tokenNames; }
    public String getGrammarFileName() { return "CollectSymbols.g"; }


    Rule currentRule = null;
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
    // CollectSymbols.g:93:1: topdown : ( globalScope | globalNamedAction | tokensSection | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | finallyClause | exceptionHandler );
    public final void topdown() throws RecognitionException {
        try {
            // CollectSymbols.g:95:5: ( globalScope | globalNamedAction | tokensSection | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | finallyClause | exceptionHandler )
            int alt1=16;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // CollectSymbols.g:95:7: globalScope
                    {
                    pushFollow(FOLLOW_globalScope_in_topdown97);
                    globalScope();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:96:7: globalNamedAction
                    {
                    pushFollow(FOLLOW_globalNamedAction_in_topdown105);
                    globalNamedAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // CollectSymbols.g:97:7: tokensSection
                    {
                    pushFollow(FOLLOW_tokensSection_in_topdown113);
                    tokensSection();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // CollectSymbols.g:98:7: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown121);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // CollectSymbols.g:99:7: ruleArg
                    {
                    pushFollow(FOLLOW_ruleArg_in_topdown129);
                    ruleArg();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // CollectSymbols.g:100:7: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_topdown137);
                    ruleReturns();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // CollectSymbols.g:101:7: ruleNamedAction
                    {
                    pushFollow(FOLLOW_ruleNamedAction_in_topdown145);
                    ruleNamedAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // CollectSymbols.g:102:7: ruleScopeSpec
                    {
                    pushFollow(FOLLOW_ruleScopeSpec_in_topdown153);
                    ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // CollectSymbols.g:103:7: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown161);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // CollectSymbols.g:104:7: rewriteElement
                    {
                    pushFollow(FOLLOW_rewriteElement_in_topdown169);
                    rewriteElement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // CollectSymbols.g:106:7: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_topdown190);
                    terminal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // CollectSymbols.g:107:7: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_topdown198);
                    labeledElement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 13 :
                    // CollectSymbols.g:108:7: setAlt
                    {
                    pushFollow(FOLLOW_setAlt_in_topdown206);
                    setAlt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 14 :
                    // CollectSymbols.g:109:7: ruleAction
                    {
                    pushFollow(FOLLOW_ruleAction_in_topdown214);
                    ruleAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 15 :
                    // CollectSymbols.g:110:7: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_topdown222);
                    finallyClause();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 16 :
                    // CollectSymbols.g:111:7: exceptionHandler
                    {
                    pushFollow(FOLLOW_exceptionHandler_in_topdown230);
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
    // CollectSymbols.g:114:1: bottomup : finishRule ;
    public final void bottomup() throws RecognitionException {
        try {
            // CollectSymbols.g:115:2: ( finishRule )
            // CollectSymbols.g:115:4: finishRule
            {
            pushFollow(FOLLOW_finishRule_in_bottomup241);
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
    // CollectSymbols.g:118:1: globalScope : {...}? ^( SCOPE ID ACTION ) ;
    public final void globalScope() throws RecognitionException {
        GrammarAST ACTION1=null;
        GrammarAST ID2=null;

        try {
            // CollectSymbols.g:119:2: ({...}? ^( SCOPE ID ACTION ) )
            // CollectSymbols.g:119:4: {...}? ^( SCOPE ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "globalScope", "inContext(\"GRAMMAR\")");
            }
            match(input,SCOPE,FOLLOW_SCOPE_in_globalScope255); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID2=(GrammarAST)match(input,ID,FOLLOW_ID_in_globalScope257); if (state.failed) return ;
            ACTION1=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_globalScope259); if (state.failed) return ;

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
    // CollectSymbols.g:128:1: globalNamedAction : {...}? ^( AT ( ID )? ID ACTION ) ;
    public final void globalNamedAction() throws RecognitionException {
        GrammarAST AT3=null;
        GrammarAST ACTION4=null;

        try {
            // CollectSymbols.g:129:2: ({...}? ^( AT ( ID )? ID ACTION ) )
            // CollectSymbols.g:129:4: {...}? ^( AT ( ID )? ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "globalNamedAction", "inContext(\"GRAMMAR\")");
            }
            AT3=(GrammarAST)match(input,AT,FOLLOW_AT_in_globalNamedAction278); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // CollectSymbols.g:129:33: ( ID )?
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
                    // CollectSymbols.g:129:33: ID
                    {
                    match(input,ID,FOLLOW_ID_in_globalNamedAction280); if (state.failed) return ;

                    }
                    break;

            }

            match(input,ID,FOLLOW_ID_in_globalNamedAction283); if (state.failed) return ;
            ACTION4=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_globalNamedAction285); if (state.failed) return ;

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
    // CollectSymbols.g:133:1: tokensSection : {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) ;
    public final void tokensSection() throws RecognitionException {
        GrammarAST t=null;
        GrammarAST ASSIGN5=null;
        GrammarAST STRING_LITERAL6=null;

        try {
            // CollectSymbols.g:134:2: ({...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) )
            // CollectSymbols.g:134:4: {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokensSection", "inContext(\"TOKENS\")");
            }
            // CollectSymbols.g:135:3: ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
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
                    // CollectSymbols.g:135:5: ^( ASSIGN t= ID STRING_LITERAL )
                    {
                    ASSIGN5=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokensSection308); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection312); if (state.failed) return ;
                    STRING_LITERAL6=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokensSection314); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      terminals.add(t); tokenIDRefs.add(t);
                      			 tokensDefs.add(ASSIGN5); strings.add((STRING_LITERAL6!=null?STRING_LITERAL6.getText():null));
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:138:5: t= ID
                    {
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection328); if (state.failed) return ;
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
    // CollectSymbols.g:143:1: rule : ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ^( BLOCK ( . )+ ) ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST name=null;
        GrammarAST RULE7=null;
        GrammarAST m=null;

        List<GrammarAST> modifiers = new ArrayList<GrammarAST>();
        try {
            // CollectSymbols.g:145:2: ( ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ^( BLOCK ( . )+ ) ( . )* ) )
            // CollectSymbols.g:145:6: ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ^( BLOCK ( . )+ ) ( . )* )
            {
            RULE7=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_rule357); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            name=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule369); if (state.failed) return ;
            // CollectSymbols.g:146:17: ( options {greedy=false; } : . )*
            loop4:
            do {
                int alt4=2;
                alt4 = dfa4.predict(input);
                switch (alt4) {
            	case 1 :
            	    // CollectSymbols.g:146:42: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            // CollectSymbols.g:147:9: ( ^( RULEMODIFIERS (m= . )+ ) )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==RULEMODIFIERS) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // CollectSymbols.g:147:10: ^( RULEMODIFIERS (m= . )+ )
                    {
                    match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_rule393); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    // CollectSymbols.g:147:26: (m= . )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>=SEMPRED && LA5_0<=ALT_REWRITE)) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // CollectSymbols.g:147:27: m= .
                    	    {
                    	    m=(GrammarAST)input.LT(1);
                    	    matchAny(input); if (state.failed) return ;
                    	    if ( state.backtracking==1 ) {
                    	      modifiers.add(m);
                    	    }

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

            match(input,BLOCK,FOLLOW_BLOCK_in_rule416); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // CollectSymbols.g:148:17: ( . )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0>=SEMPRED && LA7_0<=ALT_REWRITE)) ) {
                    alt7=1;
                }
                else if ( (LA7_0==UP) ) {
                    alt7=2;
                }


                switch (alt7) {
            	case 1 :
            	    // CollectSymbols.g:148:17: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            // CollectSymbols.g:149:9: ( . )*
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
            	    // CollectSymbols.g:149:9: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		int numAlts = RULE7.getFirstChildWithType(BLOCK).getChildCount();
              		Rule r = new Rule(g, (name!=null?name.getText():null), (GrammarASTWithOptions)RULE7, numAlts);
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
    // CollectSymbols.g:161:1: setAlt : {...}? ( ALT | ALT_REWRITE ) ;
    public final CollectSymbols.setAlt_return setAlt() throws RecognitionException {
        CollectSymbols.setAlt_return retval = new CollectSymbols.setAlt_return();
        retval.start = input.LT(1);

        try {
            // CollectSymbols.g:162:2: ({...}? ( ALT | ALT_REWRITE ) )
            // CollectSymbols.g:162:4: {...}? ( ALT | ALT_REWRITE )
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
    // CollectSymbols.g:166:1: finishRule : RULE ;
    public final void finishRule() throws RecognitionException {
        try {
            // CollectSymbols.g:167:2: ( RULE )
            // CollectSymbols.g:167:4: RULE
            {
            match(input,RULE,FOLLOW_RULE_in_finishRule484); if (state.failed) return ;
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
    // CollectSymbols.g:170:1: ruleNamedAction : {...}? ^( AT ID ACTION ) ;
    public final void ruleNamedAction() throws RecognitionException {
        GrammarAST ID8=null;
        GrammarAST ACTION9=null;

        try {
            // CollectSymbols.g:171:2: ({...}? ^( AT ID ACTION ) )
            // CollectSymbols.g:171:4: {...}? ^( AT ID ACTION )
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleNamedAction", "inContext(\"RULE\")");
            }
            match(input,AT,FOLLOW_AT_in_ruleNamedAction500); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID8=(GrammarAST)match(input,ID,FOLLOW_ID_in_ruleNamedAction502); if (state.failed) return ;
            ACTION9=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleNamedAction504); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.namedActions.put((ID8!=null?ID8.getText():null),(ActionAST)ACTION9);
              		((ActionAST)ACTION9).resolver = currentRule;
              		
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
    // CollectSymbols.g:178:1: ruleAction : {...}? ACTION ;
    public final void ruleAction() throws RecognitionException {
        GrammarAST ACTION10=null;

        try {
            // CollectSymbols.g:179:2: ({...}? ACTION )
            // CollectSymbols.g:179:4: {...}? ACTION
            {
            if ( !((inContext("RULE ...")&&!inContext("SCOPE")&&
            		 !inContext("CATCH")&&!inContext("FINALLY")&&!inContext("AT"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleAction", "inContext(\"RULE ...\")&&!inContext(\"SCOPE\")&&\n\t\t !inContext(\"CATCH\")&&!inContext(\"FINALLY\")&&!inContext(\"AT\")");
            }
            ACTION10=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleAction524); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.alt[currentAlt].actions.add((ActionAST)ACTION10);
              		((ActionAST)ACTION10).resolver = currentRule.alt[currentAlt];
              		
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
    // CollectSymbols.g:188:1: exceptionHandler : ^( CATCH ARG_ACTION ACTION ) ;
    public final void exceptionHandler() throws RecognitionException {
        GrammarAST ACTION11=null;

        try {
            // CollectSymbols.g:189:2: ( ^( CATCH ARG_ACTION ACTION ) )
            // CollectSymbols.g:189:4: ^( CATCH ARG_ACTION ACTION )
            {
            match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler540); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler542); if (state.failed) return ;
            ACTION11=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler544); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.exceptionActions.add((ActionAST)ACTION11);
              		((ActionAST)ACTION11).resolver = currentRule;
              		
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
    // CollectSymbols.g:196:1: finallyClause : ^( FINALLY ACTION ) ;
    public final void finallyClause() throws RecognitionException {
        GrammarAST ACTION12=null;

        try {
            // CollectSymbols.g:197:2: ( ^( FINALLY ACTION ) )
            // CollectSymbols.g:197:4: ^( FINALLY ACTION )
            {
            match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause561); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ACTION12=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_finallyClause563); if (state.failed) return ;

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
    // $ANTLR end "finallyClause"


    // $ANTLR start "ruleArg"
    // CollectSymbols.g:204:1: ruleArg : {...}? ARG_ACTION ;
    public final void ruleArg() throws RecognitionException {
        GrammarAST ARG_ACTION13=null;

        try {
            // CollectSymbols.g:205:2: ({...}? ARG_ACTION )
            // CollectSymbols.g:205:4: {...}? ARG_ACTION
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleArg", "inContext(\"RULE\")");
            }
            ARG_ACTION13=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleArg583); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.args = ScopeParser.parseTypeList((ARG_ACTION13!=null?ARG_ACTION13.getText():null));
              		currentRule.args.ast = ARG_ACTION13;
              		
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
    // CollectSymbols.g:212:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
    public final void ruleReturns() throws RecognitionException {
        GrammarAST ARG_ACTION14=null;

        try {
            // CollectSymbols.g:213:2: ( ^( RETURNS ARG_ACTION ) )
            // CollectSymbols.g:213:4: ^( RETURNS ARG_ACTION )
            {
            match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns600); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ARG_ACTION14=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns602); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.retvals = ScopeParser.parseTypeList((ARG_ACTION14!=null?ARG_ACTION14.getText():null));
              		currentRule.retvals.ast = ARG_ACTION14;
              		
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
    // CollectSymbols.g:220:1: ruleScopeSpec : {...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) ) ;
    public final void ruleScopeSpec() throws RecognitionException {
        GrammarAST ACTION15=null;
        GrammarAST ids=null;
        List list_ids=null;

        try {
            // CollectSymbols.g:221:2: ({...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) ) )
            // CollectSymbols.g:221:4: {...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) )
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleScopeSpec", "inContext(\"RULE\")");
            }
            // CollectSymbols.g:222:3: ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==SCOPE) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==DOWN) ) {
                    int LA10_2 = input.LA(3);

                    if ( (LA10_2==ACTION) ) {
                        alt10=1;
                    }
                    else if ( (LA10_2==ID) ) {
                        alt10=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // CollectSymbols.g:222:5: ^( SCOPE ACTION )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec625); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    ACTION15=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec627); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {

                      			currentRule.scope = ScopeParser.parseDynamicScope((ACTION15!=null?ACTION15.getText():null));
                      			currentRule.scope.name = currentRule.name;
                      			currentRule.scope.ast = ACTION15;
                      			
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:228:5: ^( SCOPE (ids+= ID )+ )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec640); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    // CollectSymbols.g:228:16: (ids+= ID )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0==ID) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // CollectSymbols.g:228:16: ids+= ID
                    	    {
                    	    ids=(GrammarAST)match(input,ID,FOLLOW_ID_in_ruleScopeSpec644); if (state.failed) return ;
                    	    if (list_ids==null) list_ids=new ArrayList();
                    	    list_ids.add(ids);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
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
    // CollectSymbols.g:232:1: rewriteElement : {...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL ) ;
    public final CollectSymbols.rewriteElement_return rewriteElement() throws RecognitionException {
        CollectSymbols.rewriteElement_return retval = new CollectSymbols.rewriteElement_return();
        retval.start = input.LT(1);

        try {
            // CollectSymbols.g:234:2: ({...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL ) )
            // CollectSymbols.g:235:6: {...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL )
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
    // CollectSymbols.g:239:1: labeledElement : {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) ;
    public final CollectSymbols.labeledElement_return labeledElement() throws RecognitionException {
        CollectSymbols.labeledElement_return retval = new CollectSymbols.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST id=null;
        GrammarAST e=null;

        try {
            // CollectSymbols.g:245:2: ({...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) )
            // CollectSymbols.g:245:4: {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            {
            if ( !((inContext("RULE ..."))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "labeledElement", "inContext(\"RULE ...\")");
            }
            // CollectSymbols.g:246:3: ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ASSIGN) ) {
                alt11=1;
            }
            else if ( (LA11_0==PLUS_ASSIGN) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // CollectSymbols.g:246:5: ^( ASSIGN id= ID e= . )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement708); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement712); if (state.failed) return retval;
                    e=(GrammarAST)input.LT(1);
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:247:5: ^( PLUS_ASSIGN id= ID e= . )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement724); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement728); if (state.failed) return retval;
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
    // CollectSymbols.g:251:1: terminal : ({...}? STRING_LITERAL | TOKEN_REF );
    public final CollectSymbols.terminal_return terminal() throws RecognitionException {
        CollectSymbols.terminal_return retval = new CollectSymbols.terminal_return();
        retval.start = input.LT(1);

        GrammarAST STRING_LITERAL16=null;
        GrammarAST TOKEN_REF17=null;

        try {
            // CollectSymbols.g:252:5: ({...}? STRING_LITERAL | TOKEN_REF )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==STRING_LITERAL) ) {
                alt12=1;
            }
            else if ( (LA12_0==TOKEN_REF) ) {
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
                    // CollectSymbols.g:252:7: {...}? STRING_LITERAL
                    {
                    if ( !((!inContext("TOKENS ASSIGN"))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "terminal", "!inContext(\"TOKENS ASSIGN\")");
                    }
                    STRING_LITERAL16=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal754); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {

                          	terminals.add(((GrammarAST)retval.start));
                          	strings.add((STRING_LITERAL16!=null?STRING_LITERAL16.getText():null));
                          	if ( currentRule!=null ) {
                          		currentRule.alt[currentAlt].tokenRefs.map((STRING_LITERAL16!=null?STRING_LITERAL16.getText():null), STRING_LITERAL16);
                          	}
                          	
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:260:7: TOKEN_REF
                    {
                    TOKEN_REF17=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal769); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {

                          	terminals.add(TOKEN_REF17);
                          	tokenIDRefs.add(TOKEN_REF17);
                          	if ( currentRule!=null ) {
                          		currentRule.alt[currentAlt].tokenRefs.map((TOKEN_REF17!=null?TOKEN_REF17.getText():null), TOKEN_REF17);
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
    // CollectSymbols.g:270:1: ruleref : ({...}?r= RULE_REF | r= RULE_REF ) ;
    public final void ruleref() throws RecognitionException {
        GrammarAST r=null;

        try {
            // CollectSymbols.g:272:5: ( ({...}?r= RULE_REF | r= RULE_REF ) )
            // CollectSymbols.g:272:7: ({...}?r= RULE_REF | r= RULE_REF )
            {
            // CollectSymbols.g:272:7: ({...}?r= RULE_REF | r= RULE_REF )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_REF) ) {
                int LA13_1 = input.LA(2);

                if ( ((inContext("DOT ..."))) ) {
                    alt13=1;
                }
                else if ( (true) ) {
                    alt13=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // CollectSymbols.g:272:9: {...}?r= RULE_REF
                    {
                    if ( !((inContext("DOT ..."))) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "ruleref", "inContext(\"DOT ...\")");
                    }
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref806); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      qualifiedRulerefs.add((GrammarAST)r.getParent());
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:274:8: r= RULE_REF
                    {
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref819); if (state.failed) return ;

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
    protected DFA4 dfa4 = new DFA4(this);
    static final String DFA1_eotS =
        "\41\uffff";
    static final String DFA1_eofS =
        "\41\uffff";
    static final String DFA1_minS =
        "\1\16\3\2\4\uffff\3\0\6\uffff\1\20\2\126\2\uffff\1\3\1\uffff\1\20"+
        "\1\4\1\uffff\1\3\1\uffff\1\2\2\0\1\uffff";
    static final String DFA1_maxS =
        "\1\145\3\2\4\uffff\3\0\6\uffff\3\126\2\uffff\1\126\1\uffff\1\126"+
        "\1\145\1\uffff\1\3\1\uffff\1\3\2\0\1\uffff";
    static final String DFA1_acceptS =
        "\4\uffff\1\3\1\4\1\5\1\6\3\uffff\1\12\1\14\1\15\1\16\1\17\1\20\3"+
        "\uffff\1\11\1\13\1\uffff\1\10\2\uffff\1\1\1\uffff\1\2\3\uffff\1"+
        "\7";
    static final String DFA1_specialS =
        "\10\uffff\1\4\1\2\1\0\23\uffff\1\1\1\3\1\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\6\1\uffff\1\16\4\uffff\1\1\11\uffff\1\7\1\uffff\1\20\1\17"+
            "\12\uffff\1\3\4\uffff\1\14\10\uffff\1\2\2\uffff\1\12\1\10\3"+
            "\uffff\1\11\4\uffff\1\5\13\uffff\1\15\1\uffff\1\4\5\uffff\1"+
            "\13\10\uffff\1\15",
            "\1\21",
            "\1\22",
            "\1\23",
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
            "\1\27\105\uffff\1\26",
            "\1\30",
            "\1\31",
            "",
            "",
            "\1\27\14\uffff\1\32\105\uffff\1\27",
            "",
            "\1\33\105\uffff\1\34",
            "\77\14\1\35\42\14",
            "",
            "\1\36",
            "",
            "\1\14\1\37",
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
            return "93:1: topdown : ( globalScope | globalNamedAction | tokensSection | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | finallyClause | exceptionHandler );";
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
                        if ( ((inContext("RESULT ..."))) ) {s = 11;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index1_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA1_30 = input.LA(1);

                         
                        int index1_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("GRAMMAR"))) ) {s = 28;}

                        else if ( ((inContext("RULE"))) ) {s = 32;}

                         
                        input.seek(index1_30);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA1_9 = input.LA(1);

                         
                        int index1_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("RESULT ..."))) ) {s = 11;}

                        else if ( ((!inContext("TOKENS ASSIGN"))) ) {s = 21;}

                         
                        input.seek(index1_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA1_31 = input.LA(1);

                         
                        int index1_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("TOKENS"))) ) {s = 4;}

                        else if ( ((inContext("RULE ..."))) ) {s = 12;}

                         
                        input.seek(index1_31);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA1_8 = input.LA(1);

                         
                        int index1_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(((inContext("RESULT ..."))))) ) {s = 20;}

                        else if ( ((inContext("RESULT ..."))) ) {s = 11;}

                         
                        input.seek(index1_8);
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
    static final String DFA4_eotS =
        "\26\uffff";
    static final String DFA4_eofS =
        "\26\uffff";
    static final String DFA4_minS =
        "\1\4\2\2\1\uffff\2\4\2\2\1\uffff\1\3\3\2\3\4\2\2\2\3\1\uffff\1\3";
    static final String DFA4_maxS =
        "\3\145\1\uffff\4\145\1\uffff\13\145\1\uffff\1\145";
    static final String DFA4_acceptS =
        "\3\uffff\1\1\4\uffff\1\2\13\uffff\1\2\1\uffff";
    static final String DFA4_specialS =
        "\26\uffff}>";
    static final String[] DFA4_transitionS = {
            "\106\3\1\1\1\3\1\2\31\3",
            "\1\4\1\uffff\142\3",
            "\1\5\1\uffff\142\3",
            "",
            "\142\6",
            "\142\7",
            "\2\10\142\6",
            "\1\10\1\11\142\7",
            "",
            "\1\10\106\14\1\12\1\14\1\13\31\14",
            "\1\15\1\10\106\14\1\12\1\14\1\13\31\14",
            "\1\16\1\10\106\14\1\12\1\14\1\13\31\14",
            "\1\17\1\10\106\14\1\12\1\14\1\13\31\14",
            "\142\20",
            "\142\21",
            "\142\22",
            "\1\3\1\23\142\20",
            "\1\3\1\24\142\21",
            "\1\25\142\22",
            "\1\24\106\14\1\12\1\14\1\13\31\14",
            "",
            "\1\24\106\14\1\12\1\14\1\13\31\14"
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "()* loopback of 146:17: ( options {greedy=false; } : . )*";
        }
    }
 

    public static final BitSet FOLLOW_globalScope_in_topdown97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_globalNamedAction_in_topdown105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSection_in_topdown113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_topdown121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArg_in_topdown129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturns_in_topdown137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNamedAction_in_topdown145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_topdown153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_topdown161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteElement_in_topdown169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_topdown190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_labeledElement_in_topdown198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_setAlt_in_topdown206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_topdown214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finallyClause_in_topdown222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exceptionHandler_in_topdown230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finishRule_in_bottomup241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_globalScope255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_globalScope257 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_globalScope259 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_globalNamedAction278 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_globalNamedAction280 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_globalNamedAction283 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_globalNamedAction285 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_tokensSection308 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokensSection312 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokensSection314 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_tokensSection328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule357 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule369 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_RULEMODIFIERS_in_rule393 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BLOCK_in_rule416 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_setAlt460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_finishRule484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_ruleNamedAction500 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleNamedAction502 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleNamedAction504 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler540 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler542 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler544 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause561 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause563 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleArg583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns600 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns602 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec625 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec627 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec640 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec644 = new BitSet(new long[]{0x0000000000000008L,0x0000000000400000L});
    public static final BitSet FOLLOW_set_in_rewriteElement672 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement708 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement712 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement724 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement728 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref819 = new BitSet(new long[]{0x0000000000000002L});

}