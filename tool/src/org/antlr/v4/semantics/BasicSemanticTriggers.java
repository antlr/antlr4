// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 BasicSemanticTriggers.g 2010-02-02 14:40:46

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "WILDCARD", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "RESULT", "ID", "ARG", "ARGLIST", "RET", "LEXER_GRAMMAR", "PARSER_GRAMMAR", "TREE_GRAMMAR", "COMBINED_GRAMMAR", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "DOT", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "ALT_REWRITE"
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
    public static final int RULEMODIFIERS=74;
    public static final int ST_RESULT=103;
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
    public static final int INITACTION=95;
    public static final int ALT_REWRITE=104;
    public static final int IMPLIES=42;
    public static final int RULE=72;
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
    public static final int WS=70;
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
    public static final int ESC_SEQ=66;
    public static final int ALT=84;
    public static final int TREE=26;
    public static final int SCOPE=21;
    public static final int ETC=56;
    public static final int COMMA=38;
    public static final int WILDCARD=54;
    public static final int DOC_COMMENT=6;
    public static final int PLUS=49;
    public static final int REWRITE_BLOCK=77;
    public static final int DOT=100;
    public static final int RETURNS=31;
    public static final int RULES=73;
    public static final int RARROW=57;
    public static final int UNICODE_ESC=69;
    public static final int HEX_DIGIT=68;
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
    public static final int TEMPLATE=35;
    public static final int LABEL=96;
    public static final int SYN_SEMPRED=98;
    public static final int ERRCHAR=71;
    public static final int BLOCK=76;
    public static final int ASSIGN=45;
    public static final int PLUS_ASSIGN=50;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=80;
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
    GrammarASTWithOptions root;
    protected int gtype;
    //Grammar g; // which grammar are we checking
    public BasicSemanticTriggers(TreeNodeStream input, String fileName) {
    	this(input);
    	this.fileName = fileName;
    }



    // $ANTLR start "topdown"
    // BasicSemanticTriggers.g:86:1: topdown : ( grammarSpec | rules | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption | multiElementAltInTreeGrammar );
    public final void topdown() throws RecognitionException {
        try {
            // BasicSemanticTriggers.g:87:2: ( grammarSpec | rules | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption | multiElementAltInTreeGrammar )
            int alt1=9;
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
                    // BasicSemanticTriggers.g:88:4: rules
                    {
                    pushFollow(FOLLOW_rules_in_topdown98);
                    rules();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // BasicSemanticTriggers.g:89:4: option
                    {
                    pushFollow(FOLLOW_option_in_topdown103);
                    option();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // BasicSemanticTriggers.g:90:4: rule
                    {
                    pushFollow(FOLLOW_rule_in_topdown108);
                    rule();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // BasicSemanticTriggers.g:91:4: ruleref
                    {
                    pushFollow(FOLLOW_ruleref_in_topdown113);
                    ruleref();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // BasicSemanticTriggers.g:92:4: tokenAlias
                    {
                    pushFollow(FOLLOW_tokenAlias_in_topdown118);
                    tokenAlias();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 7 :
                    // BasicSemanticTriggers.g:93:4: tokenRefWithArgs
                    {
                    pushFollow(FOLLOW_tokenRefWithArgs_in_topdown123);
                    tokenRefWithArgs();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 8 :
                    // BasicSemanticTriggers.g:94:4: elementOption
                    {
                    pushFollow(FOLLOW_elementOption_in_topdown128);
                    elementOption();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 9 :
                    // BasicSemanticTriggers.g:95:4: multiElementAltInTreeGrammar
                    {
                    pushFollow(FOLLOW_multiElementAltInTreeGrammar_in_topdown133);
                    multiElementAltInTreeGrammar();

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
    // BasicSemanticTriggers.g:98:1: grammarSpec : ^( grammarType ID ( DOC_COMMENT )? prequelConstructs ( ^( RULE ( . )* ) )* ) ;
    public final void grammarSpec() throws RecognitionException {
        GrammarAST ID1=null;

        try {
            // BasicSemanticTriggers.g:99:5: ( ^( grammarType ID ( DOC_COMMENT )? prequelConstructs ( ^( RULE ( . )* ) )* ) )
            // BasicSemanticTriggers.g:99:9: ^( grammarType ID ( DOC_COMMENT )? prequelConstructs ( ^( RULE ( . )* ) )* )
            {
            pushFollow(FOLLOW_grammarType_in_grammarSpec150);
            grammarType();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID1=(GrammarAST)match(input,ID,FOLLOW_ID_in_grammarSpec152); if (state.failed) return ;
            // BasicSemanticTriggers.g:99:26: ( DOC_COMMENT )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==DOC_COMMENT) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // BasicSemanticTriggers.g:99:26: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec154); if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_prequelConstructs_in_grammarSpec157);
            prequelConstructs();

            state._fsp--;
            if (state.failed) return ;
            // BasicSemanticTriggers.g:99:57: ( ^( RULE ( . )* ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==RULE) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // BasicSemanticTriggers.g:99:58: ^( RULE ( . )* )
            	    {
            	    match(input,RULE,FOLLOW_RULE_in_grammarSpec161); if (state.failed) return ;

            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:99:65: ( . )*
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
            	        	    // BasicSemanticTriggers.g:99:65: .
            	        	    {
            	        	    matchAny(input); if (state.failed) return ;

            	        	    }
            	        	    break;

            	        	default :
            	        	    break loop3;
            	            }
            	        } while (true);


            	        match(input, Token.UP, null); if (state.failed) return ;
            	    }

            	    }
            	    break;

            	default :
            	    break loop4;
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
    // BasicSemanticTriggers.g:106:1: grammarType : ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR );
    public final BasicSemanticTriggers.grammarType_return grammarType() throws RecognitionException {
        BasicSemanticTriggers.grammarType_return retval = new BasicSemanticTriggers.grammarType_return();
        retval.start = input.LT(1);

        gtype = ((GrammarAST)retval.start).getType(); root = (GrammarASTWithOptions)((GrammarAST)retval.start);
        try {
            // BasicSemanticTriggers.g:108:5: ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR )
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


    // $ANTLR start "prequelConstructs"
    // BasicSemanticTriggers.g:111:1: prequelConstructs : ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )* ;
    public final void prequelConstructs() throws RecognitionException {
        GrammarAST o=null;
        GrammarAST i=null;
        GrammarAST t=null;
        List list_o=null;
        List list_i=null;
        List list_t=null;

        try {
            // BasicSemanticTriggers.g:112:2: ( ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )* )
            // BasicSemanticTriggers.g:112:4: ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )*
            {
            // BasicSemanticTriggers.g:112:4: ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )*
            loop8:
            do {
                int alt8=4;
                switch ( input.LA(1) ) {
                case OPTIONS:
                    {
                    alt8=1;
                    }
                    break;
                case IMPORT:
                    {
                    alt8=2;
                    }
                    break;
                case TOKENS:
                    {
                    alt8=3;
                    }
                    break;

                }

                switch (alt8) {
            	case 1 :
            	    // BasicSemanticTriggers.g:112:6: ^(o+= OPTIONS ( . )* )
            	    {
            	    o=(GrammarAST)match(input,OPTIONS,FOLLOW_OPTIONS_in_prequelConstructs229); if (state.failed) return ;
            	    if (list_o==null) list_o=new ArrayList();
            	    list_o.add(o);


            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:112:19: ( . )*
            	        loop5:
            	        do {
            	            int alt5=2;
            	            int LA5_0 = input.LA(1);

            	            if ( ((LA5_0>=SEMPRED && LA5_0<=ALT_REWRITE)) ) {
            	                alt5=1;
            	            }
            	            else if ( (LA5_0==UP) ) {
            	                alt5=2;
            	            }


            	            switch (alt5) {
            	        	case 1 :
            	        	    // BasicSemanticTriggers.g:112:19: .
            	        	    {
            	        	    matchAny(input); if (state.failed) return ;

            	        	    }
            	        	    break;

            	        	default :
            	        	    break loop5;
            	            }
            	        } while (true);


            	        match(input, Token.UP, null); if (state.failed) return ;
            	    }

            	    }
            	    break;
            	case 2 :
            	    // BasicSemanticTriggers.g:113:5: ^(i+= IMPORT ( . )* )
            	    {
            	    i=(GrammarAST)match(input,IMPORT,FOLLOW_IMPORT_in_prequelConstructs242); if (state.failed) return ;
            	    if (list_i==null) list_i=new ArrayList();
            	    list_i.add(i);


            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:113:17: ( . )*
            	        loop6:
            	        do {
            	            int alt6=2;
            	            int LA6_0 = input.LA(1);

            	            if ( ((LA6_0>=SEMPRED && LA6_0<=ALT_REWRITE)) ) {
            	                alt6=1;
            	            }
            	            else if ( (LA6_0==UP) ) {
            	                alt6=2;
            	            }


            	            switch (alt6) {
            	        	case 1 :
            	        	    // BasicSemanticTriggers.g:113:17: .
            	        	    {
            	        	    matchAny(input); if (state.failed) return ;

            	        	    }
            	        	    break;

            	        	default :
            	        	    break loop6;
            	            }
            	        } while (true);


            	        match(input, Token.UP, null); if (state.failed) return ;
            	    }

            	    }
            	    break;
            	case 3 :
            	    // BasicSemanticTriggers.g:114:5: ^(t+= TOKENS ( . )* )
            	    {
            	    t=(GrammarAST)match(input,TOKENS,FOLLOW_TOKENS_in_prequelConstructs255); if (state.failed) return ;
            	    if (list_t==null) list_t=new ArrayList();
            	    list_t.add(t);


            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:114:17: ( . )*
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
            	        	    // BasicSemanticTriggers.g:114:17: .
            	        	    {
            	        	    matchAny(input); if (state.failed) return ;

            	        	    }
            	        	    break;

            	        	default :
            	        	    break loop7;
            	            }
            	        } while (true);


            	        match(input, Token.UP, null); if (state.failed) return ;
            	    }

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkNumPrequels(gtype, list_o, list_i, list_t);
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
    // $ANTLR end "prequelConstructs"


    // $ANTLR start "rules"
    // BasicSemanticTriggers.g:119:1: rules : RULES ;
    public final void rules() throws RecognitionException {
        GrammarAST RULES2=null;

        try {
            // BasicSemanticTriggers.g:119:7: ( RULES )
            // BasicSemanticTriggers.g:119:9: RULES
            {
            RULES2=(GrammarAST)match(input,RULES,FOLLOW_RULES_in_rules278); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkNumRules(gtype, fileName, RULES2);
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

    public static class option_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "option"
    // BasicSemanticTriggers.g:121:1: option : {...}? ^( ASSIGN o= ID optionValue ) ;
    public final BasicSemanticTriggers.option_return option() throws RecognitionException {
        BasicSemanticTriggers.option_return retval = new BasicSemanticTriggers.option_return();
        retval.start = input.LT(1);

        GrammarAST o=null;
        BasicSemanticTriggers.optionValue_return optionValue3 = null;


        try {
            // BasicSemanticTriggers.g:122:5: ({...}? ^( ASSIGN o= ID optionValue ) )
            // BasicSemanticTriggers.g:122:9: {...}? ^( ASSIGN o= ID optionValue )
            {
            if ( !((inContext("OPTIONS"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "option", "inContext(\"OPTIONS\")");
            }
            match(input,ASSIGN,FOLLOW_ASSIGN_in_option299); if (state.failed) return retval;

            match(input, Token.DOWN, null); if (state.failed) return retval;
            o=(GrammarAST)match(input,ID,FOLLOW_ID_in_option303); if (state.failed) return retval;
            pushFollow(FOLLOW_optionValue_in_option305);
            optionValue3=optionValue();

            state._fsp--;
            if (state.failed) return retval;

            match(input, Token.UP, null); if (state.failed) return retval;
            if ( state.backtracking==1 ) {

                 	    GrammarAST parent = (GrammarAST)((GrammarAST)retval.start).getParent();   // OPTION
                 		GrammarAST parentWithOptionKind = (GrammarAST)parent.getParent();
                  	boolean ok = BasicSemanticChecks.checkOptions(gtype, parentWithOptionKind,
                  												  o.token, (optionValue3!=null?optionValue3.v:null));
              		//  store options into XXX_GRAMMAR, RULE, BLOCK nodes
                  	if ( ok ) {
                  		((GrammarASTWithOptions)parentWithOptionKind).setOption((o!=null?o.getText():null), (optionValue3!=null?optionValue3.v:null)); 
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
    // $ANTLR end "option"

    public static class optionValue_return extends TreeRuleReturnScope {
        public String v;
    };

    // $ANTLR start "optionValue"
    // BasicSemanticTriggers.g:135:1: optionValue returns [String v] : ( ID | STRING_LITERAL | INT | STAR );
    public final BasicSemanticTriggers.optionValue_return optionValue() throws RecognitionException {
        BasicSemanticTriggers.optionValue_return retval = new BasicSemanticTriggers.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // BasicSemanticTriggers.g:137:5: ( ID | STRING_LITERAL | INT | STAR )
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
    // BasicSemanticTriggers.g:143:1: rule : ^( RULE r= ID ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST r=null;

        try {
            // BasicSemanticTriggers.g:143:5: ( ^( RULE r= ID ( . )* ) )
            // BasicSemanticTriggers.g:143:9: ^( RULE r= ID ( . )* )
            {
            match(input,RULE,FOLLOW_RULE_in_rule387); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            r=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule391); if (state.failed) return ;
            // BasicSemanticTriggers.g:143:22: ( . )*
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
            	    // BasicSemanticTriggers.g:143:22: .
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
    // BasicSemanticTriggers.g:146:1: ruleref : RULE_REF ;
    public final void ruleref() throws RecognitionException {
        GrammarAST RULE_REF4=null;

        try {
            // BasicSemanticTriggers.g:147:5: ( RULE_REF )
            // BasicSemanticTriggers.g:147:7: RULE_REF
            {
            RULE_REF4=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref414); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkInvalidRuleRef(gtype, RULE_REF4.token);
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
    // BasicSemanticTriggers.g:150:1: tokenAlias : {...}? ^( ASSIGN ID STRING_LITERAL ) ;
    public final void tokenAlias() throws RecognitionException {
        GrammarAST ID5=null;

        try {
            // BasicSemanticTriggers.g:151:2: ({...}? ^( ASSIGN ID STRING_LITERAL ) )
            // BasicSemanticTriggers.g:151:4: {...}? ^( ASSIGN ID STRING_LITERAL )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokenAlias", "inContext(\"TOKENS\")");
            }
            match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenAlias433); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID5=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokenAlias435); if (state.failed) return ;
            match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenAlias437); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkTokenAlias(gtype, ID5.token);
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
    // BasicSemanticTriggers.g:155:1: tokenRefWithArgs : ^( TOKEN_REF ARG_ACTION ) ;
    public final void tokenRefWithArgs() throws RecognitionException {
        GrammarAST TOKEN_REF6=null;

        try {
            // BasicSemanticTriggers.g:156:2: ( ^( TOKEN_REF ARG_ACTION ) )
            // BasicSemanticTriggers.g:156:4: ^( TOKEN_REF ARG_ACTION )
            {
            TOKEN_REF6=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenRefWithArgs454); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_tokenRefWithArgs456); if (state.failed) return ;

            match(input, Token.UP, null); if (state.failed) return ;
            if ( state.backtracking==1 ) {
              BasicSemanticChecks.checkTokenArgs(gtype, TOKEN_REF6.token);
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
    // BasicSemanticTriggers.g:160:1: elementOption : ^( ELEMENT_OPTIONS ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) ) ;
    public final BasicSemanticTriggers.elementOption_return elementOption() throws RecognitionException {
        BasicSemanticTriggers.elementOption_return retval = new BasicSemanticTriggers.elementOption_return();
        retval.start = input.LT(1);

        GrammarAST o=null;
        GrammarAST value=null;

        try {
            // BasicSemanticTriggers.g:161:5: ( ^( ELEMENT_OPTIONS ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) ) )
            // BasicSemanticTriggers.g:161:7: ^( ELEMENT_OPTIONS ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) )
            {
            match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOption478); if (state.failed) return retval;

            match(input, Token.DOWN, null); if (state.failed) return retval;
            // BasicSemanticTriggers.g:162:7: ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ASSIGN) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==DOWN) ) {
                    int LA10_2 = input.LA(3);

                    if ( (LA10_2==ID) ) {
                        int LA10_3 = input.LA(4);

                        if ( (LA10_3==ID) ) {
                            alt10=1;
                        }
                        else if ( (LA10_3==STRING_LITERAL) ) {
                            alt10=2;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 10, 3, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 2, input);

                        throw nvae;
                    }
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // BasicSemanticTriggers.g:162:9: ^( ASSIGN o= ID value= ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption489); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    o=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption493); if (state.failed) return retval;
                    value=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption497); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // BasicSemanticTriggers.g:163:11: ^( ASSIGN o= ID value= STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption511); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    o=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption515); if (state.failed) return retval;
                    value=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption519); if (state.failed) return retval;

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
                  		terminal.setOption((o!=null?o.getText():null), (value!=null?value.getText():null));
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

    public static class multiElementAltInTreeGrammar_return extends TreeRuleReturnScope {
    };

    // $ANTLR start "multiElementAltInTreeGrammar"
    // BasicSemanticTriggers.g:178:1: multiElementAltInTreeGrammar : {...}? ^( ALT ~ ( SEMPRED | ACTION ) (~ ( SEMPRED | ACTION ) )+ ) ;
    public final BasicSemanticTriggers.multiElementAltInTreeGrammar_return multiElementAltInTreeGrammar() throws RecognitionException {
        BasicSemanticTriggers.multiElementAltInTreeGrammar_return retval = new BasicSemanticTriggers.multiElementAltInTreeGrammar_return();
        retval.start = input.LT(1);

        try {
            // BasicSemanticTriggers.g:179:2: ({...}? ^( ALT ~ ( SEMPRED | ACTION ) (~ ( SEMPRED | ACTION ) )+ ) )
            // BasicSemanticTriggers.g:179:4: {...}? ^( ALT ~ ( SEMPRED | ACTION ) (~ ( SEMPRED | ACTION ) )+ )
            {
            if ( !((inContext("ALT_REWRITE") &&
            		 root.getOption("output")!=null && root.getOption("output").equals("template"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "multiElementAltInTreeGrammar", "inContext(\"ALT_REWRITE\") &&\n\t\t root.getOption(\"output\")!=null && root.getOption(\"output\").equals(\"template\")");
            }
            match(input,ALT,FOLLOW_ALT_in_multiElementAltInTreeGrammar560); if (state.failed) return retval;

            match(input, Token.DOWN, null); if (state.failed) return retval;
            if ( (input.LA(1)>=FORCED_ACTION && input.LA(1)<=NESTED_ACTION)||(input.LA(1)>=ACTION_ESC && input.LA(1)<=ALT_REWRITE) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // BasicSemanticTriggers.g:181:28: (~ ( SEMPRED | ACTION ) )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( ((LA11_0>=FORCED_ACTION && LA11_0<=NESTED_ACTION)||(LA11_0>=ACTION_ESC && LA11_0<=ALT_REWRITE)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // BasicSemanticTriggers.g:181:28: ~ ( SEMPRED | ACTION )
            	    {
            	    if ( (input.LA(1)>=FORCED_ACTION && input.LA(1)<=NESTED_ACTION)||(input.LA(1)>=ACTION_ESC && input.LA(1)<=ALT_REWRITE) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            match(input, Token.UP, null); if (state.failed) return retval;
            if ( state.backtracking==1 ) {

              		int altNum = ((GrammarAST)retval.start).getParent().getChildIndex() + 1; // alts are 1..n
              		GrammarAST firstNode = (GrammarAST)((GrammarAST)retval.start).getChild(0);
              		BasicSemanticChecks.checkRewriteForMultiRootAltInTreeGrammar(gtype,root.getOptions(),
              																	 firstNode.token,
              																	 altNum);
              		
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
    // $ANTLR end "multiElementAltInTreeGrammar"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\17\uffff";
    static final String DFA1_eofS =
        "\17\uffff";
    static final String DFA1_minS =
        "\1\55\2\uffff\1\2\5\uffff\1\127\1\60\1\3\1\uffff\1\0\1\uffff";
    static final String DFA1_maxS =
        "\1\146\2\uffff\1\2\5\uffff\2\127\1\3\1\uffff\1\0\1\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\1\5\1\7\1\10\1\11\3\uffff\1\3\1\uffff"+
        "\1\6";
    static final String DFA1_specialS =
        "\15\uffff\1\0\1\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\3\20\uffff\1\6\1\5\10\uffff\1\4\1\2\12\uffff\1\10\6\uffff"+
            "\4\1\7\uffff\1\7",
            "",
            "",
            "\1\11",
            "",
            "",
            "",
            "",
            "",
            "\1\12",
            "\1\14\17\uffff\1\14\2\uffff\1\13\23\uffff\1\14",
            "\1\15",
            "",
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
            return "86:1: topdown : ( grammarSpec | rules | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption | multiElementAltInTreeGrammar );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TreeNodeStream input = (TreeNodeStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_13 = input.LA(1);

                         
                        int index1_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("OPTIONS"))) ) {s = 12;}

                        else if ( ((inContext("TOKENS"))) ) {s = 14;}

                         
                        input.seek(index1_13);
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
 

    public static final BitSet FOLLOW_grammarSpec_in_topdown93 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rules_in_topdown98 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_option_in_topdown103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_topdown108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_topdown113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokenAlias_in_topdown118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokenRefWithArgs_in_topdown123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementOption_in_topdown128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multiElementAltInTreeGrammar_in_topdown133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec150 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec152 = new BitSet(new long[]{0x0000000000580048L,0x0000000000000100L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec154 = new BitSet(new long[]{0x0000000000580008L,0x0000000000000100L});
    public static final BitSet FOLLOW_prequelConstructs_in_grammarSpec157 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000100L});
    public static final BitSet FOLLOW_RULE_in_grammarSpec161 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_grammarType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_prequelConstructs229 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IMPORT_in_prequelConstructs242 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKENS_in_prequelConstructs255 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULES_in_rules278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_option299 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option303 = new BitSet(new long[]{0x0001000000000000L,0x0000000000800009L});
    public static final BitSet FOLLOW_optionValue_in_option305 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule387 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule391 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000001FFFFFFFFFFL});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenAlias433 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokenAlias435 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenAlias437 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenRefWithArgs454 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_tokenRefWithArgs456 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOption478 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption489 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption493 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption497 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption511 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption515 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption519 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ALT_in_multiElementAltInTreeGrammar560 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_multiElementAltInTreeGrammar562 = new BitSet(new long[]{0xFFFFFFFFFFFEFFE0L,0x000001FFFFFFFFFFL});
    public static final BitSet FOLLOW_set_in_multiElementAltInTreeGrammar569 = new BitSet(new long[]{0xFFFFFFFFFFFEFFE8L,0x000001FFFFFFFFFFL});

}