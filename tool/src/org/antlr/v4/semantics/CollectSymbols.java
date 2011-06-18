// $ANTLR 3.3 Nov 30, 2010 12:50:56 CollectSymbols.g 2011-06-11 10:28:59

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
import org.antlr.runtime.BitSet;
import org.antlr.runtime.tree.*;
import org.antlr.v4.parse.ScopeParser;
import org.antlr.v4.tool.*;

import java.util.*;
/** Collects rules, terminals, strings, actions, scopes etc... from AST
 *  No side-effects
 */
public class CollectSymbols extends TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "MODE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "RESULT", "ALT_REWRITE"
    };
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int FORCED_ACTION=5;
    public static final int DOC_COMMENT=6;
    public static final int SRC=7;
    public static final int NLCHARS=8;
    public static final int COMMENT=9;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=10;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int ACTION_STRING_LITERAL=12;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int ARG_ACTION=14;
    public static final int NESTED_ACTION=15;
    public static final int ACTION=16;
    public static final int ACTION_ESC=17;
    public static final int WSNLCHARS=18;
    public static final int OPTIONS=19;
    public static final int TOKENS=20;
    public static final int SCOPE=21;
    public static final int IMPORT=22;
    public static final int FRAGMENT=23;
    public static final int LEXER=24;
    public static final int PARSER=25;
    public static final int TREE=26;
    public static final int GRAMMAR=27;
    public static final int PROTECTED=28;
    public static final int PUBLIC=29;
    public static final int PRIVATE=30;
    public static final int RETURNS=31;
    public static final int THROWS=32;
    public static final int CATCH=33;
    public static final int FINALLY=34;
    public static final int TEMPLATE=35;
    public static final int MODE=36;
    public static final int COLON=37;
    public static final int COLONCOLON=38;
    public static final int COMMA=39;
    public static final int SEMI=40;
    public static final int LPAREN=41;
    public static final int RPAREN=42;
    public static final int IMPLIES=43;
    public static final int LT=44;
    public static final int GT=45;
    public static final int ASSIGN=46;
    public static final int QUESTION=47;
    public static final int BANG=48;
    public static final int STAR=49;
    public static final int PLUS=50;
    public static final int PLUS_ASSIGN=51;
    public static final int OR=52;
    public static final int ROOT=53;
    public static final int DOLLAR=54;
    public static final int DOT=55;
    public static final int RANGE=56;
    public static final int ETC=57;
    public static final int RARROW=58;
    public static final int TREE_BEGIN=59;
    public static final int AT=60;
    public static final int NOT=61;
    public static final int RBRACE=62;
    public static final int TOKEN_REF=63;
    public static final int RULE_REF=64;
    public static final int INT=65;
    public static final int WSCHARS=66;
    public static final int ESC_SEQ=67;
    public static final int STRING_LITERAL=68;
    public static final int HEX_DIGIT=69;
    public static final int UNICODE_ESC=70;
    public static final int WS=71;
    public static final int ERRCHAR=72;
    public static final int RULE=73;
    public static final int RULES=74;
    public static final int RULEMODIFIERS=75;
    public static final int RULEACTIONS=76;
    public static final int BLOCK=77;
    public static final int REWRITE_BLOCK=78;
    public static final int OPTIONAL=79;
    public static final int CLOSURE=80;
    public static final int POSITIVE_CLOSURE=81;
    public static final int SYNPRED=82;
    public static final int CHAR_RANGE=83;
    public static final int EPSILON=84;
    public static final int ALT=85;
    public static final int ALTLIST=86;
    public static final int ID=87;
    public static final int ARG=88;
    public static final int ARGLIST=89;
    public static final int RET=90;
    public static final int COMBINED=91;
    public static final int INITACTION=92;
    public static final int LABEL=93;
    public static final int GATED_SEMPRED=94;
    public static final int SYN_SEMPRED=95;
    public static final int BACKTRACK_SEMPRED=96;
    public static final int WILDCARD=97;
    public static final int LIST=98;
    public static final int ELEMENT_OPTIONS=99;
    public static final int ST_RESULT=100;
    public static final int RESULT=101;
    public static final int ALT_REWRITE=102;

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
    // CollectSymbols.g:94:1: topdown : ( globalScope | globalNamedAction | tokensSection | mode | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | sempred | finallyClause | exceptionHandler );
    public final void topdown() throws RecognitionException {
        try {
            // CollectSymbols.g:96:5: ( globalScope | globalNamedAction | tokensSection | mode | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | sempred | finallyClause | exceptionHandler )
            int alt1=18;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // CollectSymbols.g:96:7: globalScope
                    {
                    pushFollow(FOLLOW_globalScope_in_topdown89);
                    globalScope();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:97:7: globalNamedAction
                    {
                    pushFollow(FOLLOW_globalNamedAction_in_topdown97);
                    globalNamedAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // CollectSymbols.g:98:7: tokensSection
                    {
                    pushFollow(FOLLOW_tokensSection_in_topdown105);
                    tokensSection();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // CollectSymbols.g:99:7: mode
                    {
                    pushFollow(FOLLOW_mode_in_topdown113);
                    mode();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // CollectSymbols.g:100:7: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown121);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // CollectSymbols.g:101:7: ruleArg
                    {
                    pushFollow(FOLLOW_ruleArg_in_topdown129);
                    ruleArg();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // CollectSymbols.g:102:7: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_topdown137);
                    ruleReturns();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // CollectSymbols.g:103:7: ruleNamedAction
                    {
                    pushFollow(FOLLOW_ruleNamedAction_in_topdown145);
                    ruleNamedAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // CollectSymbols.g:104:7: ruleScopeSpec
                    {
                    pushFollow(FOLLOW_ruleScopeSpec_in_topdown153);
                    ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 10 :
                    // CollectSymbols.g:105:7: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown161);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 11 :
                    // CollectSymbols.g:106:7: rewriteElement
                    {
                    pushFollow(FOLLOW_rewriteElement_in_topdown169);
                    rewriteElement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 12 :
                    // CollectSymbols.g:108:7: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_topdown190);
                    terminal();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 13 :
                    // CollectSymbols.g:109:7: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_topdown198);
                    labeledElement();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 14 :
                    // CollectSymbols.g:110:7: setAlt
                    {
                    pushFollow(FOLLOW_setAlt_in_topdown206);
                    setAlt();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 15 :
                    // CollectSymbols.g:111:7: ruleAction
                    {
                    pushFollow(FOLLOW_ruleAction_in_topdown214);
                    ruleAction();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 16 :
                    // CollectSymbols.g:112:9: sempred
                    {
                    pushFollow(FOLLOW_sempred_in_topdown224);
                    sempred();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 17 :
                    // CollectSymbols.g:113:7: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_topdown232);
                    finallyClause();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 18 :
                    // CollectSymbols.g:114:7: exceptionHandler
                    {
                    pushFollow(FOLLOW_exceptionHandler_in_topdown240);
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
    // CollectSymbols.g:117:1: bottomup : finishRule ;
    public final void bottomup() throws RecognitionException {
        try {
            // CollectSymbols.g:118:2: ( finishRule )
            // CollectSymbols.g:118:4: finishRule
            {
            pushFollow(FOLLOW_finishRule_in_bottomup251);
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
    // CollectSymbols.g:121:1: globalScope : {...}? ^( SCOPE ID ACTION ) ;
    public final void globalScope() throws RecognitionException {
        GrammarAST ACTION1=null;
        GrammarAST ID2=null;

        try {
            // CollectSymbols.g:122:2: ({...}? ^( SCOPE ID ACTION ) )
            // CollectSymbols.g:122:4: {...}? ^( SCOPE ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "globalScope", "inContext(\"GRAMMAR\")");
            }
            match(input,SCOPE,FOLLOW_SCOPE_in_globalScope265); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID2=(GrammarAST)match(input,ID,FOLLOW_ID_in_globalScope267); if (state.failed) return ;
            ACTION1=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_globalScope269); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		AttributeDict s = ScopeParser.parseDynamicScope((ACTION1!=null?ACTION1.getText():null));
              		s.type = AttributeDict.DictType.GLOBAL_SCOPE;
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
    // CollectSymbols.g:132:1: globalNamedAction : {...}? ^( AT ( ID )? ID ACTION ) ;
    public final void globalNamedAction() throws RecognitionException {
        GrammarAST AT3=null;
        GrammarAST ACTION4=null;

        try {
            // CollectSymbols.g:133:2: ({...}? ^( AT ( ID )? ID ACTION ) )
            // CollectSymbols.g:133:4: {...}? ^( AT ( ID )? ID ACTION )
            {
            if ( !((inContext("GRAMMAR"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "globalNamedAction", "inContext(\"GRAMMAR\")");
            }
            AT3=(GrammarAST)match(input,AT,FOLLOW_AT_in_globalNamedAction288); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // CollectSymbols.g:133:33: ( ID )?
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
                    // CollectSymbols.g:133:33: ID
                    {
                    match(input,ID,FOLLOW_ID_in_globalNamedAction290); if (state.failed) return ;

                    }
                    break;

            }

            match(input,ID,FOLLOW_ID_in_globalNamedAction293); if (state.failed) return ;
            ACTION4=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_globalNamedAction295); if (state.failed) return ;

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
    // CollectSymbols.g:137:1: tokensSection : {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) ;
    public final void tokensSection() throws RecognitionException {
        GrammarAST t=null;
        GrammarAST ASSIGN5=null;
        GrammarAST STRING_LITERAL6=null;

        try {
            // CollectSymbols.g:138:2: ({...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID ) )
            // CollectSymbols.g:138:4: {...}? ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokensSection", "inContext(\"TOKENS\")");
            }
            // CollectSymbols.g:139:3: ( ^( ASSIGN t= ID STRING_LITERAL ) | t= ID )
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
                    // CollectSymbols.g:139:5: ^( ASSIGN t= ID STRING_LITERAL )
                    {
                    ASSIGN5=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokensSection318); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection322); if (state.failed) return ;
                    STRING_LITERAL6=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokensSection324); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      terminals.add(t); tokenIDRefs.add(t);
                      			 tokensDefs.add(ASSIGN5); strings.add((STRING_LITERAL6!=null?STRING_LITERAL6.getText():null));
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:142:5: t= ID
                    {
                    t=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokensSection338); if (state.failed) return ;
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
    // CollectSymbols.g:147:1: mode : ^( MODE ID ( . )+ ) ;
    public final void mode() throws RecognitionException {
        GrammarAST ID7=null;

        try {
            // CollectSymbols.g:147:5: ( ^( MODE ID ( . )+ ) )
            // CollectSymbols.g:147:7: ^( MODE ID ( . )+ )
            {
            match(input,MODE,FOLLOW_MODE_in_mode357); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID7=(GrammarAST)match(input,ID,FOLLOW_ID_in_mode359); if (state.failed) return ;
            // CollectSymbols.g:147:17: ( . )+
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
            	    // CollectSymbols.g:147:17: .
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
    // CollectSymbols.g:149:1: rule : ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ( ^( AT ID ACTION ) )* ^( BLOCK ( . )+ ) ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST name=null;
        GrammarAST RULE8=null;
        GrammarAST m=null;

        List<GrammarAST> modifiers = new ArrayList<GrammarAST>();
        try {
            // CollectSymbols.g:151:2: ( ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ( ^( AT ID ACTION ) )* ^( BLOCK ( . )+ ) ( . )* ) )
            // CollectSymbols.g:151:6: ^( RULE name= ID ( options {greedy=false; } : . )* ( ^( RULEMODIFIERS (m= . )+ ) )? ( ^( AT ID ACTION ) )* ^( BLOCK ( . )+ ) ( . )* )
            {
            RULE8=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_rule384); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            name=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule396); if (state.failed) return ;
            // CollectSymbols.g:152:17: ( options {greedy=false; } : . )*
            loop5:
            do {
                int alt5=2;
                alt5 = dfa5.predict(input);
                switch (alt5) {
            	case 1 :
            	    // CollectSymbols.g:152:42: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // CollectSymbols.g:153:9: ( ^( RULEMODIFIERS (m= . )+ ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULEMODIFIERS) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // CollectSymbols.g:153:10: ^( RULEMODIFIERS (m= . )+ )
                    {
                    match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_rule420); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    // CollectSymbols.g:153:26: (m= . )+
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
                    	    // CollectSymbols.g:153:27: m= .
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

            // CollectSymbols.g:154:9: ( ^( AT ID ACTION ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==AT) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // CollectSymbols.g:154:10: ^( AT ID ACTION )
            	    {
            	    match(input,AT,FOLLOW_AT_in_rule444); if (state.failed) return ;

            	    match(input, Token.DOWN, null); if (state.failed) return ;
            	    match(input,ID,FOLLOW_ID_in_rule446); if (state.failed) return ;
            	    match(input,ACTION,FOLLOW_ACTION_in_rule448); if (state.failed) return ;

            	    match(input, Token.UP, null); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            match(input,BLOCK,FOLLOW_BLOCK_in_rule462); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // CollectSymbols.g:155:17: ( . )+
            int cnt9=0;
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
            	    // CollectSymbols.g:155:17: .
            	    {
            	    matchAny(input); if (state.failed) return ;

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
            // CollectSymbols.g:156:9: ( . )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( ((LA10_0>=SEMPRED && LA10_0<=ALT_REWRITE)) ) {
                    alt10=1;
                }
                else if ( (LA10_0==UP) ) {
                    alt10=2;
                }


                switch (alt10) {
            	case 1 :
            	    // CollectSymbols.g:156:9: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		int numAlts = RULE8.getFirstChildWithType(BLOCK).getChildCount();
              		Rule r = new Rule(g, (name!=null?name.getText():null), (RuleAST)RULE8, numAlts);
              		if ( g.isLexer() ) r.mode = currentMode;
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
    // CollectSymbols.g:169:1: setAlt : {...}? ( ALT | ALT_REWRITE ) ;
    public final CollectSymbols.setAlt_return setAlt() throws RecognitionException {
        CollectSymbols.setAlt_return retval = new CollectSymbols.setAlt_return();
        retval.start = input.LT(1);

        try {
            // CollectSymbols.g:170:2: ({...}? ( ALT | ALT_REWRITE ) )
            // CollectSymbols.g:170:4: {...}? ( ALT | ALT_REWRITE )
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
              		currentRule.alt[currentAlt].ast = (AltAST)((GrammarAST)retval.start);

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
    // CollectSymbols.g:177:1: finishRule : RULE ;
    public final void finishRule() throws RecognitionException {
        try {
            // CollectSymbols.g:178:2: ( RULE )
            // CollectSymbols.g:178:4: RULE
            {
            match(input,RULE,FOLLOW_RULE_in_finishRule528); if (state.failed) return ;
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
    // CollectSymbols.g:181:1: ruleNamedAction : {...}? ^( AT ID ACTION ) ;
    public final void ruleNamedAction() throws RecognitionException {
        GrammarAST ID9=null;
        GrammarAST ACTION10=null;

        try {
            // CollectSymbols.g:182:2: ({...}? ^( AT ID ACTION ) )
            // CollectSymbols.g:182:4: {...}? ^( AT ID ACTION )
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleNamedAction", "inContext(\"RULE\")");
            }
            match(input,AT,FOLLOW_AT_in_ruleNamedAction544); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID9=(GrammarAST)match(input,ID,FOLLOW_ID_in_ruleNamedAction546); if (state.failed) return ;
            ACTION10=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleNamedAction548); if (state.failed) return ;

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
    // CollectSymbols.g:189:1: ruleAction : ({...}? ACTION | FORCED_ACTION );
    public final void ruleAction() throws RecognitionException {
        GrammarAST ACTION11=null;
        GrammarAST FORCED_ACTION12=null;

        try {
            // CollectSymbols.g:190:2: ({...}? ACTION | FORCED_ACTION )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==ACTION) ) {
                alt11=1;
            }
            else if ( (LA11_0==FORCED_ACTION) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // CollectSymbols.g:190:4: {...}? ACTION
                    {
                    if ( !((inContext("RULE ...")&&!inContext("SCOPE")&&
                    		 !inContext("CATCH")&&!inContext("FINALLY")&&!inContext("AT"))) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "ruleAction", "inContext(\"RULE ...\")&&!inContext(\"SCOPE\")&&\n\t\t !inContext(\"CATCH\")&&!inContext(\"FINALLY\")&&!inContext(\"AT\")");
                    }
                    ACTION11=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleAction568); if (state.failed) return ;
                    if ( state.backtracking==1 ) {

                      		currentRule.defineActionInAlt(currentAlt, (ActionAST)ACTION11);
                      		((ActionAST)ACTION11).resolver = currentRule.alt[currentAlt];

                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:197:9: FORCED_ACTION
                    {
                    FORCED_ACTION12=(GrammarAST)match(input,FORCED_ACTION,FOLLOW_FORCED_ACTION_in_ruleAction582); if (state.failed) return ;
                    if ( state.backtracking==1 ) {

                      		currentRule.defineActionInAlt(currentAlt, (ActionAST)FORCED_ACTION12);
                      		((ActionAST)FORCED_ACTION12).resolver = currentRule.alt[currentAlt];

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
        return ;
    }
    // $ANTLR end "ruleAction"


    // $ANTLR start "sempred"
    // CollectSymbols.g:204:1: sempred : {...}? SEMPRED ;
    public final void sempred() throws RecognitionException {
        GrammarAST SEMPRED13=null;

        try {
            // CollectSymbols.g:205:2: ({...}? SEMPRED )
            // CollectSymbols.g:205:4: {...}? SEMPRED
            {
            if ( !((inContext("RULE ..."))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "sempred", "inContext(\"RULE ...\")");
            }
            SEMPRED13=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_sempred601); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.definePredicateInAlt(currentAlt, (PredAST)SEMPRED13);
              		((PredAST)SEMPRED13).resolver = currentRule.alt[currentAlt];

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
    // $ANTLR end "sempred"


    // $ANTLR start "exceptionHandler"
    // CollectSymbols.g:213:1: exceptionHandler : ^( CATCH ARG_ACTION ACTION ) ;
    public final void exceptionHandler() throws RecognitionException {
        GrammarAST ACTION14=null;

        try {
            // CollectSymbols.g:214:2: ( ^( CATCH ARG_ACTION ACTION ) )
            // CollectSymbols.g:214:4: ^( CATCH ARG_ACTION ACTION )
            {
            match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler617); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler619); if (state.failed) return ;
            ACTION14=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler621); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.exceptionActions.add((ActionAST)ACTION14);
              		((ActionAST)ACTION14).resolver = currentRule;

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
    // CollectSymbols.g:221:1: finallyClause : ^( FINALLY ACTION ) ;
    public final void finallyClause() throws RecognitionException {
        GrammarAST ACTION15=null;

        try {
            // CollectSymbols.g:222:2: ( ^( FINALLY ACTION ) )
            // CollectSymbols.g:222:4: ^( FINALLY ACTION )
            {
            match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause638); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ACTION15=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_finallyClause640); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.finallyAction = (ActionAST)ACTION15;
              		((ActionAST)ACTION15).resolver = currentRule;

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
    // CollectSymbols.g:229:1: ruleArg : {...}? ARG_ACTION ;
    public final void ruleArg() throws RecognitionException {
        GrammarAST ARG_ACTION16=null;

        try {
            // CollectSymbols.g:230:2: ({...}? ARG_ACTION )
            // CollectSymbols.g:230:4: {...}? ARG_ACTION
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleArg", "inContext(\"RULE\")");
            }
            ARG_ACTION16=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleArg658); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.args = ScopeParser.parseTypeList((ARG_ACTION16!=null?ARG_ACTION16.getText():null));
              		currentRule.args.type = AttributeDict.DictType.ARG;
              		currentRule.args.ast = ARG_ACTION16;

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
    // CollectSymbols.g:238:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
    public final void ruleReturns() throws RecognitionException {
        GrammarAST ARG_ACTION17=null;

        try {
            // CollectSymbols.g:239:2: ( ^( RETURNS ARG_ACTION ) )
            // CollectSymbols.g:239:4: ^( RETURNS ARG_ACTION )
            {
            match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns674); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ARG_ACTION17=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns676); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              		currentRule.retvals = ScopeParser.parseTypeList((ARG_ACTION17!=null?ARG_ACTION17.getText():null));
              		currentRule.retvals.type = AttributeDict.DictType.RET;
              		currentRule.retvals.ast = ARG_ACTION17;

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
    // CollectSymbols.g:247:1: ruleScopeSpec : {...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) ) ;
    public final void ruleScopeSpec() throws RecognitionException {
        GrammarAST ACTION18=null;
        GrammarAST ids=null;
        List list_ids=null;

        try {
            // CollectSymbols.g:248:2: ({...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) ) )
            // CollectSymbols.g:248:4: {...}? ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) )
            {
            if ( !((inContext("RULE"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleScopeSpec", "inContext(\"RULE\")");
            }
            // CollectSymbols.g:249:3: ( ^( SCOPE ACTION ) | ^( SCOPE (ids+= ID )+ ) )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==SCOPE) ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1==DOWN) ) {
                    int LA13_2 = input.LA(3);

                    if ( (LA13_2==ACTION) ) {
                        alt13=1;
                    }
                    else if ( (LA13_2==ID) ) {
                        alt13=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 2, input);

                        throw nvae;
                    }
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
                    // CollectSymbols.g:249:5: ^( SCOPE ACTION )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec699); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    ACTION18=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec701); if (state.failed) return ;

                    match(input, Token.UP, null); if (state.failed) return ;
                    if ( state.backtracking==1 ) {

                      			currentRule.scope = ScopeParser.parseDynamicScope((ACTION18!=null?ACTION18.getText():null));
                      			currentRule.scope.type = AttributeDict.DictType.RULE_SCOPE;
                      			currentRule.scope.name = currentRule.name;
                      			currentRule.scope.ast = ACTION18;

                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:256:5: ^( SCOPE (ids+= ID )+ )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec714); if (state.failed) return ;

                    match(input, Token.DOWN, null); if (state.failed) return ;
                    // CollectSymbols.g:256:16: (ids+= ID )+
                    int cnt12=0;
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( (LA12_0==ID) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // CollectSymbols.g:256:16: ids+= ID
                    	    {
                    	    ids=(GrammarAST)match(input,ID,FOLLOW_ID_in_ruleScopeSpec718); if (state.failed) return ;
                    	    if (list_ids==null) list_ids=new ArrayList();
                    	    list_ids.add(ids);


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt12 >= 1 ) break loop12;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
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
    // CollectSymbols.g:260:1: rewriteElement : {...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL ) ;
    public final CollectSymbols.rewriteElement_return rewriteElement() throws RecognitionException {
        CollectSymbols.rewriteElement_return retval = new CollectSymbols.rewriteElement_return();
        retval.start = input.LT(1);

        try {
            // CollectSymbols.g:262:2: ({...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL ) )
            // CollectSymbols.g:263:6: {...}? ( TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL )
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
    // CollectSymbols.g:267:1: labeledElement : {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) ;
    public final CollectSymbols.labeledElement_return labeledElement() throws RecognitionException {
        CollectSymbols.labeledElement_return retval = new CollectSymbols.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST id=null;
        GrammarAST e=null;

        try {
            // CollectSymbols.g:273:2: ({...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) ) )
            // CollectSymbols.g:273:4: {...}? ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            {
            if ( !((inContext("RULE ..."))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "labeledElement", "inContext(\"RULE ...\")");
            }
            // CollectSymbols.g:274:3: ( ^( ASSIGN id= ID e= . ) | ^( PLUS_ASSIGN id= ID e= . ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==ASSIGN) ) {
                alt14=1;
            }
            else if ( (LA14_0==PLUS_ASSIGN) ) {
                alt14=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // CollectSymbols.g:274:5: ^( ASSIGN id= ID e= . )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement780); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement784); if (state.failed) return retval;
                    e=(GrammarAST)input.LT(1);
                    matchAny(input); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:275:5: ^( PLUS_ASSIGN id= ID e= . )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement796); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    id=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement800); if (state.failed) return retval;
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
    // CollectSymbols.g:279:1: terminal : ({...}? STRING_LITERAL | TOKEN_REF );
    public final CollectSymbols.terminal_return terminal() throws RecognitionException {
        CollectSymbols.terminal_return retval = new CollectSymbols.terminal_return();
        retval.start = input.LT(1);

        GrammarAST STRING_LITERAL19=null;
        GrammarAST TOKEN_REF20=null;

        try {
            // CollectSymbols.g:280:5: ({...}? STRING_LITERAL | TOKEN_REF )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==STRING_LITERAL) ) {
                alt15=1;
            }
            else if ( (LA15_0==TOKEN_REF) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // CollectSymbols.g:280:7: {...}? STRING_LITERAL
                    {
                    if ( !((!inContext("TOKENS ASSIGN"))) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "terminal", "!inContext(\"TOKENS ASSIGN\")");
                    }
                    STRING_LITERAL19=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal825); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {

                          	terminals.add(((GrammarAST)retval.start));
                          	strings.add((STRING_LITERAL19!=null?STRING_LITERAL19.getText():null));
                          	if ( currentRule!=null ) {
                          		currentRule.alt[currentAlt].tokenRefs.map((STRING_LITERAL19!=null?STRING_LITERAL19.getText():null), (TerminalAST)STRING_LITERAL19);
                          	}

                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:288:7: TOKEN_REF
                    {
                    TOKEN_REF20=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal840); if (state.failed) return retval;
                    if ( state.backtracking==1 ) {

                          	terminals.add(TOKEN_REF20);
                          	tokenIDRefs.add(TOKEN_REF20);
                          	if ( currentRule!=null ) {
                          		currentRule.alt[currentAlt].tokenRefs.map((TOKEN_REF20!=null?TOKEN_REF20.getText():null), (TerminalAST)TOKEN_REF20);
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
    // CollectSymbols.g:298:1: ruleref : ({...}?r= RULE_REF | r= RULE_REF ) ;
    public final void ruleref() throws RecognitionException {
        GrammarAST r=null;

        try {
            // CollectSymbols.g:300:5: ( ({...}?r= RULE_REF | r= RULE_REF ) )
            // CollectSymbols.g:300:7: ({...}?r= RULE_REF | r= RULE_REF )
            {
            // CollectSymbols.g:300:7: ({...}?r= RULE_REF | r= RULE_REF )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_REF) ) {
                int LA16_1 = input.LA(2);

                if ( ((inContext("DOT ..."))) ) {
                    alt16=1;
                }
                else if ( (true) ) {
                    alt16=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 16, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // CollectSymbols.g:300:9: {...}?r= RULE_REF
                    {
                    if ( !((inContext("DOT ..."))) ) {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        throw new FailedPredicateException(input, "ruleref", "inContext(\"DOT ...\")");
                    }
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref877); if (state.failed) return ;
                    if ( state.backtracking==1 ) {
                      qualifiedRulerefs.add((GrammarAST)r.getParent());
                    }

                    }
                    break;
                case 2 :
                    // CollectSymbols.g:302:8: r= RULE_REF
                    {
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref890); if (state.failed) return ;

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
        "\43\uffff";
    static final String DFA1_eofS =
        "\43\uffff";
    static final String DFA1_minS =
        "\1\4\3\2\5\uffff\3\0\7\uffff\1\20\2\127\2\uffff\1\3\1\uffff\1\20"+
        "\1\4\1\uffff\1\3\1\uffff\1\2\2\0\1\uffff";
    static final String DFA1_maxS =
        "\1\146\3\2\5\uffff\3\0\7\uffff\3\127\2\uffff\1\127\1\uffff\1\127"+
        "\1\146\1\uffff\1\3\1\uffff\1\3\2\0\1\uffff";
    static final String DFA1_acceptS =
        "\4\uffff\1\3\1\4\1\5\1\6\1\7\3\uffff\1\13\1\15\1\16\1\17\1\20\1"+
        "\21\1\22\3\uffff\1\12\1\14\1\uffff\1\11\2\uffff\1\1\1\uffff\1\2"+
        "\3\uffff\1\10";
    static final String DFA1_specialS =
        "\11\uffff\1\4\1\0\1\2\24\uffff\1\3\1\1\1\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\20\1\17\10\uffff\1\7\1\uffff\1\17\4\uffff\1\1\11\uffff\1"+
            "\10\1\uffff\1\22\1\21\1\uffff\1\5\11\uffff\1\3\4\uffff\1\15"+
            "\10\uffff\1\2\2\uffff\1\13\1\11\3\uffff\1\12\4\uffff\1\6\13"+
            "\uffff\1\16\1\uffff\1\4\5\uffff\1\14\10\uffff\1\16",
            "\1\23",
            "\1\24",
            "\1\25",
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
            "",
            "\1\31\106\uffff\1\30",
            "\1\32",
            "\1\33",
            "",
            "",
            "\1\31\14\uffff\1\34\106\uffff\1\31",
            "",
            "\1\35\106\uffff\1\36",
            "\100\15\1\37\42\15",
            "",
            "\1\40",
            "",
            "\1\15\1\41",
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
            return "94:1: topdown : ( globalScope | globalNamedAction | tokensSection | mode | rule | ruleArg | ruleReturns | ruleNamedAction | ruleScopeSpec | ruleref | rewriteElement | terminal | labeledElement | setAlt | ruleAction | sempred | finallyClause | exceptionHandler );";
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
                        if ( ((inContext("RESULT ..."))) ) {s = 12;}

                        else if ( ((!inContext("TOKENS ASSIGN"))) ) {s = 23;}


                        input.seek(index1_10);
                        if ( s>=0 ) return s;
                        break;
                    case 1 :
                        int LA1_33 = input.LA(1);


                        int index1_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("TOKENS"))) ) {s = 4;}

                        else if ( ((inContext("RULE ..."))) ) {s = 13;}


                        input.seek(index1_33);
                        if ( s>=0 ) return s;
                        break;
                    case 2 :
                        int LA1_11 = input.LA(1);


                        int index1_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("RESULT ..."))) ) {s = 12;}

                        else if ( (true) ) {s = 23;}


                        input.seek(index1_11);
                        if ( s>=0 ) return s;
                        break;
                    case 3 :
                        int LA1_32 = input.LA(1);


                        int index1_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("GRAMMAR"))) ) {s = 30;}

                        else if ( ((inContext("RULE"))) ) {s = 34;}


                        input.seek(index1_32);
                        if ( s>=0 ) return s;
                        break;
                    case 4 :
                        int LA1_9 = input.LA(1);


                        int index1_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (!(((inContext("RESULT ..."))))) ) {s = 22;}

                        else if ( ((inContext("RESULT ..."))) ) {s = 12;}


                        input.seek(index1_9);
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
        "\41\uffff";
    static final String DFA5_eofS =
        "\41\uffff";
    static final String DFA5_minS =
        "\1\4\3\2\1\uffff\3\4\1\2\1\3\1\2\2\uffff\2\3\1\uffff\4\2\4\4\1\2"+
        "\2\3\1\2\3\3\1\uffff\1\3";
    static final String DFA5_maxS =
        "\4\146\1\uffff\6\146\2\uffff\2\146\1\uffff\17\146\1\uffff\1\146";
    static final String DFA5_acceptS =
        "\4\uffff\1\1\6\uffff\2\2\2\uffff\1\2\17\uffff\1\2\1\uffff";
    static final String DFA5_specialS =
        "\41\uffff}>";
    static final String[] DFA5_transitionS = {
            "\70\4\1\2\16\4\1\1\1\4\1\3\31\4",
            "\1\5\1\uffff\143\4",
            "\1\6\1\uffff\143\4",
            "\1\7\1\uffff\143\4",
            "",
            "\143\10",
            "\123\4\1\11\17\4",
            "\143\12",
            "\1\13\1\14\143\10",
            "\15\4\1\15\126\4",
            "\1\14\1\16\143\12",
            "",
            "",
            "\1\17\143\4",
            "\1\17\70\23\1\21\16\23\1\20\1\23\1\22\31\23",
            "",
            "\1\24\1\17\70\23\1\21\16\23\1\20\1\23\1\22\31\23",
            "\1\25\1\17\70\23\1\21\16\23\1\20\1\23\1\22\31\23",
            "\1\26\1\17\70\23\1\21\16\23\1\20\1\23\1\22\31\23",
            "\1\27\1\17\70\23\1\21\16\23\1\20\1\23\1\22\31\23",
            "\143\30",
            "\123\32\1\31\17\32",
            "\143\33",
            "\143\32",
            "\1\4\1\34\143\30",
            "\1\36\14\32\1\35\126\32",
            "\1\36\143\32",
            "\1\4\1\37\143\33",
            "\1\37\70\23\1\21\16\23\1\20\1\23\1\22\31\23",
            "\1\40\143\32",
            "\1\37\70\23\1\21\16\23\1\20\1\23\1\22\31\23",
            "",
            "\1\37\70\23\1\21\16\23\1\20\1\23\1\22\31\23"
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
            return "()* loopback of 152:17: ( options {greedy=false; } : . )*";
        }
    }


    public static final BitSet FOLLOW_globalScope_in_topdown89 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_globalNamedAction_in_topdown97 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSection_in_topdown105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mode_in_topdown113 = new BitSet(new long[]{0x0000000000000002L});
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
    public static final BitSet FOLLOW_sempred_in_topdown224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finallyClause_in_topdown232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exceptionHandler_in_topdown240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_finishRule_in_bottomup251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_globalScope265 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_globalScope267 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_globalScope269 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_globalNamedAction288 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_globalNamedAction290 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_globalNamedAction293 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_globalNamedAction295 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_tokensSection318 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokensSection322 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokensSection324 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_tokensSection338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODE_in_mode357 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_mode359 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_RULE_in_rule384 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule396 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_RULEMODIFIERS_in_rule420 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_AT_in_rule444 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule446 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rule448 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_rule462 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_setAlt505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_finishRule528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_ruleNamedAction544 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleNamedAction546 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleNamedAction548 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORCED_ACTION_in_ruleAction582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_sempred601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler617 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler619 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler621 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause638 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause640 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleArg658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns674 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns676 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec699 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec701 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec714 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec718 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_set_in_rewriteElement745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement780 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement784 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement796 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement800 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal840 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref890 = new BitSet(new long[]{0x0000000000000002L});

}