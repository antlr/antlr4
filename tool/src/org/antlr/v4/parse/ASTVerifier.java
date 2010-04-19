// $ANTLR ${project.version} ${buildNumber} ASTVerifier.g 2010-04-19 15:55:56

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
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.runtime.tree.TreeParser;
import org.antlr.runtime.tree.TreeRuleReturnScope;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** The definitive ANTLR v3 tree grammar to parse ANTLR v4 grammars. 
 *  Parses trees created in ANTLRParser.g.
 */
public class ASTVerifier extends TreeParser {
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
              input.LT(-3) == null ? "" : ((Tree)input.LT(-3)).getText()+" "+
              input.LT(-2) == null ? "" : ((Tree)input.LT(-2)).getText()+" "+
              input.LT(-1) == null ? "" : ((Tree)input.LT(-1)).getText()+" >>>"+
              input.LT(1) == null ? "" : ((Tree)input.LT(1)).getText()+"<<< "+
              input.LT(2) == null ? "" : ((Tree)input.LT(2)).getText()+" "+
              input.LT(3) == null ? "" : ((Tree)input.LT(3)).getText();
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
    	protected void mismatch(IntStream input, int ttype, BitSet follow)
    		throws RecognitionException {
    		throw new MismatchedTokenException(ttype, input);
    	}
    	public void recoverFromMismatchedToken(IntStream input,
    										   RecognitionException e, BitSet follow)
    		throws RecognitionException

    	{
    		throw e;
    	}



