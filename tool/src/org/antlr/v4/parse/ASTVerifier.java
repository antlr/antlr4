// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ASTVerifier.g 2010-02-10 17:19:39

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

import org.antlr.runtime.*;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;
import org.antlr.v4.runtime.tree.CommonTree;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** The definitive ANTLR v3 tree grammar to parse ANTLR v4 grammars. 
 *  Parses trees created in ANTLRParser.g.
 */
public class ASTVerifier extends TreeParser {
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
    // ASTVerifier.g:110:1: grammarSpec : ^( GRAMMAR ID ( DOC_COMMENT )? ( prequelConstruct )* rules ) ;
    public final void grammarSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:111:5: ( ^( GRAMMAR ID ( DOC_COMMENT )? ( prequelConstruct )* rules ) )
            // ASTVerifier.g:111:9: ^( GRAMMAR ID ( DOC_COMMENT )? ( prequelConstruct )* rules )
            {
            match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarSpec74); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_grammarSpec76); 
            // ASTVerifier.g:111:22: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ASTVerifier.g:111:22: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec78); 

                    }
                    break;

            }

            // ASTVerifier.g:111:35: ( prequelConstruct )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=OPTIONS && LA2_0<=IMPORT)||LA2_0==AT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ASTVerifier.g:111:35: prequelConstruct
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


    // $ANTLR start "prequelConstruct"
    // ASTVerifier.g:120:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action );
    public final void prequelConstruct() throws RecognitionException {
        try {
            // ASTVerifier.g:121:2: ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action )
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
                    // ASTVerifier.g:121:6: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_prequelConstruct102);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:122:9: delegateGrammars
                    {
                    pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct112);
                    delegateGrammars();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:123:9: tokensSpec
                    {
                    pushFollow(FOLLOW_tokensSpec_in_prequelConstruct122);
                    tokensSpec();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:124:9: attrScope
                    {
                    pushFollow(FOLLOW_attrScope_in_prequelConstruct132);
                    attrScope();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // ASTVerifier.g:125:9: action
                    {
                    pushFollow(FOLLOW_action_in_prequelConstruct142);
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
    // ASTVerifier.g:128:1: optionsSpec : ^( OPTIONS ( option )* ) ;
    public final void optionsSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:129:2: ( ^( OPTIONS ( option )* ) )
            // ASTVerifier.g:129:4: ^( OPTIONS ( option )* )
            {
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec157); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // ASTVerifier.g:129:14: ( option )*
                loop4:
                do {
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==ASSIGN) ) {
                        alt4=1;
                    }


                    switch (alt4) {
                	case 1 :
                	    // ASTVerifier.g:129:14: option
                	    {
                	    pushFollow(FOLLOW_option_in_optionsSpec159);
                	    option();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop4;
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
    // $ANTLR end "optionsSpec"


    // $ANTLR start "option"
    // ASTVerifier.g:132:1: option : ^( ASSIGN ID optionValue ) ;
    public final void option() throws RecognitionException {
        try {
            // ASTVerifier.g:133:5: ( ^( ASSIGN ID optionValue ) )
            // ASTVerifier.g:133:9: ^( ASSIGN ID optionValue )
            {
            match(input,ASSIGN,FOLLOW_ASSIGN_in_option181); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_option183); 
            pushFollow(FOLLOW_optionValue_in_option185);
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
    // ASTVerifier.g:136:1: optionValue returns [String v] : ( ID | STRING_LITERAL | INT | STAR );
    public final ASTVerifier.optionValue_return optionValue() throws RecognitionException {
        ASTVerifier.optionValue_return retval = new ASTVerifier.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // ASTVerifier.g:138:5: ( ID | STRING_LITERAL | INT | STAR )
            // ASTVerifier.g:
            {
            if ( input.LA(1)==STAR||input.LA(1)==INT||input.LA(1)==STRING_LITERAL||input.LA(1)==ID ) {
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
    // ASTVerifier.g:144:1: delegateGrammars : ^( IMPORT ( delegateGrammar )+ ) ;
    public final void delegateGrammars() throws RecognitionException {
        try {
            // ASTVerifier.g:145:2: ( ^( IMPORT ( delegateGrammar )+ ) )
            // ASTVerifier.g:145:6: ^( IMPORT ( delegateGrammar )+ )
            {
            match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars270); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:145:15: ( delegateGrammar )+
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
            	    // ASTVerifier.g:145:15: delegateGrammar
            	    {
            	    pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars272);
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
    // ASTVerifier.g:148:1: delegateGrammar : ( ^( ASSIGN ID ID ) | ID );
    public final void delegateGrammar() throws RecognitionException {
        try {
            // ASTVerifier.g:149:5: ( ^( ASSIGN ID ID ) | ID )
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
                    // ASTVerifier.g:149:9: ^( ASSIGN ID ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar291); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_delegateGrammar293); 
                    match(input,ID,FOLLOW_ID_in_delegateGrammar295); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:150:9: ID
                    {
                    match(input,ID,FOLLOW_ID_in_delegateGrammar306); 

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
    // ASTVerifier.g:153:1: tokensSpec : ^( TOKENS ( tokenSpec )+ ) ;
    public final void tokensSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:154:2: ( ^( TOKENS ( tokenSpec )+ ) )
            // ASTVerifier.g:154:6: ^( TOKENS ( tokenSpec )+ )
            {
            match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec323); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:154:15: ( tokenSpec )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==ASSIGN||LA7_0==ID) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ASTVerifier.g:154:15: tokenSpec
            	    {
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec325);
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
    // ASTVerifier.g:157:1: tokenSpec : ( ^( ASSIGN ID STRING_LITERAL ) | ID );
    public final void tokenSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:158:2: ( ^( ASSIGN ID STRING_LITERAL ) | ID )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==ASSIGN) ) {
                alt8=1;
            }
            else if ( (LA8_0==ID) ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // ASTVerifier.g:158:4: ^( ASSIGN ID STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec339); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_tokenSpec341); 
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec343); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:159:4: ID
                    {
                    match(input,ID,FOLLOW_ID_in_tokenSpec349); 

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
    // ASTVerifier.g:162:1: attrScope : ^( SCOPE ID ACTION ) ;
    public final void attrScope() throws RecognitionException {
        try {
            // ASTVerifier.g:163:2: ( ^( SCOPE ID ACTION ) )
            // ASTVerifier.g:163:4: ^( SCOPE ID ACTION )
            {
            match(input,SCOPE,FOLLOW_SCOPE_in_attrScope361); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_attrScope363); 
            match(input,ACTION,FOLLOW_ACTION_in_attrScope365); 

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
    // ASTVerifier.g:166:1: action : ^( AT ( ID )? ID ACTION ) ;
    public final void action() throws RecognitionException {
        try {
            // ASTVerifier.g:167:2: ( ^( AT ( ID )? ID ACTION ) )
            // ASTVerifier.g:167:4: ^( AT ( ID )? ID ACTION )
            {
            match(input,AT,FOLLOW_AT_in_action378); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:167:9: ( ID )?
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
                    // ASTVerifier.g:167:9: ID
                    {
                    match(input,ID,FOLLOW_ID_in_action380); 

                    }
                    break;

            }

            match(input,ID,FOLLOW_ID_in_action383); 
            match(input,ACTION,FOLLOW_ACTION_in_action385); 

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
    // ASTVerifier.g:170:1: rules : ^( RULES ( rule )* ) ;
    public final void rules() throws RecognitionException {
        try {
            // ASTVerifier.g:171:5: ( ^( RULES ( rule )* ) )
            // ASTVerifier.g:171:7: ^( RULES ( rule )* )
            {
            match(input,RULES,FOLLOW_RULES_in_rules401); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // ASTVerifier.g:171:15: ( rule )*
                loop10:
                do {
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==RULE) ) {
                        alt10=1;
                    }


                    switch (alt10) {
                	case 1 :
                	    // ASTVerifier.g:171:15: rule
                	    {
                	    pushFollow(FOLLOW_rule_in_rules403);
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
    // ASTVerifier.g:174:1: rule : ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup ) ;
    public final void rule() throws RecognitionException {
        try {
            // ASTVerifier.g:174:5: ( ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup ) )
            // ASTVerifier.g:174:9: ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup )
            {
            match(input,RULE,FOLLOW_RULE_in_rule421); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rule423); 
            // ASTVerifier.g:174:20: ( DOC_COMMENT )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==DOC_COMMENT) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ASTVerifier.g:174:20: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_rule425); 

                    }
                    break;

            }

            // ASTVerifier.g:174:33: ( ruleModifiers )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULEMODIFIERS) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ASTVerifier.g:174:33: ruleModifiers
                    {
                    pushFollow(FOLLOW_ruleModifiers_in_rule428);
                    ruleModifiers();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:174:48: ( ARG_ACTION )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==ARG_ACTION) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ASTVerifier.g:174:48: ARG_ACTION
                    {
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule431); 

                    }
                    break;

            }

            // ASTVerifier.g:175:11: ( ruleReturns )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RETURNS) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ASTVerifier.g:175:11: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_rule444);
                    ruleReturns();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:175:24: ( rulePrequel )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==OPTIONS||LA15_0==SCOPE||LA15_0==THROWS||LA15_0==AT) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ASTVerifier.g:175:24: rulePrequel
            	    {
            	    pushFollow(FOLLOW_rulePrequel_in_rule447);
            	    rulePrequel();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            pushFollow(FOLLOW_altListAsBlock_in_rule450);
            altListAsBlock();

            state._fsp--;

            pushFollow(FOLLOW_exceptionGroup_in_rule452);
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
    // ASTVerifier.g:179:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
    public final void exceptionGroup() throws RecognitionException {
        try {
            // ASTVerifier.g:180:5: ( ( exceptionHandler )* ( finallyClause )? )
            // ASTVerifier.g:180:7: ( exceptionHandler )* ( finallyClause )?
            {
            // ASTVerifier.g:180:7: ( exceptionHandler )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==CATCH) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ASTVerifier.g:180:7: exceptionHandler
            	    {
            	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup479);
            	    exceptionHandler();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            // ASTVerifier.g:180:25: ( finallyClause )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==FINALLY) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ASTVerifier.g:180:25: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup482);
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
    // ASTVerifier.g:183:1: exceptionHandler : ^( CATCH ARG_ACTION ACTION ) ;
    public final void exceptionHandler() throws RecognitionException {
        try {
            // ASTVerifier.g:184:2: ( ^( CATCH ARG_ACTION ACTION ) )
            // ASTVerifier.g:184:4: ^( CATCH ARG_ACTION ACTION )
            {
            match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler498); 

            match(input, Token.DOWN, null); 
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler500); 
            match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler502); 

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
    // ASTVerifier.g:187:1: finallyClause : ^( FINALLY ACTION ) ;
    public final void finallyClause() throws RecognitionException {
        try {
            // ASTVerifier.g:188:2: ( ^( FINALLY ACTION ) )
            // ASTVerifier.g:188:4: ^( FINALLY ACTION )
            {
            match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause515); 

            match(input, Token.DOWN, null); 
            match(input,ACTION,FOLLOW_ACTION_in_finallyClause517); 

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
    // ASTVerifier.g:191:1: rulePrequel : ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction );
    public final void rulePrequel() throws RecognitionException {
        try {
            // ASTVerifier.g:192:5: ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction )
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
                    // ASTVerifier.g:192:9: throwsSpec
                    {
                    pushFollow(FOLLOW_throwsSpec_in_rulePrequel534);
                    throwsSpec();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:193:9: ruleScopeSpec
                    {
                    pushFollow(FOLLOW_ruleScopeSpec_in_rulePrequel544);
                    ruleScopeSpec();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:194:9: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_rulePrequel554);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:195:9: ruleAction
                    {
                    pushFollow(FOLLOW_ruleAction_in_rulePrequel564);
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
    // ASTVerifier.g:198:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
    public final void ruleReturns() throws RecognitionException {
        try {
            // ASTVerifier.g:199:2: ( ^( RETURNS ARG_ACTION ) )
            // ASTVerifier.g:199:4: ^( RETURNS ARG_ACTION )
            {
            match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns579); 

            match(input, Token.DOWN, null); 
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns581); 

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
    // ASTVerifier.g:201:1: throwsSpec : ^( THROWS ( ID )+ ) ;
    public final void throwsSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:202:5: ( ^( THROWS ( ID )+ ) )
            // ASTVerifier.g:202:7: ^( THROWS ( ID )+ )
            {
            match(input,THROWS,FOLLOW_THROWS_in_throwsSpec596); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:202:16: ( ID )+
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
            	    // ASTVerifier.g:202:16: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_throwsSpec598); 

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
    // ASTVerifier.g:205:1: ruleScopeSpec : ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) );
    public final void ruleScopeSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:206:2: ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) )
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
                    // ASTVerifier.g:206:4: ^( SCOPE ACTION )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec615); 

                    match(input, Token.DOWN, null); 
                    match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec617); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:207:4: ^( SCOPE ( ID )+ )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec624); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:207:12: ( ID )+
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
                    	    // ASTVerifier.g:207:12: ID
                    	    {
                    	    match(input,ID,FOLLOW_ID_in_ruleScopeSpec626); 

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
    // ASTVerifier.g:210:1: ruleAction : ^( AT ID ACTION ) ;
    public final void ruleAction() throws RecognitionException {
        try {
            // ASTVerifier.g:211:2: ( ^( AT ID ACTION ) )
            // ASTVerifier.g:211:4: ^( AT ID ACTION )
            {
            match(input,AT,FOLLOW_AT_in_ruleAction640); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_ruleAction642); 
            match(input,ACTION,FOLLOW_ACTION_in_ruleAction644); 

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
    // ASTVerifier.g:214:1: ruleModifiers : ^( RULEMODIFIERS ( ruleModifier )+ ) ;
    public final void ruleModifiers() throws RecognitionException {
        try {
            // ASTVerifier.g:215:5: ( ^( RULEMODIFIERS ( ruleModifier )+ ) )
            // ASTVerifier.g:215:7: ^( RULEMODIFIERS ( ruleModifier )+ )
            {
            match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_ruleModifiers660); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:215:23: ( ruleModifier )+
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
            	    // ASTVerifier.g:215:23: ruleModifier
            	    {
            	    pushFollow(FOLLOW_ruleModifier_in_ruleModifiers662);
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
    // ASTVerifier.g:218:1: ruleModifier : ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT );
    public final void ruleModifier() throws RecognitionException {
        try {
            // ASTVerifier.g:219:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
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
    // ASTVerifier.g:225:1: altList : ( alternative )+ ;
    public final void altList() throws RecognitionException {
        try {
            // ASTVerifier.g:226:5: ( ( alternative )+ )
            // ASTVerifier.g:226:7: ( alternative )+
            {
            // ASTVerifier.g:226:7: ( alternative )+
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
            	    // ASTVerifier.g:226:7: alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_altList722);
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
    // ASTVerifier.g:229:1: altListAsBlock : ^( BLOCK altList ) ;
    public final void altListAsBlock() throws RecognitionException {
        try {
            // ASTVerifier.g:230:5: ( ^( BLOCK altList ) )
            // ASTVerifier.g:230:7: ^( BLOCK altList )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_altListAsBlock741); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_altList_in_altListAsBlock743);
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
    // ASTVerifier.g:233:1: alternative : ( ^( ALT_REWRITE alternative rewrite ) | ^( ALT EPSILON ) | elements );
    public final void alternative() throws RecognitionException {
        try {
            // ASTVerifier.g:234:5: ( ^( ALT_REWRITE alternative rewrite ) | ^( ALT EPSILON ) | elements )
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
                    else if ( (LA24_3==SEMPRED||LA24_3==ACTION||LA24_3==IMPLIES||LA24_3==ASSIGN||LA24_3==BANG||LA24_3==PLUS_ASSIGN||LA24_3==ROOT||(LA24_3>=DOT && LA24_3<=RANGE)||LA24_3==TREE_BEGIN||(LA24_3>=TOKEN_REF && LA24_3<=RULE_REF)||LA24_3==STRING_LITERAL||LA24_3==BLOCK||(LA24_3>=OPTIONAL && LA24_3<=POSITIVE_CLOSURE)||LA24_3==GATED_SEMPRED||LA24_3==WILDCARD) ) {
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
                    // ASTVerifier.g:234:7: ^( ALT_REWRITE alternative rewrite )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative762); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative764);
                    alternative();

                    state._fsp--;

                    pushFollow(FOLLOW_rewrite_in_alternative766);
                    rewrite();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:235:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative776); 

                    match(input, Token.DOWN, null); 
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative778); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:236:9: elements
                    {
                    pushFollow(FOLLOW_elements_in_alternative789);
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
    // ASTVerifier.g:239:1: elements : ^( ALT ( element )+ ) ;
    public final void elements() throws RecognitionException {
        try {
            // ASTVerifier.g:240:5: ( ^( ALT ( element )+ ) )
            // ASTVerifier.g:240:7: ^( ALT ( element )+ )
            {
            match(input,ALT,FOLLOW_ALT_in_elements807); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:240:13: ( element )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==SEMPRED||LA25_0==ACTION||LA25_0==IMPLIES||LA25_0==ASSIGN||LA25_0==BANG||LA25_0==PLUS_ASSIGN||LA25_0==ROOT||(LA25_0>=DOT && LA25_0<=RANGE)||LA25_0==TREE_BEGIN||(LA25_0>=TOKEN_REF && LA25_0<=RULE_REF)||LA25_0==STRING_LITERAL||LA25_0==BLOCK||(LA25_0>=OPTIONAL && LA25_0<=POSITIVE_CLOSURE)||LA25_0==GATED_SEMPRED||LA25_0==WILDCARD) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ASTVerifier.g:240:13: element
            	    {
            	    pushFollow(FOLLOW_element_in_elements809);
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
    // ASTVerifier.g:243:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final void element() throws RecognitionException {
        try {
            // ASTVerifier.g:244:2: ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt26=7;
            alt26 = dfa26.predict(input);
            switch (alt26) {
                case 1 :
                    // ASTVerifier.g:244:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element825);
                    labeledElement();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:245:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element830);
                    atom();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:246:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element835);
                    ebnf();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:247:6: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_element842); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:248:6: SEMPRED
                    {
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_element849); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:249:4: GATED_SEMPRED
                    {
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element854); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:250:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element859);
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
    // ASTVerifier.g:253:1: labeledElement : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );
    public final void labeledElement() throws RecognitionException {
        try {
            // ASTVerifier.g:254:2: ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) )
            int alt27=4;
            alt27 = dfa27.predict(input);
            switch (alt27) {
                case 1 :
                    // ASTVerifier.g:254:4: ^( ASSIGN ID atom )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement872); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement874); 
                    pushFollow(FOLLOW_atom_in_labeledElement876);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:255:4: ^( ASSIGN ID block )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement883); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement885); 
                    pushFollow(FOLLOW_block_in_labeledElement887);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:256:4: ^( PLUS_ASSIGN ID atom )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement894); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement896); 
                    pushFollow(FOLLOW_atom_in_labeledElement898);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:257:4: ^( PLUS_ASSIGN ID block )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement905); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement907); 
                    pushFollow(FOLLOW_block_in_labeledElement909);
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
    // $ANTLR end "labeledElement"


    // $ANTLR start "treeSpec"
    // ASTVerifier.g:260:1: treeSpec : ^( TREE_BEGIN ( element )+ ) ;
    public final void treeSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:261:5: ( ^( TREE_BEGIN ( element )+ ) )
            // ASTVerifier.g:261:7: ^( TREE_BEGIN ( element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec925); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:261:20: ( element )+
            int cnt28=0;
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==SEMPRED||LA28_0==ACTION||LA28_0==IMPLIES||LA28_0==ASSIGN||LA28_0==BANG||LA28_0==PLUS_ASSIGN||LA28_0==ROOT||(LA28_0>=DOT && LA28_0<=RANGE)||LA28_0==TREE_BEGIN||(LA28_0>=TOKEN_REF && LA28_0<=RULE_REF)||LA28_0==STRING_LITERAL||LA28_0==BLOCK||(LA28_0>=OPTIONAL && LA28_0<=POSITIVE_CLOSURE)||LA28_0==GATED_SEMPRED||LA28_0==WILDCARD) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ASTVerifier.g:261:20: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec927);
            	    element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt28 >= 1 ) break loop28;
                        EarlyExitException eee =
                            new EarlyExitException(28, input);
                        throw eee;
                }
                cnt28++;
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
    // ASTVerifier.g:264:1: ebnf : ( ^( blockSuffix block ) | block );
    public final void ebnf() throws RecognitionException {
        try {
            // ASTVerifier.g:264:5: ( ^( blockSuffix block ) | block )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==IMPLIES||LA29_0==BANG||LA29_0==ROOT||(LA29_0>=OPTIONAL && LA29_0<=POSITIVE_CLOSURE)) ) {
                alt29=1;
            }
            else if ( (LA29_0==BLOCK) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // ASTVerifier.g:264:7: ^( blockSuffix block )
                    {
                    pushFollow(FOLLOW_blockSuffix_in_ebnf942);
                    blockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf944);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:265:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf951);
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
    // ASTVerifier.g:268:1: blockSuffix : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final void blockSuffix() throws RecognitionException {
        try {
            // ASTVerifier.g:269:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
            int alt30=4;
            switch ( input.LA(1) ) {
            case OPTIONAL:
            case CLOSURE:
            case POSITIVE_CLOSURE:
                {
                alt30=1;
                }
                break;
            case ROOT:
                {
                alt30=2;
                }
                break;
            case IMPLIES:
                {
                alt30=3;
                }
                break;
            case BANG:
                {
                alt30=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // ASTVerifier.g:269:7: ebnfSuffix
                    {
                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffix968);
                    ebnfSuffix();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:270:7: ROOT
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_blockSuffix976); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:271:7: IMPLIES
                    {
                    match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffix984); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:272:7: BANG
                    {
                    match(input,BANG,FOLLOW_BANG_in_blockSuffix992); 

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
    // ASTVerifier.g:275:1: ebnfSuffix : ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE );
    public final void ebnfSuffix() throws RecognitionException {
        try {
            // ASTVerifier.g:276:2: ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE )
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
    // ASTVerifier.g:281:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );
    public final void atom() throws RecognitionException {
        try {
            // ASTVerifier.g:281:5: ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref )
            int alt31=9;
            alt31 = dfa31.predict(input);
            switch (alt31) {
                case 1 :
                    // ASTVerifier.g:281:7: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom1032); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom1034);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:282:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom1041); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom1043);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:283:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom1050); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom1052);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:284:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom1059); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom1061);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:285:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom1067);
                    range();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // ASTVerifier.g:286:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom1073); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom1075); 
                    pushFollow(FOLLOW_terminal_in_atom1077);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:287:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom1084); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom1086); 
                    pushFollow(FOLLOW_ruleref_in_atom1088);
                    ruleref();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // ASTVerifier.g:288:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom1099);
                    terminal();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // ASTVerifier.g:289:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom1109);
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
    // ASTVerifier.g:292:1: notSet : ( ^( NOT notTerminal ) | ^( NOT block ) );
    public final void notSet() throws RecognitionException {
        try {
            // ASTVerifier.g:293:5: ( ^( NOT notTerminal ) | ^( NOT block ) )
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==NOT) ) {
                int LA32_1 = input.LA(2);

                if ( (LA32_1==DOWN) ) {
                    int LA32_2 = input.LA(3);

                    if ( (LA32_2==TOKEN_REF||LA32_2==STRING_LITERAL) ) {
                        alt32=1;
                    }
                    else if ( (LA32_2==BLOCK) ) {
                        alt32=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // ASTVerifier.g:293:7: ^( NOT notTerminal )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet1127); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notTerminal_in_notSet1129);
                    notTerminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:294:7: ^( NOT block )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet1139); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet1141);
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
    // ASTVerifier.g:297:1: notTerminal : ( TOKEN_REF | STRING_LITERAL );
    public final void notTerminal() throws RecognitionException {
        try {
            // ASTVerifier.g:298:5: ( TOKEN_REF | STRING_LITERAL )
            // ASTVerifier.g:
            {
            if ( input.LA(1)==TOKEN_REF||input.LA(1)==STRING_LITERAL ) {
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
            match(input,BLOCK,FOLLOW_BLOCK_in_block1185); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:303:15: ( optionsSpec )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==OPTIONS) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // ASTVerifier.g:303:15: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_block1187);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:303:28: ( ruleAction )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==AT) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // ASTVerifier.g:303:28: ruleAction
            	    {
            	    pushFollow(FOLLOW_ruleAction_in_block1190);
            	    ruleAction();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            // ASTVerifier.g:303:40: ( ACTION )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==ACTION) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // ASTVerifier.g:303:40: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_block1193); 

                    }
                    break;

            }

            pushFollow(FOLLOW_altList_in_block1196);
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
    // ASTVerifier.g:306:1: ruleref : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final void ruleref() throws RecognitionException {
        try {
            // ASTVerifier.g:307:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
            int alt39=3;
            switch ( input.LA(1) ) {
            case ROOT:
                {
                alt39=1;
                }
                break;
            case BANG:
                {
                alt39=2;
                }
                break;
            case RULE_REF:
                {
                alt39=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // ASTVerifier.g:307:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref1215); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1218); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // ASTVerifier.g:307:25: ( ARG_ACTION )?
                        int alt36=2;
                        int LA36_0 = input.LA(1);

                        if ( (LA36_0==ARG_ACTION) ) {
                            alt36=1;
                        }
                        switch (alt36) {
                            case 1 :
                                // ASTVerifier.g:307:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1220); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:308:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref1232); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1235); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // ASTVerifier.g:308:25: ( ARG_ACTION )?
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( (LA37_0==ARG_ACTION) ) {
                            alt37=1;
                        }
                        switch (alt37) {
                            case 1 :
                                // ASTVerifier.g:308:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1237); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:309:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1249); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // ASTVerifier.g:309:18: ( ARG_ACTION )?
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==ARG_ACTION) ) {
                            alt38=1;
                        }
                        switch (alt38) {
                            case 1 :
                                // ASTVerifier.g:309:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1251); 

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
            match(input,RANGE,FOLLOW_RANGE_in_range1271); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rangeElement_in_range1273);
            rangeElement();

            state._fsp--;

            pushFollow(FOLLOW_rangeElement_in_range1275);
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
    // ASTVerifier.g:316:1: rangeElement : ( STRING_LITERAL | RULE_REF | TOKEN_REF );
    public final void rangeElement() throws RecognitionException {
        try {
            // ASTVerifier.g:317:5: ( STRING_LITERAL | RULE_REF | TOKEN_REF )
            // ASTVerifier.g:
            {
            if ( (input.LA(1)>=TOKEN_REF && input.LA(1)<=RULE_REF)||input.LA(1)==STRING_LITERAL ) {
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
    // ASTVerifier.g:322:1: terminal : ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | TOKEN_REF | ^( WILDCARD elementOptions ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) );
    public final void terminal() throws RecognitionException {
        try {
            // ASTVerifier.g:323:5: ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | TOKEN_REF | ^( WILDCARD elementOptions ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) )
            int alt40=10;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // ASTVerifier.g:323:8: ^( STRING_LITERAL elementOptions )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1328); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1330);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:324:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1339); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:325:7: ^( TOKEN_REF ARG_ACTION elementOptions )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1348); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal1350); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1352);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:326:7: ^( TOKEN_REF ARG_ACTION )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1362); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal1364); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:327:7: ^( TOKEN_REF elementOptions )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1374); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1376);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:328:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1385); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:329:7: ^( WILDCARD elementOptions )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal1394); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1396);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // ASTVerifier.g:330:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_terminal1405); 

                    }
                    break;
                case 9 :
                    // ASTVerifier.g:331:7: ^( ROOT terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_terminal1414); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal1416);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // ASTVerifier.g:332:7: ^( BANG terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_terminal1426); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_terminal1428);
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
    // ASTVerifier.g:335:1: elementOptions : ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
    public final void elementOptions() throws RecognitionException {
        try {
            // ASTVerifier.g:336:5: ( ^( ELEMENT_OPTIONS ( elementOption )+ ) )
            // ASTVerifier.g:336:7: ^( ELEMENT_OPTIONS ( elementOption )+ )
            {
            match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOptions1447); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:336:25: ( elementOption )+
            int cnt41=0;
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==ASSIGN||LA41_0==ID) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // ASTVerifier.g:336:25: elementOption
            	    {
            	    pushFollow(FOLLOW_elementOption_in_elementOptions1449);
            	    elementOption();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt41 >= 1 ) break loop41;
                        EarlyExitException eee =
                            new EarlyExitException(41, input);
                        throw eee;
                }
                cnt41++;
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
    // ASTVerifier.g:339:1: elementOption : ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) );
    public final void elementOption() throws RecognitionException {
        try {
            // ASTVerifier.g:340:5: ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) )
            int alt42=3;
            int LA42_0 = input.LA(1);

            if ( (LA42_0==ID) ) {
                alt42=1;
            }
            else if ( (LA42_0==ASSIGN) ) {
                int LA42_2 = input.LA(2);

                if ( (LA42_2==DOWN) ) {
                    int LA42_3 = input.LA(3);

                    if ( (LA42_3==ID) ) {
                        int LA42_4 = input.LA(4);

                        if ( (LA42_4==ID) ) {
                            alt42=2;
                        }
                        else if ( (LA42_4==STRING_LITERAL) ) {
                            alt42=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 42, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 42, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // ASTVerifier.g:340:7: ID
                    {
                    match(input,ID,FOLLOW_ID_in_elementOption1468); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:341:9: ^( ASSIGN ID ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1479); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_elementOption1481); 
                    match(input,ID,FOLLOW_ID_in_elementOption1483); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:342:9: ^( ASSIGN ID STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1495); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_elementOption1497); 
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption1499); 

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
    // ASTVerifier.g:345:1: rewrite : ( predicatedRewrite )* nakedRewrite ;
    public final void rewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:346:2: ( ( predicatedRewrite )* nakedRewrite )
            // ASTVerifier.g:346:4: ( predicatedRewrite )* nakedRewrite
            {
            // ASTVerifier.g:346:4: ( predicatedRewrite )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==ST_RESULT) ) {
                    int LA43_1 = input.LA(2);

                    if ( (LA43_1==DOWN) ) {
                        int LA43_3 = input.LA(3);

                        if ( (LA43_3==SEMPRED) ) {
                            alt43=1;
                        }


                    }


                }
                else if ( (LA43_0==RESULT) ) {
                    int LA43_2 = input.LA(2);

                    if ( (LA43_2==DOWN) ) {
                        int LA43_4 = input.LA(3);

                        if ( (LA43_4==SEMPRED) ) {
                            alt43=1;
                        }


                    }


                }


                switch (alt43) {
            	case 1 :
            	    // ASTVerifier.g:346:4: predicatedRewrite
            	    {
            	    pushFollow(FOLLOW_predicatedRewrite_in_rewrite1514);
            	    predicatedRewrite();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            pushFollow(FOLLOW_nakedRewrite_in_rewrite1517);
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
    // ASTVerifier.g:349:1: predicatedRewrite : ( ^( ST_RESULT SEMPRED rewriteAlt ) | ^( RESULT SEMPRED rewriteAlt ) );
    public final void predicatedRewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:350:2: ( ^( ST_RESULT SEMPRED rewriteAlt ) | ^( RESULT SEMPRED rewriteAlt ) )
            int alt44=2;
            int LA44_0 = input.LA(1);

            if ( (LA44_0==ST_RESULT) ) {
                alt44=1;
            }
            else if ( (LA44_0==RESULT) ) {
                alt44=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // ASTVerifier.g:350:4: ^( ST_RESULT SEMPRED rewriteAlt )
                    {
                    match(input,ST_RESULT,FOLLOW_ST_RESULT_in_predicatedRewrite1529); 

                    match(input, Token.DOWN, null); 
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite1531); 
                    pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite1533);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:351:4: ^( RESULT SEMPRED rewriteAlt )
                    {
                    match(input,RESULT,FOLLOW_RESULT_in_predicatedRewrite1540); 

                    match(input, Token.DOWN, null); 
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite1542); 
                    pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite1544);
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
    // ASTVerifier.g:354:1: nakedRewrite : ( ^( ST_RESULT rewriteAlt ) | ^( RESULT rewriteAlt ) );
    public final void nakedRewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:355:2: ( ^( ST_RESULT rewriteAlt ) | ^( RESULT rewriteAlt ) )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==ST_RESULT) ) {
                alt45=1;
            }
            else if ( (LA45_0==RESULT) ) {
                alt45=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // ASTVerifier.g:355:4: ^( ST_RESULT rewriteAlt )
                    {
                    match(input,ST_RESULT,FOLLOW_ST_RESULT_in_nakedRewrite1558); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite1560);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:356:4: ^( RESULT rewriteAlt )
                    {
                    match(input,RESULT,FOLLOW_RESULT_in_nakedRewrite1567); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite1569);
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
    // ASTVerifier.g:359:1: rewriteAlt : ( rewriteTemplate | rewriteTreeAlt | ETC | EPSILON );
    public final void rewriteAlt() throws RecognitionException {
        try {
            // ASTVerifier.g:360:5: ( rewriteTemplate | rewriteTreeAlt | ETC | EPSILON )
            int alt46=4;
            switch ( input.LA(1) ) {
            case ACTION:
            case TEMPLATE:
                {
                alt46=1;
                }
                break;
            case ALT:
                {
                alt46=2;
                }
                break;
            case ETC:
                {
                alt46=3;
                }
                break;
            case EPSILON:
                {
                alt46=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 46, 0, input);

                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // ASTVerifier.g:360:7: rewriteTemplate
                    {
                    pushFollow(FOLLOW_rewriteTemplate_in_rewriteAlt1585);
                    rewriteTemplate();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:361:7: rewriteTreeAlt
                    {
                    pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteAlt1593);
                    rewriteTreeAlt();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:362:7: ETC
                    {
                    match(input,ETC,FOLLOW_ETC_in_rewriteAlt1601); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:363:7: EPSILON
                    {
                    match(input,EPSILON,FOLLOW_EPSILON_in_rewriteAlt1609); 

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
    // ASTVerifier.g:366:1: rewriteTreeAlt : ^( ALT ( rewriteTreeElement )+ ) ;
    public final void rewriteTreeAlt() throws RecognitionException {
        try {
            // ASTVerifier.g:367:5: ( ^( ALT ( rewriteTreeElement )+ ) )
            // ASTVerifier.g:367:7: ^( ALT ( rewriteTreeElement )+ )
            {
            match(input,ALT,FOLLOW_ALT_in_rewriteTreeAlt1628); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:367:13: ( rewriteTreeElement )+
            int cnt47=0;
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==ACTION||LA47_0==TREE_BEGIN||(LA47_0>=TOKEN_REF && LA47_0<=RULE_REF)||LA47_0==STRING_LITERAL||(LA47_0>=OPTIONAL && LA47_0<=POSITIVE_CLOSURE)||LA47_0==LABEL) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // ASTVerifier.g:367:13: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTreeAlt1630);
            	    rewriteTreeElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt47 >= 1 ) break loop47;
                        EarlyExitException eee =
                            new EarlyExitException(47, input);
                        throw eee;
                }
                cnt47++;
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
    // ASTVerifier.g:370:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTree | rewriteTreeEbnf );
    public final void rewriteTreeElement() throws RecognitionException {
        try {
            // ASTVerifier.g:371:2: ( rewriteTreeAtom | rewriteTree | rewriteTreeEbnf )
            int alt48=3;
            switch ( input.LA(1) ) {
            case ACTION:
            case TOKEN_REF:
            case RULE_REF:
            case STRING_LITERAL:
            case LABEL:
                {
                alt48=1;
                }
                break;
            case TREE_BEGIN:
                {
                alt48=2;
                }
                break;
            case OPTIONAL:
            case CLOSURE:
            case POSITIVE_CLOSURE:
                {
                alt48=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }

            switch (alt48) {
                case 1 :
                    // ASTVerifier.g:371:4: rewriteTreeAtom
                    {
                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement1646);
                    rewriteTreeAtom();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:372:4: rewriteTree
                    {
                    pushFollow(FOLLOW_rewriteTree_in_rewriteTreeElement1651);
                    rewriteTree();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:373:6: rewriteTreeEbnf
                    {
                    pushFollow(FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement1658);
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
    // ASTVerifier.g:376:1: rewriteTreeAtom : ( ^( TOKEN_REF elementOptions ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | LABEL | ACTION );
    public final void rewriteTreeAtom() throws RecognitionException {
        try {
            // ASTVerifier.g:377:5: ( ^( TOKEN_REF elementOptions ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | LABEL | ACTION )
            int alt49=9;
            alt49 = dfa49.predict(input);
            switch (alt49) {
                case 1 :
                    // ASTVerifier.g:377:9: ^( TOKEN_REF elementOptions ARG_ACTION )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1675); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom1677);
                    elementOptions();

                    state._fsp--;

                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom1679); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:378:9: ^( TOKEN_REF elementOptions )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1691); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom1693);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:379:9: ^( TOKEN_REF ARG_ACTION )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1705); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom1707); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:380:6: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1715); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:381:9: RULE_REF
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_rewriteTreeAtom1725); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:382:6: ^( STRING_LITERAL elementOptions )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1733); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom1735);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:383:6: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1743); 

                    }
                    break;
                case 8 :
                    // ASTVerifier.g:384:6: LABEL
                    {
                    match(input,LABEL,FOLLOW_LABEL_in_rewriteTreeAtom1751); 

                    }
                    break;
                case 9 :
                    // ASTVerifier.g:385:4: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_rewriteTreeAtom1756); 

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
    // ASTVerifier.g:388:1: rewriteTreeEbnf : ^( ebnfSuffix ^( REWRITE_BLOCK rewriteTreeAlt ) ) ;
    public final void rewriteTreeEbnf() throws RecognitionException {
        try {
            // ASTVerifier.g:389:2: ( ^( ebnfSuffix ^( REWRITE_BLOCK rewriteTreeAlt ) ) )
            // ASTVerifier.g:389:4: ^( ebnfSuffix ^( REWRITE_BLOCK rewriteTreeAlt ) )
            {
            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeEbnf1768);
            ebnfSuffix();

            state._fsp--;


            match(input, Token.DOWN, null); 
            match(input,REWRITE_BLOCK,FOLLOW_REWRITE_BLOCK_in_rewriteTreeEbnf1771); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf1773);
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
    // ASTVerifier.g:391:1: rewriteTree : ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) ;
    public final void rewriteTree() throws RecognitionException {
        try {
            // ASTVerifier.g:392:2: ( ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) )
            // ASTVerifier.g:392:4: ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewriteTree1786); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTree1788);
            rewriteTreeAtom();

            state._fsp--;

            // ASTVerifier.g:392:33: ( rewriteTreeElement )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);

                if ( (LA50_0==ACTION||LA50_0==TREE_BEGIN||(LA50_0>=TOKEN_REF && LA50_0<=RULE_REF)||LA50_0==STRING_LITERAL||(LA50_0>=OPTIONAL && LA50_0<=POSITIVE_CLOSURE)||LA50_0==LABEL) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // ASTVerifier.g:392:33: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTree1790);
            	    rewriteTreeElement();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop50;
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
    // ASTVerifier.g:395:1: rewriteTemplate : ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );
    public final void rewriteTemplate() throws RecognitionException {
        try {
            // ASTVerifier.g:396:2: ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION )
            int alt53=5;
            alt53 = dfa53.predict(input);
            switch (alt53) {
                case 1 :
                    // ASTVerifier.g:396:4: ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL )
                    {
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate1805); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:396:15: ( rewriteTemplateArgs )?
                    int alt51=2;
                    int LA51_0 = input.LA(1);

                    if ( (LA51_0==ARGLIST) ) {
                        alt51=1;
                    }
                    switch (alt51) {
                        case 1 :
                            // ASTVerifier.g:396:15: rewriteTemplateArgs
                            {
                            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1807);
                            rewriteTemplateArgs();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,DOUBLE_QUOTE_STRING_LITERAL,FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate1810); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:397:4: ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL )
                    {
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate1817); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:397:15: ( rewriteTemplateArgs )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==ARGLIST) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // ASTVerifier.g:397:15: rewriteTemplateArgs
                            {
                            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1819);
                            rewriteTemplateArgs();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,DOUBLE_ANGLE_STRING_LITERAL,FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate1822); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:398:4: rewriteTemplateRef
                    {
                    pushFollow(FOLLOW_rewriteTemplateRef_in_rewriteTemplate1828);
                    rewriteTemplateRef();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:399:4: rewriteIndirectTemplateHead
                    {
                    pushFollow(FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate1833);
                    rewriteIndirectTemplateHead();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // ASTVerifier.g:400:4: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplate1838); 

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
    // ASTVerifier.g:403:1: rewriteTemplateRef : ^( TEMPLATE ID ( rewriteTemplateArgs )? ) ;
    public final void rewriteTemplateRef() throws RecognitionException {
        try {
            // ASTVerifier.g:404:2: ( ^( TEMPLATE ID ( rewriteTemplateArgs )? ) )
            // ASTVerifier.g:404:4: ^( TEMPLATE ID ( rewriteTemplateArgs )? )
            {
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplateRef1850); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rewriteTemplateRef1852); 
            // ASTVerifier.g:404:18: ( rewriteTemplateArgs )?
            int alt54=2;
            int LA54_0 = input.LA(1);

            if ( (LA54_0==ARGLIST) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // ASTVerifier.g:404:18: rewriteTemplateArgs
                    {
                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef1854);
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
    // ASTVerifier.g:407:1: rewriteIndirectTemplateHead : ^( TEMPLATE ACTION ( rewriteTemplateArgs )? ) ;
    public final void rewriteIndirectTemplateHead() throws RecognitionException {
        try {
            // ASTVerifier.g:408:2: ( ^( TEMPLATE ACTION ( rewriteTemplateArgs )? ) )
            // ASTVerifier.g:408:4: ^( TEMPLATE ACTION ( rewriteTemplateArgs )? )
            {
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteIndirectTemplateHead1868); 

            match(input, Token.DOWN, null); 
            match(input,ACTION,FOLLOW_ACTION_in_rewriteIndirectTemplateHead1870); 
            // ASTVerifier.g:408:22: ( rewriteTemplateArgs )?
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==ARGLIST) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // ASTVerifier.g:408:22: rewriteTemplateArgs
                    {
                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead1872);
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
    // ASTVerifier.g:411:1: rewriteTemplateArgs : ^( ARGLIST ( rewriteTemplateArg )+ ) ;
    public final void rewriteTemplateArgs() throws RecognitionException {
        try {
            // ASTVerifier.g:412:2: ( ^( ARGLIST ( rewriteTemplateArg )+ ) )
            // ASTVerifier.g:412:4: ^( ARGLIST ( rewriteTemplateArg )+ )
            {
            match(input,ARGLIST,FOLLOW_ARGLIST_in_rewriteTemplateArgs1886); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:412:14: ( rewriteTemplateArg )+
            int cnt56=0;
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);

                if ( (LA56_0==ARG) ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // ASTVerifier.g:412:14: rewriteTemplateArg
            	    {
            	    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs1888);
            	    rewriteTemplateArg();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt56 >= 1 ) break loop56;
                        EarlyExitException eee =
                            new EarlyExitException(56, input);
                        throw eee;
                }
                cnt56++;
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
    // ASTVerifier.g:415:1: rewriteTemplateArg : ^( ARG ID ACTION ) ;
    public final void rewriteTemplateArg() throws RecognitionException {
        try {
            // ASTVerifier.g:416:2: ( ^( ARG ID ACTION ) )
            // ASTVerifier.g:416:6: ^( ARG ID ACTION )
            {
            match(input,ARG,FOLLOW_ARG_in_rewriteTemplateArg1904); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rewriteTemplateArg1906); 
            match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplateArg1908); 

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
    protected DFA27 dfa27 = new DFA27(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA49 dfa49 = new DFA49(this);
    protected DFA53 dfa53 = new DFA53(this);
    static final String DFA26_eotS =
        "\14\uffff";
    static final String DFA26_eofS =
        "\14\uffff";
    static final String DFA26_minS =
        "\1\4\1\uffff\2\2\6\uffff\2\57";
    static final String DFA26_maxS =
        "\1\141\1\uffff\2\2\6\uffff\2\141";
    static final String DFA26_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA26_specialS =
        "\14\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\7\13\uffff\1\6\31\uffff\1\5\2\uffff\1\1\1\uffff\1\3\2\uffff"+
            "\1\1\1\uffff\1\2\1\uffff\2\4\2\uffff\1\11\3\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5\1\uffff\3\5\15\uffff\1\10\2\uffff\1\4",
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
            "\1\4\10\uffff\1\5\24\uffff\1\4",
            "\1\4\4\uffff\1\4\2\uffff\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff"+
            "\1\4\10\uffff\1\5\24\uffff\1\4"
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
            return "243:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
    }
    static final String DFA27_eotS =
        "\13\uffff";
    static final String DFA27_eofS =
        "\13\uffff";
    static final String DFA27_minS =
        "\1\55\2\2\2\127\2\57\4\uffff";
    static final String DFA27_maxS =
        "\1\62\2\2\2\127\2\141\4\uffff";
    static final String DFA27_acceptS =
        "\7\uffff\1\2\1\1\1\3\1\4";
    static final String DFA27_specialS =
        "\13\uffff}>";
    static final String[] DFA27_transitionS = {
            "\1\1\4\uffff\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\10\4\uffff\1\10\1\uffff\2\10\6\uffff\2\10\3\uffff\1\10\10"+
            "\uffff\1\7\24\uffff\1\10",
            "\1\11\4\uffff\1\11\1\uffff\2\11\6\uffff\2\11\3\uffff\1\11\10"+
            "\uffff\1\12\24\uffff\1\11",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }
        public String getDescription() {
            return "253:1: labeledElement : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );";
        }
    }
    static final String DFA31_eotS =
        "\25\uffff";
    static final String DFA31_eofS =
        "\25\uffff";
    static final String DFA31_minS =
        "\1\57\2\2\1\uffff\1\2\2\uffff\2\57\1\127\4\uffff\1\57\2\2\2\uffff"+
        "\2\57";
    static final String DFA31_maxS =
        "\1\141\2\2\1\uffff\1\2\2\uffff\2\141\1\127\4\uffff\1\141\2\2\2\uffff"+
        "\2\141";
    static final String DFA31_acceptS =
        "\3\uffff\1\5\1\uffff\1\10\1\11\3\uffff\1\1\1\3\1\4\1\2\3\uffff\1"+
        "\7\1\6\2\uffff";
    static final String DFA31_specialS =
        "\25\uffff}>";
    static final String[] DFA31_transitionS = {
            "\1\2\4\uffff\1\1\1\uffff\1\4\1\3\6\uffff\1\5\1\6\3\uffff\1\5"+
            "\35\uffff\1\5",
            "\1\7",
            "\1\10",
            "",
            "\1\11",
            "",
            "",
            "\1\5\4\uffff\1\5\2\uffff\1\12\4\uffff\1\13\1\uffff\1\5\1\6"+
            "\3\uffff\1\5\35\uffff\1\5",
            "\1\5\4\uffff\1\5\2\uffff\1\15\4\uffff\1\14\1\uffff\1\5\1\6"+
            "\3\uffff\1\5\35\uffff\1\5",
            "\1\16",
            "",
            "",
            "",
            "",
            "\1\20\4\uffff\1\17\11\uffff\1\22\1\21\3\uffff\1\22\35\uffff"+
            "\1\22",
            "\1\23",
            "\1\24",
            "",
            "",
            "\1\22\4\uffff\1\22\11\uffff\1\22\1\21\3\uffff\1\22\35\uffff"+
            "\1\22",
            "\1\22\4\uffff\1\22\11\uffff\1\22\1\21\3\uffff\1\22\35\uffff"+
            "\1\22"
    };

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        public String getDescription() {
            return "281:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | terminal | ruleref );";
        }
    }
    static final String DFA40_eotS =
        "\20\uffff";
    static final String DFA40_eofS =
        "\20\uffff";
    static final String DFA40_minS =
        "\1\57\3\2\4\uffff\1\16\3\uffff\1\3\3\uffff";
    static final String DFA40_maxS =
        "\4\141\4\uffff\1\143\3\uffff\1\143\3\uffff";
    static final String DFA40_acceptS =
        "\4\uffff\1\11\1\12\1\1\1\2\1\uffff\1\6\1\7\1\10\1\uffff\1\5\1\4"+
        "\1\3";
    static final String DFA40_specialS =
        "\20\uffff}>";
    static final String[] DFA40_transitionS = {
            "\1\5\4\uffff\1\4\11\uffff\1\2\4\uffff\1\1\35\uffff\1\3",
            "\1\6\2\7\13\uffff\1\7\31\uffff\1\7\2\uffff\1\7\1\uffff\1\7"+
            "\2\uffff\1\7\1\uffff\1\7\1\uffff\2\7\2\uffff\1\7\3\uffff\2\7"+
            "\3\uffff\1\7\10\uffff\1\7\1\uffff\3\7\15\uffff\1\7\2\uffff\1"+
            "\7",
            "\1\10\2\11\13\uffff\1\11\31\uffff\1\11\2\uffff\1\11\1\uffff"+
            "\1\11\2\uffff\1\11\1\uffff\1\11\1\uffff\2\11\2\uffff\1\11\3"+
            "\uffff\2\11\3\uffff\1\11\10\uffff\1\11\1\uffff\3\11\15\uffff"+
            "\1\11\2\uffff\1\11",
            "\1\12\2\13\13\uffff\1\13\31\uffff\1\13\2\uffff\1\13\1\uffff"+
            "\1\13\2\uffff\1\13\1\uffff\1\13\1\uffff\2\13\2\uffff\1\13\3"+
            "\uffff\2\13\3\uffff\1\13\10\uffff\1\13\1\uffff\3\13\15\uffff"+
            "\1\13\2\uffff\1\13",
            "",
            "",
            "",
            "",
            "\1\14\124\uffff\1\15",
            "",
            "",
            "",
            "\1\16\137\uffff\1\17",
            "",
            "",
            ""
    };

    static final short[] DFA40_eot = DFA.unpackEncodedString(DFA40_eotS);
    static final short[] DFA40_eof = DFA.unpackEncodedString(DFA40_eofS);
    static final char[] DFA40_min = DFA.unpackEncodedStringToUnsignedChars(DFA40_minS);
    static final char[] DFA40_max = DFA.unpackEncodedStringToUnsignedChars(DFA40_maxS);
    static final short[] DFA40_accept = DFA.unpackEncodedString(DFA40_acceptS);
    static final short[] DFA40_special = DFA.unpackEncodedString(DFA40_specialS);
    static final short[][] DFA40_transition;

    static {
        int numStates = DFA40_transitionS.length;
        DFA40_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA40_transition[i] = DFA.unpackEncodedString(DFA40_transitionS[i]);
        }
    }

    class DFA40 extends DFA {

        public DFA40(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 40;
            this.eot = DFA40_eot;
            this.eof = DFA40_eof;
            this.min = DFA40_min;
            this.max = DFA40_max;
            this.accept = DFA40_accept;
            this.special = DFA40_special;
            this.transition = DFA40_transition;
        }
        public String getDescription() {
            return "322:1: terminal : ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | TOKEN_REF | ^( WILDCARD elementOptions ) | WILDCARD | ^( ROOT terminal ) | ^( BANG terminal ) );";
        }
    }
    static final String DFA49_eotS =
        "\30\uffff";
    static final String DFA49_eofS =
        "\30\uffff";
    static final String DFA49_minS =
        "\1\20\1\2\1\uffff\1\2\2\uffff\1\16\4\uffff\1\2\1\55\1\3\1\2\1\3"+
        "\1\127\2\uffff\1\103\4\3";
    static final String DFA49_maxS =
        "\2\135\1\uffff\1\135\2\uffff\1\143\4\uffff\1\2\2\127\1\2\1\16\1"+
        "\127\2\uffff\1\127\2\3\2\127";
    static final String DFA49_acceptS =
        "\2\uffff\1\5\1\uffff\1\10\1\11\1\uffff\1\4\1\6\1\7\1\3\6\uffff\1"+
        "\2\1\1\5\uffff";
    static final String DFA49_specialS =
        "\30\uffff}>";
    static final String[] DFA49_transitionS = {
            "\1\5\55\uffff\1\1\1\2\3\uffff\1\3\31\uffff\1\4",
            "\1\6\1\7\14\uffff\1\7\51\uffff\1\7\3\uffff\2\7\3\uffff\1\7"+
            "\12\uffff\3\7\14\uffff\1\7",
            "",
            "\1\10\1\11\14\uffff\1\11\51\uffff\1\11\3\uffff\2\11\3\uffff"+
            "\1\11\12\uffff\3\11\14\uffff\1\11",
            "",
            "",
            "\1\12\124\uffff\1\13",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\16\51\uffff\1\15",
            "\1\17\51\uffff\1\16\51\uffff\1\15",
            "\1\20",
            "\1\21\12\uffff\1\22",
            "\1\23",
            "",
            "",
            "\1\25\23\uffff\1\24",
            "\1\26",
            "\1\27",
            "\1\17\51\uffff\1\16\51\uffff\1\15",
            "\1\17\51\uffff\1\16\51\uffff\1\15"
    };

    static final short[] DFA49_eot = DFA.unpackEncodedString(DFA49_eotS);
    static final short[] DFA49_eof = DFA.unpackEncodedString(DFA49_eofS);
    static final char[] DFA49_min = DFA.unpackEncodedStringToUnsignedChars(DFA49_minS);
    static final char[] DFA49_max = DFA.unpackEncodedStringToUnsignedChars(DFA49_maxS);
    static final short[] DFA49_accept = DFA.unpackEncodedString(DFA49_acceptS);
    static final short[] DFA49_special = DFA.unpackEncodedString(DFA49_specialS);
    static final short[][] DFA49_transition;

    static {
        int numStates = DFA49_transitionS.length;
        DFA49_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA49_transition[i] = DFA.unpackEncodedString(DFA49_transitionS[i]);
        }
    }

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = DFA49_eot;
            this.eof = DFA49_eof;
            this.min = DFA49_min;
            this.max = DFA49_max;
            this.accept = DFA49_accept;
            this.special = DFA49_special;
            this.transition = DFA49_transition;
        }
        public String getDescription() {
            return "376:1: rewriteTreeAtom : ( ^( TOKEN_REF elementOptions ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | LABEL | ACTION );";
        }
    }
    static final String DFA53_eotS =
        "\20\uffff";
    static final String DFA53_eofS =
        "\20\uffff";
    static final String DFA53_minS =
        "\1\20\1\2\1\uffff\1\12\2\uffff\1\2\2\uffff\1\130\1\2\1\127\1\20"+
        "\2\3\1\12";
    static final String DFA53_maxS =
        "\1\43\1\2\1\uffff\1\131\2\uffff\1\2\2\uffff\1\130\1\2\1\127\1\20"+
        "\1\3\1\130\1\13";
    static final String DFA53_acceptS =
        "\2\uffff\1\5\1\uffff\1\3\1\4\1\uffff\1\1\1\2\7\uffff";
    static final String DFA53_specialS =
        "\20\uffff}>";
    static final String[] DFA53_transitionS = {
            "\1\2\22\uffff\1\1",
            "\1\3",
            "",
            "\1\7\1\10\4\uffff\1\5\106\uffff\1\4\1\uffff\1\6",
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
            "\1\7\1\10"
    };

    static final short[] DFA53_eot = DFA.unpackEncodedString(DFA53_eotS);
    static final short[] DFA53_eof = DFA.unpackEncodedString(DFA53_eofS);
    static final char[] DFA53_min = DFA.unpackEncodedStringToUnsignedChars(DFA53_minS);
    static final char[] DFA53_max = DFA.unpackEncodedStringToUnsignedChars(DFA53_maxS);
    static final short[] DFA53_accept = DFA.unpackEncodedString(DFA53_acceptS);
    static final short[] DFA53_special = DFA.unpackEncodedString(DFA53_specialS);
    static final short[][] DFA53_transition;

    static {
        int numStates = DFA53_transitionS.length;
        DFA53_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA53_transition[i] = DFA.unpackEncodedString(DFA53_transitionS[i]);
        }
    }

    class DFA53 extends DFA {

        public DFA53(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 53;
            this.eot = DFA53_eot;
            this.eof = DFA53_eof;
            this.min = DFA53_min;
            this.max = DFA53_max;
            this.accept = DFA53_accept;
            this.special = DFA53_special;
            this.transition = DFA53_transition;
        }
        public String getDescription() {
            return "395:1: rewriteTemplate : ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );";
        }
    }
 

    public static final BitSet FOLLOW_GRAMMAR_in_grammarSpec74 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec76 = new BitSet(new long[]{0x0800000000780040L,0x0000000000000200L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec78 = new BitSet(new long[]{0x0800000000780040L,0x0000000000000200L});
    public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec81 = new BitSet(new long[]{0x0800000000780040L,0x0000000000000200L});
    public static final BitSet FOLLOW_rules_in_grammarSpec84 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct122 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrScope_in_prequelConstruct132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_prequelConstruct142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec157 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec159 = new BitSet(new long[]{0x0000200000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_option181 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option183 = new BitSet(new long[]{0x0001000000000000L,0x0000000000800009L});
    public static final BitSet FOLLOW_optionValue_in_option185 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_delegateGrammars270 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars272 = new BitSet(new long[]{0x0000200000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar291 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar293 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar295 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec323 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec325 = new BitSet(new long[]{0x0000200000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec339 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokenSpec341 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec343 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_tokenSpec349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope361 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_attrScope363 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope365 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_action378 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action380 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_action383 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action385 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULES_in_rules401 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rule_in_rules403 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000100L});
    public static final BitSet FOLLOW_RULE_in_rule421 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule423 = new BitSet(new long[]{0x0800000180284040L,0x0000000000001400L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_rule425 = new BitSet(new long[]{0x0800000180284040L,0x0000000000001400L});
    public static final BitSet FOLLOW_ruleModifiers_in_rule428 = new BitSet(new long[]{0x0800000180284040L,0x0000000000001400L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule431 = new BitSet(new long[]{0x0800000180284040L,0x0000000000001400L});
    public static final BitSet FOLLOW_ruleReturns_in_rule444 = new BitSet(new long[]{0x0800000180284040L,0x0000000000001400L});
    public static final BitSet FOLLOW_rulePrequel_in_rule447 = new BitSet(new long[]{0x0800000180284040L,0x0000000000001400L});
    public static final BitSet FOLLOW_altListAsBlock_in_rule450 = new BitSet(new long[]{0x0000000600000008L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule452 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup479 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler498 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler500 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler502 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause515 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause517 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_throwsSpec_in_rulePrequel534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rulePrequel544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_rulePrequel554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_rulePrequel564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns579 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns581 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_THROWS_in_throwsSpec596 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_throwsSpec598 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec615 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec617 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec624 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec626 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_AT_in_ruleAction640 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleAction642 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction644 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULEMODIFIERS_in_ruleModifiers660 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ruleModifier_in_ruleModifiers662 = new BitSet(new long[]{0x0000000070800008L});
    public static final BitSet FOLLOW_set_in_ruleModifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternative_in_altList722 = new BitSet(new long[]{0x0000000000000002L,0x0000002000100000L});
    public static final BitSet FOLLOW_BLOCK_in_altListAsBlock741 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_altList_in_altListAsBlock743 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative762 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative764 = new BitSet(new long[]{0x0000000000000000L,0x0000001000400000L});
    public static final BitSet FOLLOW_rewrite_in_alternative766 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative776 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative778 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_elements_in_alternative789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_elements807 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_elements809 = new BitSet(new long[]{0xC4D4A40000010018L,0x000000024001D008L});
    public static final BitSet FOLLOW_labeledElement_in_element825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement872 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement874 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000200000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement876 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement883 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement885 = new BitSet(new long[]{0x0010840000000000L,0x000000000001D000L});
    public static final BitSet FOLLOW_block_in_labeledElement887 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement894 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement896 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000200000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement898 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement905 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement907 = new BitSet(new long[]{0x0010840000000000L,0x000000000001D000L});
    public static final BitSet FOLLOW_block_in_labeledElement909 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec925 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec927 = new BitSet(new long[]{0xC4D4A40000010018L,0x000000024001D008L});
    public static final BitSet FOLLOW_blockSuffix_in_ebnf942 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf944 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffix968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffix976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffix984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffix992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ebnfSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom1032 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom1034 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom1041 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom1043 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom1050 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom1052 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom1059 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom1061 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_range_in_atom1067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom1073 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom1075 = new BitSet(new long[]{0x4010800000000000L,0x0000000200000008L});
    public static final BitSet FOLLOW_terminal_in_atom1077 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom1084 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom1086 = new BitSet(new long[]{0xC0D0800000000000L,0x0000000200000008L});
    public static final BitSet FOLLOW_ruleref_in_atom1088 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_terminal_in_atom1099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet1127 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notTerminal_in_notSet1129 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet1139 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet1141 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_notTerminal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BLOCK_in_block1185 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_optionsSpec_in_block1187 = new BitSet(new long[]{0x0800000100290000L,0x0000002000100000L});
    public static final BitSet FOLLOW_ruleAction_in_block1190 = new BitSet(new long[]{0x0800000100290000L,0x0000002000100000L});
    public static final BitSet FOLLOW_ACTION_in_block1193 = new BitSet(new long[]{0x0800000100290000L,0x0000002000100000L});
    public static final BitSet FOLLOW_altList_in_block1196 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_ruleref1215 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1218 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1220 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref1232 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1235 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1237 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1249 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1251 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range1271 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rangeElement_in_range1273 = new BitSet(new long[]{0xC000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rangeElement_in_range1275 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_rangeElement0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1328 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1330 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1348 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal1350 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1352 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1362 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal1364 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1374 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1376 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal1394 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1396 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_terminal1405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal1414 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal1416 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_terminal1426 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_terminal1428 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOptions1447 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions1449 = new BitSet(new long[]{0x0000200000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption1468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption1479 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption1481 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption1483 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption1495 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption1497 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption1499 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_predicatedRewrite_in_rewrite1514 = new BitSet(new long[]{0x0000000000000000L,0x0000001000400000L});
    public static final BitSet FOLLOW_nakedRewrite_in_rewrite1517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ST_RESULT_in_predicatedRewrite1529 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite1531 = new BitSet(new long[]{0x0100000800010000L,0x0000000000180000L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite1533 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RESULT_in_predicatedRewrite1540 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite1542 = new BitSet(new long[]{0x0100000800010000L,0x0000000000180000L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite1544 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ST_RESULT_in_nakedRewrite1558 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite1560 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RESULT_in_nakedRewrite1567 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite1569 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTemplate_in_rewriteAlt1585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteAlt1593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ETC_in_rewriteAlt1601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EPSILON_in_rewriteAlt1609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_rewriteTreeAlt1628 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTreeAlt1630 = new BitSet(new long[]{0xC400000000010008L,0x000000002001C008L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement1646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTree_in_rewriteTreeElement1651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1675 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom1677 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom1679 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1691 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom1693 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom1707 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_rewriteTreeAtom1725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1733 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom1735 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LABEL_in_rewriteTreeAtom1751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTreeAtom1756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeEbnf1768 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_REWRITE_BLOCK_in_rewriteTreeEbnf1771 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf1773 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewriteTree1786 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTree1788 = new BitSet(new long[]{0xC400000000010008L,0x000000002001C008L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTree1790 = new BitSet(new long[]{0xC400000000010008L,0x000000002001C008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate1805 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1807 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate1810 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate1817 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1819 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate1822 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTemplateRef_in_rewriteTemplate1828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate1833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplate1838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplateRef1850 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewriteTemplateRef1852 = new BitSet(new long[]{0x0000000000000008L,0x0000000002000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef1854 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteIndirectTemplateHead1868 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_rewriteIndirectTemplateHead1870 = new BitSet(new long[]{0x0000000000000008L,0x0000000002000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead1872 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARGLIST_in_rewriteTemplateArgs1886 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs1888 = new BitSet(new long[]{0x0000000000000008L,0x0000000001000000L});
    public static final BitSet FOLLOW_ARG_in_rewriteTemplateArg1904 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewriteTemplateArg1906 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplateArg1908 = new BitSet(new long[]{0x0000000000000008L});

}