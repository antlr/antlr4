// $ANTLR 3.2.1-SNAPSHOT May 24, 2010 15:02:05 ANTLRParser.g 2010-06-14 12:35:32

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "MODE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "RESULT", "ALT_REWRITE"
    };
    public static final int LT=44;
    public static final int COMBINED=91;
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
    public static final int GRAMMAR=27;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int WSCHARS=66;
    public static final int RULEACTIONS=76;
    public static final int INITACTION=92;
    public static final int ALT_REWRITE=102;
    public static final int IMPLIES=43;
    public static final int RBRACE=62;
    public static final int RULE=73;
    public static final int ACTION_ESC=17;
    public static final int PRIVATE=30;
    public static final int SRC=7;
    public static final int THROWS=32;
    public static final int INT=65;
    public static final int CHAR_RANGE=83;
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
    public static final int LABEL=93;
    public static final int TEMPLATE=35;
    public static final int SYN_SEMPRED=95;
    public static final int ERRCHAR=72;
    public static final int BLOCK=77;
    public static final int PLUS_ASSIGN=51;
    public static final int ASSIGN=46;
    public static final int PUBLIC=29;
    public static final int POSITIVE_CLOSURE=81;
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


    public static class grammarSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "grammarSpec"
    // ANTLRParser.g:141:1: grammarSpec : ( DOC_COMMENT )? grammarType id SEMI sync ( prequelConstruct sync )* rules ( mode )* EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ( mode )* ) ;
    public final ANTLRParser.grammarSpec_return grammarSpec() throws RecognitionException {
        ANTLRParser.grammarSpec_return retval = new ANTLRParser.grammarSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOC_COMMENT1=null;
        Token SEMI4=null;
        Token EOF10=null;
        ANTLRParser.grammarType_return grammarType2 = null;

        ANTLRParser.id_return id3 = null;

        ANTLRParser.sync_return sync5 = null;

        ANTLRParser.prequelConstruct_return prequelConstruct6 = null;

        ANTLRParser.sync_return sync7 = null;

        ANTLRParser.rules_return rules8 = null;

        ANTLRParser.mode_return mode9 = null;


        GrammarAST DOC_COMMENT1_tree=null;
        GrammarAST SEMI4_tree=null;
        GrammarAST EOF10_tree=null;
        RewriteRuleTokenStream stream_DOC_COMMENT=new RewriteRuleTokenStream(adaptor,"token DOC_COMMENT");
        RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
        RewriteRuleSubtreeStream stream_prequelConstruct=new RewriteRuleSubtreeStream(adaptor,"rule prequelConstruct");
        RewriteRuleSubtreeStream stream_grammarType=new RewriteRuleSubtreeStream(adaptor,"rule grammarType");
        RewriteRuleSubtreeStream stream_rules=new RewriteRuleSubtreeStream(adaptor,"rule rules");
        RewriteRuleSubtreeStream stream_mode=new RewriteRuleSubtreeStream(adaptor,"rule mode");
        try {
            // ANTLRParser.g:142:5: ( ( DOC_COMMENT )? grammarType id SEMI sync ( prequelConstruct sync )* rules ( mode )* EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ( mode )* ) )
            // ANTLRParser.g:146:7: ( DOC_COMMENT )? grammarType id SEMI sync ( prequelConstruct sync )* rules ( mode )* EOF
            {
            // ANTLRParser.g:146:7: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ANTLRParser.g:146:7: DOC_COMMENT
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
            // ANTLRParser.g:164:12: ( prequelConstruct sync )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=OPTIONS && LA2_0<=IMPORT)||LA2_0==AT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ANTLRParser.g:164:14: prequelConstruct sync
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
            // ANTLRParser.g:172:4: ( mode )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==MODE) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ANTLRParser.g:172:4: mode
            	    {
            	    pushFollow(FOLLOW_mode_in_grammarSpec586);
            	    mode9=mode();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_mode.add(mode9.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            EOF10=(Token)match(input,EOF,FOLLOW_EOF_in_grammarSpec630); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF10);



            // AST REWRITE
            // elements: id, prequelConstruct, grammarType, mode, rules, DOC_COMMENT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 185:7: -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ( mode )* )
            {
                // ANTLRParser.g:185:10: ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ( mode )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_grammarType.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:187:14: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:188:14: ( prequelConstruct )*
                while ( stream_prequelConstruct.hasNext() ) {
                    adaptor.addChild(root_1, stream_prequelConstruct.nextTree());

                }
                stream_prequelConstruct.reset();
                adaptor.addChild(root_1, stream_rules.nextTree());
                // ANTLRParser.g:190:14: ( mode )*
                while ( stream_mode.hasNext() ) {
                    adaptor.addChild(root_1, stream_mode.nextTree());

                }
                stream_mode.reset();

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
    // ANTLRParser.g:194:1: grammarType : (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] ) ;
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
            // ANTLRParser.g:199:5: ( (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] ) )
            // ANTLRParser.g:199:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] )
            {
            // ANTLRParser.g:199:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] )
            int alt4=4;
            switch ( input.LA(1) ) {
            case LEXER:
                {
                alt4=1;
                }
                break;
            case PARSER:
                {
                alt4=2;
                }
                break;
            case TREE:
                {
                alt4=3;
                }
                break;
            case GRAMMAR:
                {
                alt4=4;
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
                    // ANTLRParser.g:199:9: t= LEXER g= GRAMMAR
                    {
                    t=(Token)match(input,LEXER,FOLLOW_LEXER_in_grammarType827); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType831); if (state.failed) return retval; 
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
                    // 199:28: -> GRAMMAR[$g, \"LEXER_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "LEXER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:201:6: t= PARSER g= GRAMMAR
                    {
                    t=(Token)match(input,PARSER,FOLLOW_PARSER_in_grammarType864); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType868); if (state.failed) return retval; 
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
                    // 201:25: -> GRAMMAR[$g, \"PARSER_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "PARSER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:204:6: t= TREE g= GRAMMAR
                    {
                    t=(Token)match(input,TREE,FOLLOW_TREE_in_grammarType895); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TREE.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType899); if (state.failed) return retval; 
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
                    // 204:25: -> GRAMMAR[$g, \"TREE_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "TREE_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // ANTLRParser.g:207:6: g= GRAMMAR
                    {
                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType926); if (state.failed) return retval; 
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
                    // 207:25: -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"]
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
    // ANTLRParser.g:214:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action );
    public final ANTLRParser.prequelConstruct_return prequelConstruct() throws RecognitionException {
        ANTLRParser.prequelConstruct_return retval = new ANTLRParser.prequelConstruct_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.optionsSpec_return optionsSpec11 = null;

        ANTLRParser.delegateGrammars_return delegateGrammars12 = null;

        ANTLRParser.tokensSpec_return tokensSpec13 = null;

        ANTLRParser.attrScope_return attrScope14 = null;

        ANTLRParser.action_return action15 = null;



        try {
            // ANTLRParser.g:215:2: ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action )
            int alt5=5;
            switch ( input.LA(1) ) {
            case OPTIONS:
                {
                alt5=1;
                }
                break;
            case IMPORT:
                {
                alt5=2;
                }
                break;
            case TOKENS:
                {
                alt5=3;
                }
                break;
            case SCOPE:
                {
                alt5=4;
                }
                break;
            case AT:
                {
                alt5=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ANTLRParser.g:216:4: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_prequelConstruct990);
                    optionsSpec11=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec11.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:220:7: delegateGrammars
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct1016);
                    delegateGrammars12=delegateGrammars();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, delegateGrammars12.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:227:7: tokensSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_tokensSpec_in_prequelConstruct1066);
                    tokensSpec13=tokensSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tokensSpec13.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:232:7: attrScope
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_attrScope_in_prequelConstruct1102);
                    attrScope14=attrScope();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, attrScope14.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:238:7: action
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_action_in_prequelConstruct1145);
                    action15=action();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, action15.getTree());

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
    // ANTLRParser.g:242:1: optionsSpec : OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ ) ;
    public final ANTLRParser.optionsSpec_return optionsSpec() throws RecognitionException {
        ANTLRParser.optionsSpec_return retval = new ANTLRParser.optionsSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token OPTIONS16=null;
        Token SEMI18=null;
        Token RBRACE19=null;
        ANTLRParser.option_return option17 = null;


        GrammarAST OPTIONS16_tree=null;
        GrammarAST SEMI18_tree=null;
        GrammarAST RBRACE19_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_OPTIONS=new RewriteRuleTokenStream(adaptor,"token OPTIONS");
        RewriteRuleSubtreeStream stream_option=new RewriteRuleSubtreeStream(adaptor,"rule option");
        try {
            // ANTLRParser.g:243:2: ( OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ ) )
            // ANTLRParser.g:243:4: OPTIONS ( option SEMI )* RBRACE
            {
            OPTIONS16=(Token)match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec1162); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPTIONS.add(OPTIONS16);

            // ANTLRParser.g:243:12: ( option SEMI )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==TEMPLATE||(LA6_0>=TOKEN_REF && LA6_0<=RULE_REF)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // ANTLRParser.g:243:13: option SEMI
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec1165);
            	    option17=option();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_option.add(option17.getTree());
            	    SEMI18=(Token)match(input,SEMI,FOLLOW_SEMI_in_optionsSpec1167); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEMI.add(SEMI18);


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            RBRACE19=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_optionsSpec1171); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE19);



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
            // 243:34: -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ )
            {
                // ANTLRParser.g:243:37: ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(OPTIONS, OPTIONS16, "OPTIONS"), root_1);

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
    // ANTLRParser.g:246:1: option : id ASSIGN optionValue ;
    public final ANTLRParser.option_return option() throws RecognitionException {
        ANTLRParser.option_return retval = new ANTLRParser.option_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN21=null;
        ANTLRParser.id_return id20 = null;

        ANTLRParser.optionValue_return optionValue22 = null;


        GrammarAST ASSIGN21_tree=null;

        try {
            // ANTLRParser.g:247:5: ( id ASSIGN optionValue )
            // ANTLRParser.g:247:9: id ASSIGN optionValue
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_option1208);
            id20=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id20.getTree());
            ASSIGN21=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_option1210); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ASSIGN21_tree = (GrammarAST)adaptor.create(ASSIGN21);
            root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN21_tree, root_0);
            }
            pushFollow(FOLLOW_optionValue_in_option1213);
            optionValue22=optionValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, optionValue22.getTree());

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
    // ANTLRParser.g:255:1: optionValue : ( qid | STRING_LITERAL | INT | STAR );
    public final ANTLRParser.optionValue_return optionValue() throws RecognitionException {
        ANTLRParser.optionValue_return retval = new ANTLRParser.optionValue_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token STRING_LITERAL24=null;
        Token INT25=null;
        Token STAR26=null;
        ANTLRParser.qid_return qid23 = null;


        GrammarAST STRING_LITERAL24_tree=null;
        GrammarAST INT25_tree=null;
        GrammarAST STAR26_tree=null;

        try {
            // ANTLRParser.g:256:5: ( qid | STRING_LITERAL | INT | STAR )
            int alt7=4;
            switch ( input.LA(1) ) {
            case TEMPLATE:
            case TOKEN_REF:
            case RULE_REF:
                {
                alt7=1;
                }
                break;
            case STRING_LITERAL:
                {
                alt7=2;
                }
                break;
            case INT:
                {
                alt7=3;
                }
                break;
            case STAR:
                {
                alt7=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // ANTLRParser.g:260:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_optionValue1263);
                    qid23=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid23.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:264:7: STRING_LITERAL
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STRING_LITERAL24=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_optionValue1287); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL24_tree = new TerminalAST(STRING_LITERAL24) ;
                    adaptor.addChild(root_0, STRING_LITERAL24_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:268:7: INT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    INT25=(Token)match(input,INT,FOLLOW_INT_in_optionValue1313); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT25_tree = (GrammarAST)adaptor.create(INT25);
                    adaptor.addChild(root_0, INT25_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:272:7: STAR
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STAR26=(Token)match(input,STAR,FOLLOW_STAR_in_optionValue1342); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STAR26_tree = (GrammarAST)adaptor.create(STAR26);
                    adaptor.addChild(root_0, STAR26_tree);
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
    // ANTLRParser.g:277:1: delegateGrammars : IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) ;
    public final ANTLRParser.delegateGrammars_return delegateGrammars() throws RecognitionException {
        ANTLRParser.delegateGrammars_return retval = new ANTLRParser.delegateGrammars_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token IMPORT27=null;
        Token COMMA29=null;
        Token SEMI31=null;
        ANTLRParser.delegateGrammar_return delegateGrammar28 = null;

        ANTLRParser.delegateGrammar_return delegateGrammar30 = null;


        GrammarAST IMPORT27_tree=null;
        GrammarAST COMMA29_tree=null;
        GrammarAST SEMI31_tree=null;
        RewriteRuleTokenStream stream_IMPORT=new RewriteRuleTokenStream(adaptor,"token IMPORT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_delegateGrammar=new RewriteRuleSubtreeStream(adaptor,"rule delegateGrammar");
        try {
            // ANTLRParser.g:278:2: ( IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) )
            // ANTLRParser.g:278:4: IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI
            {
            IMPORT27=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars1358); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IMPORT.add(IMPORT27);

            pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1360);
            delegateGrammar28=delegateGrammar();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar28.getTree());
            // ANTLRParser.g:278:27: ( COMMA delegateGrammar )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==COMMA) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ANTLRParser.g:278:28: COMMA delegateGrammar
            	    {
            	    COMMA29=(Token)match(input,COMMA,FOLLOW_COMMA_in_delegateGrammars1363); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA29);

            	    pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1365);
            	    delegateGrammar30=delegateGrammar();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar30.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            SEMI31=(Token)match(input,SEMI,FOLLOW_SEMI_in_delegateGrammars1369); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI31);



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
            // 278:57: -> ^( IMPORT ( delegateGrammar )+ )
            {
                // ANTLRParser.g:278:60: ^( IMPORT ( delegateGrammar )+ )
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
    // ANTLRParser.g:283:1: delegateGrammar : ( id ASSIGN id | id );
    public final ANTLRParser.delegateGrammar_return delegateGrammar() throws RecognitionException {
        ANTLRParser.delegateGrammar_return retval = new ANTLRParser.delegateGrammar_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN33=null;
        ANTLRParser.id_return id32 = null;

        ANTLRParser.id_return id34 = null;

        ANTLRParser.id_return id35 = null;


        GrammarAST ASSIGN33_tree=null;

        try {
            // ANTLRParser.g:284:5: ( id ASSIGN id | id )
            int alt9=2;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==ASSIGN) ) {
                    alt9=1;
                }
                else if ( ((LA9_1>=COMMA && LA9_1<=SEMI)) ) {
                    alt9=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 1, input);

                    throw nvae;
                }
                }
                break;
            case TOKEN_REF:
                {
                int LA9_2 = input.LA(2);

                if ( (LA9_2==ASSIGN) ) {
                    alt9=1;
                }
                else if ( ((LA9_2>=COMMA && LA9_2<=SEMI)) ) {
                    alt9=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 2, input);

                    throw nvae;
                }
                }
                break;
            case TEMPLATE:
                {
                int LA9_3 = input.LA(2);

                if ( (LA9_3==ASSIGN) ) {
                    alt9=1;
                }
                else if ( ((LA9_3>=COMMA && LA9_3<=SEMI)) ) {
                    alt9=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 9, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // ANTLRParser.g:284:9: id ASSIGN id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1396);
                    id32=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id32.getTree());
                    ASSIGN33=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar1398); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN33_tree = (GrammarAST)adaptor.create(ASSIGN33);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN33_tree, root_0);
                    }
                    pushFollow(FOLLOW_id_in_delegateGrammar1401);
                    id34=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id34.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:285:9: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1411);
                    id35=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id35.getTree());

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
    // ANTLRParser.g:288:1: tokensSpec : TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) ;
    public final ANTLRParser.tokensSpec_return tokensSpec() throws RecognitionException {
        ANTLRParser.tokensSpec_return retval = new ANTLRParser.tokensSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKENS36=null;
        Token RBRACE38=null;
        ANTLRParser.tokenSpec_return tokenSpec37 = null;


        GrammarAST TOKENS36_tree=null;
        GrammarAST RBRACE38_tree=null;
        RewriteRuleTokenStream stream_TOKENS=new RewriteRuleTokenStream(adaptor,"token TOKENS");
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleSubtreeStream stream_tokenSpec=new RewriteRuleSubtreeStream(adaptor,"rule tokenSpec");
        try {
            // ANTLRParser.g:295:2: ( TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) )
            // ANTLRParser.g:295:4: TOKENS ( tokenSpec )+ RBRACE
            {
            TOKENS36=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec1427); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TOKENS.add(TOKENS36);

            // ANTLRParser.g:295:11: ( tokenSpec )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==TEMPLATE||(LA10_0>=TOKEN_REF && LA10_0<=RULE_REF)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ANTLRParser.g:295:11: tokenSpec
            	    {
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec1429);
            	    tokenSpec37=tokenSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_tokenSpec.add(tokenSpec37.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            RBRACE38=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_tokensSpec1432); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE38);



            // AST REWRITE
            // elements: TOKENS, tokenSpec
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 295:29: -> ^( TOKENS ( tokenSpec )+ )
            {
                // ANTLRParser.g:295:32: ^( TOKENS ( tokenSpec )+ )
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
    // ANTLRParser.g:298:1: tokenSpec : ( id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI | RULE_REF );
    public final ANTLRParser.tokenSpec_return tokenSpec() throws RecognitionException {
        ANTLRParser.tokenSpec_return retval = new ANTLRParser.tokenSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN40=null;
        Token STRING_LITERAL41=null;
        Token SEMI42=null;
        Token RULE_REF43=null;
        ANTLRParser.id_return id39 = null;


        GrammarAST ASSIGN40_tree=null;
        GrammarAST STRING_LITERAL41_tree=null;
        GrammarAST SEMI42_tree=null;
        GrammarAST RULE_REF43_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:299:2: ( id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI | RULE_REF )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==RULE_REF) ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1==SEMI||LA12_1==ASSIGN) ) {
                    alt12=1;
                }
                else if ( (LA12_1==TEMPLATE||(LA12_1>=RBRACE && LA12_1<=RULE_REF)) ) {
                    alt12=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA12_0==TEMPLATE||LA12_0==TOKEN_REF) ) {
                alt12=1;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // ANTLRParser.g:299:4: id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI
                    {
                    pushFollow(FOLLOW_id_in_tokenSpec1452);
                    id39=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id39.getTree());
                    // ANTLRParser.g:300:3: ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id )
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==ASSIGN) ) {
                        alt11=1;
                    }
                    else if ( (LA11_0==SEMI) ) {
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
                            // ANTLRParser.g:300:5: ASSIGN STRING_LITERAL
                            {
                            ASSIGN40=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec1458); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN40);

                            STRING_LITERAL41=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec1460); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL41);



                            // AST REWRITE
                            // elements: id, STRING_LITERAL, ASSIGN
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 300:27: -> ^( ASSIGN id STRING_LITERAL )
                            {
                                // ANTLRParser.g:300:30: ^( ASSIGN id STRING_LITERAL )
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
                            // ANTLRParser.g:301:11: 
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
                            // 301:11: -> id
                            {
                                adaptor.addChild(root_0, stream_id.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }

                    SEMI42=(Token)match(input,SEMI,FOLLOW_SEMI_in_tokenSpec1495); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI42);


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:304:4: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF43=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_tokenSpec1500); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF43_tree = (GrammarAST)adaptor.create(RULE_REF43);
                    adaptor.addChild(root_0, RULE_REF43_tree);
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
    // ANTLRParser.g:310:1: attrScope : SCOPE id ACTION -> ^( SCOPE id ACTION ) ;
    public final ANTLRParser.attrScope_return attrScope() throws RecognitionException {
        ANTLRParser.attrScope_return retval = new ANTLRParser.attrScope_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token SCOPE44=null;
        Token ACTION46=null;
        ANTLRParser.id_return id45 = null;


        GrammarAST SCOPE44_tree=null;
        GrammarAST ACTION46_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:311:2: ( SCOPE id ACTION -> ^( SCOPE id ACTION ) )
            // ANTLRParser.g:311:4: SCOPE id ACTION
            {
            SCOPE44=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_attrScope1515); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE44);

            pushFollow(FOLLOW_id_in_attrScope1517);
            id45=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id45.getTree());
            ACTION46=(Token)match(input,ACTION,FOLLOW_ACTION_in_attrScope1519); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION46);



            // AST REWRITE
            // elements: id, ACTION, SCOPE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 311:20: -> ^( SCOPE id ACTION )
            {
                // ANTLRParser.g:311:23: ^( SCOPE id ACTION )
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
    // ANTLRParser.g:317:1: action : AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) ;
    public final ANTLRParser.action_return action() throws RecognitionException {
        ANTLRParser.action_return retval = new ANTLRParser.action_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token AT47=null;
        Token COLONCOLON49=null;
        Token ACTION51=null;
        ANTLRParser.actionScopeName_return actionScopeName48 = null;

        ANTLRParser.id_return id50 = null;


        GrammarAST AT47_tree=null;
        GrammarAST COLONCOLON49_tree=null;
        GrammarAST ACTION51_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_COLONCOLON=new RewriteRuleTokenStream(adaptor,"token COLONCOLON");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_actionScopeName=new RewriteRuleSubtreeStream(adaptor,"rule actionScopeName");
        try {
            // ANTLRParser.g:319:2: ( AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) )
            // ANTLRParser.g:319:4: AT ( actionScopeName COLONCOLON )? id ACTION
            {
            AT47=(Token)match(input,AT,FOLLOW_AT_in_action1548); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT47);

            // ANTLRParser.g:319:7: ( actionScopeName COLONCOLON )?
            int alt13=2;
            switch ( input.LA(1) ) {
                case RULE_REF:
                    {
                    int LA13_1 = input.LA(2);

                    if ( (LA13_1==COLONCOLON) ) {
                        alt13=1;
                    }
                    }
                    break;
                case TOKEN_REF:
                    {
                    int LA13_2 = input.LA(2);

                    if ( (LA13_2==COLONCOLON) ) {
                        alt13=1;
                    }
                    }
                    break;
                case TEMPLATE:
                    {
                    int LA13_3 = input.LA(2);

                    if ( (LA13_3==COLONCOLON) ) {
                        alt13=1;
                    }
                    }
                    break;
                case LEXER:
                case PARSER:
                    {
                    alt13=1;
                    }
                    break;
            }

            switch (alt13) {
                case 1 :
                    // ANTLRParser.g:319:8: actionScopeName COLONCOLON
                    {
                    pushFollow(FOLLOW_actionScopeName_in_action1551);
                    actionScopeName48=actionScopeName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_actionScopeName.add(actionScopeName48.getTree());
                    COLONCOLON49=(Token)match(input,COLONCOLON,FOLLOW_COLONCOLON_in_action1553); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLONCOLON.add(COLONCOLON49);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_action1557);
            id50=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id50.getTree());
            ACTION51=(Token)match(input,ACTION,FOLLOW_ACTION_in_action1559); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION51);



            // AST REWRITE
            // elements: ACTION, actionScopeName, AT, id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 319:47: -> ^( AT ( actionScopeName )? id ACTION )
            {
                // ANTLRParser.g:319:50: ^( AT ( actionScopeName )? id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                // ANTLRParser.g:319:55: ( actionScopeName )?
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
    // ANTLRParser.g:322:1: actionScopeName : ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] );
    public final ANTLRParser.actionScopeName_return actionScopeName() throws RecognitionException {
        ANTLRParser.actionScopeName_return retval = new ANTLRParser.actionScopeName_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LEXER53=null;
        Token PARSER54=null;
        ANTLRParser.id_return id52 = null;


        GrammarAST LEXER53_tree=null;
        GrammarAST PARSER54_tree=null;
        RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
        RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");

        try {
            // ANTLRParser.g:326:2: ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] )
            int alt14=3;
            switch ( input.LA(1) ) {
            case TEMPLATE:
            case TOKEN_REF:
            case RULE_REF:
                {
                alt14=1;
                }
                break;
            case LEXER:
                {
                alt14=2;
                }
                break;
            case PARSER:
                {
                alt14=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // ANTLRParser.g:326:4: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_actionScopeName1590);
                    id52=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id52.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:327:4: LEXER
                    {
                    LEXER53=(Token)match(input,LEXER,FOLLOW_LEXER_in_actionScopeName1595); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(LEXER53);



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
                    // 327:10: -> ID[$LEXER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, LEXER53));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:328:9: PARSER
                    {
                    PARSER54=(Token)match(input,PARSER,FOLLOW_PARSER_in_actionScopeName1610); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(PARSER54);



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
                    // 328:16: -> ID[$PARSER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, PARSER54));

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

    public static class mode_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mode"
    // ANTLRParser.g:331:1: mode : MODE id SEMI sync ( rule sync )+ -> ^( MODE id ( rule )+ ) ;
    public final ANTLRParser.mode_return mode() throws RecognitionException {
        ANTLRParser.mode_return retval = new ANTLRParser.mode_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token MODE55=null;
        Token SEMI57=null;
        ANTLRParser.id_return id56 = null;

        ANTLRParser.sync_return sync58 = null;

        ANTLRParser.rule_return rule59 = null;

        ANTLRParser.sync_return sync60 = null;


        GrammarAST MODE55_tree=null;
        GrammarAST SEMI57_tree=null;
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_MODE=new RewriteRuleTokenStream(adaptor,"token MODE");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        try {
            // ANTLRParser.g:331:5: ( MODE id SEMI sync ( rule sync )+ -> ^( MODE id ( rule )+ ) )
            // ANTLRParser.g:331:7: MODE id SEMI sync ( rule sync )+
            {
            MODE55=(Token)match(input,MODE,FOLLOW_MODE_in_mode1624); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_MODE.add(MODE55);

            pushFollow(FOLLOW_id_in_mode1626);
            id56=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id56.getTree());
            SEMI57=(Token)match(input,SEMI,FOLLOW_SEMI_in_mode1628); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI57);

            pushFollow(FOLLOW_sync_in_mode1630);
            sync58=sync();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sync.add(sync58.getTree());
            // ANTLRParser.g:331:25: ( rule sync )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==DOC_COMMENT||LA15_0==FRAGMENT||(LA15_0>=PROTECTED && LA15_0<=PRIVATE)||LA15_0==TEMPLATE||(LA15_0>=TOKEN_REF && LA15_0<=RULE_REF)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ANTLRParser.g:331:26: rule sync
            	    {
            	    pushFollow(FOLLOW_rule_in_mode1633);
            	    rule59=rule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule.add(rule59.getTree());
            	    pushFollow(FOLLOW_sync_in_mode1635);
            	    sync60=sync();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sync.add(sync60.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);



            // AST REWRITE
            // elements: MODE, id, rule
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 331:39: -> ^( MODE id ( rule )+ )
            {
                // ANTLRParser.g:331:42: ^( MODE id ( rule )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_MODE.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                if ( !(stream_rule.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
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
    // $ANTLR end "mode"

    public static class rules_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rules"
    // ANTLRParser.g:333:1: rules : sync ( rule sync )* -> ^( RULES ( rule )* ) ;
    public final ANTLRParser.rules_return rules() throws RecognitionException {
        ANTLRParser.rules_return retval = new ANTLRParser.rules_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.sync_return sync61 = null;

        ANTLRParser.rule_return rule62 = null;

        ANTLRParser.sync_return sync63 = null;


        RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        try {
            // ANTLRParser.g:334:5: ( sync ( rule sync )* -> ^( RULES ( rule )* ) )
            // ANTLRParser.g:334:7: sync ( rule sync )*
            {
            pushFollow(FOLLOW_sync_in_rules1662);
            sync61=sync();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sync.add(sync61.getTree());
            // ANTLRParser.g:334:12: ( rule sync )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==DOC_COMMENT||LA16_0==FRAGMENT||(LA16_0>=PROTECTED && LA16_0<=PRIVATE)||LA16_0==TEMPLATE||(LA16_0>=TOKEN_REF && LA16_0<=RULE_REF)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // ANTLRParser.g:334:13: rule sync
            	    {
            	    pushFollow(FOLLOW_rule_in_rules1665);
            	    rule62=rule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule.add(rule62.getTree());
            	    pushFollow(FOLLOW_sync_in_rules1667);
            	    sync63=sync();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sync.add(sync63.getTree());

            	    }
            	    break;

            	default :
            	    break loop16;
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
            // 338:7: -> ^( RULES ( rule )* )
            {
                // ANTLRParser.g:338:9: ^( RULES ( rule )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULES, "RULES"), root_1);

                // ANTLRParser.g:338:17: ( rule )*
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
    // ANTLRParser.g:341:1: sync : ;
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
            // ANTLRParser.g:350:3: ()
            // ANTLRParser.g:351:2: 
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
    // ANTLRParser.g:362:1: rule : ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? rulePrequels COLON ruleBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* ) ;
    public final ANTLRParser.rule_return rule() throws RecognitionException {
        ANTLRParser.rule_return retval = new ANTLRParser.rule_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOC_COMMENT64=null;
        Token ARG_ACTION67=null;
        Token COLON70=null;
        Token SEMI72=null;
        ANTLRParser.ruleModifiers_return ruleModifiers65 = null;

        ANTLRParser.id_return id66 = null;

        ANTLRParser.ruleReturns_return ruleReturns68 = null;

        ANTLRParser.rulePrequels_return rulePrequels69 = null;

        ANTLRParser.ruleBlock_return ruleBlock71 = null;

        ANTLRParser.exceptionGroup_return exceptionGroup73 = null;


        GrammarAST DOC_COMMENT64_tree=null;
        GrammarAST ARG_ACTION67_tree=null;
        GrammarAST COLON70_tree=null;
        GrammarAST SEMI72_tree=null;
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
            // ANTLRParser.g:365:5: ( ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? rulePrequels COLON ruleBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* ) )
            // ANTLRParser.g:366:7: ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? rulePrequels COLON ruleBlock SEMI exceptionGroup
            {
            // ANTLRParser.g:366:7: ( DOC_COMMENT )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==DOC_COMMENT) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ANTLRParser.g:366:7: DOC_COMMENT
                    {
                    DOC_COMMENT64=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_rule1769); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOC_COMMENT.add(DOC_COMMENT64);


                    }
                    break;

            }

            // ANTLRParser.g:372:7: ( ruleModifiers )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==FRAGMENT||(LA18_0>=PROTECTED && LA18_0<=PRIVATE)) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ANTLRParser.g:372:7: ruleModifiers
                    {
                    pushFollow(FOLLOW_ruleModifiers_in_rule1813);
                    ruleModifiers65=ruleModifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleModifiers.add(ruleModifiers65.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_rule1836);
            id66=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id66.getTree());
            // ANTLRParser.g:386:4: ( ARG_ACTION )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==ARG_ACTION) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // ANTLRParser.g:386:4: ARG_ACTION
                    {
                    ARG_ACTION67=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule1869); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION67);


                    }
                    break;

            }

            // ANTLRParser.g:388:4: ( ruleReturns )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==RETURNS) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // ANTLRParser.g:388:4: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_rule1879);
                    ruleReturns68=ruleReturns();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleReturns.add(ruleReturns68.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_rulePrequels_in_rule1917);
            rulePrequels69=rulePrequels();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rulePrequels.add(rulePrequels69.getTree());
            COLON70=(Token)match(input,COLON,FOLLOW_COLON_in_rule1932); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON70);

            pushFollow(FOLLOW_ruleBlock_in_rule1961);
            ruleBlock71=ruleBlock();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ruleBlock.add(ruleBlock71.getTree());
            SEMI72=(Token)match(input,SEMI,FOLLOW_SEMI_in_rule1976); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI72);

            pushFollow(FOLLOW_exceptionGroup_in_rule1985);
            exceptionGroup73=exceptionGroup();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exceptionGroup.add(exceptionGroup73.getTree());


            // AST REWRITE
            // elements: ruleModifiers, ruleReturns, id, ruleBlock, ARG_ACTION, exceptionGroup, DOC_COMMENT, rulePrequels
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 415:7: -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* )
            {
                // ANTLRParser.g:415:10: ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequels )? ruleBlock ( exceptionGroup )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new RuleAST(RULE), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:415:30: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:415:43: ( ruleModifiers )?
                if ( stream_ruleModifiers.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleModifiers.nextTree());

                }
                stream_ruleModifiers.reset();
                // ANTLRParser.g:415:58: ( ARG_ACTION )?
                if ( stream_ARG_ACTION.hasNext() ) {
                    adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                }
                stream_ARG_ACTION.reset();
                // ANTLRParser.g:416:9: ( ruleReturns )?
                if ( stream_ruleReturns.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleReturns.nextTree());

                }
                stream_ruleReturns.reset();
                // ANTLRParser.g:416:22: ( rulePrequels )?
                if ( stream_rulePrequels.hasNext() ) {
                    adaptor.addChild(root_1, stream_rulePrequels.nextTree());

                }
                stream_rulePrequels.reset();
                adaptor.addChild(root_1, stream_ruleBlock.nextTree());
                // ANTLRParser.g:416:46: ( exceptionGroup )*
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
    // ANTLRParser.g:426:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
    public final ANTLRParser.exceptionGroup_return exceptionGroup() throws RecognitionException {
        ANTLRParser.exceptionGroup_return retval = new ANTLRParser.exceptionGroup_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.exceptionHandler_return exceptionHandler74 = null;

        ANTLRParser.finallyClause_return finallyClause75 = null;



        try {
            // ANTLRParser.g:427:5: ( ( exceptionHandler )* ( finallyClause )? )
            // ANTLRParser.g:427:7: ( exceptionHandler )* ( finallyClause )?
            {
            root_0 = (GrammarAST)adaptor.nil();

            // ANTLRParser.g:427:7: ( exceptionHandler )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==CATCH) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // ANTLRParser.g:427:7: exceptionHandler
            	    {
            	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup2077);
            	    exceptionHandler74=exceptionHandler();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exceptionHandler74.getTree());

            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);

            // ANTLRParser.g:427:25: ( finallyClause )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==FINALLY) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // ANTLRParser.g:427:25: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup2080);
                    finallyClause75=finallyClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, finallyClause75.getTree());

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
    // ANTLRParser.g:432:1: exceptionHandler : CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) ;
    public final ANTLRParser.exceptionHandler_return exceptionHandler() throws RecognitionException {
        ANTLRParser.exceptionHandler_return retval = new ANTLRParser.exceptionHandler_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token CATCH76=null;
        Token ARG_ACTION77=null;
        Token ACTION78=null;

        GrammarAST CATCH76_tree=null;
        GrammarAST ARG_ACTION77_tree=null;
        GrammarAST ACTION78_tree=null;
        RewriteRuleTokenStream stream_CATCH=new RewriteRuleTokenStream(adaptor,"token CATCH");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

        try {
            // ANTLRParser.g:433:2: ( CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) )
            // ANTLRParser.g:433:4: CATCH ARG_ACTION ACTION
            {
            CATCH76=(Token)match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler2097); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CATCH.add(CATCH76);

            ARG_ACTION77=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler2099); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION77);

            ACTION78=(Token)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler2101); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION78);



            // AST REWRITE
            // elements: ACTION, ARG_ACTION, CATCH
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 433:28: -> ^( CATCH ARG_ACTION ACTION )
            {
                // ANTLRParser.g:433:31: ^( CATCH ARG_ACTION ACTION )
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
    // ANTLRParser.g:438:1: finallyClause : FINALLY ACTION -> ^( FINALLY ACTION ) ;
    public final ANTLRParser.finallyClause_return finallyClause() throws RecognitionException {
        ANTLRParser.finallyClause_return retval = new ANTLRParser.finallyClause_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token FINALLY79=null;
        Token ACTION80=null;

        GrammarAST FINALLY79_tree=null;
        GrammarAST ACTION80_tree=null;
        RewriteRuleTokenStream stream_FINALLY=new RewriteRuleTokenStream(adaptor,"token FINALLY");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");

        try {
            // ANTLRParser.g:439:2: ( FINALLY ACTION -> ^( FINALLY ACTION ) )
            // ANTLRParser.g:439:4: FINALLY ACTION
            {
            FINALLY79=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause2127); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FINALLY.add(FINALLY79);

            ACTION80=(Token)match(input,ACTION,FOLLOW_ACTION_in_finallyClause2129); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION80);



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
            // 439:19: -> ^( FINALLY ACTION )
            {
                // ANTLRParser.g:439:22: ^( FINALLY ACTION )
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
    // ANTLRParser.g:442:1: rulePrequels : sync ( rulePrequel sync )* -> ( rulePrequel )* ;
    public final ANTLRParser.rulePrequels_return rulePrequels() throws RecognitionException {
        ANTLRParser.rulePrequels_return retval = new ANTLRParser.rulePrequels_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.sync_return sync81 = null;

        ANTLRParser.rulePrequel_return rulePrequel82 = null;

        ANTLRParser.sync_return sync83 = null;


        RewriteRuleSubtreeStream stream_rulePrequel=new RewriteRuleSubtreeStream(adaptor,"rule rulePrequel");
        RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
         paraphrases.push("matching rule preamble"); 
        try {
            // ANTLRParser.g:445:2: ( sync ( rulePrequel sync )* -> ( rulePrequel )* )
            // ANTLRParser.g:445:4: sync ( rulePrequel sync )*
            {
            pushFollow(FOLLOW_sync_in_rulePrequels2163);
            sync81=sync();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sync.add(sync81.getTree());
            // ANTLRParser.g:445:9: ( rulePrequel sync )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==OPTIONS||LA23_0==SCOPE||LA23_0==THROWS||LA23_0==AT) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ANTLRParser.g:445:10: rulePrequel sync
            	    {
            	    pushFollow(FOLLOW_rulePrequel_in_rulePrequels2166);
            	    rulePrequel82=rulePrequel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rulePrequel.add(rulePrequel82.getTree());
            	    pushFollow(FOLLOW_sync_in_rulePrequels2168);
            	    sync83=sync();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sync.add(sync83.getTree());

            	    }
            	    break;

            	default :
            	    break loop23;
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
            // 445:29: -> ( rulePrequel )*
            {
                // ANTLRParser.g:445:32: ( rulePrequel )*
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
    // ANTLRParser.g:451:1: rulePrequel : ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction );
    public final ANTLRParser.rulePrequel_return rulePrequel() throws RecognitionException {
        ANTLRParser.rulePrequel_return retval = new ANTLRParser.rulePrequel_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.throwsSpec_return throwsSpec84 = null;

        ANTLRParser.ruleScopeSpec_return ruleScopeSpec85 = null;

        ANTLRParser.optionsSpec_return optionsSpec86 = null;

        ANTLRParser.ruleAction_return ruleAction87 = null;



        try {
            // ANTLRParser.g:452:5: ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction )
            int alt24=4;
            switch ( input.LA(1) ) {
            case THROWS:
                {
                alt24=1;
                }
                break;
            case SCOPE:
                {
                alt24=2;
                }
                break;
            case OPTIONS:
                {
                alt24=3;
                }
                break;
            case AT:
                {
                alt24=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // ANTLRParser.g:452:7: throwsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_throwsSpec_in_rulePrequel2194);
                    throwsSpec84=throwsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, throwsSpec84.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:453:7: ruleScopeSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleScopeSpec_in_rulePrequel2202);
                    ruleScopeSpec85=ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleScopeSpec85.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:454:7: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_rulePrequel2210);
                    optionsSpec86=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec86.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:455:7: ruleAction
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleAction_in_rulePrequel2218);
                    ruleAction87=ruleAction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleAction87.getTree());

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
    // ANTLRParser.g:464:1: ruleReturns : RETURNS ARG_ACTION ;
    public final ANTLRParser.ruleReturns_return ruleReturns() throws RecognitionException {
        ANTLRParser.ruleReturns_return retval = new ANTLRParser.ruleReturns_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RETURNS88=null;
        Token ARG_ACTION89=null;

        GrammarAST RETURNS88_tree=null;
        GrammarAST ARG_ACTION89_tree=null;

        try {
            // ANTLRParser.g:465:2: ( RETURNS ARG_ACTION )
            // ANTLRParser.g:465:4: RETURNS ARG_ACTION
            {
            root_0 = (GrammarAST)adaptor.nil();

            RETURNS88=(Token)match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns2238); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RETURNS88_tree = (GrammarAST)adaptor.create(RETURNS88);
            root_0 = (GrammarAST)adaptor.becomeRoot(RETURNS88_tree, root_0);
            }
            ARG_ACTION89=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns2241); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ARG_ACTION89_tree = (GrammarAST)adaptor.create(ARG_ACTION89);
            adaptor.addChild(root_0, ARG_ACTION89_tree);
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
    // ANTLRParser.g:479:1: throwsSpec : THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) ;
    public final ANTLRParser.throwsSpec_return throwsSpec() throws RecognitionException {
        ANTLRParser.throwsSpec_return retval = new ANTLRParser.throwsSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token THROWS90=null;
        Token COMMA92=null;
        ANTLRParser.qid_return qid91 = null;

        ANTLRParser.qid_return qid93 = null;


        GrammarAST THROWS90_tree=null;
        GrammarAST COMMA92_tree=null;
        RewriteRuleTokenStream stream_THROWS=new RewriteRuleTokenStream(adaptor,"token THROWS");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // ANTLRParser.g:480:5: ( THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) )
            // ANTLRParser.g:480:7: THROWS qid ( COMMA qid )*
            {
            THROWS90=(Token)match(input,THROWS,FOLLOW_THROWS_in_throwsSpec2266); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_THROWS.add(THROWS90);

            pushFollow(FOLLOW_qid_in_throwsSpec2268);
            qid91=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid91.getTree());
            // ANTLRParser.g:480:18: ( COMMA qid )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==COMMA) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ANTLRParser.g:480:19: COMMA qid
            	    {
            	    COMMA92=(Token)match(input,COMMA,FOLLOW_COMMA_in_throwsSpec2271); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA92);

            	    pushFollow(FOLLOW_qid_in_throwsSpec2273);
            	    qid93=qid();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_qid.add(qid93.getTree());

            	    }
            	    break;

            	default :
            	    break loop25;
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
            // 480:31: -> ^( THROWS ( qid )+ )
            {
                // ANTLRParser.g:480:34: ^( THROWS ( qid )+ )
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
    // ANTLRParser.g:487:1: ruleScopeSpec : ( SCOPE ACTION -> ^( SCOPE ACTION ) | SCOPE id ( COMMA id )* SEMI -> ^( SCOPE ( id )+ ) );
    public final ANTLRParser.ruleScopeSpec_return ruleScopeSpec() throws RecognitionException {
        ANTLRParser.ruleScopeSpec_return retval = new ANTLRParser.ruleScopeSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token SCOPE94=null;
        Token ACTION95=null;
        Token SCOPE96=null;
        Token COMMA98=null;
        Token SEMI100=null;
        ANTLRParser.id_return id97 = null;

        ANTLRParser.id_return id99 = null;


        GrammarAST SCOPE94_tree=null;
        GrammarAST ACTION95_tree=null;
        GrammarAST SCOPE96_tree=null;
        GrammarAST COMMA98_tree=null;
        GrammarAST SEMI100_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:488:2: ( SCOPE ACTION -> ^( SCOPE ACTION ) | SCOPE id ( COMMA id )* SEMI -> ^( SCOPE ( id )+ ) )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==SCOPE) ) {
                int LA27_1 = input.LA(2);

                if ( (LA27_1==ACTION) ) {
                    alt27=1;
                }
                else if ( (LA27_1==TEMPLATE||(LA27_1>=TOKEN_REF && LA27_1<=RULE_REF)) ) {
                    alt27=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 27, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // ANTLRParser.g:488:4: SCOPE ACTION
                    {
                    SCOPE94=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2304); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE94);

                    ACTION95=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec2306); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ACTION.add(ACTION95);



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
                    // 488:17: -> ^( SCOPE ACTION )
                    {
                        // ANTLRParser.g:488:20: ^( SCOPE ACTION )
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
                    // ANTLRParser.g:489:4: SCOPE id ( COMMA id )* SEMI
                    {
                    SCOPE96=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2319); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE96);

                    pushFollow(FOLLOW_id_in_ruleScopeSpec2321);
                    id97=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id97.getTree());
                    // ANTLRParser.g:489:13: ( COMMA id )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);

                        if ( (LA26_0==COMMA) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // ANTLRParser.g:489:14: COMMA id
                    	    {
                    	    COMMA98=(Token)match(input,COMMA,FOLLOW_COMMA_in_ruleScopeSpec2324); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA98);

                    	    pushFollow(FOLLOW_id_in_ruleScopeSpec2326);
                    	    id99=id();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_id.add(id99.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop26;
                        }
                    } while (true);

                    SEMI100=(Token)match(input,SEMI,FOLLOW_SEMI_in_ruleScopeSpec2330); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI100);



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
                    // 489:30: -> ^( SCOPE ( id )+ )
                    {
                        // ANTLRParser.g:489:33: ^( SCOPE ( id )+ )
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
    // ANTLRParser.g:500:1: ruleAction : AT id ACTION -> ^( AT id ACTION ) ;
    public final ANTLRParser.ruleAction_return ruleAction() throws RecognitionException {
        ANTLRParser.ruleAction_return retval = new ANTLRParser.ruleAction_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token AT101=null;
        Token ACTION103=null;
        ANTLRParser.id_return id102 = null;


        GrammarAST AT101_tree=null;
        GrammarAST ACTION103_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:502:2: ( AT id ACTION -> ^( AT id ACTION ) )
            // ANTLRParser.g:502:4: AT id ACTION
            {
            AT101=(Token)match(input,AT,FOLLOW_AT_in_ruleAction2360); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT101);

            pushFollow(FOLLOW_id_in_ruleAction2362);
            id102=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id102.getTree());
            ACTION103=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleAction2364); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION103);



            // AST REWRITE
            // elements: ACTION, id, AT
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 502:17: -> ^( AT id ACTION )
            {
                // ANTLRParser.g:502:20: ^( AT id ACTION )
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
    // ANTLRParser.g:510:1: ruleModifiers : ( ruleModifier )+ -> ^( RULEMODIFIERS ( ruleModifier )+ ) ;
    public final ANTLRParser.ruleModifiers_return ruleModifiers() throws RecognitionException {
        ANTLRParser.ruleModifiers_return retval = new ANTLRParser.ruleModifiers_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.ruleModifier_return ruleModifier104 = null;


        RewriteRuleSubtreeStream stream_ruleModifier=new RewriteRuleSubtreeStream(adaptor,"rule ruleModifier");
        try {
            // ANTLRParser.g:511:5: ( ( ruleModifier )+ -> ^( RULEMODIFIERS ( ruleModifier )+ ) )
            // ANTLRParser.g:511:7: ( ruleModifier )+
            {
            // ANTLRParser.g:511:7: ( ruleModifier )+
            int cnt28=0;
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==FRAGMENT||(LA28_0>=PROTECTED && LA28_0<=PRIVATE)) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ANTLRParser.g:511:7: ruleModifier
            	    {
            	    pushFollow(FOLLOW_ruleModifier_in_ruleModifiers2405);
            	    ruleModifier104=ruleModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ruleModifier.add(ruleModifier104.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt28 >= 1 ) break loop28;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(28, input);
                        throw eee;
                }
                cnt28++;
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
            // 511:21: -> ^( RULEMODIFIERS ( ruleModifier )+ )
            {
                // ANTLRParser.g:511:24: ^( RULEMODIFIERS ( ruleModifier )+ )
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
    // ANTLRParser.g:520:1: ruleModifier : ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT );
    public final ANTLRParser.ruleModifier_return ruleModifier() throws RecognitionException {
        ANTLRParser.ruleModifier_return retval = new ANTLRParser.ruleModifier_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set105=null;

        GrammarAST set105_tree=null;

        try {
            // ANTLRParser.g:521:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set105=(Token)input.LT(1);
            if ( input.LA(1)==FRAGMENT||(input.LA(1)>=PROTECTED && input.LA(1)<=PRIVATE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set105));
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
    // ANTLRParser.g:527:1: altList : alternative ( OR alternative )* -> ( alternative )+ ;
    public final ANTLRParser.altList_return altList() throws RecognitionException {
        ANTLRParser.altList_return retval = new ANTLRParser.altList_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token OR107=null;
        ANTLRParser.alternative_return alternative106 = null;

        ANTLRParser.alternative_return alternative108 = null;


        GrammarAST OR107_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // ANTLRParser.g:528:5: ( alternative ( OR alternative )* -> ( alternative )+ )
            // ANTLRParser.g:528:7: alternative ( OR alternative )*
            {
            pushFollow(FOLLOW_alternative_in_altList2481);
            alternative106=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_alternative.add(alternative106.getTree());
            // ANTLRParser.g:528:19: ( OR alternative )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==OR) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // ANTLRParser.g:528:20: OR alternative
            	    {
            	    OR107=(Token)match(input,OR,FOLLOW_OR_in_altList2484); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_OR.add(OR107);

            	    pushFollow(FOLLOW_alternative_in_altList2486);
            	    alternative108=alternative();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_alternative.add(alternative108.getTree());

            	    }
            	    break;

            	default :
            	    break loop29;
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
            // 528:37: -> ( alternative )+
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
    // ANTLRParser.g:537:1: ruleBlock : altList -> ^( BLOCK[colon,\"BLOCK\"] altList ) ;
    public final ANTLRParser.ruleBlock_return ruleBlock() throws RecognitionException {
        ANTLRParser.ruleBlock_return retval = new ANTLRParser.ruleBlock_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.altList_return altList109 = null;


        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        Token colon = input.LT(-1);
        try {
            // ANTLRParser.g:539:5: ( altList -> ^( BLOCK[colon,\"BLOCK\"] altList ) )
            // ANTLRParser.g:539:7: altList
            {
            pushFollow(FOLLOW_altList_in_ruleBlock2521);
            altList109=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList109.getTree());


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
            // 539:15: -> ^( BLOCK[colon,\"BLOCK\"] altList )
            {
                // ANTLRParser.g:539:18: ^( BLOCK[colon,\"BLOCK\"] altList )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, colon, "BLOCK"), root_1);

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
    // ANTLRParser.g:548:1: alternative : ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) );
    public final ANTLRParser.alternative_return alternative() throws RecognitionException {
        ANTLRParser.alternative_return retval = new ANTLRParser.alternative_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.elements_return elements110 = null;

        ANTLRParser.rewrite_return rewrite111 = null;

        ANTLRParser.rewrite_return rewrite112 = null;


        RewriteRuleSubtreeStream stream_rewrite=new RewriteRuleSubtreeStream(adaptor,"rule rewrite");
        RewriteRuleSubtreeStream stream_elements=new RewriteRuleSubtreeStream(adaptor,"rule elements");
         paraphrases.push("matching alternative"); 
        try {
            // ANTLRParser.g:551:5: ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) )
            int alt31=3;
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
                alt31=1;
                }
                break;
            case RARROW:
                {
                alt31=2;
                }
                break;
            case EOF:
            case SEMI:
            case RPAREN:
            case OR:
                {
                alt31=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
                case 1 :
                    // ANTLRParser.g:551:7: elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
                    {
                    pushFollow(FOLLOW_elements_in_alternative2572);
                    elements110=elements();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_elements.add(elements110.getTree());
                    // ANTLRParser.g:552:6: ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
                    int alt30=2;
                    int LA30_0 = input.LA(1);

                    if ( (LA30_0==RARROW) ) {
                        alt30=1;
                    }
                    else if ( (LA30_0==EOF||LA30_0==SEMI||LA30_0==RPAREN||LA30_0==OR) ) {
                        alt30=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 30, 0, input);

                        throw nvae;
                    }
                    switch (alt30) {
                        case 1 :
                            // ANTLRParser.g:552:8: rewrite
                            {
                            pushFollow(FOLLOW_rewrite_in_alternative2581);
                            rewrite111=rewrite();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_rewrite.add(rewrite111.getTree());


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
                            // 552:16: -> ^( ALT_REWRITE elements rewrite )
                            {
                                // ANTLRParser.g:552:19: ^( ALT_REWRITE elements rewrite )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT_REWRITE), root_1);

                                adaptor.addChild(root_1, stream_elements.nextTree());
                                adaptor.addChild(root_1, stream_rewrite.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:553:10: 
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
                            // 553:10: -> elements
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
                    // ANTLRParser.g:555:7: rewrite
                    {
                    pushFollow(FOLLOW_rewrite_in_alternative2622);
                    rewrite112=rewrite();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewrite.add(rewrite112.getTree());


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
                    // 555:16: -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                    {
                        // ANTLRParser.g:555:19: ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT_REWRITE), root_1);

                        // ANTLRParser.g:555:41: ^( ALT EPSILON )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_2);

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
                    // ANTLRParser.g:556:10: 
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
                    // 556:10: -> ^( ALT EPSILON )
                    {
                        // ANTLRParser.g:556:13: ^( ALT EPSILON )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_1);

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
    // ANTLRParser.g:559:1: elements : (e+= element )+ -> ^( ALT ( $e)+ ) ;
    public final ANTLRParser.elements_return elements() throws RecognitionException {
        ANTLRParser.elements_return retval = new ANTLRParser.elements_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        List list_e=null;
        RuleReturnScope e = null;
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:560:5: ( (e+= element )+ -> ^( ALT ( $e)+ ) )
            // ANTLRParser.g:560:7: (e+= element )+
            {
            // ANTLRParser.g:560:8: (e+= element )+
            int cnt32=0;
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==SEMPRED||LA32_0==ACTION||LA32_0==TEMPLATE||LA32_0==LPAREN||LA32_0==DOT||LA32_0==TREE_BEGIN||LA32_0==NOT||(LA32_0>=TOKEN_REF && LA32_0<=RULE_REF)||LA32_0==STRING_LITERAL) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // ANTLRParser.g:560:8: e+= element
            	    {
            	    pushFollow(FOLLOW_element_in_elements2684);
            	    e=element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_element.add(e.getTree());
            	    if (list_e==null) list_e=new ArrayList();
            	    list_e.add(e.getTree());


            	    }
            	    break;

            	default :
            	    if ( cnt32 >= 1 ) break loop32;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(32, input);
                        throw eee;
                }
                cnt32++;
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
            // 560:19: -> ^( ALT ( $e)+ )
            {
                // ANTLRParser.g:560:22: ^( ALT ( $e)+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_1);

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
    // ANTLRParser.g:563:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$SEMPRED] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) ) ) | -> treeSpec ) );
    public final ANTLRParser.element_return element() throws RecognitionException {
        ANTLRParser.element_return retval = new ANTLRParser.element_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ACTION118=null;
        Token SEMPRED119=null;
        Token IMPLIES120=null;
        ANTLRParser.labeledElement_return labeledElement113 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix114 = null;

        ANTLRParser.atom_return atom115 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix116 = null;

        ANTLRParser.ebnf_return ebnf117 = null;

        ANTLRParser.treeSpec_return treeSpec121 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix122 = null;


        GrammarAST ACTION118_tree=null;
        GrammarAST SEMPRED119_tree=null;
        GrammarAST IMPLIES120_tree=null;
        RewriteRuleTokenStream stream_IMPLIES=new RewriteRuleTokenStream(adaptor,"token IMPLIES");
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        RewriteRuleSubtreeStream stream_treeSpec=new RewriteRuleSubtreeStream(adaptor,"rule treeSpec");
        RewriteRuleSubtreeStream stream_labeledElement=new RewriteRuleSubtreeStream(adaptor,"rule labeledElement");

        	paraphrases.push("looking for rule element");
        	int m = input.mark();

        try {
            // ANTLRParser.g:569:2: ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$SEMPRED] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) ) ) | -> treeSpec ) )
            int alt37=6;
            alt37 = dfa37.predict(input);
            switch (alt37) {
                case 1 :
                    // ANTLRParser.g:569:4: labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement )
                    {
                    pushFollow(FOLLOW_labeledElement_in_element2724);
                    labeledElement113=labeledElement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_labeledElement.add(labeledElement113.getTree());
                    // ANTLRParser.g:570:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement )
                    int alt33=2;
                    int LA33_0 = input.LA(1);

                    if ( (LA33_0==QUESTION||(LA33_0>=STAR && LA33_0<=PLUS)) ) {
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
                            // ANTLRParser.g:570:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2730);
                            ebnfSuffix114=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix114.getTree());


                            // AST REWRITE
                            // elements: labeledElement, ebnfSuffix
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 570:16: -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) )
                            {
                                // ANTLRParser.g:570:19: ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:570:33: ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, (labeledElement113!=null?((Token)labeledElement113.start):null), "BLOCK"), root_2);

                                // ANTLRParser.g:570:82: ^( ALT labeledElement )
                                {
                                GrammarAST root_3 = (GrammarAST)adaptor.nil();
                                root_3 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_3);

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
                            // ANTLRParser.g:571:8: 
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
                            // 571:8: -> labeledElement
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
                    // ANTLRParser.g:573:4: atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom )
                    {
                    pushFollow(FOLLOW_atom_in_element2776);
                    atom115=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_atom.add(atom115.getTree());
                    // ANTLRParser.g:574:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom )
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
                            // ANTLRParser.g:574:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2782);
                            ebnfSuffix116=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix116.getTree());


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
                            // 574:16: -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) )
                            {
                                // ANTLRParser.g:574:19: ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:574:33: ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, (atom115!=null?((Token)atom115.start):null), "BLOCK"), root_2);

                                // ANTLRParser.g:574:72: ^( ALT atom )
                                {
                                GrammarAST root_3 = (GrammarAST)adaptor.nil();
                                root_3 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_3);

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
                            // ANTLRParser.g:575:8: 
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
                            // 575:8: -> atom
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
                    // ANTLRParser.g:577:4: ebnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnf_in_element2828);
                    ebnf117=ebnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnf117.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:578:6: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION118=(Token)match(input,ACTION,FOLLOW_ACTION_in_element2835); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION118_tree = new ActionAST(ACTION118) ;
                    adaptor.addChild(root_0, ACTION118_tree);
                    }

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:579:6: SEMPRED ( IMPLIES -> GATED_SEMPRED[$SEMPRED] | -> SEMPRED )
                    {
                    SEMPRED119=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_element2845); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED119);

                    // ANTLRParser.g:580:3: ( IMPLIES -> GATED_SEMPRED[$SEMPRED] | -> SEMPRED )
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0==IMPLIES) ) {
                        alt35=1;
                    }
                    else if ( (LA35_0==EOF||LA35_0==SEMPRED||LA35_0==ACTION||LA35_0==TEMPLATE||(LA35_0>=SEMI && LA35_0<=RPAREN)||LA35_0==OR||LA35_0==DOT||(LA35_0>=RARROW && LA35_0<=TREE_BEGIN)||LA35_0==NOT||(LA35_0>=TOKEN_REF && LA35_0<=RULE_REF)||LA35_0==STRING_LITERAL) ) {
                        alt35=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 35, 0, input);

                        throw nvae;
                    }
                    switch (alt35) {
                        case 1 :
                            // ANTLRParser.g:580:5: IMPLIES
                            {
                            IMPLIES120=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_element2851); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IMPLIES.add(IMPLIES120);



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
                            // 580:14: -> GATED_SEMPRED[$SEMPRED]
                            {
                                adaptor.addChild(root_0, (GrammarAST)adaptor.create(GATED_SEMPRED, SEMPRED119));

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:581:8: 
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
                            // 581:8: -> SEMPRED
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
                    // ANTLRParser.g:583:6: treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) ) ) | -> treeSpec )
                    {
                    pushFollow(FOLLOW_treeSpec_in_element2879);
                    treeSpec121=treeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_treeSpec.add(treeSpec121.getTree());
                    // ANTLRParser.g:584:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) ) ) | -> treeSpec )
                    int alt36=2;
                    int LA36_0 = input.LA(1);

                    if ( (LA36_0==QUESTION||(LA36_0>=STAR && LA36_0<=PLUS)) ) {
                        alt36=1;
                    }
                    else if ( (LA36_0==EOF||LA36_0==SEMPRED||LA36_0==ACTION||LA36_0==TEMPLATE||(LA36_0>=SEMI && LA36_0<=RPAREN)||LA36_0==OR||LA36_0==DOT||(LA36_0>=RARROW && LA36_0<=TREE_BEGIN)||LA36_0==NOT||(LA36_0>=TOKEN_REF && LA36_0<=RULE_REF)||LA36_0==STRING_LITERAL) ) {
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
                            // ANTLRParser.g:584:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2885);
                            ebnfSuffix122=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix122.getTree());


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
                            // 584:16: -> ^( ebnfSuffix ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) ) )
                            {
                                // ANTLRParser.g:584:19: ^( ebnfSuffix ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:584:33: ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, (treeSpec121!=null?((Token)treeSpec121.start):null), "BLOCK"), root_2);

                                // ANTLRParser.g:584:76: ^( ALT treeSpec )
                                {
                                GrammarAST root_3 = (GrammarAST)adaptor.nil();
                                root_3 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_3);

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
                            // ANTLRParser.g:585:8: 
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
                            // 585:8: -> treeSpec
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
    // ANTLRParser.g:659:1: labeledElement : id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) ;
    public final ANTLRParser.labeledElement_return labeledElement() throws RecognitionException {
        ANTLRParser.labeledElement_return retval = new ANTLRParser.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN124=null;
        Token PLUS_ASSIGN125=null;
        ANTLRParser.id_return id123 = null;

        ANTLRParser.atom_return atom126 = null;

        ANTLRParser.block_return block127 = null;


        GrammarAST ASSIGN124_tree=null;
        GrammarAST PLUS_ASSIGN125_tree=null;

        try {
            // ANTLRParser.g:659:16: ( id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) )
            // ANTLRParser.g:659:18: id ( ASSIGN | PLUS_ASSIGN ) ( atom | block )
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_labeledElement2950);
            id123=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id123.getTree());
            // ANTLRParser.g:659:21: ( ASSIGN | PLUS_ASSIGN )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==ASSIGN) ) {
                alt38=1;
            }
            else if ( (LA38_0==PLUS_ASSIGN) ) {
                alt38=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // ANTLRParser.g:659:22: ASSIGN
                    {
                    ASSIGN124=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement2953); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN124_tree = (GrammarAST)adaptor.create(ASSIGN124);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN124_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:659:30: PLUS_ASSIGN
                    {
                    PLUS_ASSIGN125=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement2956); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS_ASSIGN125_tree = (GrammarAST)adaptor.create(PLUS_ASSIGN125);
                    root_0 = (GrammarAST)adaptor.becomeRoot(PLUS_ASSIGN125_tree, root_0);
                    }

                    }
                    break;

            }

            // ANTLRParser.g:659:44: ( atom | block )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==TEMPLATE||LA39_0==DOT||LA39_0==NOT||(LA39_0>=TOKEN_REF && LA39_0<=RULE_REF)||LA39_0==STRING_LITERAL) ) {
                alt39=1;
            }
            else if ( (LA39_0==LPAREN) ) {
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
                    // ANTLRParser.g:659:45: atom
                    {
                    pushFollow(FOLLOW_atom_in_labeledElement2961);
                    atom126=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom126.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:659:50: block
                    {
                    pushFollow(FOLLOW_block_in_labeledElement2963);
                    block127=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block127.getTree());

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
    // ANTLRParser.g:665:1: treeSpec : TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) ;
    public final ANTLRParser.treeSpec_return treeSpec() throws RecognitionException {
        ANTLRParser.treeSpec_return retval = new ANTLRParser.treeSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN128=null;
        Token RPAREN131=null;
        ANTLRParser.element_return element129 = null;

        ANTLRParser.element_return element130 = null;


        GrammarAST TREE_BEGIN128_tree=null;
        GrammarAST RPAREN131_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:666:5: ( TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) )
            // ANTLRParser.g:666:7: TREE_BEGIN element ( element )+ RPAREN
            {
            TREE_BEGIN128=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec2981); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN128);

            pushFollow(FOLLOW_element_in_treeSpec3022);
            element129=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_element.add(element129.getTree());
            // ANTLRParser.g:673:10: ( element )+
            int cnt40=0;
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==SEMPRED||LA40_0==ACTION||LA40_0==TEMPLATE||LA40_0==LPAREN||LA40_0==DOT||LA40_0==TREE_BEGIN||LA40_0==NOT||(LA40_0>=TOKEN_REF && LA40_0<=RULE_REF)||LA40_0==STRING_LITERAL) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // ANTLRParser.g:673:10: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec3053);
            	    element130=element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_element.add(element130.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt40 >= 1 ) break loop40;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(40, input);
                        throw eee;
                }
                cnt40++;
            } while (true);

            RPAREN131=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_treeSpec3062); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN131);



            // AST REWRITE
            // elements: element, TREE_BEGIN
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 675:7: -> ^( TREE_BEGIN ( element )+ )
            {
                // ANTLRParser.g:675:10: ^( TREE_BEGIN ( element )+ )
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
    // ANTLRParser.g:680:1: ebnf : block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) ;
    public final ANTLRParser.ebnf_return ebnf() throws RecognitionException {
        ANTLRParser.ebnf_return retval = new ANTLRParser.ebnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.block_return block132 = null;

        ANTLRParser.blockSuffixe_return blockSuffixe133 = null;


        RewriteRuleSubtreeStream stream_blockSuffixe=new RewriteRuleSubtreeStream(adaptor,"rule blockSuffixe");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:681:5: ( block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) )
            // ANTLRParser.g:681:7: block ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
            {
            pushFollow(FOLLOW_block_in_ebnf3096);
            block132=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_block.add(block132.getTree());
            // ANTLRParser.g:685:7: ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
            int alt41=2;
            int LA41_0 = input.LA(1);

            if ( (LA41_0==IMPLIES||(LA41_0>=QUESTION && LA41_0<=PLUS)||LA41_0==ROOT) ) {
                alt41=1;
            }
            else if ( (LA41_0==EOF||LA41_0==SEMPRED||LA41_0==ACTION||LA41_0==TEMPLATE||(LA41_0>=SEMI && LA41_0<=RPAREN)||LA41_0==OR||LA41_0==DOT||(LA41_0>=RARROW && LA41_0<=TREE_BEGIN)||LA41_0==NOT||(LA41_0>=TOKEN_REF && LA41_0<=RULE_REF)||LA41_0==STRING_LITERAL) ) {
                alt41=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // ANTLRParser.g:685:9: blockSuffixe
                    {
                    pushFollow(FOLLOW_blockSuffixe_in_ebnf3131);
                    blockSuffixe133=blockSuffixe();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_blockSuffixe.add(blockSuffixe133.getTree());


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
                    // 685:22: -> ^( blockSuffixe block )
                    {
                        // ANTLRParser.g:685:25: ^( blockSuffixe block )
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
                    // ANTLRParser.g:686:13: 
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
                    // 686:13: -> block
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
    // ANTLRParser.g:692:1: blockSuffixe : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final ANTLRParser.blockSuffixe_return blockSuffixe() throws RecognitionException {
        ANTLRParser.blockSuffixe_return retval = new ANTLRParser.blockSuffixe_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ROOT135=null;
        Token IMPLIES136=null;
        Token BANG137=null;
        ANTLRParser.ebnfSuffix_return ebnfSuffix134 = null;


        GrammarAST ROOT135_tree=null;
        GrammarAST IMPLIES136_tree=null;
        GrammarAST BANG137_tree=null;

        try {
            // ANTLRParser.g:693:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
            int alt42=4;
            switch ( input.LA(1) ) {
            case QUESTION:
            case STAR:
            case PLUS:
                {
                alt42=1;
                }
                break;
            case ROOT:
                {
                alt42=2;
                }
                break;
            case IMPLIES:
                {
                alt42=3;
                }
                break;
            case BANG:
                {
                alt42=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }

            switch (alt42) {
                case 1 :
                    // ANTLRParser.g:693:7: ebnfSuffix
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffixe3182);
                    ebnfSuffix134=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnfSuffix134.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:696:7: ROOT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ROOT135=(Token)match(input,ROOT,FOLLOW_ROOT_in_blockSuffixe3196); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ROOT135_tree = (GrammarAST)adaptor.create(ROOT135);
                    adaptor.addChild(root_0, ROOT135_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:697:7: IMPLIES
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    IMPLIES136=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffixe3204); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IMPLIES136_tree = (GrammarAST)adaptor.create(IMPLIES136);
                    adaptor.addChild(root_0, IMPLIES136_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:698:7: BANG
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    BANG137=(Token)match(input,BANG,FOLLOW_BANG_in_blockSuffixe3215); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BANG137_tree = (GrammarAST)adaptor.create(BANG137);
                    adaptor.addChild(root_0, BANG137_tree);
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
    // ANTLRParser.g:701:1: ebnfSuffix : ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] );
    public final ANTLRParser.ebnfSuffix_return ebnfSuffix() throws RecognitionException {
        ANTLRParser.ebnfSuffix_return retval = new ANTLRParser.ebnfSuffix_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token QUESTION138=null;
        Token STAR139=null;
        Token PLUS140=null;

        GrammarAST QUESTION138_tree=null;
        GrammarAST STAR139_tree=null;
        GrammarAST PLUS140_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");


        	Token op = input.LT(1);

        try {
            // ANTLRParser.g:705:2: ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] )
            int alt43=3;
            switch ( input.LA(1) ) {
            case QUESTION:
                {
                alt43=1;
                }
                break;
            case STAR:
                {
                alt43=2;
                }
                break;
            case PLUS:
                {
                alt43=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }

            switch (alt43) {
                case 1 :
                    // ANTLRParser.g:705:4: QUESTION
                    {
                    QUESTION138=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3234); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_QUESTION.add(QUESTION138);



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
                    // 705:13: -> OPTIONAL[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(OPTIONAL, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:706:6: STAR
                    {
                    STAR139=(Token)match(input,STAR,FOLLOW_STAR_in_ebnfSuffix3246); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STAR.add(STAR139);



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
                    // 706:13: -> CLOSURE[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(CLOSURE, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:707:7: PLUS
                    {
                    PLUS140=(Token)match(input,PLUS,FOLLOW_PLUS_in_ebnfSuffix3261); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS140);



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
                    // 707:14: -> POSITIVE_CLOSURE[op]
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
    // ANTLRParser.g:710:1: atom : ({...}? id DOT ruleref -> ^( DOT id ruleref ) | range ( ROOT | BANG )? | terminal ( ROOT | BANG )? | ruleref | notSet ( ROOT | BANG )? | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) );
    public final ANTLRParser.atom_return atom() throws RecognitionException {
        ANTLRParser.atom_return retval = new ANTLRParser.atom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOT142=null;
        Token ROOT145=null;
        Token BANG146=null;
        Token ROOT148=null;
        Token BANG149=null;
        Token ROOT152=null;
        Token BANG153=null;
        Token DOT154=null;
        ANTLRParser.id_return id141 = null;

        ANTLRParser.ruleref_return ruleref143 = null;

        ANTLRParser.range_return range144 = null;

        ANTLRParser.terminal_return terminal147 = null;

        ANTLRParser.ruleref_return ruleref150 = null;

        ANTLRParser.notSet_return notSet151 = null;

        ANTLRParser.elementOptions_return elementOptions155 = null;


        GrammarAST DOT142_tree=null;
        GrammarAST ROOT145_tree=null;
        GrammarAST BANG146_tree=null;
        GrammarAST ROOT148_tree=null;
        GrammarAST BANG149_tree=null;
        GrammarAST ROOT152_tree=null;
        GrammarAST BANG153_tree=null;
        GrammarAST DOT154_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        RewriteRuleSubtreeStream stream_ruleref=new RewriteRuleSubtreeStream(adaptor,"rule ruleref");
        try {
            // ANTLRParser.g:710:5: ({...}? id DOT ruleref -> ^( DOT id ruleref ) | range ( ROOT | BANG )? | terminal ( ROOT | BANG )? | ruleref | notSet ( ROOT | BANG )? | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) )
            int alt48=6;
            alt48 = dfa48.predict(input);
            switch (alt48) {
                case 1 :
                    // ANTLRParser.g:714:6: {...}? id DOT ruleref
                    {
                    if ( !((
                    	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                    	        input.LT(2).getCharPositionInLine() &&
                    	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                    	    )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atom", "\n\t    \tinput.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==\n\t        input.LT(2).getCharPositionInLine() &&\n\t        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()\n\t    ");
                    }
                    pushFollow(FOLLOW_id_in_atom3309);
                    id141=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id141.getTree());
                    DOT142=(Token)match(input,DOT,FOLLOW_DOT_in_atom3311); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT142);

                    pushFollow(FOLLOW_ruleref_in_atom3313);
                    ruleref143=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleref.add(ruleref143.getTree());


                    // AST REWRITE
                    // elements: id, ruleref, DOT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 719:21: -> ^( DOT id ruleref )
                    {
                        // ANTLRParser.g:719:24: ^( DOT id ruleref )
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
                case 2 :
                    // ANTLRParser.g:720:9: range ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_range_in_atom3333);
                    range144=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range144.getTree());
                    // ANTLRParser.g:720:18: ( ROOT | BANG )?
                    int alt44=3;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==ROOT) ) {
                        alt44=1;
                    }
                    else if ( (LA44_0==BANG) ) {
                        alt44=2;
                    }
                    switch (alt44) {
                        case 1 :
                            // ANTLRParser.g:720:19: ROOT
                            {
                            ROOT145=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3339); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT145_tree = (GrammarAST)adaptor.create(ROOT145);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT145_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:720:27: BANG
                            {
                            BANG146=(Token)match(input,BANG,FOLLOW_BANG_in_atom3344); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG146_tree = (GrammarAST)adaptor.create(BANG146);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG146_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // ANTLRParser.g:721:4: terminal ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_terminal_in_atom3353);
                    terminal147=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, terminal147.getTree());
                    // ANTLRParser.g:721:13: ( ROOT | BANG )?
                    int alt45=3;
                    int LA45_0 = input.LA(1);

                    if ( (LA45_0==ROOT) ) {
                        alt45=1;
                    }
                    else if ( (LA45_0==BANG) ) {
                        alt45=2;
                    }
                    switch (alt45) {
                        case 1 :
                            // ANTLRParser.g:721:14: ROOT
                            {
                            ROOT148=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3356); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT148_tree = (GrammarAST)adaptor.create(ROOT148);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT148_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:721:22: BANG
                            {
                            BANG149=(Token)match(input,BANG,FOLLOW_BANG_in_atom3361); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG149_tree = (GrammarAST)adaptor.create(BANG149);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG149_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // ANTLRParser.g:722:9: ruleref
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleref_in_atom3374);
                    ruleref150=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleref150.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:723:7: notSet ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_notSet_in_atom3382);
                    notSet151=notSet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, notSet151.getTree());
                    // ANTLRParser.g:723:16: ( ROOT | BANG )?
                    int alt46=3;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==ROOT) ) {
                        alt46=1;
                    }
                    else if ( (LA46_0==BANG) ) {
                        alt46=2;
                    }
                    switch (alt46) {
                        case 1 :
                            // ANTLRParser.g:723:17: ROOT
                            {
                            ROOT152=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3387); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT152_tree = (GrammarAST)adaptor.create(ROOT152);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT152_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:723:23: BANG
                            {
                            BANG153=(Token)match(input,BANG,FOLLOW_BANG_in_atom3390); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG153_tree = (GrammarAST)adaptor.create(BANG153);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG153_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 6 :
                    // ANTLRParser.g:729:6: DOT ( elementOptions )?
                    {
                    DOT154=(Token)match(input,DOT,FOLLOW_DOT_in_atom3418); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT154);

                    // ANTLRParser.g:729:10: ( elementOptions )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( (LA47_0==LT) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // ANTLRParser.g:729:10: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_atom3420);
                            elementOptions155=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions155.getTree());

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
                    // 729:33: -> ^( WILDCARD[$DOT] ( elementOptions )? )
                    {
                        // ANTLRParser.g:729:36: ^( WILDCARD[$DOT] ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(WILDCARD, DOT154), root_1);

                        // ANTLRParser.g:729:66: ( elementOptions )?
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
    // ANTLRParser.g:739:1: notSet : ( NOT terminal -> ^( NOT terminal ) | NOT block -> ^( NOT block ) );
    public final ANTLRParser.notSet_return notSet() throws RecognitionException {
        ANTLRParser.notSet_return retval = new ANTLRParser.notSet_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token NOT156=null;
        Token NOT158=null;
        ANTLRParser.terminal_return terminal157 = null;

        ANTLRParser.block_return block159 = null;


        GrammarAST NOT156_tree=null;
        GrammarAST NOT158_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleSubtreeStream stream_terminal=new RewriteRuleSubtreeStream(adaptor,"rule terminal");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:740:5: ( NOT terminal -> ^( NOT terminal ) | NOT block -> ^( NOT block ) )
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==NOT) ) {
                int LA49_1 = input.LA(2);

                if ( (LA49_1==TOKEN_REF||LA49_1==STRING_LITERAL) ) {
                    alt49=1;
                }
                else if ( (LA49_1==LPAREN) ) {
                    alt49=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // ANTLRParser.g:740:7: NOT terminal
                    {
                    NOT156=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3478); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT156);

                    pushFollow(FOLLOW_terminal_in_notSet3480);
                    terminal157=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_terminal.add(terminal157.getTree());


                    // AST REWRITE
                    // elements: NOT, terminal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 740:20: -> ^( NOT terminal )
                    {
                        // ANTLRParser.g:740:23: ^( NOT terminal )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_NOT.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_terminal.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:741:7: NOT block
                    {
                    NOT158=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3496); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT158);

                    pushFollow(FOLLOW_block_in_notSet3498);
                    block159=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_block.add(block159.getTree());


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
                    // 741:18: -> ^( NOT block )
                    {
                        // ANTLRParser.g:741:21: ^( NOT block )
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

    public static class block_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "block"
    // ANTLRParser.g:751:1: block : LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList ) ;
    public final ANTLRParser.block_return block() throws RecognitionException {
        ANTLRParser.block_return retval = new ANTLRParser.block_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN160=null;
        Token COLON162=null;
        Token RPAREN164=null;
        List list_ra=null;
        ANTLRParser.optionsSpec_return optionsSpec161 = null;

        ANTLRParser.altList_return altList163 = null;

        RuleReturnScope ra = null;
        GrammarAST LPAREN160_tree=null;
        GrammarAST COLON162_tree=null;
        GrammarAST RPAREN164_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_optionsSpec=new RewriteRuleSubtreeStream(adaptor,"rule optionsSpec");
        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        RewriteRuleSubtreeStream stream_ruleAction=new RewriteRuleSubtreeStream(adaptor,"rule ruleAction");
        try {
            // ANTLRParser.g:752:5: ( LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList ) )
            // ANTLRParser.g:752:7: LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN
            {
            LPAREN160=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_block3531); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN160);

            // ANTLRParser.g:755:10: ( ( optionsSpec )? (ra+= ruleAction )* COLON )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==OPTIONS||LA52_0==COLON||LA52_0==AT) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // ANTLRParser.g:755:12: ( optionsSpec )? (ra+= ruleAction )* COLON
                    {
                    // ANTLRParser.g:755:12: ( optionsSpec )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( (LA50_0==OPTIONS) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // ANTLRParser.g:755:12: optionsSpec
                            {
                            pushFollow(FOLLOW_optionsSpec_in_block3568);
                            optionsSpec161=optionsSpec();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_optionsSpec.add(optionsSpec161.getTree());

                            }
                            break;

                    }

                    // ANTLRParser.g:755:27: (ra+= ruleAction )*
                    loop51:
                    do {
                        int alt51=2;
                        int LA51_0 = input.LA(1);

                        if ( (LA51_0==AT) ) {
                            alt51=1;
                        }


                        switch (alt51) {
                    	case 1 :
                    	    // ANTLRParser.g:755:27: ra+= ruleAction
                    	    {
                    	    pushFollow(FOLLOW_ruleAction_in_block3573);
                    	    ra=ruleAction();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_ruleAction.add(ra.getTree());
                    	    if (list_ra==null) list_ra=new ArrayList();
                    	    list_ra.add(ra.getTree());


                    	    }
                    	    break;

                    	default :
                    	    break loop51;
                        }
                    } while (true);

                    COLON162=(Token)match(input,COLON,FOLLOW_COLON_in_block3576); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON162);


                    }
                    break;

            }

            pushFollow(FOLLOW_altList_in_block3590);
            altList163=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList163.getTree());
            RPAREN164=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_block3607); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN164);



            // AST REWRITE
            // elements: altList, optionsSpec, ra
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
            // 758:7: -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList )
            {
                // ANTLRParser.g:758:10: ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, LPAREN160, "BLOCK"), root_1);

                // ANTLRParser.g:758:45: ( optionsSpec )?
                if ( stream_optionsSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_optionsSpec.nextTree());

                }
                stream_optionsSpec.reset();
                // ANTLRParser.g:758:58: ( $ra)*
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
    // ANTLRParser.g:767:1: ruleref : RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) ;
    public final ANTLRParser.ruleref_return ruleref() throws RecognitionException {
        ANTLRParser.ruleref_return retval = new ANTLRParser.ruleref_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token op=null;
        Token RULE_REF165=null;
        Token ARG_ACTION166=null;

        GrammarAST op_tree=null;
        GrammarAST RULE_REF165_tree=null;
        GrammarAST ARG_ACTION166_tree=null;
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_ROOT=new RewriteRuleTokenStream(adaptor,"token ROOT");
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

        try {
            // ANTLRParser.g:768:5: ( RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) )
            // ANTLRParser.g:768:7: RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
            {
            RULE_REF165=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref3657); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF165);

            // ANTLRParser.g:768:16: ( ARG_ACTION )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==ARG_ACTION) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // ANTLRParser.g:768:16: ARG_ACTION
                    {
                    ARG_ACTION166=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref3659); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION166);


                    }
                    break;

            }

            // ANTLRParser.g:769:3: ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
            int alt55=2;
            int LA55_0 = input.LA(1);

            if ( (LA55_0==BANG||LA55_0==ROOT) ) {
                alt55=1;
            }
            else if ( (LA55_0==EOF||LA55_0==SEMPRED||LA55_0==ACTION||LA55_0==TEMPLATE||(LA55_0>=SEMI && LA55_0<=RPAREN)||LA55_0==QUESTION||(LA55_0>=STAR && LA55_0<=PLUS)||LA55_0==OR||LA55_0==DOT||(LA55_0>=RARROW && LA55_0<=TREE_BEGIN)||LA55_0==NOT||(LA55_0>=TOKEN_REF && LA55_0<=RULE_REF)||LA55_0==STRING_LITERAL) ) {
                alt55=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // ANTLRParser.g:769:5: (op= ROOT | op= BANG )
                    {
                    // ANTLRParser.g:769:5: (op= ROOT | op= BANG )
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==ROOT) ) {
                        alt54=1;
                    }
                    else if ( (LA54_0==BANG) ) {
                        alt54=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 54, 0, input);

                        throw nvae;
                    }
                    switch (alt54) {
                        case 1 :
                            // ANTLRParser.g:769:6: op= ROOT
                            {
                            op=(Token)match(input,ROOT,FOLLOW_ROOT_in_ruleref3669); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ROOT.add(op);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:769:14: op= BANG
                            {
                            op=(Token)match(input,BANG,FOLLOW_BANG_in_ruleref3673); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_BANG.add(op);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: RULE_REF, ARG_ACTION, op
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
                    // 769:23: -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                        // ANTLRParser.g:769:26: ^( $op ^( RULE_REF ( ARG_ACTION )? ) )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_op.nextNode(), root_1);

                        // ANTLRParser.g:769:32: ^( RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot(stream_RULE_REF.nextNode(), root_2);

                        // ANTLRParser.g:769:43: ( ARG_ACTION )?
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
                    // ANTLRParser.g:770:10: 
                    {

                    // AST REWRITE
                    // elements: RULE_REF, ARG_ACTION
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 770:10: -> ^( RULE_REF ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:770:13: ^( RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_RULE_REF.nextNode(), root_1);

                        // ANTLRParser.g:770:24: ( ARG_ACTION )?
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
    // ANTLRParser.g:784:1: range : STRING_LITERAL RANGE STRING_LITERAL ;
    public final ANTLRParser.range_return range() throws RecognitionException {
        ANTLRParser.range_return retval = new ANTLRParser.range_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token STRING_LITERAL167=null;
        Token RANGE168=null;
        Token STRING_LITERAL169=null;

        GrammarAST STRING_LITERAL167_tree=null;
        GrammarAST RANGE168_tree=null;
        GrammarAST STRING_LITERAL169_tree=null;

        try {
            // ANTLRParser.g:785:5: ( STRING_LITERAL RANGE STRING_LITERAL )
            // ANTLRParser.g:785:7: STRING_LITERAL RANGE STRING_LITERAL
            {
            root_0 = (GrammarAST)adaptor.nil();

            STRING_LITERAL167=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range3747); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING_LITERAL167_tree = new TerminalAST(STRING_LITERAL167) ;
            adaptor.addChild(root_0, STRING_LITERAL167_tree);
            }
            RANGE168=(Token)match(input,RANGE,FOLLOW_RANGE_in_range3752); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RANGE168_tree = (GrammarAST)adaptor.create(RANGE168);
            root_0 = (GrammarAST)adaptor.becomeRoot(RANGE168_tree, root_0);
            }
            STRING_LITERAL169=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range3755); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            STRING_LITERAL169_tree = new TerminalAST(STRING_LITERAL169) ;
            adaptor.addChild(root_0, STRING_LITERAL169_tree);
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
    // $ANTLR end "range"

    public static class terminal_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "terminal"
    // ANTLRParser.g:788:1: terminal : ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) );
    public final ANTLRParser.terminal_return terminal() throws RecognitionException {
        ANTLRParser.terminal_return retval = new ANTLRParser.terminal_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF170=null;
        Token ARG_ACTION171=null;
        Token STRING_LITERAL173=null;
        ANTLRParser.elementOptions_return elementOptions172 = null;

        ANTLRParser.elementOptions_return elementOptions174 = null;


        GrammarAST TOKEN_REF170_tree=null;
        GrammarAST ARG_ACTION171_tree=null;
        GrammarAST STRING_LITERAL173_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        try {
            // ANTLRParser.g:789:5: ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) )
            int alt59=2;
            int LA59_0 = input.LA(1);

            if ( (LA59_0==TOKEN_REF) ) {
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
                    // ANTLRParser.g:790:3: TOKEN_REF ( ARG_ACTION )? ( elementOptions )?
                    {
                    TOKEN_REF170=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal3780); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF170);

                    // ANTLRParser.g:790:13: ( ARG_ACTION )?
                    int alt56=2;
                    int LA56_0 = input.LA(1);

                    if ( (LA56_0==ARG_ACTION) ) {
                        alt56=1;
                    }
                    switch (alt56) {
                        case 1 :
                            // ANTLRParser.g:790:13: ARG_ACTION
                            {
                            ARG_ACTION171=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal3782); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION171);


                            }
                            break;

                    }

                    // ANTLRParser.g:790:25: ( elementOptions )?
                    int alt57=2;
                    int LA57_0 = input.LA(1);

                    if ( (LA57_0==LT) ) {
                        alt57=1;
                    }
                    switch (alt57) {
                        case 1 :
                            // ANTLRParser.g:790:25: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3785);
                            elementOptions172=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions172.getTree());

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
                    // 790:41: -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                    {
                        // ANTLRParser.g:790:44: ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_TOKEN_REF.nextToken()), root_1);

                        // ANTLRParser.g:790:69: ( ARG_ACTION )?
                        if ( stream_ARG_ACTION.hasNext() ) {
                            adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                        }
                        stream_ARG_ACTION.reset();
                        // ANTLRParser.g:790:81: ( elementOptions )?
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
                    // ANTLRParser.g:791:6: STRING_LITERAL ( elementOptions )?
                    {
                    STRING_LITERAL173=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal3808); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL173);

                    // ANTLRParser.g:791:21: ( elementOptions )?
                    int alt58=2;
                    int LA58_0 = input.LA(1);

                    if ( (LA58_0==LT) ) {
                        alt58=1;
                    }
                    switch (alt58) {
                        case 1 :
                            // ANTLRParser.g:791:21: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3810);
                            elementOptions174=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions174.getTree());

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
                    // 791:40: -> ^( STRING_LITERAL ( elementOptions )? )
                    {
                        // ANTLRParser.g:791:43: ^( STRING_LITERAL ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_STRING_LITERAL.nextToken()), root_1);

                        // ANTLRParser.g:791:73: ( elementOptions )?
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
    // ANTLRParser.g:796:1: elementOptions : LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
    public final ANTLRParser.elementOptions_return elementOptions() throws RecognitionException {
        ANTLRParser.elementOptions_return retval = new ANTLRParser.elementOptions_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LT175=null;
        Token COMMA177=null;
        Token GT179=null;
        ANTLRParser.elementOption_return elementOption176 = null;

        ANTLRParser.elementOption_return elementOption178 = null;


        GrammarAST LT175_tree=null;
        GrammarAST COMMA177_tree=null;
        GrammarAST GT179_tree=null;
        RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
        RewriteRuleTokenStream stream_LT=new RewriteRuleTokenStream(adaptor,"token LT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_elementOption=new RewriteRuleSubtreeStream(adaptor,"rule elementOption");
        try {
            // ANTLRParser.g:797:5: ( LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) )
            // ANTLRParser.g:797:7: LT elementOption ( COMMA elementOption )* GT
            {
            LT175=(Token)match(input,LT,FOLLOW_LT_in_elementOptions3842); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LT.add(LT175);

            pushFollow(FOLLOW_elementOption_in_elementOptions3844);
            elementOption176=elementOption();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_elementOption.add(elementOption176.getTree());
            // ANTLRParser.g:797:24: ( COMMA elementOption )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==COMMA) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // ANTLRParser.g:797:25: COMMA elementOption
            	    {
            	    COMMA177=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementOptions3847); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA177);

            	    pushFollow(FOLLOW_elementOption_in_elementOptions3849);
            	    elementOption178=elementOption();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_elementOption.add(elementOption178.getTree());

            	    }
            	    break;

            	default :
            	    break loop60;
                }
            } while (true);

            GT179=(Token)match(input,GT,FOLLOW_GT_in_elementOptions3853); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GT.add(GT179);



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
            // 797:50: -> ^( ELEMENT_OPTIONS ( elementOption )+ )
            {
                // ANTLRParser.g:797:53: ^( ELEMENT_OPTIONS ( elementOption )+ )
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
    // ANTLRParser.g:802:1: elementOption : ( qid | id ASSIGN ( qid | STRING_LITERAL ) );
    public final ANTLRParser.elementOption_return elementOption() throws RecognitionException {
        ANTLRParser.elementOption_return retval = new ANTLRParser.elementOption_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN182=null;
        Token STRING_LITERAL184=null;
        ANTLRParser.qid_return qid180 = null;

        ANTLRParser.id_return id181 = null;

        ANTLRParser.qid_return qid183 = null;


        GrammarAST ASSIGN182_tree=null;
        GrammarAST STRING_LITERAL184_tree=null;

        try {
            // ANTLRParser.g:803:5: ( qid | id ASSIGN ( qid | STRING_LITERAL ) )
            int alt62=2;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                int LA62_1 = input.LA(2);

                if ( (LA62_1==COMMA||LA62_1==GT||LA62_1==DOT) ) {
                    alt62=1;
                }
                else if ( (LA62_1==ASSIGN) ) {
                    alt62=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 1, input);

                    throw nvae;
                }
                }
                break;
            case TOKEN_REF:
                {
                int LA62_2 = input.LA(2);

                if ( (LA62_2==COMMA||LA62_2==GT||LA62_2==DOT) ) {
                    alt62=1;
                }
                else if ( (LA62_2==ASSIGN) ) {
                    alt62=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 2, input);

                    throw nvae;
                }
                }
                break;
            case TEMPLATE:
                {
                int LA62_3 = input.LA(2);

                if ( (LA62_3==COMMA||LA62_3==GT||LA62_3==DOT) ) {
                    alt62=1;
                }
                else if ( (LA62_3==ASSIGN) ) {
                    alt62=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 3, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 62, 0, input);

                throw nvae;
            }

            switch (alt62) {
                case 1 :
                    // ANTLRParser.g:804:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_elementOption3888);
                    qid180=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid180.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:807:7: id ASSIGN ( qid | STRING_LITERAL )
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_elementOption3910);
                    id181=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id181.getTree());
                    ASSIGN182=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption3912); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN182_tree = (GrammarAST)adaptor.create(ASSIGN182);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN182_tree, root_0);
                    }
                    // ANTLRParser.g:807:18: ( qid | STRING_LITERAL )
                    int alt61=2;
                    int LA61_0 = input.LA(1);

                    if ( (LA61_0==TEMPLATE||(LA61_0>=TOKEN_REF && LA61_0<=RULE_REF)) ) {
                        alt61=1;
                    }
                    else if ( (LA61_0==STRING_LITERAL) ) {
                        alt61=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 61, 0, input);

                        throw nvae;
                    }
                    switch (alt61) {
                        case 1 :
                            // ANTLRParser.g:807:19: qid
                            {
                            pushFollow(FOLLOW_qid_in_elementOption3916);
                            qid183=qid();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qid183.getTree());

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:807:25: STRING_LITERAL
                            {
                            STRING_LITERAL184=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption3920); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            STRING_LITERAL184_tree = new TerminalAST(STRING_LITERAL184) ;
                            adaptor.addChild(root_0, STRING_LITERAL184_tree);
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
    // ANTLRParser.g:810:1: rewrite : ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite ;
    public final ANTLRParser.rewrite_return rewrite() throws RecognitionException {
        ANTLRParser.rewrite_return retval = new ANTLRParser.rewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.predicatedRewrite_return predicatedRewrite185 = null;

        ANTLRParser.nakedRewrite_return nakedRewrite186 = null;


        RewriteRuleSubtreeStream stream_predicatedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule predicatedRewrite");
        RewriteRuleSubtreeStream stream_nakedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule nakedRewrite");
        try {
            // ANTLRParser.g:811:2: ( ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite )
            // ANTLRParser.g:811:4: ( predicatedRewrite )* nakedRewrite
            {
            // ANTLRParser.g:811:4: ( predicatedRewrite )*
            loop63:
            do {
                int alt63=2;
                int LA63_0 = input.LA(1);

                if ( (LA63_0==RARROW) ) {
                    int LA63_1 = input.LA(2);

                    if ( (LA63_1==SEMPRED) ) {
                        alt63=1;
                    }


                }


                switch (alt63) {
            	case 1 :
            	    // ANTLRParser.g:811:4: predicatedRewrite
            	    {
            	    pushFollow(FOLLOW_predicatedRewrite_in_rewrite3938);
            	    predicatedRewrite185=predicatedRewrite();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_predicatedRewrite.add(predicatedRewrite185.getTree());

            	    }
            	    break;

            	default :
            	    break loop63;
                }
            } while (true);

            pushFollow(FOLLOW_nakedRewrite_in_rewrite3941);
            nakedRewrite186=nakedRewrite();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_nakedRewrite.add(nakedRewrite186.getTree());


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
            // 811:36: -> ( predicatedRewrite )* nakedRewrite
            {
                // ANTLRParser.g:811:39: ( predicatedRewrite )*
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
    // ANTLRParser.g:814:1: predicatedRewrite : RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) ;
    public final ANTLRParser.predicatedRewrite_return predicatedRewrite() throws RecognitionException {
        ANTLRParser.predicatedRewrite_return retval = new ANTLRParser.predicatedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW187=null;
        Token SEMPRED188=null;
        ANTLRParser.rewriteAlt_return rewriteAlt189 = null;


        GrammarAST RARROW187_tree=null;
        GrammarAST SEMPRED188_tree=null;
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:815:2: ( RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) )
            // ANTLRParser.g:815:4: RARROW SEMPRED rewriteAlt
            {
            RARROW187=(Token)match(input,RARROW,FOLLOW_RARROW_in_predicatedRewrite3959); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW187);

            SEMPRED188=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite3961); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED188);

            pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite3963);
            rewriteAlt189=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt189.getTree());


            // AST REWRITE
            // elements: SEMPRED, SEMPRED, rewriteAlt, rewriteAlt
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 816:3: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
            if ((rewriteAlt189!=null?rewriteAlt189.isTemplate:false)) {
                // ANTLRParser.g:816:32: ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW187), root_1);

                adaptor.addChild(root_1, stream_SEMPRED.nextNode());
                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 817:3: -> ^( RESULT[$RARROW] SEMPRED rewriteAlt )
            {
                // ANTLRParser.g:817:6: ^( RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW187), root_1);

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
    // ANTLRParser.g:820:1: nakedRewrite : RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) ;
    public final ANTLRParser.nakedRewrite_return nakedRewrite() throws RecognitionException {
        ANTLRParser.nakedRewrite_return retval = new ANTLRParser.nakedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW190=null;
        ANTLRParser.rewriteAlt_return rewriteAlt191 = null;


        GrammarAST RARROW190_tree=null;
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:821:2: ( RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) )
            // ANTLRParser.g:821:4: RARROW rewriteAlt
            {
            RARROW190=(Token)match(input,RARROW,FOLLOW_RARROW_in_nakedRewrite4003); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW190);

            pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite4005);
            rewriteAlt191=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt191.getTree());


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
            // 821:22: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt )
            if ((rewriteAlt191!=null?rewriteAlt191.isTemplate:false)) {
                // ANTLRParser.g:821:51: ^( ST_RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW190), root_1);

                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 822:10: -> ^( RESULT[$RARROW] rewriteAlt )
            {
                // ANTLRParser.g:822:13: ^( RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW190), root_1);

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
    // ANTLRParser.g:827:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );
    public final ANTLRParser.rewriteAlt_return rewriteAlt() throws RecognitionException {
        ANTLRParser.rewriteAlt_return retval = new ANTLRParser.rewriteAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ETC194=null;
        ANTLRParser.rewriteTemplate_return rewriteTemplate192 = null;

        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt193 = null;


        GrammarAST ETC194_tree=null;

        try {
            // ANTLRParser.g:829:5: ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON )
            int alt64=4;
            alt64 = dfa64.predict(input);
            switch (alt64) {
                case 1 :
                    // ANTLRParser.g:830:7: rewriteTemplate
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplate_in_rewriteAlt4069);
                    rewriteTemplate192=rewriteTemplate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplate192.getTree());
                    if ( state.backtracking==0 ) {
                      retval.isTemplate =true;
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:836:7: rewriteTreeAlt
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteAlt4108);
                    rewriteTreeAlt193=rewriteTreeAlt();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAlt193.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:838:7: ETC
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ETC194=(Token)match(input,ETC,FOLLOW_ETC_in_rewriteAlt4117); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ETC194_tree = (GrammarAST)adaptor.create(ETC194);
                    adaptor.addChild(root_0, ETC194_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:840:27: 
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
                    // 840:27: -> EPSILON
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
    // ANTLRParser.g:843:1: rewriteTreeAlt : ( rewriteTreeElement )+ -> ^( ALT ( rewriteTreeElement )+ ) ;
    public final ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt() throws RecognitionException {
        ANTLRParser.rewriteTreeAlt_return retval = new ANTLRParser.rewriteTreeAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement195 = null;


        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:844:5: ( ( rewriteTreeElement )+ -> ^( ALT ( rewriteTreeElement )+ ) )
            // ANTLRParser.g:844:7: ( rewriteTreeElement )+
            {
            // ANTLRParser.g:844:7: ( rewriteTreeElement )+
            int cnt65=0;
            loop65:
            do {
                int alt65=2;
                int LA65_0 = input.LA(1);

                if ( (LA65_0==ACTION||LA65_0==LPAREN||LA65_0==DOLLAR||LA65_0==TREE_BEGIN||(LA65_0>=TOKEN_REF && LA65_0<=RULE_REF)||LA65_0==STRING_LITERAL) ) {
                    alt65=1;
                }


                switch (alt65) {
            	case 1 :
            	    // ANTLRParser.g:844:7: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4148);
            	    rewriteTreeElement195=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement195.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt65 >= 1 ) break loop65;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(65, input);
                        throw eee;
                }
                cnt65++;
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
            // 844:27: -> ^( ALT ( rewriteTreeElement )+ )
            {
                // ANTLRParser.g:844:30: ^( ALT ( rewriteTreeElement )+ )
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
    // ANTLRParser.g:847:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );
    public final ANTLRParser.rewriteTreeElement_return rewriteTreeElement() throws RecognitionException {
        ANTLRParser.rewriteTreeElement_return retval = new ANTLRParser.rewriteTreeElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom196 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom197 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix198 = null;

        ANTLRParser.rewriteTree_return rewriteTree199 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix200 = null;

        ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf201 = null;


        RewriteRuleSubtreeStream stream_rewriteTree=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTree");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        try {
            // ANTLRParser.g:848:2: ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf )
            int alt67=4;
            alt67 = dfa67.predict(input);
            switch (alt67) {
                case 1 :
                    // ANTLRParser.g:848:4: rewriteTreeAtom
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4172);
                    rewriteTreeAtom196=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAtom196.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:849:4: rewriteTreeAtom ebnfSuffix
                    {
                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4177);
                    rewriteTreeAtom197=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom197.getTree());
                    pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4179);
                    ebnfSuffix198=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix198.getTree());


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
                    // 849:31: -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) )
                    {
                        // ANTLRParser.g:849:34: ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                        // ANTLRParser.g:849:48: ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(REWRITE_BLOCK, "REWRITE_BLOCK"), root_2);

                        // ANTLRParser.g:849:64: ^( ALT rewriteTreeAtom )
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
                    // ANTLRParser.g:850:6: rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree )
                    {
                    pushFollow(FOLLOW_rewriteTree_in_rewriteTreeElement4204);
                    rewriteTree199=rewriteTree();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTree.add(rewriteTree199.getTree());
                    // ANTLRParser.g:851:3: ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree )
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==QUESTION||(LA66_0>=STAR && LA66_0<=PLUS)) ) {
                        alt66=1;
                    }
                    else if ( (LA66_0==EOF||LA66_0==ACTION||(LA66_0>=SEMI && LA66_0<=RPAREN)||LA66_0==OR||LA66_0==DOLLAR||(LA66_0>=RARROW && LA66_0<=TREE_BEGIN)||(LA66_0>=TOKEN_REF && LA66_0<=RULE_REF)||LA66_0==STRING_LITERAL) ) {
                        alt66=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 66, 0, input);

                        throw nvae;
                    }
                    switch (alt66) {
                        case 1 :
                            // ANTLRParser.g:851:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4210);
                            ebnfSuffix200=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix200.getTree());


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
                            // 852:4: -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) )
                            {
                                // ANTLRParser.g:852:7: ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:852:20: ^( REWRITE_BLOCK ^( ALT rewriteTree ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(REWRITE_BLOCK, "REWRITE_BLOCK"), root_2);

                                // ANTLRParser.g:852:36: ^( ALT rewriteTree )
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
                            // ANTLRParser.g:853:5: 
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
                            // 853:5: -> rewriteTree
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
                    // ANTLRParser.g:855:6: rewriteTreeEbnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4249);
                    rewriteTreeEbnf201=rewriteTreeEbnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeEbnf201.getTree());

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
    // ANTLRParser.g:858:1: rewriteTreeAtom : ( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION );
    public final ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom() throws RecognitionException {
        ANTLRParser.rewriteTreeAtom_return retval = new ANTLRParser.rewriteTreeAtom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF202=null;
        Token ARG_ACTION204=null;
        Token RULE_REF205=null;
        Token STRING_LITERAL206=null;
        Token DOLLAR208=null;
        Token ACTION210=null;
        ANTLRParser.elementOptions_return elementOptions203 = null;

        ANTLRParser.elementOptions_return elementOptions207 = null;

        ANTLRParser.id_return id209 = null;


        GrammarAST TOKEN_REF202_tree=null;
        GrammarAST ARG_ACTION204_tree=null;
        GrammarAST RULE_REF205_tree=null;
        GrammarAST STRING_LITERAL206_tree=null;
        GrammarAST DOLLAR208_tree=null;
        GrammarAST ACTION210_tree=null;
        RewriteRuleTokenStream stream_DOLLAR=new RewriteRuleTokenStream(adaptor,"token DOLLAR");
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        try {
            // ANTLRParser.g:859:5: ( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION )
            int alt71=5;
            switch ( input.LA(1) ) {
            case TOKEN_REF:
                {
                alt71=1;
                }
                break;
            case RULE_REF:
                {
                alt71=2;
                }
                break;
            case STRING_LITERAL:
                {
                alt71=3;
                }
                break;
            case DOLLAR:
                {
                alt71=4;
                }
                break;
            case ACTION:
                {
                alt71=5;
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
                    // ANTLRParser.g:859:9: TOKEN_REF ( elementOptions )? ( ARG_ACTION )?
                    {
                    TOKEN_REF202=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom4265); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF202);

                    // ANTLRParser.g:859:19: ( elementOptions )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==LT) ) {
                        alt68=1;
                    }
                    switch (alt68) {
                        case 1 :
                            // ANTLRParser.g:859:19: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom4267);
                            elementOptions203=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions203.getTree());

                            }
                            break;

                    }

                    // ANTLRParser.g:859:35: ( ARG_ACTION )?
                    int alt69=2;
                    int LA69_0 = input.LA(1);

                    if ( (LA69_0==ARG_ACTION) ) {
                        alt69=1;
                    }
                    switch (alt69) {
                        case 1 :
                            // ANTLRParser.g:859:35: ARG_ACTION
                            {
                            ARG_ACTION204=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom4270); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION204);


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
                    // 859:47: -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:859:50: ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_TOKEN_REF.nextToken()), root_1);

                        // ANTLRParser.g:859:75: ( elementOptions )?
                        if ( stream_elementOptions.hasNext() ) {
                            adaptor.addChild(root_1, stream_elementOptions.nextTree());

                        }
                        stream_elementOptions.reset();
                        // ANTLRParser.g:859:91: ( ARG_ACTION )?
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
                    // ANTLRParser.g:860:9: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF205=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_rewriteTreeAtom4297); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF205_tree = (GrammarAST)adaptor.create(RULE_REF205);
                    adaptor.addChild(root_0, RULE_REF205_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:861:6: STRING_LITERAL ( elementOptions )?
                    {
                    STRING_LITERAL206=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4304); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL206);

                    // ANTLRParser.g:861:21: ( elementOptions )?
                    int alt70=2;
                    int LA70_0 = input.LA(1);

                    if ( (LA70_0==LT) ) {
                        alt70=1;
                    }
                    switch (alt70) {
                        case 1 :
                            // ANTLRParser.g:861:21: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom4306);
                            elementOptions207=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions207.getTree());

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
                    // 861:40: -> ^( STRING_LITERAL ( elementOptions )? )
                    {
                        // ANTLRParser.g:861:43: ^( STRING_LITERAL ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_STRING_LITERAL.nextToken()), root_1);

                        // ANTLRParser.g:861:73: ( elementOptions )?
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
                    // ANTLRParser.g:862:6: DOLLAR id
                    {
                    DOLLAR208=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_rewriteTreeAtom4329); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOLLAR.add(DOLLAR208);

                    pushFollow(FOLLOW_id_in_rewriteTreeAtom4331);
                    id209=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id209.getTree());


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
                    // 862:16: -> LABEL[$DOLLAR,$id.text]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(LABEL, DOLLAR208, (id209!=null?input.toString(id209.start,id209.stop):null)));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // ANTLRParser.g:863:4: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION210=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTreeAtom4342); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION210_tree = new ActionAST(ACTION210) ;
                    adaptor.addChild(root_0, ACTION210_tree);
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
    // ANTLRParser.g:866:1: rewriteTreeEbnf : lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) ) ;
    public final ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf() throws RecognitionException {
        ANTLRParser.rewriteTreeEbnf_return retval = new ANTLRParser.rewriteTreeEbnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token RPAREN212=null;
        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt211 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix213 = null;


        GrammarAST lp_tree=null;
        GrammarAST RPAREN212_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTreeAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAlt");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");

            Token firstToken = input.LT(1);

        try {
            // ANTLRParser.g:874:2: (lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) ) )
            // ANTLRParser.g:874:4: lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTreeEbnf4368); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4370);
            rewriteTreeAlt211=rewriteTreeAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAlt.add(rewriteTreeAlt211.getTree());
            RPAREN212=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTreeEbnf4372); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN212);

            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4374);
            ebnfSuffix213=ebnfSuffix();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix213.getTree());


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
            // 874:47: -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) )
            {
                // ANTLRParser.g:874:50: ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                // ANTLRParser.g:874:63: ^( REWRITE_BLOCK[$lp] rewriteTreeAlt )
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
    // ANTLRParser.g:877:1: rewriteTree : TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) ;
    public final ANTLRParser.rewriteTree_return rewriteTree() throws RecognitionException {
        ANTLRParser.rewriteTree_return retval = new ANTLRParser.rewriteTree_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN214=null;
        Token RPAREN217=null;
        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom215 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement216 = null;


        GrammarAST TREE_BEGIN214_tree=null;
        GrammarAST RPAREN217_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:878:2: ( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) )
            // ANTLRParser.g:878:4: TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN
            {
            TREE_BEGIN214=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewriteTree4398); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN214);

            pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTree4400);
            rewriteTreeAtom215=rewriteTreeAtom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom215.getTree());
            // ANTLRParser.g:878:31: ( rewriteTreeElement )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);

                if ( (LA72_0==ACTION||LA72_0==LPAREN||LA72_0==DOLLAR||LA72_0==TREE_BEGIN||(LA72_0>=TOKEN_REF && LA72_0<=RULE_REF)||LA72_0==STRING_LITERAL) ) {
                    alt72=1;
                }


                switch (alt72) {
            	case 1 :
            	    // ANTLRParser.g:878:31: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTree4402);
            	    rewriteTreeElement216=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement216.getTree());

            	    }
            	    break;

            	default :
            	    break loop72;
                }
            } while (true);

            RPAREN217=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTree4405); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN217);



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
            // 879:3: -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
            {
                // ANTLRParser.g:879:6: ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_TREE_BEGIN.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rewriteTreeAtom.nextTree());
                // ANTLRParser.g:879:35: ( rewriteTreeElement )*
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
    // ANTLRParser.g:882:1: rewriteTemplate : ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );
    public final ANTLRParser.rewriteTemplate_return rewriteTemplate() throws RecognitionException {
        ANTLRParser.rewriteTemplate_return retval = new ANTLRParser.rewriteTemplate_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token str=null;
        Token TEMPLATE218=null;
        Token LPAREN219=null;
        Token RPAREN221=null;
        Token ACTION224=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs220 = null;

        ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef222 = null;

        ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead223 = null;


        GrammarAST str_tree=null;
        GrammarAST TEMPLATE218_tree=null;
        GrammarAST LPAREN219_tree=null;
        GrammarAST RPAREN221_tree=null;
        GrammarAST ACTION224_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_DOUBLE_QUOTE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_QUOTE_STRING_LITERAL");
        RewriteRuleTokenStream stream_TEMPLATE=new RewriteRuleTokenStream(adaptor,"token TEMPLATE");
        RewriteRuleTokenStream stream_DOUBLE_ANGLE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_ANGLE_STRING_LITERAL");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:893:2: ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION )
            int alt74=4;
            alt74 = dfa74.predict(input);
            switch (alt74) {
                case 1 :
                    // ANTLRParser.g:894:3: TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
                    {
                    TEMPLATE218=(Token)match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate4437); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TEMPLATE.add(TEMPLATE218);

                    LPAREN219=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplate4439); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN219);

                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4441);
                    rewriteTemplateArgs220=rewriteTemplateArgs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs220.getTree());
                    RPAREN221=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplate4443); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN221);

                    // ANTLRParser.g:895:3: (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
                    int alt73=2;
                    int LA73_0 = input.LA(1);

                    if ( (LA73_0==DOUBLE_QUOTE_STRING_LITERAL) ) {
                        alt73=1;
                    }
                    else if ( (LA73_0==DOUBLE_ANGLE_STRING_LITERAL) ) {
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
                            // ANTLRParser.g:895:5: str= DOUBLE_QUOTE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_QUOTE_STRING_LITERAL,FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4451); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOUBLE_QUOTE_STRING_LITERAL.add(str);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:895:39: str= DOUBLE_ANGLE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_ANGLE_STRING_LITERAL,FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4457); if (state.failed) return retval; 
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
                    // 896:3: -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                    {
                        // ANTLRParser.g:896:6: ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, TEMPLATE218, "TEMPLATE"), root_1);

                        // ANTLRParser.g:896:39: ( rewriteTemplateArgs )?
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
                    // ANTLRParser.g:899:3: rewriteTemplateRef
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplateRef_in_rewriteTemplate4483);
                    rewriteTemplateRef222=rewriteTemplateRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplateRef222.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:902:3: rewriteIndirectTemplateHead
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4492);
                    rewriteIndirectTemplateHead223=rewriteIndirectTemplateHead();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteIndirectTemplateHead223.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:905:3: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION224=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplate4501); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION224_tree = new ActionAST(ACTION224) ;
                    adaptor.addChild(root_0, ACTION224_tree);
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
    // ANTLRParser.g:908:1: rewriteTemplateRef : id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef() throws RecognitionException {
        ANTLRParser.rewriteTemplateRef_return retval = new ANTLRParser.rewriteTemplateRef_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN226=null;
        Token RPAREN228=null;
        ANTLRParser.id_return id225 = null;

        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs227 = null;


        GrammarAST LPAREN226_tree=null;
        GrammarAST RPAREN228_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:910:2: ( id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:910:4: id LPAREN rewriteTemplateArgs RPAREN
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateRef4517);
            id225=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id225.getTree());
            LPAREN226=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplateRef4519); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN226);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4521);
            rewriteTemplateArgs227=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs227.getTree());
            RPAREN228=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplateRef4523); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN228);



            // AST REWRITE
            // elements: rewriteTemplateArgs, id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 911:3: -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:911:6: ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, LPAREN226, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:911:40: ( rewriteTemplateArgs )?
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
    // ANTLRParser.g:914:1: rewriteIndirectTemplateHead : lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead() throws RecognitionException {
        ANTLRParser.rewriteIndirectTemplateHead_return retval = new ANTLRParser.rewriteIndirectTemplateHead_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token ACTION229=null;
        Token RPAREN230=null;
        Token LPAREN231=null;
        Token RPAREN233=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs232 = null;


        GrammarAST lp_tree=null;
        GrammarAST ACTION229_tree=null;
        GrammarAST RPAREN230_tree=null;
        GrammarAST LPAREN231_tree=null;
        GrammarAST RPAREN233_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:916:2: (lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:916:4: lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4552); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            ACTION229=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteIndirectTemplateHead4554); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION229);

            RPAREN230=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4556); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN230);

            LPAREN231=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4558); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN231);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4560);
            rewriteTemplateArgs232=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs232.getTree());
            RPAREN233=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4562); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN233);



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
            // 917:3: -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:917:6: ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, lp, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));
                // ANTLRParser.g:917:51: ( rewriteTemplateArgs )?
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
    // ANTLRParser.g:920:1: rewriteTemplateArgs : ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | );
    public final ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs() throws RecognitionException {
        ANTLRParser.rewriteTemplateArgs_return retval = new ANTLRParser.rewriteTemplateArgs_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token COMMA235=null;
        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg234 = null;

        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg236 = null;


        GrammarAST COMMA235_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_rewriteTemplateArg=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArg");
        try {
            // ANTLRParser.g:921:2: ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | )
            int alt76=2;
            int LA76_0 = input.LA(1);

            if ( (LA76_0==TEMPLATE||(LA76_0>=TOKEN_REF && LA76_0<=RULE_REF)) ) {
                alt76=1;
            }
            else if ( (LA76_0==RPAREN) ) {
                alt76=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // ANTLRParser.g:921:4: rewriteTemplateArg ( COMMA rewriteTemplateArg )*
                    {
                    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4590);
                    rewriteTemplateArg234=rewriteTemplateArg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg234.getTree());
                    // ANTLRParser.g:921:23: ( COMMA rewriteTemplateArg )*
                    loop75:
                    do {
                        int alt75=2;
                        int LA75_0 = input.LA(1);

                        if ( (LA75_0==COMMA) ) {
                            alt75=1;
                        }


                        switch (alt75) {
                    	case 1 :
                    	    // ANTLRParser.g:921:24: COMMA rewriteTemplateArg
                    	    {
                    	    COMMA235=(Token)match(input,COMMA,FOLLOW_COMMA_in_rewriteTemplateArgs4593); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA235);

                    	    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4595);
                    	    rewriteTemplateArg236=rewriteTemplateArg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg236.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop75;
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
                    // 922:3: -> ^( ARGLIST ( rewriteTemplateArg )+ )
                    {
                        // ANTLRParser.g:922:6: ^( ARGLIST ( rewriteTemplateArg )+ )
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
                    // ANTLRParser.g:924:2: 
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
    // ANTLRParser.g:926:1: rewriteTemplateArg : id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) ;
    public final ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg() throws RecognitionException {
        ANTLRParser.rewriteTemplateArg_return retval = new ANTLRParser.rewriteTemplateArg_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN238=null;
        Token ACTION239=null;
        ANTLRParser.id_return id237 = null;


        GrammarAST ASSIGN238_tree=null;
        GrammarAST ACTION239_tree=null;
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:927:2: ( id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) )
            // ANTLRParser.g:927:6: id ASSIGN ACTION
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateArg4624);
            id237=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id237.getTree());
            ASSIGN238=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_rewriteTemplateArg4626); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN238);

            ACTION239=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplateArg4628); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION239);



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
            // 927:23: -> ^( ARG[$ASSIGN] id ACTION )
            {
                // ANTLRParser.g:927:26: ^( ARG[$ASSIGN] id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ARG, ASSIGN238), root_1);

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
    // ANTLRParser.g:934:1: id : ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] | TEMPLATE -> ID[$TEMPLATE] );
    public final ANTLRParser.id_return id() throws RecognitionException {
        ANTLRParser.id_return retval = new ANTLRParser.id_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RULE_REF240=null;
        Token TOKEN_REF241=null;
        Token TEMPLATE242=null;

        GrammarAST RULE_REF240_tree=null;
        GrammarAST TOKEN_REF241_tree=null;
        GrammarAST TEMPLATE242_tree=null;
        RewriteRuleTokenStream stream_TEMPLATE=new RewriteRuleTokenStream(adaptor,"token TEMPLATE");
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");

         paraphrases.push("looking for an identifier"); 
        try {
            // ANTLRParser.g:937:5: ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] | TEMPLATE -> ID[$TEMPLATE] )
            int alt77=3;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                alt77=1;
                }
                break;
            case TOKEN_REF:
                {
                alt77=2;
                }
                break;
            case TEMPLATE:
                {
                alt77=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 77, 0, input);

                throw nvae;
            }

            switch (alt77) {
                case 1 :
                    // ANTLRParser.g:937:7: RULE_REF
                    {
                    RULE_REF240=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_id4670); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF240);



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
                    // 937:17: -> ID[$RULE_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, RULE_REF240));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:938:7: TOKEN_REF
                    {
                    TOKEN_REF241=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_id4683); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF241);



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
                    // 938:17: -> ID[$TOKEN_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TOKEN_REF241));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:939:7: TEMPLATE
                    {
                    TEMPLATE242=(Token)match(input,TEMPLATE,FOLLOW_TEMPLATE_in_id4695); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TEMPLATE.add(TEMPLATE242);



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
                    // 939:17: -> ID[$TEMPLATE]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TEMPLATE242));

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
    // ANTLRParser.g:942:1: qid : id ( DOT id )* -> ID[$qid.start, $text] ;
    public final ANTLRParser.qid_return qid() throws RecognitionException {
        ANTLRParser.qid_return retval = new ANTLRParser.qid_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOT244=null;
        ANTLRParser.id_return id243 = null;

        ANTLRParser.id_return id245 = null;


        GrammarAST DOT244_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
         paraphrases.push("looking for a qualified identifier"); 
        try {
            // ANTLRParser.g:945:2: ( id ( DOT id )* -> ID[$qid.start, $text] )
            // ANTLRParser.g:945:4: id ( DOT id )*
            {
            pushFollow(FOLLOW_id_in_qid4729);
            id243=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id243.getTree());
            // ANTLRParser.g:945:7: ( DOT id )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==DOT) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // ANTLRParser.g:945:8: DOT id
            	    {
            	    DOT244=(Token)match(input,DOT,FOLLOW_DOT_in_qid4732); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT244);

            	    pushFollow(FOLLOW_id_in_qid4734);
            	    id245=id();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_id.add(id245.getTree());

            	    }
            	    break;

            	default :
            	    break loop78;
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
            // 945:17: -> ID[$qid.start, $text]
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
    // ANTLRParser.g:948:1: alternativeEntry : alternative EOF ;
    public final ANTLRParser.alternativeEntry_return alternativeEntry() throws RecognitionException {
        ANTLRParser.alternativeEntry_return retval = new ANTLRParser.alternativeEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF247=null;
        ANTLRParser.alternative_return alternative246 = null;


        GrammarAST EOF247_tree=null;

        try {
            // ANTLRParser.g:948:18: ( alternative EOF )
            // ANTLRParser.g:948:20: alternative EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_alternative_in_alternativeEntry4751);
            alternative246=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, alternative246.getTree());
            EOF247=(Token)match(input,EOF,FOLLOW_EOF_in_alternativeEntry4753); if (state.failed) return retval;
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
    // $ANTLR end "alternativeEntry"

    public static class elementEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "elementEntry"
    // ANTLRParser.g:949:1: elementEntry : element EOF ;
    public final ANTLRParser.elementEntry_return elementEntry() throws RecognitionException {
        ANTLRParser.elementEntry_return retval = new ANTLRParser.elementEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF249=null;
        ANTLRParser.element_return element248 = null;


        GrammarAST EOF249_tree=null;

        try {
            // ANTLRParser.g:949:14: ( element EOF )
            // ANTLRParser.g:949:16: element EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_element_in_elementEntry4762);
            element248=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, element248.getTree());
            EOF249=(Token)match(input,EOF,FOLLOW_EOF_in_elementEntry4764); if (state.failed) return retval;
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
    // $ANTLR end "elementEntry"

    public static class ruleEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleEntry"
    // ANTLRParser.g:950:1: ruleEntry : rule EOF ;
    public final ANTLRParser.ruleEntry_return ruleEntry() throws RecognitionException {
        ANTLRParser.ruleEntry_return retval = new ANTLRParser.ruleEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF251=null;
        ANTLRParser.rule_return rule250 = null;


        GrammarAST EOF251_tree=null;

        try {
            // ANTLRParser.g:950:11: ( rule EOF )
            // ANTLRParser.g:950:13: rule EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_rule_in_ruleEntry4772);
            rule250=rule();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rule250.getTree());
            EOF251=(Token)match(input,EOF,FOLLOW_EOF_in_ruleEntry4774); if (state.failed) return retval;
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
    // $ANTLR end "ruleEntry"

    public static class blockEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blockEntry"
    // ANTLRParser.g:951:1: blockEntry : block EOF ;
    public final ANTLRParser.blockEntry_return blockEntry() throws RecognitionException {
        ANTLRParser.blockEntry_return retval = new ANTLRParser.blockEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF253=null;
        ANTLRParser.block_return block252 = null;


        GrammarAST EOF253_tree=null;

        try {
            // ANTLRParser.g:951:12: ( block EOF )
            // ANTLRParser.g:951:14: block EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_block_in_blockEntry4782);
            block252=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block252.getTree());
            EOF253=(Token)match(input,EOF,FOLLOW_EOF_in_blockEntry4784); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF253_tree = (GrammarAST)adaptor.create(EOF253);
            adaptor.addChild(root_0, EOF253_tree);
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
        // ANTLRParser.g:830:7: ( rewriteTemplate )
        // ANTLRParser.g:830:7: rewriteTemplate
        {
        pushFollow(FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4069);
        rewriteTemplate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_ANTLRParser

    // $ANTLR start synpred2_ANTLRParser
    public final void synpred2_ANTLRParser_fragment() throws RecognitionException {   
        // ANTLRParser.g:836:7: ( rewriteTreeAlt )
        // ANTLRParser.g:836:7: rewriteTreeAlt
        {
        pushFollow(FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4108);
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


    protected DFA37 dfa37 = new DFA37(this);
    protected DFA48 dfa48 = new DFA48(this);
    protected DFA64 dfa64 = new DFA64(this);
    protected DFA67 dfa67 = new DFA67(this);
    protected DFA74 dfa74 = new DFA74(this);
    static final String DFA37_eotS =
        "\12\uffff";
    static final String DFA37_eofS =
        "\1\uffff\2\4\7\uffff";
    static final String DFA37_minS =
        "\3\4\1\56\6\uffff";
    static final String DFA37_maxS =
        "\3\104\1\67\6\uffff";
    static final String DFA37_acceptS =
        "\4\uffff\1\2\1\3\1\4\1\5\1\6\1\1";
    static final String DFA37_specialS =
        "\12\uffff}>";
    static final String[] DFA37_transitionS = {
            "\1\7\13\uffff\1\6\22\uffff\1\3\5\uffff\1\5\15\uffff\1\4\3\uffff"+
            "\1\10\1\uffff\1\4\1\uffff\1\2\1\1\3\uffff\1\4",
            "\1\4\11\uffff\1\4\1\uffff\1\4\22\uffff\1\4\4\uffff\3\4\3\uffff"+
            "\1\11\4\4\1\11\2\4\1\uffff\1\4\2\uffff\2\4\1\uffff\1\4\1\uffff"+
            "\2\4\3\uffff\1\4",
            "\1\4\11\uffff\1\4\1\uffff\1\4\22\uffff\1\4\4\uffff\3\4\1\uffff"+
            "\1\4\1\uffff\1\11\4\4\1\11\2\4\1\uffff\1\4\2\uffff\2\4\1\uffff"+
            "\1\4\1\uffff\2\4\3\uffff\1\4",
            "\1\11\4\uffff\1\11\3\uffff\1\4",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA37_eot = DFA.unpackEncodedString(DFA37_eotS);
    static final short[] DFA37_eof = DFA.unpackEncodedString(DFA37_eofS);
    static final char[] DFA37_min = DFA.unpackEncodedStringToUnsignedChars(DFA37_minS);
    static final char[] DFA37_max = DFA.unpackEncodedStringToUnsignedChars(DFA37_maxS);
    static final short[] DFA37_accept = DFA.unpackEncodedString(DFA37_acceptS);
    static final short[] DFA37_special = DFA.unpackEncodedString(DFA37_specialS);
    static final short[][] DFA37_transition;

    static {
        int numStates = DFA37_transitionS.length;
        DFA37_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA37_transition[i] = DFA.unpackEncodedString(DFA37_transitionS[i]);
        }
    }

    class DFA37 extends DFA {

        public DFA37(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 37;
            this.eot = DFA37_eot;
            this.eof = DFA37_eof;
            this.min = DFA37_min;
            this.max = DFA37_max;
            this.accept = DFA37_accept;
            this.special = DFA37_special;
            this.transition = DFA37_transition;
        }
        public String getDescription() {
            return "563:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$SEMPRED] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$treeSpec.start,\"BLOCK\"] ^( ALT treeSpec ) ) ) | -> treeSpec ) );";
        }
    }
    static final String DFA48_eotS =
        "\16\uffff";
    static final String DFA48_eofS =
        "\1\uffff\1\10\1\12\1\uffff\1\12\2\uffff\1\10\1\uffff\1\12\4\uffff";
    static final String DFA48_minS =
        "\1\43\2\4\1\uffff\1\4\2\uffff\1\4\1\uffff\1\4\2\uffff\2\0";
    static final String DFA48_maxS =
        "\3\104\1\uffff\1\104\2\uffff\1\104\1\uffff\1\104\2\uffff\2\0";
    static final String DFA48_acceptS =
        "\3\uffff\1\1\1\uffff\1\5\1\6\1\uffff\1\4\1\uffff\1\3\1\2\2\uffff";
    static final String DFA48_specialS =
        "\14\uffff\1\0\1\1}>";
    static final String[] DFA48_transitionS = {
            "\1\3\23\uffff\1\6\5\uffff\1\5\1\uffff\1\2\1\1\3\uffff\1\4",
            "\1\10\11\uffff\1\10\1\uffff\1\10\22\uffff\1\10\4\uffff\3\10"+
            "\4\uffff\4\10\1\uffff\2\10\1\uffff\1\7\2\uffff\2\10\1\uffff"+
            "\1\10\1\uffff\2\10\3\uffff\1\10",
            "\1\12\11\uffff\1\12\1\uffff\1\12\22\uffff\1\12\4\uffff\3\12"+
            "\1\uffff\1\12\2\uffff\4\12\1\uffff\2\12\1\uffff\1\11\2\uffff"+
            "\2\12\1\uffff\1\12\1\uffff\2\12\3\uffff\1\12",
            "",
            "\1\12\13\uffff\1\12\22\uffff\1\12\4\uffff\3\12\1\uffff\1\12"+
            "\2\uffff\4\12\1\uffff\2\12\1\uffff\1\12\1\13\1\uffff\2\12\1"+
            "\uffff\1\12\1\uffff\2\12\3\uffff\1\12",
            "",
            "",
            "\1\10\13\uffff\1\10\22\uffff\1\10\4\uffff\3\10\1\uffff\1\10"+
            "\2\uffff\1\10\1\uffff\2\10\1\uffff\1\10\2\uffff\1\10\2\uffff"+
            "\2\10\1\uffff\1\10\1\uffff\1\10\1\14\3\uffff\1\10",
            "",
            "\1\12\13\uffff\1\12\22\uffff\1\12\4\uffff\3\12\1\uffff\1\12"+
            "\2\uffff\1\12\1\uffff\2\12\1\uffff\1\12\2\uffff\1\12\2\uffff"+
            "\2\12\1\uffff\1\12\1\uffff\1\12\1\15\3\uffff\1\12",
            "",
            "",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA48_eot = DFA.unpackEncodedString(DFA48_eotS);
    static final short[] DFA48_eof = DFA.unpackEncodedString(DFA48_eofS);
    static final char[] DFA48_min = DFA.unpackEncodedStringToUnsignedChars(DFA48_minS);
    static final char[] DFA48_max = DFA.unpackEncodedStringToUnsignedChars(DFA48_maxS);
    static final short[] DFA48_accept = DFA.unpackEncodedString(DFA48_acceptS);
    static final short[] DFA48_special = DFA.unpackEncodedString(DFA48_specialS);
    static final short[][] DFA48_transition;

    static {
        int numStates = DFA48_transitionS.length;
        DFA48_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA48_transition[i] = DFA.unpackEncodedString(DFA48_transitionS[i]);
        }
    }

    class DFA48 extends DFA {

        public DFA48(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 48;
            this.eot = DFA48_eot;
            this.eof = DFA48_eof;
            this.min = DFA48_min;
            this.max = DFA48_max;
            this.accept = DFA48_accept;
            this.special = DFA48_special;
            this.transition = DFA48_transition;
        }
        public String getDescription() {
            return "710:1: atom : ({...}? id DOT ruleref -> ^( DOT id ruleref ) | range ( ROOT | BANG )? | terminal ( ROOT | BANG )? | ruleref | notSet ( ROOT | BANG )? | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA48_12 = input.LA(1);

                         
                        int index48_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 3;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index48_12);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA48_13 = input.LA(1);

                         
                        int index48_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 3;}

                        else if ( (true) ) {s = 10;}

                         
                        input.seek(index48_13);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 48, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA64_eotS =
        "\16\uffff";
    static final String DFA64_eofS =
        "\1\10\1\uffff\2\6\12\uffff";
    static final String DFA64_minS =
        "\1\20\1\uffff\1\20\1\16\1\20\1\0\3\uffff\3\20\1\16\1\51";
    static final String DFA64_maxS =
        "\1\104\1\uffff\3\104\1\0\3\uffff\4\104\1\62";
    static final String DFA64_acceptS =
        "\1\uffff\1\1\4\uffff\1\2\1\3\1\4\5\uffff";
    static final String DFA64_specialS =
        "\5\uffff\1\0\10\uffff}>";
    static final String[] DFA64_transitionS = {
            "\1\5\22\uffff\1\1\4\uffff\1\10\1\4\1\10\11\uffff\1\10\1\uffff"+
            "\1\6\2\uffff\1\7\1\10\1\6\3\uffff\1\3\1\2\3\uffff\1\6",
            "",
            "\1\6\27\uffff\1\6\1\11\1\6\4\uffff\1\6\1\uffff\2\6\1\uffff"+
            "\1\6\1\uffff\1\6\3\uffff\2\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\1\uffff\1\6\27\uffff\1\6\1\11\1\6\1\uffff\1\6\2\uffff"+
            "\1\6\1\uffff\2\6\1\uffff\1\6\1\uffff\1\6\3\uffff\2\6\3\uffff"+
            "\2\6\3\uffff\1\6",
            "\1\12\30\uffff\1\6\14\uffff\1\6\4\uffff\1\6\3\uffff\2\6\3\uffff"+
            "\1\6",
            "\1\uffff",
            "",
            "",
            "",
            "\1\6\22\uffff\1\1\5\uffff\1\6\1\1\13\uffff\1\6\4\uffff\1\6"+
            "\3\uffff\1\14\1\13\3\uffff\1\6",
            "\1\6\30\uffff\1\6\1\15\4\uffff\1\6\1\uffff\2\6\3\uffff\1\6"+
            "\4\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\30\uffff\2\6\3\uffff\1\1\1\6\1\uffff\2\6\3\uffff\1\6\4"+
            "\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\6\1\uffff\1\6\30\uffff\2\6\1\uffff\1\6\1\uffff\1\1\1\6\1"+
            "\uffff\2\6\3\uffff\1\6\4\uffff\1\6\3\uffff\2\6\3\uffff\1\6",
            "\1\1\5\uffff\1\6\1\uffff\2\6"
    };

    static final short[] DFA64_eot = DFA.unpackEncodedString(DFA64_eotS);
    static final short[] DFA64_eof = DFA.unpackEncodedString(DFA64_eofS);
    static final char[] DFA64_min = DFA.unpackEncodedStringToUnsignedChars(DFA64_minS);
    static final char[] DFA64_max = DFA.unpackEncodedStringToUnsignedChars(DFA64_maxS);
    static final short[] DFA64_accept = DFA.unpackEncodedString(DFA64_acceptS);
    static final short[] DFA64_special = DFA.unpackEncodedString(DFA64_specialS);
    static final short[][] DFA64_transition;

    static {
        int numStates = DFA64_transitionS.length;
        DFA64_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA64_transition[i] = DFA.unpackEncodedString(DFA64_transitionS[i]);
        }
    }

    class DFA64 extends DFA {

        public DFA64(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 64;
            this.eot = DFA64_eot;
            this.eof = DFA64_eof;
            this.min = DFA64_min;
            this.max = DFA64_max;
            this.accept = DFA64_accept;
            this.special = DFA64_special;
            this.transition = DFA64_transition;
        }
        public String getDescription() {
            return "827:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA64_5 = input.LA(1);

                         
                        int index64_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_ANTLRParser()) ) {s = 1;}

                        else if ( (synpred2_ANTLRParser()) ) {s = 6;}

                         
                        input.seek(index64_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 64, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA67_eotS =
        "\124\uffff";
    static final String DFA67_eofS =
        "\1\uffff\3\12\1\uffff\1\12\3\uffff\1\12\3\uffff\3\12\10\uffff\1"+
        "\12\3\uffff\1\12\67\uffff";
    static final String DFA67_minS =
        "\1\20\1\16\2\20\1\43\1\20\2\uffff\1\43\1\20\2\uffff\1\43\3\20\6"+
        "\47\2\43\1\16\3\43\1\20\1\43\24\47\6\43\24\47\2\43\6\47";
    static final String DFA67_maxS =
        "\4\104\1\100\1\104\2\uffff\1\100\1\104\2\uffff\1\100\3\104\6\67"+
        "\2\100\2\104\2\100\2\104\11\67\1\55\11\67\1\55\1\100\1\104\2\100"+
        "\1\104\1\100\6\67\1\55\11\67\1\55\3\67\2\100\6\67";
    static final String DFA67_acceptS =
        "\6\uffff\1\3\1\4\2\uffff\1\1\1\2\110\uffff";
    static final String DFA67_specialS =
        "\124\uffff}>";
    static final String[] DFA67_transitionS = {
            "\1\5\30\uffff\1\7\14\uffff\1\4\4\uffff\1\6\3\uffff\1\1\1\2\3"+
            "\uffff\1\3",
            "\1\11\1\uffff\1\12\27\uffff\3\12\1\uffff\1\10\2\uffff\1\13"+
            "\1\uffff\2\13\1\uffff\1\12\1\uffff\1\12\3\uffff\2\12\3\uffff"+
            "\2\12\3\uffff\1\12",
            "\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\12\27\uffff\3\12\1\uffff\1\14\2\uffff\1\13\1\uffff\2\13"+
            "\1\uffff\1\12\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff"+
            "\1\12",
            "\1\17\33\uffff\1\16\1\15",
            "\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "",
            "",
            "\1\22\33\uffff\1\21\1\20",
            "\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "",
            "",
            "\1\25\33\uffff\1\24\1\23",
            "\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\27\5\uffff\1\30\1\31\10\uffff\1\26",
            "\1\27\5\uffff\1\30\1\31\10\uffff\1\26",
            "\1\27\5\uffff\1\30\1\31\10\uffff\1\26",
            "\1\33\5\uffff\1\34\1\35\10\uffff\1\32",
            "\1\33\5\uffff\1\34\1\35\10\uffff\1\32",
            "\1\33\5\uffff\1\34\1\35\10\uffff\1\32",
            "\1\40\33\uffff\1\37\1\36",
            "\1\43\33\uffff\1\42\1\41",
            "\1\11\1\uffff\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13"+
            "\1\uffff\1\12\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff"+
            "\1\12",
            "\1\46\33\uffff\1\45\1\44\3\uffff\1\47",
            "\1\52\33\uffff\1\51\1\50",
            "\1\55\33\uffff\1\54\1\53",
            "\1\12\27\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\60\33\uffff\1\57\1\56\3\uffff\1\61",
            "\1\27\5\uffff\1\30\11\uffff\1\26",
            "\1\27\5\uffff\1\30\11\uffff\1\26",
            "\1\27\5\uffff\1\30\11\uffff\1\26",
            "\1\27\5\uffff\1\30\1\63\10\uffff\1\62",
            "\1\27\5\uffff\1\30\1\63\10\uffff\1\62",
            "\1\27\5\uffff\1\30\1\63\10\uffff\1\62",
            "\1\27\5\uffff\1\30\11\uffff\1\64",
            "\1\27\5\uffff\1\30\11\uffff\1\64",
            "\1\27\5\uffff\1\30\11\uffff\1\64",
            "\1\27\5\uffff\1\30",
            "\1\33\5\uffff\1\34\11\uffff\1\32",
            "\1\33\5\uffff\1\34\11\uffff\1\32",
            "\1\33\5\uffff\1\34\11\uffff\1\32",
            "\1\33\5\uffff\1\34\1\66\10\uffff\1\65",
            "\1\33\5\uffff\1\34\1\66\10\uffff\1\65",
            "\1\33\5\uffff\1\34\1\66\10\uffff\1\65",
            "\1\33\5\uffff\1\34\11\uffff\1\67",
            "\1\33\5\uffff\1\34\11\uffff\1\67",
            "\1\33\5\uffff\1\34\11\uffff\1\67",
            "\1\33\5\uffff\1\34",
            "\1\72\33\uffff\1\71\1\70",
            "\1\75\33\uffff\1\74\1\73\3\uffff\1\76",
            "\1\101\33\uffff\1\100\1\77",
            "\1\104\33\uffff\1\103\1\102",
            "\1\107\33\uffff\1\106\1\105\3\uffff\1\110",
            "\1\113\33\uffff\1\112\1\111",
            "\1\27\5\uffff\1\30\11\uffff\1\62",
            "\1\27\5\uffff\1\30\11\uffff\1\62",
            "\1\27\5\uffff\1\30\11\uffff\1\62",
            "\1\27\5\uffff\1\30\11\uffff\1\114",
            "\1\27\5\uffff\1\30\11\uffff\1\114",
            "\1\27\5\uffff\1\30\11\uffff\1\114",
            "\1\27\5\uffff\1\30",
            "\1\27\5\uffff\1\30\11\uffff\1\64",
            "\1\27\5\uffff\1\30\11\uffff\1\64",
            "\1\27\5\uffff\1\30\11\uffff\1\64",
            "\1\33\5\uffff\1\34\11\uffff\1\65",
            "\1\33\5\uffff\1\34\11\uffff\1\65",
            "\1\33\5\uffff\1\34\11\uffff\1\65",
            "\1\33\5\uffff\1\34\11\uffff\1\115",
            "\1\33\5\uffff\1\34\11\uffff\1\115",
            "\1\33\5\uffff\1\34\11\uffff\1\115",
            "\1\33\5\uffff\1\34",
            "\1\33\5\uffff\1\34\11\uffff\1\67",
            "\1\33\5\uffff\1\34\11\uffff\1\67",
            "\1\33\5\uffff\1\34\11\uffff\1\67",
            "\1\120\33\uffff\1\117\1\116",
            "\1\123\33\uffff\1\122\1\121",
            "\1\27\5\uffff\1\30\11\uffff\1\114",
            "\1\27\5\uffff\1\30\11\uffff\1\114",
            "\1\27\5\uffff\1\30\11\uffff\1\114",
            "\1\33\5\uffff\1\34\11\uffff\1\115",
            "\1\33\5\uffff\1\34\11\uffff\1\115",
            "\1\33\5\uffff\1\34\11\uffff\1\115"
    };

    static final short[] DFA67_eot = DFA.unpackEncodedString(DFA67_eotS);
    static final short[] DFA67_eof = DFA.unpackEncodedString(DFA67_eofS);
    static final char[] DFA67_min = DFA.unpackEncodedStringToUnsignedChars(DFA67_minS);
    static final char[] DFA67_max = DFA.unpackEncodedStringToUnsignedChars(DFA67_maxS);
    static final short[] DFA67_accept = DFA.unpackEncodedString(DFA67_acceptS);
    static final short[] DFA67_special = DFA.unpackEncodedString(DFA67_specialS);
    static final short[][] DFA67_transition;

    static {
        int numStates = DFA67_transitionS.length;
        DFA67_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA67_transition[i] = DFA.unpackEncodedString(DFA67_transitionS[i]);
        }
    }

    class DFA67 extends DFA {

        public DFA67(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 67;
            this.eot = DFA67_eot;
            this.eof = DFA67_eof;
            this.min = DFA67_min;
            this.max = DFA67_max;
            this.accept = DFA67_accept;
            this.special = DFA67_special;
            this.transition = DFA67_transition;
        }
        public String getDescription() {
            return "847:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );";
        }
    }
    static final String DFA74_eotS =
        "\23\uffff";
    static final String DFA74_eofS =
        "\11\uffff\1\2\11\uffff";
    static final String DFA74_minS =
        "\1\20\1\51\3\uffff\1\43\3\56\1\12\1\20\1\uffff\1\47\1\43\3\56\1"+
        "\20\1\47";
    static final String DFA74_maxS =
        "\1\100\1\51\3\uffff\1\100\3\56\1\72\1\20\1\uffff\1\52\1\100\3\56"+
        "\1\20\1\52";
    static final String DFA74_acceptS =
        "\2\uffff\1\2\1\3\1\4\6\uffff\1\1\7\uffff";
    static final String DFA74_specialS =
        "\23\uffff}>";
    static final String[] DFA74_transitionS = {
            "\1\4\22\uffff\1\1\5\uffff\1\3\25\uffff\2\2",
            "\1\5",
            "",
            "",
            "",
            "\1\10\6\uffff\1\11\24\uffff\1\7\1\6",
            "\1\12",
            "\1\12",
            "\1\12",
            "\2\13\34\uffff\1\2\1\uffff\1\2\11\uffff\1\2\5\uffff\1\2",
            "\1\14",
            "",
            "\1\15\2\uffff\1\11",
            "\1\20\33\uffff\1\17\1\16",
            "\1\21",
            "\1\21",
            "\1\21",
            "\1\22",
            "\1\15\2\uffff\1\11"
    };

    static final short[] DFA74_eot = DFA.unpackEncodedString(DFA74_eotS);
    static final short[] DFA74_eof = DFA.unpackEncodedString(DFA74_eofS);
    static final char[] DFA74_min = DFA.unpackEncodedStringToUnsignedChars(DFA74_minS);
    static final char[] DFA74_max = DFA.unpackEncodedStringToUnsignedChars(DFA74_maxS);
    static final short[] DFA74_accept = DFA.unpackEncodedString(DFA74_acceptS);
    static final short[] DFA74_special = DFA.unpackEncodedString(DFA74_specialS);
    static final short[][] DFA74_transition;

    static {
        int numStates = DFA74_transitionS.length;
        DFA74_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA74_transition[i] = DFA.unpackEncodedString(DFA74_transitionS[i]);
        }
    }

    class DFA74 extends DFA {

        public DFA74(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 74;
            this.eot = DFA74_eot;
            this.eof = DFA74_eof;
            this.min = DFA74_min;
            this.max = DFA74_max;
            this.accept = DFA74_accept;
            this.special = DFA74_special;
            this.transition = DFA74_transition;
        }
        public String getDescription() {
            return "882:1: rewriteTemplate : ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );";
        }
    }
 

    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec456 = new BitSet(new long[]{0x000000000F000000L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec493 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_grammarSpec495 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_SEMI_in_grammarSpec497 = new BitSet(new long[]{0x9000000870F80040L,0x0000000000000001L});
    public static final BitSet FOLLOW_sync_in_grammarSpec541 = new BitSet(new long[]{0x9000000870F80040L,0x0000000000000001L});
    public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec545 = new BitSet(new long[]{0x9000000870F80040L,0x0000000000000001L});
    public static final BitSet FOLLOW_sync_in_grammarSpec547 = new BitSet(new long[]{0x9000000870F80040L,0x0000000000000001L});
    public static final BitSet FOLLOW_rules_in_grammarSpec577 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_mode_in_grammarSpec586 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_EOF_in_grammarSpec630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_grammarType827 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARSER_in_grammarType864 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_in_grammarType895 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct1016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct1066 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrScope_in_prequelConstruct1102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_prequelConstruct1145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec1162 = new BitSet(new long[]{0xC000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_option_in_optionsSpec1165 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_SEMI_in_optionsSpec1167 = new BitSet(new long[]{0xC000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RBRACE_in_optionsSpec1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_option1208 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_option1210 = new BitSet(new long[]{0x8002000800000000L,0x0000000000000013L});
    public static final BitSet FOLLOW_optionValue_in_option1213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_optionValue1263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_optionValue1287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_optionValue1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_optionValue1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_delegateGrammars1358 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1360 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_COMMA_in_delegateGrammars1363 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1365 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_SEMI_in_delegateGrammars1369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1396 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar1398 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec1427 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec1429 = new BitSet(new long[]{0xC000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RBRACE_in_tokensSpec1432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_tokenSpec1452 = new BitSet(new long[]{0x0000410000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec1458 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec1460 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_SEMI_in_tokenSpec1495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_tokenSpec1500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope1515 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_attrScope1517 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope1519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_action1548 = new BitSet(new long[]{0x8000000803000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_actionScopeName_in_action1551 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_COLONCOLON_in_action1553 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_action1557 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action1559 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_actionScopeName1590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_actionScopeName1595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARSER_in_actionScopeName1610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODE_in_mode1624 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_mode1626 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_SEMI_in_mode1628 = new BitSet(new long[]{0x8000000870800040L,0x0000000000000001L});
    public static final BitSet FOLLOW_sync_in_mode1630 = new BitSet(new long[]{0x8000000870800040L,0x0000000000000001L});
    public static final BitSet FOLLOW_rule_in_mode1633 = new BitSet(new long[]{0x8000000870800040L,0x0000000000000001L});
    public static final BitSet FOLLOW_sync_in_mode1635 = new BitSet(new long[]{0x8000000870800042L,0x0000000000000001L});
    public static final BitSet FOLLOW_sync_in_rules1662 = new BitSet(new long[]{0x8000000870800042L,0x0000000000000001L});
    public static final BitSet FOLLOW_rule_in_rules1665 = new BitSet(new long[]{0x8000000870800040L,0x0000000000000001L});
    public static final BitSet FOLLOW_sync_in_rules1667 = new BitSet(new long[]{0x8000000870800042L,0x0000000000000001L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_rule1769 = new BitSet(new long[]{0x8000000870800000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleModifiers_in_rule1813 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_rule1836 = new BitSet(new long[]{0x1000000180284000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule1869 = new BitSet(new long[]{0x1000000180284000L});
    public static final BitSet FOLLOW_ruleReturns_in_rule1879 = new BitSet(new long[]{0x1000000180284000L});
    public static final BitSet FOLLOW_rulePrequels_in_rule1917 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_COLON_in_rule1932 = new BitSet(new long[]{0xAC90020800010010L,0x0000000000000011L});
    public static final BitSet FOLLOW_ruleBlock_in_rule1961 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_SEMI_in_rule1976 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule1985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup2077 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup2080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler2097 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler2099 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler2101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause2127 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause2129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sync_in_rulePrequels2163 = new BitSet(new long[]{0x1000000100280002L});
    public static final BitSet FOLLOW_rulePrequel_in_rulePrequels2166 = new BitSet(new long[]{0x1000000100280000L});
    public static final BitSet FOLLOW_sync_in_rulePrequels2168 = new BitSet(new long[]{0x1000000100280002L});
    public static final BitSet FOLLOW_throwsSpec_in_rulePrequel2194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rulePrequel2202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_rulePrequel2210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_rulePrequel2218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns2238 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns2241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROWS_in_throwsSpec2266 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2268 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_COMMA_in_throwsSpec2271 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2273 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2304 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec2306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2319 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2321 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_COMMA_in_ruleScopeSpec2324 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2326 = new BitSet(new long[]{0x0000018000000000L});
    public static final BitSet FOLLOW_SEMI_in_ruleScopeSpec2330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_ruleAction2360 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_ruleAction2362 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction2364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModifier_in_ruleModifiers2405 = new BitSet(new long[]{0x0000000070800002L});
    public static final BitSet FOLLOW_set_in_ruleModifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternative_in_altList2481 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_OR_in_altList2484 = new BitSet(new long[]{0xAC90020800010010L,0x0000000000000011L});
    public static final BitSet FOLLOW_alternative_in_altList2486 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_altList_in_ruleBlock2521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elements_in_alternative2572 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elements2684 = new BitSet(new long[]{0xA880020800010012L,0x0000000000000011L});
    public static final BitSet FOLLOW_labeledElement_in_element2724 = new BitSet(new long[]{0x0006800000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element2776 = new BitSet(new long[]{0x0006800000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element2828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element2835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element2845 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_element2851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element2879 = new BitSet(new long[]{0x0006800000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_labeledElement2950 = new BitSet(new long[]{0x0008400000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement2953 = new BitSet(new long[]{0xA080020800000000L,0x0000000000000011L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement2956 = new BitSet(new long[]{0xA080020800000000L,0x0000000000000011L});
    public static final BitSet FOLLOW_atom_in_labeledElement2961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_labeledElement2963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec2981 = new BitSet(new long[]{0xA880020800010010L,0x0000000000000011L});
    public static final BitSet FOLLOW_element_in_treeSpec3022 = new BitSet(new long[]{0xA880020800010010L,0x0000000000000011L});
    public static final BitSet FOLLOW_element_in_treeSpec3053 = new BitSet(new long[]{0xA880060800010010L,0x0000000000000011L});
    public static final BitSet FOLLOW_RPAREN_in_treeSpec3062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_ebnf3096 = new BitSet(new long[]{0x0027880000000002L});
    public static final BitSet FOLLOW_blockSuffixe_in_ebnf3131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffixe3182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffixe3196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffixe3204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffixe3215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_ebnfSuffix3246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_ebnfSuffix3261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom3309 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_DOT_in_atom3311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleref_in_atom3313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom3333 = new BitSet(new long[]{0x0021000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_terminal_in_atom3353 = new BitSet(new long[]{0x0021000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleref_in_atom3374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_notSet_in_atom3382 = new BitSet(new long[]{0x0021000000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_atom3418 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_elementOptions_in_atom3420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet3478 = new BitSet(new long[]{0x8000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_terminal_in_notSet3480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_notSet3496 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_block_in_notSet3498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_block3531 = new BitSet(new long[]{0xBC90022900290010L,0x0000000000000011L});
    public static final BitSet FOLLOW_optionsSpec_in_block3568 = new BitSet(new long[]{0x1000002100280000L});
    public static final BitSet FOLLOW_ruleAction_in_block3573 = new BitSet(new long[]{0x1000002100280000L});
    public static final BitSet FOLLOW_COLON_in_block3576 = new BitSet(new long[]{0xAC90020800010010L,0x0000000000000011L});
    public static final BitSet FOLLOW_altList_in_block3590 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RPAREN_in_block3607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref3657 = new BitSet(new long[]{0x0021000000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref3659 = new BitSet(new long[]{0x0021000000000002L});
    public static final BitSet FOLLOW_ROOT_in_ruleref3669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_ruleref3673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range3747 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_RANGE_in_range3752 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_range3755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal3780 = new BitSet(new long[]{0x0000100000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal3782 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal3808 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_elementOptions3842 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions3844 = new BitSet(new long[]{0x0000208000000000L});
    public static final BitSet FOLLOW_COMMA_in_elementOptions3847 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions3849 = new BitSet(new long[]{0x0000208000000000L});
    public static final BitSet FOLLOW_GT_in_elementOptions3853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_elementOption3888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_elementOption3910 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption3912 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000011L});
    public static final BitSet FOLLOW_qid_in_elementOption3916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption3920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicatedRewrite_in_rewrite3938 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_nakedRewrite_in_rewrite3941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_predicatedRewrite3959 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite3961 = new BitSet(new long[]{0x8A40020800010000L,0x0000000000000011L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite3963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_nakedRewrite4003 = new BitSet(new long[]{0x8A40020800010000L,0x0000000000000011L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite4005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_rewriteAlt4069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteAlt4108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ETC_in_rewriteAlt4117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4148 = new BitSet(new long[]{0x8840020000010002L,0x0000000000000011L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4177 = new BitSet(new long[]{0x0006800000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTree_in_rewriteTreeElement4204 = new BitSet(new long[]{0x0006800000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom4265 = new BitSet(new long[]{0x0000100000004002L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom4267 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom4270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_rewriteTreeAtom4297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4304 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom4306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLAR_in_rewriteTreeAtom4329 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_rewriteTreeAtom4331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTreeAtom4342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTreeEbnf4368 = new BitSet(new long[]{0x8840020000010000L,0x0000000000000011L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4370 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTreeEbnf4372 = new BitSet(new long[]{0x0006800000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewriteTree4398 = new BitSet(new long[]{0x8040000000010000L,0x0000000000000011L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTree4400 = new BitSet(new long[]{0x8840060000010000L,0x0000000000000011L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTree4402 = new BitSet(new long[]{0x8840060000010000L,0x0000000000000011L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTree4405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate4437 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplate4439 = new BitSet(new long[]{0x8000040800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4441 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplate4443 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateRef_in_rewriteTemplate4483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplate4501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateRef4517 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplateRef4519 = new BitSet(new long[]{0x8000040800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4521 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplateRef4523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4552 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteIndirectTemplateHead4554 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4556 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4558 = new BitSet(new long[]{0x8000040800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4560 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4590 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_COMMA_in_rewriteTemplateArgs4593 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4595 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateArg4624 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_rewriteTemplateArg4626 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplateArg4628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_id4670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_id4683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_id4695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_qid4729 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_DOT_in_qid4732 = new BitSet(new long[]{0x8000000800000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_id_in_qid4734 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_alternative_in_alternativeEntry4751 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_alternativeEntry4753 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elementEntry4762 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_elementEntry4764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_ruleEntry4772 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_ruleEntry4774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_blockEntry4782 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_blockEntry4784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4108 = new BitSet(new long[]{0x0000000000000002L});

}