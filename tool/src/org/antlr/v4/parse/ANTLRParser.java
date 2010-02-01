// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ANTLRParser.g 2010-01-31 17:56:18

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

import org.antlr.v4.tool.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.tree.*;

/** The definitive ANTLR v3 grammar to parse ANTLR v4 grammars.
 *  The grammar builds ASTs that are sniffed by subsequent stages.
 */
public class ANTLRParser extends Parser {
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
    public static final int GRAMMAR=27;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int WSCHARS=65;
    public static final int RULEACTIONS=75;
    public static final int INITACTION=94;
    public static final int ALT_REWRITE=103;
    public static final int IMPLIES=42;
    public static final int RBRACE=61;
    public static final int RULE=72;
    public static final int ACTION_ESC=17;
    public static final int PARSER_GRAMMAR=91;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int INT=64;
    public static final int CHAR_RANGE=81;
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
    public static final int LABEL=95;
    public static final int TEMPLATE=35;
    public static final int SYN_SEMPRED=97;
    public static final int ERRCHAR=71;
    public static final int BLOCK=76;
    public static final int PLUS_ASSIGN=50;
    public static final int ASSIGN=45;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=79;
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


    public static class grammarSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "grammarSpec"
    // ANTLRParser.g:140:1: grammarSpec : ( DOC_COMMENT )? grammarType id SEMI ( prequelConstruct )* rules EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ) ;
    public final ANTLRParser.grammarSpec_return grammarSpec() throws RecognitionException {
        ANTLRParser.grammarSpec_return retval = new ANTLRParser.grammarSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOC_COMMENT1=null;
        Token SEMI4=null;
        Token EOF7=null;
        ANTLRParser.grammarType_return grammarType2 = null;

        ANTLRParser.id_return id3 = null;

        ANTLRParser.prequelConstruct_return prequelConstruct5 = null;

        ANTLRParser.rules_return rules6 = null;


        GrammarAST DOC_COMMENT1_tree=null;
        GrammarAST SEMI4_tree=null;
        GrammarAST EOF7_tree=null;
        RewriteRuleTokenStream stream_DOC_COMMENT=new RewriteRuleTokenStream(adaptor,"token DOC_COMMENT");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_prequelConstruct=new RewriteRuleSubtreeStream(adaptor,"rule prequelConstruct");
        RewriteRuleSubtreeStream stream_grammarType=new RewriteRuleSubtreeStream(adaptor,"rule grammarType");
        RewriteRuleSubtreeStream stream_rules=new RewriteRuleSubtreeStream(adaptor,"rule rules");
        try {
            // ANTLRParser.g:141:5: ( ( DOC_COMMENT )? grammarType id SEMI ( prequelConstruct )* rules EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ) )
            // ANTLRParser.g:145:7: ( DOC_COMMENT )? grammarType id SEMI ( prequelConstruct )* rules EOF
            {
            // ANTLRParser.g:145:7: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ANTLRParser.g:145:7: DOC_COMMENT
                    {
                    DOC_COMMENT1=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec476); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOC_COMMENT.add(DOC_COMMENT1);


                    }
                    break;

            }

            pushFollow(FOLLOW_grammarType_in_grammarSpec513);
            grammarType2=grammarType();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_grammarType.add(grammarType2.getTree());
            pushFollow(FOLLOW_id_in_grammarSpec515);
            id3=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id3.getTree());
            SEMI4=(Token)match(input,SEMI,FOLLOW_SEMI_in_grammarSpec517); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI4);

            // ANTLRParser.g:163:7: ( prequelConstruct )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=OPTIONS && LA2_0<=IMPORT)||LA2_0==AT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ANTLRParser.g:163:7: prequelConstruct
            	    {
            	    pushFollow(FOLLOW_prequelConstruct_in_grammarSpec561);
            	    prequelConstruct5=prequelConstruct();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_prequelConstruct.add(prequelConstruct5.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            pushFollow(FOLLOW_rules_in_grammarSpec589);
            rules6=rules();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rules.add(rules6.getTree());
            EOF7=(Token)match(input,EOF,FOLLOW_EOF_in_grammarSpec632); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF7);



            // AST REWRITE
            // elements: prequelConstruct, DOC_COMMENT, id, rules, grammarType
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 182:7: -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules )
            {
                // ANTLRParser.g:182:10: ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_grammarType.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:184:14: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:185:14: ( prequelConstruct )*
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
    // ANTLRParser.g:201:1: grammarType : ( LEXER -> LEXER_GRAMMAR | PARSER -> PARSER_GRAMMAR | TREE -> TREE_GRAMMAR | -> COMBINED_GRAMMAR ) GRAMMAR ;
    public final ANTLRParser.grammarType_return grammarType() throws RecognitionException {
        ANTLRParser.grammarType_return retval = new ANTLRParser.grammarType_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LEXER8=null;
        Token PARSER9=null;
        Token TREE10=null;
        Token GRAMMAR11=null;

        GrammarAST LEXER8_tree=null;
        GrammarAST PARSER9_tree=null;
        GrammarAST TREE10_tree=null;
        GrammarAST GRAMMAR11_tree=null;
        RewriteRuleTokenStream stream_TREE=new RewriteRuleTokenStream(adaptor,"token TREE");
        RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
        RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");
        RewriteRuleTokenStream stream_GRAMMAR=new RewriteRuleTokenStream(adaptor,"token GRAMMAR");

        try {
            // ANTLRParser.g:202:5: ( ( LEXER -> LEXER_GRAMMAR | PARSER -> PARSER_GRAMMAR | TREE -> TREE_GRAMMAR | -> COMBINED_GRAMMAR ) GRAMMAR )
            // ANTLRParser.g:202:7: ( LEXER -> LEXER_GRAMMAR | PARSER -> PARSER_GRAMMAR | TREE -> TREE_GRAMMAR | -> COMBINED_GRAMMAR ) GRAMMAR
            {
            // ANTLRParser.g:202:7: ( LEXER -> LEXER_GRAMMAR | PARSER -> PARSER_GRAMMAR | TREE -> TREE_GRAMMAR | -> COMBINED_GRAMMAR )
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
                    // ANTLRParser.g:203:11: LEXER
                    {
                    LEXER8=(Token)match(input,LEXER,FOLLOW_LEXER_in_grammarType830); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(LEXER8);



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
                    // 203:19: -> LEXER_GRAMMAR
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(LEXER_GRAMMAR, "LEXER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:206:11: PARSER
                    {
                    PARSER9=(Token)match(input,PARSER,FOLLOW_PARSER_in_grammarType870); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(PARSER9);



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
                    // 206:19: -> PARSER_GRAMMAR
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(PARSER_GRAMMAR, "PARSER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:209:11: TREE
                    {
                    TREE10=(Token)match(input,TREE,FOLLOW_TREE_in_grammarType909); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TREE.add(TREE10);



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
                    // 209:19: -> TREE_GRAMMAR
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(TREE_GRAMMAR, "TREE_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // ANTLRParser.g:212:19: 
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
                    // 212:19: -> COMBINED_GRAMMAR
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(COMBINED_GRAMMAR, "COMBINED_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }

            GRAMMAR11=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType978); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GRAMMAR.add(GRAMMAR11);


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
    // $ANTLR end "grammarType"

    public static class prequelConstruct_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "prequelConstruct"
    // ANTLRParser.g:220:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action );
    public final ANTLRParser.prequelConstruct_return prequelConstruct() throws RecognitionException {
        ANTLRParser.prequelConstruct_return retval = new ANTLRParser.prequelConstruct_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.optionsSpec_return optionsSpec12 = null;

        ANTLRParser.delegateGrammars_return delegateGrammars13 = null;

        ANTLRParser.tokensSpec_return tokensSpec14 = null;

        ANTLRParser.attrScope_return attrScope15 = null;

        ANTLRParser.action_return action16 = null;



        try {
            // ANTLRParser.g:221:2: ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action )
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
                    // ANTLRParser.g:222:4: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_prequelConstruct1003);
                    optionsSpec12=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec12.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:226:7: delegateGrammars
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct1029);
                    delegateGrammars13=delegateGrammars();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, delegateGrammars13.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:233:7: tokensSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_tokensSpec_in_prequelConstruct1079);
                    tokensSpec14=tokensSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tokensSpec14.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:238:7: attrScope
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_attrScope_in_prequelConstruct1115);
                    attrScope15=attrScope();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, attrScope15.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:244:7: action
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_action_in_prequelConstruct1158);
                    action16=action();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, action16.getTree());

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
    // ANTLRParser.g:248:1: optionsSpec : OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[\"OPTIONS\"] ( option )+ ) ;
    public final ANTLRParser.optionsSpec_return optionsSpec() throws RecognitionException {
        ANTLRParser.optionsSpec_return retval = new ANTLRParser.optionsSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token OPTIONS17=null;
        Token SEMI19=null;
        Token RBRACE20=null;
        ANTLRParser.option_return option18 = null;


        GrammarAST OPTIONS17_tree=null;
        GrammarAST SEMI19_tree=null;
        GrammarAST RBRACE20_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_OPTIONS=new RewriteRuleTokenStream(adaptor,"token OPTIONS");
        RewriteRuleSubtreeStream stream_option=new RewriteRuleSubtreeStream(adaptor,"rule option");
        try {
            // ANTLRParser.g:249:2: ( OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[\"OPTIONS\"] ( option )+ ) )
            // ANTLRParser.g:249:4: OPTIONS ( option SEMI )* RBRACE
            {
            OPTIONS17=(Token)match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec1175); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPTIONS.add(OPTIONS17);

            // ANTLRParser.g:249:12: ( option SEMI )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( ((LA5_0>=TOKEN_REF && LA5_0<=RULE_REF)) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ANTLRParser.g:249:13: option SEMI
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec1178);
            	    option18=option();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_option.add(option18.getTree());
            	    SEMI19=(Token)match(input,SEMI,FOLLOW_SEMI_in_optionsSpec1180); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEMI.add(SEMI19);


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            RBRACE20=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_optionsSpec1184); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE20);



            // AST REWRITE
            // elements: OPTIONS, option
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 249:34: -> ^( OPTIONS[\"OPTIONS\"] ( option )+ )
            {
                // ANTLRParser.g:249:37: ^( OPTIONS[\"OPTIONS\"] ( option )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(OPTIONS, "OPTIONS"), root_1);

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
    // ANTLRParser.g:252:1: option : id ASSIGN optionValue ;
    public final ANTLRParser.option_return option() throws RecognitionException {
        ANTLRParser.option_return retval = new ANTLRParser.option_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN22=null;
        ANTLRParser.id_return id21 = null;

        ANTLRParser.optionValue_return optionValue23 = null;


        GrammarAST ASSIGN22_tree=null;

        try {
            // ANTLRParser.g:253:5: ( id ASSIGN optionValue )
            // ANTLRParser.g:253:9: id ASSIGN optionValue
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_option1221);
            id21=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id21.getTree());
            ASSIGN22=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_option1223); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ASSIGN22_tree = (GrammarAST)adaptor.create(ASSIGN22);
            root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN22_tree, root_0);
            }
            pushFollow(FOLLOW_optionValue_in_option1226);
            optionValue23=optionValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, optionValue23.getTree());

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
    // ANTLRParser.g:261:1: optionValue : ( qid | STRING_LITERAL | INT | STAR );
    public final ANTLRParser.optionValue_return optionValue() throws RecognitionException {
        ANTLRParser.optionValue_return retval = new ANTLRParser.optionValue_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token STRING_LITERAL25=null;
        Token INT26=null;
        Token STAR27=null;
        ANTLRParser.qid_return qid24 = null;


        GrammarAST STRING_LITERAL25_tree=null;
        GrammarAST INT26_tree=null;
        GrammarAST STAR27_tree=null;

        try {
            // ANTLRParser.g:262:5: ( qid | STRING_LITERAL | INT | STAR )
            int alt6=4;
            switch ( input.LA(1) ) {
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
                    // ANTLRParser.g:266:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_optionValue1276);
                    qid24=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid24.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:270:7: STRING_LITERAL
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STRING_LITERAL25=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_optionValue1300); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL25_tree = (GrammarAST)adaptor.create(STRING_LITERAL25);
                    adaptor.addChild(root_0, STRING_LITERAL25_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:274:7: INT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    INT26=(Token)match(input,INT,FOLLOW_INT_in_optionValue1323); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT26_tree = (GrammarAST)adaptor.create(INT26);
                    adaptor.addChild(root_0, INT26_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:278:7: STAR
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STAR27=(Token)match(input,STAR,FOLLOW_STAR_in_optionValue1352); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STAR27_tree = (GrammarAST)adaptor.create(STAR27);
                    adaptor.addChild(root_0, STAR27_tree);
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
    // ANTLRParser.g:283:1: delegateGrammars : IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) ;
    public final ANTLRParser.delegateGrammars_return delegateGrammars() throws RecognitionException {
        ANTLRParser.delegateGrammars_return retval = new ANTLRParser.delegateGrammars_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token IMPORT28=null;
        Token COMMA30=null;
        Token SEMI32=null;
        ANTLRParser.delegateGrammar_return delegateGrammar29 = null;

        ANTLRParser.delegateGrammar_return delegateGrammar31 = null;


        GrammarAST IMPORT28_tree=null;
        GrammarAST COMMA30_tree=null;
        GrammarAST SEMI32_tree=null;
        RewriteRuleTokenStream stream_IMPORT=new RewriteRuleTokenStream(adaptor,"token IMPORT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_delegateGrammar=new RewriteRuleSubtreeStream(adaptor,"rule delegateGrammar");
        try {
            // ANTLRParser.g:284:2: ( IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) )
            // ANTLRParser.g:284:4: IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI
            {
            IMPORT28=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars1368); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IMPORT.add(IMPORT28);

            pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1370);
            delegateGrammar29=delegateGrammar();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar29.getTree());
            // ANTLRParser.g:284:27: ( COMMA delegateGrammar )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==COMMA) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ANTLRParser.g:284:28: COMMA delegateGrammar
            	    {
            	    COMMA30=(Token)match(input,COMMA,FOLLOW_COMMA_in_delegateGrammars1373); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA30);

            	    pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1375);
            	    delegateGrammar31=delegateGrammar();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar31.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            SEMI32=(Token)match(input,SEMI,FOLLOW_SEMI_in_delegateGrammars1379); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI32);



            // AST REWRITE
            // elements: IMPORT, delegateGrammar
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 284:57: -> ^( IMPORT ( delegateGrammar )+ )
            {
                // ANTLRParser.g:284:60: ^( IMPORT ( delegateGrammar )+ )
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
    // ANTLRParser.g:289:1: delegateGrammar : ( id ASSIGN id | id );
    public final ANTLRParser.delegateGrammar_return delegateGrammar() throws RecognitionException {
        ANTLRParser.delegateGrammar_return retval = new ANTLRParser.delegateGrammar_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN34=null;
        ANTLRParser.id_return id33 = null;

        ANTLRParser.id_return id35 = null;

        ANTLRParser.id_return id36 = null;


        GrammarAST ASSIGN34_tree=null;

        try {
            // ANTLRParser.g:290:5: ( id ASSIGN id | id )
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==RULE_REF) ) {
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
            else if ( (LA8_0==TOKEN_REF) ) {
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
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // ANTLRParser.g:290:9: id ASSIGN id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1406);
                    id33=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id33.getTree());
                    ASSIGN34=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar1408); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN34_tree = (GrammarAST)adaptor.create(ASSIGN34);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN34_tree, root_0);
                    }
                    pushFollow(FOLLOW_id_in_delegateGrammar1411);
                    id35=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id35.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:291:9: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1421);
                    id36=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id36.getTree());

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
    // ANTLRParser.g:294:1: tokensSpec : TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) ;
    public final ANTLRParser.tokensSpec_return tokensSpec() throws RecognitionException {
        ANTLRParser.tokensSpec_return retval = new ANTLRParser.tokensSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKENS37=null;
        Token RBRACE39=null;
        ANTLRParser.tokenSpec_return tokenSpec38 = null;


        GrammarAST TOKENS37_tree=null;
        GrammarAST RBRACE39_tree=null;
        RewriteRuleTokenStream stream_TOKENS=new RewriteRuleTokenStream(adaptor,"token TOKENS");
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleSubtreeStream stream_tokenSpec=new RewriteRuleSubtreeStream(adaptor,"rule tokenSpec");
        try {
            // ANTLRParser.g:301:2: ( TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) )
            // ANTLRParser.g:301:4: TOKENS ( tokenSpec )+ RBRACE
            {
            TOKENS37=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec1437); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TOKENS.add(TOKENS37);

            // ANTLRParser.g:301:11: ( tokenSpec )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0>=TOKEN_REF && LA9_0<=RULE_REF)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ANTLRParser.g:301:11: tokenSpec
            	    {
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec1439);
            	    tokenSpec38=tokenSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_tokenSpec.add(tokenSpec38.getTree());

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

            RBRACE39=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_tokensSpec1442); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE39);



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
            // 301:29: -> ^( TOKENS ( tokenSpec )+ )
            {
                // ANTLRParser.g:301:32: ^( TOKENS ( tokenSpec )+ )
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
    // ANTLRParser.g:304:1: tokenSpec : ( TOKEN_REF ( ASSIGN STRING_LITERAL -> ^( ASSIGN TOKEN_REF STRING_LITERAL ) | -> TOKEN_REF ) SEMI | RULE_REF );
    public final ANTLRParser.tokenSpec_return tokenSpec() throws RecognitionException {
        ANTLRParser.tokenSpec_return retval = new ANTLRParser.tokenSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF40=null;
        Token ASSIGN41=null;
        Token STRING_LITERAL42=null;
        Token SEMI43=null;
        Token RULE_REF44=null;

        GrammarAST TOKEN_REF40_tree=null;
        GrammarAST ASSIGN41_tree=null;
        GrammarAST STRING_LITERAL42_tree=null;
        GrammarAST SEMI43_tree=null;
        GrammarAST RULE_REF44_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");

        try {
            // ANTLRParser.g:305:2: ( TOKEN_REF ( ASSIGN STRING_LITERAL -> ^( ASSIGN TOKEN_REF STRING_LITERAL ) | -> TOKEN_REF ) SEMI | RULE_REF )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==TOKEN_REF) ) {
                alt11=1;
            }
            else if ( (LA11_0==RULE_REF) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // ANTLRParser.g:305:4: TOKEN_REF ( ASSIGN STRING_LITERAL -> ^( ASSIGN TOKEN_REF STRING_LITERAL ) | -> TOKEN_REF ) SEMI
                    {
                    TOKEN_REF40=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_tokenSpec1462); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF40);

                    // ANTLRParser.g:306:3: ( ASSIGN STRING_LITERAL -> ^( ASSIGN TOKEN_REF STRING_LITERAL ) | -> TOKEN_REF )
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
                            // ANTLRParser.g:306:5: ASSIGN STRING_LITERAL
                            {
                            ASSIGN41=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec1468); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN41);

                            STRING_LITERAL42=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec1470); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL42);



                            // AST REWRITE
                            // elements: ASSIGN, TOKEN_REF, STRING_LITERAL
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 306:27: -> ^( ASSIGN TOKEN_REF STRING_LITERAL )
                            {
                                // ANTLRParser.g:306:30: ^( ASSIGN TOKEN_REF STRING_LITERAL )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ASSIGN.nextNode(), root_1);

                                adaptor.addChild(root_1, stream_TOKEN_REF.nextNode());
                                adaptor.addChild(root_1, stream_STRING_LITERAL.nextNode());

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:307:11: 
                            {

                            // AST REWRITE
                            // elements: TOKEN_REF
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 307:11: -> TOKEN_REF
                            {
                                adaptor.addChild(root_0, stream_TOKEN_REF.nextNode());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }

                    SEMI43=(Token)match(input,SEMI,FOLLOW_SEMI_in_tokenSpec1502); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI43);


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:310:4: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF44=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_tokenSpec1507); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF44_tree = (GrammarAST)adaptor.create(RULE_REF44);
                    adaptor.addChild(root_0, RULE_REF44_tree);
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
    // ANTLRParser.g:316:1: attrScope : SCOPE id ACTION -> ^( SCOPE id ACTION ) ;
    public final ANTLRParser.attrScope_return attrScope() throws RecognitionException {
        ANTLRParser.attrScope_return retval = new ANTLRParser.attrScope_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token SCOPE45=null;
        Token ACTION47=null;
        ANTLRParser.id_return id46 = null;


        GrammarAST SCOPE45_tree=null;
        GrammarAST ACTION47_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:317:2: ( SCOPE id ACTION -> ^( SCOPE id ACTION ) )
            // ANTLRParser.g:317:4: SCOPE id ACTION
            {
            SCOPE45=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_attrScope1522); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE45);

            pushFollow(FOLLOW_id_in_attrScope1524);
            id46=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id46.getTree());
            ACTION47=(Token)match(input,ACTION,FOLLOW_ACTION_in_attrScope1526); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION47);



            // AST REWRITE
            // elements: id, SCOPE, ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 317:20: -> ^( SCOPE id ACTION )
            {
                // ANTLRParser.g:317:23: ^( SCOPE id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_SCOPE.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, stream_ACTION.nextNode());

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
    // ANTLRParser.g:323:1: action : AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) ;
    public final ANTLRParser.action_return action() throws RecognitionException {
        ANTLRParser.action_return retval = new ANTLRParser.action_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token AT48=null;
        Token COLONCOLON50=null;
        Token ACTION52=null;
        ANTLRParser.actionScopeName_return actionScopeName49 = null;

        ANTLRParser.id_return id51 = null;


        GrammarAST AT48_tree=null;
        GrammarAST COLONCOLON50_tree=null;
        GrammarAST ACTION52_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_COLONCOLON=new RewriteRuleTokenStream(adaptor,"token COLONCOLON");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_actionScopeName=new RewriteRuleSubtreeStream(adaptor,"rule actionScopeName");
        try {
            // ANTLRParser.g:325:2: ( AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) )
            // ANTLRParser.g:325:4: AT ( actionScopeName COLONCOLON )? id ACTION
            {
            AT48=(Token)match(input,AT,FOLLOW_AT_in_action1552); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT48);

            // ANTLRParser.g:325:7: ( actionScopeName COLONCOLON )?
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
                case LEXER:
                case PARSER:
                    {
                    alt12=1;
                    }
                    break;
            }

            switch (alt12) {
                case 1 :
                    // ANTLRParser.g:325:8: actionScopeName COLONCOLON
                    {
                    pushFollow(FOLLOW_actionScopeName_in_action1555);
                    actionScopeName49=actionScopeName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_actionScopeName.add(actionScopeName49.getTree());
                    COLONCOLON50=(Token)match(input,COLONCOLON,FOLLOW_COLONCOLON_in_action1557); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLONCOLON.add(COLONCOLON50);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_action1561);
            id51=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id51.getTree());
            ACTION52=(Token)match(input,ACTION,FOLLOW_ACTION_in_action1563); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION52);



            // AST REWRITE
            // elements: ACTION, id, actionScopeName, AT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 325:47: -> ^( AT ( actionScopeName )? id ACTION )
            {
                // ANTLRParser.g:325:50: ^( AT ( actionScopeName )? id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                // ANTLRParser.g:325:55: ( actionScopeName )?
                if ( stream_actionScopeName.hasNext() ) {
                    adaptor.addChild(root_1, stream_actionScopeName.nextTree());

                }
                stream_actionScopeName.reset();
                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, stream_ACTION.nextNode());

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
    // ANTLRParser.g:328:1: actionScopeName : ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] );
    public final ANTLRParser.actionScopeName_return actionScopeName() throws RecognitionException {
        ANTLRParser.actionScopeName_return retval = new ANTLRParser.actionScopeName_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LEXER54=null;
        Token PARSER55=null;
        ANTLRParser.id_return id53 = null;


        GrammarAST LEXER54_tree=null;
        GrammarAST PARSER55_tree=null;
        RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
        RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");

        try {
            // ANTLRParser.g:332:2: ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] )
            int alt13=3;
            switch ( input.LA(1) ) {
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
                    // ANTLRParser.g:332:4: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_actionScopeName1591);
                    id53=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id53.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:333:4: LEXER
                    {
                    LEXER54=(Token)match(input,LEXER,FOLLOW_LEXER_in_actionScopeName1596); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(LEXER54);



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
                    // 333:10: -> ID[$LEXER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, LEXER54));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:334:9: PARSER
                    {
                    PARSER55=(Token)match(input,PARSER,FOLLOW_PARSER_in_actionScopeName1611); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(PARSER55);



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
                    // 334:16: -> ID[$PARSER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, PARSER55));

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
    // ANTLRParser.g:337:1: rules : ( rule )* -> ^( RULES ( rule )* ) ;
    public final ANTLRParser.rules_return rules() throws RecognitionException {
        ANTLRParser.rules_return retval = new ANTLRParser.rules_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rule_return rule56 = null;


        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        try {
            // ANTLRParser.g:338:5: ( ( rule )* -> ^( RULES ( rule )* ) )
            // ANTLRParser.g:338:7: ( rule )*
            {
            // ANTLRParser.g:338:7: ( rule )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==DOC_COMMENT||LA14_0==FRAGMENT||(LA14_0>=PROTECTED && LA14_0<=PRIVATE)||(LA14_0>=TOKEN_REF && LA14_0<=RULE_REF)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ANTLRParser.g:338:7: rule
            	    {
            	    pushFollow(FOLLOW_rule_in_rules1630);
            	    rule56=rule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule.add(rule56.getTree());

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
            // 342:7: -> ^( RULES ( rule )* )
            {
                // ANTLRParser.g:342:9: ^( RULES ( rule )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULES, "RULES"), root_1);

                // ANTLRParser.g:342:17: ( rule )*
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

    public static class rule_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // ANTLRParser.g:354:1: rule : ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* COLON altListAsBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* ) ;
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

        ANTLRParser.rulePrequel_return rulePrequel62 = null;

        ANTLRParser.altListAsBlock_return altListAsBlock64 = null;

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
        RewriteRuleSubtreeStream stream_rulePrequel=new RewriteRuleSubtreeStream(adaptor,"rule rulePrequel");
        RewriteRuleSubtreeStream stream_exceptionGroup=new RewriteRuleSubtreeStream(adaptor,"rule exceptionGroup");
        RewriteRuleSubtreeStream stream_ruleReturns=new RewriteRuleSubtreeStream(adaptor,"rule ruleReturns");
        RewriteRuleSubtreeStream stream_altListAsBlock=new RewriteRuleSubtreeStream(adaptor,"rule altListAsBlock");
        try {
            // ANTLRParser.g:355:5: ( ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* COLON altListAsBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* ) )
            // ANTLRParser.g:356:7: ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* COLON altListAsBlock SEMI exceptionGroup
            {
            // ANTLRParser.g:356:7: ( DOC_COMMENT )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==DOC_COMMENT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ANTLRParser.g:356:7: DOC_COMMENT
                    {
                    DOC_COMMENT57=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_rule1709); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOC_COMMENT.add(DOC_COMMENT57);


                    }
                    break;

            }

            // ANTLRParser.g:362:7: ( ruleModifiers )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==FRAGMENT||(LA16_0>=PROTECTED && LA16_0<=PRIVATE)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ANTLRParser.g:362:7: ruleModifiers
                    {
                    pushFollow(FOLLOW_ruleModifiers_in_rule1753);
                    ruleModifiers58=ruleModifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleModifiers.add(ruleModifiers58.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_rule1776);
            id59=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id59.getTree());
            // ANTLRParser.g:376:4: ( ARG_ACTION )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==ARG_ACTION) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ANTLRParser.g:376:4: ARG_ACTION
                    {
                    ARG_ACTION60=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule1809); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION60);


                    }
                    break;

            }

            // ANTLRParser.g:378:4: ( ruleReturns )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RETURNS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ANTLRParser.g:378:4: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_rule1819);
                    ruleReturns61=ruleReturns();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleReturns.add(ruleReturns61.getTree());

                    }
                    break;

            }

            // ANTLRParser.g:393:7: ( rulePrequel )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==OPTIONS||LA19_0==SCOPE||LA19_0==THROWS||LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // ANTLRParser.g:393:7: rulePrequel
            	    {
            	    pushFollow(FOLLOW_rulePrequel_in_rule1857);
            	    rulePrequel62=rulePrequel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rulePrequel.add(rulePrequel62.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            COLON63=(Token)match(input,COLON,FOLLOW_COLON_in_rule1873); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON63);

            pushFollow(FOLLOW_altListAsBlock_in_rule1902);
            altListAsBlock64=altListAsBlock();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altListAsBlock.add(altListAsBlock64.getTree());
            SEMI65=(Token)match(input,SEMI,FOLLOW_SEMI_in_rule1917); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI65);

            pushFollow(FOLLOW_exceptionGroup_in_rule1926);
            exceptionGroup66=exceptionGroup();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exceptionGroup.add(exceptionGroup66.getTree());


            // AST REWRITE
            // elements: altListAsBlock, DOC_COMMENT, ARG_ACTION, exceptionGroup, id, ruleReturns, ruleModifiers, rulePrequel
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 405:7: -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* )
            {
                // ANTLRParser.g:405:10: ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULE, "RULE"), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:405:21: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:405:34: ( ruleModifiers )?
                if ( stream_ruleModifiers.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleModifiers.nextTree());

                }
                stream_ruleModifiers.reset();
                // ANTLRParser.g:405:49: ( ARG_ACTION )?
                if ( stream_ARG_ACTION.hasNext() ) {
                    adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                }
                stream_ARG_ACTION.reset();
                // ANTLRParser.g:406:9: ( ruleReturns )?
                if ( stream_ruleReturns.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleReturns.nextTree());

                }
                stream_ruleReturns.reset();
                // ANTLRParser.g:406:22: ( rulePrequel )*
                while ( stream_rulePrequel.hasNext() ) {
                    adaptor.addChild(root_1, stream_rulePrequel.nextTree());

                }
                stream_rulePrequel.reset();
                adaptor.addChild(root_1, stream_altListAsBlock.nextTree());
                // ANTLRParser.g:406:50: ( exceptionGroup )*
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
    // ANTLRParser.g:416:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
    public final ANTLRParser.exceptionGroup_return exceptionGroup() throws RecognitionException {
        ANTLRParser.exceptionGroup_return retval = new ANTLRParser.exceptionGroup_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.exceptionHandler_return exceptionHandler67 = null;

        ANTLRParser.finallyClause_return finallyClause68 = null;



        try {
            // ANTLRParser.g:417:5: ( ( exceptionHandler )* ( finallyClause )? )
            // ANTLRParser.g:417:7: ( exceptionHandler )* ( finallyClause )?
            {
            root_0 = (GrammarAST)adaptor.nil();

            // ANTLRParser.g:417:7: ( exceptionHandler )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==CATCH) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ANTLRParser.g:417:7: exceptionHandler
            	    {
            	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup2015);
            	    exceptionHandler67=exceptionHandler();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exceptionHandler67.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            // ANTLRParser.g:417:25: ( finallyClause )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==FINALLY) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ANTLRParser.g:417:25: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup2018);
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
    // ANTLRParser.g:422:1: exceptionHandler : CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) ;
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
            // ANTLRParser.g:423:2: ( CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) )
            // ANTLRParser.g:423:4: CATCH ARG_ACTION ACTION
            {
            CATCH69=(Token)match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler2035); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CATCH.add(CATCH69);

            ARG_ACTION70=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler2037); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION70);

            ACTION71=(Token)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler2039); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION71);



            // AST REWRITE
            // elements: ACTION, CATCH, ARG_ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 423:28: -> ^( CATCH ARG_ACTION ACTION )
            {
                // ANTLRParser.g:423:31: ^( CATCH ARG_ACTION ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_CATCH.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());
                adaptor.addChild(root_1, stream_ACTION.nextNode());

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
    // ANTLRParser.g:428:1: finallyClause : FINALLY ACTION -> ^( FINALLY ACTION ) ;
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
            // ANTLRParser.g:429:2: ( FINALLY ACTION -> ^( FINALLY ACTION ) )
            // ANTLRParser.g:429:4: FINALLY ACTION
            {
            FINALLY72=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause2062); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FINALLY.add(FINALLY72);

            ACTION73=(Token)match(input,ACTION,FOLLOW_ACTION_in_finallyClause2064); if (state.failed) return retval; 
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
            // 429:19: -> ^( FINALLY ACTION )
            {
                // ANTLRParser.g:429:22: ^( FINALLY ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_FINALLY.nextNode(), root_1);

                adaptor.addChild(root_1, stream_ACTION.nextNode());

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

    public static class rulePrequel_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rulePrequel"
    // ANTLRParser.g:435:1: rulePrequel : ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction );
    public final ANTLRParser.rulePrequel_return rulePrequel() throws RecognitionException {
        ANTLRParser.rulePrequel_return retval = new ANTLRParser.rulePrequel_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.throwsSpec_return throwsSpec74 = null;

        ANTLRParser.ruleScopeSpec_return ruleScopeSpec75 = null;

        ANTLRParser.optionsSpec_return optionsSpec76 = null;

        ANTLRParser.ruleAction_return ruleAction77 = null;



        try {
            // ANTLRParser.g:436:5: ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction )
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
                    // ANTLRParser.g:436:7: throwsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_throwsSpec_in_rulePrequel2091);
                    throwsSpec74=throwsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, throwsSpec74.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:437:7: ruleScopeSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleScopeSpec_in_rulePrequel2099);
                    ruleScopeSpec75=ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleScopeSpec75.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:438:7: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_rulePrequel2107);
                    optionsSpec76=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec76.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:439:7: ruleAction
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleAction_in_rulePrequel2115);
                    ruleAction77=ruleAction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleAction77.getTree());

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
    // ANTLRParser.g:448:1: ruleReturns : RETURNS ARG_ACTION ;
    public final ANTLRParser.ruleReturns_return ruleReturns() throws RecognitionException {
        ANTLRParser.ruleReturns_return retval = new ANTLRParser.ruleReturns_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RETURNS78=null;
        Token ARG_ACTION79=null;

        GrammarAST RETURNS78_tree=null;
        GrammarAST ARG_ACTION79_tree=null;

        try {
            // ANTLRParser.g:449:2: ( RETURNS ARG_ACTION )
            // ANTLRParser.g:449:4: RETURNS ARG_ACTION
            {
            root_0 = (GrammarAST)adaptor.nil();

            RETURNS78=(Token)match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns2135); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RETURNS78_tree = (GrammarAST)adaptor.create(RETURNS78);
            root_0 = (GrammarAST)adaptor.becomeRoot(RETURNS78_tree, root_0);
            }
            ARG_ACTION79=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns2138); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ARG_ACTION79_tree = (GrammarAST)adaptor.create(ARG_ACTION79);
            adaptor.addChild(root_0, ARG_ACTION79_tree);
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
    // ANTLRParser.g:463:1: throwsSpec : THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) ;
    public final ANTLRParser.throwsSpec_return throwsSpec() throws RecognitionException {
        ANTLRParser.throwsSpec_return retval = new ANTLRParser.throwsSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token THROWS80=null;
        Token COMMA82=null;
        ANTLRParser.qid_return qid81 = null;

        ANTLRParser.qid_return qid83 = null;


        GrammarAST THROWS80_tree=null;
        GrammarAST COMMA82_tree=null;
        RewriteRuleTokenStream stream_THROWS=new RewriteRuleTokenStream(adaptor,"token THROWS");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // ANTLRParser.g:464:5: ( THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) )
            // ANTLRParser.g:464:7: THROWS qid ( COMMA qid )*
            {
            THROWS80=(Token)match(input,THROWS,FOLLOW_THROWS_in_throwsSpec2163); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_THROWS.add(THROWS80);

            pushFollow(FOLLOW_qid_in_throwsSpec2165);
            qid81=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid81.getTree());
            // ANTLRParser.g:464:18: ( COMMA qid )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==COMMA) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ANTLRParser.g:464:19: COMMA qid
            	    {
            	    COMMA82=(Token)match(input,COMMA,FOLLOW_COMMA_in_throwsSpec2168); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA82);

            	    pushFollow(FOLLOW_qid_in_throwsSpec2170);
            	    qid83=qid();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_qid.add(qid83.getTree());

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);



            // AST REWRITE
            // elements: THROWS, qid
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 464:31: -> ^( THROWS ( qid )+ )
            {
                // ANTLRParser.g:464:34: ^( THROWS ( qid )+ )
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
    // ANTLRParser.g:471:1: ruleScopeSpec : ( SCOPE ACTION -> ^( SCOPE ACTION ) | SCOPE id ( COMMA id )* SEMI -> ^( SCOPE ( id )+ ) );
    public final ANTLRParser.ruleScopeSpec_return ruleScopeSpec() throws RecognitionException {
        ANTLRParser.ruleScopeSpec_return retval = new ANTLRParser.ruleScopeSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token SCOPE84=null;
        Token ACTION85=null;
        Token SCOPE86=null;
        Token COMMA88=null;
        Token SEMI90=null;
        ANTLRParser.id_return id87 = null;

        ANTLRParser.id_return id89 = null;


        GrammarAST SCOPE84_tree=null;
        GrammarAST ACTION85_tree=null;
        GrammarAST SCOPE86_tree=null;
        GrammarAST COMMA88_tree=null;
        GrammarAST SEMI90_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:472:2: ( SCOPE ACTION -> ^( SCOPE ACTION ) | SCOPE id ( COMMA id )* SEMI -> ^( SCOPE ( id )+ ) )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==SCOPE) ) {
                int LA25_1 = input.LA(2);

                if ( (LA25_1==ACTION) ) {
                    alt25=1;
                }
                else if ( ((LA25_1>=TOKEN_REF && LA25_1<=RULE_REF)) ) {
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
                    // ANTLRParser.g:472:4: SCOPE ACTION
                    {
                    SCOPE84=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2201); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE84);

                    ACTION85=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec2203); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ACTION.add(ACTION85);



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
                    // 472:17: -> ^( SCOPE ACTION )
                    {
                        // ANTLRParser.g:472:20: ^( SCOPE ACTION )
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
                    // ANTLRParser.g:473:4: SCOPE id ( COMMA id )* SEMI
                    {
                    SCOPE86=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2216); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE86);

                    pushFollow(FOLLOW_id_in_ruleScopeSpec2218);
                    id87=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id87.getTree());
                    // ANTLRParser.g:473:13: ( COMMA id )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==COMMA) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // ANTLRParser.g:473:14: COMMA id
                    	    {
                    	    COMMA88=(Token)match(input,COMMA,FOLLOW_COMMA_in_ruleScopeSpec2221); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA88);

                    	    pushFollow(FOLLOW_id_in_ruleScopeSpec2223);
                    	    id89=id();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_id.add(id89.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);

                    SEMI90=(Token)match(input,SEMI,FOLLOW_SEMI_in_ruleScopeSpec2227); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI90);



                    // AST REWRITE
                    // elements: SCOPE, id
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 473:30: -> ^( SCOPE ( id )+ )
                    {
                        // ANTLRParser.g:473:33: ^( SCOPE ( id )+ )
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
    // ANTLRParser.g:484:1: ruleAction : AT id ACTION -> ^( AT id ACTION ) ;
    public final ANTLRParser.ruleAction_return ruleAction() throws RecognitionException {
        ANTLRParser.ruleAction_return retval = new ANTLRParser.ruleAction_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token AT91=null;
        Token ACTION93=null;
        ANTLRParser.id_return id92 = null;


        GrammarAST AT91_tree=null;
        GrammarAST ACTION93_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:486:2: ( AT id ACTION -> ^( AT id ACTION ) )
            // ANTLRParser.g:486:4: AT id ACTION
            {
            AT91=(Token)match(input,AT,FOLLOW_AT_in_ruleAction2257); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT91);

            pushFollow(FOLLOW_id_in_ruleAction2259);
            id92=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id92.getTree());
            ACTION93=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleAction2261); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION93);



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
            // 486:17: -> ^( AT id ACTION )
            {
                // ANTLRParser.g:486:20: ^( AT id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, stream_ACTION.nextNode());

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
    // ANTLRParser.g:494:1: ruleModifiers : ( ruleModifier )+ -> ^( RULEMODIFIERS ( ruleModifier )+ ) ;
    public final ANTLRParser.ruleModifiers_return ruleModifiers() throws RecognitionException {
        ANTLRParser.ruleModifiers_return retval = new ANTLRParser.ruleModifiers_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.ruleModifier_return ruleModifier94 = null;


        RewriteRuleSubtreeStream stream_ruleModifier=new RewriteRuleSubtreeStream(adaptor,"rule ruleModifier");
        try {
            // ANTLRParser.g:495:5: ( ( ruleModifier )+ -> ^( RULEMODIFIERS ( ruleModifier )+ ) )
            // ANTLRParser.g:495:7: ( ruleModifier )+
            {
            // ANTLRParser.g:495:7: ( ruleModifier )+
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
            	    // ANTLRParser.g:495:7: ruleModifier
            	    {
            	    pushFollow(FOLLOW_ruleModifier_in_ruleModifiers2299);
            	    ruleModifier94=ruleModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ruleModifier.add(ruleModifier94.getTree());

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
            // 495:21: -> ^( RULEMODIFIERS ( ruleModifier )+ )
            {
                // ANTLRParser.g:495:24: ^( RULEMODIFIERS ( ruleModifier )+ )
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
    // ANTLRParser.g:504:1: ruleModifier : ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT );
    public final ANTLRParser.ruleModifier_return ruleModifier() throws RecognitionException {
        ANTLRParser.ruleModifier_return retval = new ANTLRParser.ruleModifier_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set95=null;

        GrammarAST set95_tree=null;

        try {
            // ANTLRParser.g:505:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set95=(Token)input.LT(1);
            if ( input.LA(1)==FRAGMENT||(input.LA(1)>=PROTECTED && input.LA(1)<=PRIVATE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set95));
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
    // ANTLRParser.g:511:1: altList : alternative ( OR alternative )* -> ( alternative )+ ;
    public final ANTLRParser.altList_return altList() throws RecognitionException {
        ANTLRParser.altList_return retval = new ANTLRParser.altList_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token OR97=null;
        ANTLRParser.alternative_return alternative96 = null;

        ANTLRParser.alternative_return alternative98 = null;


        GrammarAST OR97_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // ANTLRParser.g:512:5: ( alternative ( OR alternative )* -> ( alternative )+ )
            // ANTLRParser.g:512:7: alternative ( OR alternative )*
            {
            pushFollow(FOLLOW_alternative_in_altList2375);
            alternative96=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_alternative.add(alternative96.getTree());
            // ANTLRParser.g:512:19: ( OR alternative )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==OR) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ANTLRParser.g:512:20: OR alternative
            	    {
            	    OR97=(Token)match(input,OR,FOLLOW_OR_in_altList2378); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_OR.add(OR97);

            	    pushFollow(FOLLOW_alternative_in_altList2380);
            	    alternative98=alternative();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_alternative.add(alternative98.getTree());

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
            // 512:37: -> ( alternative )+
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

    public static class altListAsBlock_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "altListAsBlock"
    // ANTLRParser.g:521:1: altListAsBlock : altList -> ^( BLOCK altList ) ;
    public final ANTLRParser.altListAsBlock_return altListAsBlock() throws RecognitionException {
        ANTLRParser.altListAsBlock_return retval = new ANTLRParser.altListAsBlock_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.altList_return altList99 = null;


        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        try {
            // ANTLRParser.g:522:5: ( altList -> ^( BLOCK altList ) )
            // ANTLRParser.g:522:7: altList
            {
            pushFollow(FOLLOW_altList_in_altListAsBlock2410);
            altList99=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList99.getTree());


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
            // 522:15: -> ^( BLOCK altList )
            {
                // ANTLRParser.g:522:18: ^( BLOCK altList )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, "BLOCK"), root_1);

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
    // $ANTLR end "altListAsBlock"

    public static class alternative_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "alternative"
    // ANTLRParser.g:527:1: alternative : ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) );
    public final ANTLRParser.alternative_return alternative() throws RecognitionException {
        ANTLRParser.alternative_return retval = new ANTLRParser.alternative_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.elements_return elements100 = null;

        ANTLRParser.rewrite_return rewrite101 = null;

        ANTLRParser.rewrite_return rewrite102 = null;


        RewriteRuleSubtreeStream stream_rewrite=new RewriteRuleSubtreeStream(adaptor,"rule rewrite");
        RewriteRuleSubtreeStream stream_elements=new RewriteRuleSubtreeStream(adaptor,"rule elements");
        try {
            // ANTLRParser.g:528:5: ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) )
            int alt29=3;
            switch ( input.LA(1) ) {
            case SEMPRED:
            case ACTION:
            case LPAREN:
            case TREE_BEGIN:
            case NOT:
            case TOKEN_REF:
            case RULE_REF:
            case STRING_LITERAL:
            case DOT:
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
                    // ANTLRParser.g:528:7: elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
                    {
                    pushFollow(FOLLOW_elements_in_alternative2437);
                    elements100=elements();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_elements.add(elements100.getTree());
                    // ANTLRParser.g:529:6: ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
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
                            // ANTLRParser.g:529:8: rewrite
                            {
                            pushFollow(FOLLOW_rewrite_in_alternative2446);
                            rewrite101=rewrite();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_rewrite.add(rewrite101.getTree());


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
                            // 529:16: -> ^( ALT_REWRITE elements rewrite )
                            {
                                // ANTLRParser.g:529:19: ^( ALT_REWRITE elements rewrite )
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
                            // ANTLRParser.g:530:10: 
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
                            // 530:10: -> elements
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
                    // ANTLRParser.g:532:7: rewrite
                    {
                    pushFollow(FOLLOW_rewrite_in_alternative2484);
                    rewrite102=rewrite();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewrite.add(rewrite102.getTree());


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
                    // 532:16: -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                    {
                        // ANTLRParser.g:532:19: ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT_REWRITE, "ALT_REWRITE"), root_1);

                        // ANTLRParser.g:532:33: ^( ALT EPSILON )
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
                    // ANTLRParser.g:533:10: 
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
                    // 533:10: -> ^( ALT EPSILON )
                    {
                        // ANTLRParser.g:533:13: ^( ALT EPSILON )
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
    // ANTLRParser.g:536:1: elements : (e+= element )+ -> ^( ALT ( $e)+ ) ;
    public final ANTLRParser.elements_return elements() throws RecognitionException {
        ANTLRParser.elements_return retval = new ANTLRParser.elements_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        List list_e=null;
        RuleReturnScope e = null;
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:537:5: ( (e+= element )+ -> ^( ALT ( $e)+ ) )
            // ANTLRParser.g:537:7: (e+= element )+
            {
            // ANTLRParser.g:537:8: (e+= element )+
            int cnt30=0;
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( (LA30_0==SEMPRED||LA30_0==ACTION||LA30_0==LPAREN||LA30_0==TREE_BEGIN||LA30_0==NOT||(LA30_0>=TOKEN_REF && LA30_0<=RULE_REF)||LA30_0==STRING_LITERAL||LA30_0==DOT) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // ANTLRParser.g:537:8: e+= element
            	    {
            	    pushFollow(FOLLOW_element_in_elements2537);
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
            // 537:19: -> ^( ALT ( $e)+ )
            {
                // ANTLRParser.g:537:22: ^( ALT ( $e)+ )
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
    // ANTLRParser.g:540:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] treeSpec ) ) ) | -> treeSpec ) );
    public final ANTLRParser.element_return element() throws RecognitionException {
        ANTLRParser.element_return retval = new ANTLRParser.element_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ACTION108=null;
        Token SEMPRED109=null;
        Token IMPLIES110=null;
        ANTLRParser.labeledElement_return labeledElement103 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix104 = null;

        ANTLRParser.atom_return atom105 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix106 = null;

        ANTLRParser.ebnf_return ebnf107 = null;

        ANTLRParser.treeSpec_return treeSpec111 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix112 = null;


        GrammarAST ACTION108_tree=null;
        GrammarAST SEMPRED109_tree=null;
        GrammarAST IMPLIES110_tree=null;
        RewriteRuleTokenStream stream_IMPLIES=new RewriteRuleTokenStream(adaptor,"token IMPLIES");
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        RewriteRuleSubtreeStream stream_treeSpec=new RewriteRuleSubtreeStream(adaptor,"rule treeSpec");
        RewriteRuleSubtreeStream stream_labeledElement=new RewriteRuleSubtreeStream(adaptor,"rule labeledElement");
        try {
            // ANTLRParser.g:541:2: ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] treeSpec ) ) ) | -> treeSpec ) )
            int alt35=6;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                int LA35_1 = input.LA(2);

                if ( (LA35_1==ASSIGN||LA35_1==PLUS_ASSIGN) ) {
                    alt35=1;
                }
                else if ( (LA35_1==EOF||LA35_1==SEMPRED||LA35_1==ARG_ACTION||LA35_1==ACTION||(LA35_1>=SEMI && LA35_1<=RPAREN)||(LA35_1>=QUESTION && LA35_1<=PLUS)||(LA35_1>=OR && LA35_1<=ROOT)||(LA35_1>=WILDCARD && LA35_1<=RANGE)||(LA35_1>=RARROW && LA35_1<=TREE_BEGIN)||LA35_1==NOT||(LA35_1>=TOKEN_REF && LA35_1<=RULE_REF)||LA35_1==STRING_LITERAL||LA35_1==DOT) ) {
                    alt35=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 35, 1, input);

                    throw nvae;
                }
                }
                break;
            case TOKEN_REF:
                {
                int LA35_2 = input.LA(2);

                if ( (LA35_2==EOF||LA35_2==SEMPRED||LA35_2==ARG_ACTION||LA35_2==ACTION||(LA35_2>=SEMI && LA35_2<=RPAREN)||LA35_2==LT||(LA35_2>=QUESTION && LA35_2<=PLUS)||(LA35_2>=OR && LA35_2<=ROOT)||(LA35_2>=WILDCARD && LA35_2<=RANGE)||(LA35_2>=RARROW && LA35_2<=TREE_BEGIN)||LA35_2==NOT||(LA35_2>=TOKEN_REF && LA35_2<=RULE_REF)||LA35_2==STRING_LITERAL||LA35_2==DOT) ) {
                    alt35=2;
                }
                else if ( (LA35_2==ASSIGN||LA35_2==PLUS_ASSIGN) ) {
                    alt35=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 35, 2, input);

                    throw nvae;
                }
                }
                break;
            case NOT:
            case STRING_LITERAL:
            case DOT:
                {
                alt35=2;
                }
                break;
            case LPAREN:
                {
                alt35=3;
                }
                break;
            case ACTION:
                {
                alt35=4;
                }
                break;
            case SEMPRED:
                {
                alt35=5;
                }
                break;
            case TREE_BEGIN:
                {
                alt35=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // ANTLRParser.g:541:4: labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] labeledElement ) ) ) | -> labeledElement )
                    {
                    pushFollow(FOLLOW_labeledElement_in_element2564);
                    labeledElement103=labeledElement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_labeledElement.add(labeledElement103.getTree());
                    // ANTLRParser.g:542:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] labeledElement ) ) ) | -> labeledElement )
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==QUESTION||(LA31_0>=STAR && LA31_0<=PLUS)) ) {
                        alt31=1;
                    }
                    else if ( (LA31_0==EOF||LA31_0==SEMPRED||LA31_0==ACTION||(LA31_0>=SEMI && LA31_0<=RPAREN)||LA31_0==OR||(LA31_0>=RARROW && LA31_0<=TREE_BEGIN)||LA31_0==NOT||(LA31_0>=TOKEN_REF && LA31_0<=RULE_REF)||LA31_0==STRING_LITERAL||LA31_0==DOT) ) {
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
                            // ANTLRParser.g:542:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2570);
                            ebnfSuffix104=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix104.getTree());


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
                            // 542:16: -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] labeledElement ) ) )
                            {
                                // ANTLRParser.g:542:19: ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] labeledElement ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:542:33: ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] labeledElement ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, "BLOCK"), root_2);

                                // ANTLRParser.g:542:50: ^( ALT[\"ALT\"] labeledElement )
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
                            // ANTLRParser.g:543:8: 
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
                            // 543:8: -> labeledElement
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
                    // ANTLRParser.g:545:4: atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] atom ) ) ) | -> atom )
                    {
                    pushFollow(FOLLOW_atom_in_element2611);
                    atom105=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_atom.add(atom105.getTree());
                    // ANTLRParser.g:546:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] atom ) ) ) | -> atom )
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==QUESTION||(LA32_0>=STAR && LA32_0<=PLUS)) ) {
                        alt32=1;
                    }
                    else if ( (LA32_0==EOF||LA32_0==SEMPRED||LA32_0==ACTION||(LA32_0>=SEMI && LA32_0<=RPAREN)||LA32_0==OR||(LA32_0>=RARROW && LA32_0<=TREE_BEGIN)||LA32_0==NOT||(LA32_0>=TOKEN_REF && LA32_0<=RULE_REF)||LA32_0==STRING_LITERAL||LA32_0==DOT) ) {
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
                            // ANTLRParser.g:546:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2617);
                            ebnfSuffix106=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix106.getTree());


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
                            // 546:16: -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] atom ) ) )
                            {
                                // ANTLRParser.g:546:19: ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] atom ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:546:33: ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] atom ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, "BLOCK"), root_2);

                                // ANTLRParser.g:546:50: ^( ALT[\"ALT\"] atom )
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
                            // ANTLRParser.g:547:8: 
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
                            // 547:8: -> atom
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
                    // ANTLRParser.g:549:4: ebnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnf_in_element2659);
                    ebnf107=ebnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnf107.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:550:6: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION108=(Token)match(input,ACTION,FOLLOW_ACTION_in_element2666); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION108_tree = (GrammarAST)adaptor.create(ACTION108);
                    adaptor.addChild(root_0, ACTION108_tree);
                    }

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:551:6: SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED )
                    {
                    SEMPRED109=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_element2673); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED109);

                    // ANTLRParser.g:552:3: ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED )
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==IMPLIES) ) {
                        alt33=1;
                    }
                    else if ( (LA33_0==EOF||LA33_0==SEMPRED||LA33_0==ACTION||(LA33_0>=SEMI && LA33_0<=RPAREN)||LA33_0==OR||(LA33_0>=RARROW && LA33_0<=TREE_BEGIN)||LA33_0==NOT||(LA33_0>=TOKEN_REF && LA33_0<=RULE_REF)||LA33_0==STRING_LITERAL||LA33_0==DOT) ) {
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
                            // ANTLRParser.g:552:5: IMPLIES
                            {
                            IMPLIES110=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_element2679); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IMPLIES.add(IMPLIES110);



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
                            // 552:14: -> GATED_SEMPRED[$IMPLIES]
                            {
                                adaptor.addChild(root_0, (GrammarAST)adaptor.create(GATED_SEMPRED, IMPLIES110));

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:553:8: 
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
                            // 553:8: -> SEMPRED
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
                    // ANTLRParser.g:555:6: treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] treeSpec ) ) ) | -> treeSpec )
                    {
                    pushFollow(FOLLOW_treeSpec_in_element2707);
                    treeSpec111=treeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_treeSpec.add(treeSpec111.getTree());
                    // ANTLRParser.g:556:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] treeSpec ) ) ) | -> treeSpec )
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==QUESTION||(LA34_0>=STAR && LA34_0<=PLUS)) ) {
                        alt34=1;
                    }
                    else if ( (LA34_0==EOF||LA34_0==SEMPRED||LA34_0==ACTION||(LA34_0>=SEMI && LA34_0<=RPAREN)||LA34_0==OR||(LA34_0>=RARROW && LA34_0<=TREE_BEGIN)||LA34_0==NOT||(LA34_0>=TOKEN_REF && LA34_0<=RULE_REF)||LA34_0==STRING_LITERAL||LA34_0==DOT) ) {
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
                            // ANTLRParser.g:556:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2713);
                            ebnfSuffix112=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix112.getTree());


                            // AST REWRITE
                            // elements: ebnfSuffix, treeSpec
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 556:16: -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] treeSpec ) ) )
                            {
                                // ANTLRParser.g:556:19: ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] treeSpec ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:556:33: ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] treeSpec ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, "BLOCK"), root_2);

                                // ANTLRParser.g:556:50: ^( ALT[\"ALT\"] treeSpec )
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
                            // ANTLRParser.g:557:8: 
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
                            // 557:8: -> treeSpec
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
    // $ANTLR end "element"

    public static class labeledElement_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "labeledElement"
    // ANTLRParser.g:561:1: labeledElement : id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) ;
    public final ANTLRParser.labeledElement_return labeledElement() throws RecognitionException {
        ANTLRParser.labeledElement_return retval = new ANTLRParser.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN114=null;
        Token PLUS_ASSIGN115=null;
        ANTLRParser.id_return id113 = null;

        ANTLRParser.atom_return atom116 = null;

        ANTLRParser.block_return block117 = null;


        GrammarAST ASSIGN114_tree=null;
        GrammarAST PLUS_ASSIGN115_tree=null;

        try {
            // ANTLRParser.g:561:16: ( id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) )
            // ANTLRParser.g:561:18: id ( ASSIGN | PLUS_ASSIGN ) ( atom | block )
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_labeledElement2761);
            id113=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id113.getTree());
            // ANTLRParser.g:561:21: ( ASSIGN | PLUS_ASSIGN )
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
                    // ANTLRParser.g:561:22: ASSIGN
                    {
                    ASSIGN114=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement2764); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN114_tree = (GrammarAST)adaptor.create(ASSIGN114);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN114_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:561:30: PLUS_ASSIGN
                    {
                    PLUS_ASSIGN115=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement2767); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS_ASSIGN115_tree = (GrammarAST)adaptor.create(PLUS_ASSIGN115);
                    root_0 = (GrammarAST)adaptor.becomeRoot(PLUS_ASSIGN115_tree, root_0);
                    }

                    }
                    break;

            }

            // ANTLRParser.g:561:44: ( atom | block )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==NOT||(LA37_0>=TOKEN_REF && LA37_0<=RULE_REF)||LA37_0==STRING_LITERAL||LA37_0==DOT) ) {
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
                    // ANTLRParser.g:561:45: atom
                    {
                    pushFollow(FOLLOW_atom_in_labeledElement2772);
                    atom116=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom116.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:561:50: block
                    {
                    pushFollow(FOLLOW_block_in_labeledElement2774);
                    block117=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block117.getTree());

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
    // ANTLRParser.g:567:1: treeSpec : TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) ;
    public final ANTLRParser.treeSpec_return treeSpec() throws RecognitionException {
        ANTLRParser.treeSpec_return retval = new ANTLRParser.treeSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN118=null;
        Token RPAREN121=null;
        ANTLRParser.element_return element119 = null;

        ANTLRParser.element_return element120 = null;


        GrammarAST TREE_BEGIN118_tree=null;
        GrammarAST RPAREN121_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:568:5: ( TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) )
            // ANTLRParser.g:568:7: TREE_BEGIN element ( element )+ RPAREN
            {
            TREE_BEGIN118=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec2796); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN118);

            pushFollow(FOLLOW_element_in_treeSpec2837);
            element119=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_element.add(element119.getTree());
            // ANTLRParser.g:575:10: ( element )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( (LA38_0==SEMPRED||LA38_0==ACTION||LA38_0==LPAREN||LA38_0==TREE_BEGIN||LA38_0==NOT||(LA38_0>=TOKEN_REF && LA38_0<=RULE_REF)||LA38_0==STRING_LITERAL||LA38_0==DOT) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // ANTLRParser.g:575:10: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec2868);
            	    element120=element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_element.add(element120.getTree());

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

            RPAREN121=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_treeSpec2877); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN121);



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
            // 577:7: -> ^( TREE_BEGIN ( element )+ )
            {
                // ANTLRParser.g:577:10: ^( TREE_BEGIN ( element )+ )
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
    // ANTLRParser.g:582:1: ebnf : block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) ;
    public final ANTLRParser.ebnf_return ebnf() throws RecognitionException {
        ANTLRParser.ebnf_return retval = new ANTLRParser.ebnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.block_return block122 = null;

        ANTLRParser.blockSuffixe_return blockSuffixe123 = null;


        RewriteRuleSubtreeStream stream_blockSuffixe=new RewriteRuleSubtreeStream(adaptor,"rule blockSuffixe");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:583:5: ( block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) )
            // ANTLRParser.g:583:7: block ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
            {
            pushFollow(FOLLOW_block_in_ebnf2911);
            block122=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_block.add(block122.getTree());
            // ANTLRParser.g:587:7: ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==IMPLIES||(LA39_0>=QUESTION && LA39_0<=PLUS)||LA39_0==ROOT) ) {
                alt39=1;
            }
            else if ( (LA39_0==EOF||LA39_0==SEMPRED||LA39_0==ACTION||(LA39_0>=SEMI && LA39_0<=RPAREN)||LA39_0==OR||(LA39_0>=RARROW && LA39_0<=TREE_BEGIN)||LA39_0==NOT||(LA39_0>=TOKEN_REF && LA39_0<=RULE_REF)||LA39_0==STRING_LITERAL||LA39_0==DOT) ) {
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
                    // ANTLRParser.g:587:9: blockSuffixe
                    {
                    pushFollow(FOLLOW_blockSuffixe_in_ebnf2946);
                    blockSuffixe123=blockSuffixe();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_blockSuffixe.add(blockSuffixe123.getTree());


                    // AST REWRITE
                    // elements: blockSuffixe, block
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 587:22: -> ^( blockSuffixe block )
                    {
                        // ANTLRParser.g:587:25: ^( blockSuffixe block )
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
                    // ANTLRParser.g:588:13: 
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
                    // 588:13: -> block
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
    // ANTLRParser.g:594:1: blockSuffixe : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final ANTLRParser.blockSuffixe_return blockSuffixe() throws RecognitionException {
        ANTLRParser.blockSuffixe_return retval = new ANTLRParser.blockSuffixe_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ROOT125=null;
        Token IMPLIES126=null;
        Token BANG127=null;
        ANTLRParser.ebnfSuffix_return ebnfSuffix124 = null;


        GrammarAST ROOT125_tree=null;
        GrammarAST IMPLIES126_tree=null;
        GrammarAST BANG127_tree=null;

        try {
            // ANTLRParser.g:595:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
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
                    // ANTLRParser.g:595:7: ebnfSuffix
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffixe2997);
                    ebnfSuffix124=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnfSuffix124.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:598:7: ROOT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ROOT125=(Token)match(input,ROOT,FOLLOW_ROOT_in_blockSuffixe3011); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ROOT125_tree = (GrammarAST)adaptor.create(ROOT125);
                    adaptor.addChild(root_0, ROOT125_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:599:7: IMPLIES
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    IMPLIES126=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffixe3019); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IMPLIES126_tree = (GrammarAST)adaptor.create(IMPLIES126);
                    adaptor.addChild(root_0, IMPLIES126_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:600:7: BANG
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    BANG127=(Token)match(input,BANG,FOLLOW_BANG_in_blockSuffixe3030); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BANG127_tree = (GrammarAST)adaptor.create(BANG127);
                    adaptor.addChild(root_0, BANG127_tree);
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
    // ANTLRParser.g:603:1: ebnfSuffix : ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] );
    public final ANTLRParser.ebnfSuffix_return ebnfSuffix() throws RecognitionException {
        ANTLRParser.ebnfSuffix_return retval = new ANTLRParser.ebnfSuffix_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token QUESTION128=null;
        Token STAR129=null;
        Token PLUS130=null;

        GrammarAST QUESTION128_tree=null;
        GrammarAST STAR129_tree=null;
        GrammarAST PLUS130_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");


        	Token op = input.LT(1);

        try {
            // ANTLRParser.g:607:2: ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] )
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
                    // ANTLRParser.g:607:4: QUESTION
                    {
                    QUESTION128=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3049); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_QUESTION.add(QUESTION128);



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
                    // 607:13: -> OPTIONAL[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(OPTIONAL, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:608:6: STAR
                    {
                    STAR129=(Token)match(input,STAR,FOLLOW_STAR_in_ebnfSuffix3061); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STAR.add(STAR129);



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
                    // 608:13: -> CLOSURE[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(CLOSURE, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:609:7: PLUS
                    {
                    PLUS130=(Token)match(input,PLUS,FOLLOW_PLUS_in_ebnfSuffix3076); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS130);



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
                    // 609:14: -> POSITIVE_CLOSURE[op]
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
    // ANTLRParser.g:612:1: atom : ( range ( ROOT | BANG )? | {...}? id WILDCARD ruleref -> ^( DOT[$WILDCARD] id ruleref ) | {...}? id WILDCARD terminal -> ^( DOT[$WILDCARD] id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? );
    public final ANTLRParser.atom_return atom() throws RecognitionException {
        ANTLRParser.atom_return retval = new ANTLRParser.atom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ROOT132=null;
        Token BANG133=null;
        Token WILDCARD135=null;
        Token WILDCARD138=null;
        Token ROOT143=null;
        Token BANG144=null;
        ANTLRParser.range_return range131 = null;

        ANTLRParser.id_return id134 = null;

        ANTLRParser.ruleref_return ruleref136 = null;

        ANTLRParser.id_return id137 = null;

        ANTLRParser.terminal_return terminal139 = null;

        ANTLRParser.terminal_return terminal140 = null;

        ANTLRParser.ruleref_return ruleref141 = null;

        ANTLRParser.notSet_return notSet142 = null;


        GrammarAST ROOT132_tree=null;
        GrammarAST BANG133_tree=null;
        GrammarAST WILDCARD135_tree=null;
        GrammarAST WILDCARD138_tree=null;
        GrammarAST ROOT143_tree=null;
        GrammarAST BANG144_tree=null;
        RewriteRuleTokenStream stream_WILDCARD=new RewriteRuleTokenStream(adaptor,"token WILDCARD");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_terminal=new RewriteRuleSubtreeStream(adaptor,"rule terminal");
        RewriteRuleSubtreeStream stream_ruleref=new RewriteRuleSubtreeStream(adaptor,"rule ruleref");
        try {
            // ANTLRParser.g:612:5: ( range ( ROOT | BANG )? | {...}? id WILDCARD ruleref -> ^( DOT[$WILDCARD] id ruleref ) | {...}? id WILDCARD terminal -> ^( DOT[$WILDCARD] id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? )
            int alt44=6;
            alt44 = dfa44.predict(input);
            switch (alt44) {
                case 1 :
                    // ANTLRParser.g:612:7: range ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_range_in_atom3093);
                    range131=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range131.getTree());
                    // ANTLRParser.g:612:13: ( ROOT | BANG )?
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
                            // ANTLRParser.g:612:14: ROOT
                            {
                            ROOT132=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3096); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT132_tree = (GrammarAST)adaptor.create(ROOT132);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT132_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:612:22: BANG
                            {
                            BANG133=(Token)match(input,BANG,FOLLOW_BANG_in_atom3101); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG133_tree = (GrammarAST)adaptor.create(BANG133);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG133_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:618:6: {...}? id WILDCARD ruleref
                    {
                    if ( !((
                    	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                    	        input.LT(2).getCharPositionInLine() &&
                    	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                    	    )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atom", "\n\t    \tinput.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==\n\t        input.LT(2).getCharPositionInLine() &&\n\t        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()\n\t    ");
                    }
                    pushFollow(FOLLOW_id_in_atom3148);
                    id134=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id134.getTree());
                    WILDCARD135=(Token)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom3150); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_WILDCARD.add(WILDCARD135);

                    pushFollow(FOLLOW_ruleref_in_atom3152);
                    ruleref136=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleref.add(ruleref136.getTree());


                    // AST REWRITE
                    // elements: id, ruleref
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 624:6: -> ^( DOT[$WILDCARD] id ruleref )
                    {
                        // ANTLRParser.g:624:9: ^( DOT[$WILDCARD] id ruleref )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(DOT, WILDCARD135), root_1);

                        adaptor.addChild(root_1, stream_id.nextTree());
                        adaptor.addChild(root_1, stream_ruleref.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:626:6: {...}? id WILDCARD terminal
                    {
                    if ( !((
                    	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                    	        input.LT(2).getCharPositionInLine() &&
                    	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                    	    )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atom", "\n\t    \tinput.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==\n\t        input.LT(2).getCharPositionInLine() &&\n\t        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()\n\t    ");
                    }
                    pushFollow(FOLLOW_id_in_atom3187);
                    id137=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id137.getTree());
                    WILDCARD138=(Token)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom3189); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_WILDCARD.add(WILDCARD138);

                    pushFollow(FOLLOW_terminal_in_atom3191);
                    terminal139=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_terminal.add(terminal139.getTree());


                    // AST REWRITE
                    // elements: terminal, id
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 632:6: -> ^( DOT[$WILDCARD] id terminal )
                    {
                        // ANTLRParser.g:632:9: ^( DOT[$WILDCARD] id terminal )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(DOT, WILDCARD138), root_1);

                        adaptor.addChild(root_1, stream_id.nextTree());
                        adaptor.addChild(root_1, stream_terminal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // ANTLRParser.g:633:9: terminal
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_terminal_in_atom3217);
                    terminal140=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, terminal140.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:634:9: ruleref
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleref_in_atom3227);
                    ruleref141=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleref141.getTree());

                    }
                    break;
                case 6 :
                    // ANTLRParser.g:635:7: notSet ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_notSet_in_atom3235);
                    notSet142=notSet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, notSet142.getTree());
                    // ANTLRParser.g:635:14: ( ROOT | BANG )?
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
                            // ANTLRParser.g:635:15: ROOT
                            {
                            ROOT143=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3238); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT143_tree = (GrammarAST)adaptor.create(ROOT143);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT143_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:635:21: BANG
                            {
                            BANG144=(Token)match(input,BANG,FOLLOW_BANG_in_atom3241); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG144_tree = (GrammarAST)adaptor.create(BANG144);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG144_tree, root_0);
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
    // $ANTLR end "atom"

    public static class notSet_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "notSet"
    // ANTLRParser.g:646:1: notSet : ( NOT notTerminal -> ^( NOT notTerminal ) | NOT block -> ^( NOT block ) );
    public final ANTLRParser.notSet_return notSet() throws RecognitionException {
        ANTLRParser.notSet_return retval = new ANTLRParser.notSet_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token NOT145=null;
        Token NOT147=null;
        ANTLRParser.notTerminal_return notTerminal146 = null;

        ANTLRParser.block_return block148 = null;


        GrammarAST NOT145_tree=null;
        GrammarAST NOT147_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleSubtreeStream stream_notTerminal=new RewriteRuleSubtreeStream(adaptor,"rule notTerminal");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:647:5: ( NOT notTerminal -> ^( NOT notTerminal ) | NOT block -> ^( NOT block ) )
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
                    // ANTLRParser.g:647:7: NOT notTerminal
                    {
                    NOT145=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3274); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT145);

                    pushFollow(FOLLOW_notTerminal_in_notSet3276);
                    notTerminal146=notTerminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_notTerminal.add(notTerminal146.getTree());


                    // AST REWRITE
                    // elements: notTerminal, NOT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 647:23: -> ^( NOT notTerminal )
                    {
                        // ANTLRParser.g:647:26: ^( NOT notTerminal )
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
                    // ANTLRParser.g:648:7: NOT block
                    {
                    NOT147=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3292); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT147);

                    pushFollow(FOLLOW_block_in_notSet3294);
                    block148=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_block.add(block148.getTree());


                    // AST REWRITE
                    // elements: block, NOT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 648:19: -> ^( NOT block )
                    {
                        // ANTLRParser.g:648:22: ^( NOT block )
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
    // ANTLRParser.g:657:1: notTerminal : ( TOKEN_REF | STRING_LITERAL );
    public final ANTLRParser.notTerminal_return notTerminal() throws RecognitionException {
        ANTLRParser.notTerminal_return retval = new ANTLRParser.notTerminal_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set149=null;

        GrammarAST set149_tree=null;

        try {
            // ANTLRParser.g:658:5: ( TOKEN_REF | STRING_LITERAL )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set149=(Token)input.LT(1);
            if ( input.LA(1)==TOKEN_REF||input.LA(1)==STRING_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set149));
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
    // ANTLRParser.g:669:1: block : LPAREN ( optionsSpec )? ( (ra+= ruleAction )* ( ACTION )? COLON )? altList RPAREN -> ^( BLOCK ( optionsSpec )? ( $ra)* ( ACTION )? altList ) ;
    public final ANTLRParser.block_return block() throws RecognitionException {
        ANTLRParser.block_return retval = new ANTLRParser.block_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN150=null;
        Token ACTION152=null;
        Token COLON153=null;
        Token RPAREN155=null;
        List list_ra=null;
        ANTLRParser.optionsSpec_return optionsSpec151 = null;

        ANTLRParser.altList_return altList154 = null;

        RuleReturnScope ra = null;
        GrammarAST LPAREN150_tree=null;
        GrammarAST ACTION152_tree=null;
        GrammarAST COLON153_tree=null;
        GrammarAST RPAREN155_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_optionsSpec=new RewriteRuleSubtreeStream(adaptor,"rule optionsSpec");
        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        RewriteRuleSubtreeStream stream_ruleAction=new RewriteRuleSubtreeStream(adaptor,"rule ruleAction");
        try {
            // ANTLRParser.g:670:5: ( LPAREN ( optionsSpec )? ( (ra+= ruleAction )* ( ACTION )? COLON )? altList RPAREN -> ^( BLOCK ( optionsSpec )? ( $ra)* ( ACTION )? altList ) )
            // ANTLRParser.g:670:7: LPAREN ( optionsSpec )? ( (ra+= ruleAction )* ( ACTION )? COLON )? altList RPAREN
            {
            LPAREN150=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_block3362); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN150);

            // ANTLRParser.g:675:10: ( optionsSpec )?
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==OPTIONS) ) {
                alt46=1;
            }
            switch (alt46) {
                case 1 :
                    // ANTLRParser.g:675:10: optionsSpec
                    {
                    pushFollow(FOLLOW_optionsSpec_in_block3408);
                    optionsSpec151=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_optionsSpec.add(optionsSpec151.getTree());

                    }
                    break;

            }

            // ANTLRParser.g:677:10: ( (ra+= ruleAction )* ( ACTION )? COLON )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==COLON||LA49_0==AT) ) {
                alt49=1;
            }
            else if ( (LA49_0==ACTION) ) {
                int LA49_2 = input.LA(2);

                if ( (LA49_2==COLON) ) {
                    alt49=1;
                }
            }
            switch (alt49) {
                case 1 :
                    // ANTLRParser.g:682:14: (ra+= ruleAction )* ( ACTION )? COLON
                    {
                    // ANTLRParser.g:682:16: (ra+= ruleAction )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==AT) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // ANTLRParser.g:682:16: ra+= ruleAction
                    	    {
                    	    pushFollow(FOLLOW_ruleAction_in_block3503);
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

                    // ANTLRParser.g:683:14: ( ACTION )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==ACTION) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // ANTLRParser.g:683:14: ACTION
                            {
                            ACTION152=(Token)match(input,ACTION,FOLLOW_ACTION_in_block3519); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ACTION.add(ACTION152);


                            }
                            break;

                    }

                    COLON153=(Token)match(input,COLON,FOLLOW_COLON_in_block3570); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON153);


                    }
                    break;

            }

            pushFollow(FOLLOW_altList_in_block3623);
            altList154=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList154.getTree());
            RPAREN155=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_block3641); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN155);



            // AST REWRITE
            // elements: ACTION, optionsSpec, altList, ra
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
            // 698:7: -> ^( BLOCK ( optionsSpec )? ( $ra)* ( ACTION )? altList )
            {
                // ANTLRParser.g:698:10: ^( BLOCK ( optionsSpec )? ( $ra)* ( ACTION )? altList )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, "BLOCK"), root_1);

                // ANTLRParser.g:698:18: ( optionsSpec )?
                if ( stream_optionsSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_optionsSpec.nextTree());

                }
                stream_optionsSpec.reset();
                // ANTLRParser.g:698:31: ( $ra)*
                while ( stream_ra.hasNext() ) {
                    adaptor.addChild(root_1, stream_ra.nextTree());

                }
                stream_ra.reset();
                // ANTLRParser.g:698:36: ( ACTION )?
                if ( stream_ACTION.hasNext() ) {
                    adaptor.addChild(root_1, stream_ACTION.nextNode());

                }
                stream_ACTION.reset();
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
    // ANTLRParser.g:707:1: ruleref : RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op RULE_REF ( ARG_ACTION )? ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) ;
    public final ANTLRParser.ruleref_return ruleref() throws RecognitionException {
        ANTLRParser.ruleref_return retval = new ANTLRParser.ruleref_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token op=null;
        Token RULE_REF156=null;
        Token ARG_ACTION157=null;

        GrammarAST op_tree=null;
        GrammarAST RULE_REF156_tree=null;
        GrammarAST ARG_ACTION157_tree=null;
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_ROOT=new RewriteRuleTokenStream(adaptor,"token ROOT");
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

        try {
            // ANTLRParser.g:708:5: ( RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op RULE_REF ( ARG_ACTION )? ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) )
            // ANTLRParser.g:708:7: RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op RULE_REF ( ARG_ACTION )? ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
            {
            RULE_REF156=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref3711); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF156);

            // ANTLRParser.g:708:16: ( ARG_ACTION )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==ARG_ACTION) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // ANTLRParser.g:708:16: ARG_ACTION
                    {
                    ARG_ACTION157=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref3713); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION157);


                    }
                    break;

            }

            // ANTLRParser.g:709:3: ( (op= ROOT | op= BANG ) -> ^( $op RULE_REF ( ARG_ACTION )? ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==BANG||LA52_0==ROOT) ) {
                alt52=1;
            }
            else if ( (LA52_0==EOF||LA52_0==SEMPRED||LA52_0==ACTION||(LA52_0>=SEMI && LA52_0<=RPAREN)||LA52_0==QUESTION||(LA52_0>=STAR && LA52_0<=PLUS)||LA52_0==OR||(LA52_0>=RARROW && LA52_0<=TREE_BEGIN)||LA52_0==NOT||(LA52_0>=TOKEN_REF && LA52_0<=RULE_REF)||LA52_0==STRING_LITERAL||LA52_0==DOT) ) {
                alt52=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // ANTLRParser.g:709:5: (op= ROOT | op= BANG )
                    {
                    // ANTLRParser.g:709:5: (op= ROOT | op= BANG )
                    int alt51=2;
                    int LA51_0 = input.LA(1);

                    if ( (LA51_0==ROOT) ) {
                        alt51=1;
                    }
                    else if ( (LA51_0==BANG) ) {
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
                            // ANTLRParser.g:709:6: op= ROOT
                            {
                            op=(Token)match(input,ROOT,FOLLOW_ROOT_in_ruleref3723); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ROOT.add(op);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:709:14: op= BANG
                            {
                            op=(Token)match(input,BANG,FOLLOW_BANG_in_ruleref3727); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_BANG.add(op);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: op, RULE_REF, ARG_ACTION
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
                    // 709:23: -> ^( $op RULE_REF ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:709:26: ^( $op RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_op.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_RULE_REF.nextNode());
                        // ANTLRParser.g:709:41: ( ARG_ACTION )?
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
                    // ANTLRParser.g:710:10: 
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
                    // 710:10: -> ^( RULE_REF ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:710:13: ^( RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_RULE_REF.nextNode(), root_1);

                        // ANTLRParser.g:710:24: ( ARG_ACTION )?
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
            reportError(re);
            recover(input,re);
    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);

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
    // ANTLRParser.g:723:1: range : rangeElement RANGE rangeElement ;
    public final ANTLRParser.range_return range() throws RecognitionException {
        ANTLRParser.range_return retval = new ANTLRParser.range_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RANGE159=null;
        ANTLRParser.rangeElement_return rangeElement158 = null;

        ANTLRParser.rangeElement_return rangeElement160 = null;


        GrammarAST RANGE159_tree=null;

        try {
            // ANTLRParser.g:724:5: ( rangeElement RANGE rangeElement )
            // ANTLRParser.g:724:7: rangeElement RANGE rangeElement
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_rangeElement_in_range3790);
            rangeElement158=rangeElement();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rangeElement158.getTree());
            RANGE159=(Token)match(input,RANGE,FOLLOW_RANGE_in_range3792); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RANGE159_tree = (GrammarAST)adaptor.create(RANGE159);
            root_0 = (GrammarAST)adaptor.becomeRoot(RANGE159_tree, root_0);
            }
            pushFollow(FOLLOW_rangeElement_in_range3795);
            rangeElement160=rangeElement();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rangeElement160.getTree());

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
    // ANTLRParser.g:735:1: rangeElement : ( STRING_LITERAL | RULE_REF | TOKEN_REF );
    public final ANTLRParser.rangeElement_return rangeElement() throws RecognitionException {
        ANTLRParser.rangeElement_return retval = new ANTLRParser.rangeElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set161=null;

        GrammarAST set161_tree=null;

        try {
            // ANTLRParser.g:736:5: ( STRING_LITERAL | RULE_REF | TOKEN_REF )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set161=(Token)input.LT(1);
            if ( (input.LA(1)>=TOKEN_REF && input.LA(1)<=RULE_REF)||input.LA(1)==STRING_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set161));
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
    // ANTLRParser.g:741:1: terminal : ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )? ;
    public final ANTLRParser.terminal_return terminal() throws RecognitionException {
        ANTLRParser.terminal_return retval = new ANTLRParser.terminal_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF162=null;
        Token ARG_ACTION163=null;
        Token STRING_LITERAL165=null;
        Token DOT167=null;
        Token ROOT169=null;
        Token BANG170=null;
        ANTLRParser.elementOptions_return elementOptions164 = null;

        ANTLRParser.elementOptions_return elementOptions166 = null;

        ANTLRParser.elementOptions_return elementOptions168 = null;


        GrammarAST TOKEN_REF162_tree=null;
        GrammarAST ARG_ACTION163_tree=null;
        GrammarAST STRING_LITERAL165_tree=null;
        GrammarAST DOT167_tree=null;
        GrammarAST ROOT169_tree=null;
        GrammarAST BANG170_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ROOT=new RewriteRuleTokenStream(adaptor,"token ROOT");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        try {
            // ANTLRParser.g:742:5: ( ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )? )
            // ANTLRParser.g:742:9: ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )?
            {
            // ANTLRParser.g:742:9: ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) )
            int alt57=3;
            switch ( input.LA(1) ) {
            case TOKEN_REF:
                {
                alt57=1;
                }
                break;
            case STRING_LITERAL:
                {
                alt57=2;
                }
                break;
            case DOT:
                {
                alt57=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 57, 0, input);

                throw nvae;
            }

            switch (alt57) {
                case 1 :
                    // ANTLRParser.g:743:7: TOKEN_REF ( ARG_ACTION )? ( elementOptions )?
                    {
                    TOKEN_REF162=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal3883); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF162);

                    // ANTLRParser.g:743:17: ( ARG_ACTION )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==ARG_ACTION) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // ANTLRParser.g:743:17: ARG_ACTION
                            {
                            ARG_ACTION163=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal3885); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION163);


                            }
                            break;

                    }

                    // ANTLRParser.g:743:29: ( elementOptions )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==LT) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // ANTLRParser.g:743:29: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3888);
                            elementOptions164=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions164.getTree());

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
                    // 743:45: -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                    {
                        // ANTLRParser.g:743:48: ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_TOKEN_REF.nextNode(), root_1);

                        // ANTLRParser.g:743:60: ( ARG_ACTION )?
                        if ( stream_ARG_ACTION.hasNext() ) {
                            adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                        }
                        stream_ARG_ACTION.reset();
                        // ANTLRParser.g:743:72: ( elementOptions )?
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
                    // ANTLRParser.g:744:7: STRING_LITERAL ( elementOptions )?
                    {
                    STRING_LITERAL165=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal3909); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL165);

                    // ANTLRParser.g:744:22: ( elementOptions )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==LT) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // ANTLRParser.g:744:22: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3911);
                            elementOptions166=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions166.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: elementOptions, STRING_LITERAL
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 744:41: -> ^( STRING_LITERAL ( elementOptions )? )
                    {
                        // ANTLRParser.g:744:44: ^( STRING_LITERAL ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_STRING_LITERAL.nextNode(), root_1);

                        // ANTLRParser.g:744:61: ( elementOptions )?
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
                    // ANTLRParser.g:750:7: DOT ( elementOptions )?
                    {
                    DOT167=(Token)match(input,DOT,FOLLOW_DOT_in_terminal3955); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT167);

                    // ANTLRParser.g:750:11: ( elementOptions )?
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==LT) ) {
                        alt56=1;
                    }
                    switch (alt56) {
                        case 1 :
                            // ANTLRParser.g:750:11: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3957);
                            elementOptions168=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions168.getTree());

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
                    // 750:34: -> ^( WILDCARD[$DOT] ( elementOptions )? )
                    {
                        // ANTLRParser.g:750:37: ^( WILDCARD[$DOT] ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(WILDCARD, DOT167), root_1);

                        // ANTLRParser.g:750:54: ( elementOptions )?
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

            // ANTLRParser.g:752:3: ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )?
            int alt58=3;
            int LA58_0 = input.LA(1);

            if ( (LA58_0==ROOT) ) {
                alt58=1;
            }
            else if ( (LA58_0==BANG) ) {
                alt58=2;
            }
            switch (alt58) {
                case 1 :
                    // ANTLRParser.g:752:5: ROOT
                    {
                    ROOT169=(Token)match(input,ROOT,FOLLOW_ROOT_in_terminal3985); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ROOT.add(ROOT169);



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
                    // 752:19: -> ^( ROOT $terminal)
                    {
                        // ANTLRParser.g:752:22: ^( ROOT $terminal)
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
                    // ANTLRParser.g:753:5: BANG
                    {
                    BANG170=(Token)match(input,BANG,FOLLOW_BANG_in_terminal4009); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BANG.add(BANG170);



                    // AST REWRITE
                    // elements: BANG, terminal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 753:19: -> ^( BANG $terminal)
                    {
                        // ANTLRParser.g:753:22: ^( BANG $terminal)
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
    // ANTLRParser.g:763:2: elementOptions : LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
    public final ANTLRParser.elementOptions_return elementOptions() throws RecognitionException {
        ANTLRParser.elementOptions_return retval = new ANTLRParser.elementOptions_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LT171=null;
        Token COMMA173=null;
        Token GT175=null;
        ANTLRParser.elementOption_return elementOption172 = null;

        ANTLRParser.elementOption_return elementOption174 = null;


        GrammarAST LT171_tree=null;
        GrammarAST COMMA173_tree=null;
        GrammarAST GT175_tree=null;
        RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
        RewriteRuleTokenStream stream_LT=new RewriteRuleTokenStream(adaptor,"token LT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_elementOption=new RewriteRuleSubtreeStream(adaptor,"rule elementOption");
        try {
            // ANTLRParser.g:764:5: ( LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) )
            // ANTLRParser.g:766:7: LT elementOption ( COMMA elementOption )* GT
            {
            LT171=(Token)match(input,LT,FOLLOW_LT_in_elementOptions4073); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LT.add(LT171);

            pushFollow(FOLLOW_elementOption_in_elementOptions4075);
            elementOption172=elementOption();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_elementOption.add(elementOption172.getTree());
            // ANTLRParser.g:766:24: ( COMMA elementOption )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==COMMA) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // ANTLRParser.g:766:25: COMMA elementOption
            	    {
            	    COMMA173=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementOptions4078); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA173);

            	    pushFollow(FOLLOW_elementOption_in_elementOptions4080);
            	    elementOption174=elementOption();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_elementOption.add(elementOption174.getTree());

            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);

            GT175=(Token)match(input,GT,FOLLOW_GT_in_elementOptions4084); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GT.add(GT175);



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
            // 766:50: -> ^( ELEMENT_OPTIONS ( elementOption )+ )
            {
                // ANTLRParser.g:766:53: ^( ELEMENT_OPTIONS ( elementOption )+ )
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
    // ANTLRParser.g:771:1: elementOption : ( qid | id ASSIGN ( qid | STRING_LITERAL ) );
    public final ANTLRParser.elementOption_return elementOption() throws RecognitionException {
        ANTLRParser.elementOption_return retval = new ANTLRParser.elementOption_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN178=null;
        Token STRING_LITERAL180=null;
        ANTLRParser.qid_return qid176 = null;

        ANTLRParser.id_return id177 = null;

        ANTLRParser.qid_return qid179 = null;


        GrammarAST ASSIGN178_tree=null;
        GrammarAST STRING_LITERAL180_tree=null;

        try {
            // ANTLRParser.g:772:5: ( qid | id ASSIGN ( qid | STRING_LITERAL ) )
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==RULE_REF) ) {
                int LA61_1 = input.LA(2);

                if ( (LA61_1==COMMA||LA61_1==GT||LA61_1==WILDCARD) ) {
                    alt61=1;
                }
                else if ( (LA61_1==ASSIGN) ) {
                    alt61=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA61_0==TOKEN_REF) ) {
                int LA61_2 = input.LA(2);

                if ( (LA61_2==COMMA||LA61_2==GT||LA61_2==WILDCARD) ) {
                    alt61=1;
                }
                else if ( (LA61_2==ASSIGN) ) {
                    alt61=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 2, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // ANTLRParser.g:773:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_elementOption4119);
                    qid176=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid176.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:776:7: id ASSIGN ( qid | STRING_LITERAL )
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_elementOption4141);
                    id177=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id177.getTree());
                    ASSIGN178=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption4143); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN178_tree = (GrammarAST)adaptor.create(ASSIGN178);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN178_tree, root_0);
                    }
                    // ANTLRParser.g:776:18: ( qid | STRING_LITERAL )
                    int alt60=2;
                    int LA60_0 = input.LA(1);

                    if ( ((LA60_0>=TOKEN_REF && LA60_0<=RULE_REF)) ) {
                        alt60=1;
                    }
                    else if ( (LA60_0==STRING_LITERAL) ) {
                        alt60=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 60, 0, input);

                        throw nvae;
                    }
                    switch (alt60) {
                        case 1 :
                            // ANTLRParser.g:776:19: qid
                            {
                            pushFollow(FOLLOW_qid_in_elementOption4147);
                            qid179=qid();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qid179.getTree());

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:776:25: STRING_LITERAL
                            {
                            STRING_LITERAL180=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption4151); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            STRING_LITERAL180_tree = (GrammarAST)adaptor.create(STRING_LITERAL180);
                            adaptor.addChild(root_0, STRING_LITERAL180_tree);
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
    // ANTLRParser.g:779:1: rewrite : ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite ;
    public final ANTLRParser.rewrite_return rewrite() throws RecognitionException {
        ANTLRParser.rewrite_return retval = new ANTLRParser.rewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.predicatedRewrite_return predicatedRewrite181 = null;

        ANTLRParser.nakedRewrite_return nakedRewrite182 = null;


        RewriteRuleSubtreeStream stream_predicatedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule predicatedRewrite");
        RewriteRuleSubtreeStream stream_nakedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule nakedRewrite");
        try {
            // ANTLRParser.g:780:2: ( ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite )
            // ANTLRParser.g:780:4: ( predicatedRewrite )* nakedRewrite
            {
            // ANTLRParser.g:780:4: ( predicatedRewrite )*
            loop62:
            do {
                int alt62=2;
                int LA62_0 = input.LA(1);

                if ( (LA62_0==RARROW) ) {
                    int LA62_1 = input.LA(2);

                    if ( (LA62_1==SEMPRED) ) {
                        alt62=1;
                    }


                }


                switch (alt62) {
            	case 1 :
            	    // ANTLRParser.g:780:4: predicatedRewrite
            	    {
            	    pushFollow(FOLLOW_predicatedRewrite_in_rewrite4166);
            	    predicatedRewrite181=predicatedRewrite();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_predicatedRewrite.add(predicatedRewrite181.getTree());

            	    }
            	    break;

            	default :
            	    break loop62;
                }
            } while (true);

            pushFollow(FOLLOW_nakedRewrite_in_rewrite4169);
            nakedRewrite182=nakedRewrite();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_nakedRewrite.add(nakedRewrite182.getTree());


            // AST REWRITE
            // elements: predicatedRewrite, nakedRewrite
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 780:36: -> ( predicatedRewrite )* nakedRewrite
            {
                // ANTLRParser.g:780:39: ( predicatedRewrite )*
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
    // ANTLRParser.g:783:1: predicatedRewrite : RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) ;
    public final ANTLRParser.predicatedRewrite_return predicatedRewrite() throws RecognitionException {
        ANTLRParser.predicatedRewrite_return retval = new ANTLRParser.predicatedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW183=null;
        Token SEMPRED184=null;
        ANTLRParser.rewriteAlt_return rewriteAlt185 = null;


        GrammarAST RARROW183_tree=null;
        GrammarAST SEMPRED184_tree=null;
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:784:2: ( RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) )
            // ANTLRParser.g:784:4: RARROW SEMPRED rewriteAlt
            {
            RARROW183=(Token)match(input,RARROW,FOLLOW_RARROW_in_predicatedRewrite4187); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW183);

            SEMPRED184=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite4189); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED184);

            pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite4191);
            rewriteAlt185=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt185.getTree());


            // AST REWRITE
            // elements: SEMPRED, rewriteAlt, SEMPRED, rewriteAlt
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 785:3: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
            if ((rewriteAlt185!=null?rewriteAlt185.isTemplate:false)) {
                // ANTLRParser.g:785:32: ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW183), root_1);

                adaptor.addChild(root_1, stream_SEMPRED.nextNode());
                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 786:3: -> ^( RESULT[$RARROW] SEMPRED rewriteAlt )
            {
                // ANTLRParser.g:786:6: ^( RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW183), root_1);

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
    // ANTLRParser.g:789:1: nakedRewrite : RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) ;
    public final ANTLRParser.nakedRewrite_return nakedRewrite() throws RecognitionException {
        ANTLRParser.nakedRewrite_return retval = new ANTLRParser.nakedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW186=null;
        ANTLRParser.rewriteAlt_return rewriteAlt187 = null;


        GrammarAST RARROW186_tree=null;
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:790:2: ( RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) )
            // ANTLRParser.g:790:4: RARROW rewriteAlt
            {
            RARROW186=(Token)match(input,RARROW,FOLLOW_RARROW_in_nakedRewrite4231); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW186);

            pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite4233);
            rewriteAlt187=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt187.getTree());


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
            // 790:22: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt )
            if ((rewriteAlt187!=null?rewriteAlt187.isTemplate:false)) {
                // ANTLRParser.g:790:51: ^( ST_RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW186), root_1);

                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 791:10: -> ^( RESULT[$RARROW] rewriteAlt )
            {
                // ANTLRParser.g:791:13: ^( RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW186), root_1);

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
    // ANTLRParser.g:796:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );
    public final ANTLRParser.rewriteAlt_return rewriteAlt() throws RecognitionException {
        ANTLRParser.rewriteAlt_return retval = new ANTLRParser.rewriteAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ETC190=null;
        ANTLRParser.rewriteTemplate_return rewriteTemplate188 = null;

        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt189 = null;


        GrammarAST ETC190_tree=null;

        try {
            // ANTLRParser.g:798:5: ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON )
            int alt63=4;
            alt63 = dfa63.predict(input);
            switch (alt63) {
                case 1 :
                    // ANTLRParser.g:799:7: rewriteTemplate
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplate_in_rewriteAlt4297);
                    rewriteTemplate188=rewriteTemplate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplate188.getTree());
                    if ( state.backtracking==0 ) {
                      retval.isTemplate =true;
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:805:7: rewriteTreeAlt
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteAlt4336);
                    rewriteTreeAlt189=rewriteTreeAlt();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAlt189.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:807:7: ETC
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ETC190=(Token)match(input,ETC,FOLLOW_ETC_in_rewriteAlt4351); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ETC190_tree = (GrammarAST)adaptor.create(ETC190);
                    adaptor.addChild(root_0, ETC190_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:809:27: 
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
                    // 809:27: -> EPSILON
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
    // ANTLRParser.g:812:1: rewriteTreeAlt : ( rewriteTreeElement )+ -> ^( ALT[\"ALT\"] ( rewriteTreeElement )+ ) ;
    public final ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt() throws RecognitionException {
        ANTLRParser.rewriteTreeAlt_return retval = new ANTLRParser.rewriteTreeAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement191 = null;


        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:813:5: ( ( rewriteTreeElement )+ -> ^( ALT[\"ALT\"] ( rewriteTreeElement )+ ) )
            // ANTLRParser.g:813:7: ( rewriteTreeElement )+
            {
            // ANTLRParser.g:813:7: ( rewriteTreeElement )+
            int cnt64=0;
            loop64:
            do {
                int alt64=2;
                int LA64_0 = input.LA(1);

                if ( (LA64_0==ACTION||LA64_0==LPAREN||LA64_0==DOLLAR||LA64_0==TREE_BEGIN||(LA64_0>=TOKEN_REF && LA64_0<=RULE_REF)||LA64_0==STRING_LITERAL) ) {
                    alt64=1;
                }


                switch (alt64) {
            	case 1 :
            	    // ANTLRParser.g:813:7: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4382);
            	    rewriteTreeElement191=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement191.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt64 >= 1 ) break loop64;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(64, input);
                        throw eee;
                }
                cnt64++;
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
            // 813:27: -> ^( ALT[\"ALT\"] ( rewriteTreeElement )+ )
            {
                // ANTLRParser.g:813:30: ^( ALT[\"ALT\"] ( rewriteTreeElement )+ )
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
    // ANTLRParser.g:816:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );
    public final ANTLRParser.rewriteTreeElement_return rewriteTreeElement() throws RecognitionException {
        ANTLRParser.rewriteTreeElement_return retval = new ANTLRParser.rewriteTreeElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom192 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom193 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix194 = null;

        ANTLRParser.rewriteTree_return rewriteTree195 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix196 = null;

        ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf197 = null;


        RewriteRuleSubtreeStream stream_rewriteTree=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTree");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        try {
            // ANTLRParser.g:817:2: ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf )
            int alt66=4;
            alt66 = dfa66.predict(input);
            switch (alt66) {
                case 1 :
                    // ANTLRParser.g:817:4: rewriteTreeAtom
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4407);
                    rewriteTreeAtom192=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAtom192.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:818:4: rewriteTreeAtom ebnfSuffix
                    {
                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4412);
                    rewriteTreeAtom193=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom193.getTree());
                    pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4414);
                    ebnfSuffix194=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix194.getTree());


                    // AST REWRITE
                    // elements: ebnfSuffix, rewriteTreeAtom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 819:3: -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTreeAtom ) ) )
                    {
                        // ANTLRParser.g:819:6: ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTreeAtom ) ) )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                        // ANTLRParser.g:819:20: ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTreeAtom ) )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, "BLOCK"), root_2);

                        // ANTLRParser.g:819:37: ^( ALT[\"ALT\"] rewriteTreeAtom )
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
                    // ANTLRParser.g:820:6: rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) ) ) | -> rewriteTree )
                    {
                    pushFollow(FOLLOW_rewriteTree_in_rewriteTreeElement4443);
                    rewriteTree195=rewriteTree();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTree.add(rewriteTree195.getTree());
                    // ANTLRParser.g:821:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) ) ) | -> rewriteTree )
                    int alt65=2;
                    int LA65_0 = input.LA(1);

                    if ( (LA65_0==QUESTION||(LA65_0>=STAR && LA65_0<=PLUS)) ) {
                        alt65=1;
                    }
                    else if ( (LA65_0==EOF||LA65_0==ACTION||(LA65_0>=SEMI && LA65_0<=RPAREN)||LA65_0==OR||LA65_0==DOLLAR||(LA65_0>=RARROW && LA65_0<=TREE_BEGIN)||(LA65_0>=TOKEN_REF && LA65_0<=RULE_REF)||LA65_0==STRING_LITERAL) ) {
                        alt65=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 65, 0, input);

                        throw nvae;
                    }
                    switch (alt65) {
                        case 1 :
                            // ANTLRParser.g:821:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4449);
                            ebnfSuffix196=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix196.getTree());


                            // AST REWRITE
                            // elements: rewriteTree, ebnfSuffix
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 822:4: -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) ) )
                            {
                                // ANTLRParser.g:822:7: ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:822:20: ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, "BLOCK"), root_2);

                                // ANTLRParser.g:822:37: ^( ALT[\"ALT\"] rewriteTree )
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
                            // ANTLRParser.g:823:5: 
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
                            // 823:5: -> rewriteTree
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
                    // ANTLRParser.g:825:6: rewriteTreeEbnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4490);
                    rewriteTreeEbnf197=rewriteTreeEbnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeEbnf197.getTree());

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
    // ANTLRParser.g:828:1: rewriteTreeAtom : ( TOKEN_REF ( ARG_ACTION )? -> ^( TOKEN_REF ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION );
    public final ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom() throws RecognitionException {
        ANTLRParser.rewriteTreeAtom_return retval = new ANTLRParser.rewriteTreeAtom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF198=null;
        Token ARG_ACTION199=null;
        Token RULE_REF200=null;
        Token STRING_LITERAL201=null;
        Token DOLLAR202=null;
        Token ACTION204=null;
        ANTLRParser.id_return id203 = null;


        GrammarAST TOKEN_REF198_tree=null;
        GrammarAST ARG_ACTION199_tree=null;
        GrammarAST RULE_REF200_tree=null;
        GrammarAST STRING_LITERAL201_tree=null;
        GrammarAST DOLLAR202_tree=null;
        GrammarAST ACTION204_tree=null;
        RewriteRuleTokenStream stream_DOLLAR=new RewriteRuleTokenStream(adaptor,"token DOLLAR");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:829:5: ( TOKEN_REF ( ARG_ACTION )? -> ^( TOKEN_REF ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION )
            int alt68=5;
            switch ( input.LA(1) ) {
            case TOKEN_REF:
                {
                alt68=1;
                }
                break;
            case RULE_REF:
                {
                alt68=2;
                }
                break;
            case STRING_LITERAL:
                {
                alt68=3;
                }
                break;
            case DOLLAR:
                {
                alt68=4;
                }
                break;
            case ACTION:
                {
                alt68=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 68, 0, input);

                throw nvae;
            }

            switch (alt68) {
                case 1 :
                    // ANTLRParser.g:829:9: TOKEN_REF ( ARG_ACTION )?
                    {
                    TOKEN_REF198=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom4506); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF198);

                    // ANTLRParser.g:829:19: ( ARG_ACTION )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==ARG_ACTION) ) {
                        alt67=1;
                    }
                    switch (alt67) {
                        case 1 :
                            // ANTLRParser.g:829:19: ARG_ACTION
                            {
                            ARG_ACTION199=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom4508); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION199);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: ARG_ACTION, TOKEN_REF
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 829:31: -> ^( TOKEN_REF ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:829:34: ^( TOKEN_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_TOKEN_REF.nextNode(), root_1);

                        // ANTLRParser.g:829:46: ( ARG_ACTION )?
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
                    // ANTLRParser.g:830:9: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF200=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_rewriteTreeAtom4529); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF200_tree = (GrammarAST)adaptor.create(RULE_REF200);
                    adaptor.addChild(root_0, RULE_REF200_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:831:6: STRING_LITERAL
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STRING_LITERAL201=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4536); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL201_tree = (GrammarAST)adaptor.create(STRING_LITERAL201);
                    adaptor.addChild(root_0, STRING_LITERAL201_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:832:6: DOLLAR id
                    {
                    DOLLAR202=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_rewriteTreeAtom4543); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOLLAR.add(DOLLAR202);

                    pushFollow(FOLLOW_id_in_rewriteTreeAtom4545);
                    id203=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id203.getTree());


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
                    // 832:16: -> LABEL[$DOLLAR,$id.text]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(LABEL, DOLLAR202, (id203!=null?input.toString(id203.start,id203.stop):null)));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // ANTLRParser.g:833:4: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION204=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTreeAtom4556); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION204_tree = (GrammarAST)adaptor.create(ACTION204);
                    adaptor.addChild(root_0, ACTION204_tree);
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
    // ANTLRParser.g:836:1: rewriteTreeEbnf : lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$lp,\"BLOCK\"] rewriteTreeAlt ) ) ;
    public final ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf() throws RecognitionException {
        ANTLRParser.rewriteTreeEbnf_return retval = new ANTLRParser.rewriteTreeEbnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token RPAREN206=null;
        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt205 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix207 = null;


        GrammarAST lp_tree=null;
        GrammarAST RPAREN206_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTreeAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAlt");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");

            Token firstToken = input.LT(1);

        try {
            // ANTLRParser.g:844:2: (lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$lp,\"BLOCK\"] rewriteTreeAlt ) ) )
            // ANTLRParser.g:844:4: lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTreeEbnf4579); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4581);
            rewriteTreeAlt205=rewriteTreeAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAlt.add(rewriteTreeAlt205.getTree());
            RPAREN206=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTreeEbnf4583); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN206);

            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4585);
            ebnfSuffix207=ebnfSuffix();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix207.getTree());


            // AST REWRITE
            // elements: rewriteTreeAlt, ebnfSuffix
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 844:47: -> ^( ebnfSuffix ^( BLOCK[$lp,\"BLOCK\"] rewriteTreeAlt ) )
            {
                // ANTLRParser.g:844:50: ^( ebnfSuffix ^( BLOCK[$lp,\"BLOCK\"] rewriteTreeAlt ) )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                // ANTLRParser.g:844:63: ^( BLOCK[$lp,\"BLOCK\"] rewriteTreeAlt )
                {
                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(BLOCK, lp, "BLOCK"), root_2);

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
    // ANTLRParser.g:847:1: rewriteTree : TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) ;
    public final ANTLRParser.rewriteTree_return rewriteTree() throws RecognitionException {
        ANTLRParser.rewriteTree_return retval = new ANTLRParser.rewriteTree_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN208=null;
        Token RPAREN211=null;
        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom209 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement210 = null;


        GrammarAST TREE_BEGIN208_tree=null;
        GrammarAST RPAREN211_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:848:2: ( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) )
            // ANTLRParser.g:848:4: TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN
            {
            TREE_BEGIN208=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewriteTree4609); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN208);

            pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTree4611);
            rewriteTreeAtom209=rewriteTreeAtom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom209.getTree());
            // ANTLRParser.g:848:31: ( rewriteTreeElement )*
            loop69:
            do {
                int alt69=2;
                int LA69_0 = input.LA(1);

                if ( (LA69_0==ACTION||LA69_0==LPAREN||LA69_0==DOLLAR||LA69_0==TREE_BEGIN||(LA69_0>=TOKEN_REF && LA69_0<=RULE_REF)||LA69_0==STRING_LITERAL) ) {
                    alt69=1;
                }


                switch (alt69) {
            	case 1 :
            	    // ANTLRParser.g:848:31: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTree4613);
            	    rewriteTreeElement210=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement210.getTree());

            	    }
            	    break;

            	default :
            	    break loop69;
                }
            } while (true);

            RPAREN211=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTree4616); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN211);



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
            // 849:3: -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
            {
                // ANTLRParser.g:849:6: ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_TREE_BEGIN.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rewriteTreeAtom.nextTree());
                // ANTLRParser.g:849:35: ( rewriteTreeElement )*
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
    // ANTLRParser.g:852:1: rewriteTemplate : ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );
    public final ANTLRParser.rewriteTemplate_return rewriteTemplate() throws RecognitionException {
        ANTLRParser.rewriteTemplate_return retval = new ANTLRParser.rewriteTemplate_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token str=null;
        Token TEMPLATE212=null;
        Token LPAREN213=null;
        Token RPAREN215=null;
        Token ACTION218=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs214 = null;

        ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef216 = null;

        ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead217 = null;


        GrammarAST str_tree=null;
        GrammarAST TEMPLATE212_tree=null;
        GrammarAST LPAREN213_tree=null;
        GrammarAST RPAREN215_tree=null;
        GrammarAST ACTION218_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_DOUBLE_QUOTE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_QUOTE_STRING_LITERAL");
        RewriteRuleTokenStream stream_TEMPLATE=new RewriteRuleTokenStream(adaptor,"token TEMPLATE");
        RewriteRuleTokenStream stream_DOUBLE_ANGLE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_ANGLE_STRING_LITERAL");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:863:2: ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION )
            int alt71=4;
            switch ( input.LA(1) ) {
            case TEMPLATE:
                {
                alt71=1;
                }
                break;
            case TOKEN_REF:
            case RULE_REF:
                {
                alt71=2;
                }
                break;
            case LPAREN:
                {
                alt71=3;
                }
                break;
            case ACTION:
                {
                alt71=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 71, 0, input);

                throw nvae;
            }

            switch (alt71) {
                case 1 :
                    // ANTLRParser.g:864:3: TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
                    {
                    TEMPLATE212=(Token)match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate4648); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TEMPLATE.add(TEMPLATE212);

                    LPAREN213=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplate4650); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN213);

                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4652);
                    rewriteTemplateArgs214=rewriteTemplateArgs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs214.getTree());
                    RPAREN215=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplate4654); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN215);

                    // ANTLRParser.g:865:3: (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
                    int alt70=2;
                    int LA70_0 = input.LA(1);

                    if ( (LA70_0==DOUBLE_QUOTE_STRING_LITERAL) ) {
                        alt70=1;
                    }
                    else if ( (LA70_0==DOUBLE_ANGLE_STRING_LITERAL) ) {
                        alt70=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 70, 0, input);

                        throw nvae;
                    }
                    switch (alt70) {
                        case 1 :
                            // ANTLRParser.g:865:5: str= DOUBLE_QUOTE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_QUOTE_STRING_LITERAL,FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4662); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOUBLE_QUOTE_STRING_LITERAL.add(str);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:865:39: str= DOUBLE_ANGLE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_ANGLE_STRING_LITERAL,FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4668); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOUBLE_ANGLE_STRING_LITERAL.add(str);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: TEMPLATE, rewriteTemplateArgs, str
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
                    // 866:3: -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                    {
                        // ANTLRParser.g:866:6: ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, TEMPLATE212, "TEMPLATE"), root_1);

                        // ANTLRParser.g:866:39: ( rewriteTemplateArgs )?
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
                    // ANTLRParser.g:869:3: rewriteTemplateRef
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplateRef_in_rewriteTemplate4694);
                    rewriteTemplateRef216=rewriteTemplateRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplateRef216.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:872:3: rewriteIndirectTemplateHead
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4703);
                    rewriteIndirectTemplateHead217=rewriteIndirectTemplateHead();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteIndirectTemplateHead217.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:875:3: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION218=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplate4712); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION218_tree = (GrammarAST)adaptor.create(ACTION218);
                    adaptor.addChild(root_0, ACTION218_tree);
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
    // ANTLRParser.g:878:1: rewriteTemplateRef : id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef() throws RecognitionException {
        ANTLRParser.rewriteTemplateRef_return retval = new ANTLRParser.rewriteTemplateRef_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN220=null;
        Token RPAREN222=null;
        ANTLRParser.id_return id219 = null;

        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs221 = null;


        GrammarAST LPAREN220_tree=null;
        GrammarAST RPAREN222_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:880:2: ( id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:880:4: id LPAREN rewriteTemplateArgs RPAREN
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateRef4725);
            id219=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id219.getTree());
            LPAREN220=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplateRef4727); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN220);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4729);
            rewriteTemplateArgs221=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs221.getTree());
            RPAREN222=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplateRef4731); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN222);



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
            // 881:3: -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:881:6: ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, LPAREN220, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:881:40: ( rewriteTemplateArgs )?
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
    // ANTLRParser.g:884:1: rewriteIndirectTemplateHead : lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead() throws RecognitionException {
        ANTLRParser.rewriteIndirectTemplateHead_return retval = new ANTLRParser.rewriteIndirectTemplateHead_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token ACTION223=null;
        Token RPAREN224=null;
        Token LPAREN225=null;
        Token RPAREN227=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs226 = null;


        GrammarAST lp_tree=null;
        GrammarAST ACTION223_tree=null;
        GrammarAST RPAREN224_tree=null;
        GrammarAST LPAREN225_tree=null;
        GrammarAST RPAREN227_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:886:2: (lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:886:4: lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4760); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            ACTION223=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteIndirectTemplateHead4762); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION223);

            RPAREN224=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4764); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN224);

            LPAREN225=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4766); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN225);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4768);
            rewriteTemplateArgs226=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs226.getTree());
            RPAREN227=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4770); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN227);



            // AST REWRITE
            // elements: ACTION, rewriteTemplateArgs
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 887:3: -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:887:6: ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, lp, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, stream_ACTION.nextNode());
                // ANTLRParser.g:887:40: ( rewriteTemplateArgs )?
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
    // ANTLRParser.g:890:1: rewriteTemplateArgs : ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | );
    public final ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs() throws RecognitionException {
        ANTLRParser.rewriteTemplateArgs_return retval = new ANTLRParser.rewriteTemplateArgs_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token COMMA229=null;
        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg228 = null;

        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg230 = null;


        GrammarAST COMMA229_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_rewriteTemplateArg=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArg");
        try {
            // ANTLRParser.g:891:2: ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | )
            int alt73=2;
            int LA73_0 = input.LA(1);

            if ( ((LA73_0>=TOKEN_REF && LA73_0<=RULE_REF)) ) {
                alt73=1;
            }
            else if ( (LA73_0==RPAREN) ) {
                alt73=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 73, 0, input);

                throw nvae;
            }
            switch (alt73) {
                case 1 :
                    // ANTLRParser.g:891:4: rewriteTemplateArg ( COMMA rewriteTemplateArg )*
                    {
                    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4795);
                    rewriteTemplateArg228=rewriteTemplateArg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg228.getTree());
                    // ANTLRParser.g:891:23: ( COMMA rewriteTemplateArg )*
                    loop72:
                    do {
                        int alt72=2;
                        int LA72_0 = input.LA(1);

                        if ( (LA72_0==COMMA) ) {
                            alt72=1;
                        }


                        switch (alt72) {
                    	case 1 :
                    	    // ANTLRParser.g:891:24: COMMA rewriteTemplateArg
                    	    {
                    	    COMMA229=(Token)match(input,COMMA,FOLLOW_COMMA_in_rewriteTemplateArgs4798); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA229);

                    	    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4800);
                    	    rewriteTemplateArg230=rewriteTemplateArg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg230.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop72;
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
                    // 892:3: -> ^( ARGLIST ( rewriteTemplateArg )+ )
                    {
                        // ANTLRParser.g:892:6: ^( ARGLIST ( rewriteTemplateArg )+ )
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
                    // ANTLRParser.g:894:2: 
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
    // ANTLRParser.g:896:1: rewriteTemplateArg : id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) ;
    public final ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg() throws RecognitionException {
        ANTLRParser.rewriteTemplateArg_return retval = new ANTLRParser.rewriteTemplateArg_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN232=null;
        Token ACTION233=null;
        ANTLRParser.id_return id231 = null;


        GrammarAST ASSIGN232_tree=null;
        GrammarAST ACTION233_tree=null;
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:897:2: ( id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) )
            // ANTLRParser.g:897:6: id ASSIGN ACTION
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateArg4829);
            id231=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id231.getTree());
            ASSIGN232=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_rewriteTemplateArg4831); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN232);

            ACTION233=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplateArg4833); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION233);



            // AST REWRITE
            // elements: ACTION, id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 897:23: -> ^( ARG[$ASSIGN] id ACTION )
            {
                // ANTLRParser.g:897:26: ^( ARG[$ASSIGN] id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ARG, ASSIGN232), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                adaptor.addChild(root_1, stream_ACTION.nextNode());

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
    // ANTLRParser.g:904:1: id : ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] );
    public final ANTLRParser.id_return id() throws RecognitionException {
        ANTLRParser.id_return retval = new ANTLRParser.id_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RULE_REF234=null;
        Token TOKEN_REF235=null;

        GrammarAST RULE_REF234_tree=null;
        GrammarAST TOKEN_REF235_tree=null;
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");

        try {
            // ANTLRParser.g:905:5: ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] )
            int alt74=2;
            int LA74_0 = input.LA(1);

            if ( (LA74_0==RULE_REF) ) {
                alt74=1;
            }
            else if ( (LA74_0==TOKEN_REF) ) {
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
                    // ANTLRParser.g:905:7: RULE_REF
                    {
                    RULE_REF234=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_id4862); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF234);



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
                    // 905:17: -> ID[$RULE_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, RULE_REF234));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:906:7: TOKEN_REF
                    {
                    TOKEN_REF235=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_id4875); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF235);



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
                    // 906:17: -> ID[$TOKEN_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TOKEN_REF235));

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
    // $ANTLR end "id"

    public static class qid_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "qid"
    // ANTLRParser.g:909:1: qid : id ( WILDCARD id )* -> ID[$text] ;
    public final ANTLRParser.qid_return qid() throws RecognitionException {
        ANTLRParser.qid_return retval = new ANTLRParser.qid_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token WILDCARD237=null;
        ANTLRParser.id_return id236 = null;

        ANTLRParser.id_return id238 = null;


        GrammarAST WILDCARD237_tree=null;
        RewriteRuleTokenStream stream_WILDCARD=new RewriteRuleTokenStream(adaptor,"token WILDCARD");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:909:5: ( id ( WILDCARD id )* -> ID[$text] )
            // ANTLRParser.g:909:7: id ( WILDCARD id )*
            {
            pushFollow(FOLLOW_id_in_qid4896);
            id236=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id236.getTree());
            // ANTLRParser.g:909:10: ( WILDCARD id )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==WILDCARD) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // ANTLRParser.g:909:11: WILDCARD id
            	    {
            	    WILDCARD237=(Token)match(input,WILDCARD,FOLLOW_WILDCARD_in_qid4899); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_WILDCARD.add(WILDCARD237);

            	    pushFollow(FOLLOW_id_in_qid4901);
            	    id238=id();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_id.add(id238.getTree());

            	    }
            	    break;

            	default :
            	    break loop75;
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
            // 909:25: -> ID[$text]
            {
                adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, input.toString(retval.start,input.LT(-1))));

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
    // $ANTLR end "qid"

    public static class alternativeEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "alternativeEntry"
    // ANTLRParser.g:911:1: alternativeEntry : alternative EOF ;
    public final ANTLRParser.alternativeEntry_return alternativeEntry() throws RecognitionException {
        ANTLRParser.alternativeEntry_return retval = new ANTLRParser.alternativeEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF240=null;
        ANTLRParser.alternative_return alternative239 = null;


        GrammarAST EOF240_tree=null;

        try {
            // ANTLRParser.g:911:18: ( alternative EOF )
            // ANTLRParser.g:911:20: alternative EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_alternative_in_alternativeEntry4917);
            alternative239=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, alternative239.getTree());
            EOF240=(Token)match(input,EOF,FOLLOW_EOF_in_alternativeEntry4919); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF240_tree = (GrammarAST)adaptor.create(EOF240);
            adaptor.addChild(root_0, EOF240_tree);
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
    // ANTLRParser.g:912:1: elementEntry : element EOF ;
    public final ANTLRParser.elementEntry_return elementEntry() throws RecognitionException {
        ANTLRParser.elementEntry_return retval = new ANTLRParser.elementEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF242=null;
        ANTLRParser.element_return element241 = null;


        GrammarAST EOF242_tree=null;

        try {
            // ANTLRParser.g:912:14: ( element EOF )
            // ANTLRParser.g:912:16: element EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_element_in_elementEntry4928);
            element241=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, element241.getTree());
            EOF242=(Token)match(input,EOF,FOLLOW_EOF_in_elementEntry4930); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF242_tree = (GrammarAST)adaptor.create(EOF242);
            adaptor.addChild(root_0, EOF242_tree);
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
    // ANTLRParser.g:913:1: ruleEntry : rule EOF ;
    public final ANTLRParser.ruleEntry_return ruleEntry() throws RecognitionException {
        ANTLRParser.ruleEntry_return retval = new ANTLRParser.ruleEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF244=null;
        ANTLRParser.rule_return rule243 = null;


        GrammarAST EOF244_tree=null;

        try {
            // ANTLRParser.g:913:11: ( rule EOF )
            // ANTLRParser.g:913:13: rule EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_rule_in_ruleEntry4938);
            rule243=rule();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rule243.getTree());
            EOF244=(Token)match(input,EOF,FOLLOW_EOF_in_ruleEntry4940); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF244_tree = (GrammarAST)adaptor.create(EOF244);
            adaptor.addChild(root_0, EOF244_tree);
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
    // ANTLRParser.g:914:1: blockEntry : block EOF ;
    public final ANTLRParser.blockEntry_return blockEntry() throws RecognitionException {
        ANTLRParser.blockEntry_return retval = new ANTLRParser.blockEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF246=null;
        ANTLRParser.block_return block245 = null;


        GrammarAST EOF246_tree=null;

        try {
            // ANTLRParser.g:914:12: ( block EOF )
            // ANTLRParser.g:914:14: block EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_block_in_blockEntry4948);
            block245=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block245.getTree());
            EOF246=(Token)match(input,EOF,FOLLOW_EOF_in_blockEntry4950); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF246_tree = (GrammarAST)adaptor.create(EOF246);
            adaptor.addChild(root_0, EOF246_tree);
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
        // ANTLRParser.g:799:7: ( rewriteTemplate )
        // ANTLRParser.g:799:7: rewriteTemplate
        {
        pushFollow(FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4297);
        rewriteTemplate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_ANTLRParser

    // $ANTLR start synpred2_ANTLRParser
    public final void synpred2_ANTLRParser_fragment() throws RecognitionException {   
        // ANTLRParser.g:805:7: ( rewriteTreeAlt )
        // ANTLRParser.g:805:7: rewriteTreeAlt
        {
        pushFollow(FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4336);
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


    protected DFA44 dfa44 = new DFA44(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA66 dfa66 = new DFA66(this);
    static final String DFA44_eotS =
        "\13\uffff";
    static final String DFA44_eofS =
        "\1\uffff\1\7\2\4\7\uffff";
    static final String DFA44_minS =
        "\1\74\3\4\2\uffff\1\76\4\uffff";
    static final String DFA44_maxS =
        "\4\143\2\uffff\1\143\4\uffff";
    static final String DFA44_acceptS =
        "\4\uffff\1\4\1\6\1\uffff\1\5\1\1\1\2\1\3";
    static final String DFA44_specialS =
        "\13\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\5\1\uffff\1\2\1\1\3\uffff\1\3\37\uffff\1\4",
            "\1\7\11\uffff\1\7\1\uffff\1\7\26\uffff\3\7\4\uffff\4\7\1\uffff"+
            "\2\7\1\uffff\1\6\1\10\1\uffff\2\7\1\uffff\1\7\1\uffff\2\7\3"+
            "\uffff\1\7\37\uffff\1\7",
            "\1\4\11\uffff\1\4\1\uffff\1\4\26\uffff\3\4\1\uffff\1\4\2\uffff"+
            "\4\4\1\uffff\2\4\1\uffff\1\6\1\10\1\uffff\2\4\1\uffff\1\4\1"+
            "\uffff\2\4\3\uffff\1\4\37\uffff\1\4",
            "\1\4\13\uffff\1\4\26\uffff\3\4\1\uffff\1\4\2\uffff\4\4\1\uffff"+
            "\2\4\2\uffff\1\10\1\uffff\2\4\1\uffff\1\4\1\uffff\2\4\3\uffff"+
            "\1\4\37\uffff\1\4",
            "",
            "",
            "\1\12\1\11\3\uffff\1\12\37\uffff\1\12",
            "",
            "",
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
            return "612:1: atom : ( range ( ROOT | BANG )? | {...}? id WILDCARD ruleref -> ^( DOT[$WILDCARD] id ruleref ) | {...}? id WILDCARD terminal -> ^( DOT[$WILDCARD] id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? );";
        }
    }
    static final String DFA63_eotS =
        "\16\uffff";
    static final String DFA63_eofS =
        "\1\10\1\uffff\2\6\12\uffff";
    static final String DFA63_minS =
        "\1\20\1\uffff\1\20\1\16\1\20\1\0\3\uffff\2\20\1\16\1\20\1\50";
    static final String DFA63_maxS =
        "\1\103\1\uffff\3\103\1\0\3\uffff\4\103\1\61";
    static final String DFA63_acceptS =
        "\1\uffff\1\1\4\uffff\1\2\1\3\1\4\5\uffff";
    static final String DFA63_specialS =
        "\5\uffff\1\0\10\uffff}>";
    static final String[] DFA63_transitionS = {
            "\1\5\22\uffff\1\1\3\uffff\1\10\1\4\1\10\11\uffff\1\10\1\uffff"+
            "\1\6\2\uffff\1\7\1\10\1\6\3\uffff\1\3\1\2\3\uffff\1\6",
            "",
            "\1\6\26\uffff\1\6\1\11\1\6\4\uffff\1\6\1\uffff\2\6\1\uffff"+
            "\1\6\1\uffff\1\6\3\uffff\2\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\1\uffff\1\6\26\uffff\1\6\1\11\1\6\4\uffff\1\6\1\uffff"+
            "\2\6\1\uffff\1\6\1\uffff\1\6\3\uffff\2\6\3\uffff\2\6\3\uffff"+
            "\1\6",
            "\1\12\27\uffff\1\6\14\uffff\1\6\4\uffff\1\6\3\uffff\2\6\3\uffff"+
            "\1\6",
            "\1\uffff",
            "",
            "",
            "",
            "\1\6\27\uffff\1\6\1\1\13\uffff\1\6\4\uffff\1\6\3\uffff\1\13"+
            "\1\14\3\uffff\1\6",
            "\1\6\27\uffff\1\6\1\15\4\uffff\1\6\1\uffff\2\6\3\uffff\1\6"+
            "\4\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\1\uffff\1\6\27\uffff\2\6\3\uffff\1\1\1\6\1\uffff\2\6\3"+
            "\uffff\1\6\4\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\27\uffff\2\6\3\uffff\1\1\1\6\1\uffff\2\6\3\uffff\1\6\4"+
            "\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\1\5\uffff\1\6\1\uffff\2\6"
    };

    static final short[] DFA63_eot = DFA.unpackEncodedString(DFA63_eotS);
    static final short[] DFA63_eof = DFA.unpackEncodedString(DFA63_eofS);
    static final char[] DFA63_min = DFA.unpackEncodedStringToUnsignedChars(DFA63_minS);
    static final char[] DFA63_max = DFA.unpackEncodedStringToUnsignedChars(DFA63_maxS);
    static final short[] DFA63_accept = DFA.unpackEncodedString(DFA63_acceptS);
    static final short[] DFA63_special = DFA.unpackEncodedString(DFA63_specialS);
    static final short[][] DFA63_transition;

    static {
        int numStates = DFA63_transitionS.length;
        DFA63_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA63_transition[i] = DFA.unpackEncodedString(DFA63_transitionS[i]);
        }
    }

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = DFA63_eot;
            this.eof = DFA63_eof;
            this.min = DFA63_min;
            this.max = DFA63_max;
            this.accept = DFA63_accept;
            this.special = DFA63_special;
            this.transition = DFA63_transition;
        }
        public String getDescription() {
            return "796:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_5 = input.LA(1);

                         
                        int index63_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_ANTLRParser()) ) {s = 1;}

                        else if ( (synpred2_ANTLRParser()) ) {s = 6;}

                         
                        input.seek(index63_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 63, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA66_eotS =
        "\15\uffff";
    static final String DFA66_eofS =
        "\1\uffff\3\11\1\uffff\1\11\2\uffff\1\11\2\uffff\2\11";
    static final String DFA66_minS =
        "\1\20\1\16\2\20\1\76\1\20\2\uffff\1\20\2\uffff\2\20";
    static final String DFA66_maxS =
        "\4\103\1\77\1\103\2\uffff\1\103\2\uffff\2\103";
    static final String DFA66_acceptS =
        "\6\uffff\1\3\1\4\1\uffff\1\1\1\2\2\uffff";
    static final String DFA66_specialS =
        "\15\uffff}>";
    static final String[] DFA66_transitionS = {
            "\1\5\27\uffff\1\7\14\uffff\1\4\4\uffff\1\6\3\uffff\1\1\1\2\3"+
            "\uffff\1\3",
            "\1\10\1\uffff\1\11\26\uffff\3\11\4\uffff\1\12\1\uffff\2\12"+
            "\1\uffff\1\11\1\uffff\1\11\3\uffff\2\11\3\uffff\2\11\3\uffff"+
            "\1\11",
            "\1\11\26\uffff\3\11\4\uffff\1\12\1\uffff\2\12\1\uffff\1\11"+
            "\1\uffff\1\11\3\uffff\2\11\3\uffff\2\11\3\uffff\1\11",
            "\1\11\26\uffff\3\11\4\uffff\1\12\1\uffff\2\12\1\uffff\1\11"+
            "\1\uffff\1\11\3\uffff\2\11\3\uffff\2\11\3\uffff\1\11",
            "\1\14\1\13",
            "\1\11\26\uffff\3\11\4\uffff\1\12\1\uffff\2\12\1\uffff\1\11"+
            "\1\uffff\1\11\3\uffff\2\11\3\uffff\2\11\3\uffff\1\11",
            "",
            "",
            "\1\11\26\uffff\3\11\4\uffff\1\12\1\uffff\2\12\1\uffff\1\11"+
            "\1\uffff\1\11\3\uffff\2\11\3\uffff\2\11\3\uffff\1\11",
            "",
            "",
            "\1\11\26\uffff\3\11\4\uffff\1\12\1\uffff\2\12\1\uffff\1\11"+
            "\1\uffff\1\11\3\uffff\2\11\3\uffff\2\11\3\uffff\1\11",
            "\1\11\26\uffff\3\11\4\uffff\1\12\1\uffff\2\12\1\uffff\1\11"+
            "\1\uffff\1\11\3\uffff\2\11\3\uffff\2\11\3\uffff\1\11"
    };

    static final short[] DFA66_eot = DFA.unpackEncodedString(DFA66_eotS);
    static final short[] DFA66_eof = DFA.unpackEncodedString(DFA66_eofS);
    static final char[] DFA66_min = DFA.unpackEncodedStringToUnsignedChars(DFA66_minS);
    static final char[] DFA66_max = DFA.unpackEncodedStringToUnsignedChars(DFA66_maxS);
    static final short[] DFA66_accept = DFA.unpackEncodedString(DFA66_acceptS);
    static final short[] DFA66_special = DFA.unpackEncodedString(DFA66_specialS);
    static final short[][] DFA66_transition;

    static {
        int numStates = DFA66_transitionS.length;
        DFA66_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA66_transition[i] = DFA.unpackEncodedString(DFA66_transitionS[i]);
        }
    }

    class DFA66 extends DFA {

        public DFA66(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 66;
            this.eot = DFA66_eot;
            this.eof = DFA66_eof;
            this.min = DFA66_min;
            this.max = DFA66_max;
            this.accept = DFA66_accept;
            this.special = DFA66_special;
            this.transition = DFA66_transition;
        }
        public String getDescription() {
            return "816:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[\"BLOCK\"] ^( ALT[\"ALT\"] rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );";
        }
    }
 

    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec476 = new BitSet(new long[]{0x000000000F000000L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec513 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_grammarSpec515 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_grammarSpec517 = new BitSet(new long[]{0xC800000070F80040L});
    public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec561 = new BitSet(new long[]{0xC800000070F80040L});
    public static final BitSet FOLLOW_rules_in_grammarSpec589 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_grammarSpec632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_grammarType830 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_PARSER_in_grammarType870 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_TREE_in_grammarType909 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct1029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct1079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrScope_in_prequelConstruct1115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_prequelConstruct1158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec1175 = new BitSet(new long[]{0xE000000000000000L});
    public static final BitSet FOLLOW_option_in_optionsSpec1178 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_optionsSpec1180 = new BitSet(new long[]{0xE000000000000000L});
    public static final BitSet FOLLOW_RBRACE_in_optionsSpec1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_option1221 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_option1223 = new BitSet(new long[]{0xC001000000000000L,0x0000000000000009L});
    public static final BitSet FOLLOW_optionValue_in_option1226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_optionValue1276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_optionValue1300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_optionValue1323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_optionValue1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_delegateGrammars1368 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1370 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_COMMA_in_delegateGrammars1373 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1375 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_SEMI_in_delegateGrammars1379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1406 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar1408 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec1437 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec1439 = new BitSet(new long[]{0xE000000000000000L});
    public static final BitSet FOLLOW_RBRACE_in_tokensSpec1442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_tokenSpec1462 = new BitSet(new long[]{0x0000208000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec1468 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec1470 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_tokenSpec1502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_tokenSpec1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope1522 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_attrScope1524 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope1526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_action1552 = new BitSet(new long[]{0xC000000003000000L});
    public static final BitSet FOLLOW_actionScopeName_in_action1555 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_COLONCOLON_in_action1557 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_action1561 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action1563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_actionScopeName1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_actionScopeName1596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARSER_in_actionScopeName1611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_rules1630 = new BitSet(new long[]{0xC000000070800042L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_rule1709 = new BitSet(new long[]{0xC000000070800000L});
    public static final BitSet FOLLOW_ruleModifiers_in_rule1753 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_rule1776 = new BitSet(new long[]{0x0800001180284000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule1809 = new BitSet(new long[]{0x0800001180280000L});
    public static final BitSet FOLLOW_ruleReturns_in_rule1819 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_rulePrequel_in_rule1857 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_COLON_in_rule1873 = new BitSet(new long[]{0xD608010000010010L,0x0000000800000008L});
    public static final BitSet FOLLOW_altListAsBlock_in_rule1902 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_rule1917 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule1926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup2015 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup2018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler2035 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler2037 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler2039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause2062 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause2064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_throwsSpec_in_rulePrequel2091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rulePrequel2099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_rulePrequel2107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_rulePrequel2115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns2135 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns2138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROWS_in_throwsSpec2163 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2165 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_COMMA_in_throwsSpec2168 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2170 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2201 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec2203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2216 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2218 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_COMMA_in_ruleScopeSpec2221 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2223 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_SEMI_in_ruleScopeSpec2227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_ruleAction2257 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_ruleAction2259 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction2261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModifier_in_ruleModifiers2299 = new BitSet(new long[]{0x0000000070800002L});
    public static final BitSet FOLLOW_set_in_ruleModifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternative_in_altList2375 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_OR_in_altList2378 = new BitSet(new long[]{0xD608010000010010L,0x0000000800000008L});
    public static final BitSet FOLLOW_alternative_in_altList2380 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_altList_in_altListAsBlock2410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elements_in_alternative2437 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elements2537 = new BitSet(new long[]{0xD400010000010012L,0x0000000800000008L});
    public static final BitSet FOLLOW_labeledElement_in_element2564 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element2611 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element2659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element2666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element2673 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_element2679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element2707 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_labeledElement2761 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement2764 = new BitSet(new long[]{0xD000010000000000L,0x0000000800000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement2767 = new BitSet(new long[]{0xD000010000000000L,0x0000000800000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement2772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_labeledElement2774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec2796 = new BitSet(new long[]{0xD400010000010010L,0x0000000800000008L});
    public static final BitSet FOLLOW_element_in_treeSpec2837 = new BitSet(new long[]{0xD400010000010010L,0x0000000800000008L});
    public static final BitSet FOLLOW_element_in_treeSpec2868 = new BitSet(new long[]{0xD400030000010010L,0x0000000800000008L});
    public static final BitSet FOLLOW_RPAREN_in_treeSpec2877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_ebnf2911 = new BitSet(new long[]{0x0013C40000000002L});
    public static final BitSet FOLLOW_blockSuffixe_in_ebnf2946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffixe2997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffixe3011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffixe3019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffixe3030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_ebnfSuffix3061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_ebnfSuffix3076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom3093 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3096 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom3148 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_WILDCARD_in_atom3150 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_ruleref_in_atom3152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom3187 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_WILDCARD_in_atom3189 = new BitSet(new long[]{0x4000000000000000L,0x0000000800000008L});
    public static final BitSet FOLLOW_terminal_in_atom3191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom3217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom3227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notSet_in_atom3235 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet3274 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_notTerminal_in_notSet3276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet3292 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_block_in_notSet3294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_notTerminal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_block3362 = new BitSet(new long[]{0xDE08011100290010L,0x0000000800000008L});
    public static final BitSet FOLLOW_optionsSpec_in_block3408 = new BitSet(new long[]{0xDE08011100290010L,0x0000000800000008L});
    public static final BitSet FOLLOW_ruleAction_in_block3503 = new BitSet(new long[]{0x0800001100290000L});
    public static final BitSet FOLLOW_ACTION_in_block3519 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_COLON_in_block3570 = new BitSet(new long[]{0xD608010000010010L,0x0000000800000008L});
    public static final BitSet FOLLOW_altList_in_block3623 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_block3641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref3711 = new BitSet(new long[]{0x0010800000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref3713 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_ruleref3723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_ruleref3727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rangeElement_in_range3790 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RANGE_in_range3792 = new BitSet(new long[]{0xC000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rangeElement_in_range3795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_rangeElement0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal3883 = new BitSet(new long[]{0x0010880000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal3885 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3888 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal3909 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3911 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_DOT_in_terminal3955 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3957 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal3985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_terminal4009 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_elementOptions4073 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions4075 = new BitSet(new long[]{0x0000104000000000L});
    public static final BitSet FOLLOW_COMMA_in_elementOptions4078 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions4080 = new BitSet(new long[]{0x0000104000000000L});
    public static final BitSet FOLLOW_GT_in_elementOptions4084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_elementOption4119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_elementOption4141 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption4143 = new BitSet(new long[]{0xC000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_qid_in_elementOption4147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption4151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicatedRewrite_in_rewrite4166 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_nakedRewrite_in_rewrite4169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_predicatedRewrite4187 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite4189 = new BitSet(new long[]{0xC520010800010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite4191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_nakedRewrite4231 = new BitSet(new long[]{0xC520010800010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite4233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_rewriteAlt4297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteAlt4336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ETC_in_rewriteAlt4351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4382 = new BitSet(new long[]{0xC420010000010002L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4412 = new BitSet(new long[]{0x0003400000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTree_in_rewriteTreeElement4443 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom4506 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom4508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_rewriteTreeAtom4529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLAR_in_rewriteTreeAtom4543 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_rewriteTreeAtom4545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTreeAtom4556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTreeEbnf4579 = new BitSet(new long[]{0xC420010000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4581 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTreeEbnf4583 = new BitSet(new long[]{0x0003400000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewriteTree4609 = new BitSet(new long[]{0xC020000000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTree4611 = new BitSet(new long[]{0xC420030000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTree4613 = new BitSet(new long[]{0xC420030000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTree4616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate4648 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplate4650 = new BitSet(new long[]{0xC000020000000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4652 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplate4654 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateRef_in_rewriteTemplate4694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplate4712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateRef4725 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplateRef4727 = new BitSet(new long[]{0xC000020000000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4729 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplateRef4731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4760 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteIndirectTemplateHead4762 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4764 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4766 = new BitSet(new long[]{0xC000020000000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4768 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4795 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_COMMA_in_rewriteTemplateArgs4798 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4800 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateArg4829 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_rewriteTemplateArg4831 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplateArg4833 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_id4862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_id4875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_qid4896 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_WILDCARD_in_qid4899 = new BitSet(new long[]{0xC000000000000000L});
    public static final BitSet FOLLOW_id_in_qid4901 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_alternative_in_alternativeEntry4917 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_alternativeEntry4919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elementEntry4928 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_elementEntry4930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_ruleEntry4938 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_ruleEntry4940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_blockEntry4948 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_blockEntry4950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4336 = new BitSet(new long[]{0x0000000000000002L});

}