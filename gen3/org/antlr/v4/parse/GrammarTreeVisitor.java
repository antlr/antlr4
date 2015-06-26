// $ANTLR 3.5.2 /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g 2015-06-23 21:59:56

/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
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
import org.antlr.v4.Tool;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/** The definitive ANTLR v3 tree grammar to walk/visit ANTLR v4 grammars.
 *  Parses trees created by ANTLRParser.g.
 *
 *  Rather than have multiple tree grammars, one for each visit, I'm
 *  creating this generic visitor that knows about context. All of the
 *  boilerplate pattern recognition is done here. Then, subclasses can
 *  override the methods they care about. This prevents a lot of the same
 *  context tracking stuff like "set current alternative for current
 *  rule node" that is repeated in lots of tree filters.
 */
@SuppressWarnings("all")
public class GrammarTreeVisitor extends TreeParser {
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


	public GrammarTreeVisitor(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public GrammarTreeVisitor(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return GrammarTreeVisitor.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g"; }


	public String grammarName;
	public GrammarAST currentRuleAST;
	public String currentModeName = LexerGrammar.DEFAULT_MODE_NAME;
	public String currentRuleName;
	public GrammarAST currentOuterAltRoot;
	public int currentOuterAltNumber = 1; // 1..n
	public int rewriteEBNFLevel = 0;

	public GrammarTreeVisitor() { this(null); }

	// Should be abstract but can't make gen'd parser abstract;
	// subclasses should implement else everything goes to stderr!
	public ErrorManager getErrorManager() { return null; }

	public void visitGrammar(GrammarAST t) { visit(t, "grammarSpec"); }
	public void visit(GrammarAST t, String ruleName) {
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(new GrammarASTAdaptor(), t);
		setTreeNodeStream(nodes);
		try {
			Method m = getClass().getMethod(ruleName);
			m.invoke(this);
		}
		catch (Throwable e) {
			ErrorManager errMgr = getErrorManager();
			if ( e instanceof InvocationTargetException ) {
				e = e.getCause();
			}
			//e.printStackTrace(System.err);
			if ( errMgr==null ) {
				System.err.println("can't find rule "+ruleName+
								   " or tree structure error: "+t.toStringTree()
								   );
				e.printStackTrace(System.err);
			}
			else errMgr.toolError(ErrorType.INTERNAL_ERROR, e);
		}
	}

	public void discoverGrammar(GrammarRootAST root, GrammarAST ID) { }
	public void finishPrequels(GrammarAST firstPrequel) { }
	public void finishGrammar(GrammarRootAST root, GrammarAST ID) { }

	public void grammarOption(GrammarAST ID, GrammarAST valueAST) { }
	public void ruleOption(GrammarAST ID, GrammarAST valueAST) { }
	public void blockOption(GrammarAST ID, GrammarAST valueAST) { }
	public void defineToken(GrammarAST ID) { }
	public void defineChannel(GrammarAST ID) { }
	public void globalNamedAction(GrammarAST scope, GrammarAST ID, ActionAST action) { }
	public void importGrammar(GrammarAST label, GrammarAST ID) { }

	public void modeDef(GrammarAST m, GrammarAST ID) { }

	public void discoverRules(GrammarAST rules) { }
	public void finishRules(GrammarAST rule) { }
	public void discoverRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
							 ActionAST arg, ActionAST returns, GrammarAST thrws,
							 GrammarAST options, ActionAST locals,
							 List<GrammarAST> actions,
							 GrammarAST block) { }
	public void finishRule(RuleAST rule, GrammarAST ID, GrammarAST block) { }
	public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
	                              GrammarAST block) { }
	public void finishLexerRule(RuleAST rule, GrammarAST ID, GrammarAST block) { }
	public void ruleCatch(GrammarAST arg, ActionAST action) { }
	public void finallyAction(ActionAST action) { }
	public void discoverOuterAlt(AltAST alt) { }
	public void finishOuterAlt(AltAST alt) { }
	public void discoverAlt(AltAST alt) { }
	public void finishAlt(AltAST alt) { }

	public void ruleRef(GrammarAST ref, ActionAST arg) { }
	public void tokenRef(TerminalAST ref) { }
	public void elementOption(GrammarASTWithOptions t, GrammarAST ID, GrammarAST valueAST) { }
	public void stringRef(TerminalAST ref) { }
	public void wildcardRef(GrammarAST ref) { }
	public void actionInAlt(ActionAST action) { }
	public void sempredInAlt(PredAST pred) { }
	public void label(GrammarAST op, GrammarAST ID, GrammarAST element) { }
	public void lexerCallCommand(int outerAltNumber, GrammarAST ID, GrammarAST arg) { }
	public void lexerCommand(int outerAltNumber, GrammarAST ID) { }

	protected void enterGrammarSpec(GrammarAST tree) { }
	protected void exitGrammarSpec(GrammarAST tree) { }

	protected void enterPrequelConstructs(GrammarAST tree) { }
	protected void exitPrequelConstructs(GrammarAST tree) { }

	protected void enterPrequelConstruct(GrammarAST tree) { }
	protected void exitPrequelConstruct(GrammarAST tree) { }

	protected void enterOptionsSpec(GrammarAST tree) { }
	protected void exitOptionsSpec(GrammarAST tree) { }

	protected void enterOption(GrammarAST tree) { }
	protected void exitOption(GrammarAST tree) { }

	protected void enterOptionValue(GrammarAST tree) { }
	protected void exitOptionValue(GrammarAST tree) { }

	protected void enterDelegateGrammars(GrammarAST tree) { }
	protected void exitDelegateGrammars(GrammarAST tree) { }

	protected void enterDelegateGrammar(GrammarAST tree) { }
	protected void exitDelegateGrammar(GrammarAST tree) { }

	protected void enterTokensSpec(GrammarAST tree) { }
	protected void exitTokensSpec(GrammarAST tree) { }

	protected void enterTokenSpec(GrammarAST tree) { }
	protected void exitTokenSpec(GrammarAST tree) { }

	protected void enterChannelsSpec(GrammarAST tree) { }
	protected void exitChannelsSpec(GrammarAST tree) { }

	protected void enterChannelSpec(GrammarAST tree) { }
	protected void exitChannelSpec(GrammarAST tree) { }

	protected void enterAction(GrammarAST tree) { }
	protected void exitAction(GrammarAST tree) { }

	protected void enterRules(GrammarAST tree) { }
	protected void exitRules(GrammarAST tree) { }

	protected void enterMode(GrammarAST tree) { }
	protected void exitMode(GrammarAST tree) { }

	protected void enterLexerRule(GrammarAST tree) { }
	protected void exitLexerRule(GrammarAST tree) { }

	protected void enterRule(GrammarAST tree) { }
	protected void exitRule(GrammarAST tree) { }

	protected void enterExceptionGroup(GrammarAST tree) { }
	protected void exitExceptionGroup(GrammarAST tree) { }

	protected void enterExceptionHandler(GrammarAST tree) { }
	protected void exitExceptionHandler(GrammarAST tree) { }

	protected void enterFinallyClause(GrammarAST tree) { }
	protected void exitFinallyClause(GrammarAST tree) { }

	protected void enterLocals(GrammarAST tree) { }
	protected void exitLocals(GrammarAST tree) { }

	protected void enterRuleReturns(GrammarAST tree) { }
	protected void exitRuleReturns(GrammarAST tree) { }

	protected void enterThrowsSpec(GrammarAST tree) { }
	protected void exitThrowsSpec(GrammarAST tree) { }

	protected void enterRuleAction(GrammarAST tree) { }
	protected void exitRuleAction(GrammarAST tree) { }

	protected void enterRuleModifier(GrammarAST tree) { }
	protected void exitRuleModifier(GrammarAST tree) { }

	protected void enterLexerRuleBlock(GrammarAST tree) { }
	protected void exitLexerRuleBlock(GrammarAST tree) { }

	protected void enterRuleBlock(GrammarAST tree) { }
	protected void exitRuleBlock(GrammarAST tree) { }

	protected void enterLexerOuterAlternative(AltAST tree) { }
	protected void exitLexerOuterAlternative(AltAST tree) { }

	protected void enterOuterAlternative(AltAST tree) { }
	protected void exitOuterAlternative(AltAST tree) { }

	protected void enterLexerAlternative(GrammarAST tree) { }
	protected void exitLexerAlternative(GrammarAST tree) { }

	protected void enterLexerElements(GrammarAST tree) { }
	protected void exitLexerElements(GrammarAST tree) { }

	protected void enterLexerElement(GrammarAST tree) { }
	protected void exitLexerElement(GrammarAST tree) { }

	protected void enterLabeledLexerElement(GrammarAST tree) { }
	protected void exitLabeledLexerElement(GrammarAST tree) { }

	protected void enterLexerBlock(GrammarAST tree) { }
	protected void exitLexerBlock(GrammarAST tree) { }

	protected void enterLexerAtom(GrammarAST tree) { }
	protected void exitLexerAtom(GrammarAST tree) { }

	protected void enterActionElement(GrammarAST tree) { }
	protected void exitActionElement(GrammarAST tree) { }

	protected void enterAlternative(AltAST tree) { }
	protected void exitAlternative(AltAST tree) { }

	protected void enterLexerCommand(GrammarAST tree) { }
	protected void exitLexerCommand(GrammarAST tree) { }

	protected void enterLexerCommandExpr(GrammarAST tree) { }
	protected void exitLexerCommandExpr(GrammarAST tree) { }

	protected void enterElement(GrammarAST tree) { }
	protected void exitElement(GrammarAST tree) { }

	protected void enterAstOperand(GrammarAST tree) { }
	protected void exitAstOperand(GrammarAST tree) { }

	protected void enterLabeledElement(GrammarAST tree) { }
	protected void exitLabeledElement(GrammarAST tree) { }

	protected void enterSubrule(GrammarAST tree) { }
	protected void exitSubrule(GrammarAST tree) { }

	protected void enterLexerSubrule(GrammarAST tree) { }
	protected void exitLexerSubrule(GrammarAST tree) { }

	protected void enterBlockSuffix(GrammarAST tree) { }
	protected void exitBlockSuffix(GrammarAST tree) { }

	protected void enterEbnfSuffix(GrammarAST tree) { }
	protected void exitEbnfSuffix(GrammarAST tree) { }

	protected void enterAtom(GrammarAST tree) { }
	protected void exitAtom(GrammarAST tree) { }

	protected void enterBlockSet(GrammarAST tree) { }
	protected void exitBlockSet(GrammarAST tree) { }

	protected void enterSetElement(GrammarAST tree) { }
	protected void exitSetElement(GrammarAST tree) { }

	protected void enterBlock(GrammarAST tree) { }
	protected void exitBlock(GrammarAST tree) { }

	protected void enterRuleref(GrammarAST tree) { }
	protected void exitRuleref(GrammarAST tree) { }

	protected void enterRange(GrammarAST tree) { }
	protected void exitRange(GrammarAST tree) { }

	protected void enterTerminal(GrammarAST tree) { }
	protected void exitTerminal(GrammarAST tree) { }

	protected void enterElementOptions(GrammarAST tree) { }
	protected void exitElementOptions(GrammarAST tree) { }

	protected void enterElementOption(GrammarAST tree) { }
	protected void exitElementOption(GrammarAST tree) { }

		@Override
		public void traceIn(String ruleName, int ruleIndex)  {
			System.err.println("enter "+ruleName+": "+input.LT(1));
		}

		@Override
		public void traceOut(String ruleName, int ruleIndex)  {
			System.err.println("exit "+ruleName+": "+input.LT(1));
		}


