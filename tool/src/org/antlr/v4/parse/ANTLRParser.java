// $ANTLR ${project.version} ${buildNumber} ANTLRParser.g 2010-02-23 11:32:34

/*
 [The "BSD licence"]
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
import org.antlr.runtime.tree.*;
import org.antlr.v4.tool.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** The definitive ANTLR v3 grammar to parse ANTLR v4 grammars.
 *  The grammar builds ASTs that are sniffed by subsequent stages.
 */
public class ANTLRParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "RESULT", "ALT_REWRITE"
    };
    public static final int LT=43;
    public static final int COMBINED=90;
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
    public static final int GRAMMAR=27;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int WSCHARS=65;
    public static final int RULEACTIONS=75;
    public static final int INITACTION=91;
    public static final int ALT_REWRITE=101;
    public static final int IMPLIES=42;
    public static final int RBRACE=61;
    public static final int RULE=72;
    public static final int ACTION_ESC=17;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int INT=64;
    public static final int CHAR_RANGE=82;
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
    public static final int LABEL=92;
    public static final int TEMPLATE=35;
    public static final int SYN_SEMPRED=94;
    public static final int ERRCHAR=71;
    public static final int BLOCK=76;
    public static final int PLUS_ASSIGN=50;
    public static final int ASSIGN=45;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=80;
    public static final int OPTIONS=19;

    // delegates
    // delegators


        public ANTLRParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public ANTLRParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return ANTLRParser.tokenNames; }
    public String getGrammarFileName() { return "ANTLRParser.g"; }


    Stack paraphrases = new Stack();
    public void displayRecognitionError(String[] tokenNames,
    									RecognitionException e)
    {
    	String msg = ErrorManager.getParserErrorMessage(this, e);
    	if ( paraphrases.size()>0 ) {
    		String paraphrase = (String)paraphrases.peek();
    		msg = msg+" while "+paraphrase;
    	}
    //	List stack = getRuleInvocationStack(e, this.getClass().getName());
    //	msg += ", rule stack = "+stack;
    	ErrorManager.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e.token, e, msg);
    }


    public static class grammarSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "grammarSpec"
    // ANTLRParser.g:153:1: grammarSpec : ( DOC_COMMENT )? grammarType id SEMI sync ( prequelConstruct sync )* rules EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ) ;
    public final ANTLRParser.grammarSpec_return grammarSpec() throws RecognitionException {
        ANTLRParser.grammarSpec_return retval = new ANTLRParser.grammarSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOC_COMMENT1=null;
        Token SEMI4=null;
        Token EOF9=null;
        ANTLRParser.grammarType_return grammarType2 = null;

        ANTLRParser.id_return id3 = null;

        ANTLRParser.sync_return sync5 = null;

        ANTLRParser.prequelConstruct_return prequelConstruct6 = null;

        ANTLRParser.sync_return sync7 = null;

        ANTLRParser.rules_return rules8 = null;


        GrammarAST DOC_COMMENT1_tree=null;
        GrammarAST SEMI4_tree=null;
        GrammarAST EOF9_tree=null;
        RewriteRuleTokenStream stream_DOC_COMMENT=new RewriteRuleTokenStream(adaptor,"token DOC_COMMENT");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
        RewriteRuleSubtreeStream stream_prequelConstruct=new RewriteRuleSubtreeStream(adaptor,"rule prequelConstruct");
        RewriteRuleSubtreeStream stream_grammarType=new RewriteRuleSubtreeStream(adaptor,"rule grammarType");
        RewriteRuleSubtreeStream stream_rules=new RewriteRuleSubtreeStream(adaptor,"rule rules");
        try {
            // ANTLRParser.g:154:5: ( ( DOC_COMMENT )? grammarType id SEMI sync ( prequelConstruct sync )* rules EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ) )
            // ANTLRParser.g:158:7: ( DOC_COMMENT )? grammarType id SEMI sync ( prequelConstruct sync )* rules EOF
            {
            // ANTLRParser.g:158:7: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ANTLRParser.g:158:7: DOC_COMMENT
                    {
                    DOC_COMMENT1=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec456); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOC_COMMENT.add(DOC_COMMENT1);


                    }
                    break;

            }

            pushFollow(FOLLOW_grammarType_in_grammarSpec493);
            grammarType2=grammarType();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_grammarType.add(grammarType2.getTree());
            pushFollow(FOLLOW_id_in_grammarSpec495);
            id3=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id3.getTree());
            SEMI4=(Token)match(input,SEMI,FOLLOW_SEMI_in_grammarSpec497); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI4);

            pushFollow(FOLLOW_sync_in_grammarSpec541);
            sync5=sync();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sync.add(sync5.getTree());
            // ANTLRParser.g:176:12: ( prequelConstruct sync )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=OPTIONS && LA2_0<=IMPORT)||LA2_0==AT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ANTLRParser.g:176:14: prequelConstruct sync
            	    {
            	    pushFollow(FOLLOW_prequelConstruct_in_grammarSpec545);
            	    prequelConstruct6=prequelConstruct();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_prequelConstruct.add(prequelConstruct6.getTree());
            	    pushFollow(FOLLOW_sync_in_grammarSpec547);
            	    sync7=sync();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sync.add(sync7.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            pushFollow(FOLLOW_rules_in_grammarSpec577);
            rules8=rules();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rules.add(rules8.getTree());
            EOF9=(Token)match(input,EOF,FOLLOW_EOF_in_grammarSpec620); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF9);



            // AST REWRITE
            // elements: rules, id, DOC_COMMENT, grammarType, prequelConstruct
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 195:7: -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules )
            {
                // ANTLRParser.g:195:10: ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_grammarType.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:197:14: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:198:14: ( prequelConstruct )*
                while ( stream_prequelConstruct.hasNext() ) {
                    adaptor.addChild(root_1, stream_prequelConstruct.nextTree());

                }
                stream_prequelConstruct.reset();
                adaptor.addChild(root_1, stream_rules.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "grammarSpec"

    public static class grammarType_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "grammarType"
    // ANTLRParser.g:231:1: grammarType : (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] ) ;
    public final ANTLRParser.grammarType_return grammarType() throws RecognitionException {
        ANTLRParser.grammarType_return retval = new ANTLRParser.grammarType_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token t=null;
        Token g=null;

        GrammarAST t_tree=null;
        GrammarAST g_tree=null;
        RewriteRuleTokenStream stream_TREE=new RewriteRuleTokenStream(adaptor,"token TREE");
        RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
        RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");
        RewriteRuleTokenStream stream_GRAMMAR=new RewriteRuleTokenStream(adaptor,"token GRAMMAR");

        try {
            // ANTLRParser.g:236:5: ( (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] ) )
            // ANTLRParser.g:236:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] )
            {
            // ANTLRParser.g:236:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] )
            int alt3=4;
            switch ( input.LA(1) ) {
            case LEXER:
                {
                alt3=1;
                }
                break;
            case PARSER:
                {
                alt3=2;
                }
                break;
            case TREE:
                {
                alt3=3;
                }
                break;
            case GRAMMAR:
                {
                alt3=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ANTLRParser.g:236:9: t= LEXER g= GRAMMAR
                    {
                    t=(Token)match(input,LEXER,FOLLOW_LEXER_in_grammarType814); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType818); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GRAMMAR.add(g);



                    // AST REWRITE
                    // elements: GRAMMAR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 236:28: -> GRAMMAR[$g, \"LEXER_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "LEXER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:238:6: t= PARSER g= GRAMMAR
                    {
                    t=(Token)match(input,PARSER,FOLLOW_PARSER_in_grammarType851); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType855); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GRAMMAR.add(g);



                    // AST REWRITE
                    // elements: GRAMMAR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 238:25: -> GRAMMAR[$g, \"PARSER_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "PARSER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:241:6: t= TREE g= GRAMMAR
                    {
                    t=(Token)match(input,TREE,FOLLOW_TREE_in_grammarType882); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TREE.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType886); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GRAMMAR.add(g);



                    // AST REWRITE
                    // elements: GRAMMAR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 241:25: -> GRAMMAR[$g, \"TREE_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "TREE_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // ANTLRParser.g:244:6: g= GRAMMAR
                    {
                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType913); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_GRAMMAR.add(g);



                    // AST REWRITE
                    // elements: GRAMMAR
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 244:25: -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "COMBINED_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {

              	if ( t!=null ) ((GrammarRootAST)((GrammarAST)retval.tree)).grammarType = (t!=null?t.getType():0);
              	else ((GrammarRootAST)((GrammarAST)retval.tree)).grammarType=COMBINED;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "grammarType"

    public static class prequelConstruct_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prequelConstruct"
    // ANTLRParser.g:251:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action );
    public final ANTLRParser.prequelConstruct_return prequelConstruct() throws RecognitionException {
        ANTLRParser.prequelConstruct_return retval = new ANTLRParser.prequelConstruct_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.optionsSpec_return optionsSpec10 = null;

        ANTLRParser.delegateGrammars_return delegateGrammars11 = null;

        ANTLRParser.tokensSpec_return tokensSpec12 = null;

        ANTLRParser.attrScope_return attrScope13 = null;

        ANTLRParser.action_return action14 = null;



        try {
            // ANTLRParser.g:252:2: ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action )
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
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // ANTLRParser.g:253:4: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_prequelConstruct977);
                    optionsSpec10=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec10.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:257:7: delegateGrammars
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct1003);
                    delegateGrammars11=delegateGrammars();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, delegateGrammars11.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:264:7: tokensSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_tokensSpec_in_prequelConstruct1053);
                    tokensSpec12=tokensSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tokensSpec12.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:269:7: attrScope
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_attrScope_in_prequelConstruct1089);
                    attrScope13=attrScope();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, attrScope13.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:275:7: action
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_action_in_prequelConstruct1132);
                    action14=action();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, action14.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "prequelConstruct"

    public static class optionsSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "optionsSpec"
    // ANTLRParser.g:279:1: optionsSpec : OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ ) ;
    public final ANTLRParser.optionsSpec_return optionsSpec() throws RecognitionException {
        ANTLRParser.optionsSpec_return retval = new ANTLRParser.optionsSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token OPTIONS15=null;
        Token SEMI17=null;
        Token RBRACE18=null;
        ANTLRParser.option_return option16 = null;


        GrammarAST OPTIONS15_tree=null;
        GrammarAST SEMI17_tree=null;
        GrammarAST RBRACE18_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_OPTIONS=new RewriteRuleTokenStream(adaptor,"token OPTIONS");
        RewriteRuleSubtreeStream stream_option=new RewriteRuleSubtreeStream(adaptor,"rule option");
        try {
            // ANTLRParser.g:280:2: ( OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ ) )
            // ANTLRParser.g:280:4: OPTIONS ( option SEMI )* RBRACE
            {
            OPTIONS15=(Token)match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec1149); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPTIONS.add(OPTIONS15);

            // ANTLRParser.g:280:12: ( option SEMI )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==TEMPLATE||(LA5_0>=TOKEN_REF && LA5_0<=RULE_REF)) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ANTLRParser.g:280:13: option SEMI
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec1152);
            	    option16=option();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_option.add(option16.getTree());
            	    SEMI17=(Token)match(input,SEMI,FOLLOW_SEMI_in_optionsSpec1154); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEMI.add(SEMI17);


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            RBRACE18=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_optionsSpec1158); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE18);



            // AST REWRITE
            // elements: option, OPTIONS
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 280:34: -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ )
            {
                // ANTLRParser.g:280:37: ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(OPTIONS, OPTIONS15, "OPTIONS"), root_1);

                if ( !(stream_option.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_option.hasNext() ) {
                    adaptor.addChild(root_1, stream_option.nextTree());

                }
                stream_option.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "optionsSpec"

    public static class option_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "option"
    // ANTLRParser.g:283:1: option : id ASSIGN optionValue ;
    public final ANTLRParser.option_return option() throws RecognitionException {
        ANTLRParser.option_return retval = new ANTLRParser.option_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN20=null;
        ANTLRParser.id_return id19 = null;

        ANTLRParser.optionValue_return optionValue21 = null;


        GrammarAST ASSIGN20_tree=null;

        try {
            // ANTLRParser.g:284:5: ( id ASSIGN optionValue )
            // ANTLRParser.g:284:9: id ASSIGN optionValue
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_option1195);
            id19=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id19.getTree());
            ASSIGN20=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_option1197); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ASSIGN20_tree = (GrammarAST)adaptor.create(ASSIGN20);
            root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN20_tree, root_0);
            }
            pushFollow(FOLLOW_optionValue_in_option1200);
            optionValue21=optionValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, optionValue21.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "option"

    public static class optionValue_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "optionValue"
    // ANTLRParser.g:292:1: optionValue : ( qid | STRING_LITERAL | INT | STAR );
    public final ANTLRParser.optionValue_return optionValue() throws RecognitionException {
        ANTLRParser.optionValue_return retval = new ANTLRParser.optionValue_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token STRING_LITERAL23=null;
        Token INT24=null;
        Token STAR25=null;
        ANTLRParser.qid_return qid22 = null;


        GrammarAST STRING_LITERAL23_tree=null;
        GrammarAST INT24_tree=null;
        GrammarAST STAR25_tree=null;

        try {
            // ANTLRParser.g:293:5: ( qid | STRING_LITERAL | INT | STAR )
            int alt6=4;
            switch ( input.LA(1) ) {
            case TEMPLATE:
            case TOKEN_REF:
            case RULE_REF:
                {
                alt6=1;
                }
                break;
            case STRING_LITERAL:
                {
                alt6=2;
                }
                break;
            case INT:
                {
                alt6=3;
                }
                break;
            case STAR:
                {
                alt6=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ANTLRParser.g:297:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_optionValue1250);
                    qid22=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid22.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:301:7: STRING_LITERAL
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STRING_LITERAL23=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_optionValue1274); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL23_tree = new TerminalAST(STRING_LITERAL23) ;
                    adaptor.addChild(root_0, STRING_LITERAL23_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:305:7: INT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    INT24=(Token)match(input,INT,FOLLOW_INT_in_optionValue1300); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT24_tree = (GrammarAST)adaptor.create(INT24);
                    adaptor.addChild(root_0, INT24_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:309:7: STAR
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STAR25=(Token)match(input,STAR,FOLLOW_STAR_in_optionValue1329); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STAR25_tree = (GrammarAST)adaptor.create(STAR25);
                    adaptor.addChild(root_0, STAR25_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "optionValue"

    public static class delegateGrammars_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "delegateGrammars"
    // ANTLRParser.g:314:1: delegateGrammars : IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) ;
    public final ANTLRParser.delegateGrammars_return delegateGrammars() throws RecognitionException {
        ANTLRParser.delegateGrammars_return retval = new ANTLRParser.delegateGrammars_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token IMPORT26=null;
        Token COMMA28=null;
        Token SEMI30=null;
        ANTLRParser.delegateGrammar_return delegateGrammar27 = null;

        ANTLRParser.delegateGrammar_return delegateGrammar29 = null;


        GrammarAST IMPORT26_tree=null;
        GrammarAST COMMA28_tree=null;
        GrammarAST SEMI30_tree=null;
        RewriteRuleTokenStream stream_IMPORT=new RewriteRuleTokenStream(adaptor,"token IMPORT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_delegateGrammar=new RewriteRuleSubtreeStream(adaptor,"rule delegateGrammar");
        try {
            // ANTLRParser.g:315:2: ( IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) )
            // ANTLRParser.g:315:4: IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI
            {
            IMPORT26=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars1345); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IMPORT.add(IMPORT26);

            pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1347);
            delegateGrammar27=delegateGrammar();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar27.getTree());
            // ANTLRParser.g:315:27: ( COMMA delegateGrammar )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==COMMA) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ANTLRParser.g:315:28: COMMA delegateGrammar
            	    {
            	    COMMA28=(Token)match(input,COMMA,FOLLOW_COMMA_in_delegateGrammars1350); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA28);

            	    pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1352);
            	    delegateGrammar29=delegateGrammar();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar29.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            SEMI30=(Token)match(input,SEMI,FOLLOW_SEMI_in_delegateGrammars1356); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI30);



            // AST REWRITE
            // elements: delegateGrammar, IMPORT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 315:57: -> ^( IMPORT ( delegateGrammar )+ )
            {
                // ANTLRParser.g:315:60: ^( IMPORT ( delegateGrammar )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_IMPORT.nextNode(), root_1);

                if ( !(stream_delegateGrammar.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_delegateGrammar.hasNext() ) {
                    adaptor.addChild(root_1, stream_delegateGrammar.nextTree());

                }
                stream_delegateGrammar.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "delegateGrammars"

    public static class delegateGrammar_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "delegateGrammar"
    // ANTLRParser.g:320:1: delegateGrammar : ( id ASSIGN id | id );
    public final ANTLRParser.delegateGrammar_return delegateGrammar() throws RecognitionException {
        ANTLRParser.delegateGrammar_return retval = new ANTLRParser.delegateGrammar_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN32=null;
        ANTLRParser.id_return id31 = null;

        ANTLRParser.id_return id33 = null;

        ANTLRParser.id_return id34 = null;


        GrammarAST ASSIGN32_tree=null;

        try {
            // ANTLRParser.g:321:5: ( id ASSIGN id | id )
            int alt8=2;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                int LA8_1 = input.LA(2);

                if ( ((LA8_1>=COMMA && LA8_1<=SEMI)) ) {
                    alt8=2;
                }
                else if ( (LA8_1==ASSIGN) ) {
                    alt8=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 1, input);

                    throw nvae;
                }
                }
                break;
            case TOKEN_REF:
                {
                int LA8_2 = input.LA(2);

                if ( ((LA8_2>=COMMA && LA8_2<=SEMI)) ) {
                    alt8=2;
                }
                else if ( (LA8_2==ASSIGN) ) {
                    alt8=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 2, input);

                    throw nvae;
                }
                }
                break;
            case TEMPLATE:
                {
                int LA8_3 = input.LA(2);

                if ( ((LA8_3>=COMMA && LA8_3<=SEMI)) ) {
                    alt8=2;
                }
                else if ( (LA8_3==ASSIGN) ) {
                    alt8=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 8, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // ANTLRParser.g:321:9: id ASSIGN id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1383);
                    id31=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id31.getTree());
                    ASSIGN32=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar1385); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN32_tree = (GrammarAST)adaptor.create(ASSIGN32);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN32_tree, root_0);
                    }
                    pushFollow(FOLLOW_id_in_delegateGrammar1388);
                    id33=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id33.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:322:9: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1398);
                    id34=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id34.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "delegateGrammar"

    public static class tokensSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "tokensSpec"
    // ANTLRParser.g:325:1: tokensSpec : TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) ;
    public final ANTLRParser.tokensSpec_return tokensSpec() throws RecognitionException {
        ANTLRParser.tokensSpec_return retval = new ANTLRParser.tokensSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKENS35=null;
        Token RBRACE37=null;
        ANTLRParser.tokenSpec_return tokenSpec36 = null;


        GrammarAST TOKENS35_tree=null;
        GrammarAST RBRACE37_tree=null;
        RewriteRuleTokenStream stream_TOKENS=new RewriteRuleTokenStream(adaptor,"token TOKENS");
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleSubtreeStream stream_tokenSpec=new RewriteRuleSubtreeStream(adaptor,"rule tokenSpec");
        try {
            // ANTLRParser.g:332:2: ( TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) )
            // ANTLRParser.g:332:4: TOKENS ( tokenSpec )+ RBRACE
            {
            TOKENS35=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec1414); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TOKENS.add(TOKENS35);

            // ANTLRParser.g:332:11: ( tokenSpec )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==TEMPLATE||(LA9_0>=TOKEN_REF && LA9_0<=RULE_REF)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ANTLRParser.g:332:11: tokenSpec
            	    {
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec1416);
            	    tokenSpec36=tokenSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_tokenSpec.add(tokenSpec36.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            RBRACE37=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_tokensSpec1419); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE37);



            // AST REWRITE
            // elements: tokenSpec, TOKENS
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 332:29: -> ^( TOKENS ( tokenSpec )+ )
            {
                // ANTLRParser.g:332:32: ^( TOKENS ( tokenSpec )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_TOKENS.nextNode(), root_1);

                if ( !(stream_tokenSpec.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_tokenSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_tokenSpec.nextTree());

                }
                stream_tokenSpec.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "tokensSpec"

    public static class tokenSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "tokenSpec"
    // ANTLRParser.g:335:1: tokenSpec : ( id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI | RULE_REF );
    public final ANTLRParser.tokenSpec_return tokenSpec() throws RecognitionException {
        ANTLRParser.tokenSpec_return retval = new ANTLRParser.tokenSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN39=null;
        Token STRING_LITERAL40=null;
        Token SEMI41=null;
        Token RULE_REF42=null;
        ANTLRParser.id_return id38 = null;


        GrammarAST ASSIGN39_tree=null;
        GrammarAST STRING_LITERAL40_tree=null;
        GrammarAST SEMI41_tree=null;
        GrammarAST RULE_REF42_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:336:2: ( id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI | RULE_REF )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_REF) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==SEMI||LA11_1==ASSIGN) ) {
                    alt11=1;
                }
                else if ( (LA11_1==TEMPLATE||(LA11_1>=RBRACE && LA11_1<=RULE_REF)) ) {
                    alt11=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA11_0==TEMPLATE||LA11_0==TOKEN_REF) ) {
                alt11=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // ANTLRParser.g:336:4: id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI
                    {
                    pushFollow(FOLLOW_id_in_tokenSpec1439);
                    id38=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id38.getTree());
                    // ANTLRParser.g:337:3: ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id )
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==ASSIGN) ) {
                        alt10=1;
                    }
                    else if ( (LA10_0==SEMI) ) {
                        alt10=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 0, input);

                        throw nvae;
                    }
                    switch (alt10) {
                        case 1 :
                            // ANTLRParser.g:337:5: ASSIGN STRING_LITERAL
                            {
                            ASSIGN39=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec1445); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN39);

                            STRING_LITERAL40=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec1447); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL40);



                            // AST REWRITE
                            // elements: ASSIGN, STRING_LITERAL, id
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 337:27: -> ^( ASSIGN id STRING_LITERAL )
                            {
                                // ANTLRParser.g:337:30: ^( ASSIGN id STRING_LITERAL )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ASSIGN.nextNode(), root_1);

                                adaptor.addChild(root_1, stream_id.nextTree());
                                adaptor.addChild(root_1, new TerminalAST(stream_STRING_LITERAL.nextToken()));

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:338:11: 
                            {

                            // AST REWRITE
                            // elements: id
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 338:11: -> id
                            {
                                adaptor.addChild(root_0, stream_id.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }

                    SEMI41=(Token)match(input,SEMI,FOLLOW_SEMI_in_tokenSpec1482); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI41);


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:341:4: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF42=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_tokenSpec1487); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF42_tree = (GrammarAST)adaptor.create(RULE_REF42);
                    adaptor.addChild(root_0, RULE_REF42_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "tokenSpec"

    public static class attrScope_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attrScope"
    // ANTLRParser.g:347:1: attrScope : SCOPE id ACTION -> ^( SCOPE id ACTION ) ;
    public final ANTLRParser.attrScope_return attrScope() throws RecognitionException {
        ANTLRParser.attrScope_return retval = new ANTLRParser.attrScope_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token SCOPE43=null;
        Token ACTION45=null;
        ANTLRParser.id_return id44 = null;


        GrammarAST SCOPE43_tree=null;
        GrammarAST ACTION45_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:348:2: ( SCOPE id ACTION -> ^( SCOPE id ACTION ) )
            // ANTLRParser.g:348:4: SCOPE id ACTION
            {
            SCOPE43=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_attrScope1502); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE43);

            pushFollow(FOLLOW_id_in_attrScope1504);
            id44=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id44.getTree());
            ACTION45=(Token)match(input,ACTION,FOLLOW_ACTION_in_attrScope1506); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION45);



            // AST REWRITE
            // elements: ACTION, id, SCOPE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 348:20: -> ^( SCOPE id ACTION )
            {
                // ANTLRParser.g:348:23: ^( SCOPE id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_SCOPE.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attrScope"

    public static class action_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "action"
    // ANTLRParser.g:354:1: action : AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) ;
    public final ANTLRParser.action_return action() throws RecognitionException {
        ANTLRParser.action_return retval = new ANTLRParser.action_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token AT46=null;
        Token COLONCOLON48=null;
        Token ACTION50=null;
        ANTLRParser.actionScopeName_return actionScopeName47 = null;

        ANTLRParser.id_return id49 = null;


        GrammarAST AT46_tree=null;
        GrammarAST COLONCOLON48_tree=null;
        GrammarAST ACTION50_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_COLONCOLON=new RewriteRuleTokenStream(adaptor,"token COLONCOLON");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_actionScopeName=new RewriteRuleSubtreeStream(adaptor,"rule actionScopeName");
        try {
            // ANTLRParser.g:356:2: ( AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) )
            // ANTLRParser.g:356:4: AT ( actionScopeName COLONCOLON )? id ACTION
            {
            AT46=(Token)match(input,AT,FOLLOW_AT_in_action1535); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT46);

            // ANTLRParser.g:356:7: ( actionScopeName COLONCOLON )?
            int alt12=2;
            switch ( input.LA(1) ) {
                case RULE_REF:
                    {
                    int LA12_1 = input.LA(2);

                    if ( (LA12_1==COLONCOLON) ) {
                        alt12=1;
                    }
                    }
                    break;
                case TOKEN_REF:
                    {
                    int LA12_2 = input.LA(2);

                    if ( (LA12_2==COLONCOLON) ) {
                        alt12=1;
                    }
                    }
                    break;
                case TEMPLATE:
                    {
                    int LA12_3 = input.LA(2);

                    if ( (LA12_3==COLONCOLON) ) {
                        alt12=1;
                    }
                    }
                    break;
                case LEXER:
                case PARSER:
                    {
                    alt12=1;
                    }
                    break;
            }

            switch (alt12) {
                case 1 :
                    // ANTLRParser.g:356:8: actionScopeName COLONCOLON
                    {
                    pushFollow(FOLLOW_actionScopeName_in_action1538);
                    actionScopeName47=actionScopeName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_actionScopeName.add(actionScopeName47.getTree());
                    COLONCOLON48=(Token)match(input,COLONCOLON,FOLLOW_COLONCOLON_in_action1540); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLONCOLON.add(COLONCOLON48);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_action1544);
            id49=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id49.getTree());
            ACTION50=(Token)match(input,ACTION,FOLLOW_ACTION_in_action1546); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION50);



            // AST REWRITE
            // elements: ACTION, AT, actionScopeName, id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 356:47: -> ^( AT ( actionScopeName )? id ACTION )
            {
                // ANTLRParser.g:356:50: ^( AT ( actionScopeName )? id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                // ANTLRParser.g:356:55: ( actionScopeName )?
                if ( stream_actionScopeName.hasNext() ) {
                    adaptor.addChild(root_1, stream_actionScopeName.nextTree());

                }
                stream_actionScopeName.reset();
                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "action"

    public static class actionScopeName_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "actionScopeName"
    // ANTLRParser.g:359:1: actionScopeName : ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] );
    public final ANTLRParser.actionScopeName_return actionScopeName() throws RecognitionException {
        ANTLRParser.actionScopeName_return retval = new ANTLRParser.actionScopeName_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LEXER52=null;
        Token PARSER53=null;
        ANTLRParser.id_return id51 = null;


        GrammarAST LEXER52_tree=null;
        GrammarAST PARSER53_tree=null;
        RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
        RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");

        try {
            // ANTLRParser.g:363:2: ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] )
            int alt13=3;
            switch ( input.LA(1) ) {
            case TEMPLATE:
            case TOKEN_REF:
            case RULE_REF:
                {
                alt13=1;
                }
                break;
            case LEXER:
                {
                alt13=2;
                }
                break;
            case PARSER:
                {
                alt13=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }

            switch (alt13) {
                case 1 :
                    // ANTLRParser.g:363:4: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_actionScopeName1577);
                    id51=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id51.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:364:4: LEXER
                    {
                    LEXER52=(Token)match(input,LEXER,FOLLOW_LEXER_in_actionScopeName1582); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(LEXER52);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 364:10: -> ID[$LEXER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, LEXER52));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:365:9: PARSER
                    {
                    PARSER53=(Token)match(input,PARSER,FOLLOW_PARSER_in_actionScopeName1597); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(PARSER53);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 365:16: -> ID[$PARSER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, PARSER53));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "actionScopeName"

    public static class rules_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rules"
    // ANTLRParser.g:368:1: rules : sync ( rule sync )* -> ^( RULES ( rule )* ) ;
    public final ANTLRParser.rules_return rules() throws RecognitionException {
        ANTLRParser.rules_return retval = new ANTLRParser.rules_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.sync_return sync54 = null;

        ANTLRParser.rule_return rule55 = null;

        ANTLRParser.sync_return sync56 = null;


        RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        try {
            // ANTLRParser.g:369:5: ( sync ( rule sync )* -> ^( RULES ( rule )* ) )
            // ANTLRParser.g:369:7: sync ( rule sync )*
            {
            pushFollow(FOLLOW_sync_in_rules1616);
            sync54=sync();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sync.add(sync54.getTree());
            // ANTLRParser.g:369:12: ( rule sync )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==DOC_COMMENT||LA14_0==FRAGMENT||(LA14_0>=PROTECTED && LA14_0<=PRIVATE)||LA14_0==TEMPLATE||(LA14_0>=TOKEN_REF && LA14_0<=RULE_REF)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ANTLRParser.g:369:13: rule sync
            	    {
            	    pushFollow(FOLLOW_rule_in_rules1619);
            	    rule55=rule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule.add(rule55.getTree());
            	    pushFollow(FOLLOW_sync_in_rules1621);
            	    sync56=sync();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sync.add(sync56.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);



            // AST REWRITE
            // elements: rule
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 373:7: -> ^( RULES ( rule )* )
            {
                // ANTLRParser.g:373:9: ^( RULES ( rule )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULES, "RULES"), root_1);

                // ANTLRParser.g:373:17: ( rule )*
                while ( stream_rule.hasNext() ) {
                    adaptor.addChild(root_1, stream_rule.nextTree());

                }
                stream_rule.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rules"

    public static class sync_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sync"
    // ANTLRParser.g:376:1: sync : ;
    public final ANTLRParser.sync_return sync() throws RecognitionException {
        ANTLRParser.sync_return retval = new ANTLRParser.sync_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;


        	BitSet followSet = computeErrorRecoverySet();
        	if ( input.LA(1)!=Token.EOF && !followSet.member(input.LA(1)) ) {
        		reportError(new NoViableAltException("",0,0,input));			
               	beginResync();
               	consumeUntil(input, followSet);
               	endResync();
        	}

        try {
            // ANTLRParser.g:385:3: ()
            // ANTLRParser.g:386:2: 
            {
            root_0 = (GrammarAST)adaptor.nil();

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sync"

    public static class rule_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // ANTLRParser.g:397:1: rule : ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? rulePrequels COLON ruleBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* ) ;
    public final ANTLRParser.rule_return rule() throws RecognitionException {
        ANTLRParser.rule_return retval = new ANTLRParser.rule_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOC_COMMENT57=null;
        Token ARG_ACTION60=null;
        Token COLON63=null;
        Token SEMI65=null;
        ANTLRParser.ruleModifiers_return ruleModifiers58 = null;

        ANTLRParser.id_return id59 = null;

        ANTLRParser.ruleReturns_return ruleReturns61 = null;

        ANTLRParser.rulePrequels_return rulePrequels62 = null;

        ANTLRParser.ruleBlock_return ruleBlock64 = null;

        ANTLRParser.exceptionGroup_return exceptionGroup66 = null;


        GrammarAST DOC_COMMENT57_tree=null;
        GrammarAST ARG_ACTION60_tree=null;
        GrammarAST COLON63_tree=null;
        GrammarAST SEMI65_tree=null;
        RewriteRuleTokenStream stream_DOC_COMMENT=new RewriteRuleTokenStream(adaptor,"token DOC_COMMENT");
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_ruleModifiers=new RewriteRuleSubtreeStream(adaptor,"rule ruleModifiers");
        RewriteRuleSubtreeStream stream_rulePrequels=new RewriteRuleSubtreeStream(adaptor,"rule rulePrequels");
        RewriteRuleSubtreeStream stream_exceptionGroup=new RewriteRuleSubtreeStream(adaptor,"rule exceptionGroup");
        RewriteRuleSubtreeStream stream_ruleReturns=new RewriteRuleSubtreeStream(adaptor,"rule ruleReturns");
        RewriteRuleSubtreeStream stream_ruleBlock=new RewriteRuleSubtreeStream(adaptor,"rule ruleBlock");
         paraphrases.push("matching a rule"); 
        try {
            // ANTLRParser.g:400:5: ( ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? rulePrequels COLON ruleBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* ) )
            // ANTLRParser.g:401:7: ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? rulePrequels COLON ruleBlock SEMI exceptionGroup
            {
            // ANTLRParser.g:401:7: ( DOC_COMMENT )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==DOC_COMMENT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ANTLRParser.g:401:7: DOC_COMMENT
                    {
                    DOC_COMMENT57=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_rule1722); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOC_COMMENT.add(DOC_COMMENT57);


                    }
                    break;

            }

            // ANTLRParser.g:407:7: ( ruleModifiers )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==FRAGMENT||(LA16_0>=PROTECTED && LA16_0<=PRIVATE)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ANTLRParser.g:407:7: ruleModifiers
                    {
                    pushFollow(FOLLOW_ruleModifiers_in_rule1766);
                    ruleModifiers58=ruleModifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleModifiers.add(ruleModifiers58.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_rule1789);
            id59=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id59.getTree());
            // ANTLRParser.g:421:4: ( ARG_ACTION )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==ARG_ACTION) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ANTLRParser.g:421:4: ARG_ACTION
                    {
                    ARG_ACTION60=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule1822); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION60);


                    }
                    break;

            }

            // ANTLRParser.g:423:4: ( ruleReturns )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RETURNS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ANTLRParser.g:423:4: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_rule1832);
                    ruleReturns61=ruleReturns();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleReturns.add(ruleReturns61.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rulePrequels_in_rule1870);
            rulePrequels62=rulePrequels();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rulePrequels.add(rulePrequels62.getTree());
            COLON63=(Token)match(input,COLON,FOLLOW_COLON_in_rule1885); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON63);

            pushFollow(FOLLOW_ruleBlock_in_rule1914);
            ruleBlock64=ruleBlock();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ruleBlock.add(ruleBlock64.getTree());
            SEMI65=(Token)match(input,SEMI,FOLLOW_SEMI_in_rule1929); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI65);

            pushFollow(FOLLOW_exceptionGroup_in_rule1938);
            exceptionGroup66=exceptionGroup();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exceptionGroup.add(exceptionGroup66.getTree());


            // AST REWRITE
            // elements: rulePrequels, ruleReturns, ruleModifiers, DOC_COMMENT, id, exceptionGroup, ARG_ACTION, ruleBlock
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 450:7: -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* )
            {
                // ANTLRParser.g:450:10: ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new RuleAST(RULE), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:450:30: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:450:43: ( ruleModifiers )?
                if ( stream_ruleModifiers.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleModifiers.nextTree());

                }
                stream_ruleModifiers.reset();
                // ANTLRParser.g:450:58: ( ARG_ACTION )?
                if ( stream_ARG_ACTION.hasNext() ) {
                    adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                }
                stream_ARG_ACTION.reset();
                // ANTLRParser.g:451:9: ( ruleReturns )?
                if ( stream_ruleReturns.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleReturns.nextTree());

                }
                stream_ruleReturns.reset();
                // ANTLRParser.g:451:22: ( rulePrequels )?
                if ( stream_rulePrequels.hasNext() ) {
                    adaptor.addChild(root_1, stream_rulePrequels.nextTree());

                }
                stream_rulePrequels.reset();
                adaptor.addChild(root_1, stream_ruleBlock.nextTree());
                // ANTLRParser.g:451:46: ( exceptionGroup )*
                while ( stream_exceptionGroup.hasNext() ) {
                    adaptor.addChild(root_1, stream_exceptionGroup.nextTree());

                }
                stream_exceptionGroup.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule"

    public static class exceptionGroup_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exceptionGroup"
    // ANTLRParser.g:461:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
    public final ANTLRParser.exceptionGroup_return exceptionGroup() throws RecognitionException {
        ANTLRParser.exceptionGroup_return retval = new ANTLRParser.exceptionGroup_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.exceptionHandler_return exceptionHandler67 = null;

        ANTLRParser.finallyClause_return finallyClause68 = null;



        try {
            // ANTLRParser.g:462:5: ( ( exceptionHandler )* ( finallyClause )? )
            // ANTLRParser.g:462:7: ( exceptionHandler )* ( finallyClause )?
            {
            root_0 = (GrammarAST)adaptor.nil();

            // ANTLRParser.g:462:7: ( exceptionHandler )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==CATCH) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // ANTLRParser.g:462:7: exceptionHandler
            	    {
            	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup2030);
            	    exceptionHandler67=exceptionHandler();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exceptionHandler67.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            // ANTLRParser.g:462:25: ( finallyClause )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==FINALLY) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ANTLRParser.g:462:25: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup2033);
                    finallyClause68=finallyClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, finallyClause68.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "exceptionGroup"

    public static class exceptionHandler_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exceptionHandler"
    // ANTLRParser.g:467:1: exceptionHandler : CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) ;
    public final ANTLRParser.exceptionHandler_return exceptionHandler() throws RecognitionException {
        ANTLRParser.exceptionHandler_return retval = new ANTLRParser.exceptionHandler_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token CATCH69=null;
        Token ARG_ACTION70=null;
        Token ACTION71=null;

        GrammarAST CATCH69_tree=null;
        GrammarAST ARG_ACTION70_tree=null;
        GrammarAST ACTION71_tree=null;
        RewriteRuleTokenStream stream_CATCH=new RewriteRuleTokenStream(adaptor,"token CATCH");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

        try {
            // ANTLRParser.g:468:2: ( CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) )
            // ANTLRParser.g:468:4: CATCH ARG_ACTION ACTION
            {
            CATCH69=(Token)match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler2050); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CATCH.add(CATCH69);

            ARG_ACTION70=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler2052); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION70);

            ACTION71=(Token)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler2054); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION71);



            // AST REWRITE
            // elements: ARG_ACTION, CATCH, ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 468:28: -> ^( CATCH ARG_ACTION ACTION )
            {
                // ANTLRParser.g:468:31: ^( CATCH ARG_ACTION ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_CATCH.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());
                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "exceptionHandler"

    public static class finallyClause_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "finallyClause"
    // ANTLRParser.g:473:1: finallyClause : FINALLY ACTION -> ^( FINALLY ACTION ) ;
    public final ANTLRParser.finallyClause_return finallyClause() throws RecognitionException {
        ANTLRParser.finallyClause_return retval = new ANTLRParser.finallyClause_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token FINALLY72=null;
        Token ACTION73=null;

        GrammarAST FINALLY72_tree=null;
        GrammarAST ACTION73_tree=null;
        RewriteRuleTokenStream stream_FINALLY=new RewriteRuleTokenStream(adaptor,"token FINALLY");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");

        try {
            // ANTLRParser.g:474:2: ( FINALLY ACTION -> ^( FINALLY ACTION ) )
            // ANTLRParser.g:474:4: FINALLY ACTION
            {
            FINALLY72=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause2080); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FINALLY.add(FINALLY72);

            ACTION73=(Token)match(input,ACTION,FOLLOW_ACTION_in_finallyClause2082); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION73);



            // AST REWRITE
            // elements: ACTION, FINALLY
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 474:19: -> ^( FINALLY ACTION )
            {
                // ANTLRParser.g:474:22: ^( FINALLY ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_FINALLY.nextNode(), root_1);

                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "finallyClause"

    public static class rulePrequels_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rulePrequels"
    // ANTLRParser.g:477:1: rulePrequels : sync ( rulePrequel sync )* -> ( rulePrequel )* ;
    public final ANTLRParser.rulePrequels_return rulePrequels() throws RecognitionException {
        ANTLRParser.rulePrequels_return retval = new ANTLRParser.rulePrequels_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.sync_return sync74 = null;

        ANTLRParser.rulePrequel_return rulePrequel75 = null;

        ANTLRParser.sync_return sync76 = null;


        RewriteRuleSubtreeStream stream_rulePrequel=new RewriteRuleSubtreeStream(adaptor,"rule rulePrequel");
        RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
         paraphrases.push("matching rule preamble"); 
        try {
            // ANTLRParser.g:480:2: ( sync ( rulePrequel sync )* -> ( rulePrequel )* )
            // ANTLRParser.g:480:4: sync ( rulePrequel sync )*
            {
            pushFollow(FOLLOW_sync_in_rulePrequels2116);
            sync74=sync();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sync.add(sync74.getTree());
            // ANTLRParser.g:480:9: ( rulePrequel sync )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==OPTIONS||LA21_0==SCOPE||LA21_0==THROWS||LA21_0==AT) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ANTLRParser.g:480:10: rulePrequel sync
            	    {
            	    pushFollow(FOLLOW_rulePrequel_in_rulePrequels2119);
            	    rulePrequel75=rulePrequel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rulePrequel.add(rulePrequel75.getTree());
            	    pushFollow(FOLLOW_sync_in_rulePrequels2121);
            	    sync76=sync();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sync.add(sync76.getTree());

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);



            // AST REWRITE
            // elements: rulePrequel
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 480:29: -> ( rulePrequel )*
            {
                // ANTLRParser.g:480:32: ( rulePrequel )*
                while ( stream_rulePrequel.hasNext() ) {
                    adaptor.addChild(root_0, stream_rulePrequel.nextTree());

                }
                stream_rulePrequel.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rulePrequels"

    public static class rulePrequel_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rulePrequel"
    // ANTLRParser.g:486:1: rulePrequel : ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction );
    public final ANTLRParser.rulePrequel_return rulePrequel() throws RecognitionException {
        ANTLRParser.rulePrequel_return retval = new ANTLRParser.rulePrequel_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.throwsSpec_return throwsSpec77 = null;

        ANTLRParser.ruleScopeSpec_return ruleScopeSpec78 = null;

        ANTLRParser.optionsSpec_return optionsSpec79 = null;

        ANTLRParser.ruleAction_return ruleAction80 = null;



        try {
            // ANTLRParser.g:487:5: ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction )
            int alt22=4;
            switch ( input.LA(1) ) {
            case THROWS:
                {
                alt22=1;
                }
                break;
            case SCOPE:
                {
                alt22=2;
                }
                break;
            case OPTIONS:
                {
                alt22=3;
                }
                break;
            case AT:
                {
                alt22=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // ANTLRParser.g:487:7: throwsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_throwsSpec_in_rulePrequel2147);
                    throwsSpec77=throwsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, throwsSpec77.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:488:7: ruleScopeSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleScopeSpec_in_rulePrequel2155);
                    ruleScopeSpec78=ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleScopeSpec78.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:489:7: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_rulePrequel2163);
                    optionsSpec79=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec79.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:490:7: ruleAction
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleAction_in_rulePrequel2171);
                    ruleAction80=ruleAction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleAction80.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rulePrequel"

    public static class ruleReturns_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleReturns"
    // ANTLRParser.g:499:1: ruleReturns : RETURNS ARG_ACTION ;
    public final ANTLRParser.ruleReturns_return ruleReturns() throws RecognitionException {
        ANTLRParser.ruleReturns_return retval = new ANTLRParser.ruleReturns_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RETURNS81=null;
        Token ARG_ACTION82=null;

        GrammarAST RETURNS81_tree=null;
        GrammarAST ARG_ACTION82_tree=null;

        try {
            // ANTLRParser.g:500:2: ( RETURNS ARG_ACTION )
            // ANTLRParser.g:500:4: RETURNS ARG_ACTION
            {
            root_0 = (GrammarAST)adaptor.nil();

            RETURNS81=(Token)match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns2191); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RETURNS81_tree = (GrammarAST)adaptor.create(RETURNS81);
            root_0 = (GrammarAST)adaptor.becomeRoot(RETURNS81_tree, root_0);
            }
            ARG_ACTION82=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns2194); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ARG_ACTION82_tree = (GrammarAST)adaptor.create(ARG_ACTION82);
            adaptor.addChild(root_0, ARG_ACTION82_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleReturns"

    public static class throwsSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "throwsSpec"
    // ANTLRParser.g:514:1: throwsSpec : THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) ;
    public final ANTLRParser.throwsSpec_return throwsSpec() throws RecognitionException {
        ANTLRParser.throwsSpec_return retval = new ANTLRParser.throwsSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token THROWS83=null;
        Token COMMA85=null;
        ANTLRParser.qid_return qid84 = null;

        ANTLRParser.qid_return qid86 = null;


        GrammarAST THROWS83_tree=null;
        GrammarAST COMMA85_tree=null;
        RewriteRuleTokenStream stream_THROWS=new RewriteRuleTokenStream(adaptor,"token THROWS");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // ANTLRParser.g:515:5: ( THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) )
            // ANTLRParser.g:515:7: THROWS qid ( COMMA qid )*
            {
            THROWS83=(Token)match(input,THROWS,FOLLOW_THROWS_in_throwsSpec2219); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_THROWS.add(THROWS83);

            pushFollow(FOLLOW_qid_in_throwsSpec2221);
            qid84=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid84.getTree());
            // ANTLRParser.g:515:18: ( COMMA qid )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==COMMA) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ANTLRParser.g:515:19: COMMA qid
            	    {
            	    COMMA85=(Token)match(input,COMMA,FOLLOW_COMMA_in_throwsSpec2224); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA85);

            	    pushFollow(FOLLOW_qid_in_throwsSpec2226);
            	    qid86=qid();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_qid.add(qid86.getTree());

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);



            // AST REWRITE
            // elements: qid, THROWS
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 515:31: -> ^( THROWS ( qid )+ )
            {
                // ANTLRParser.g:515:34: ^( THROWS ( qid )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_THROWS.nextNode(), root_1);

                if ( !(stream_qid.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_qid.hasNext() ) {
                    adaptor.addChild(root_1, stream_qid.nextTree());

                }
                stream_qid.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "throwsSpec"

    public static class ruleScopeSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleScopeSpec"
    // ANTLRParser.g:522:1: ruleScopeSpec : ( SCOPE ACTION -> ^( SCOPE ACTION ) | SCOPE id ( COMMA id )* SEMI -> ^( SCOPE ( id )+ ) );
    public final ANTLRParser.ruleScopeSpec_return ruleScopeSpec() throws RecognitionException {
        ANTLRParser.ruleScopeSpec_return retval = new ANTLRParser.ruleScopeSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token SCOPE87=null;
        Token ACTION88=null;
        Token SCOPE89=null;
        Token COMMA91=null;
        Token SEMI93=null;
        ANTLRParser.id_return id90 = null;

        ANTLRParser.id_return id92 = null;


        GrammarAST SCOPE87_tree=null;
        GrammarAST ACTION88_tree=null;
        GrammarAST SCOPE89_tree=null;
        GrammarAST COMMA91_tree=null;
        GrammarAST SEMI93_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:523:2: ( SCOPE ACTION -> ^( SCOPE ACTION ) | SCOPE id ( COMMA id )* SEMI -> ^( SCOPE ( id )+ ) )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SCOPE) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==ACTION) ) {
                    alt25=1;
                }
                else if ( (LA25_1==TEMPLATE||(LA25_1>=TOKEN_REF && LA25_1<=RULE_REF)) ) {
                    alt25=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // ANTLRParser.g:523:4: SCOPE ACTION
                    {
                    SCOPE87=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2257); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE87);

                    ACTION88=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec2259); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ACTION.add(ACTION88);



                    // AST REWRITE
                    // elements: ACTION, SCOPE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 523:17: -> ^( SCOPE ACTION )
                    {
                        // ANTLRParser.g:523:20: ^( SCOPE ACTION )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_SCOPE.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_ACTION.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:524:4: SCOPE id ( COMMA id )* SEMI
                    {
                    SCOPE89=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2272); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE89);

                    pushFollow(FOLLOW_id_in_ruleScopeSpec2274);
                    id90=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id90.getTree());
                    // ANTLRParser.g:524:13: ( COMMA id )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==COMMA) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // ANTLRParser.g:524:14: COMMA id
                    	    {
                    	    COMMA91=(Token)match(input,COMMA,FOLLOW_COMMA_in_ruleScopeSpec2277); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA91);

                    	    pushFollow(FOLLOW_id_in_ruleScopeSpec2279);
                    	    id92=id();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_id.add(id92.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);

                    SEMI93=(Token)match(input,SEMI,FOLLOW_SEMI_in_ruleScopeSpec2283); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI93);



                    // AST REWRITE
                    // elements: id, SCOPE
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 524:30: -> ^( SCOPE ( id )+ )
                    {
                        // ANTLRParser.g:524:33: ^( SCOPE ( id )+ )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_SCOPE.nextNode(), root_1);

                        if ( !(stream_id.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_id.hasNext() ) {
                            adaptor.addChild(root_1, stream_id.nextTree());

                        }
                        stream_id.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleScopeSpec"

    public static class ruleAction_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleAction"
    // ANTLRParser.g:535:1: ruleAction : AT id ACTION -> ^( AT id ACTION ) ;
    public final ANTLRParser.ruleAction_return ruleAction() throws RecognitionException {
        ANTLRParser.ruleAction_return retval = new ANTLRParser.ruleAction_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token AT94=null;
        Token ACTION96=null;
        ANTLRParser.id_return id95 = null;


        GrammarAST AT94_tree=null;
        GrammarAST ACTION96_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:537:2: ( AT id ACTION -> ^( AT id ACTION ) )
            // ANTLRParser.g:537:4: AT id ACTION
            {
            AT94=(Token)match(input,AT,FOLLOW_AT_in_ruleAction2313); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT94);

            pushFollow(FOLLOW_id_in_ruleAction2315);
            id95=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id95.getTree());
            ACTION96=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleAction2317); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION96);



            // AST REWRITE
            // elements: AT, id, ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 537:17: -> ^( AT id ACTION )
            {
                // ANTLRParser.g:537:20: ^( AT id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleAction"

    public static class ruleModifiers_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleModifiers"
    // ANTLRParser.g:545:1: ruleModifiers : ( ruleModifier )+ -> ^( RULEMODIFIERS ( ruleModifier )+ ) ;
    public final ANTLRParser.ruleModifiers_return ruleModifiers() throws RecognitionException {
        ANTLRParser.ruleModifiers_return retval = new ANTLRParser.ruleModifiers_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.ruleModifier_return ruleModifier97 = null;


        RewriteRuleSubtreeStream stream_ruleModifier=new RewriteRuleSubtreeStream(adaptor,"rule ruleModifier");
        try {
            // ANTLRParser.g:546:5: ( ( ruleModifier )+ -> ^( RULEMODIFIERS ( ruleModifier )+ ) )
            // ANTLRParser.g:546:7: ( ruleModifier )+
            {
            // ANTLRParser.g:546:7: ( ruleModifier )+
            int cnt26=0;
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==FRAGMENT||(LA26_0>=PROTECTED && LA26_0<=PRIVATE)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ANTLRParser.g:546:7: ruleModifier
            	    {
            	    pushFollow(FOLLOW_ruleModifier_in_ruleModifiers2358);
            	    ruleModifier97=ruleModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ruleModifier.add(ruleModifier97.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt26 >= 1 ) break loop26;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(26, input);
                        throw eee;
                }
                cnt26++;
            } while (true);



            // AST REWRITE
            // elements: ruleModifier
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 546:21: -> ^( RULEMODIFIERS ( ruleModifier )+ )
            {
                // ANTLRParser.g:546:24: ^( RULEMODIFIERS ( ruleModifier )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULEMODIFIERS, "RULEMODIFIERS"), root_1);

                if ( !(stream_ruleModifier.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_ruleModifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleModifier.nextTree());

                }
                stream_ruleModifier.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleModifiers"

    public static class ruleModifier_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleModifier"
    // ANTLRParser.g:555:1: ruleModifier : ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT );
    public final ANTLRParser.ruleModifier_return ruleModifier() throws RecognitionException {
        ANTLRParser.ruleModifier_return retval = new ANTLRParser.ruleModifier_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set98=null;

        GrammarAST set98_tree=null;

        try {
            // ANTLRParser.g:556:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set98=(Token)input.LT(1);
            if ( input.LA(1)==FRAGMENT||(input.LA(1)>=PROTECTED && input.LA(1)<=PRIVATE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set98));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleModifier"

    public static class altList_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "altList"
    // ANTLRParser.g:562:1: altList : alternative ( OR alternative )* -> ( alternative )+ ;
    public final ANTLRParser.altList_return altList() throws RecognitionException {
        ANTLRParser.altList_return retval = new ANTLRParser.altList_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token OR100=null;
        ANTLRParser.alternative_return alternative99 = null;

        ANTLRParser.alternative_return alternative101 = null;


        GrammarAST OR100_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // ANTLRParser.g:563:5: ( alternative ( OR alternative )* -> ( alternative )+ )
            // ANTLRParser.g:563:7: alternative ( OR alternative )*
            {
            pushFollow(FOLLOW_alternative_in_altList2434);
            alternative99=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_alternative.add(alternative99.getTree());
            // ANTLRParser.g:563:19: ( OR alternative )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==OR) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ANTLRParser.g:563:20: OR alternative
            	    {
            	    OR100=(Token)match(input,OR,FOLLOW_OR_in_altList2437); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_OR.add(OR100);

            	    pushFollow(FOLLOW_alternative_in_altList2439);
            	    alternative101=alternative();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_alternative.add(alternative101.getTree());

            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);



            // AST REWRITE
            // elements: alternative
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 563:37: -> ( alternative )+
            {
                if ( !(stream_alternative.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_alternative.hasNext() ) {
                    adaptor.addChild(root_0, stream_alternative.nextTree());

                }
                stream_alternative.reset();

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "altList"

    public static class ruleBlock_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleBlock"
    // ANTLRParser.g:572:1: ruleBlock : altList -> ^( BLOCK altList ) ;
    public final ANTLRParser.ruleBlock_return ruleBlock() throws RecognitionException {
        ANTLRParser.ruleBlock_return retval = new ANTLRParser.ruleBlock_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.altList_return altList102 = null;


        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        try {
            // ANTLRParser.g:573:5: ( altList -> ^( BLOCK altList ) )
            // ANTLRParser.g:573:7: altList
            {
            pushFollow(FOLLOW_altList_in_ruleBlock2469);
            altList102=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList102.getTree());


            // AST REWRITE
            // elements: altList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 573:15: -> ^( BLOCK altList )
            {
                // ANTLRParser.g:573:18: ^( BLOCK altList )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_1);

                adaptor.addChild(root_1, stream_altList.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (ResyncToEndOfRuleBlock e) {

                	// just resyncing; ignore error
            		retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), null);			
                
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleBlock"

    public static class alternative_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "alternative"
    // ANTLRParser.g:582:1: alternative : ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) );
    public final ANTLRParser.alternative_return alternative() throws RecognitionException {
        ANTLRParser.alternative_return retval = new ANTLRParser.alternative_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.elements_return elements103 = null;

        ANTLRParser.rewrite_return rewrite104 = null;

        ANTLRParser.rewrite_return rewrite105 = null;


        RewriteRuleSubtreeStream stream_rewrite=new RewriteRuleSubtreeStream(adaptor,"rule rewrite");
        RewriteRuleSubtreeStream stream_elements=new RewriteRuleSubtreeStream(adaptor,"rule elements");
         paraphrases.push("matching alternative"); 
        try {
            // ANTLRParser.g:585:5: ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) )
            int alt29=3;
            switch ( input.LA(1) ) {
            case SEMPRED:
            case ACTION:
            case TEMPLATE:
            case LPAREN:
            case DOT:
            case TREE_BEGIN:
            case NOT:
            case TOKEN_REF:
            case RULE_REF:
            case STRING_LITERAL:
                {
                alt29=1;
                }
                break;
            case RARROW:
                {
                alt29=2;
                }
                break;
            case EOF:
            case SEMI:
            case RPAREN:
            case OR:
                {
                alt29=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // ANTLRParser.g:585:7: elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
                    {
                    pushFollow(FOLLOW_elements_in_alternative2519);
                    elements103=elements();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_elements.add(elements103.getTree());
                    // ANTLRParser.g:586:6: ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==RARROW) ) {
                        alt28=1;
                    }
                    else if ( (LA28_0==EOF||LA28_0==SEMI||LA28_0==RPAREN||LA28_0==OR) ) {
                        alt28=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 0, input);

                        throw nvae;
                    }
                    switch (alt28) {
                        case 1 :
                            // ANTLRParser.g:586:8: rewrite
                            {
                            pushFollow(FOLLOW_rewrite_in_alternative2528);
                            rewrite104=rewrite();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_rewrite.add(rewrite104.getTree());


                            // AST REWRITE
                            // elements: rewrite, elements
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 586:16: -> ^( ALT_REWRITE elements rewrite )
                            {
                                // ANTLRParser.g:586:19: ^( ALT_REWRITE elements rewrite )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT_REWRITE, "ALT_REWRITE"), root_1);

                                adaptor.addChild(root_1, stream_elements.nextTree());
                                adaptor.addChild(root_1, stream_rewrite.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:587:10: 
                            {

                            // AST REWRITE
                            // elements: elements
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 587:10: -> elements
                            {
                                adaptor.addChild(root_0, stream_elements.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:589:7: rewrite
                    {
                    pushFollow(FOLLOW_rewrite_in_alternative2566);
                    rewrite105=rewrite();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewrite.add(rewrite105.getTree());


                    // AST REWRITE
                    // elements: rewrite
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 589:16: -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                    {
                        // ANTLRParser.g:589:19: ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT_REWRITE, "ALT_REWRITE"), root_1);

                        // ANTLRParser.g:589:33: ^( ALT EPSILON )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_2);

                        adaptor.addChild(root_2, (GrammarAST)adaptor.create(EPSILON, "EPSILON"));

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, stream_rewrite.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:590:10: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 590:10: -> ^( ALT EPSILON )
                    {
                        // ANTLRParser.g:590:13: ^( ALT EPSILON )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_1);

                        adaptor.addChild(root_1, (GrammarAST)adaptor.create(EPSILON, "EPSILON"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "alternative"

    public static class elements_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elements"
    // ANTLRParser.g:593:1: elements : (e+= element )+ -> ^( ALT ( $e)+ ) ;
    public final ANTLRParser.elements_return elements() throws RecognitionException {
        ANTLRParser.elements_return retval = new ANTLRParser.elements_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        List list_e=null;
        RuleReturnScope e = null;
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:594:5: ( (e+= element )+ -> ^( ALT ( $e)+ ) )
            // ANTLRParser.g:594:7: (e+= element )+
            {
            // ANTLRParser.g:594:8: (e+= element )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==SEMPRED||LA30_0==ACTION||LA30_0==TEMPLATE||LA30_0==LPAREN||LA30_0==DOT||LA30_0==TREE_BEGIN||LA30_0==NOT||(LA30_0>=TOKEN_REF && LA30_0<=RULE_REF)||LA30_0==STRING_LITERAL) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // ANTLRParser.g:594:8: e+= element
            	    {
            	    pushFollow(FOLLOW_element_in_elements2619);
            	    e=element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_element.add(e.getTree());
            	    if (list_e==null) list_e=new ArrayList();
            	    list_e.add(e.getTree());


            	    }
            	    break;

            	default :
            	    if ( cnt30 >= 1 ) break loop30;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(30, input);
                        throw eee;
                }
                cnt30++;
            } while (true);



            // AST REWRITE
            // elements: e
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: e
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"token e",list_e);
            root_0 = (GrammarAST)adaptor.nil();
            // 594:19: -> ^( ALT ( $e)+ )
            {
                // ANTLRParser.g:594:22: ^( ALT ( $e)+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_1);

                if ( !(stream_e.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_e.hasNext() ) {
                    adaptor.addChild(root_1, stream_e.nextTree());

                }
                stream_e.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "elements"

    public static class element_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "element"
    // ANTLRParser.g:597:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec ) );
    public final ANTLRParser.element_return element() throws RecognitionException {
        ANTLRParser.element_return retval = new ANTLRParser.element_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ACTION111=null;
        Token SEMPRED112=null;
        Token IMPLIES113=null;
        ANTLRParser.labeledElement_return labeledElement106 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix107 = null;

        ANTLRParser.atom_return atom108 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix109 = null;

        ANTLRParser.ebnf_return ebnf110 = null;

        ANTLRParser.treeSpec_return treeSpec114 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix115 = null;


        GrammarAST ACTION111_tree=null;
        GrammarAST SEMPRED112_tree=null;
        GrammarAST IMPLIES113_tree=null;
        RewriteRuleTokenStream stream_IMPLIES=new RewriteRuleTokenStream(adaptor,"token IMPLIES");
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        RewriteRuleSubtreeStream stream_treeSpec=new RewriteRuleSubtreeStream(adaptor,"rule treeSpec");
        RewriteRuleSubtreeStream stream_labeledElement=new RewriteRuleSubtreeStream(adaptor,"rule labeledElement");

        	paraphrases.push("looking for rule element");
        	int m = input.mark();

        try {
            // ANTLRParser.g:603:2: ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec ) )
            int alt35=6;
            alt35 = dfa35.predict(input);
            switch (alt35) {
                case 1 :
                    // ANTLRParser.g:603:4: labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement )
                    {
                    pushFollow(FOLLOW_labeledElement_in_element2656);
                    labeledElement106=labeledElement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_labeledElement.add(labeledElement106.getTree());
                    // ANTLRParser.g:604:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement )
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==QUESTION||(LA31_0>=STAR && LA31_0<=PLUS)) ) {
                        alt31=1;
                    }
                    else if ( (LA31_0==EOF||LA31_0==SEMPRED||LA31_0==ACTION||LA31_0==TEMPLATE||(LA31_0>=SEMI && LA31_0<=RPAREN)||LA31_0==OR||LA31_0==DOT||(LA31_0>=RARROW && LA31_0<=TREE_BEGIN)||LA31_0==NOT||(LA31_0>=TOKEN_REF && LA31_0<=RULE_REF)||LA31_0==STRING_LITERAL) ) {
                        alt31=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 31, 0, input);

                        throw nvae;
                    }
                    switch (alt31) {
                        case 1 :
                            // ANTLRParser.g:604:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2662);
                            ebnfSuffix107=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix107.getTree());


                            // AST REWRITE
                            // elements: ebnfSuffix, labeledElement
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 604:16: -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) )
                            {
                                // ANTLRParser.g:604:19: ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:604:33: ^( BLOCK ^( ALT labeledElement ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_2);

                                // ANTLRParser.g:604:51: ^( ALT labeledElement )
                                {
                                GrammarAST root_3 = (GrammarAST)adaptor.nil();
                                root_3 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_3);

                                adaptor.addChild(root_3, stream_labeledElement.nextTree());

                                adaptor.addChild(root_2, root_3);
                                }

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:605:8: 
                            {

                            // AST REWRITE
                            // elements: labeledElement
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 605:8: -> labeledElement
                            {
                                adaptor.addChild(root_0, stream_labeledElement.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:607:4: atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom )
                    {
                    pushFollow(FOLLOW_atom_in_element2704);
                    atom108=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_atom.add(atom108.getTree());
                    // ANTLRParser.g:608:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom )
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==QUESTION||(LA32_0>=STAR && LA32_0<=PLUS)) ) {
                        alt32=1;
                    }
                    else if ( (LA32_0==EOF||LA32_0==SEMPRED||LA32_0==ACTION||LA32_0==TEMPLATE||(LA32_0>=SEMI && LA32_0<=RPAREN)||LA32_0==OR||LA32_0==DOT||(LA32_0>=RARROW && LA32_0<=TREE_BEGIN)||LA32_0==NOT||(LA32_0>=TOKEN_REF && LA32_0<=RULE_REF)||LA32_0==STRING_LITERAL) ) {
                        alt32=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 0, input);

                        throw nvae;
                    }
                    switch (alt32) {
                        case 1 :
                            // ANTLRParser.g:608:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2710);
                            ebnfSuffix109=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix109.getTree());


                            // AST REWRITE
                            // elements: atom, ebnfSuffix
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 608:16: -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) )
                            {
                                // ANTLRParser.g:608:19: ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:608:33: ^( BLOCK ^( ALT atom ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_2);

                                // ANTLRParser.g:608:51: ^( ALT atom )
                                {
                                GrammarAST root_3 = (GrammarAST)adaptor.nil();
                                root_3 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_3);

                                adaptor.addChild(root_3, stream_atom.nextTree());

                                adaptor.addChild(root_2, root_3);
                                }

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:609:8: 
                            {

                            // AST REWRITE
                            // elements: atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 609:8: -> atom
                            {
                                adaptor.addChild(root_0, stream_atom.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // ANTLRParser.g:611:4: ebnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnf_in_element2753);
                    ebnf110=ebnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnf110.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:612:6: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION111=(Token)match(input,ACTION,FOLLOW_ACTION_in_element2760); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION111_tree = new ActionAST(ACTION111) ;
                    adaptor.addChild(root_0, ACTION111_tree);
                    }

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:613:6: SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED )
                    {
                    SEMPRED112=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_element2770); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED112);

                    // ANTLRParser.g:614:3: ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED )
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==IMPLIES) ) {
                        alt33=1;
                    }
                    else if ( (LA33_0==EOF||LA33_0==SEMPRED||LA33_0==ACTION||LA33_0==TEMPLATE||(LA33_0>=SEMI && LA33_0<=RPAREN)||LA33_0==OR||LA33_0==DOT||(LA33_0>=RARROW && LA33_0<=TREE_BEGIN)||LA33_0==NOT||(LA33_0>=TOKEN_REF && LA33_0<=RULE_REF)||LA33_0==STRING_LITERAL) ) {
                        alt33=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 33, 0, input);

                        throw nvae;
                    }
                    switch (alt33) {
                        case 1 :
                            // ANTLRParser.g:614:5: IMPLIES
                            {
                            IMPLIES113=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_element2776); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IMPLIES.add(IMPLIES113);



                            // AST REWRITE
                            // elements: 
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 614:14: -> GATED_SEMPRED[$IMPLIES]
                            {
                                adaptor.addChild(root_0, (GrammarAST)adaptor.create(GATED_SEMPRED, IMPLIES113));

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:615:8: 
                            {

                            // AST REWRITE
                            // elements: SEMPRED
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 615:8: -> SEMPRED
                            {
                                adaptor.addChild(root_0, stream_SEMPRED.nextNode());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // ANTLRParser.g:617:6: treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec )
                    {
                    pushFollow(FOLLOW_treeSpec_in_element2804);
                    treeSpec114=treeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_treeSpec.add(treeSpec114.getTree());
                    // ANTLRParser.g:618:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec )
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==QUESTION||(LA34_0>=STAR && LA34_0<=PLUS)) ) {
                        alt34=1;
                    }
                    else if ( (LA34_0==EOF||LA34_0==SEMPRED||LA34_0==ACTION||LA34_0==TEMPLATE||(LA34_0>=SEMI && LA34_0<=RPAREN)||LA34_0==OR||LA34_0==DOT||(LA34_0>=RARROW && LA34_0<=TREE_BEGIN)||LA34_0==NOT||(LA34_0>=TOKEN_REF && LA34_0<=RULE_REF)||LA34_0==STRING_LITERAL) ) {
                        alt34=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 34, 0, input);

                        throw nvae;
                    }
                    switch (alt34) {
                        case 1 :
                            // ANTLRParser.g:618:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2810);
                            ebnfSuffix115=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix115.getTree());


                            // AST REWRITE
                            // elements: treeSpec, ebnfSuffix
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 618:16: -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) )
                            {
                                // ANTLRParser.g:618:19: ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:618:33: ^( BLOCK ^( ALT treeSpec ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_2);

                                // ANTLRParser.g:618:51: ^( ALT treeSpec )
                                {
                                GrammarAST root_3 = (GrammarAST)adaptor.nil();
                                root_3 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_3);

                                adaptor.addChild(root_3, stream_treeSpec.nextTree());

                                adaptor.addChild(root_2, root_3);
                                }

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:619:8: 
                            {

                            // AST REWRITE
                            // elements: treeSpec
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 619:8: -> treeSpec
                            {
                                adaptor.addChild(root_0, stream_treeSpec.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {

                	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
                	int ttype = input.get(input.range()).getType();
            	    // look for anything that really belongs at the start of the rule minus the initial ID
                	if ( ttype==COLON || ttype==RETURNS || ttype==CATCH || ttype==FINALLY || ttype==AT ) {
            			RecognitionException missingSemi =
            				new v4ParserException("unterminated rule (missing ';') detected at '"+
            									  input.LT(1).getText()+" "+input.LT(2).getText()+"'", input);
            			reportError(missingSemi);
            			if ( ttype==CATCH || ttype==FINALLY ) {
            				input.seek(input.range()); // ignore what's before rule trailer stuff
            			}
            			if ( ttype==RETURNS || ttype==AT ) { // scan back looking for ID of rule header
            				int p = input.index();
            				Token t = input.get(p);
            				while ( t.getType()!=RULE_REF && t.getType()!=TOKEN_REF ) {
            					p--;
            					t = input.get(p);
            				}
            				input.seek(p);
            			}
            			throw new ResyncToEndOfRuleBlock(); // make sure it goes back to rule block level to recover
            		}
                    reportError(re);
                    recover(input,re);
            /*
            		input.rewind(m);
            		final List subset = input.get(input.index(), input.range());
            		System.out.println("failed to match as element: '"+subset);
            		CommonTokenStream ns = new CommonTokenStream(
            			new TokenSource() {
            				int i = 0;
            				public Token nextToken() {
            					if ( i>=subset.size() ) return Token.EOF_TOKEN;
            					return (Token)subset.get(i++);
            				}
            				public String getSourceName() { return null; }
            			});
            		ANTLRParser errorParser = new ANTLRParser(ns);
            		errorParser.setTreeAdaptor(this.adaptor);
            		errorParser.element_errors(re);
                    retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
                    */
            	
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "element"

    public static class labeledElement_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "labeledElement"
    // ANTLRParser.g:693:1: labeledElement : id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) ;
    public final ANTLRParser.labeledElement_return labeledElement() throws RecognitionException {
        ANTLRParser.labeledElement_return retval = new ANTLRParser.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN117=null;
        Token PLUS_ASSIGN118=null;
        ANTLRParser.id_return id116 = null;

        ANTLRParser.atom_return atom119 = null;

        ANTLRParser.block_return block120 = null;


        GrammarAST ASSIGN117_tree=null;
        GrammarAST PLUS_ASSIGN118_tree=null;

        try {
            // ANTLRParser.g:693:16: ( id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) )
            // ANTLRParser.g:693:18: id ( ASSIGN | PLUS_ASSIGN ) ( atom | block )
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_labeledElement2871);
            id116=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id116.getTree());
            // ANTLRParser.g:693:21: ( ASSIGN | PLUS_ASSIGN )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==ASSIGN) ) {
                alt36=1;
            }
            else if ( (LA36_0==PLUS_ASSIGN) ) {
                alt36=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // ANTLRParser.g:693:22: ASSIGN
                    {
                    ASSIGN117=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement2874); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN117_tree = (GrammarAST)adaptor.create(ASSIGN117);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN117_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:693:30: PLUS_ASSIGN
                    {
                    PLUS_ASSIGN118=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement2877); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS_ASSIGN118_tree = (GrammarAST)adaptor.create(PLUS_ASSIGN118);
                    root_0 = (GrammarAST)adaptor.becomeRoot(PLUS_ASSIGN118_tree, root_0);
                    }

                    }
                    break;

            }

            // ANTLRParser.g:693:44: ( atom | block )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==TEMPLATE||LA37_0==DOT||LA37_0==NOT||(LA37_0>=TOKEN_REF && LA37_0<=RULE_REF)||LA37_0==STRING_LITERAL) ) {
                alt37=1;
            }
            else if ( (LA37_0==LPAREN) ) {
                alt37=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // ANTLRParser.g:693:45: atom
                    {
                    pushFollow(FOLLOW_atom_in_labeledElement2882);
                    atom119=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom119.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:693:50: block
                    {
                    pushFollow(FOLLOW_block_in_labeledElement2884);
                    block120=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block120.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "labeledElement"

    public static class treeSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "treeSpec"
    // ANTLRParser.g:699:1: treeSpec : TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) ;
    public final ANTLRParser.treeSpec_return treeSpec() throws RecognitionException {
        ANTLRParser.treeSpec_return retval = new ANTLRParser.treeSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN121=null;
        Token RPAREN124=null;
        ANTLRParser.element_return element122 = null;

        ANTLRParser.element_return element123 = null;


        GrammarAST TREE_BEGIN121_tree=null;
        GrammarAST RPAREN124_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:700:5: ( TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) )
            // ANTLRParser.g:700:7: TREE_BEGIN element ( element )+ RPAREN
            {
            TREE_BEGIN121=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec2902); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN121);

            pushFollow(FOLLOW_element_in_treeSpec2943);
            element122=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_element.add(element122.getTree());
            // ANTLRParser.g:707:10: ( element )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==SEMPRED||LA38_0==ACTION||LA38_0==TEMPLATE||LA38_0==LPAREN||LA38_0==DOT||LA38_0==TREE_BEGIN||LA38_0==NOT||(LA38_0>=TOKEN_REF && LA38_0<=RULE_REF)||LA38_0==STRING_LITERAL) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // ANTLRParser.g:707:10: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec2974);
            	    element123=element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_element.add(element123.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt38 >= 1 ) break loop38;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(38, input);
                        throw eee;
                }
                cnt38++;
            } while (true);

            RPAREN124=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_treeSpec2983); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN124);



            // AST REWRITE
            // elements: TREE_BEGIN, element
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 709:7: -> ^( TREE_BEGIN ( element )+ )
            {
                // ANTLRParser.g:709:10: ^( TREE_BEGIN ( element )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_TREE_BEGIN.nextNode(), root_1);

                if ( !(stream_element.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_element.hasNext() ) {
                    adaptor.addChild(root_1, stream_element.nextTree());

                }
                stream_element.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "treeSpec"

    public static class ebnf_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ebnf"
    // ANTLRParser.g:714:1: ebnf : block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) ;
    public final ANTLRParser.ebnf_return ebnf() throws RecognitionException {
        ANTLRParser.ebnf_return retval = new ANTLRParser.ebnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.block_return block125 = null;

        ANTLRParser.blockSuffixe_return blockSuffixe126 = null;


        RewriteRuleSubtreeStream stream_blockSuffixe=new RewriteRuleSubtreeStream(adaptor,"rule blockSuffixe");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:715:5: ( block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) )
            // ANTLRParser.g:715:7: block ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
            {
            pushFollow(FOLLOW_block_in_ebnf3017);
            block125=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_block.add(block125.getTree());
            // ANTLRParser.g:719:7: ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==IMPLIES||(LA39_0>=QUESTION && LA39_0<=PLUS)||LA39_0==ROOT) ) {
                alt39=1;
            }
            else if ( (LA39_0==EOF||LA39_0==SEMPRED||LA39_0==ACTION||LA39_0==TEMPLATE||(LA39_0>=SEMI && LA39_0<=RPAREN)||LA39_0==OR||LA39_0==DOT||(LA39_0>=RARROW && LA39_0<=TREE_BEGIN)||LA39_0==NOT||(LA39_0>=TOKEN_REF && LA39_0<=RULE_REF)||LA39_0==STRING_LITERAL) ) {
                alt39=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // ANTLRParser.g:719:9: blockSuffixe
                    {
                    pushFollow(FOLLOW_blockSuffixe_in_ebnf3052);
                    blockSuffixe126=blockSuffixe();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_blockSuffixe.add(blockSuffixe126.getTree());


                    // AST REWRITE
                    // elements: block, blockSuffixe
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 719:22: -> ^( blockSuffixe block )
                    {
                        // ANTLRParser.g:719:25: ^( blockSuffixe block )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_blockSuffixe.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:720:13: 
                    {

                    // AST REWRITE
                    // elements: block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 720:13: -> block
                    {
                        adaptor.addChild(root_0, stream_block.nextTree());

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ebnf"

    public static class blockSuffixe_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blockSuffixe"
    // ANTLRParser.g:726:1: blockSuffixe : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final ANTLRParser.blockSuffixe_return blockSuffixe() throws RecognitionException {
        ANTLRParser.blockSuffixe_return retval = new ANTLRParser.blockSuffixe_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ROOT128=null;
        Token IMPLIES129=null;
        Token BANG130=null;
        ANTLRParser.ebnfSuffix_return ebnfSuffix127 = null;


        GrammarAST ROOT128_tree=null;
        GrammarAST IMPLIES129_tree=null;
        GrammarAST BANG130_tree=null;

        try {
            // ANTLRParser.g:727:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
            int alt40=4;
            switch ( input.LA(1) ) {
            case QUESTION:
            case STAR:
            case PLUS:
                {
                alt40=1;
                }
                break;
            case ROOT:
                {
                alt40=2;
                }
                break;
            case IMPLIES:
                {
                alt40=3;
                }
                break;
            case BANG:
                {
                alt40=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // ANTLRParser.g:727:7: ebnfSuffix
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffixe3103);
                    ebnfSuffix127=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnfSuffix127.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:730:7: ROOT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ROOT128=(Token)match(input,ROOT,FOLLOW_ROOT_in_blockSuffixe3117); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ROOT128_tree = (GrammarAST)adaptor.create(ROOT128);
                    adaptor.addChild(root_0, ROOT128_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:731:7: IMPLIES
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    IMPLIES129=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffixe3125); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IMPLIES129_tree = (GrammarAST)adaptor.create(IMPLIES129);
                    adaptor.addChild(root_0, IMPLIES129_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:732:7: BANG
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    BANG130=(Token)match(input,BANG,FOLLOW_BANG_in_blockSuffixe3136); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BANG130_tree = (GrammarAST)adaptor.create(BANG130);
                    adaptor.addChild(root_0, BANG130_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "blockSuffixe"

    public static class ebnfSuffix_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ebnfSuffix"
    // ANTLRParser.g:735:1: ebnfSuffix : ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] );
    public final ANTLRParser.ebnfSuffix_return ebnfSuffix() throws RecognitionException {
        ANTLRParser.ebnfSuffix_return retval = new ANTLRParser.ebnfSuffix_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token QUESTION131=null;
        Token STAR132=null;
        Token PLUS133=null;

        GrammarAST QUESTION131_tree=null;
        GrammarAST STAR132_tree=null;
        GrammarAST PLUS133_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");


        	Token op = input.LT(1);

        try {
            // ANTLRParser.g:739:2: ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] )
            int alt41=3;
            switch ( input.LA(1) ) {
            case QUESTION:
                {
                alt41=1;
                }
                break;
            case STAR:
                {
                alt41=2;
                }
                break;
            case PLUS:
                {
                alt41=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // ANTLRParser.g:739:4: QUESTION
                    {
                    QUESTION131=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3155); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_QUESTION.add(QUESTION131);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 739:13: -> OPTIONAL[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(OPTIONAL, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:740:6: STAR
                    {
                    STAR132=(Token)match(input,STAR,FOLLOW_STAR_in_ebnfSuffix3167); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STAR.add(STAR132);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 740:13: -> CLOSURE[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(CLOSURE, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:741:7: PLUS
                    {
                    PLUS133=(Token)match(input,PLUS,FOLLOW_PLUS_in_ebnfSuffix3182); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS133);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 741:14: -> POSITIVE_CLOSURE[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(POSITIVE_CLOSURE, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ebnfSuffix"

    public static class atom_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // ANTLRParser.g:744:1: atom : ( range ( ROOT | BANG )? | {...}? id DOT ruleref -> ^( DOT id ruleref ) | {...}? id DOT terminal -> ^( DOT id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? );
    public final ANTLRParser.atom_return atom() throws RecognitionException {
        ANTLRParser.atom_return retval = new ANTLRParser.atom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ROOT135=null;
        Token BANG136=null;
        Token DOT138=null;
        Token DOT141=null;
        Token ROOT146=null;
        Token BANG147=null;
        ANTLRParser.range_return range134 = null;

        ANTLRParser.id_return id137 = null;

        ANTLRParser.ruleref_return ruleref139 = null;

        ANTLRParser.id_return id140 = null;

        ANTLRParser.terminal_return terminal142 = null;

        ANTLRParser.terminal_return terminal143 = null;

        ANTLRParser.ruleref_return ruleref144 = null;

        ANTLRParser.notSet_return notSet145 = null;


        GrammarAST ROOT135_tree=null;
        GrammarAST BANG136_tree=null;
        GrammarAST DOT138_tree=null;
        GrammarAST DOT141_tree=null;
        GrammarAST ROOT146_tree=null;
        GrammarAST BANG147_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_terminal=new RewriteRuleSubtreeStream(adaptor,"rule terminal");
        RewriteRuleSubtreeStream stream_ruleref=new RewriteRuleSubtreeStream(adaptor,"rule ruleref");
        try {
            // ANTLRParser.g:744:5: ( range ( ROOT | BANG )? | {...}? id DOT ruleref -> ^( DOT id ruleref ) | {...}? id DOT terminal -> ^( DOT id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? )
            int alt44=6;
            alt44 = dfa44.predict(input);
            switch (alt44) {
                case 1 :
                    // ANTLRParser.g:744:7: range ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_range_in_atom3199);
                    range134=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range134.getTree());
                    // ANTLRParser.g:744:13: ( ROOT | BANG )?
                    int alt42=3;
                    int LA42_0 = input.LA(1);

                    if ( (LA42_0==ROOT) ) {
                        alt42=1;
                    }
                    else if ( (LA42_0==BANG) ) {
                        alt42=2;
                    }
                    switch (alt42) {
                        case 1 :
                            // ANTLRParser.g:744:14: ROOT
                            {
                            ROOT135=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3202); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT135_tree = (GrammarAST)adaptor.create(ROOT135);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT135_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:744:22: BANG
                            {
                            BANG136=(Token)match(input,BANG,FOLLOW_BANG_in_atom3207); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG136_tree = (GrammarAST)adaptor.create(BANG136);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG136_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:750:6: {...}? id DOT ruleref
                    {
                    if ( !((
                    	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                    	        input.LT(2).getCharPositionInLine() &&
                    	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                    	    )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atom", "\n\t    \tinput.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==\n\t        input.LT(2).getCharPositionInLine() &&\n\t        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()\n\t    ");
                    }
                    pushFollow(FOLLOW_id_in_atom3254);
                    id137=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id137.getTree());
                    DOT138=(Token)match(input,DOT,FOLLOW_DOT_in_atom3256); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT138);

                    pushFollow(FOLLOW_ruleref_in_atom3258);
                    ruleref139=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleref.add(ruleref139.getTree());


                    // AST REWRITE
                    // elements: ruleref, id, DOT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 756:6: -> ^( DOT id ruleref )
                    {
                        // ANTLRParser.g:756:9: ^( DOT id ruleref )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_DOT.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_id.nextTree());
                        adaptor.addChild(root_1, stream_ruleref.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:758:6: {...}? id DOT terminal
                    {
                    if ( !((
                    	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                    	        input.LT(2).getCharPositionInLine() &&
                    	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                    	    )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atom", "\n\t    \tinput.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==\n\t        input.LT(2).getCharPositionInLine() &&\n\t        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()\n\t    ");
                    }
                    pushFollow(FOLLOW_id_in_atom3292);
                    id140=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id140.getTree());
                    DOT141=(Token)match(input,DOT,FOLLOW_DOT_in_atom3294); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT141);

                    pushFollow(FOLLOW_terminal_in_atom3296);
                    terminal142=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_terminal.add(terminal142.getTree());


                    // AST REWRITE
                    // elements: id, terminal, DOT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 764:6: -> ^( DOT id terminal )
                    {
                        // ANTLRParser.g:764:9: ^( DOT id terminal )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_DOT.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_id.nextTree());
                        adaptor.addChild(root_1, stream_terminal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // ANTLRParser.g:765:9: terminal
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_terminal_in_atom3321);
                    terminal143=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, terminal143.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:766:9: ruleref
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleref_in_atom3331);
                    ruleref144=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleref144.getTree());

                    }
                    break;
                case 6 :
                    // ANTLRParser.g:767:7: notSet ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_notSet_in_atom3339);
                    notSet145=notSet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, notSet145.getTree());
                    // ANTLRParser.g:767:14: ( ROOT | BANG )?
                    int alt43=3;
                    int LA43_0 = input.LA(1);

                    if ( (LA43_0==ROOT) ) {
                        alt43=1;
                    }
                    else if ( (LA43_0==BANG) ) {
                        alt43=2;
                    }
                    switch (alt43) {
                        case 1 :
                            // ANTLRParser.g:767:15: ROOT
                            {
                            ROOT146=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3342); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT146_tree = (GrammarAST)adaptor.create(ROOT146);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT146_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:767:21: BANG
                            {
                            BANG147=(Token)match(input,BANG,FOLLOW_BANG_in_atom3345); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG147_tree = (GrammarAST)adaptor.create(BANG147);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG147_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
             throw re; 
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "atom"

    public static class notSet_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "notSet"
    // ANTLRParser.g:777:1: notSet : ( NOT notTerminal -> ^( NOT notTerminal ) | NOT block -> ^( NOT block ) );
    public final ANTLRParser.notSet_return notSet() throws RecognitionException {
        ANTLRParser.notSet_return retval = new ANTLRParser.notSet_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token NOT148=null;
        Token NOT150=null;
        ANTLRParser.notTerminal_return notTerminal149 = null;

        ANTLRParser.block_return block151 = null;


        GrammarAST NOT148_tree=null;
        GrammarAST NOT150_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleSubtreeStream stream_notTerminal=new RewriteRuleSubtreeStream(adaptor,"rule notTerminal");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:778:5: ( NOT notTerminal -> ^( NOT notTerminal ) | NOT block -> ^( NOT block ) )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==NOT) ) {
                int LA45_1 = input.LA(2);

                if ( (LA45_1==TOKEN_REF||LA45_1==STRING_LITERAL) ) {
                    alt45=1;
                }
                else if ( (LA45_1==LPAREN) ) {
                    alt45=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 45, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // ANTLRParser.g:778:7: NOT notTerminal
                    {
                    NOT148=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3385); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT148);

                    pushFollow(FOLLOW_notTerminal_in_notSet3387);
                    notTerminal149=notTerminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_notTerminal.add(notTerminal149.getTree());


                    // AST REWRITE
                    // elements: NOT, notTerminal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 778:23: -> ^( NOT notTerminal )
                    {
                        // ANTLRParser.g:778:26: ^( NOT notTerminal )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_NOT.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_notTerminal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:779:7: NOT block
                    {
                    NOT150=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3403); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT150);

                    pushFollow(FOLLOW_block_in_notSet3405);
                    block151=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_block.add(block151.getTree());


                    // AST REWRITE
                    // elements: NOT, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 779:19: -> ^( NOT block )
                    {
                        // ANTLRParser.g:779:22: ^( NOT block )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_NOT.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_block.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "notSet"

    public static class notTerminal_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "notTerminal"
    // ANTLRParser.g:788:1: notTerminal : ( TOKEN_REF | STRING_LITERAL );
    public final ANTLRParser.notTerminal_return notTerminal() throws RecognitionException {
        ANTLRParser.notTerminal_return retval = new ANTLRParser.notTerminal_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set152=null;

        GrammarAST set152_tree=null;

        try {
            // ANTLRParser.g:789:5: ( TOKEN_REF | STRING_LITERAL )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set152=(Token)input.LT(1);
            if ( input.LA(1)==TOKEN_REF||input.LA(1)==STRING_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set152));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "notTerminal"

    public static class block_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "block"
    // ANTLRParser.g:800:1: block : LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK ( optionsSpec )? ( $ra)* altList ) ;
    public final ANTLRParser.block_return block() throws RecognitionException {
        ANTLRParser.block_return retval = new ANTLRParser.block_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN153=null;
        Token COLON155=null;
        Token RPAREN157=null;
        List list_ra=null;
        ANTLRParser.optionsSpec_return optionsSpec154 = null;

        ANTLRParser.altList_return altList156 = null;

        RuleReturnScope ra = null;
        GrammarAST LPAREN153_tree=null;
        GrammarAST COLON155_tree=null;
        GrammarAST RPAREN157_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_optionsSpec=new RewriteRuleSubtreeStream(adaptor,"rule optionsSpec");
        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        RewriteRuleSubtreeStream stream_ruleAction=new RewriteRuleSubtreeStream(adaptor,"rule ruleAction");
        try {
            // ANTLRParser.g:801:5: ( LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK ( optionsSpec )? ( $ra)* altList ) )
            // ANTLRParser.g:801:7: LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN
            {
            LPAREN153=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_block3479); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN153);

            // ANTLRParser.g:804:10: ( ( optionsSpec )? (ra+= ruleAction )* COLON )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==OPTIONS||LA48_0==COLON||LA48_0==AT) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // ANTLRParser.g:804:12: ( optionsSpec )? (ra+= ruleAction )* COLON
                    {
                    // ANTLRParser.g:804:12: ( optionsSpec )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==OPTIONS) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // ANTLRParser.g:804:12: optionsSpec
                            {
                            pushFollow(FOLLOW_optionsSpec_in_block3516);
                            optionsSpec154=optionsSpec();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_optionsSpec.add(optionsSpec154.getTree());

                            }
                            break;

                    }

                    // ANTLRParser.g:804:27: (ra+= ruleAction )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==AT) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // ANTLRParser.g:804:27: ra+= ruleAction
                    	    {
                    	    pushFollow(FOLLOW_ruleAction_in_block3521);
                    	    ra=ruleAction();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_ruleAction.add(ra.getTree());
                    	    if (list_ra==null) list_ra=new ArrayList();
                    	    list_ra.add(ra.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop47;
                        }
                    } while (true);

                    COLON155=(Token)match(input,COLON,FOLLOW_COLON_in_block3524); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON155);


                    }
                    break;

            }

            pushFollow(FOLLOW_altList_in_block3538);
            altList156=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList156.getTree());
            RPAREN157=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_block3555); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN157);



            // AST REWRITE
            // elements: optionsSpec, ra, altList
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: ra
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_ra=new RewriteRuleSubtreeStream(adaptor,"token ra",list_ra);
            root_0 = (GrammarAST)adaptor.nil();
            // 807:7: -> ^( BLOCK ( optionsSpec )? ( $ra)* altList )
            {
                // ANTLRParser.g:807:10: ^( BLOCK ( optionsSpec )? ( $ra)* altList )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_1);

                // ANTLRParser.g:807:28: ( optionsSpec )?
                if ( stream_optionsSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_optionsSpec.nextTree());

                }
                stream_optionsSpec.reset();
                // ANTLRParser.g:807:41: ( $ra)*
                while ( stream_ra.hasNext() ) {
                    adaptor.addChild(root_1, stream_ra.nextTree());

                }
                stream_ra.reset();
                adaptor.addChild(root_1, stream_altList.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "block"

    public static class ruleref_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleref"
    // ANTLRParser.g:816:1: ruleref : RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) ;
    public final ANTLRParser.ruleref_return ruleref() throws RecognitionException {
        ANTLRParser.ruleref_return retval = new ANTLRParser.ruleref_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token op=null;
        Token RULE_REF158=null;
        Token ARG_ACTION159=null;

        GrammarAST op_tree=null;
        GrammarAST RULE_REF158_tree=null;
        GrammarAST ARG_ACTION159_tree=null;
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_ROOT=new RewriteRuleTokenStream(adaptor,"token ROOT");
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

        try {
            // ANTLRParser.g:817:5: ( RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) )
            // ANTLRParser.g:817:7: RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
            {
            RULE_REF158=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref3604); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF158);

            // ANTLRParser.g:817:16: ( ARG_ACTION )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==ARG_ACTION) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // ANTLRParser.g:817:16: ARG_ACTION
                    {
                    ARG_ACTION159=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref3606); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION159);


                    }
                    break;

            }

            // ANTLRParser.g:818:3: ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==BANG||LA51_0==ROOT) ) {
                alt51=1;
            }
            else if ( (LA51_0==EOF||LA51_0==SEMPRED||LA51_0==ACTION||LA51_0==TEMPLATE||(LA51_0>=SEMI && LA51_0<=RPAREN)||LA51_0==QUESTION||(LA51_0>=STAR && LA51_0<=PLUS)||LA51_0==OR||LA51_0==DOT||(LA51_0>=RARROW && LA51_0<=TREE_BEGIN)||LA51_0==NOT||(LA51_0>=TOKEN_REF && LA51_0<=RULE_REF)||LA51_0==STRING_LITERAL) ) {
                alt51=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // ANTLRParser.g:818:5: (op= ROOT | op= BANG )
                    {
                    // ANTLRParser.g:818:5: (op= ROOT | op= BANG )
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==ROOT) ) {
                        alt50=1;
                    }
                    else if ( (LA50_0==BANG) ) {
                        alt50=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 50, 0, input);

                        throw nvae;
                    }
                    switch (alt50) {
                        case 1 :
                            // ANTLRParser.g:818:6: op= ROOT
                            {
                            op=(Token)match(input,ROOT,FOLLOW_ROOT_in_ruleref3616); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ROOT.add(op);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:818:14: op= BANG
                            {
                            op=(Token)match(input,BANG,FOLLOW_BANG_in_ruleref3620); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_BANG.add(op);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ARG_ACTION, op, RULE_REF
                    // token labels: op
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_op=new RewriteRuleTokenStream(adaptor,"token op",op);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 818:23: -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                        // ANTLRParser.g:818:26: ^( $op ^( RULE_REF ( ARG_ACTION )? ) )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_op.nextNode(), root_1);

                        // ANTLRParser.g:818:32: ^( RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot(stream_RULE_REF.nextNode(), root_2);

                        // ANTLRParser.g:818:43: ( ARG_ACTION )?
                        if ( stream_ARG_ACTION.hasNext() ) {
                            adaptor.addChild(root_2, stream_ARG_ACTION.nextNode());

                        }
                        stream_ARG_ACTION.reset();

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:819:10: 
                    {

                    // AST REWRITE
                    // elements: ARG_ACTION, RULE_REF
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 819:10: -> ^( RULE_REF ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:819:13: ^( RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_RULE_REF.nextNode(), root_1);

                        // ANTLRParser.g:819:24: ( ARG_ACTION )?
                        if ( stream_ARG_ACTION.hasNext() ) {
                            adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                        }
                        stream_ARG_ACTION.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
             throw re; 
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleref"

    public static class range_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range"
    // ANTLRParser.g:833:1: range : rangeElement RANGE rangeElement ;
    public final ANTLRParser.range_return range() throws RecognitionException {
        ANTLRParser.range_return retval = new ANTLRParser.range_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RANGE161=null;
        ANTLRParser.rangeElement_return rangeElement160 = null;

        ANTLRParser.rangeElement_return rangeElement162 = null;


        GrammarAST RANGE161_tree=null;

        try {
            // ANTLRParser.g:834:5: ( rangeElement RANGE rangeElement )
            // ANTLRParser.g:834:7: rangeElement RANGE rangeElement
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_rangeElement_in_range3694);
            rangeElement160=rangeElement();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rangeElement160.getTree());
            RANGE161=(Token)match(input,RANGE,FOLLOW_RANGE_in_range3696); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RANGE161_tree = (GrammarAST)adaptor.create(RANGE161);
            root_0 = (GrammarAST)adaptor.becomeRoot(RANGE161_tree, root_0);
            }
            pushFollow(FOLLOW_rangeElement_in_range3699);
            rangeElement162=rangeElement();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rangeElement162.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "range"

    public static class rangeElement_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rangeElement"
    // ANTLRParser.g:845:1: rangeElement : ( STRING_LITERAL | RULE_REF | TOKEN_REF );
    public final ANTLRParser.rangeElement_return rangeElement() throws RecognitionException {
        ANTLRParser.rangeElement_return retval = new ANTLRParser.rangeElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set163=null;

        GrammarAST set163_tree=null;

        try {
            // ANTLRParser.g:846:5: ( STRING_LITERAL | RULE_REF | TOKEN_REF )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set163=(Token)input.LT(1);
            if ( (input.LA(1)>=TOKEN_REF && input.LA(1)<=RULE_REF)||input.LA(1)==STRING_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set163));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rangeElement"

    public static class terminal_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "terminal"
    // ANTLRParser.g:851:1: terminal : ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )? ;
    public final ANTLRParser.terminal_return terminal() throws RecognitionException {
        ANTLRParser.terminal_return retval = new ANTLRParser.terminal_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF164=null;
        Token ARG_ACTION165=null;
        Token STRING_LITERAL167=null;
        Token DOT169=null;
        Token ROOT171=null;
        Token BANG172=null;
        ANTLRParser.elementOptions_return elementOptions166 = null;

        ANTLRParser.elementOptions_return elementOptions168 = null;

        ANTLRParser.elementOptions_return elementOptions170 = null;


        GrammarAST TOKEN_REF164_tree=null;
        GrammarAST ARG_ACTION165_tree=null;
        GrammarAST STRING_LITERAL167_tree=null;
        GrammarAST DOT169_tree=null;
        GrammarAST ROOT171_tree=null;
        GrammarAST BANG172_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ROOT=new RewriteRuleTokenStream(adaptor,"token ROOT");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        try {
            // ANTLRParser.g:852:5: ( ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )? )
            // ANTLRParser.g:852:9: ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )?
            {
            // ANTLRParser.g:852:9: ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) )
            int alt56=3;
            switch ( input.LA(1) ) {
            case TOKEN_REF:
                {
                alt56=1;
                }
                break;
            case STRING_LITERAL:
                {
                alt56=2;
                }
                break;
            case DOT:
                {
                alt56=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // ANTLRParser.g:853:7: TOKEN_REF ( ARG_ACTION )? ( elementOptions )?
                    {
                    TOKEN_REF164=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal3793); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF164);

                    // ANTLRParser.g:853:17: ( ARG_ACTION )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==ARG_ACTION) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // ANTLRParser.g:853:17: ARG_ACTION
                            {
                            ARG_ACTION165=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal3795); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION165);


                            }
                            break;

                    }

                    // ANTLRParser.g:853:29: ( elementOptions )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==LT) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // ANTLRParser.g:853:29: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3798);
                            elementOptions166=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions166.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: TOKEN_REF, ARG_ACTION, elementOptions
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 853:45: -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                    {
                        // ANTLRParser.g:853:48: ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_TOKEN_REF.nextToken()), root_1);

                        // ANTLRParser.g:853:73: ( ARG_ACTION )?
                        if ( stream_ARG_ACTION.hasNext() ) {
                            adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                        }
                        stream_ARG_ACTION.reset();
                        // ANTLRParser.g:853:85: ( elementOptions )?
                        if ( stream_elementOptions.hasNext() ) {
                            adaptor.addChild(root_1, stream_elementOptions.nextTree());

                        }
                        stream_elementOptions.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:854:7: STRING_LITERAL ( elementOptions )?
                    {
                    STRING_LITERAL167=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal3822); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL167);

                    // ANTLRParser.g:854:22: ( elementOptions )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==LT) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // ANTLRParser.g:854:22: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3824);
                            elementOptions168=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions168.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: STRING_LITERAL, elementOptions
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 854:41: -> ^( STRING_LITERAL ( elementOptions )? )
                    {
                        // ANTLRParser.g:854:44: ^( STRING_LITERAL ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_STRING_LITERAL.nextToken()), root_1);

                        // ANTLRParser.g:854:74: ( elementOptions )?
                        if ( stream_elementOptions.hasNext() ) {
                            adaptor.addChild(root_1, stream_elementOptions.nextTree());

                        }
                        stream_elementOptions.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:860:7: DOT ( elementOptions )?
                    {
                    DOT169=(Token)match(input,DOT,FOLLOW_DOT_in_terminal3871); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT169);

                    // ANTLRParser.g:860:11: ( elementOptions )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==LT) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // ANTLRParser.g:860:11: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3873);
                            elementOptions170=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions170.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: elementOptions
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 860:34: -> ^( WILDCARD[$DOT] ( elementOptions )? )
                    {
                        // ANTLRParser.g:860:37: ^( WILDCARD[$DOT] ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(WILDCARD, DOT169), root_1);

                        // ANTLRParser.g:860:67: ( elementOptions )?
                        if ( stream_elementOptions.hasNext() ) {
                            adaptor.addChild(root_1, stream_elementOptions.nextTree());

                        }
                        stream_elementOptions.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }

            // ANTLRParser.g:862:3: ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )?
            int alt57=3;
            int LA57_0 = input.LA(1);

            if ( (LA57_0==ROOT) ) {
                alt57=1;
            }
            else if ( (LA57_0==BANG) ) {
                alt57=2;
            }
            switch (alt57) {
                case 1 :
                    // ANTLRParser.g:862:5: ROOT
                    {
                    ROOT171=(Token)match(input,ROOT,FOLLOW_ROOT_in_terminal3904); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ROOT.add(ROOT171);



                    // AST REWRITE
                    // elements: terminal, ROOT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 862:19: -> ^( ROOT $terminal)
                    {
                        // ANTLRParser.g:862:22: ^( ROOT $terminal)
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_ROOT.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:863:5: BANG
                    {
                    BANG172=(Token)match(input,BANG,FOLLOW_BANG_in_terminal3928); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BANG.add(BANG172);



                    // AST REWRITE
                    // elements: terminal, BANG
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 863:19: -> ^( BANG $terminal)
                    {
                        // ANTLRParser.g:863:22: ^( BANG $terminal)
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_BANG.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_retval.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "terminal"

    public static class elementOptions_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementOptions"
    // ANTLRParser.g:873:2: elementOptions : LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
    public final ANTLRParser.elementOptions_return elementOptions() throws RecognitionException {
        ANTLRParser.elementOptions_return retval = new ANTLRParser.elementOptions_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LT173=null;
        Token COMMA175=null;
        Token GT177=null;
        ANTLRParser.elementOption_return elementOption174 = null;

        ANTLRParser.elementOption_return elementOption176 = null;


        GrammarAST LT173_tree=null;
        GrammarAST COMMA175_tree=null;
        GrammarAST GT177_tree=null;
        RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
        RewriteRuleTokenStream stream_LT=new RewriteRuleTokenStream(adaptor,"token LT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_elementOption=new RewriteRuleSubtreeStream(adaptor,"rule elementOption");
        try {
            // ANTLRParser.g:874:5: ( LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) )
            // ANTLRParser.g:876:7: LT elementOption ( COMMA elementOption )* GT
            {
            LT173=(Token)match(input,LT,FOLLOW_LT_in_elementOptions3992); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LT.add(LT173);

            pushFollow(FOLLOW_elementOption_in_elementOptions3994);
            elementOption174=elementOption();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_elementOption.add(elementOption174.getTree());
            // ANTLRParser.g:876:24: ( COMMA elementOption )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==COMMA) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // ANTLRParser.g:876:25: COMMA elementOption
            	    {
            	    COMMA175=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementOptions3997); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA175);

            	    pushFollow(FOLLOW_elementOption_in_elementOptions3999);
            	    elementOption176=elementOption();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_elementOption.add(elementOption176.getTree());

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            GT177=(Token)match(input,GT,FOLLOW_GT_in_elementOptions4003); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GT.add(GT177);



            // AST REWRITE
            // elements: elementOption
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 876:50: -> ^( ELEMENT_OPTIONS ( elementOption )+ )
            {
                // ANTLRParser.g:876:53: ^( ELEMENT_OPTIONS ( elementOption )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ELEMENT_OPTIONS, "ELEMENT_OPTIONS"), root_1);

                if ( !(stream_elementOption.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_elementOption.hasNext() ) {
                    adaptor.addChild(root_1, stream_elementOption.nextTree());

                }
                stream_elementOption.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "elementOptions"

    public static class elementOption_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementOption"
    // ANTLRParser.g:881:1: elementOption : ( qid | id ASSIGN ( qid | STRING_LITERAL ) );
    public final ANTLRParser.elementOption_return elementOption() throws RecognitionException {
        ANTLRParser.elementOption_return retval = new ANTLRParser.elementOption_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN180=null;
        Token STRING_LITERAL182=null;
        ANTLRParser.qid_return qid178 = null;

        ANTLRParser.id_return id179 = null;

        ANTLRParser.qid_return qid181 = null;


        GrammarAST ASSIGN180_tree=null;
        GrammarAST STRING_LITERAL182_tree=null;

        try {
            // ANTLRParser.g:882:5: ( qid | id ASSIGN ( qid | STRING_LITERAL ) )
            int alt60=2;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                int LA60_1 = input.LA(2);

                if ( (LA60_1==COMMA||LA60_1==GT||LA60_1==DOT) ) {
                    alt60=1;
                }
                else if ( (LA60_1==ASSIGN) ) {
                    alt60=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 60, 1, input);

                    throw nvae;
                }
                }
                break;
            case TOKEN_REF:
                {
                int LA60_2 = input.LA(2);

                if ( (LA60_2==COMMA||LA60_2==GT||LA60_2==DOT) ) {
                    alt60=1;
                }
                else if ( (LA60_2==ASSIGN) ) {
                    alt60=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 60, 2, input);

                    throw nvae;
                }
                }
                break;
            case TEMPLATE:
                {
                int LA60_3 = input.LA(2);

                if ( (LA60_3==COMMA||LA60_3==GT||LA60_3==DOT) ) {
                    alt60=1;
                }
                else if ( (LA60_3==ASSIGN) ) {
                    alt60=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 60, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 60, 0, input);

                throw nvae;
            }

            switch (alt60) {
                case 1 :
                    // ANTLRParser.g:883:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_elementOption4038);
                    qid178=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid178.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:886:7: id ASSIGN ( qid | STRING_LITERAL )
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_elementOption4060);
                    id179=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id179.getTree());
                    ASSIGN180=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption4062); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN180_tree = (GrammarAST)adaptor.create(ASSIGN180);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN180_tree, root_0);
                    }
                    // ANTLRParser.g:886:18: ( qid | STRING_LITERAL )
                    int alt59=2;
                    int LA59_0 = input.LA(1);

                    if ( (LA59_0==TEMPLATE||(LA59_0>=TOKEN_REF && LA59_0<=RULE_REF)) ) {
                        alt59=1;
                    }
                    else if ( (LA59_0==STRING_LITERAL) ) {
                        alt59=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 59, 0, input);

                        throw nvae;
                    }
                    switch (alt59) {
                        case 1 :
                            // ANTLRParser.g:886:19: qid
                            {
                            pushFollow(FOLLOW_qid_in_elementOption4066);
                            qid181=qid();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qid181.getTree());

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:886:25: STRING_LITERAL
                            {
                            STRING_LITERAL182=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption4070); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            STRING_LITERAL182_tree = new TerminalAST(STRING_LITERAL182) ;
                            adaptor.addChild(root_0, STRING_LITERAL182_tree);
                            }

                            }
                            break;

                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "elementOption"

    public static class rewrite_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewrite"
    // ANTLRParser.g:889:1: rewrite : ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite ;
    public final ANTLRParser.rewrite_return rewrite() throws RecognitionException {
        ANTLRParser.rewrite_return retval = new ANTLRParser.rewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.predicatedRewrite_return predicatedRewrite183 = null;

        ANTLRParser.nakedRewrite_return nakedRewrite184 = null;


        RewriteRuleSubtreeStream stream_predicatedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule predicatedRewrite");
        RewriteRuleSubtreeStream stream_nakedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule nakedRewrite");
        try {
            // ANTLRParser.g:890:2: ( ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite )
            // ANTLRParser.g:890:4: ( predicatedRewrite )* nakedRewrite
            {
            // ANTLRParser.g:890:4: ( predicatedRewrite )*
            loop61:
            do {
                int alt61=2;
                int LA61_0 = input.LA(1);

                if ( (LA61_0==RARROW) ) {
                    int LA61_1 = input.LA(2);

                    if ( (LA61_1==SEMPRED) ) {
                        alt61=1;
                    }


                }


                switch (alt61) {
            	case 1 :
            	    // ANTLRParser.g:890:4: predicatedRewrite
            	    {
            	    pushFollow(FOLLOW_predicatedRewrite_in_rewrite4088);
            	    predicatedRewrite183=predicatedRewrite();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_predicatedRewrite.add(predicatedRewrite183.getTree());

            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);

            pushFollow(FOLLOW_nakedRewrite_in_rewrite4091);
            nakedRewrite184=nakedRewrite();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_nakedRewrite.add(nakedRewrite184.getTree());


            // AST REWRITE
            // elements: nakedRewrite, predicatedRewrite
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 890:36: -> ( predicatedRewrite )* nakedRewrite
            {
                // ANTLRParser.g:890:39: ( predicatedRewrite )*
                while ( stream_predicatedRewrite.hasNext() ) {
                    adaptor.addChild(root_0, stream_predicatedRewrite.nextTree());

                }
                stream_predicatedRewrite.reset();
                adaptor.addChild(root_0, stream_nakedRewrite.nextTree());

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewrite"

    public static class predicatedRewrite_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "predicatedRewrite"
    // ANTLRParser.g:893:1: predicatedRewrite : RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) ;
    public final ANTLRParser.predicatedRewrite_return predicatedRewrite() throws RecognitionException {
        ANTLRParser.predicatedRewrite_return retval = new ANTLRParser.predicatedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW185=null;
        Token SEMPRED186=null;
        ANTLRParser.rewriteAlt_return rewriteAlt187 = null;


        GrammarAST RARROW185_tree=null;
        GrammarAST SEMPRED186_tree=null;
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:894:2: ( RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) )
            // ANTLRParser.g:894:4: RARROW SEMPRED rewriteAlt
            {
            RARROW185=(Token)match(input,RARROW,FOLLOW_RARROW_in_predicatedRewrite4109); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW185);

            SEMPRED186=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite4111); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED186);

            pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite4113);
            rewriteAlt187=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt187.getTree());


            // AST REWRITE
            // elements: rewriteAlt, SEMPRED, SEMPRED, rewriteAlt
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 895:3: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
            if ((rewriteAlt187!=null?rewriteAlt187.isTemplate:false)) {
                // ANTLRParser.g:895:32: ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW185), root_1);

                adaptor.addChild(root_1, stream_SEMPRED.nextNode());
                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 896:3: -> ^( RESULT[$RARROW] SEMPRED rewriteAlt )
            {
                // ANTLRParser.g:896:6: ^( RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW185), root_1);

                adaptor.addChild(root_1, stream_SEMPRED.nextNode());
                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "predicatedRewrite"

    public static class nakedRewrite_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "nakedRewrite"
    // ANTLRParser.g:899:1: nakedRewrite : RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) ;
    public final ANTLRParser.nakedRewrite_return nakedRewrite() throws RecognitionException {
        ANTLRParser.nakedRewrite_return retval = new ANTLRParser.nakedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW188=null;
        ANTLRParser.rewriteAlt_return rewriteAlt189 = null;


        GrammarAST RARROW188_tree=null;
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:900:2: ( RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) )
            // ANTLRParser.g:900:4: RARROW rewriteAlt
            {
            RARROW188=(Token)match(input,RARROW,FOLLOW_RARROW_in_nakedRewrite4153); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW188);

            pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite4155);
            rewriteAlt189=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt189.getTree());


            // AST REWRITE
            // elements: rewriteAlt, rewriteAlt
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 900:22: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt )
            if ((rewriteAlt189!=null?rewriteAlt189.isTemplate:false)) {
                // ANTLRParser.g:900:51: ^( ST_RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW188), root_1);

                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 901:10: -> ^( RESULT[$RARROW] rewriteAlt )
            {
                // ANTLRParser.g:901:13: ^( RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW188), root_1);

                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "nakedRewrite"

    public static class rewriteAlt_return extends ParserRuleReturnScope {
        public boolean isTemplate;
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteAlt"
    // ANTLRParser.g:906:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );
    public final ANTLRParser.rewriteAlt_return rewriteAlt() throws RecognitionException {
        ANTLRParser.rewriteAlt_return retval = new ANTLRParser.rewriteAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ETC192=null;
        ANTLRParser.rewriteTemplate_return rewriteTemplate190 = null;

        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt191 = null;


        GrammarAST ETC192_tree=null;

        try {
            // ANTLRParser.g:908:5: ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON )
            int alt62=4;
            alt62 = dfa62.predict(input);
            switch (alt62) {
                case 1 :
                    // ANTLRParser.g:909:7: rewriteTemplate
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplate_in_rewriteAlt4219);
                    rewriteTemplate190=rewriteTemplate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplate190.getTree());
                    if ( state.backtracking==0 ) {
                      retval.isTemplate =true;
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:915:7: rewriteTreeAlt
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteAlt4258);
                    rewriteTreeAlt191=rewriteTreeAlt();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAlt191.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:917:7: ETC
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ETC192=(Token)match(input,ETC,FOLLOW_ETC_in_rewriteAlt4267); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ETC192_tree = (GrammarAST)adaptor.create(ETC192);
                    adaptor.addChild(root_0, ETC192_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:919:27: 
                    {

                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 919:27: -> EPSILON
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(EPSILON, "EPSILON"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteAlt"

    public static class rewriteTreeAlt_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTreeAlt"
    // ANTLRParser.g:922:1: rewriteTreeAlt : ( rewriteTreeElement )+ -> ^( ALT ( rewriteTreeElement )+ ) ;
    public final ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt() throws RecognitionException {
        ANTLRParser.rewriteTreeAlt_return retval = new ANTLRParser.rewriteTreeAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement193 = null;


        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:923:5: ( ( rewriteTreeElement )+ -> ^( ALT ( rewriteTreeElement )+ ) )
            // ANTLRParser.g:923:7: ( rewriteTreeElement )+
            {
            // ANTLRParser.g:923:7: ( rewriteTreeElement )+
            int cnt63=0;
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==ACTION||LA63_0==LPAREN||LA63_0==DOLLAR||LA63_0==TREE_BEGIN||(LA63_0>=TOKEN_REF && LA63_0<=RULE_REF)||LA63_0==STRING_LITERAL) ) {
                    alt63=1;
                }


                switch (alt63) {
            	case 1 :
            	    // ANTLRParser.g:923:7: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4298);
            	    rewriteTreeElement193=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement193.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt63 >= 1 ) break loop63;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(63, input);
                        throw eee;
                }
                cnt63++;
            } while (true);



            // AST REWRITE
            // elements: rewriteTreeElement
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 923:27: -> ^( ALT ( rewriteTreeElement )+ )
            {
                // ANTLRParser.g:923:30: ^( ALT ( rewriteTreeElement )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_1);

                if ( !(stream_rewriteTreeElement.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_rewriteTreeElement.hasNext() ) {
                    adaptor.addChild(root_1, stream_rewriteTreeElement.nextTree());

                }
                stream_rewriteTreeElement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTreeAlt"

    public static class rewriteTreeElement_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTreeElement"
    // ANTLRParser.g:926:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );
    public final ANTLRParser.rewriteTreeElement_return rewriteTreeElement() throws RecognitionException {
        ANTLRParser.rewriteTreeElement_return retval = new ANTLRParser.rewriteTreeElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom194 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom195 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix196 = null;

        ANTLRParser.rewriteTree_return rewriteTree197 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix198 = null;

        ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf199 = null;


        RewriteRuleSubtreeStream stream_rewriteTree=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTree");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        try {
            // ANTLRParser.g:927:2: ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf )
            int alt65=4;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // ANTLRParser.g:927:4: rewriteTreeAtom
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4322);
                    rewriteTreeAtom194=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAtom194.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:928:4: rewriteTreeAtom ebnfSuffix
                    {
                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4327);
                    rewriteTreeAtom195=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom195.getTree());
                    pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4329);
                    ebnfSuffix196=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix196.getTree());


                    // AST REWRITE
                    // elements: rewriteTreeAtom, ebnfSuffix
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 928:31: -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) )
                    {
                        // ANTLRParser.g:928:34: ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                        // ANTLRParser.g:928:48: ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(REWRITE_BLOCK, "REWRITE_BLOCK"), root_2);

                        // ANTLRParser.g:928:64: ^( ALT rewriteTreeAtom )
                        {
                        GrammarAST root_3 = (GrammarAST)adaptor.nil();
                        root_3 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_3);

                        adaptor.addChild(root_3, stream_rewriteTreeAtom.nextTree());

                        adaptor.addChild(root_2, root_3);
                        }

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:929:6: rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree )
                    {
                    pushFollow(FOLLOW_rewriteTree_in_rewriteTreeElement4354);
                    rewriteTree197=rewriteTree();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTree.add(rewriteTree197.getTree());
                    // ANTLRParser.g:930:3: ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree )
                    int alt64=2;
                    int LA64_0 = input.LA(1);

                    if ( (LA64_0==QUESTION||(LA64_0>=STAR && LA64_0<=PLUS)) ) {
                        alt64=1;
                    }
                    else if ( (LA64_0==EOF||LA64_0==ACTION||(LA64_0>=SEMI && LA64_0<=RPAREN)||LA64_0==OR||LA64_0==DOLLAR||(LA64_0>=RARROW && LA64_0<=TREE_BEGIN)||(LA64_0>=TOKEN_REF && LA64_0<=RULE_REF)||LA64_0==STRING_LITERAL) ) {
                        alt64=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 64, 0, input);

                        throw nvae;
                    }
                    switch (alt64) {
                        case 1 :
                            // ANTLRParser.g:930:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4360);
                            ebnfSuffix198=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix198.getTree());


                            // AST REWRITE
                            // elements: ebnfSuffix, rewriteTree
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 931:4: -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) )
                            {
                                // ANTLRParser.g:931:7: ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:931:20: ^( REWRITE_BLOCK ^( ALT rewriteTree ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(REWRITE_BLOCK, "REWRITE_BLOCK"), root_2);

                                // ANTLRParser.g:931:36: ^( ALT rewriteTree )
                                {
                                GrammarAST root_3 = (GrammarAST)adaptor.nil();
                                root_3 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT, "ALT"), root_3);

                                adaptor.addChild(root_3, stream_rewriteTree.nextTree());

                                adaptor.addChild(root_2, root_3);
                                }

                                adaptor.addChild(root_1, root_2);
                                }

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:932:5: 
                            {

                            // AST REWRITE
                            // elements: rewriteTree
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 932:5: -> rewriteTree
                            {
                                adaptor.addChild(root_0, stream_rewriteTree.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // ANTLRParser.g:934:6: rewriteTreeEbnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4399);
                    rewriteTreeEbnf199=rewriteTreeEbnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeEbnf199.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTreeElement"

    public static class rewriteTreeAtom_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTreeAtom"
    // ANTLRParser.g:937:1: rewriteTreeAtom : ( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION );
    public final ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom() throws RecognitionException {
        ANTLRParser.rewriteTreeAtom_return retval = new ANTLRParser.rewriteTreeAtom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF200=null;
        Token ARG_ACTION202=null;
        Token RULE_REF203=null;
        Token STRING_LITERAL204=null;
        Token DOLLAR206=null;
        Token ACTION208=null;
        ANTLRParser.elementOptions_return elementOptions201 = null;

        ANTLRParser.elementOptions_return elementOptions205 = null;

        ANTLRParser.id_return id207 = null;


        GrammarAST TOKEN_REF200_tree=null;
        GrammarAST ARG_ACTION202_tree=null;
        GrammarAST RULE_REF203_tree=null;
        GrammarAST STRING_LITERAL204_tree=null;
        GrammarAST DOLLAR206_tree=null;
        GrammarAST ACTION208_tree=null;
        RewriteRuleTokenStream stream_DOLLAR=new RewriteRuleTokenStream(adaptor,"token DOLLAR");
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        try {
            // ANTLRParser.g:938:5: ( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION )
            int alt69=5;
            switch ( input.LA(1) ) {
            case TOKEN_REF:
                {
                alt69=1;
                }
                break;
            case RULE_REF:
                {
                alt69=2;
                }
                break;
            case STRING_LITERAL:
                {
                alt69=3;
                }
                break;
            case DOLLAR:
                {
                alt69=4;
                }
                break;
            case ACTION:
                {
                alt69=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 69, 0, input);

                throw nvae;
            }

            switch (alt69) {
                case 1 :
                    // ANTLRParser.g:938:9: TOKEN_REF ( elementOptions )? ( ARG_ACTION )?
                    {
                    TOKEN_REF200=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom4415); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF200);

                    // ANTLRParser.g:938:19: ( elementOptions )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==LT) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // ANTLRParser.g:938:19: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom4417);
                            elementOptions201=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions201.getTree());

                            }
                            break;

                    }

                    // ANTLRParser.g:938:35: ( ARG_ACTION )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==ARG_ACTION) ) {
                        alt67=1;
                    }
                    switch (alt67) {
                        case 1 :
                            // ANTLRParser.g:938:35: ARG_ACTION
                            {
                            ARG_ACTION202=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom4420); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION202);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ARG_ACTION, elementOptions, TOKEN_REF
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 938:47: -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:938:50: ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_TOKEN_REF.nextToken()), root_1);

                        // ANTLRParser.g:938:75: ( elementOptions )?
                        if ( stream_elementOptions.hasNext() ) {
                            adaptor.addChild(root_1, stream_elementOptions.nextTree());

                        }
                        stream_elementOptions.reset();
                        // ANTLRParser.g:938:91: ( ARG_ACTION )?
                        if ( stream_ARG_ACTION.hasNext() ) {
                            adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                        }
                        stream_ARG_ACTION.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:939:9: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF203=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_rewriteTreeAtom4447); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF203_tree = (GrammarAST)adaptor.create(RULE_REF203);
                    adaptor.addChild(root_0, RULE_REF203_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:940:6: STRING_LITERAL ( elementOptions )?
                    {
                    STRING_LITERAL204=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4454); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL204);

                    // ANTLRParser.g:940:21: ( elementOptions )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==LT) ) {
                        alt68=1;
                    }
                    switch (alt68) {
                        case 1 :
                            // ANTLRParser.g:940:21: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom4456);
                            elementOptions205=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions205.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: STRING_LITERAL, elementOptions
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 940:40: -> ^( STRING_LITERAL ( elementOptions )? )
                    {
                        // ANTLRParser.g:940:43: ^( STRING_LITERAL ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_STRING_LITERAL.nextToken()), root_1);

                        // ANTLRParser.g:940:73: ( elementOptions )?
                        if ( stream_elementOptions.hasNext() ) {
                            adaptor.addChild(root_1, stream_elementOptions.nextTree());

                        }
                        stream_elementOptions.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // ANTLRParser.g:941:6: DOLLAR id
                    {
                    DOLLAR206=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_rewriteTreeAtom4479); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOLLAR.add(DOLLAR206);

                    pushFollow(FOLLOW_id_in_rewriteTreeAtom4481);
                    id207=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id207.getTree());


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 941:16: -> LABEL[$DOLLAR,$id.text]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(LABEL, DOLLAR206, (id207!=null?input.toString(id207.start,id207.stop):null)));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // ANTLRParser.g:942:4: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION208=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTreeAtom4492); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION208_tree = new ActionAST(ACTION208) ;
                    adaptor.addChild(root_0, ACTION208_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTreeAtom"

    public static class rewriteTreeEbnf_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTreeEbnf"
    // ANTLRParser.g:945:1: rewriteTreeEbnf : lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) ) ;
    public final ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf() throws RecognitionException {
        ANTLRParser.rewriteTreeEbnf_return retval = new ANTLRParser.rewriteTreeEbnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token RPAREN210=null;
        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt209 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix211 = null;


        GrammarAST lp_tree=null;
        GrammarAST RPAREN210_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTreeAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAlt");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");

            Token firstToken = input.LT(1);

        try {
            // ANTLRParser.g:953:2: (lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) ) )
            // ANTLRParser.g:953:4: lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTreeEbnf4518); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4520);
            rewriteTreeAlt209=rewriteTreeAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAlt.add(rewriteTreeAlt209.getTree());
            RPAREN210=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTreeEbnf4522); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN210);

            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4524);
            ebnfSuffix211=ebnfSuffix();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix211.getTree());


            // AST REWRITE
            // elements: ebnfSuffix, rewriteTreeAlt
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 953:47: -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) )
            {
                // ANTLRParser.g:953:50: ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                // ANTLRParser.g:953:63: ^( REWRITE_BLOCK[$lp] rewriteTreeAlt )
                {
                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(REWRITE_BLOCK, lp), root_2);

                adaptor.addChild(root_2, stream_rewriteTreeAlt.nextTree());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {

              	((GrammarAST)retval.tree).getToken().setLine(firstToken.getLine());
              	((GrammarAST)retval.tree).getToken().setCharPositionInLine(firstToken.getCharPositionInLine());

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTreeEbnf"

    public static class rewriteTree_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTree"
    // ANTLRParser.g:956:1: rewriteTree : TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) ;
    public final ANTLRParser.rewriteTree_return rewriteTree() throws RecognitionException {
        ANTLRParser.rewriteTree_return retval = new ANTLRParser.rewriteTree_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN212=null;
        Token RPAREN215=null;
        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom213 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement214 = null;


        GrammarAST TREE_BEGIN212_tree=null;
        GrammarAST RPAREN215_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:957:2: ( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) )
            // ANTLRParser.g:957:4: TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN
            {
            TREE_BEGIN212=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewriteTree4548); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN212);

            pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTree4550);
            rewriteTreeAtom213=rewriteTreeAtom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom213.getTree());
            // ANTLRParser.g:957:31: ( rewriteTreeElement )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==ACTION||LA70_0==LPAREN||LA70_0==DOLLAR||LA70_0==TREE_BEGIN||(LA70_0>=TOKEN_REF && LA70_0<=RULE_REF)||LA70_0==STRING_LITERAL) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // ANTLRParser.g:957:31: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTree4552);
            	    rewriteTreeElement214=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement214.getTree());

            	    }
            	    break;

            	default :
            	    break loop70;
                }
            } while (true);

            RPAREN215=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTree4555); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN215);



            // AST REWRITE
            // elements: rewriteTreeAtom, rewriteTreeElement, TREE_BEGIN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 958:3: -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
            {
                // ANTLRParser.g:958:6: ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_TREE_BEGIN.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rewriteTreeAtom.nextTree());
                // ANTLRParser.g:958:35: ( rewriteTreeElement )*
                while ( stream_rewriteTreeElement.hasNext() ) {
                    adaptor.addChild(root_1, stream_rewriteTreeElement.nextTree());

                }
                stream_rewriteTreeElement.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTree"

    public static class rewriteTemplate_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTemplate"
    // ANTLRParser.g:961:1: rewriteTemplate : ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );
    public final ANTLRParser.rewriteTemplate_return rewriteTemplate() throws RecognitionException {
        ANTLRParser.rewriteTemplate_return retval = new ANTLRParser.rewriteTemplate_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token str=null;
        Token TEMPLATE216=null;
        Token LPAREN217=null;
        Token RPAREN219=null;
        Token ACTION222=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs218 = null;

        ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef220 = null;

        ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead221 = null;


        GrammarAST str_tree=null;
        GrammarAST TEMPLATE216_tree=null;
        GrammarAST LPAREN217_tree=null;
        GrammarAST RPAREN219_tree=null;
        GrammarAST ACTION222_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_DOUBLE_QUOTE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_QUOTE_STRING_LITERAL");
        RewriteRuleTokenStream stream_TEMPLATE=new RewriteRuleTokenStream(adaptor,"token TEMPLATE");
        RewriteRuleTokenStream stream_DOUBLE_ANGLE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_ANGLE_STRING_LITERAL");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:972:2: ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION )
            int alt72=4;
            alt72 = dfa72.predict(input);
            switch (alt72) {
                case 1 :
                    // ANTLRParser.g:973:3: TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
                    {
                    TEMPLATE216=(Token)match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate4587); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TEMPLATE.add(TEMPLATE216);

                    LPAREN217=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplate4589); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN217);

                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4591);
                    rewriteTemplateArgs218=rewriteTemplateArgs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs218.getTree());
                    RPAREN219=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplate4593); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN219);

                    // ANTLRParser.g:974:3: (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
                    int alt71=2;
                    int LA71_0 = input.LA(1);

                    if ( (LA71_0==DOUBLE_QUOTE_STRING_LITERAL) ) {
                        alt71=1;
                    }
                    else if ( (LA71_0==DOUBLE_ANGLE_STRING_LITERAL) ) {
                        alt71=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 71, 0, input);

                        throw nvae;
                    }
                    switch (alt71) {
                        case 1 :
                            // ANTLRParser.g:974:5: str= DOUBLE_QUOTE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_QUOTE_STRING_LITERAL,FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4601); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOUBLE_QUOTE_STRING_LITERAL.add(str);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:974:39: str= DOUBLE_ANGLE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_ANGLE_STRING_LITERAL,FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4607); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOUBLE_ANGLE_STRING_LITERAL.add(str);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: rewriteTemplateArgs, str, TEMPLATE
                    // token labels: str
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_str=new RewriteRuleTokenStream(adaptor,"token str",str);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 975:3: -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                    {
                        // ANTLRParser.g:975:6: ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, TEMPLATE216, "TEMPLATE"), root_1);

                        // ANTLRParser.g:975:39: ( rewriteTemplateArgs )?
                        if ( stream_rewriteTemplateArgs.hasNext() ) {
                            adaptor.addChild(root_1, stream_rewriteTemplateArgs.nextTree());

                        }
                        stream_rewriteTemplateArgs.reset();
                        adaptor.addChild(root_1, stream_str.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:978:3: rewriteTemplateRef
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplateRef_in_rewriteTemplate4633);
                    rewriteTemplateRef220=rewriteTemplateRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplateRef220.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:981:3: rewriteIndirectTemplateHead
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4642);
                    rewriteIndirectTemplateHead221=rewriteIndirectTemplateHead();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteIndirectTemplateHead221.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:984:3: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION222=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplate4651); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION222_tree = new ActionAST(ACTION222) ;
                    adaptor.addChild(root_0, ACTION222_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTemplate"

    public static class rewriteTemplateRef_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTemplateRef"
    // ANTLRParser.g:987:1: rewriteTemplateRef : id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef() throws RecognitionException {
        ANTLRParser.rewriteTemplateRef_return retval = new ANTLRParser.rewriteTemplateRef_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN224=null;
        Token RPAREN226=null;
        ANTLRParser.id_return id223 = null;

        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs225 = null;


        GrammarAST LPAREN224_tree=null;
        GrammarAST RPAREN226_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:989:2: ( id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:989:4: id LPAREN rewriteTemplateArgs RPAREN
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateRef4667);
            id223=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id223.getTree());
            LPAREN224=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplateRef4669); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN224);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4671);
            rewriteTemplateArgs225=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs225.getTree());
            RPAREN226=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplateRef4673); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN226);



            // AST REWRITE
            // elements: id, rewriteTemplateArgs
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 990:3: -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:990:6: ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, LPAREN224, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:990:40: ( rewriteTemplateArgs )?
                if ( stream_rewriteTemplateArgs.hasNext() ) {
                    adaptor.addChild(root_1, stream_rewriteTemplateArgs.nextTree());

                }
                stream_rewriteTemplateArgs.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTemplateRef"

    public static class rewriteIndirectTemplateHead_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteIndirectTemplateHead"
    // ANTLRParser.g:993:1: rewriteIndirectTemplateHead : lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead() throws RecognitionException {
        ANTLRParser.rewriteIndirectTemplateHead_return retval = new ANTLRParser.rewriteIndirectTemplateHead_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token ACTION227=null;
        Token RPAREN228=null;
        Token LPAREN229=null;
        Token RPAREN231=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs230 = null;


        GrammarAST lp_tree=null;
        GrammarAST ACTION227_tree=null;
        GrammarAST RPAREN228_tree=null;
        GrammarAST LPAREN229_tree=null;
        GrammarAST RPAREN231_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:995:2: (lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:995:4: lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4702); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            ACTION227=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteIndirectTemplateHead4704); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION227);

            RPAREN228=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4706); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN228);

            LPAREN229=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4708); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN229);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4710);
            rewriteTemplateArgs230=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs230.getTree());
            RPAREN231=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4712); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN231);



            // AST REWRITE
            // elements: rewriteTemplateArgs, ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 996:3: -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:996:6: ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, lp, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));
                // ANTLRParser.g:996:51: ( rewriteTemplateArgs )?
                if ( stream_rewriteTemplateArgs.hasNext() ) {
                    adaptor.addChild(root_1, stream_rewriteTemplateArgs.nextTree());

                }
                stream_rewriteTemplateArgs.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteIndirectTemplateHead"

    public static class rewriteTemplateArgs_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTemplateArgs"
    // ANTLRParser.g:999:1: rewriteTemplateArgs : ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | );
    public final ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs() throws RecognitionException {
        ANTLRParser.rewriteTemplateArgs_return retval = new ANTLRParser.rewriteTemplateArgs_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token COMMA233=null;
        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg232 = null;

        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg234 = null;


        GrammarAST COMMA233_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_rewriteTemplateArg=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArg");
        try {
            // ANTLRParser.g:1000:2: ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==TEMPLATE||(LA74_0>=TOKEN_REF && LA74_0<=RULE_REF)) ) {
                alt74=1;
            }
            else if ( (LA74_0==RPAREN) ) {
                alt74=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // ANTLRParser.g:1000:4: rewriteTemplateArg ( COMMA rewriteTemplateArg )*
                    {
                    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4740);
                    rewriteTemplateArg232=rewriteTemplateArg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg232.getTree());
                    // ANTLRParser.g:1000:23: ( COMMA rewriteTemplateArg )*
                    loop73:
                    do {
                        int alt73=2;
                        int LA73_0 = input.LA(1);

                        if ( (LA73_0==COMMA) ) {
                            alt73=1;
                        }


                        switch (alt73) {
                    	case 1 :
                    	    // ANTLRParser.g:1000:24: COMMA rewriteTemplateArg
                    	    {
                    	    COMMA233=(Token)match(input,COMMA,FOLLOW_COMMA_in_rewriteTemplateArgs4743); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA233);

                    	    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4745);
                    	    rewriteTemplateArg234=rewriteTemplateArg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg234.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop73;
                        }
                    } while (true);



                    // AST REWRITE
                    // elements: rewriteTemplateArg
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 1001:3: -> ^( ARGLIST ( rewriteTemplateArg )+ )
                    {
                        // ANTLRParser.g:1001:6: ^( ARGLIST ( rewriteTemplateArg )+ )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ARGLIST, "ARGLIST"), root_1);

                        if ( !(stream_rewriteTemplateArg.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_rewriteTemplateArg.hasNext() ) {
                            adaptor.addChild(root_1, stream_rewriteTemplateArg.nextTree());

                        }
                        stream_rewriteTemplateArg.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:1003:2: 
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTemplateArgs"

    public static class rewriteTemplateArg_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rewriteTemplateArg"
    // ANTLRParser.g:1005:1: rewriteTemplateArg : id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) ;
    public final ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg() throws RecognitionException {
        ANTLRParser.rewriteTemplateArg_return retval = new ANTLRParser.rewriteTemplateArg_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN236=null;
        Token ACTION237=null;
        ANTLRParser.id_return id235 = null;


        GrammarAST ASSIGN236_tree=null;
        GrammarAST ACTION237_tree=null;
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:1006:2: ( id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) )
            // ANTLRParser.g:1006:6: id ASSIGN ACTION
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateArg4774);
            id235=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id235.getTree());
            ASSIGN236=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_rewriteTemplateArg4776); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN236);

            ACTION237=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplateArg4778); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION237);



            // AST REWRITE
            // elements: id, ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 1006:23: -> ^( ARG[$ASSIGN] id ACTION )
            {
                // ANTLRParser.g:1006:26: ^( ARG[$ASSIGN] id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ARG, ASSIGN236), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rewriteTemplateArg"

    public static class id_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "id"
    // ANTLRParser.g:1013:1: id : ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] | TEMPLATE -> ID[$TEMPLATE] );
    public final ANTLRParser.id_return id() throws RecognitionException {
        ANTLRParser.id_return retval = new ANTLRParser.id_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RULE_REF238=null;
        Token TOKEN_REF239=null;
        Token TEMPLATE240=null;

        GrammarAST RULE_REF238_tree=null;
        GrammarAST TOKEN_REF239_tree=null;
        GrammarAST TEMPLATE240_tree=null;
        RewriteRuleTokenStream stream_TEMPLATE=new RewriteRuleTokenStream(adaptor,"token TEMPLATE");
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");

         paraphrases.push("looking for an identifier"); 
        try {
            // ANTLRParser.g:1016:5: ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] | TEMPLATE -> ID[$TEMPLATE] )
            int alt75=3;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                alt75=1;
                }
                break;
            case TOKEN_REF:
                {
                alt75=2;
                }
                break;
            case TEMPLATE:
                {
                alt75=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 75, 0, input);

                throw nvae;
            }

            switch (alt75) {
                case 1 :
                    // ANTLRParser.g:1016:7: RULE_REF
                    {
                    RULE_REF238=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_id4820); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF238);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 1016:17: -> ID[$RULE_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, RULE_REF238));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:1017:7: TOKEN_REF
                    {
                    TOKEN_REF239=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_id4833); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF239);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 1017:17: -> ID[$TOKEN_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TOKEN_REF239));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:1018:7: TEMPLATE
                    {
                    TEMPLATE240=(Token)match(input,TEMPLATE,FOLLOW_TEMPLATE_in_id4845); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TEMPLATE.add(TEMPLATE240);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 1018:17: -> ID[$TEMPLATE]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TEMPLATE240));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "id"

    public static class qid_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qid"
    // ANTLRParser.g:1021:1: qid : id ( DOT id )* -> ID[$qid.start, $text] ;
    public final ANTLRParser.qid_return qid() throws RecognitionException {
        ANTLRParser.qid_return retval = new ANTLRParser.qid_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOT242=null;
        ANTLRParser.id_return id241 = null;

        ANTLRParser.id_return id243 = null;


        GrammarAST DOT242_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
         paraphrases.push("looking for a qualified identifier"); 
        try {
            // ANTLRParser.g:1024:2: ( id ( DOT id )* -> ID[$qid.start, $text] )
            // ANTLRParser.g:1024:4: id ( DOT id )*
            {
            pushFollow(FOLLOW_id_in_qid4879);
            id241=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id241.getTree());
            // ANTLRParser.g:1024:7: ( DOT id )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==DOT) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // ANTLRParser.g:1024:8: DOT id
            	    {
            	    DOT242=(Token)match(input,DOT,FOLLOW_DOT_in_qid4882); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT242);

            	    pushFollow(FOLLOW_id_in_qid4884);
            	    id243=id();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_id.add(id243.getTree());

            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 1024:17: -> ID[$qid.start, $text]
            {
                adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, ((Token)retval.start), input.toString(retval.start,input.LT(-1))));

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
            if ( state.backtracking==0 ) {
               paraphrases.pop(); 
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "qid"

    public static class alternativeEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "alternativeEntry"
    // ANTLRParser.g:1027:1: alternativeEntry : alternative EOF ;
    public final ANTLRParser.alternativeEntry_return alternativeEntry() throws RecognitionException {
        ANTLRParser.alternativeEntry_return retval = new ANTLRParser.alternativeEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF245=null;
        ANTLRParser.alternative_return alternative244 = null;


        GrammarAST EOF245_tree=null;

        try {
            // ANTLRParser.g:1027:18: ( alternative EOF )
            // ANTLRParser.g:1027:20: alternative EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_alternative_in_alternativeEntry4901);
            alternative244=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, alternative244.getTree());
            EOF245=(Token)match(input,EOF,FOLLOW_EOF_in_alternativeEntry4903); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF245_tree = (GrammarAST)adaptor.create(EOF245);
            adaptor.addChild(root_0, EOF245_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "alternativeEntry"

    public static class elementEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementEntry"
    // ANTLRParser.g:1028:1: elementEntry : element EOF ;
    public final ANTLRParser.elementEntry_return elementEntry() throws RecognitionException {
        ANTLRParser.elementEntry_return retval = new ANTLRParser.elementEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF247=null;
        ANTLRParser.element_return element246 = null;


        GrammarAST EOF247_tree=null;

        try {
            // ANTLRParser.g:1028:14: ( element EOF )
            // ANTLRParser.g:1028:16: element EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_element_in_elementEntry4912);
            element246=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, element246.getTree());
            EOF247=(Token)match(input,EOF,FOLLOW_EOF_in_elementEntry4914); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF247_tree = (GrammarAST)adaptor.create(EOF247);
            adaptor.addChild(root_0, EOF247_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "elementEntry"

    public static class ruleEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleEntry"
    // ANTLRParser.g:1029:1: ruleEntry : rule EOF ;
    public final ANTLRParser.ruleEntry_return ruleEntry() throws RecognitionException {
        ANTLRParser.ruleEntry_return retval = new ANTLRParser.ruleEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF249=null;
        ANTLRParser.rule_return rule248 = null;


        GrammarAST EOF249_tree=null;

        try {
            // ANTLRParser.g:1029:11: ( rule EOF )
            // ANTLRParser.g:1029:13: rule EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_rule_in_ruleEntry4922);
            rule248=rule();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rule248.getTree());
            EOF249=(Token)match(input,EOF,FOLLOW_EOF_in_ruleEntry4924); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF249_tree = (GrammarAST)adaptor.create(EOF249);
            adaptor.addChild(root_0, EOF249_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "ruleEntry"

    public static class blockEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blockEntry"
    // ANTLRParser.g:1030:1: blockEntry : block EOF ;
    public final ANTLRParser.blockEntry_return blockEntry() throws RecognitionException {
        ANTLRParser.blockEntry_return retval = new ANTLRParser.blockEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF251=null;
        ANTLRParser.block_return block250 = null;


        GrammarAST EOF251_tree=null;

        try {
            // ANTLRParser.g:1030:12: ( block EOF )
            // ANTLRParser.g:1030:14: block EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_block_in_blockEntry4932);
            block250=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block250.getTree());
            EOF251=(Token)match(input,EOF,FOLLOW_EOF_in_blockEntry4934); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF251_tree = (GrammarAST)adaptor.create(EOF251);
            adaptor.addChild(root_0, EOF251_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "blockEntry"

    // $ANTLR start synpred1_ANTLRParser
    public final void synpred1_ANTLRParser_fragment() throws RecognitionException {   
        // ANTLRParser.g:909:7: ( rewriteTemplate )
        // ANTLRParser.g:909:7: rewriteTemplate
        {
        pushFollow(FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4219);
        rewriteTemplate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_ANTLRParser

    // $ANTLR start synpred2_ANTLRParser
    public final void synpred2_ANTLRParser_fragment() throws RecognitionException {   
        // ANTLRParser.g:915:7: ( rewriteTreeAlt )
        // ANTLRParser.g:915:7: rewriteTreeAlt
        {
        pushFollow(FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4258);
        rewriteTreeAlt();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_ANTLRParser

    // Delegated rules

    public final boolean synpred1_ANTLRParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_ANTLRParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_ANTLRParser() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_ANTLRParser_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA35 dfa35 = new DFA35(this);
    protected DFA44 dfa44 = new DFA44(this);
    protected DFA62 dfa62 = new DFA62(this);
    protected DFA65 dfa65 = new DFA65(this);
    protected DFA72 dfa72 = new DFA72(this);
    static final String DFA35_eotS =
        "\12\uffff";
    static final String DFA35_eofS =
        "\1\uffff\2\4\7\uffff";
    static final String DFA35_minS =
        "\3\4\1\55\6\uffff";
    static final String DFA35_maxS =
        "\3\103\1\66\6\uffff";
    static final String DFA35_acceptS =
        "\4\uffff\1\2\1\3\1\4\1\5\1\6\1\1";
    static final String DFA35_specialS =
        "\12\uffff}>";
    static final String[] DFA35_transitionS = {
            "\1\7\13\uffff\1\6\22\uffff\1\3\4\uffff\1\5\15\uffff\1\4\3\uffff"+
            "\1\10\1\uffff\1\4\1\uffff\1\2\1\1\3\uffff\1\4",
            "\1\4\11\uffff\1\4\1\uffff\1\4\22\uffff\1\4\3\uffff\3\4\3\uffff"+
            "\1\11\4\4\1\11\2\4\1\uffff\2\4\1\uffff\2\4\1\uffff\1\4\1\uffff"+
            "\2\4\3\uffff\1\4",
            "\1\4\11\uffff\1\4\1\uffff\1\4\22\uffff\1\4\3\uffff\3\4\1\uffff"+
            "\1\4\1\uffff\1\11\4\4\1\11\2\4\1\uffff\2\4\1\uffff\2\4\1\uffff"+
            "\1\4\1\uffff\2\4\3\uffff\1\4",
            "\1\11\4\uffff\1\11\3\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "597:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec ) );";
        }
    }
    static final String DFA44_eotS =
        "\26\uffff";
    static final String DFA44_eofS =
        "\1\uffff\1\11\2\5\3\uffff\1\11\2\uffff\1\5\13\uffff";
    static final String DFA44_minS =
        "\1\43\3\4\1\66\2\uffff\1\4\2\uffff\1\4\1\66\10\0\2\uffff";
    static final String DFA44_maxS =
        "\4\103\1\66\2\uffff\1\103\2\uffff\2\103\10\0\2\uffff";
    static final String DFA44_acceptS =
        "\5\uffff\1\4\1\6\1\uffff\1\1\1\5\12\uffff\1\2\1\3";
    static final String DFA44_specialS =
        "\14\uffff\1\3\1\1\1\2\1\4\1\7\1\6\1\5\1\0\2\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\4\22\uffff\1\5\5\uffff\1\6\1\uffff\1\2\1\1\3\uffff\1\3",
            "\1\11\11\uffff\1\11\1\uffff\1\11\22\uffff\1\11\3\uffff\3\11"+
            "\4\uffff\4\11\1\uffff\2\11\1\uffff\1\7\1\10\1\uffff\2\11\1\uffff"+
            "\1\11\1\uffff\2\11\3\uffff\1\11",
            "\1\5\11\uffff\1\5\1\uffff\1\5\22\uffff\1\5\3\uffff\3\5\1\uffff"+
            "\1\5\2\uffff\4\5\1\uffff\2\5\1\uffff\1\12\1\10\1\uffff\2\5\1"+
            "\uffff\1\5\1\uffff\2\5\3\uffff\1\5",
            "\1\5\13\uffff\1\5\22\uffff\1\5\3\uffff\3\5\1\uffff\1\5\2\uffff"+
            "\4\5\1\uffff\2\5\1\uffff\1\5\1\10\1\uffff\2\5\1\uffff\1\5\1"+
            "\uffff\2\5\3\uffff\1\5",
            "\1\13",
            "",
            "",
            "\1\11\13\uffff\1\11\22\uffff\1\11\3\uffff\3\11\1\uffff\1\11"+
            "\2\uffff\4\11\1\uffff\2\11\1\uffff\1\17\2\uffff\2\11\1\uffff"+
            "\1\11\1\uffff\1\15\1\14\3\uffff\1\16",
            "",
            "",
            "\1\5\13\uffff\1\5\22\uffff\1\5\3\uffff\3\5\1\uffff\1\5\2\uffff"+
            "\4\5\1\uffff\2\5\1\uffff\1\23\2\uffff\2\5\1\uffff\1\5\1\uffff"+
            "\1\21\1\20\3\uffff\1\22",
            "\1\25\7\uffff\1\25\1\24\3\uffff\1\25",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA44_eot = DFA.unpackEncodedString(DFA44_eotS);
    static final short[] DFA44_eof = DFA.unpackEncodedString(DFA44_eofS);
    static final char[] DFA44_min = DFA.unpackEncodedStringToUnsignedChars(DFA44_minS);
    static final char[] DFA44_max = DFA.unpackEncodedStringToUnsignedChars(DFA44_maxS);
    static final short[] DFA44_accept = DFA.unpackEncodedString(DFA44_acceptS);
    static final short[] DFA44_special = DFA.unpackEncodedString(DFA44_specialS);
    static final short[][] DFA44_transition;

    static {
        int numStates = DFA44_transitionS.length;
        DFA44_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA44_transition[i] = DFA.unpackEncodedString(DFA44_transitionS[i]);
        }
    }

    class DFA44 extends DFA {

        public DFA44(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 44;
            this.eot = DFA44_eot;
            this.eof = DFA44_eof;
            this.min = DFA44_min;
            this.max = DFA44_max;
            this.accept = DFA44_accept;
            this.special = DFA44_special;
            this.transition = DFA44_transition;
        }
        public String getDescription() {
            return "744:1: atom : ( range ( ROOT | BANG )? | {...}? id DOT ruleref -> ^( DOT id ruleref ) | {...}? id DOT terminal -> ^( DOT id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA44_19 = input.LA(1);

                         
                        int index44_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 21;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index44_19);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA44_13 = input.LA(1);

                         
                        int index44_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 21;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index44_13);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA44_14 = input.LA(1);

                         
                        int index44_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 21;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index44_14);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA44_12 = input.LA(1);

                         
                        int index44_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 20;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index44_12);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA44_15 = input.LA(1);

                         
                        int index44_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 21;}

                        else if ( (true) ) {s = 9;}

                         
                        input.seek(index44_15);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA44_18 = input.LA(1);

                         
                        int index44_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 21;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index44_18);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA44_17 = input.LA(1);

                         
                        int index44_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 21;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index44_17);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA44_16 = input.LA(1);

                         
                        int index44_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 20;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index44_16);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 44, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA62_eotS =
        "\16\uffff";
    static final String DFA62_eofS =
        "\1\10\1\uffff\2\6\12\uffff";
    static final String DFA62_minS =
        "\1\20\1\uffff\1\20\1\16\1\20\1\0\3\uffff\3\20\1\16\1\50";
    static final String DFA62_maxS =
        "\1\103\1\uffff\3\103\1\0\3\uffff\4\103\1\61";
    static final String DFA62_acceptS =
        "\1\uffff\1\1\4\uffff\1\2\1\3\1\4\5\uffff";
    static final String DFA62_specialS =
        "\5\uffff\1\0\10\uffff}>";
    static final String[] DFA62_transitionS = {
            "\1\5\22\uffff\1\1\3\uffff\1\10\1\4\1\10\11\uffff\1\10\1\uffff"+
            "\1\6\2\uffff\1\7\1\10\1\6\3\uffff\1\3\1\2\3\uffff\1\6",
            "",
            "\1\6\26\uffff\1\6\1\11\1\6\4\uffff\1\6\1\uffff\2\6\1\uffff"+
            "\1\6\1\uffff\1\6\3\uffff\2\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\1\uffff\1\6\26\uffff\1\6\1\11\1\6\1\uffff\1\6\2\uffff"+
            "\1\6\1\uffff\2\6\1\uffff\1\6\1\uffff\1\6\3\uffff\2\6\3\uffff"+
            "\2\6\3\uffff\1\6",
            "\1\12\27\uffff\1\6\14\uffff\1\6\4\uffff\1\6\3\uffff\2\6\3\uffff"+
            "\1\6",
            "\1\uffff",
            "",
            "",
            "",
            "\1\6\22\uffff\1\1\4\uffff\1\6\1\1\13\uffff\1\6\4\uffff\1\6"+
            "\3\uffff\1\14\1\13\3\uffff\1\6",
            "\1\6\27\uffff\1\6\1\15\4\uffff\1\6\1\uffff\2\6\3\uffff\1\6"+
            "\4\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\27\uffff\2\6\3\uffff\1\1\1\6\1\uffff\2\6\3\uffff\1\6\4"+
            "\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\1\uffff\1\6\27\uffff\2\6\1\uffff\1\6\1\uffff\1\1\1\6\1"+
            "\uffff\2\6\3\uffff\1\6\4\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\1\5\uffff\1\6\1\uffff\2\6"
    };

    static final short[] DFA62_eot = DFA.unpackEncodedString(DFA62_eotS);
    static final short[] DFA62_eof = DFA.unpackEncodedString(DFA62_eofS);
    static final char[] DFA62_min = DFA.unpackEncodedStringToUnsignedChars(DFA62_minS);
    static final char[] DFA62_max = DFA.unpackEncodedStringToUnsignedChars(DFA62_maxS);
    static final short[] DFA62_accept = DFA.unpackEncodedString(DFA62_acceptS);
    static final short[] DFA62_special = DFA.unpackEncodedString(DFA62_specialS);
    static final short[][] DFA62_transition;

    static {
        int numStates = DFA62_transitionS.length;
        DFA62_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA62_transition[i] = DFA.unpackEncodedString(DFA62_transitionS[i]);
        }
    }

    class DFA62 extends DFA {

        public DFA62(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 62;
            this.eot = DFA62_eot;
            this.eof = DFA62_eof;
            this.min = DFA62_min;
            this.max = DFA62_max;
            this.accept = DFA62_accept;
            this.special = DFA62_special;
            this.transition = DFA62_transition;
        }
        public String getDescription() {
            return "906:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA62_5 = input.LA(1);

                         
                        int index62_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_ANTLRParser()) ) {s = 1;}

                        else if ( (synpred2_ANTLRParser()) ) {s = 6;}

                         
                        input.seek(index62_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 62, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA65_eotS =
        "\124\uffff";
    static final String DFA65_eofS =
        "\1\uffff\3\13\1\uffff\1\13\3\uffff\1\13\3\uffff\3\13\11\uffff\1"+
        "\13\3\uffff\1\13\66\uffff";
    static final String DFA65_minS =
        "\1\20\1\16\2\20\1\43\1\20\2\uffff\1\43\1\20\2\uffff\1\43\3\20\6"+
        "\46\3\43\1\16\3\43\1\20\24\46\6\43\24\46\2\43\6\46";
    static final String DFA65_maxS =
        "\4\103\1\77\1\103\2\uffff\1\77\1\103\2\uffff\1\77\3\103\6\66\1\103"+
        "\2\77\2\103\2\77\1\103\3\66\1\54\11\66\1\54\6\66\2\77\1\103\2\77"+
        "\1\103\11\66\1\54\11\66\1\54\2\77\6\66";
    static final String DFA65_acceptS =
        "\6\uffff\1\3\1\4\2\uffff\1\2\1\1\110\uffff";
    static final String DFA65_specialS =
        "\124\uffff}>";
    static final String[] DFA65_transitionS = {
            "\1\5\27\uffff\1\7\14\uffff\1\4\4\uffff\1\6\3\uffff\1\1\1\2\3"+
            "\uffff\1\3",
            "\1\11\1\uffff\1\13\26\uffff\3\13\1\uffff\1\10\2\uffff\1\12"+
            "\1\uffff\2\12\1\uffff\1\13\1\uffff\1\13\3\uffff\2\13\3\uffff"+
            "\2\13\3\uffff\1\13",
            "\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12\1\uffff\1\13"+
            "\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff\1\13",
            "\1\13\26\uffff\3\13\1\uffff\1\14\2\uffff\1\12\1\uffff\2\12"+
            "\1\uffff\1\13\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff"+
            "\1\13",
            "\1\17\32\uffff\1\16\1\15",
            "\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12\1\uffff\1\13"+
            "\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff\1\13",
            "",
            "",
            "\1\22\32\uffff\1\21\1\20",
            "\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12\1\uffff\1\13"+
            "\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff\1\13",
            "",
            "",
            "\1\25\32\uffff\1\24\1\23",
            "\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12\1\uffff\1\13"+
            "\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff\1\13",
            "\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12\1\uffff\1\13"+
            "\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff\1\13",
            "\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12\1\uffff\1\13"+
            "\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff\1\13",
            "\1\30\5\uffff\1\31\1\26\10\uffff\1\27",
            "\1\30\5\uffff\1\31\1\26\10\uffff\1\27",
            "\1\30\5\uffff\1\31\1\26\10\uffff\1\27",
            "\1\34\5\uffff\1\35\1\32\10\uffff\1\33",
            "\1\34\5\uffff\1\35\1\32\10\uffff\1\33",
            "\1\34\5\uffff\1\35\1\32\10\uffff\1\33",
            "\1\40\32\uffff\1\37\1\36\3\uffff\1\41",
            "\1\44\32\uffff\1\43\1\42",
            "\1\47\32\uffff\1\46\1\45",
            "\1\11\1\uffff\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12"+
            "\1\uffff\1\13\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff"+
            "\1\13",
            "\1\52\32\uffff\1\51\1\50\3\uffff\1\53",
            "\1\56\32\uffff\1\55\1\54",
            "\1\61\32\uffff\1\60\1\57",
            "\1\13\26\uffff\3\13\4\uffff\1\12\1\uffff\2\12\1\uffff\1\13"+
            "\1\uffff\1\13\3\uffff\2\13\3\uffff\2\13\3\uffff\1\13",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31",
            "\1\30\5\uffff\1\31\11\uffff\1\27",
            "\1\30\5\uffff\1\31\11\uffff\1\27",
            "\1\30\5\uffff\1\31\11\uffff\1\27",
            "\1\30\5\uffff\1\31\1\64\10\uffff\1\63",
            "\1\30\5\uffff\1\31\1\64\10\uffff\1\63",
            "\1\30\5\uffff\1\31\1\64\10\uffff\1\63",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35",
            "\1\34\5\uffff\1\35\11\uffff\1\33",
            "\1\34\5\uffff\1\35\11\uffff\1\33",
            "\1\34\5\uffff\1\35\11\uffff\1\33",
            "\1\34\5\uffff\1\35\1\67\10\uffff\1\66",
            "\1\34\5\uffff\1\35\1\67\10\uffff\1\66",
            "\1\34\5\uffff\1\35\1\67\10\uffff\1\66",
            "\1\72\32\uffff\1\71\1\70",
            "\1\75\32\uffff\1\74\1\73",
            "\1\100\32\uffff\1\77\1\76\3\uffff\1\101",
            "\1\104\32\uffff\1\103\1\102",
            "\1\107\32\uffff\1\106\1\105",
            "\1\112\32\uffff\1\111\1\110\3\uffff\1\113",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\63",
            "\1\30\5\uffff\1\31\11\uffff\1\63",
            "\1\30\5\uffff\1\31\11\uffff\1\63",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\66",
            "\1\34\5\uffff\1\35\11\uffff\1\66",
            "\1\34\5\uffff\1\35\11\uffff\1\66",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35",
            "\1\120\32\uffff\1\117\1\116",
            "\1\123\32\uffff\1\122\1\121",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35\11\uffff\1\115"
    };

    static final short[] DFA65_eot = DFA.unpackEncodedString(DFA65_eotS);
    static final short[] DFA65_eof = DFA.unpackEncodedString(DFA65_eofS);
    static final char[] DFA65_min = DFA.unpackEncodedStringToUnsignedChars(DFA65_minS);
    static final char[] DFA65_max = DFA.unpackEncodedStringToUnsignedChars(DFA65_maxS);
    static final short[] DFA65_accept = DFA.unpackEncodedString(DFA65_acceptS);
    static final short[] DFA65_special = DFA.unpackEncodedString(DFA65_specialS);
    static final short[][] DFA65_transition;

    static {
        int numStates = DFA65_transitionS.length;
        DFA65_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA65_transition[i] = DFA.unpackEncodedString(DFA65_transitionS[i]);
        }
    }

    class DFA65 extends DFA {

        public DFA65(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 65;
            this.eot = DFA65_eot;
            this.eof = DFA65_eof;
            this.min = DFA65_min;
            this.max = DFA65_max;
            this.accept = DFA65_accept;
            this.special = DFA65_special;
            this.transition = DFA65_transition;
        }
        public String getDescription() {
            return "926:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );";
        }
    }
    static final String DFA72_eotS =
        "\23\uffff";
    static final String DFA72_eofS =
        "\11\uffff\1\2\11\uffff";
    static final String DFA72_minS =
        "\1\20\1\50\3\uffff\1\43\3\55\1\12\1\20\1\uffff\1\46\1\43\3\55\1"+
        "\20\1\46";
    static final String DFA72_maxS =
        "\1\77\1\50\3\uffff\1\77\3\55\1\71\1\20\1\uffff\1\51\1\77\3\55\1"+
        "\20\1\51";
    static final String DFA72_acceptS =
        "\2\uffff\1\2\1\3\1\4\6\uffff\1\1\7\uffff";
    static final String DFA72_specialS =
        "\23\uffff}>";
    static final String[] DFA72_transitionS = {
            "\1\4\22\uffff\1\1\4\uffff\1\3\25\uffff\2\2",
            "\1\5",
            "",
            "",
            "",
            "\1\10\5\uffff\1\11\24\uffff\1\7\1\6",
            "\1\12",
            "\1\12",
            "\1\12",
            "\2\13\33\uffff\1\2\1\uffff\1\2\11\uffff\1\2\5\uffff\1\2",
            "\1\14",
            "",
            "\1\15\2\uffff\1\11",
            "\1\20\32\uffff\1\17\1\16",
            "\1\21",
            "\1\21",
            "\1\21",
            "\1\22",
            "\1\15\2\uffff\1\11"
    };

    static final short[] DFA72_eot = DFA.unpackEncodedString(DFA72_eotS);
    static final short[] DFA72_eof = DFA.unpackEncodedString(DFA72_eofS);
    static final char[] DFA72_min = DFA.unpackEncodedStringToUnsignedChars(DFA72_minS);
    static final char[] DFA72_max = DFA.unpackEncodedStringToUnsignedChars(DFA72_maxS);
    static final short[] DFA72_accept = DFA.unpackEncodedString(DFA72_acceptS);
    static final short[] DFA72_special = DFA.unpackEncodedString(DFA72_specialS);
    static final short[][] DFA72_transition;

    static {
        int numStates = DFA72_transitionS.length;
        DFA72_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA72_transition[i] = DFA.unpackEncodedString(DFA72_transitionS[i]);
        }
    }

    class DFA72 extends DFA {

        public DFA72(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 72;
            this.eot = DFA72_eot;
            this.eof = DFA72_eof;
            this.min = DFA72_min;
            this.max = DFA72_max;
            this.accept = DFA72_accept;
            this.special = DFA72_special;
            this.transition = DFA72_transition;
        }
        public String getDescription() {
            return "961:1: rewriteTemplate : ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );";
        }
    }
 

    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec456 = new BitSet(new long[]{0x000000000F000000L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec493 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_grammarSpec495 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_grammarSpec497 = new BitSet(new long[]{0xC800000870F80040L});
    public static final BitSet FOLLOW_sync_in_grammarSpec541 = new BitSet(new long[]{0xC800000870F80040L});
    public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec545 = new BitSet(new long[]{0xC800000870F80040L});
    public static final BitSet FOLLOW_sync_in_grammarSpec547 = new BitSet(new long[]{0xC800000870F80040L});
    public static final BitSet FOLLOW_rules_in_grammarSpec577 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_grammarSpec620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_grammarType814 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARSER_in_grammarType851 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_in_grammarType882 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct1053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrScope_in_prequelConstruct1089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_prequelConstruct1132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec1149 = new BitSet(new long[]{0xE000000800000000L});
    public static final BitSet FOLLOW_option_in_optionsSpec1152 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_optionsSpec1154 = new BitSet(new long[]{0xE000000800000000L});
    public static final BitSet FOLLOW_RBRACE_in_optionsSpec1158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_option1195 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_option1197 = new BitSet(new long[]{0xC001000800000000L,0x0000000000000009L});
    public static final BitSet FOLLOW_optionValue_in_option1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_optionValue1250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_optionValue1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_optionValue1300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_optionValue1329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_delegateGrammars1345 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1347 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_COMMA_in_delegateGrammars1350 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1352 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_SEMI_in_delegateGrammars1356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1383 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar1385 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec1414 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec1416 = new BitSet(new long[]{0xE000000800000000L});
    public static final BitSet FOLLOW_RBRACE_in_tokensSpec1419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_tokenSpec1439 = new BitSet(new long[]{0x0000208000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec1445 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec1447 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_tokenSpec1482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_tokenSpec1487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope1502 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_attrScope1504 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope1506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_action1535 = new BitSet(new long[]{0xC000000803000000L});
    public static final BitSet FOLLOW_actionScopeName_in_action1538 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_COLONCOLON_in_action1540 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_action1544 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action1546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_actionScopeName1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_actionScopeName1582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARSER_in_actionScopeName1597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sync_in_rules1616 = new BitSet(new long[]{0xC000000870800042L});
    public static final BitSet FOLLOW_rule_in_rules1619 = new BitSet(new long[]{0xC000000870800040L});
    public static final BitSet FOLLOW_sync_in_rules1621 = new BitSet(new long[]{0xC000000870800042L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_rule1722 = new BitSet(new long[]{0xC000000870800000L});
    public static final BitSet FOLLOW_ruleModifiers_in_rule1766 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_rule1789 = new BitSet(new long[]{0x0800000180284000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule1822 = new BitSet(new long[]{0x0800000180284000L});
    public static final BitSet FOLLOW_ruleReturns_in_rule1832 = new BitSet(new long[]{0x0800000180284000L});
    public static final BitSet FOLLOW_rulePrequels_in_rule1870 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_COLON_in_rule1885 = new BitSet(new long[]{0xD648010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_ruleBlock_in_rule1914 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_rule1929 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule1938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup2030 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup2033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler2050 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler2052 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause2080 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause2082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sync_in_rulePrequels2116 = new BitSet(new long[]{0x0800000100280002L});
    public static final BitSet FOLLOW_rulePrequel_in_rulePrequels2119 = new BitSet(new long[]{0x0800000100280000L});
    public static final BitSet FOLLOW_sync_in_rulePrequels2121 = new BitSet(new long[]{0x0800000100280002L});
    public static final BitSet FOLLOW_throwsSpec_in_rulePrequel2147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rulePrequel2155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_rulePrequel2163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_rulePrequel2171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns2191 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns2194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROWS_in_throwsSpec2219 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2221 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_COMMA_in_throwsSpec2224 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2226 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2257 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec2259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2272 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2274 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_COMMA_in_ruleScopeSpec2277 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2279 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_SEMI_in_ruleScopeSpec2283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_ruleAction2313 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_ruleAction2315 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction2317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModifier_in_ruleModifiers2358 = new BitSet(new long[]{0x0000000070800002L});
    public static final BitSet FOLLOW_set_in_ruleModifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternative_in_altList2434 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_OR_in_altList2437 = new BitSet(new long[]{0xD648010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_alternative_in_altList2439 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_altList_in_ruleBlock2469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elements_in_alternative2519 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elements2619 = new BitSet(new long[]{0xD440010800010012L,0x0000000000000008L});
    public static final BitSet FOLLOW_labeledElement_in_element2656 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element2704 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element2753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element2760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element2770 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_element2776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element2804 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_labeledElement2871 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement2874 = new BitSet(new long[]{0xD040010800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement2877 = new BitSet(new long[]{0xD040010800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement2882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_labeledElement2884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec2902 = new BitSet(new long[]{0xD440010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_element_in_treeSpec2943 = new BitSet(new long[]{0xD440010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_element_in_treeSpec2974 = new BitSet(new long[]{0xD440030800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_RPAREN_in_treeSpec2983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_ebnf3017 = new BitSet(new long[]{0x0013C40000000002L});
    public static final BitSet FOLLOW_blockSuffixe_in_ebnf3052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffixe3103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffixe3117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffixe3125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffixe3136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_ebnfSuffix3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_ebnfSuffix3182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom3199 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom3254 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_DOT_in_atom3256 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_ruleref_in_atom3258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom3292 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_DOT_in_atom3294 = new BitSet(new long[]{0x4040000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_terminal_in_atom3296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom3321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom3331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notSet_in_atom3339 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet3385 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_notTerminal_in_notSet3387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet3403 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_block_in_notSet3405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_notTerminal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_block3479 = new BitSet(new long[]{0xDE48011900290010L,0x0000000000000008L});
    public static final BitSet FOLLOW_optionsSpec_in_block3516 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_ruleAction_in_block3521 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_COLON_in_block3524 = new BitSet(new long[]{0xD648010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_altList_in_block3538 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_block3555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref3604 = new BitSet(new long[]{0x0010800000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref3606 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_ruleref3616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_ruleref3620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rangeElement_in_range3694 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RANGE_in_range3696 = new BitSet(new long[]{0xC000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rangeElement_in_range3699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_rangeElement0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal3793 = new BitSet(new long[]{0x0010880000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal3795 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3798 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal3822 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3824 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_DOT_in_terminal3871 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3873 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal3904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_terminal3928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_elementOptions3992 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions3994 = new BitSet(new long[]{0x0000104000000000L});
    public static final BitSet FOLLOW_COMMA_in_elementOptions3997 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions3999 = new BitSet(new long[]{0x0000104000000000L});
    public static final BitSet FOLLOW_GT_in_elementOptions4003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_elementOption4038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_elementOption4060 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption4062 = new BitSet(new long[]{0xC000000800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_qid_in_elementOption4066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption4070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicatedRewrite_in_rewrite4088 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_nakedRewrite_in_rewrite4091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_predicatedRewrite4109 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite4111 = new BitSet(new long[]{0xC520010800010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite4113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_nakedRewrite4153 = new BitSet(new long[]{0xC520010800010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite4155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_rewriteAlt4219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteAlt4258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ETC_in_rewriteAlt4267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4298 = new BitSet(new long[]{0xC420010000010002L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4327 = new BitSet(new long[]{0x0003400000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTree_in_rewriteTreeElement4354 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom4415 = new BitSet(new long[]{0x0000080000004002L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom4417 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom4420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_rewriteTreeAtom4447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4454 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom4456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLAR_in_rewriteTreeAtom4479 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_rewriteTreeAtom4481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTreeAtom4492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTreeEbnf4518 = new BitSet(new long[]{0xC420010000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4520 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTreeEbnf4522 = new BitSet(new long[]{0x0003400000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewriteTree4548 = new BitSet(new long[]{0xC020000000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTree4550 = new BitSet(new long[]{0xC420030000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTree4552 = new BitSet(new long[]{0xC420030000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTree4555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate4587 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplate4589 = new BitSet(new long[]{0xC000020800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4591 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplate4593 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateRef_in_rewriteTemplate4633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplate4651 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateRef4667 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplateRef4669 = new BitSet(new long[]{0xC000020800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4671 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplateRef4673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4702 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteIndirectTemplateHead4704 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4706 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4708 = new BitSet(new long[]{0xC000020800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4710 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4740 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_COMMA_in_rewriteTemplateArgs4743 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4745 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateArg4774 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_rewriteTemplateArg4776 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplateArg4778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_id4820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_id4833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_id4845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_qid4879 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_DOT_in_qid4882 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_qid4884 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_alternative_in_alternativeEntry4901 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_alternativeEntry4903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elementEntry4912 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_elementEntry4914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_ruleEntry4922 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_ruleEntry4924 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_blockEntry4932 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_blockEntry4934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4258 = new BitSet(new long[]{0x0000000000000002L});

}