// $ANTLR 3.2.1-SNAPSHOT Jan 26, 2010 15:12:28 ANTLRParser.g 2010-02-12 16:46:36

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

/** The definitive ANTLR v3 grammar to parse ANTLR v4 grammars.
 *  The grammar builds ASTs that are sniffed by subsequent stages.
 */
public class ANTLRParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEMPRED", "FORCED_ACTION", "DOC_COMMENT", "SRC", "NLCHARS", "COMMENT", "DOUBLE_QUOTE_STRING_LITERAL", "DOUBLE_ANGLE_STRING_LITERAL", "ACTION_STRING_LITERAL", "ACTION_CHAR_LITERAL", "ARG_ACTION", "NESTED_ACTION", "ACTION", "ACTION_ESC", "WSNLCHARS", "OPTIONS", "TOKENS", "SCOPE", "IMPORT", "FRAGMENT", "LEXER", "PARSER", "TREE", "GRAMMAR", "PROTECTED", "PUBLIC", "PRIVATE", "RETURNS", "THROWS", "CATCH", "FINALLY", "TEMPLATE", "COLON", "COLONCOLON", "COMMA", "SEMI", "LPAREN", "RPAREN", "IMPLIES", "LT", "GT", "ASSIGN", "QUESTION", "BANG", "STAR", "PLUS", "PLUS_ASSIGN", "OR", "ROOT", "DOLLAR", "DOT", "RANGE", "ETC", "RARROW", "TREE_BEGIN", "AT", "NOT", "RBRACE", "TOKEN_REF", "RULE_REF", "INT", "WSCHARS", "ESC_SEQ", "STRING_LITERAL", "HEX_DIGIT", "UNICODE_ESC", "WS", "ERRCHAR", "RULE", "RULES", "RULEMODIFIERS", "RULEACTIONS", "BLOCK", "REWRITE_BLOCK", "OPTIONAL", "CLOSURE", "POSITIVE_CLOSURE", "SYNPRED", "CHAR_RANGE", "EPSILON", "ALT", "ALTLIST", "RESULT", "ID", "ARG", "ARGLIST", "RET", "COMBINED", "INITACTION", "LABEL", "GATED_SEMPRED", "SYN_SEMPRED", "BACKTRACK_SEMPRED", "WILDCARD", "LIST", "ELEMENT_OPTIONS", "ST_RESULT", "ALT_REWRITE"
    };
    public static final int LT=43;
    public static final int COMBINED=91;
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
    public static final int GRAMMAR=27;
    public static final int ACTION_CHAR_LITERAL=13;
    public static final int WSCHARS=65;
    public static final int RULEACTIONS=75;
    public static final int INITACTION=92;
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
    public static final int LABEL=93;
    public static final int TEMPLATE=35;
    public static final int SYN_SEMPRED=95;
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


    public static class grammarSpec_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "grammarSpec"
    // ANTLRParser.g:138:1: grammarSpec : ( DOC_COMMENT )? grammarType id SEMI ( prequelConstruct )* rules EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ) ;
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
            // ANTLRParser.g:139:5: ( ( DOC_COMMENT )? grammarType id SEMI ( prequelConstruct )* rules EOF -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules ) )
            // ANTLRParser.g:143:7: ( DOC_COMMENT )? grammarType id SEMI ( prequelConstruct )* rules EOF
            {
            // ANTLRParser.g:143:7: ( DOC_COMMENT )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==DOC_COMMENT) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // ANTLRParser.g:143:7: DOC_COMMENT
                    {
                    DOC_COMMENT1=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_grammarSpec459); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOC_COMMENT.add(DOC_COMMENT1);


                    }
                    break;

            }

            pushFollow(FOLLOW_grammarType_in_grammarSpec496);
            grammarType2=grammarType();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_grammarType.add(grammarType2.getTree());
            pushFollow(FOLLOW_id_in_grammarSpec498);
            id3=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id3.getTree());
            SEMI4=(Token)match(input,SEMI,FOLLOW_SEMI_in_grammarSpec500); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI4);

            // ANTLRParser.g:161:7: ( prequelConstruct )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=OPTIONS && LA2_0<=IMPORT)||LA2_0==AT) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ANTLRParser.g:161:7: prequelConstruct
            	    {
            	    pushFollow(FOLLOW_prequelConstruct_in_grammarSpec544);
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

            pushFollow(FOLLOW_rules_in_grammarSpec572);
            rules6=rules();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rules.add(rules6.getTree());
            EOF7=(Token)match(input,EOF,FOLLOW_EOF_in_grammarSpec615); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_EOF.add(EOF7);



            // AST REWRITE
            // elements: rules, DOC_COMMENT, id, grammarType, prequelConstruct
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 180:7: -> ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules )
            {
                // ANTLRParser.g:180:10: ^( grammarType id ( DOC_COMMENT )? ( prequelConstruct )* rules )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_grammarType.nextNode(), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:182:14: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:183:14: ( prequelConstruct )*
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
    // ANTLRParser.g:216:1: grammarType : (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] ) ;
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
            // ANTLRParser.g:221:5: ( (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] ) )
            // ANTLRParser.g:221:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] )
            {
            // ANTLRParser.g:221:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\"] | t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\"] | t= TREE g= GRAMMAR -> GRAMMAR[$g, \"TREE_GRAMMAR\"] | g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"] )
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
                    // ANTLRParser.g:221:9: t= LEXER g= GRAMMAR
                    {
                    t=(Token)match(input,LEXER,FOLLOW_LEXER_in_grammarType809); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType813); if (state.failed) return retval; 
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
                    // 221:28: -> GRAMMAR[$g, \"LEXER_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "LEXER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:223:6: t= PARSER g= GRAMMAR
                    {
                    t=(Token)match(input,PARSER,FOLLOW_PARSER_in_grammarType846); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType850); if (state.failed) return retval; 
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
                    // 223:25: -> GRAMMAR[$g, \"PARSER_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "PARSER_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:226:6: t= TREE g= GRAMMAR
                    {
                    t=(Token)match(input,TREE,FOLLOW_TREE_in_grammarType877); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TREE.add(t);

                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType881); if (state.failed) return retval; 
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
                    // 226:25: -> GRAMMAR[$g, \"TREE_GRAMMAR\"]
                    {
                        adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "TREE_GRAMMAR"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // ANTLRParser.g:229:6: g= GRAMMAR
                    {
                    g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType908); if (state.failed) return retval; 
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
                    // 229:25: -> GRAMMAR[$g, \"COMBINED_GRAMMAR\"]
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
    // ANTLRParser.g:236:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action );
    public final ANTLRParser.prequelConstruct_return prequelConstruct() throws RecognitionException {
        ANTLRParser.prequelConstruct_return retval = new ANTLRParser.prequelConstruct_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.optionsSpec_return optionsSpec8 = null;

        ANTLRParser.delegateGrammars_return delegateGrammars9 = null;

        ANTLRParser.tokensSpec_return tokensSpec10 = null;

        ANTLRParser.attrScope_return attrScope11 = null;

        ANTLRParser.action_return action12 = null;



        try {
            // ANTLRParser.g:237:2: ( optionsSpec | delegateGrammars | tokensSpec | attrScope | action )
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
                    // ANTLRParser.g:238:4: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_prequelConstruct972);
                    optionsSpec8=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec8.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:242:7: delegateGrammars
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct998);
                    delegateGrammars9=delegateGrammars();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, delegateGrammars9.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:249:7: tokensSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_tokensSpec_in_prequelConstruct1048);
                    tokensSpec10=tokensSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, tokensSpec10.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:254:7: attrScope
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_attrScope_in_prequelConstruct1084);
                    attrScope11=attrScope();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, attrScope11.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:260:7: action
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_action_in_prequelConstruct1127);
                    action12=action();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, action12.getTree());

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
    // ANTLRParser.g:264:1: optionsSpec : OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ ) ;
    public final ANTLRParser.optionsSpec_return optionsSpec() throws RecognitionException {
        ANTLRParser.optionsSpec_return retval = new ANTLRParser.optionsSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token OPTIONS13=null;
        Token SEMI15=null;
        Token RBRACE16=null;
        ANTLRParser.option_return option14 = null;


        GrammarAST OPTIONS13_tree=null;
        GrammarAST SEMI15_tree=null;
        GrammarAST RBRACE16_tree=null;
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_OPTIONS=new RewriteRuleTokenStream(adaptor,"token OPTIONS");
        RewriteRuleSubtreeStream stream_option=new RewriteRuleSubtreeStream(adaptor,"rule option");
        try {
            // ANTLRParser.g:265:2: ( OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ ) )
            // ANTLRParser.g:265:4: OPTIONS ( option SEMI )* RBRACE
            {
            OPTIONS13=(Token)match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec1144); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_OPTIONS.add(OPTIONS13);

            // ANTLRParser.g:265:12: ( option SEMI )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==TEMPLATE||(LA5_0>=TOKEN_REF && LA5_0<=RULE_REF)) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // ANTLRParser.g:265:13: option SEMI
            	    {
            	    pushFollow(FOLLOW_option_in_optionsSpec1147);
            	    option14=option();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_option.add(option14.getTree());
            	    SEMI15=(Token)match(input,SEMI,FOLLOW_SEMI_in_optionsSpec1149); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_SEMI.add(SEMI15);


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            RBRACE16=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_optionsSpec1153); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE16);



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
            // 265:34: -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ )
            {
                // ANTLRParser.g:265:37: ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )+ )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(OPTIONS, OPTIONS13, "OPTIONS"), root_1);

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
    // ANTLRParser.g:268:1: option : id ASSIGN optionValue ;
    public final ANTLRParser.option_return option() throws RecognitionException {
        ANTLRParser.option_return retval = new ANTLRParser.option_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN18=null;
        ANTLRParser.id_return id17 = null;

        ANTLRParser.optionValue_return optionValue19 = null;


        GrammarAST ASSIGN18_tree=null;

        try {
            // ANTLRParser.g:269:5: ( id ASSIGN optionValue )
            // ANTLRParser.g:269:9: id ASSIGN optionValue
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_option1190);
            id17=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id17.getTree());
            ASSIGN18=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_option1192); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ASSIGN18_tree = (GrammarAST)adaptor.create(ASSIGN18);
            root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN18_tree, root_0);
            }
            pushFollow(FOLLOW_optionValue_in_option1195);
            optionValue19=optionValue();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, optionValue19.getTree());

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
    // ANTLRParser.g:277:1: optionValue : ( qid | STRING_LITERAL | INT | STAR );
    public final ANTLRParser.optionValue_return optionValue() throws RecognitionException {
        ANTLRParser.optionValue_return retval = new ANTLRParser.optionValue_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token STRING_LITERAL21=null;
        Token INT22=null;
        Token STAR23=null;
        ANTLRParser.qid_return qid20 = null;


        GrammarAST STRING_LITERAL21_tree=null;
        GrammarAST INT22_tree=null;
        GrammarAST STAR23_tree=null;

        try {
            // ANTLRParser.g:278:5: ( qid | STRING_LITERAL | INT | STAR )
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
                    // ANTLRParser.g:282:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_optionValue1245);
                    qid20=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid20.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:286:7: STRING_LITERAL
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STRING_LITERAL21=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_optionValue1269); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING_LITERAL21_tree = new TerminalAST(STRING_LITERAL21) ;
                    adaptor.addChild(root_0, STRING_LITERAL21_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:290:7: INT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    INT22=(Token)match(input,INT,FOLLOW_INT_in_optionValue1295); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT22_tree = (GrammarAST)adaptor.create(INT22);
                    adaptor.addChild(root_0, INT22_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:294:7: STAR
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    STAR23=(Token)match(input,STAR,FOLLOW_STAR_in_optionValue1324); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STAR23_tree = (GrammarAST)adaptor.create(STAR23);
                    adaptor.addChild(root_0, STAR23_tree);
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
    // ANTLRParser.g:299:1: delegateGrammars : IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) ;
    public final ANTLRParser.delegateGrammars_return delegateGrammars() throws RecognitionException {
        ANTLRParser.delegateGrammars_return retval = new ANTLRParser.delegateGrammars_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token IMPORT24=null;
        Token COMMA26=null;
        Token SEMI28=null;
        ANTLRParser.delegateGrammar_return delegateGrammar25 = null;

        ANTLRParser.delegateGrammar_return delegateGrammar27 = null;


        GrammarAST IMPORT24_tree=null;
        GrammarAST COMMA26_tree=null;
        GrammarAST SEMI28_tree=null;
        RewriteRuleTokenStream stream_IMPORT=new RewriteRuleTokenStream(adaptor,"token IMPORT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleSubtreeStream stream_delegateGrammar=new RewriteRuleSubtreeStream(adaptor,"rule delegateGrammar");
        try {
            // ANTLRParser.g:300:2: ( IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) )
            // ANTLRParser.g:300:4: IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI
            {
            IMPORT24=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars1340); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_IMPORT.add(IMPORT24);

            pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1342);
            delegateGrammar25=delegateGrammar();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar25.getTree());
            // ANTLRParser.g:300:27: ( COMMA delegateGrammar )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==COMMA) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // ANTLRParser.g:300:28: COMMA delegateGrammar
            	    {
            	    COMMA26=(Token)match(input,COMMA,FOLLOW_COMMA_in_delegateGrammars1345); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA26);

            	    pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1347);
            	    delegateGrammar27=delegateGrammar();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_delegateGrammar.add(delegateGrammar27.getTree());

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            SEMI28=(Token)match(input,SEMI,FOLLOW_SEMI_in_delegateGrammars1351); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI28);



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
            // 300:57: -> ^( IMPORT ( delegateGrammar )+ )
            {
                // ANTLRParser.g:300:60: ^( IMPORT ( delegateGrammar )+ )
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
    // ANTLRParser.g:305:1: delegateGrammar : ( id ASSIGN id | id );
    public final ANTLRParser.delegateGrammar_return delegateGrammar() throws RecognitionException {
        ANTLRParser.delegateGrammar_return retval = new ANTLRParser.delegateGrammar_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN30=null;
        ANTLRParser.id_return id29 = null;

        ANTLRParser.id_return id31 = null;

        ANTLRParser.id_return id32 = null;


        GrammarAST ASSIGN30_tree=null;

        try {
            // ANTLRParser.g:306:5: ( id ASSIGN id | id )
            int alt8=2;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                int LA8_1 = input.LA(2);

                if ( (LA8_1==ASSIGN) ) {
                    alt8=1;
                }
                else if ( ((LA8_1>=COMMA && LA8_1<=SEMI)) ) {
                    alt8=2;
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

                if ( (LA8_2==ASSIGN) ) {
                    alt8=1;
                }
                else if ( ((LA8_2>=COMMA && LA8_2<=SEMI)) ) {
                    alt8=2;
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
                    // ANTLRParser.g:306:9: id ASSIGN id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1378);
                    id29=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id29.getTree());
                    ASSIGN30=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar1380); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN30_tree = (GrammarAST)adaptor.create(ASSIGN30);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN30_tree, root_0);
                    }
                    pushFollow(FOLLOW_id_in_delegateGrammar1383);
                    id31=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id31.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:307:9: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_delegateGrammar1393);
                    id32=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id32.getTree());

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
    // ANTLRParser.g:310:1: tokensSpec : TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) ;
    public final ANTLRParser.tokensSpec_return tokensSpec() throws RecognitionException {
        ANTLRParser.tokensSpec_return retval = new ANTLRParser.tokensSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKENS33=null;
        Token RBRACE35=null;
        ANTLRParser.tokenSpec_return tokenSpec34 = null;


        GrammarAST TOKENS33_tree=null;
        GrammarAST RBRACE35_tree=null;
        RewriteRuleTokenStream stream_TOKENS=new RewriteRuleTokenStream(adaptor,"token TOKENS");
        RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
        RewriteRuleSubtreeStream stream_tokenSpec=new RewriteRuleSubtreeStream(adaptor,"rule tokenSpec");
        try {
            // ANTLRParser.g:317:2: ( TOKENS ( tokenSpec )+ RBRACE -> ^( TOKENS ( tokenSpec )+ ) )
            // ANTLRParser.g:317:4: TOKENS ( tokenSpec )+ RBRACE
            {
            TOKENS33=(Token)match(input,TOKENS,FOLLOW_TOKENS_in_tokensSpec1409); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TOKENS.add(TOKENS33);

            // ANTLRParser.g:317:11: ( tokenSpec )+
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
            	    // ANTLRParser.g:317:11: tokenSpec
            	    {
            	    pushFollow(FOLLOW_tokenSpec_in_tokensSpec1411);
            	    tokenSpec34=tokenSpec();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_tokenSpec.add(tokenSpec34.getTree());

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

            RBRACE35=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_tokensSpec1414); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RBRACE.add(RBRACE35);



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
            // 317:29: -> ^( TOKENS ( tokenSpec )+ )
            {
                // ANTLRParser.g:317:32: ^( TOKENS ( tokenSpec )+ )
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
    // ANTLRParser.g:320:1: tokenSpec : ( id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI | RULE_REF );
    public final ANTLRParser.tokenSpec_return tokenSpec() throws RecognitionException {
        ANTLRParser.tokenSpec_return retval = new ANTLRParser.tokenSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN37=null;
        Token STRING_LITERAL38=null;
        Token SEMI39=null;
        Token RULE_REF40=null;
        ANTLRParser.id_return id36 = null;


        GrammarAST ASSIGN37_tree=null;
        GrammarAST STRING_LITERAL38_tree=null;
        GrammarAST SEMI39_tree=null;
        GrammarAST RULE_REF40_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:321:2: ( id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI | RULE_REF )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==RULE_REF) ) {
                int LA11_1 = input.LA(2);

                if ( (LA11_1==TEMPLATE||(LA11_1>=RBRACE && LA11_1<=RULE_REF)) ) {
                    alt11=2;
                }
                else if ( (LA11_1==SEMI||LA11_1==ASSIGN) ) {
                    alt11=1;
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
                    // ANTLRParser.g:321:4: id ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id ) SEMI
                    {
                    pushFollow(FOLLOW_id_in_tokenSpec1434);
                    id36=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id36.getTree());
                    // ANTLRParser.g:322:3: ( ASSIGN STRING_LITERAL -> ^( ASSIGN id STRING_LITERAL ) | -> id )
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
                            // ANTLRParser.g:322:5: ASSIGN STRING_LITERAL
                            {
                            ASSIGN37=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_tokenSpec1440); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN37);

                            STRING_LITERAL38=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_tokenSpec1442); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL38);



                            // AST REWRITE
                            // elements: STRING_LITERAL, id, ASSIGN
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 322:27: -> ^( ASSIGN id STRING_LITERAL )
                            {
                                // ANTLRParser.g:322:30: ^( ASSIGN id STRING_LITERAL )
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
                            // ANTLRParser.g:323:11: 
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
                            // 323:11: -> id
                            {
                                adaptor.addChild(root_0, stream_id.nextTree());

                            }

                            retval.tree = root_0;}
                            }
                            break;

                    }

                    SEMI39=(Token)match(input,SEMI,FOLLOW_SEMI_in_tokenSpec1477); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI39);


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:326:4: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF40=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_tokenSpec1482); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF40_tree = (GrammarAST)adaptor.create(RULE_REF40);
                    adaptor.addChild(root_0, RULE_REF40_tree);
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
    // ANTLRParser.g:332:1: attrScope : SCOPE id ACTION -> ^( SCOPE id ACTION ) ;
    public final ANTLRParser.attrScope_return attrScope() throws RecognitionException {
        ANTLRParser.attrScope_return retval = new ANTLRParser.attrScope_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token SCOPE41=null;
        Token ACTION43=null;
        ANTLRParser.id_return id42 = null;


        GrammarAST SCOPE41_tree=null;
        GrammarAST ACTION43_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:333:2: ( SCOPE id ACTION -> ^( SCOPE id ACTION ) )
            // ANTLRParser.g:333:4: SCOPE id ACTION
            {
            SCOPE41=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_attrScope1497); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE41);

            pushFollow(FOLLOW_id_in_attrScope1499);
            id42=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id42.getTree());
            ACTION43=(Token)match(input,ACTION,FOLLOW_ACTION_in_attrScope1501); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION43);



            // AST REWRITE
            // elements: ACTION, SCOPE, id
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 333:20: -> ^( SCOPE id ACTION )
            {
                // ANTLRParser.g:333:23: ^( SCOPE id ACTION )
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
    // ANTLRParser.g:339:1: action : AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) ;
    public final ANTLRParser.action_return action() throws RecognitionException {
        ANTLRParser.action_return retval = new ANTLRParser.action_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token AT44=null;
        Token COLONCOLON46=null;
        Token ACTION48=null;
        ANTLRParser.actionScopeName_return actionScopeName45 = null;

        ANTLRParser.id_return id47 = null;


        GrammarAST AT44_tree=null;
        GrammarAST COLONCOLON46_tree=null;
        GrammarAST ACTION48_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_COLONCOLON=new RewriteRuleTokenStream(adaptor,"token COLONCOLON");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_actionScopeName=new RewriteRuleSubtreeStream(adaptor,"rule actionScopeName");
        try {
            // ANTLRParser.g:341:2: ( AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) )
            // ANTLRParser.g:341:4: AT ( actionScopeName COLONCOLON )? id ACTION
            {
            AT44=(Token)match(input,AT,FOLLOW_AT_in_action1530); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT44);

            // ANTLRParser.g:341:7: ( actionScopeName COLONCOLON )?
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
                    // ANTLRParser.g:341:8: actionScopeName COLONCOLON
                    {
                    pushFollow(FOLLOW_actionScopeName_in_action1533);
                    actionScopeName45=actionScopeName();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_actionScopeName.add(actionScopeName45.getTree());
                    COLONCOLON46=(Token)match(input,COLONCOLON,FOLLOW_COLONCOLON_in_action1535); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLONCOLON.add(COLONCOLON46);


                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_action1539);
            id47=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id47.getTree());
            ACTION48=(Token)match(input,ACTION,FOLLOW_ACTION_in_action1541); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION48);



            // AST REWRITE
            // elements: AT, actionScopeName, id, ACTION
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 341:47: -> ^( AT ( actionScopeName )? id ACTION )
            {
                // ANTLRParser.g:341:50: ^( AT ( actionScopeName )? id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);

                // ANTLRParser.g:341:55: ( actionScopeName )?
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
    // ANTLRParser.g:344:1: actionScopeName : ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] );
    public final ANTLRParser.actionScopeName_return actionScopeName() throws RecognitionException {
        ANTLRParser.actionScopeName_return retval = new ANTLRParser.actionScopeName_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LEXER50=null;
        Token PARSER51=null;
        ANTLRParser.id_return id49 = null;


        GrammarAST LEXER50_tree=null;
        GrammarAST PARSER51_tree=null;
        RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
        RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");

        try {
            // ANTLRParser.g:348:2: ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] )
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
                    // ANTLRParser.g:348:4: id
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_actionScopeName1572);
                    id49=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id49.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:349:4: LEXER
                    {
                    LEXER50=(Token)match(input,LEXER,FOLLOW_LEXER_in_actionScopeName1577); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LEXER.add(LEXER50);



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
                    // 349:10: -> ID[$LEXER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, LEXER50));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:350:9: PARSER
                    {
                    PARSER51=(Token)match(input,PARSER,FOLLOW_PARSER_in_actionScopeName1592); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PARSER.add(PARSER51);



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
                    // 350:16: -> ID[$PARSER]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, PARSER51));

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
    // ANTLRParser.g:353:1: rules : ( rule )* -> ^( RULES ( rule )* ) ;
    public final ANTLRParser.rules_return rules() throws RecognitionException {
        ANTLRParser.rules_return retval = new ANTLRParser.rules_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rule_return rule52 = null;


        RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");
        try {
            // ANTLRParser.g:354:5: ( ( rule )* -> ^( RULES ( rule )* ) )
            // ANTLRParser.g:354:7: ( rule )*
            {
            // ANTLRParser.g:354:7: ( rule )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==DOC_COMMENT||LA14_0==FRAGMENT||(LA14_0>=PROTECTED && LA14_0<=PRIVATE)||LA14_0==TEMPLATE||(LA14_0>=TOKEN_REF && LA14_0<=RULE_REF)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ANTLRParser.g:354:7: rule
            	    {
            	    pushFollow(FOLLOW_rule_in_rules1611);
            	    rule52=rule();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rule.add(rule52.getTree());

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
            // 358:7: -> ^( RULES ( rule )* )
            {
                // ANTLRParser.g:358:9: ^( RULES ( rule )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULES, "RULES"), root_1);

                // ANTLRParser.g:358:17: ( rule )*
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
    // ANTLRParser.g:370:1: rule : ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* COLON altListAsBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* ) ;
    public final ANTLRParser.rule_return rule() throws RecognitionException {
        ANTLRParser.rule_return retval = new ANTLRParser.rule_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOC_COMMENT53=null;
        Token ARG_ACTION56=null;
        Token COLON59=null;
        Token SEMI61=null;
        ANTLRParser.ruleModifiers_return ruleModifiers54 = null;

        ANTLRParser.id_return id55 = null;

        ANTLRParser.ruleReturns_return ruleReturns57 = null;

        ANTLRParser.rulePrequel_return rulePrequel58 = null;

        ANTLRParser.altListAsBlock_return altListAsBlock60 = null;

        ANTLRParser.exceptionGroup_return exceptionGroup62 = null;


        GrammarAST DOC_COMMENT53_tree=null;
        GrammarAST ARG_ACTION56_tree=null;
        GrammarAST COLON59_tree=null;
        GrammarAST SEMI61_tree=null;
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
            // ANTLRParser.g:371:5: ( ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* COLON altListAsBlock SEMI exceptionGroup -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* ) )
            // ANTLRParser.g:372:7: ( DOC_COMMENT )? ( ruleModifiers )? id ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* COLON altListAsBlock SEMI exceptionGroup
            {
            // ANTLRParser.g:372:7: ( DOC_COMMENT )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==DOC_COMMENT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // ANTLRParser.g:372:7: DOC_COMMENT
                    {
                    DOC_COMMENT53=(Token)match(input,DOC_COMMENT,FOLLOW_DOC_COMMENT_in_rule1690); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOC_COMMENT.add(DOC_COMMENT53);


                    }
                    break;

            }

            // ANTLRParser.g:378:7: ( ruleModifiers )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==FRAGMENT||(LA16_0>=PROTECTED && LA16_0<=PRIVATE)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ANTLRParser.g:378:7: ruleModifiers
                    {
                    pushFollow(FOLLOW_ruleModifiers_in_rule1734);
                    ruleModifiers54=ruleModifiers();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleModifiers.add(ruleModifiers54.getTree());

                    }
                    break;

            }

            pushFollow(FOLLOW_id_in_rule1757);
            id55=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id55.getTree());
            // ANTLRParser.g:392:4: ( ARG_ACTION )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==ARG_ACTION) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ANTLRParser.g:392:4: ARG_ACTION
                    {
                    ARG_ACTION56=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule1790); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION56);


                    }
                    break;

            }

            // ANTLRParser.g:394:4: ( ruleReturns )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==RETURNS) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ANTLRParser.g:394:4: ruleReturns
                    {
                    pushFollow(FOLLOW_ruleReturns_in_rule1800);
                    ruleReturns57=ruleReturns();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleReturns.add(ruleReturns57.getTree());

                    }
                    break;

            }

            // ANTLRParser.g:409:7: ( rulePrequel )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==OPTIONS||LA19_0==SCOPE||LA19_0==THROWS||LA19_0==AT) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // ANTLRParser.g:409:7: rulePrequel
            	    {
            	    pushFollow(FOLLOW_rulePrequel_in_rule1838);
            	    rulePrequel58=rulePrequel();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rulePrequel.add(rulePrequel58.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);

            COLON59=(Token)match(input,COLON,FOLLOW_COLON_in_rule1854); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_COLON.add(COLON59);

            pushFollow(FOLLOW_altListAsBlock_in_rule1883);
            altListAsBlock60=altListAsBlock();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altListAsBlock.add(altListAsBlock60.getTree());
            SEMI61=(Token)match(input,SEMI,FOLLOW_SEMI_in_rule1898); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMI.add(SEMI61);

            pushFollow(FOLLOW_exceptionGroup_in_rule1907);
            exceptionGroup62=exceptionGroup();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_exceptionGroup.add(exceptionGroup62.getTree());


            // AST REWRITE
            // elements: exceptionGroup, DOC_COMMENT, id, ARG_ACTION, rulePrequel, altListAsBlock, ruleReturns, ruleModifiers
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 421:7: -> ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* )
            {
                // ANTLRParser.g:421:10: ^( RULE id ( DOC_COMMENT )? ( ruleModifiers )? ( ARG_ACTION )? ( ruleReturns )? ( rulePrequel )* altListAsBlock ( exceptionGroup )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new RuleAST(RULE), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:421:30: ( DOC_COMMENT )?
                if ( stream_DOC_COMMENT.hasNext() ) {
                    adaptor.addChild(root_1, stream_DOC_COMMENT.nextNode());

                }
                stream_DOC_COMMENT.reset();
                // ANTLRParser.g:421:43: ( ruleModifiers )?
                if ( stream_ruleModifiers.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleModifiers.nextTree());

                }
                stream_ruleModifiers.reset();
                // ANTLRParser.g:421:58: ( ARG_ACTION )?
                if ( stream_ARG_ACTION.hasNext() ) {
                    adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                }
                stream_ARG_ACTION.reset();
                // ANTLRParser.g:422:9: ( ruleReturns )?
                if ( stream_ruleReturns.hasNext() ) {
                    adaptor.addChild(root_1, stream_ruleReturns.nextTree());

                }
                stream_ruleReturns.reset();
                // ANTLRParser.g:422:22: ( rulePrequel )*
                while ( stream_rulePrequel.hasNext() ) {
                    adaptor.addChild(root_1, stream_rulePrequel.nextTree());

                }
                stream_rulePrequel.reset();
                adaptor.addChild(root_1, stream_altListAsBlock.nextTree());
                // ANTLRParser.g:422:50: ( exceptionGroup )*
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
    // ANTLRParser.g:432:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
    public final ANTLRParser.exceptionGroup_return exceptionGroup() throws RecognitionException {
        ANTLRParser.exceptionGroup_return retval = new ANTLRParser.exceptionGroup_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.exceptionHandler_return exceptionHandler63 = null;

        ANTLRParser.finallyClause_return finallyClause64 = null;



        try {
            // ANTLRParser.g:433:5: ( ( exceptionHandler )* ( finallyClause )? )
            // ANTLRParser.g:433:7: ( exceptionHandler )* ( finallyClause )?
            {
            root_0 = (GrammarAST)adaptor.nil();

            // ANTLRParser.g:433:7: ( exceptionHandler )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==CATCH) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ANTLRParser.g:433:7: exceptionHandler
            	    {
            	    pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup1999);
            	    exceptionHandler63=exceptionHandler();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, exceptionHandler63.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            // ANTLRParser.g:433:25: ( finallyClause )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==FINALLY) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // ANTLRParser.g:433:25: finallyClause
                    {
                    pushFollow(FOLLOW_finallyClause_in_exceptionGroup2002);
                    finallyClause64=finallyClause();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, finallyClause64.getTree());

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
    // ANTLRParser.g:438:1: exceptionHandler : CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) ;
    public final ANTLRParser.exceptionHandler_return exceptionHandler() throws RecognitionException {
        ANTLRParser.exceptionHandler_return retval = new ANTLRParser.exceptionHandler_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token CATCH65=null;
        Token ARG_ACTION66=null;
        Token ACTION67=null;

        GrammarAST CATCH65_tree=null;
        GrammarAST ARG_ACTION66_tree=null;
        GrammarAST ACTION67_tree=null;
        RewriteRuleTokenStream stream_CATCH=new RewriteRuleTokenStream(adaptor,"token CATCH");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

        try {
            // ANTLRParser.g:439:2: ( CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) )
            // ANTLRParser.g:439:4: CATCH ARG_ACTION ACTION
            {
            CATCH65=(Token)match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler2019); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_CATCH.add(CATCH65);

            ARG_ACTION66=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler2021); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION66);

            ACTION67=(Token)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler2023); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION67);



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
            // 439:28: -> ^( CATCH ARG_ACTION ACTION )
            {
                // ANTLRParser.g:439:31: ^( CATCH ARG_ACTION ACTION )
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
    // ANTLRParser.g:444:1: finallyClause : FINALLY ACTION -> ^( FINALLY ACTION ) ;
    public final ANTLRParser.finallyClause_return finallyClause() throws RecognitionException {
        ANTLRParser.finallyClause_return retval = new ANTLRParser.finallyClause_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token FINALLY68=null;
        Token ACTION69=null;

        GrammarAST FINALLY68_tree=null;
        GrammarAST ACTION69_tree=null;
        RewriteRuleTokenStream stream_FINALLY=new RewriteRuleTokenStream(adaptor,"token FINALLY");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");

        try {
            // ANTLRParser.g:445:2: ( FINALLY ACTION -> ^( FINALLY ACTION ) )
            // ANTLRParser.g:445:4: FINALLY ACTION
            {
            FINALLY68=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause2049); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_FINALLY.add(FINALLY68);

            ACTION69=(Token)match(input,ACTION,FOLLOW_ACTION_in_finallyClause2051); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION69);



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
            // 445:19: -> ^( FINALLY ACTION )
            {
                // ANTLRParser.g:445:22: ^( FINALLY ACTION )
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

        ANTLRParser.throwsSpec_return throwsSpec70 = null;

        ANTLRParser.ruleScopeSpec_return ruleScopeSpec71 = null;

        ANTLRParser.optionsSpec_return optionsSpec72 = null;

        ANTLRParser.ruleAction_return ruleAction73 = null;



        try {
            // ANTLRParser.g:452:5: ( throwsSpec | ruleScopeSpec | optionsSpec | ruleAction )
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
                    // ANTLRParser.g:452:7: throwsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_throwsSpec_in_rulePrequel2081);
                    throwsSpec70=throwsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, throwsSpec70.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:453:7: ruleScopeSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleScopeSpec_in_rulePrequel2089);
                    ruleScopeSpec71=ruleScopeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleScopeSpec71.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:454:7: optionsSpec
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_optionsSpec_in_rulePrequel2097);
                    optionsSpec72=optionsSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, optionsSpec72.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:455:7: ruleAction
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleAction_in_rulePrequel2105);
                    ruleAction73=ruleAction();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleAction73.getTree());

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

        Token RETURNS74=null;
        Token ARG_ACTION75=null;

        GrammarAST RETURNS74_tree=null;
        GrammarAST ARG_ACTION75_tree=null;

        try {
            // ANTLRParser.g:465:2: ( RETURNS ARG_ACTION )
            // ANTLRParser.g:465:4: RETURNS ARG_ACTION
            {
            root_0 = (GrammarAST)adaptor.nil();

            RETURNS74=(Token)match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns2125); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RETURNS74_tree = (GrammarAST)adaptor.create(RETURNS74);
            root_0 = (GrammarAST)adaptor.becomeRoot(RETURNS74_tree, root_0);
            }
            ARG_ACTION75=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns2128); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ARG_ACTION75_tree = (GrammarAST)adaptor.create(ARG_ACTION75);
            adaptor.addChild(root_0, ARG_ACTION75_tree);
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

        Token THROWS76=null;
        Token COMMA78=null;
        ANTLRParser.qid_return qid77 = null;

        ANTLRParser.qid_return qid79 = null;


        GrammarAST THROWS76_tree=null;
        GrammarAST COMMA78_tree=null;
        RewriteRuleTokenStream stream_THROWS=new RewriteRuleTokenStream(adaptor,"token THROWS");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");
        try {
            // ANTLRParser.g:480:5: ( THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) )
            // ANTLRParser.g:480:7: THROWS qid ( COMMA qid )*
            {
            THROWS76=(Token)match(input,THROWS,FOLLOW_THROWS_in_throwsSpec2153); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_THROWS.add(THROWS76);

            pushFollow(FOLLOW_qid_in_throwsSpec2155);
            qid77=qid();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_qid.add(qid77.getTree());
            // ANTLRParser.g:480:18: ( COMMA qid )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==COMMA) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // ANTLRParser.g:480:19: COMMA qid
            	    {
            	    COMMA78=(Token)match(input,COMMA,FOLLOW_COMMA_in_throwsSpec2158); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA78);

            	    pushFollow(FOLLOW_qid_in_throwsSpec2160);
            	    qid79=qid();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_qid.add(qid79.getTree());

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

        Token SCOPE80=null;
        Token ACTION81=null;
        Token SCOPE82=null;
        Token COMMA84=null;
        Token SEMI86=null;
        ANTLRParser.id_return id83 = null;

        ANTLRParser.id_return id85 = null;


        GrammarAST SCOPE80_tree=null;
        GrammarAST ACTION81_tree=null;
        GrammarAST SCOPE82_tree=null;
        GrammarAST COMMA84_tree=null;
        GrammarAST SEMI86_tree=null;
        RewriteRuleTokenStream stream_SCOPE=new RewriteRuleTokenStream(adaptor,"token SCOPE");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:488:2: ( SCOPE ACTION -> ^( SCOPE ACTION ) | SCOPE id ( COMMA id )* SEMI -> ^( SCOPE ( id )+ ) )
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
                    // ANTLRParser.g:488:4: SCOPE ACTION
                    {
                    SCOPE80=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2191); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE80);

                    ACTION81=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleScopeSpec2193); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ACTION.add(ACTION81);



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
                    SCOPE82=(Token)match(input,SCOPE,FOLLOW_SCOPE_in_ruleScopeSpec2206); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SCOPE.add(SCOPE82);

                    pushFollow(FOLLOW_id_in_ruleScopeSpec2208);
                    id83=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id83.getTree());
                    // ANTLRParser.g:489:13: ( COMMA id )*
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( (LA24_0==COMMA) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // ANTLRParser.g:489:14: COMMA id
                    	    {
                    	    COMMA84=(Token)match(input,COMMA,FOLLOW_COMMA_in_ruleScopeSpec2211); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA84);

                    	    pushFollow(FOLLOW_id_in_ruleScopeSpec2213);
                    	    id85=id();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_id.add(id85.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop24;
                        }
                    } while (true);

                    SEMI86=(Token)match(input,SEMI,FOLLOW_SEMI_in_ruleScopeSpec2217); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMI.add(SEMI86);



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

        Token AT87=null;
        Token ACTION89=null;
        ANTLRParser.id_return id88 = null;


        GrammarAST AT87_tree=null;
        GrammarAST ACTION89_tree=null;
        RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:502:2: ( AT id ACTION -> ^( AT id ACTION ) )
            // ANTLRParser.g:502:4: AT id ACTION
            {
            AT87=(Token)match(input,AT,FOLLOW_AT_in_ruleAction2247); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_AT.add(AT87);

            pushFollow(FOLLOW_id_in_ruleAction2249);
            id88=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id88.getTree());
            ACTION89=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleAction2251); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION89);



            // AST REWRITE
            // elements: AT, ACTION, id
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

        ANTLRParser.ruleModifier_return ruleModifier90 = null;


        RewriteRuleSubtreeStream stream_ruleModifier=new RewriteRuleSubtreeStream(adaptor,"rule ruleModifier");
        try {
            // ANTLRParser.g:511:5: ( ( ruleModifier )+ -> ^( RULEMODIFIERS ( ruleModifier )+ ) )
            // ANTLRParser.g:511:7: ( ruleModifier )+
            {
            // ANTLRParser.g:511:7: ( ruleModifier )+
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
            	    // ANTLRParser.g:511:7: ruleModifier
            	    {
            	    pushFollow(FOLLOW_ruleModifier_in_ruleModifiers2292);
            	    ruleModifier90=ruleModifier();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_ruleModifier.add(ruleModifier90.getTree());

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

        Token set91=null;

        GrammarAST set91_tree=null;

        try {
            // ANTLRParser.g:521:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set91=(Token)input.LT(1);
            if ( input.LA(1)==FRAGMENT||(input.LA(1)>=PROTECTED && input.LA(1)<=PRIVATE) ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set91));
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

        Token OR93=null;
        ANTLRParser.alternative_return alternative92 = null;

        ANTLRParser.alternative_return alternative94 = null;


        GrammarAST OR93_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");
        try {
            // ANTLRParser.g:528:5: ( alternative ( OR alternative )* -> ( alternative )+ )
            // ANTLRParser.g:528:7: alternative ( OR alternative )*
            {
            pushFollow(FOLLOW_alternative_in_altList2368);
            alternative92=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_alternative.add(alternative92.getTree());
            // ANTLRParser.g:528:19: ( OR alternative )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==OR) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ANTLRParser.g:528:20: OR alternative
            	    {
            	    OR93=(Token)match(input,OR,FOLLOW_OR_in_altList2371); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_OR.add(OR93);

            	    pushFollow(FOLLOW_alternative_in_altList2373);
            	    alternative94=alternative();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_alternative.add(alternative94.getTree());

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

    public static class altListAsBlock_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "altListAsBlock"
    // ANTLRParser.g:537:1: altListAsBlock : altList -> ^( BLOCK altList ) ;
    public final ANTLRParser.altListAsBlock_return altListAsBlock() throws RecognitionException {
        ANTLRParser.altListAsBlock_return retval = new ANTLRParser.altListAsBlock_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.altList_return altList95 = null;


        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        try {
            // ANTLRParser.g:538:5: ( altList -> ^( BLOCK altList ) )
            // ANTLRParser.g:538:7: altList
            {
            pushFollow(FOLLOW_altList_in_altListAsBlock2403);
            altList95=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList95.getTree());


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
            // 538:15: -> ^( BLOCK altList )
            {
                // ANTLRParser.g:538:18: ^( BLOCK altList )
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
    // ANTLRParser.g:543:1: alternative : ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) );
    public final ANTLRParser.alternative_return alternative() throws RecognitionException {
        ANTLRParser.alternative_return retval = new ANTLRParser.alternative_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.elements_return elements96 = null;

        ANTLRParser.rewrite_return rewrite97 = null;

        ANTLRParser.rewrite_return rewrite98 = null;


        RewriteRuleSubtreeStream stream_rewrite=new RewriteRuleSubtreeStream(adaptor,"rule rewrite");
        RewriteRuleSubtreeStream stream_elements=new RewriteRuleSubtreeStream(adaptor,"rule elements");
        try {
            // ANTLRParser.g:544:5: ( elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements ) | rewrite -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite ) | -> ^( ALT EPSILON ) )
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
                    // ANTLRParser.g:544:7: elements ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
                    {
                    pushFollow(FOLLOW_elements_in_alternative2433);
                    elements96=elements();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_elements.add(elements96.getTree());
                    // ANTLRParser.g:545:6: ( rewrite -> ^( ALT_REWRITE elements rewrite ) | -> elements )
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
                            // ANTLRParser.g:545:8: rewrite
                            {
                            pushFollow(FOLLOW_rewrite_in_alternative2442);
                            rewrite97=rewrite();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_rewrite.add(rewrite97.getTree());


                            // AST REWRITE
                            // elements: elements, rewrite
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 545:16: -> ^( ALT_REWRITE elements rewrite )
                            {
                                // ANTLRParser.g:545:19: ^( ALT_REWRITE elements rewrite )
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
                            // ANTLRParser.g:546:10: 
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
                            // 546:10: -> elements
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
                    // ANTLRParser.g:548:7: rewrite
                    {
                    pushFollow(FOLLOW_rewrite_in_alternative2480);
                    rewrite98=rewrite();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewrite.add(rewrite98.getTree());


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
                    // 548:16: -> ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                    {
                        // ANTLRParser.g:548:19: ^( ALT_REWRITE ^( ALT EPSILON ) rewrite )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ALT_REWRITE, "ALT_REWRITE"), root_1);

                        // ANTLRParser.g:548:33: ^( ALT EPSILON )
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
                    // ANTLRParser.g:549:10: 
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
                    // 549:10: -> ^( ALT EPSILON )
                    {
                        // ANTLRParser.g:549:13: ^( ALT EPSILON )
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
    // ANTLRParser.g:552:1: elements : (e+= element )+ -> ^( ALT ( $e)+ ) ;
    public final ANTLRParser.elements_return elements() throws RecognitionException {
        ANTLRParser.elements_return retval = new ANTLRParser.elements_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        List list_e=null;
        RuleReturnScope e = null;
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:553:5: ( (e+= element )+ -> ^( ALT ( $e)+ ) )
            // ANTLRParser.g:553:7: (e+= element )+
            {
            // ANTLRParser.g:553:8: (e+= element )+
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
            	    // ANTLRParser.g:553:8: e+= element
            	    {
            	    pushFollow(FOLLOW_element_in_elements2533);
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
            // 553:19: -> ^( ALT ( $e)+ )
            {
                // ANTLRParser.g:553:22: ^( ALT ( $e)+ )
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
    // ANTLRParser.g:556:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec ) );
    public final ANTLRParser.element_return element() throws RecognitionException {
        ANTLRParser.element_return retval = new ANTLRParser.element_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ACTION104=null;
        Token SEMPRED105=null;
        Token IMPLIES106=null;
        ANTLRParser.labeledElement_return labeledElement99 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix100 = null;

        ANTLRParser.atom_return atom101 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix102 = null;

        ANTLRParser.ebnf_return ebnf103 = null;

        ANTLRParser.treeSpec_return treeSpec107 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix108 = null;


        GrammarAST ACTION104_tree=null;
        GrammarAST SEMPRED105_tree=null;
        GrammarAST IMPLIES106_tree=null;
        RewriteRuleTokenStream stream_IMPLIES=new RewriteRuleTokenStream(adaptor,"token IMPLIES");
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        RewriteRuleSubtreeStream stream_treeSpec=new RewriteRuleSubtreeStream(adaptor,"rule treeSpec");
        RewriteRuleSubtreeStream stream_labeledElement=new RewriteRuleSubtreeStream(adaptor,"rule labeledElement");
        try {
            // ANTLRParser.g:557:2: ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec ) )
            int alt35=6;
            alt35 = dfa35.predict(input);
            switch (alt35) {
                case 1 :
                    // ANTLRParser.g:557:4: labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement )
                    {
                    pushFollow(FOLLOW_labeledElement_in_element2560);
                    labeledElement99=labeledElement();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_labeledElement.add(labeledElement99.getTree());
                    // ANTLRParser.g:558:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement )
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
                            // ANTLRParser.g:558:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2566);
                            ebnfSuffix100=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix100.getTree());


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
                            // 558:16: -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) )
                            {
                                // ANTLRParser.g:558:19: ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:558:33: ^( BLOCK ^( ALT labeledElement ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_2);

                                // ANTLRParser.g:558:51: ^( ALT labeledElement )
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
                            // ANTLRParser.g:559:8: 
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
                            // 559:8: -> labeledElement
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
                    // ANTLRParser.g:561:4: atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom )
                    {
                    pushFollow(FOLLOW_atom_in_element2608);
                    atom101=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_atom.add(atom101.getTree());
                    // ANTLRParser.g:562:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom )
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
                            // ANTLRParser.g:562:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2614);
                            ebnfSuffix102=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix102.getTree());


                            // AST REWRITE
                            // elements: ebnfSuffix, atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            if ( state.backtracking==0 ) {
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (GrammarAST)adaptor.nil();
                            // 562:16: -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) )
                            {
                                // ANTLRParser.g:562:19: ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:562:33: ^( BLOCK ^( ALT atom ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_2);

                                // ANTLRParser.g:562:51: ^( ALT atom )
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
                            // ANTLRParser.g:563:8: 
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
                            // 563:8: -> atom
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
                    // ANTLRParser.g:565:4: ebnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnf_in_element2657);
                    ebnf103=ebnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnf103.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:566:6: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION104=(Token)match(input,ACTION,FOLLOW_ACTION_in_element2664); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION104_tree = new ActionAST(ACTION104) ;
                    adaptor.addChild(root_0, ACTION104_tree);
                    }

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:567:6: SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED )
                    {
                    SEMPRED105=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_element2674); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED105);

                    // ANTLRParser.g:568:3: ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED )
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
                            // ANTLRParser.g:568:5: IMPLIES
                            {
                            IMPLIES106=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_element2680); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_IMPLIES.add(IMPLIES106);



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
                            // 568:14: -> GATED_SEMPRED[$IMPLIES]
                            {
                                adaptor.addChild(root_0, (GrammarAST)adaptor.create(GATED_SEMPRED, IMPLIES106));

                            }

                            retval.tree = root_0;}
                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:569:8: 
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
                            // 569:8: -> SEMPRED
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
                    // ANTLRParser.g:571:6: treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec )
                    {
                    pushFollow(FOLLOW_treeSpec_in_element2708);
                    treeSpec107=treeSpec();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_treeSpec.add(treeSpec107.getTree());
                    // ANTLRParser.g:572:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec )
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
                            // ANTLRParser.g:572:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_element2714);
                            ebnfSuffix108=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix108.getTree());


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
                            // 572:16: -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) )
                            {
                                // ANTLRParser.g:572:19: ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:572:33: ^( BLOCK ^( ALT treeSpec ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_2);

                                // ANTLRParser.g:572:51: ^( ALT treeSpec )
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
                            // ANTLRParser.g:573:8: 
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
                            // 573:8: -> treeSpec
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
    // ANTLRParser.g:577:1: labeledElement : id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) ;
    public final ANTLRParser.labeledElement_return labeledElement() throws RecognitionException {
        ANTLRParser.labeledElement_return retval = new ANTLRParser.labeledElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN110=null;
        Token PLUS_ASSIGN111=null;
        ANTLRParser.id_return id109 = null;

        ANTLRParser.atom_return atom112 = null;

        ANTLRParser.block_return block113 = null;


        GrammarAST ASSIGN110_tree=null;
        GrammarAST PLUS_ASSIGN111_tree=null;

        try {
            // ANTLRParser.g:577:16: ( id ( ASSIGN | PLUS_ASSIGN ) ( atom | block ) )
            // ANTLRParser.g:577:18: id ( ASSIGN | PLUS_ASSIGN ) ( atom | block )
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_id_in_labeledElement2763);
            id109=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, id109.getTree());
            // ANTLRParser.g:577:21: ( ASSIGN | PLUS_ASSIGN )
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
                    // ANTLRParser.g:577:22: ASSIGN
                    {
                    ASSIGN110=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement2766); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN110_tree = (GrammarAST)adaptor.create(ASSIGN110);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN110_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:577:30: PLUS_ASSIGN
                    {
                    PLUS_ASSIGN111=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement2769); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    PLUS_ASSIGN111_tree = (GrammarAST)adaptor.create(PLUS_ASSIGN111);
                    root_0 = (GrammarAST)adaptor.becomeRoot(PLUS_ASSIGN111_tree, root_0);
                    }

                    }
                    break;

            }

            // ANTLRParser.g:577:44: ( atom | block )
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
                    // ANTLRParser.g:577:45: atom
                    {
                    pushFollow(FOLLOW_atom_in_labeledElement2774);
                    atom112=atom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, atom112.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:577:50: block
                    {
                    pushFollow(FOLLOW_block_in_labeledElement2776);
                    block113=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, block113.getTree());

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
    // ANTLRParser.g:583:1: treeSpec : TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) ;
    public final ANTLRParser.treeSpec_return treeSpec() throws RecognitionException {
        ANTLRParser.treeSpec_return retval = new ANTLRParser.treeSpec_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN114=null;
        Token RPAREN117=null;
        ANTLRParser.element_return element115 = null;

        ANTLRParser.element_return element116 = null;


        GrammarAST TREE_BEGIN114_tree=null;
        GrammarAST RPAREN117_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
        try {
            // ANTLRParser.g:584:5: ( TREE_BEGIN element ( element )+ RPAREN -> ^( TREE_BEGIN ( element )+ ) )
            // ANTLRParser.g:584:7: TREE_BEGIN element ( element )+ RPAREN
            {
            TREE_BEGIN114=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_treeSpec2798); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN114);

            pushFollow(FOLLOW_element_in_treeSpec2839);
            element115=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_element.add(element115.getTree());
            // ANTLRParser.g:591:10: ( element )+
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
            	    // ANTLRParser.g:591:10: element
            	    {
            	    pushFollow(FOLLOW_element_in_treeSpec2870);
            	    element116=element();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_element.add(element116.getTree());

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

            RPAREN117=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_treeSpec2879); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN117);



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
            // 593:7: -> ^( TREE_BEGIN ( element )+ )
            {
                // ANTLRParser.g:593:10: ^( TREE_BEGIN ( element )+ )
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
    // ANTLRParser.g:598:1: ebnf : block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) ;
    public final ANTLRParser.ebnf_return ebnf() throws RecognitionException {
        ANTLRParser.ebnf_return retval = new ANTLRParser.ebnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.block_return block118 = null;

        ANTLRParser.blockSuffixe_return blockSuffixe119 = null;


        RewriteRuleSubtreeStream stream_blockSuffixe=new RewriteRuleSubtreeStream(adaptor,"rule blockSuffixe");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:599:5: ( block ( blockSuffixe -> ^( blockSuffixe block ) | -> block ) )
            // ANTLRParser.g:599:7: block ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
            {
            pushFollow(FOLLOW_block_in_ebnf2913);
            block118=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_block.add(block118.getTree());
            // ANTLRParser.g:603:7: ( blockSuffixe -> ^( blockSuffixe block ) | -> block )
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
                    // ANTLRParser.g:603:9: blockSuffixe
                    {
                    pushFollow(FOLLOW_blockSuffixe_in_ebnf2948);
                    blockSuffixe119=blockSuffixe();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_blockSuffixe.add(blockSuffixe119.getTree());


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
                    // 603:22: -> ^( blockSuffixe block )
                    {
                        // ANTLRParser.g:603:25: ^( blockSuffixe block )
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
                    // ANTLRParser.g:604:13: 
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
                    // 604:13: -> block
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
    // ANTLRParser.g:610:1: blockSuffixe : ( ebnfSuffix | ROOT | IMPLIES | BANG );
    public final ANTLRParser.blockSuffixe_return blockSuffixe() throws RecognitionException {
        ANTLRParser.blockSuffixe_return retval = new ANTLRParser.blockSuffixe_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ROOT121=null;
        Token IMPLIES122=null;
        Token BANG123=null;
        ANTLRParser.ebnfSuffix_return ebnfSuffix120 = null;


        GrammarAST ROOT121_tree=null;
        GrammarAST IMPLIES122_tree=null;
        GrammarAST BANG123_tree=null;

        try {
            // ANTLRParser.g:611:5: ( ebnfSuffix | ROOT | IMPLIES | BANG )
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
                    // ANTLRParser.g:611:7: ebnfSuffix
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ebnfSuffix_in_blockSuffixe2999);
                    ebnfSuffix120=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ebnfSuffix120.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:614:7: ROOT
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ROOT121=(Token)match(input,ROOT,FOLLOW_ROOT_in_blockSuffixe3013); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ROOT121_tree = (GrammarAST)adaptor.create(ROOT121);
                    adaptor.addChild(root_0, ROOT121_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:615:7: IMPLIES
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    IMPLIES122=(Token)match(input,IMPLIES,FOLLOW_IMPLIES_in_blockSuffixe3021); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    IMPLIES122_tree = (GrammarAST)adaptor.create(IMPLIES122);
                    adaptor.addChild(root_0, IMPLIES122_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:616:7: BANG
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    BANG123=(Token)match(input,BANG,FOLLOW_BANG_in_blockSuffixe3032); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    BANG123_tree = (GrammarAST)adaptor.create(BANG123);
                    adaptor.addChild(root_0, BANG123_tree);
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
    // ANTLRParser.g:619:1: ebnfSuffix : ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] );
    public final ANTLRParser.ebnfSuffix_return ebnfSuffix() throws RecognitionException {
        ANTLRParser.ebnfSuffix_return retval = new ANTLRParser.ebnfSuffix_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token QUESTION124=null;
        Token STAR125=null;
        Token PLUS126=null;

        GrammarAST QUESTION124_tree=null;
        GrammarAST STAR125_tree=null;
        GrammarAST PLUS126_tree=null;
        RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
        RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
        RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");


        	Token op = input.LT(1);

        try {
            // ANTLRParser.g:623:2: ( QUESTION -> OPTIONAL[op] | STAR -> CLOSURE[op] | PLUS -> POSITIVE_CLOSURE[op] )
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
                    // ANTLRParser.g:623:4: QUESTION
                    {
                    QUESTION124=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3051); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_QUESTION.add(QUESTION124);



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
                    // 623:13: -> OPTIONAL[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(OPTIONAL, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:624:6: STAR
                    {
                    STAR125=(Token)match(input,STAR,FOLLOW_STAR_in_ebnfSuffix3063); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STAR.add(STAR125);



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
                    // 624:13: -> CLOSURE[op]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(CLOSURE, op));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:625:7: PLUS
                    {
                    PLUS126=(Token)match(input,PLUS,FOLLOW_PLUS_in_ebnfSuffix3078); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_PLUS.add(PLUS126);



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
                    // 625:14: -> POSITIVE_CLOSURE[op]
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
    // ANTLRParser.g:628:1: atom : ( range ( ROOT | BANG )? | {...}? id DOT ruleref -> ^( DOT id ruleref ) | {...}? id DOT terminal -> ^( DOT id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? );
    public final ANTLRParser.atom_return atom() throws RecognitionException {
        ANTLRParser.atom_return retval = new ANTLRParser.atom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ROOT128=null;
        Token BANG129=null;
        Token DOT131=null;
        Token DOT134=null;
        Token ROOT139=null;
        Token BANG140=null;
        ANTLRParser.range_return range127 = null;

        ANTLRParser.id_return id130 = null;

        ANTLRParser.ruleref_return ruleref132 = null;

        ANTLRParser.id_return id133 = null;

        ANTLRParser.terminal_return terminal135 = null;

        ANTLRParser.terminal_return terminal136 = null;

        ANTLRParser.ruleref_return ruleref137 = null;

        ANTLRParser.notSet_return notSet138 = null;


        GrammarAST ROOT128_tree=null;
        GrammarAST BANG129_tree=null;
        GrammarAST DOT131_tree=null;
        GrammarAST DOT134_tree=null;
        GrammarAST ROOT139_tree=null;
        GrammarAST BANG140_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_terminal=new RewriteRuleSubtreeStream(adaptor,"rule terminal");
        RewriteRuleSubtreeStream stream_ruleref=new RewriteRuleSubtreeStream(adaptor,"rule ruleref");
        try {
            // ANTLRParser.g:628:5: ( range ( ROOT | BANG )? | {...}? id DOT ruleref -> ^( DOT id ruleref ) | {...}? id DOT terminal -> ^( DOT id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? )
            int alt44=6;
            alt44 = dfa44.predict(input);
            switch (alt44) {
                case 1 :
                    // ANTLRParser.g:628:7: range ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_range_in_atom3095);
                    range127=range();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, range127.getTree());
                    // ANTLRParser.g:628:13: ( ROOT | BANG )?
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
                            // ANTLRParser.g:628:14: ROOT
                            {
                            ROOT128=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3098); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT128_tree = (GrammarAST)adaptor.create(ROOT128);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT128_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:628:22: BANG
                            {
                            BANG129=(Token)match(input,BANG,FOLLOW_BANG_in_atom3103); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG129_tree = (GrammarAST)adaptor.create(BANG129);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG129_tree, root_0);
                            }

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // ANTLRParser.g:634:6: {...}? id DOT ruleref
                    {
                    if ( !((
                    	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                    	        input.LT(2).getCharPositionInLine() &&
                    	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                    	    )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atom", "\n\t    \tinput.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==\n\t        input.LT(2).getCharPositionInLine() &&\n\t        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()\n\t    ");
                    }
                    pushFollow(FOLLOW_id_in_atom3150);
                    id130=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id130.getTree());
                    DOT131=(Token)match(input,DOT,FOLLOW_DOT_in_atom3152); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT131);

                    pushFollow(FOLLOW_ruleref_in_atom3154);
                    ruleref132=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ruleref.add(ruleref132.getTree());


                    // AST REWRITE
                    // elements: ruleref, DOT, id
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 640:6: -> ^( DOT id ruleref )
                    {
                        // ANTLRParser.g:640:9: ^( DOT id ruleref )
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
                    // ANTLRParser.g:642:6: {...}? id DOT terminal
                    {
                    if ( !((
                    	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                    	        input.LT(2).getCharPositionInLine() &&
                    	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                    	    )) ) {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        throw new FailedPredicateException(input, "atom", "\n\t    \tinput.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==\n\t        input.LT(2).getCharPositionInLine() &&\n\t        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()\n\t    ");
                    }
                    pushFollow(FOLLOW_id_in_atom3188);
                    id133=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id133.getTree());
                    DOT134=(Token)match(input,DOT,FOLLOW_DOT_in_atom3190); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT134);

                    pushFollow(FOLLOW_terminal_in_atom3192);
                    terminal135=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_terminal.add(terminal135.getTree());


                    // AST REWRITE
                    // elements: terminal, id, DOT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 648:6: -> ^( DOT id terminal )
                    {
                        // ANTLRParser.g:648:9: ^( DOT id terminal )
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
                    // ANTLRParser.g:649:9: terminal
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_terminal_in_atom3217);
                    terminal136=terminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, terminal136.getTree());

                    }
                    break;
                case 5 :
                    // ANTLRParser.g:650:9: ruleref
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_ruleref_in_atom3227);
                    ruleref137=ruleref();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, ruleref137.getTree());

                    }
                    break;
                case 6 :
                    // ANTLRParser.g:651:7: notSet ( ROOT | BANG )?
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_notSet_in_atom3235);
                    notSet138=notSet();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, notSet138.getTree());
                    // ANTLRParser.g:651:14: ( ROOT | BANG )?
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
                            // ANTLRParser.g:651:15: ROOT
                            {
                            ROOT139=(Token)match(input,ROOT,FOLLOW_ROOT_in_atom3238); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            ROOT139_tree = (GrammarAST)adaptor.create(ROOT139);
                            root_0 = (GrammarAST)adaptor.becomeRoot(ROOT139_tree, root_0);
                            }

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:651:21: BANG
                            {
                            BANG140=(Token)match(input,BANG,FOLLOW_BANG_in_atom3241); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            BANG140_tree = (GrammarAST)adaptor.create(BANG140);
                            root_0 = (GrammarAST)adaptor.becomeRoot(BANG140_tree, root_0);
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
    // ANTLRParser.g:662:1: notSet : ( NOT notTerminal -> ^( NOT notTerminal ) | NOT block -> ^( NOT block ) );
    public final ANTLRParser.notSet_return notSet() throws RecognitionException {
        ANTLRParser.notSet_return retval = new ANTLRParser.notSet_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token NOT141=null;
        Token NOT143=null;
        ANTLRParser.notTerminal_return notTerminal142 = null;

        ANTLRParser.block_return block144 = null;


        GrammarAST NOT141_tree=null;
        GrammarAST NOT143_tree=null;
        RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
        RewriteRuleSubtreeStream stream_notTerminal=new RewriteRuleSubtreeStream(adaptor,"rule notTerminal");
        RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
        try {
            // ANTLRParser.g:663:5: ( NOT notTerminal -> ^( NOT notTerminal ) | NOT block -> ^( NOT block ) )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==NOT) ) {
                int LA45_1 = input.LA(2);

                if ( (LA45_1==LPAREN) ) {
                    alt45=2;
                }
                else if ( (LA45_1==TOKEN_REF||LA45_1==STRING_LITERAL) ) {
                    alt45=1;
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
                    // ANTLRParser.g:663:7: NOT notTerminal
                    {
                    NOT141=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3274); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT141);

                    pushFollow(FOLLOW_notTerminal_in_notSet3276);
                    notTerminal142=notTerminal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_notTerminal.add(notTerminal142.getTree());


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
                    // 663:23: -> ^( NOT notTerminal )
                    {
                        // ANTLRParser.g:663:26: ^( NOT notTerminal )
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
                    // ANTLRParser.g:664:7: NOT block
                    {
                    NOT143=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3292); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_NOT.add(NOT143);

                    pushFollow(FOLLOW_block_in_notSet3294);
                    block144=block();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_block.add(block144.getTree());


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
                    // 664:19: -> ^( NOT block )
                    {
                        // ANTLRParser.g:664:22: ^( NOT block )
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
    // ANTLRParser.g:673:1: notTerminal : ( TOKEN_REF | STRING_LITERAL );
    public final ANTLRParser.notTerminal_return notTerminal() throws RecognitionException {
        ANTLRParser.notTerminal_return retval = new ANTLRParser.notTerminal_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set145=null;

        GrammarAST set145_tree=null;

        try {
            // ANTLRParser.g:674:5: ( TOKEN_REF | STRING_LITERAL )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set145=(Token)input.LT(1);
            if ( input.LA(1)==TOKEN_REF||input.LA(1)==STRING_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set145));
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
    // ANTLRParser.g:685:1: block : LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK ( optionsSpec )? ( $ra)* altList ) ;
    public final ANTLRParser.block_return block() throws RecognitionException {
        ANTLRParser.block_return retval = new ANTLRParser.block_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN146=null;
        Token COLON148=null;
        Token RPAREN150=null;
        List list_ra=null;
        ANTLRParser.optionsSpec_return optionsSpec147 = null;

        ANTLRParser.altList_return altList149 = null;

        RuleReturnScope ra = null;
        GrammarAST LPAREN146_tree=null;
        GrammarAST COLON148_tree=null;
        GrammarAST RPAREN150_tree=null;
        RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_optionsSpec=new RewriteRuleSubtreeStream(adaptor,"rule optionsSpec");
        RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
        RewriteRuleSubtreeStream stream_ruleAction=new RewriteRuleSubtreeStream(adaptor,"rule ruleAction");
        try {
            // ANTLRParser.g:686:5: ( LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK ( optionsSpec )? ( $ra)* altList ) )
            // ANTLRParser.g:686:7: LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN
            {
            LPAREN146=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_block3368); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN146);

            // ANTLRParser.g:689:10: ( ( optionsSpec )? (ra+= ruleAction )* COLON )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==OPTIONS||LA48_0==COLON||LA48_0==AT) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // ANTLRParser.g:689:12: ( optionsSpec )? (ra+= ruleAction )* COLON
                    {
                    // ANTLRParser.g:689:12: ( optionsSpec )?
                    int alt46=2;
                    int LA46_0 = input.LA(1);

                    if ( (LA46_0==OPTIONS) ) {
                        alt46=1;
                    }
                    switch (alt46) {
                        case 1 :
                            // ANTLRParser.g:689:12: optionsSpec
                            {
                            pushFollow(FOLLOW_optionsSpec_in_block3405);
                            optionsSpec147=optionsSpec();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_optionsSpec.add(optionsSpec147.getTree());

                            }
                            break;

                    }

                    // ANTLRParser.g:689:27: (ra+= ruleAction )*
                    loop47:
                    do {
                        int alt47=2;
                        int LA47_0 = input.LA(1);

                        if ( (LA47_0==AT) ) {
                            alt47=1;
                        }


                        switch (alt47) {
                    	case 1 :
                    	    // ANTLRParser.g:689:27: ra+= ruleAction
                    	    {
                    	    pushFollow(FOLLOW_ruleAction_in_block3410);
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

                    COLON148=(Token)match(input,COLON,FOLLOW_COLON_in_block3413); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_COLON.add(COLON148);


                    }
                    break;

            }

            pushFollow(FOLLOW_altList_in_block3427);
            altList149=altList();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_altList.add(altList149.getTree());
            RPAREN150=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_block3444); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN150);



            // AST REWRITE
            // elements: altList, ra, optionsSpec
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
            // 692:7: -> ^( BLOCK ( optionsSpec )? ( $ra)* altList )
            {
                // ANTLRParser.g:692:10: ^( BLOCK ( optionsSpec )? ( $ra)* altList )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_1);

                // ANTLRParser.g:692:28: ( optionsSpec )?
                if ( stream_optionsSpec.hasNext() ) {
                    adaptor.addChild(root_1, stream_optionsSpec.nextTree());

                }
                stream_optionsSpec.reset();
                // ANTLRParser.g:692:41: ( $ra)*
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
    // ANTLRParser.g:701:1: ruleref : RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) ;
    public final ANTLRParser.ruleref_return ruleref() throws RecognitionException {
        ANTLRParser.ruleref_return retval = new ANTLRParser.ruleref_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token op=null;
        Token RULE_REF151=null;
        Token ARG_ACTION152=null;

        GrammarAST op_tree=null;
        GrammarAST RULE_REF151_tree=null;
        GrammarAST ARG_ACTION152_tree=null;
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_ROOT=new RewriteRuleTokenStream(adaptor,"token ROOT");
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

        try {
            // ANTLRParser.g:702:5: ( RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) ) )
            // ANTLRParser.g:702:7: RULE_REF ( ARG_ACTION )? ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
            {
            RULE_REF151=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref3493); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF151);

            // ANTLRParser.g:702:16: ( ARG_ACTION )?
            int alt49=2;
            int LA49_0 = input.LA(1);

            if ( (LA49_0==ARG_ACTION) ) {
                alt49=1;
            }
            switch (alt49) {
                case 1 :
                    // ANTLRParser.g:702:16: ARG_ACTION
                    {
                    ARG_ACTION152=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref3495); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION152);


                    }
                    break;

            }

            // ANTLRParser.g:703:3: ( (op= ROOT | op= BANG ) -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) ) | -> ^( RULE_REF ( ARG_ACTION )? ) )
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
                    // ANTLRParser.g:703:5: (op= ROOT | op= BANG )
                    {
                    // ANTLRParser.g:703:5: (op= ROOT | op= BANG )
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
                            // ANTLRParser.g:703:6: op= ROOT
                            {
                            op=(Token)match(input,ROOT,FOLLOW_ROOT_in_ruleref3505); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ROOT.add(op);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:703:14: op= BANG
                            {
                            op=(Token)match(input,BANG,FOLLOW_BANG_in_ruleref3509); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_BANG.add(op);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: RULE_REF, op, ARG_ACTION
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
                    // 703:23: -> ^( $op ^( RULE_REF ( ARG_ACTION )? ) )
                    {
                        // ANTLRParser.g:703:26: ^( $op ^( RULE_REF ( ARG_ACTION )? ) )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_op.nextNode(), root_1);

                        // ANTLRParser.g:703:32: ^( RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot(stream_RULE_REF.nextNode(), root_2);

                        // ANTLRParser.g:703:43: ( ARG_ACTION )?
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
                    // ANTLRParser.g:704:10: 
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
                    // 704:10: -> ^( RULE_REF ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:704:13: ^( RULE_REF ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_RULE_REF.nextNode(), root_1);

                        // ANTLRParser.g:704:24: ( ARG_ACTION )?
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
    // ANTLRParser.g:717:1: range : rangeElement RANGE rangeElement ;
    public final ANTLRParser.range_return range() throws RecognitionException {
        ANTLRParser.range_return retval = new ANTLRParser.range_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RANGE154=null;
        ANTLRParser.rangeElement_return rangeElement153 = null;

        ANTLRParser.rangeElement_return rangeElement155 = null;


        GrammarAST RANGE154_tree=null;

        try {
            // ANTLRParser.g:718:5: ( rangeElement RANGE rangeElement )
            // ANTLRParser.g:718:7: rangeElement RANGE rangeElement
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_rangeElement_in_range3574);
            rangeElement153=rangeElement();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rangeElement153.getTree());
            RANGE154=(Token)match(input,RANGE,FOLLOW_RANGE_in_range3576); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            RANGE154_tree = (GrammarAST)adaptor.create(RANGE154);
            root_0 = (GrammarAST)adaptor.becomeRoot(RANGE154_tree, root_0);
            }
            pushFollow(FOLLOW_rangeElement_in_range3579);
            rangeElement155=rangeElement();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rangeElement155.getTree());

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
    // ANTLRParser.g:729:1: rangeElement : ( STRING_LITERAL | RULE_REF | TOKEN_REF );
    public final ANTLRParser.rangeElement_return rangeElement() throws RecognitionException {
        ANTLRParser.rangeElement_return retval = new ANTLRParser.rangeElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token set156=null;

        GrammarAST set156_tree=null;

        try {
            // ANTLRParser.g:730:5: ( STRING_LITERAL | RULE_REF | TOKEN_REF )
            // ANTLRParser.g:
            {
            root_0 = (GrammarAST)adaptor.nil();

            set156=(Token)input.LT(1);
            if ( (input.LA(1)>=TOKEN_REF && input.LA(1)<=RULE_REF)||input.LA(1)==STRING_LITERAL ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (GrammarAST)adaptor.create(set156));
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
    // ANTLRParser.g:735:1: terminal : ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )? ;
    public final ANTLRParser.terminal_return terminal() throws RecognitionException {
        ANTLRParser.terminal_return retval = new ANTLRParser.terminal_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF157=null;
        Token ARG_ACTION158=null;
        Token STRING_LITERAL160=null;
        Token DOT162=null;
        Token ROOT164=null;
        Token BANG165=null;
        ANTLRParser.elementOptions_return elementOptions159 = null;

        ANTLRParser.elementOptions_return elementOptions161 = null;

        ANTLRParser.elementOptions_return elementOptions163 = null;


        GrammarAST TOKEN_REF157_tree=null;
        GrammarAST ARG_ACTION158_tree=null;
        GrammarAST STRING_LITERAL160_tree=null;
        GrammarAST DOT162_tree=null;
        GrammarAST ROOT164_tree=null;
        GrammarAST BANG165_tree=null;
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_BANG=new RewriteRuleTokenStream(adaptor,"token BANG");
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleTokenStream stream_ROOT=new RewriteRuleTokenStream(adaptor,"token ROOT");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        try {
            // ANTLRParser.g:736:5: ( ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )? )
            // ANTLRParser.g:736:9: ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ) ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )?
            {
            // ANTLRParser.g:736:9: ( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) )
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
                    // ANTLRParser.g:737:7: TOKEN_REF ( ARG_ACTION )? ( elementOptions )?
                    {
                    TOKEN_REF157=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal3673); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF157);

                    // ANTLRParser.g:737:17: ( ARG_ACTION )?
                    int alt52=2;
                    int LA52_0 = input.LA(1);

                    if ( (LA52_0==ARG_ACTION) ) {
                        alt52=1;
                    }
                    switch (alt52) {
                        case 1 :
                            // ANTLRParser.g:737:17: ARG_ACTION
                            {
                            ARG_ACTION158=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal3675); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION158);


                            }
                            break;

                    }

                    // ANTLRParser.g:737:29: ( elementOptions )?
                    int alt53=2;
                    int LA53_0 = input.LA(1);

                    if ( (LA53_0==LT) ) {
                        alt53=1;
                    }
                    switch (alt53) {
                        case 1 :
                            // ANTLRParser.g:737:29: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3678);
                            elementOptions159=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions159.getTree());

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
                    // 737:45: -> ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                    {
                        // ANTLRParser.g:737:48: ^( TOKEN_REF ( ARG_ACTION )? ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_TOKEN_REF.nextToken()), root_1);

                        // ANTLRParser.g:737:73: ( ARG_ACTION )?
                        if ( stream_ARG_ACTION.hasNext() ) {
                            adaptor.addChild(root_1, stream_ARG_ACTION.nextNode());

                        }
                        stream_ARG_ACTION.reset();
                        // ANTLRParser.g:737:85: ( elementOptions )?
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
                    // ANTLRParser.g:738:7: STRING_LITERAL ( elementOptions )?
                    {
                    STRING_LITERAL160=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal3702); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL160);

                    // ANTLRParser.g:738:22: ( elementOptions )?
                    int alt54=2;
                    int LA54_0 = input.LA(1);

                    if ( (LA54_0==LT) ) {
                        alt54=1;
                    }
                    switch (alt54) {
                        case 1 :
                            // ANTLRParser.g:738:22: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3704);
                            elementOptions161=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions161.getTree());

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
                    // 738:41: -> ^( STRING_LITERAL ( elementOptions )? )
                    {
                        // ANTLRParser.g:738:44: ^( STRING_LITERAL ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_STRING_LITERAL.nextToken()), root_1);

                        // ANTLRParser.g:738:74: ( elementOptions )?
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
                    // ANTLRParser.g:744:7: DOT ( elementOptions )?
                    {
                    DOT162=(Token)match(input,DOT,FOLLOW_DOT_in_terminal3751); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOT.add(DOT162);

                    // ANTLRParser.g:744:11: ( elementOptions )?
                    int alt55=2;
                    int LA55_0 = input.LA(1);

                    if ( (LA55_0==LT) ) {
                        alt55=1;
                    }
                    switch (alt55) {
                        case 1 :
                            // ANTLRParser.g:744:11: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_terminal3753);
                            elementOptions163=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions163.getTree());

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
                    // 744:34: -> ^( WILDCARD[$DOT] ( elementOptions )? )
                    {
                        // ANTLRParser.g:744:37: ^( WILDCARD[$DOT] ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(WILDCARD, DOT162), root_1);

                        // ANTLRParser.g:744:67: ( elementOptions )?
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

            // ANTLRParser.g:746:3: ( ROOT -> ^( ROOT $terminal) | BANG -> ^( BANG $terminal) )?
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
                    // ANTLRParser.g:746:5: ROOT
                    {
                    ROOT164=(Token)match(input,ROOT,FOLLOW_ROOT_in_terminal3784); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ROOT.add(ROOT164);



                    // AST REWRITE
                    // elements: ROOT, terminal
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 746:19: -> ^( ROOT $terminal)
                    {
                        // ANTLRParser.g:746:22: ^( ROOT $terminal)
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
                    // ANTLRParser.g:747:5: BANG
                    {
                    BANG165=(Token)match(input,BANG,FOLLOW_BANG_in_terminal3808); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_BANG.add(BANG165);



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
                    // 747:19: -> ^( BANG $terminal)
                    {
                        // ANTLRParser.g:747:22: ^( BANG $terminal)
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
    // ANTLRParser.g:757:2: elementOptions : LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
    public final ANTLRParser.elementOptions_return elementOptions() throws RecognitionException {
        ANTLRParser.elementOptions_return retval = new ANTLRParser.elementOptions_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LT166=null;
        Token COMMA168=null;
        Token GT170=null;
        ANTLRParser.elementOption_return elementOption167 = null;

        ANTLRParser.elementOption_return elementOption169 = null;


        GrammarAST LT166_tree=null;
        GrammarAST COMMA168_tree=null;
        GrammarAST GT170_tree=null;
        RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
        RewriteRuleTokenStream stream_LT=new RewriteRuleTokenStream(adaptor,"token LT");
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_elementOption=new RewriteRuleSubtreeStream(adaptor,"rule elementOption");
        try {
            // ANTLRParser.g:758:5: ( LT elementOption ( COMMA elementOption )* GT -> ^( ELEMENT_OPTIONS ( elementOption )+ ) )
            // ANTLRParser.g:760:7: LT elementOption ( COMMA elementOption )* GT
            {
            LT166=(Token)match(input,LT,FOLLOW_LT_in_elementOptions3872); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LT.add(LT166);

            pushFollow(FOLLOW_elementOption_in_elementOptions3874);
            elementOption167=elementOption();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_elementOption.add(elementOption167.getTree());
            // ANTLRParser.g:760:24: ( COMMA elementOption )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==COMMA) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // ANTLRParser.g:760:25: COMMA elementOption
            	    {
            	    COMMA168=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementOptions3877); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA168);

            	    pushFollow(FOLLOW_elementOption_in_elementOptions3879);
            	    elementOption169=elementOption();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_elementOption.add(elementOption169.getTree());

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            GT170=(Token)match(input,GT,FOLLOW_GT_in_elementOptions3883); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_GT.add(GT170);



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
            // 760:50: -> ^( ELEMENT_OPTIONS ( elementOption )+ )
            {
                // ANTLRParser.g:760:53: ^( ELEMENT_OPTIONS ( elementOption )+ )
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
    // ANTLRParser.g:765:1: elementOption : ( qid | id ASSIGN ( qid | STRING_LITERAL ) );
    public final ANTLRParser.elementOption_return elementOption() throws RecognitionException {
        ANTLRParser.elementOption_return retval = new ANTLRParser.elementOption_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN173=null;
        Token STRING_LITERAL175=null;
        ANTLRParser.qid_return qid171 = null;

        ANTLRParser.id_return id172 = null;

        ANTLRParser.qid_return qid174 = null;


        GrammarAST ASSIGN173_tree=null;
        GrammarAST STRING_LITERAL175_tree=null;

        try {
            // ANTLRParser.g:766:5: ( qid | id ASSIGN ( qid | STRING_LITERAL ) )
            int alt60=2;
            switch ( input.LA(1) ) {
            case RULE_REF:
                {
                int LA60_1 = input.LA(2);

                if ( (LA60_1==ASSIGN) ) {
                    alt60=2;
                }
                else if ( (LA60_1==COMMA||LA60_1==GT||LA60_1==DOT) ) {
                    alt60=1;
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

                if ( (LA60_2==ASSIGN) ) {
                    alt60=2;
                }
                else if ( (LA60_2==COMMA||LA60_2==GT||LA60_2==DOT) ) {
                    alt60=1;
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
                    // ANTLRParser.g:767:7: qid
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_qid_in_elementOption3918);
                    qid171=qid();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, qid171.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:770:7: id ASSIGN ( qid | STRING_LITERAL )
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_id_in_elementOption3940);
                    id172=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, id172.getTree());
                    ASSIGN173=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption3942); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ASSIGN173_tree = (GrammarAST)adaptor.create(ASSIGN173);
                    root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN173_tree, root_0);
                    }
                    // ANTLRParser.g:770:18: ( qid | STRING_LITERAL )
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
                            // ANTLRParser.g:770:19: qid
                            {
                            pushFollow(FOLLOW_qid_in_elementOption3946);
                            qid174=qid();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) adaptor.addChild(root_0, qid174.getTree());

                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:770:25: STRING_LITERAL
                            {
                            STRING_LITERAL175=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption3950); if (state.failed) return retval;
                            if ( state.backtracking==0 ) {
                            STRING_LITERAL175_tree = new TerminalAST(STRING_LITERAL175) ;
                            adaptor.addChild(root_0, STRING_LITERAL175_tree);
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
    // ANTLRParser.g:773:1: rewrite : ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite ;
    public final ANTLRParser.rewrite_return rewrite() throws RecognitionException {
        ANTLRParser.rewrite_return retval = new ANTLRParser.rewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.predicatedRewrite_return predicatedRewrite176 = null;

        ANTLRParser.nakedRewrite_return nakedRewrite177 = null;


        RewriteRuleSubtreeStream stream_predicatedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule predicatedRewrite");
        RewriteRuleSubtreeStream stream_nakedRewrite=new RewriteRuleSubtreeStream(adaptor,"rule nakedRewrite");
        try {
            // ANTLRParser.g:774:2: ( ( predicatedRewrite )* nakedRewrite -> ( predicatedRewrite )* nakedRewrite )
            // ANTLRParser.g:774:4: ( predicatedRewrite )* nakedRewrite
            {
            // ANTLRParser.g:774:4: ( predicatedRewrite )*
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
            	    // ANTLRParser.g:774:4: predicatedRewrite
            	    {
            	    pushFollow(FOLLOW_predicatedRewrite_in_rewrite3968);
            	    predicatedRewrite176=predicatedRewrite();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_predicatedRewrite.add(predicatedRewrite176.getTree());

            	    }
            	    break;

            	default :
            	    break loop61;
                }
            } while (true);

            pushFollow(FOLLOW_nakedRewrite_in_rewrite3971);
            nakedRewrite177=nakedRewrite();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_nakedRewrite.add(nakedRewrite177.getTree());


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
            // 774:36: -> ( predicatedRewrite )* nakedRewrite
            {
                // ANTLRParser.g:774:39: ( predicatedRewrite )*
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
    // ANTLRParser.g:777:1: predicatedRewrite : RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) ;
    public final ANTLRParser.predicatedRewrite_return predicatedRewrite() throws RecognitionException {
        ANTLRParser.predicatedRewrite_return retval = new ANTLRParser.predicatedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW178=null;
        Token SEMPRED179=null;
        ANTLRParser.rewriteAlt_return rewriteAlt180 = null;


        GrammarAST RARROW178_tree=null;
        GrammarAST SEMPRED179_tree=null;
        RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:778:2: ( RARROW SEMPRED rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt ) -> ^( RESULT[$RARROW] SEMPRED rewriteAlt ) )
            // ANTLRParser.g:778:4: RARROW SEMPRED rewriteAlt
            {
            RARROW178=(Token)match(input,RARROW,FOLLOW_RARROW_in_predicatedRewrite3989); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW178);

            SEMPRED179=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_predicatedRewrite3991); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_SEMPRED.add(SEMPRED179);

            pushFollow(FOLLOW_rewriteAlt_in_predicatedRewrite3993);
            rewriteAlt180=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt180.getTree());


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
            // 779:3: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
            if ((rewriteAlt180!=null?rewriteAlt180.isTemplate:false)) {
                // ANTLRParser.g:779:32: ^( ST_RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW178), root_1);

                adaptor.addChild(root_1, stream_SEMPRED.nextNode());
                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 780:3: -> ^( RESULT[$RARROW] SEMPRED rewriteAlt )
            {
                // ANTLRParser.g:780:6: ^( RESULT[$RARROW] SEMPRED rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW178), root_1);

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
    // ANTLRParser.g:783:1: nakedRewrite : RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) ;
    public final ANTLRParser.nakedRewrite_return nakedRewrite() throws RecognitionException {
        ANTLRParser.nakedRewrite_return retval = new ANTLRParser.nakedRewrite_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RARROW181=null;
        ANTLRParser.rewriteAlt_return rewriteAlt182 = null;


        GrammarAST RARROW181_tree=null;
        RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
        RewriteRuleSubtreeStream stream_rewriteAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteAlt");
        try {
            // ANTLRParser.g:784:2: ( RARROW rewriteAlt -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt ) -> ^( RESULT[$RARROW] rewriteAlt ) )
            // ANTLRParser.g:784:4: RARROW rewriteAlt
            {
            RARROW181=(Token)match(input,RARROW,FOLLOW_RARROW_in_nakedRewrite4033); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RARROW.add(RARROW181);

            pushFollow(FOLLOW_rewriteAlt_in_nakedRewrite4035);
            rewriteAlt182=rewriteAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteAlt.add(rewriteAlt182.getTree());


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
            // 784:22: -> {$rewriteAlt.isTemplate}? ^( ST_RESULT[$RARROW] rewriteAlt )
            if ((rewriteAlt182!=null?rewriteAlt182.isTemplate:false)) {
                // ANTLRParser.g:784:51: ^( ST_RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ST_RESULT, RARROW181), root_1);

                adaptor.addChild(root_1, stream_rewriteAlt.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }
            else // 785:10: -> ^( RESULT[$RARROW] rewriteAlt )
            {
                // ANTLRParser.g:785:13: ^( RESULT[$RARROW] rewriteAlt )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RESULT, RARROW181), root_1);

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
    // ANTLRParser.g:790:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );
    public final ANTLRParser.rewriteAlt_return rewriteAlt() throws RecognitionException {
        ANTLRParser.rewriteAlt_return retval = new ANTLRParser.rewriteAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ETC185=null;
        ANTLRParser.rewriteTemplate_return rewriteTemplate183 = null;

        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt184 = null;


        GrammarAST ETC185_tree=null;

        try {
            // ANTLRParser.g:792:5: ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON )
            int alt62=4;
            alt62 = dfa62.predict(input);
            switch (alt62) {
                case 1 :
                    // ANTLRParser.g:793:7: rewriteTemplate
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplate_in_rewriteAlt4099);
                    rewriteTemplate183=rewriteTemplate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplate183.getTree());
                    if ( state.backtracking==0 ) {
                      retval.isTemplate =true;
                    }

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:799:7: rewriteTreeAlt
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteAlt4138);
                    rewriteTreeAlt184=rewriteTreeAlt();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAlt184.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:801:7: ETC
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ETC185=(Token)match(input,ETC,FOLLOW_ETC_in_rewriteAlt4147); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ETC185_tree = (GrammarAST)adaptor.create(ETC185);
                    adaptor.addChild(root_0, ETC185_tree);
                    }

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:803:27: 
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
                    // 803:27: -> EPSILON
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
    // ANTLRParser.g:806:1: rewriteTreeAlt : ( rewriteTreeElement )+ -> ^( ALT ( rewriteTreeElement )+ ) ;
    public final ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt() throws RecognitionException {
        ANTLRParser.rewriteTreeAlt_return retval = new ANTLRParser.rewriteTreeAlt_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement186 = null;


        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:807:5: ( ( rewriteTreeElement )+ -> ^( ALT ( rewriteTreeElement )+ ) )
            // ANTLRParser.g:807:7: ( rewriteTreeElement )+
            {
            // ANTLRParser.g:807:7: ( rewriteTreeElement )+
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
            	    // ANTLRParser.g:807:7: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4178);
            	    rewriteTreeElement186=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement186.getTree());

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
            // 807:27: -> ^( ALT ( rewriteTreeElement )+ )
            {
                // ANTLRParser.g:807:30: ^( ALT ( rewriteTreeElement )+ )
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
    // ANTLRParser.g:810:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );
    public final ANTLRParser.rewriteTreeElement_return rewriteTreeElement() throws RecognitionException {
        ANTLRParser.rewriteTreeElement_return retval = new ANTLRParser.rewriteTreeElement_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom187 = null;

        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom188 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix189 = null;

        ANTLRParser.rewriteTree_return rewriteTree190 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix191 = null;

        ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf192 = null;


        RewriteRuleSubtreeStream stream_rewriteTree=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTree");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
        try {
            // ANTLRParser.g:811:2: ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf )
            int alt65=4;
            alt65 = dfa65.predict(input);
            switch (alt65) {
                case 1 :
                    // ANTLRParser.g:811:4: rewriteTreeAtom
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4202);
                    rewriteTreeAtom187=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeAtom187.getTree());

                    }
                    break;
                case 2 :
                    // ANTLRParser.g:812:4: rewriteTreeAtom ebnfSuffix
                    {
                    pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4207);
                    rewriteTreeAtom188=rewriteTreeAtom();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom188.getTree());
                    pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4209);
                    ebnfSuffix189=ebnfSuffix();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix189.getTree());


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
                    // 812:31: -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) )
                    {
                        // ANTLRParser.g:812:34: ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                        // ANTLRParser.g:812:48: ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) )
                        {
                        GrammarAST root_2 = (GrammarAST)adaptor.nil();
                        root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(REWRITE_BLOCK, "REWRITE_BLOCK"), root_2);

                        // ANTLRParser.g:812:64: ^( ALT rewriteTreeAtom )
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
                    // ANTLRParser.g:813:6: rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree )
                    {
                    pushFollow(FOLLOW_rewriteTree_in_rewriteTreeElement4234);
                    rewriteTree190=rewriteTree();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTree.add(rewriteTree190.getTree());
                    // ANTLRParser.g:814:3: ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree )
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
                            // ANTLRParser.g:814:5: ebnfSuffix
                            {
                            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeElement4240);
                            ebnfSuffix191=ebnfSuffix();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix191.getTree());


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
                            // 815:4: -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) )
                            {
                                // ANTLRParser.g:815:7: ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) )
                                {
                                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                                // ANTLRParser.g:815:20: ^( REWRITE_BLOCK ^( ALT rewriteTree ) )
                                {
                                GrammarAST root_2 = (GrammarAST)adaptor.nil();
                                root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(REWRITE_BLOCK, "REWRITE_BLOCK"), root_2);

                                // ANTLRParser.g:815:36: ^( ALT rewriteTree )
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
                            // ANTLRParser.g:816:5: 
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
                            // 816:5: -> rewriteTree
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
                    // ANTLRParser.g:818:6: rewriteTreeEbnf
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4279);
                    rewriteTreeEbnf192=rewriteTreeEbnf();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTreeEbnf192.getTree());

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
    // ANTLRParser.g:821:1: rewriteTreeAtom : ( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION );
    public final ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom() throws RecognitionException {
        ANTLRParser.rewriteTreeAtom_return retval = new ANTLRParser.rewriteTreeAtom_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TOKEN_REF193=null;
        Token ARG_ACTION195=null;
        Token RULE_REF196=null;
        Token STRING_LITERAL197=null;
        Token DOLLAR199=null;
        Token ACTION201=null;
        ANTLRParser.elementOptions_return elementOptions194 = null;

        ANTLRParser.elementOptions_return elementOptions198 = null;

        ANTLRParser.id_return id200 = null;


        GrammarAST TOKEN_REF193_tree=null;
        GrammarAST ARG_ACTION195_tree=null;
        GrammarAST RULE_REF196_tree=null;
        GrammarAST STRING_LITERAL197_tree=null;
        GrammarAST DOLLAR199_tree=null;
        GrammarAST ACTION201_tree=null;
        RewriteRuleTokenStream stream_DOLLAR=new RewriteRuleTokenStream(adaptor,"token DOLLAR");
        RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
        RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");
        try {
            // ANTLRParser.g:822:5: ( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? ) | RULE_REF | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) | DOLLAR id -> LABEL[$DOLLAR,$id.text] | ACTION )
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
                    // ANTLRParser.g:822:9: TOKEN_REF ( elementOptions )? ( ARG_ACTION )?
                    {
                    TOKEN_REF193=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_rewriteTreeAtom4295); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF193);

                    // ANTLRParser.g:822:19: ( elementOptions )?
                    int alt66=2;
                    int LA66_0 = input.LA(1);

                    if ( (LA66_0==LT) ) {
                        alt66=1;
                    }
                    switch (alt66) {
                        case 1 :
                            // ANTLRParser.g:822:19: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom4297);
                            elementOptions194=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions194.getTree());

                            }
                            break;

                    }

                    // ANTLRParser.g:822:35: ( ARG_ACTION )?
                    int alt67=2;
                    int LA67_0 = input.LA(1);

                    if ( (LA67_0==ARG_ACTION) ) {
                        alt67=1;
                    }
                    switch (alt67) {
                        case 1 :
                            // ANTLRParser.g:822:35: ARG_ACTION
                            {
                            ARG_ACTION195=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rewriteTreeAtom4300); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ARG_ACTION.add(ARG_ACTION195);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: elementOptions, TOKEN_REF, ARG_ACTION
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (GrammarAST)adaptor.nil();
                    // 822:47: -> ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? )
                    {
                        // ANTLRParser.g:822:50: ^( TOKEN_REF ( elementOptions )? ( ARG_ACTION )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_TOKEN_REF.nextToken()), root_1);

                        // ANTLRParser.g:822:75: ( elementOptions )?
                        if ( stream_elementOptions.hasNext() ) {
                            adaptor.addChild(root_1, stream_elementOptions.nextTree());

                        }
                        stream_elementOptions.reset();
                        // ANTLRParser.g:822:91: ( ARG_ACTION )?
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
                    // ANTLRParser.g:823:9: RULE_REF
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    RULE_REF196=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_rewriteTreeAtom4327); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    RULE_REF196_tree = (GrammarAST)adaptor.create(RULE_REF196);
                    adaptor.addChild(root_0, RULE_REF196_tree);
                    }

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:824:6: STRING_LITERAL ( elementOptions )?
                    {
                    STRING_LITERAL197=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4334); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_STRING_LITERAL.add(STRING_LITERAL197);

                    // ANTLRParser.g:824:21: ( elementOptions )?
                    int alt68=2;
                    int LA68_0 = input.LA(1);

                    if ( (LA68_0==LT) ) {
                        alt68=1;
                    }
                    switch (alt68) {
                        case 1 :
                            // ANTLRParser.g:824:21: elementOptions
                            {
                            pushFollow(FOLLOW_elementOptions_in_rewriteTreeAtom4336);
                            elementOptions198=elementOptions();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_elementOptions.add(elementOptions198.getTree());

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
                    // 824:40: -> ^( STRING_LITERAL ( elementOptions )? )
                    {
                        // ANTLRParser.g:824:43: ^( STRING_LITERAL ( elementOptions )? )
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_STRING_LITERAL.nextToken()), root_1);

                        // ANTLRParser.g:824:73: ( elementOptions )?
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
                    // ANTLRParser.g:825:6: DOLLAR id
                    {
                    DOLLAR199=(Token)match(input,DOLLAR,FOLLOW_DOLLAR_in_rewriteTreeAtom4359); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_DOLLAR.add(DOLLAR199);

                    pushFollow(FOLLOW_id_in_rewriteTreeAtom4361);
                    id200=id();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_id.add(id200.getTree());


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
                    // 825:16: -> LABEL[$DOLLAR,$id.text]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(LABEL, DOLLAR199, (id200!=null?input.toString(id200.start,id200.stop):null)));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // ANTLRParser.g:826:4: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION201=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTreeAtom4372); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION201_tree = new ActionAST(ACTION201) ;
                    adaptor.addChild(root_0, ACTION201_tree);
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
    // ANTLRParser.g:829:1: rewriteTreeEbnf : lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) ) ;
    public final ANTLRParser.rewriteTreeEbnf_return rewriteTreeEbnf() throws RecognitionException {
        ANTLRParser.rewriteTreeEbnf_return retval = new ANTLRParser.rewriteTreeEbnf_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token RPAREN203=null;
        ANTLRParser.rewriteTreeAlt_return rewriteTreeAlt202 = null;

        ANTLRParser.ebnfSuffix_return ebnfSuffix204 = null;


        GrammarAST lp_tree=null;
        GrammarAST RPAREN203_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTreeAlt=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAlt");
        RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");

            Token firstToken = input.LT(1);

        try {
            // ANTLRParser.g:837:2: (lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) ) )
            // ANTLRParser.g:837:4: lp= LPAREN rewriteTreeAlt RPAREN ebnfSuffix
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTreeEbnf4398); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            pushFollow(FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4400);
            rewriteTreeAlt202=rewriteTreeAlt();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAlt.add(rewriteTreeAlt202.getTree());
            RPAREN203=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTreeEbnf4402); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN203);

            pushFollow(FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4404);
            ebnfSuffix204=ebnfSuffix();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_ebnfSuffix.add(ebnfSuffix204.getTree());


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
            // 837:47: -> ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) )
            {
                // ANTLRParser.g:837:50: ^( ebnfSuffix ^( REWRITE_BLOCK[$lp] rewriteTreeAlt ) )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);

                // ANTLRParser.g:837:63: ^( REWRITE_BLOCK[$lp] rewriteTreeAlt )
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
    // ANTLRParser.g:840:1: rewriteTree : TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) ;
    public final ANTLRParser.rewriteTree_return rewriteTree() throws RecognitionException {
        ANTLRParser.rewriteTree_return retval = new ANTLRParser.rewriteTree_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token TREE_BEGIN205=null;
        Token RPAREN208=null;
        ANTLRParser.rewriteTreeAtom_return rewriteTreeAtom206 = null;

        ANTLRParser.rewriteTreeElement_return rewriteTreeElement207 = null;


        GrammarAST TREE_BEGIN205_tree=null;
        GrammarAST RPAREN208_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_TREE_BEGIN=new RewriteRuleTokenStream(adaptor,"token TREE_BEGIN");
        RewriteRuleSubtreeStream stream_rewriteTreeAtom=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeAtom");
        RewriteRuleSubtreeStream stream_rewriteTreeElement=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTreeElement");
        try {
            // ANTLRParser.g:841:2: ( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* ) )
            // ANTLRParser.g:841:4: TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* RPAREN
            {
            TREE_BEGIN205=(Token)match(input,TREE_BEGIN,FOLLOW_TREE_BEGIN_in_rewriteTree4428); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_TREE_BEGIN.add(TREE_BEGIN205);

            pushFollow(FOLLOW_rewriteTreeAtom_in_rewriteTree4430);
            rewriteTreeAtom206=rewriteTreeAtom();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTreeAtom.add(rewriteTreeAtom206.getTree());
            // ANTLRParser.g:841:31: ( rewriteTreeElement )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==ACTION||LA70_0==LPAREN||LA70_0==DOLLAR||LA70_0==TREE_BEGIN||(LA70_0>=TOKEN_REF && LA70_0<=RULE_REF)||LA70_0==STRING_LITERAL) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // ANTLRParser.g:841:31: rewriteTreeElement
            	    {
            	    pushFollow(FOLLOW_rewriteTreeElement_in_rewriteTree4432);
            	    rewriteTreeElement207=rewriteTreeElement();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_rewriteTreeElement.add(rewriteTreeElement207.getTree());

            	    }
            	    break;

            	default :
            	    break loop70;
                }
            } while (true);

            RPAREN208=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTree4435); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN208);



            // AST REWRITE
            // elements: TREE_BEGIN, rewriteTreeElement, rewriteTreeAtom
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (GrammarAST)adaptor.nil();
            // 842:3: -> ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
            {
                // ANTLRParser.g:842:6: ^( TREE_BEGIN rewriteTreeAtom ( rewriteTreeElement )* )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot(stream_TREE_BEGIN.nextNode(), root_1);

                adaptor.addChild(root_1, stream_rewriteTreeAtom.nextTree());
                // ANTLRParser.g:842:35: ( rewriteTreeElement )*
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
    // ANTLRParser.g:845:1: rewriteTemplate : ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );
    public final ANTLRParser.rewriteTemplate_return rewriteTemplate() throws RecognitionException {
        ANTLRParser.rewriteTemplate_return retval = new ANTLRParser.rewriteTemplate_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token str=null;
        Token TEMPLATE209=null;
        Token LPAREN210=null;
        Token RPAREN212=null;
        Token ACTION215=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs211 = null;

        ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef213 = null;

        ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead214 = null;


        GrammarAST str_tree=null;
        GrammarAST TEMPLATE209_tree=null;
        GrammarAST LPAREN210_tree=null;
        GrammarAST RPAREN212_tree=null;
        GrammarAST ACTION215_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_DOUBLE_QUOTE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_QUOTE_STRING_LITERAL");
        RewriteRuleTokenStream stream_TEMPLATE=new RewriteRuleTokenStream(adaptor,"token TEMPLATE");
        RewriteRuleTokenStream stream_DOUBLE_ANGLE_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token DOUBLE_ANGLE_STRING_LITERAL");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:856:2: ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION )
            int alt72=4;
            alt72 = dfa72.predict(input);
            switch (alt72) {
                case 1 :
                    // ANTLRParser.g:857:3: TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
                    {
                    TEMPLATE209=(Token)match(input,TEMPLATE,FOLLOW_TEMPLATE_in_rewriteTemplate4467); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TEMPLATE.add(TEMPLATE209);

                    LPAREN210=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplate4469); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN210);

                    pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4471);
                    rewriteTemplateArgs211=rewriteTemplateArgs();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs211.getTree());
                    RPAREN212=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplate4473); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN212);

                    // ANTLRParser.g:858:3: (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL )
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
                            // ANTLRParser.g:858:5: str= DOUBLE_QUOTE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_QUOTE_STRING_LITERAL,FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4481); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_DOUBLE_QUOTE_STRING_LITERAL.add(str);


                            }
                            break;
                        case 2 :
                            // ANTLRParser.g:858:39: str= DOUBLE_ANGLE_STRING_LITERAL
                            {
                            str=(Token)match(input,DOUBLE_ANGLE_STRING_LITERAL,FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4487); if (state.failed) return retval; 
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
                    // 859:3: -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                    {
                        // ANTLRParser.g:859:6: ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str)
                        {
                        GrammarAST root_1 = (GrammarAST)adaptor.nil();
                        root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, TEMPLATE209, "TEMPLATE"), root_1);

                        // ANTLRParser.g:859:39: ( rewriteTemplateArgs )?
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
                    // ANTLRParser.g:862:3: rewriteTemplateRef
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteTemplateRef_in_rewriteTemplate4513);
                    rewriteTemplateRef213=rewriteTemplateRef();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteTemplateRef213.getTree());

                    }
                    break;
                case 3 :
                    // ANTLRParser.g:865:3: rewriteIndirectTemplateHead
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    pushFollow(FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4522);
                    rewriteIndirectTemplateHead214=rewriteIndirectTemplateHead();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, rewriteIndirectTemplateHead214.getTree());

                    }
                    break;
                case 4 :
                    // ANTLRParser.g:868:3: ACTION
                    {
                    root_0 = (GrammarAST)adaptor.nil();

                    ACTION215=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplate4531); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    ACTION215_tree = new ActionAST(ACTION215) ;
                    adaptor.addChild(root_0, ACTION215_tree);
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
    // ANTLRParser.g:871:1: rewriteTemplateRef : id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteTemplateRef_return rewriteTemplateRef() throws RecognitionException {
        ANTLRParser.rewriteTemplateRef_return retval = new ANTLRParser.rewriteTemplateRef_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token LPAREN217=null;
        Token RPAREN219=null;
        ANTLRParser.id_return id216 = null;

        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs218 = null;


        GrammarAST LPAREN217_tree=null;
        GrammarAST RPAREN219_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:873:2: ( id LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:873:4: id LPAREN rewriteTemplateArgs RPAREN
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateRef4547);
            id216=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id216.getTree());
            LPAREN217=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteTemplateRef4549); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN217);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4551);
            rewriteTemplateArgs218=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs218.getTree());
            RPAREN219=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteTemplateRef4553); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN219);



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
            // 874:3: -> ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:874:6: ^( TEMPLATE[$LPAREN,\"TEMPLATE\"] id ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, LPAREN217, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, stream_id.nextTree());
                // ANTLRParser.g:874:40: ( rewriteTemplateArgs )?
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
    // ANTLRParser.g:877:1: rewriteIndirectTemplateHead : lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) ;
    public final ANTLRParser.rewriteIndirectTemplateHead_return rewriteIndirectTemplateHead() throws RecognitionException {
        ANTLRParser.rewriteIndirectTemplateHead_return retval = new ANTLRParser.rewriteIndirectTemplateHead_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token lp=null;
        Token ACTION220=null;
        Token RPAREN221=null;
        Token LPAREN222=null;
        Token RPAREN224=null;
        ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs223 = null;


        GrammarAST lp_tree=null;
        GrammarAST ACTION220_tree=null;
        GrammarAST RPAREN221_tree=null;
        GrammarAST LPAREN222_tree=null;
        GrammarAST RPAREN224_tree=null;
        RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
        RewriteRuleSubtreeStream stream_rewriteTemplateArgs=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArgs");
        try {
            // ANTLRParser.g:879:2: (lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? ) )
            // ANTLRParser.g:879:4: lp= LPAREN ACTION RPAREN LPAREN rewriteTemplateArgs RPAREN
            {
            lp=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4582); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(lp);

            ACTION220=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteIndirectTemplateHead4584); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION220);

            RPAREN221=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4586); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN221);

            LPAREN222=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4588); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_LPAREN.add(LPAREN222);

            pushFollow(FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4590);
            rewriteTemplateArgs223=rewriteTemplateArgs();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_rewriteTemplateArgs.add(rewriteTemplateArgs223.getTree());
            RPAREN224=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4592); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_RPAREN.add(RPAREN224);



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
            // 880:3: -> ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
            {
                // ANTLRParser.g:880:6: ^( TEMPLATE[$lp,\"TEMPLATE\"] ACTION ( rewriteTemplateArgs )? )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(TEMPLATE, lp, "TEMPLATE"), root_1);

                adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));
                // ANTLRParser.g:880:51: ( rewriteTemplateArgs )?
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
    // ANTLRParser.g:883:1: rewriteTemplateArgs : ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | );
    public final ANTLRParser.rewriteTemplateArgs_return rewriteTemplateArgs() throws RecognitionException {
        ANTLRParser.rewriteTemplateArgs_return retval = new ANTLRParser.rewriteTemplateArgs_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token COMMA226=null;
        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg225 = null;

        ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg227 = null;


        GrammarAST COMMA226_tree=null;
        RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
        RewriteRuleSubtreeStream stream_rewriteTemplateArg=new RewriteRuleSubtreeStream(adaptor,"rule rewriteTemplateArg");
        try {
            // ANTLRParser.g:884:2: ( rewriteTemplateArg ( COMMA rewriteTemplateArg )* -> ^( ARGLIST ( rewriteTemplateArg )+ ) | )
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
                    // ANTLRParser.g:884:4: rewriteTemplateArg ( COMMA rewriteTemplateArg )*
                    {
                    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4620);
                    rewriteTemplateArg225=rewriteTemplateArg();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg225.getTree());
                    // ANTLRParser.g:884:23: ( COMMA rewriteTemplateArg )*
                    loop73:
                    do {
                        int alt73=2;
                        int LA73_0 = input.LA(1);

                        if ( (LA73_0==COMMA) ) {
                            alt73=1;
                        }


                        switch (alt73) {
                    	case 1 :
                    	    // ANTLRParser.g:884:24: COMMA rewriteTemplateArg
                    	    {
                    	    COMMA226=(Token)match(input,COMMA,FOLLOW_COMMA_in_rewriteTemplateArgs4623); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_COMMA.add(COMMA226);

                    	    pushFollow(FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4625);
                    	    rewriteTemplateArg227=rewriteTemplateArg();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_rewriteTemplateArg.add(rewriteTemplateArg227.getTree());

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
                    // 885:3: -> ^( ARGLIST ( rewriteTemplateArg )+ )
                    {
                        // ANTLRParser.g:885:6: ^( ARGLIST ( rewriteTemplateArg )+ )
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
                    // ANTLRParser.g:887:2: 
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
    // ANTLRParser.g:889:1: rewriteTemplateArg : id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) ;
    public final ANTLRParser.rewriteTemplateArg_return rewriteTemplateArg() throws RecognitionException {
        ANTLRParser.rewriteTemplateArg_return retval = new ANTLRParser.rewriteTemplateArg_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token ASSIGN229=null;
        Token ACTION230=null;
        ANTLRParser.id_return id228 = null;


        GrammarAST ASSIGN229_tree=null;
        GrammarAST ACTION230_tree=null;
        RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
        RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:890:2: ( id ASSIGN ACTION -> ^( ARG[$ASSIGN] id ACTION ) )
            // ANTLRParser.g:890:6: id ASSIGN ACTION
            {
            pushFollow(FOLLOW_id_in_rewriteTemplateArg4654);
            id228=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id228.getTree());
            ASSIGN229=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_rewriteTemplateArg4656); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ASSIGN.add(ASSIGN229);

            ACTION230=(Token)match(input,ACTION,FOLLOW_ACTION_in_rewriteTemplateArg4658); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ACTION.add(ACTION230);



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
            // 890:23: -> ^( ARG[$ASSIGN] id ACTION )
            {
                // ANTLRParser.g:890:26: ^( ARG[$ASSIGN] id ACTION )
                {
                GrammarAST root_1 = (GrammarAST)adaptor.nil();
                root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ARG, ASSIGN229), root_1);

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
    // ANTLRParser.g:897:1: id : ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] | TEMPLATE -> ID[$TEMPLATE] );
    public final ANTLRParser.id_return id() throws RecognitionException {
        ANTLRParser.id_return retval = new ANTLRParser.id_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token RULE_REF231=null;
        Token TOKEN_REF232=null;
        Token TEMPLATE233=null;

        GrammarAST RULE_REF231_tree=null;
        GrammarAST TOKEN_REF232_tree=null;
        GrammarAST TEMPLATE233_tree=null;
        RewriteRuleTokenStream stream_TEMPLATE=new RewriteRuleTokenStream(adaptor,"token TEMPLATE");
        RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
        RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");

        try {
            // ANTLRParser.g:898:5: ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] | TEMPLATE -> ID[$TEMPLATE] )
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
                    // ANTLRParser.g:898:7: RULE_REF
                    {
                    RULE_REF231=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_id4690); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_RULE_REF.add(RULE_REF231);



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
                    // 898:17: -> ID[$RULE_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, RULE_REF231));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // ANTLRParser.g:899:7: TOKEN_REF
                    {
                    TOKEN_REF232=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_id4703); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TOKEN_REF.add(TOKEN_REF232);



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
                    // 899:17: -> ID[$TOKEN_REF]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TOKEN_REF232));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // ANTLRParser.g:900:7: TEMPLATE
                    {
                    TEMPLATE233=(Token)match(input,TEMPLATE,FOLLOW_TEMPLATE_in_id4715); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_TEMPLATE.add(TEMPLATE233);



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
                    // 900:17: -> ID[$TEMPLATE]
                    {
                        adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TEMPLATE233));

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
    // ANTLRParser.g:903:1: qid : id ( DOT id )* -> ID[$qid.start, $text] ;
    public final ANTLRParser.qid_return qid() throws RecognitionException {
        ANTLRParser.qid_return retval = new ANTLRParser.qid_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token DOT235=null;
        ANTLRParser.id_return id234 = null;

        ANTLRParser.id_return id236 = null;


        GrammarAST DOT235_tree=null;
        RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
        RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
        try {
            // ANTLRParser.g:903:5: ( id ( DOT id )* -> ID[$qid.start, $text] )
            // ANTLRParser.g:903:7: id ( DOT id )*
            {
            pushFollow(FOLLOW_id_in_qid4738);
            id234=id();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_id.add(id234.getTree());
            // ANTLRParser.g:903:10: ( DOT id )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==DOT) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // ANTLRParser.g:903:11: DOT id
            	    {
            	    DOT235=(Token)match(input,DOT,FOLLOW_DOT_in_qid4741); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_DOT.add(DOT235);

            	    pushFollow(FOLLOW_id_in_qid4743);
            	    id236=id();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_id.add(id236.getTree());

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
            // 903:20: -> ID[$qid.start, $text]
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
    // ANTLRParser.g:905:1: alternativeEntry : alternative EOF ;
    public final ANTLRParser.alternativeEntry_return alternativeEntry() throws RecognitionException {
        ANTLRParser.alternativeEntry_return retval = new ANTLRParser.alternativeEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF238=null;
        ANTLRParser.alternative_return alternative237 = null;


        GrammarAST EOF238_tree=null;

        try {
            // ANTLRParser.g:905:18: ( alternative EOF )
            // ANTLRParser.g:905:20: alternative EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_alternative_in_alternativeEntry4759);
            alternative237=alternative();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, alternative237.getTree());
            EOF238=(Token)match(input,EOF,FOLLOW_EOF_in_alternativeEntry4761); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            EOF238_tree = (GrammarAST)adaptor.create(EOF238);
            adaptor.addChild(root_0, EOF238_tree);
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
    // ANTLRParser.g:906:1: elementEntry : element EOF ;
    public final ANTLRParser.elementEntry_return elementEntry() throws RecognitionException {
        ANTLRParser.elementEntry_return retval = new ANTLRParser.elementEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF240=null;
        ANTLRParser.element_return element239 = null;


        GrammarAST EOF240_tree=null;

        try {
            // ANTLRParser.g:906:14: ( element EOF )
            // ANTLRParser.g:906:16: element EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_element_in_elementEntry4770);
            element239=element();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, element239.getTree());
            EOF240=(Token)match(input,EOF,FOLLOW_EOF_in_elementEntry4772); if (state.failed) return retval;
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
    // $ANTLR end "elementEntry"

    public static class ruleEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "ruleEntry"
    // ANTLRParser.g:907:1: ruleEntry : rule EOF ;
    public final ANTLRParser.ruleEntry_return ruleEntry() throws RecognitionException {
        ANTLRParser.ruleEntry_return retval = new ANTLRParser.ruleEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF242=null;
        ANTLRParser.rule_return rule241 = null;


        GrammarAST EOF242_tree=null;

        try {
            // ANTLRParser.g:907:11: ( rule EOF )
            // ANTLRParser.g:907:13: rule EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_rule_in_ruleEntry4780);
            rule241=rule();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, rule241.getTree());
            EOF242=(Token)match(input,EOF,FOLLOW_EOF_in_ruleEntry4782); if (state.failed) return retval;
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
    // $ANTLR end "ruleEntry"

    public static class blockEntry_return extends ParserRuleReturnScope {
        GrammarAST tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "blockEntry"
    // ANTLRParser.g:908:1: blockEntry : block EOF ;
    public final ANTLRParser.blockEntry_return blockEntry() throws RecognitionException {
        ANTLRParser.blockEntry_return retval = new ANTLRParser.blockEntry_return();
        retval.start = input.LT(1);

        GrammarAST root_0 = null;

        Token EOF244=null;
        ANTLRParser.block_return block243 = null;


        GrammarAST EOF244_tree=null;

        try {
            // ANTLRParser.g:908:12: ( block EOF )
            // ANTLRParser.g:908:14: block EOF
            {
            root_0 = (GrammarAST)adaptor.nil();

            pushFollow(FOLLOW_block_in_blockEntry4790);
            block243=block();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, block243.getTree());
            EOF244=(Token)match(input,EOF,FOLLOW_EOF_in_blockEntry4792); if (state.failed) return retval;
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
    // $ANTLR end "blockEntry"

    // $ANTLR start synpred1_ANTLRParser
    public final void synpred1_ANTLRParser_fragment() throws RecognitionException {   
        // ANTLRParser.g:793:7: ( rewriteTemplate )
        // ANTLRParser.g:793:7: rewriteTemplate
        {
        pushFollow(FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4099);
        rewriteTemplate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_ANTLRParser

    // $ANTLR start synpred2_ANTLRParser
    public final void synpred2_ANTLRParser_fragment() throws RecognitionException {   
        // ANTLRParser.g:799:7: ( rewriteTreeAlt )
        // ANTLRParser.g:799:7: rewriteTreeAlt
        {
        pushFollow(FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4138);
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
            return "556:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT atom ) ) ) | -> atom ) | ebnf | ACTION | SEMPRED ( IMPLIES -> GATED_SEMPRED[$IMPLIES] | -> SEMPRED ) | treeSpec ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK ^( ALT treeSpec ) ) ) | -> treeSpec ) );";
        }
    }
    static final String DFA44_eotS =
        "\26\uffff";
    static final String DFA44_eofS =
        "\1\uffff\1\10\2\5\3\uffff\1\10\2\uffff\1\5\13\uffff";
    static final String DFA44_minS =
        "\1\43\3\4\1\66\2\uffff\1\4\2\uffff\1\4\1\66\10\0\2\uffff";
    static final String DFA44_maxS =
        "\4\103\1\66\2\uffff\1\103\2\uffff\2\103\10\0\2\uffff";
    static final String DFA44_acceptS =
        "\5\uffff\1\4\1\6\1\uffff\1\5\1\1\12\uffff\1\3\1\2";
    static final String DFA44_specialS =
        "\14\uffff\1\4\1\5\1\1\1\7\1\6\1\3\1\0\1\2\2\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\4\22\uffff\1\5\5\uffff\1\6\1\uffff\1\2\1\1\3\uffff\1\3",
            "\1\10\11\uffff\1\10\1\uffff\1\10\22\uffff\1\10\3\uffff\3\10"+
            "\4\uffff\4\10\1\uffff\2\10\1\uffff\1\7\1\11\1\uffff\2\10\1\uffff"+
            "\1\10\1\uffff\2\10\3\uffff\1\10",
            "\1\5\11\uffff\1\5\1\uffff\1\5\22\uffff\1\5\3\uffff\3\5\1\uffff"+
            "\1\5\2\uffff\4\5\1\uffff\2\5\1\uffff\1\12\1\11\1\uffff\2\5\1"+
            "\uffff\1\5\1\uffff\2\5\3\uffff\1\5",
            "\1\5\13\uffff\1\5\22\uffff\1\5\3\uffff\3\5\1\uffff\1\5\2\uffff"+
            "\4\5\1\uffff\2\5\1\uffff\1\5\1\11\1\uffff\2\5\1\uffff\1\5\1"+
            "\uffff\2\5\3\uffff\1\5",
            "\1\13",
            "",
            "",
            "\1\10\13\uffff\1\10\22\uffff\1\10\3\uffff\3\10\1\uffff\1\10"+
            "\2\uffff\4\10\1\uffff\2\10\1\uffff\1\16\2\uffff\2\10\1\uffff"+
            "\1\10\1\uffff\1\14\1\17\3\uffff\1\15",
            "",
            "",
            "\1\5\13\uffff\1\5\22\uffff\1\5\3\uffff\3\5\1\uffff\1\5\2\uffff"+
            "\4\5\1\uffff\2\5\1\uffff\1\22\2\uffff\2\5\1\uffff\1\5\1\uffff"+
            "\1\20\1\23\3\uffff\1\21",
            "\1\24\7\uffff\1\24\1\25\3\uffff\1\24",
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
            return "628:1: atom : ( range ( ROOT | BANG )? | {...}? id DOT ruleref -> ^( DOT id ruleref ) | {...}? id DOT terminal -> ^( DOT id terminal ) | terminal | ruleref | notSet ( ROOT | BANG )? );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA44_18 = input.LA(1);

                         
                        int index44_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 20;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index44_18);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA44_14 = input.LA(1);

                         
                        int index44_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 20;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index44_14);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
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
                    case 3 : 
                        int LA44_17 = input.LA(1);

                         
                        int index44_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 20;}

                        else if ( (true) ) {s = 5;}

                         
                        input.seek(index44_17);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA44_12 = input.LA(1);

                         
                        int index44_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 20;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index44_12);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA44_13 = input.LA(1);

                         
                        int index44_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 20;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index44_13);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
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
                    case 7 : 
                        int LA44_15 = input.LA(1);

                         
                        int index44_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((
                        	    	input.LT(1).getCharPositionInLine()+input.LT(1).getText().length()==
                        	        input.LT(2).getCharPositionInLine() &&
                        	        input.LT(2).getCharPositionInLine()+1==input.LT(3).getCharPositionInLine()
                        	    )) ) {s = 21;}

                        else if ( (true) ) {s = 8;}

                         
                        input.seek(index44_15);
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
            return "790:1: rewriteAlt returns [boolean isTemplate] options {backtrack=true; } : ( rewriteTemplate | rewriteTreeAlt | ETC | -> EPSILON );";
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
        "\1\uffff\3\12\1\uffff\1\12\3\uffff\1\12\3\uffff\3\12\11\uffff\1"+
        "\12\3\uffff\1\12\66\uffff";
    static final String DFA65_minS =
        "\1\20\1\16\2\20\1\43\1\20\2\uffff\1\43\1\20\2\uffff\1\43\3\20\6"+
        "\46\3\43\1\16\3\43\1\20\24\46\6\43\24\46\2\43\6\46";
    static final String DFA65_maxS =
        "\4\103\1\77\1\103\2\uffff\1\77\1\103\2\uffff\1\77\3\103\6\66\1\103"+
        "\2\77\2\103\2\77\1\103\3\66\1\54\11\66\1\54\6\66\1\77\1\103\2\77"+
        "\1\103\1\77\6\66\1\54\11\66\1\54\3\66\2\77\6\66";
    static final String DFA65_acceptS =
        "\6\uffff\1\3\1\4\2\uffff\1\1\1\2\110\uffff";
    static final String DFA65_specialS =
        "\124\uffff}>";
    static final String[] DFA65_transitionS = {
            "\1\5\27\uffff\1\7\14\uffff\1\4\4\uffff\1\6\3\uffff\1\1\1\2\3"+
            "\uffff\1\3",
            "\1\11\1\uffff\1\12\26\uffff\3\12\1\uffff\1\10\2\uffff\1\13"+
            "\1\uffff\2\13\1\uffff\1\12\1\uffff\1\12\3\uffff\2\12\3\uffff"+
            "\2\12\3\uffff\1\12",
            "\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\12\26\uffff\3\12\1\uffff\1\14\2\uffff\1\13\1\uffff\2\13"+
            "\1\uffff\1\12\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff"+
            "\1\12",
            "\1\17\32\uffff\1\16\1\15",
            "\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "",
            "",
            "\1\22\32\uffff\1\21\1\20",
            "\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "",
            "",
            "\1\25\32\uffff\1\24\1\23",
            "\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\30\5\uffff\1\31\1\26\10\uffff\1\27",
            "\1\30\5\uffff\1\31\1\26\10\uffff\1\27",
            "\1\30\5\uffff\1\31\1\26\10\uffff\1\27",
            "\1\34\5\uffff\1\35\1\32\10\uffff\1\33",
            "\1\34\5\uffff\1\35\1\32\10\uffff\1\33",
            "\1\34\5\uffff\1\35\1\32\10\uffff\1\33",
            "\1\40\32\uffff\1\37\1\36\3\uffff\1\41",
            "\1\44\32\uffff\1\43\1\42",
            "\1\47\32\uffff\1\46\1\45",
            "\1\11\1\uffff\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13"+
            "\1\uffff\1\12\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff"+
            "\1\12",
            "\1\52\32\uffff\1\51\1\50\3\uffff\1\53",
            "\1\56\32\uffff\1\55\1\54",
            "\1\61\32\uffff\1\60\1\57",
            "\1\12\26\uffff\3\12\4\uffff\1\13\1\uffff\2\13\1\uffff\1\12"+
            "\1\uffff\1\12\3\uffff\2\12\3\uffff\2\12\3\uffff\1\12",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31",
            "\1\30\5\uffff\1\31\11\uffff\1\27",
            "\1\30\5\uffff\1\31\11\uffff\1\27",
            "\1\30\5\uffff\1\31\11\uffff\1\27",
            "\1\30\5\uffff\1\31\1\63\10\uffff\1\64",
            "\1\30\5\uffff\1\31\1\63\10\uffff\1\64",
            "\1\30\5\uffff\1\31\1\63\10\uffff\1\64",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35",
            "\1\34\5\uffff\1\35\11\uffff\1\33",
            "\1\34\5\uffff\1\35\11\uffff\1\33",
            "\1\34\5\uffff\1\35\11\uffff\1\33",
            "\1\34\5\uffff\1\35\1\66\10\uffff\1\67",
            "\1\34\5\uffff\1\35\1\66\10\uffff\1\67",
            "\1\34\5\uffff\1\35\1\66\10\uffff\1\67",
            "\1\72\32\uffff\1\71\1\70",
            "\1\75\32\uffff\1\74\1\73\3\uffff\1\76",
            "\1\101\32\uffff\1\100\1\77",
            "\1\104\32\uffff\1\103\1\102",
            "\1\107\32\uffff\1\106\1\105\3\uffff\1\110",
            "\1\113\32\uffff\1\112\1\111",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\62",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31\11\uffff\1\114",
            "\1\30\5\uffff\1\31",
            "\1\30\5\uffff\1\31\11\uffff\1\64",
            "\1\30\5\uffff\1\31\11\uffff\1\64",
            "\1\30\5\uffff\1\31\11\uffff\1\64",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\65",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35\11\uffff\1\115",
            "\1\34\5\uffff\1\35",
            "\1\34\5\uffff\1\35\11\uffff\1\67",
            "\1\34\5\uffff\1\35\11\uffff\1\67",
            "\1\34\5\uffff\1\35\11\uffff\1\67",
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
            return "810:1: rewriteTreeElement : ( rewriteTreeAtom | rewriteTreeAtom ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTreeAtom ) ) ) | rewriteTree ( ebnfSuffix -> ^( ebnfSuffix ^( REWRITE_BLOCK ^( ALT rewriteTree ) ) ) | -> rewriteTree ) | rewriteTreeEbnf );";
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
            return "845:1: rewriteTemplate : ( TEMPLATE LPAREN rewriteTemplateArgs RPAREN (str= DOUBLE_QUOTE_STRING_LITERAL | str= DOUBLE_ANGLE_STRING_LITERAL ) -> ^( TEMPLATE[$TEMPLATE,\"TEMPLATE\"] ( rewriteTemplateArgs )? $str) | rewriteTemplateRef | rewriteIndirectTemplateHead | ACTION );";
        }
    }
 

    public static final BitSet FOLLOW_DOC_COMMENT_in_grammarSpec459 = new BitSet(new long[]{0x000000000F000000L});
    public static final BitSet FOLLOW_grammarType_in_grammarSpec496 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_grammarSpec498 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_grammarSpec500 = new BitSet(new long[]{0xC800000870F80040L});
    public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec544 = new BitSet(new long[]{0xC800000870F80040L});
    public static final BitSet FOLLOW_rules_in_grammarSpec572 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_grammarSpec615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_grammarType809 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARSER_in_grammarType846 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_in_grammarType877 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GRAMMAR_in_grammarType908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct1048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrScope_in_prequelConstruct1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_action_in_prequelConstruct1127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPTIONS_in_optionsSpec1144 = new BitSet(new long[]{0xE000000800000000L});
    public static final BitSet FOLLOW_option_in_optionsSpec1147 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_optionsSpec1149 = new BitSet(new long[]{0xE000000800000000L});
    public static final BitSet FOLLOW_RBRACE_in_optionsSpec1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_option1190 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_option1192 = new BitSet(new long[]{0xC001000800000000L,0x0000000000000009L});
    public static final BitSet FOLLOW_optionValue_in_option1195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_optionValue1245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_optionValue1269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_optionValue1295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_optionValue1324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_delegateGrammars1340 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1342 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_COMMA_in_delegateGrammars1345 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1347 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_SEMI_in_delegateGrammars1351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1378 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar1380 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_delegateGrammar1393 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKENS_in_tokensSpec1409 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_tokenSpec_in_tokensSpec1411 = new BitSet(new long[]{0xE000000800000000L});
    public static final BitSet FOLLOW_RBRACE_in_tokensSpec1414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_tokenSpec1434 = new BitSet(new long[]{0x0000208000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_tokenSpec1440 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_tokenSpec1442 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_tokenSpec1477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_tokenSpec1482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_attrScope1497 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_attrScope1499 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_attrScope1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_action1530 = new BitSet(new long[]{0xC000000803000000L});
    public static final BitSet FOLLOW_actionScopeName_in_action1533 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_COLONCOLON_in_action1535 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_action1539 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_action1541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_actionScopeName1572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEXER_in_actionScopeName1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PARSER_in_actionScopeName1592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_rules1611 = new BitSet(new long[]{0xC000000870800042L});
    public static final BitSet FOLLOW_DOC_COMMENT_in_rule1690 = new BitSet(new long[]{0xC000000870800000L});
    public static final BitSet FOLLOW_ruleModifiers_in_rule1734 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_rule1757 = new BitSet(new long[]{0x0800001180284000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rule1790 = new BitSet(new long[]{0x0800001180280000L});
    public static final BitSet FOLLOW_ruleReturns_in_rule1800 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_rulePrequel_in_rule1838 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_COLON_in_rule1854 = new BitSet(new long[]{0xD648010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_altListAsBlock_in_rule1883 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_SEMI_in_rule1898 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_exceptionGroup_in_rule1907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup1999 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_finallyClause_in_exceptionGroup2002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CATCH_in_exceptionHandler2019 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler2021 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_exceptionHandler2023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FINALLY_in_finallyClause2049 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_finallyClause2051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_throwsSpec_in_rulePrequel2081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleScopeSpec_in_rulePrequel2089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optionsSpec_in_rulePrequel2097 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAction_in_rulePrequel2105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURNS_in_ruleReturns2125 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns2128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THROWS_in_throwsSpec2153 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2155 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_COMMA_in_throwsSpec2158 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_qid_in_throwsSpec2160 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2191 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleScopeSpec2193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SCOPE_in_ruleScopeSpec2206 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2208 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_COMMA_in_ruleScopeSpec2211 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_ruleScopeSpec2213 = new BitSet(new long[]{0x000000C000000000L});
    public static final BitSet FOLLOW_SEMI_in_ruleScopeSpec2217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_ruleAction2247 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_ruleAction2249 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_ruleAction2251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleModifier_in_ruleModifiers2292 = new BitSet(new long[]{0x0000000070800002L});
    public static final BitSet FOLLOW_set_in_ruleModifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alternative_in_altList2368 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_OR_in_altList2371 = new BitSet(new long[]{0xD648010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_alternative_in_altList2373 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_altList_in_altListAsBlock2403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elements_in_alternative2433 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewrite_in_alternative2480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elements2533 = new BitSet(new long[]{0xD440010800010012L,0x0000000000000008L});
    public static final BitSet FOLLOW_labeledElement_in_element2560 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_element2608 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnf_in_element2657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_element2664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMPRED_in_element2674 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_element2680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_treeSpec_in_element2708 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_element2714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_labeledElement2763 = new BitSet(new long[]{0x0004200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_labeledElement2766 = new BitSet(new long[]{0xD040010800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement2769 = new BitSet(new long[]{0xD040010800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_atom_in_labeledElement2774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_labeledElement2776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_treeSpec2798 = new BitSet(new long[]{0xD440010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_element_in_treeSpec2839 = new BitSet(new long[]{0xD440010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_element_in_treeSpec2870 = new BitSet(new long[]{0xD440030800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_RPAREN_in_treeSpec2879 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_ebnf2913 = new BitSet(new long[]{0x0013C40000000002L});
    public static final BitSet FOLLOW_blockSuffixe_in_ebnf2948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffixe2999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ROOT_in_blockSuffixe3013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPLIES_in_blockSuffixe3021 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_blockSuffixe3032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_ebnfSuffix3063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_ebnfSuffix3078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_range_in_atom3095 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_atom3098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_atom3103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom3150 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_DOT_in_atom3152 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_ruleref_in_atom3154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_atom3188 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_DOT_in_atom3190 = new BitSet(new long[]{0x4040000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_terminal_in_atom3192 = new BitSet(new long[]{0x0000000000000002L});
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
    public static final BitSet FOLLOW_LPAREN_in_block3368 = new BitSet(new long[]{0xDE48011900290010L,0x0000000000000008L});
    public static final BitSet FOLLOW_optionsSpec_in_block3405 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_ruleAction_in_block3410 = new BitSet(new long[]{0x0800001100280000L});
    public static final BitSet FOLLOW_COLON_in_block3413 = new BitSet(new long[]{0xD648010800010010L,0x0000000000000008L});
    public static final BitSet FOLLOW_altList_in_block3427 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_block3444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_ruleref3493 = new BitSet(new long[]{0x0010800000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_ruleref3495 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_ruleref3505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_ruleref3509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rangeElement_in_range3574 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_RANGE_in_range3576 = new BitSet(new long[]{0xC000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rangeElement_in_range3579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_rangeElement0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_terminal3673 = new BitSet(new long[]{0x0010880000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_terminal3675 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3678 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_terminal3702 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3704 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_DOT_in_terminal3751 = new BitSet(new long[]{0x0010880000000002L});
    public static final BitSet FOLLOW_elementOptions_in_terminal3753 = new BitSet(new long[]{0x0010800000000002L});
    public static final BitSet FOLLOW_ROOT_in_terminal3784 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BANG_in_terminal3808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_elementOptions3872 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions3874 = new BitSet(new long[]{0x0000104000000000L});
    public static final BitSet FOLLOW_COMMA_in_elementOptions3877 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_elementOption_in_elementOptions3879 = new BitSet(new long[]{0x0000104000000000L});
    public static final BitSet FOLLOW_GT_in_elementOptions3883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qid_in_elementOption3918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_elementOption3940 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_elementOption3942 = new BitSet(new long[]{0xC000000800000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_qid_in_elementOption3946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption3950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicatedRewrite_in_rewrite3968 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_nakedRewrite_in_rewrite3971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_predicatedRewrite3989 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_SEMPRED_in_predicatedRewrite3991 = new BitSet(new long[]{0xC520010800010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteAlt_in_predicatedRewrite3993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RARROW_in_nakedRewrite4033 = new BitSet(new long[]{0xC520010800010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteAlt_in_nakedRewrite4035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_rewriteAlt4099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteAlt4138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ETC_in_rewriteAlt4147 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTreeAlt4178 = new BitSet(new long[]{0xC420010000010002L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTreeElement4207 = new BitSet(new long[]{0x0003400000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTree_in_rewriteTreeElement4234 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeElement4240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeEbnf_in_rewriteTreeElement4279 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_rewriteTreeAtom4295 = new BitSet(new long[]{0x0000080000004002L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom4297 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_ARG_ACTION_in_rewriteTreeAtom4300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_rewriteTreeAtom4327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_in_rewriteTreeAtom4334 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_elementOptions_in_rewriteTreeAtom4336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOLLAR_in_rewriteTreeAtom4359 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_rewriteTreeAtom4361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTreeAtom4372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTreeEbnf4398 = new BitSet(new long[]{0xC420010000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_rewriteTreeEbnf4400 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTreeEbnf4402 = new BitSet(new long[]{0x0003400000000000L});
    public static final BitSet FOLLOW_ebnfSuffix_in_rewriteTreeEbnf4404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TREE_BEGIN_in_rewriteTree4428 = new BitSet(new long[]{0xC020000000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeAtom_in_rewriteTree4430 = new BitSet(new long[]{0xC420030000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_rewriteTreeElement_in_rewriteTree4432 = new BitSet(new long[]{0xC420030000010000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTree4435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_rewriteTemplate4467 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplate4469 = new BitSet(new long[]{0xC000020800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplate4471 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplate4473 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_DOUBLE_QUOTE_STRING_LITERAL_in_rewriteTemplate4481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_ANGLE_STRING_LITERAL_in_rewriteTemplate4487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateRef_in_rewriteTemplate4513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteIndirectTemplateHead_in_rewriteTemplate4522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplate4531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateRef4547 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteTemplateRef4549 = new BitSet(new long[]{0xC000020800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteTemplateRef4551 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteTemplateRef4553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4582 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteIndirectTemplateHead4584 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4586 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_LPAREN_in_rewriteIndirectTemplateHead4588 = new BitSet(new long[]{0xC000020800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArgs_in_rewriteIndirectTemplateHead4590 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_RPAREN_in_rewriteIndirectTemplateHead4592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4620 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_COMMA_in_rewriteTemplateArgs4623 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_rewriteTemplateArg_in_rewriteTemplateArgs4625 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_id_in_rewriteTemplateArg4654 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ASSIGN_in_rewriteTemplateArg4656 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_ACTION_in_rewriteTemplateArg4658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_REF_in_id4690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TOKEN_REF_in_id4703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_id4715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_qid4738 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_DOT_in_qid4741 = new BitSet(new long[]{0xC000000800000000L});
    public static final BitSet FOLLOW_id_in_qid4743 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_alternative_in_alternativeEntry4759 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_alternativeEntry4761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_element_in_elementEntry4770 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_elementEntry4772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_ruleEntry4780 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_ruleEntry4782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_block_in_blockEntry4790 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_blockEntry4792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTemplate_in_synpred1_ANTLRParser4099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rewriteTreeAlt_in_synpred2_ANTLRParser4138 = new BitSet(new long[]{0x0000000000000002L});

}