    // $ANTLR start "grammarSpec"
    // ASTVerifier.g:127:1: grammarSpec : ^( GRAMMAR ID ( DOC_COMMENT )? ( prequelConstruct )* rules ( mode )* ) ;
    public final void grammarSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:128:5: ( ^( GRAMMAR ID ( DOC_COMMENT )? ( prequelConstruct )* rules ( mode )* ) )
            // ASTVerifier.g:128:9: ^( GRAMMAR ID ( DOC_COMMENT )? ( prequelConstruct )* rules ( mode )* )
            {
            match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarSpec81); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_grammarSpec83); 
            // ASTVerifier.g:128:22: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ASTVerifier.g:128:22: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec85); 

                    }
                    break;

            }

            // ASTVerifier.g:128:35: ( prequelConstruct )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=OPTIONS && LA2_0<=IMPORT)||LA2_0==AT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ASTVerifier.g:128:35: prequelConstruct
            	    {
            	    pushFollow(FOLLOW_prequelConstruct_in_grammarSpec88);
            	    prequelConstruct();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            pushFollow(FOLLOW_rules_in_grammarSpec91);
            rules();

            state._fsp--;

            // ASTVerifier.g:128:59: ( mode )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==MODE) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ASTVerifier.g:128:59: mode
            	    {
            	    pushFollow(FOLLOW_mode_in_grammarSpec93);
            	    mode();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "grammarSpec"


    // $ANTLR start "prequelConstruct"
    // ASTVerifier.g:131:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action );
    public final void prequelConstruct() throws RecognitionException {
        try {
            // ASTVerifier.g:132:2: ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action )
            int alt4=5;
            switch ( input.LA(1) ) {
            case OPTIONS:
                {
                alt4=1;
                }
                break;
            case IMPORT:
                {
                alt4=2;
                }
                break;
            case TOKENS:
                {
                alt4=3;
                }
                break;
            case SCOPE:
                {
                alt4=4;
                }
                break;
            case AT:
                {
                alt4=5;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ASTVerifier.g:132:6: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_prequelConstruct109);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:133:9: delegateGrammars
                    {
                    pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct119);
                    delegateGrammars();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:134:9: tokensSpec
                    {
                    pushFollow(FOLLOW_tokensSpec_in_prequelConstruct129);
                    tokensSpec();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:135:9: attrScope
                    {
                    pushFollow(FOLLOW_attrScope_in_prequelConstruct139);
                    attrScope();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // ASTVerifier.g:136:9: action
                    {
                    pushFollow(FOLLOW_action_in_prequelConstruct149);
                    action();

                    state._fsp--;


                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "prequelConstruct"


    // $ANTLR start "optionsSpec"
    // ASTVerifier.g:139:1: optionsSpec : ^( OPTIONS ( option )* ) ;
    public final void optionsSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:140:2: ( ^( OPTIONS ( option )* ) )
            // ASTVerifier.g:140:4: ^( OPTIONS ( option )* )
            {
            match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec164); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // ASTVerifier.g:140:14: ( option )*
                loop5:
                do {
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==ASSIGN) ) {
                        alt5=1;
                    }


                    switch (alt5) {
                	case 1 :
                	    // ASTVerifier.g:140:14: option
                	    {
                	    pushFollow(FOLLOW_option_in_optionsSpec166);
                	    option();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop5;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "optionsSpec"


    // $ANTLR start "option"
    // ASTVerifier.g:143:1: option : ^( ASSIGN ID optionValue ) ;
    public final void option() throws RecognitionException {
        try {
            // ASTVerifier.g:144:5: ( ^( ASSIGN ID optionValue ) )
            // ASTVerifier.g:144:9: ^( ASSIGN ID optionValue )
            {
            match(input,ASSIGN,FOLLOW_ASSIGN_in_option188); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_option190); 
            pushFollow(FOLLOW_optionValue_in_option192);
            optionValue();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
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
    // ASTVerifier.g:147:1: optionValue returns [String v] : ( ID | STRING_LITERAL | INT | STAR );
    public final ASTVerifier.optionValue_return optionValue() throws RecognitionException {
        ASTVerifier.optionValue_return retval = new ASTVerifier.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // ASTVerifier.g:149:5: ( ID | STRING_LITERAL | INT | STAR )
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "optionValue"


    // $ANTLR start "delegateGrammars"
    // ASTVerifier.g:155:1: delegateGrammars : ^( IMPORT ( delegateGrammar )+ ) ;
    public final void delegateGrammars() throws RecognitionException {
        try {
            // ASTVerifier.g:156:2: ( ^( IMPORT ( delegateGrammar )+ ) )
            // ASTVerifier.g:156:6: ^( IMPORT ( delegateGrammar )+ )
            {
            match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars277); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:156:15: ( delegateGrammar )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==ASSIGN||LA6_0==ID) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // ASTVerifier.g:156:15: delegateGrammar
            	    {
            	    pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars279);
            	    delegateGrammar();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "delegateGrammars"


    // $ANTLR start "delegateGrammar"
    // ASTVerifier.g:159:1: delegateGrammar : ( ^( ASSIGN ID ID ) | ID );
    public final void delegateGrammar() throws RecognitionException {
        try {
            // ASTVerifier.g:160:5: ( ^( ASSIGN ID ID ) | ID )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==ASSIGN) ) {
                alt7=1;
            }
            else if ( (LA7_0==ID) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // ASTVerifier.g:160:9: ^( ASSIGN ID ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar298); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_delegateGrammar300); 
                    match(input,ID,FOLLOW_ID_in_delegateGrammar302); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:161:9: ID
                    {
                    match(input,ID,FOLLOW_ID_in_delegateGrammar313); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "delegateGrammar"


    // $ANTLR start "tokensSpec"
    // ASTVerifier.g:164:1: tokensSpec : ^( TOKENS ( tokenSpec )+ ) ;
    public final void tokensSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:165:2: ( ^( TOKENS ( tokenSpec )+ ) )
            // ASTVerifier.g:165:6: ^( TOKENS ( tokenSpec )+ )
            {
            match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec330); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:165:15: ( tokenSpec )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==ASSIGN||LA8_0==ID) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ASTVerifier.g:165:15: tokenSpec
            	    {
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec332);
            	    tokenSpec();

            	    state._fsp--;


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

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "tokensSpec"


    // $ANTLR start "tokenSpec"
    // ASTVerifier.g:168:1: tokenSpec : ( ^( ASSIGN ID STRING_LITERAL ) | ID );
    public final void tokenSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:169:2: ( ^( ASSIGN ID STRING_LITERAL ) | ID )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==ASSIGN) ) {
                alt9=1;
            }
            else if ( (LA9_0==ID) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // ASTVerifier.g:169:4: ^( ASSIGN ID STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec346); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_tokenSpec348); 
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec350); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:170:4: ID
                    {
                    match(input,ID,FOLLOW_ID_in_tokenSpec356); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "tokenSpec"


    // $ANTLR start "attrScope"
    // ASTVerifier.g:173:1: attrScope : ^( SCOPE ID ACTION ) ;
    public final void attrScope() throws RecognitionException {
        try {
            // ASTVerifier.g:174:2: ( ^( SCOPE ID ACTION ) )
            // ASTVerifier.g:174:4: ^( SCOPE ID ACTION )
            {
            match(input,SCOPE,FOLLOW_SCOPE_in_attrScope368); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_attrScope370); 
            match(input,ACTION,FOLLOW_ACTION_in_attrScope372); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "attrScope"


    // $ANTLR start "action"
    // ASTVerifier.g:177:1: action : ^( AT ( ID )? ID ACTION ) ;
    public final void action() throws RecognitionException {
        try {
            // ASTVerifier.g:178:2: ( ^( AT ( ID )? ID ACTION ) )
            // ASTVerifier.g:178:4: ^( AT ( ID )? ID ACTION )
            {
            match(input,AT,FOLLOW_AT_in_action385); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:178:9: ( ID )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ID) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==ID) ) {
                    alt10=1;
                }
            }
            switch (alt10) {
                case 1 :
                    // ASTVerifier.g:178:9: ID
                    {
                    match(input,ID,FOLLOW_ID_in_action387); 

                    }
                    break;

            }

            match(input,ID,FOLLOW_ID_in_action390); 
            match(input,ACTION,FOLLOW_ACTION_in_action392); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "action"


    // $ANTLR start "rules"
    // ASTVerifier.g:181:1: rules : ^( RULES ( rule )* ) ;
    public final void rules() throws RecognitionException {
        try {
            // ASTVerifier.g:182:5: ( ^( RULES ( rule )* ) )
            // ASTVerifier.g:182:7: ^( RULES ( rule )* )
            {
            match(input,RULES,FOLLOW_RULES_in_rules408); 

            if ( input.LA(1)==Token.DOWN ) {
                match(input, Token.DOWN, null); 
                // ASTVerifier.g:182:15: ( rule )*
                loop11:
                do {
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==RULE) ) {
                        alt11=1;
                    }


                    switch (alt11) {
                	case 1 :
                	    // ASTVerifier.g:182:15: rule
                	    {
                	    pushFollow(FOLLOW_rule_in_rules410);
                	    rule();

                	    state._fsp--;


                	    }
                	    break;

                	default :
                	    break loop11;
                    }
                } while (true);


                match(input, Token.UP, null); 
            }

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rules"


    // $ANTLR start "mode"
    // ASTVerifier.g:185:1: mode : ^( MODE ID ( rule )+ ) ;
    public final void mode() throws RecognitionException {
        try {
            // ASTVerifier.g:185:5: ( ^( MODE ID ( rule )+ ) )
            // ASTVerifier.g:185:7: ^( MODE ID ( rule )+ )
            {
            match(input,MODE,FOLLOW_MODE_in_mode426); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_mode428); 
            // ASTVerifier.g:185:18: ( rule )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==RULE) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // ASTVerifier.g:185:18: rule
            	    {
            	    pushFollow(FOLLOW_rule_in_mode430);
            	    rule();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "mode"


    // $ANTLR start "rule"
    // ASTVerifier.g:187:1: rule : ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup ) ;
    public final void rule() throws RecognitionException {
        try {
            // ASTVerifier.g:187:5: ( ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup ) )
            // ASTVerifier.g:187:9: ^( RULE ID ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock exceptionGroup )
            {
            match(input,RULE,FOLLOW_RULE_in_rule446); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rule448); 
            // ASTVerifier.g:187:20: ( DOC_COMMENT )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==DOC_COMMENT) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ASTVerifier.g:187:20: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_rule450); 

                    }
                    break;

            }

            // ASTVerifier.g:187:33: ( ruleModifiers )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULEMODIFIERS) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ASTVerifier.g:187:33: ruleModifiers
                    {
                    pushFollow(FOLLOW_ruleModifiers_in_rule453);
                    ruleModifiers();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:187:48: ( ARG_ACTION )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ARG_ACTION) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ASTVerifier.g:187:48: ARG_ACTION
                    {
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule456); 

                    }
                    break;

            }

            // ASTVerifier.g:188:11: ( ruleReturns )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RETURNS) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ASTVerifier.g:188:11: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_rule469);
                    ruleReturns();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:188:24: ( rulePrequel )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==OPTIONS||LA17_0==SCOPE||LA17_0==THROWS||LA17_0==AT) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // ASTVerifier.g:188:24: rulePrequel
            	    {
            	    pushFollow(FOLLOW_rulePrequel_in_rule472);
            	    rulePrequel();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            pushFollow(FOLLOW_altListAsBlock_in_rule475);
            altListAsBlock();

            state._fsp--;

            pushFollow(FOLLOW_exceptionGroup_in_rule477);
            exceptionGroup();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rule"


    // $ANTLR start "exceptionGroup"
    // ASTVerifier.g:192:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
    public final void exceptionGroup() throws RecognitionException {
        try {
            // ASTVerifier.g:193:5: ( ( exceptionHandler )* ( finallyClause )? )
            // ASTVerifier.g:193:7: ( exceptionHandler )* ( finallyClause )?
            {
            // ASTVerifier.g:193:7: ( exceptionHandler )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==CATCH) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // ASTVerifier.g:193:7: exceptionHandler
            	    {
            	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup504);
            	    exceptionHandler();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            // ASTVerifier.g:193:25: ( finallyClause )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==FINALLY) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ASTVerifier.g:193:25: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup507);
                    finallyClause();

                    state._fsp--;


                    }
                    break;

            }


            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "exceptionGroup"


    // $ANTLR start "exceptionHandler"
    // ASTVerifier.g:196:1: exceptionHandler : ^( CATCH ARG_ACTION ACTION ) ;
    public final void exceptionHandler() throws RecognitionException {
        try {
            // ASTVerifier.g:197:2: ( ^( CATCH ARG_ACTION ACTION ) )
            // ASTVerifier.g:197:4: ^( CATCH ARG_ACTION ACTION )
            {
            match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler523); 

            match(input, Token.DOWN, null); 
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler525); 
            match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler527); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "exceptionHandler"


    // $ANTLR start "finallyClause"
    // ASTVerifier.g:200:1: finallyClause : ^( FINALLY ACTION ) ;
    public final void finallyClause() throws RecognitionException {
        try {
            // ASTVerifier.g:201:2: ( ^( FINALLY ACTION ) )
            // ASTVerifier.g:201:4: ^( FINALLY ACTION )
            {
            match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause540); 

            match(input, Token.DOWN, null); 
            match(input,ACTION,FOLLOW_ACTION_in_finallyClause542); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "finallyClause"


    // $ANTLR start "rulePrequel"
    // ASTVerifier.g:204:1: rulePrequel : ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction );
    public final void rulePrequel() throws RecognitionException {
        try {
            // ASTVerifier.g:205:5: ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction )
            int alt20=4;
            switch ( input.LA(1) ) {
            case THROWS:
                {
                alt20=1;
                }
                break;
            case SCOPE:
                {
                alt20=2;
                }
                break;
            case OPTIONS:
                {
                alt20=3;
                }
                break;
            case AT:
                {
                alt20=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // ASTVerifier.g:205:9: throwsSpec
                    {
                    pushFollow(FOLLOW_throwsSpec_in_rulePrequel559);
                    throwsSpec();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:206:9: ruleScopeSpec
                    {
                    pushFollow(FOLLOW_ruleScopeSpec_in_rulePrequel569);
                    ruleScopeSpec();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:207:9: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_rulePrequel579);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:208:9: ruleAction
                    {
                    pushFollow(FOLLOW_ruleAction_in_rulePrequel589);
                    ruleAction();

                    state._fsp--;


                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rulePrequel"


    // $ANTLR start "ruleReturns"
    // ASTVerifier.g:211:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
    public final void ruleReturns() throws RecognitionException {
        try {
            // ASTVerifier.g:212:2: ( ^( RETURNS ARG_ACTION ) )
            // ASTVerifier.g:212:4: ^( RETURNS ARG_ACTION )
            {
            match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns604); 

            match(input, Token.DOWN, null); 
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns606); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ruleReturns"


    // $ANTLR start "throwsSpec"
    // ASTVerifier.g:214:1: throwsSpec : ^( THROWS ( ID )+ ) ;
    public final void throwsSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:215:5: ( ^( THROWS ( ID )+ ) )
            // ASTVerifier.g:215:7: ^( THROWS ( ID )+ )
            {
            match(input,THROWS,FOLLOW_THROWS_in_throwsSpec621); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:215:16: ( ID )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==ID) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ASTVerifier.g:215:16: ID
            	    {
            	    match(input,ID,FOLLOW_ID_in_throwsSpec623); 

            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "throwsSpec"


    // $ANTLR start "ruleScopeSpec"
    // ASTVerifier.g:218:1: ruleScopeSpec : ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) );
    public final void ruleScopeSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:219:2: ( ^( SCOPE ACTION ) | ^( SCOPE ( ID )+ ) )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==SCOPE) ) {
                int LA23_1 = input.LA(2);

                if ( (LA23_1==DOWN) ) {
                    int LA23_2 = input.LA(3);

                    if ( (LA23_2==ACTION) ) {
                        alt23=1;
                    }
                    else if ( (LA23_2==ID) ) {
                        alt23=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 23, 2, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 23, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // ASTVerifier.g:219:4: ^( SCOPE ACTION )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec640); 

                    match(input, Token.DOWN, null); 
                    match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec642); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:220:4: ^( SCOPE ( ID )+ )
                    {
                    match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec649); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:220:12: ( ID )+
                    int cnt22=0;
                    loop22:
                    do {
                        int alt22=2;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0==ID) ) {
                            alt22=1;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // ASTVerifier.g:220:12: ID
                    	    {
                    	    match(input,ID,FOLLOW_ID_in_ruleScopeSpec651); 

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
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ruleScopeSpec"


    // $ANTLR start "ruleAction"
    // ASTVerifier.g:223:1: ruleAction : ^( AT ID ACTION ) ;
    public final void ruleAction() throws RecognitionException {
        try {
            // ASTVerifier.g:224:2: ( ^( AT ID ACTION ) )
            // ASTVerifier.g:224:4: ^( AT ID ACTION )
            {
            match(input,AT,FOLLOW_AT_in_ruleAction665); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_ruleAction667); 
            match(input,ACTION,FOLLOW_ACTION_in_ruleAction669); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ruleAction"


    // $ANTLR start "ruleModifiers"
    // ASTVerifier.g:227:1: ruleModifiers : ^( RULEMODIFIERS ( ruleModifier )+ ) ;
    public final void ruleModifiers() throws RecognitionException {
        try {
            // ASTVerifier.g:228:5: ( ^( RULEMODIFIERS ( ruleModifier )+ ) )
            // ASTVerifier.g:228:7: ^( RULEMODIFIERS ( ruleModifier )+ )
            {
            match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_ruleModifiers685); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:228:23: ( ruleModifier )+
            int cnt24=0;
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==FRAGMENT||(LA24_0>=PROTECTED && LA24_0<=PRIVATE)) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ASTVerifier.g:228:23: ruleModifier
            	    {
            	    pushFollow(FOLLOW_ruleModifier_in_ruleModifiers687);
            	    ruleModifier();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt24 >= 1 ) break loop24;
                        EarlyExitException eee =
                            new EarlyExitException(24, input);
                        throw eee;
                }
                cnt24++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ruleModifiers"


    // $ANTLR start "ruleModifier"
    // ASTVerifier.g:231:1: ruleModifier : ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT );
    public final void ruleModifier() throws RecognitionException {
        try {
            // ASTVerifier.g:232:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ruleModifier"


    // $ANTLR start "altList"
    // ASTVerifier.g:238:1: altList : ( alternative )+ ;
    public final void altList() throws RecognitionException {
        try {
            // ASTVerifier.g:239:5: ( ( alternative )+ )
            // ASTVerifier.g:239:7: ( alternative )+
            {
            // ASTVerifier.g:239:7: ( alternative )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==ALT||LA25_0==ALT_REWRITE) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ASTVerifier.g:239:7: alternative
            	    {
            	    pushFollow(FOLLOW_alternative_in_altList747);
            	    alternative();

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


            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "altList"


    // $ANTLR start "altListAsBlock"
    // ASTVerifier.g:242:1: altListAsBlock : ^( BLOCK altList ) ;
    public final void altListAsBlock() throws RecognitionException {
        try {
            // ASTVerifier.g:243:5: ( ^( BLOCK altList ) )
            // ASTVerifier.g:243:7: ^( BLOCK altList )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_altListAsBlock766); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_altList_in_altListAsBlock768);
            altList();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "altListAsBlock"


    // $ANTLR start "alternative"
    // ASTVerifier.g:246:1: alternative : ( ^( ALT_REWRITE alternative rewrite ) | ^( ALT EPSILON ) | elements );
    public final void alternative() throws RecognitionException {
        try {
            // ASTVerifier.g:247:5: ( ^( ALT_REWRITE alternative rewrite ) | ^( ALT EPSILON ) | elements )
            int alt26=3;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==ALT_REWRITE) ) {
                alt26=1;
            }
            else if ( (LA26_0==ALT) ) {
                int LA26_2 = input.LA(2);

                if ( (LA26_2==DOWN) ) {
                    int LA26_3 = input.LA(3);

                    if ( (LA26_3==EPSILON) ) {
                        alt26=2;
                    }
                    else if ( (LA26_3==SEMPRED||LA26_3==ACTION||LA26_3==IMPLIES||LA26_3==ASSIGN||LA26_3==BANG||LA26_3==PLUS_ASSIGN||LA26_3==ROOT||(LA26_3>=DOT && LA26_3<=RANGE)||LA26_3==TREE_BEGIN||LA26_3==NOT||(LA26_3>=TOKEN_REF && LA26_3<=RULE_REF)||LA26_3==STRING_LITERAL||LA26_3==BLOCK||(LA26_3>=OPTIONAL && LA26_3<=POSITIVE_CLOSURE)||LA26_3==GATED_SEMPRED||LA26_3==WILDCARD) ) {
                        alt26=3;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // ASTVerifier.g:247:7: ^( ALT_REWRITE alternative rewrite )
                    {
                    match(input,ALT_REWRITE,FOLLOW_ALT_REWRITE_in_alternative787); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_alternative_in_alternative789);
                    alternative();

                    state._fsp--;

                    pushFollow(FOLLOW_rewrite_in_alternative791);
                    rewrite();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:248:7: ^( ALT EPSILON )
                    {
                    match(input,ALT,FOLLOW_ALT_in_alternative801); 

                    match(input, Token.DOWN, null); 
                    match(input,EPSILON,FOLLOW_EPSILON_in_alternative803); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:249:9: elements
                    {
                    pushFollow(FOLLOW_elements_in_alternative814);
                    elements();

                    state._fsp--;


                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "alternative"


    // $ANTLR start "elements"
    // ASTVerifier.g:252:1: elements : ^( ALT ( element )+ ) ;
    public final void elements() throws RecognitionException {
        try {
            // ASTVerifier.g:253:5: ( ^( ALT ( element )+ ) )
            // ASTVerifier.g:253:7: ^( ALT ( element )+ )
            {
            match(input,ALT,FOLLOW_ALT_in_elements832); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:253:13: ( element )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==SEMPRED||LA27_0==ACTION||LA27_0==IMPLIES||LA27_0==ASSIGN||LA27_0==BANG||LA27_0==PLUS_ASSIGN||LA27_0==ROOT||(LA27_0>=DOT && LA27_0<=RANGE)||LA27_0==TREE_BEGIN||LA27_0==NOT||(LA27_0>=TOKEN_REF && LA27_0<=RULE_REF)||LA27_0==STRING_LITERAL||LA27_0==BLOCK||(LA27_0>=OPTIONAL && LA27_0<=POSITIVE_CLOSURE)||LA27_0==GATED_SEMPRED||LA27_0==WILDCARD) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ASTVerifier.g:253:13: element
            	    {
            	    pushFollow(FOLLOW_element_in_elements834);
            	    element();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elements"


    // $ANTLR start "element"
    // ASTVerifier.g:256:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );
    public final void element() throws RecognitionException {
        try {
            // ASTVerifier.g:257:2: ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec )
            int alt28=7;
            alt28 = dfa28.predict(input);
            switch (alt28) {
                case 1 :
                    // ASTVerifier.g:257:4: labeledElement
                    {
                    pushFollow(FOLLOW_labeledElement_in_element850);
                    labeledElement();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:258:4: atom
                    {
                    pushFollow(FOLLOW_atom_in_element855);
                    atom();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:259:4: ebnf
                    {
                    pushFollow(FOLLOW_ebnf_in_element860);
                    ebnf();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:260:6: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_element867); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:261:6: SEMPRED
                    {
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_element874); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:262:4: GATED_SEMPRED
                    {
                    match(input,GATED_SEMPRED,FOLLOW_GATED_SEMPRED_in_element879); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:263:4: treeSpec
                    {
                    pushFollow(FOLLOW_treeSpec_in_element884);
                    treeSpec();

                    state._fsp--;


                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "element"


    // $ANTLR start "labeledElement"
    // ASTVerifier.g:266:1: labeledElement : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );
    public final void labeledElement() throws RecognitionException {
        try {
            // ASTVerifier.g:267:2: ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) )
            int alt29=4;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // ASTVerifier.g:267:4: ^( ASSIGN ID atom )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement897); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement899); 
                    pushFollow(FOLLOW_atom_in_labeledElement901);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:268:4: ^( ASSIGN ID block )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement908); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement910); 
                    pushFollow(FOLLOW_block_in_labeledElement912);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:269:4: ^( PLUS_ASSIGN ID atom )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement919); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement921); 
                    pushFollow(FOLLOW_atom_in_labeledElement923);
                    atom();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:270:4: ^( PLUS_ASSIGN ID block )
                    {
                    match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement930); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_labeledElement932); 
                    pushFollow(FOLLOW_block_in_labeledElement934);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "labeledElement"


    // $ANTLR start "treeSpec"
    // ASTVerifier.g:273:1: treeSpec : ^( TREE_BEGIN ( element )+ ) ;
    public final void treeSpec() throws RecognitionException {
        try {
            // ASTVerifier.g:274:5: ( ^( TREE_BEGIN ( element )+ ) )
            // ASTVerifier.g:274:7: ^( TREE_BEGIN ( element )+ )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec950); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:274:20: ( element )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==SEMPRED||LA30_0==ACTION||LA30_0==IMPLIES||LA30_0==ASSIGN||LA30_0==BANG||LA30_0==PLUS_ASSIGN||LA30_0==ROOT||(LA30_0>=DOT && LA30_0<=RANGE)||LA30_0==TREE_BEGIN||LA30_0==NOT||(LA30_0>=TOKEN_REF && LA30_0<=RULE_REF)||LA30_0==STRING_LITERAL||LA30_0==BLOCK||(LA30_0>=OPTIONAL && LA30_0<=POSITIVE_CLOSURE)||LA30_0==GATED_SEMPRED||LA30_0==WILDCARD) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // ASTVerifier.g:274:20: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec952);
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "treeSpec"


    // $ANTLR start "ebnf"
    // ASTVerifier.g:277:1: ebnf : ( ^( blockSuffix block ) | block );
    public final void ebnf() throws RecognitionException {
        try {
            // ASTVerifier.g:277:5: ( ^( blockSuffix block ) | block )
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
                    // ASTVerifier.g:277:7: ^( blockSuffix block )
                    {
                    pushFollow(FOLLOW_blockSuffix_in_ebnf967);
                    blockSuffix();

                    state._fsp--;


                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_ebnf969);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:278:5: block
                    {
                    pushFollow(FOLLOW_block_in_ebnf976);
                    block();

                    state._fsp--;


                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ebnf"


    // $ANTLR start "blockSuffix"
    // ASTVerifier.g:281:1: blockSuffix : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final void blockSuffix() throws RecognitionException {
        try {
            // ASTVerifier.g:282:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
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
                    // ASTVerifier.g:282:7: ebnfSuffix
                    {
                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffix993);
                    ebnfSuffix();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:283:7: ROOT
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_blockSuffix1001); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:284:7: IMPLIES
                    {
                    match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffix1009); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:285:7: BANG
                    {
                    match(input,BANG,FOLLOW_BANG_in_blockSuffix1017); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "blockSuffix"


    // $ANTLR start "ebnfSuffix"
    // ASTVerifier.g:288:1: ebnfSuffix : ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE );
    public final void ebnfSuffix() throws RecognitionException {
        try {
            // ASTVerifier.g:289:2: ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE )
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ebnfSuffix"


    // $ANTLR start "atom"
    // ASTVerifier.g:294:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | ^( ROOT terminal ) | ^( BANG terminal ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD elementOptions ) | WILDCARD | terminal | ruleref );
    public final void atom() throws RecognitionException {
        try {
            // ASTVerifier.g:294:5: ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | ^( ROOT terminal ) | ^( BANG terminal ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD elementOptions ) | WILDCARD | terminal | ruleref )
            int alt33=14;
            alt33 = dfa33.predict(input);
            switch (alt33) {
                case 1 :
                    // ASTVerifier.g:294:7: ^( ROOT range )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom1057); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom1059);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:295:4: ^( BANG range )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom1066); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_range_in_atom1068);
                    range();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:296:4: ^( ROOT notSet )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom1075); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom1077);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:297:4: ^( BANG notSet )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom1084); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_notSet_in_atom1086);
                    notSet();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:298:4: notSet
                    {
                    pushFollow(FOLLOW_notSet_in_atom1092);
                    notSet();

                    state._fsp--;


                    }
                    break;
                case 6 :
                    // ASTVerifier.g:299:7: ^( ROOT terminal )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_atom1101); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_atom1103);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:300:7: ^( BANG terminal )
                    {
                    match(input,BANG,FOLLOW_BANG_in_atom1113); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_atom1115);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 8 :
                    // ASTVerifier.g:301:4: range
                    {
                    pushFollow(FOLLOW_range_in_atom1121);
                    range();

                    state._fsp--;


                    }
                    break;
                case 9 :
                    // ASTVerifier.g:302:4: ^( DOT ID terminal )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom1127); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom1129); 
                    pushFollow(FOLLOW_terminal_in_atom1131);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 10 :
                    // ASTVerifier.g:303:4: ^( DOT ID ruleref )
                    {
                    match(input,DOT,FOLLOW_DOT_in_atom1138); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_atom1140); 
                    pushFollow(FOLLOW_ruleref_in_atom1142);
                    ruleref();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 11 :
                    // ASTVerifier.g:304:7: ^( WILDCARD elementOptions )
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom1152); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_atom1154);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 12 :
                    // ASTVerifier.g:305:7: WILDCARD
                    {
                    match(input,WILDCARD,FOLLOW_WILDCARD_in_atom1163); 

                    }
                    break;
                case 13 :
                    // ASTVerifier.g:306:9: terminal
                    {
                    pushFollow(FOLLOW_terminal_in_atom1173);
                    terminal();

                    state._fsp--;


                    }
                    break;
                case 14 :
                    // ASTVerifier.g:307:9: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_atom1183);
                    ruleref();

                    state._fsp--;


                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "atom"


    // $ANTLR start "notSet"
    // ASTVerifier.g:310:1: notSet : ( ^( NOT terminal ) | ^( NOT block ) );
    public final void notSet() throws RecognitionException {
        try {
            // ASTVerifier.g:311:5: ( ^( NOT terminal ) | ^( NOT block ) )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==NOT) ) {
                int LA34_1 = input.LA(2);

                if ( (LA34_1==DOWN) ) {
                    int LA34_2 = input.LA(3);

                    if ( (LA34_2==BLOCK) ) {
                        alt34=2;
                    }
                    else if ( (LA34_2==TOKEN_REF||LA34_2==STRING_LITERAL) ) {
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
                    // ASTVerifier.g:311:7: ^( NOT terminal )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet1201); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_terminal_in_notSet1203);
                    terminal();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:312:7: ^( NOT block )
                    {
                    match(input,NOT,FOLLOW_NOT_in_notSet1213); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_block_in_notSet1215);
                    block();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "notSet"


    // $ANTLR start "block"
    // ASTVerifier.g:315:1: block : ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? altList ) ;
    public final void block() throws RecognitionException {
        try {
            // ASTVerifier.g:316:5: ( ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? altList ) )
            // ASTVerifier.g:316:7: ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? altList )
            {
            match(input,BLOCK,FOLLOW_BLOCK_in_block1234); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:316:15: ( optionsSpec )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==OPTIONS) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // ASTVerifier.g:316:15: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_block1236);
                    optionsSpec();

                    state._fsp--;


                    }
                    break;

            }

            // ASTVerifier.g:316:28: ( ruleAction )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( (LA36_0==AT) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // ASTVerifier.g:316:28: ruleAction
            	    {
            	    pushFollow(FOLLOW_ruleAction_in_block1239);
            	    ruleAction();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);

            // ASTVerifier.g:316:40: ( ACTION )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==ACTION) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // ASTVerifier.g:316:40: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_block1242); 

                    }
                    break;

            }

            pushFollow(FOLLOW_altList_in_block1245);
            altList();

            state._fsp--;


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "block"


    // $ANTLR start "ruleref"
    // ASTVerifier.g:319:1: ruleref : ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) );
    public final void ruleref() throws RecognitionException {
        try {
            // ASTVerifier.g:320:5: ( ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) ) | ^( BANG ^( RULE_REF ( ARG_ACTION )? ) ) | ^( RULE_REF ( ARG_ACTION )? ) )
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
                    // ASTVerifier.g:320:7: ^( ROOT ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,ROOT,FOLLOW_ROOT_in_ruleref1264); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1267); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // ASTVerifier.g:320:25: ( ARG_ACTION )?
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( (LA38_0==ARG_ACTION) ) {
                            alt38=1;
                        }
                        switch (alt38) {
                            case 1 :
                                // ASTVerifier.g:320:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1269); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:321:7: ^( BANG ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                    match(input,BANG,FOLLOW_BANG_in_ruleref1281); 

                    match(input, Token.DOWN, null); 
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1284); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // ASTVerifier.g:321:25: ( ARG_ACTION )?
                        int alt39=2;
                        int LA39_0 = input.LA(1);

                        if ( (LA39_0==ARG_ACTION) ) {
                            alt39=1;
                        }
                        switch (alt39) {
                            case 1 :
                                // ASTVerifier.g:321:25: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1286); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:322:7: ^( RULE_REF ( ARG_ACTION )? )
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1298); 

                    if ( input.LA(1)==Token.DOWN ) {
                        match(input, Token.DOWN, null); 
                        // ASTVerifier.g:322:18: ( ARG_ACTION )?
                        int alt40=2;
                        int LA40_0 = input.LA(1);

                        if ( (LA40_0==ARG_ACTION) ) {
                            alt40=1;
                        }
                        switch (alt40) {
                            case 1 :
                                // ASTVerifier.g:322:18: ARG_ACTION
                                {
                                match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref1300); 

                                }
                                break;

                        }


                        match(input, Token.UP, null); 
                    }

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ruleref"


    // $ANTLR start "range"
    // ASTVerifier.g:325:1: range : ^( RANGE STRING_LITERAL STRING_LITERAL ) ;
    public final void range() throws RecognitionException {
        try {
            // ASTVerifier.g:326:5: ( ^( RANGE STRING_LITERAL STRING_LITERAL ) )
            // ASTVerifier.g:326:7: ^( RANGE STRING_LITERAL STRING_LITERAL )
            {
            match(input,RANGE,FOLLOW_RANGE_in_range1320); 

            match(input, Token.DOWN, null); 
            match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range1322); 
            match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range1324); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "range"


    // $ANTLR start "terminal"
    // ASTVerifier.g:329:1: terminal : ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | TOKEN_REF );
    public final void terminal() throws RecognitionException {
        try {
            // ASTVerifier.g:330:5: ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | TOKEN_REF )
            int alt42=6;
            alt42 = dfa42.predict(input);
            switch (alt42) {
                case 1 :
                    // ASTVerifier.g:330:8: ^( STRING_LITERAL elementOptions )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1344); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1346);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:331:7: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1355); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:332:7: ^( TOKEN_REF ARG_ACTION elementOptions )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1364); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal1366); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1368);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:333:7: ^( TOKEN_REF ARG_ACTION )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1378); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal1380); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:334:7: ^( TOKEN_REF elementOptions )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1390); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_terminal1392);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:335:7: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1401); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "terminal"


    // $ANTLR start "elementOptions"
    // ASTVerifier.g:338:1: elementOptions : ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
    public final void elementOptions() throws RecognitionException {
        try {
            // ASTVerifier.g:339:5: ( ^( ELEMENT_OPTIONS ( elementOption )+ ) )
            // ASTVerifier.g:339:7: ^( ELEMENT_OPTIONS ( elementOption )+ )
            {
            match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOptions1419); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:339:25: ( elementOption )+
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
            	    // ASTVerifier.g:339:25: elementOption
            	    {
            	    pushFollow(FOLLOW_elementOption_in_elementOptions1421);
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementOptions"


    // $ANTLR start "elementOption"
    // ASTVerifier.g:342:1: elementOption : ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) );
    public final void elementOption() throws RecognitionException {
        try {
            // ASTVerifier.g:343:5: ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) )
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
                    // ASTVerifier.g:343:7: ID
                    {
                    match(input,ID,FOLLOW_ID_in_elementOption1440); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:344:9: ^( ASSIGN ID ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1451); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_elementOption1453); 
                    match(input,ID,FOLLOW_ID_in_elementOption1455); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:345:9: ^( ASSIGN ID STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1467); 

                    match(input, Token.DOWN, null); 
                    match(input,ID,FOLLOW_ID_in_elementOption1469); 
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption1471); 

                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementOption"


    // $ANTLR start "rewrite"
    // ASTVerifier.g:348:1: rewrite : ( predicatedRewrite )* nakedRewrite ;
    public final void rewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:349:2: ( ( predicatedRewrite )* nakedRewrite )
            // ASTVerifier.g:349:4: ( predicatedRewrite )* nakedRewrite
            {
            // ASTVerifier.g:349:4: ( predicatedRewrite )*
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
            	    // ASTVerifier.g:349:4: predicatedRewrite
            	    {
            	    pushFollow(FOLLOW_predicatedRewrite_in_rewrite1486);
            	    predicatedRewrite();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);

            pushFollow(FOLLOW_nakedRewrite_in_rewrite1489);
            nakedRewrite();

            state._fsp--;


            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewrite"


    // $ANTLR start "predicatedRewrite"
    // ASTVerifier.g:352:1: predicatedRewrite : ( ^( ST_RESULT SEMPRED rewriteAlt ) | ^( RESULT SEMPRED rewriteAlt ) );
    public final void predicatedRewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:353:2: ( ^( ST_RESULT SEMPRED rewriteAlt ) | ^( RESULT SEMPRED rewriteAlt ) )
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
                    // ASTVerifier.g:353:4: ^( ST_RESULT SEMPRED rewriteAlt )
                    {
                    match(input,ST_RESULT,FOLLOW_ST_RESULT_in_predicatedRewrite1501); 

                    match(input, Token.DOWN, null); 
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite1503); 
                    pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite1505);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:354:4: ^( RESULT SEMPRED rewriteAlt )
                    {
                    match(input,RESULT,FOLLOW_RESULT_in_predicatedRewrite1512); 

                    match(input, Token.DOWN, null); 
                    match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite1514); 
                    pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite1516);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "predicatedRewrite"


    // $ANTLR start "nakedRewrite"
    // ASTVerifier.g:357:1: nakedRewrite : ( ^( ST_RESULT rewriteAlt ) | ^( RESULT rewriteAlt ) );
    public final void nakedRewrite() throws RecognitionException {
        try {
            // ASTVerifier.g:358:2: ( ^( ST_RESULT rewriteAlt ) | ^( RESULT rewriteAlt ) )
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
                    // ASTVerifier.g:358:4: ^( ST_RESULT rewriteAlt )
                    {
                    match(input,ST_RESULT,FOLLOW_ST_RESULT_in_nakedRewrite1530); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite1532);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:359:4: ^( RESULT rewriteAlt )
                    {
                    match(input,RESULT,FOLLOW_RESULT_in_nakedRewrite1539); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite1541);
                    rewriteAlt();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "nakedRewrite"


    // $ANTLR start "rewriteAlt"
    // ASTVerifier.g:362:1: rewriteAlt : ( rewriteTemplate | rewriteTreeAlt | ETC | EPSILON );
    public final void rewriteAlt() throws RecognitionException {
        try {
            // ASTVerifier.g:363:5: ( rewriteTemplate | rewriteTreeAlt | ETC | EPSILON )
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
                    // ASTVerifier.g:363:7: rewriteTemplate
                    {
                    pushFollow(FOLLOW_rewriteTemplate_in_rewriteAlt1557);
                    rewriteTemplate();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:364:7: rewriteTreeAlt
                    {
                    pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteAlt1565);
                    rewriteTreeAlt();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:365:7: ETC
                    {
                    match(input,ETC,FOLLOW_ETC_in_rewriteAlt1573); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:366:7: EPSILON
                    {
                    match(input,EPSILON,FOLLOW_EPSILON_in_rewriteAlt1581); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteAlt"


    // $ANTLR start "rewriteTreeAlt"
    // ASTVerifier.g:369:1: rewriteTreeAlt : ^( ALT ( rewriteTreeElement )+ ) ;
    public final void rewriteTreeAlt() throws RecognitionException {
        try {
            // ASTVerifier.g:370:5: ( ^( ALT ( rewriteTreeElement )+ ) )
            // ASTVerifier.g:370:7: ^( ALT ( rewriteTreeElement )+ )
            {
            match(input,ALT,FOLLOW_ALT_in_rewriteTreeAlt1600); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:370:13: ( rewriteTreeElement )+
            int cnt49=0;
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);

                if ( (LA49_0==ACTION||LA49_0==TREE_BEGIN||(LA49_0>=TOKEN_REF && LA49_0<=RULE_REF)||LA49_0==STRING_LITERAL||(LA49_0>=OPTIONAL && LA49_0<=POSITIVE_CLOSURE)||LA49_0==LABEL) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // ASTVerifier.g:370:13: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTreeAlt1602);
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTreeAlt"


    // $ANTLR start "rewriteTreeElement"
    // ASTVerifier.g:373:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTree | rewriteTreeEbnf );
    public final void rewriteTreeElement() throws RecognitionException {
        try {
            // ASTVerifier.g:374:2: ( rewriteTreeAtom | rewriteTree | rewriteTreeEbnf )
            int alt50=3;
            switch ( input.LA(1) ) {
            case ACTION:
            case TOKEN_REF:
            case RULE_REF:
            case STRING_LITERAL:
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
                    // ASTVerifier.g:374:4: rewriteTreeAtom
                    {
                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement1618);
                    rewriteTreeAtom();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // ASTVerifier.g:375:4: rewriteTree
                    {
                    pushFollow(FOLLOW_rewriteTree_in_rewriteTreeElement1623);
                    rewriteTree();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // ASTVerifier.g:376:6: rewriteTreeEbnf
                    {
                    pushFollow(FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement1630);
                    rewriteTreeEbnf();

                    state._fsp--;


                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTreeElement"


    // $ANTLR start "rewriteTreeAtom"
    // ASTVerifier.g:379:1: rewriteTreeAtom : ( ^( TOKEN_REF elementOptions ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | LABEL | ACTION );
    public final void rewriteTreeAtom() throws RecognitionException {
        try {
            // ASTVerifier.g:380:5: ( ^( TOKEN_REF elementOptions ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | LABEL | ACTION )
            int alt51=9;
            alt51 = dfa51.predict(input);
            switch (alt51) {
                case 1 :
                    // ASTVerifier.g:380:9: ^( TOKEN_REF elementOptions ARG_ACTION )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1647); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom1649);
                    elementOptions();

                    state._fsp--;

                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom1651); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:381:9: ^( TOKEN_REF elementOptions )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1663); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom1665);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:382:9: ^( TOKEN_REF ARG_ACTION )
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1677); 

                    match(input, Token.DOWN, null); 
                    match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom1679); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 4 :
                    // ASTVerifier.g:383:6: TOKEN_REF
                    {
                    match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom1687); 

                    }
                    break;
                case 5 :
                    // ASTVerifier.g:384:9: RULE_REF
                    {
                    match(input,RULE_REF,FOLLOW_RULE_REF_in_rewriteTreeAtom1697); 

                    }
                    break;
                case 6 :
                    // ASTVerifier.g:385:6: ^( STRING_LITERAL elementOptions )
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1705); 

                    match(input, Token.DOWN, null); 
                    pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom1707);
                    elementOptions();

                    state._fsp--;


                    match(input, Token.UP, null); 

                    }
                    break;
                case 7 :
                    // ASTVerifier.g:386:6: STRING_LITERAL
                    {
                    match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1715); 

                    }
                    break;
                case 8 :
                    // ASTVerifier.g:387:6: LABEL
                    {
                    match(input,LABEL,FOLLOW_LABEL_in_rewriteTreeAtom1723); 

                    }
                    break;
                case 9 :
                    // ASTVerifier.g:388:4: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_rewriteTreeAtom1728); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTreeAtom"


    // $ANTLR start "rewriteTreeEbnf"
    // ASTVerifier.g:391:1: rewriteTreeEbnf : ^( ebnfSuffix ^( REWRITE_BLOCK rewriteTreeAlt ) ) ;
    public final void rewriteTreeEbnf() throws RecognitionException {
        try {
            // ASTVerifier.g:392:2: ( ^( ebnfSuffix ^( REWRITE_BLOCK rewriteTreeAlt ) ) )
            // ASTVerifier.g:392:4: ^( ebnfSuffix ^( REWRITE_BLOCK rewriteTreeAlt ) )
            {
            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeEbnf1740);
            ebnfSuffix();

            state._fsp--;


            match(input, Token.DOWN, null); 
            match(input,REWRITE_BLOCK,FOLLOW_REWRITE_BLOCK_in_rewriteTreeEbnf1743); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf1745);
            rewriteTreeAlt();

            state._fsp--;


            match(input, Token.UP, null); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTreeEbnf"


    // $ANTLR start "rewriteTree"
    // ASTVerifier.g:394:1: rewriteTree : ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) ;
    public final void rewriteTree() throws RecognitionException {
        try {
            // ASTVerifier.g:395:2: ( ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) )
            // ASTVerifier.g:395:4: ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
            {
            match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewriteTree1758); 

            match(input, Token.DOWN, null); 
            pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTree1760);
            rewriteTreeAtom();

            state._fsp--;

            // ASTVerifier.g:395:33: ( rewriteTreeElement )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==ACTION||LA52_0==TREE_BEGIN||(LA52_0>=TOKEN_REF && LA52_0<=RULE_REF)||LA52_0==STRING_LITERAL||(LA52_0>=OPTIONAL && LA52_0<=POSITIVE_CLOSURE)||LA52_0==LABEL) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // ASTVerifier.g:395:33: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTree1762);
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTree"


    // $ANTLR start "rewriteTemplate"
    // ASTVerifier.g:398:1: rewriteTemplate : ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );
    public final void rewriteTemplate() throws RecognitionException {
        try {
            // ASTVerifier.g:399:2: ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION )
            int alt55=5;
            alt55 = dfa55.predict(input);
            switch (alt55) {
                case 1 :
                    // ASTVerifier.g:399:4: ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL )
                    {
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate1777); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:399:15: ( rewriteTemplateArgs )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==ARGLIST) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // ASTVerifier.g:399:15: rewriteTemplateArgs
                            {
                            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1779);
                            rewriteTemplateArgs();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,DOUBLE_QUOTE_STRING_LITERAL,FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate1782); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 2 :
                    // ASTVerifier.g:400:4: ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL )
                    {
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate1789); 

                    match(input, Token.DOWN, null); 
                    // ASTVerifier.g:400:15: ( rewriteTemplateArgs )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==ARGLIST) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // ASTVerifier.g:400:15: rewriteTemplateArgs
                            {
                            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1791);
                            rewriteTemplateArgs();

                            state._fsp--;


                            }
                            break;

                    }

                    match(input,DOUBLE_ANGLE_STRING_LITERAL,FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate1794); 

                    match(input, Token.UP, null); 

                    }
                    break;
                case 3 :
                    // ASTVerifier.g:401:4: rewriteTemplateRef
                    {
                    pushFollow(FOLLOW_rewriteTemplateRef_in_rewriteTemplate1800);
                    rewriteTemplateRef();

                    state._fsp--;


                    }
                    break;
                case 4 :
                    // ASTVerifier.g:402:4: rewriteIndirectTemplateHead
                    {
                    pushFollow(FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate1805);
                    rewriteIndirectTemplateHead();

                    state._fsp--;


                    }
                    break;
                case 5 :
                    // ASTVerifier.g:403:4: ACTION
                    {
                    match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplate1810); 

                    }
                    break;

            }
        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTemplate"


    // $ANTLR start "rewriteTemplateRef"
    // ASTVerifier.g:406:1: rewriteTemplateRef : ^( TEMPLATE ID ( rewriteTemplateArgs )? ) ;
    public final void rewriteTemplateRef() throws RecognitionException {
        try {
            // ASTVerifier.g:407:2: ( ^( TEMPLATE ID ( rewriteTemplateArgs )? ) )
            // ASTVerifier.g:407:4: ^( TEMPLATE ID ( rewriteTemplateArgs )? )
            {
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplateRef1822); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rewriteTemplateRef1824); 
            // ASTVerifier.g:407:18: ( rewriteTemplateArgs )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( (LA56_0==ARGLIST) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // ASTVerifier.g:407:18: rewriteTemplateArgs
                    {
                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef1826);
                    rewriteTemplateArgs();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTemplateRef"


    // $ANTLR start "rewriteIndirectTemplateHead"
    // ASTVerifier.g:410:1: rewriteIndirectTemplateHead : ^( TEMPLATE ACTION ( rewriteTemplateArgs )? ) ;
    public final void rewriteIndirectTemplateHead() throws RecognitionException {
        try {
            // ASTVerifier.g:411:2: ( ^( TEMPLATE ACTION ( rewriteTemplateArgs )? ) )
            // ASTVerifier.g:411:4: ^( TEMPLATE ACTION ( rewriteTemplateArgs )? )
            {
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteIndirectTemplateHead1840); 

            match(input, Token.DOWN, null); 
            match(input,ACTION,FOLLOW_ACTION_in_rewriteIndirectTemplateHead1842); 
            // ASTVerifier.g:411:22: ( rewriteTemplateArgs )?
            int alt57=2;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==ARGLIST) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // ASTVerifier.g:411:22: rewriteTemplateArgs
                    {
                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead1844);
                    rewriteTemplateArgs();

                    state._fsp--;


                    }
                    break;

            }


            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteIndirectTemplateHead"


    // $ANTLR start "rewriteTemplateArgs"
    // ASTVerifier.g:414:1: rewriteTemplateArgs : ^( ARGLIST ( rewriteTemplateArg )+ ) ;
    public final void rewriteTemplateArgs() throws RecognitionException {
        try {
            // ASTVerifier.g:415:2: ( ^( ARGLIST ( rewriteTemplateArg )+ ) )
            // ASTVerifier.g:415:4: ^( ARGLIST ( rewriteTemplateArg )+ )
            {
            match(input,ARGLIST,FOLLOW_ARGLIST_in_rewriteTemplateArgs1858); 

            match(input, Token.DOWN, null); 
            // ASTVerifier.g:415:14: ( rewriteTemplateArg )+
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
            	    // ASTVerifier.g:415:14: rewriteTemplateArg
            	    {
            	    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs1860);
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
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTemplateArgs"


    // $ANTLR start "rewriteTemplateArg"
    // ASTVerifier.g:418:1: rewriteTemplateArg : ^( ARG ID ACTION ) ;
    public final void rewriteTemplateArg() throws RecognitionException {
        try {
            // ASTVerifier.g:419:2: ( ^( ARG ID ACTION ) )
            // ASTVerifier.g:419:6: ^( ARG ID ACTION )
            {
            match(input,ARG,FOLLOW_ARG_in_rewriteTemplateArg1876); 

            match(input, Token.DOWN, null); 
            match(input,ID,FOLLOW_ID_in_rewriteTemplateArg1878); 
            match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplateArg1880); 

            match(input, Token.UP, null); 

            }

        }
         catch (RecognitionException e) {
        throw e;
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "rewriteTemplateArg"

    // Delegated rules


    protected DFA28 dfa28 = new DFA28(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA51 dfa51 = new DFA51(this);
    protected DFA55 dfa55 = new DFA55(this);
    static final String DFA28_eotS =
        "\14\uffff";
    static final String DFA28_eofS =
        "\14\uffff";
    static final String DFA28_minS =
        "\1\4\1\uffff\2\2\6\uffff\2\70";
    static final String DFA28_maxS =
        "\1\141\1\uffff\2\2\6\uffff\2\115";
    static final String DFA28_acceptS =
        "\1\uffff\1\1\2\uffff\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff";
    static final String DFA28_specialS =
        "\14\uffff}>";
    static final String[] DFA28_transitionS = {
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
            "\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff\1\4\10\uffff\1\5",
            "\1\4\4\uffff\1\4\1\uffff\2\4\3\uffff\1\4\10\uffff\1\5"
    };

    static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
    static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
    static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
    static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
    static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
    static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
    static final short[][] DFA28_transition;

    static {
        int numStates = DFA28_transitionS.length;
        DFA28_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
        }
    }

    class DFA28 extends DFA {

        public DFA28(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 28;
            this.eot = DFA28_eot;
            this.eof = DFA28_eof;
            this.min = DFA28_min;
            this.max = DFA28_max;
            this.accept = DFA28_accept;
            this.special = DFA28_special;
            this.transition = DFA28_transition;
        }
        public String getDescription() {
            return "256:1: element : ( labeledElement | atom | ebnf | ACTION | SEMPRED | GATED_SEMPRED | treeSpec );";
        }
    }
    static final String DFA29_eotS =
        "\13\uffff";
    static final String DFA29_eofS =
        "\13\uffff";
    static final String DFA29_minS =
        "\1\56\2\2\2\127\2\60\4\uffff";
    static final String DFA29_maxS =
        "\1\63\2\2\2\127\2\141\4\uffff";
    static final String DFA29_acceptS =
        "\7\uffff\1\1\1\2\1\3\1\4";
    static final String DFA29_specialS =
        "\13\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\1\4\uffff\1\2",
            "\1\3",
            "\1\4",
            "\1\5",
            "\1\6",
            "\1\7\4\uffff\1\7\1\uffff\2\7\4\uffff\1\7\1\uffff\2\7\3\uffff"+
            "\1\7\10\uffff\1\10\23\uffff\1\7",
            "\1\11\4\uffff\1\11\1\uffff\2\11\4\uffff\1\11\1\uffff\2\11\3"+
            "\uffff\1\11\10\uffff\1\12\23\uffff\1\11",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "266:1: labeledElement : ( ^( ASSIGN ID atom ) | ^( ASSIGN ID block ) | ^( PLUS_ASSIGN ID atom ) | ^( PLUS_ASSIGN ID block ) );";
        }
    }
    static final String DFA33_eotS =
        "\27\uffff";
    static final String DFA33_eofS =
        "\27\uffff";
    static final String DFA33_minS =
        "\1\60\2\2\2\uffff\2\2\2\uffff\2\70\1\127\10\uffff\1\60\2\uffff";
    static final String DFA33_maxS =
        "\1\141\2\2\2\uffff\1\2\1\141\2\uffff\2\104\1\127\10\uffff\1\104"+
        "\2\uffff";
    static final String DFA33_acceptS =
        "\3\uffff\1\5\1\10\2\uffff\1\15\1\16\3\uffff\1\13\1\14\1\3\1\1\1"+
        "\6\1\2\1\7\1\4\1\uffff\1\11\1\12";
    static final String DFA33_specialS =
        "\27\uffff}>";
    static final String[] DFA33_transitionS = {
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
            "\1\17\4\uffff\1\16\1\uffff\1\20\1\10\3\uffff\1\20",
            "\1\21\4\uffff\1\23\1\uffff\1\22\1\10\3\uffff\1\22",
            "\1\24",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\26\4\uffff\1\26\11\uffff\1\25\1\26\3\uffff\1\25",
            "",
            ""
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
            return "294:1: atom : ( ^( ROOT range ) | ^( BANG range ) | ^( ROOT notSet ) | ^( BANG notSet ) | notSet | ^( ROOT terminal ) | ^( BANG terminal ) | range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD elementOptions ) | WILDCARD | terminal | ruleref );";
        }
    }
    static final String DFA42_eotS =
        "\13\uffff";
    static final String DFA42_eofS =
        "\13\uffff";
    static final String DFA42_minS =
        "\1\77\2\2\2\uffff\1\16\1\uffff\1\3\3\uffff";
    static final String DFA42_maxS =
        "\1\104\2\141\2\uffff\1\143\1\uffff\1\143\3\uffff";
    static final String DFA42_acceptS =
        "\3\uffff\1\1\1\2\1\uffff\1\6\1\uffff\1\5\1\4\1\3";
    static final String DFA42_specialS =
        "\13\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\2\4\uffff\1\1",
            "\1\3\2\4\13\uffff\1\4\32\uffff\1\4\2\uffff\1\4\1\uffff\1\4"+
            "\2\uffff\1\4\1\uffff\1\4\1\uffff\2\4\2\uffff\1\4\1\uffff\1\4"+
            "\1\uffff\2\4\3\uffff\1\4\10\uffff\1\4\1\uffff\3\4\14\uffff\1"+
            "\4\2\uffff\1\4",
            "\1\5\2\6\13\uffff\1\6\32\uffff\1\6\2\uffff\1\6\1\uffff\1\6"+
            "\2\uffff\1\6\1\uffff\1\6\1\uffff\2\6\2\uffff\1\6\1\uffff\1\6"+
            "\1\uffff\2\6\3\uffff\1\6\10\uffff\1\6\1\uffff\3\6\14\uffff\1"+
            "\6\2\uffff\1\6",
            "",
            "",
            "\1\7\124\uffff\1\10",
            "",
            "\1\11\137\uffff\1\12",
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
            return "329:1: terminal : ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | TOKEN_REF );";
        }
    }
    static final String DFA51_eotS =
        "\30\uffff";
    static final String DFA51_eofS =
        "\30\uffff";
    static final String DFA51_minS =
        "\1\20\1\2\1\uffff\1\2\2\uffff\1\16\4\uffff\1\2\1\56\1\3\1\2\1\3"+
        "\1\127\2\uffff\1\104\4\3";
    static final String DFA51_maxS =
        "\2\135\1\uffff\1\135\2\uffff\1\143\4\uffff\1\2\2\127\1\2\1\16\1"+
        "\127\2\uffff\1\127\2\3\2\127";
    static final String DFA51_acceptS =
        "\2\uffff\1\5\1\uffff\1\10\1\11\1\uffff\1\4\1\6\1\7\1\3\6\uffff\1"+
        "\1\1\2\5\uffff";
    static final String DFA51_specialS =
        "\30\uffff}>";
    static final String[] DFA51_transitionS = {
            "\1\5\56\uffff\1\1\1\2\3\uffff\1\3\30\uffff\1\4",
            "\1\6\1\7\14\uffff\1\7\52\uffff\1\7\3\uffff\2\7\3\uffff\1\7"+
            "\12\uffff\3\7\13\uffff\1\7",
            "",
            "\1\10\1\11\14\uffff\1\11\52\uffff\1\11\3\uffff\2\11\3\uffff"+
            "\1\11\12\uffff\3\11\13\uffff\1\11",
            "",
            "",
            "\1\12\124\uffff\1\13",
            "",
            "",
            "",
            "",
            "\1\14",
            "\1\16\50\uffff\1\15",
            "\1\17\52\uffff\1\16\50\uffff\1\15",
            "\1\20",
            "\1\22\12\uffff\1\21",
            "\1\23",
            "",
            "",
            "\1\25\22\uffff\1\24",
            "\1\26",
            "\1\27",
            "\1\17\52\uffff\1\16\50\uffff\1\15",
            "\1\17\52\uffff\1\16\50\uffff\1\15"
    };

    static final short[] DFA51_eot = DFA.unpackEncodedString(DFA51_eotS);
    static final short[] DFA51_eof = DFA.unpackEncodedString(DFA51_eofS);
    static final char[] DFA51_min = DFA.unpackEncodedStringToUnsignedChars(DFA51_minS);
    static final char[] DFA51_max = DFA.unpackEncodedStringToUnsignedChars(DFA51_maxS);
    static final short[] DFA51_accept = DFA.unpackEncodedString(DFA51_acceptS);
    static final short[] DFA51_special = DFA.unpackEncodedString(DFA51_specialS);
    static final short[][] DFA51_transition;

    static {
        int numStates = DFA51_transitionS.length;
        DFA51_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA51_transition[i] = DFA.unpackEncodedString(DFA51_transitionS[i]);
        }
    }

    class DFA51 extends DFA {

        public DFA51(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 51;
            this.eot = DFA51_eot;
            this.eof = DFA51_eof;
            this.min = DFA51_min;
            this.max = DFA51_max;
            this.accept = DFA51_accept;
            this.special = DFA51_special;
            this.transition = DFA51_transition;
        }
        public String getDescription() {
            return "379:1: rewriteTreeAtom : ( ^( TOKEN_REF elementOptions ARG_ACTION ) | ^( TOKEN_REF elementOptions ) | ^( TOKEN_REF ARG_ACTION ) | TOKEN_REF | RULE_REF | ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | LABEL | ACTION );";
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
        "\2\uffff\1\5\1\uffff\1\3\1\4\1\uffff\1\1\1\2\7\uffff";
    static final String DFA55_specialS =
        "\20\uffff}>";
    static final String[] DFA55_transitionS = {
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
            return "398:1: rewriteTemplate : ( ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_QUOTE_STRING_LITERAL ) | ^( TEMPLATE ( rewriteTemplateArgs )? DOUBLE_ANGLE_STRING_LITERAL ) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );";
        }
    }
 

    public static final BitSet FOLLOW_GRAMMAR_in_grammarSpec81 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec83 = new BitSet(new long[]{0x1000000000780040L,0x0000000000000400L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec85 = new BitSet(new long[]{0x1000000000780040L,0x0000000000000400L});
    public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec88 = new BitSet(new long[]{0x1000000000780040L,0x0000000000000400L});
    public static final BitSet FOLLOW_rules_in_grammarSpec91 = new BitSet(new long[]{0x0000001000000008L});
    public static final BitSet FOLLOW_mode_in_grammarSpec93 = new BitSet(new long[]{0x0000001000000008L});
    public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrScope_in_prequelConstruct139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_prequelConstruct149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec164 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_option_in_optionsSpec166 = new BitSet(new long[]{0x0000400000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_option188 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option190 = new BitSet(new long[]{0x0002000000000000L,0x0000000000800012L});
    public static final BitSet FOLLOW_optionValue_in_option192 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_delegateGrammars277 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars279 = new BitSet(new long[]{0x0000400000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar298 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar300 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar302 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_delegateGrammar313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec330 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec332 = new BitSet(new long[]{0x0000400000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec346 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokenSpec348 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec350 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_tokenSpec356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope368 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_attrScope370 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope372 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_AT_in_action385 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_action387 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_action390 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action392 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULES_in_rules408 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rule_in_rules410 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
    public static final BitSet FOLLOW_MODE_in_mode426 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_mode428 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
    public static final BitSet FOLLOW_rule_in_mode430 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
    public static final BitSet FOLLOW_RULE_in_rule446 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule448 = new BitSet(new long[]{0x1000000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_rule450 = new BitSet(new long[]{0x1000000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_ruleModifiers_in_rule453 = new BitSet(new long[]{0x1000000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule456 = new BitSet(new long[]{0x1000000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_ruleReturns_in_rule469 = new BitSet(new long[]{0x1000000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_rulePrequel_in_rule472 = new BitSet(new long[]{0x1000000180284040L,0x0000000000002800L});
    public static final BitSet FOLLOW_altListAsBlock_in_rule475 = new BitSet(new long[]{0x0000000600000008L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule477 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup504 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler523 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler525 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler527 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause540 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause542 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_throwsSpec_in_rulePrequel559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rulePrequel569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_rulePrequel579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_rulePrequel589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns604 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns606 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_THROWS_in_throwsSpec621 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_throwsSpec623 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec640 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec642 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec649 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleScopeSpec651 = new BitSet(new long[]{0x0000000000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_AT_in_ruleAction665 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_ruleAction667 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction669 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULEMODIFIERS_in_ruleModifiers685 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ruleModifier_in_ruleModifiers687 = new BitSet(new long[]{0x0000000070800008L});
    public static final BitSet FOLLOW_set_in_ruleModifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternative_in_altList747 = new BitSet(new long[]{0x0000000000000002L,0x0000004000200000L});
    public static final BitSet FOLLOW_BLOCK_in_altListAsBlock766 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_altList_in_altListAsBlock768 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_REWRITE_in_alternative787 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_alternative_in_alternative789 = new BitSet(new long[]{0x0000000000000000L,0x0000003000000000L});
    public static final BitSet FOLLOW_rewrite_in_alternative791 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_alternative801 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_EPSILON_in_alternative803 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_elements_in_alternative814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_elements832 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_elements834 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_labeledElement_in_element850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GATED_SEMPRED_in_element879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement897 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement899 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement901 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement908 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement910 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement912 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement919 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement921 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement923 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement930 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_labeledElement932 = new BitSet(new long[]{0x0021080000000000L,0x000000000003A000L});
    public static final BitSet FOLLOW_block_in_labeledElement934 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec950 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_element_in_treeSpec952 = new BitSet(new long[]{0xA9A9480000010018L,0x000000024003A011L});
    public static final BitSet FOLLOW_blockSuffix_in_ebnf967 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_ebnf969 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_block_in_ebnf976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffix993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffix1001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffix1009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffix1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_ebnfSuffix0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom1057 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom1059 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom1066 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_range_in_atom1068 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_atom1075 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom1077 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom1084 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_notSet_in_atom1086 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_notSet_in_atom1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom1101 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_atom1103 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_atom1113 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_atom1115 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_range_in_atom1121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom1127 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom1129 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_terminal_in_atom1131 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_DOT_in_atom1138 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_atom1140 = new BitSet(new long[]{0xA1A1000000000000L,0x0000000200000011L});
    public static final BitSet FOLLOW_ruleref_in_atom1142 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_atom1152 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_atom1154 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_WILDCARD_in_atom1163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom1173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom1183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet1201 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_terminal_in_notSet1203 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_NOT_in_notSet1213 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_block_in_notSet1215 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BLOCK_in_block1234 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_optionsSpec_in_block1236 = new BitSet(new long[]{0x1000000100290000L,0x0000004000200000L});
    public static final BitSet FOLLOW_ruleAction_in_block1239 = new BitSet(new long[]{0x1000000100290000L,0x0000004000200000L});
    public static final BitSet FOLLOW_ACTION_in_block1242 = new BitSet(new long[]{0x1000000100290000L,0x0000004000200000L});
    public static final BitSet FOLLOW_altList_in_block1245 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ROOT_in_ruleref1264 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1267 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1269 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_BANG_in_ruleref1281 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1284 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1286 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref1298 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref1300 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RANGE_in_range1320 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range1322 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range1324 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1344 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1364 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal1366 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1368 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1378 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal1380 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1390 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_terminal1392 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal1401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOptions1419 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions1421 = new BitSet(new long[]{0x0000400000000008L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption1440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption1451 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption1453 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption1455 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption1467 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption1469 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption1471 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_predicatedRewrite_in_rewrite1486 = new BitSet(new long[]{0x0000000000000000L,0x0000003000000000L});
    public static final BitSet FOLLOW_nakedRewrite_in_rewrite1489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ST_RESULT_in_predicatedRewrite1501 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite1503 = new BitSet(new long[]{0x0200000800010000L,0x0000000000300000L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite1505 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RESULT_in_predicatedRewrite1512 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite1514 = new BitSet(new long[]{0x0200000800010000L,0x0000000000300000L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite1516 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ST_RESULT_in_nakedRewrite1530 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite1532 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_RESULT_in_nakedRewrite1539 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite1541 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTemplate_in_rewriteAlt1557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteAlt1565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ETC_in_rewriteAlt1573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EPSILON_in_rewriteAlt1581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALT_in_rewriteTreeAlt1600 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTreeAlt1602 = new BitSet(new long[]{0x8800000000010008L,0x0000000020038011L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement1618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTree_in_rewriteTreeElement1623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement1630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1647 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom1649 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom1651 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1663 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom1665 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1677 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom1679 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom1687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_rewriteTreeAtom1697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1705 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom1707 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LABEL_in_rewriteTreeAtom1723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTreeAtom1728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeEbnf1740 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_REWRITE_BLOCK_in_rewriteTreeEbnf1743 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf1745 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewriteTree1758 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTree1760 = new BitSet(new long[]{0x8800000000010008L,0x0000000020038011L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTree1762 = new BitSet(new long[]{0x8800000000010008L,0x0000000020038011L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate1777 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1779 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate1782 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate1789 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate1791 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate1794 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTemplateRef_in_rewriteTemplate1800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate1805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplate1810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplateRef1822 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewriteTemplateRef1824 = new BitSet(new long[]{0x0000000000000008L,0x0000000002000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef1826 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteIndirectTemplateHead1840 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ACTION_in_rewriteIndirectTemplateHead1842 = new BitSet(new long[]{0x0000000000000008L,0x0000000002000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead1844 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ARGLIST_in_rewriteTemplateArgs1858 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs1860 = new BitSet(new long[]{0x0000000000000008L,0x0000000001000000L});
    public static final BitSet FOLLOW_ARG_in_rewriteTemplateArg1876 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rewriteTemplateArg1878 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplateArg1880 = new BitSet(new long[]{0x0000000000000008L});

}