	public static class grammarSpec_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "grammarSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:344:1: grammarSpec : ^( GRAMMAR ID prequelConstructs rules ( mode )* ) ;
	public final GrammarTreeVisitor.grammarSpec_return grammarSpec() throws RecognitionException {
		GrammarTreeVisitor.grammarSpec_return retval = new GrammarTreeVisitor.grammarSpec_return();
		retval.start = input.LT(1);

		GrammarAST ID1=null;
		GrammarAST GRAMMAR2=null;
		TreeRuleReturnScope prequelConstructs3 =null;


			enterGrammarSpec(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:351:5: ( ^( GRAMMAR ID prequelConstructs rules ( mode )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:351:9: ^( GRAMMAR ID prequelConstructs rules ( mode )* )
			{
			GRAMMAR2=(GrammarAST)match(input,GRAMMAR,FOLLOW_GRAMMAR_in_grammarSpec85); 
			match(input, Token.DOWN, null); 
			ID1=(GrammarAST)match(input,ID,FOLLOW_ID_in_grammarSpec87); 
			grammarName=(ID1!=null?ID1.getText():null);
			discoverGrammar((GrammarRootAST)GRAMMAR2, ID1);
			pushFollow(FOLLOW_prequelConstructs_in_grammarSpec106);
			prequelConstructs3=prequelConstructs();
			state._fsp--;

			finishPrequels((prequelConstructs3!=null?((GrammarTreeVisitor.prequelConstructs_return)prequelConstructs3).firstOne:null));
			pushFollow(FOLLOW_rules_in_grammarSpec123);
			rules();
			state._fsp--;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:355:14: ( mode )*
			loop1:
			while (true) {
				int alt1=2;
				int LA1_0 = input.LA(1);
				if ( (LA1_0==MODE) ) {
					alt1=1;
				}

				switch (alt1) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:355:14: mode
					{
					pushFollow(FOLLOW_mode_in_grammarSpec125);
					mode();
					state._fsp--;

					}
					break;

				default :
					break loop1;
				}
			}

			finishGrammar((GrammarRootAST)GRAMMAR2, ID1);
			match(input, Token.UP, null); 

			}


				exitGrammarSpec(((GrammarAST)retval.start));

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
	// $ANTLR end "grammarSpec"


	public static class prequelConstructs_return extends TreeRuleReturnScope {
		public GrammarAST firstOne=null;
	};


	// $ANTLR start "prequelConstructs"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:360:1: prequelConstructs returns [GrammarAST firstOne=null] : ( ( prequelConstruct )+ |);
	public final GrammarTreeVisitor.prequelConstructs_return prequelConstructs() throws RecognitionException {
		GrammarTreeVisitor.prequelConstructs_return retval = new GrammarTreeVisitor.prequelConstructs_return();
		retval.start = input.LT(1);


			enterPrequelConstructs(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:367:2: ( ( prequelConstruct )+ |)
			int alt3=2;
			int LA3_0 = input.LA(1);
			if ( (LA3_0==AT||LA3_0==CHANNELS||LA3_0==IMPORT||LA3_0==OPTIONS||LA3_0==TOKENS_SPEC) ) {
				alt3=1;
			}
			else if ( (LA3_0==RULES) ) {
				alt3=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 3, 0, input);
				throw nvae;
			}

			switch (alt3) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:367:4: ( prequelConstruct )+
					{
					retval.firstOne =((GrammarAST)retval.start);
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:367:24: ( prequelConstruct )+
					int cnt2=0;
					loop2:
					while (true) {
						int alt2=2;
						int LA2_0 = input.LA(1);
						if ( (LA2_0==AT||LA2_0==CHANNELS||LA2_0==IMPORT||LA2_0==OPTIONS||LA2_0==TOKENS_SPEC) ) {
							alt2=1;
						}

						switch (alt2) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:367:24: prequelConstruct
							{
							pushFollow(FOLLOW_prequelConstruct_in_prequelConstructs167);
							prequelConstruct();
							state._fsp--;

							}
							break;

						default :
							if ( cnt2 >= 1 ) break loop2;
							EarlyExitException eee = new EarlyExitException(2, input);
							throw eee;
						}
						cnt2++;
					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:369:2: 
					{
					}
					break;

			}

				exitPrequelConstructs(((GrammarAST)retval.start));

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
	// $ANTLR end "prequelConstructs"


	public static class prequelConstruct_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "prequelConstruct"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:371:1: prequelConstruct : ( optionsSpec | delegateGrammars | tokensSpec | channelsSpec | action );
	public final GrammarTreeVisitor.prequelConstruct_return prequelConstruct() throws RecognitionException {
		GrammarTreeVisitor.prequelConstruct_return retval = new GrammarTreeVisitor.prequelConstruct_return();
		retval.start = input.LT(1);


			enterPrequelConstructs(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:378:2: ( optionsSpec | delegateGrammars | tokensSpec | channelsSpec | action )
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
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:378:6: optionsSpec
					{
					pushFollow(FOLLOW_optionsSpec_in_prequelConstruct194);
					optionsSpec();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:379:9: delegateGrammars
					{
					pushFollow(FOLLOW_delegateGrammars_in_prequelConstruct204);
					delegateGrammars();
					state._fsp--;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:380:9: tokensSpec
					{
					pushFollow(FOLLOW_tokensSpec_in_prequelConstruct214);
					tokensSpec();
					state._fsp--;

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:381:9: channelsSpec
					{
					pushFollow(FOLLOW_channelsSpec_in_prequelConstruct224);
					channelsSpec();
					state._fsp--;

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:382:9: action
					{
					pushFollow(FOLLOW_action_in_prequelConstruct234);
					action();
					state._fsp--;

					}
					break;

			}

				exitPrequelConstructs(((GrammarAST)retval.start));

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
	// $ANTLR end "prequelConstruct"


	public static class optionsSpec_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "optionsSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:385:1: optionsSpec : ^( OPTIONS ( option )* ) ;
	public final GrammarTreeVisitor.optionsSpec_return optionsSpec() throws RecognitionException {
		GrammarTreeVisitor.optionsSpec_return retval = new GrammarTreeVisitor.optionsSpec_return();
		retval.start = input.LT(1);


			enterOptionsSpec(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:392:2: ( ^( OPTIONS ( option )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:392:4: ^( OPTIONS ( option )* )
			{
			match(input,OPTIONS,FOLLOW_OPTIONS_in_optionsSpec259); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:392:14: ( option )*
				loop5:
				while (true) {
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0==ASSIGN) ) {
						alt5=1;
					}

					switch (alt5) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:392:14: option
						{
						pushFollow(FOLLOW_option_in_optionsSpec261);
						option();
						state._fsp--;

						}
						break;

					default :
						break loop5;
					}
				}

				match(input, Token.UP, null); 
			}

			}


				exitOptionsSpec(((GrammarAST)retval.start));

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
	// $ANTLR end "optionsSpec"


	public static class option_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "option"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:395:1: option : ^(a= ASSIGN ID v= optionValue ) ;
	public final GrammarTreeVisitor.option_return option() throws RecognitionException {
		GrammarTreeVisitor.option_return retval = new GrammarTreeVisitor.option_return();
		retval.start = input.LT(1);

		GrammarAST a=null;
		GrammarAST ID4=null;
		TreeRuleReturnScope v =null;


			enterOption(((GrammarAST)retval.start));
			boolean rule = inContext("RULE ...");
			boolean block = inContext("BLOCK ...");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:404:5: ( ^(a= ASSIGN ID v= optionValue ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:404:9: ^(a= ASSIGN ID v= optionValue )
			{
			a=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_option295); 
			match(input, Token.DOWN, null); 
			ID4=(GrammarAST)match(input,ID,FOLLOW_ID_in_option297); 
			pushFollow(FOLLOW_optionValue_in_option301);
			v=optionValue();
			state._fsp--;

			match(input, Token.UP, null); 


			    	if ( block ) blockOption(ID4, (v!=null?((GrammarAST)v.start):null)); // most specific first
			    	else if ( rule ) ruleOption(ID4, (v!=null?((GrammarAST)v.start):null));
			    	else grammarOption(ID4, (v!=null?((GrammarAST)v.start):null));
			    	
			}


				exitOption(((GrammarAST)retval.start));

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
	// $ANTLR end "option"


	public static class optionValue_return extends TreeRuleReturnScope {
		public String v;
	};


	// $ANTLR start "optionValue"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:412:1: optionValue returns [String v] : ( ID | STRING_LITERAL | INT );
	public final GrammarTreeVisitor.optionValue_return optionValue() throws RecognitionException {
		GrammarTreeVisitor.optionValue_return retval = new GrammarTreeVisitor.optionValue_return();
		retval.start = input.LT(1);


			enterOptionValue(((GrammarAST)retval.start));
			retval.v = ((GrammarAST)retval.start).token.getText();

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:420:5: ( ID | STRING_LITERAL | INT )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:
			{
			if ( input.LA(1)==ID||input.LA(1)==INT||input.LA(1)==STRING_LITERAL ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}


				exitOptionValue(((GrammarAST)retval.start));

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
	// $ANTLR end "optionValue"


	public static class delegateGrammars_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "delegateGrammars"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:425:1: delegateGrammars : ^( IMPORT ( delegateGrammar )+ ) ;
	public final GrammarTreeVisitor.delegateGrammars_return delegateGrammars() throws RecognitionException {
		GrammarTreeVisitor.delegateGrammars_return retval = new GrammarTreeVisitor.delegateGrammars_return();
		retval.start = input.LT(1);


			enterDelegateGrammars(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:432:2: ( ^( IMPORT ( delegateGrammar )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:432:6: ^( IMPORT ( delegateGrammar )+ )
			{
			match(input,IMPORT,FOLLOW_IMPORT_in_delegateGrammars389); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:432:15: ( delegateGrammar )+
			int cnt6=0;
			loop6:
			while (true) {
				int alt6=2;
				int LA6_0 = input.LA(1);
				if ( (LA6_0==ASSIGN||LA6_0==ID) ) {
					alt6=1;
				}

				switch (alt6) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:432:15: delegateGrammar
					{
					pushFollow(FOLLOW_delegateGrammar_in_delegateGrammars391);
					delegateGrammar();
					state._fsp--;

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

			}


				exitDelegateGrammars(((GrammarAST)retval.start));

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
	// $ANTLR end "delegateGrammars"


	public static class delegateGrammar_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "delegateGrammar"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:435:1: delegateGrammar : ( ^( ASSIGN label= ID id= ID ) |id= ID );
	public final GrammarTreeVisitor.delegateGrammar_return delegateGrammar() throws RecognitionException {
		GrammarTreeVisitor.delegateGrammar_return retval = new GrammarTreeVisitor.delegateGrammar_return();
		retval.start = input.LT(1);

		GrammarAST label=null;
		GrammarAST id=null;


			enterDelegateGrammar(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:442:5: ( ^( ASSIGN label= ID id= ID ) |id= ID )
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
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:442:9: ^( ASSIGN label= ID id= ID )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_delegateGrammar420); 
					match(input, Token.DOWN, null); 
					label=(GrammarAST)match(input,ID,FOLLOW_ID_in_delegateGrammar424); 
					id=(GrammarAST)match(input,ID,FOLLOW_ID_in_delegateGrammar428); 
					match(input, Token.UP, null); 

					importGrammar(label, id);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:443:9: id= ID
					{
					id=(GrammarAST)match(input,ID,FOLLOW_ID_in_delegateGrammar443); 
					importGrammar(null, id);
					}
					break;

			}

				exitDelegateGrammar(((GrammarAST)retval.start));

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
	// $ANTLR end "delegateGrammar"


	public static class tokensSpec_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "tokensSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:446:1: tokensSpec : ^( TOKENS_SPEC ( tokenSpec )+ ) ;
	public final GrammarTreeVisitor.tokensSpec_return tokensSpec() throws RecognitionException {
		GrammarTreeVisitor.tokensSpec_return retval = new GrammarTreeVisitor.tokensSpec_return();
		retval.start = input.LT(1);


			enterTokensSpec(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:453:2: ( ^( TOKENS_SPEC ( tokenSpec )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:453:6: ^( TOKENS_SPEC ( tokenSpec )+ )
			{
			match(input,TOKENS_SPEC,FOLLOW_TOKENS_SPEC_in_tokensSpec477); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:453:20: ( tokenSpec )+
			int cnt8=0;
			loop8:
			while (true) {
				int alt8=2;
				int LA8_0 = input.LA(1);
				if ( (LA8_0==ID) ) {
					alt8=1;
				}

				switch (alt8) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:453:20: tokenSpec
					{
					pushFollow(FOLLOW_tokenSpec_in_tokensSpec479);
					tokenSpec();
					state._fsp--;

					}
					break;

				default :
					if ( cnt8 >= 1 ) break loop8;
					EarlyExitException eee = new EarlyExitException(8, input);
					throw eee;
				}
				cnt8++;
			}

			match(input, Token.UP, null); 

			}


				exitTokensSpec(((GrammarAST)retval.start));

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
	// $ANTLR end "tokensSpec"


	public static class tokenSpec_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "tokenSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:456:1: tokenSpec : ID ;
	public final GrammarTreeVisitor.tokenSpec_return tokenSpec() throws RecognitionException {
		GrammarTreeVisitor.tokenSpec_return retval = new GrammarTreeVisitor.tokenSpec_return();
		retval.start = input.LT(1);

		GrammarAST ID5=null;


			enterTokenSpec(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:463:2: ( ID )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:463:4: ID
			{
			ID5=(GrammarAST)match(input,ID,FOLLOW_ID_in_tokenSpec502); 
			defineToken(ID5);
			}


				exitTokenSpec(((GrammarAST)retval.start));

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
	// $ANTLR end "tokenSpec"


	public static class channelsSpec_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "channelsSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:466:1: channelsSpec : ^( CHANNELS ( channelSpec )+ ) ;
	public final GrammarTreeVisitor.channelsSpec_return channelsSpec() throws RecognitionException {
		GrammarTreeVisitor.channelsSpec_return retval = new GrammarTreeVisitor.channelsSpec_return();
		retval.start = input.LT(1);


			enterChannelsSpec(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:473:2: ( ^( CHANNELS ( channelSpec )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:473:6: ^( CHANNELS ( channelSpec )+ )
			{
			match(input,CHANNELS,FOLLOW_CHANNELS_in_channelsSpec532); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:473:17: ( channelSpec )+
			int cnt9=0;
			loop9:
			while (true) {
				int alt9=2;
				int LA9_0 = input.LA(1);
				if ( (LA9_0==ID) ) {
					alt9=1;
				}

				switch (alt9) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:473:17: channelSpec
					{
					pushFollow(FOLLOW_channelSpec_in_channelsSpec534);
					channelSpec();
					state._fsp--;

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

			}


				exitChannelsSpec(((GrammarAST)retval.start));

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
	// $ANTLR end "channelsSpec"


	public static class channelSpec_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "channelSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:476:1: channelSpec : ID ;
	public final GrammarTreeVisitor.channelSpec_return channelSpec() throws RecognitionException {
		GrammarTreeVisitor.channelSpec_return retval = new GrammarTreeVisitor.channelSpec_return();
		retval.start = input.LT(1);

		GrammarAST ID6=null;


			enterChannelSpec(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:483:2: ( ID )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:483:4: ID
			{
			ID6=(GrammarAST)match(input,ID,FOLLOW_ID_in_channelSpec557); 
			defineChannel(ID6);
			}


				exitChannelSpec(((GrammarAST)retval.start));

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
	// $ANTLR end "channelSpec"


	public static class action_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "action"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:486:1: action : ^( AT (sc= ID )? name= ID ACTION ) ;
	public final GrammarTreeVisitor.action_return action() throws RecognitionException {
		GrammarTreeVisitor.action_return retval = new GrammarTreeVisitor.action_return();
		retval.start = input.LT(1);

		GrammarAST sc=null;
		GrammarAST name=null;
		GrammarAST ACTION7=null;


			enterAction(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:493:2: ( ^( AT (sc= ID )? name= ID ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:493:4: ^( AT (sc= ID )? name= ID ACTION )
			{
			match(input,AT,FOLLOW_AT_in_action585); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:493:11: (sc= ID )?
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
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:493:11: sc= ID
					{
					sc=(GrammarAST)match(input,ID,FOLLOW_ID_in_action589); 
					}
					break;

			}

			name=(GrammarAST)match(input,ID,FOLLOW_ID_in_action594); 
			ACTION7=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_action596); 
			match(input, Token.UP, null); 

			globalNamedAction(sc, name, (ActionAST)ACTION7);
			}


				exitAction(((GrammarAST)retval.start));

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
	// $ANTLR end "action"


	public static class rules_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "rules"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:496:1: rules : ^( RULES ( rule | lexerRule )* ) ;
	public final GrammarTreeVisitor.rules_return rules() throws RecognitionException {
		GrammarTreeVisitor.rules_return retval = new GrammarTreeVisitor.rules_return();
		retval.start = input.LT(1);

		GrammarAST RULES8=null;


			enterRules(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:503:5: ( ^( RULES ( rule | lexerRule )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:503:7: ^( RULES ( rule | lexerRule )* )
			{
			RULES8=(GrammarAST)match(input,RULES,FOLLOW_RULES_in_rules624); 
			discoverRules(RULES8);
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:503:40: ( rule | lexerRule )*
				loop11:
				while (true) {
					int alt11=3;
					int LA11_0 = input.LA(1);
					if ( (LA11_0==RULE) ) {
						int LA11_2 = input.LA(2);
						if ( (LA11_2==DOWN) ) {
							int LA11_3 = input.LA(3);
							if ( (LA11_3==RULE_REF) ) {
								alt11=1;
							}
							else if ( (LA11_3==TOKEN_REF) ) {
								alt11=2;
							}

						}

					}

					switch (alt11) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:503:41: rule
						{
						pushFollow(FOLLOW_rule_in_rules629);
						rule();
						state._fsp--;

						}
						break;
					case 2 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:503:46: lexerRule
						{
						pushFollow(FOLLOW_lexerRule_in_rules631);
						lexerRule();
						state._fsp--;

						}
						break;

					default :
						break loop11;
					}
				}

				finishRules(RULES8);
				match(input, Token.UP, null); 
			}

			}


				exitRules(((GrammarAST)retval.start));

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
	// $ANTLR end "rules"


	public static class mode_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "mode"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:506:1: mode : ^( MODE ID ( lexerRule )* ) ;
	public final GrammarTreeVisitor.mode_return mode() throws RecognitionException {
		GrammarTreeVisitor.mode_return retval = new GrammarTreeVisitor.mode_return();
		retval.start = input.LT(1);

		GrammarAST ID9=null;
		GrammarAST MODE10=null;


			enterMode(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:513:2: ( ^( MODE ID ( lexerRule )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:513:4: ^( MODE ID ( lexerRule )* )
			{
			MODE10=(GrammarAST)match(input,MODE,FOLLOW_MODE_in_mode662); 
			match(input, Token.DOWN, null); 
			ID9=(GrammarAST)match(input,ID,FOLLOW_ID_in_mode664); 
			currentModeName=(ID9!=null?ID9.getText():null); modeDef(MODE10, ID9);
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:513:64: ( lexerRule )*
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==RULE) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:513:64: lexerRule
					{
					pushFollow(FOLLOW_lexerRule_in_mode668);
					lexerRule();
					state._fsp--;

					}
					break;

				default :
					break loop12;
				}
			}

			match(input, Token.UP, null); 

			}


				exitMode(((GrammarAST)retval.start));

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
	// $ANTLR end "mode"


	public static class lexerRule_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerRule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:516:1: lexerRule : ^( RULE TOKEN_REF ( ^( RULEMODIFIERS m= FRAGMENT ) )? lexerRuleBlock ) ;
	public final GrammarTreeVisitor.lexerRule_return lexerRule() throws RecognitionException {
		GrammarTreeVisitor.lexerRule_return retval = new GrammarTreeVisitor.lexerRule_return();
		retval.start = input.LT(1);

		GrammarAST m=null;
		GrammarAST TOKEN_REF11=null;
		GrammarAST RULE12=null;
		TreeRuleReturnScope lexerRuleBlock13 =null;


			enterLexerRule(((GrammarAST)retval.start));
			List<GrammarAST> mods = new ArrayList<GrammarAST>();
			currentOuterAltNumber=0;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:525:2: ( ^( RULE TOKEN_REF ( ^( RULEMODIFIERS m= FRAGMENT ) )? lexerRuleBlock ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:525:4: ^( RULE TOKEN_REF ( ^( RULEMODIFIERS m= FRAGMENT ) )? lexerRuleBlock )
			{
			RULE12=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_lexerRule694); 
			match(input, Token.DOWN, null); 
			TOKEN_REF11=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_lexerRule696); 
			currentRuleName=(TOKEN_REF11!=null?TOKEN_REF11.getText():null); currentRuleAST=RULE12;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:527:4: ( ^( RULEMODIFIERS m= FRAGMENT ) )?
			int alt13=2;
			int LA13_0 = input.LA(1);
			if ( (LA13_0==RULEMODIFIERS) ) {
				alt13=1;
			}
			switch (alt13) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:527:5: ^( RULEMODIFIERS m= FRAGMENT )
					{
					match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_lexerRule708); 
					match(input, Token.DOWN, null); 
					m=(GrammarAST)match(input,FRAGMENT,FOLLOW_FRAGMENT_in_lexerRule712); 
					mods.add(m);
					match(input, Token.UP, null); 

					}
					break;

			}

			discoverLexerRule((RuleAST)RULE12, TOKEN_REF11, mods, (GrammarAST)input.LT(1));
			pushFollow(FOLLOW_lexerRuleBlock_in_lexerRule737);
			lexerRuleBlock13=lexerRuleBlock();
			state._fsp--;


			      		finishLexerRule((RuleAST)RULE12, TOKEN_REF11, (lexerRuleBlock13!=null?((GrammarAST)lexerRuleBlock13.start):null));
			      		currentRuleName=null; currentRuleAST=null;
			      		
			match(input, Token.UP, null); 

			}


				exitLexerRule(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerRule"


	public static class rule_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "rule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:537:1: rule : ^( RULE RULE_REF ( ^( RULEMODIFIERS (m= ruleModifier )+ ) )? ( ARG_ACTION )? (ret= ruleReturns )? (thr= throwsSpec )? (loc= locals )? (opts= optionsSpec |a= ruleAction )* ruleBlock exceptionGroup ) ;
	public final GrammarTreeVisitor.rule_return rule() throws RecognitionException {
		GrammarTreeVisitor.rule_return retval = new GrammarTreeVisitor.rule_return();
		retval.start = input.LT(1);

		GrammarAST RULE_REF14=null;
		GrammarAST RULE15=null;
		GrammarAST ARG_ACTION16=null;
		TreeRuleReturnScope m =null;
		TreeRuleReturnScope ret =null;
		TreeRuleReturnScope thr =null;
		TreeRuleReturnScope loc =null;
		TreeRuleReturnScope opts =null;
		TreeRuleReturnScope a =null;
		TreeRuleReturnScope ruleBlock17 =null;


			enterRule(((GrammarAST)retval.start));
			List<GrammarAST> mods = new ArrayList<GrammarAST>();
			List<GrammarAST> actions = new ArrayList<GrammarAST>(); // track roots
			currentOuterAltNumber=0;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:547:2: ( ^( RULE RULE_REF ( ^( RULEMODIFIERS (m= ruleModifier )+ ) )? ( ARG_ACTION )? (ret= ruleReturns )? (thr= throwsSpec )? (loc= locals )? (opts= optionsSpec |a= ruleAction )* ruleBlock exceptionGroup ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:547:6: ^( RULE RULE_REF ( ^( RULEMODIFIERS (m= ruleModifier )+ ) )? ( ARG_ACTION )? (ret= ruleReturns )? (thr= throwsSpec )? (loc= locals )? (opts= optionsSpec |a= ruleAction )* ruleBlock exceptionGroup )
			{
			RULE15=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_rule782); 
			match(input, Token.DOWN, null); 
			RULE_REF14=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_rule784); 
			currentRuleName=(RULE_REF14!=null?RULE_REF14.getText():null); currentRuleAST=RULE15;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:548:4: ( ^( RULEMODIFIERS (m= ruleModifier )+ ) )?
			int alt15=2;
			int LA15_0 = input.LA(1);
			if ( (LA15_0==RULEMODIFIERS) ) {
				alt15=1;
			}
			switch (alt15) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:548:5: ^( RULEMODIFIERS (m= ruleModifier )+ )
					{
					match(input,RULEMODIFIERS,FOLLOW_RULEMODIFIERS_in_rule793); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:548:21: (m= ruleModifier )+
					int cnt14=0;
					loop14:
					while (true) {
						int alt14=2;
						int LA14_0 = input.LA(1);
						if ( (LA14_0==FRAGMENT||(LA14_0 >= PRIVATE && LA14_0 <= PUBLIC)) ) {
							alt14=1;
						}

						switch (alt14) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:548:22: m= ruleModifier
							{
							pushFollow(FOLLOW_ruleModifier_in_rule798);
							m=ruleModifier();
							state._fsp--;

							mods.add((m!=null?((GrammarAST)m.start):null));
							}
							break;

						default :
							if ( cnt14 >= 1 ) break loop14;
							EarlyExitException eee = new EarlyExitException(14, input);
							throw eee;
						}
						cnt14++;
					}

					match(input, Token.UP, null); 

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:549:4: ( ARG_ACTION )?
			int alt16=2;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==ARG_ACTION) ) {
				alt16=1;
			}
			switch (alt16) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:549:4: ARG_ACTION
					{
					ARG_ACTION16=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_rule809); 
					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:550:12: (ret= ruleReturns )?
			int alt17=2;
			int LA17_0 = input.LA(1);
			if ( (LA17_0==RETURNS) ) {
				alt17=1;
			}
			switch (alt17) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:550:12: ret= ruleReturns
					{
					pushFollow(FOLLOW_ruleReturns_in_rule822);
					ret=ruleReturns();
					state._fsp--;

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:551:12: (thr= throwsSpec )?
			int alt18=2;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==THROWS) ) {
				alt18=1;
			}
			switch (alt18) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:551:12: thr= throwsSpec
					{
					pushFollow(FOLLOW_throwsSpec_in_rule835);
					thr=throwsSpec();
					state._fsp--;

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:552:12: (loc= locals )?
			int alt19=2;
			int LA19_0 = input.LA(1);
			if ( (LA19_0==LOCALS) ) {
				alt19=1;
			}
			switch (alt19) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:552:12: loc= locals
					{
					pushFollow(FOLLOW_locals_in_rule848);
					loc=locals();
					state._fsp--;

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:553:9: (opts= optionsSpec |a= ruleAction )*
			loop20:
			while (true) {
				int alt20=3;
				int LA20_0 = input.LA(1);
				if ( (LA20_0==OPTIONS) ) {
					alt20=1;
				}
				else if ( (LA20_0==AT) ) {
					alt20=2;
				}

				switch (alt20) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:553:11: opts= optionsSpec
					{
					pushFollow(FOLLOW_optionsSpec_in_rule863);
					opts=optionsSpec();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:554:11: a= ruleAction
					{
					pushFollow(FOLLOW_ruleAction_in_rule877);
					a=ruleAction();
					state._fsp--;

					actions.add((a!=null?((GrammarAST)a.start):null));
					}
					break;

				default :
					break loop20;
				}
			}

			discoverRule((RuleAST)RULE15, RULE_REF14, mods, (ActionAST)ARG_ACTION16,
			      					  (ret!=null?((GrammarAST)ret.start):null)!=null?(ActionAST)(ret!=null?((GrammarAST)ret.start):null).getChild(0):null,
			      					  (thr!=null?((GrammarAST)thr.start):null), (opts!=null?((GrammarAST)opts.start):null),
			      					  (loc!=null?((GrammarAST)loc.start):null)!=null?(ActionAST)(loc!=null?((GrammarAST)loc.start):null).getChild(0):null,
			      					  actions, (GrammarAST)input.LT(1));
			pushFollow(FOLLOW_ruleBlock_in_rule908);
			ruleBlock17=ruleBlock();
			state._fsp--;

			pushFollow(FOLLOW_exceptionGroup_in_rule910);
			exceptionGroup();
			state._fsp--;

			finishRule((RuleAST)RULE15, RULE_REF14, (ruleBlock17!=null?((GrammarAST)ruleBlock17.start):null)); currentRuleName=null; currentRuleAST=null;
			match(input, Token.UP, null); 

			}


				exitRule(((GrammarAST)retval.start));

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
	// $ANTLR end "rule"


	public static class exceptionGroup_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "exceptionGroup"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:566:1: exceptionGroup : ( exceptionHandler )* ( finallyClause )? ;
	public final GrammarTreeVisitor.exceptionGroup_return exceptionGroup() throws RecognitionException {
		GrammarTreeVisitor.exceptionGroup_return retval = new GrammarTreeVisitor.exceptionGroup_return();
		retval.start = input.LT(1);


			enterExceptionGroup(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:573:5: ( ( exceptionHandler )* ( finallyClause )? )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:573:7: ( exceptionHandler )* ( finallyClause )?
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:573:7: ( exceptionHandler )*
			loop21:
			while (true) {
				int alt21=2;
				int LA21_0 = input.LA(1);
				if ( (LA21_0==CATCH) ) {
					alt21=1;
				}

				switch (alt21) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:573:7: exceptionHandler
					{
					pushFollow(FOLLOW_exceptionHandler_in_exceptionGroup957);
					exceptionHandler();
					state._fsp--;

					}
					break;

				default :
					break loop21;
				}
			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:573:25: ( finallyClause )?
			int alt22=2;
			int LA22_0 = input.LA(1);
			if ( (LA22_0==FINALLY) ) {
				alt22=1;
			}
			switch (alt22) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:573:25: finallyClause
					{
					pushFollow(FOLLOW_finallyClause_in_exceptionGroup960);
					finallyClause();
					state._fsp--;

					}
					break;

			}

			}


				exitExceptionGroup(((GrammarAST)retval.start));

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
	// $ANTLR end "exceptionGroup"


	public static class exceptionHandler_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "exceptionHandler"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:576:1: exceptionHandler : ^( CATCH ARG_ACTION ACTION ) ;
	public final GrammarTreeVisitor.exceptionHandler_return exceptionHandler() throws RecognitionException {
		GrammarTreeVisitor.exceptionHandler_return retval = new GrammarTreeVisitor.exceptionHandler_return();
		retval.start = input.LT(1);

		GrammarAST ARG_ACTION18=null;
		GrammarAST ACTION19=null;


			enterExceptionHandler(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:583:2: ( ^( CATCH ARG_ACTION ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:583:4: ^( CATCH ARG_ACTION ACTION )
			{
			match(input,CATCH,FOLLOW_CATCH_in_exceptionHandler986); 
			match(input, Token.DOWN, null); 
			ARG_ACTION18=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_exceptionHandler988); 
			ACTION19=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_exceptionHandler990); 
			match(input, Token.UP, null); 

			ruleCatch(ARG_ACTION18, (ActionAST)ACTION19);
			}


				exitExceptionHandler(((GrammarAST)retval.start));

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
	// $ANTLR end "exceptionHandler"


	public static class finallyClause_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "finallyClause"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:586:1: finallyClause : ^( FINALLY ACTION ) ;
	public final GrammarTreeVisitor.finallyClause_return finallyClause() throws RecognitionException {
		GrammarTreeVisitor.finallyClause_return retval = new GrammarTreeVisitor.finallyClause_return();
		retval.start = input.LT(1);

		GrammarAST ACTION20=null;


			enterFinallyClause(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:593:2: ( ^( FINALLY ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:593:4: ^( FINALLY ACTION )
			{
			match(input,FINALLY,FOLLOW_FINALLY_in_finallyClause1015); 
			match(input, Token.DOWN, null); 
			ACTION20=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_finallyClause1017); 
			match(input, Token.UP, null); 

			finallyAction((ActionAST)ACTION20);
			}


				exitFinallyClause(((GrammarAST)retval.start));

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
	// $ANTLR end "finallyClause"


	public static class locals_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "locals"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:596:1: locals : ^( LOCALS ARG_ACTION ) ;
	public final GrammarTreeVisitor.locals_return locals() throws RecognitionException {
		GrammarTreeVisitor.locals_return retval = new GrammarTreeVisitor.locals_return();
		retval.start = input.LT(1);


			enterLocals(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:603:2: ( ^( LOCALS ARG_ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:603:4: ^( LOCALS ARG_ACTION )
			{
			match(input,LOCALS,FOLLOW_LOCALS_in_locals1045); 
			match(input, Token.DOWN, null); 
			match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_locals1047); 
			match(input, Token.UP, null); 

			}


				exitLocals(((GrammarAST)retval.start));

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
	// $ANTLR end "locals"


	public static class ruleReturns_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "ruleReturns"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:606:1: ruleReturns : ^( RETURNS ARG_ACTION ) ;
	public final GrammarTreeVisitor.ruleReturns_return ruleReturns() throws RecognitionException {
		GrammarTreeVisitor.ruleReturns_return retval = new GrammarTreeVisitor.ruleReturns_return();
		retval.start = input.LT(1);


			enterRuleReturns(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:613:2: ( ^( RETURNS ARG_ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:613:4: ^( RETURNS ARG_ACTION )
			{
			match(input,RETURNS,FOLLOW_RETURNS_in_ruleReturns1070); 
			match(input, Token.DOWN, null); 
			match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleReturns1072); 
			match(input, Token.UP, null); 

			}


				exitRuleReturns(((GrammarAST)retval.start));

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
	// $ANTLR end "ruleReturns"


	public static class throwsSpec_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "throwsSpec"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:616:1: throwsSpec : ^( THROWS ( ID )+ ) ;
	public final GrammarTreeVisitor.throwsSpec_return throwsSpec() throws RecognitionException {
		GrammarTreeVisitor.throwsSpec_return retval = new GrammarTreeVisitor.throwsSpec_return();
		retval.start = input.LT(1);


			enterThrowsSpec(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:623:5: ( ^( THROWS ( ID )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:623:7: ^( THROWS ( ID )+ )
			{
			match(input,THROWS,FOLLOW_THROWS_in_throwsSpec1098); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:623:16: ( ID )+
			int cnt23=0;
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( (LA23_0==ID) ) {
					alt23=1;
				}

				switch (alt23) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:623:16: ID
					{
					match(input,ID,FOLLOW_ID_in_throwsSpec1100); 
					}
					break;

				default :
					if ( cnt23 >= 1 ) break loop23;
					EarlyExitException eee = new EarlyExitException(23, input);
					throw eee;
				}
				cnt23++;
			}

			match(input, Token.UP, null); 

			}


				exitThrowsSpec(((GrammarAST)retval.start));

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
	// $ANTLR end "throwsSpec"


	public static class ruleAction_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "ruleAction"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:626:1: ruleAction : ^( AT ID ACTION ) ;
	public final GrammarTreeVisitor.ruleAction_return ruleAction() throws RecognitionException {
		GrammarTreeVisitor.ruleAction_return retval = new GrammarTreeVisitor.ruleAction_return();
		retval.start = input.LT(1);


			enterRuleAction(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:633:2: ( ^( AT ID ACTION ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:633:4: ^( AT ID ACTION )
			{
			match(input,AT,FOLLOW_AT_in_ruleAction1127); 
			match(input, Token.DOWN, null); 
			match(input,ID,FOLLOW_ID_in_ruleAction1129); 
			match(input,ACTION,FOLLOW_ACTION_in_ruleAction1131); 
			match(input, Token.UP, null); 

			}


				exitRuleAction(((GrammarAST)retval.start));

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
	// $ANTLR end "ruleAction"


	public static class ruleModifier_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "ruleModifier"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:636:1: ruleModifier : ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT );
	public final GrammarTreeVisitor.ruleModifier_return ruleModifier() throws RecognitionException {
		GrammarTreeVisitor.ruleModifier_return retval = new GrammarTreeVisitor.ruleModifier_return();
		retval.start = input.LT(1);


			enterRuleModifier(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:643:5: ( PUBLIC | PRIVATE | PROTECTED | FRAGMENT )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:
			{
			if ( input.LA(1)==FRAGMENT||(input.LA(1) >= PRIVATE && input.LA(1) <= PUBLIC) ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}


				exitRuleModifier(((GrammarAST)retval.start));

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
	// $ANTLR end "ruleModifier"


	public static class lexerRuleBlock_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerRuleBlock"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:649:1: lexerRuleBlock : ^( BLOCK ( lexerOuterAlternative )+ ) ;
	public final GrammarTreeVisitor.lexerRuleBlock_return lexerRuleBlock() throws RecognitionException {
		GrammarTreeVisitor.lexerRuleBlock_return retval = new GrammarTreeVisitor.lexerRuleBlock_return();
		retval.start = input.LT(1);


			enterLexerRuleBlock(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:656:5: ( ^( BLOCK ( lexerOuterAlternative )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:656:7: ^( BLOCK ( lexerOuterAlternative )+ )
			{
			match(input,BLOCK,FOLLOW_BLOCK_in_lexerRuleBlock1209); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:657:7: ( lexerOuterAlternative )+
			int cnt24=0;
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0==ALT||LA24_0==LEXER_ALT_ACTION) ) {
					alt24=1;
				}

				switch (alt24) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:657:9: lexerOuterAlternative
					{

					    			currentOuterAltRoot = (GrammarAST)input.LT(1);
									currentOuterAltNumber++;
									
					pushFollow(FOLLOW_lexerOuterAlternative_in_lexerRuleBlock1228);
					lexerOuterAlternative();
					state._fsp--;

					}
					break;

				default :
					if ( cnt24 >= 1 ) break loop24;
					EarlyExitException eee = new EarlyExitException(24, input);
					throw eee;
				}
				cnt24++;
			}

			match(input, Token.UP, null); 

			}


				exitLexerRuleBlock(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerRuleBlock"


	public static class ruleBlock_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "ruleBlock"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:666:1: ruleBlock : ^( BLOCK ( outerAlternative )+ ) ;
	public final GrammarTreeVisitor.ruleBlock_return ruleBlock() throws RecognitionException {
		GrammarTreeVisitor.ruleBlock_return retval = new GrammarTreeVisitor.ruleBlock_return();
		retval.start = input.LT(1);


			enterRuleBlock(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:673:5: ( ^( BLOCK ( outerAlternative )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:673:7: ^( BLOCK ( outerAlternative )+ )
			{
			match(input,BLOCK,FOLLOW_BLOCK_in_ruleBlock1273); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:674:7: ( outerAlternative )+
			int cnt25=0;
			loop25:
			while (true) {
				int alt25=2;
				int LA25_0 = input.LA(1);
				if ( (LA25_0==ALT) ) {
					alt25=1;
				}

				switch (alt25) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:674:9: outerAlternative
					{

					    			currentOuterAltRoot = (GrammarAST)input.LT(1);
									currentOuterAltNumber++;
									
					pushFollow(FOLLOW_outerAlternative_in_ruleBlock1292);
					outerAlternative();
					state._fsp--;

					}
					break;

				default :
					if ( cnt25 >= 1 ) break loop25;
					EarlyExitException eee = new EarlyExitException(25, input);
					throw eee;
				}
				cnt25++;
			}

			match(input, Token.UP, null); 

			}


				exitRuleBlock(((GrammarAST)retval.start));

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
	// $ANTLR end "ruleBlock"


	public static class lexerOuterAlternative_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerOuterAlternative"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:683:1: lexerOuterAlternative : lexerAlternative ;
	public final GrammarTreeVisitor.lexerOuterAlternative_return lexerOuterAlternative() throws RecognitionException {
		GrammarTreeVisitor.lexerOuterAlternative_return retval = new GrammarTreeVisitor.lexerOuterAlternative_return();
		retval.start = input.LT(1);


			enterLexerOuterAlternative((AltAST)((GrammarAST)retval.start));
			discoverOuterAlt((AltAST)((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:692:2: ( lexerAlternative )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:692:4: lexerAlternative
			{
			pushFollow(FOLLOW_lexerAlternative_in_lexerOuterAlternative1332);
			lexerAlternative();
			state._fsp--;

			}


				finishOuterAlt((AltAST)((GrammarAST)retval.start));
				exitLexerOuterAlternative((AltAST)((GrammarAST)retval.start));

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
	// $ANTLR end "lexerOuterAlternative"


	public static class outerAlternative_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "outerAlternative"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:696:1: outerAlternative : alternative ;
	public final GrammarTreeVisitor.outerAlternative_return outerAlternative() throws RecognitionException {
		GrammarTreeVisitor.outerAlternative_return retval = new GrammarTreeVisitor.outerAlternative_return();
		retval.start = input.LT(1);


			enterOuterAlternative((AltAST)((GrammarAST)retval.start));
			discoverOuterAlt((AltAST)((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:705:2: ( alternative )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:705:4: alternative
			{
			pushFollow(FOLLOW_alternative_in_outerAlternative1354);
			alternative();
			state._fsp--;

			}


				finishOuterAlt((AltAST)((GrammarAST)retval.start));
				exitOuterAlternative((AltAST)((GrammarAST)retval.start));

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
	// $ANTLR end "outerAlternative"


	public static class lexerAlternative_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerAlternative"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:708:1: lexerAlternative : ( ^( LEXER_ALT_ACTION lexerElements ( lexerCommand )+ ) | lexerElements );
	public final GrammarTreeVisitor.lexerAlternative_return lexerAlternative() throws RecognitionException {
		GrammarTreeVisitor.lexerAlternative_return retval = new GrammarTreeVisitor.lexerAlternative_return();
		retval.start = input.LT(1);


			enterLexerAlternative(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:715:2: ( ^( LEXER_ALT_ACTION lexerElements ( lexerCommand )+ ) | lexerElements )
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0==LEXER_ALT_ACTION) ) {
				alt27=1;
			}
			else if ( (LA27_0==ALT) ) {
				alt27=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 27, 0, input);
				throw nvae;
			}

			switch (alt27) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:715:4: ^( LEXER_ALT_ACTION lexerElements ( lexerCommand )+ )
					{
					match(input,LEXER_ALT_ACTION,FOLLOW_LEXER_ALT_ACTION_in_lexerAlternative1376); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_lexerElements_in_lexerAlternative1378);
					lexerElements();
					state._fsp--;

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:715:37: ( lexerCommand )+
					int cnt26=0;
					loop26:
					while (true) {
						int alt26=2;
						int LA26_0 = input.LA(1);
						if ( (LA26_0==ID||LA26_0==LEXER_ACTION_CALL) ) {
							alt26=1;
						}

						switch (alt26) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:715:37: lexerCommand
							{
							pushFollow(FOLLOW_lexerCommand_in_lexerAlternative1380);
							lexerCommand();
							state._fsp--;

							}
							break;

						default :
							if ( cnt26 >= 1 ) break loop26;
							EarlyExitException eee = new EarlyExitException(26, input);
							throw eee;
						}
						cnt26++;
					}

					match(input, Token.UP, null); 

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:716:9: lexerElements
					{
					pushFollow(FOLLOW_lexerElements_in_lexerAlternative1392);
					lexerElements();
					state._fsp--;

					}
					break;

			}

				exitLexerAlternative(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerAlternative"


	public static class lexerElements_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerElements"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:719:1: lexerElements : ^( ALT ( lexerElement )+ ) ;
	public final GrammarTreeVisitor.lexerElements_return lexerElements() throws RecognitionException {
		GrammarTreeVisitor.lexerElements_return retval = new GrammarTreeVisitor.lexerElements_return();
		retval.start = input.LT(1);


			enterLexerElements(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:726:5: ( ^( ALT ( lexerElement )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:726:7: ^( ALT ( lexerElement )+ )
			{
			match(input,ALT,FOLLOW_ALT_in_lexerElements1420); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:726:13: ( lexerElement )+
			int cnt28=0;
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( (LA28_0==ACTION||LA28_0==ASSIGN||LA28_0==LEXER_CHAR_SET||LA28_0==NOT||LA28_0==PLUS_ASSIGN||LA28_0==RANGE||LA28_0==RULE_REF||LA28_0==SEMPRED||LA28_0==STRING_LITERAL||LA28_0==TOKEN_REF||LA28_0==BLOCK||LA28_0==CLOSURE||LA28_0==EPSILON||(LA28_0 >= OPTIONAL && LA28_0 <= POSITIVE_CLOSURE)||LA28_0==SET||LA28_0==WILDCARD) ) {
					alt28=1;
				}

				switch (alt28) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:726:13: lexerElement
					{
					pushFollow(FOLLOW_lexerElement_in_lexerElements1422);
					lexerElement();
					state._fsp--;

					}
					break;

				default :
					if ( cnt28 >= 1 ) break loop28;
					EarlyExitException eee = new EarlyExitException(28, input);
					throw eee;
				}
				cnt28++;
			}

			match(input, Token.UP, null); 

			}


				exitLexerElements(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerElements"


	public static class lexerElement_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:729:1: lexerElement : ( labeledLexerElement | lexerAtom | lexerSubrule | ACTION | SEMPRED | ^( ACTION elementOptions ) | ^( SEMPRED elementOptions ) | EPSILON );
	public final GrammarTreeVisitor.lexerElement_return lexerElement() throws RecognitionException {
		GrammarTreeVisitor.lexerElement_return retval = new GrammarTreeVisitor.lexerElement_return();
		retval.start = input.LT(1);

		GrammarAST ACTION21=null;
		GrammarAST SEMPRED22=null;
		GrammarAST ACTION23=null;
		GrammarAST SEMPRED24=null;


			enterLexerElement(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:736:2: ( labeledLexerElement | lexerAtom | lexerSubrule | ACTION | SEMPRED | ^( ACTION elementOptions ) | ^( SEMPRED elementOptions ) | EPSILON )
			int alt29=8;
			switch ( input.LA(1) ) {
			case ASSIGN:
			case PLUS_ASSIGN:
				{
				alt29=1;
				}
				break;
			case LEXER_CHAR_SET:
			case NOT:
			case RANGE:
			case RULE_REF:
			case STRING_LITERAL:
			case TOKEN_REF:
			case SET:
			case WILDCARD:
				{
				alt29=2;
				}
				break;
			case BLOCK:
			case CLOSURE:
			case OPTIONAL:
			case POSITIVE_CLOSURE:
				{
				alt29=3;
				}
				break;
			case ACTION:
				{
				int LA29_4 = input.LA(2);
				if ( (LA29_4==DOWN) ) {
					alt29=6;
				}
				else if ( ((LA29_4 >= UP && LA29_4 <= ACTION)||LA29_4==ASSIGN||LA29_4==LEXER_CHAR_SET||LA29_4==NOT||LA29_4==PLUS_ASSIGN||LA29_4==RANGE||LA29_4==RULE_REF||LA29_4==SEMPRED||LA29_4==STRING_LITERAL||LA29_4==TOKEN_REF||LA29_4==BLOCK||LA29_4==CLOSURE||LA29_4==EPSILON||(LA29_4 >= OPTIONAL && LA29_4 <= POSITIVE_CLOSURE)||LA29_4==SET||LA29_4==WILDCARD) ) {
					alt29=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 29, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SEMPRED:
				{
				int LA29_5 = input.LA(2);
				if ( (LA29_5==DOWN) ) {
					alt29=7;
				}
				else if ( ((LA29_5 >= UP && LA29_5 <= ACTION)||LA29_5==ASSIGN||LA29_5==LEXER_CHAR_SET||LA29_5==NOT||LA29_5==PLUS_ASSIGN||LA29_5==RANGE||LA29_5==RULE_REF||LA29_5==SEMPRED||LA29_5==STRING_LITERAL||LA29_5==TOKEN_REF||LA29_5==BLOCK||LA29_5==CLOSURE||LA29_5==EPSILON||(LA29_5 >= OPTIONAL && LA29_5 <= POSITIVE_CLOSURE)||LA29_5==SET||LA29_5==WILDCARD) ) {
					alt29=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 29, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case EPSILON:
				{
				alt29=8;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 29, 0, input);
				throw nvae;
			}
			switch (alt29) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:736:4: labeledLexerElement
					{
					pushFollow(FOLLOW_labeledLexerElement_in_lexerElement1448);
					labeledLexerElement();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:737:4: lexerAtom
					{
					pushFollow(FOLLOW_lexerAtom_in_lexerElement1453);
					lexerAtom();
					state._fsp--;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:738:4: lexerSubrule
					{
					pushFollow(FOLLOW_lexerSubrule_in_lexerElement1458);
					lexerSubrule();
					state._fsp--;

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:739:6: ACTION
					{
					ACTION21=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_lexerElement1465); 
					actionInAlt((ActionAST)ACTION21);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:740:6: SEMPRED
					{
					SEMPRED22=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_lexerElement1479); 
					sempredInAlt((PredAST)SEMPRED22);
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:741:6: ^( ACTION elementOptions )
					{
					ACTION23=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_lexerElement1494); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_lexerElement1496);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					actionInAlt((ActionAST)ACTION23);
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:742:6: ^( SEMPRED elementOptions )
					{
					SEMPRED24=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_lexerElement1507); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_lexerElement1509);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					sempredInAlt((PredAST)SEMPRED24);
					}
					break;
				case 8 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:743:4: EPSILON
					{
					match(input,EPSILON,FOLLOW_EPSILON_in_lexerElement1517); 
					}
					break;

			}

				exitLexerElement(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerElement"


	public static class labeledLexerElement_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "labeledLexerElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:746:1: labeledLexerElement : ^( ( ASSIGN | PLUS_ASSIGN ) ID ( lexerAtom | block ) ) ;
	public final GrammarTreeVisitor.labeledLexerElement_return labeledLexerElement() throws RecognitionException {
		GrammarTreeVisitor.labeledLexerElement_return retval = new GrammarTreeVisitor.labeledLexerElement_return();
		retval.start = input.LT(1);


			enterLabeledLexerElement(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:753:5: ( ^( ( ASSIGN | PLUS_ASSIGN ) ID ( lexerAtom | block ) ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:753:9: ^( ( ASSIGN | PLUS_ASSIGN ) ID ( lexerAtom | block ) )
			{
			if ( input.LA(1)==ASSIGN||input.LA(1)==PLUS_ASSIGN ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			match(input, Token.DOWN, null); 
			match(input,ID,FOLLOW_ID_in_labeledLexerElement1550); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:753:35: ( lexerAtom | block )
			int alt30=2;
			int LA30_0 = input.LA(1);
			if ( (LA30_0==LEXER_CHAR_SET||LA30_0==NOT||LA30_0==RANGE||LA30_0==RULE_REF||LA30_0==STRING_LITERAL||LA30_0==TOKEN_REF||LA30_0==SET||LA30_0==WILDCARD) ) {
				alt30=1;
			}
			else if ( (LA30_0==BLOCK) ) {
				alt30=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 30, 0, input);
				throw nvae;
			}

			switch (alt30) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:753:36: lexerAtom
					{
					pushFollow(FOLLOW_lexerAtom_in_labeledLexerElement1553);
					lexerAtom();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:753:46: block
					{
					pushFollow(FOLLOW_block_in_labeledLexerElement1555);
					block();
					state._fsp--;

					}
					break;

			}

			match(input, Token.UP, null); 

			}


				exitLabeledLexerElement(((GrammarAST)retval.start));

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
	// $ANTLR end "labeledLexerElement"


	public static class lexerBlock_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerBlock"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:756:1: lexerBlock : ^( BLOCK ( optionsSpec )? ( lexerAlternative )+ ) ;
	public final GrammarTreeVisitor.lexerBlock_return lexerBlock() throws RecognitionException {
		GrammarTreeVisitor.lexerBlock_return retval = new GrammarTreeVisitor.lexerBlock_return();
		retval.start = input.LT(1);


			enterLexerBlock(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:763:3: ( ^( BLOCK ( optionsSpec )? ( lexerAlternative )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:763:5: ^( BLOCK ( optionsSpec )? ( lexerAlternative )+ )
			{
			match(input,BLOCK,FOLLOW_BLOCK_in_lexerBlock1580); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:763:13: ( optionsSpec )?
			int alt31=2;
			int LA31_0 = input.LA(1);
			if ( (LA31_0==OPTIONS) ) {
				alt31=1;
			}
			switch (alt31) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:763:13: optionsSpec
					{
					pushFollow(FOLLOW_optionsSpec_in_lexerBlock1582);
					optionsSpec();
					state._fsp--;

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:763:26: ( lexerAlternative )+
			int cnt32=0;
			loop32:
			while (true) {
				int alt32=2;
				int LA32_0 = input.LA(1);
				if ( (LA32_0==ALT||LA32_0==LEXER_ALT_ACTION) ) {
					alt32=1;
				}

				switch (alt32) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:763:26: lexerAlternative
					{
					pushFollow(FOLLOW_lexerAlternative_in_lexerBlock1585);
					lexerAlternative();
					state._fsp--;

					}
					break;

				default :
					if ( cnt32 >= 1 ) break loop32;
					EarlyExitException eee = new EarlyExitException(32, input);
					throw eee;
				}
				cnt32++;
			}

			match(input, Token.UP, null); 

			}


				exitLexerBlock(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerBlock"


	public static class lexerAtom_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerAtom"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:766:1: lexerAtom : ( terminal | ^( NOT blockSet ) | blockSet | ^( WILDCARD elementOptions ) | WILDCARD | LEXER_CHAR_SET | range | ruleref );
	public final GrammarTreeVisitor.lexerAtom_return lexerAtom() throws RecognitionException {
		GrammarTreeVisitor.lexerAtom_return retval = new GrammarTreeVisitor.lexerAtom_return();
		retval.start = input.LT(1);


			enterLexerAtom(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:773:5: ( terminal | ^( NOT blockSet ) | blockSet | ^( WILDCARD elementOptions ) | WILDCARD | LEXER_CHAR_SET | range | ruleref )
			int alt33=8;
			switch ( input.LA(1) ) {
			case STRING_LITERAL:
			case TOKEN_REF:
				{
				alt33=1;
				}
				break;
			case NOT:
				{
				alt33=2;
				}
				break;
			case SET:
				{
				alt33=3;
				}
				break;
			case WILDCARD:
				{
				int LA33_4 = input.LA(2);
				if ( (LA33_4==DOWN) ) {
					alt33=4;
				}
				else if ( ((LA33_4 >= UP && LA33_4 <= ACTION)||LA33_4==ASSIGN||LA33_4==LEXER_CHAR_SET||LA33_4==NOT||LA33_4==PLUS_ASSIGN||LA33_4==RANGE||LA33_4==RULE_REF||LA33_4==SEMPRED||LA33_4==STRING_LITERAL||LA33_4==TOKEN_REF||LA33_4==BLOCK||LA33_4==CLOSURE||LA33_4==EPSILON||(LA33_4 >= OPTIONAL && LA33_4 <= POSITIVE_CLOSURE)||LA33_4==SET||LA33_4==WILDCARD) ) {
					alt33=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 33, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case LEXER_CHAR_SET:
				{
				alt33=6;
				}
				break;
			case RANGE:
				{
				alt33=7;
				}
				break;
			case RULE_REF:
				{
				alt33=8;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 33, 0, input);
				throw nvae;
			}
			switch (alt33) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:773:9: terminal
					{
					pushFollow(FOLLOW_terminal_in_lexerAtom1616);
					terminal();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:774:9: ^( NOT blockSet )
					{
					match(input,NOT,FOLLOW_NOT_in_lexerAtom1627); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_blockSet_in_lexerAtom1629);
					blockSet();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:775:9: blockSet
					{
					pushFollow(FOLLOW_blockSet_in_lexerAtom1640);
					blockSet();
					state._fsp--;

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:776:9: ^( WILDCARD elementOptions )
					{
					match(input,WILDCARD,FOLLOW_WILDCARD_in_lexerAtom1651); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_lexerAtom1653);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:777:9: WILDCARD
					{
					match(input,WILDCARD,FOLLOW_WILDCARD_in_lexerAtom1664); 
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:778:7: LEXER_CHAR_SET
					{
					match(input,LEXER_CHAR_SET,FOLLOW_LEXER_CHAR_SET_in_lexerAtom1672); 
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:779:9: range
					{
					pushFollow(FOLLOW_range_in_lexerAtom1682);
					range();
					state._fsp--;

					}
					break;
				case 8 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:780:9: ruleref
					{
					pushFollow(FOLLOW_ruleref_in_lexerAtom1692);
					ruleref();
					state._fsp--;

					}
					break;

			}

				exitLexerAtom(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerAtom"


	public static class actionElement_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "actionElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:783:1: actionElement : ( ACTION | ^( ACTION elementOptions ) | SEMPRED | ^( SEMPRED elementOptions ) );
	public final GrammarTreeVisitor.actionElement_return actionElement() throws RecognitionException {
		GrammarTreeVisitor.actionElement_return retval = new GrammarTreeVisitor.actionElement_return();
		retval.start = input.LT(1);


			enterActionElement(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:790:2: ( ACTION | ^( ACTION elementOptions ) | SEMPRED | ^( SEMPRED elementOptions ) )
			int alt34=4;
			int LA34_0 = input.LA(1);
			if ( (LA34_0==ACTION) ) {
				int LA34_1 = input.LA(2);
				if ( (LA34_1==DOWN) ) {
					alt34=2;
				}
				else if ( (LA34_1==EOF) ) {
					alt34=1;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 34, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA34_0==SEMPRED) ) {
				int LA34_2 = input.LA(2);
				if ( (LA34_2==DOWN) ) {
					alt34=4;
				}
				else if ( (LA34_2==EOF) ) {
					alt34=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 34, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 34, 0, input);
				throw nvae;
			}

			switch (alt34) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:790:4: ACTION
					{
					match(input,ACTION,FOLLOW_ACTION_in_actionElement1716); 
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:791:6: ^( ACTION elementOptions )
					{
					match(input,ACTION,FOLLOW_ACTION_in_actionElement1724); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_actionElement1726);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:792:6: SEMPRED
					{
					match(input,SEMPRED,FOLLOW_SEMPRED_in_actionElement1734); 
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:793:6: ^( SEMPRED elementOptions )
					{
					match(input,SEMPRED,FOLLOW_SEMPRED_in_actionElement1742); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_actionElement1744);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;

			}

				exitActionElement(((GrammarAST)retval.start));

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
	// $ANTLR end "actionElement"


	public static class alternative_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "alternative"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:796:1: alternative : ( ^( ALT ( elementOptions )? ( element )+ ) | ^( ALT ( elementOptions )? EPSILON ) );
	public final GrammarTreeVisitor.alternative_return alternative() throws RecognitionException {
		GrammarTreeVisitor.alternative_return retval = new GrammarTreeVisitor.alternative_return();
		retval.start = input.LT(1);


			enterAlternative((AltAST)((GrammarAST)retval.start));
			discoverAlt((AltAST)((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:805:2: ( ^( ALT ( elementOptions )? ( element )+ ) | ^( ALT ( elementOptions )? EPSILON ) )
			int alt38=2;
			alt38 = dfa38.predict(input);
			switch (alt38) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:805:4: ^( ALT ( elementOptions )? ( element )+ )
					{
					match(input,ALT,FOLLOW_ALT_in_alternative1767); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:805:10: ( elementOptions )?
					int alt35=2;
					int LA35_0 = input.LA(1);
					if ( (LA35_0==ELEMENT_OPTIONS) ) {
						alt35=1;
					}
					switch (alt35) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:805:10: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_alternative1769);
							elementOptions();
							state._fsp--;

							}
							break;

					}

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:805:26: ( element )+
					int cnt36=0;
					loop36:
					while (true) {
						int alt36=2;
						int LA36_0 = input.LA(1);
						if ( (LA36_0==ACTION||LA36_0==ASSIGN||LA36_0==DOT||LA36_0==NOT||LA36_0==PLUS_ASSIGN||LA36_0==RULE_REF||LA36_0==SEMPRED||LA36_0==STRING_LITERAL||LA36_0==TOKEN_REF||LA36_0==BLOCK||LA36_0==CLOSURE||(LA36_0 >= OPTIONAL && LA36_0 <= POSITIVE_CLOSURE)||LA36_0==SET||LA36_0==WILDCARD) ) {
							alt36=1;
						}

						switch (alt36) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:805:26: element
							{
							pushFollow(FOLLOW_element_in_alternative1772);
							element();
							state._fsp--;

							}
							break;

						default :
							if ( cnt36 >= 1 ) break loop36;
							EarlyExitException eee = new EarlyExitException(36, input);
							throw eee;
						}
						cnt36++;
					}

					match(input, Token.UP, null); 

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:806:4: ^( ALT ( elementOptions )? EPSILON )
					{
					match(input,ALT,FOLLOW_ALT_in_alternative1780); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:806:10: ( elementOptions )?
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( (LA37_0==ELEMENT_OPTIONS) ) {
						alt37=1;
					}
					switch (alt37) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:806:10: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_alternative1782);
							elementOptions();
							state._fsp--;

							}
							break;

					}

					match(input,EPSILON,FOLLOW_EPSILON_in_alternative1785); 
					match(input, Token.UP, null); 

					}
					break;

			}

				finishAlt((AltAST)((GrammarAST)retval.start));
				exitAlternative((AltAST)((GrammarAST)retval.start));

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
	// $ANTLR end "alternative"


	public static class lexerCommand_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerCommand"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:809:1: lexerCommand : ( ^( LEXER_ACTION_CALL ID lexerCommandExpr ) | ID );
	public final GrammarTreeVisitor.lexerCommand_return lexerCommand() throws RecognitionException {
		GrammarTreeVisitor.lexerCommand_return retval = new GrammarTreeVisitor.lexerCommand_return();
		retval.start = input.LT(1);

		GrammarAST ID25=null;
		GrammarAST ID27=null;
		TreeRuleReturnScope lexerCommandExpr26 =null;


			enterLexerCommand(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:816:2: ( ^( LEXER_ACTION_CALL ID lexerCommandExpr ) | ID )
			int alt39=2;
			int LA39_0 = input.LA(1);
			if ( (LA39_0==LEXER_ACTION_CALL) ) {
				alt39=1;
			}
			else if ( (LA39_0==ID) ) {
				alt39=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 39, 0, input);
				throw nvae;
			}

			switch (alt39) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:816:4: ^( LEXER_ACTION_CALL ID lexerCommandExpr )
					{
					match(input,LEXER_ACTION_CALL,FOLLOW_LEXER_ACTION_CALL_in_lexerCommand1811); 
					match(input, Token.DOWN, null); 
					ID25=(GrammarAST)match(input,ID,FOLLOW_ID_in_lexerCommand1813); 
					pushFollow(FOLLOW_lexerCommandExpr_in_lexerCommand1815);
					lexerCommandExpr26=lexerCommandExpr();
					state._fsp--;

					match(input, Token.UP, null); 

					lexerCallCommand(currentOuterAltNumber, ID25, (lexerCommandExpr26!=null?((GrammarAST)lexerCommandExpr26.start):null));
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:818:4: ID
					{
					ID27=(GrammarAST)match(input,ID,FOLLOW_ID_in_lexerCommand1831); 
					lexerCommand(currentOuterAltNumber, ID27);
					}
					break;

			}

				exitLexerCommand(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerCommand"


	public static class lexerCommandExpr_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerCommandExpr"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:822:1: lexerCommandExpr : ( ID | INT );
	public final GrammarTreeVisitor.lexerCommandExpr_return lexerCommandExpr() throws RecognitionException {
		GrammarTreeVisitor.lexerCommandExpr_return retval = new GrammarTreeVisitor.lexerCommandExpr_return();
		retval.start = input.LT(1);


			enterLexerCommandExpr(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:829:2: ( ID | INT )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:
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


				exitLexerCommandExpr(((GrammarAST)retval.start));

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
	};


	// $ANTLR start "element"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:833:1: element : ( labeledElement | atom | subrule | ACTION | SEMPRED | ^( ACTION elementOptions ) | ^( SEMPRED elementOptions ) | ^( NOT blockSet ) | ^( NOT block ) );
	public final GrammarTreeVisitor.element_return element() throws RecognitionException {
		GrammarTreeVisitor.element_return retval = new GrammarTreeVisitor.element_return();
		retval.start = input.LT(1);

		GrammarAST ACTION28=null;
		GrammarAST SEMPRED29=null;
		GrammarAST ACTION30=null;
		GrammarAST SEMPRED31=null;


			enterElement(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:840:2: ( labeledElement | atom | subrule | ACTION | SEMPRED | ^( ACTION elementOptions ) | ^( SEMPRED elementOptions ) | ^( NOT blockSet ) | ^( NOT block ) )
			int alt40=9;
			switch ( input.LA(1) ) {
			case ASSIGN:
			case PLUS_ASSIGN:
				{
				alt40=1;
				}
				break;
			case DOT:
			case RULE_REF:
			case STRING_LITERAL:
			case TOKEN_REF:
			case SET:
			case WILDCARD:
				{
				alt40=2;
				}
				break;
			case BLOCK:
			case CLOSURE:
			case OPTIONAL:
			case POSITIVE_CLOSURE:
				{
				alt40=3;
				}
				break;
			case ACTION:
				{
				int LA40_4 = input.LA(2);
				if ( (LA40_4==DOWN) ) {
					alt40=6;
				}
				else if ( ((LA40_4 >= UP && LA40_4 <= ACTION)||LA40_4==ASSIGN||LA40_4==DOT||LA40_4==NOT||LA40_4==PLUS_ASSIGN||LA40_4==RULE_REF||LA40_4==SEMPRED||LA40_4==STRING_LITERAL||LA40_4==TOKEN_REF||LA40_4==BLOCK||LA40_4==CLOSURE||(LA40_4 >= OPTIONAL && LA40_4 <= POSITIVE_CLOSURE)||LA40_4==SET||LA40_4==WILDCARD) ) {
					alt40=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 40, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SEMPRED:
				{
				int LA40_5 = input.LA(2);
				if ( (LA40_5==DOWN) ) {
					alt40=7;
				}
				else if ( ((LA40_5 >= UP && LA40_5 <= ACTION)||LA40_5==ASSIGN||LA40_5==DOT||LA40_5==NOT||LA40_5==PLUS_ASSIGN||LA40_5==RULE_REF||LA40_5==SEMPRED||LA40_5==STRING_LITERAL||LA40_5==TOKEN_REF||LA40_5==BLOCK||LA40_5==CLOSURE||(LA40_5 >= OPTIONAL && LA40_5 <= POSITIVE_CLOSURE)||LA40_5==SET||LA40_5==WILDCARD) ) {
					alt40=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 40, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case NOT:
				{
				int LA40_6 = input.LA(2);
				if ( (LA40_6==DOWN) ) {
					int LA40_11 = input.LA(3);
					if ( (LA40_11==SET) ) {
						alt40=8;
					}
					else if ( (LA40_11==BLOCK) ) {
						alt40=9;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 40, 11, input);
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
							new NoViableAltException("", 40, 6, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 40, 0, input);
				throw nvae;
			}
			switch (alt40) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:840:4: labeledElement
					{
					pushFollow(FOLLOW_labeledElement_in_element1888);
					labeledElement();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:841:4: atom
					{
					pushFollow(FOLLOW_atom_in_element1893);
					atom();
					state._fsp--;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:842:4: subrule
					{
					pushFollow(FOLLOW_subrule_in_element1898);
					subrule();
					state._fsp--;

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:843:6: ACTION
					{
					ACTION28=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element1905); 
					actionInAlt((ActionAST)ACTION28);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:844:6: SEMPRED
					{
					SEMPRED29=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element1919); 
					sempredInAlt((PredAST)SEMPRED29);
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:845:6: ^( ACTION elementOptions )
					{
					ACTION30=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element1934); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_element1936);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					actionInAlt((ActionAST)ACTION30);
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:846:6: ^( SEMPRED elementOptions )
					{
					SEMPRED31=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element1947); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_element1949);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					sempredInAlt((PredAST)SEMPRED31);
					}
					break;
				case 8 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:848:4: ^( NOT blockSet )
					{
					match(input,NOT,FOLLOW_NOT_in_element1959); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_blockSet_in_element1961);
					blockSet();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 9 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:849:4: ^( NOT block )
					{
					match(input,NOT,FOLLOW_NOT_in_element1968); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_block_in_element1970);
					block();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;

			}

				exitElement(((GrammarAST)retval.start));

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


	public static class astOperand_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "astOperand"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:852:1: astOperand : ( atom | ^( NOT blockSet ) | ^( NOT block ) );
	public final GrammarTreeVisitor.astOperand_return astOperand() throws RecognitionException {
		GrammarTreeVisitor.astOperand_return retval = new GrammarTreeVisitor.astOperand_return();
		retval.start = input.LT(1);


			enterAstOperand(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:859:2: ( atom | ^( NOT blockSet ) | ^( NOT block ) )
			int alt41=3;
			int LA41_0 = input.LA(1);
			if ( (LA41_0==DOT||LA41_0==RULE_REF||LA41_0==STRING_LITERAL||LA41_0==TOKEN_REF||LA41_0==SET||LA41_0==WILDCARD) ) {
				alt41=1;
			}
			else if ( (LA41_0==NOT) ) {
				int LA41_2 = input.LA(2);
				if ( (LA41_2==DOWN) ) {
					int LA41_3 = input.LA(3);
					if ( (LA41_3==SET) ) {
						alt41=2;
					}
					else if ( (LA41_3==BLOCK) ) {
						alt41=3;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 41, 3, input);
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
							new NoViableAltException("", 41, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 41, 0, input);
				throw nvae;
			}

			switch (alt41) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:859:4: atom
					{
					pushFollow(FOLLOW_atom_in_astOperand1992);
					atom();
					state._fsp--;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:860:4: ^( NOT blockSet )
					{
					match(input,NOT,FOLLOW_NOT_in_astOperand1998); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_blockSet_in_astOperand2000);
					blockSet();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:861:4: ^( NOT block )
					{
					match(input,NOT,FOLLOW_NOT_in_astOperand2007); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_block_in_astOperand2009);
					block();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;

			}

				exitAstOperand(((GrammarAST)retval.start));

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
	// $ANTLR end "astOperand"


	public static class labeledElement_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "labeledElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:864:1: labeledElement : ^( ( ASSIGN | PLUS_ASSIGN ) ID element ) ;
	public final GrammarTreeVisitor.labeledElement_return labeledElement() throws RecognitionException {
		GrammarTreeVisitor.labeledElement_return retval = new GrammarTreeVisitor.labeledElement_return();
		retval.start = input.LT(1);

		GrammarAST ID32=null;
		TreeRuleReturnScope element33 =null;


			enterLabeledElement(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:871:2: ( ^( ( ASSIGN | PLUS_ASSIGN ) ID element ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:871:4: ^( ( ASSIGN | PLUS_ASSIGN ) ID element )
			{
			if ( input.LA(1)==ASSIGN||input.LA(1)==PLUS_ASSIGN ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			match(input, Token.DOWN, null); 
			ID32=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement2038); 
			pushFollow(FOLLOW_element_in_labeledElement2040);
			element33=element();
			state._fsp--;

			match(input, Token.UP, null); 

			label(((GrammarAST)retval.start), ID32, (element33!=null?((GrammarAST)element33.start):null));
			}


				exitLabeledElement(((GrammarAST)retval.start));

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
	// $ANTLR end "labeledElement"


	public static class subrule_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "subrule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:874:1: subrule : ( ^( blockSuffix block ) | block );
	public final GrammarTreeVisitor.subrule_return subrule() throws RecognitionException {
		GrammarTreeVisitor.subrule_return retval = new GrammarTreeVisitor.subrule_return();
		retval.start = input.LT(1);


			enterSubrule(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:881:2: ( ^( blockSuffix block ) | block )
			int alt42=2;
			int LA42_0 = input.LA(1);
			if ( (LA42_0==CLOSURE||(LA42_0 >= OPTIONAL && LA42_0 <= POSITIVE_CLOSURE)) ) {
				alt42=1;
			}
			else if ( (LA42_0==BLOCK) ) {
				alt42=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 42, 0, input);
				throw nvae;
			}

			switch (alt42) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:881:4: ^( blockSuffix block )
					{
					pushFollow(FOLLOW_blockSuffix_in_subrule2065);
					blockSuffix();
					state._fsp--;

					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_block_in_subrule2067);
					block();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:882:5: block
					{
					pushFollow(FOLLOW_block_in_subrule2074);
					block();
					state._fsp--;

					}
					break;

			}

				exitSubrule(((GrammarAST)retval.start));

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


	public static class lexerSubrule_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "lexerSubrule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:885:1: lexerSubrule : ( ^( blockSuffix lexerBlock ) | lexerBlock );
	public final GrammarTreeVisitor.lexerSubrule_return lexerSubrule() throws RecognitionException {
		GrammarTreeVisitor.lexerSubrule_return retval = new GrammarTreeVisitor.lexerSubrule_return();
		retval.start = input.LT(1);


			enterLexerSubrule(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:892:2: ( ^( blockSuffix lexerBlock ) | lexerBlock )
			int alt43=2;
			int LA43_0 = input.LA(1);
			if ( (LA43_0==CLOSURE||(LA43_0 >= OPTIONAL && LA43_0 <= POSITIVE_CLOSURE)) ) {
				alt43=1;
			}
			else if ( (LA43_0==BLOCK) ) {
				alt43=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 43, 0, input);
				throw nvae;
			}

			switch (alt43) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:892:4: ^( blockSuffix lexerBlock )
					{
					pushFollow(FOLLOW_blockSuffix_in_lexerSubrule2099);
					blockSuffix();
					state._fsp--;

					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_lexerBlock_in_lexerSubrule2101);
					lexerBlock();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:893:5: lexerBlock
					{
					pushFollow(FOLLOW_lexerBlock_in_lexerSubrule2108);
					lexerBlock();
					state._fsp--;

					}
					break;

			}

				exitLexerSubrule(((GrammarAST)retval.start));

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
	// $ANTLR end "lexerSubrule"


	public static class blockSuffix_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "blockSuffix"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:896:1: blockSuffix : ebnfSuffix ;
	public final GrammarTreeVisitor.blockSuffix_return blockSuffix() throws RecognitionException {
		GrammarTreeVisitor.blockSuffix_return retval = new GrammarTreeVisitor.blockSuffix_return();
		retval.start = input.LT(1);


			enterBlockSuffix(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:903:5: ( ebnfSuffix )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:903:7: ebnfSuffix
			{
			pushFollow(FOLLOW_ebnfSuffix_in_blockSuffix2135);
			ebnfSuffix();
			state._fsp--;

			}


				exitBlockSuffix(((GrammarAST)retval.start));

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
	// $ANTLR end "blockSuffix"


	public static class ebnfSuffix_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "ebnfSuffix"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:906:1: ebnfSuffix : ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE );
	public final GrammarTreeVisitor.ebnfSuffix_return ebnfSuffix() throws RecognitionException {
		GrammarTreeVisitor.ebnfSuffix_return retval = new GrammarTreeVisitor.ebnfSuffix_return();
		retval.start = input.LT(1);


			enterEbnfSuffix(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:913:2: ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:
			{
			if ( input.LA(1)==CLOSURE||(input.LA(1) >= OPTIONAL && input.LA(1) <= POSITIVE_CLOSURE) ) {
				input.consume();
				state.errorRecovery=false;
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}
			}


				exitEbnfSuffix(((GrammarAST)retval.start));

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
	// $ANTLR end "ebnfSuffix"


	public static class atom_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "atom"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:918:1: atom : ( ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD elementOptions ) | WILDCARD | terminal | blockSet | ruleref );
	public final GrammarTreeVisitor.atom_return atom() throws RecognitionException {
		GrammarTreeVisitor.atom_return retval = new GrammarTreeVisitor.atom_return();
		retval.start = input.LT(1);

		GrammarAST WILDCARD34=null;
		GrammarAST WILDCARD35=null;


			enterAtom(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:925:2: ( ^( DOT ID terminal ) | ^( DOT ID ruleref ) | ^( WILDCARD elementOptions ) | WILDCARD | terminal | blockSet | ruleref )
			int alt44=7;
			switch ( input.LA(1) ) {
			case DOT:
				{
				int LA44_1 = input.LA(2);
				if ( (LA44_1==DOWN) ) {
					int LA44_6 = input.LA(3);
					if ( (LA44_6==ID) ) {
						int LA44_9 = input.LA(4);
						if ( (LA44_9==STRING_LITERAL||LA44_9==TOKEN_REF) ) {
							alt44=1;
						}
						else if ( (LA44_9==RULE_REF) ) {
							alt44=2;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 44, 9, input);
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
								new NoViableAltException("", 44, 6, input);
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
							new NoViableAltException("", 44, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case WILDCARD:
				{
				int LA44_2 = input.LA(2);
				if ( (LA44_2==DOWN) ) {
					alt44=3;
				}
				else if ( (LA44_2==EOF||(LA44_2 >= UP && LA44_2 <= ACTION)||LA44_2==ASSIGN||LA44_2==DOT||LA44_2==NOT||LA44_2==PLUS_ASSIGN||LA44_2==RULE_REF||LA44_2==SEMPRED||LA44_2==STRING_LITERAL||LA44_2==TOKEN_REF||LA44_2==BLOCK||LA44_2==CLOSURE||(LA44_2 >= OPTIONAL && LA44_2 <= POSITIVE_CLOSURE)||LA44_2==SET||LA44_2==WILDCARD) ) {
					alt44=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 44, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case STRING_LITERAL:
			case TOKEN_REF:
				{
				alt44=5;
				}
				break;
			case SET:
				{
				alt44=6;
				}
				break;
			case RULE_REF:
				{
				alt44=7;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 44, 0, input);
				throw nvae;
			}
			switch (alt44) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:925:4: ^( DOT ID terminal )
					{
					match(input,DOT,FOLLOW_DOT_in_atom2196); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_atom2198); 
					pushFollow(FOLLOW_terminal_in_atom2200);
					terminal();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:926:4: ^( DOT ID ruleref )
					{
					match(input,DOT,FOLLOW_DOT_in_atom2207); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_atom2209); 
					pushFollow(FOLLOW_ruleref_in_atom2211);
					ruleref();
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:927:7: ^( WILDCARD elementOptions )
					{
					WILDCARD34=(GrammarAST)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom2221); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_atom2223);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					wildcardRef(WILDCARD34);
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:928:7: WILDCARD
					{
					WILDCARD35=(GrammarAST)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom2234); 
					wildcardRef(WILDCARD35);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:929:9: terminal
					{
					pushFollow(FOLLOW_terminal_in_atom2250);
					terminal();
					state._fsp--;

					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:930:7: blockSet
					{
					pushFollow(FOLLOW_blockSet_in_atom2258);
					blockSet();
					state._fsp--;

					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:931:9: ruleref
					{
					pushFollow(FOLLOW_ruleref_in_atom2268);
					ruleref();
					state._fsp--;

					}
					break;

			}

				exitAtom(((GrammarAST)retval.start));

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


	public static class blockSet_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "blockSet"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:934:1: blockSet : ^( SET ( setElement )+ ) ;
	public final GrammarTreeVisitor.blockSet_return blockSet() throws RecognitionException {
		GrammarTreeVisitor.blockSet_return retval = new GrammarTreeVisitor.blockSet_return();
		retval.start = input.LT(1);


			enterBlockSet(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:941:2: ( ^( SET ( setElement )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:941:4: ^( SET ( setElement )+ )
			{
			match(input,SET,FOLLOW_SET_in_blockSet2293); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:941:10: ( setElement )+
			int cnt45=0;
			loop45:
			while (true) {
				int alt45=2;
				int LA45_0 = input.LA(1);
				if ( (LA45_0==LEXER_CHAR_SET||LA45_0==RANGE||LA45_0==STRING_LITERAL||LA45_0==TOKEN_REF) ) {
					alt45=1;
				}

				switch (alt45) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:941:10: setElement
					{
					pushFollow(FOLLOW_setElement_in_blockSet2295);
					setElement();
					state._fsp--;

					}
					break;

				default :
					if ( cnt45 >= 1 ) break loop45;
					EarlyExitException eee = new EarlyExitException(45, input);
					throw eee;
				}
				cnt45++;
			}

			match(input, Token.UP, null); 

			}


				exitBlockSet(((GrammarAST)retval.start));

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
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:944:1: setElement : ( ^( STRING_LITERAL elementOptions ) | ^( TOKEN_REF elementOptions ) | STRING_LITERAL | TOKEN_REF | ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) | LEXER_CHAR_SET );
	public final GrammarTreeVisitor.setElement_return setElement() throws RecognitionException {
		GrammarTreeVisitor.setElement_return retval = new GrammarTreeVisitor.setElement_return();
		retval.start = input.LT(1);

		GrammarAST a=null;
		GrammarAST b=null;
		GrammarAST STRING_LITERAL36=null;
		GrammarAST TOKEN_REF37=null;
		GrammarAST STRING_LITERAL38=null;
		GrammarAST TOKEN_REF39=null;


			enterSetElement(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:951:2: ( ^( STRING_LITERAL elementOptions ) | ^( TOKEN_REF elementOptions ) | STRING_LITERAL | TOKEN_REF | ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) | LEXER_CHAR_SET )
			int alt46=6;
			switch ( input.LA(1) ) {
			case STRING_LITERAL:
				{
				int LA46_1 = input.LA(2);
				if ( (LA46_1==DOWN) ) {
					alt46=1;
				}
				else if ( (LA46_1==UP||LA46_1==LEXER_CHAR_SET||LA46_1==RANGE||LA46_1==STRING_LITERAL||LA46_1==TOKEN_REF) ) {
					alt46=3;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 46, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case TOKEN_REF:
				{
				int LA46_2 = input.LA(2);
				if ( (LA46_2==DOWN) ) {
					alt46=2;
				}
				else if ( (LA46_2==UP||LA46_2==LEXER_CHAR_SET||LA46_2==RANGE||LA46_2==STRING_LITERAL||LA46_2==TOKEN_REF) ) {
					alt46=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 46, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case RANGE:
				{
				alt46=5;
				}
				break;
			case LEXER_CHAR_SET:
				{
				alt46=6;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 46, 0, input);
				throw nvae;
			}
			switch (alt46) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:951:4: ^( STRING_LITERAL elementOptions )
					{
					STRING_LITERAL36=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement2319); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_setElement2321);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					stringRef((TerminalAST)STRING_LITERAL36);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:952:4: ^( TOKEN_REF elementOptions )
					{
					TOKEN_REF37=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_setElement2333); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_setElement2335);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					tokenRef((TerminalAST)TOKEN_REF37);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:953:4: STRING_LITERAL
					{
					STRING_LITERAL38=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement2345); 
					stringRef((TerminalAST)STRING_LITERAL38);
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:954:4: TOKEN_REF
					{
					TOKEN_REF39=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_setElement2370); 
					tokenRef((TerminalAST)TOKEN_REF39);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:955:4: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
					{
					match(input,RANGE,FOLLOW_RANGE_in_setElement2399); 
					match(input, Token.DOWN, null); 
					a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement2403); 
					b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement2407); 
					match(input, Token.UP, null); 


							stringRef((TerminalAST)a);
							stringRef((TerminalAST)b);
							
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:960:17: LEXER_CHAR_SET
					{
					match(input,LEXER_CHAR_SET,FOLLOW_LEXER_CHAR_SET_in_setElement2430); 
					}
					break;

			}

				exitSetElement(((GrammarAST)retval.start));

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


	public static class block_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "block"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:963:1: block : ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? ( alternative )+ ) ;
	public final GrammarTreeVisitor.block_return block() throws RecognitionException {
		GrammarTreeVisitor.block_return retval = new GrammarTreeVisitor.block_return();
		retval.start = input.LT(1);


			enterBlock(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:5: ( ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? ( alternative )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:7: ^( BLOCK ( optionsSpec )? ( ruleAction )* ( ACTION )? ( alternative )+ )
			{
			match(input,BLOCK,FOLLOW_BLOCK_in_block2455); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:15: ( optionsSpec )?
			int alt47=2;
			int LA47_0 = input.LA(1);
			if ( (LA47_0==OPTIONS) ) {
				alt47=1;
			}
			switch (alt47) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:15: optionsSpec
					{
					pushFollow(FOLLOW_optionsSpec_in_block2457);
					optionsSpec();
					state._fsp--;

					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:28: ( ruleAction )*
			loop48:
			while (true) {
				int alt48=2;
				int LA48_0 = input.LA(1);
				if ( (LA48_0==AT) ) {
					alt48=1;
				}

				switch (alt48) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:28: ruleAction
					{
					pushFollow(FOLLOW_ruleAction_in_block2460);
					ruleAction();
					state._fsp--;

					}
					break;

				default :
					break loop48;
				}
			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:40: ( ACTION )?
			int alt49=2;
			int LA49_0 = input.LA(1);
			if ( (LA49_0==ACTION) ) {
				alt49=1;
			}
			switch (alt49) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:40: ACTION
					{
					match(input,ACTION,FOLLOW_ACTION_in_block2463); 
					}
					break;

			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:48: ( alternative )+
			int cnt50=0;
			loop50:
			while (true) {
				int alt50=2;
				int LA50_0 = input.LA(1);
				if ( (LA50_0==ALT) ) {
					alt50=1;
				}

				switch (alt50) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:970:48: alternative
					{
					pushFollow(FOLLOW_alternative_in_block2466);
					alternative();
					state._fsp--;

					}
					break;

				default :
					if ( cnt50 >= 1 ) break loop50;
					EarlyExitException eee = new EarlyExitException(50, input);
					throw eee;
				}
				cnt50++;
			}

			match(input, Token.UP, null); 

			}


				exitBlock(((GrammarAST)retval.start));

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
	// $ANTLR end "block"


	public static class ruleref_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "ruleref"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:973:1: ruleref : ^( RULE_REF (arg= ARG_ACTION )? ( elementOptions )? ) ;
	public final GrammarTreeVisitor.ruleref_return ruleref() throws RecognitionException {
		GrammarTreeVisitor.ruleref_return retval = new GrammarTreeVisitor.ruleref_return();
		retval.start = input.LT(1);

		GrammarAST arg=null;
		GrammarAST RULE_REF40=null;


			enterRuleref(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:980:5: ( ^( RULE_REF (arg= ARG_ACTION )? ( elementOptions )? ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:980:7: ^( RULE_REF (arg= ARG_ACTION )? ( elementOptions )? )
			{
			RULE_REF40=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref2496); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:980:21: (arg= ARG_ACTION )?
				int alt51=2;
				int LA51_0 = input.LA(1);
				if ( (LA51_0==ARG_ACTION) ) {
					alt51=1;
				}
				switch (alt51) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:980:21: arg= ARG_ACTION
						{
						arg=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref2500); 
						}
						break;

				}

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:980:34: ( elementOptions )?
				int alt52=2;
				int LA52_0 = input.LA(1);
				if ( (LA52_0==ELEMENT_OPTIONS) ) {
					alt52=1;
				}
				switch (alt52) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:980:34: elementOptions
						{
						pushFollow(FOLLOW_elementOptions_in_ruleref2503);
						elementOptions();
						state._fsp--;

						}
						break;

				}

				match(input, Token.UP, null); 
			}


			    	ruleRef(RULE_REF40, (ActionAST)arg);
			    	if ( arg!=null ) actionInAlt((ActionAST)arg);
			    	
			}


				exitRuleref(((GrammarAST)retval.start));

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
	// $ANTLR end "ruleref"


	public static class range_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "range"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:987:1: range : ^( RANGE STRING_LITERAL STRING_LITERAL ) ;
	public final GrammarTreeVisitor.range_return range() throws RecognitionException {
		GrammarTreeVisitor.range_return retval = new GrammarTreeVisitor.range_return();
		retval.start = input.LT(1);


			enterRange(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:994:5: ( ^( RANGE STRING_LITERAL STRING_LITERAL ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:994:7: ^( RANGE STRING_LITERAL STRING_LITERAL )
			{
			match(input,RANGE,FOLLOW_RANGE_in_range2540); 
			match(input, Token.DOWN, null); 
			match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range2542); 
			match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range2544); 
			match(input, Token.UP, null); 

			}


				exitRange(((GrammarAST)retval.start));

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
	// $ANTLR end "range"


	public static class terminal_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "terminal"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:997:1: terminal : ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF elementOptions ) | TOKEN_REF );
	public final GrammarTreeVisitor.terminal_return terminal() throws RecognitionException {
		GrammarTreeVisitor.terminal_return retval = new GrammarTreeVisitor.terminal_return();
		retval.start = input.LT(1);

		GrammarAST STRING_LITERAL41=null;
		GrammarAST STRING_LITERAL42=null;
		GrammarAST TOKEN_REF43=null;
		GrammarAST TOKEN_REF44=null;


			enterTerminal(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1004:5: ( ^( STRING_LITERAL elementOptions ) | STRING_LITERAL | ^( TOKEN_REF elementOptions ) | TOKEN_REF )
			int alt53=4;
			int LA53_0 = input.LA(1);
			if ( (LA53_0==STRING_LITERAL) ) {
				int LA53_1 = input.LA(2);
				if ( (LA53_1==DOWN) ) {
					alt53=1;
				}
				else if ( (LA53_1==EOF||(LA53_1 >= UP && LA53_1 <= ACTION)||LA53_1==ASSIGN||LA53_1==DOT||LA53_1==LEXER_CHAR_SET||LA53_1==NOT||LA53_1==PLUS_ASSIGN||LA53_1==RANGE||LA53_1==RULE_REF||LA53_1==SEMPRED||LA53_1==STRING_LITERAL||LA53_1==TOKEN_REF||LA53_1==BLOCK||LA53_1==CLOSURE||LA53_1==EPSILON||(LA53_1 >= OPTIONAL && LA53_1 <= POSITIVE_CLOSURE)||LA53_1==SET||LA53_1==WILDCARD) ) {
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
			else if ( (LA53_0==TOKEN_REF) ) {
				int LA53_2 = input.LA(2);
				if ( (LA53_2==DOWN) ) {
					alt53=3;
				}
				else if ( (LA53_2==EOF||(LA53_2 >= UP && LA53_2 <= ACTION)||LA53_2==ASSIGN||LA53_2==DOT||LA53_2==LEXER_CHAR_SET||LA53_2==NOT||LA53_2==PLUS_ASSIGN||LA53_2==RANGE||LA53_2==RULE_REF||LA53_2==SEMPRED||LA53_2==STRING_LITERAL||LA53_2==TOKEN_REF||LA53_2==BLOCK||LA53_2==CLOSURE||LA53_2==EPSILON||(LA53_2 >= OPTIONAL && LA53_2 <= POSITIVE_CLOSURE)||LA53_2==SET||LA53_2==WILDCARD) ) {
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
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1004:8: ^( STRING_LITERAL elementOptions )
					{
					STRING_LITERAL41=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal2574); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_terminal2576);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					stringRef((TerminalAST)STRING_LITERAL41);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1006:7: STRING_LITERAL
					{
					STRING_LITERAL42=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal2599); 
					stringRef((TerminalAST)STRING_LITERAL42);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1007:7: ^( TOKEN_REF elementOptions )
					{
					TOKEN_REF43=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal2613); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_terminal2615);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					tokenRef((TerminalAST)TOKEN_REF43);
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1008:7: TOKEN_REF
					{
					TOKEN_REF44=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal2626); 
					tokenRef((TerminalAST)TOKEN_REF44);
					}
					break;

			}

				exitTerminal(((GrammarAST)retval.start));

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


	public static class elementOptions_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "elementOptions"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1011:1: elementOptions : ^( ELEMENT_OPTIONS ( elementOption[(GrammarASTWithOptions)$start.getParent()] )* ) ;
	public final GrammarTreeVisitor.elementOptions_return elementOptions() throws RecognitionException {
		GrammarTreeVisitor.elementOptions_return retval = new GrammarTreeVisitor.elementOptions_return();
		retval.start = input.LT(1);


			enterElementOptions(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1018:5: ( ^( ELEMENT_OPTIONS ( elementOption[(GrammarASTWithOptions)$start.getParent()] )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1018:7: ^( ELEMENT_OPTIONS ( elementOption[(GrammarASTWithOptions)$start.getParent()] )* )
			{
			match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOptions2663); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1018:25: ( elementOption[(GrammarASTWithOptions)$start.getParent()] )*
				loop54:
				while (true) {
					int alt54=2;
					int LA54_0 = input.LA(1);
					if ( (LA54_0==ASSIGN||LA54_0==ID) ) {
						alt54=1;
					}

					switch (alt54) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1018:25: elementOption[(GrammarASTWithOptions)$start.getParent()]
						{
						pushFollow(FOLLOW_elementOption_in_elementOptions2665);
						elementOption((GrammarASTWithOptions)((GrammarAST)retval.start).getParent());
						state._fsp--;

						}
						break;

					default :
						break loop54;
					}
				}

				match(input, Token.UP, null); 
			}

			}


				exitElementOptions(((GrammarAST)retval.start));

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
	// $ANTLR end "elementOptions"


	public static class elementOption_return extends TreeRuleReturnScope {
	};


	// $ANTLR start "elementOption"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1021:1: elementOption[GrammarASTWithOptions t] : ( ID | ^( ASSIGN id= ID v= ID ) | ^( ASSIGN ID v= STRING_LITERAL ) | ^( ASSIGN ID v= ACTION ) | ^( ASSIGN ID v= INT ) );
	public final GrammarTreeVisitor.elementOption_return elementOption(GrammarASTWithOptions t) throws RecognitionException {
		GrammarTreeVisitor.elementOption_return retval = new GrammarTreeVisitor.elementOption_return();
		retval.start = input.LT(1);

		GrammarAST id=null;
		GrammarAST v=null;
		GrammarAST ID45=null;
		GrammarAST ID46=null;
		GrammarAST ID47=null;
		GrammarAST ID48=null;


			enterElementOption(((GrammarAST)retval.start));

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1028:5: ( ID | ^( ASSIGN id= ID v= ID ) | ^( ASSIGN ID v= STRING_LITERAL ) | ^( ASSIGN ID v= ACTION ) | ^( ASSIGN ID v= INT ) )
			int alt55=5;
			int LA55_0 = input.LA(1);
			if ( (LA55_0==ID) ) {
				alt55=1;
			}
			else if ( (LA55_0==ASSIGN) ) {
				int LA55_2 = input.LA(2);
				if ( (LA55_2==DOWN) ) {
					int LA55_3 = input.LA(3);
					if ( (LA55_3==ID) ) {
						switch ( input.LA(4) ) {
						case ID:
							{
							alt55=2;
							}
							break;
						case STRING_LITERAL:
							{
							alt55=3;
							}
							break;
						case ACTION:
							{
							alt55=4;
							}
							break;
						case INT:
							{
							alt55=5;
							}
							break;
						default:
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 55, 4, input);
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
								new NoViableAltException("", 55, 3, input);
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
							new NoViableAltException("", 55, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 55, 0, input);
				throw nvae;
			}

			switch (alt55) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1028:7: ID
					{
					ID45=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption2696); 
					elementOption(t, ID45, null);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1029:9: ^( ASSIGN id= ID v= ID )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption2716); 
					match(input, Token.DOWN, null); 
					id=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption2720); 
					v=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption2724); 
					match(input, Token.UP, null); 

					elementOption(t, id, v);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1030:9: ^( ASSIGN ID v= STRING_LITERAL )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption2740); 
					match(input, Token.DOWN, null); 
					ID46=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption2742); 
					v=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption2746); 
					match(input, Token.UP, null); 

					elementOption(t, ID46, v);
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1031:9: ^( ASSIGN ID v= ACTION )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption2760); 
					match(input, Token.DOWN, null); 
					ID47=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption2762); 
					v=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_elementOption2766); 
					match(input, Token.UP, null); 

					elementOption(t, ID47, v);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/GrammarTreeVisitor.g:1032:9: ^( ASSIGN ID v= INT )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption2782); 
					match(input, Token.DOWN, null); 
					ID48=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption2784); 
					v=(GrammarAST)match(input,INT,FOLLOW_INT_in_elementOption2788); 
					match(input, Token.UP, null); 

					elementOption(t, ID48, v);
					}
					break;

			}

				exitElementOption(((GrammarAST)retval.start));

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
	// $ANTLR end "elementOption"

	// Delegated rules


	protected DFA38 dfa38 = new DFA38(this);
	static final String DFA38_eotS =
		"\24\uffff";
	static final String DFA38_eofS =
		"\24\uffff";
	static final String DFA38_minS =
		"\1\111\1\2\1\4\1\2\2\uffff\2\3\1\2\1\4\1\34\1\4\10\3";
	static final String DFA38_maxS =
		"\1\111\1\2\1\143\1\2\2\uffff\2\34\1\2\1\143\1\34\1\76\4\3\4\34";
	static final String DFA38_acceptS =
		"\4\uffff\1\1\1\2\16\uffff";
	static final String DFA38_specialS =
		"\24\uffff}>";
	static final String[] DFA38_transitionS = {
			"\1\1",
			"\1\2",
			"\1\4\5\uffff\1\4\11\uffff\1\4\22\uffff\1\4\6\uffff\1\4\12\uffff\1\4"+
			"\1\uffff\1\4\2\uffff\1\4\3\uffff\1\4\12\uffff\1\4\1\uffff\1\4\1\uffff"+
			"\1\3\1\5\5\uffff\2\4\7\uffff\1\4\1\uffff\1\4",
			"\1\6",
			"",
			"",
			"\1\11\6\uffff\1\10\21\uffff\1\7",
			"\1\11\6\uffff\1\10\21\uffff\1\7",
			"\1\12",
			"\1\4\5\uffff\1\4\11\uffff\1\4\22\uffff\1\4\6\uffff\1\4\12\uffff\1\4"+
			"\1\uffff\1\4\2\uffff\1\4\3\uffff\1\4\12\uffff\1\4\1\uffff\1\4\2\uffff"+
			"\1\5\5\uffff\2\4\7\uffff\1\4\1\uffff\1\4",
			"\1\13",
			"\1\16\27\uffff\1\14\1\uffff\1\17\37\uffff\1\15",
			"\1\20",
			"\1\21",
			"\1\22",
			"\1\23",
			"\1\11\6\uffff\1\10\21\uffff\1\7",
			"\1\11\6\uffff\1\10\21\uffff\1\7",
			"\1\11\6\uffff\1\10\21\uffff\1\7",
			"\1\11\6\uffff\1\10\21\uffff\1\7"
	};

	static final short[] DFA38_eot = DFA.unpackEncodedString(DFA38_eotS);
	static final short[] DFA38_eof = DFA.unpackEncodedString(DFA38_eofS);
	static final char[] DFA38_min = DFA.unpackEncodedStringToUnsignedChars(DFA38_minS);
	static final char[] DFA38_max = DFA.unpackEncodedStringToUnsignedChars(DFA38_maxS);
	static final short[] DFA38_accept = DFA.unpackEncodedString(DFA38_acceptS);
	static final short[] DFA38_special = DFA.unpackEncodedString(DFA38_specialS);
	static final short[][] DFA38_transition;

	static {
		int numStates = DFA38_transitionS.length;
		DFA38_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA38_transition[i] = DFA.unpackEncodedString(DFA38_transitionS[i]);
		}
	}

	protected class DFA38 extends DFA {

		public DFA38(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 38;
			this.eot = DFA38_eot;
			this.eof = DFA38_eof;
			this.min = DFA38_min;
			this.max = DFA38_max;
			this.accept = DFA38_accept;
			this.special = DFA38_special;
			this.transition = DFA38_transition;
		}
		@Override
		public String getDescription() {
			return "796:1: alternative : ( ^( ALT ( elementOptions )? ( element )+ ) | ^( ALT ( elementOptions )? EPSILON ) );";
		}
	}

	public static final BitSet FOLLOW_GRAMMAR_in_grammarSpec85 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_grammarSpec87 = new BitSet(new long[]{0x0000040020002800L,0x0000000100000002L});
	public static final BitSet FOLLOW_prequelConstructs_in_grammarSpec106 = new BitSet(new long[]{0x0000000000000000L,0x0000000100000000L});
	public static final BitSet FOLLOW_rules_in_grammarSpec123 = new BitSet(new long[]{0x0000001000000008L});
	public static final BitSet FOLLOW_mode_in_grammarSpec125 = new BitSet(new long[]{0x0000001000000008L});
	public static final BitSet FOLLOW_prequelConstruct_in_prequelConstructs167 = new BitSet(new long[]{0x0000040020002802L,0x0000000000000002L});
	public static final BitSet FOLLOW_optionsSpec_in_prequelConstruct194 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_delegateGrammars_in_prequelConstruct204 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_tokensSpec_in_prequelConstruct214 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_channelsSpec_in_prequelConstruct224 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_action_in_prequelConstruct234 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_OPTIONS_in_optionsSpec259 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_option_in_optionsSpec261 = new BitSet(new long[]{0x0000000000000408L});
	public static final BitSet FOLLOW_ASSIGN_in_option295 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_option297 = new BitSet(new long[]{0x4000000050000000L});
	public static final BitSet FOLLOW_optionValue_in_option301 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_IMPORT_in_delegateGrammars389 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_delegateGrammar_in_delegateGrammars391 = new BitSet(new long[]{0x0000000010000408L});
	public static final BitSet FOLLOW_ASSIGN_in_delegateGrammar420 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_delegateGrammar424 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_ID_in_delegateGrammar428 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ID_in_delegateGrammar443 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKENS_SPEC_in_tokensSpec477 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_tokenSpec_in_tokensSpec479 = new BitSet(new long[]{0x0000000010000008L});
	public static final BitSet FOLLOW_ID_in_tokenSpec502 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CHANNELS_in_channelsSpec532 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_channelSpec_in_channelsSpec534 = new BitSet(new long[]{0x0000000010000008L});
	public static final BitSet FOLLOW_ID_in_channelSpec557 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_AT_in_action585 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_action589 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_ID_in_action594 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_action596 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_RULES_in_rules624 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_rule_in_rules629 = new BitSet(new long[]{0x0000000000000008L,0x0000000020000000L});
	public static final BitSet FOLLOW_lexerRule_in_rules631 = new BitSet(new long[]{0x0000000000000008L,0x0000000020000000L});
	public static final BitSet FOLLOW_MODE_in_mode662 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_mode664 = new BitSet(new long[]{0x0000000000000008L,0x0000000020000000L});
	public static final BitSet FOLLOW_lexerRule_in_mode668 = new BitSet(new long[]{0x0000000000000008L,0x0000000020000000L});
	public static final BitSet FOLLOW_RULE_in_lexerRule694 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TOKEN_REF_in_lexerRule696 = new BitSet(new long[]{0x0000000000000000L,0x0000000080002000L});
	public static final BitSet FOLLOW_RULEMODIFIERS_in_lexerRule708 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_FRAGMENT_in_lexerRule712 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_lexerRuleBlock_in_lexerRule737 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_RULE_in_rule782 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_RULE_REF_in_rule784 = new BitSet(new long[]{0x0080040200000900L,0x0000000080002001L});
	public static final BitSet FOLLOW_RULEMODIFIERS_in_rule793 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ruleModifier_in_rule798 = new BitSet(new long[]{0x0007000001000008L});
	public static final BitSet FOLLOW_ARG_ACTION_in_rule809 = new BitSet(new long[]{0x0080040200000800L,0x0000000000002001L});
	public static final BitSet FOLLOW_ruleReturns_in_rule822 = new BitSet(new long[]{0x0000040200000800L,0x0000000000002001L});
	public static final BitSet FOLLOW_throwsSpec_in_rule835 = new BitSet(new long[]{0x0000040200000800L,0x0000000000002000L});
	public static final BitSet FOLLOW_locals_in_rule848 = new BitSet(new long[]{0x0000040000000800L,0x0000000000002000L});
	public static final BitSet FOLLOW_optionsSpec_in_rule863 = new BitSet(new long[]{0x0000040000000800L,0x0000000000002000L});
	public static final BitSet FOLLOW_ruleAction_in_rule877 = new BitSet(new long[]{0x0000040000000800L,0x0000000000002000L});
	public static final BitSet FOLLOW_ruleBlock_in_rule908 = new BitSet(new long[]{0x0000000000801008L});
	public static final BitSet FOLLOW_exceptionGroup_in_rule910 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_exceptionHandler_in_exceptionGroup957 = new BitSet(new long[]{0x0000000000801002L});
	public static final BitSet FOLLOW_finallyClause_in_exceptionGroup960 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_CATCH_in_exceptionHandler986 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_exceptionHandler988 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_exceptionHandler990 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_FINALLY_in_finallyClause1015 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ACTION_in_finallyClause1017 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_LOCALS_in_locals1045 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_locals1047 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_RETURNS_in_ruleReturns1070 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_ruleReturns1072 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_THROWS_in_throwsSpec1098 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_throwsSpec1100 = new BitSet(new long[]{0x0000000010000008L});
	public static final BitSet FOLLOW_AT_in_ruleAction1127 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_ruleAction1129 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_ruleAction1131 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_BLOCK_in_lexerRuleBlock1209 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_lexerOuterAlternative_in_lexerRuleBlock1228 = new BitSet(new long[]{0x0000000000000008L,0x0000000000400200L});
	public static final BitSet FOLLOW_BLOCK_in_ruleBlock1273 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_outerAlternative_in_ruleBlock1292 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
	public static final BitSet FOLLOW_lexerAlternative_in_lexerOuterAlternative1332 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_alternative_in_outerAlternative1354 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEXER_ALT_ACTION_in_lexerAlternative1376 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_lexerElements_in_lexerAlternative1378 = new BitSet(new long[]{0x0000000010000000L,0x0000000000200000L});
	public static final BitSet FOLLOW_lexerCommand_in_lexerAlternative1380 = new BitSet(new long[]{0x0000000010000008L,0x0000000000200000L});
	public static final BitSet FOLLOW_lexerElements_in_lexerAlternative1392 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ALT_in_lexerElements1420 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_lexerElement_in_lexerElements1422 = new BitSet(new long[]{0x4A10408100000418L,0x0000000A0304A004L});
	public static final BitSet FOLLOW_labeledLexerElement_in_lexerElement1448 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerAtom_in_lexerElement1453 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_lexerSubrule_in_lexerElement1458 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_lexerElement1465 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMPRED_in_lexerElement1479 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_lexerElement1494 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_lexerElement1496 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_SEMPRED_in_lexerElement1507 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_lexerElement1509 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_EPSILON_in_lexerElement1517 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_set_in_labeledLexerElement1544 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledLexerElement1550 = new BitSet(new long[]{0x4210008100000000L,0x0000000A00002004L});
	public static final BitSet FOLLOW_lexerAtom_in_labeledLexerElement1553 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_block_in_labeledLexerElement1555 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_BLOCK_in_lexerBlock1580 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_optionsSpec_in_lexerBlock1582 = new BitSet(new long[]{0x0000000000000000L,0x0000000000400200L});
	public static final BitSet FOLLOW_lexerAlternative_in_lexerBlock1585 = new BitSet(new long[]{0x0000000000000008L,0x0000000000400200L});
	public static final BitSet FOLLOW_terminal_in_lexerAtom1616 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_lexerAtom1627 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_blockSet_in_lexerAtom1629 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_blockSet_in_lexerAtom1640 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_WILDCARD_in_lexerAtom1651 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_lexerAtom1653 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_WILDCARD_in_lexerAtom1664 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_LEXER_CHAR_SET_in_lexerAtom1672 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_range_in_lexerAtom1682 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ruleref_in_lexerAtom1692 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_actionElement1716 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_actionElement1724 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_actionElement1726 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_SEMPRED_in_actionElement1734 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMPRED_in_actionElement1742 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_actionElement1744 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ALT_in_alternative1767 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_alternative1769 = new BitSet(new long[]{0x4A00408000100410L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_element_in_alternative1772 = new BitSet(new long[]{0x4A00408000100418L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_ALT_in_alternative1780 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_alternative1782 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
	public static final BitSet FOLLOW_EPSILON_in_alternative1785 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_LEXER_ACTION_CALL_in_lexerCommand1811 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_lexerCommand1813 = new BitSet(new long[]{0x0000000050000000L});
	public static final BitSet FOLLOW_lexerCommandExpr_in_lexerCommand1815 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ID_in_lexerCommand1831 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_labeledElement_in_element1888 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_element1893 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_subrule_in_element1898 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_element1905 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMPRED_in_element1919 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_element1934 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_element1936 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_SEMPRED_in_element1947 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_element1949 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_NOT_in_element1959 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_blockSet_in_element1961 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_NOT_in_element1968 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_element1970 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_atom_in_astOperand1992 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_NOT_in_astOperand1998 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_blockSet_in_astOperand2000 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_NOT_in_astOperand2007 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_astOperand2009 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_set_in_labeledElement2032 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledElement2038 = new BitSet(new long[]{0x4A00408000100410L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_element_in_labeledElement2040 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_blockSuffix_in_subrule2065 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_subrule2067 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_block_in_subrule2074 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_blockSuffix_in_lexerSubrule2099 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_lexerBlock_in_lexerSubrule2101 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_lexerBlock_in_lexerSubrule2108 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_blockSuffix2135 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_atom2196 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_atom2198 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_terminal_in_atom2200 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_DOT_in_atom2207 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_atom2209 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_ruleref_in_atom2211 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_WILDCARD_in_atom2221 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_atom2223 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_WILDCARD_in_atom2234 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_terminal_in_atom2250 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_blockSet_in_atom2258 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ruleref_in_atom2268 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SET_in_blockSet2293 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet2295 = new BitSet(new long[]{0x4010000100000008L,0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement2319 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_setElement2321 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_TOKEN_REF_in_setElement2333 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_setElement2335 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement2345 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_setElement2370 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RANGE_in_setElement2399 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement2403 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement2407 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_LEXER_CHAR_SET_in_setElement2430 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BLOCK_in_block2455 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_optionsSpec_in_block2457 = new BitSet(new long[]{0x0000000000000810L,0x0000000000000200L});
	public static final BitSet FOLLOW_ruleAction_in_block2460 = new BitSet(new long[]{0x0000000000000810L,0x0000000000000200L});
	public static final BitSet FOLLOW_ACTION_in_block2463 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
	public static final BitSet FOLLOW_alternative_in_block2466 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
	public static final BitSet FOLLOW_RULE_REF_in_ruleref2496 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_ruleref2500 = new BitSet(new long[]{0x0000000000000008L,0x0000000000020000L});
	public static final BitSet FOLLOW_elementOptions_in_ruleref2503 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_RANGE_in_range2540 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range2542 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range2544 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_terminal2574 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_terminal2576 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_terminal2599 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal2613 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_terminal2615 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal2626 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOptions2663 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOption_in_elementOptions2665 = new BitSet(new long[]{0x0000000010000408L});
	public static final BitSet FOLLOW_ID_in_elementOption2696 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption2716 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption2720 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_ID_in_elementOption2724 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption2740 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption2742 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption2746 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption2760 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption2762 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_elementOption2766 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption2782 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption2784 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_INT_in_elementOption2788 = new BitSet(new long[]{0x0000000000000008L});
}
