// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 BasicSemanticTriggers.g 2010-02-01 14:30:07

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
import org.antlr.v4.tool.*;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/** Triggers for the basic semantics of the input.  Side-effects:
 *  Set token, block, rule options in the tree.  Load field option
 *  with grammar options. Only legal options are set.
 */
public class BasicSemanticTriggers extends org.antlr.v4.runtime.tree.TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "WILDCARD", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "RESULT", "ID", "ARG", "ARGLIST", "RET", "LEXER_GRAMMAR", "PARSER_GRAMMAR", "TREE_GRAMMAR", "COMBINED_GRAMMAR", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "DOT", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "ALT_REWRITE"
    };
    public static final int LT=43;
    public static final int STAR=48;
    public static final int BACKTRACK_SEMPRED=98;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int FORCED_ACTION=5;
    public static final int LEXER_GRAMMAR=90;
    public static final int ARGLIST=88;
    public static final int ALTLIST=84;
    public static final int NOT=60;
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int ACTION=16;
    public static final int TOKEN_REF=62;
    public static final int RULEMODIFIERS=74;
    public static final int ST_RESULT=102;
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
    public static final int INITACTION=94;
    public static final int ALT_REWRITE=103;
    public static final int IMPLIES=42;
    public static final int RULE=72;
    public static final int RBRACE=61;
    public static final int ACTION_ESC=17;
    public static final int PARSER_GRAMMAR=91;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int CHAR_RANGE=81;
    public static final int INT=64;
    public static final int EPSILON=82;
    public static final int LIST=100;
    public static final int COLONCOLON=37;
    public static final int WSNLCHARS=18;
    public static final int WS=70;
    public static final int COMBINED_GRAMMAR=93;
    public static final int LEXER=24;
    public static final int OR=51;
    public static final int GT=44;
    public static final int TREE_GRAMMAR=92;
    public static final int CATCH=33;
    public static final int CLOSURE=78;
    public static final int PARSER=25;
    public static final int DOLLAR=53;
    public static final int PROTECTED=28;
    public static final int ELEMENT_OPTIONS=101;
    public static final int NESTED_ACTION=15;
    public static final int FRAGMENT=23;
    public static final int ID=86;
    public static final int TREE_BEGIN=58;
    public static final int LPAREN=40;
    public static final int AT=59;
    public static final int ESC_SEQ=66;
    public static final int ALT=83;
    public static final int TREE=26;
    public static final int SCOPE=21;
    public static final int ETC=56;
    public static final int COMMA=38;
    public static final int WILDCARD=54;
    public static final int DOC_COMMENT=6;
    public static final int PLUS=49;
    public static final int DOT=99;
    public static final int RETURNS=31;
    public static final int RULES=73;
    public static final int RARROW=57;
    public static final int UNICODE_ESC=69;
    public static final int HEX_DIGIT=68;
    public static final int RANGE=55;
    public static final int TOKENS=20;
    public static final int GATED_SEMPRED=96;
    public static final int RESULT=85;
    public static final int BANG=47;
    public static final int ACTION_STRING_LITERAL=12;
    public static final int ROOT=52;
    public static final int SEMI=39;
    public static final int RULE_REF=63;
    public static final int NLCHARS=8;
    public static final int OPTIONAL=77;
    public static final int SYNPRED=80;
    public static final int COLON=36;
    public static final int QUESTION=46;
    public static final int FINALLY=34;
    public static final int TEMPLATE=35;
    public static final int LABEL=95;
    public static final int SYN_SEMPRED=97;
    public static final int ERRCHAR=71;
    public static final int BLOCK=76;
    public static final int ASSIGN=45;
    public static final int PLUS_ASSIGN=50;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=79;
    public static final int OPTIONS=19;

    // delegates
    // delegators


        public BasicSemanticTriggers(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public BasicSemanticTriggers(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return BasicSemanticTriggers.tokenNames; }
    public String getGrammarFileName() { return "BasicSemanticTriggers.g"; }


    // TODO: SHOULD we fix up grammar AST to remove errors?  Like kill refs to bad rules?
    // that is, rewrite tree?  maybe all passes are filters until code gen, which needs
    // tree grammar. 'course we won't try codegen if errors.
    public String name;
    public String fileName;
    public Map<String,String> options = new HashMap<String,String>();
    protected int gtype;
    //Grammar g; // which grammar are we checking
    public BasicSemanticTriggers(TreeNodeStream input, String fileName) {
    	this(input);
    	this.fileName = fileName;
    }



    // $ANTLR start "topdown"
    // BasicSemanticTriggers.g:86:1: topdown : ( grammarSpec | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption );
    public final void topdown() throws RecognitionException {
        try {
            // BasicSemanticTriggers.g:87:2: ( grammarSpec | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption )
            int alt1=7;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // BasicSemanticTriggers.g:87:4: grammarSpec
                    {
                    pushFollow(FOLLOW_grammarSpec_in_topdown93);
                    grammarSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // BasicSemanticTriggers.g:88:4: option
                    {
                    pushFollow(FOLLOW_option_in_topdown98);
                    option();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // BasicSemanticTriggers.g:89:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown103);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // BasicSemanticTriggers.g:90:4: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown108);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // BasicSemanticTriggers.g:91:4: tokenAlias
                    {
                    pushFollow(FOLLOW_tokenAlias_in_topdown113);
                    tokenAlias();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // BasicSemanticTriggers.g:92:4: tokenRefWithArgs
                    {
                    pushFollow(FOLLOW_tokenRefWithArgs_in_topdown118);
                    tokenRefWithArgs();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // BasicSemanticTriggers.g:93:4: elementOption
                    {
                    pushFollow(FOLLOW_elementOption_in_topdown123);
                    elementOption();

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


    // $ANTLR start "grammarSpec"
    // BasicSemanticTriggers.g:96:1: grammarSpec : ^( grammarType ID ( . )* ) ;
    public final void grammarSpec() throws RecognitionException {
        GrammarAST ID1=null;

        try {
            // BasicSemanticTriggers.g:97:5: ( ^( grammarType ID ( . )* ) )
            // BasicSemanticTriggers.g:97:9: ^( grammarType ID ( . )* )
            {
            pushFollow(FOLLOW_grammarType_in_grammarSpec140);
            grammarType();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID1=(GrammarAST)match(input,ID,FOLLOW_ID_in_grammarSpec142); if (state.failed) return ;
            // BasicSemanticTriggers.g:97:26: ( . )*
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
            	    // BasicSemanticTriggers.g:97:26: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

                  	name = (ID1!=null?ID1.getText():null);
                  	BasicSemanticChecks.checkGrammarName(ID1.token);
                  	
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
    // $ANTLR end "grammarSpec"

    public static class grammarType_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "grammarType"
    // BasicSemanticTriggers.g:104:1: grammarType : ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR );
    public final BasicSemanticTriggers.grammarType_return grammarType() throws RecognitionException {
        BasicSemanticTriggers.grammarType_return retval = new BasicSemanticTriggers.grammarType_return();
        retval.start = input.LT(1);

        gtype = ((GrammarAST)retval.start).getType();
        try {
            // BasicSemanticTriggers.g:106:5: ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR )
            // BasicSemanticTriggers.g:
            {
            if ( (input.LA(1)>=LEXER_GRAMMAR && input.LA(1)<=COMBINED_GRAMMAR) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
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
        return retval;
    }
    // $ANTLR end "grammarType"

    public static class option_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "option"
    // BasicSemanticTriggers.g:109:1: option : {...}? ^( ASSIGN o= ID optionValue ) ;
    public final BasicSemanticTriggers.option_return option() throws RecognitionException {
        BasicSemanticTriggers.option_return retval = new BasicSemanticTriggers.option_return();
        retval.start = input.LT(1);

        GrammarAST o=null;
        BasicSemanticTriggers.optionValue_return optionValue2 = null;


        try {
            // BasicSemanticTriggers.g:110:5: ({...}? ^( ASSIGN o= ID optionValue ) )
            // BasicSemanticTriggers.g:110:9: {...}? ^( ASSIGN o= ID optionValue )
            {
            if ( !((inContext("OPTIONS"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "option", "inContext(\"OPTIONS\")");
            }
            match(input,ASSIGN,FOLLOW_ASSIGN_in_option211); if (state.failed) return retval;

            match(input, Token.DOWN, null); if (state.failed) return retval;
            o=(GrammarAST)match(input,ID,FOLLOW_ID_in_option215); if (state.failed) return retval;
            pushFollow(FOLLOW_optionValue_in_option217);
            optionValue2=optionValue();

            state._fsp--;
            if (state.failed) return retval;

            match(input, Token.UP, null); if (state.failed) return retval;
            if ( state.backtracking==1 ) {

                 	    GrammarAST parent = (GrammarAST)((GrammarAST)retval.start).getParent();   // OPTION
                 		GrammarAST parentWithOptionKind = (GrammarAST)parent.getParent();
                  	boolean ok = BasicSemanticChecks.checkOptions(gtype, parentWithOptionKind,
                  												  o.token, (optionValue2!=null?optionValue2.v:null));
                  	if ( ok ) options.put((o!=null?o.getText():null), (optionValue2!=null?optionValue2.v:null));
                  	
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
    // $ANTLR end "option"

    public static class optionValue_return extends TreeRuleReturnScope {
        public String v;
    };

    // $ANTLR start "optionValue"
    // BasicSemanticTriggers.g:120:1: optionValue returns [String v] : ( ID | STRING_LITERAL | INT | STAR );
    public final BasicSemanticTriggers.optionValue_return optionValue() throws RecognitionException {
        BasicSemanticTriggers.optionValue_return retval = new BasicSemanticTriggers.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // BasicSemanticTriggers.g:122:5: ( ID | STRING_LITERAL | INT | STAR )
            // BasicSemanticTriggers.g:
            {
            if ( input.LA(1)==STAR||input.LA(1)==INT||input.LA(1)==STRING_LITERAL||input.LA(1)==ID ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
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
        return retval;
    }
    // $ANTLR end "optionValue"


    // $ANTLR start "rule"
    // BasicSemanticTriggers.g:128:1: rule : ^( RULE r= ID ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST r=null;

        try {
            // BasicSemanticTriggers.g:128:5: ( ^( RULE r= ID ( . )* ) )
            // BasicSemanticTriggers.g:128:9: ^( RULE r= ID ( . )* )
            {
            match(input,RULE,FOLLOW_RULE_in_rule299); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            r=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule303); if (state.failed) return ;
            // BasicSemanticTriggers.g:128:22: ( . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>=SEMPRED && LA3_0<=ALT_REWRITE)) ) {
                    alt3=1;
                }
                else if ( (LA3_0==UP) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // BasicSemanticTriggers.g:128:22: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkInvalidRuleDef(gtype, r.token);
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


    // $ANTLR start "ruleref"
    // BasicSemanticTriggers.g:131:1: ruleref : RULE_REF ;
    public final void ruleref() throws RecognitionException {
        GrammarAST RULE_REF3=null;

        try {
            // BasicSemanticTriggers.g:132:5: ( RULE_REF )
            // BasicSemanticTriggers.g:132:7: RULE_REF
            {
            RULE_REF3=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref326); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkInvalidRuleRef(gtype, RULE_REF3.token);
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


    // $ANTLR start "tokenAlias"
    // BasicSemanticTriggers.g:135:1: tokenAlias : {...}? ^( ASSIGN TOKEN_REF STRING_LITERAL ) ;
    public final void tokenAlias() throws RecognitionException {
        GrammarAST TOKEN_REF4=null;

        try {
            // BasicSemanticTriggers.g:136:2: ({...}? ^( ASSIGN TOKEN_REF STRING_LITERAL ) )
            // BasicSemanticTriggers.g:136:4: {...}? ^( ASSIGN TOKEN_REF STRING_LITERAL )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokenAlias", "inContext(\"TOKENS\")");
            }
            match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenAlias345); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            TOKEN_REF4=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenAlias347); if (state.failed) return ;
            match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenAlias349); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkTokenAlias(gtype, TOKEN_REF4.token);
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
    // $ANTLR end "tokenAlias"


    // $ANTLR start "tokenRefWithArgs"
    // BasicSemanticTriggers.g:140:1: tokenRefWithArgs : ^( TOKEN_REF ARG_ACTION ) ;
    public final void tokenRefWithArgs() throws RecognitionException {
        GrammarAST TOKEN_REF5=null;

        try {
            // BasicSemanticTriggers.g:141:2: ( ^( TOKEN_REF ARG_ACTION ) )
            // BasicSemanticTriggers.g:141:4: ^( TOKEN_REF ARG_ACTION )
            {
            TOKEN_REF5=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenRefWithArgs366); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_tokenRefWithArgs368); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkTokenArgs(gtype, TOKEN_REF5.token);
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
    // $ANTLR end "tokenRefWithArgs"

    public static class elementOption_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "elementOption"
    // BasicSemanticTriggers.g:145:1: elementOption : ^( ELEMENT_OPTIONS (o= ID | ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) ) ;
    public final BasicSemanticTriggers.elementOption_return elementOption() throws RecognitionException {
        BasicSemanticTriggers.elementOption_return retval = new BasicSemanticTriggers.elementOption_return();
        retval.start = input.LT(1);

        GrammarAST o=null;
        GrammarAST value=null;

        try {
            // BasicSemanticTriggers.g:146:5: ( ^( ELEMENT_OPTIONS (o= ID | ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) ) )
            // BasicSemanticTriggers.g:146:7: ^( ELEMENT_OPTIONS (o= ID | ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) )
            {
            match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOption390); if (state.failed) return retval;

            match(input, Token.DOWN, null); if (state.failed) return retval;
            // BasicSemanticTriggers.g:147:7: (o= ID | ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) )
            int alt4=3;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ID) ) {
                alt4=1;
            }
            else if ( (LA4_0==ASSIGN) ) {
                int LA4_2 = input.LA(2);

                if ( (LA4_2==DOWN) ) {
                    int LA4_3 = input.LA(3);

                    if ( (LA4_3==ID) ) {
                        int LA4_4 = input.LA(4);

                        if ( (LA4_4==ID) ) {
                            alt4=2;
                        }
                        else if ( (LA4_4==STRING_LITERAL) ) {
                            alt4=3;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 4, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 3, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // BasicSemanticTriggers.g:147:9: o= ID
                    {
                    o=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption402); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // BasicSemanticTriggers.g:148:11: ^( ASSIGN o= ID value= ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption415); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    o=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption419); if (state.failed) return retval;
                    value=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption423); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 3 :
                    // BasicSemanticTriggers.g:149:11: ^( ASSIGN o= ID value= STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption437); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    o=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption441); if (state.failed) return retval;
                    value=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption445); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;

            }


            match(input, Token.UP, null); if (state.failed) return retval;
            if ( state.backtracking==1 ) {

                  	boolean ok = BasicSemanticChecks.checkTokenOptions(gtype, (GrammarAST)o.getParent(),
                  												       o.token, (value!=null?value.getText():null));
                  	if ( ok ) {
                  	    GrammarAST parent = (GrammarAST)((GrammarAST)retval.start).getParent();   // ELEMENT_OPTIONS
                  		TerminalAST terminal = (TerminalAST)parent.getParent();
                  		terminal.options.put((o!=null?o.getText():null), (value!=null?value.getText():null));
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
        return retval;
    }
    // $ANTLR end "elementOption"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\12\uffff";
    static final String DFA1_eofS =
        "\12\uffff";
    static final String DFA1_minS =
        "\1\55\1\uffff\1\2\4\uffff\1\76\2\uffff";
    static final String DFA1_maxS =
        "\1\145\1\uffff\1\2\4\uffff\1\126\2\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\1\4\1\6\1\7\1\uffff\1\2\1\5";
    static final String DFA1_specialS =
        "\12\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\2\20\uffff\1\5\1\4\10\uffff\1\3\21\uffff\4\1\7\uffff\1\6",
            "",
            "\1\7",
            "",
            "",
            "",
            "",
            "\1\11\27\uffff\1\10",
            "",
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
            return "86:1: topdown : ( grammarSpec | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption );";
        }
    }
 

    public static final BitSet FOLLOW_grammarSpec_in_topdown93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_option_in_topdown98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_topdown103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_topdown108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokenAlias_in_topdown113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokenRefWithArgs_in_topdown118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementOption_in_topdown123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec140 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec142 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000000FFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_grammarType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_option211 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option215 = new BitSet(new long[]{0x0001000000000000L,0x0000000000400009L});
    public static final BitSet FOLLOW_optionValue_in_option217 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule303 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000000FFFFFFFFFFL});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenAlias345 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenAlias347 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenAlias349 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenRefWithArgs366 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_tokenRefWithArgs368 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOption390 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption402 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption415 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption419 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400000L});
    public static final BitSet FOLLOW_ID_in_elementOption423 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption437 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption441 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption445 = new BitSet(new long[]{0x0000000000000008L});

}