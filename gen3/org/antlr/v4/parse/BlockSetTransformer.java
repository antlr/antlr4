// $ANTLR 3.5.2 /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g 2015-06-23 21:59:57

package org.antlr.v4.parse;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.misc.*;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import org.antlr.v4.runtime.misc.IntervalSet;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


@SuppressWarnings("all")
public class BlockSetTransformer extends TreeRewriter {
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
	public TreeRewriter[] getDelegates() {
		return new TreeRewriter[] {};
	}

	// delegators


	public BlockSetTransformer(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public BlockSetTransformer(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	protected TreeAdaptor adaptor = new CommonTreeAdaptor();

	public void setTreeAdaptor(TreeAdaptor adaptor) {
		this.adaptor = adaptor;
	}
	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
	@Override public String[] getTokenNames() { return BlockSetTransformer.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g"; }


	public String currentRuleName;
	public GrammarAST currentAlt;
	public Grammar g;
	public BlockSetTransformer(TreeNodeStream input, Grammar g) {
	    this(input, new RecognizerSharedState());
	    this.g = g;
	}


	public static class topdown_return extends TreeRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "topdown"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:63:1: topdown : ( ^( RULE (id= TOKEN_REF |id= RULE_REF ) ( . )+ ) | setAlt | ebnfBlockSet | blockSet );
	@Override
	public final BlockSetTransformer.topdown_return topdown() throws RecognitionException {
		BlockSetTransformer.topdown_return retval = new BlockSetTransformer.topdown_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		GrammarAST id=null;
		GrammarAST RULE1=null;
		GrammarAST wildcard2=null;
		TreeRuleReturnScope setAlt3 =null;
		TreeRuleReturnScope ebnfBlockSet4 =null;
		TreeRuleReturnScope blockSet5 =null;

		GrammarAST id_tree=null;
		GrammarAST RULE1_tree=null;
		GrammarAST wildcard2_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:64:5: ( ^( RULE (id= TOKEN_REF |id= RULE_REF ) ( . )+ ) | setAlt | ebnfBlockSet | blockSet )
			int alt3=4;
			switch ( input.LA(1) ) {
			case RULE:
				{
				alt3=1;
				}
				break;
			case ALT:
				{
				alt3=2;
				}
				break;
			case CLOSURE:
			case OPTIONAL:
			case POSITIVE_CLOSURE:
				{
				alt3=3;
				}
				break;
			case BLOCK:
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
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:64:7: ^( RULE (id= TOKEN_REF |id= RULE_REF ) ( . )+ )
					{
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					RULE1=(GrammarAST)match(input,RULE,FOLLOW_RULE_in_topdown86); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = RULE1;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:64:14: (id= TOKEN_REF |id= RULE_REF )
					int alt1=2;
					int LA1_0 = input.LA(1);
					if ( (LA1_0==TOKEN_REF) ) {
						alt1=1;
					}
					else if ( (LA1_0==RULE_REF) ) {
						alt1=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						NoViableAltException nvae =
							new NoViableAltException("", 1, 0, input);
						throw nvae;
					}

					switch (alt1) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:64:15: id= TOKEN_REF
							{
							_last = (GrammarAST)input.LT(1);
							id=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_topdown91); if (state.failed) return retval;
							 
							if ( state.backtracking==1 )
							if ( _first_1==null ) _first_1 = id;

							if ( state.backtracking==1 ) {
							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
							}

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:64:28: id= RULE_REF
							{
							_last = (GrammarAST)input.LT(1);
							id=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_topdown95); if (state.failed) return retval;
							 
							if ( state.backtracking==1 )
							if ( _first_1==null ) _first_1 = id;

							if ( state.backtracking==1 ) {
							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
							}

							}
							break;

					}

					if ( state.backtracking==1 ) {currentRuleName=(id!=null?id.getText():null);}
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:64:69: ( . )+
					int cnt2=0;
					loop2:
					while (true) {
						int alt2=2;
						int LA2_0 = input.LA(1);
						if ( ((LA2_0 >= ACTION && LA2_0 <= WILDCARD)) ) {
							alt2=1;
						}
						else if ( (LA2_0==UP) ) {
							alt2=2;
						}

						switch (alt2) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:64:69: .
							{
							_last = (GrammarAST)input.LT(1);
							wildcard2=(GrammarAST)input.LT(1);
							matchAny(input); if (state.failed) return retval;
							 
							if ( state.backtracking==1 )
							if ( _first_1==null ) _first_1 = wildcard2;

							if ( state.backtracking==1 ) {
							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
							}

							}
							break;

						default :
							if ( cnt2 >= 1 ) break loop2;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(2, input);
							throw eee;
						}
						cnt2++;
					}

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:65:7: setAlt
					{
					_last = (GrammarAST)input.LT(1);
					pushFollow(FOLLOW_setAlt_in_topdown110);
					setAlt3=setAlt();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==1 ) 
					 
					if ( _first_0==null ) _first_0 = (GrammarAST)setAlt3.getTree();

					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:66:7: ebnfBlockSet
					{
					_last = (GrammarAST)input.LT(1);
					pushFollow(FOLLOW_ebnfBlockSet_in_topdown118);
					ebnfBlockSet4=ebnfBlockSet();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==1 ) 
					 
					if ( _first_0==null ) _first_0 = (GrammarAST)ebnfBlockSet4.getTree();

					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:67:7: blockSet
					{
					_last = (GrammarAST)input.LT(1);
					pushFollow(FOLLOW_blockSet_in_topdown126);
					blockSet5=blockSet();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==1 ) 
					 
					if ( _first_0==null ) _first_0 = (GrammarAST)blockSet5.getTree();

					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

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
	// $ANTLR end "topdown"


	public static class setAlt_return extends TreeRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "setAlt"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:70:1: setAlt :{...}? ALT ;
	public final BlockSetTransformer.setAlt_return setAlt() throws RecognitionException {
		BlockSetTransformer.setAlt_return retval = new BlockSetTransformer.setAlt_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		GrammarAST ALT6=null;

		GrammarAST ALT6_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:71:2: ({...}? ALT )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:71:4: {...}? ALT
			{
			if ( !((inContext("RULE BLOCK"))) ) {
				if (state.backtracking>0) {state.failed=true; return retval;}
				throw new FailedPredicateException(input, "setAlt", "inContext(\"RULE BLOCK\")");
			}
			_last = (GrammarAST)input.LT(1);
			ALT6=(GrammarAST)match(input,ALT,FOLLOW_ALT_in_setAlt141); if (state.failed) return retval;
			 
			if ( state.backtracking==1 )
			if ( _first_0==null ) _first_0 = ALT6;

			if ( state.backtracking==1 ) {currentAlt = ((GrammarAST)retval.start);}
			if ( state.backtracking==1 ) {
			retval.tree = _first_0;
			if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
				retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
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
	// $ANTLR end "setAlt"


	public static class ebnfBlockSet_return extends TreeRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ebnfBlockSet"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:76:1: ebnfBlockSet : ^( ebnfSuffix blockSet ) -> ^( ebnfSuffix ^( BLOCK ^( ALT blockSet ) ) ) ;
	public final BlockSetTransformer.ebnfBlockSet_return ebnfBlockSet() throws RecognitionException {
		BlockSetTransformer.ebnfBlockSet_return retval = new BlockSetTransformer.ebnfBlockSet_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		TreeRuleReturnScope ebnfSuffix7 =null;
		TreeRuleReturnScope blockSet8 =null;

		RewriteRuleSubtreeStream stream_ebnfSuffix=new RewriteRuleSubtreeStream(adaptor,"rule ebnfSuffix");
		RewriteRuleSubtreeStream stream_blockSet=new RewriteRuleSubtreeStream(adaptor,"rule blockSet");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:80:2: ( ^( ebnfSuffix blockSet ) -> ^( ebnfSuffix ^( BLOCK ^( ALT blockSet ) ) ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:80:4: ^( ebnfSuffix blockSet )
			{
			_last = (GrammarAST)input.LT(1);
			{
			GrammarAST _save_last_1 = _last;
			GrammarAST _first_1 = null;
			_last = (GrammarAST)input.LT(1);
			pushFollow(FOLLOW_ebnfSuffix_in_ebnfBlockSet161);
			ebnfSuffix7=ebnfSuffix();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==1 ) stream_ebnfSuffix.add(ebnfSuffix7.getTree());
			if ( state.backtracking==1 )
			if ( _first_0==null ) _first_0 = (GrammarAST)ebnfSuffix7.getTree();
			match(input, Token.DOWN, null); if (state.failed) return retval;
			_last = (GrammarAST)input.LT(1);
			pushFollow(FOLLOW_blockSet_in_ebnfBlockSet163);
			blockSet8=blockSet();
			state._fsp--;
			if (state.failed) return retval;
			if ( state.backtracking==1 ) stream_blockSet.add(blockSet8.getTree());
			match(input, Token.UP, null); if (state.failed) return retval;
			_last = _save_last_1;
			}


			// AST REWRITE
			// elements: blockSet, ebnfSuffix
			// token labels: 
			// rule labels: retval
			// token list labels: 
			// rule list labels: 
			// wildcard labels: 
			if ( state.backtracking==1 ) {
			retval.tree = root_0;
			RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

			root_0 = (GrammarAST)adaptor.nil();
			// 80:27: -> ^( ebnfSuffix ^( BLOCK ^( ALT blockSet ) ) )
			{
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:80:30: ^( ebnfSuffix ^( BLOCK ^( ALT blockSet ) ) )
				{
				GrammarAST root_1 = (GrammarAST)adaptor.nil();
				root_1 = (GrammarAST)adaptor.becomeRoot(stream_ebnfSuffix.nextNode(), root_1);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:80:43: ^( BLOCK ^( ALT blockSet ) )
				{
				GrammarAST root_2 = (GrammarAST)adaptor.nil();
				root_2 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK), root_2);
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:80:61: ^( ALT blockSet )
				{
				GrammarAST root_3 = (GrammarAST)adaptor.nil();
				root_3 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT), root_3);
				adaptor.addChild(root_3, stream_blockSet.nextTree());
				adaptor.addChild(root_2, root_3);
				}

				adaptor.addChild(root_1, root_2);
				}

				adaptor.addChild(root_0, root_1);
				}

			}


			retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
			input.replaceChildren(adaptor.getParent(retval.start),
								  adaptor.getChildIndex(retval.start),
								  adaptor.getChildIndex(_last),
								  retval.tree);
			}

			}

			if ( state.backtracking==1 ) {
				GrammarTransformPipeline.setGrammarPtr(g, retval.tree);
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
	// $ANTLR end "ebnfBlockSet"


	public static class ebnfSuffix_return extends TreeRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "ebnfSuffix"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:83:1: ebnfSuffix : ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE );
	public final BlockSetTransformer.ebnfSuffix_return ebnfSuffix() throws RecognitionException {
		BlockSetTransformer.ebnfSuffix_return retval = new BlockSetTransformer.ebnfSuffix_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		GrammarAST set9=null;

		GrammarAST set9_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:85:2: ( OPTIONAL | CLOSURE | POSITIVE_CLOSURE )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:
			{
			_last = (GrammarAST)input.LT(1);
			set9=(GrammarAST)input.LT(1);
			if ( input.LA(1)==CLOSURE||(input.LA(1) >= OPTIONAL && input.LA(1) <= POSITIVE_CLOSURE) ) {
				input.consume();
				state.errorRecovery=false;
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				throw mse;
			}

			if ( state.backtracking==1 ) {
			retval.tree = _first_0;
			if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
				retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
			}
			 

			}

			if ( state.backtracking==1 ) {retval.tree = (GrammarAST)adaptor.dupNode(((GrammarAST)retval.start));}
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


	public static class blockSet_return extends TreeRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "blockSet"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:90:1: blockSet : ({...}? ^( BLOCK ^(alt= ALT ( elementOptions )? {...}? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ ) -> ^( BLOCK[$BLOCK.token] ^( ALT[$BLOCK.token,\"ALT\"] ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) ) ) |{...}? ^( BLOCK ^( ALT ( elementOptions )? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ ) -> ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) );
	public final BlockSetTransformer.blockSet_return blockSet() throws RecognitionException {
		BlockSetTransformer.blockSet_return retval = new BlockSetTransformer.blockSet_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		GrammarAST alt=null;
		GrammarAST BLOCK10=null;
		GrammarAST ALT13=null;
		GrammarAST BLOCK16=null;
		GrammarAST ALT17=null;
		GrammarAST ALT20=null;
		TreeRuleReturnScope elementOptions11 =null;
		TreeRuleReturnScope setElement12 =null;
		TreeRuleReturnScope elementOptions14 =null;
		TreeRuleReturnScope setElement15 =null;
		TreeRuleReturnScope elementOptions18 =null;
		TreeRuleReturnScope setElement19 =null;
		TreeRuleReturnScope elementOptions21 =null;
		TreeRuleReturnScope setElement22 =null;

		GrammarAST alt_tree=null;
		GrammarAST BLOCK10_tree=null;
		GrammarAST ALT13_tree=null;
		GrammarAST BLOCK16_tree=null;
		GrammarAST ALT17_tree=null;
		GrammarAST ALT20_tree=null;
		RewriteRuleNodeStream stream_ALT=new RewriteRuleNodeStream(adaptor,"token ALT");
		RewriteRuleNodeStream stream_BLOCK=new RewriteRuleNodeStream(adaptor,"token BLOCK");
		RewriteRuleSubtreeStream stream_setElement=new RewriteRuleSubtreeStream(adaptor,"rule setElement");
		RewriteRuleSubtreeStream stream_elementOptions=new RewriteRuleSubtreeStream(adaptor,"rule elementOptions");


		boolean inLexer = Grammar.isTokenName(currentRuleName);

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:97:2: ({...}? ^( BLOCK ^(alt= ALT ( elementOptions )? {...}? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ ) -> ^( BLOCK[$BLOCK.token] ^( ALT[$BLOCK.token,\"ALT\"] ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) ) ) |{...}? ^( BLOCK ^( ALT ( elementOptions )? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ ) -> ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) )
			int alt10=2;
			alt10 = dfa10.predict(input);
			switch (alt10) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:97:4: {...}? ^( BLOCK ^(alt= ALT ( elementOptions )? {...}? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ )
					{
					if ( !((inContext("RULE"))) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "blockSet", "inContext(\"RULE\")");
					}
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					BLOCK10=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_blockSet244); if (state.failed) return retval;
					 
					if ( state.backtracking==1 ) stream_BLOCK.add(BLOCK10);

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = BLOCK10;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_2 = _last;
					GrammarAST _first_2 = null;
					_last = (GrammarAST)input.LT(1);
					alt=(GrammarAST)match(input,ALT,FOLLOW_ALT_in_blockSet249); if (state.failed) return retval;
					 
					if ( state.backtracking==1 ) stream_ALT.add(alt);

					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = alt;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:98:21: ( elementOptions )?
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==ELEMENT_OPTIONS) ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:98:21: elementOptions
							{
							_last = (GrammarAST)input.LT(1);
							pushFollow(FOLLOW_elementOptions_in_blockSet251);
							elementOptions11=elementOptions();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==1 ) stream_elementOptions.add(elementOptions11.getTree());
							if ( state.backtracking==1 ) {
							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
							}

							}
							break;

					}

					if ( !((((AltAST)alt).altLabel==null)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "blockSet", "((AltAST)$alt).altLabel==null");
					}
					_last = (GrammarAST)input.LT(1);
					pushFollow(FOLLOW_setElement_in_blockSet256);
					setElement12=setElement(inLexer);
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==1 ) stream_setElement.add(setElement12.getTree());
					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_2;
					}


					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:98:91: ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+
					int cnt6=0;
					loop6:
					while (true) {
						int alt6=2;
						int LA6_0 = input.LA(1);
						if ( (LA6_0==ALT) ) {
							alt6=1;
						}

						switch (alt6) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:98:93: ^( ALT ( elementOptions )? setElement[inLexer] )
							{
							_last = (GrammarAST)input.LT(1);
							{
							GrammarAST _save_last_2 = _last;
							GrammarAST _first_2 = null;
							_last = (GrammarAST)input.LT(1);
							ALT13=(GrammarAST)match(input,ALT,FOLLOW_ALT_in_blockSet263); if (state.failed) return retval;
							 
							if ( state.backtracking==1 ) stream_ALT.add(ALT13);

							if ( state.backtracking==1 )
							if ( _first_1==null ) _first_1 = ALT13;
							match(input, Token.DOWN, null); if (state.failed) return retval;
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:98:99: ( elementOptions )?
							int alt5=2;
							int LA5_0 = input.LA(1);
							if ( (LA5_0==ELEMENT_OPTIONS) ) {
								alt5=1;
							}
							switch (alt5) {
								case 1 :
									// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:98:99: elementOptions
									{
									_last = (GrammarAST)input.LT(1);
									pushFollow(FOLLOW_elementOptions_in_blockSet265);
									elementOptions14=elementOptions();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==1 ) stream_elementOptions.add(elementOptions14.getTree());
									if ( state.backtracking==1 ) {
									retval.tree = _first_0;
									if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
										retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
									}

									}
									break;

							}

							_last = (GrammarAST)input.LT(1);
							pushFollow(FOLLOW_setElement_in_blockSet268);
							setElement15=setElement(inLexer);
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==1 ) stream_setElement.add(setElement15.getTree());
							match(input, Token.UP, null); if (state.failed) return retval;
							_last = _save_last_2;
							}


							if ( state.backtracking==1 ) {
							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
							}

							}
							break;

						default :
							if ( cnt6 >= 1 ) break loop6;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(6, input);
							throw eee;
						}
						cnt6++;
					}

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					// AST REWRITE
					// elements: ALT, setElement, BLOCK
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==1 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 99:3: -> ^( BLOCK[$BLOCK.token] ^( ALT[$BLOCK.token,\"ALT\"] ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) ) )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:99:6: ^( BLOCK[$BLOCK.token] ^( ALT[$BLOCK.token,\"ALT\"] ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) ) )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot(new BlockAST(BLOCK, BLOCK10.token), root_1);
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:99:38: ^( ALT[$BLOCK.token,\"ALT\"] ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) )
						{
						GrammarAST root_2 = (GrammarAST)adaptor.nil();
						root_2 = (GrammarAST)adaptor.becomeRoot(new AltAST(ALT, BLOCK10.token, "ALT"), root_2);
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:99:72: ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ )
						{
						GrammarAST root_3 = (GrammarAST)adaptor.nil();
						root_3 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(SET, BLOCK10.token, "SET"), root_3);
						if ( !(stream_setElement.hasNext()) ) {
							throw new RewriteEarlyExitException();
						}
						while ( stream_setElement.hasNext() ) {
							adaptor.addChild(root_3, stream_setElement.nextTree());
						}
						stream_setElement.reset();

						adaptor.addChild(root_2, root_3);
						}

						adaptor.addChild(root_1, root_2);
						}

						adaptor.addChild(root_0, root_1);
						}

					}


					retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
					input.replaceChildren(adaptor.getParent(retval.start),
										  adaptor.getChildIndex(retval.start),
										  adaptor.getChildIndex(_last),
										  retval.tree);
					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:100:4: {...}? ^( BLOCK ^( ALT ( elementOptions )? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ )
					{
					if ( !((!inContext("RULE"))) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "blockSet", "!inContext(\"RULE\")");
					}
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					BLOCK16=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_blockSet313); if (state.failed) return retval;
					 
					if ( state.backtracking==1 ) stream_BLOCK.add(BLOCK16);

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = BLOCK16;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_2 = _last;
					GrammarAST _first_2 = null;
					_last = (GrammarAST)input.LT(1);
					ALT17=(GrammarAST)match(input,ALT,FOLLOW_ALT_in_blockSet316); if (state.failed) return retval;
					 
					if ( state.backtracking==1 ) stream_ALT.add(ALT17);

					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = ALT17;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:101:17: ( elementOptions )?
					int alt7=2;
					int LA7_0 = input.LA(1);
					if ( (LA7_0==ELEMENT_OPTIONS) ) {
						alt7=1;
					}
					switch (alt7) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:101:17: elementOptions
							{
							_last = (GrammarAST)input.LT(1);
							pushFollow(FOLLOW_elementOptions_in_blockSet318);
							elementOptions18=elementOptions();
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==1 ) stream_elementOptions.add(elementOptions18.getTree());
							if ( state.backtracking==1 ) {
							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
							}

							}
							break;

					}

					_last = (GrammarAST)input.LT(1);
					pushFollow(FOLLOW_setElement_in_blockSet321);
					setElement19=setElement(inLexer);
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==1 ) stream_setElement.add(setElement19.getTree());
					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_2;
					}


					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:101:54: ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+
					int cnt9=0;
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( (LA9_0==ALT) ) {
							alt9=1;
						}

						switch (alt9) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:101:56: ^( ALT ( elementOptions )? setElement[inLexer] )
							{
							_last = (GrammarAST)input.LT(1);
							{
							GrammarAST _save_last_2 = _last;
							GrammarAST _first_2 = null;
							_last = (GrammarAST)input.LT(1);
							ALT20=(GrammarAST)match(input,ALT,FOLLOW_ALT_in_blockSet328); if (state.failed) return retval;
							 
							if ( state.backtracking==1 ) stream_ALT.add(ALT20);

							if ( state.backtracking==1 )
							if ( _first_1==null ) _first_1 = ALT20;
							match(input, Token.DOWN, null); if (state.failed) return retval;
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:101:62: ( elementOptions )?
							int alt8=2;
							int LA8_0 = input.LA(1);
							if ( (LA8_0==ELEMENT_OPTIONS) ) {
								alt8=1;
							}
							switch (alt8) {
								case 1 :
									// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:101:62: elementOptions
									{
									_last = (GrammarAST)input.LT(1);
									pushFollow(FOLLOW_elementOptions_in_blockSet330);
									elementOptions21=elementOptions();
									state._fsp--;
									if (state.failed) return retval;
									if ( state.backtracking==1 ) stream_elementOptions.add(elementOptions21.getTree());
									if ( state.backtracking==1 ) {
									retval.tree = _first_0;
									if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
										retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
									}

									}
									break;

							}

							_last = (GrammarAST)input.LT(1);
							pushFollow(FOLLOW_setElement_in_blockSet333);
							setElement22=setElement(inLexer);
							state._fsp--;
							if (state.failed) return retval;
							if ( state.backtracking==1 ) stream_setElement.add(setElement22.getTree());
							match(input, Token.UP, null); if (state.failed) return retval;
							_last = _save_last_2;
							}


							if ( state.backtracking==1 ) {
							retval.tree = _first_0;
							if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
								retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
							}

							}
							break;

						default :
							if ( cnt9 >= 1 ) break loop9;
							if (state.backtracking>0) {state.failed=true; return retval;}
							EarlyExitException eee = new EarlyExitException(9, input);
							throw eee;
						}
						cnt9++;
					}

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					// AST REWRITE
					// elements: setElement
					// token labels: 
					// rule labels: retval
					// token list labels: 
					// rule list labels: 
					// wildcard labels: 
					if ( state.backtracking==1 ) {
					retval.tree = root_0;
					RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.getTree():null);

					root_0 = (GrammarAST)adaptor.nil();
					// 102:3: -> ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ )
					{
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:102:6: ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ )
						{
						GrammarAST root_1 = (GrammarAST)adaptor.nil();
						root_1 = (GrammarAST)adaptor.becomeRoot((GrammarAST)adaptor.create(SET, BLOCK16.token, "SET"), root_1);
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


					retval.tree = (GrammarAST)adaptor.rulePostProcessing(root_0);
					input.replaceChildren(adaptor.getParent(retval.start),
										  adaptor.getChildIndex(retval.start),
										  adaptor.getChildIndex(_last),
										  retval.tree);
					}

					}
					break;

			}
			if ( state.backtracking==1 ) {
				GrammarTransformPipeline.setGrammarPtr(g, retval.tree);
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
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "setElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:105:1: setElement[boolean inLexer] : ( ^(a= STRING_LITERAL elementOptions ) {...}?|a= STRING_LITERAL {...}?|{...}? => ^( TOKEN_REF elementOptions ) |{...}? => TOKEN_REF |{...}? => ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) {...}?) ;
	public final BlockSetTransformer.setElement_return setElement(boolean inLexer) throws RecognitionException {
		BlockSetTransformer.setElement_return retval = new BlockSetTransformer.setElement_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		GrammarAST a=null;
		GrammarAST b=null;
		GrammarAST TOKEN_REF24=null;
		GrammarAST TOKEN_REF26=null;
		GrammarAST RANGE27=null;
		TreeRuleReturnScope elementOptions23 =null;
		TreeRuleReturnScope elementOptions25 =null;

		GrammarAST a_tree=null;
		GrammarAST b_tree=null;
		GrammarAST TOKEN_REF24_tree=null;
		GrammarAST TOKEN_REF26_tree=null;
		GrammarAST RANGE27_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:109:2: ( ( ^(a= STRING_LITERAL elementOptions ) {...}?|a= STRING_LITERAL {...}?|{...}? => ^( TOKEN_REF elementOptions ) |{...}? => TOKEN_REF |{...}? => ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) {...}?) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:109:4: ( ^(a= STRING_LITERAL elementOptions ) {...}?|a= STRING_LITERAL {...}?|{...}? => ^( TOKEN_REF elementOptions ) |{...}? => TOKEN_REF |{...}? => ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) {...}?)
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:109:4: ( ^(a= STRING_LITERAL elementOptions ) {...}?|a= STRING_LITERAL {...}?|{...}? => ^( TOKEN_REF elementOptions ) |{...}? => TOKEN_REF |{...}? => ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) {...}?)
			int alt11=5;
			int LA11_0 = input.LA(1);
			if ( (LA11_0==STRING_LITERAL) ) {
				int LA11_1 = input.LA(2);
				if ( (LA11_1==DOWN) ) {
					alt11=1;
				}
				else if ( (LA11_1==UP) ) {
					alt11=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
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
			else if ( (LA11_0==TOKEN_REF) && ((!inLexer))) {
				int LA11_2 = input.LA(2);
				if ( (LA11_2==DOWN) && ((!inLexer))) {
					alt11=3;
				}
				else if ( (LA11_2==UP) && ((!inLexer))) {
					alt11=4;
				}

			}
			else if ( (LA11_0==RANGE) && ((inLexer))) {
				alt11=5;
			}

			switch (alt11) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:109:6: ^(a= STRING_LITERAL elementOptions ) {...}?
					{
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement373); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = a;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					pushFollow(FOLLOW_elementOptions_in_setElement375);
					elementOptions23=elementOptions();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==1 ) 
					 
					if ( _first_1==null ) _first_1 = (GrammarAST)elementOptions23.getTree();

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( !((!inLexer || CharSupport.getCharValueFromGrammarCharLiteral(a.getText())!=-1)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "setElement", "!inLexer || CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1");
					}
					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:110:7: a= STRING_LITERAL {...}?
					{
					_last = (GrammarAST)input.LT(1);
					a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement388); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = a;

					if ( !((!inLexer || CharSupport.getCharValueFromGrammarCharLiteral(a.getText())!=-1)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "setElement", "!inLexer || CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1");
					}
					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:111:5: {...}? => ^( TOKEN_REF elementOptions )
					{
					if ( !((!inLexer)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "setElement", "!inLexer");
					}
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					TOKEN_REF24=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_setElement400); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = TOKEN_REF24;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					pushFollow(FOLLOW_elementOptions_in_setElement402);
					elementOptions25=elementOptions();
					state._fsp--;
					if (state.failed) return retval;
					if ( state.backtracking==1 ) 
					 
					if ( _first_1==null ) _first_1 = (GrammarAST)elementOptions25.getTree();

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:112:5: {...}? => TOKEN_REF
					{
					if ( !((!inLexer)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "setElement", "!inLexer");
					}
					_last = (GrammarAST)input.LT(1);
					TOKEN_REF26=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_setElement414); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = TOKEN_REF26;

					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:113:5: {...}? => ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) {...}?
					{
					if ( !((inLexer)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "setElement", "inLexer");
					}
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					RANGE27=(GrammarAST)match(input,RANGE,FOLLOW_RANGE_in_setElement425); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = RANGE27;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement429); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = a;

					_last = (GrammarAST)input.LT(1);
					b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_setElement433); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = b;

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( !((CharSupport.getCharValueFromGrammarCharLiteral(a.getText())!=-1 &&
								 CharSupport.getCharValueFromGrammarCharLiteral(b.getText())!=-1)) ) {
						if (state.backtracking>0) {state.failed=true; return retval;}
						throw new FailedPredicateException(input, "setElement", "CharSupport.getCharValueFromGrammarCharLiteral($a.getText())!=-1 &&\n\t\t\t CharSupport.getCharValueFromGrammarCharLiteral($b.getText())!=-1");
					}
					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;

			}

			if ( state.backtracking==1 ) {
			retval.tree = _first_0;
			if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
				retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
			}

			}

			if ( state.backtracking==1 ) {
				GrammarTransformPipeline.setGrammarPtr(g, retval.tree);
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


	public static class elementOptions_return extends TreeRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "elementOptions"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:119:1: elementOptions : ^( ELEMENT_OPTIONS ( elementOption )* ) ;
	public final BlockSetTransformer.elementOptions_return elementOptions() throws RecognitionException {
		BlockSetTransformer.elementOptions_return retval = new BlockSetTransformer.elementOptions_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		GrammarAST ELEMENT_OPTIONS28=null;
		TreeRuleReturnScope elementOption29 =null;

		GrammarAST ELEMENT_OPTIONS28_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:120:2: ( ^( ELEMENT_OPTIONS ( elementOption )* ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:120:4: ^( ELEMENT_OPTIONS ( elementOption )* )
			{
			_last = (GrammarAST)input.LT(1);
			{
			GrammarAST _save_last_1 = _last;
			GrammarAST _first_1 = null;
			_last = (GrammarAST)input.LT(1);
			ELEMENT_OPTIONS28=(GrammarAST)match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOptions455); if (state.failed) return retval;

			if ( state.backtracking==1 )
			if ( _first_0==null ) _first_0 = ELEMENT_OPTIONS28;
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); if (state.failed) return retval;
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:120:22: ( elementOption )*
				loop12:
				while (true) {
					int alt12=2;
					int LA12_0 = input.LA(1);
					if ( (LA12_0==ASSIGN||LA12_0==ID) ) {
						alt12=1;
					}

					switch (alt12) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:120:22: elementOption
						{
						_last = (GrammarAST)input.LT(1);
						pushFollow(FOLLOW_elementOption_in_elementOptions457);
						elementOption29=elementOption();
						state._fsp--;
						if (state.failed) return retval;
						if ( state.backtracking==1 ) 
						 
						if ( _first_1==null ) _first_1 = (GrammarAST)elementOption29.getTree();

						if ( state.backtracking==1 ) {
						retval.tree = _first_0;
						if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
							retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
						}

						}
						break;

					default :
						break loop12;
					}
				}

				match(input, Token.UP, null); if (state.failed) return retval;
			}
			_last = _save_last_1;
			}


			if ( state.backtracking==1 ) {
			retval.tree = _first_0;
			if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
				retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
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
	// $ANTLR end "elementOptions"


	public static class elementOption_return extends TreeRuleReturnScope {
		GrammarAST tree;
		@Override
		public GrammarAST getTree() { return tree; }
	};


	// $ANTLR start "elementOption"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:123:1: elementOption : ( ID | ^( ASSIGN id= ID v= ID ) | ^( ASSIGN ID v= STRING_LITERAL ) | ^( ASSIGN ID v= ACTION ) | ^( ASSIGN ID v= INT ) );
	public final BlockSetTransformer.elementOption_return elementOption() throws RecognitionException {
		BlockSetTransformer.elementOption_return retval = new BlockSetTransformer.elementOption_return();
		retval.start = input.LT(1);

		GrammarAST root_0 = null;

		GrammarAST _first_0 = null;
		GrammarAST _last = null;


		GrammarAST id=null;
		GrammarAST v=null;
		GrammarAST ID30=null;
		GrammarAST ASSIGN31=null;
		GrammarAST ASSIGN32=null;
		GrammarAST ID33=null;
		GrammarAST ASSIGN34=null;
		GrammarAST ID35=null;
		GrammarAST ASSIGN36=null;
		GrammarAST ID37=null;

		GrammarAST id_tree=null;
		GrammarAST v_tree=null;
		GrammarAST ID30_tree=null;
		GrammarAST ASSIGN31_tree=null;
		GrammarAST ASSIGN32_tree=null;
		GrammarAST ID33_tree=null;
		GrammarAST ASSIGN34_tree=null;
		GrammarAST ID35_tree=null;
		GrammarAST ASSIGN36_tree=null;
		GrammarAST ID37_tree=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:124:2: ( ID | ^( ASSIGN id= ID v= ID ) | ^( ASSIGN ID v= STRING_LITERAL ) | ^( ASSIGN ID v= ACTION ) | ^( ASSIGN ID v= INT ) )
			int alt13=5;
			int LA13_0 = input.LA(1);
			if ( (LA13_0==ID) ) {
				alt13=1;
			}
			else if ( (LA13_0==ASSIGN) ) {
				int LA13_2 = input.LA(2);
				if ( (LA13_2==DOWN) ) {
					int LA13_3 = input.LA(3);
					if ( (LA13_3==ID) ) {
						switch ( input.LA(4) ) {
						case ID:
							{
							alt13=2;
							}
							break;
						case STRING_LITERAL:
							{
							alt13=3;
							}
							break;
						case ACTION:
							{
							alt13=4;
							}
							break;
						case INT:
							{
							alt13=5;
							}
							break;
						default:
							if (state.backtracking>0) {state.failed=true; return retval;}
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 4, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}
					}

					else {
						if (state.backtracking>0) {state.failed=true; return retval;}
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 13, 3, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}

				else {
					if (state.backtracking>0) {state.failed=true; return retval;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 13, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				if (state.backtracking>0) {state.failed=true; return retval;}
				NoViableAltException nvae =
					new NoViableAltException("", 13, 0, input);
				throw nvae;
			}

			switch (alt13) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:124:4: ID
					{
					_last = (GrammarAST)input.LT(1);
					ID30=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption470); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = ID30;

					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:125:4: ^( ASSIGN id= ID v= ID )
					{
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					ASSIGN31=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption476); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = ASSIGN31;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					id=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption480); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = id;

					_last = (GrammarAST)input.LT(1);
					v=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption484); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = v;

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:126:4: ^( ASSIGN ID v= STRING_LITERAL )
					{
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					ASSIGN32=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption491); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = ASSIGN32;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					ID33=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption493); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = ID33;

					_last = (GrammarAST)input.LT(1);
					v=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption497); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = v;

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:127:4: ^( ASSIGN ID v= ACTION )
					{
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					ASSIGN34=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption504); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = ASSIGN34;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					ID35=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption506); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = ID35;

					_last = (GrammarAST)input.LT(1);
					v=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_elementOption510); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = v;

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/BlockSetTransformer.g:128:4: ^( ASSIGN ID v= INT )
					{
					_last = (GrammarAST)input.LT(1);
					{
					GrammarAST _save_last_1 = _last;
					GrammarAST _first_1 = null;
					_last = (GrammarAST)input.LT(1);
					ASSIGN36=(GrammarAST)match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption517); if (state.failed) return retval;

					if ( state.backtracking==1 )
					if ( _first_0==null ) _first_0 = ASSIGN36;
					match(input, Token.DOWN, null); if (state.failed) return retval;
					_last = (GrammarAST)input.LT(1);
					ID37=(GrammarAST)match(input,ID,FOLLOW_ID_in_elementOption519); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = ID37;

					_last = (GrammarAST)input.LT(1);
					v=(GrammarAST)match(input,INT,FOLLOW_INT_in_elementOption523); if (state.failed) return retval;
					 
					if ( state.backtracking==1 )
					if ( _first_1==null ) _first_1 = v;

					match(input, Token.UP, null); if (state.failed) return retval;
					_last = _save_last_1;
					}


					if ( state.backtracking==1 ) {
					retval.tree = _first_0;
					if ( adaptor.getParent(retval.tree)!=null && adaptor.isNil( adaptor.getParent(retval.tree) ) )
						retval.tree = (GrammarAST)adaptor.getParent(retval.tree);
					}

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
	// $ANTLR end "elementOption"

	// Delegated rules


	protected DFA10 dfa10 = new DFA10(this);
	static final String DFA10_eotS =
		"\174\uffff";
	static final String DFA10_eofS =
		"\174\uffff";
	static final String DFA10_minS =
		"\1\115\1\2\1\111\1\2\1\64\4\2\1\3\1\121\1\111\1\121\1\76\1\3\1\2\1\64"+
		"\3\2\1\76\1\34\1\3\1\64\2\3\1\4\1\3\1\2\1\3\4\2\1\3\1\2\6\3\1\34\2\3\1"+
		"\121\1\3\1\121\1\76\1\34\5\3\1\4\1\3\1\2\1\64\1\2\1\0\1\2\1\76\1\4\4\3"+
		"\1\34\1\3\2\uffff\12\3\1\4\1\3\1\2\2\3\1\2\12\3\1\34\1\3\1\34\5\3\2\4"+
		"\20\3";
	static final String DFA10_maxS =
		"\1\115\1\2\1\111\1\2\1\121\1\2\2\3\1\2\1\34\1\121\1\111\1\121\1\76\1\34"+
		"\1\2\1\102\3\2\1\76\2\34\1\121\1\34\1\3\1\76\1\34\1\2\1\3\1\2\2\3\1\2"+
		"\1\34\1\2\6\3\1\34\1\3\1\34\1\121\1\111\1\121\1\76\1\34\1\3\4\34\1\76"+
		"\1\34\1\2\1\102\1\2\1\0\1\2\2\76\4\3\2\34\2\uffff\1\34\5\3\4\34\1\76\1"+
		"\34\1\2\1\3\1\34\1\2\2\3\4\34\4\3\1\34\1\3\1\34\1\3\4\34\2\76\10\3\10"+
		"\34";
	static final String DFA10_acceptS =
		"\106\uffff\1\1\1\2\64\uffff";
	static final String DFA10_specialS =
		"\74\uffff\1\0\77\uffff}>";
	static final String[] DFA10_transitionS = {
			"\1\1",
			"\1\2",
			"\1\3",
			"\1\4",
			"\1\10\11\uffff\1\6\3\uffff\1\7\16\uffff\1\5",
			"\1\11",
			"\1\12\1\13",
			"\1\14\1\13",
			"\1\15",
			"\1\20\6\uffff\1\17\21\uffff\1\16",
			"\1\21",
			"\1\22",
			"\1\23",
			"\1\24",
			"\1\20\6\uffff\1\17\21\uffff\1\16",
			"\1\25",
			"\1\10\11\uffff\1\6\3\uffff\1\7",
			"\1\26",
			"\1\27",
			"\1\30",
			"\1\31",
			"\1\32",
			"\1\35\6\uffff\1\34\21\uffff\1\33",
			"\1\41\11\uffff\1\37\3\uffff\1\40\16\uffff\1\36",
			"\1\44\6\uffff\1\43\21\uffff\1\42",
			"\1\45",
			"\1\50\27\uffff\1\46\1\uffff\1\51\37\uffff\1\47",
			"\1\35\6\uffff\1\34\21\uffff\1\33",
			"\1\52",
			"\1\53",
			"\1\54",
			"\1\55\1\56",
			"\1\57\1\56",
			"\1\60",
			"\1\44\6\uffff\1\43\21\uffff\1\42",
			"\1\61",
			"\1\62",
			"\1\13",
			"\1\63",
			"\1\64",
			"\1\65",
			"\1\66",
			"\1\67",
			"\1\13",
			"\1\72\6\uffff\1\71\21\uffff\1\70",
			"\1\73",
			"\1\74\105\uffff\1\22",
			"\1\75",
			"\1\76",
			"\1\77",
			"\1\13",
			"\1\20\6\uffff\1\17\21\uffff\1\16",
			"\1\20\6\uffff\1\17\21\uffff\1\16",
			"\1\20\6\uffff\1\17\21\uffff\1\16",
			"\1\20\6\uffff\1\17\21\uffff\1\16",
			"\1\102\27\uffff\1\100\1\uffff\1\103\37\uffff\1\101",
			"\1\72\6\uffff\1\71\21\uffff\1\70",
			"\1\104",
			"\1\41\11\uffff\1\37\3\uffff\1\40",
			"\1\105",
			"\1\uffff",
			"\1\110",
			"\1\111",
			"\1\114\27\uffff\1\112\1\uffff\1\115\37\uffff\1\113",
			"\1\116",
			"\1\117",
			"\1\120",
			"\1\121",
			"\1\122",
			"\1\125\6\uffff\1\124\21\uffff\1\123",
			"",
			"",
			"\1\130\6\uffff\1\127\21\uffff\1\126",
			"\1\131",
			"\1\132",
			"\1\133",
			"\1\134",
			"\1\135",
			"\1\35\6\uffff\1\34\21\uffff\1\33",
			"\1\35\6\uffff\1\34\21\uffff\1\33",
			"\1\35\6\uffff\1\34\21\uffff\1\33",
			"\1\35\6\uffff\1\34\21\uffff\1\33",
			"\1\140\27\uffff\1\136\1\uffff\1\141\37\uffff\1\137",
			"\1\125\6\uffff\1\124\21\uffff\1\123",
			"\1\142",
			"\1\143",
			"\1\130\6\uffff\1\127\21\uffff\1\126",
			"\1\144",
			"\1\145",
			"\1\56",
			"\1\44\6\uffff\1\43\21\uffff\1\42",
			"\1\44\6\uffff\1\43\21\uffff\1\42",
			"\1\44\6\uffff\1\43\21\uffff\1\42",
			"\1\44\6\uffff\1\43\21\uffff\1\42",
			"\1\146",
			"\1\147",
			"\1\150",
			"\1\151",
			"\1\152",
			"\1\56",
			"\1\153",
			"\1\56",
			"\1\72\6\uffff\1\71\21\uffff\1\70",
			"\1\72\6\uffff\1\71\21\uffff\1\70",
			"\1\72\6\uffff\1\71\21\uffff\1\70",
			"\1\72\6\uffff\1\71\21\uffff\1\70",
			"\1\156\27\uffff\1\154\1\uffff\1\157\37\uffff\1\155",
			"\1\162\27\uffff\1\160\1\uffff\1\163\37\uffff\1\161",
			"\1\164",
			"\1\165",
			"\1\166",
			"\1\167",
			"\1\170",
			"\1\171",
			"\1\172",
			"\1\173",
			"\1\125\6\uffff\1\124\21\uffff\1\123",
			"\1\125\6\uffff\1\124\21\uffff\1\123",
			"\1\125\6\uffff\1\124\21\uffff\1\123",
			"\1\125\6\uffff\1\124\21\uffff\1\123",
			"\1\130\6\uffff\1\127\21\uffff\1\126",
			"\1\130\6\uffff\1\127\21\uffff\1\126",
			"\1\130\6\uffff\1\127\21\uffff\1\126",
			"\1\130\6\uffff\1\127\21\uffff\1\126"
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
			return "90:1: blockSet : ({...}? ^( BLOCK ^(alt= ALT ( elementOptions )? {...}? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ ) -> ^( BLOCK[$BLOCK.token] ^( ALT[$BLOCK.token,\"ALT\"] ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) ) ) |{...}? ^( BLOCK ^( ALT ( elementOptions )? setElement[inLexer] ) ( ^( ALT ( elementOptions )? setElement[inLexer] ) )+ ) -> ^( SET[$BLOCK.token, \"SET\"] ( setElement )+ ) );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			TreeNodeStream input = (TreeNodeStream)_input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA10_60 = input.LA(1);
						 
						int index10_60 = input.index();
						input.rewind();
						s = -1;
						if ( ((inContext("RULE"))) ) {s = 70;}
						else if ( ((!inContext("RULE"))) ) {s = 71;}
						 
						input.seek(index10_60);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 10, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	public static final BitSet FOLLOW_RULE_in_topdown86 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TOKEN_REF_in_topdown91 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000FFFFFFFFFL});
	public static final BitSet FOLLOW_RULE_REF_in_topdown95 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000FFFFFFFFFL});
	public static final BitSet FOLLOW_setAlt_in_topdown110 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ebnfBlockSet_in_topdown118 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_blockSet_in_topdown126 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ALT_in_setAlt141 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ebnfSuffix_in_ebnfBlockSet161 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_blockSet_in_ebnfBlockSet163 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_BLOCK_in_blockSet244 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ALT_in_blockSet249 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_blockSet251 = new BitSet(new long[]{0x4010000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet256 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ALT_in_blockSet263 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_blockSet265 = new BitSet(new long[]{0x4010000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet268 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_BLOCK_in_blockSet313 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ALT_in_blockSet316 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_blockSet318 = new BitSet(new long[]{0x4010000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet321 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ALT_in_blockSet328 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_blockSet330 = new BitSet(new long[]{0x4010000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_setElement_in_blockSet333 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement373 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_setElement375 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement388 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_setElement400 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_setElement402 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_TOKEN_REF_in_setElement414 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RANGE_in_setElement425 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement429 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_setElement433 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOptions455 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOption_in_elementOptions457 = new BitSet(new long[]{0x0000000010000408L});
	public static final BitSet FOLLOW_ID_in_elementOption470 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption476 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption480 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_ID_in_elementOption484 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption491 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption493 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption497 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption504 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption506 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_elementOption510 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption517 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption519 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_INT_in_elementOption523 = new BitSet(new long[]{0x0000000000000008L});
}
