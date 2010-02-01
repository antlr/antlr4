// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 BasicSemanticTriggers.g 2010-01-31 17:57:29

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
/** Check the basic semantics of the input.  We check for: */
public class BasicSemanticTriggers extends org.antlr.v4.runtime.tree.TreeFilter {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "WILDCARD", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "RESULT", "ID", "ARG", "ARGLIST", "RET", "LEXER_GRAMMAR", "PARSER_GRAMMAR", "TREE_GRAMMAR", "COMBINED_GRAMMAR", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "DOT", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "ALT_REWRITE", "'='"
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
    public static final int T__104=104;
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
    // BasicSemanticTriggers.g:83:1: topdown : ( grammarSpec | option | rule | ruleref | tokenAlias | tokenRef );
    public final void topdown() throws RecognitionException {
        try {
            // BasicSemanticTriggers.g:84:2: ( grammarSpec | option | rule | ruleref | tokenAlias | tokenRef )
            int alt1=6;
            switch ( input.LA(1) ) {
            case LEXER_GRAMMAR:
            case PARSER_GRAMMAR:
            case TREE_GRAMMAR:
            case COMBINED_GRAMMAR:
                {
                alt1=1;
                }
                break;
            case 104:
                {
                alt1=2;
                }
                break;
            case RULE:
                {
                alt1=3;
                }
                break;
            case RULE_REF:
                {
                alt1=4;
                }
                break;
            case ASSIGN:
                {
                alt1=5;
                }
                break;
            case TOKEN_REF:
                {
                alt1=6;
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
                    // BasicSemanticTriggers.g:84:4: grammarSpec
                    {
                    pushFollow(FOLLOW_grammarSpec_in_topdown96);
                    grammarSpec();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // BasicSemanticTriggers.g:85:4: option
                    {
                    pushFollow(FOLLOW_option_in_topdown101);
                    option();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // BasicSemanticTriggers.g:86:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown106);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // BasicSemanticTriggers.g:87:4: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown111);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // BasicSemanticTriggers.g:88:4: tokenAlias
                    {
                    pushFollow(FOLLOW_tokenAlias_in_topdown116);
                    tokenAlias();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // BasicSemanticTriggers.g:89:4: tokenRef
                    {
                    pushFollow(FOLLOW_tokenRef_in_topdown121);
                    tokenRef();

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
    // BasicSemanticTriggers.g:92:1: grammarSpec : ^( grammarType ID ( . )* ) ;
    public final void grammarSpec() throws RecognitionException {
        GrammarAST ID1=null;

        try {
            // BasicSemanticTriggers.g:93:5: ( ^( grammarType ID ( . )* ) )
            // BasicSemanticTriggers.g:93:9: ^( grammarType ID ( . )* )
            {
            pushFollow(FOLLOW_grammarType_in_grammarSpec138);
            grammarType();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID1=(GrammarAST)match(input,ID,FOLLOW_ID_in_grammarSpec140); if (state.failed) return ;
            // BasicSemanticTriggers.g:93:26: ( . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=SEMPRED && LA2_0<=104)) ) {
                    alt2=1;
                }
                else if ( (LA2_0==UP) ) {
                    alt2=2;
                }


                switch (alt2) {
            	case 1 :
            	    // BasicSemanticTriggers.g:93:26: .
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
    // BasicSemanticTriggers.g:100:1: grammarType : ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR );
    public final BasicSemanticTriggers.grammarType_return grammarType() throws RecognitionException {
        BasicSemanticTriggers.grammarType_return retval = new BasicSemanticTriggers.grammarType_return();
        retval.start = input.LT(1);

        gtype = ((GrammarAST)retval.start).getType();
        try {
            // BasicSemanticTriggers.g:102:5: ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR )
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


    // $ANTLR start "option"
    // BasicSemanticTriggers.g:105:1: option : {...}? ^( '=' o= ID optionValue ) ;
    public final void option() throws RecognitionException {
        GrammarAST o=null;
        BasicSemanticTriggers.optionValue_return optionValue2 = null;


        try {
            // BasicSemanticTriggers.g:106:5: ({...}? ^( '=' o= ID optionValue ) )
            // BasicSemanticTriggers.g:106:9: {...}? ^( '=' o= ID optionValue )
            {
            if ( !((inContext("OPTIONS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "option", "inContext(\"OPTIONS\")");
            }
            match(input,104,FOLLOW_104_in_option208); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            o=(GrammarAST)match(input,ID,FOLLOW_ID_in_option212); if (state.failed) return ;
            pushFollow(FOLLOW_optionValue_in_option214);
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
    // BasicSemanticTriggers.g:110:1: optionValue returns [String v] : ( ID | STRING_LITERAL | INT | STAR );
    public final BasicSemanticTriggers.optionValue_return optionValue() throws RecognitionException {
        BasicSemanticTriggers.optionValue_return retval = new BasicSemanticTriggers.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // BasicSemanticTriggers.g:112:5: ( ID | STRING_LITERAL | INT | STAR )
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
    // BasicSemanticTriggers.g:118:1: rule : ^( RULE r= ID ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST r=null;

        try {
            // BasicSemanticTriggers.g:118:5: ( ^( RULE r= ID ( . )* ) )
            // BasicSemanticTriggers.g:118:9: ^( RULE r= ID ( . )* )
            {
            match(input,RULE,FOLLOW_RULE_in_rule296); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            r=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule300); if (state.failed) return ;
            // BasicSemanticTriggers.g:118:22: ( . )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0>=SEMPRED && LA3_0<=104)) ) {
                    alt3=1;
                }
                else if ( (LA3_0==UP) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // BasicSemanticTriggers.g:118:22: .
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
    // BasicSemanticTriggers.g:121:1: ruleref : RULE_REF ;
    public final void ruleref() throws RecognitionException {
        GrammarAST RULE_REF3=null;

        try {
            // BasicSemanticTriggers.g:122:5: ( RULE_REF )
            // BasicSemanticTriggers.g:122:7: RULE_REF
            {
            RULE_REF3=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref323); if (state.failed) return ;
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
    // BasicSemanticTriggers.g:125:1: tokenAlias : {...}? ^( ASSIGN TOKEN_REF STRING_LITERAL ) ;
    public final void tokenAlias() throws RecognitionException {
        GrammarAST TOKEN_REF4=null;

        try {
            // BasicSemanticTriggers.g:126:2: ({...}? ^( ASSIGN TOKEN_REF STRING_LITERAL ) )
            // BasicSemanticTriggers.g:126:4: {...}? ^( ASSIGN TOKEN_REF STRING_LITERAL )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokenAlias", "inContext(\"TOKENS\")");
            }
            match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenAlias342); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            TOKEN_REF4=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenAlias344); if (state.failed) return ;
            match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenAlias346); if (state.failed) return ;

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


    // $ANTLR start "tokenRef"
    // BasicSemanticTriggers.g:130:1: tokenRef : ^( TOKEN_REF ARG_ACTION ( . )* ) ;
    public final void tokenRef() throws RecognitionException {
        GrammarAST TOKEN_REF5=null;

        try {
            // BasicSemanticTriggers.g:131:2: ( ^( TOKEN_REF ARG_ACTION ( . )* ) )
            // BasicSemanticTriggers.g:131:4: ^( TOKEN_REF ARG_ACTION ( . )* )
            {
            TOKEN_REF5=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenRef363); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_tokenRef365); if (state.failed) return ;
            // BasicSemanticTriggers.g:131:27: ( . )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=SEMPRED && LA4_0<=104)) ) {
                    alt4=1;
                }
                else if ( (LA4_0==UP) ) {
                    alt4=2;
                }


                switch (alt4) {
            	case 1 :
            	    // BasicSemanticTriggers.g:131:27: .
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
    // $ANTLR end "tokenRef"

    // Delegated rules


 

    public static final BitSet FOLLOW_grammarSpec_in_topdown96 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_option_in_topdown101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_topdown106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_topdown111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokenAlias_in_topdown116 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokenRef_in_topdown121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec138 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec140 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000001FFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_grammarType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_104_in_option208 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option212 = new BitSet(new long[]{0x0001000000000000L,0x0000000000400009L});
    public static final BitSet FOLLOW_optionValue_in_option214 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule296 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule300 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000001FFFFFFFFFFL});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenAlias342 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenAlias344 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenAlias346 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenRef363 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_tokenRef365 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000001FFFFFFFFFFL});

}