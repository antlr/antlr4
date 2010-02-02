// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 BasicSemanticTriggers.g 2010-02-01 18:48:03

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
    //public Map<String,String> options = new HashMap<String,String>();
    protected int gtype;
    //Grammar g; // which grammar are we checking
    public BasicSemanticTriggers(TreeNodeStream input, String fileName) {
    	this(input);
    	this.fileName = fileName;
    }



    // $ANTLR start "topdown"
    // BasicSemanticTriggers.g:86:1: topdown : ( grammarSpec | rules | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption );
    public final void topdown() throws RecognitionException {
        try {
            // BasicSemanticTriggers.g:87:2: ( grammarSpec | rules | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption )
            int alt1=8;
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
    // BasicSemanticTriggers.g:97:1: grammarSpec : ^( grammarType ID ( DOC_COMMENT )? prequelConstructs ( ^( RULE ( . )* ) )* ) ;
    public final void grammarSpec() throws RecognitionException {
        GrammarAST ID1=null;

        try {
            // BasicSemanticTriggers.g:98:5: ( ^( grammarType ID ( DOC_COMMENT )? prequelConstructs ( ^( RULE ( . )* ) )* ) )
            // BasicSemanticTriggers.g:98:9: ^( grammarType ID ( DOC_COMMENT )? prequelConstructs ( ^( RULE ( . )* ) )* )
            {
            pushFollow(FOLLOW_grammarType_in_grammarSpec145);
            grammarType();

            state._fsp--;
            if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID1=(GrammarAST)match(input,ID,FOLLOW_ID_in_grammarSpec147); if (state.failed) return ;
            // BasicSemanticTriggers.g:98:26: ( DOC_COMMENT )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==DOC_COMMENT) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // BasicSemanticTriggers.g:98:26: DOC_COMMENT
                    {
                    match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec149); if (state.failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_prequelConstructs_in_grammarSpec152);
            prequelConstructs();

            state._fsp--;
            if (state.failed) return ;
            // BasicSemanticTriggers.g:98:57: ( ^( RULE ( . )* ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==RULE) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // BasicSemanticTriggers.g:98:58: ^( RULE ( . )* )
            	    {
            	    match(input,RULE,FOLLOW_RULE_in_grammarSpec156); if (state.failed) return ;

            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:98:65: ( . )*
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
            	        	    // BasicSemanticTriggers.g:98:65: .
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
    // BasicSemanticTriggers.g:105:1: grammarType : ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR );
    public final BasicSemanticTriggers.grammarType_return grammarType() throws RecognitionException {
        BasicSemanticTriggers.grammarType_return retval = new BasicSemanticTriggers.grammarType_return();
        retval.start = input.LT(1);

        gtype = ((GrammarAST)retval.start).getType();
        try {
            // BasicSemanticTriggers.g:107:5: ( LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR )
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
    // BasicSemanticTriggers.g:110:1: prequelConstructs : ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )* ;
    public final void prequelConstructs() throws RecognitionException {
        GrammarAST o=null;
        GrammarAST i=null;
        GrammarAST t=null;
        List list_o=null;
        List list_i=null;
        List list_t=null;

        try {
            // BasicSemanticTriggers.g:111:2: ( ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )* )
            // BasicSemanticTriggers.g:111:4: ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )*
            {
            // BasicSemanticTriggers.g:111:4: ( ^(o+= OPTIONS ( . )* ) | ^(i+= IMPORT ( . )* ) | ^(t+= TOKENS ( . )* ) )*
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
            	    // BasicSemanticTriggers.g:111:6: ^(o+= OPTIONS ( . )* )
            	    {
            	    o=(GrammarAST)match(input,OPTIONS,FOLLOW_OPTIONS_in_prequelConstructs224); if (state.failed) return ;
            	    if (list_o==null) list_o=new ArrayList();
            	    list_o.add(o);


            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:111:19: ( . )*
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
            	        	    // BasicSemanticTriggers.g:111:19: .
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
            	    // BasicSemanticTriggers.g:112:5: ^(i+= IMPORT ( . )* )
            	    {
            	    i=(GrammarAST)match(input,IMPORT,FOLLOW_IMPORT_in_prequelConstructs237); if (state.failed) return ;
            	    if (list_i==null) list_i=new ArrayList();
            	    list_i.add(i);


            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:112:17: ( . )*
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
            	        	    // BasicSemanticTriggers.g:112:17: .
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
            	    // BasicSemanticTriggers.g:113:5: ^(t+= TOKENS ( . )* )
            	    {
            	    t=(GrammarAST)match(input,TOKENS,FOLLOW_TOKENS_in_prequelConstructs250); if (state.failed) return ;
            	    if (list_t==null) list_t=new ArrayList();
            	    list_t.add(t);


            	    if ( input.LA(1)==Token.DOWN ) {
            	        match(input, Token.DOWN, null); if (state.failed) return ;
            	        // BasicSemanticTriggers.g:113:17: ( . )*
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
            	        	    // BasicSemanticTriggers.g:113:17: .
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
    // BasicSemanticTriggers.g:118:1: rules : RULES ;
    public final void rules() throws RecognitionException {
        GrammarAST RULES2=null;

        try {
            // BasicSemanticTriggers.g:118:7: ( RULES )
            // BasicSemanticTriggers.g:118:9: RULES
            {
            RULES2=(GrammarAST)match(input,RULES,FOLLOW_RULES_in_rules273); if (state.failed) return ;
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
    // BasicSemanticTriggers.g:120:1: option : {...}? ^( ASSIGN o= ID optionValue ) ;
    public final BasicSemanticTriggers.option_return option() throws RecognitionException {
        BasicSemanticTriggers.option_return retval = new BasicSemanticTriggers.option_return();
        retval.start = input.LT(1);

        GrammarAST o=null;
        BasicSemanticTriggers.optionValue_return optionValue3 = null;


        try {
            // BasicSemanticTriggers.g:121:5: ({...}? ^( ASSIGN o= ID optionValue ) )
            // BasicSemanticTriggers.g:121:9: {...}? ^( ASSIGN o= ID optionValue )
            {
            if ( !((inContext("OPTIONS"))) ) {
                if (state.backtracking>0) {state.failed=true; return retval;}
                throw new FailedPredicateException(input, "option", "inContext(\"OPTIONS\")");
            }
            match(input,ASSIGN,FOLLOW_ASSIGN_in_option294); if (state.failed) return retval;

            match(input, Token.DOWN, null); if (state.failed) return retval;
            o=(GrammarAST)match(input,ID,FOLLOW_ID_in_option298); if (state.failed) return retval;
            pushFollow(FOLLOW_optionValue_in_option300);
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
    // BasicSemanticTriggers.g:134:1: optionValue returns [String v] : ( ID | STRING_LITERAL | INT | STAR );
    public final BasicSemanticTriggers.optionValue_return optionValue() throws RecognitionException {
        BasicSemanticTriggers.optionValue_return retval = new BasicSemanticTriggers.optionValue_return();
        retval.start = input.LT(1);

        retval.v = ((GrammarAST)retval.start).token.getText();
        try {
            // BasicSemanticTriggers.g:136:5: ( ID | STRING_LITERAL | INT | STAR )
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
    // BasicSemanticTriggers.g:142:1: rule : ^( RULE r= ID ( . )* ) ;
    public final void rule() throws RecognitionException {
        GrammarAST r=null;

        try {
            // BasicSemanticTriggers.g:142:5: ( ^( RULE r= ID ( . )* ) )
            // BasicSemanticTriggers.g:142:9: ^( RULE r= ID ( . )* )
            {
            match(input,RULE,FOLLOW_RULE_in_rule382); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            r=(GrammarAST)match(input,ID,FOLLOW_ID_in_rule386); if (state.failed) return ;
            // BasicSemanticTriggers.g:142:22: ( . )*
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
            	    // BasicSemanticTriggers.g:142:22: .
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
    // BasicSemanticTriggers.g:145:1: ruleref : RULE_REF ;
    public final void ruleref() throws RecognitionException {
        GrammarAST RULE_REF4=null;

        try {
            // BasicSemanticTriggers.g:146:5: ( RULE_REF )
            // BasicSemanticTriggers.g:146:7: RULE_REF
            {
            RULE_REF4=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref409); if (state.failed) return ;
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
    // BasicSemanticTriggers.g:149:1: tokenAlias : {...}? ^( ASSIGN ID STRING_LITERAL ) ;
    public final void tokenAlias() throws RecognitionException {
        GrammarAST ID5=null;

        try {
            // BasicSemanticTriggers.g:150:2: ({...}? ^( ASSIGN ID STRING_LITERAL ) )
            // BasicSemanticTriggers.g:150:4: {...}? ^( ASSIGN ID STRING_LITERAL )
            {
            if ( !((inContext("TOKENS"))) ) {
                if (state.backtracking>0) {state.failed=true; return ;}
                throw new FailedPredicateException(input, "tokenAlias", "inContext(\"TOKENS\")");
            }
            match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenAlias428); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            ID5=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokenAlias430); if (state.failed) return ;
            match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenAlias432); if (state.failed) return ;

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
    // BasicSemanticTriggers.g:154:1: tokenRefWithArgs : ^( TOKEN_REF ARG_ACTION ) ;
    public final void tokenRefWithArgs() throws RecognitionException {
        GrammarAST TOKEN_REF6=null;

        try {
            // BasicSemanticTriggers.g:155:2: ( ^( TOKEN_REF ARG_ACTION ) )
            // BasicSemanticTriggers.g:155:4: ^( TOKEN_REF ARG_ACTION )
            {
            TOKEN_REF6=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenRefWithArgs449); if (state.failed) return ;

            match(input, Token.DOWN, null); if (state.failed) return ;
            match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_tokenRefWithArgs451); if (state.failed) return ;

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
    // BasicSemanticTriggers.g:159:1: elementOption : ^( ELEMENT_OPTIONS ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) ) ;
    public final BasicSemanticTriggers.elementOption_return elementOption() throws RecognitionException {
        BasicSemanticTriggers.elementOption_return retval = new BasicSemanticTriggers.elementOption_return();
        retval.start = input.LT(1);

        GrammarAST o=null;
        GrammarAST value=null;

        try {
            // BasicSemanticTriggers.g:160:5: ( ^( ELEMENT_OPTIONS ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) ) )
            // BasicSemanticTriggers.g:160:7: ^( ELEMENT_OPTIONS ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) ) )
            {
            match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOption473); if (state.failed) return retval;

            match(input, Token.DOWN, null); if (state.failed) return retval;
            // BasicSemanticTriggers.g:161:7: ( ^( ASSIGN o= ID value= ID ) | ^( ASSIGN o= ID value= STRING_LITERAL ) )
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
                    // BasicSemanticTriggers.g:161:9: ^( ASSIGN o= ID value= ID )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption484); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    o=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption488); if (state.failed) return retval;
                    value=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption492); if (state.failed) return retval;

                    match(input, Token.UP, null); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // BasicSemanticTriggers.g:162:11: ^( ASSIGN o= ID value= STRING_LITERAL )
                    {
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption506); if (state.failed) return retval;

                    match(input, Token.DOWN, null); if (state.failed) return retval;
                    o=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption510); if (state.failed) return retval;
                    value=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption514); if (state.failed) return retval;

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

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\16\uffff";
    static final String DFA1_eofS =
        "\16\uffff";
    static final String DFA1_minS =
        "\1\55\2\uffff\1\2\4\uffff\1\127\1\60\1\3\1\uffff\1\0\1\uffff";
    static final String DFA1_maxS =
        "\1\146\2\uffff\1\2\4\uffff\2\127\1\3\1\uffff\1\0\1\uffff";
    static final String DFA1_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\1\5\1\7\1\10\3\uffff\1\3\1\uffff\1"+
        "\6";
    static final String DFA1_specialS =
        "\14\uffff\1\0\1\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\3\20\uffff\1\6\1\5\10\uffff\1\4\1\2\21\uffff\4\1\7\uffff"+
            "\1\7",
            "",
            "",
            "\1\10",
            "",
            "",
            "",
            "",
            "\1\11",
            "\1\13\17\uffff\1\13\2\uffff\1\12\23\uffff\1\13",
            "\1\14",
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
            return "86:1: topdown : ( grammarSpec | rules | option | rule | ruleref | tokenAlias | tokenRefWithArgs | elementOption );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TreeNodeStream input = (TreeNodeStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA1_12 = input.LA(1);

                         
                        int index1_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((inContext("OPTIONS"))) ) {s = 11;}

                        else if ( ((inContext("TOKENS"))) ) {s = 13;}

                         
                        input.seek(index1_12);
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
    public static final BitSet FOLLOW_grammarType_in_grammarSpec145 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_grammarSpec147 = new BitSet(new long[]{0x0000000000580048L,0x0000000000000100L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec149 = new BitSet(new long[]{0x0000000000580008L,0x0000000000000100L});
    public static final BitSet FOLLOW_prequelConstructs_in_grammarSpec152 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000100L});
    public static final BitSet FOLLOW_RULE_in_grammarSpec156 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_set_in_grammarType0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_prequelConstructs224 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_IMPORT_in_prequelConstructs237 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_TOKENS_in_prequelConstructs250 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_RULES_in_rules273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_option294 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_option298 = new BitSet(new long[]{0x0001000000000000L,0x0000000000800009L});
    public static final BitSet FOLLOW_optionValue_in_option300 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_set_in_optionValue0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule382 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_rule386 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF8L,0x000001FFFFFFFFFFL});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenAlias428 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_tokenAlias430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenAlias432 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenRefWithArgs449 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ARG_ACTION_in_tokenRefWithArgs451 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOption473 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption484 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption488 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_ID_in_elementOption492 = new BitSet(new long[]{0x0000000000000008L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption506 = new BitSet(new long[]{0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_elementOption510 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption514 = new BitSet(new long[]{0x0000000000000008L});

}