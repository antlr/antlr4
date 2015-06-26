// $ANTLR 3.5.2 /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g 2015-06-23 21:59:57

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
package org.antlr.v4.parse;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import org.antlr.v4.automata.ATNFactory;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class ATNBuilder extends TreeParser {
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
	public TreeParser[] getDelegates() {
		return new TreeParser[] {};
	}

	// delegators


	public ATNBuilder(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public ATNBuilder(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return ATNBuilder.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g"; }


	    ATNFactory factory;
	    public ATNBuilder(TreeNodeStream input, ATNFactory factory) {
	    	this(input);
	    	this.factory = factory;
	    }



	// $ANTLR start "dummy"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:80:1: dummy : block[null] ;
	public final void dummy() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:80:7: ( block[null] )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:80:9: block[null]
			{
			pushFollow(FOLLOW_block_in_dummy63);
			block(null);
			state._fsp--;

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "dummy"



	// $ANTLR start "ruleBlock"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:82:1: ruleBlock[GrammarAST ebnfRoot] returns [ATNFactory.Handle p] : ^( BLOCK ( ^( OPTIONS ( . )* ) )? (a= alternative )+ ) ;
	public final ATNFactory.Handle ruleBlock(GrammarAST ebnfRoot) throws RecognitionException {
		ATNFactory.Handle p = null;


		GrammarAST BLOCK1=null;
		ATNFactory.Handle a =null;


		    List<ATNFactory.Handle> alts = new ArrayList<ATNFactory.Handle>();
		    int alt = 1;
		    factory.setCurrentOuterAlt(alt);

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:88:5: ( ^( BLOCK ( ^( OPTIONS ( . )* ) )? (a= alternative )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:88:7: ^( BLOCK ( ^( OPTIONS ( . )* ) )? (a= alternative )+ )
			{
			BLOCK1=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_ruleBlock89); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:89:13: ( ^( OPTIONS ( . )* ) )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==OPTIONS) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:89:14: ^( OPTIONS ( . )* )
					{
					match(input,OPTIONS,FOLLOW_OPTIONS_in_ruleBlock105); 
					if ( input.LA(1)==Token.DOWN ) {
						match(input, Token.DOWN, null); 
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:89:24: ( . )*
						loop1:
						while (true) {
							int alt1=2;
							int LA1_0 = input.LA(1);
							if ( ((LA1_0 >= ACTION && LA1_0 <= WILDCARD)) ) {
								alt1=1;
							}
							else if ( (LA1_0==UP) ) {
								alt1=2;
							}

							switch (alt1) {
							case 1 :
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:89:24: .
								{
								matchAny(input); 
								}
								break;

							default :
								break loop1;
							}
						}

						match(input, Token.UP, null); 
					}

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:90:13: (a= alternative )+
			int cnt3=0;
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==ALT||LA3_0==LEXER_ALT_ACTION) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:90:17: a= alternative
					{
					pushFollow(FOLLOW_alternative_in_ruleBlock131);
					a=alternative();
					state._fsp--;

					alts.add(a); factory.setCurrentOuterAlt(++alt);
					}
					break;

				default :
					if ( cnt3 >= 1 ) break loop3;
					EarlyExitException eee = new EarlyExitException(3, input);
					throw eee;
				}
				cnt3++;
			}

			match(input, Token.UP, null); 

			p = factory.block((BlockAST)BLOCK1, ebnfRoot, alts);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "ruleBlock"



	// $ANTLR start "block"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:97:1: block[GrammarAST ebnfRoot] returns [ATNFactory.Handle p] : ^( BLOCK ( ^( OPTIONS ( . )* ) )? (a= alternative )+ ) ;
	public final ATNFactory.Handle block(GrammarAST ebnfRoot) throws RecognitionException {
		ATNFactory.Handle p = null;


		GrammarAST BLOCK2=null;
		ATNFactory.Handle a =null;

		List<ATNFactory.Handle> alts = new ArrayList<ATNFactory.Handle>();
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:5: ( ^( BLOCK ( ^( OPTIONS ( . )* ) )? (a= alternative )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:7: ^( BLOCK ( ^( OPTIONS ( . )* ) )? (a= alternative )+ )
			{
			BLOCK2=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_block209); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:15: ( ^( OPTIONS ( . )* ) )?
			int alt5=2;
			int LA5_0 = input.LA(1);
			if ( (LA5_0==OPTIONS) ) {
				alt5=1;
			}
			switch (alt5) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:16: ^( OPTIONS ( . )* )
					{
					match(input,OPTIONS,FOLLOW_OPTIONS_in_block213); 
					if ( input.LA(1)==Token.DOWN ) {
						match(input, Token.DOWN, null); 
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:26: ( . )*
						loop4:
						while (true) {
							int alt4=2;
							int LA4_0 = input.LA(1);
							if ( ((LA4_0 >= ACTION && LA4_0 <= WILDCARD)) ) {
								alt4=1;
							}
							else if ( (LA4_0==UP) ) {
								alt4=2;
							}

							switch (alt4) {
							case 1 :
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:26: .
								{
								matchAny(input); 
								}
								break;

							default :
								break loop4;
							}
						}

						match(input, Token.UP, null); 
					}

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:32: (a= alternative )+
			int cnt6=0;
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==ALT||LA6_0==LEXER_ALT_ACTION) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:99:33: a= alternative
					{
					pushFollow(FOLLOW_alternative_in_block224);
					a=alternative();
					state._fsp--;

					alts.add(a);
					}
					break;

				default :
					if ( cnt6 >= 1 ) break loop6;
					EarlyExitException eee = new EarlyExitException(6, input);
					throw eee;
				}
				cnt6++;
			}

			match(input, Token.UP, null); 

			p = factory.block((BlockAST)BLOCK2, ebnfRoot, alts);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "block"



	// $ANTLR start "alternative"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:103:1: alternative returns [ATNFactory.Handle p] : ( ^( LEXER_ALT_ACTION a= alternative lexerCommands ) | ^( ALT ( elementOptions )? EPSILON ) | ^( ALT ( elementOptions )? (e= element )+ ) );
	public final ATNFactory.Handle alternative() throws RecognitionException {
		ATNFactory.Handle p = null;


		GrammarAST EPSILON4=null;
		ATNFactory.Handle a =null;
		TreeRuleReturnScope e =null;
		ATNFactory.Handle lexerCommands3 =null;

		List<ATNFactory.Handle> els = new ArrayList<ATNFactory.Handle>();
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:105:5: ( ^( LEXER_ALT_ACTION a= alternative lexerCommands ) | ^( ALT ( elementOptions )? EPSILON ) | ^( ALT ( elementOptions )? (e= element )+ ) )
			int alt10=3;
			alt10 = dfa10.predict(input);
			switch (alt10) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:105:7: ^( LEXER_ALT_ACTION a= alternative lexerCommands )
					{
					match(input,LEXER_ALT_ACTION,FOLLOW_LEXER_ALT_ACTION_in_alternative263); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_alternative_in_alternative267);
					a=alternative();
					state._fsp--;

					pushFollow(FOLLOW_lexerCommands_in_alternative269);
					lexerCommands3=lexerCommands();
					state._fsp--;

					match(input, Token.UP, null); 

					p = factory.lexerAltCommands(a,lexerCommands3);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:107:7: ^( ALT ( elementOptions )? EPSILON )
					{
					match(input,ALT,FOLLOW_ALT_in_alternative289); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:107:13: ( elementOptions )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==ELEMENT_OPTIONS) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:107:13: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_alternative291);
							elementOptions();
							state._fsp--;

							}
							break;

					}

					EPSILON4=(GrammarAST)match(input,EPSILON,FOLLOW_EPSILON_in_alternative294); 
					match(input, Token.UP, null); 

					p = factory.epsilon(EPSILON4);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:108:9: ^( ALT ( elementOptions )? (e= element )+ )
					{
					match(input,ALT,FOLLOW_ALT_in_alternative314); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:108:15: ( elementOptions )?
					int alt8=2;
					int LA8_0 = input.LA(1);
					if ( (LA8_0==ELEMENT_OPTIONS) ) {
						alt8=1;
					}
					switch (alt8) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:108:15: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_alternative316);
							elementOptions();
							state._fsp--;

							}
							break;

					}

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:108:31: (e= element )+
					int cnt9=0;
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==ACTION||LA9_0==ASSIGN||LA9_0==DOT||LA9_0==LEXER_CHAR_SET||LA9_0==NOT||LA9_0==PLUS_ASSIGN||LA9_0==RANGE||LA9_0==RULE_REF||LA9_0==SEMPRED||LA9_0==STRING_LITERAL||LA9_0==TOKEN_REF||LA9_0==BLOCK||LA9_0==CLOSURE||(LA9_0 >= OPTIONAL && LA9_0 <= POSITIVE_CLOSURE)||LA9_0==SET||LA9_0==WILDCARD) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:108:32: e= element
							{
							pushFollow(FOLLOW_element_in_alternative322);
							e=element();
							state._fsp--;

							els.add((e!=null?((ATNBuilder.element_return)e).p:null));
							}
							break;

						default :
							if ( cnt9 >= 1 ) break loop9;
							EarlyExitException eee = new EarlyExitException(9, input);
							throw eee;
						}
						cnt9++;
					}

					match(input, Token.UP, null); 

					p = factory.alt(els);
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "alternative"



	// $ANTLR start "lexerCommands"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:111:1: lexerCommands returns [ATNFactory.Handle p] : (c= lexerCommand )+ ;
	public final ATNFactory.Handle lexerCommands() throws RecognitionException {
		ATNFactory.Handle p = null;


		ATNFactory.Handle c =null;

		List<ATNFactory.Handle> cmds = new ArrayList<ATNFactory.Handle>();
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:113:5: ( (c= lexerCommand )+ )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:113:9: (c= lexerCommand )+
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:113:9: (c= lexerCommand )+
			int cnt11=0;
			loop11:
			while (true) {
				int alt11=2;
				int LA11_0 = input.LA(1);
				if ( (LA11_0==ID||LA11_0==LEXER_ACTION_CALL) ) {
					alt11=1;
				}

				switch (alt11) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:113:10: c= lexerCommand
					{
					pushFollow(FOLLOW_lexerCommand_in_lexerCommands360);
					c=lexerCommand();
					state._fsp--;

					if (c != null) cmds.add(c);
					}
					break;

				default :
					if ( cnt11 >= 1 ) break loop11;
					EarlyExitException eee = new EarlyExitException(11, input);
					throw eee;
				}
				cnt11++;
			}


			        p = factory.alt(cmds);
			        
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "lexerCommands"



	// $ANTLR start "lexerCommand"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:119:1: lexerCommand returns [ATNFactory.Handle cmd] : ( ^( LEXER_ACTION_CALL ID lexerCommandExpr ) | ID );
	public final ATNFactory.Handle lexerCommand() throws RecognitionException {
		ATNFactory.Handle cmd = null;


		GrammarAST ID5=null;
		GrammarAST ID7=null;
		TreeRuleReturnScope lexerCommandExpr6 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:120:2: ( ^( LEXER_ACTION_CALL ID lexerCommandExpr ) | ID )
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0==LEXER_ACTION_CALL) ) {
				alt12=1;
			}
			else if ( (LA12_0==ID) ) {
				alt12=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 12, 0, input);
				throw nvae;
			}

			switch (alt12) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:120:4: ^( LEXER_ACTION_CALL ID lexerCommandExpr )
					{
					match(input,LEXER_ACTION_CALL,FOLLOW_LEXER_ACTION_CALL_in_lexerCommand393); 
					match(input, Token.DOWN, null); 
					ID5=(GrammarAST)match(input,ID,FOLLOW_ID_in_lexerCommand395); 
					pushFollow(FOLLOW_lexerCommandExpr_in_lexerCommand397);
					lexerCommandExpr6=lexerCommandExpr();
					state._fsp--;

					match(input, Token.UP, null); 

					cmd = factory.lexerCallCommand(ID5, (lexerCommandExpr6!=null?((GrammarAST)lexerCommandExpr6.start):null));
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:122:4: ID
					{
					ID7=(GrammarAST)match(input,ID,FOLLOW_ID_in_lexerCommand413); 
					cmd = factory.lexerCommand(ID7);
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return cmd;
	}
	// $ANTLR end "lexerCommand"


	public static class lexerCommandExpr_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerCommandExpr"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:126:1: lexerCommandExpr : ( ID | INT );
	public final ATNBuilder.lexerCommandExpr_return lexerCommandExpr() throws RecognitionException {
		ATNBuilder.lexerCommandExpr_return retval = new ATNBuilder.lexerCommandExpr_return();
		retval.start = input.LT(1);

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:127:2: ( ID | INT )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:
			{
			if ( input.LA(1)==ID||input.LA(1)==INT ) {
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
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "lexerCommandExpr"


	public static class element_return extends TreeRuleReturnScope {
		public ATNFactory.Handle p;
	};


	// $ANTLR start "element"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:131:1: element returns [ATNFactory.Handle p] : ( labeledElement | atom | subrule | ACTION | SEMPRED | ^( ACTION . ) | ^( SEMPRED . ) | ^( NOT b= blockSet[true] ) | LEXER_CHAR_SET );
	public final ATNBuilder.element_return element() throws RecognitionException {
		ATNBuilder.element_return retval = new ATNBuilder.element_return();
		retval.start = input.LT(1);

		GrammarAST ACTION11=null;
		GrammarAST SEMPRED12=null;
		GrammarAST ACTION13=null;
		GrammarAST SEMPRED14=null;
		TreeRuleReturnScope b =null;
		ATNFactory.Handle labeledElement8 =null;
		TreeRuleReturnScope atom9 =null;
		TreeRuleReturnScope subrule10 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:132:2: ( labeledElement | atom | subrule | ACTION | SEMPRED | ^( ACTION . ) | ^( SEMPRED . ) | ^( NOT b= blockSet[true] ) | LEXER_CHAR_SET )
			int alt13=9;
			switch ( input.LA(1) ) {
			case ASSIGN:
			case PLUS_ASSIGN:
				{
				alt13=1;
				}
				break;
			case DOT:
			case RANGE:
			case RULE_REF:
			case STRING_LITERAL:
			case TOKEN_REF:
			case SET:
			case WILDCARD:
				{
				alt13=2;
				}
				break;
			case BLOCK:
			case CLOSURE:
			case OPTIONAL:
			case POSITIVE_CLOSURE:
				{
				alt13=3;
				}
				break;
			case ACTION:
				{
				int LA13_4 = input.LA(2);
				if ( (LA13_4==DOWN) ) {
					alt13=6;
				}
				else if ( ((LA13_4 >= UP && LA13_4 <= ACTION)||LA13_4==ASSIGN||LA13_4==DOT||LA13_4==LEXER_CHAR_SET||LA13_4==NOT||LA13_4==PLUS_ASSIGN||LA13_4==RANGE||LA13_4==RULE_REF||LA13_4==SEMPRED||LA13_4==STRING_LITERAL||LA13_4==TOKEN_REF||LA13_4==BLOCK||LA13_4==CLOSURE||(LA13_4 >= OPTIONAL && LA13_4 <= POSITIVE_CLOSURE)||LA13_4==SET||LA13_4==WILDCARD) ) {
					alt13=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SEMPRED:
				{
				int LA13_5 = input.LA(2);
				if ( (LA13_5==DOWN) ) {
					alt13=7;
				}
				else if ( ((LA13_5 >= UP && LA13_5 <= ACTION)||LA13_5==ASSIGN||LA13_5==DOT||LA13_5==LEXER_CHAR_SET||LA13_5==NOT||LA13_5==PLUS_ASSIGN||LA13_5==RANGE||LA13_5==RULE_REF||LA13_5==SEMPRED||LA13_5==STRING_LITERAL||LA13_5==TOKEN_REF||LA13_5==BLOCK||LA13_5==CLOSURE||(LA13_5 >= OPTIONAL && LA13_5 <= POSITIVE_CLOSURE)||LA13_5==SET||LA13_5==WILDCARD) ) {
					alt13=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NOT:
				{
				alt13=8;
				}
				break;
			case LEXER_CHAR_SET:
				{
				alt13=9;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}
			switch (alt13) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:132:4: labeledElement
					{
					pushFollow(FOLLOW_labeledElement_in_element454);
					labeledElement8=labeledElement();
					state._fsp--;

					retval.p = labeledElement8;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:133:4: atom
					{
					pushFollow(FOLLOW_atom_in_element464);
					atom9=atom();
					state._fsp--;

					retval.p = (atom9!=null?((ATNBuilder.atom_return)atom9).p:null);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:134:4: subrule
					{
					pushFollow(FOLLOW_subrule_in_element476);
					subrule10=subrule();
					state._fsp--;

					retval.p = (subrule10!=null?((ATNBuilder.subrule_return)subrule10).p:null);
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:135:6: ACTION
					{
					ACTION11=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element490); 
					retval.p = factory.action((ActionAST)ACTION11);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:136:6: SEMPRED
					{
					SEMPRED12=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element504); 
					retval.p = factory.sempred((PredAST)SEMPRED12);
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:137:6: ^( ACTION . )
					{
					ACTION13=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element519); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					retval.p = factory.action((ActionAST)ACTION13);
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:138:6: ^( SEMPRED . )
					{
					SEMPRED14=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element536); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					retval.p = factory.sempred((PredAST)SEMPRED14);
					}
					break;
				case 8 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:139:7: ^( NOT b= blockSet[true] )
					{
					match(input,NOT,FOLLOW_NOT_in_element553); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_blockSet_in_element557);
					b=blockSet(true);
					state._fsp--;

					match(input, Token.UP, null); 

					retval.p = (b!=null?((ATNBuilder.blockSet_return)b).p:null);
					}
					break;
				case 9 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:140:7: LEXER_CHAR_SET
					{
					match(input,LEXER_CHAR_SET,FOLLOW_LEXER_CHAR_SET_in_element570); 
					retval.p = factory.charSetLiteral(((GrammarAST)retval.start));
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "element"



	// $ANTLR start "astOperand"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:143:1: astOperand returns [ATNFactory.Handle p] : ( atom | ^( NOT blockSet[true] ) );
	public final ATNFactory.Handle astOperand() throws RecognitionException {
		ATNFactory.Handle p = null;


		TreeRuleReturnScope atom15 =null;
		TreeRuleReturnScope blockSet16 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:144:2: ( atom | ^( NOT blockSet[true] ) )
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0==DOT||LA14_0==RANGE||LA14_0==RULE_REF||LA14_0==STRING_LITERAL||LA14_0==TOKEN_REF||LA14_0==SET||LA14_0==WILDCARD) ) {
				alt14=1;
			}
			else if ( (LA14_0==NOT) ) {
				alt14=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 14, 0, input);
				throw nvae;
			}

			switch (alt14) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:144:4: atom
					{
					pushFollow(FOLLOW_atom_in_astOperand591);
					atom15=atom();
					state._fsp--;

					p = (atom15!=null?((ATNBuilder.atom_return)atom15).p:null);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:145:4: ^( NOT blockSet[true] )
					{
					match(input,NOT,FOLLOW_NOT_in_astOperand604); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_blockSet_in_astOperand606);
					blockSet16=blockSet(true);
					state._fsp--;

					match(input, Token.UP, null); 

					p = (blockSet16!=null?((ATNBuilder.blockSet_return)blockSet16).p:null);
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "astOperand"



	// $ANTLR start "labeledElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:148:1: labeledElement returns [ATNFactory.Handle p] : ( ^( ASSIGN ID element ) | ^( PLUS_ASSIGN ID element ) );
	public final ATNFactory.Handle labeledElement() throws RecognitionException {
		ATNFactory.Handle p = null;


		TreeRuleReturnScope element17 =null;
		TreeRuleReturnScope element18 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:149:2: ( ^( ASSIGN ID element ) | ^( PLUS_ASSIGN ID element ) )
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==ASSIGN) ) {
				alt15=1;
			}
			else if ( (LA15_0==PLUS_ASSIGN) ) {
				alt15=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 15, 0, input);
				throw nvae;
			}

			switch (alt15) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:149:4: ^( ASSIGN ID element )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement627); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_labeledElement629); 
					pushFollow(FOLLOW_element_in_labeledElement631);
					element17=element();
					state._fsp--;

					match(input, Token.UP, null); 

					p = factory.label((element17!=null?((ATNBuilder.element_return)element17).p:null));
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:150:4: ^( PLUS_ASSIGN ID element )
					{
					match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement644); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_labeledElement646); 
					pushFollow(FOLLOW_element_in_labeledElement648);
					element18=element();
					state._fsp--;

					match(input, Token.UP, null); 

					p = factory.listLabel((element18!=null?((ATNBuilder.element_return)element18).p:null));
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "labeledElement"


	public static class subrule_return extends TreeRuleReturnScope {
		public ATNFactory.Handle p;
	};


	// $ANTLR start "subrule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:153:1: subrule returns [ATNFactory.Handle p] : ( ^( OPTIONAL block[$start] ) | ^( CLOSURE block[$start] ) | ^( POSITIVE_CLOSURE block[$start] ) | block[null] );
	public final ATNBuilder.subrule_return subrule() throws RecognitionException {
		ATNBuilder.subrule_return retval = new ATNBuilder.subrule_return();
		retval.start = input.LT(1);

		ATNFactory.Handle block19 =null;
		ATNFactory.Handle block20 =null;
		ATNFactory.Handle block21 =null;
		ATNFactory.Handle block22 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:154:2: ( ^( OPTIONAL block[$start] ) | ^( CLOSURE block[$start] ) | ^( POSITIVE_CLOSURE block[$start] ) | block[null] )
			int alt16=4;
			switch ( input.LA(1) ) {
			case OPTIONAL:
				{
				alt16=1;
				}
				break;
			case CLOSURE:
				{
				alt16=2;
				}
				break;
			case POSITIVE_CLOSURE:
				{
				alt16=3;
				}
				break;
			case BLOCK:
				{
				alt16=4;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}
			switch (alt16) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:154:4: ^( OPTIONAL block[$start] )
					{
					match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subrule669); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_block_in_subrule671);
					block19=block(((GrammarAST)retval.start));
					state._fsp--;

					match(input, Token.UP, null); 

					retval.p = block19;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:155:4: ^( CLOSURE block[$start] )
					{
					match(input,CLOSURE,FOLLOW_CLOSURE_in_subrule683); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_block_in_subrule685);
					block20=block(((GrammarAST)retval.start));
					state._fsp--;

					match(input, Token.UP, null); 

					retval.p = block20;
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:156:4: ^( POSITIVE_CLOSURE block[$start] )
					{
					match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_subrule697); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_block_in_subrule699);
					block21=block(((GrammarAST)retval.start));
					state._fsp--;

					match(input, Token.UP, null); 

					retval.p = block21;
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:157:5: block[null]
					{
					pushFollow(FOLLOW_block_in_subrule709);
					block22=block(null);
					state._fsp--;

					retval.p = block22;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "subrule"


	public static class blockSet_return extends TreeRuleReturnScope {
		public ATNFactory.Handle p;
	};


	// $ANTLR start "blockSet"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:160:1: blockSet[boolean invert] returns [ATNFactory.Handle p] : ^( SET ( setElement )+ ) ;
	public final ATNBuilder.blockSet_return blockSet(boolean invert) throws RecognitionException {
		ATNBuilder.blockSet_return retval = new ATNBuilder.blockSet_return();
		retval.start = input.LT(1);

		TreeRuleReturnScope setElement23 =null;

		List<GrammarAST> alts = new ArrayList<GrammarAST>();
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:162:2: ( ^( SET ( setElement )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:162:4: ^( SET ( setElement )+ )
			{
			match(input,SET,FOLLOW_SET_in_blockSet743); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:162:10: ( setElement )+
			int cnt17=0;
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==LEXER_CHAR_SET||LA17_0==RANGE||LA17_0==STRING_LITERAL||LA17_0==TOKEN_REF) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:162:11: setElement
					{
					pushFollow(FOLLOW_setElement_in_blockSet746);
					setElement23=setElement();
					state._fsp--;

					alts.add((setElement23!=null?((GrammarAST)setElement23.start):null));
					}
					break;

				default :
					if ( cnt17 >= 1 ) break loop17;
					EarlyExitException eee = new EarlyExitException(17, input);
					throw eee;
				}
				cnt17++;
			}

			match(input, Token.UP, null); 

			retval.p = factory.set(((GrammarAST)retval.start), alts, invert);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "blockSet"


	public static class setElement_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "setElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:166:1: setElement : ( ^( STRING_LITERAL . ) | ^( TOKEN_REF . ) | STRING_LITERAL | TOKEN_REF | ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) | LEXER_CHAR_SET );
	public final ATNBuilder.setElement_return setElement() throws RecognitionException {
		ATNBuilder.setElement_return retval = new ATNBuilder.setElement_return();
		retval.start = input.LT(1);

		GrammarAST a=null;
		GrammarAST b=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:167:2: ( ^( STRING_LITERAL . ) | ^( TOKEN_REF . ) | STRING_LITERAL | TOKEN_REF | ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) | LEXER_CHAR_SET )
			int alt18=6;
			switch ( input.LA(1) ) {
			case STRING_LITERAL:
				{
				int LA18_1 = input.LA(2);
				if ( (LA18_1==DOWN) ) {
					alt18=1;
				}
				else if ( (LA18_1==UP||LA18_1==LEXER_CHAR_SET||LA18_1==RANGE||LA18_1==STRING_LITERAL||LA18_1==TOKEN_REF) ) {
					alt18=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 18, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TOKEN_REF:
				{
				int LA18_2 = input.LA(2);
				if ( (LA18_2==DOWN) ) {
					alt18=2;
				}
				else if ( (LA18_2==UP||LA18_2==LEXER_CHAR_SET||LA18_2==RANGE||LA18_2==STRING_LITERAL||LA18_2==TOKEN_REF) ) {
					alt18=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 18, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case RANGE:
				{
				alt18=5;
				}
				break;
			case LEXER_CHAR_SET:
				{
				alt18=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}
			switch (alt18) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:167:4: ^( STRING_LITERAL . )
					{
					match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement767); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:168:4: ^( TOKEN_REF . )
					{
					match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_setElement776); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:169:4: STRING_LITERAL
					{
					match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement784); 
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:170:4: TOKEN_REF
					{
					match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_setElement789); 
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:171:4: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
					{
					match(input,RANGE,FOLLOW_RANGE_in_setElement795); 
					match(input, Token.DOWN, null); 
					a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement799); 
					b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement803); 
					match(input, Token.UP, null); 

					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:172:9: LEXER_CHAR_SET
					{
					match(input,LEXER_CHAR_SET,FOLLOW_LEXER_CHAR_SET_in_setElement814); 
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "setElement"


	public static class atom_return extends TreeRuleReturnScope {
		public ATNFactory.Handle p;
	};


	// $ANTLR start "atom"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:175:1: atom returns [ATNFactory.Handle p] : ( range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | blockSet[false] | terminal | ruleref );
	public final ATNBuilder.atom_return atom() throws RecognitionException {
		ATNBuilder.atom_return retval = new ATNBuilder.atom_return();
		retval.start = input.LT(1);

		ATNFactory.Handle range24 =null;
		TreeRuleReturnScope terminal25 =null;
		ATNFactory.Handle ruleref26 =null;
		TreeRuleReturnScope blockSet27 =null;
		TreeRuleReturnScope terminal28 =null;
		ATNFactory.Handle ruleref29 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:176:2: ( range | ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD . ) | WILDCARD | blockSet[false] | terminal | ruleref )
			int alt19=8;
			switch ( input.LA(1) ) {
			case RANGE:
				{
				alt19=1;
				}
				break;
			case DOT:
				{
				int LA19_2 = input.LA(2);
				if ( (LA19_2==DOWN) ) {
					int LA19_7 = input.LA(3);
					if ( (LA19_7==ID) ) {
						int LA19_10 = input.LA(4);
						if ( (LA19_10==STRING_LITERAL||LA19_10==TOKEN_REF) ) {
							alt19=2;
						}
						else if ( (LA19_10==RULE_REF) ) {
							alt19=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 19, 10, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 19, 7, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 19, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case WILDCARD:
				{
				int LA19_3 = input.LA(2);
				if ( (LA19_3==DOWN) ) {
					alt19=4;
				}
				else if ( (LA19_3==EOF||(LA19_3 >= UP && LA19_3 <= ACTION)||LA19_3==ASSIGN||LA19_3==DOT||LA19_3==LEXER_CHAR_SET||LA19_3==NOT||LA19_3==PLUS_ASSIGN||LA19_3==RANGE||LA19_3==RULE_REF||LA19_3==SEMPRED||LA19_3==STRING_LITERAL||LA19_3==TOKEN_REF||LA19_3==BLOCK||LA19_3==CLOSURE||(LA19_3 >= OPTIONAL && LA19_3 <= POSITIVE_CLOSURE)||LA19_3==SET||LA19_3==WILDCARD) ) {
					alt19=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 19, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SET:
				{
				alt19=6;
				}
				break;
			case STRING_LITERAL:
			case TOKEN_REF:
				{
				alt19=7;
				}
				break;
			case RULE_REF:
				{
				alt19=8;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 19, 0, input);
				throw nvae;
			}
			switch (alt19) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:176:4: range
					{
					pushFollow(FOLLOW_range_in_atom829);
					range24=range();
					state._fsp--;

					retval.p = range24;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:177:4: ^( DOT ID terminal )
					{
					match(input,DOT,FOLLOW_DOT_in_atom841); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_atom843); 
					pushFollow(FOLLOW_terminal_in_atom845);
					terminal25=terminal();
					state._fsp--;

					match(input, Token.UP, null); 

					retval.p = (terminal25!=null?((ATNBuilder.terminal_return)terminal25).p:null);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:178:4: ^( DOT ID ruleref )
					{
					match(input,DOT,FOLLOW_DOT_in_atom855); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_atom857); 
					pushFollow(FOLLOW_ruleref_in_atom859);
					ruleref26=ruleref();
					state._fsp--;

					match(input, Token.UP, null); 

					retval.p = ruleref26;
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:179:7: ^( WILDCARD . )
					{
					match(input,WILDCARD,FOLLOW_WILDCARD_in_atom872); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					retval.p = factory.wildcard(((GrammarAST)retval.start));
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:180:7: WILDCARD
					{
					match(input,WILDCARD,FOLLOW_WILDCARD_in_atom887); 
					retval.p = factory.wildcard(((GrammarAST)retval.start));
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:181:7: blockSet[false]
					{
					pushFollow(FOLLOW_blockSet_in_atom900);
					blockSet27=blockSet(false);
					state._fsp--;

					retval.p = (blockSet27!=null?((ATNBuilder.blockSet_return)blockSet27).p:null);
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:182:9: terminal
					{
					pushFollow(FOLLOW_terminal_in_atom915);
					terminal28=terminal();
					state._fsp--;

					retval.p = (terminal28!=null?((ATNBuilder.terminal_return)terminal28).p:null);
					}
					break;
				case 8 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:183:9: ruleref
					{
					pushFollow(FOLLOW_ruleref_in_atom930);
					ruleref29=ruleref();
					state._fsp--;

					retval.p = ruleref29;
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "atom"



	// $ANTLR start "ruleref"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:186:1: ruleref returns [ATNFactory.Handle p] : ( ^( RULE_REF ( ARG_ACTION )? ^( ELEMENT_OPTIONS ( . )* ) ) | ^( RULE_REF ( ARG_ACTION )? ) | RULE_REF );
	public final ATNFactory.Handle ruleref() throws RecognitionException {
		ATNFactory.Handle p = null;


		GrammarAST RULE_REF30=null;
		GrammarAST RULE_REF31=null;
		GrammarAST RULE_REF32=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:187:5: ( ^( RULE_REF ( ARG_ACTION )? ^( ELEMENT_OPTIONS ( . )* ) ) | ^( RULE_REF ( ARG_ACTION )? ) | RULE_REF )
			int alt23=3;
			int LA23_0 = input.LA(1);
			if ( (LA23_0==RULE_REF) ) {
				int LA23_1 = input.LA(2);
				if ( (LA23_1==DOWN) ) {
					switch ( input.LA(3) ) {
					case ARG_ACTION:
						{
						int LA23_4 = input.LA(4);
						if ( (LA23_4==ELEMENT_OPTIONS) ) {
							alt23=1;
						}
						else if ( (LA23_4==UP) ) {
							alt23=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 23, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

						}
						break;
					case ELEMENT_OPTIONS:
						{
						alt23=1;
						}
						break;
					case UP:
						{
						alt23=2;
						}
						break;
					default:
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 23, 2, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}
				}
				else if ( (LA23_1==EOF||(LA23_1 >= UP && LA23_1 <= ACTION)||LA23_1==ASSIGN||LA23_1==DOT||LA23_1==LEXER_CHAR_SET||LA23_1==NOT||LA23_1==PLUS_ASSIGN||LA23_1==RANGE||LA23_1==RULE_REF||LA23_1==SEMPRED||LA23_1==STRING_LITERAL||LA23_1==TOKEN_REF||LA23_1==BLOCK||LA23_1==CLOSURE||(LA23_1 >= OPTIONAL && LA23_1 <= POSITIVE_CLOSURE)||LA23_1==SET||LA23_1==WILDCARD) ) {
					alt23=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 23, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 23, 0, input);
				throw nvae;
			}

			switch (alt23) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:187:7: ^( RULE_REF ( ARG_ACTION )? ^( ELEMENT_OPTIONS ( . )* ) )
					{
					RULE_REF30=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref958); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:187:18: ( ARG_ACTION )?
					int alt20=2;
					int LA20_0 = input.LA(1);
					if ( (LA20_0==ARG_ACTION) ) {
						alt20=1;
					}
					switch (alt20) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:187:18: ARG_ACTION
							{
							match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref960); 
							}
							break;

					}

					match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_ruleref964); 
					if ( input.LA(1)==Token.DOWN ) {
						match(input, Token.DOWN, null); 
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:187:48: ( . )*
						loop21:
						while (true) {
							int alt21=2;
							int LA21_0 = input.LA(1);
							if ( ((LA21_0 >= ACTION && LA21_0 <= WILDCARD)) ) {
								alt21=1;
							}
							else if ( (LA21_0==UP) ) {
								alt21=2;
							}

							switch (alt21) {
							case 1 :
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:187:48: .
								{
								matchAny(input); 
								}
								break;

							default :
								break loop21;
							}
						}

						match(input, Token.UP, null); 
					}

					match(input, Token.UP, null); 

					p = factory.ruleRef(RULE_REF30);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:188:7: ^( RULE_REF ( ARG_ACTION )? )
					{
					RULE_REF31=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref981); 
					if ( input.LA(1)==Token.DOWN ) {
						match(input, Token.DOWN, null); 
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:188:18: ( ARG_ACTION )?
						int alt22=2;
						int LA22_0 = input.LA(1);
						if ( (LA22_0==ARG_ACTION) ) {
							alt22=1;
						}
						switch (alt22) {
							case 1 :
								// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:188:18: ARG_ACTION
								{
								match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref983); 
								}
								break;

						}

						match(input, Token.UP, null); 
					}

					p = factory.ruleRef(RULE_REF31);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:189:7: RULE_REF
					{
					RULE_REF32=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref1002); 
					p = factory.ruleRef(RULE_REF32);
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "ruleref"



	// $ANTLR start "range"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:192:1: range returns [ATNFactory.Handle p] : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
	public final ATNFactory.Handle range() throws RecognitionException {
		ATNFactory.Handle p = null;


		GrammarAST a=null;
		GrammarAST b=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:193:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:193:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
			{
			match(input,RANGE,FOLLOW_RANGE_in_range1036); 
			match(input, Token.DOWN, null); 
			a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range1040); 
			b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range1044); 
			match(input, Token.UP, null); 

			p = factory.range(a,b);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return p;
	}
	// $ANTLR end "range"


	public static class terminal_return extends TreeRuleReturnScope {
		public ATNFactory.Handle p;
	};


	// $ANTLR start "terminal"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:196:1: terminal returns [ATNFactory.Handle p] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF );
	public final ATNBuilder.terminal_return terminal() throws RecognitionException {
		ATNBuilder.terminal_return retval = new ATNBuilder.terminal_return();
		retval.start = input.LT(1);

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:197:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF )
			int alt24=5;
			int LA24_0 = input.LA(1);
			if ( (LA24_0==STRING_LITERAL) ) {
				int LA24_1 = input.LA(2);
				if ( (LA24_1==DOWN) ) {
					alt24=1;
				}
				else if ( (LA24_1==EOF||(LA24_1 >= UP && LA24_1 <= ACTION)||LA24_1==ASSIGN||LA24_1==DOT||LA24_1==LEXER_CHAR_SET||LA24_1==NOT||LA24_1==PLUS_ASSIGN||LA24_1==RANGE||LA24_1==RULE_REF||LA24_1==SEMPRED||LA24_1==STRING_LITERAL||LA24_1==TOKEN_REF||LA24_1==BLOCK||LA24_1==CLOSURE||(LA24_1 >= OPTIONAL && LA24_1 <= POSITIVE_CLOSURE)||LA24_1==SET||LA24_1==WILDCARD) ) {
					alt24=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 24, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA24_0==TOKEN_REF) ) {
				int LA24_2 = input.LA(2);
				if ( (LA24_2==DOWN) ) {
					int LA24_5 = input.LA(3);
					if ( (LA24_5==ARG_ACTION) ) {
						int LA24_7 = input.LA(4);
						if ( ((LA24_7 >= ACTION && LA24_7 <= WILDCARD)) ) {
							alt24=3;
						}
						else if ( ((LA24_7 >= DOWN && LA24_7 <= UP)) ) {
							alt24=4;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 24, 7, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( ((LA24_5 >= ACTION && LA24_5 <= ACTION_STRING_LITERAL)||(LA24_5 >= ARG_OR_CHARSET && LA24_5 <= WILDCARD)) ) {
						alt24=4;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 24, 5, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( (LA24_2==EOF||(LA24_2 >= UP && LA24_2 <= ACTION)||LA24_2==ASSIGN||LA24_2==DOT||LA24_2==LEXER_CHAR_SET||LA24_2==NOT||LA24_2==PLUS_ASSIGN||LA24_2==RANGE||LA24_2==RULE_REF||LA24_2==SEMPRED||LA24_2==STRING_LITERAL||LA24_2==TOKEN_REF||LA24_2==BLOCK||LA24_2==CLOSURE||(LA24_2 >= OPTIONAL && LA24_2 <= POSITIVE_CLOSURE)||LA24_2==SET||LA24_2==WILDCARD) ) {
					alt24=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 24, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 24, 0, input);
				throw nvae;
			}

			switch (alt24) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:197:8: ^( STRING_LITERAL . )
					{
					match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1070); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:198:7: STRING_LITERAL
					{
					match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal1085); 
					retval.p = factory.stringLiteral((TerminalAST)((GrammarAST)retval.start));
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:199:7: ^( TOKEN_REF ARG_ACTION . )
					{
					match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1099); 
					match(input, Token.DOWN, null); 
					match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal1101); 
					matchAny(input); 
					match(input, Token.UP, null); 

					retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:200:7: ^( TOKEN_REF . )
					{
					match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1115); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:201:7: TOKEN_REF
					{
					match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal1131); 
					retval.p = factory.tokenRef((TerminalAST)((GrammarAST)retval.start));
					}
					break;

			}
		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return retval;
	}
	// $ANTLR end "terminal"



	// $ANTLR start "elementOptions"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:204:1: elementOptions : ^( ELEMENT_OPTIONS ( elementOption )* ) ;
	public final void elementOptions() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:205:2: ( ^( ELEMENT_OPTIONS ( elementOption )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:205:4: ^( ELEMENT_OPTIONS ( elementOption )* )
			{
			match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOptions1152); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:205:22: ( elementOption )*
				loop25:
				while (true) {
					int alt25=2;
					int LA25_0 = input.LA(1);
					if ( (LA25_0==ASSIGN||LA25_0==ID) ) {
						alt25=1;
					}

					switch (alt25) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:205:22: elementOption
						{
						pushFollow(FOLLOW_elementOption_in_elementOptions1154);
						elementOption();
						state._fsp--;

						}
						break;

					default :
						break loop25;
					}
				}

				match(input, Token.UP, null); 
			}

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "elementOptions"



	// $ANTLR start "elementOption"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:208:1: elementOption : ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) | ^( ASSIGN ID ACTION ) | ^( ASSIGN ID INT ) );
	public final void elementOption() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:209:2: ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) | ^( ASSIGN ID ACTION ) | ^( ASSIGN ID INT ) )
			int alt26=5;
			int LA26_0 = input.LA(1);
			if ( (LA26_0==ID) ) {
				alt26=1;
			}
			else if ( (LA26_0==ASSIGN) ) {
				int LA26_2 = input.LA(2);
				if ( (LA26_2==DOWN) ) {
					int LA26_3 = input.LA(3);
					if ( (LA26_3==ID) ) {
						switch ( input.LA(4) ) {
						case ID:
							{
							alt26=2;
							}
							break;
						case STRING_LITERAL:
							{
							alt26=3;
							}
							break;
						case ACTION:
							{
							alt26=4;
							}
							break;
						case INT:
							{
							alt26=5;
							}
							break;
						default:
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 26, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 26, 3, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 26, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 26, 0, input);
				throw nvae;
			}

			switch (alt26) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:209:4: ID
					{
					match(input,ID,FOLLOW_ID_in_elementOption1167); 
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:210:4: ^( ASSIGN ID ID )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1173); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption1175); 
					match(input,ID,FOLLOW_ID_in_elementOption1177); 
					match(input, Token.UP, null); 

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:211:4: ^( ASSIGN ID STRING_LITERAL )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1184); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption1186); 
					match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption1188); 
					match(input, Token.UP, null); 

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:212:4: ^( ASSIGN ID ACTION )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1195); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption1197); 
					match(input,ACTION,FOLLOW_ACTION_in_elementOption1199); 
					match(input, Token.UP, null); 

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ATNBuilder.g:213:4: ^( ASSIGN ID INT )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption1206); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption1208); 
					match(input,INT,FOLLOW_INT_in_elementOption1210); 
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
			// do for sure before leaving
		}
	}
	// $ANTLR end "elementOption"

	// Delegated rules


	protected DFA10 dfa10 = new DFA10(this);
	static final String DFA10_eotS =
		"\25\uffff";
	static final String DFA10_eofS =
		"\25\uffff";
	static final String DFA10_minS =
		"\1\111\1\uffff\1\2\1\4\1\2\2\uffff\2\3\1\2\1\4\1\34\1\4\10\3";
	static final String DFA10_maxS =
		"\1\126\1\uffff\1\2\1\143\1\2\2\uffff\2\34\1\2\1\143\1\34\1\76\4\3\4\34";
	static final String DFA10_acceptS =
		"\1\uffff\1\1\3\uffff\1\2\1\3\16\uffff";
	static final String DFA10_specialS =
		"\25\uffff}>";
	static final String[] DFA10_transitionS = {
			"\1\2\14\uffff\1\1",
			"",
			"\1\3",
			"\1\6\5\uffff\1\6\11\uffff\1\6\13\uffff\1\6\6\uffff\1\6\6\uffff\1\6\5"+
			"\uffff\1\6\4\uffff\1\6\1\uffff\1\6\2\uffff\1\6\3\uffff\1\6\12\uffff\1"+
			"\6\1\uffff\1\6\1\uffff\1\4\1\5\5\uffff\2\6\7\uffff\1\6\1\uffff\1\6",
			"\1\7",
			"",
			"",
			"\1\12\6\uffff\1\11\21\uffff\1\10",
			"\1\12\6\uffff\1\11\21\uffff\1\10",
			"\1\13",
			"\1\6\5\uffff\1\6\11\uffff\1\6\13\uffff\1\6\6\uffff\1\6\6\uffff\1\6\5"+
			"\uffff\1\6\4\uffff\1\6\1\uffff\1\6\2\uffff\1\6\3\uffff\1\6\12\uffff\1"+
			"\6\1\uffff\1\6\2\uffff\1\5\5\uffff\2\6\7\uffff\1\6\1\uffff\1\6",
			"\1\14",
			"\1\17\27\uffff\1\15\1\uffff\1\20\37\uffff\1\16",
			"\1\21",
			"\1\22",
			"\1\23",
			"\1\24",
			"\1\12\6\uffff\1\11\21\uffff\1\10",
			"\1\12\6\uffff\1\11\21\uffff\1\10",
			"\1\12\6\uffff\1\11\21\uffff\1\10",
			"\1\12\6\uffff\1\11\21\uffff\1\10"
	};

	static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
	static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
	static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
	static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
	static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
	static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
	static final short[][] DFA10_transition;

	static {
		int numStates = DFA10_transitionS.length;
		DFA10_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
		}
	}

	protected class DFA10 extends DFA {

		public DFA10(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 10;
			this.eot = DFA10_eot;
			this.eof = DFA10_eof;
			this.min = DFA10_min;
			this.max = DFA10_max;
			this.accept = DFA10_accept;
			this.special = DFA10_special;
			this.transition = DFA10_transition;
		}
		@Override
		public String getDescription() {
			return "103:1: alternative returns [ATNFactory.Handle p] : ( ^( LEXER_ALT_ACTION a= alternative lexerCommands ) | ^( ALT ( elementOptions )? EPSILON ) | ^( ALT ( elementOptions )? (e= element )+ ) );";
		}
	}

	public static final BitSet FOLLOW_block_in_dummy63 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BLOCK_in_ruleBlock89 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_OPTIONS_in_ruleBlock105 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_alternative_in_ruleBlock131 = new BitSet(new long[]{0x0000000000000008L,0x0000000000400200L});
	public static final BitSet FOLLOW_BLOCK_in_block209 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_OPTIONS_in_block213 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_alternative_in_block224 = new BitSet(new long[]{0x0000000000000008L,0x0000000000400200L});
	public static final BitSet FOLLOW_LEXER_ALT_ACTION_in_alternative263 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_alternative_in_alternative267 = new BitSet(new long[]{0x0000000010000000L,0x0000000000200000L});
	public static final BitSet FOLLOW_lexerCommands_in_alternative269 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ALT_in_alternative289 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_alternative291 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
	public static final BitSet FOLLOW_EPSILON_in_alternative294 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ALT_in_alternative314 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_alternative316 = new BitSet(new long[]{0x4A10408100100410L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_element_in_alternative322 = new BitSet(new long[]{0x4A10408100100418L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_lexerCommand_in_lexerCommands360 = new BitSet(new long[]{0x0000000010000002L,0x0000000000200000L});
	public static final BitSet FOLLOW_LEXER_ACTION_CALL_in_lexerCommand393 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_lexerCommand395 = new BitSet(new long[]{0x0000000050000000L});
	public static final BitSet FOLLOW_lexerCommandExpr_in_lexerCommand397 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ID_in_lexerCommand413 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_labeledElement_in_element454 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_element464 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_subrule_in_element476 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_element490 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMPRED_in_element504 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_element519 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_SEMPRED_in_element536 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_NOT_in_element553 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_blockSet_in_element557 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_LEXER_CHAR_SET_in_element570 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_astOperand591 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_astOperand604 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_blockSet_in_astOperand606 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_labeledElement627 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledElement629 = new BitSet(new long[]{0x4A10408100100410L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_element_in_labeledElement631 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement644 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledElement646 = new BitSet(new long[]{0x4A10408100100410L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_element_in_labeledElement648 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_OPTIONAL_in_subrule669 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_subrule671 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_CLOSURE_in_subrule683 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_subrule685 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_subrule697 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_subrule699 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_block_in_subrule709 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SET_in_blockSet743 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet746 = new BitSet(new long[]{0x4010000100000008L,0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement767 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TOKEN_REF_in_setElement776 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement784 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_setElement789 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RANGE_in_setElement795 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement799 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement803 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_LEXER_CHAR_SET_in_setElement814 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_range_in_atom829 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_atom841 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_atom843 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_terminal_in_atom845 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_DOT_in_atom855 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_atom857 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_ruleref_in_atom859 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_WILDCARD_in_atom872 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_WILDCARD_in_atom887 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_blockSet_in_atom900 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_terminal_in_atom915 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ruleref_in_atom930 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RULE_REF_in_ruleref958 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_ruleref960 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
	public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_ruleref964 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_RULE_REF_in_ruleref981 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_ruleref983 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_RULE_REF_in_ruleref1002 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RANGE_in_range1036 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range1040 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range1044 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1070 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_terminal1085 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal1099 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_terminal1101 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000FFFFFFFFFL});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal1115 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal1131 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOptions1152 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOption_in_elementOptions1154 = new BitSet(new long[]{0x0000000010000408L});
	public static final BitSet FOLLOW_ID_in_elementOption1167 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption1173 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption1175 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_ID_in_elementOption1177 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption1184 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption1186 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption1188 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption1195 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption1197 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_elementOption1199 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption1206 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption1208 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_INT_in_elementOption1210 = new BitSet(new long[]{0x0000000000000008L});
}
