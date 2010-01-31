// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 BasicSemanticsChecker.g 2010-01-31 13:57:42

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
/** Check the basic semantics of the input.  We check for:
	FILE_AND_GRAMMAR_NAME_DIFFER
	RULE_REDEFINITION(MessageSeverity.ERROR, true, true),
	LEXER_RULES_NOT_ALLOWED(MessageSeverity.ERROR, true, true),
	PARSER_RULES_NOT_ALLOWED(MessageSeverity.ERROR, true, true),
	UNDEFINED_RULE_REF(MessageSeverity.ERROR, true, true),
	CANNOT_ALIAS_TOKENS_IN_LEXER(MessageSeverity.ERROR, true, true),
	INVALID_RULE_PARAMETER_REF(MessageSeverity.ERROR, true, true),
	SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE(MessageSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_RULE(MessageSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_TOKEN(MessageSeverity.ERROR, true, true),
	LABEL_TYPE_CONFLICT(MessageSeverity.ERROR, true, true),
	MISSING_RULE_ARGS(MessageSeverity.ERROR, true, true),
	RULE_HAS_NO_ARGS(MessageSeverity.ERROR, true, true),
	ARGS_ON_TOKEN_REF(MessageSeverity.ERROR, true, true),
	ILLEGAL_OPTION(MessageSeverity.ERROR, true, true),
	UNDEFINED_TOKEN_REF_IN_REWRITE(MessageSeverity.ERROR, true, true),
	REWRITE_ELEMENT_NOT_PRESENT_ON_LHS(MessageSeverity.ERROR, true, true),
	UNDEFINED_LABEL_REF_IN_REWRITE(MessageSeverity.ERROR, true, true),
	EMPTY_COMPLEMENT(MessageSeverity.ERROR, true, true),
	ACTION_REDEFINITION(MessageSeverity.ERROR, true, true),
	REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION(MessageSeverity.ERROR, true, true),
	NO_RULES(MessageSeverity.ERROR, true, true),
	REWRITE_FOR_MULTI_ELEMENT_ALT(MessageSeverity.ERROR, true, true),
	RULE_INVALID_SET(MessageSeverity.ERROR, true, true),
	HETERO_ILLEGAL_IN_REWRITE_ALT(MessageSeverity.ERROR, true, true),
	NO_SUCH_RULE_IN_SCOPE(MessageSeverity.ERROR, true, true),
	TOKEN_ALIAS_CONFLICT(MessageSeverity.ERROR, true, true),
	TOKEN_ALIAS_REASSIGNMENT(MessageSeverity.ERROR, true, true),
	TOKEN_VOCAB_IN_DELEGATE(MessageSeverity.ERROR, true, true),
	INVALID_IMPORT(MessageSeverity.ERROR, true, true),
	IMPORTED_TOKENS_RULE_EMPTY(MessageSeverity.ERROR, true, true),
	IMPORT_NAME_CLASH(MessageSeverity.ERROR, true, true),
	AST_OP_WITH_NON_AST_OUTPUT_OPTION(MessageSeverity.ERROR, true, true),
	AST_OP_IN_ALT_WITH_REWRITE(MessageSeverity.ERROR, true, true),
    WILDCARD_AS_ROOT(MessageSeverity.ERROR, true, true),
    CONFLICTING_OPTION_IN_TREE_FILTER(MessageSeverity.ERROR, true, true),
 * 
 */
