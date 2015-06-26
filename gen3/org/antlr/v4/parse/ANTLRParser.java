// $ANTLR 3.5.2 /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g 2015-06-23 21:59:56

/*
 [The "BSD licence"]
 Copyright (c) 2005-20012 Terence Parr
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
import org.antlr.v4.tool.ast.*;

import java.util.ArrayDeque;
import java.util.Deque;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


/** The definitive ANTLR v3 grammar to parse ANTLR v4 grammars.
 *  The grammar builds ASTs that are sniffed by subsequent stages.
 */
@SuppressWarnings("all")
public class ANTLRParser extends Parser {
	public static final String[] tokenNames = new String[] {
		"<invalid>", "<EOR>", "<DOWN>", "<UP>", "ACTION", "ACTION_CHAR_LITERAL", 
		"ACTION_ESC", "ACTION_STRING_LITERAL", "ARG_ACTION", "ARG_OR_CHARSET", 
		"ASSIGN", "AT", "CATCH", "CHANNELS", "COLON", "COLONCOLON", "COMMA", "COMMENT", 
		"DOC_COMMENT", "DOLLAR", "DOT", "ERRCHAR", "ESC_SEQ", "FINALLY", "FRAGMENT", 
		"GRAMMAR", "GT", "HEX_DIGIT", "ID", "IMPORT", "INT", "LEXER", "LEXER_CHAR_SET", 
		"LOCALS", "LPAREN", "LT", "MODE", "NESTED_ACTION", "NLCHARS", "NOT", "NameChar", 
		"NameStartChar", "OPTIONS", "OR", "PARSER", "PLUS", "PLUS_ASSIGN", "POUND", 
		"PRIVATE", "PROTECTED", "PUBLIC", "QUESTION", "RANGE", "RARROW", "RBRACE", 
		"RETURNS", "RPAREN", "RULE_REF", "SEMI", "SEMPRED", "SRC", "STAR", "STRING_LITERAL", 
		"SYNPRED", "THROWS", "TOKENS_SPEC", "TOKEN_REF", "TREE_GRAMMAR", "UNICODE_ESC", 
		"UnicodeBOM", "WS", "WSCHARS", "WSNLCHARS", "ALT", "ALTLIST", "ARG", "ARGLIST", 
		"BLOCK", "CHAR_RANGE", "CLOSURE", "COMBINED", "ELEMENT_OPTIONS", "EPSILON", 
		"INITACTION", "LABEL", "LEXER_ACTION_CALL", "LEXER_ALT_ACTION", "LIST", 
		"OPTIONAL", "POSITIVE_CLOSURE", "PREC_RULE", "RESULT", "RET", "RULE", 
		"RULEACTIONS", "RULEMODIFIERS", "RULES", "SET", "TEMPLATE", "WILDCARD"
	};
	public static final int EOF=-1;
	public static final int ACTION=4;
	public static final int ACTION_CHAR_LITERAL=5;
	public static final int ACTION_ESC=6;
	public static final int ACTION_STRING_LITERAL=7;
	public static final int ARG_ACTION=8;
	public static final int ARG_OR_CHARSET=9;
	public static final int ASSIGN=10;
	public static final int AT=11;
	public static final int CATCH=12;
	public static final int CHANNELS=13;
	public static final int COLON=14;
	public static final int COLONCOLON=15;
	public static final int COMMA=16;
	public static final int COMMENT=17;
	public static final int DOC_COMMENT=18;
	public static final int DOLLAR=19;
	public static final int DOT=20;
	public static final int ERRCHAR=21;
	public static final int ESC_SEQ=22;
	public static final int FINALLY=23;
	public static final int FRAGMENT=24;
	public static final int GRAMMAR=25;
	public static final int GT=26;
	public static final int HEX_DIGIT=27;
	public static final int ID=28;
	public static final int IMPORT=29;
	public static final int INT=30;
	public static final int LEXER=31;
	public static final int LEXER_CHAR_SET=32;
	public static final int LOCALS=33;
	public static final int LPAREN=34;
	public static final int LT=35;
	public static final int MODE=36;
	public static final int NESTED_ACTION=37;
	public static final int NLCHARS=38;
	public static final int NOT=39;
	public static final int NameChar=40;
	public static final int NameStartChar=41;
	public static final int OPTIONS=42;
	public static final int OR=43;
	public static final int PARSER=44;
	public static final int PLUS=45;
	public static final int PLUS_ASSIGN=46;
	public static final int POUND=47;
	public static final int PRIVATE=48;
	public static final int PROTECTED=49;
	public static final int PUBLIC=50;
	public static final int QUESTION=51;
	public static final int RANGE=52;
	public static final int RARROW=53;
	public static final int RBRACE=54;
	public static final int RETURNS=55;
	public static final int RPAREN=56;
	public static final int RULE_REF=57;
	public static final int SEMI=58;
	public static final int SEMPRED=59;
	public static final int SRC=60;
	public static final int STAR=61;
	public static final int STRING_LITERAL=62;
	public static final int SYNPRED=63;
	public static final int THROWS=64;
	public static final int TOKENS_SPEC=65;
	public static final int TOKEN_REF=66;
	public static final int TREE_GRAMMAR=67;
	public static final int UNICODE_ESC=68;
	public static final int UnicodeBOM=69;
	public static final int WS=70;
	public static final int WSCHARS=71;
	public static final int WSNLCHARS=72;
	public static final int ALT=73;
	public static final int ALTLIST=74;
	public static final int ARG=75;
	public static final int ARGLIST=76;
	public static final int BLOCK=77;
	public static final int CHAR_RANGE=78;
	public static final int CLOSURE=79;
	public static final int COMBINED=80;
	public static final int ELEMENT_OPTIONS=81;
	public static final int EPSILON=82;
	public static final int INITACTION=83;
	public static final int LABEL=84;
	public static final int LEXER_ACTION_CALL=85;
	public static final int LEXER_ALT_ACTION=86;
	public static final int LIST=87;
	public static final int OPTIONAL=88;
	public static final int POSITIVE_CLOSURE=89;
	public static final int PREC_RULE=90;
	public static final int RESULT=91;
	public static final int RET=92;
	public static final int RULE=93;
	public static final int RULEACTIONS=94;
	public static final int RULEMODIFIERS=95;
	public static final int RULES=96;
	public static final int SET=97;
	public static final int TEMPLATE=98;
	public static final int WILDCARD=99;

