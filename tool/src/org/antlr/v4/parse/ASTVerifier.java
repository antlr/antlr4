// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ASTVerifier.g 2010-01-31 13:25:13

/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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
import org.antlr.v4.tool.*;
import org.antlr.v4.runtime.tree.CommonTree; // use updated v4 one not v3


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/** The definitive ANTLR v3 tree grammar to parse ANTLR v4 grammars. 
 *  Parses trees created in ANTLRParser.g.
 */
public class ASTVerifier extends TreeParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "WILDCARD", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "STRING_LITERAL", "ESC_SEQ", "CHAR_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "RESULT", "ID", "ARG", "ARGLIST", "RET", "LEXER_GRAMMAR", "PARSER_GRAMMAR", "TREE_GRAMMAR", "COMBINED_GRAMMAR", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "DOT", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "ALT_REWRITE"
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


        public ASTVerifier(TreeNodeStream input) {
            this(input, new RecognizerSharedState());
        }
        public ASTVerifier(TreeNodeStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return ASTVerifier.tokenNames; }
    public String getGrammarFileName() { return "ASTVerifier.g"; }


    public String getErrorMessage(RecognitionException e,
                                  String[] tokenNames)
    {
        List stack = getRuleInvocationStack(e, this.getClass().getName());
        String msg = null;
        String inputContext =
            ((CommonTree)input.LT(-3)).token+" "+
            ((CommonTree)input.LT(-2)).token+" "+
            ((CommonTree)input.LT(-1)).token+" >>>"+
            ((CommonTree)input.LT(1)).token+"<<< "+
            ((CommonTree)input.LT(2)).token+" "+
            ((CommonTree)input.LT(3)).token;
        if ( e instanceof NoViableAltException ) {
           NoViableAltException nvae = (NoViableAltException)e;
           msg = " no viable alt; token="+e.token+
              " (decision="+nvae.decisionNumber+
              " state "+nvae.stateNumber+")"+
              " decision=<<"+nvae.grammarDecisionDescription+">>";
        }
        else {
           msg = super.getErrorMessage(e, tokenNames);
        }
        return stack+" "+msg+"\ncontext=..."+inputContext+"...";
    }
    public String getTokenErrorDisplay(Token t) {
        return t.toString();
    }
    public void traceIn(String ruleName, int ruleIndex)  {
       	System.out.print("enter "+ruleName+" "+
                         ((GrammarAST)input.LT(1)).token+" "+
                         ((GrammarAST)input.LT(2)).token+" "+
                         ((GrammarAST)input.LT(3)).token+" "+
                         ((GrammarAST)input.LT(4)).token);
    	if ( state.backtracking>0 ) {
    		System.out.print(" backtracking="+state.backtracking);
    	}
    	System.out.println();
    }



    // $ANTLR start "grammarSpec"
    // ASTVerifier.g:110:1: grammarSpec : ^( grammarType ID ( DOC_COMMENT )? ( prequelConstruct )* rules ) ;
    public final void grammarSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:111:5: ( ^( grammarType ID ( DOC_COMMENT )? ( prequelConstruct )* rules ) )
            // ASTVerifier.g:111:9: ^( grammarType ID ( DOC_COMMENT )? ( prequelConstruct )* rules )
            {
            pushFollow(FOLLOW_grammarType_in_grammarSpec74);
            grammarType();

            state._fsp--;


            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_grammarSpec76); 
            // ASTVerifier.g:111:26: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ASTVerifier.g:111:26: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec78); 

                    }
                    break;

            }

            // ASTVerifier.g:111:39: ( prequelConstruct )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=OPTIONS && LA2_0<=IMPORT)||LA2_0==AT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ASTVerifier.g:111:39: prequelConstruct
            	    {
            	    pushFollow(FOLLOW_prequelConstruct_in_grammarSpec81);
            	    prequelConstruct();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            pushFollow(FOLLOW_rules_in_grammarSpec84);
            rules();

            state._fsp--;


            match(input, Token.UP, null); 

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


    // $ANTLR start "grammarType"
    // ASTVerifier.g:114:1: grammarType : ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR );
    public final void grammarType() throws RecognitionException {
        try {
            // ASTVerifier.g:115:5: ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR )
            // ASTVerifier.g:
            {
            if ( (input.LA(1)>=LEXER_GRAMMAR && input.LA(1)<=COMBINED_GRAMMAR) ) {
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
    // $ANTLR end "grammarType"


    // $ANTLR start "prequelConstruct"
    // ASTVerifier.g:118:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action );
    public final void prequelConstruct() throws RecognitionException {
        try {
            // ASTVerifier.g:119:2: ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action )
            int alt3=5;
            switch ( input.LA(1) ) {
            case OPTIONS:
                {
                alt3=1;
                }
                break;
            case IMPORT:
                {
                alt3=2;
                }
                break;
            case TOKENS:
                {
                alt3=3;
                }
                break;
            case SCOPE:
                {
                alt3=4;
                }
                break;
            case AT:
                {
                alt3=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ASTVerifier.g:119:6: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_prequelConstruct131);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:120:9: delegateGrammars
                    {
                    pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct141);
                    delegateGrammars();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:121:9: tokensSpec
                    {
                    pushFollow(FOLLOW_tokensSpec_in_prequelConstruct151);
                    tokensSpec();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:122:9: attrScope
                    {
                    pushFollow(FOLLOW_attrScope_in_prequelConstruct161);
                    attrScope();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // ASTVerifier.g:123:9: action
                    {
                    pushFollow(FOLLOW_action_in_prequelConstruct171);
                    action();

                    state._fsp--;


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
    // $ANTLR end "prequelConstruct"


    // $ANTLR start "optionsSpec"
    // ASTVerifier.g:126:1: optionsSpec : ^( OPTIONS ( option )+ ) ;
    public final void optionsSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:127:2: ( ^( OPTIONS ( option )+ ) )
            // ASTVerifier.g:127:4: ^( OPTIONS ( option )+ )
            {
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec186); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:127:14: ( option )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==ASSIGN) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // ASTVerifier.g:127:14: option
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec188);
            	    option();

            	    state._fsp--;


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
    // ASTVerifier.g:130:1: option : ^( ASSIGN ID optionValue ) ;
    public final void option() throws RecognitionException {
        try {
            // ASTVerifier.g:131:5: ( ^( ASSIGN ID optionValue ) )
            // ASTVerifier.g:131:9: ^( ASSIGN ID optionValue )
            {
            match(input,ASSIGN,FOLLOW_ASSIGN_in_option210); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_option212); 
            pushFollow(FOLLOW_optionValue_in_option214);
            optionValue();

            state._fsp--;


            match(input, Token.UP, null); 

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
    // ASTVerifier.g:134:1: optionValue returns [String v] : ( ID | STRING_LITERAL | CHAR_LITERAL | INT | STAR );
    public final ASTVerifier.optionValue_return optionValue() throws RecognitionException {
        ASTVerifier.optionValue_return retval = new ASTVerifier.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // ASTVerifier.g:136:5: ( ID | STRING_LITERAL | CHAR_LITERAL | INT | STAR )
            // ASTVerifier.g:
            {
            if ( input.LA(1)==STAR||input.LA(1)==INT||input.LA(1)==STRING_LITERAL||input.LA(1)==CHAR_LITERAL||input.LA(1)==ID ) {
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
        return retval;
    }
    // $ANTLR end "optionValue"


    // $ANTLR start "delegateGrammars"
    // ASTVerifier.g:143:1: delegateGrammars : ^( IMPORT ( delegateGrammar )+ ) ;
    public final void delegateGrammars() throws RecognitionException {
        try {
            // ASTVerifier.g:144:2: ( ^( IMPORT ( delegateGrammar )+ ) )
            // ASTVerifier.g:144:6: ^( IMPORT ( delegateGrammar )+ )
            {
            match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars309); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:144:15: ( delegateGrammar )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ASSIGN||LA5_0==ID) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ASTVerifier.g:144:15: delegateGrammar
            	    {
            	    pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars311);
            	    delegateGrammar();

            	    state._fsp--;


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
    // $ANTLR end "delegateGrammars"


    // $ANTLR start "delegateGrammar"
    // ASTVerifier.g:147:1: delegateGrammar : ( ^( ASSIGN ID ID ) | ID );
    public final void delegateGrammar() throws RecognitionException {
        try {
            // ASTVerifier.g:148:5: ( ^( ASSIGN ID ID ) | ID )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==ASSIGN) ) {
                alt6=1;
            }
            else if ( (LA6_0==ID) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // ASTVerifier.g:148:9: ^( ASSIGN ID ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar330); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_delegateGrammar332); 
                    match(input,ID,FOLLOW_ID_in_delegateGrammar334); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:149:9: ID
                    {
                    match(input,ID,FOLLOW_ID_in_delegateGrammar345); 

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
    // $ANTLR end "delegateGrammar"


    // $ANTLR start "tokensSpec"
    // ASTVerifier.g:152:1: tokensSpec : ^( TOKENS ( tokenSpec )+ ) ;
    public final void tokensSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:153:2: ( ^( TOKENS ( tokenSpec )+ ) )
            // ASTVerifier.g:153:6: ^( TOKENS ( tokenSpec )+ )
            {
            match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec362); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:153:15: ( tokenSpec )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==ASSIGN||(LA7_0>=TOKEN_REF && LA7_0<=RULE_REF)) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ASTVerifier.g:153:15: tokenSpec
            	    {
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec364);
            	    tokenSpec();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "tokensSpec"


    // $ANTLR start "tokenSpec"
    // ASTVerifier.g:156:1: tokenSpec : ( ^( ASSIGN TOKEN_REF STRING_LITERAL ) | ^( ASSIGN TOKEN_REF CHAR_LITERAL ) | TOKEN_REF | RULE_REF );
    public final void tokenSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:157:2: ( ^( ASSIGN TOKEN_REF STRING_LITERAL ) | ^( ASSIGN TOKEN_REF CHAR_LITERAL ) | TOKEN_REF | RULE_REF )
            int alt8=4;
            switch ( input.LA(1) ) {
            case ASSIGN:
                {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==DOWN) ) {
                    int LA8_4 = input.LA(3);

                    if ( (LA8_4==TOKEN_REF) ) {
                        int LA8_5 = input.LA(4);

                        if ( (LA8_5==STRING_LITERAL) ) {
                            alt8=1;
                        }
                        else if ( (LA8_5==CHAR_LITERAL) ) {
                            alt8=2;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 8, 5, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 8, 4, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;
                }
                }
                break;
            case TOKEN_REF:
                {
                alt8=3;
                }
                break;
            case RULE_REF:
                {
                alt8=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ASTVerifier.g:157:4: ^( ASSIGN TOKEN_REF STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec378); 

                    match(input, Token.DOWN, null); 
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenSpec380); 
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec382); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:158:9: ^( ASSIGN TOKEN_REF CHAR_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec394); 

                    match(input, Token.DOWN, null); 
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenSpec396); 
                    match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_tokenSpec398); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:159:6: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenSpec406); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:160:4: RULE_REF
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_tokenSpec411); 

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
    // $ANTLR end "tokenSpec"


    // $ANTLR start "attrScope"
    // ASTVerifier.g:163:1: attrScope : ^( SCOPE ID ACTION ) ;
    public final void attrScope() throws RecognitionException {
        try {
            // ASTVerifier.g:164:2: ( ^( SCOPE ID ACTION ) )
            // ASTVerifier.g:164:4: ^( SCOPE ID ACTION )
            {
            match(input,SCOPE,FOLLOW_SCOPE_in_attrScope423); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_attrScope425); 
            match(input,ACTION,FOLLOW_ACTION_in_attrScope427); 

            match(input, Token.UP, null); 

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
    // $ANTLR end "attrScope"


    // $ANTLR start "action"
    // ASTVerifier.g:167:1: action : ^( AT ( ID )? ID ACTION ) ;
    public final void action() throws RecognitionException {
        try {
            // ASTVerifier.g:168:2: ( ^( AT ( ID )? ID ACTION ) )
            // ASTVerifier.g:168:4: ^( AT ( ID )? ID ACTION )
            {
            match(input,AT,FOLLOW_AT_in_action440); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:168:9: ( ID )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==ID) ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==ID) ) {
                    alt9=1;
                }
            }
            switch (alt9) {
                case 1 :
                    // ASTVerifier.g:168:9: ID
                    {
                    match(input,ID,FOLLOW_ID_in_action442); 

                    }
                    break;

            }

            match(input,ID,FOLLOW_ID_in_action445); 
            match(input,ACTION,FOLLOW_ACTION_in_action447); 

            match(input, Token.UP, null); 

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


    // $ANTLR start "rules"
    // ASTVerifier.g:171:1: rules : ^( RULES ( rule )* ) ;
    public final void rules() throws RecognitionException {
        try {
            // ASTVerifier.g:172:5: ( ^( RULES ( rule )* ) )
            // ASTVerifier.g:172:7: ^( RULES ( rule )* )
            {
            match(input,RULES,FOLLOW_RULES_in_rules463); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // ASTVerifier.g:172:15: ( rule )*
                loop10:
                do {
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==RULE) ) {
                        alt10=1;
                    }


                    switch (alt10) {
                	case 1 :
                	    // ASTVerifier.g:172:15: rule
                	    {
                	    pushFollow(FOLLOW_rule_in_rules465);
                	    rule();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop10;
                    }
                } while (true);


                match(input, Token.UP, null); 
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
    // $ANTLR end "rules"


    // $ANTLR start "rule"
    // ASTVerifier.g:175:1: rule : ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup ) ;
    public final void rule() throws RecognitionException {
        try {
            // ASTVerifier.g:175:5: ( ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup ) )
            // ASTVerifier.g:175:9: ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup )
            {
            match(input,RULE,FOLLOW_RULE_in_rule483); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rule485); 
            // ASTVerifier.g:175:20: ( DOC_COMMENT )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==DOC_COMMENT) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ASTVerifier.g:175:20: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_rule487); 

                    }
                    break;

            }

            // ASTVerifier.g:175:33: ( ruleModifiers )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULEMODIFIERS) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ASTVerifier.g:175:33: ruleModifiers
                    {
                    pushFollow(FOLLOW_ruleModifiers_in_rule490);
                    ruleModifiers();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:175:48: ( ARG_ACTION )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ARG_ACTION) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ASTVerifier.g:175:48: ARG_ACTION
                    {
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule493); 

                    }
                    break;

            }

            // ASTVerifier.g:176:11: ( ruleReturns )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RETURNS) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ASTVerifier.g:176:11: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_rule506);
                    ruleReturns();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:176:24: ( rulePrequel )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==OPTIONS||LA15_0==SCOPE||LA15_0==THROWS||LA15_0==AT) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ASTVerifier.g:176:24: rulePrequel
            	    {
            	    pushFollow(FOLLOW_rulePrequel_in_rule509);
            	    rulePrequel();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            pushFollow(FOLLOW_altListAsBlock_in_rule512);
            altListAsBlock();

            state._fsp--;

            pushFollow(FOLLOW_exceptionGroup_in_rule514);
            exceptionGroup();

            state._fsp--;


            match(input, Token.UP, null); 

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


    // $ANTLR start "exceptionGroup"
    // ASTVerifier.g:180:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
    public final void exceptionGroup() throws RecognitionException {
        try {
            // ASTVerifier.g:181:5: ( ( exceptionHandler )* ( finallyClause )? )
            // ASTVerifier.g:181:7: ( exceptionHandler )* ( finallyClause )?
            {
            // ASTVerifier.g:181:7: ( exceptionHandler )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==CATCH) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ASTVerifier.g:181:7: exceptionHandler
            	    {
            	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup541);
            	    exceptionHandler();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            // ASTVerifier.g:181:25: ( finallyClause )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==FINALLY) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ASTVerifier.g:181:25: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup544);
                    finallyClause();

                    state._fsp--;


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
    // $ANTLR end "exceptionGroup"


    // $ANTLR start "exceptionHandler"
    // ASTVerifier.g:184:1: exceptionHandler : ^( CATCH ARG_ACTION ACTION ) ;
    public final void exceptionHandler() throws RecognitionException {
        try {
            // ASTVerifier.g:185:2: ( ^( CATCH ARG_ACTION ACTION ) )
            // ASTVerifier.g:185:4: ^( CATCH ARG_ACTION ACTION )
            {
            match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler560); 

            match(input, Token.DOWN, null); 
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler562); 
            match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler564); 

            match(input, Token.UP, null); 

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
    // ASTVerifier.g:188:1: finallyClause : ^( FINALLY ACTION ) ;
    public final void finallyClause() throws RecognitionException {
        try {
            // ASTVerifier.g:189:2: ( ^( FINALLY ACTION ) )
            // ASTVerifier.g:189:4: ^( FINALLY ACTION )
            {
            match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause577); 

            match(input, Token.DOWN, null); 
            match(input,ACTION,FOLLOW_ACTION_in_finallyClause579); 

            match(input, Token.UP, null); 

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


    // $ANTLR start "rulePrequel"
    // ASTVerifier.g:192:1: rulePrequel : ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction );
    public final void rulePrequel() throws RecognitionException {
        try {
            // ASTVerifier.g:193:5: ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction )
            int alt18=4;
            switch ( input.LA(1) ) {
            case THROWS:
                {
                alt18=1;
                }
                break;
            case SCOPE:
                {
                alt18=2;
                }
                break;
            case OPTIONS:
                {
                alt18=3;
                }
                break;
            case AT:
                {
                alt18=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // ASTVerifier.g:193:9: throwsSpec
                    {
                    pushFollow(FOLLOW_throwsSpec_in_rulePrequel596);
                    throwsSpec();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:194:9: ruleScopeSpec
                    {
                    pushFollow(FOLLOW_ruleScopeSpec_in_rulePrequel606);
                    ruleScopeSpec();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:195:9: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_rulePrequel616);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:196:9: ruleAction
                    {
                    pushFollow(FOLLOW_ruleAction_in_rulePrequel626);
                    ruleAction();

                    state._fsp--;


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
    // $ANTLR end "rulePrequel"


    // $ANTLR start "ruleReturns"
    // ASTVerifier.g:199:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
    public final void ruleReturns() throws RecognitionException {
        try {
            // ASTVerifier.g:200:2: ( ^( RETURNS ARG_ACTION ) )
            // ASTVerifier.g:200:4: ^( RETURNS ARG_ACTION )
            {
            match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns641); 

            match(input, Token.DOWN, null); 
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns643); 

            match(input, Token.UP, null); 

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


    // $ANTLR start "throwsSpec"
    // ASTVerifier.g:202:1: throwsSpec : ^( THROWS ( ID )+ ) ;
    public final void throwsSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:203:5: ( ^( THROWS ( ID )+ ) )
            // ASTVerifier.g:203:7: ^( THROWS ( ID )+ )
            {
            match(input,THROWS,FOLLOW_THROWS_in_throwsSpec658); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:203:16: ( ID )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==ID) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // ASTVerifier.g:203:16: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_throwsSpec660); 

            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "throwsSpec"


    // $ANTLR start "ruleScopeSpec"
    // ASTVerifier.g:206:1: ruleScopeSpec : ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) );
    public final void ruleScopeSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:207:2: ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==SCOPE) ) {
                int LA21_1 = input.LA(2);

                if ( (LA21_1==DOWN) ) {
                    int LA21_2 = input.LA(3);

                    if ( (LA21_2==ACTION) ) {
                        alt21=1;
                    }
                    else if ( (LA21_2==ID) ) {
                        alt21=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 21, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // ASTVerifier.g:207:4: ^( SCOPE ACTION )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec677); 

                    match(input, Token.DOWN, null); 
                    match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec679); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:208:4: ^( SCOPE ( ID )+ )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec686); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:208:12: ( ID )+
                    int cnt20=0;
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( (LA20_0==ID) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // ASTVerifier.g:208:12: ID
                    	    {
                    	    match(input,ID,FOLLOW_ID_in_ruleScopeSpec688); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt20 >= 1 ) break loop20;
                                EarlyExitException eee =
                                    new EarlyExitException(20, input);
                                throw eee;
                        }
                        cnt20++;
                    } while (true);


                    match(input, Token.UP, null); 

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
    // $ANTLR end "ruleScopeSpec"


    // $ANTLR start "ruleAction"
    // ASTVerifier.g:211:1: ruleAction : ^( AT ID ACTION ) ;
    public final void ruleAction() throws RecognitionException {
        try {
            // ASTVerifier.g:212:2: ( ^( AT ID ACTION ) )
            // ASTVerifier.g:212:4: ^( AT ID ACTION )
            {
            match(input,AT,FOLLOW_AT_in_ruleAction702); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_ruleAction704); 
            match(input,ACTION,FOLLOW_ACTION_in_ruleAction706); 

            match(input, Token.UP, null); 

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


    // $ANTLR start "ruleModifiers"
    // ASTVerifier.g:215:1: ruleModifiers : ^( RULEMODIFIERS ( ruleModifier )+ ) ;
    public final void ruleModifiers() throws RecognitionException {
        try {
            // ASTVerifier.g:216:5: ( ^( RULEMODIFIERS ( ruleModifier )+ ) )
            // ASTVerifier.g:216:7: ^( RULEMODIFIERS ( ruleModifier )+ )
            {
            match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_ruleModifiers722); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:216:23: ( ruleModifier )+
            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==FRAGMENT||(LA22_0>=PROTECTED && LA22_0<=PRIVATE)) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // ASTVerifier.g:216:23: ruleModifier
            	    {
            	    pushFollow(FOLLOW_ruleModifier_in_ruleModifiers724);
            	    ruleModifier();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt22 >= 1 ) break loop22;
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "ruleModifiers"


    // $ANTLR start "ruleModifier"
    // ASTVerifier.g:219:1: ruleModifier : ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT );
    public final void ruleModifier() throws RecognitionException {
        try {
            // ASTVerifier.g:220:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
            // ASTVerifier.g:
            {
            if ( input.LA(1)==FRAGMENT||(input.LA(1)>=PROTECTED && input.LA(1)<=PRIVATE) ) {
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
    // $ANTLR end "ruleModifier"


    // $ANTLR start "altList"
    // ASTVerifier.g:226:1: altList : ( alternative )+ ;
    public final void altList() throws RecognitionException {
        try {
            // ASTVerifier.g:227:5: ( ( alternative )+ )
            // ASTVerifier.g:227:7: ( alternative )+
            {
            // ASTVerifier.g:227:7: ( alternative )+
            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==ALT||LA23_0==ALT_REWRITE) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ASTVerifier.g:227:7: alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_altList784);
            	    alternative();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);


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
    // $ANTLR end "altList"


    // $ANTLR start "altListAsBlock"
    // ASTVerifier.g:230:1: altListAsBlock : ^( BLOCK altList ) ;
    public final void altListAsBlock() throws RecognitionException {
        try {
            // ASTVerifier.g:231:5: ( ^( BLOCK altList ) )
            // ASTVerifier.g:231:7: ^( BLOCK altList )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_altListAsBlock803); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_altList_in_altListAsBlock805);
            altList();

            state._fsp--;


            match(input, Token.UP, null); 

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
    // $ANTLR end "altListAsBlock"


    // $ANTLR start "alternative"
    // ASTVerifier.g:234:1: alternative : ( ^( ALT_REWRITE alternative rewrite ) | ^( ALT EPSILON ) | elements );
    public final void alternative() throws RecognitionException {
        try {
            // ASTVerifier.g:235:5: ( ^( ALT_REWRITE alternative rewrite ) | ^( ALT EPSILON ) | elements )
            int alt24=3;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==ALT_REWRITE) ) {
                alt24=1;
            }
            else if ( (LA24_0==ALT) ) {
                int LA24_2 = input.LA(2);

                if ( (LA24_2==DOWN) ) {
                    int LA24_3 = input.LA(3);

                    if ( (LA24_3==EPSILON) ) {
                        alt24=2;
                    }
                    else if ( (LA24_3==SEMPRED||LA24_3==ACTION||LA24_3==IMPLIES||LA24_3==ASSIGN||LA24_3==BANG||LA24_3==PLUS_ASSIGN||LA24_3==ROOT||(LA24_3>=WILDCARD && LA24_3<=RANGE)||LA24_3==TREE_BEGIN||(LA24_3>=TOKEN_REF && LA24_3<=RULE_REF)||LA24_3==STRING_LITERAL||LA24_3==CHAR_LITERAL||(LA24_3>=BLOCK && LA24_3<=POSITIVE_CLOSURE)||LA24_3==GATED_SEMPRED||LA24_3==DOT) ) {
                        alt24=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 24, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // ASTVerifier.g:235:7: ^( ALT_REWRITE alternative rewrite )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative824); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative826);
                    alternative();

                    state._fsp--;

                    pushFollow(FOLLOW_rewrite_in_alternative828);
                    rewrite();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:236:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative838); 

                    match(input, Token.DOWN, null); 
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative840); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:237:9: elements
                    {
                    pushFollow(FOLLOW_elements_in_alternative851);
                    elements();

                    state._fsp--;


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
    // $ANTLR end "alternative"


    // $ANTLR start "elements"
    // ASTVerifier.g:240:1: elements : ^( ALT ( element )+ ) ;
    public final void elements() throws RecognitionException {
        try {
            // ASTVerifier.g:241:5: ( ^( ALT ( element )+ ) )
            // ASTVerifier.g:241:7: ^( ALT ( element )+ )
            {
            match(input,ALT,FOLLOW_ALT_in_elements869); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:241:13: ( element )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==SEMPRED||LA25_0==ACTION||LA25_0==IMPLIES||LA25_0==ASSIGN||LA25_0==BANG||LA25_0==PLUS_ASSIGN||LA25_0==ROOT||(LA25_0>=WILDCARD && LA25_0<=RANGE)||LA25_0==TREE_BEGIN||(LA25_0>=TOKEN_REF && LA25_0<=RULE_REF)||LA25_0==STRING_LITERAL||LA25_0==CHAR_LITERAL||(LA25_0>=BLOCK && LA25_0<=POSITIVE_CLOSURE)||LA25_0==GATED_SEMPRED||LA25_0==DOT) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ASTVerifier.g:241:13: element
            	    {
            	    pushFollow(FOLLOW_element_in_elements871);
            	    element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt25 >= 1 ) break loop25;
                        EarlyExitException eee =
                            new EarlyExitException(25, input);
                        throw eee;
                }
                cnt25++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "elements"


    // $ANTLR start "element"
    // ASTVerifier.g:244:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final void element() throws RecognitionException {
        try {
            // ASTVerifier.g:245:2: ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt26=7;
            alt26 = dfa26.predict(input);
            switch (alt26) {
                case 1 :
                    // ASTVerifier.g:245:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element887);
                    labeledElement();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:246:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element892);
                    atom();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:247:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element897);
                    ebnf();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:248:6: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_element904); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:249:6: SEMPRED
                    {
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_element911); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:250:4: GATED_SEMPRED
                    {
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element916); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:251:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element921);
                    treeSpec();

                    state._fsp--;


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
    // $ANTLR end "element"


    // $ANTLR start "labeledElement"
    // ASTVerifier.g:254:1: labeledElement : ( ^( ASSIGN ID ( atom | block ) ) | ^( PLUS_ASSIGN ID ( atom | block ) ) );
    public final void labeledElement() throws RecognitionException {
        try {
            // ASTVerifier.g:255:2: ( ^( ASSIGN ID ( atom | block ) ) | ^( PLUS_ASSIGN ID ( atom | block ) ) )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==ASSIGN) ) {
                alt29=1;
            }
            else if ( (LA29_0==PLUS_ASSIGN) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // ASTVerifier.g:255:4: ^( ASSIGN ID ( atom | block ) )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement934); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement936); 
                    // ASTVerifier.g:255:16: ( atom | block )
                    int alt27=2;
                    int LA27_0 = input.LA(1);

                    if ( (LA27_0==BANG||LA27_0==ROOT||(LA27_0>=WILDCARD && LA27_0<=RANGE)||(LA27_0>=TOKEN_REF && LA27_0<=RULE_REF)||LA27_0==STRING_LITERAL||LA27_0==CHAR_LITERAL||LA27_0==DOT) ) {
                        alt27=1;
                    }
                    else if ( (LA27_0==BLOCK) ) {
                        alt27=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 27, 0, input);

                        throw nvae;
                    }
                    switch (alt27) {
                        case 1 :
                            // ASTVerifier.g:255:17: atom
                            {
                            pushFollow(FOLLOW_atom_in_labeledElement939);
                            atom();

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // ASTVerifier.g:255:22: block
                            {
                            pushFollow(FOLLOW_block_in_labeledElement941);
                            block();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:256:4: ^( PLUS_ASSIGN ID ( atom | block ) )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement949); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement951); 
                    // ASTVerifier.g:256:21: ( atom | block )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==BANG||LA28_0==ROOT||(LA28_0>=WILDCARD && LA28_0<=RANGE)||(LA28_0>=TOKEN_REF && LA28_0<=RULE_REF)||LA28_0==STRING_LITERAL||LA28_0==CHAR_LITERAL||LA28_0==DOT) ) {
                        alt28=1;
                    }
                    else if ( (LA28_0==BLOCK) ) {
                        alt28=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 0, input);

                        throw nvae;
                    }
                    switch (alt28) {
                        case 1 :
                            // ASTVerifier.g:256:22: atom
                            {
                            pushFollow(FOLLOW_atom_in_labeledElement954);
                            atom();

                            state._fsp--;


                            }
                            break;
                        case 2 :
                            // ASTVerifier.g:256:27: block
                            {
                            pushFollow(FOLLOW_block_in_labeledElement956);
                            block();

                            state._fsp--;


                            }
                            break;

                    }


                    match(input, Token.UP, null); 

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
    // $ANTLR end "labeledElement"


    // $ANTLR start "treeSpec"
    // ASTVerifier.g:259:1: treeSpec : ^( TREE_BEGIN ( element )+ ) ;
    public final void treeSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:260:5: ( ^( TREE_BEGIN ( element )+ ) )
            // ASTVerifier.g:260:7: ^( TREE_BEGIN ( element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec973); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:260:20: ( element )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==SEMPRED||LA30_0==ACTION||LA30_0==IMPLIES||LA30_0==ASSIGN||LA30_0==BANG||LA30_0==PLUS_ASSIGN||LA30_0==ROOT||(LA30_0>=WILDCARD && LA30_0<=RANGE)||LA30_0==TREE_BEGIN||(LA30_0>=TOKEN_REF && LA30_0<=RULE_REF)||LA30_0==STRING_LITERAL||LA30_0==CHAR_LITERAL||(LA30_0>=BLOCK && LA30_0<=POSITIVE_CLOSURE)||LA30_0==GATED_SEMPRED||LA30_0==DOT) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // ASTVerifier.g:260:20: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec975);
            	    element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "treeSpec"


    // $ANTLR start "ebnf"
    // ASTVerifier.g:263:1: ebnf : ( ^( blockSuffix block ) | block );
    public final void ebnf() throws RecognitionException {
        try {
            // ASTVerifier.g:263:5: ( ^( blockSuffix block ) | block )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==IMPLIES||LA31_0==BANG||LA31_0==ROOT||(LA31_0>=OPTIONAL && LA31_0<=POSITIVE_CLOSURE)) ) {
                alt31=1;
            }
            else if ( (LA31_0==BLOCK) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // ASTVerifier.g:263:7: ^( blockSuffix block )
                    {
                    pushFollow(FOLLOW_blockSuffix_in_ebnf990);
                    blockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf992);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:264:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf999);
                    block();

                    state._fsp--;


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
    // $ANTLR end "ebnf"


    // $ANTLR start "blockSuffix"
    // ASTVerifier.g:267:1: blockSuffix : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final void blockSuffix() throws RecognitionException {
        try {
            // ASTVerifier.g:268:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
            int alt32=4;
            switch ( input.LA(1) ) {
            case OPTIONAL:
            case CLOSURE:
            case POSITIVE_CLOSURE:
                {
                alt32=1;
                }
                break;
            case ROOT:
                {
                alt32=2;
                }
                break;
            case IMPLIES:
                {
                alt32=3;
                }
                break;
            case BANG:
                {
                alt32=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // ASTVerifier.g:268:7: ebnfSuffix
                    {
                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffix1016);
                    ebnfSuffix();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:269:7: ROOT
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_blockSuffix1024); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:270:7: IMPLIES
                    {
                    match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffix1032); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:271:7: BANG
                    {
                    match(input,BANG,FOLLOW_BANG_in_blockSuffix1040); 

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
    // $ANTLR end "blockSuffix"


    // $ANTLR start "ebnfSuffix"
    // ASTVerifier.g:274:1: ebnfSuffix : ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE );
    public final void ebnfSuffix() throws RecognitionException {
        try {
            // ASTVerifier.g:275:2: ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE )
            // ASTVerifier.g:
            {
            if ( (input.LA(1)>=OPTIONAL && input.LA(1)<=POSITIVE_CLOSURE) ) {
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
    // $ANTLR end "ebnfSuffix"


    // $ANTLR start "atom"
    // ASTVerifier.g:280:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );
    public final void atom() throws RecognitionException {
        try {
            // ASTVerifier.g:280:5: ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref )
            int alt33=9;
            alt33 = dfa33.predict(input);
            switch (alt33) {
                case 1 :
                    // ASTVerifier.g:280:7: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom1080); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom1082);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:281:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom1089); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom1091);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:282:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom1098); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom1100);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:283:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom1107); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom1109);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:284:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom1115);
                    range();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // ASTVerifier.g:285:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom1121); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom1123); 
                    pushFollow(FOLLOW_terminal_in_atom1125);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:286:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom1132); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom1134); 
                    pushFollow(FOLLOW_ruleref_in_atom1136);
                    ruleref();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // ASTVerifier.g:287:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom1147);
                    terminal();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // ASTVerifier.g:288:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom1157);
                    ruleref();

                    state._fsp--;


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
    // $ANTLR end "atom"


    // $ANTLR start "notSet"
    // ASTVerifier.g:291:1: notSet : ( ^( NOT notTerminal ) | ^( NOT block ) );
    public final void notSet() throws RecognitionException {
        try {
            // ASTVerifier.g:292:5: ( ^( NOT notTerminal ) | ^( NOT block ) )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==NOT) ) {
                int LA34_1 = input.LA(2);

                if ( (LA34_1==DOWN) ) {
                    int LA34_2 = input.LA(3);

                    if ( (LA34_2==BLOCK) ) {
                        alt34=2;
                    }
                    else if ( (LA34_2==TOKEN_REF||LA34_2==STRING_LITERAL||LA34_2==CHAR_LITERAL) ) {
                        alt34=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 34, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 34, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // ASTVerifier.g:292:7: ^( NOT notTerminal )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet1175); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notTerminal_in_notSet1177);
                    notTerminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:293:7: ^( NOT block )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet1187); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet1189);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

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
    // $ANTLR end "notSet"


    // $ANTLR start "notTerminal"
    // ASTVerifier.g:296:1: notTerminal : ( CHAR_LITERAL | TOKEN_REF | STRING_LITERAL );
    public final void notTerminal() throws RecognitionException {
        try {
            // ASTVerifier.g:297:5: ( CHAR_LITERAL | TOKEN_REF | STRING_LITERAL )
            // ASTVerifier.g:
            {
            if ( input.LA(1)==TOKEN_REF||input.LA(1)==STRING_LITERAL||input.LA(1)==CHAR_LITERAL ) {
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
    // $ANTLR end "notTerminal"


    // $ANTLR start "block"
    // ASTVerifier.g:302:1: block : ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? altList ) ;
    public final void block() throws RecognitionException {
        try {
            // ASTVerifier.g:303:5: ( ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? altList ) )
            // ASTVerifier.g:303:7: ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? altList )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block1241); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:303:15: ( optionsSpec )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==OPTIONS) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // ASTVerifier.g:303:15: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_block1243);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:303:28: ( ruleAction )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==AT) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // ASTVerifier.g:303:28: ruleAction
            	    {
            	    pushFollow(FOLLOW_ruleAction_in_block1246);
            	    ruleAction();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            // ASTVerifier.g:303:40: ( ACTION )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==ACTION) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // ASTVerifier.g:303:40: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_block1249); 

                    }
                    break;

            }

            pushFollow(FOLLOW_altList_in_block1252);
            altList();

            state._fsp--;


            match(input, Token.UP, null); 

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
    // $ANTLR end "block"


    // $ANTLR start "ruleref"
    // ASTVerifier.g:306:1: ruleref : ( ^( ROOT RULE_REF ( ARG_ACTION )? ) | ^( BANG RULE_REF ( ARG_ACTION )? ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final void ruleref() throws RecognitionException {
        try {
            // ASTVerifier.g:307:5: ( ^( ROOT RULE_REF ( ARG_ACTION )? ) | ^( BANG RULE_REF ( ARG_ACTION )? ) | ^( RULE_REF ( ARG_ACTION )? ) )
            int alt41=3;
            switch ( input.LA(1) ) {
            case ROOT:
                {
                alt41=1;
                }
                break;
            case BANG:
                {
                alt41=2;
                }
                break;
            case RULE_REF:
                {
                alt41=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // ASTVerifier.g:307:7: ^( ROOT RULE_REF ( ARG_ACTION )? )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref1271); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1273); 
                    // ASTVerifier.g:307:23: ( ARG_ACTION )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==ARG_ACTION) ) {
                        alt38=1;
                    }
                    switch (alt38) {
                        case 1 :
                            // ASTVerifier.g:307:23: ARG_ACTION
                            {
                            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1275); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:308:7: ^( BANG RULE_REF ( ARG_ACTION )? )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref1286); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1288); 
                    // ASTVerifier.g:308:23: ( ARG_ACTION )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);

                    if ( (LA39_0==ARG_ACTION) ) {
                        alt39=1;
                    }
                    switch (alt39) {
                        case 1 :
                            // ASTVerifier.g:308:23: ARG_ACTION
                            {
                            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1290); 

                            }
                            break;

                    }


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:309:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1301); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // ASTVerifier.g:309:18: ( ARG_ACTION )?
                        int alt40=2;
                        int LA40_0 = input.LA(1);

                        if ( (LA40_0==ARG_ACTION) ) {
                            alt40=1;
                        }
                        switch (alt40) {
                            case 1 :
                                // ASTVerifier.g:309:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1303); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
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
    // $ANTLR end "ruleref"


    // $ANTLR start "range"
    // ASTVerifier.g:312:1: range : ^( RANGE rangeElement rangeElement ) ;
    public final void range() throws RecognitionException {
        try {
            // ASTVerifier.g:313:5: ( ^( RANGE rangeElement rangeElement ) )
            // ASTVerifier.g:313:7: ^( RANGE rangeElement rangeElement )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range1323); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rangeElement_in_range1325);
            rangeElement();

            state._fsp--;

            pushFollow(FOLLOW_rangeElement_in_range1327);
            rangeElement();

            state._fsp--;


            match(input, Token.UP, null); 

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
    // $ANTLR end "range"


    // $ANTLR start "rangeElement"
    // ASTVerifier.g:316:1: rangeElement : ( CHAR_LITERAL | STRING_LITERAL | RULE_REF | TOKEN_REF );
    public final void rangeElement() throws RecognitionException {
        try {
            // ASTVerifier.g:317:5: ( CHAR_LITERAL | STRING_LITERAL | RULE_REF | TOKEN_REF )
            // ASTVerifier.g:
            {
            if ( (input.LA(1)>=TOKEN_REF && input.LA(1)<=RULE_REF)||input.LA(1)==STRING_LITERAL||input.LA(1)==CHAR_LITERAL ) {
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
    // $ANTLR end "rangeElement"


    // $ANTLR start "terminal"
    // ASTVerifier.g:323:1: terminal : ( ^( CHAR_LITERAL elementOptions ) | CHAR_LITERAL | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF elementOptions ) | TOKEN_REF | ^( WILDCARD elementOptions ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) );
    public final void terminal() throws RecognitionException {
        try {
            // ASTVerifier.g:324:5: ( ^( CHAR_LITERAL elementOptions ) | CHAR_LITERAL | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF elementOptions ) | TOKEN_REF | ^( WILDCARD elementOptions ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) )
            int alt42=10;
            alt42 = dfa42.predict(input);
            switch (alt42) {
                case 1 :
                    // ASTVerifier.g:324:9: ^( CHAR_LITERAL elementOptions )
                    {
                    match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_terminal1389); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1391);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:325:9: CHAR_LITERAL
                    {
                    match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_terminal1402); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:326:7: ^( STRING_LITERAL elementOptions )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1411); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1413);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:327:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1422); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:328:7: ^( TOKEN_REF elementOptions )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1431); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1433);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:329:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1442); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:330:7: ^( WILDCARD elementOptions )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal1451); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1453);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // ASTVerifier.g:331:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal1462); 

                    }
                    break;
                case 9 :
                    // ASTVerifier.g:332:7: ^( ROOT terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal1471); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal1473);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // ASTVerifier.g:333:7: ^( BANG terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal1483); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal1485);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

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
    // $ANTLR end "terminal"


    // $ANTLR start "elementOptions"
    // ASTVerifier.g:336:1: elementOptions : ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
    public final void elementOptions() throws RecognitionException {
        try {
            // ASTVerifier.g:337:5: ( ^( ELEMENT_OPTIONS ( elementOption )+ ) )
            // ASTVerifier.g:337:7: ^( ELEMENT_OPTIONS ( elementOption )+ )
            {
            match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOptions1504); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:337:25: ( elementOption )+
            int cnt43=0;
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==ASSIGN||LA43_0==ID) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // ASTVerifier.g:337:25: elementOption
            	    {
            	    pushFollow(FOLLOW_elementOption_in_elementOptions1506);
            	    elementOption();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt43 >= 1 ) break loop43;
                        EarlyExitException eee =
                            new EarlyExitException(43, input);
                        throw eee;
                }
                cnt43++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "elementOptions"


    // $ANTLR start "elementOption"
    // ASTVerifier.g:340:1: elementOption : ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) );
    public final void elementOption() throws RecognitionException {
        try {
            // ASTVerifier.g:341:5: ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) )
            int alt44=3;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==ID) ) {
                alt44=1;
            }
            else if ( (LA44_0==ASSIGN) ) {
                int LA44_2 = input.LA(2);

                if ( (LA44_2==DOWN) ) {
                    int LA44_3 = input.LA(3);

                    if ( (LA44_3==ID) ) {
                        int LA44_4 = input.LA(4);

                        if ( (LA44_4==ID) ) {
                            alt44=2;
                        }
                        else if ( (LA44_4==STRING_LITERAL) ) {
                            alt44=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 44, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // ASTVerifier.g:341:7: ID
                    {
                    match(input,ID,FOLLOW_ID_in_elementOption1525); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:342:9: ^( ASSIGN ID ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1536); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_elementOption1538); 
                    match(input,ID,FOLLOW_ID_in_elementOption1540); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:343:9: ^( ASSIGN ID STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1552); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_elementOption1554); 
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption1556); 

                    match(input, Token.UP, null); 

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
    // $ANTLR end "elementOption"


    // $ANTLR start "rewrite"
    // ASTVerifier.g:346:1: rewrite : ( predicatedRewrite )* nakedRewrite ;
    public final void rewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:347:2: ( ( predicatedRewrite )* nakedRewrite )
            // ASTVerifier.g:347:4: ( predicatedRewrite )* nakedRewrite
            {
            // ASTVerifier.g:347:4: ( predicatedRewrite )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==ST_RESULT) ) {
                    int LA45_1 = input.LA(2);

                    if ( (LA45_1==DOWN) ) {
                        int LA45_3 = input.LA(3);

                        if ( (LA45_3==SEMPRED) ) {
                            alt45=1;
                        }


                    }


                }
                else if ( (LA45_0==RESULT) ) {
                    int LA45_2 = input.LA(2);

                    if ( (LA45_2==DOWN) ) {
                        int LA45_4 = input.LA(3);

                        if ( (LA45_4==SEMPRED) ) {
                            alt45=1;
                        }


                    }


                }


                switch (alt45) {
            	case 1 :
            	    // ASTVerifier.g:347:4: predicatedRewrite
            	    {
            	    pushFollow(FOLLOW_predicatedRewrite_in_rewrite1571);
            	    predicatedRewrite();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);

            pushFollow(FOLLOW_nakedRewrite_in_rewrite1574);
            nakedRewrite();

            state._fsp--;


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
    // $ANTLR end "rewrite"


    // $ANTLR start "predicatedRewrite"
    // ASTVerifier.g:350:1: predicatedRewrite : ( ^( ST_RESULT SEMPRED rewriteAlt ) | ^( RESULT SEMPRED rewriteAlt ) );
    public final void predicatedRewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:351:2: ( ^( ST_RESULT SEMPRED rewriteAlt ) | ^( RESULT SEMPRED rewriteAlt ) )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==ST_RESULT) ) {
                alt46=1;
            }
            else if ( (LA46_0==RESULT) ) {
                alt46=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // ASTVerifier.g:351:4: ^( ST_RESULT SEMPRED rewriteAlt )
                    {
                    match(input,ST_RESULT,FOLLOW_ST_RESULT_in_predicatedRewrite1586); 

                    match(input, Token.DOWN, null); 
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite1588); 
                    pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite1590);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:352:4: ^( RESULT SEMPRED rewriteAlt )
                    {
                    match(input,RESULT,FOLLOW_RESULT_in_predicatedRewrite1597); 

                    match(input, Token.DOWN, null); 
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite1599); 
                    pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite1601);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

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
    // $ANTLR end "predicatedRewrite"


    // $ANTLR start "nakedRewrite"
    // ASTVerifier.g:355:1: nakedRewrite : ( ^( ST_RESULT rewriteAlt ) | ^( RESULT rewriteAlt ) );
    public final void nakedRewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:356:2: ( ^( ST_RESULT rewriteAlt ) | ^( RESULT rewriteAlt ) )
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==ST_RESULT) ) {
                alt47=1;
            }
            else if ( (LA47_0==RESULT) ) {
                alt47=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // ASTVerifier.g:356:4: ^( ST_RESULT rewriteAlt )
                    {
                    match(input,ST_RESULT,FOLLOW_ST_RESULT_in_nakedRewrite1615); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite1617);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:357:4: ^( RESULT rewriteAlt )
                    {
                    match(input,RESULT,FOLLOW_RESULT_in_nakedRewrite1624); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite1626);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

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
    // $ANTLR end "nakedRewrite"


    // $ANTLR start "rewriteAlt"
    // ASTVerifier.g:360:1: rewriteAlt : ( rewriteTemplate | rewriteTreeAlt | ETC | EPSILON );
    public final void rewriteAlt() throws RecognitionException {
        try {
            // ASTVerifier.g:361:5: ( rewriteTemplate | rewriteTreeAlt | ETC | EPSILON )
            int alt48=4;
            switch ( input.LA(1) ) {
            case ACTION:
            case TEMPLATE:
                {
                alt48=1;
                }
                break;
            case ALT:
                {
                alt48=2;
                }
                break;
            case ETC:
                {
                alt48=3;
                }
                break;
            case EPSILON:
                {
                alt48=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }

            switch (alt48) {
                case 1 :
                    // ASTVerifier.g:361:7: rewriteTemplate
                    {
                    pushFollow(FOLLOW_rewriteTemplate_in_rewriteAlt1642);
                    rewriteTemplate();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:362:7: rewriteTreeAlt
                    {
                    pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteAlt1650);
                    rewriteTreeAlt();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:363:7: ETC
                    {
                    match(input,ETC,FOLLOW_ETC_in_rewriteAlt1658); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:364:7: EPSILON
                    {
                    match(input,EPSILON,FOLLOW_EPSILON_in_rewriteAlt1666); 

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
    // $ANTLR end "rewriteAlt"


    // $ANTLR start "rewriteTreeAlt"
    // ASTVerifier.g:367:1: rewriteTreeAlt : ^( ALT ( rewriteTreeElement )+ ) ;
    public final void rewriteTreeAlt() throws RecognitionException {
        try {
            // ASTVerifier.g:368:5: ( ^( ALT ( rewriteTreeElement )+ ) )
            // ASTVerifier.g:368:7: ^( ALT ( rewriteTreeElement )+ )
            {
            match(input,ALT,FOLLOW_ALT_in_rewriteTreeAlt1685); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:368:13: ( rewriteTreeElement )+
            int cnt49=0;
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==ACTION||LA49_0==TREE_BEGIN||(LA49_0>=TOKEN_REF && LA49_0<=RULE_REF)||LA49_0==STRING_LITERAL||LA49_0==CHAR_LITERAL||(LA49_0>=OPTIONAL && LA49_0<=POSITIVE_CLOSURE)||LA49_0==LABEL) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // ASTVerifier.g:368:13: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTreeAlt1687);
            	    rewriteTreeElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt49 >= 1 ) break loop49;
                        EarlyExitException eee =
                            new EarlyExitException(49, input);
                        throw eee;
                }
                cnt49++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "rewriteTreeAlt"


    // $ANTLR start "rewriteTreeElement"
    // ASTVerifier.g:371:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTree | rewriteTreeEbnf );
    public final void rewriteTreeElement() throws RecognitionException {
        try {
            // ASTVerifier.g:372:2: ( rewriteTreeAtom | rewriteTree | rewriteTreeEbnf )
            int alt50=3;
            switch ( input.LA(1) ) {
            case ACTION:
            case TOKEN_REF:
            case RULE_REF:
            case STRING_LITERAL:
            case CHAR_LITERAL:
            case LABEL:
                {
                alt50=1;
                }
                break;
            case TREE_BEGIN:
                {
                alt50=2;
                }
                break;
            case OPTIONAL:
            case CLOSURE:
            case POSITIVE_CLOSURE:
                {
                alt50=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
                case 1 :
                    // ASTVerifier.g:372:4: rewriteTreeAtom
                    {
                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement1703);
                    rewriteTreeAtom();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:373:4: rewriteTree
                    {
                    pushFollow(FOLLOW_rewriteTree_in_rewriteTreeElement1708);
                    rewriteTree();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:374:6: rewriteTreeEbnf
                    {
                    pushFollow(FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement1715);
                    rewriteTreeEbnf();

                    state._fsp--;


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
    // $ANTLR end "rewriteTreeElement"


    // $ANTLR start "rewriteTreeAtom"
    // ASTVerifier.g:377:1: rewriteTreeAtom : ( CHAR_LITERAL | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL | ACTION );
    public final void rewriteTreeAtom() throws RecognitionException {
        try {
            // ASTVerifier.g:378:5: ( CHAR_LITERAL | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | STRING_LITERAL | LABEL | ACTION )
            int alt51=7;
            switch ( input.LA(1) ) {
            case CHAR_LITERAL:
                {
                alt51=1;
                }
                break;
            case TOKEN_REF:
                {
                int LA51_2 = input.LA(2);

                if ( (LA51_2==DOWN) ) {
                    alt51=2;
                }
                else if ( (LA51_2==UP||LA51_2==ACTION||LA51_2==TREE_BEGIN||(LA51_2>=TOKEN_REF && LA51_2<=RULE_REF)||LA51_2==STRING_LITERAL||LA51_2==CHAR_LITERAL||(LA51_2>=OPTIONAL && LA51_2<=POSITIVE_CLOSURE)||LA51_2==LABEL) ) {
                    alt51=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 51, 2, input);

                    throw nvae;
                }
                }
                break;
            case RULE_REF:
                {
                alt51=4;
                }
                break;
            case STRING_LITERAL:
                {
                alt51=5;
                }
                break;
            case LABEL:
                {
                alt51=6;
                }
                break;
            case ACTION:
                {
                alt51=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // ASTVerifier.g:378:9: CHAR_LITERAL
                    {
                    match(input,CHAR_LITERAL,FOLLOW_CHAR_LITERAL_in_rewriteTreeAtom1731); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:379:6: ^( TOKEN_REF ARG_ACTION )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1739); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom1741); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:380:6: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1749); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:381:9: RULE_REF
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_rewriteTreeAtom1759); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:382:6: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1766); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:383:6: LABEL
                    {
                    match(input,LABEL,FOLLOW_LABEL_in_rewriteTreeAtom1773); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:384:4: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_rewriteTreeAtom1778); 

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
    // $ANTLR end "rewriteTreeAtom"


    // $ANTLR start "rewriteTreeEbnf"
    // ASTVerifier.g:387:1: rewriteTreeEbnf : ^( ebnfSuffix ^( BLOCK rewriteTreeAlt ) ) ;
    public final void rewriteTreeEbnf() throws RecognitionException {
        try {
            // ASTVerifier.g:388:2: ( ^( ebnfSuffix ^( BLOCK rewriteTreeAlt ) ) )
            // ASTVerifier.g:388:4: ^( ebnfSuffix ^( BLOCK rewriteTreeAlt ) )
            {
            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeEbnf1790);
            ebnfSuffix();

            state._fsp--;


            match(input, Token.DOWN, null); 
            match(input,BLOCK,FOLLOW_BLOCK_in_rewriteTreeEbnf1793); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf1795);
            rewriteTreeAlt();

            state._fsp--;


            match(input, Token.UP, null); 

            match(input, Token.UP, null); 

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
    // $ANTLR end "rewriteTreeEbnf"


    // $ANTLR start "rewriteTree"
    // ASTVerifier.g:390:1: rewriteTree : ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) ;
    public final void rewriteTree() throws RecognitionException {
        try {
            // ASTVerifier.g:391:2: ( ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) )
            // ASTVerifier.g:391:4: ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewriteTree1808); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTree1810);
            rewriteTreeAtom();

            state._fsp--;

            // ASTVerifier.g:391:33: ( rewriteTreeElement )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==ACTION||LA52_0==TREE_BEGIN||(LA52_0>=TOKEN_REF && LA52_0<=RULE_REF)||LA52_0==STRING_LITERAL||LA52_0==CHAR_LITERAL||(LA52_0>=OPTIONAL && LA52_0<=POSITIVE_CLOSURE)||LA52_0==LABEL) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // ASTVerifier.g:391:33: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTree1812);
            	    rewriteTreeElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "rewriteTree"


    // $ANTLR start "rewriteTemplate"
    // ASTVerifier.g:394:1: rewriteTemplate : ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );
    public final void rewriteTemplate() throws RecognitionException {
        try {
            // ASTVerifier.g:395:2: ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION )
            int alt55=5;
            alt55 = dfa55.predict(input);
            switch (alt55) {
                case 1 :
                    // ASTVerifier.g:395:4: ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL )
                    {
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate1827); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:395:15: ( rewriteTemplateArgs )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==ARGLIST) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // ASTVerifier.g:395:15: rewriteTemplateArgs
                            {
                            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1829);
                            rewriteTemplateArgs();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,DOUBLE_QUOTE_STRING_LITERAL,FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate1832); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:396:4: ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL )
                    {
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate1839); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:396:15: ( rewriteTemplateArgs )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==ARGLIST) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // ASTVerifier.g:396:15: rewriteTemplateArgs
                            {
                            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1841);
                            rewriteTemplateArgs();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,DOUBLE_ANGLE_STRING_LITERAL,FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate1844); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:397:4: rewriteTemplateRef
                    {
                    pushFollow(FOLLOW_rewriteTemplateRef_in_rewriteTemplate1850);
                    rewriteTemplateRef();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:398:4: rewriteIndirectTemplateHead
                    {
                    pushFollow(FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate1855);
                    rewriteIndirectTemplateHead();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // ASTVerifier.g:399:4: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplate1860); 

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
    // $ANTLR end "rewriteTemplate"


    // $ANTLR start "rewriteTemplateRef"
    // ASTVerifier.g:402:1: rewriteTemplateRef : ^( TEMPLATE ID ( rewriteTemplateArgs )? ) ;
    public final void rewriteTemplateRef() throws RecognitionException {
        try {
            // ASTVerifier.g:403:2: ( ^( TEMPLATE ID ( rewriteTemplateArgs )? ) )
            // ASTVerifier.g:403:4: ^( TEMPLATE ID ( rewriteTemplateArgs )? )
            {
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplateRef1872); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rewriteTemplateRef1874); 
            // ASTVerifier.g:403:18: ( rewriteTemplateArgs )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==ARGLIST) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // ASTVerifier.g:403:18: rewriteTemplateArgs
                    {
                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef1876);
                    rewriteTemplateArgs();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

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
    // $ANTLR end "rewriteTemplateRef"


    // $ANTLR start "rewriteIndirectTemplateHead"
    // ASTVerifier.g:406:1: rewriteIndirectTemplateHead : ^( TEMPLATE ACTION ( rewriteTemplateArgs )? ) ;
    public final void rewriteIndirectTemplateHead() throws RecognitionException {
        try {
            // ASTVerifier.g:407:2: ( ^( TEMPLATE ACTION ( rewriteTemplateArgs )? ) )
            // ASTVerifier.g:407:4: ^( TEMPLATE ACTION ( rewriteTemplateArgs )? )
            {
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteIndirectTemplateHead1890); 

            match(input, Token.DOWN, null); 
            match(input,ACTION,FOLLOW_ACTION_in_rewriteIndirectTemplateHead1892); 
            // ASTVerifier.g:407:22: ( rewriteTemplateArgs )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==ARGLIST) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // ASTVerifier.g:407:22: rewriteTemplateArgs
                    {
                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead1894);
                    rewriteTemplateArgs();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

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
    // $ANTLR end "rewriteIndirectTemplateHead"


    // $ANTLR start "rewriteTemplateArgs"
    // ASTVerifier.g:410:1: rewriteTemplateArgs : ^( ARGLIST ( rewriteTemplateArg )+ ) ;
    public final void rewriteTemplateArgs() throws RecognitionException {
        try {
            // ASTVerifier.g:411:2: ( ^( ARGLIST ( rewriteTemplateArg )+ ) )
            // ASTVerifier.g:411:4: ^( ARGLIST ( rewriteTemplateArg )+ )
            {
            match(input,ARGLIST,FOLLOW_ARGLIST_in_rewriteTemplateArgs1908); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:411:14: ( rewriteTemplateArg )+
            int cnt58=0;
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==ARG) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // ASTVerifier.g:411:14: rewriteTemplateArg
            	    {
            	    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs1910);
            	    rewriteTemplateArg();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt58 >= 1 ) break loop58;
                        EarlyExitException eee =
                            new EarlyExitException(58, input);
                        throw eee;
                }
                cnt58++;
            } while (true);


            match(input, Token.UP, null); 

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
    // $ANTLR end "rewriteTemplateArgs"


    // $ANTLR start "rewriteTemplateArg"
    // ASTVerifier.g:414:1: rewriteTemplateArg : ^( ARG ID ACTION ) ;
    public final void rewriteTemplateArg() throws RecognitionException {
        try {
            // ASTVerifier.g:415:2: ( ^( ARG ID ACTION ) )
            // ASTVerifier.g:415:6: ^( ARG ID ACTION )
            {
            match(input,ARG,FOLLOW_ARG_in_rewriteTemplateArg1926); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rewriteTemplateArg1928); 
            match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplateArg1930); 

            match(input, Token.UP, null); 

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
    // $ANTLR end "rewriteTemplateArg"

    // Delegated rules


    protected DFA26 dfa26 = new DFA26(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA55 dfa55 = new DFA55(this);
    static final String DFA26_eotS =
        "\14\uffff";
    static final String DFA26_eofS =
        "\14\uffff";
    static final String DFA26_minS =
        "\1\4\1\uffff\2\2\6\uffff\2\57";
    static final String DFA26_maxS =
        "\1\144\1\uffff\2\2\6\uffff\2\115";
    static final String DFA26_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA26_specialS =
        "\14\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\7\13\uffff\1\6\31\uffff\1\5\2\uffff\1\1\1\uffff\1\3\2\uffff"+
            "\1\1\1\uffff\1\2\1\uffff\2\4\2\uffff\1\11\3\uffff\2\4\2\uffff"+
            "\1\4\1\uffff\1\4\10\uffff\4\5\20\uffff\1\10\2\uffff\1\4",
            "",
            "\1\12",
            "\1\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\4\4\uffff\1\4\1\uffff\2\4\4\uffff\1\4\1\uffff\2\4\2\uffff"+
            "\1\4\1\uffff\1\4\10\uffff\1\5",
            "\1\4\4\uffff\1\4\1\uffff\2\4\4\uffff\1\4\1\uffff\2\4\2\uffff"+
            "\1\4\1\uffff\1\4\10\uffff\1\5"
    };

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        public String getDescription() {
            return "244:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
    }
    static final String DFA33_eotS =
        "\25\uffff";
    static final String DFA33_eofS =
        "\25\uffff";
    static final String DFA33_minS =
        "\1\57\2\2\1\uffff\1\2\2\uffff\2\57\1\127\4\uffff\1\57\1\uffff\2"+
        "\2\1\uffff\2\57";
    static final String DFA33_maxS =
        "\1\144\2\2\1\uffff\1\2\2\uffff\2\104\1\127\4\uffff\1\104\1\uffff"+
        "\2\2\1\uffff\2\104";
    static final String DFA33_acceptS =
        "\3\uffff\1\5\1\uffff\1\10\1\11\3\uffff\1\1\1\3\1\4\1\2\1\uffff\1"+
        "\6\2\uffff\1\7\2\uffff";
    static final String DFA33_specialS =
        "\25\uffff}>";
    static final String[] DFA33_transitionS = {
            "\1\2\4\uffff\1\1\1\uffff\1\5\1\3\6\uffff\1\5\1\6\2\uffff\1\5"+
            "\1\uffff\1\5\37\uffff\1\4",
            "\1\7",
            "\1\10",
            "",
            "\1\11",
            "",
            "",
            "\1\5\4\uffff\1\5\1\uffff\1\5\1\12\4\uffff\1\13\1\uffff\1\5"+
            "\1\6\2\uffff\1\5\1\uffff\1\5",
            "\1\5\4\uffff\1\5\1\uffff\1\5\1\15\4\uffff\1\14\1\uffff\1\5"+
            "\1\6\2\uffff\1\5\1\uffff\1\5",
            "\1\16",
            "",
            "",
            "",
            "",
            "\1\21\4\uffff\1\20\1\uffff\1\17\7\uffff\1\17\1\22\2\uffff\1"+
            "\17\1\uffff\1\17",
            "",
            "\1\23",
            "\1\24",
            "",
            "\1\17\4\uffff\1\17\1\uffff\1\17\7\uffff\1\17\1\22\2\uffff\1"+
            "\17\1\uffff\1\17",
            "\1\17\4\uffff\1\17\1\uffff\1\17\7\uffff\1\17\1\22\2\uffff\1"+
            "\17\1\uffff\1\17"
    };

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        public String getDescription() {
            return "280:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );";
        }
    }
    static final String DFA42_eotS =
        "\17\uffff";
    static final String DFA42_eofS =
        "\17\uffff";
    static final String DFA42_minS =
        "\1\57\4\2\12\uffff";
    static final String DFA42_maxS =
        "\1\104\4\144\12\uffff";
    static final String DFA42_acceptS =
        "\5\uffff\1\11\1\12\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10";
    static final String DFA42_specialS =
        "\17\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\6\4\uffff\1\5\1\uffff\1\4\7\uffff\1\3\3\uffff\1\2\1\uffff"+
            "\1\1",
            "\1\7\2\10\13\uffff\1\10\31\uffff\1\10\2\uffff\1\10\1\uffff"+
            "\1\10\2\uffff\1\10\1\uffff\1\10\1\uffff\2\10\2\uffff\1\10\3"+
            "\uffff\2\10\2\uffff\1\10\1\uffff\1\10\10\uffff\4\10\20\uffff"+
            "\1\10\2\uffff\1\10",
            "\1\11\2\12\13\uffff\1\12\31\uffff\1\12\2\uffff\1\12\1\uffff"+
            "\1\12\2\uffff\1\12\1\uffff\1\12\1\uffff\2\12\2\uffff\1\12\3"+
            "\uffff\2\12\2\uffff\1\12\1\uffff\1\12\10\uffff\4\12\20\uffff"+
            "\1\12\2\uffff\1\12",
            "\1\13\2\14\13\uffff\1\14\31\uffff\1\14\2\uffff\1\14\1\uffff"+
            "\1\14\2\uffff\1\14\1\uffff\1\14\1\uffff\2\14\2\uffff\1\14\3"+
            "\uffff\2\14\2\uffff\1\14\1\uffff\1\14\10\uffff\4\14\20\uffff"+
            "\1\14\2\uffff\1\14",
            "\1\15\2\16\13\uffff\1\16\31\uffff\1\16\2\uffff\1\16\1\uffff"+
            "\1\16\2\uffff\1\16\1\uffff\1\16\1\uffff\2\16\2\uffff\1\16\3"+
            "\uffff\2\16\2\uffff\1\16\1\uffff\1\16\10\uffff\4\16\20\uffff"+
            "\1\16\2\uffff\1\16",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA42_eot = DFA.unpackEncodedString(DFA42_eotS);
    static final short[] DFA42_eof = DFA.unpackEncodedString(DFA42_eofS);
    static final char[] DFA42_min = DFA.unpackEncodedStringToUnsignedChars(DFA42_minS);
    static final char[] DFA42_max = DFA.unpackEncodedStringToUnsignedChars(DFA42_maxS);
    static final short[] DFA42_accept = DFA.unpackEncodedString(DFA42_acceptS);
    static final short[] DFA42_special = DFA.unpackEncodedString(DFA42_specialS);
    static final short[][] DFA42_transition;

    static {
        int numStates = DFA42_transitionS.length;
        DFA42_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA42_transition[i] = DFA.unpackEncodedString(DFA42_transitionS[i]);
        }
    }

    class DFA42 extends DFA {

        public DFA42(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 42;
            this.eot = DFA42_eot;
            this.eof = DFA42_eof;
            this.min = DFA42_min;
            this.max = DFA42_max;
            this.accept = DFA42_accept;
            this.special = DFA42_special;
            this.transition = DFA42_transition;
        }
        public String getDescription() {
            return "323:1: terminal : ( ^( CHAR_LITERAL elementOptions ) | CHAR_LITERAL | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF elementOptions ) | TOKEN_REF | ^( WILDCARD elementOptions ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) );";
        }
    }
    static final String DFA55_eotS =
        "\20\uffff";
    static final String DFA55_eofS =
        "\20\uffff";
    static final String DFA55_minS =
        "\1\20\1\2\1\uffff\1\12\2\uffff\1\2\2\uffff\1\130\1\2\1\127\1\20"+
        "\2\3\1\12";
    static final String DFA55_maxS =
        "\1\43\1\2\1\uffff\1\131\2\uffff\1\2\2\uffff\1\130\1\2\1\127\1\20"+
        "\1\3\1\130\1\13";
    static final String DFA55_acceptS =
        "\2\uffff\1\5\1\uffff\1\3\1\4\1\uffff\1\2\1\1\7\uffff";
    static final String DFA55_specialS =
        "\20\uffff}>";
    static final String[] DFA55_transitionS = {
            "\1\2\22\uffff\1\1",
            "\1\3",
            "",
            "\1\10\1\7\4\uffff\1\5\106\uffff\1\4\1\uffff\1\6",
            "",
            "",
            "\1\11",
            "",
            "",
            "\1\12",
            "\1\13",
            "\1\14",
            "\1\15",
            "\1\16",
            "\1\17\124\uffff\1\12",
            "\1\10\1\7"
    };

    static final short[] DFA55_eot = DFA.unpackEncodedString(DFA55_eotS);
    static final short[] DFA55_eof = DFA.unpackEncodedString(DFA55_eofS);
    static final char[] DFA55_min = DFA.unpackEncodedStringToUnsignedChars(DFA55_minS);
    static final char[] DFA55_max = DFA.unpackEncodedStringToUnsignedChars(DFA55_maxS);
    static final short[] DFA55_accept = DFA.unpackEncodedString(DFA55_acceptS);
    static final short[] DFA55_special = DFA.unpackEncodedString(DFA55_specialS);
    static final short[][] DFA55_transition;

    static {
        int numStates = DFA55_transitionS.length;
        DFA55_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA55_transition[i] = DFA.unpackEncodedString(DFA55_transitionS[i]);
        }
    }

    class DFA55 extends DFA {

        public DFA55(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 55;
            this.eot = DFA55_eot;
            this.eof = DFA55_eof;
            this.min = DFA55_min;
            this.max = DFA55_max;
            this.accept = DFA55_accept;
            this.special = DFA55_special;
            this.transition = DFA55_transition;
        }
        public String getDescription() {
            return "394:1: rewriteTemplate : ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );";
        }
    }
 

    public static final BitSet FOLLOW_grammarType_in_grammarSpec74 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec76 = new BitSet(new long[]{0x0800000000780040L,0x0000000000000400L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec78 = new BitSet(new long[]{0x0800000000780040L,0x0000000000000400L});
    public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec81 = new BitSet(new long[]{0x0800000000780040L,0x0000000000000400L});
    public static final BitSet FOLLOW_rules_in_grammarSpec84 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_grammarType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrScope_in_prequelConstruct161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_prequelConstruct171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec186 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec188 = new BitSet(new long[]{0x0000200000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_option210 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option212 = new BitSet(new long[]{0x0001000000000000L,0x0000000000800015L});
    public static final BitSet FOLLOW_optionValue_in_option214 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_delegateGrammars309 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars311 = new BitSet(new long[]{0x0000200000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar330 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar332 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar334 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec362 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec364 = new BitSet(new long[]{0xC000200000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec378 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenSpec380 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec382 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec394 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenSpec396 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_tokenSpec398 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenSpec406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_tokenSpec411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope423 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_attrScope425 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope427 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_action440 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action442 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_action445 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action447 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULES_in_rules463 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rule_in_rules465 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
    public static final BitSet FOLLOW_RULE_in_rule483 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule485 = new BitSet(new long[]{0x0800000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_rule487 = new BitSet(new long[]{0x0800000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_ruleModifiers_in_rule490 = new BitSet(new long[]{0x0800000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule493 = new BitSet(new long[]{0x0800000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_ruleReturns_in_rule506 = new BitSet(new long[]{0x0800000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_rulePrequel_in_rule509 = new BitSet(new long[]{0x0800000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_altListAsBlock_in_rule512 = new BitSet(new long[]{0x0000000600000008L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule514 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup541 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler560 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler562 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler564 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause577 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause579 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_throwsSpec_in_rulePrequel596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rulePrequel606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_rulePrequel616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_rulePrequel626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns641 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns643 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_THROWS_in_throwsSpec658 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_throwsSpec660 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec677 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec679 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec686 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec688 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_AT_in_ruleAction702 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleAction704 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction706 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULEMODIFIERS_in_ruleModifiers722 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ruleModifier_in_ruleModifiers724 = new BitSet(new long[]{0x0000000070800008L});
    public static final BitSet FOLLOW_set_in_ruleModifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternative_in_altList784 = new BitSet(new long[]{0x0000000000000002L,0x0000010000100000L});
    public static final BitSet FOLLOW_BLOCK_in_altListAsBlock803 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_altList_in_altListAsBlock805 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative824 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative826 = new BitSet(new long[]{0x0000000000000000L,0x0000008000400000L});
    public static final BitSet FOLLOW_rewrite_in_alternative828 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative838 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative840 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_elements_in_alternative851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_elements869 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_elements871 = new BitSet(new long[]{0xC4D4A40000010018L,0x000000120001E014L});
    public static final BitSet FOLLOW_labeledElement_in_element887 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement934 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement936 = new BitSet(new long[]{0xC0D0840000000000L,0x000000100001E014L});
    public static final BitSet FOLLOW_atom_in_labeledElement939 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_labeledElement941 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement949 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement951 = new BitSet(new long[]{0xC0D0840000000000L,0x000000100001E014L});
    public static final BitSet FOLLOW_atom_in_labeledElement954 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_labeledElement956 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec973 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec975 = new BitSet(new long[]{0xC4D4A40000010018L,0x000000120001E014L});
    public static final BitSet FOLLOW_blockSuffix_in_ebnf990 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf992 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffix1016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffix1024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffix1032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffix1040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ebnfSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom1080 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom1082 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom1089 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom1091 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom1098 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom1100 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom1107 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom1109 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_range_in_atom1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom1121 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom1123 = new BitSet(new long[]{0x4050800000000000L,0x0000000000000014L});
    public static final BitSet FOLLOW_terminal_in_atom1125 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom1132 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom1134 = new BitSet(new long[]{0xC0D0800000000000L,0x0000001000000014L});
    public static final BitSet FOLLOW_ruleref_in_atom1136 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_terminal_in_atom1147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet1175 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notTerminal_in_notSet1177 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet1187 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet1189 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_notTerminal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_block1241 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_optionsSpec_in_block1243 = new BitSet(new long[]{0x0800000100290000L,0x0000010000100000L});
    public static final BitSet FOLLOW_ruleAction_in_block1246 = new BitSet(new long[]{0x0800000100290000L,0x0000010000100000L});
    public static final BitSet FOLLOW_ACTION_in_block1249 = new BitSet(new long[]{0x0800000100290000L,0x0000010000100000L});
    public static final BitSet FOLLOW_altList_in_block1252 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_ruleref1271 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1273 = new BitSet(new long[]{0x0000000000004008L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1275 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref1286 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1288 = new BitSet(new long[]{0x0000000000004008L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1290 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1301 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1303 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range1323 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rangeElement_in_range1325 = new BitSet(new long[]{0xC000000000000000L,0x0000000000000014L});
    public static final BitSet FOLLOW_rangeElement_in_range1327 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_rangeElement0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_terminal1389 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1391 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_terminal1402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1411 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1413 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1431 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1433 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal1451 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1453 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal1462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal1471 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal1473 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal1483 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal1485 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOptions1504 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions1506 = new BitSet(new long[]{0x0000200000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption1525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption1536 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption1538 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption1540 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption1552 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption1554 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption1556 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_predicatedRewrite_in_rewrite1571 = new BitSet(new long[]{0x0000000000000000L,0x0000008000400000L});
    public static final BitSet FOLLOW_nakedRewrite_in_rewrite1574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ST_RESULT_in_predicatedRewrite1586 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite1588 = new BitSet(new long[]{0x0100000800010000L,0x0000000000180000L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite1590 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RESULT_in_predicatedRewrite1597 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite1599 = new BitSet(new long[]{0x0100000800010000L,0x0000000000180000L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite1601 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ST_RESULT_in_nakedRewrite1615 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite1617 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RESULT_in_nakedRewrite1624 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite1626 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTemplate_in_rewriteAlt1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteAlt1650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ETC_in_rewriteAlt1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EPSILON_in_rewriteAlt1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_rewriteTreeAlt1685 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTreeAlt1687 = new BitSet(new long[]{0xC400000000010008L,0x000000010001C014L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTree_in_rewriteTreeElement1708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHAR_LITERAL_in_rewriteTreeAtom1731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1739 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom1741 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1749 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_rewriteTreeAtom1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1766 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LABEL_in_rewriteTreeAtom1773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTreeAtom1778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeEbnf1790 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_BLOCK_in_rewriteTreeEbnf1793 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf1795 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewriteTree1808 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTree1810 = new BitSet(new long[]{0xC400000000010008L,0x000000010001C014L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTree1812 = new BitSet(new long[]{0xC400000000010008L,0x000000010001C014L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate1827 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1829 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate1832 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate1839 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1841 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate1844 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTemplateRef_in_rewriteTemplate1850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate1855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplate1860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplateRef1872 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewriteTemplateRef1874 = new BitSet(new long[]{0x0000000000000008L,0x0000000002000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef1876 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteIndirectTemplateHead1890 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_rewriteIndirectTemplateHead1892 = new BitSet(new long[]{0x0000000000000008L,0x0000000002000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead1894 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARGLIST_in_rewriteTemplateArgs1908 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs1910 = new BitSet(new long[]{0x0000000000000008L,0x0000000001000000L});
    public static final BitSet FOLLOW_ARG_in_rewriteTemplateArg1926 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewriteTemplateArg1928 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplateArg1930 = new BitSet(new long[]{0x0000000000000008L});

}