public class BasicSemanticsChecker extends TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "WILDCARD", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "STRING_LITERAL", "ESC_SEQ", "CHAR_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "RESULT", "ID", "ARG", "ARGLIST", "RET", "LEXER_GRAMMAR", "PARSER_GRAMMAR", "TREE_GRAMMAR", "COMBINED_GRAMMAR", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "DOT", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "ALT_REWRITE", "'='"
    };
    public static final int LT=43;
    public static final int STAR=48;
    public static final int BACKTRACK_SEMPRED=99;
    public static final int DOUBLE_ANGLE_STRING_LITERAL=11;
    public static final int FORCED_ACTION=5;
    public static final int LEXER_GRAMMAR=91;
    public static final int ARGLIST=89;
    public static final int ALTLIST=85;
    public static final int NOT=60;
    public static final int EOF=-1;
    public static final int SEMPRED=4;
    public static final int ACTION=16;
    public static final int TOKEN_REF=62;
    public static final int RULEMODIFIERS=75;
    public static final int ST_RESULT=103;
    public static final int RPAREN=41;
    public static final int RET=90;
    public static final int IMPORT=22;
    public static final int STRING_LITERAL=66;
    public static final int ARG=88;
    public static final int ARG_ACTION=14;
    public static final int DOUBLE_QUOTE_STRING_LITERAL=10;
    public static final int COMMENT=9;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int GRAMMAR=27;
    public static final int RULEACTIONS=76;
    public static final int WSCHARS=65;
    public static final int INITACTION=95;
    public static final int ALT_REWRITE=104;
    public static final int IMPLIES=42;
    public static final int RULE=73;
    public static final int RBRACE=61;
    public static final int ACTION_ESC=17;
    public static final int PARSER_GRAMMAR=92;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int CHAR_RANGE=82;
    public static final int INT=64;
    public static final int EPSILON=83;
    public static final int LIST=101;
    public static final int COLONCOLON=37;
    public static final int WSNLCHARS=18;
    public static final int WS=71;
    public static final int COMBINED_GRAMMAR=94;
    public static final int LEXER=24;
    public static final int OR=51;
    public static final int GT=44;
    public static final int TREE_GRAMMAR=93;
    public static final int CATCH=33;
    public static final int CLOSURE=79;
    public static final int PARSER=25;
    public static final int DOLLAR=53;
    public static final int PROTECTED=28;
    public static final int ELEMENT_OPTIONS=102;
    public static final int NESTED_ACTION=15;
    public static final int FRAGMENT=23;
    public static final int ID=87;
    public static final int TREE_BEGIN=58;
    public static final int LPAREN=40;
    public static final int AT=59;
    public static final int ESC_SEQ=67;
    public static final int ALT=84;
    public static final int TREE=26;
    public static final int SCOPE=21;
    public static final int ETC=56;
    public static final int COMMA=38;
    public static final int WILDCARD=54;
    public static final int T__105=105;
    public static final int DOC_COMMENT=6;
    public static final int PLUS=49;
    public static final int DOT=100;
    public static final int RETURNS=31;
    public static final int RULES=74;
    public static final int RARROW=57;
    public static final int UNICODE_ESC=70;
    public static final int HEX_DIGIT=69;
    public static final int RANGE=55;
    public static final int TOKENS=20;
    public static final int GATED_SEMPRED=97;
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
    public static final int CHAR_LITERAL=68;
    public static final int TEMPLATE=35;
    public static final int LABEL=96;
    public static final int SYN_SEMPRED=98;
    public static final int ERRCHAR=72;
    public static final int BLOCK=77;
    public static final int ASSIGN=45;
    public static final int PLUS_ASSIGN=50;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=80;
    public static final int OPTIONS=19;

    // delegates
    // delegators


        public BasicSemanticsChecker(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public BasicSemanticsChecker(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return BasicSemanticsChecker.tokenNames; }
    public String getGrammarFileName() { return "BasicSemanticsChecker.g"; }


    public String name;
    public String fileName;
    public Map<String,String> options = new HashMap<String,String>();
    protected int gtype;
    //Grammar g; // which grammar are we checking
    public BasicSemanticsChecker(TreeNodeStream input, String fileName) {
    	this(input);
    	this.fileName = fileName;
    }



    // $ANTLR start "topdown"
    // BasicSemanticsChecker.g:117:1: topdown : ( grammarSpec | optionsSpec | rule | ruleref );
    public final void topdown() throws RecognitionException {
        try {
            // BasicSemanticsChecker.g:118:2: ( grammarSpec | optionsSpec | rule | ruleref )
            int alt1=4;
            switch ( input.LA(1) ) {
            case LEXER_GRAMMAR:
            case PARSER_GRAMMAR:
            case TREE_GRAMMAR:
            case COMBINED_GRAMMAR:
                {
                alt1=1;
                }
                break;
            case OPTIONS:
                {
                alt1=2;
                }
                break;
            case RULE:
                {
                alt1=3;
                }
                break;
            case BANG:
            case ROOT:
            case RULE_REF:
                {
                alt1=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;
            }

            switch (alt1) {
                case 1 :
                    // BasicSemanticsChecker.g:118:4: grammarSpec
                    {
                    pushFollow(FOLLOW_grammarSpec_in_topdown83);
                    grammarSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // BasicSemanticsChecker.g:119:4: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_topdown88);
                    optionsSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // BasicSemanticsChecker.g:120:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown93);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // BasicSemanticsChecker.g:121:4: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown98);
                    ruleref();

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
    // BasicSemanticsChecker.g:125:1: grammarSpec : ^( grammarType ID ( . )* ) ;
    public final void grammarSpec() throws RecognitionException {
        GrammarAST ID1=null;

        try {
            // BasicSemanticsChecker.g:126:5: ( ^( grammarType ID ( . )* ) )
            // BasicSemanticsChecker.g:126:9: ^( grammarType ID ( . )* )
            {
            pushFollow(FOLLOW_grammarType_in_grammarSpec116);
            grammarType();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID1=(GrammarAST)match(input,ID,FOLLOW_ID_in_grammarSpec118); if (state.failed) return ;
            // BasicSemanticsChecker.g:126:26: ( . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=SEMPRED && LA2_0<=105)) ) {
                    alt2=1;
                }
                else if ( (LA2_0==UP) ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // BasicSemanticsChecker.g:126:26: .
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
                  	if ( !fileName.equals(name+".g") ) {
                  		ErrorManager.grammarError(ErrorType.FILE_AND_GRAMMAR_NAME_DIFFER,
                  							      fileName, ID1.token, name, fileName);
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
    // $ANTLR end "grammarSpec"

    public static class grammarType_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "grammarType"
    // BasicSemanticsChecker.g:136:1: grammarType : ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR );
    public final BasicSemanticsChecker.grammarType_return grammarType() throws RecognitionException {
        BasicSemanticsChecker.grammarType_return retval = new BasicSemanticsChecker.grammarType_return();
        retval.start = input.LT(1);

        gtype = ((GrammarAST)retval.start).getType();
        try {
            // BasicSemanticsChecker.g:138:5: ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR )
            // BasicSemanticsChecker.g:
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


    // $ANTLR start "optionsSpec"
    // BasicSemanticsChecker.g:141:1: optionsSpec : ^( OPTIONS ( option )+ ) ;
    public final void optionsSpec() throws RecognitionException {
        try {
            // BasicSemanticsChecker.g:142:2: ( ^( OPTIONS ( option )+ ) )
            // BasicSemanticsChecker.g:142:4: ^( OPTIONS ( option )+ )
            {
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec179); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            // BasicSemanticsChecker.g:142:14: ( option )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==105) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // BasicSemanticsChecker.g:142:14: option
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec181);
            	    option();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
            	    if (state.backtracking>0) {state.failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;

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
    // $ANTLR end "optionsSpec"


    // $ANTLR start "option"
    // BasicSemanticsChecker.g:145:1: option : ^( '=' o= ID optionValue ) ;
    public final void option() throws RecognitionException {
        GrammarAST o=null;
        BasicSemanticsChecker.optionValue_return optionValue2 = null;


        try {
            // BasicSemanticsChecker.g:146:5: ( ^( '=' o= ID optionValue ) )
            // BasicSemanticsChecker.g:146:9: ^( '=' o= ID optionValue )
            {
            match(input,105,FOLLOW_105_in_option203); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            o=(GrammarAST)match(input,ID,FOLLOW_ID_in_option207); if (state.failed) return ;
            pushFollow(FOLLOW_optionValue_in_option209);
            optionValue2=optionValue();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              options.put((o!=null?o.getText():null), (optionValue2!=null?optionValue2.v:null));
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
    // $ANTLR end "option"

    public static class optionValue_return extends TreeRuleReturnScope {
        public String v;
    };

    // $ANTLR start "optionValue"
    // BasicSemanticsChecker.g:149:1: optionValue returns [String v] : ( ID | STRING_LITERAL | CHAR_LITERAL | INT | STAR );
    public final BasicSemanticsChecker.optionValue_return optionValue() throws RecognitionException {
        BasicSemanticsChecker.optionValue_return retval = new BasicSemanticsChecker.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // BasicSemanticsChecker.g:151:5: ( ID | STRING_LITERAL | CHAR_LITERAL | INT | STAR )
            // BasicSemanticsChecker.g:
            {
            if ( input.LA(1)==STAR||input.LA(1)==INT||input.LA(1)==STRING_LITERAL||input.LA(1)==CHAR_LITERAL||input.LA(1)==ID ) {
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
    // BasicSemanticsChecker.g:158:1: rule : ^( RULE r= ID ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST r=null;

        try {
            // BasicSemanticsChecker.g:158:5: ( ^( RULE r= ID ( . )* ) )
            // BasicSemanticsChecker.g:158:9: ^( RULE r= ID ( . )* )
            {
            match(input,RULE,FOLLOW_RULE_in_rule297); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            r=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule301); if (state.failed) return ;
            // BasicSemanticsChecker.g:158:22: ( . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=SEMPRED && LA4_0<=105)) ) {
                    alt4=1;
                }
                else if ( (LA4_0==UP) ) {
                    alt4=2;
                }


                switch (alt4) {
            	case 1 :
            	    // BasicSemanticsChecker.g:158:22: .
            	    {
            	    matchAny(input); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {

              	    if ( gtype==LEXER_GRAMMAR && Character.isLowerCase((r!=null?r.getText():null).charAt(0)) ) {
              	    	ErrorManager.grammarError(ErrorType.PARSER_RULES_NOT_ALLOWED,
                  							      fileName, r.token, (r!=null?r.getText():null));
              		}
              	    if ( (gtype==PARSER_GRAMMAR||gtype==PARSER_GRAMMAR) &&
              	         Character.isUpperCase((r!=null?r.getText():null).charAt(0)) )
              	    {
              	    	ErrorManager.grammarError(ErrorType.LEXER_RULES_NOT_ALLOWED,
                  							      fileName, r.token, (r!=null?r.getText():null));
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
    // $ANTLR end "rule"


    // $ANTLR start "ruleref"
    // BasicSemanticsChecker.g:173:1: ruleref : {...}? ( ^( ( ROOT | BANG ) r= RULE_REF ( ARG_ACTION )? ) | ^(r= RULE_REF ( ARG_ACTION )? ) ) ;
    public final void ruleref() throws RecognitionException {
        GrammarAST r=null;

        try {
            // BasicSemanticsChecker.g:174:5: ({...}? ( ^( ( ROOT | BANG ) r= RULE_REF ( ARG_ACTION )? ) | ^(r= RULE_REF ( ARG_ACTION )? ) ) )
            // BasicSemanticsChecker.g:174:7: {...}? ( ^( ( ROOT | BANG ) r= RULE_REF ( ARG_ACTION )? ) | ^(r= RULE_REF ( ARG_ACTION )? ) )
            {
            if ( !((gtype==LEXER_GRAMMAR)) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "ruleref", "gtype==LEXER_GRAMMAR");
            }
            // BasicSemanticsChecker.g:175:6: ( ^( ( ROOT | BANG ) r= RULE_REF ( ARG_ACTION )? ) | ^(r= RULE_REF ( ARG_ACTION )? ) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==BANG||LA7_0==ROOT) ) {
                alt7=1;
            }
            else if ( (LA7_0==RULE_REF) ) {
                alt7=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // BasicSemanticsChecker.g:175:8: ^( ( ROOT | BANG ) r= RULE_REF ( ARG_ACTION )? )
                    {
                    if ( input.LA(1)==BANG||input.LA(1)==ROOT ) {
                        input.consume();
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    match(input, Token.DOWN, null); if (state.failed) return ;
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref347); if (state.failed) return ;
                    // BasicSemanticsChecker.g:175:33: ( ARG_ACTION )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ARG_ACTION) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // BasicSemanticsChecker.g:175:33: ARG_ACTION
                            {
                            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref349); if (state.failed) return ;

                            }
                            break;

                    }


                    match(input, Token.UP, null); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // BasicSemanticsChecker.g:176:8: ^(r= RULE_REF ( ARG_ACTION )? )
                    {
                    r=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref363); if (state.failed) return ;

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); if (state.failed) return ;
                        // BasicSemanticsChecker.g:176:21: ( ARG_ACTION )?
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==ARG_ACTION) ) {
                            alt6=1;
                        }
                        switch (alt6) {
                            case 1 :
                                // BasicSemanticsChecker.g:176:21: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref365); if (state.failed) return ;

                                }
                                break;

                        }


                        match(input, Token.UP, null); if (state.failed) return ;
                    }

                    }
                    break;

            }

            if ( state.backtracking==1 ) {

              	    ErrorManager.grammarError(ErrorType.PARSER_RULES_NOT_ALLOWED,
                  						      fileName, r.token, (r!=null?r.getText():null));
              		
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


 

    public static final BitSet FOLLOW_grammarSpec_in_topdown83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_topdown88 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_topdown93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_topdown98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec116 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec118 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000003FFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_grammarType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec179 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec181 = new BitSet(new long[]{0x0000000000000008L,0x0000020000000000L});
    public static final BitSet FOLLOW_105_in_option203 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option207 = new BitSet(new long[]{0x0001000000000000L,0x0000000000800015L});
    public static final BitSet FOLLOW_optionValue_in_option209 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule297 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule301 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000003FFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_ruleref339 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref347 = new BitSet(new long[]{0x0000000000004008L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref349 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref363 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref365 = new BitSet(new long[]{0x0000000000000008L});

}