	// delegates
	public Parser[] getDelegates() {
		return new Parser[] {};
	}

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
	@Override public String[] getTokenNames() { return ANTLRParser.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g"; }


	Deque<String> paraphrases = new ArrayDeque<String>();
	public void grammarError(ErrorType etype, org.antlr.runtime.Token token, Object... args) { }


	public static class grammarSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "grammarSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:146:1: grammarSpec : grammarType id SEMI sync ( prequelConstruct sync )* rules ( modeSpec )* EOF -> ^( grammarType id ( prequelConstruct )* rules ( modeSpec )* ) ;
	public final ANTLRParser.grammarSpec_return grammarSpec() throws RecognitionException {
		ANTLRParser.grammarSpec_return retval = new ANTLRParser.grammarSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token SEMI3=null;
		Token EOF9=null;
		ParserRuleReturnScope grammarType1 =null;
		ParserRuleReturnScope id2 =null;
		ParserRuleReturnScope sync4 =null;
		ParserRuleReturnScope prequelConstruct5 =null;
		ParserRuleReturnScope sync6 =null;
		ParserRuleReturnScope rules7 =null;
		ParserRuleReturnScope modeSpec8 =null;

		GrammarAST SEMI3_tree=null;
		GrammarAST EOF9_tree=null;
		RewriteRuleTokenStream stream_EOF=new RewriteRuleTokenStream(adaptor,"token EOF");
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
		RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
		RewriteRuleSubtreeStream stream_modeSpec=new RewriteRuleSubtreeStream(adaptor,"rule modeSpec");
		RewriteRuleSubtreeStream stream_prequelConstruct=new RewriteRuleSubtreeStream(adaptor,"rule prequelConstruct");
		RewriteRuleSubtreeStream stream_grammarType=new RewriteRuleSubtreeStream(adaptor,"rule grammarType");
		RewriteRuleSubtreeStream stream_rules=new RewriteRuleSubtreeStream(adaptor,"rule rules");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:153:5: ( grammarType id SEMI sync ( prequelConstruct sync )* rules ( modeSpec )* EOF -> ^( grammarType id ( prequelConstruct )* rules ( modeSpec )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:156:7: grammarType id SEMI sync ( prequelConstruct sync )* rules ( modeSpec )* EOF
			{
			pushFollow(FOLLOW_grammarType_in_grammarSpec396);
			grammarType1=grammarType();
			state._fsp--;

			stream_grammarType.add(grammarType1.getTree());
			pushFollow(FOLLOW_id_in_grammarSpec398);
			id2=id();
			state._fsp--;

			stream_id.add(id2.getTree());
			SEMI3=(Token)match(input,SEMI,FOLLOW_SEMI_in_grammarSpec400);  
			stream_SEMI.add(SEMI3);

			pushFollow(FOLLOW_sync_in_grammarSpec438);
			sync4=sync();
			state._fsp--;

			stream_sync.add(sync4.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:169:12: ( prequelConstruct sync )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==AT||LA1_0==CHANNELS||LA1_0==IMPORT||LA1_0==OPTIONS||LA1_0==TOKENS_SPEC) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:169:14: prequelConstruct sync
					{
					pushFollow(FOLLOW_prequelConstruct_in_grammarSpec442);
					prequelConstruct5=prequelConstruct();
					state._fsp--;

					stream_prequelConstruct.add(prequelConstruct5.getTree());
					pushFollow(FOLLOW_sync_in_grammarSpec444);
					sync6=sync();
					state._fsp--;

					stream_sync.add(sync6.getTree());
					}
					break;

				default :
					break loop1;
				}
			}

			pushFollow(FOLLOW_rules_in_grammarSpec469);
			rules7=rules();
			state._fsp--;

			stream_rules.add(rules7.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:177:4: ( modeSpec )*
			loop2:
			while (true) {
				int alt2=2;
				int LA2_0 = input.LA(1);
				if ( (LA2_0==MODE) ) {
					alt2=1;
				}

				switch (alt2) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:177:4: modeSpec
					{
					pushFollow(FOLLOW_modeSpec_in_grammarSpec475);
					modeSpec8=modeSpec();
					state._fsp--;

					stream_modeSpec.add(modeSpec8.getTree());
					}
					break;

				default :
					break loop2;
				}
			}

			EOF9=(Token)match(input,EOF,FOLLOW_EOF_in_grammarSpec513);  
			stream_EOF.add(EOF9);

			// AST REWRITE
			// elements: modeSpec, rules, id, prequelConstruct, grammarType
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 190:7: -> ^( grammarType id ( prequelConstruct )* rules ( modeSpec )* )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:190:10: ^( grammarType id ( prequelConstruct )* rules ( modeSpec )* )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(stream_grammarType.nextNode(), root_1);
				adaptor.addChild(root_1, stream_id.nextTree());
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:192:14: ( prequelConstruct )*
				while ( stream_prequelConstruct.hasNext() ) {
					adaptor.addChild(root_1, stream_prequelConstruct.nextTree());
				}
				stream_prequelConstruct.reset();

				adaptor.addChild(root_1, stream_rules.nextTree());
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:194:14: ( modeSpec )*
				while ( stream_modeSpec.hasNext() ) {
					adaptor.addChild(root_1, stream_modeSpec.nextTree());
				}
				stream_modeSpec.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


			GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.OPTIONS);
			if ( options!=null ) {
				Grammar.setNodeOptions(retval.tree, options);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "grammarSpec"


	public static class grammarType_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "grammarType"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:198:1: grammarType : (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\", getTokenStream()] |t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\", getTokenStream()] |g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\", getTokenStream()] |tg= TREE_GRAMMAR ) ;
	public final ANTLRParser.grammarType_return grammarType() throws RecognitionException {
		ANTLRParser.grammarType_return retval = new ANTLRParser.grammarType_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token t=null;
		Token g=null;
		Token tg=null;

		GrammarAST t_tree=null;
		GrammarAST g_tree=null;
		GrammarAST tg_tree=null;
		RewriteRuleTokenStream stream_TREE_GRAMMAR=new RewriteRuleTokenStream(adaptor,"token TREE_GRAMMAR");
		RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
		RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");
		RewriteRuleTokenStream stream_GRAMMAR=new RewriteRuleTokenStream(adaptor,"token GRAMMAR");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:204:5: ( (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\", getTokenStream()] |t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\", getTokenStream()] |g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\", getTokenStream()] |tg= TREE_GRAMMAR ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:204:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\", getTokenStream()] |t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\", getTokenStream()] |g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\", getTokenStream()] |tg= TREE_GRAMMAR )
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:204:7: (t= LEXER g= GRAMMAR -> GRAMMAR[$g, \"LEXER_GRAMMAR\", getTokenStream()] |t= PARSER g= GRAMMAR -> GRAMMAR[$g, \"PARSER_GRAMMAR\", getTokenStream()] |g= GRAMMAR -> GRAMMAR[$g, \"COMBINED_GRAMMAR\", getTokenStream()] |tg= TREE_GRAMMAR )
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
			case GRAMMAR:
				{
				alt3=3;
				}
				break;
			case TREE_GRAMMAR:
				{
				alt3=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}
			switch (alt3) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:204:9: t= LEXER g= GRAMMAR
					{
					t=(Token)match(input,LEXER,FOLLOW_LEXER_in_grammarType683);  
					stream_LEXER.add(t);

					g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType687);  
					stream_GRAMMAR.add(g);

					// AST REWRITE
					// elements: GRAMMAR
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 204:28: -> GRAMMAR[$g, \"LEXER_GRAMMAR\", getTokenStream()]
					{
						adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "LEXER_GRAMMAR", getTokenStream()));
					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:206:6: t= PARSER g= GRAMMAR
					{
					t=(Token)match(input,PARSER,FOLLOW_PARSER_in_grammarType710);  
					stream_PARSER.add(t);

					g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType714);  
					stream_GRAMMAR.add(g);

					// AST REWRITE
					// elements: GRAMMAR
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 206:25: -> GRAMMAR[$g, \"PARSER_GRAMMAR\", getTokenStream()]
					{
						adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "PARSER_GRAMMAR", getTokenStream()));
					}


					retval.tree = root_0;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:209:6: g= GRAMMAR
					{
					g=(Token)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarType735);  
					stream_GRAMMAR.add(g);

					// AST REWRITE
					// elements: GRAMMAR
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 209:25: -> GRAMMAR[$g, \"COMBINED_GRAMMAR\", getTokenStream()]
					{
						adaptor.addChild(root_0, new GrammarRootAST(GRAMMAR, g, "COMBINED_GRAMMAR", getTokenStream()));
					}


					retval.tree = root_0;

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:210:7: tg= TREE_GRAMMAR
					{
					tg=(Token)match(input,TREE_GRAMMAR,FOLLOW_TREE_GRAMMAR_in_grammarType762);  
					stream_TREE_GRAMMAR.add(tg);

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


				if ( tg!=null ) throw new v3TreeGrammarException(tg);
				if ( t!=null ) ((GrammarRootAST)retval.tree).grammarType = (t!=null?t.getType():0);
				else ((GrammarRootAST)retval.tree).grammarType=COMBINED;

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "grammarType"


	public static class prequelConstruct_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "prequelConstruct"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:218:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | channelsSpec | action );
	public final ANTLRParser.prequelConstruct_return prequelConstruct() throws RecognitionException {
		ANTLRParser.prequelConstruct_return retval = new ANTLRParser.prequelConstruct_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope optionsSpec10 =null;
		ParserRuleReturnScope delegateGrammars11 =null;
		ParserRuleReturnScope tokensSpec12 =null;
		ParserRuleReturnScope channelsSpec13 =null;
		ParserRuleReturnScope action14 =null;


		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:219:2: ( optionsSpec | delegateGrammars | tokensSpec | channelsSpec | action )
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
			case TOKENS_SPEC:
				{
				alt4=3;
				}
				break;
			case CHANNELS:
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
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:220:4: optionsSpec
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_optionsSpec_in_prequelConstruct788);
					optionsSpec10=optionsSpec();
					state._fsp--;

					adaptor.addChild(root_0, optionsSpec10.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:224:7: delegateGrammars
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct811);
					delegateGrammars11=delegateGrammars();
					state._fsp--;

					adaptor.addChild(root_0, delegateGrammars11.getTree());

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:231:7: tokensSpec
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_tokensSpec_in_prequelConstruct855);
					tokensSpec12=tokensSpec();
					state._fsp--;

					adaptor.addChild(root_0, tokensSpec12.getTree());

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:234:4: channelsSpec
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_channelsSpec_in_prequelConstruct865);
					channelsSpec13=channelsSpec();
					state._fsp--;

					adaptor.addChild(root_0, channelsSpec13.getTree());

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:240:7: action
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_action_in_prequelConstruct902);
					action14=action();
					state._fsp--;

					adaptor.addChild(root_0, action14.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "prequelConstruct"


	public static class optionsSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "optionsSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:244:1: optionsSpec : OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )* ) ;
	public final ANTLRParser.optionsSpec_return optionsSpec() throws RecognitionException {
		ANTLRParser.optionsSpec_return retval = new ANTLRParser.optionsSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token OPTIONS15=null;
		Token SEMI17=null;
		Token RBRACE18=null;
		ParserRuleReturnScope option16 =null;

		GrammarAST OPTIONS15_tree=null;
		GrammarAST SEMI17_tree=null;
		GrammarAST RBRACE18_tree=null;
		RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleTokenStream stream_OPTIONS=new RewriteRuleTokenStream(adaptor,"token OPTIONS");
		RewriteRuleSubtreeStream stream_option=new RewriteRuleSubtreeStream(adaptor,"rule option");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:245:2: ( OPTIONS ( option SEMI )* RBRACE -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:245:4: OPTIONS ( option SEMI )* RBRACE
			{
			OPTIONS15=(Token)match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec917);  
			stream_OPTIONS.add(OPTIONS15);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:245:12: ( option SEMI )*
			loop5:
			while (true) {
				int alt5=2;
				int LA5_0 = input.LA(1);
				if ( (LA5_0==RULE_REF||LA5_0==TOKEN_REF) ) {
					alt5=1;
				}

				switch (alt5) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:245:13: option SEMI
					{
					pushFollow(FOLLOW_option_in_optionsSpec920);
					option16=option();
					state._fsp--;

					stream_option.add(option16.getTree());
					SEMI17=(Token)match(input,SEMI,FOLLOW_SEMI_in_optionsSpec922);  
					stream_SEMI.add(SEMI17);

					}
					break;

				default :
					break loop5;
				}
			}

			RBRACE18=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_optionsSpec926);  
			stream_RBRACE.add(RBRACE18);

			// AST REWRITE
			// elements: option, OPTIONS
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 245:34: -> ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )* )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:245:37: ^( OPTIONS[$OPTIONS, \"OPTIONS\"] ( option )* )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(OPTIONS, OPTIONS15, "OPTIONS"), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:245:68: ( option )*
				while ( stream_option.hasNext() ) {
					adaptor.addChild(root_1, stream_option.nextTree());
				}
				stream_option.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "optionsSpec"


	public static class option_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "option"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:248:1: option : id ASSIGN ^ optionValue ;
	public final ANTLRParser.option_return option() throws RecognitionException {
		ANTLRParser.option_return retval = new ANTLRParser.option_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token ASSIGN20=null;
		ParserRuleReturnScope id19 =null;
		ParserRuleReturnScope optionValue21 =null;

		GrammarAST ASSIGN20_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:249:5: ( id ASSIGN ^ optionValue )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:249:9: id ASSIGN ^ optionValue
			{
			root_0 = (GrammarAST)adaptor.nil();


			pushFollow(FOLLOW_id_in_option955);
			id19=id();
			state._fsp--;

			adaptor.addChild(root_0, id19.getTree());

			ASSIGN20=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_option957); 
			ASSIGN20_tree = (GrammarAST)adaptor.create(ASSIGN20);
			root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN20_tree, root_0);

			pushFollow(FOLLOW_optionValue_in_option960);
			optionValue21=optionValue();
			state._fsp--;

			adaptor.addChild(root_0, optionValue21.getTree());

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "option"


	public static class optionValue_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "optionValue"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:257:1: optionValue : ( qid | STRING_LITERAL | ACTION | INT );
	public final ANTLRParser.optionValue_return optionValue() throws RecognitionException {
		ANTLRParser.optionValue_return retval = new ANTLRParser.optionValue_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token STRING_LITERAL23=null;
		Token ACTION24=null;
		Token INT25=null;
		ParserRuleReturnScope qid22 =null;

		GrammarAST STRING_LITERAL23_tree=null;
		GrammarAST ACTION24_tree=null;
		GrammarAST INT25_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:258:5: ( qid | STRING_LITERAL | ACTION | INT )
			int alt6=4;
			switch ( input.LA(1) ) {
			case RULE_REF:
			case TOKEN_REF:
				{
				alt6=1;
				}
				break;
			case STRING_LITERAL:
				{
				alt6=2;
				}
				break;
			case ACTION:
				{
				alt6=3;
				}
				break;
			case INT:
				{
				alt6=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 6, 0, input);
				throw nvae;
			}
			switch (alt6) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:261:7: qid
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_qid_in_optionValue1003);
					qid22=qid();
					state._fsp--;

					adaptor.addChild(root_0, qid22.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:262:7: STRING_LITERAL
					{
					root_0 = (GrammarAST)adaptor.nil();


					STRING_LITERAL23=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_optionValue1011); 
					STRING_LITERAL23_tree = (GrammarAST)adaptor.create(STRING_LITERAL23);
					adaptor.addChild(root_0, STRING_LITERAL23_tree);

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:263:4: ACTION
					{
					root_0 = (GrammarAST)adaptor.nil();


					ACTION24=(Token)match(input,ACTION,FOLLOW_ACTION_in_optionValue1016); 
					ACTION24_tree = new ActionAST(ACTION24) ;
					adaptor.addChild(root_0, ACTION24_tree);

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:264:7: INT
					{
					root_0 = (GrammarAST)adaptor.nil();


					INT25=(Token)match(input,INT,FOLLOW_INT_in_optionValue1027); 
					INT25_tree = (GrammarAST)adaptor.create(INT25);
					adaptor.addChild(root_0, INT25_tree);

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "optionValue"


	public static class delegateGrammars_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "delegateGrammars"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:269:1: delegateGrammars : IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) ;
	public final ANTLRParser.delegateGrammars_return delegateGrammars() throws RecognitionException {
		ANTLRParser.delegateGrammars_return retval = new ANTLRParser.delegateGrammars_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token IMPORT26=null;
		Token COMMA28=null;
		Token SEMI30=null;
		ParserRuleReturnScope delegateGrammar27 =null;
		ParserRuleReturnScope delegateGrammar29 =null;

		GrammarAST IMPORT26_tree=null;
		GrammarAST COMMA28_tree=null;
		GrammarAST SEMI30_tree=null;
		RewriteRuleTokenStream stream_IMPORT=new RewriteRuleTokenStream(adaptor,"token IMPORT");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleSubtreeStream stream_delegateGrammar=new RewriteRuleSubtreeStream(adaptor,"rule delegateGrammar");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:270:2: ( IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI -> ^( IMPORT ( delegateGrammar )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:270:4: IMPORT delegateGrammar ( COMMA delegateGrammar )* SEMI
			{
			IMPORT26=(Token)match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars1043);  
			stream_IMPORT.add(IMPORT26);

			pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1045);
			delegateGrammar27=delegateGrammar();
			state._fsp--;

			stream_delegateGrammar.add(delegateGrammar27.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:270:27: ( COMMA delegateGrammar )*
			loop7:
			while (true) {
				int alt7=2;
				int LA7_0 = input.LA(1);
				if ( (LA7_0==COMMA) ) {
					alt7=1;
				}

				switch (alt7) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:270:28: COMMA delegateGrammar
					{
					COMMA28=(Token)match(input,COMMA,FOLLOW_COMMA_in_delegateGrammars1048);  
					stream_COMMA.add(COMMA28);

					pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars1050);
					delegateGrammar29=delegateGrammar();
					state._fsp--;

					stream_delegateGrammar.add(delegateGrammar29.getTree());
					}
					break;

				default :
					break loop7;
				}
			}

			SEMI30=(Token)match(input,SEMI,FOLLOW_SEMI_in_delegateGrammars1054);  
			stream_SEMI.add(SEMI30);

			// AST REWRITE
			// elements: delegateGrammar, IMPORT
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 270:57: -> ^( IMPORT ( delegateGrammar )+ )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:270:60: ^( IMPORT ( delegateGrammar )+ )
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


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "delegateGrammars"


	public static class delegateGrammar_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "delegateGrammar"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:275:1: delegateGrammar : ( id ASSIGN ^ id | id );
	public final ANTLRParser.delegateGrammar_return delegateGrammar() throws RecognitionException {
		ANTLRParser.delegateGrammar_return retval = new ANTLRParser.delegateGrammar_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token ASSIGN32=null;
		ParserRuleReturnScope id31 =null;
		ParserRuleReturnScope id33 =null;
		ParserRuleReturnScope id34 =null;

		GrammarAST ASSIGN32_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:276:5: ( id ASSIGN ^ id | id )
			int alt8=2;
			int LA8_0 = input.LA(1);
			if ( (LA8_0==RULE_REF) ) {
				int LA8_1 = input.LA(2);
				if ( (LA8_1==ASSIGN) ) {
					alt8=1;
				}
				else if ( (LA8_1==COMMA||LA8_1==SEMI) ) {
					alt8=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 8, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA8_0==TOKEN_REF) ) {
				int LA8_2 = input.LA(2);
				if ( (LA8_2==ASSIGN) ) {
					alt8=1;
				}
				else if ( (LA8_2==COMMA||LA8_2==SEMI) ) {
					alt8=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 8, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}

			switch (alt8) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:276:9: id ASSIGN ^ id
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_id_in_delegateGrammar1081);
					id31=id();
					state._fsp--;

					adaptor.addChild(root_0, id31.getTree());

					ASSIGN32=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar1083); 
					ASSIGN32_tree = (GrammarAST)adaptor.create(ASSIGN32);
					root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN32_tree, root_0);

					pushFollow(FOLLOW_id_in_delegateGrammar1086);
					id33=id();
					state._fsp--;

					adaptor.addChild(root_0, id33.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:277:9: id
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_id_in_delegateGrammar1096);
					id34=id();
					state._fsp--;

					adaptor.addChild(root_0, id34.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "delegateGrammar"


	public static class tokensSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "tokensSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:280:1: tokensSpec : ( TOKENS_SPEC id ( COMMA id )* RBRACE -> ^( TOKENS_SPEC ( id )+ ) | TOKENS_SPEC RBRACE ->| TOKENS_SPEC ^ ( v3tokenSpec )+ RBRACE !);
	public final ANTLRParser.tokensSpec_return tokensSpec() throws RecognitionException {
		ANTLRParser.tokensSpec_return retval = new ANTLRParser.tokensSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token TOKENS_SPEC35=null;
		Token COMMA37=null;
		Token RBRACE39=null;
		Token TOKENS_SPEC40=null;
		Token RBRACE41=null;
		Token TOKENS_SPEC42=null;
		Token RBRACE44=null;
		ParserRuleReturnScope id36 =null;
		ParserRuleReturnScope id38 =null;
		ParserRuleReturnScope v3tokenSpec43 =null;

		GrammarAST TOKENS_SPEC35_tree=null;
		GrammarAST COMMA37_tree=null;
		GrammarAST RBRACE39_tree=null;
		GrammarAST TOKENS_SPEC40_tree=null;
		GrammarAST RBRACE41_tree=null;
		GrammarAST TOKENS_SPEC42_tree=null;
		GrammarAST RBRACE44_tree=null;
		RewriteRuleTokenStream stream_RBRACE=new RewriteRuleTokenStream(adaptor,"token RBRACE");
		RewriteRuleTokenStream stream_TOKENS_SPEC=new RewriteRuleTokenStream(adaptor,"token TOKENS_SPEC");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:281:2: ( TOKENS_SPEC id ( COMMA id )* RBRACE -> ^( TOKENS_SPEC ( id )+ ) | TOKENS_SPEC RBRACE ->| TOKENS_SPEC ^ ( v3tokenSpec )+ RBRACE !)
			int alt11=3;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==TOKENS_SPEC) ) {
				switch ( input.LA(2) ) {
				case RBRACE:
					{
					alt11=2;
					}
					break;
				case RULE_REF:
					{
					int LA11_3 = input.LA(3);
					if ( (LA11_3==COMMA||LA11_3==RBRACE) ) {
						alt11=1;
					}
					else if ( (LA11_3==ASSIGN||LA11_3==SEMI) ) {
						alt11=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 3, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				case TOKEN_REF:
					{
					int LA11_4 = input.LA(3);
					if ( (LA11_4==COMMA||LA11_4==RBRACE) ) {
						alt11=1;
					}
					else if ( (LA11_4==ASSIGN||LA11_4==SEMI) ) {
						alt11=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 11, 4, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 11, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}

			switch (alt11) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:281:4: TOKENS_SPEC id ( COMMA id )* RBRACE
					{
					TOKENS_SPEC35=(Token)match(input,TOKENS_SPEC,FOLLOW_TOKENS_SPEC_in_tokensSpec1110);  
					stream_TOKENS_SPEC.add(TOKENS_SPEC35);

					pushFollow(FOLLOW_id_in_tokensSpec1112);
					id36=id();
					state._fsp--;

					stream_id.add(id36.getTree());
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:281:19: ( COMMA id )*
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==COMMA) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:281:20: COMMA id
							{
							COMMA37=(Token)match(input,COMMA,FOLLOW_COMMA_in_tokensSpec1115);  
							stream_COMMA.add(COMMA37);

							pushFollow(FOLLOW_id_in_tokensSpec1117);
							id38=id();
							state._fsp--;

							stream_id.add(id38.getTree());
							}
							break;

						default :
							break loop9;
						}
					}

					RBRACE39=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_tokensSpec1121);  
					stream_RBRACE.add(RBRACE39);

					// AST REWRITE
					// elements: TOKENS_SPEC, id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 281:38: -> ^( TOKENS_SPEC ( id )+ )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:281:41: ^( TOKENS_SPEC ( id )+ )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(stream_TOKENS_SPEC.nextNode(), root_1);
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


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:282:7: TOKENS_SPEC RBRACE
					{
					TOKENS_SPEC40=(Token)match(input,TOKENS_SPEC,FOLLOW_TOKENS_SPEC_in_tokensSpec1138);  
					stream_TOKENS_SPEC.add(TOKENS_SPEC40);

					RBRACE41=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_tokensSpec1140);  
					stream_RBRACE.add(RBRACE41);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 282:26: ->
					{
						root_0 = null;
					}


					retval.tree = root_0;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:283:7: TOKENS_SPEC ^ ( v3tokenSpec )+ RBRACE !
					{
					root_0 = (GrammarAST)adaptor.nil();


					TOKENS_SPEC42=(Token)match(input,TOKENS_SPEC,FOLLOW_TOKENS_SPEC_in_tokensSpec1150); 
					TOKENS_SPEC42_tree = (GrammarAST)adaptor.create(TOKENS_SPEC42);
					root_0 = (GrammarAST)adaptor.becomeRoot(TOKENS_SPEC42_tree, root_0);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:283:20: ( v3tokenSpec )+
					int cnt10=0;
					loop10:
					while (true) {
						int alt10=2;
						int LA10_0 = input.LA(1);
						if ( (LA10_0==RULE_REF||LA10_0==TOKEN_REF) ) {
							alt10=1;
						}

						switch (alt10) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:283:20: v3tokenSpec
							{
							pushFollow(FOLLOW_v3tokenSpec_in_tokensSpec1153);
							v3tokenSpec43=v3tokenSpec();
							state._fsp--;

							adaptor.addChild(root_0, v3tokenSpec43.getTree());

							}
							break;

						default :
							if ( cnt10 >= 1 ) break loop10;
							EarlyExitException eee = new EarlyExitException(10, input);
							throw eee;
						}
						cnt10++;
					}

					RBRACE44=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_tokensSpec1156); 
					grammarError(ErrorType.V3_TOKENS_SYNTAX, TOKENS_SPEC42);
					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "tokensSpec"


	public static class v3tokenSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "v3tokenSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:287:1: v3tokenSpec : id ( ASSIGN lit= STRING_LITERAL -> id | -> id ) SEMI ;
	public final ANTLRParser.v3tokenSpec_return v3tokenSpec() throws RecognitionException {
		ANTLRParser.v3tokenSpec_return retval = new ANTLRParser.v3tokenSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token lit=null;
		Token ASSIGN46=null;
		Token SEMI47=null;
		ParserRuleReturnScope id45 =null;

		GrammarAST lit_tree=null;
		GrammarAST ASSIGN46_tree=null;
		GrammarAST SEMI47_tree=null;
		RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:288:2: ( id ( ASSIGN lit= STRING_LITERAL -> id | -> id ) SEMI )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:288:4: id ( ASSIGN lit= STRING_LITERAL -> id | -> id ) SEMI
			{
			pushFollow(FOLLOW_id_in_v3tokenSpec1176);
			id45=id();
			state._fsp--;

			stream_id.add(id45.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:289:3: ( ASSIGN lit= STRING_LITERAL -> id | -> id )
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==ASSIGN) ) {
				alt12=1;
			}
			else if ( (LA12_0==SEMI) ) {
				alt12=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}

			switch (alt12) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:289:5: ASSIGN lit= STRING_LITERAL
					{
					ASSIGN46=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_v3tokenSpec1182);  
					stream_ASSIGN.add(ASSIGN46);

					lit=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_v3tokenSpec1186);  
					stream_STRING_LITERAL.add(lit);


					            grammarError(ErrorType.V3_ASSIGN_IN_TOKENS, (id45!=null?(id45.start):null),
					                         (id45!=null?input.toString(id45.start,id45.stop):null), lit.getText());
					            
					// AST REWRITE
					// elements: id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 294:20: -> id
					{
						adaptor.addChild(root_0, stream_id.nextTree());
					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:295:12: 
					{
					// AST REWRITE
					// elements: id
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 295:12: -> id
					{
						adaptor.addChild(root_0, stream_id.nextTree());
					}


					retval.tree = root_0;

					}
					break;

			}

			SEMI47=(Token)match(input,SEMI,FOLLOW_SEMI_in_v3tokenSpec1247);  
			stream_SEMI.add(SEMI47);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "v3tokenSpec"


	public static class channelsSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "channelsSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:300:1: channelsSpec : CHANNELS ^ id ( COMMA ! id )* RBRACE !;
	public final ANTLRParser.channelsSpec_return channelsSpec() throws RecognitionException {
		ANTLRParser.channelsSpec_return retval = new ANTLRParser.channelsSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token CHANNELS48=null;
		Token COMMA50=null;
		Token RBRACE52=null;
		ParserRuleReturnScope id49 =null;
		ParserRuleReturnScope id51 =null;

		GrammarAST CHANNELS48_tree=null;
		GrammarAST COMMA50_tree=null;
		GrammarAST RBRACE52_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:301:2: ( CHANNELS ^ id ( COMMA ! id )* RBRACE !)
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:301:4: CHANNELS ^ id ( COMMA ! id )* RBRACE !
			{
			root_0 = (GrammarAST)adaptor.nil();


			CHANNELS48=(Token)match(input,CHANNELS,FOLLOW_CHANNELS_in_channelsSpec1258); 
			CHANNELS48_tree = (GrammarAST)adaptor.create(CHANNELS48);
			root_0 = (GrammarAST)adaptor.becomeRoot(CHANNELS48_tree, root_0);

			pushFollow(FOLLOW_id_in_channelsSpec1261);
			id49=id();
			state._fsp--;

			adaptor.addChild(root_0, id49.getTree());

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:301:17: ( COMMA ! id )*
			loop13:
			while (true) {
				int alt13=2;
				int LA13_0 = input.LA(1);
				if ( (LA13_0==COMMA) ) {
					alt13=1;
				}

				switch (alt13) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:301:18: COMMA ! id
					{
					COMMA50=(Token)match(input,COMMA,FOLLOW_COMMA_in_channelsSpec1264); 
					pushFollow(FOLLOW_id_in_channelsSpec1267);
					id51=id();
					state._fsp--;

					adaptor.addChild(root_0, id51.getTree());

					}
					break;

				default :
					break loop13;
				}
			}

			RBRACE52=(Token)match(input,RBRACE,FOLLOW_RBRACE_in_channelsSpec1271); 
			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "channelsSpec"


	public static class action_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "action"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:308:1: action : AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) ;
	public final ANTLRParser.action_return action() throws RecognitionException {
		ANTLRParser.action_return retval = new ANTLRParser.action_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token AT53=null;
		Token COLONCOLON55=null;
		Token ACTION57=null;
		ParserRuleReturnScope actionScopeName54 =null;
		ParserRuleReturnScope id56 =null;

		GrammarAST AT53_tree=null;
		GrammarAST COLONCOLON55_tree=null;
		GrammarAST ACTION57_tree=null;
		RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
		RewriteRuleTokenStream stream_COLONCOLON=new RewriteRuleTokenStream(adaptor,"token COLONCOLON");
		RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
		RewriteRuleSubtreeStream stream_actionScopeName=new RewriteRuleSubtreeStream(adaptor,"rule actionScopeName");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:309:2: ( AT ( actionScopeName COLONCOLON )? id ACTION -> ^( AT ( actionScopeName )? id ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:309:4: AT ( actionScopeName COLONCOLON )? id ACTION
			{
			AT53=(Token)match(input,AT,FOLLOW_AT_in_action1288);  
			stream_AT.add(AT53);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:309:7: ( actionScopeName COLONCOLON )?
			int alt14=2;
			switch ( input.LA(1) ) {
				case RULE_REF:
					{
					int LA14_1 = input.LA(2);
					if ( (LA14_1==COLONCOLON) ) {
						alt14=1;
					}
					}
					break;
				case TOKEN_REF:
					{
					int LA14_2 = input.LA(2);
					if ( (LA14_2==COLONCOLON) ) {
						alt14=1;
					}
					}
					break;
				case LEXER:
				case PARSER:
					{
					alt14=1;
					}
					break;
			}
			switch (alt14) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:309:8: actionScopeName COLONCOLON
					{
					pushFollow(FOLLOW_actionScopeName_in_action1291);
					actionScopeName54=actionScopeName();
					state._fsp--;

					stream_actionScopeName.add(actionScopeName54.getTree());
					COLONCOLON55=(Token)match(input,COLONCOLON,FOLLOW_COLONCOLON_in_action1293);  
					stream_COLONCOLON.add(COLONCOLON55);

					}
					break;

			}

			pushFollow(FOLLOW_id_in_action1297);
			id56=id();
			state._fsp--;

			stream_id.add(id56.getTree());
			ACTION57=(Token)match(input,ACTION,FOLLOW_ACTION_in_action1299);  
			stream_ACTION.add(ACTION57);

			// AST REWRITE
			// elements: id, ACTION, actionScopeName, AT
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 309:47: -> ^( AT ( actionScopeName )? id ACTION )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:309:50: ^( AT ( actionScopeName )? id ACTION )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:309:55: ( actionScopeName )?
				if ( stream_actionScopeName.hasNext() ) {
					adaptor.addChild(root_1, stream_actionScopeName.nextTree());
				}
				stream_actionScopeName.reset();

				adaptor.addChild(root_1, stream_id.nextTree());
				adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "action"


	public static class actionScopeName_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "actionScopeName"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:315:1: actionScopeName : ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] );
	public final ANTLRParser.actionScopeName_return actionScopeName() throws RecognitionException {
		ANTLRParser.actionScopeName_return retval = new ANTLRParser.actionScopeName_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token LEXER59=null;
		Token PARSER60=null;
		ParserRuleReturnScope id58 =null;

		GrammarAST LEXER59_tree=null;
		GrammarAST PARSER60_tree=null;
		RewriteRuleTokenStream stream_PARSER=new RewriteRuleTokenStream(adaptor,"token PARSER");
		RewriteRuleTokenStream stream_LEXER=new RewriteRuleTokenStream(adaptor,"token LEXER");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:316:2: ( id | LEXER -> ID[$LEXER] | PARSER -> ID[$PARSER] )
			int alt15=3;
			switch ( input.LA(1) ) {
			case RULE_REF:
			case TOKEN_REF:
				{
				alt15=1;
				}
				break;
			case LEXER:
				{
				alt15=2;
				}
				break;
			case PARSER:
				{
				alt15=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}
			switch (alt15) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:316:4: id
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_id_in_actionScopeName1328);
					id58=id();
					state._fsp--;

					adaptor.addChild(root_0, id58.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:317:4: LEXER
					{
					LEXER59=(Token)match(input,LEXER,FOLLOW_LEXER_in_actionScopeName1333);  
					stream_LEXER.add(LEXER59);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 317:10: -> ID[$LEXER]
					{
						adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, LEXER59));
					}


					retval.tree = root_0;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:318:9: PARSER
					{
					PARSER60=(Token)match(input,PARSER,FOLLOW_PARSER_in_actionScopeName1348);  
					stream_PARSER.add(PARSER60);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 318:16: -> ID[$PARSER]
					{
						adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, PARSER60));
					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "actionScopeName"


	public static class modeSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "modeSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:321:1: modeSpec : MODE id SEMI sync ( lexerRule sync )* -> ^( MODE id ( lexerRule )* ) ;
	public final ANTLRParser.modeSpec_return modeSpec() throws RecognitionException {
		ANTLRParser.modeSpec_return retval = new ANTLRParser.modeSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token MODE61=null;
		Token SEMI63=null;
		ParserRuleReturnScope id62 =null;
		ParserRuleReturnScope sync64 =null;
		ParserRuleReturnScope lexerRule65 =null;
		ParserRuleReturnScope sync66 =null;

		GrammarAST MODE61_tree=null;
		GrammarAST SEMI63_tree=null;
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleTokenStream stream_MODE=new RewriteRuleTokenStream(adaptor,"token MODE");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
		RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
		RewriteRuleSubtreeStream stream_lexerRule=new RewriteRuleSubtreeStream(adaptor,"rule lexerRule");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:322:5: ( MODE id SEMI sync ( lexerRule sync )* -> ^( MODE id ( lexerRule )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:322:7: MODE id SEMI sync ( lexerRule sync )*
			{
			MODE61=(Token)match(input,MODE,FOLLOW_MODE_in_modeSpec1367);  
			stream_MODE.add(MODE61);

			pushFollow(FOLLOW_id_in_modeSpec1369);
			id62=id();
			state._fsp--;

			stream_id.add(id62.getTree());
			SEMI63=(Token)match(input,SEMI,FOLLOW_SEMI_in_modeSpec1371);  
			stream_SEMI.add(SEMI63);

			pushFollow(FOLLOW_sync_in_modeSpec1373);
			sync64=sync();
			state._fsp--;

			stream_sync.add(sync64.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:322:25: ( lexerRule sync )*
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( (LA16_0==FRAGMENT||LA16_0==TOKEN_REF) ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:322:26: lexerRule sync
					{
					pushFollow(FOLLOW_lexerRule_in_modeSpec1376);
					lexerRule65=lexerRule();
					state._fsp--;

					stream_lexerRule.add(lexerRule65.getTree());
					pushFollow(FOLLOW_sync_in_modeSpec1378);
					sync66=sync();
					state._fsp--;

					stream_sync.add(sync66.getTree());
					}
					break;

				default :
					break loop16;
				}
			}

			// AST REWRITE
			// elements: MODE, lexerRule, id
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 322:44: -> ^( MODE id ( lexerRule )* )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:322:47: ^( MODE id ( lexerRule )* )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(stream_MODE.nextNode(), root_1);
				adaptor.addChild(root_1, stream_id.nextTree());
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:322:57: ( lexerRule )*
				while ( stream_lexerRule.hasNext() ) {
					adaptor.addChild(root_1, stream_lexerRule.nextTree());
				}
				stream_lexerRule.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "modeSpec"


	public static class rules_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "rules"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:325:1: rules : sync ( rule sync )* -> ^( RULES ( rule )* ) ;
	public final ANTLRParser.rules_return rules() throws RecognitionException {
		ANTLRParser.rules_return retval = new ANTLRParser.rules_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope sync67 =null;
		ParserRuleReturnScope rule68 =null;
		ParserRuleReturnScope sync69 =null;

		RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");
		RewriteRuleSubtreeStream stream_rule=new RewriteRuleSubtreeStream(adaptor,"rule rule");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:326:5: ( sync ( rule sync )* -> ^( RULES ( rule )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:326:7: sync ( rule sync )*
			{
			pushFollow(FOLLOW_sync_in_rules1409);
			sync67=sync();
			state._fsp--;

			stream_sync.add(sync67.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:326:12: ( rule sync )*
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==FRAGMENT||LA17_0==RULE_REF||LA17_0==TOKEN_REF) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:326:13: rule sync
					{
					pushFollow(FOLLOW_rule_in_rules1412);
					rule68=rule();
					state._fsp--;

					stream_rule.add(rule68.getTree());
					pushFollow(FOLLOW_sync_in_rules1414);
					sync69=sync();
					state._fsp--;

					stream_sync.add(sync69.getTree());
					}
					break;

				default :
					break loop17;
				}
			}

			// AST REWRITE
			// elements: rule
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 330:7: -> ^( RULES ( rule )* )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:330:9: ^( RULES ( rule )* )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULES, "RULES"), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:330:17: ( rule )*
				while ( stream_rule.hasNext() ) {
					adaptor.addChild(root_1, stream_rule.nextTree());
				}
				stream_rule.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rules"


	public static class sync_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "sync"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:333:1: sync :;
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
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:342:3: ()
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:343:2: 
			{
			root_0 = (GrammarAST)adaptor.nil();


			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "sync"


	public static class rule_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "rule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:345:1: rule : ( parserRule | lexerRule );
	public final ANTLRParser.rule_return rule() throws RecognitionException {
		ANTLRParser.rule_return retval = new ANTLRParser.rule_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope parserRule70 =null;
		ParserRuleReturnScope lexerRule71 =null;


		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:345:5: ( parserRule | lexerRule )
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==RULE_REF) ) {
				alt18=1;
			}
			else if ( (LA18_0==FRAGMENT||LA18_0==TOKEN_REF) ) {
				alt18=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}

			switch (alt18) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:345:7: parserRule
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_parserRule_in_rule1476);
					parserRule70=parserRule();
					state._fsp--;

					adaptor.addChild(root_0, parserRule70.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:346:4: lexerRule
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_lexerRule_in_rule1481);
					lexerRule71=lexerRule();
					state._fsp--;

					adaptor.addChild(root_0, lexerRule71.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rule"


	public static class parserRule_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "parserRule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:358:1: parserRule : RULE_REF ( ARG_ACTION )? ( ruleReturns )? ( throwsSpec )? ( localsSpec )? rulePrequels COLON ruleBlock SEMI exceptionGroup -> ^( RULE RULE_REF ( ARG_ACTION )? ( ruleReturns )? ( throwsSpec )? ( localsSpec )? ( rulePrequels )? ruleBlock ( exceptionGroup )* ) ;
	public final ANTLRParser.parserRule_return parserRule() throws RecognitionException {
		ANTLRParser.parserRule_return retval = new ANTLRParser.parserRule_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token RULE_REF72=null;
		Token ARG_ACTION73=null;
		Token COLON78=null;
		Token SEMI80=null;
		ParserRuleReturnScope ruleReturns74 =null;
		ParserRuleReturnScope throwsSpec75 =null;
		ParserRuleReturnScope localsSpec76 =null;
		ParserRuleReturnScope rulePrequels77 =null;
		ParserRuleReturnScope ruleBlock79 =null;
		ParserRuleReturnScope exceptionGroup81 =null;

		GrammarAST RULE_REF72_tree=null;
		GrammarAST ARG_ACTION73_tree=null;
		GrammarAST COLON78_tree=null;
		GrammarAST SEMI80_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
		RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
		RewriteRuleSubtreeStream stream_rulePrequels=new RewriteRuleSubtreeStream(adaptor,"rule rulePrequels");
		RewriteRuleSubtreeStream stream_exceptionGroup=new RewriteRuleSubtreeStream(adaptor,"rule exceptionGroup");
		RewriteRuleSubtreeStream stream_ruleReturns=new RewriteRuleSubtreeStream(adaptor,"rule ruleReturns");
		RewriteRuleSubtreeStream stream_throwsSpec=new RewriteRuleSubtreeStream(adaptor,"rule throwsSpec");
		RewriteRuleSubtreeStream stream_ruleBlock=new RewriteRuleSubtreeStream(adaptor,"rule ruleBlock");
		RewriteRuleSubtreeStream stream_localsSpec=new RewriteRuleSubtreeStream(adaptor,"rule localsSpec");

		 paraphrases.push("matching a rule"); 
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:367:5: ( RULE_REF ( ARG_ACTION )? ( ruleReturns )? ( throwsSpec )? ( localsSpec )? rulePrequels COLON ruleBlock SEMI exceptionGroup -> ^( RULE RULE_REF ( ARG_ACTION )? ( ruleReturns )? ( throwsSpec )? ( localsSpec )? ( rulePrequels )? ruleBlock ( exceptionGroup )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:371:4: RULE_REF ( ARG_ACTION )? ( ruleReturns )? ( throwsSpec )? ( localsSpec )? rulePrequels COLON ruleBlock SEMI exceptionGroup
			{
			RULE_REF72=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_parserRule1530);  
			stream_RULE_REF.add(RULE_REF72);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:379:4: ( ARG_ACTION )?
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==ARG_ACTION) ) {
				alt19=1;
			}
			switch (alt19) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:379:4: ARG_ACTION
					{
					ARG_ACTION73=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_parserRule1560);  
					stream_ARG_ACTION.add(ARG_ACTION73);

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:381:4: ( ruleReturns )?
			int alt20=2;
			int LA20_0 = input.LA(1);
			if ( (LA20_0==RETURNS) ) {
				alt20=1;
			}
			switch (alt20) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:381:4: ruleReturns
					{
					pushFollow(FOLLOW_ruleReturns_in_parserRule1567);
					ruleReturns74=ruleReturns();
					state._fsp--;

					stream_ruleReturns.add(ruleReturns74.getTree());
					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:383:4: ( throwsSpec )?
			int alt21=2;
			int LA21_0 = input.LA(1);
			if ( (LA21_0==THROWS) ) {
				alt21=1;
			}
			switch (alt21) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:383:4: throwsSpec
					{
					pushFollow(FOLLOW_throwsSpec_in_parserRule1574);
					throwsSpec75=throwsSpec();
					state._fsp--;

					stream_throwsSpec.add(throwsSpec75.getTree());
					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:385:4: ( localsSpec )?
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( (LA22_0==LOCALS) ) {
				alt22=1;
			}
			switch (alt22) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:385:4: localsSpec
					{
					pushFollow(FOLLOW_localsSpec_in_parserRule1581);
					localsSpec76=localsSpec();
					state._fsp--;

					stream_localsSpec.add(localsSpec76.getTree());
					}
					break;

			}

			pushFollow(FOLLOW_rulePrequels_in_parserRule1619);
			rulePrequels77=rulePrequels();
			state._fsp--;

			stream_rulePrequels.add(rulePrequels77.getTree());
			COLON78=(Token)match(input,COLON,FOLLOW_COLON_in_parserRule1628);  
			stream_COLON.add(COLON78);

			pushFollow(FOLLOW_ruleBlock_in_parserRule1651);
			ruleBlock79=ruleBlock();
			state._fsp--;

			stream_ruleBlock.add(ruleBlock79.getTree());
			SEMI80=(Token)match(input,SEMI,FOLLOW_SEMI_in_parserRule1660);  
			stream_SEMI.add(SEMI80);

			pushFollow(FOLLOW_exceptionGroup_in_parserRule1669);
			exceptionGroup81=exceptionGroup();
			state._fsp--;

			stream_exceptionGroup.add(exceptionGroup81.getTree());
			// AST REWRITE
			// elements: ARG_ACTION, ruleReturns, RULE_REF, throwsSpec, rulePrequels, ruleBlock, localsSpec, exceptionGroup
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 412:7: -> ^( RULE RULE_REF ( ARG_ACTION )? ( ruleReturns )? ( throwsSpec )? ( localsSpec )? ( rulePrequels )? ruleBlock ( exceptionGroup )* )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:412:10: ^( RULE RULE_REF ( ARG_ACTION )? ( ruleReturns )? ( throwsSpec )? ( localsSpec )? ( rulePrequels )? ruleBlock ( exceptionGroup )* )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new RuleAST(RULE), root_1);
				adaptor.addChild(root_1, stream_RULE_REF.nextNode());
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:412:36: ( ARG_ACTION )?
				if ( stream_ARG_ACTION.hasNext() ) {
					adaptor.addChild(root_1, new ActionAST(stream_ARG_ACTION.nextToken()));
				}
				stream_ARG_ACTION.reset();

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:413:9: ( ruleReturns )?
				if ( stream_ruleReturns.hasNext() ) {
					adaptor.addChild(root_1, stream_ruleReturns.nextTree());
				}
				stream_ruleReturns.reset();

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:413:22: ( throwsSpec )?
				if ( stream_throwsSpec.hasNext() ) {
					adaptor.addChild(root_1, stream_throwsSpec.nextTree());
				}
				stream_throwsSpec.reset();

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:413:34: ( localsSpec )?
				if ( stream_localsSpec.hasNext() ) {
					adaptor.addChild(root_1, stream_localsSpec.nextTree());
				}
				stream_localsSpec.reset();

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:413:46: ( rulePrequels )?
				if ( stream_rulePrequels.hasNext() ) {
					adaptor.addChild(root_1, stream_rulePrequels.nextTree());
				}
				stream_rulePrequels.reset();

				adaptor.addChild(root_1, stream_ruleBlock.nextTree());
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:413:70: ( exceptionGroup )*
				while ( stream_exceptionGroup.hasNext() ) {
					adaptor.addChild(root_1, stream_exceptionGroup.nextTree());
				}
				stream_exceptionGroup.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


				paraphrases.pop();
				GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.OPTIONS);
				if ( options!=null ) {
					Grammar.setNodeOptions(retval.tree, options);
				}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "parserRule"


	public static class exceptionGroup_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "exceptionGroup"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:423:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
	public final ANTLRParser.exceptionGroup_return exceptionGroup() throws RecognitionException {
		ANTLRParser.exceptionGroup_return retval = new ANTLRParser.exceptionGroup_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope exceptionHandler82 =null;
		ParserRuleReturnScope finallyClause83 =null;


		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:424:5: ( ( exceptionHandler )* ( finallyClause )? )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:424:7: ( exceptionHandler )* ( finallyClause )?
			{
			root_0 = (GrammarAST)adaptor.nil();


			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:424:7: ( exceptionHandler )*
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( (LA23_0==CATCH) ) {
					alt23=1;
				}

				switch (alt23) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:424:7: exceptionHandler
					{
					pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup1752);
					exceptionHandler82=exceptionHandler();
					state._fsp--;

					adaptor.addChild(root_0, exceptionHandler82.getTree());

					}
					break;

				default :
					break loop23;
				}
			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:424:25: ( finallyClause )?
			int alt24=2;
			int LA24_0 = input.LA(1);
			if ( (LA24_0==FINALLY) ) {
				alt24=1;
			}
			switch (alt24) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:424:25: finallyClause
					{
					pushFollow(FOLLOW_finallyClause_in_exceptionGroup1755);
					finallyClause83=finallyClause();
					state._fsp--;

					adaptor.addChild(root_0, finallyClause83.getTree());

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "exceptionGroup"


	public static class exceptionHandler_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "exceptionHandler"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:429:1: exceptionHandler : CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) ;
	public final ANTLRParser.exceptionHandler_return exceptionHandler() throws RecognitionException {
		ANTLRParser.exceptionHandler_return retval = new ANTLRParser.exceptionHandler_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token CATCH84=null;
		Token ARG_ACTION85=null;
		Token ACTION86=null;

		GrammarAST CATCH84_tree=null;
		GrammarAST ARG_ACTION85_tree=null;
		GrammarAST ACTION86_tree=null;
		RewriteRuleTokenStream stream_CATCH=new RewriteRuleTokenStream(adaptor,"token CATCH");
		RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
		RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:430:2: ( CATCH ARG_ACTION ACTION -> ^( CATCH ARG_ACTION ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:430:4: CATCH ARG_ACTION ACTION
			{
			CATCH84=(Token)match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler1772);  
			stream_CATCH.add(CATCH84);

			ARG_ACTION85=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler1774);  
			stream_ARG_ACTION.add(ARG_ACTION85);

			ACTION86=(Token)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler1776);  
			stream_ACTION.add(ACTION86);

			// AST REWRITE
			// elements: ACTION, ARG_ACTION, CATCH
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 430:28: -> ^( CATCH ARG_ACTION ACTION )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:430:31: ^( CATCH ARG_ACTION ACTION )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(stream_CATCH.nextNode(), root_1);
				adaptor.addChild(root_1, new ActionAST(stream_ARG_ACTION.nextToken()));
				adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "exceptionHandler"


	public static class finallyClause_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "finallyClause"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:433:1: finallyClause : FINALLY ACTION -> ^( FINALLY ACTION ) ;
	public final ANTLRParser.finallyClause_return finallyClause() throws RecognitionException {
		ANTLRParser.finallyClause_return retval = new ANTLRParser.finallyClause_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token FINALLY87=null;
		Token ACTION88=null;

		GrammarAST FINALLY87_tree=null;
		GrammarAST ACTION88_tree=null;
		RewriteRuleTokenStream stream_FINALLY=new RewriteRuleTokenStream(adaptor,"token FINALLY");
		RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:434:2: ( FINALLY ACTION -> ^( FINALLY ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:434:4: FINALLY ACTION
			{
			FINALLY87=(Token)match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause1803);  
			stream_FINALLY.add(FINALLY87);

			ACTION88=(Token)match(input,ACTION,FOLLOW_ACTION_in_finallyClause1805);  
			stream_ACTION.add(ACTION88);

			// AST REWRITE
			// elements: ACTION, FINALLY
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 434:19: -> ^( FINALLY ACTION )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:434:22: ^( FINALLY ACTION )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(stream_FINALLY.nextNode(), root_1);
				adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "finallyClause"


	public static class rulePrequels_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "rulePrequels"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:437:1: rulePrequels : sync ( rulePrequel sync )* -> ( rulePrequel )* ;
	public final ANTLRParser.rulePrequels_return rulePrequels() throws RecognitionException {
		ANTLRParser.rulePrequels_return retval = new ANTLRParser.rulePrequels_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope sync89 =null;
		ParserRuleReturnScope rulePrequel90 =null;
		ParserRuleReturnScope sync91 =null;

		RewriteRuleSubtreeStream stream_rulePrequel=new RewriteRuleSubtreeStream(adaptor,"rule rulePrequel");
		RewriteRuleSubtreeStream stream_sync=new RewriteRuleSubtreeStream(adaptor,"rule sync");

		 paraphrases.push("matching rule preamble"); 
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:440:2: ( sync ( rulePrequel sync )* -> ( rulePrequel )* )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:440:4: sync ( rulePrequel sync )*
			{
			pushFollow(FOLLOW_sync_in_rulePrequels1837);
			sync89=sync();
			state._fsp--;

			stream_sync.add(sync89.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:440:9: ( rulePrequel sync )*
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==AT||LA25_0==OPTIONS) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:440:10: rulePrequel sync
					{
					pushFollow(FOLLOW_rulePrequel_in_rulePrequels1840);
					rulePrequel90=rulePrequel();
					state._fsp--;

					stream_rulePrequel.add(rulePrequel90.getTree());
					pushFollow(FOLLOW_sync_in_rulePrequels1842);
					sync91=sync();
					state._fsp--;

					stream_sync.add(sync91.getTree());
					}
					break;

				default :
					break loop25;
				}
			}

			// AST REWRITE
			// elements: rulePrequel
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 440:29: -> ( rulePrequel )*
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:440:32: ( rulePrequel )*
				while ( stream_rulePrequel.hasNext() ) {
					adaptor.addChild(root_0, stream_rulePrequel.nextTree());
				}
				stream_rulePrequel.reset();

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

			 paraphrases.pop(); 
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rulePrequels"


	public static class rulePrequel_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "rulePrequel"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:446:1: rulePrequel : ( optionsSpec | ruleAction );
	public final ANTLRParser.rulePrequel_return rulePrequel() throws RecognitionException {
		ANTLRParser.rulePrequel_return retval = new ANTLRParser.rulePrequel_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope optionsSpec92 =null;
		ParserRuleReturnScope ruleAction93 =null;


		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:447:5: ( optionsSpec | ruleAction )
			int alt26=2;
			int LA26_0 = input.LA(1);
			if ( (LA26_0==OPTIONS) ) {
				alt26=1;
			}
			else if ( (LA26_0==AT) ) {
				alt26=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 26, 0, input);
				throw nvae;
			}

			switch (alt26) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:447:7: optionsSpec
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_optionsSpec_in_rulePrequel1866);
					optionsSpec92=optionsSpec();
					state._fsp--;

					adaptor.addChild(root_0, optionsSpec92.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:448:7: ruleAction
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_ruleAction_in_rulePrequel1874);
					ruleAction93=ruleAction();
					state._fsp--;

					adaptor.addChild(root_0, ruleAction93.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "rulePrequel"


	public static class ruleReturns_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ruleReturns"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:457:1: ruleReturns : RETURNS ^ ARG_ACTION ;
	public final ANTLRParser.ruleReturns_return ruleReturns() throws RecognitionException {
		ANTLRParser.ruleReturns_return retval = new ANTLRParser.ruleReturns_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token RETURNS94=null;
		Token ARG_ACTION95=null;

		GrammarAST RETURNS94_tree=null;
		GrammarAST ARG_ACTION95_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:458:2: ( RETURNS ^ ARG_ACTION )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:458:4: RETURNS ^ ARG_ACTION
			{
			root_0 = (GrammarAST)adaptor.nil();


			RETURNS94=(Token)match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns1894); 
			RETURNS94_tree = (GrammarAST)adaptor.create(RETURNS94);
			root_0 = (GrammarAST)adaptor.becomeRoot(RETURNS94_tree, root_0);

			ARG_ACTION95=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns1897); 
			ARG_ACTION95_tree = new ActionAST(ARG_ACTION95) ;
			adaptor.addChild(root_0, ARG_ACTION95_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ruleReturns"


	public static class throwsSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "throwsSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:472:1: throwsSpec : THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) ;
	public final ANTLRParser.throwsSpec_return throwsSpec() throws RecognitionException {
		ANTLRParser.throwsSpec_return retval = new ANTLRParser.throwsSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token THROWS96=null;
		Token COMMA98=null;
		ParserRuleReturnScope qid97 =null;
		ParserRuleReturnScope qid99 =null;

		GrammarAST THROWS96_tree=null;
		GrammarAST COMMA98_tree=null;
		RewriteRuleTokenStream stream_THROWS=new RewriteRuleTokenStream(adaptor,"token THROWS");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleSubtreeStream stream_qid=new RewriteRuleSubtreeStream(adaptor,"rule qid");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:473:5: ( THROWS qid ( COMMA qid )* -> ^( THROWS ( qid )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:473:7: THROWS qid ( COMMA qid )*
			{
			THROWS96=(Token)match(input,THROWS,FOLLOW_THROWS_in_throwsSpec1925);  
			stream_THROWS.add(THROWS96);

			pushFollow(FOLLOW_qid_in_throwsSpec1927);
			qid97=qid();
			state._fsp--;

			stream_qid.add(qid97.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:473:18: ( COMMA qid )*
			loop27:
			while (true) {
				int alt27=2;
				int LA27_0 = input.LA(1);
				if ( (LA27_0==COMMA) ) {
					alt27=1;
				}

				switch (alt27) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:473:19: COMMA qid
					{
					COMMA98=(Token)match(input,COMMA,FOLLOW_COMMA_in_throwsSpec1930);  
					stream_COMMA.add(COMMA98);

					pushFollow(FOLLOW_qid_in_throwsSpec1932);
					qid99=qid();
					state._fsp--;

					stream_qid.add(qid99.getTree());
					}
					break;

				default :
					break loop27;
				}
			}

			// AST REWRITE
			// elements: THROWS, qid
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 473:31: -> ^( THROWS ( qid )+ )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:473:34: ^( THROWS ( qid )+ )
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


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "throwsSpec"


	public static class localsSpec_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "localsSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:477:1: localsSpec : LOCALS ^ ARG_ACTION ;
	public final ANTLRParser.localsSpec_return localsSpec() throws RecognitionException {
		ANTLRParser.localsSpec_return retval = new ANTLRParser.localsSpec_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token LOCALS100=null;
		Token ARG_ACTION101=null;

		GrammarAST LOCALS100_tree=null;
		GrammarAST ARG_ACTION101_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:477:12: ( LOCALS ^ ARG_ACTION )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:477:14: LOCALS ^ ARG_ACTION
			{
			root_0 = (GrammarAST)adaptor.nil();


			LOCALS100=(Token)match(input,LOCALS,FOLLOW_LOCALS_in_localsSpec1957); 
			LOCALS100_tree = (GrammarAST)adaptor.create(LOCALS100);
			root_0 = (GrammarAST)adaptor.becomeRoot(LOCALS100_tree, root_0);

			ARG_ACTION101=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_localsSpec1960); 
			ARG_ACTION101_tree = new ActionAST(ARG_ACTION101) ;
			adaptor.addChild(root_0, ARG_ACTION101_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "localsSpec"


	public static class ruleAction_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ruleAction"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:488:1: ruleAction : AT id ACTION -> ^( AT id ACTION ) ;
	public final ANTLRParser.ruleAction_return ruleAction() throws RecognitionException {
		ANTLRParser.ruleAction_return retval = new ANTLRParser.ruleAction_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token AT102=null;
		Token ACTION104=null;
		ParserRuleReturnScope id103 =null;

		GrammarAST AT102_tree=null;
		GrammarAST ACTION104_tree=null;
		RewriteRuleTokenStream stream_AT=new RewriteRuleTokenStream(adaptor,"token AT");
		RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:489:2: ( AT id ACTION -> ^( AT id ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:489:4: AT id ACTION
			{
			AT102=(Token)match(input,AT,FOLLOW_AT_in_ruleAction1983);  
			stream_AT.add(AT102);

			pushFollow(FOLLOW_id_in_ruleAction1985);
			id103=id();
			state._fsp--;

			stream_id.add(id103.getTree());
			ACTION104=(Token)match(input,ACTION,FOLLOW_ACTION_in_ruleAction1987);  
			stream_ACTION.add(ACTION104);

			// AST REWRITE
			// elements: AT, ACTION, id
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 489:17: -> ^( AT id ACTION )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:489:20: ^( AT id ACTION )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(stream_AT.nextNode(), root_1);
				adaptor.addChild(root_1, stream_id.nextTree());
				adaptor.addChild(root_1, new ActionAST(stream_ACTION.nextToken()));
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ruleAction"


	public static class ruleBlock_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ruleBlock"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:498:1: ruleBlock : ruleAltList -> ^( BLOCK[colon,\"BLOCK\"] ruleAltList ) ;
	public final ANTLRParser.ruleBlock_return ruleBlock() throws RecognitionException {
		ANTLRParser.ruleBlock_return retval = new ANTLRParser.ruleBlock_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope ruleAltList105 =null;

		RewriteRuleSubtreeStream stream_ruleAltList=new RewriteRuleSubtreeStream(adaptor,"rule ruleAltList");

		Token colon = input.LT(-1);
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:500:5: ( ruleAltList -> ^( BLOCK[colon,\"BLOCK\"] ruleAltList ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:500:7: ruleAltList
			{
			pushFollow(FOLLOW_ruleAltList_in_ruleBlock2025);
			ruleAltList105=ruleAltList();
			state._fsp--;

			stream_ruleAltList.add(ruleAltList105.getTree());
			// AST REWRITE
			// elements: ruleAltList
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 500:19: -> ^( BLOCK[colon,\"BLOCK\"] ruleAltList )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:500:22: ^( BLOCK[colon,\"BLOCK\"] ruleAltList )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, colon, "BLOCK"), root_1);
				adaptor.addChild(root_1, stream_ruleAltList.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (ResyncToEndOfRuleBlock e) {

			    	// just resyncing; ignore error
					retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), null);
			    
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ruleBlock"


	public static class ruleAltList_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ruleAltList"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:507:1: ruleAltList : labeledAlt ( OR labeledAlt )* -> ( labeledAlt )+ ;
	public final ANTLRParser.ruleAltList_return ruleAltList() throws RecognitionException {
		ANTLRParser.ruleAltList_return retval = new ANTLRParser.ruleAltList_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token OR107=null;
		ParserRuleReturnScope labeledAlt106 =null;
		ParserRuleReturnScope labeledAlt108 =null;

		GrammarAST OR107_tree=null;
		RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
		RewriteRuleSubtreeStream stream_labeledAlt=new RewriteRuleSubtreeStream(adaptor,"rule labeledAlt");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:508:2: ( labeledAlt ( OR labeledAlt )* -> ( labeledAlt )+ )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:508:4: labeledAlt ( OR labeledAlt )*
			{
			pushFollow(FOLLOW_labeledAlt_in_ruleAltList2061);
			labeledAlt106=labeledAlt();
			state._fsp--;

			stream_labeledAlt.add(labeledAlt106.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:508:15: ( OR labeledAlt )*
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( (LA28_0==OR) ) {
					alt28=1;
				}

				switch (alt28) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:508:16: OR labeledAlt
					{
					OR107=(Token)match(input,OR,FOLLOW_OR_in_ruleAltList2064);  
					stream_OR.add(OR107);

					pushFollow(FOLLOW_labeledAlt_in_ruleAltList2066);
					labeledAlt108=labeledAlt();
					state._fsp--;

					stream_labeledAlt.add(labeledAlt108.getTree());
					}
					break;

				default :
					break loop28;
				}
			}

			// AST REWRITE
			// elements: labeledAlt
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 508:32: -> ( labeledAlt )+
			{
				if ( !(stream_labeledAlt.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_labeledAlt.hasNext() ) {
					adaptor.addChild(root_0, stream_labeledAlt.nextTree());
				}
				stream_labeledAlt.reset();

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ruleAltList"


	public static class labeledAlt_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "labeledAlt"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:511:1: labeledAlt : alternative ( POUND ! id !)? ;
	public final ANTLRParser.labeledAlt_return labeledAlt() throws RecognitionException {
		ANTLRParser.labeledAlt_return retval = new ANTLRParser.labeledAlt_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token POUND110=null;
		ParserRuleReturnScope alternative109 =null;
		ParserRuleReturnScope id111 =null;

		GrammarAST POUND110_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:512:2: ( alternative ( POUND ! id !)? )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:512:4: alternative ( POUND ! id !)?
			{
			root_0 = (GrammarAST)adaptor.nil();


			pushFollow(FOLLOW_alternative_in_labeledAlt2084);
			alternative109=alternative();
			state._fsp--;

			adaptor.addChild(root_0, alternative109.getTree());

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:513:3: ( POUND ! id !)?
			int alt29=2;
			int LA29_0 = input.LA(1);
			if ( (LA29_0==POUND) ) {
				alt29=1;
			}
			switch (alt29) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:513:5: POUND ! id !
					{
					POUND110=(Token)match(input,POUND,FOLLOW_POUND_in_labeledAlt2090); 
					pushFollow(FOLLOW_id_in_labeledAlt2093);
					id111=id();
					state._fsp--;

					((AltAST)(alternative109!=null?((GrammarAST)alternative109.getTree()):null)).altLabel=(id111!=null?((GrammarAST)id111.getTree()):null);
					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "labeledAlt"


	public static class lexerRule_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerRule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:517:1: lexerRule : ( FRAGMENT )? TOKEN_REF COLON lexerRuleBlock SEMI -> ^( RULE TOKEN_REF ( ^( RULEMODIFIERS FRAGMENT ) )? lexerRuleBlock ) ;
	public final ANTLRParser.lexerRule_return lexerRule() throws RecognitionException {
		ANTLRParser.lexerRule_return retval = new ANTLRParser.lexerRule_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token FRAGMENT112=null;
		Token TOKEN_REF113=null;
		Token COLON114=null;
		Token SEMI116=null;
		ParserRuleReturnScope lexerRuleBlock115 =null;

		GrammarAST FRAGMENT112_tree=null;
		GrammarAST TOKEN_REF113_tree=null;
		GrammarAST COLON114_tree=null;
		GrammarAST SEMI116_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_SEMI=new RewriteRuleTokenStream(adaptor,"token SEMI");
		RewriteRuleTokenStream stream_FRAGMENT=new RewriteRuleTokenStream(adaptor,"token FRAGMENT");
		RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
		RewriteRuleSubtreeStream stream_lexerRuleBlock=new RewriteRuleSubtreeStream(adaptor,"rule lexerRuleBlock");

		 paraphrases.push("matching a lexer rule"); 
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:522:5: ( ( FRAGMENT )? TOKEN_REF COLON lexerRuleBlock SEMI -> ^( RULE TOKEN_REF ( ^( RULEMODIFIERS FRAGMENT ) )? lexerRuleBlock ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:522:7: ( FRAGMENT )? TOKEN_REF COLON lexerRuleBlock SEMI
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:522:7: ( FRAGMENT )?
			int alt30=2;
			int LA30_0 = input.LA(1);
			if ( (LA30_0==FRAGMENT) ) {
				alt30=1;
			}
			switch (alt30) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:522:7: FRAGMENT
					{
					FRAGMENT112=(Token)match(input,FRAGMENT,FOLLOW_FRAGMENT_in_lexerRule2125);  
					stream_FRAGMENT.add(FRAGMENT112);

					}
					break;

			}

			TOKEN_REF113=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_lexerRule2131);  
			stream_TOKEN_REF.add(TOKEN_REF113);

			COLON114=(Token)match(input,COLON,FOLLOW_COLON_in_lexerRule2133);  
			stream_COLON.add(COLON114);

			pushFollow(FOLLOW_lexerRuleBlock_in_lexerRule2135);
			lexerRuleBlock115=lexerRuleBlock();
			state._fsp--;

			stream_lexerRuleBlock.add(lexerRuleBlock115.getTree());
			SEMI116=(Token)match(input,SEMI,FOLLOW_SEMI_in_lexerRule2137);  
			stream_SEMI.add(SEMI116);

			// AST REWRITE
			// elements: FRAGMENT, TOKEN_REF, lexerRuleBlock
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 524:7: -> ^( RULE TOKEN_REF ( ^( RULEMODIFIERS FRAGMENT ) )? lexerRuleBlock )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:524:10: ^( RULE TOKEN_REF ( ^( RULEMODIFIERS FRAGMENT ) )? lexerRuleBlock )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new RuleAST(RULE), root_1);
				adaptor.addChild(root_1, stream_TOKEN_REF.nextNode());
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:525:9: ( ^( RULEMODIFIERS FRAGMENT ) )?
				if ( stream_FRAGMENT.hasNext() ) {
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:525:9: ^( RULEMODIFIERS FRAGMENT )
					{
					GrammarAST root_2 = (GrammarAST)adaptor.nil();
					root_2 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(RULEMODIFIERS, "RULEMODIFIERS"), root_2);
					adaptor.addChild(root_2, stream_FRAGMENT.nextNode());
					adaptor.addChild(root_1, root_2);
					}

				}
				stream_FRAGMENT.reset();

				adaptor.addChild(root_1, stream_lexerRuleBlock.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


				paraphrases.pop();

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerRule"


	public static class lexerRuleBlock_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerRuleBlock"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:529:1: lexerRuleBlock : lexerAltList -> ^( BLOCK[colon,\"BLOCK\"] lexerAltList ) ;
	public final ANTLRParser.lexerRuleBlock_return lexerRuleBlock() throws RecognitionException {
		ANTLRParser.lexerRuleBlock_return retval = new ANTLRParser.lexerRuleBlock_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope lexerAltList117 =null;

		RewriteRuleSubtreeStream stream_lexerAltList=new RewriteRuleSubtreeStream(adaptor,"rule lexerAltList");

		Token colon = input.LT(-1);
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:531:5: ( lexerAltList -> ^( BLOCK[colon,\"BLOCK\"] lexerAltList ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:531:7: lexerAltList
			{
			pushFollow(FOLLOW_lexerAltList_in_lexerRuleBlock2201);
			lexerAltList117=lexerAltList();
			state._fsp--;

			stream_lexerAltList.add(lexerAltList117.getTree());
			// AST REWRITE
			// elements: lexerAltList
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 531:20: -> ^( BLOCK[colon,\"BLOCK\"] lexerAltList )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:531:23: ^( BLOCK[colon,\"BLOCK\"] lexerAltList )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, colon, "BLOCK"), root_1);
				adaptor.addChild(root_1, stream_lexerAltList.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (ResyncToEndOfRuleBlock e) {

			    	// just resyncing; ignore error
					retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), null);
			    
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerRuleBlock"


	public static class lexerAltList_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerAltList"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:538:1: lexerAltList : lexerAlt ( OR lexerAlt )* -> ( lexerAlt )+ ;
	public final ANTLRParser.lexerAltList_return lexerAltList() throws RecognitionException {
		ANTLRParser.lexerAltList_return retval = new ANTLRParser.lexerAltList_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token OR119=null;
		ParserRuleReturnScope lexerAlt118 =null;
		ParserRuleReturnScope lexerAlt120 =null;

		GrammarAST OR119_tree=null;
		RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
		RewriteRuleSubtreeStream stream_lexerAlt=new RewriteRuleSubtreeStream(adaptor,"rule lexerAlt");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:539:2: ( lexerAlt ( OR lexerAlt )* -> ( lexerAlt )+ )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:539:4: lexerAlt ( OR lexerAlt )*
			{
			pushFollow(FOLLOW_lexerAlt_in_lexerAltList2237);
			lexerAlt118=lexerAlt();
			state._fsp--;

			stream_lexerAlt.add(lexerAlt118.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:539:13: ( OR lexerAlt )*
			loop31:
			while (true) {
				int alt31=2;
				int LA31_0 = input.LA(1);
				if ( (LA31_0==OR) ) {
					alt31=1;
				}

				switch (alt31) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:539:14: OR lexerAlt
					{
					OR119=(Token)match(input,OR,FOLLOW_OR_in_lexerAltList2240);  
					stream_OR.add(OR119);

					pushFollow(FOLLOW_lexerAlt_in_lexerAltList2242);
					lexerAlt120=lexerAlt();
					state._fsp--;

					stream_lexerAlt.add(lexerAlt120.getTree());
					}
					break;

				default :
					break loop31;
				}
			}

			// AST REWRITE
			// elements: lexerAlt
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 539:28: -> ( lexerAlt )+
			{
				if ( !(stream_lexerAlt.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_lexerAlt.hasNext() ) {
					adaptor.addChild(root_0, stream_lexerAlt.nextTree());
				}
				stream_lexerAlt.reset();

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerAltList"


	public static class lexerAlt_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerAlt"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:542:1: lexerAlt : lexerElements ( lexerCommands -> ^( LEXER_ALT_ACTION lexerElements lexerCommands ) | -> lexerElements ) ;
	public final ANTLRParser.lexerAlt_return lexerAlt() throws RecognitionException {
		ANTLRParser.lexerAlt_return retval = new ANTLRParser.lexerAlt_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope lexerElements121 =null;
		ParserRuleReturnScope lexerCommands122 =null;

		RewriteRuleSubtreeStream stream_lexerElements=new RewriteRuleSubtreeStream(adaptor,"rule lexerElements");
		RewriteRuleSubtreeStream stream_lexerCommands=new RewriteRuleSubtreeStream(adaptor,"rule lexerCommands");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:543:2: ( lexerElements ( lexerCommands -> ^( LEXER_ALT_ACTION lexerElements lexerCommands ) | -> lexerElements ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:543:4: lexerElements ( lexerCommands -> ^( LEXER_ALT_ACTION lexerElements lexerCommands ) | -> lexerElements )
			{
			pushFollow(FOLLOW_lexerElements_in_lexerAlt2260);
			lexerElements121=lexerElements();
			state._fsp--;

			stream_lexerElements.add(lexerElements121.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:544:3: ( lexerCommands -> ^( LEXER_ALT_ACTION lexerElements lexerCommands ) | -> lexerElements )
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( (LA32_0==RARROW) ) {
				alt32=1;
			}
			else if ( (LA32_0==OR||LA32_0==RPAREN||LA32_0==SEMI) ) {
				alt32=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 32, 0, input);
				throw nvae;
			}

			switch (alt32) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:544:5: lexerCommands
					{
					pushFollow(FOLLOW_lexerCommands_in_lexerAlt2266);
					lexerCommands122=lexerCommands();
					state._fsp--;

					stream_lexerCommands.add(lexerCommands122.getTree());
					// AST REWRITE
					// elements: lexerCommands, lexerElements
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 544:19: -> ^( LEXER_ALT_ACTION lexerElements lexerCommands )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:544:22: ^( LEXER_ALT_ACTION lexerElements lexerCommands )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(LEXER_ALT_ACTION), root_1);
						adaptor.addChild(root_1, stream_lexerElements.nextTree());
						adaptor.addChild(root_1, stream_lexerCommands.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:545:9: 
					{
					// AST REWRITE
					// elements: lexerElements
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 545:9: -> lexerElements
					{
						adaptor.addChild(root_0, stream_lexerElements.nextTree());
					}


					retval.tree = root_0;

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerAlt"


	public static class lexerElements_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerElements"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:549:1: lexerElements : ( ( lexerElement )+ -> ^( ALT ( lexerElement )+ ) | -> ^( ALT EPSILON ) );
	public final ANTLRParser.lexerElements_return lexerElements() throws RecognitionException {
		ANTLRParser.lexerElements_return retval = new ANTLRParser.lexerElements_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope lexerElement123 =null;

		RewriteRuleSubtreeStream stream_lexerElement=new RewriteRuleSubtreeStream(adaptor,"rule lexerElement");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:550:5: ( ( lexerElement )+ -> ^( ALT ( lexerElement )+ ) | -> ^( ALT EPSILON ) )
			int alt34=2;
			int LA34_0 = input.LA(1);
			if ( (LA34_0==ACTION||LA34_0==DOT||LA34_0==LEXER_CHAR_SET||LA34_0==LPAREN||LA34_0==NOT||LA34_0==RULE_REF||LA34_0==SEMPRED||LA34_0==STRING_LITERAL||LA34_0==TOKEN_REF) ) {
				alt34=1;
			}
			else if ( (LA34_0==OR||LA34_0==RARROW||LA34_0==RPAREN||LA34_0==SEMI) ) {
				alt34=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 34, 0, input);
				throw nvae;
			}

			switch (alt34) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:550:7: ( lexerElement )+
					{
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:550:7: ( lexerElement )+
					int cnt33=0;
					loop33:
					while (true) {
						int alt33=2;
						int LA33_0 = input.LA(1);
						if ( (LA33_0==ACTION||LA33_0==DOT||LA33_0==LEXER_CHAR_SET||LA33_0==LPAREN||LA33_0==NOT||LA33_0==RULE_REF||LA33_0==SEMPRED||LA33_0==STRING_LITERAL||LA33_0==TOKEN_REF) ) {
							alt33=1;
						}

						switch (alt33) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:550:7: lexerElement
							{
							pushFollow(FOLLOW_lexerElement_in_lexerElements2309);
							lexerElement123=lexerElement();
							state._fsp--;

							stream_lexerElement.add(lexerElement123.getTree());
							}
							break;

						default :
							if ( cnt33 >= 1 ) break loop33;
							EarlyExitException eee = new EarlyExitException(33, input);
							throw eee;
						}
						cnt33++;
					}

					// AST REWRITE
					// elements: lexerElement
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 550:21: -> ^( ALT ( lexerElement )+ )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:550:24: ^( ALT ( lexerElement )+ )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_1);
						if ( !(stream_lexerElement.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_lexerElement.hasNext() ) {
							adaptor.addChild(root_1, stream_lexerElement.nextTree());
						}
						stream_lexerElement.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:551:8: 
					{
					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 551:8: -> ^( ALT EPSILON )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:551:11: ^( ALT EPSILON )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_1);
						adaptor.addChild(root_1, (GrammarAST)adaptor.create(EPSILON, "EPSILON"));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerElements"


	public static class lexerElement_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:554:1: lexerElement : ( labeledLexerElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledLexerElement.start,\"BLOCK\"] ^( ALT labeledLexerElement ) ) ) | -> labeledLexerElement ) | lexerAtom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$lexerAtom.start,\"BLOCK\"] ^( ALT lexerAtom ) ) ) | -> lexerAtom ) | lexerBlock ( ebnfSuffix -> ^( ebnfSuffix lexerBlock ) | -> lexerBlock ) | actionElement );
	public final ANTLRParser.lexerElement_return lexerElement() throws RecognitionException {
		ANTLRParser.lexerElement_return retval = new ANTLRParser.lexerElement_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope labeledLexerElement124 =null;
		ParserRuleReturnScope ebnfSuffix125 =null;
		ParserRuleReturnScope lexerAtom126 =null;
		ParserRuleReturnScope ebnfSuffix127 =null;
		ParserRuleReturnScope lexerBlock128 =null;
		ParserRuleReturnScope ebnfSuffix129 =null;
		ParserRuleReturnScope actionElement130 =null;

		RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
		RewriteRuleSubtreeStream stream_lexerBlock=new RewriteRuleSubtreeStream(adaptor,"rule lexerBlock");
		RewriteRuleSubtreeStream stream_labeledLexerElement=new RewriteRuleSubtreeStream(adaptor,"rule labeledLexerElement");
		RewriteRuleSubtreeStream stream_lexerAtom=new RewriteRuleSubtreeStream(adaptor,"rule lexerAtom");


			paraphrases.push("looking for lexer rule element");
			int m = input.mark();

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:560:2: ( labeledLexerElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledLexerElement.start,\"BLOCK\"] ^( ALT labeledLexerElement ) ) ) | -> labeledLexerElement ) | lexerAtom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$lexerAtom.start,\"BLOCK\"] ^( ALT lexerAtom ) ) ) | -> lexerAtom ) | lexerBlock ( ebnfSuffix -> ^( ebnfSuffix lexerBlock ) | -> lexerBlock ) | actionElement )
			int alt38=4;
			switch ( input.LA(1) ) {
			case RULE_REF:
				{
				int LA38_1 = input.LA(2);
				if ( (LA38_1==ASSIGN||LA38_1==PLUS_ASSIGN) ) {
					alt38=1;
				}
				else if ( (LA38_1==ACTION||LA38_1==DOT||LA38_1==LEXER_CHAR_SET||LA38_1==LPAREN||LA38_1==NOT||LA38_1==OR||LA38_1==PLUS||LA38_1==QUESTION||LA38_1==RARROW||(LA38_1 >= RPAREN && LA38_1 <= SEMPRED)||(LA38_1 >= STAR && LA38_1 <= STRING_LITERAL)||LA38_1==TOKEN_REF) ) {
					alt38=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 38, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TOKEN_REF:
				{
				int LA38_2 = input.LA(2);
				if ( (LA38_2==ASSIGN||LA38_2==PLUS_ASSIGN) ) {
					alt38=1;
				}
				else if ( (LA38_2==ACTION||LA38_2==DOT||LA38_2==LEXER_CHAR_SET||(LA38_2 >= LPAREN && LA38_2 <= LT)||LA38_2==NOT||LA38_2==OR||LA38_2==PLUS||LA38_2==QUESTION||LA38_2==RARROW||(LA38_2 >= RPAREN && LA38_2 <= SEMPRED)||(LA38_2 >= STAR && LA38_2 <= STRING_LITERAL)||LA38_2==TOKEN_REF) ) {
					alt38=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 38, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case DOT:
			case LEXER_CHAR_SET:
			case NOT:
			case STRING_LITERAL:
				{
				alt38=2;
				}
				break;
			case LPAREN:
				{
				alt38=3;
				}
				break;
			case ACTION:
			case SEMPRED:
				{
				alt38=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 38, 0, input);
				throw nvae;
			}
			switch (alt38) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:560:4: labeledLexerElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledLexerElement.start,\"BLOCK\"] ^( ALT labeledLexerElement ) ) ) | -> labeledLexerElement )
					{
					pushFollow(FOLLOW_labeledLexerElement_in_lexerElement2365);
					labeledLexerElement124=labeledLexerElement();
					state._fsp--;

					stream_labeledLexerElement.add(labeledLexerElement124.getTree());
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:561:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledLexerElement.start,\"BLOCK\"] ^( ALT labeledLexerElement ) ) ) | -> labeledLexerElement )
					int alt35=2;
					int LA35_0 = input.LA(1);
					if ( (LA35_0==PLUS||LA35_0==QUESTION||LA35_0==STAR) ) {
						alt35=1;
					}
					else if ( (LA35_0==ACTION||LA35_0==DOT||LA35_0==LEXER_CHAR_SET||LA35_0==LPAREN||LA35_0==NOT||LA35_0==OR||LA35_0==RARROW||(LA35_0 >= RPAREN && LA35_0 <= SEMPRED)||LA35_0==STRING_LITERAL||LA35_0==TOKEN_REF) ) {
						alt35=2;
					}

					else {
						NoViableAltException nvae =
							new NoViableAltException("", 35, 0, input);
						throw nvae;
					}

					switch (alt35) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:561:5: ebnfSuffix
							{
							pushFollow(FOLLOW_ebnfSuffix_in_lexerElement2371);
							ebnfSuffix125=ebnfSuffix();
							state._fsp--;

							stream_ebnfSuffix.add(ebnfSuffix125.getTree());
							// AST REWRITE
							// elements: ebnfSuffix, labeledLexerElement
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 561:16: -> ^( ebnfSuffix ^( BLOCK[$labeledLexerElement.start,\"BLOCK\"] ^( ALT labeledLexerElement ) ) )
							{
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:561:19: ^( ebnfSuffix ^( BLOCK[$labeledLexerElement.start,\"BLOCK\"] ^( ALT labeledLexerElement ) ) )
								{
								GrammarAST root_1 = (GrammarAST)adaptor.nil();
								root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:561:33: ^( BLOCK[$labeledLexerElement.start,\"BLOCK\"] ^( ALT labeledLexerElement ) )
								{
								GrammarAST root_2 = (GrammarAST)adaptor.nil();
								root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, (labeledLexerElement124!=null?(labeledLexerElement124.start):null), "BLOCK"), root_2);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:561:87: ^( ALT labeledLexerElement )
								{
								GrammarAST root_3 = (GrammarAST)adaptor.nil();
								root_3 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_3);
								adaptor.addChild(root_3, stream_labeledLexerElement.nextTree());
								adaptor.addChild(root_2, root_3);
								}

								adaptor.addChild(root_1, root_2);
								}

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:562:8: 
							{
							// AST REWRITE
							// elements: labeledLexerElement
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 562:8: -> labeledLexerElement
							{
								adaptor.addChild(root_0, stream_labeledLexerElement.nextTree());
							}


							retval.tree = root_0;

							}
							break;

					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:564:4: lexerAtom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$lexerAtom.start,\"BLOCK\"] ^( ALT lexerAtom ) ) ) | -> lexerAtom )
					{
					pushFollow(FOLLOW_lexerAtom_in_lexerElement2417);
					lexerAtom126=lexerAtom();
					state._fsp--;

					stream_lexerAtom.add(lexerAtom126.getTree());
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:565:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$lexerAtom.start,\"BLOCK\"] ^( ALT lexerAtom ) ) ) | -> lexerAtom )
					int alt36=2;
					int LA36_0 = input.LA(1);
					if ( (LA36_0==PLUS||LA36_0==QUESTION||LA36_0==STAR) ) {
						alt36=1;
					}
					else if ( (LA36_0==ACTION||LA36_0==DOT||LA36_0==LEXER_CHAR_SET||LA36_0==LPAREN||LA36_0==NOT||LA36_0==OR||LA36_0==RARROW||(LA36_0 >= RPAREN && LA36_0 <= SEMPRED)||LA36_0==STRING_LITERAL||LA36_0==TOKEN_REF) ) {
						alt36=2;
					}

					else {
						NoViableAltException nvae =
							new NoViableAltException("", 36, 0, input);
						throw nvae;
					}

					switch (alt36) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:565:5: ebnfSuffix
							{
							pushFollow(FOLLOW_ebnfSuffix_in_lexerElement2423);
							ebnfSuffix127=ebnfSuffix();
							state._fsp--;

							stream_ebnfSuffix.add(ebnfSuffix127.getTree());
							// AST REWRITE
							// elements: ebnfSuffix, lexerAtom
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 565:16: -> ^( ebnfSuffix ^( BLOCK[$lexerAtom.start,\"BLOCK\"] ^( ALT lexerAtom ) ) )
							{
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:565:19: ^( ebnfSuffix ^( BLOCK[$lexerAtom.start,\"BLOCK\"] ^( ALT lexerAtom ) ) )
								{
								GrammarAST root_1 = (GrammarAST)adaptor.nil();
								root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:565:33: ^( BLOCK[$lexerAtom.start,\"BLOCK\"] ^( ALT lexerAtom ) )
								{
								GrammarAST root_2 = (GrammarAST)adaptor.nil();
								root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, (lexerAtom126!=null?(lexerAtom126.start):null), "BLOCK"), root_2);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:565:77: ^( ALT lexerAtom )
								{
								GrammarAST root_3 = (GrammarAST)adaptor.nil();
								root_3 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_3);
								adaptor.addChild(root_3, stream_lexerAtom.nextTree());
								adaptor.addChild(root_2, root_3);
								}

								adaptor.addChild(root_1, root_2);
								}

								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:566:8: 
							{
							// AST REWRITE
							// elements: lexerAtom
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 566:8: -> lexerAtom
							{
								adaptor.addChild(root_0, stream_lexerAtom.nextTree());
							}


							retval.tree = root_0;

							}
							break;

					}

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:568:4: lexerBlock ( ebnfSuffix -> ^( ebnfSuffix lexerBlock ) | -> lexerBlock )
					{
					pushFollow(FOLLOW_lexerBlock_in_lexerElement2469);
					lexerBlock128=lexerBlock();
					state._fsp--;

					stream_lexerBlock.add(lexerBlock128.getTree());
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:569:3: ( ebnfSuffix -> ^( ebnfSuffix lexerBlock ) | -> lexerBlock )
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( (LA37_0==PLUS||LA37_0==QUESTION||LA37_0==STAR) ) {
						alt37=1;
					}
					else if ( (LA37_0==ACTION||LA37_0==DOT||LA37_0==LEXER_CHAR_SET||LA37_0==LPAREN||LA37_0==NOT||LA37_0==OR||LA37_0==RARROW||(LA37_0 >= RPAREN && LA37_0 <= SEMPRED)||LA37_0==STRING_LITERAL||LA37_0==TOKEN_REF) ) {
						alt37=2;
					}

					else {
						NoViableAltException nvae =
							new NoViableAltException("", 37, 0, input);
						throw nvae;
					}

					switch (alt37) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:569:5: ebnfSuffix
							{
							pushFollow(FOLLOW_ebnfSuffix_in_lexerElement2475);
							ebnfSuffix129=ebnfSuffix();
							state._fsp--;

							stream_ebnfSuffix.add(ebnfSuffix129.getTree());
							// AST REWRITE
							// elements: lexerBlock, ebnfSuffix
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 569:16: -> ^( ebnfSuffix lexerBlock )
							{
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:569:19: ^( ebnfSuffix lexerBlock )
								{
								GrammarAST root_1 = (GrammarAST)adaptor.nil();
								root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);
								adaptor.addChild(root_1, stream_lexerBlock.nextTree());
								adaptor.addChild(root_0, root_1);
								}

							}


							retval.tree = root_0;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:570:8: 
							{
							// AST REWRITE
							// elements: lexerBlock
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 570:8: -> lexerBlock
							{
								adaptor.addChild(root_0, stream_lexerBlock.nextTree());
							}


							retval.tree = root_0;

							}
							break;

					}

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:572:4: actionElement
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_actionElement_in_lexerElement2503);
					actionElement130=actionElement();
					state._fsp--;

					adaptor.addChild(root_0, actionElement130.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

			 paraphrases.pop(); 
		}
		catch (RecognitionException re) {

			    	retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
			    	int ttype = input.get(input.range()).getType(); // seems to be next token
				    // look for anything that really belongs at the start of the rule minus the initial ID
			    	if ( ttype==COLON || ttype==RETURNS || ttype==CATCH || ttype==FINALLY || ttype==AT || ttype==EOF ) {
						RecognitionException missingSemi =
							new v4ParserException("unterminated rule (missing ';') detected at '"+
												  input.LT(1).getText()+" "+input.LT(2).getText()+"'", input);
						reportError(missingSemi);
						if ( ttype==EOF ) {
							input.seek(input.index()+1);
						}
						else if ( ttype==CATCH || ttype==FINALLY ) {
							input.seek(input.range()); // ignore what's before rule trailer stuff
						}
						else if ( ttype==RETURNS || ttype==AT ) { // scan back looking for ID of rule header
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
				
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerElement"


	public static class labeledLexerElement_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "labeledLexerElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:605:1: labeledLexerElement : id (ass= ASSIGN |ass= PLUS_ASSIGN ) ( lexerAtom -> ^( $ass id lexerAtom ) | lexerBlock -> ^( $ass id lexerBlock ) ) ;
	public final ANTLRParser.labeledLexerElement_return labeledLexerElement() throws RecognitionException {
		ANTLRParser.labeledLexerElement_return retval = new ANTLRParser.labeledLexerElement_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token ass=null;
		ParserRuleReturnScope id131 =null;
		ParserRuleReturnScope lexerAtom132 =null;
		ParserRuleReturnScope lexerBlock133 =null;

		GrammarAST ass_tree=null;
		RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
		RewriteRuleTokenStream stream_PLUS_ASSIGN=new RewriteRuleTokenStream(adaptor,"token PLUS_ASSIGN");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
		RewriteRuleSubtreeStream stream_lexerBlock=new RewriteRuleSubtreeStream(adaptor,"rule lexerBlock");
		RewriteRuleSubtreeStream stream_lexerAtom=new RewriteRuleSubtreeStream(adaptor,"rule lexerAtom");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:606:2: ( id (ass= ASSIGN |ass= PLUS_ASSIGN ) ( lexerAtom -> ^( $ass id lexerAtom ) | lexerBlock -> ^( $ass id lexerBlock ) ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:606:4: id (ass= ASSIGN |ass= PLUS_ASSIGN ) ( lexerAtom -> ^( $ass id lexerAtom ) | lexerBlock -> ^( $ass id lexerBlock ) )
			{
			pushFollow(FOLLOW_id_in_labeledLexerElement2533);
			id131=id();
			state._fsp--;

			stream_id.add(id131.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:606:7: (ass= ASSIGN |ass= PLUS_ASSIGN )
			int alt39=2;
			int LA39_0 = input.LA(1);
			if ( (LA39_0==ASSIGN) ) {
				alt39=1;
			}
			else if ( (LA39_0==PLUS_ASSIGN) ) {
				alt39=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 39, 0, input);
				throw nvae;
			}

			switch (alt39) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:606:8: ass= ASSIGN
					{
					ass=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledLexerElement2538);  
					stream_ASSIGN.add(ass);

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:606:19: ass= PLUS_ASSIGN
					{
					ass=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledLexerElement2542);  
					stream_PLUS_ASSIGN.add(ass);

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:607:3: ( lexerAtom -> ^( $ass id lexerAtom ) | lexerBlock -> ^( $ass id lexerBlock ) )
			int alt40=2;
			int LA40_0 = input.LA(1);
			if ( (LA40_0==DOT||LA40_0==LEXER_CHAR_SET||LA40_0==NOT||LA40_0==RULE_REF||LA40_0==STRING_LITERAL||LA40_0==TOKEN_REF) ) {
				alt40=1;
			}
			else if ( (LA40_0==LPAREN) ) {
				alt40=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 40, 0, input);
				throw nvae;
			}

			switch (alt40) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:607:5: lexerAtom
					{
					pushFollow(FOLLOW_lexerAtom_in_labeledLexerElement2549);
					lexerAtom132=lexerAtom();
					state._fsp--;

					stream_lexerAtom.add(lexerAtom132.getTree());
					// AST REWRITE
					// elements: ass, id, lexerAtom
					// token labels: ass
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleTokenStream stream_ass=new RewriteRuleTokenStream(adaptor,"token ass",ass);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 607:15: -> ^( $ass id lexerAtom )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:607:18: ^( $ass id lexerAtom )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(stream_ass.nextNode(), root_1);
						adaptor.addChild(root_1, stream_id.nextTree());
						adaptor.addChild(root_1, stream_lexerAtom.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:608:5: lexerBlock
					{
					pushFollow(FOLLOW_lexerBlock_in_labeledLexerElement2566);
					lexerBlock133=lexerBlock();
					state._fsp--;

					stream_lexerBlock.add(lexerBlock133.getTree());
					// AST REWRITE
					// elements: ass, lexerBlock, id
					// token labels: ass
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleTokenStream stream_ass=new RewriteRuleTokenStream(adaptor,"token ass",ass);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 608:16: -> ^( $ass id lexerBlock )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:608:19: ^( $ass id lexerBlock )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(stream_ass.nextNode(), root_1);
						adaptor.addChild(root_1, stream_id.nextTree());
						adaptor.addChild(root_1, stream_lexerBlock.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "labeledLexerElement"


	public static class lexerBlock_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerBlock"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:613:1: lexerBlock : LPAREN ( optionsSpec COLON )? lexerAltList RPAREN -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? lexerAltList ) ;
	public final ANTLRParser.lexerBlock_return lexerBlock() throws RecognitionException {
		ANTLRParser.lexerBlock_return retval = new ANTLRParser.lexerBlock_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token LPAREN134=null;
		Token COLON136=null;
		Token RPAREN138=null;
		ParserRuleReturnScope optionsSpec135 =null;
		ParserRuleReturnScope lexerAltList137 =null;

		GrammarAST LPAREN134_tree=null;
		GrammarAST COLON136_tree=null;
		GrammarAST RPAREN138_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_optionsSpec=new RewriteRuleSubtreeStream(adaptor,"rule optionsSpec");
		RewriteRuleSubtreeStream stream_lexerAltList=new RewriteRuleSubtreeStream(adaptor,"rule lexerAltList");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:620:3: ( LPAREN ( optionsSpec COLON )? lexerAltList RPAREN -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? lexerAltList ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:620:5: LPAREN ( optionsSpec COLON )? lexerAltList RPAREN
			{
			LPAREN134=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_lexerBlock2599);  
			stream_LPAREN.add(LPAREN134);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:621:9: ( optionsSpec COLON )?
			int alt41=2;
			int LA41_0 = input.LA(1);
			if ( (LA41_0==OPTIONS) ) {
				alt41=1;
			}
			switch (alt41) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:621:11: optionsSpec COLON
					{
					pushFollow(FOLLOW_optionsSpec_in_lexerBlock2611);
					optionsSpec135=optionsSpec();
					state._fsp--;

					stream_optionsSpec.add(optionsSpec135.getTree());
					COLON136=(Token)match(input,COLON,FOLLOW_COLON_in_lexerBlock2613);  
					stream_COLON.add(COLON136);

					}
					break;

			}

			pushFollow(FOLLOW_lexerAltList_in_lexerBlock2626);
			lexerAltList137=lexerAltList();
			state._fsp--;

			stream_lexerAltList.add(lexerAltList137.getTree());
			RPAREN138=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_lexerBlock2636);  
			stream_RPAREN.add(RPAREN138);

			// AST REWRITE
			// elements: optionsSpec, lexerAltList
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 624:7: -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? lexerAltList )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:624:10: ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? lexerAltList )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, LPAREN134, "BLOCK"), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:624:45: ( optionsSpec )?
				if ( stream_optionsSpec.hasNext() ) {
					adaptor.addChild(root_1, stream_optionsSpec.nextTree());
				}
				stream_optionsSpec.reset();

				adaptor.addChild(root_1, stream_lexerAltList.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


			GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.OPTIONS);
			if ( options!=null ) {
				Grammar.setNodeOptions(retval.tree, options);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerBlock"


	public static class lexerCommands_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerCommands"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:628:1: lexerCommands : RARROW lexerCommand ( COMMA lexerCommand )* -> ( lexerCommand )+ ;
	public final ANTLRParser.lexerCommands_return lexerCommands() throws RecognitionException {
		ANTLRParser.lexerCommands_return retval = new ANTLRParser.lexerCommands_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token RARROW139=null;
		Token COMMA141=null;
		ParserRuleReturnScope lexerCommand140 =null;
		ParserRuleReturnScope lexerCommand142 =null;

		GrammarAST RARROW139_tree=null;
		GrammarAST COMMA141_tree=null;
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleTokenStream stream_RARROW=new RewriteRuleTokenStream(adaptor,"token RARROW");
		RewriteRuleSubtreeStream stream_lexerCommand=new RewriteRuleSubtreeStream(adaptor,"rule lexerCommand");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:629:2: ( RARROW lexerCommand ( COMMA lexerCommand )* -> ( lexerCommand )+ )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:629:4: RARROW lexerCommand ( COMMA lexerCommand )*
			{
			RARROW139=(Token)match(input,RARROW,FOLLOW_RARROW_in_lexerCommands2673);  
			stream_RARROW.add(RARROW139);

			pushFollow(FOLLOW_lexerCommand_in_lexerCommands2675);
			lexerCommand140=lexerCommand();
			state._fsp--;

			stream_lexerCommand.add(lexerCommand140.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:629:24: ( COMMA lexerCommand )*
			loop42:
			while (true) {
				int alt42=2;
				int LA42_0 = input.LA(1);
				if ( (LA42_0==COMMA) ) {
					alt42=1;
				}

				switch (alt42) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:629:25: COMMA lexerCommand
					{
					COMMA141=(Token)match(input,COMMA,FOLLOW_COMMA_in_lexerCommands2678);  
					stream_COMMA.add(COMMA141);

					pushFollow(FOLLOW_lexerCommand_in_lexerCommands2680);
					lexerCommand142=lexerCommand();
					state._fsp--;

					stream_lexerCommand.add(lexerCommand142.getTree());
					}
					break;

				default :
					break loop42;
				}
			}

			// AST REWRITE
			// elements: lexerCommand
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 629:46: -> ( lexerCommand )+
			{
				if ( !(stream_lexerCommand.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_lexerCommand.hasNext() ) {
					adaptor.addChild(root_0, stream_lexerCommand.nextTree());
				}
				stream_lexerCommand.reset();

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerCommands"


	public static class lexerCommand_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerCommand"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:632:1: lexerCommand : ( lexerCommandName LPAREN lexerCommandExpr RPAREN -> ^( LEXER_ACTION_CALL lexerCommandName lexerCommandExpr ) | lexerCommandName );
	public final ANTLRParser.lexerCommand_return lexerCommand() throws RecognitionException {
		ANTLRParser.lexerCommand_return retval = new ANTLRParser.lexerCommand_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token LPAREN144=null;
		Token RPAREN146=null;
		ParserRuleReturnScope lexerCommandName143 =null;
		ParserRuleReturnScope lexerCommandExpr145 =null;
		ParserRuleReturnScope lexerCommandName147 =null;

		GrammarAST LPAREN144_tree=null;
		GrammarAST RPAREN146_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_lexerCommandName=new RewriteRuleSubtreeStream(adaptor,"rule lexerCommandName");
		RewriteRuleSubtreeStream stream_lexerCommandExpr=new RewriteRuleSubtreeStream(adaptor,"rule lexerCommandExpr");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:633:2: ( lexerCommandName LPAREN lexerCommandExpr RPAREN -> ^( LEXER_ACTION_CALL lexerCommandName lexerCommandExpr ) | lexerCommandName )
			int alt43=2;
			switch ( input.LA(1) ) {
			case RULE_REF:
				{
				int LA43_1 = input.LA(2);
				if ( (LA43_1==LPAREN) ) {
					alt43=1;
				}
				else if ( (LA43_1==COMMA||LA43_1==OR||LA43_1==RPAREN||LA43_1==SEMI) ) {
					alt43=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 43, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TOKEN_REF:
				{
				int LA43_2 = input.LA(2);
				if ( (LA43_2==LPAREN) ) {
					alt43=1;
				}
				else if ( (LA43_2==COMMA||LA43_2==OR||LA43_2==RPAREN||LA43_2==SEMI) ) {
					alt43=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 43, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case MODE:
				{
				int LA43_3 = input.LA(2);
				if ( (LA43_3==LPAREN) ) {
					alt43=1;
				}
				else if ( (LA43_3==COMMA||LA43_3==OR||LA43_3==RPAREN||LA43_3==SEMI) ) {
					alt43=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 43, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 43, 0, input);
				throw nvae;
			}
			switch (alt43) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:633:4: lexerCommandName LPAREN lexerCommandExpr RPAREN
					{
					pushFollow(FOLLOW_lexerCommandName_in_lexerCommand2698);
					lexerCommandName143=lexerCommandName();
					state._fsp--;

					stream_lexerCommandName.add(lexerCommandName143.getTree());
					LPAREN144=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_lexerCommand2700);  
					stream_LPAREN.add(LPAREN144);

					pushFollow(FOLLOW_lexerCommandExpr_in_lexerCommand2702);
					lexerCommandExpr145=lexerCommandExpr();
					state._fsp--;

					stream_lexerCommandExpr.add(lexerCommandExpr145.getTree());
					RPAREN146=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_lexerCommand2704);  
					stream_RPAREN.add(RPAREN146);

					// AST REWRITE
					// elements: lexerCommandName, lexerCommandExpr
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 633:52: -> ^( LEXER_ACTION_CALL lexerCommandName lexerCommandExpr )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:633:55: ^( LEXER_ACTION_CALL lexerCommandName lexerCommandExpr )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(LEXER_ACTION_CALL, "LEXER_ACTION_CALL"), root_1);
						adaptor.addChild(root_1, stream_lexerCommandName.nextTree());
						adaptor.addChild(root_1, stream_lexerCommandExpr.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:634:4: lexerCommandName
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_lexerCommandName_in_lexerCommand2719);
					lexerCommandName147=lexerCommandName();
					state._fsp--;

					adaptor.addChild(root_0, lexerCommandName147.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerCommand"


	public static class lexerCommandExpr_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerCommandExpr"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:637:1: lexerCommandExpr : ( id | INT );
	public final ANTLRParser.lexerCommandExpr_return lexerCommandExpr() throws RecognitionException {
		ANTLRParser.lexerCommandExpr_return retval = new ANTLRParser.lexerCommandExpr_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token INT149=null;
		ParserRuleReturnScope id148 =null;

		GrammarAST INT149_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:638:2: ( id | INT )
			int alt44=2;
			int LA44_0 = input.LA(1);
			if ( (LA44_0==RULE_REF||LA44_0==TOKEN_REF) ) {
				alt44=1;
			}
			else if ( (LA44_0==INT) ) {
				alt44=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 44, 0, input);
				throw nvae;
			}

			switch (alt44) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:638:4: id
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_id_in_lexerCommandExpr2730);
					id148=id();
					state._fsp--;

					adaptor.addChild(root_0, id148.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:639:4: INT
					{
					root_0 = (GrammarAST)adaptor.nil();


					INT149=(Token)match(input,INT,FOLLOW_INT_in_lexerCommandExpr2735); 
					INT149_tree = (GrammarAST)adaptor.create(INT149);
					adaptor.addChild(root_0, INT149_tree);

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerCommandExpr"


	public static class lexerCommandName_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerCommandName"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:642:1: lexerCommandName : ( id | MODE -> ID[$MODE] );
	public final ANTLRParser.lexerCommandName_return lexerCommandName() throws RecognitionException {
		ANTLRParser.lexerCommandName_return retval = new ANTLRParser.lexerCommandName_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token MODE151=null;
		ParserRuleReturnScope id150 =null;

		GrammarAST MODE151_tree=null;
		RewriteRuleTokenStream stream_MODE=new RewriteRuleTokenStream(adaptor,"token MODE");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:643:9: ( id | MODE -> ID[$MODE] )
			int alt45=2;
			int LA45_0 = input.LA(1);
			if ( (LA45_0==RULE_REF||LA45_0==TOKEN_REF) ) {
				alt45=1;
			}
			else if ( (LA45_0==MODE) ) {
				alt45=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 45, 0, input);
				throw nvae;
			}

			switch (alt45) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:643:17: id
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_id_in_lexerCommandName2759);
					id150=id();
					state._fsp--;

					adaptor.addChild(root_0, id150.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:644:17: MODE
					{
					MODE151=(Token)match(input,MODE,FOLLOW_MODE_in_lexerCommandName2777);  
					stream_MODE.add(MODE151);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 644:25: -> ID[$MODE]
					{
						adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, MODE151));
					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerCommandName"


	public static class altList_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "altList"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:647:1: altList : alternative ( OR alternative )* -> ( alternative )+ ;
	public final ANTLRParser.altList_return altList() throws RecognitionException {
		ANTLRParser.altList_return retval = new ANTLRParser.altList_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token OR153=null;
		ParserRuleReturnScope alternative152 =null;
		ParserRuleReturnScope alternative154 =null;

		GrammarAST OR153_tree=null;
		RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
		RewriteRuleSubtreeStream stream_alternative=new RewriteRuleSubtreeStream(adaptor,"rule alternative");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:648:5: ( alternative ( OR alternative )* -> ( alternative )+ )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:648:7: alternative ( OR alternative )*
			{
			pushFollow(FOLLOW_alternative_in_altList2805);
			alternative152=alternative();
			state._fsp--;

			stream_alternative.add(alternative152.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:648:19: ( OR alternative )*
			loop46:
			while (true) {
				int alt46=2;
				int LA46_0 = input.LA(1);
				if ( (LA46_0==OR) ) {
					alt46=1;
				}

				switch (alt46) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:648:20: OR alternative
					{
					OR153=(Token)match(input,OR,FOLLOW_OR_in_altList2808);  
					stream_OR.add(OR153);

					pushFollow(FOLLOW_alternative_in_altList2810);
					alternative154=alternative();
					state._fsp--;

					stream_alternative.add(alternative154.getTree());
					}
					break;

				default :
					break loop46;
				}
			}

			// AST REWRITE
			// elements: alternative
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 648:37: -> ( alternative )+
			{
				if ( !(stream_alternative.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_alternative.hasNext() ) {
					adaptor.addChild(root_0, stream_alternative.nextTree());
				}
				stream_alternative.reset();

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "altList"


	public static class alternative_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "alternative"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:652:1: alternative : (o= elementOptions )? ( (e+= element )+ -> ^( ALT ( elementOptions )? ( $e)+ ) | -> ^( ALT ( elementOptions )? EPSILON ) ) ;
	public final ANTLRParser.alternative_return alternative() throws RecognitionException {
		ANTLRParser.alternative_return retval = new ANTLRParser.alternative_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		List<Object> list_e=null;
		ParserRuleReturnScope o =null;
		RuleReturnScope e = null;
		RewriteRuleSubtreeStream stream_element=new RewriteRuleSubtreeStream(adaptor,"rule element");
		RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");

		 paraphrases.push("matching alternative"); 
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:658:2: ( (o= elementOptions )? ( (e+= element )+ -> ^( ALT ( elementOptions )? ( $e)+ ) | -> ^( ALT ( elementOptions )? EPSILON ) ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:658:4: (o= elementOptions )? ( (e+= element )+ -> ^( ALT ( elementOptions )? ( $e)+ ) | -> ^( ALT ( elementOptions )? EPSILON ) )
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:658:5: (o= elementOptions )?
			int alt47=2;
			int LA47_0 = input.LA(1);
			if ( (LA47_0==LT) ) {
				alt47=1;
			}
			switch (alt47) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:658:5: o= elementOptions
					{
					pushFollow(FOLLOW_elementOptions_in_alternative2844);
					o=elementOptions();
					state._fsp--;

					stream_elementOptions.add(o.getTree());
					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:659:3: ( (e+= element )+ -> ^( ALT ( elementOptions )? ( $e)+ ) | -> ^( ALT ( elementOptions )? EPSILON ) )
			int alt49=2;
			int LA49_0 = input.LA(1);
			if ( (LA49_0==ACTION||LA49_0==DOT||LA49_0==LPAREN||LA49_0==NOT||LA49_0==RULE_REF||LA49_0==SEMPRED||LA49_0==STRING_LITERAL||LA49_0==TOKEN_REF) ) {
				alt49=1;
			}
			else if ( (LA49_0==EOF||LA49_0==OR||LA49_0==POUND||LA49_0==RPAREN||LA49_0==SEMI) ) {
				alt49=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 49, 0, input);
				throw nvae;
			}

			switch (alt49) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:659:5: (e+= element )+
					{
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:659:6: (e+= element )+
					int cnt48=0;
					loop48:
					while (true) {
						int alt48=2;
						int LA48_0 = input.LA(1);
						if ( (LA48_0==ACTION||LA48_0==DOT||LA48_0==LPAREN||LA48_0==NOT||LA48_0==RULE_REF||LA48_0==SEMPRED||LA48_0==STRING_LITERAL||LA48_0==TOKEN_REF) ) {
							alt48=1;
						}

						switch (alt48) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:659:6: e+= element
							{
							pushFollow(FOLLOW_element_in_alternative2853);
							e=element();
							state._fsp--;

							stream_element.add(e.getTree());
							if (list_e==null) list_e=new ArrayList<Object>();
							list_e.add(e.getTree());
							}
							break;

						default :
							if ( cnt48 >= 1 ) break loop48;
							EarlyExitException eee = new EarlyExitException(48, input);
							throw eee;
						}
						cnt48++;
					}

					// AST REWRITE
					// elements: elementOptions, e
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: e
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
					RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"token e",list_e);
					root_0 = (GrammarAST)adaptor.nil();
					// 659:37: -> ^( ALT ( elementOptions )? ( $e)+ )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:659:40: ^( ALT ( elementOptions )? ( $e)+ )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_1);
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:659:54: ( elementOptions )?
						if ( stream_elementOptions.hasNext() ) {
							adaptor.addChild(root_1, stream_elementOptions.nextTree());
						}
						stream_elementOptions.reset();

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


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:660:39: 
					{
					// AST REWRITE
					// elements: elementOptions
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 660:39: -> ^( ALT ( elementOptions )? EPSILON )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:660:42: ^( ALT ( elementOptions )? EPSILON )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_1);
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:660:56: ( elementOptions )?
						if ( stream_elementOptions.hasNext() ) {
							adaptor.addChild(root_1, stream_elementOptions.nextTree());
						}
						stream_elementOptions.reset();

						adaptor.addChild(root_1, (GrammarAST)adaptor.create(EPSILON, "EPSILON"));
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


			    paraphrases.pop();
			    Grammar.setNodeOptions(retval.tree, (o!=null?((GrammarAST)o.getTree()):null));

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "alternative"


	public static class element_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "element"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:664:1: element : ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom ) | ebnf | actionElement );
	public final ANTLRParser.element_return element() throws RecognitionException {
		ANTLRParser.element_return retval = new ANTLRParser.element_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope labeledElement155 =null;
		ParserRuleReturnScope ebnfSuffix156 =null;
		ParserRuleReturnScope atom157 =null;
		ParserRuleReturnScope ebnfSuffix158 =null;
		ParserRuleReturnScope ebnf159 =null;
		ParserRuleReturnScope actionElement160 =null;

		RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
		RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
		RewriteRuleSubtreeStream stream_labeledElement=new RewriteRuleSubtreeStream(adaptor,"rule labeledElement");


			paraphrases.push("looking for rule element");
			int m = input.mark();

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:670:2: ( labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement ) | atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom ) | ebnf | actionElement )
			int alt52=4;
			switch ( input.LA(1) ) {
			case RULE_REF:
				{
				int LA52_1 = input.LA(2);
				if ( (LA52_1==ASSIGN||LA52_1==PLUS_ASSIGN) ) {
					alt52=1;
				}
				else if ( (LA52_1==EOF||LA52_1==ACTION||LA52_1==ARG_ACTION||LA52_1==DOT||(LA52_1 >= LPAREN && LA52_1 <= LT)||LA52_1==NOT||LA52_1==OR||LA52_1==PLUS||LA52_1==POUND||LA52_1==QUESTION||(LA52_1 >= RPAREN && LA52_1 <= SEMPRED)||(LA52_1 >= STAR && LA52_1 <= STRING_LITERAL)||LA52_1==TOKEN_REF) ) {
					alt52=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 52, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TOKEN_REF:
				{
				int LA52_2 = input.LA(2);
				if ( (LA52_2==ASSIGN||LA52_2==PLUS_ASSIGN) ) {
					alt52=1;
				}
				else if ( (LA52_2==EOF||LA52_2==ACTION||LA52_2==DOT||(LA52_2 >= LPAREN && LA52_2 <= LT)||LA52_2==NOT||LA52_2==OR||LA52_2==PLUS||LA52_2==POUND||LA52_2==QUESTION||(LA52_2 >= RPAREN && LA52_2 <= SEMPRED)||(LA52_2 >= STAR && LA52_2 <= STRING_LITERAL)||LA52_2==TOKEN_REF) ) {
					alt52=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 52, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case DOT:
			case NOT:
			case STRING_LITERAL:
				{
				alt52=2;
				}
				break;
			case LPAREN:
				{
				alt52=3;
				}
				break;
			case ACTION:
			case SEMPRED:
				{
				alt52=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 52, 0, input);
				throw nvae;
			}
			switch (alt52) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:670:4: labeledElement ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement )
					{
					pushFollow(FOLLOW_labeledElement_in_element2968);
					labeledElement155=labeledElement();
					state._fsp--;

					stream_labeledElement.add(labeledElement155.getTree());
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:671:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) ) | -> labeledElement )
					int alt50=2;
					int LA50_0 = input.LA(1);
					if ( (LA50_0==PLUS||LA50_0==QUESTION||LA50_0==STAR) ) {
						alt50=1;
					}
					else if ( (LA50_0==EOF||LA50_0==ACTION||LA50_0==DOT||LA50_0==LPAREN||LA50_0==NOT||LA50_0==OR||LA50_0==POUND||(LA50_0 >= RPAREN && LA50_0 <= SEMPRED)||LA50_0==STRING_LITERAL||LA50_0==TOKEN_REF) ) {
						alt50=2;
					}

					else {
						NoViableAltException nvae =
							new NoViableAltException("", 50, 0, input);
						throw nvae;
					}

					switch (alt50) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:671:5: ebnfSuffix
							{
							pushFollow(FOLLOW_ebnfSuffix_in_element2974);
							ebnfSuffix156=ebnfSuffix();
							state._fsp--;

							stream_ebnfSuffix.add(ebnfSuffix156.getTree());
							// AST REWRITE
							// elements: labeledElement, ebnfSuffix
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 671:16: -> ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) )
							{
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:671:19: ^( ebnfSuffix ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) ) )
								{
								GrammarAST root_1 = (GrammarAST)adaptor.nil();
								root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:671:33: ^( BLOCK[$labeledElement.start,\"BLOCK\"] ^( ALT labeledElement ) )
								{
								GrammarAST root_2 = (GrammarAST)adaptor.nil();
								root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, (labeledElement155!=null?(labeledElement155.start):null), "BLOCK"), root_2);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:671:82: ^( ALT labeledElement )
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


							retval.tree = root_0;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:672:8: 
							{
							// AST REWRITE
							// elements: labeledElement
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 672:8: -> labeledElement
							{
								adaptor.addChild(root_0, stream_labeledElement.nextTree());
							}


							retval.tree = root_0;

							}
							break;

					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:674:4: atom ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom )
					{
					pushFollow(FOLLOW_atom_in_element3020);
					atom157=atom();
					state._fsp--;

					stream_atom.add(atom157.getTree());
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:675:3: ( ebnfSuffix -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) ) | -> atom )
					int alt51=2;
					int LA51_0 = input.LA(1);
					if ( (LA51_0==PLUS||LA51_0==QUESTION||LA51_0==STAR) ) {
						alt51=1;
					}
					else if ( (LA51_0==EOF||LA51_0==ACTION||LA51_0==DOT||LA51_0==LPAREN||LA51_0==NOT||LA51_0==OR||LA51_0==POUND||(LA51_0 >= RPAREN && LA51_0 <= SEMPRED)||LA51_0==STRING_LITERAL||LA51_0==TOKEN_REF) ) {
						alt51=2;
					}

					else {
						NoViableAltException nvae =
							new NoViableAltException("", 51, 0, input);
						throw nvae;
					}

					switch (alt51) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:675:5: ebnfSuffix
							{
							pushFollow(FOLLOW_ebnfSuffix_in_element3026);
							ebnfSuffix158=ebnfSuffix();
							state._fsp--;

							stream_ebnfSuffix.add(ebnfSuffix158.getTree());
							// AST REWRITE
							// elements: atom, ebnfSuffix
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 675:16: -> ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) )
							{
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:675:19: ^( ebnfSuffix ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) ) )
								{
								GrammarAST root_1 = (GrammarAST)adaptor.nil();
								root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:675:33: ^( BLOCK[$atom.start,\"BLOCK\"] ^( ALT atom ) )
								{
								GrammarAST root_2 = (GrammarAST)adaptor.nil();
								root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, (atom157!=null?(atom157.start):null), "BLOCK"), root_2);
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:675:72: ^( ALT atom )
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


							retval.tree = root_0;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:676:8: 
							{
							// AST REWRITE
							// elements: atom
							// token labels: 
							// rule labels: retval
							// token list labels: 
							// rule list labels: 
							// wildcard labels: 
							retval.tree = root_0;
							RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

							root_0 = (GrammarAST)adaptor.nil();
							// 676:8: -> atom
							{
								adaptor.addChild(root_0, stream_atom.nextTree());
							}


							retval.tree = root_0;

							}
							break;

					}

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:678:4: ebnf
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_ebnf_in_element3072);
					ebnf159=ebnf();
					state._fsp--;

					adaptor.addChild(root_0, ebnf159.getTree());

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:679:4: actionElement
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_actionElement_in_element3077);
					actionElement160=actionElement();
					state._fsp--;

					adaptor.addChild(root_0, actionElement160.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

			 paraphrases.pop(); 
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
				
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "element"


	public static class actionElement_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "actionElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:708:1: actionElement : ( ACTION | ACTION elementOptions -> ^( ACTION elementOptions ) | SEMPRED | SEMPRED elementOptions -> ^( SEMPRED elementOptions ) );
	public final ANTLRParser.actionElement_return actionElement() throws RecognitionException {
		ANTLRParser.actionElement_return retval = new ANTLRParser.actionElement_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token ACTION161=null;
		Token ACTION162=null;
		Token SEMPRED164=null;
		Token SEMPRED165=null;
		ParserRuleReturnScope elementOptions163 =null;
		ParserRuleReturnScope elementOptions166 =null;

		GrammarAST ACTION161_tree=null;
		GrammarAST ACTION162_tree=null;
		GrammarAST SEMPRED164_tree=null;
		GrammarAST SEMPRED165_tree=null;
		RewriteRuleTokenStream stream_SEMPRED=new RewriteRuleTokenStream(adaptor,"token SEMPRED");
		RewriteRuleTokenStream stream_ACTION=new RewriteRuleTokenStream(adaptor,"token ACTION");
		RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:715:2: ( ACTION | ACTION elementOptions -> ^( ACTION elementOptions ) | SEMPRED | SEMPRED elementOptions -> ^( SEMPRED elementOptions ) )
			int alt53=4;
			int LA53_0 = input.LA(1);
			if ( (LA53_0==ACTION) ) {
				int LA53_1 = input.LA(2);
				if ( (LA53_1==EOF||LA53_1==ACTION||LA53_1==DOT||LA53_1==LEXER_CHAR_SET||LA53_1==LPAREN||LA53_1==NOT||LA53_1==OR||LA53_1==POUND||LA53_1==RARROW||(LA53_1 >= RPAREN && LA53_1 <= SEMPRED)||LA53_1==STRING_LITERAL||LA53_1==TOKEN_REF) ) {
					alt53=1;
				}
				else if ( (LA53_1==LT) ) {
					alt53=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA53_0==SEMPRED) ) {
				int LA53_2 = input.LA(2);
				if ( (LA53_2==EOF||LA53_2==ACTION||LA53_2==DOT||LA53_2==LEXER_CHAR_SET||LA53_2==LPAREN||LA53_2==NOT||LA53_2==OR||LA53_2==POUND||LA53_2==RARROW||(LA53_2 >= RPAREN && LA53_2 <= SEMPRED)||LA53_2==STRING_LITERAL||LA53_2==TOKEN_REF) ) {
					alt53=3;
				}
				else if ( (LA53_2==LT) ) {
					alt53=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 53, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 53, 0, input);
				throw nvae;
			}

			switch (alt53) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:715:4: ACTION
					{
					root_0 = (GrammarAST)adaptor.nil();


					ACTION161=(Token)match(input,ACTION,FOLLOW_ACTION_in_actionElement3103); 
					ACTION161_tree = new ActionAST(ACTION161) ;
					adaptor.addChild(root_0, ACTION161_tree);

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:716:6: ACTION elementOptions
					{
					ACTION162=(Token)match(input,ACTION,FOLLOW_ACTION_in_actionElement3113);  
					stream_ACTION.add(ACTION162);

					pushFollow(FOLLOW_elementOptions_in_actionElement3115);
					elementOptions163=elementOptions();
					state._fsp--;

					stream_elementOptions.add(elementOptions163.getTree());
					// AST REWRITE
					// elements: ACTION, elementOptions
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 716:28: -> ^( ACTION elementOptions )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:716:31: ^( ACTION elementOptions )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new ActionAST(stream_ACTION.nextToken()), root_1);
						adaptor.addChild(root_1, stream_elementOptions.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:717:6: SEMPRED
					{
					root_0 = (GrammarAST)adaptor.nil();


					SEMPRED164=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_actionElement3133); 
					SEMPRED164_tree = new PredAST(SEMPRED164) ;
					adaptor.addChild(root_0, SEMPRED164_tree);

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:718:6: SEMPRED elementOptions
					{
					SEMPRED165=(Token)match(input,SEMPRED,FOLLOW_SEMPRED_in_actionElement3143);  
					stream_SEMPRED.add(SEMPRED165);

					pushFollow(FOLLOW_elementOptions_in_actionElement3145);
					elementOptions166=elementOptions();
					state._fsp--;

					stream_elementOptions.add(elementOptions166.getTree());
					// AST REWRITE
					// elements: elementOptions, SEMPRED
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 718:29: -> ^( SEMPRED elementOptions )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:718:32: ^( SEMPRED elementOptions )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new PredAST(stream_SEMPRED.nextToken()), root_1);
						adaptor.addChild(root_1, stream_elementOptions.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


				GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.ELEMENT_OPTIONS);
				if ( options!=null ) {
					Grammar.setNodeOptions(retval.tree, options);
				}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "actionElement"


	public static class labeledElement_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "labeledElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:721:1: labeledElement : id (ass= ASSIGN |ass= PLUS_ASSIGN ) ( atom -> ^( $ass id atom ) | block -> ^( $ass id block ) ) ;
	public final ANTLRParser.labeledElement_return labeledElement() throws RecognitionException {
		ANTLRParser.labeledElement_return retval = new ANTLRParser.labeledElement_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token ass=null;
		ParserRuleReturnScope id167 =null;
		ParserRuleReturnScope atom168 =null;
		ParserRuleReturnScope block169 =null;

		GrammarAST ass_tree=null;
		RewriteRuleTokenStream stream_ASSIGN=new RewriteRuleTokenStream(adaptor,"token ASSIGN");
		RewriteRuleTokenStream stream_PLUS_ASSIGN=new RewriteRuleTokenStream(adaptor,"token PLUS_ASSIGN");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");
		RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:722:2: ( id (ass= ASSIGN |ass= PLUS_ASSIGN ) ( atom -> ^( $ass id atom ) | block -> ^( $ass id block ) ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:722:4: id (ass= ASSIGN |ass= PLUS_ASSIGN ) ( atom -> ^( $ass id atom ) | block -> ^( $ass id block ) )
			{
			pushFollow(FOLLOW_id_in_labeledElement3167);
			id167=id();
			state._fsp--;

			stream_id.add(id167.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:722:7: (ass= ASSIGN |ass= PLUS_ASSIGN )
			int alt54=2;
			int LA54_0 = input.LA(1);
			if ( (LA54_0==ASSIGN) ) {
				alt54=1;
			}
			else if ( (LA54_0==PLUS_ASSIGN) ) {
				alt54=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 54, 0, input);
				throw nvae;
			}

			switch (alt54) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:722:8: ass= ASSIGN
					{
					ass=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement3172);  
					stream_ASSIGN.add(ass);

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:722:19: ass= PLUS_ASSIGN
					{
					ass=(Token)match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement3176);  
					stream_PLUS_ASSIGN.add(ass);

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:723:3: ( atom -> ^( $ass id atom ) | block -> ^( $ass id block ) )
			int alt55=2;
			int LA55_0 = input.LA(1);
			if ( (LA55_0==DOT||LA55_0==NOT||LA55_0==RULE_REF||LA55_0==STRING_LITERAL||LA55_0==TOKEN_REF) ) {
				alt55=1;
			}
			else if ( (LA55_0==LPAREN) ) {
				alt55=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 55, 0, input);
				throw nvae;
			}

			switch (alt55) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:723:5: atom
					{
					pushFollow(FOLLOW_atom_in_labeledElement3183);
					atom168=atom();
					state._fsp--;

					stream_atom.add(atom168.getTree());
					// AST REWRITE
					// elements: id, atom, ass
					// token labels: ass
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleTokenStream stream_ass=new RewriteRuleTokenStream(adaptor,"token ass",ass);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 723:15: -> ^( $ass id atom )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:723:18: ^( $ass id atom )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(stream_ass.nextNode(), root_1);
						adaptor.addChild(root_1, stream_id.nextTree());
						adaptor.addChild(root_1, stream_atom.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:724:5: block
					{
					pushFollow(FOLLOW_block_in_labeledElement3205);
					block169=block();
					state._fsp--;

					stream_block.add(block169.getTree());
					// AST REWRITE
					// elements: ass, id, block
					// token labels: ass
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleTokenStream stream_ass=new RewriteRuleTokenStream(adaptor,"token ass",ass);
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 724:16: -> ^( $ass id block )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:724:19: ^( $ass id block )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(stream_ass.nextNode(), root_1);
						adaptor.addChild(root_1, stream_id.nextTree());
						adaptor.addChild(root_1, stream_block.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "labeledElement"


	public static class ebnf_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ebnf"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:730:1: ebnf : block ( blockSuffix -> ^( blockSuffix block ) | -> block ) ;
	public final ANTLRParser.ebnf_return ebnf() throws RecognitionException {
		ANTLRParser.ebnf_return retval = new ANTLRParser.ebnf_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope block170 =null;
		ParserRuleReturnScope blockSuffix171 =null;

		RewriteRuleSubtreeStream stream_block=new RewriteRuleSubtreeStream(adaptor,"rule block");
		RewriteRuleSubtreeStream stream_blockSuffix=new RewriteRuleSubtreeStream(adaptor,"rule blockSuffix");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:731:5: ( block ( blockSuffix -> ^( blockSuffix block ) | -> block ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:731:7: block ( blockSuffix -> ^( blockSuffix block ) | -> block )
			{
			pushFollow(FOLLOW_block_in_ebnf3241);
			block170=block();
			state._fsp--;

			stream_block.add(block170.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:734:7: ( blockSuffix -> ^( blockSuffix block ) | -> block )
			int alt56=2;
			int LA56_0 = input.LA(1);
			if ( (LA56_0==PLUS||LA56_0==QUESTION||LA56_0==STAR) ) {
				alt56=1;
			}
			else if ( (LA56_0==EOF||LA56_0==ACTION||LA56_0==DOT||LA56_0==LPAREN||LA56_0==NOT||LA56_0==OR||LA56_0==POUND||(LA56_0 >= RPAREN && LA56_0 <= SEMPRED)||LA56_0==STRING_LITERAL||LA56_0==TOKEN_REF) ) {
				alt56=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 56, 0, input);
				throw nvae;
			}

			switch (alt56) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:734:9: blockSuffix
					{
					pushFollow(FOLLOW_blockSuffix_in_ebnf3265);
					blockSuffix171=blockSuffix();
					state._fsp--;

					stream_blockSuffix.add(blockSuffix171.getTree());
					// AST REWRITE
					// elements: block, blockSuffix
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 734:21: -> ^( blockSuffix block )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:734:24: ^( blockSuffix block )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(stream_blockSuffix.nextNode(), root_1);
						adaptor.addChild(root_1, stream_block.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:735:12: 
					{
					// AST REWRITE
					// elements: block
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 735:12: -> block
					{
						adaptor.addChild(root_0, stream_block.nextTree());
					}


					retval.tree = root_0;

					}
					break;

			}

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ebnf"


	public static class blockSuffix_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "blockSuffix"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:741:1: blockSuffix : ebnfSuffix ;
	public final ANTLRParser.blockSuffix_return blockSuffix() throws RecognitionException {
		ANTLRParser.blockSuffix_return retval = new ANTLRParser.blockSuffix_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope ebnfSuffix172 =null;


		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:742:5: ( ebnfSuffix )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:742:7: ebnfSuffix
			{
			root_0 = (GrammarAST)adaptor.nil();


			pushFollow(FOLLOW_ebnfSuffix_in_blockSuffix3315);
			ebnfSuffix172=ebnfSuffix();
			state._fsp--;

			adaptor.addChild(root_0, ebnfSuffix172.getTree());

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "blockSuffix"


	public static class ebnfSuffix_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ebnfSuffix"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:745:1: ebnfSuffix : ( QUESTION (nongreedy= QUESTION )? -> OPTIONAL[$start, $nongreedy] | STAR (nongreedy= QUESTION )? -> CLOSURE[$start, $nongreedy] | PLUS (nongreedy= QUESTION )? -> POSITIVE_CLOSURE[$start, $nongreedy] );
	public final ANTLRParser.ebnfSuffix_return ebnfSuffix() throws RecognitionException {
		ANTLRParser.ebnfSuffix_return retval = new ANTLRParser.ebnfSuffix_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token nongreedy=null;
		Token QUESTION173=null;
		Token STAR174=null;
		Token PLUS175=null;

		GrammarAST nongreedy_tree=null;
		GrammarAST QUESTION173_tree=null;
		GrammarAST STAR174_tree=null;
		GrammarAST PLUS175_tree=null;
		RewriteRuleTokenStream stream_PLUS=new RewriteRuleTokenStream(adaptor,"token PLUS");
		RewriteRuleTokenStream stream_STAR=new RewriteRuleTokenStream(adaptor,"token STAR");
		RewriteRuleTokenStream stream_QUESTION=new RewriteRuleTokenStream(adaptor,"token QUESTION");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:746:2: ( QUESTION (nongreedy= QUESTION )? -> OPTIONAL[$start, $nongreedy] | STAR (nongreedy= QUESTION )? -> CLOSURE[$start, $nongreedy] | PLUS (nongreedy= QUESTION )? -> POSITIVE_CLOSURE[$start, $nongreedy] )
			int alt60=3;
			switch ( input.LA(1) ) {
			case QUESTION:
				{
				alt60=1;
				}
				break;
			case STAR:
				{
				alt60=2;
				}
				break;
			case PLUS:
				{
				alt60=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 60, 0, input);
				throw nvae;
			}
			switch (alt60) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:746:4: QUESTION (nongreedy= QUESTION )?
					{
					QUESTION173=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3330);  
					stream_QUESTION.add(QUESTION173);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:746:22: (nongreedy= QUESTION )?
					int alt57=2;
					int LA57_0 = input.LA(1);
					if ( (LA57_0==QUESTION) ) {
						alt57=1;
					}
					switch (alt57) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:746:22: nongreedy= QUESTION
							{
							nongreedy=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3334);  
							stream_QUESTION.add(nongreedy);

							}
							break;

					}

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 746:33: -> OPTIONAL[$start, $nongreedy]
					{
						adaptor.addChild(root_0, new OptionalBlockAST(OPTIONAL, (retval.start), nongreedy));
					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:747:6: STAR (nongreedy= QUESTION )?
					{
					STAR174=(Token)match(input,STAR,FOLLOW_STAR_in_ebnfSuffix3350);  
					stream_STAR.add(STAR174);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:747:20: (nongreedy= QUESTION )?
					int alt58=2;
					int LA58_0 = input.LA(1);
					if ( (LA58_0==QUESTION) ) {
						alt58=1;
					}
					switch (alt58) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:747:20: nongreedy= QUESTION
							{
							nongreedy=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3354);  
							stream_QUESTION.add(nongreedy);

							}
							break;

					}

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 747:32: -> CLOSURE[$start, $nongreedy]
					{
						adaptor.addChild(root_0, new StarBlockAST(CLOSURE, (retval.start), nongreedy));
					}


					retval.tree = root_0;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:748:7: PLUS (nongreedy= QUESTION )?
					{
					PLUS175=(Token)match(input,PLUS,FOLLOW_PLUS_in_ebnfSuffix3372);  
					stream_PLUS.add(PLUS175);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:748:21: (nongreedy= QUESTION )?
					int alt59=2;
					int LA59_0 = input.LA(1);
					if ( (LA59_0==QUESTION) ) {
						alt59=1;
					}
					switch (alt59) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:748:21: nongreedy= QUESTION
							{
							nongreedy=(Token)match(input,QUESTION,FOLLOW_QUESTION_in_ebnfSuffix3376);  
							stream_QUESTION.add(nongreedy);

							}
							break;

					}

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 748:33: -> POSITIVE_CLOSURE[$start, $nongreedy]
					{
						adaptor.addChild(root_0, new PlusBlockAST(POSITIVE_CLOSURE, (retval.start), nongreedy));
					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ebnfSuffix"


	public static class lexerAtom_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "lexerAtom"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:751:1: lexerAtom : ( range | terminal | RULE_REF | notSet | wildcard | LEXER_CHAR_SET );
	public final ANTLRParser.lexerAtom_return lexerAtom() throws RecognitionException {
		ANTLRParser.lexerAtom_return retval = new ANTLRParser.lexerAtom_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token RULE_REF178=null;
		Token LEXER_CHAR_SET181=null;
		ParserRuleReturnScope range176 =null;
		ParserRuleReturnScope terminal177 =null;
		ParserRuleReturnScope notSet179 =null;
		ParserRuleReturnScope wildcard180 =null;

		GrammarAST RULE_REF178_tree=null;
		GrammarAST LEXER_CHAR_SET181_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:752:2: ( range | terminal | RULE_REF | notSet | wildcard | LEXER_CHAR_SET )
			int alt61=6;
			switch ( input.LA(1) ) {
			case STRING_LITERAL:
				{
				int LA61_1 = input.LA(2);
				if ( (LA61_1==RANGE) ) {
					alt61=1;
				}
				else if ( (LA61_1==ACTION||LA61_1==DOT||LA61_1==LEXER_CHAR_SET||(LA61_1 >= LPAREN && LA61_1 <= LT)||LA61_1==NOT||LA61_1==OR||LA61_1==PLUS||LA61_1==QUESTION||LA61_1==RARROW||(LA61_1 >= RPAREN && LA61_1 <= SEMPRED)||(LA61_1 >= STAR && LA61_1 <= STRING_LITERAL)||LA61_1==TOKEN_REF) ) {
					alt61=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 61, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TOKEN_REF:
				{
				alt61=2;
				}
				break;
			case RULE_REF:
				{
				alt61=3;
				}
				break;
			case NOT:
				{
				alt61=4;
				}
				break;
			case DOT:
				{
				alt61=5;
				}
				break;
			case LEXER_CHAR_SET:
				{
				alt61=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 61, 0, input);
				throw nvae;
			}
			switch (alt61) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:752:4: range
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_range_in_lexerAtom3397);
					range176=range();
					state._fsp--;

					adaptor.addChild(root_0, range176.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:753:4: terminal
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_terminal_in_lexerAtom3402);
					terminal177=terminal();
					state._fsp--;

					adaptor.addChild(root_0, terminal177.getTree());

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:754:9: RULE_REF
					{
					root_0 = (GrammarAST)adaptor.nil();


					RULE_REF178=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_lexerAtom3412); 
					RULE_REF178_tree = new RuleRefAST(RULE_REF178) ;
					adaptor.addChild(root_0, RULE_REF178_tree);

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:755:7: notSet
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_notSet_in_lexerAtom3423);
					notSet179=notSet();
					state._fsp--;

					adaptor.addChild(root_0, notSet179.getTree());

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:756:7: wildcard
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_wildcard_in_lexerAtom3431);
					wildcard180=wildcard();
					state._fsp--;

					adaptor.addChild(root_0, wildcard180.getTree());

					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:757:7: LEXER_CHAR_SET
					{
					root_0 = (GrammarAST)adaptor.nil();


					LEXER_CHAR_SET181=(Token)match(input,LEXER_CHAR_SET,FOLLOW_LEXER_CHAR_SET_in_lexerAtom3439); 
					LEXER_CHAR_SET181_tree = (GrammarAST)adaptor.create(LEXER_CHAR_SET181);
					adaptor.addChild(root_0, LEXER_CHAR_SET181_tree);

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerAtom"


	public static class atom_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "atom"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:760:1: atom : ( range | terminal | ruleref | notSet | wildcard );
	public final ANTLRParser.atom_return atom() throws RecognitionException {
		ANTLRParser.atom_return retval = new ANTLRParser.atom_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		ParserRuleReturnScope range182 =null;
		ParserRuleReturnScope terminal183 =null;
		ParserRuleReturnScope ruleref184 =null;
		ParserRuleReturnScope notSet185 =null;
		ParserRuleReturnScope wildcard186 =null;


		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:761:2: ( range | terminal | ruleref | notSet | wildcard )
			int alt62=5;
			switch ( input.LA(1) ) {
			case STRING_LITERAL:
				{
				int LA62_1 = input.LA(2);
				if ( (LA62_1==RANGE) ) {
					alt62=1;
				}
				else if ( (LA62_1==EOF||LA62_1==ACTION||LA62_1==DOT||(LA62_1 >= LPAREN && LA62_1 <= LT)||LA62_1==NOT||LA62_1==OR||LA62_1==PLUS||LA62_1==POUND||LA62_1==QUESTION||(LA62_1 >= RPAREN && LA62_1 <= SEMPRED)||(LA62_1 >= STAR && LA62_1 <= STRING_LITERAL)||LA62_1==TOKEN_REF) ) {
					alt62=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 62, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TOKEN_REF:
				{
				alt62=2;
				}
				break;
			case RULE_REF:
				{
				alt62=3;
				}
				break;
			case NOT:
				{
				alt62=4;
				}
				break;
			case DOT:
				{
				alt62=5;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 62, 0, input);
				throw nvae;
			}
			switch (alt62) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:775:9: range
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_range_in_atom3484);
					range182=range();
					state._fsp--;

					adaptor.addChild(root_0, range182.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:776:4: terminal
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_terminal_in_atom3491);
					terminal183=terminal();
					state._fsp--;

					adaptor.addChild(root_0, terminal183.getTree());

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:777:9: ruleref
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_ruleref_in_atom3501);
					ruleref184=ruleref();
					state._fsp--;

					adaptor.addChild(root_0, ruleref184.getTree());

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:778:7: notSet
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_notSet_in_atom3509);
					notSet185=notSet();
					state._fsp--;

					adaptor.addChild(root_0, notSet185.getTree());

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:779:7: wildcard
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_wildcard_in_atom3517);
					wildcard186=wildcard();
					state._fsp--;

					adaptor.addChild(root_0, wildcard186.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			 throw re; 
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "atom"


	public static class wildcard_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "wildcard"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:783:1: wildcard : DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) ;
	public final ANTLRParser.wildcard_return wildcard() throws RecognitionException {
		ANTLRParser.wildcard_return retval = new ANTLRParser.wildcard_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token DOT187=null;
		ParserRuleReturnScope elementOptions188 =null;

		GrammarAST DOT187_tree=null;
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:790:2: ( DOT ( elementOptions )? -> ^( WILDCARD[$DOT] ( elementOptions )? ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:795:6: DOT ( elementOptions )?
			{
			DOT187=(Token)match(input,DOT,FOLLOW_DOT_in_wildcard3565);  
			stream_DOT.add(DOT187);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:795:10: ( elementOptions )?
			int alt63=2;
			int LA63_0 = input.LA(1);
			if ( (LA63_0==LT) ) {
				alt63=1;
			}
			switch (alt63) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:795:10: elementOptions
					{
					pushFollow(FOLLOW_elementOptions_in_wildcard3567);
					elementOptions188=elementOptions();
					state._fsp--;

					stream_elementOptions.add(elementOptions188.getTree());
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
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 796:6: -> ^( WILDCARD[$DOT] ( elementOptions )? )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:796:9: ^( WILDCARD[$DOT] ( elementOptions )? )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(WILDCARD, DOT187), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:796:39: ( elementOptions )?
				if ( stream_elementOptions.hasNext() ) {
					adaptor.addChild(root_1, stream_elementOptions.nextTree());
				}
				stream_elementOptions.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


				GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.ELEMENT_OPTIONS);
				if ( options!=null ) {
					Grammar.setNodeOptions(retval.tree, options);
				}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "wildcard"


	public static class notSet_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "notSet"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:804:1: notSet : ( NOT setElement -> ^( NOT[$NOT] ^( SET[$setElement.start,\"SET\"] setElement ) ) | NOT blockSet -> ^( NOT[$NOT] blockSet ) );
	public final ANTLRParser.notSet_return notSet() throws RecognitionException {
		ANTLRParser.notSet_return retval = new ANTLRParser.notSet_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token NOT189=null;
		Token NOT191=null;
		ParserRuleReturnScope setElement190 =null;
		ParserRuleReturnScope blockSet192 =null;

		GrammarAST NOT189_tree=null;
		GrammarAST NOT191_tree=null;
		RewriteRuleTokenStream stream_NOT=new RewriteRuleTokenStream(adaptor,"token NOT");
		RewriteRuleSubtreeStream stream_setElement=new RewriteRuleSubtreeStream(adaptor,"rule setElement");
		RewriteRuleSubtreeStream stream_blockSet=new RewriteRuleSubtreeStream(adaptor,"rule blockSet");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:805:5: ( NOT setElement -> ^( NOT[$NOT] ^( SET[$setElement.start,\"SET\"] setElement ) ) | NOT blockSet -> ^( NOT[$NOT] blockSet ) )
			int alt64=2;
			int LA64_0 = input.LA(1);
			if ( (LA64_0==NOT) ) {
				int LA64_1 = input.LA(2);
				if ( (LA64_1==LEXER_CHAR_SET||LA64_1==STRING_LITERAL||LA64_1==TOKEN_REF) ) {
					alt64=1;
				}
				else if ( (LA64_1==LPAREN) ) {
					alt64=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 64, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 64, 0, input);
				throw nvae;
			}

			switch (alt64) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:805:7: NOT setElement
					{
					NOT189=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3605);  
					stream_NOT.add(NOT189);

					pushFollow(FOLLOW_setElement_in_notSet3607);
					setElement190=setElement();
					state._fsp--;

					stream_setElement.add(setElement190.getTree());
					// AST REWRITE
					// elements: setElement, NOT
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 805:22: -> ^( NOT[$NOT] ^( SET[$setElement.start,\"SET\"] setElement ) )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:805:25: ^( NOT[$NOT] ^( SET[$setElement.start,\"SET\"] setElement ) )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new NotAST(NOT, NOT189), root_1);
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:805:45: ^( SET[$setElement.start,\"SET\"] setElement )
						{
						GrammarAST root_2 = (GrammarAST)adaptor.nil();
						root_2 = (GrammarAST)adaptor.becomeRoot(new SetAST(SET, (setElement190!=null?(setElement190.start):null), "SET"), root_2);
						adaptor.addChild(root_2, stream_setElement.nextTree());
						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:806:7: NOT blockSet
					{
					NOT191=(Token)match(input,NOT,FOLLOW_NOT_in_notSet3635);  
					stream_NOT.add(NOT191);

					pushFollow(FOLLOW_blockSet_in_notSet3637);
					blockSet192=blockSet();
					state._fsp--;

					stream_blockSet.add(blockSet192.getTree());
					// AST REWRITE
					// elements: NOT, blockSet
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 806:21: -> ^( NOT[$NOT] blockSet )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:806:24: ^( NOT[$NOT] blockSet )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new NotAST(NOT, NOT191), root_1);
						adaptor.addChild(root_1, stream_blockSet.nextTree());
						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "notSet"


	public static class blockSet_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "blockSet"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:809:1: blockSet : LPAREN setElement ( OR setElement )* RPAREN -> ^( SET[$LPAREN,\"SET\"] ( setElement )+ ) ;
	public final ANTLRParser.blockSet_return blockSet() throws RecognitionException {
		ANTLRParser.blockSet_return retval = new ANTLRParser.blockSet_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token LPAREN193=null;
		Token OR195=null;
		Token RPAREN197=null;
		ParserRuleReturnScope setElement194 =null;
		ParserRuleReturnScope setElement196 =null;

		GrammarAST LPAREN193_tree=null;
		GrammarAST OR195_tree=null;
		GrammarAST RPAREN197_tree=null;
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
		RewriteRuleSubtreeStream stream_setElement=new RewriteRuleSubtreeStream(adaptor,"rule setElement");


			Token t;
			boolean ebnf = false;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:814:5: ( LPAREN setElement ( OR setElement )* RPAREN -> ^( SET[$LPAREN,\"SET\"] ( setElement )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:814:7: LPAREN setElement ( OR setElement )* RPAREN
			{
			LPAREN193=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_blockSet3672);  
			stream_LPAREN.add(LPAREN193);

			pushFollow(FOLLOW_setElement_in_blockSet3674);
			setElement194=setElement();
			state._fsp--;

			stream_setElement.add(setElement194.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:814:25: ( OR setElement )*
			loop65:
			while (true) {
				int alt65=2;
				int LA65_0 = input.LA(1);
				if ( (LA65_0==OR) ) {
					alt65=1;
				}

				switch (alt65) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:814:26: OR setElement
					{
					OR195=(Token)match(input,OR,FOLLOW_OR_in_blockSet3677);  
					stream_OR.add(OR195);

					pushFollow(FOLLOW_setElement_in_blockSet3679);
					setElement196=setElement();
					state._fsp--;

					stream_setElement.add(setElement196.getTree());
					}
					break;

				default :
					break loop65;
				}
			}

			RPAREN197=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_blockSet3683);  
			stream_RPAREN.add(RPAREN197);

			// AST REWRITE
			// elements: setElement
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 815:3: -> ^( SET[$LPAREN,\"SET\"] ( setElement )+ )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:815:6: ^( SET[$LPAREN,\"SET\"] ( setElement )+ )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new SetAST(SET, LPAREN193, "SET"), root_1);
				if ( !(stream_setElement.hasNext()) ) {
					throw new RewriteEarlyExitException();
				}
				while ( stream_setElement.hasNext() ) {
					adaptor.addChild(root_1, stream_setElement.nextTree());
				}
				stream_setElement.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "blockSet"


	public static class setElement_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "setElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:818:1: setElement : ( TOKEN_REF ^ ( elementOptions )? | STRING_LITERAL ^ ( elementOptions )? | range | LEXER_CHAR_SET );
	public final ANTLRParser.setElement_return setElement() throws RecognitionException {
		ANTLRParser.setElement_return retval = new ANTLRParser.setElement_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token TOKEN_REF198=null;
		Token STRING_LITERAL200=null;
		Token LEXER_CHAR_SET203=null;
		ParserRuleReturnScope elementOptions199 =null;
		ParserRuleReturnScope elementOptions201 =null;
		ParserRuleReturnScope range202 =null;

		GrammarAST TOKEN_REF198_tree=null;
		GrammarAST STRING_LITERAL200_tree=null;
		GrammarAST LEXER_CHAR_SET203_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:819:2: ( TOKEN_REF ^ ( elementOptions )? | STRING_LITERAL ^ ( elementOptions )? | range | LEXER_CHAR_SET )
			int alt68=4;
			switch ( input.LA(1) ) {
			case TOKEN_REF:
				{
				alt68=1;
				}
				break;
			case STRING_LITERAL:
				{
				int LA68_2 = input.LA(2);
				if ( (LA68_2==RANGE) ) {
					alt68=3;
				}
				else if ( (LA68_2==EOF||LA68_2==ACTION||LA68_2==DOT||LA68_2==LEXER_CHAR_SET||(LA68_2 >= LPAREN && LA68_2 <= LT)||LA68_2==NOT||LA68_2==OR||LA68_2==PLUS||LA68_2==POUND||LA68_2==QUESTION||LA68_2==RARROW||(LA68_2 >= RPAREN && LA68_2 <= SEMPRED)||(LA68_2 >= STAR && LA68_2 <= STRING_LITERAL)||LA68_2==TOKEN_REF) ) {
					alt68=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 68, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LEXER_CHAR_SET:
				{
				alt68=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 68, 0, input);
				throw nvae;
			}
			switch (alt68) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:819:4: TOKEN_REF ^ ( elementOptions )?
					{
					root_0 = (GrammarAST)adaptor.nil();


					TOKEN_REF198=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_setElement3713); 
					TOKEN_REF198_tree = new TerminalAST(TOKEN_REF198) ;
					root_0 = (GrammarAST)adaptor.becomeRoot(TOKEN_REF198_tree, root_0);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:819:28: ( elementOptions )?
					int alt66=2;
					int LA66_0 = input.LA(1);
					if ( (LA66_0==LT) ) {
						alt66=1;
					}
					switch (alt66) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:819:28: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_setElement3719);
							elementOptions199=elementOptions();
							state._fsp--;

							adaptor.addChild(root_0, elementOptions199.getTree());

							}
							break;

					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:820:4: STRING_LITERAL ^ ( elementOptions )?
					{
					root_0 = (GrammarAST)adaptor.nil();


					STRING_LITERAL200=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement3725); 
					STRING_LITERAL200_tree = new TerminalAST(STRING_LITERAL200) ;
					root_0 = (GrammarAST)adaptor.becomeRoot(STRING_LITERAL200_tree, root_0);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:820:33: ( elementOptions )?
					int alt67=2;
					int LA67_0 = input.LA(1);
					if ( (LA67_0==LT) ) {
						alt67=1;
					}
					switch (alt67) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:820:33: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_setElement3731);
							elementOptions201=elementOptions();
							state._fsp--;

							adaptor.addChild(root_0, elementOptions201.getTree());

							}
							break;

					}

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:821:4: range
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_range_in_setElement3737);
					range202=range();
					state._fsp--;

					adaptor.addChild(root_0, range202.getTree());

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:822:9: LEXER_CHAR_SET
					{
					root_0 = (GrammarAST)adaptor.nil();


					LEXER_CHAR_SET203=(Token)match(input,LEXER_CHAR_SET,FOLLOW_LEXER_CHAR_SET_in_setElement3747); 
					LEXER_CHAR_SET203_tree = (GrammarAST)adaptor.create(LEXER_CHAR_SET203);
					adaptor.addChild(root_0, LEXER_CHAR_SET203_tree);

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "setElement"


	public static class block_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "block"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:832:1: block : LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList ) ;
	public final ANTLRParser.block_return block() throws RecognitionException {
		ANTLRParser.block_return retval = new ANTLRParser.block_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token LPAREN204=null;
		Token COLON206=null;
		Token RPAREN208=null;
		List<Object> list_ra=null;
		ParserRuleReturnScope optionsSpec205 =null;
		ParserRuleReturnScope altList207 =null;
		RuleReturnScope ra = null;
		GrammarAST LPAREN204_tree=null;
		GrammarAST COLON206_tree=null;
		GrammarAST RPAREN208_tree=null;
		RewriteRuleTokenStream stream_COLON=new RewriteRuleTokenStream(adaptor,"token COLON");
		RewriteRuleTokenStream stream_RPAREN=new RewriteRuleTokenStream(adaptor,"token RPAREN");
		RewriteRuleTokenStream stream_LPAREN=new RewriteRuleTokenStream(adaptor,"token LPAREN");
		RewriteRuleSubtreeStream stream_optionsSpec=new RewriteRuleSubtreeStream(adaptor,"rule optionsSpec");
		RewriteRuleSubtreeStream stream_altList=new RewriteRuleSubtreeStream(adaptor,"rule altList");
		RewriteRuleSubtreeStream stream_ruleAction=new RewriteRuleSubtreeStream(adaptor,"rule ruleAction");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:839:3: ( LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:839:5: LPAREN ( ( optionsSpec )? (ra+= ruleAction )* COLON )? altList RPAREN
			{
			LPAREN204=(Token)match(input,LPAREN,FOLLOW_LPAREN_in_block3771);  
			stream_LPAREN.add(LPAREN204);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:840:9: ( ( optionsSpec )? (ra+= ruleAction )* COLON )?
			int alt71=2;
			int LA71_0 = input.LA(1);
			if ( (LA71_0==AT||LA71_0==COLON||LA71_0==OPTIONS) ) {
				alt71=1;
			}
			switch (alt71) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:840:11: ( optionsSpec )? (ra+= ruleAction )* COLON
					{
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:840:11: ( optionsSpec )?
					int alt69=2;
					int LA69_0 = input.LA(1);
					if ( (LA69_0==OPTIONS) ) {
						alt69=1;
					}
					switch (alt69) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:840:11: optionsSpec
							{
							pushFollow(FOLLOW_optionsSpec_in_block3783);
							optionsSpec205=optionsSpec();
							state._fsp--;

							stream_optionsSpec.add(optionsSpec205.getTree());
							}
							break;

					}

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:840:26: (ra+= ruleAction )*
					loop70:
					while (true) {
						int alt70=2;
						int LA70_0 = input.LA(1);
						if ( (LA70_0==AT) ) {
							alt70=1;
						}

						switch (alt70) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:840:26: ra+= ruleAction
							{
							pushFollow(FOLLOW_ruleAction_in_block3788);
							ra=ruleAction();
							state._fsp--;

							stream_ruleAction.add(ra.getTree());
							if (list_ra==null) list_ra=new ArrayList<Object>();
							list_ra.add(ra.getTree());
							}
							break;

						default :
							break loop70;
						}
					}

					COLON206=(Token)match(input,COLON,FOLLOW_COLON_in_block3791);  
					stream_COLON.add(COLON206);

					}
					break;

			}

			pushFollow(FOLLOW_altList_in_block3804);
			altList207=altList();
			state._fsp--;

			stream_altList.add(altList207.getTree());
			RPAREN208=(Token)match(input,RPAREN,FOLLOW_RPAREN_in_block3808);  
			stream_RPAREN.add(RPAREN208);

			// AST REWRITE
			// elements: altList, optionsSpec, ra
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: ra
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);
			RewriteRuleSubtreeStream stream_ra=new RewriteRuleSubtreeStream(adaptor,"token ra",list_ra);
			root_0 = (GrammarAST)adaptor.nil();
			// 843:7: -> ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:843:10: ^( BLOCK[$LPAREN,\"BLOCK\"] ( optionsSpec )? ( $ra)* altList )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, LPAREN204, "BLOCK"), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:843:45: ( optionsSpec )?
				if ( stream_optionsSpec.hasNext() ) {
					adaptor.addChild(root_1, stream_optionsSpec.nextTree());
				}
				stream_optionsSpec.reset();

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:843:59: ( $ra)*
				while ( stream_ra.hasNext() ) {
					adaptor.addChild(root_1, stream_ra.nextTree());
				}
				stream_ra.reset();

				adaptor.addChild(root_1, stream_altList.nextTree());
				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


			GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.OPTIONS);
			if ( options!=null ) {
				Grammar.setNodeOptions(retval.tree, options);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "block"


	public static class ruleref_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ruleref"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:852:1: ruleref : RULE_REF ( ARG_ACTION )? ( elementOptions )? -> ^( RULE_REF ( ARG_ACTION )? ( elementOptions )? ) ;
	public final ANTLRParser.ruleref_return ruleref() throws RecognitionException {
		ANTLRParser.ruleref_return retval = new ANTLRParser.ruleref_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token RULE_REF209=null;
		Token ARG_ACTION210=null;
		ParserRuleReturnScope elementOptions211 =null;

		GrammarAST RULE_REF209_tree=null;
		GrammarAST ARG_ACTION210_tree=null;
		RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
		RewriteRuleTokenStream stream_ARG_ACTION=new RewriteRuleTokenStream(adaptor,"token ARG_ACTION");
		RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:5: ( RULE_REF ( ARG_ACTION )? ( elementOptions )? -> ^( RULE_REF ( ARG_ACTION )? ( elementOptions )? ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:7: RULE_REF ( ARG_ACTION )? ( elementOptions )?
			{
			RULE_REF209=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref3862);  
			stream_RULE_REF.add(RULE_REF209);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:16: ( ARG_ACTION )?
			int alt72=2;
			int LA72_0 = input.LA(1);
			if ( (LA72_0==ARG_ACTION) ) {
				alt72=1;
			}
			switch (alt72) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:16: ARG_ACTION
					{
					ARG_ACTION210=(Token)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref3864);  
					stream_ARG_ACTION.add(ARG_ACTION210);

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:28: ( elementOptions )?
			int alt73=2;
			int LA73_0 = input.LA(1);
			if ( (LA73_0==LT) ) {
				alt73=1;
			}
			switch (alt73) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:28: elementOptions
					{
					pushFollow(FOLLOW_elementOptions_in_ruleref3867);
					elementOptions211=elementOptions();
					state._fsp--;

					stream_elementOptions.add(elementOptions211.getTree());
					}
					break;

			}

			// AST REWRITE
			// elements: RULE_REF, elementOptions, ARG_ACTION
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 859:44: -> ^( RULE_REF ( ARG_ACTION )? ( elementOptions )? )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:47: ^( RULE_REF ( ARG_ACTION )? ( elementOptions )? )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(new RuleRefAST(stream_RULE_REF.nextToken()), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:70: ( ARG_ACTION )?
				if ( stream_ARG_ACTION.hasNext() ) {
					adaptor.addChild(root_1, new ActionAST(stream_ARG_ACTION.nextToken()));
				}
				stream_ARG_ACTION.reset();

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:859:93: ( elementOptions )?
				if ( stream_elementOptions.hasNext() ) {
					adaptor.addChild(root_1, stream_elementOptions.nextTree());
				}
				stream_elementOptions.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


			GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.ELEMENT_OPTIONS);
			if ( options!=null ) {
				Grammar.setNodeOptions(retval.tree, options);
			}

		}
		catch (RecognitionException re) {
			 throw re; 
		}

		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ruleref"


	public static class range_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "range"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:872:1: range : STRING_LITERAL RANGE ^ STRING_LITERAL ;
	public final ANTLRParser.range_return range() throws RecognitionException {
		ANTLRParser.range_return retval = new ANTLRParser.range_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token STRING_LITERAL212=null;
		Token RANGE213=null;
		Token STRING_LITERAL214=null;

		GrammarAST STRING_LITERAL212_tree=null;
		GrammarAST RANGE213_tree=null;
		GrammarAST STRING_LITERAL214_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:873:5: ( STRING_LITERAL RANGE ^ STRING_LITERAL )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:873:7: STRING_LITERAL RANGE ^ STRING_LITERAL
			{
			root_0 = (GrammarAST)adaptor.nil();


			STRING_LITERAL212=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range3923); 
			STRING_LITERAL212_tree = new TerminalAST(STRING_LITERAL212) ;
			adaptor.addChild(root_0, STRING_LITERAL212_tree);

			RANGE213=(Token)match(input,RANGE,FOLLOW_RANGE_in_range3928); 
			RANGE213_tree = new RangeAST(RANGE213) ;
			root_0 = (GrammarAST)adaptor.becomeRoot(RANGE213_tree, root_0);

			STRING_LITERAL214=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range3934); 
			STRING_LITERAL214_tree = new TerminalAST(STRING_LITERAL214) ;
			adaptor.addChild(root_0, STRING_LITERAL214_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "range"


	public static class terminal_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "terminal"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:876:1: terminal : ( TOKEN_REF ( elementOptions )? -> ^( TOKEN_REF ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) );
	public final ANTLRParser.terminal_return terminal() throws RecognitionException {
		ANTLRParser.terminal_return retval = new ANTLRParser.terminal_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token TOKEN_REF215=null;
		Token STRING_LITERAL217=null;
		ParserRuleReturnScope elementOptions216 =null;
		ParserRuleReturnScope elementOptions218 =null;

		GrammarAST TOKEN_REF215_tree=null;
		GrammarAST STRING_LITERAL217_tree=null;
		RewriteRuleTokenStream stream_STRING_LITERAL=new RewriteRuleTokenStream(adaptor,"token STRING_LITERAL");
		RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");
		RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:883:2: ( TOKEN_REF ( elementOptions )? -> ^( TOKEN_REF ( elementOptions )? ) | STRING_LITERAL ( elementOptions )? -> ^( STRING_LITERAL ( elementOptions )? ) )
			int alt76=2;
			int LA76_0 = input.LA(1);
			if ( (LA76_0==TOKEN_REF) ) {
				alt76=1;
			}
			else if ( (LA76_0==STRING_LITERAL) ) {
				alt76=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 76, 0, input);
				throw nvae;
			}

			switch (alt76) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:883:6: TOKEN_REF ( elementOptions )?
					{
					TOKEN_REF215=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal3958);  
					stream_TOKEN_REF.add(TOKEN_REF215);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:883:16: ( elementOptions )?
					int alt74=2;
					int LA74_0 = input.LA(1);
					if ( (LA74_0==LT) ) {
						alt74=1;
					}
					switch (alt74) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:883:16: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_terminal3960);
							elementOptions216=elementOptions();
							state._fsp--;

							stream_elementOptions.add(elementOptions216.getTree());
							}
							break;

					}

					// AST REWRITE
					// elements: elementOptions, TOKEN_REF
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 883:33: -> ^( TOKEN_REF ( elementOptions )? )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:883:36: ^( TOKEN_REF ( elementOptions )? )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_TOKEN_REF.nextToken()), root_1);
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:883:61: ( elementOptions )?
						if ( stream_elementOptions.hasNext() ) {
							adaptor.addChild(root_1, stream_elementOptions.nextTree());
						}
						stream_elementOptions.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:884:6: STRING_LITERAL ( elementOptions )?
					{
					STRING_LITERAL217=(Token)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal3981);  
					stream_STRING_LITERAL.add(STRING_LITERAL217);

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:884:21: ( elementOptions )?
					int alt75=2;
					int LA75_0 = input.LA(1);
					if ( (LA75_0==LT) ) {
						alt75=1;
					}
					switch (alt75) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:884:21: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_terminal3983);
							elementOptions218=elementOptions();
							state._fsp--;

							stream_elementOptions.add(elementOptions218.getTree());
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
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 884:37: -> ^( STRING_LITERAL ( elementOptions )? )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:884:40: ^( STRING_LITERAL ( elementOptions )? )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new TerminalAST(stream_STRING_LITERAL.nextToken()), root_1);
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:884:70: ( elementOptions )?
						if ( stream_elementOptions.hasNext() ) {
							adaptor.addChild(root_1, stream_elementOptions.nextTree());
						}
						stream_elementOptions.reset();

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);


			GrammarAST options = (GrammarAST)retval.tree.getFirstChildWithType(ANTLRParser.ELEMENT_OPTIONS);
			if ( options!=null ) {
				Grammar.setNodeOptions(retval.tree, options);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "terminal"


	public static class elementOptions_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "elementOptions"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:889:1: elementOptions : LT ( elementOption ( COMMA elementOption )* )? GT -> ^( ELEMENT_OPTIONS[$LT,\"ELEMENT_OPTIONS\"] ( elementOption )* ) ;
	public final ANTLRParser.elementOptions_return elementOptions() throws RecognitionException {
		ANTLRParser.elementOptions_return retval = new ANTLRParser.elementOptions_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token LT219=null;
		Token COMMA221=null;
		Token GT223=null;
		ParserRuleReturnScope elementOption220 =null;
		ParserRuleReturnScope elementOption222 =null;

		GrammarAST LT219_tree=null;
		GrammarAST COMMA221_tree=null;
		GrammarAST GT223_tree=null;
		RewriteRuleTokenStream stream_GT=new RewriteRuleTokenStream(adaptor,"token GT");
		RewriteRuleTokenStream stream_LT=new RewriteRuleTokenStream(adaptor,"token LT");
		RewriteRuleTokenStream stream_COMMA=new RewriteRuleTokenStream(adaptor,"token COMMA");
		RewriteRuleSubtreeStream stream_elementOption=new RewriteRuleSubtreeStream(adaptor,"rule elementOption");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:890:5: ( LT ( elementOption ( COMMA elementOption )* )? GT -> ^( ELEMENT_OPTIONS[$LT,\"ELEMENT_OPTIONS\"] ( elementOption )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:890:9: LT ( elementOption ( COMMA elementOption )* )? GT
			{
			LT219=(Token)match(input,LT,FOLLOW_LT_in_elementOptions4014);  
			stream_LT.add(LT219);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:890:12: ( elementOption ( COMMA elementOption )* )?
			int alt78=2;
			int LA78_0 = input.LA(1);
			if ( (LA78_0==RULE_REF||LA78_0==TOKEN_REF) ) {
				alt78=1;
			}
			switch (alt78) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:890:13: elementOption ( COMMA elementOption )*
					{
					pushFollow(FOLLOW_elementOption_in_elementOptions4017);
					elementOption220=elementOption();
					state._fsp--;

					stream_elementOption.add(elementOption220.getTree());
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:890:27: ( COMMA elementOption )*
					loop77:
					while (true) {
						int alt77=2;
						int LA77_0 = input.LA(1);
						if ( (LA77_0==COMMA) ) {
							alt77=1;
						}

						switch (alt77) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:890:28: COMMA elementOption
							{
							COMMA221=(Token)match(input,COMMA,FOLLOW_COMMA_in_elementOptions4020);  
							stream_COMMA.add(COMMA221);

							pushFollow(FOLLOW_elementOption_in_elementOptions4022);
							elementOption222=elementOption();
							state._fsp--;

							stream_elementOption.add(elementOption222.getTree());
							}
							break;

						default :
							break loop77;
						}
					}

					}
					break;

			}

			GT223=(Token)match(input,GT,FOLLOW_GT_in_elementOptions4028);  
			stream_GT.add(GT223);

			// AST REWRITE
			// elements: elementOption
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 891:13: -> ^( ELEMENT_OPTIONS[$LT,\"ELEMENT_OPTIONS\"] ( elementOption )* )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:891:16: ^( ELEMENT_OPTIONS[$LT,\"ELEMENT_OPTIONS\"] ( elementOption )* )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(ELEMENT_OPTIONS, LT219, "ELEMENT_OPTIONS"), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:891:57: ( elementOption )*
				while ( stream_elementOption.hasNext() ) {
					adaptor.addChild(root_1, stream_elementOption.nextTree());
				}
				stream_elementOption.reset();

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "elementOptions"


	public static class elementOption_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "elementOption"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:896:1: elementOption : ( qid | id ASSIGN ^ optionValue );
	public final ANTLRParser.elementOption_return elementOption() throws RecognitionException {
		ANTLRParser.elementOption_return retval = new ANTLRParser.elementOption_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token ASSIGN226=null;
		ParserRuleReturnScope qid224 =null;
		ParserRuleReturnScope id225 =null;
		ParserRuleReturnScope optionValue227 =null;

		GrammarAST ASSIGN226_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:897:5: ( qid | id ASSIGN ^ optionValue )
			int alt79=2;
			int LA79_0 = input.LA(1);
			if ( (LA79_0==RULE_REF) ) {
				int LA79_1 = input.LA(2);
				if ( (LA79_1==COMMA||LA79_1==DOT||LA79_1==GT) ) {
					alt79=1;
				}
				else if ( (LA79_1==ASSIGN) ) {
					alt79=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 79, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA79_0==TOKEN_REF) ) {
				int LA79_2 = input.LA(2);
				if ( (LA79_2==COMMA||LA79_2==DOT||LA79_2==GT) ) {
					alt79=1;
				}
				else if ( (LA79_2==ASSIGN) ) {
					alt79=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 79, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 79, 0, input);
				throw nvae;
			}

			switch (alt79) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:898:7: qid
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_qid_in_elementOption4076);
					qid224=qid();
					state._fsp--;

					adaptor.addChild(root_0, qid224.getTree());

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:899:7: id ASSIGN ^ optionValue
					{
					root_0 = (GrammarAST)adaptor.nil();


					pushFollow(FOLLOW_id_in_elementOption4084);
					id225=id();
					state._fsp--;

					adaptor.addChild(root_0, id225.getTree());

					ASSIGN226=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption4086); 
					ASSIGN226_tree = (GrammarAST)adaptor.create(ASSIGN226);
					root_0 = (GrammarAST)adaptor.becomeRoot(ASSIGN226_tree, root_0);

					pushFollow(FOLLOW_optionValue_in_elementOption4089);
					optionValue227=optionValue();
					state._fsp--;

					adaptor.addChild(root_0, optionValue227.getTree());

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "elementOption"


	public static class id_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "id"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:906:1: id : ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] );
	public final ANTLRParser.id_return id() throws RecognitionException {
		ANTLRParser.id_return retval = new ANTLRParser.id_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token RULE_REF228=null;
		Token TOKEN_REF229=null;

		GrammarAST RULE_REF228_tree=null;
		GrammarAST TOKEN_REF229_tree=null;
		RewriteRuleTokenStream stream_RULE_REF=new RewriteRuleTokenStream(adaptor,"token RULE_REF");
		RewriteRuleTokenStream stream_TOKEN_REF=new RewriteRuleTokenStream(adaptor,"token TOKEN_REF");

		 paraphrases.push("looking for an identifier"); 
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:909:5: ( RULE_REF -> ID[$RULE_REF] | TOKEN_REF -> ID[$TOKEN_REF] )
			int alt80=2;
			int LA80_0 = input.LA(1);
			if ( (LA80_0==RULE_REF) ) {
				alt80=1;
			}
			else if ( (LA80_0==TOKEN_REF) ) {
				alt80=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 80, 0, input);
				throw nvae;
			}

			switch (alt80) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:909:7: RULE_REF
					{
					RULE_REF228=(Token)match(input,RULE_REF,FOLLOW_RULE_REF_in_id4120);  
					stream_RULE_REF.add(RULE_REF228);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 909:17: -> ID[$RULE_REF]
					{
						adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, RULE_REF228));
					}


					retval.tree = root_0;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:910:7: TOKEN_REF
					{
					TOKEN_REF229=(Token)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_id4133);  
					stream_TOKEN_REF.add(TOKEN_REF229);

					// AST REWRITE
					// elements: 
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 910:17: -> ID[$TOKEN_REF]
					{
						adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, TOKEN_REF229));
					}


					retval.tree = root_0;

					}
					break;

			}
			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

			 paraphrases.pop(); 
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "id"


	public static class qid_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "qid"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:913:1: qid : id ( DOT id )* -> ID[$qid.start, $text] ;
	public final ANTLRParser.qid_return qid() throws RecognitionException {
		ANTLRParser.qid_return retval = new ANTLRParser.qid_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token DOT231=null;
		ParserRuleReturnScope id230 =null;
		ParserRuleReturnScope id232 =null;

		GrammarAST DOT231_tree=null;
		RewriteRuleTokenStream stream_DOT=new RewriteRuleTokenStream(adaptor,"token DOT");
		RewriteRuleSubtreeStream stream_id=new RewriteRuleSubtreeStream(adaptor,"rule id");

		 paraphrases.push("looking for a qualified identifier"); 
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:916:2: ( id ( DOT id )* -> ID[$qid.start, $text] )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:916:4: id ( DOT id )*
			{
			pushFollow(FOLLOW_id_in_qid4161);
			id230=id();
			state._fsp--;

			stream_id.add(id230.getTree());
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:916:7: ( DOT id )*
			loop81:
			while (true) {
				int alt81=2;
				int LA81_0 = input.LA(1);
				if ( (LA81_0==DOT) ) {
					alt81=1;
				}

				switch (alt81) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:916:8: DOT id
					{
					DOT231=(Token)match(input,DOT,FOLLOW_DOT_in_qid4164);  
					stream_DOT.add(DOT231);

					pushFollow(FOLLOW_id_in_qid4166);
					id232=id();
					state._fsp--;

					stream_id.add(id232.getTree());
					}
					break;

				default :
					break loop81;
				}
			}

			// AST REWRITE
			// elements: 
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 916:17: -> ID[$qid.start, $text]
			{
				adaptor.addChild(root_0, (GrammarAST)adaptor.create(ID, (retval.start), input.toString(retval.start,input.LT(-1))));
			}


			retval.tree = root_0;

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

			 paraphrases.pop(); 
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "qid"


	public static class alternativeEntry_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "alternativeEntry"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:919:1: alternativeEntry : alternative EOF ;
	public final ANTLRParser.alternativeEntry_return alternativeEntry() throws RecognitionException {
		ANTLRParser.alternativeEntry_return retval = new ANTLRParser.alternativeEntry_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token EOF234=null;
		ParserRuleReturnScope alternative233 =null;

		GrammarAST EOF234_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:919:18: ( alternative EOF )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:919:20: alternative EOF
			{
			root_0 = (GrammarAST)adaptor.nil();


			pushFollow(FOLLOW_alternative_in_alternativeEntry4183);
			alternative233=alternative();
			state._fsp--;

			adaptor.addChild(root_0, alternative233.getTree());

			EOF234=(Token)match(input,EOF,FOLLOW_EOF_in_alternativeEntry4185); 
			EOF234_tree = (GrammarAST)adaptor.create(EOF234);
			adaptor.addChild(root_0, EOF234_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "alternativeEntry"


	public static class elementEntry_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "elementEntry"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:920:1: elementEntry : element EOF ;
	public final ANTLRParser.elementEntry_return elementEntry() throws RecognitionException {
		ANTLRParser.elementEntry_return retval = new ANTLRParser.elementEntry_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token EOF236=null;
		ParserRuleReturnScope element235 =null;

		GrammarAST EOF236_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:920:14: ( element EOF )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:920:16: element EOF
			{
			root_0 = (GrammarAST)adaptor.nil();


			pushFollow(FOLLOW_element_in_elementEntry4194);
			element235=element();
			state._fsp--;

			adaptor.addChild(root_0, element235.getTree());

			EOF236=(Token)match(input,EOF,FOLLOW_EOF_in_elementEntry4196); 
			EOF236_tree = (GrammarAST)adaptor.create(EOF236);
			adaptor.addChild(root_0, EOF236_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "elementEntry"


	public static class ruleEntry_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ruleEntry"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:921:1: ruleEntry : rule EOF ;
	public final ANTLRParser.ruleEntry_return ruleEntry() throws RecognitionException {
		ANTLRParser.ruleEntry_return retval = new ANTLRParser.ruleEntry_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token EOF238=null;
		ParserRuleReturnScope rule237 =null;

		GrammarAST EOF238_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:921:11: ( rule EOF )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:921:13: rule EOF
			{
			root_0 = (GrammarAST)adaptor.nil();


			pushFollow(FOLLOW_rule_in_ruleEntry4204);
			rule237=rule();
			state._fsp--;

			adaptor.addChild(root_0, rule237.getTree());

			EOF238=(Token)match(input,EOF,FOLLOW_EOF_in_ruleEntry4206); 
			EOF238_tree = (GrammarAST)adaptor.create(EOF238);
			adaptor.addChild(root_0, EOF238_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "ruleEntry"


	public static class blockEntry_return extends ParserRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "blockEntry"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:922:1: blockEntry : block EOF ;
	public final ANTLRParser.blockEntry_return blockEntry() throws RecognitionException {
		ANTLRParser.blockEntry_return retval = new ANTLRParser.blockEntry_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		Token EOF240=null;
		ParserRuleReturnScope block239 =null;

		GrammarAST EOF240_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:922:12: ( block EOF )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRParser.g:922:14: block EOF
			{
			root_0 = (GrammarAST)adaptor.nil();


			pushFollow(FOLLOW_block_in_blockEntry4214);
			block239=block();
			state._fsp--;

			adaptor.addChild(root_0, block239.getTree());

			EOF240=(Token)match(input,EOF,FOLLOW_EOF_in_blockEntry4216); 
			EOF240_tree = (GrammarAST)adaptor.create(EOF240);
			adaptor.addChild(root_0, EOF240_tree);

			}

			retval.stop = input.LT(-1);

			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
			retval.tree = (GrammarAST)adaptor.errorNode(input, retval.start, input.LT(-1), re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "blockEntry"

	// Delegated rules



	public static final BitSet FOLLOW_grammarType_in_grammarSpec396 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_grammarSpec398 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_SEMI_in_grammarSpec400 = new BitSet(new long[]{0x0200040021002800L,0x0000000000000006L});
	public static final BitSet FOLLOW_sync_in_grammarSpec438 = new BitSet(new long[]{0x0200040021002800L,0x0000000000000006L});
	public static final BitSet FOLLOW_prequelConstruct_in_grammarSpec442 = new BitSet(new long[]{0x0200040021002800L,0x0000000000000006L});
	public static final BitSet FOLLOW_sync_in_grammarSpec444 = new BitSet(new long[]{0x0200040021002800L,0x0000000000000006L});
	public static final BitSet FOLLOW_rules_in_grammarSpec469 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_modeSpec_in_grammarSpec475 = new BitSet(new long[]{0x0000001000000000L});
	public static final BitSet FOLLOW_EOF_in_grammarSpec513 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEXER_in_grammarType683 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_GRAMMAR_in_grammarType687 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PARSER_in_grammarType710 = new BitSet(new long[]{0x0000000002000000L});
	public static final BitSet FOLLOW_GRAMMAR_in_grammarType714 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_GRAMMAR_in_grammarType735 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TREE_GRAMMAR_in_grammarType762 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct788 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct811 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct855 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_channelsSpec_in_prequelConstruct865 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_action_in_prequelConstruct902 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OPTIONS_in_optionsSpec917 = new BitSet(new long[]{0x0240000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_option_in_optionsSpec920 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_SEMI_in_optionsSpec922 = new BitSet(new long[]{0x0240000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_RBRACE_in_optionsSpec926 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_option955 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_ASSIGN_in_option957 = new BitSet(new long[]{0x4200000040000010L,0x0000000000000004L});
	public static final BitSet FOLLOW_optionValue_in_option960 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qid_in_optionValue1003 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_optionValue1011 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_optionValue1016 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_optionValue1027 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_IMPORT_in_delegateGrammars1043 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1045 = new BitSet(new long[]{0x0400000000010000L});
	public static final BitSet FOLLOW_COMMA_in_delegateGrammars1048 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars1050 = new BitSet(new long[]{0x0400000000010000L});
	public static final BitSet FOLLOW_SEMI_in_delegateGrammars1054 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_delegateGrammar1081 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar1083 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_delegateGrammar1086 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_delegateGrammar1096 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKENS_SPEC_in_tokensSpec1110 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_tokensSpec1112 = new BitSet(new long[]{0x0040000000010000L});
	public static final BitSet FOLLOW_COMMA_in_tokensSpec1115 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_tokensSpec1117 = new BitSet(new long[]{0x0040000000010000L});
	public static final BitSet FOLLOW_RBRACE_in_tokensSpec1121 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKENS_SPEC_in_tokensSpec1138 = new BitSet(new long[]{0x0040000000000000L});
	public static final BitSet FOLLOW_RBRACE_in_tokensSpec1140 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKENS_SPEC_in_tokensSpec1150 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_v3tokenSpec_in_tokensSpec1153 = new BitSet(new long[]{0x0240000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_RBRACE_in_tokensSpec1156 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_v3tokenSpec1176 = new BitSet(new long[]{0x0400000000000400L});
	public static final BitSet FOLLOW_ASSIGN_in_v3tokenSpec1182 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_v3tokenSpec1186 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_SEMI_in_v3tokenSpec1247 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CHANNELS_in_channelsSpec1258 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_channelsSpec1261 = new BitSet(new long[]{0x0040000000010000L});
	public static final BitSet FOLLOW_COMMA_in_channelsSpec1264 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_channelsSpec1267 = new BitSet(new long[]{0x0040000000010000L});
	public static final BitSet FOLLOW_RBRACE_in_channelsSpec1271 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_action1288 = new BitSet(new long[]{0x0200100080000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_actionScopeName_in_action1291 = new BitSet(new long[]{0x0000000000008000L});
	public static final BitSet FOLLOW_COLONCOLON_in_action1293 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_action1297 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_action1299 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_actionScopeName1328 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEXER_in_actionScopeName1333 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PARSER_in_actionScopeName1348 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MODE_in_modeSpec1367 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_modeSpec1369 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_SEMI_in_modeSpec1371 = new BitSet(new long[]{0x0000000001000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_sync_in_modeSpec1373 = new BitSet(new long[]{0x0000000001000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerRule_in_modeSpec1376 = new BitSet(new long[]{0x0000000001000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_sync_in_modeSpec1378 = new BitSet(new long[]{0x0000000001000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_sync_in_rules1409 = new BitSet(new long[]{0x0200000001000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_rule_in_rules1412 = new BitSet(new long[]{0x0200000001000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_sync_in_rules1414 = new BitSet(new long[]{0x0200000001000002L,0x0000000000000004L});
	public static final BitSet FOLLOW_parserRule_in_rule1476 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerRule_in_rule1481 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RULE_REF_in_parserRule1530 = new BitSet(new long[]{0x0080040200000900L,0x0000000000000001L});
	public static final BitSet FOLLOW_ARG_ACTION_in_parserRule1560 = new BitSet(new long[]{0x0080040200000800L,0x0000000000000001L});
	public static final BitSet FOLLOW_ruleReturns_in_parserRule1567 = new BitSet(new long[]{0x0000040200000800L,0x0000000000000001L});
	public static final BitSet FOLLOW_throwsSpec_in_parserRule1574 = new BitSet(new long[]{0x0000040200000800L});
	public static final BitSet FOLLOW_localsSpec_in_parserRule1581 = new BitSet(new long[]{0x0000040000000800L});
	public static final BitSet FOLLOW_rulePrequels_in_parserRule1619 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_COLON_in_parserRule1628 = new BitSet(new long[]{0x4A00808C00100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_ruleBlock_in_parserRule1651 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_SEMI_in_parserRule1660 = new BitSet(new long[]{0x0000000000801000L});
	public static final BitSet FOLLOW_exceptionGroup_in_parserRule1669 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup1752 = new BitSet(new long[]{0x0000000000801002L});
	public static final BitSet FOLLOW_finallyClause_in_exceptionGroup1755 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CATCH_in_exceptionHandler1772 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler1774 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_exceptionHandler1776 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FINALLY_in_finallyClause1803 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_finallyClause1805 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_sync_in_rulePrequels1837 = new BitSet(new long[]{0x0000040000000802L});
	public static final BitSet FOLLOW_rulePrequel_in_rulePrequels1840 = new BitSet(new long[]{0x0000040000000800L});
	public static final BitSet FOLLOW_sync_in_rulePrequels1842 = new BitSet(new long[]{0x0000040000000802L});
	public static final BitSet FOLLOW_optionsSpec_in_rulePrequel1866 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ruleAction_in_rulePrequel1874 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RETURNS_in_ruleReturns1894 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns1897 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_THROWS_in_throwsSpec1925 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_qid_in_throwsSpec1927 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_COMMA_in_throwsSpec1930 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_qid_in_throwsSpec1932 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_LOCALS_in_localsSpec1957 = new BitSet(new long[]{0x0000000000000100L});
	public static final BitSet FOLLOW_ARG_ACTION_in_localsSpec1960 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_ruleAction1983 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_ruleAction1985 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_ruleAction1987 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ruleAltList_in_ruleBlock2025 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_labeledAlt_in_ruleAltList2061 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_OR_in_ruleAltList2064 = new BitSet(new long[]{0x4A00808C00100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_labeledAlt_in_ruleAltList2066 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_alternative_in_labeledAlt2084 = new BitSet(new long[]{0x0000800000000002L});
	public static final BitSet FOLLOW_POUND_in_labeledAlt2090 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_labeledAlt2093 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_FRAGMENT_in_lexerRule2125 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_TOKEN_REF_in_lexerRule2131 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_COLON_in_lexerRule2133 = new BitSet(new long[]{0x4A20008500100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerRuleBlock_in_lexerRule2135 = new BitSet(new long[]{0x0400000000000000L});
	public static final BitSet FOLLOW_SEMI_in_lexerRule2137 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerAltList_in_lexerRuleBlock2201 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerAlt_in_lexerAltList2237 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_OR_in_lexerAltList2240 = new BitSet(new long[]{0x4A20008500100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerAlt_in_lexerAltList2242 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_lexerElements_in_lexerAlt2260 = new BitSet(new long[]{0x0020000000000002L});
	public static final BitSet FOLLOW_lexerCommands_in_lexerAlt2266 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerElement_in_lexerElements2309 = new BitSet(new long[]{0x4A00008500100012L,0x0000000000000004L});
	public static final BitSet FOLLOW_labeledLexerElement_in_lexerElement2365 = new BitSet(new long[]{0x2008200000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_lexerElement2371 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerAtom_in_lexerElement2417 = new BitSet(new long[]{0x2008200000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_lexerElement2423 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerBlock_in_lexerElement2469 = new BitSet(new long[]{0x2008200000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_lexerElement2475 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_actionElement_in_lexerElement2503 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_labeledLexerElement2533 = new BitSet(new long[]{0x0000400000000400L});
	public static final BitSet FOLLOW_ASSIGN_in_labeledLexerElement2538 = new BitSet(new long[]{0x4200008500100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledLexerElement2542 = new BitSet(new long[]{0x4200008500100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerAtom_in_labeledLexerElement2549 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerBlock_in_labeledLexerElement2566 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_lexerBlock2599 = new BitSet(new long[]{0x4A20048500100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_optionsSpec_in_lexerBlock2611 = new BitSet(new long[]{0x0000000000004000L});
	public static final BitSet FOLLOW_COLON_in_lexerBlock2613 = new BitSet(new long[]{0x4A20008500100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerAltList_in_lexerBlock2626 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_RPAREN_in_lexerBlock2636 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RARROW_in_lexerCommands2673 = new BitSet(new long[]{0x0200001000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerCommand_in_lexerCommands2675 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_COMMA_in_lexerCommands2678 = new BitSet(new long[]{0x0200001000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerCommand_in_lexerCommands2680 = new BitSet(new long[]{0x0000000000010002L});
	public static final BitSet FOLLOW_lexerCommandName_in_lexerCommand2698 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_LPAREN_in_lexerCommand2700 = new BitSet(new long[]{0x0200000040000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_lexerCommandExpr_in_lexerCommand2702 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_RPAREN_in_lexerCommand2704 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerCommandName_in_lexerCommand2719 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_lexerCommandExpr2730 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_INT_in_lexerCommandExpr2735 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_lexerCommandName2759 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_MODE_in_lexerCommandName2777 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_alternative_in_altList2805 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_OR_in_altList2808 = new BitSet(new long[]{0x4A00088C00100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_alternative_in_altList2810 = new BitSet(new long[]{0x0000080000000002L});
	public static final BitSet FOLLOW_elementOptions_in_alternative2844 = new BitSet(new long[]{0x4A00008400100012L,0x0000000000000004L});
	public static final BitSet FOLLOW_element_in_alternative2853 = new BitSet(new long[]{0x4A00008400100012L,0x0000000000000004L});
	public static final BitSet FOLLOW_labeledElement_in_element2968 = new BitSet(new long[]{0x2008200000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_element2974 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_element3020 = new BitSet(new long[]{0x2008200000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_element3026 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ebnf_in_element3072 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_actionElement_in_element3077 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_actionElement3103 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_actionElement3113 = new BitSet(new long[]{0x0000000800000000L});
	public static final BitSet FOLLOW_elementOptions_in_actionElement3115 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMPRED_in_actionElement3133 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMPRED_in_actionElement3143 = new BitSet(new long[]{0x0000000800000000L});
	public static final BitSet FOLLOW_elementOptions_in_actionElement3145 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_labeledElement3167 = new BitSet(new long[]{0x0000400000000400L});
	public static final BitSet FOLLOW_ASSIGN_in_labeledElement3172 = new BitSet(new long[]{0x4200008400100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement3176 = new BitSet(new long[]{0x4200008400100000L,0x0000000000000004L});
	public static final BitSet FOLLOW_atom_in_labeledElement3183 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_labeledElement3205 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_ebnf3241 = new BitSet(new long[]{0x2008200000000002L});
	public static final BitSet FOLLOW_blockSuffix_in_ebnf3265 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffix3315 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3330 = new BitSet(new long[]{0x0008000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3334 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STAR_in_ebnfSuffix3350 = new BitSet(new long[]{0x0008000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3354 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_PLUS_in_ebnfSuffix3372 = new BitSet(new long[]{0x0008000000000002L});
	public static final BitSet FOLLOW_QUESTION_in_ebnfSuffix3376 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_range_in_lexerAtom3397 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_terminal_in_lexerAtom3402 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RULE_REF_in_lexerAtom3412 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_notSet_in_lexerAtom3423 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_wildcard_in_lexerAtom3431 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEXER_CHAR_SET_in_lexerAtom3439 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_range_in_atom3484 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_terminal_in_atom3491 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ruleref_in_atom3501 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_notSet_in_atom3509 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_wildcard_in_atom3517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_wildcard3565 = new BitSet(new long[]{0x0000000800000002L});
	public static final BitSet FOLLOW_elementOptions_in_wildcard3567 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_notSet3605 = new BitSet(new long[]{0x4000000100000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_notSet3607 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_notSet3635 = new BitSet(new long[]{0x0000000400000000L});
	public static final BitSet FOLLOW_blockSet_in_notSet3637 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_blockSet3672 = new BitSet(new long[]{0x4000000100000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet3674 = new BitSet(new long[]{0x0100080000000000L});
	public static final BitSet FOLLOW_OR_in_blockSet3677 = new BitSet(new long[]{0x4000000100000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet3679 = new BitSet(new long[]{0x0100080000000000L});
	public static final BitSet FOLLOW_RPAREN_in_blockSet3683 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_setElement3713 = new BitSet(new long[]{0x0000000800000002L});
	public static final BitSet FOLLOW_elementOptions_in_setElement3719 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement3725 = new BitSet(new long[]{0x0000000800000002L});
	public static final BitSet FOLLOW_elementOptions_in_setElement3731 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_range_in_setElement3737 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEXER_CHAR_SET_in_setElement3747 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LPAREN_in_block3771 = new BitSet(new long[]{0x4A000C8C00104810L,0x0000000000000004L});
	public static final BitSet FOLLOW_optionsSpec_in_block3783 = new BitSet(new long[]{0x0000000000004800L});
	public static final BitSet FOLLOW_ruleAction_in_block3788 = new BitSet(new long[]{0x0000000000004800L});
	public static final BitSet FOLLOW_COLON_in_block3791 = new BitSet(new long[]{0x4A00088C00100010L,0x0000000000000004L});
	public static final BitSet FOLLOW_altList_in_block3804 = new BitSet(new long[]{0x0100000000000000L});
	public static final BitSet FOLLOW_RPAREN_in_block3808 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RULE_REF_in_ruleref3862 = new BitSet(new long[]{0x0000000800000102L});
	public static final BitSet FOLLOW_ARG_ACTION_in_ruleref3864 = new BitSet(new long[]{0x0000000800000002L});
	public static final BitSet FOLLOW_elementOptions_in_ruleref3867 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range3923 = new BitSet(new long[]{0x0010000000000000L});
	public static final BitSet FOLLOW_RANGE_in_range3928 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range3934 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal3958 = new BitSet(new long[]{0x0000000800000002L});
	public static final BitSet FOLLOW_elementOptions_in_terminal3960 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_terminal3981 = new BitSet(new long[]{0x0000000800000002L});
	public static final BitSet FOLLOW_elementOptions_in_terminal3983 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LT_in_elementOptions4014 = new BitSet(new long[]{0x0200000004000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_elementOption_in_elementOptions4017 = new BitSet(new long[]{0x0000000004010000L});
	public static final BitSet FOLLOW_COMMA_in_elementOptions4020 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_elementOption_in_elementOptions4022 = new BitSet(new long[]{0x0000000004010000L});
	public static final BitSet FOLLOW_GT_in_elementOptions4028 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_qid_in_elementOption4076 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_elementOption4084 = new BitSet(new long[]{0x0000000000000400L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption4086 = new BitSet(new long[]{0x4200000040000010L,0x0000000000000004L});
	public static final BitSet FOLLOW_optionValue_in_elementOption4089 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RULE_REF_in_id4120 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_id4133 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_id_in_qid4161 = new BitSet(new long[]{0x0000000000100002L});
	public static final BitSet FOLLOW_DOT_in_qid4164 = new BitSet(new long[]{0x0200000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_id_in_qid4166 = new BitSet(new long[]{0x0000000000100002L});
	public static final BitSet FOLLOW_alternative_in_alternativeEntry4183 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_alternativeEntry4185 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_element_in_elementEntry4194 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_elementEntry4196 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_rule_in_ruleEntry4204 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_ruleEntry4206 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_block_in_blockEntry4214 = new BitSet(new long[]{0x0000000000000000L});
	public static final BitSet FOLLOW_EOF_in_blockEntry4216 = new BitSet(new long[]{0x0000000000000002L});
}
