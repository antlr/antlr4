// $ANTLR 3.5.2 /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g 2015-06-23 21:59:58

package org.antlr.v4.codegen;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;


import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class SourceGenTriggers extends TreeParser {
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


	public SourceGenTriggers(TreeNodeStream input) {
		this(input, new RecognizerSharedState());
	}
	public SourceGenTriggers(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	@Override public String[] getTokenNames() { return SourceGenTriggers.tokenNames; }
	@Override public String getGrammarFileName() { return "/Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g"; }


		public OutputModelController controller;
	    public boolean hasLookaheadBlock;
	    public SourceGenTriggers(TreeNodeStream input, OutputModelController controller) {
	    	this(input);
	    	this.controller = controller;
	    }



	// $ANTLR start "dummy"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:59:1: dummy : block[null, null] ;
	public final void dummy() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:59:7: ( block[null, null] )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:59:9: block[null, null]
			{
			pushFollow(FOLLOW_block_in_dummy61);
			block(null, null);
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



	// $ANTLR start "block"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:61:1: block[GrammarAST label, GrammarAST ebnfRoot] returns [List<? extends SrcOp> omos] : ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) ;
	public final List<? extends SrcOp> block(GrammarAST label, GrammarAST ebnfRoot) throws RecognitionException {
		List<? extends SrcOp> omos = null;


		GrammarAST blk=null;
		TreeRuleReturnScope alternative1 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:62:5: ( ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:62:7: ^(blk= BLOCK ( ^( OPTIONS ( . )+ ) )? ( alternative )+ )
			{
			blk=(GrammarAST)match(input,BLOCK,FOLLOW_BLOCK_in_block84); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:62:20: ( ^( OPTIONS ( . )+ ) )?
			int alt2=2;
			int LA2_0 = input.LA(1);
			if ( (LA2_0==OPTIONS) ) {
				alt2=1;
			}
			switch (alt2) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:62:21: ^( OPTIONS ( . )+ )
					{
					match(input,OPTIONS,FOLLOW_OPTIONS_in_block88); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:62:31: ( . )+
					int cnt1=0;
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
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:62:31: .
							{
							matchAny(input); 
							}
							break;

						default :
							if ( cnt1 >= 1 ) break loop1;
							EarlyExitException eee = new EarlyExitException(1, input);
							throw eee;
						}
						cnt1++;
					}

					match(input, Token.UP, null); 

					}
					break;

			}

			List<CodeBlockForAlt> alts = new ArrayList<CodeBlockForAlt>();
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:64:7: ( alternative )+
			int cnt3=0;
			loop3:
			while (true) {
				int alt3=2;
				int LA3_0 = input.LA(1);
				if ( (LA3_0==ALT) ) {
					alt3=1;
				}

				switch (alt3) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:64:9: alternative
					{
					pushFollow(FOLLOW_alternative_in_block109);
					alternative1=alternative();
					state._fsp--;

					alts.add((alternative1!=null?((SourceGenTriggers.alternative_return)alternative1).altCodeBlock:null));
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


			    	if ( alts.size()==1 && ebnfRoot==null) return alts;
			    	if ( ebnfRoot==null ) {
			    	    omos = DefaultOutputModelFactory.list(controller.getChoiceBlock((BlockAST)blk, alts, label));
			    	}
			    	else {
			            Choice choice = controller.getEBNFBlock(ebnfRoot, alts);
			            hasLookaheadBlock |= choice instanceof PlusBlock || choice instanceof StarBlock;
			    	    omos = DefaultOutputModelFactory.list(choice);
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
		return omos;
	}
	// $ANTLR end "block"


	public static class alternative_return extends TreeRuleReturnScope {
		public CodeBlockForAlt altCodeBlock;
		public List<SrcOp> ops;
	};


	// $ANTLR start "alternative"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:79:1: alternative returns [CodeBlockForAlt altCodeBlock, List<SrcOp> ops] : a= alt[outerMost] ;
	public final SourceGenTriggers.alternative_return alternative() throws RecognitionException {
		SourceGenTriggers.alternative_return retval = new SourceGenTriggers.alternative_return();
		retval.start = input.LT(1);

		TreeRuleReturnScope a =null;


		   	boolean outerMost = inContext("RULE BLOCK");

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:86:5: (a= alt[outerMost] )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:86:7: a= alt[outerMost]
			{
			pushFollow(FOLLOW_alt_in_alternative161);
			a=alt(outerMost);
			state._fsp--;

			retval.altCodeBlock =(a!=null?((SourceGenTriggers.alt_return)a).altCodeBlock:null); retval.ops =(a!=null?((SourceGenTriggers.alt_return)a).ops:null);
			}


			   	controller.finishAlternative(retval.altCodeBlock, retval.ops, outerMost);

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


	public static class alt_return extends TreeRuleReturnScope {
		public CodeBlockForAlt altCodeBlock;
		public List<SrcOp> ops;
	};


	// $ANTLR start "alt"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:89:1: alt[boolean outerMost] returns [CodeBlockForAlt altCodeBlock, List<SrcOp> ops] : ( ^( ALT ( elementOptions )? ( element )+ ) | ^( ALT ( elementOptions )? EPSILON ) );
	public final SourceGenTriggers.alt_return alt(boolean outerMost) throws RecognitionException {
		SourceGenTriggers.alt_return retval = new SourceGenTriggers.alt_return();
		retval.start = input.LT(1);

		List<? extends SrcOp> element2 =null;


			// set alt if outer ALT only (the only ones with alt field set to Alternative object)
			AltAST altAST = (AltAST)retval.start;
			if ( outerMost ) controller.setCurrentOuterMostAlt(altAST.alt);

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:95:2: ( ^( ALT ( elementOptions )? ( element )+ ) | ^( ALT ( elementOptions )? EPSILON ) )
			int alt7=2;
			alt7 = dfa7.predict(input);
			switch (alt7) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:95:4: ^( ALT ( elementOptions )? ( element )+ )
					{

							List<SrcOp> elems = new ArrayList<SrcOp>();
							// TODO: shouldn't we pass ((GrammarAST)retval.start) to controller.alternative()?
							retval.altCodeBlock = controller.alternative(controller.getCurrentOuterMostAlt(), outerMost);
							retval.altCodeBlock.ops = retval.ops = elems;
							controller.setCurrentBlock(retval.altCodeBlock);
							
					match(input,ALT,FOLLOW_ALT_in_alt191); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:102:10: ( elementOptions )?
					int alt4=2;
					int LA4_0 = input.LA(1);
					if ( (LA4_0==ELEMENT_OPTIONS) ) {
						alt4=1;
					}
					switch (alt4) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:102:10: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_alt193);
							elementOptions();
							state._fsp--;

							}
							break;

					}

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:102:26: ( element )+
					int cnt5=0;
					loop5:
					while (true) {
						int alt5=2;
						int LA5_0 = input.LA(1);
						if ( (LA5_0==ACTION||LA5_0==ASSIGN||LA5_0==DOT||LA5_0==NOT||LA5_0==PLUS_ASSIGN||LA5_0==RANGE||LA5_0==RULE_REF||LA5_0==SEMPRED||LA5_0==STRING_LITERAL||LA5_0==TOKEN_REF||LA5_0==BLOCK||LA5_0==CLOSURE||(LA5_0 >= OPTIONAL && LA5_0 <= POSITIVE_CLOSURE)||LA5_0==SET||LA5_0==WILDCARD) ) {
							alt5=1;
						}

						switch (alt5) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:102:28: element
							{
							pushFollow(FOLLOW_element_in_alt198);
							element2=element();
							state._fsp--;

							if (element2!=null) elems.addAll(element2);
							}
							break;

						default :
							if ( cnt5 >= 1 ) break loop5;
							EarlyExitException eee = new EarlyExitException(5, input);
							throw eee;
						}
						cnt5++;
					}

					match(input, Token.UP, null); 

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:104:4: ^( ALT ( elementOptions )? EPSILON )
					{
					match(input,ALT,FOLLOW_ALT_in_alt212); 
					match(input, Token.DOWN, null); 
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:104:10: ( elementOptions )?
					int alt6=2;
					int LA6_0 = input.LA(1);
					if ( (LA6_0==ELEMENT_OPTIONS) ) {
						alt6=1;
					}
					switch (alt6) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:104:10: elementOptions
							{
							pushFollow(FOLLOW_elementOptions_in_alt214);
							elementOptions();
							state._fsp--;

							}
							break;

					}

					match(input,EPSILON,FOLLOW_EPSILON_in_alt217); 
					match(input, Token.UP, null); 

					retval.altCodeBlock = controller.epsilon(controller.getCurrentOuterMostAlt(), outerMost);
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
	// $ANTLR end "alt"



	// $ANTLR start "element"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:108:1: element returns [List<? extends SrcOp> omos] : ( labeledElement | atom[null,false] | subrule | ACTION | SEMPRED | ^( ACTION elementOptions ) | ^( SEMPRED elementOptions ) );
	public final List<? extends SrcOp> element() throws RecognitionException {
		List<? extends SrcOp> omos = null;


		GrammarAST ACTION6=null;
		GrammarAST SEMPRED7=null;
		GrammarAST ACTION8=null;
		GrammarAST SEMPRED9=null;
		List<? extends SrcOp> labeledElement3 =null;
		List<SrcOp> atom4 =null;
		List<? extends SrcOp> subrule5 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:109:2: ( labeledElement | atom[null,false] | subrule | ACTION | SEMPRED | ^( ACTION elementOptions ) | ^( SEMPRED elementOptions ) )
			int alt8=7;
			switch ( input.LA(1) ) {
			case ASSIGN:
			case PLUS_ASSIGN:
				{
				alt8=1;
				}
				break;
			case DOT:
			case NOT:
			case RANGE:
			case RULE_REF:
			case STRING_LITERAL:
			case TOKEN_REF:
			case SET:
			case WILDCARD:
				{
				alt8=2;
				}
				break;
			case BLOCK:
			case CLOSURE:
			case OPTIONAL:
			case POSITIVE_CLOSURE:
				{
				alt8=3;
				}
				break;
			case ACTION:
				{
				int LA8_4 = input.LA(2);
				if ( (LA8_4==DOWN) ) {
					alt8=6;
				}
				else if ( ((LA8_4 >= UP && LA8_4 <= ACTION)||LA8_4==ASSIGN||LA8_4==DOT||LA8_4==NOT||LA8_4==PLUS_ASSIGN||LA8_4==RANGE||LA8_4==RULE_REF||LA8_4==SEMPRED||LA8_4==STRING_LITERAL||LA8_4==TOKEN_REF||LA8_4==BLOCK||LA8_4==CLOSURE||(LA8_4 >= OPTIONAL && LA8_4 <= POSITIVE_CLOSURE)||LA8_4==SET||LA8_4==WILDCARD) ) {
					alt8=4;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 8, 4, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case SEMPRED:
				{
				int LA8_5 = input.LA(2);
				if ( (LA8_5==DOWN) ) {
					alt8=7;
				}
				else if ( ((LA8_5 >= UP && LA8_5 <= ACTION)||LA8_5==ASSIGN||LA8_5==DOT||LA8_5==NOT||LA8_5==PLUS_ASSIGN||LA8_5==RANGE||LA8_5==RULE_REF||LA8_5==SEMPRED||LA8_5==STRING_LITERAL||LA8_5==TOKEN_REF||LA8_5==BLOCK||LA8_5==CLOSURE||(LA8_5 >= OPTIONAL && LA8_5 <= POSITIVE_CLOSURE)||LA8_5==SET||LA8_5==WILDCARD) ) {
					alt8=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 8, 5, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 8, 0, input);
				throw nvae;
			}
			switch (alt8) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:109:4: labeledElement
					{
					pushFollow(FOLLOW_labeledElement_in_element246);
					labeledElement3=labeledElement();
					state._fsp--;

					omos = labeledElement3;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:110:4: atom[null,false]
					{
					pushFollow(FOLLOW_atom_in_element257);
					atom4=atom(null, false);
					state._fsp--;

					omos = atom4;
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:111:4: subrule
					{
					pushFollow(FOLLOW_subrule_in_element267);
					subrule5=subrule();
					state._fsp--;

					omos = subrule5;
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:112:6: ACTION
					{
					ACTION6=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element282); 
					omos = controller.action((ActionAST)ACTION6);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:113:6: SEMPRED
					{
					SEMPRED7=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element297); 
					omos = controller.sempred((ActionAST)SEMPRED7);
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:114:4: ^( ACTION elementOptions )
					{
					ACTION8=(GrammarAST)match(input,ACTION,FOLLOW_ACTION_in_element311); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_element313);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					omos = controller.action((ActionAST)ACTION8);
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:115:6: ^( SEMPRED elementOptions )
					{
					SEMPRED9=(GrammarAST)match(input,SEMPRED,FOLLOW_SEMPRED_in_element325); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_elementOptions_in_element327);
					elementOptions();
					state._fsp--;

					match(input, Token.UP, null); 

					omos = controller.sempred((ActionAST)SEMPRED9);
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
		return omos;
	}
	// $ANTLR end "element"



	// $ANTLR start "labeledElement"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:118:1: labeledElement returns [List<? extends SrcOp> omos] : ( ^( ASSIGN ID atom[$ID,false] ) | ^( PLUS_ASSIGN ID atom[$ID,false] ) | ^( ASSIGN ID block[$ID,null] ) | ^( PLUS_ASSIGN ID block[$ID,null] ) );
	public final List<? extends SrcOp> labeledElement() throws RecognitionException {
		List<? extends SrcOp> omos = null;


		GrammarAST ID10=null;
		GrammarAST ID12=null;
		GrammarAST ID14=null;
		GrammarAST ID16=null;
		List<SrcOp> atom11 =null;
		List<SrcOp> atom13 =null;
		List<? extends SrcOp> block15 =null;
		List<? extends SrcOp> block17 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:119:2: ( ^( ASSIGN ID atom[$ID,false] ) | ^( PLUS_ASSIGN ID atom[$ID,false] ) | ^( ASSIGN ID block[$ID,null] ) | ^( PLUS_ASSIGN ID block[$ID,null] ) )
			int alt9=4;
			int LA9_0 = input.LA(1);
			if ( (LA9_0==ASSIGN) ) {
				int LA9_1 = input.LA(2);
				if ( (LA9_1==DOWN) ) {
					int LA9_3 = input.LA(3);
					if ( (LA9_3==ID) ) {
						int LA9_5 = input.LA(4);
						if ( (LA9_5==DOT||LA9_5==NOT||LA9_5==RANGE||LA9_5==RULE_REF||LA9_5==STRING_LITERAL||LA9_5==TOKEN_REF||LA9_5==SET||LA9_5==WILDCARD) ) {
							alt9=1;
						}
						else if ( (LA9_5==BLOCK) ) {
							alt9=3;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 9, 5, input);
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
								new NoViableAltException("", 9, 3, input);
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
							new NoViableAltException("", 9, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA9_0==PLUS_ASSIGN) ) {
				int LA9_2 = input.LA(2);
				if ( (LA9_2==DOWN) ) {
					int LA9_4 = input.LA(3);
					if ( (LA9_4==ID) ) {
						int LA9_6 = input.LA(4);
						if ( (LA9_6==DOT||LA9_6==NOT||LA9_6==RANGE||LA9_6==RULE_REF||LA9_6==STRING_LITERAL||LA9_6==TOKEN_REF||LA9_6==SET||LA9_6==WILDCARD) ) {
							alt9=2;
						}
						else if ( (LA9_6==BLOCK) ) {
							alt9=4;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 9, 6, input);
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
								new NoViableAltException("", 9, 4, input);
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
							new NoViableAltException("", 9, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 9, 0, input);
				throw nvae;
			}

			switch (alt9) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:119:4: ^( ASSIGN ID atom[$ID,false] )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement347); 
					match(input, Token.DOWN, null); 
					ID10=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement349); 
					pushFollow(FOLLOW_atom_in_labeledElement351);
					atom11=atom(ID10, false);
					state._fsp--;

					match(input, Token.UP, null); 

					omos = atom11;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:120:4: ^( PLUS_ASSIGN ID atom[$ID,false] )
					{
					match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement364); 
					match(input, Token.DOWN, null); 
					ID12=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement366); 
					pushFollow(FOLLOW_atom_in_labeledElement368);
					atom13=atom(ID12, false);
					state._fsp--;

					match(input, Token.UP, null); 

					omos = atom13;
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:121:4: ^( ASSIGN ID block[$ID,null] )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_labeledElement379); 
					match(input, Token.DOWN, null); 
					ID14=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement381); 
					pushFollow(FOLLOW_block_in_labeledElement383);
					block15=block(ID14, null);
					state._fsp--;

					match(input, Token.UP, null); 

					omos = block15;
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:122:4: ^( PLUS_ASSIGN ID block[$ID,null] )
					{
					match(input,PLUS_ASSIGN,FOLLOW_PLUS_ASSIGN_in_labeledElement396); 
					match(input, Token.DOWN, null); 
					ID16=(GrammarAST)match(input,ID,FOLLOW_ID_in_labeledElement398); 
					pushFollow(FOLLOW_block_in_labeledElement400);
					block17=block(ID16, null);
					state._fsp--;

					match(input, Token.UP, null); 

					omos = block17;
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
		return omos;
	}
	// $ANTLR end "labeledElement"



	// $ANTLR start "subrule"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:125:1: subrule returns [List<? extends SrcOp> omos] : ( ^( OPTIONAL b= block[null,$OPTIONAL] ) | ( ^(op= CLOSURE b= block[null,null] ) | ^(op= POSITIVE_CLOSURE b= block[null,null] ) ) | block[null, null] );
	public final List<? extends SrcOp> subrule() throws RecognitionException {
		List<? extends SrcOp> omos = null;


		GrammarAST op=null;
		GrammarAST OPTIONAL18=null;
		List<? extends SrcOp> b =null;
		List<? extends SrcOp> block19 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:126:2: ( ^( OPTIONAL b= block[null,$OPTIONAL] ) | ( ^(op= CLOSURE b= block[null,null] ) | ^(op= POSITIVE_CLOSURE b= block[null,null] ) ) | block[null, null] )
			int alt11=3;
			switch ( input.LA(1) ) {
			case OPTIONAL:
				{
				alt11=1;
				}
				break;
			case CLOSURE:
			case POSITIVE_CLOSURE:
				{
				alt11=2;
				}
				break;
			case BLOCK:
				{
				alt11=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 11, 0, input);
				throw nvae;
			}
			switch (alt11) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:126:4: ^( OPTIONAL b= block[null,$OPTIONAL] )
					{
					OPTIONAL18=(GrammarAST)match(input,OPTIONAL,FOLLOW_OPTIONAL_in_subrule421); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_block_in_subrule425);
					b=block(null, OPTIONAL18);
					state._fsp--;

					match(input, Token.UP, null); 


							omos = b;
							
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:130:4: ( ^(op= CLOSURE b= block[null,null] ) | ^(op= POSITIVE_CLOSURE b= block[null,null] ) )
					{
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:130:4: ( ^(op= CLOSURE b= block[null,null] ) | ^(op= POSITIVE_CLOSURE b= block[null,null] ) )
					int alt10=2;
					int LA10_0 = input.LA(1);
					if ( (LA10_0==CLOSURE) ) {
						alt10=1;
					}
					else if ( (LA10_0==POSITIVE_CLOSURE) ) {
						alt10=2;
					}

					else {
						NoViableAltException nvae =
							new NoViableAltException("", 10, 0, input);
						throw nvae;
					}

					switch (alt10) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:130:6: ^(op= CLOSURE b= block[null,null] )
							{
							op=(GrammarAST)match(input,CLOSURE,FOLLOW_CLOSURE_in_subrule441); 
							match(input, Token.DOWN, null); 
							pushFollow(FOLLOW_block_in_subrule445);
							b=block(null, null);
							state._fsp--;

							match(input, Token.UP, null); 

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:131:5: ^(op= POSITIVE_CLOSURE b= block[null,null] )
							{
							op=(GrammarAST)match(input,POSITIVE_CLOSURE,FOLLOW_POSITIVE_CLOSURE_in_subrule456); 
							match(input, Token.DOWN, null); 
							pushFollow(FOLLOW_block_in_subrule460);
							b=block(null, null);
							state._fsp--;

							match(input, Token.UP, null); 

							}
							break;

					}


							List<CodeBlockForAlt> alts = new ArrayList<CodeBlockForAlt>();
							SrcOp blk = b.get(0);
							CodeBlockForAlt alt = new CodeBlockForAlt(controller.delegate);
							alt.addOp(blk);
							alts.add(alt);
							SrcOp loop = controller.getEBNFBlock(op, alts); // "star it"
					        hasLookaheadBlock |= loop instanceof PlusBlock || loop instanceof StarBlock;
					   	    omos = DefaultOutputModelFactory.list(loop);
							
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:143:5: block[null, null]
					{
					pushFollow(FOLLOW_block_in_subrule476);
					block19=block(null, null);
					state._fsp--;

					omos = block19;
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
		return omos;
	}
	// $ANTLR end "subrule"



	// $ANTLR start "blockSet"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:146:1: blockSet[GrammarAST label, boolean invert] returns [List<SrcOp> omos] : ^( SET ( atom[label,invert] )+ ) ;
	public final List<SrcOp> blockSet(GrammarAST label, boolean invert) throws RecognitionException {
		List<SrcOp> omos = null;


		GrammarAST SET20=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:147:5: ( ^( SET ( atom[label,invert] )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:147:7: ^( SET ( atom[label,invert] )+ )
			{
			SET20=(GrammarAST)match(input,SET,FOLLOW_SET_in_blockSet506); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:147:13: ( atom[label,invert] )+
			int cnt12=0;
			loop12:
			while (true) {
				int alt12=2;
				int LA12_0 = input.LA(1);
				if ( (LA12_0==DOT||LA12_0==NOT||LA12_0==RANGE||LA12_0==RULE_REF||LA12_0==STRING_LITERAL||LA12_0==TOKEN_REF||LA12_0==SET||LA12_0==WILDCARD) ) {
					alt12=1;
				}

				switch (alt12) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:147:13: atom[label,invert]
					{
					pushFollow(FOLLOW_atom_in_blockSet508);
					atom(label, invert);
					state._fsp--;

					}
					break;

				default :
					if ( cnt12 >= 1 ) break loop12;
					EarlyExitException eee = new EarlyExitException(12, input);
					throw eee;
				}
				cnt12++;
			}

			match(input, Token.UP, null); 

			omos = controller.set(SET20, label, invert);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return omos;
	}
	// $ANTLR end "blockSet"



	// $ANTLR start "atom"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:160:1: atom[GrammarAST label, boolean invert] returns [List<SrcOp> omos] : ( ^( NOT a= atom[$label, true] ) | range[label] | ^( DOT ID terminal[$label] ) | ^( DOT ID ruleref[$label] ) | ^( WILDCARD . ) | WILDCARD | terminal[label] | ruleref[label] | blockSet[$label, invert] );
	public final List<SrcOp> atom(GrammarAST label, boolean invert) throws RecognitionException {
		List<SrcOp> omos = null;


		GrammarAST WILDCARD22=null;
		GrammarAST WILDCARD23=null;
		List<SrcOp> a =null;
		List<SrcOp> range21 =null;
		List<SrcOp> terminal24 =null;
		List<SrcOp> ruleref25 =null;
		List<SrcOp> blockSet26 =null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:161:2: ( ^( NOT a= atom[$label, true] ) | range[label] | ^( DOT ID terminal[$label] ) | ^( DOT ID ruleref[$label] ) | ^( WILDCARD . ) | WILDCARD | terminal[label] | ruleref[label] | blockSet[$label, invert] )
			int alt13=9;
			switch ( input.LA(1) ) {
			case NOT:
				{
				alt13=1;
				}
				break;
			case RANGE:
				{
				alt13=2;
				}
				break;
			case DOT:
				{
				int LA13_3 = input.LA(2);
				if ( (LA13_3==DOWN) ) {
					int LA13_8 = input.LA(3);
					if ( (LA13_8==ID) ) {
						int LA13_11 = input.LA(4);
						if ( (LA13_11==STRING_LITERAL||LA13_11==TOKEN_REF) ) {
							alt13=3;
						}
						else if ( (LA13_11==RULE_REF) ) {
							alt13=4;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 13, 11, input);
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
								new NoViableAltException("", 13, 8, input);
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
							new NoViableAltException("", 13, 3, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

				}
				break;
			case WILDCARD:
				{
				int LA13_4 = input.LA(2);
				if ( (LA13_4==DOWN) ) {
					alt13=5;
				}
				else if ( ((LA13_4 >= UP && LA13_4 <= ACTION)||LA13_4==ASSIGN||LA13_4==DOT||LA13_4==NOT||LA13_4==PLUS_ASSIGN||LA13_4==RANGE||LA13_4==RULE_REF||LA13_4==SEMPRED||LA13_4==STRING_LITERAL||LA13_4==TOKEN_REF||LA13_4==BLOCK||LA13_4==CLOSURE||(LA13_4 >= OPTIONAL && LA13_4 <= POSITIVE_CLOSURE)||LA13_4==SET||LA13_4==WILDCARD) ) {
					alt13=6;
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
			case STRING_LITERAL:
			case TOKEN_REF:
				{
				alt13=7;
				}
				break;
			case RULE_REF:
				{
				alt13=8;
				}
				break;
			case SET:
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
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:161:4: ^( NOT a= atom[$label, true] )
					{
					match(input,NOT,FOLLOW_NOT_in_atom538); 
					match(input, Token.DOWN, null); 
					pushFollow(FOLLOW_atom_in_atom542);
					a=atom(label, true);
					state._fsp--;

					match(input, Token.UP, null); 

					omos = a;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:162:4: range[label]
					{
					pushFollow(FOLLOW_range_in_atom552);
					range21=range(label);
					state._fsp--;

					omos = range21;
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:163:4: ^( DOT ID terminal[$label] )
					{
					match(input,DOT,FOLLOW_DOT_in_atom567); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_atom569); 
					pushFollow(FOLLOW_terminal_in_atom571);
					terminal(label);
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:164:4: ^( DOT ID ruleref[$label] )
					{
					match(input,DOT,FOLLOW_DOT_in_atom579); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_atom581); 
					pushFollow(FOLLOW_ruleref_in_atom583);
					ruleref(label);
					state._fsp--;

					match(input, Token.UP, null); 

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:165:7: ^( WILDCARD . )
					{
					WILDCARD22=(GrammarAST)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom594); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					omos = controller.wildcard(WILDCARD22, label);
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:166:7: WILDCARD
					{
					WILDCARD23=(GrammarAST)match(input,WILDCARD,FOLLOW_WILDCARD_in_atom613); 
					omos = controller.wildcard(WILDCARD23, label);
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:167:9: terminal[label]
					{
					pushFollow(FOLLOW_terminal_in_atom632);
					terminal24=terminal(label);
					state._fsp--;

					omos = terminal24;
					}
					break;
				case 8 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:168:9: ruleref[label]
					{
					pushFollow(FOLLOW_ruleref_in_atom649);
					ruleref25=ruleref(label);
					state._fsp--;

					omos = ruleref25;
					}
					break;
				case 9 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:169:4: blockSet[$label, invert]
					{
					pushFollow(FOLLOW_blockSet_in_atom661);
					blockSet26=blockSet(label, invert);
					state._fsp--;

					omos = blockSet26;
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
		return omos;
	}
	// $ANTLR end "atom"



	// $ANTLR start "ruleref"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:172:1: ruleref[GrammarAST label] returns [List<SrcOp> omos] : ^( RULE_REF ( ARG_ACTION )? ( elementOptions )? ) ;
	public final List<SrcOp> ruleref(GrammarAST label) throws RecognitionException {
		List<SrcOp> omos = null;


		GrammarAST RULE_REF27=null;
		GrammarAST ARG_ACTION28=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:173:5: ( ^( RULE_REF ( ARG_ACTION )? ( elementOptions )? ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:173:7: ^( RULE_REF ( ARG_ACTION )? ( elementOptions )? )
			{
			RULE_REF27=(GrammarAST)match(input,RULE_REF,FOLLOW_RULE_REF_in_ruleref685); 
			if ( input.LA(1)==Token.DOWN ) {
				match(input, Token.DOWN, null); 
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:173:18: ( ARG_ACTION )?
				int alt14=2;
				int LA14_0 = input.LA(1);
				if ( (LA14_0==ARG_ACTION) ) {
					alt14=1;
				}
				switch (alt14) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:173:18: ARG_ACTION
						{
						ARG_ACTION28=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_ruleref687); 
						}
						break;

				}

				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:173:30: ( elementOptions )?
				int alt15=2;
				int LA15_0 = input.LA(1);
				if ( (LA15_0==ELEMENT_OPTIONS) ) {
					alt15=1;
				}
				switch (alt15) {
					case 1 :
						// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:173:30: elementOptions
						{
						pushFollow(FOLLOW_elementOptions_in_ruleref690);
						elementOptions();
						state._fsp--;

						}
						break;

				}

				match(input, Token.UP, null); 
			}

			omos = controller.ruleRef(RULE_REF27, label, ARG_ACTION28);
			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return omos;
	}
	// $ANTLR end "ruleref"



	// $ANTLR start "range"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:176:1: range[GrammarAST label] returns [List<SrcOp> omos] : ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) ;
	public final List<SrcOp> range(GrammarAST label) throws RecognitionException {
		List<SrcOp> omos = null;


		GrammarAST a=null;
		GrammarAST b=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:177:5: ( ^( RANGE a= STRING_LITERAL b= STRING_LITERAL ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:177:7: ^( RANGE a= STRING_LITERAL b= STRING_LITERAL )
			{
			match(input,RANGE,FOLLOW_RANGE_in_range718); 
			match(input, Token.DOWN, null); 
			a=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range722); 
			b=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_range726); 
			match(input, Token.UP, null); 

			}

		}
		catch (RecognitionException re) {
			reportError(re);
			recover(input,re);
		}
		finally {
			// do for sure before leaving
		}
		return omos;
	}
	// $ANTLR end "range"



	// $ANTLR start "terminal"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:180:1: terminal[GrammarAST label] returns [List<SrcOp> omos] : ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF );
	public final List<SrcOp> terminal(GrammarAST label) throws RecognitionException {
		List<SrcOp> omos = null;


		GrammarAST STRING_LITERAL29=null;
		GrammarAST STRING_LITERAL30=null;
		GrammarAST TOKEN_REF31=null;
		GrammarAST ARG_ACTION32=null;
		GrammarAST TOKEN_REF33=null;
		GrammarAST TOKEN_REF34=null;

		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:181:5: ( ^( STRING_LITERAL . ) | STRING_LITERAL | ^( TOKEN_REF ARG_ACTION . ) | ^( TOKEN_REF . ) | TOKEN_REF )
			int alt16=5;
			int LA16_0 = input.LA(1);
			if ( (LA16_0==STRING_LITERAL) ) {
				int LA16_1 = input.LA(2);
				if ( (LA16_1==DOWN) ) {
					alt16=1;
				}
				else if ( ((LA16_1 >= UP && LA16_1 <= ACTION)||LA16_1==ASSIGN||LA16_1==DOT||LA16_1==NOT||LA16_1==PLUS_ASSIGN||LA16_1==RANGE||LA16_1==RULE_REF||LA16_1==SEMPRED||LA16_1==STRING_LITERAL||LA16_1==TOKEN_REF||LA16_1==BLOCK||LA16_1==CLOSURE||(LA16_1 >= OPTIONAL && LA16_1 <= POSITIVE_CLOSURE)||LA16_1==SET||LA16_1==WILDCARD) ) {
					alt16=2;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}
			else if ( (LA16_0==TOKEN_REF) ) {
				int LA16_2 = input.LA(2);
				if ( (LA16_2==DOWN) ) {
					int LA16_5 = input.LA(3);
					if ( (LA16_5==ARG_ACTION) ) {
						int LA16_7 = input.LA(4);
						if ( ((LA16_7 >= ACTION && LA16_7 <= WILDCARD)) ) {
							alt16=3;
						}
						else if ( ((LA16_7 >= DOWN && LA16_7 <= UP)) ) {
							alt16=4;
						}

						else {
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 16, 7, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}
					else if ( ((LA16_5 >= ACTION && LA16_5 <= ACTION_STRING_LITERAL)||(LA16_5 >= ARG_OR_CHARSET && LA16_5 <= WILDCARD)) ) {
						alt16=4;
					}

					else {
						int nvaeMark = input.mark();
						try {
							for (int nvaeConsume = 0; nvaeConsume < 3 - 1; nvaeConsume++) {
								input.consume();
							}
							NoViableAltException nvae =
								new NoViableAltException("", 16, 5, input);
							throw nvae;
						} finally {
							input.rewind(nvaeMark);
						}
					}

				}
				else if ( ((LA16_2 >= UP && LA16_2 <= ACTION)||LA16_2==ASSIGN||LA16_2==DOT||LA16_2==NOT||LA16_2==PLUS_ASSIGN||LA16_2==RANGE||LA16_2==RULE_REF||LA16_2==SEMPRED||LA16_2==STRING_LITERAL||LA16_2==TOKEN_REF||LA16_2==BLOCK||LA16_2==CLOSURE||(LA16_2 >= OPTIONAL && LA16_2 <= POSITIVE_CLOSURE)||LA16_2==SET||LA16_2==WILDCARD) ) {
					alt16=5;
				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 16, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 16, 0, input);
				throw nvae;
			}

			switch (alt16) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:181:8: ^( STRING_LITERAL . )
					{
					STRING_LITERAL29=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal751); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					omos = controller.stringRef(STRING_LITERAL29, label);
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:182:7: STRING_LITERAL
					{
					STRING_LITERAL30=(GrammarAST)match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_terminal766); 
					omos = controller.stringRef(STRING_LITERAL30, label);
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:183:7: ^( TOKEN_REF ARG_ACTION . )
					{
					TOKEN_REF31=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal780); 
					match(input, Token.DOWN, null); 
					ARG_ACTION32=(GrammarAST)match(input,ARG_ACTION,FOLLOW_ARG_ACTION_in_terminal782); 
					matchAny(input); 
					match(input, Token.UP, null); 

					omos = controller.tokenRef(TOKEN_REF31, label, ARG_ACTION32);
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:184:7: ^( TOKEN_REF . )
					{
					TOKEN_REF33=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal796); 
					match(input, Token.DOWN, null); 
					matchAny(input); 
					match(input, Token.UP, null); 

					omos = controller.tokenRef(TOKEN_REF33, label, null);
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:185:7: TOKEN_REF
					{
					TOKEN_REF34=(GrammarAST)match(input,TOKEN_REF,FOLLOW_TOKEN_REF_in_terminal812); 
					omos = controller.tokenRef(TOKEN_REF34, label, null);
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
		return omos;
	}
	// $ANTLR end "terminal"



	// $ANTLR start "elementOptions"
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:188:1: elementOptions : ^( ELEMENT_OPTIONS ( elementOption )+ ) ;
	public final void elementOptions() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:189:5: ( ^( ELEMENT_OPTIONS ( elementOption )+ ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:189:7: ^( ELEMENT_OPTIONS ( elementOption )+ )
			{
			match(input,ELEMENT_OPTIONS,FOLLOW_ELEMENT_OPTIONS_in_elementOptions836); 
			match(input, Token.DOWN, null); 
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:189:25: ( elementOption )+
			int cnt17=0;
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( (LA17_0==ASSIGN||LA17_0==ID) ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:189:25: elementOption
					{
					pushFollow(FOLLOW_elementOption_in_elementOptions838);
					elementOption();
					state._fsp--;

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
	// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:192:1: elementOption : ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) | ^( ASSIGN ID ACTION ) | ^( ASSIGN ID INT ) );
	public final void elementOption() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:193:5: ( ID | ^( ASSIGN ID ID ) | ^( ASSIGN ID STRING_LITERAL ) | ^( ASSIGN ID ACTION ) | ^( ASSIGN ID INT ) )
			int alt18=5;
			int LA18_0 = input.LA(1);
			if ( (LA18_0==ID) ) {
				alt18=1;
			}
			else if ( (LA18_0==ASSIGN) ) {
				int LA18_2 = input.LA(2);
				if ( (LA18_2==DOWN) ) {
					int LA18_3 = input.LA(3);
					if ( (LA18_3==ID) ) {
						switch ( input.LA(4) ) {
						case ID:
							{
							alt18=2;
							}
							break;
						case STRING_LITERAL:
							{
							alt18=3;
							}
							break;
						case ACTION:
							{
							alt18=4;
							}
							break;
						case INT:
							{
							alt18=5;
							}
							break;
						default:
							int nvaeMark = input.mark();
							try {
								for (int nvaeConsume = 0; nvaeConsume < 4 - 1; nvaeConsume++) {
									input.consume();
								}
								NoViableAltException nvae =
									new NoViableAltException("", 18, 4, input);
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
								new NoViableAltException("", 18, 3, input);
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
							new NoViableAltException("", 18, 2, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 18, 0, input);
				throw nvae;
			}

			switch (alt18) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:193:7: ID
					{
					match(input,ID,FOLLOW_ID_in_elementOption857); 
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:194:9: ^( ASSIGN ID ID )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption868); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption870); 
					match(input,ID,FOLLOW_ID_in_elementOption872); 
					match(input, Token.UP, null); 

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:195:9: ^( ASSIGN ID STRING_LITERAL )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption884); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption886); 
					match(input,STRING_LITERAL,FOLLOW_STRING_LITERAL_in_elementOption888); 
					match(input, Token.UP, null); 

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:196:9: ^( ASSIGN ID ACTION )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption900); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption902); 
					match(input,ACTION,FOLLOW_ACTION_in_elementOption904); 
					match(input, Token.UP, null); 

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g:197:9: ^( ASSIGN ID INT )
					{
					match(input,ASSIGN,FOLLOW_ASSIGN_in_elementOption916); 
					match(input, Token.DOWN, null); 
					match(input,ID,FOLLOW_ID_in_elementOption918); 
					match(input,INT,FOLLOW_INT_in_elementOption920); 
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


	protected DFA7 dfa7 = new DFA7(this);
	static final String DFA7_eotS =
		"\24\uffff";
	static final String DFA7_eofS =
		"\24\uffff";
	static final String DFA7_minS =
		"\1\111\1\2\1\4\1\2\2\uffff\1\12\1\3\1\2\1\4\1\34\1\4\10\3";
	static final String DFA7_maxS =
		"\1\111\1\2\1\143\1\2\2\uffff\2\34\1\2\1\143\1\34\1\76\4\3\4\34";
	static final String DFA7_acceptS =
		"\4\uffff\1\1\1\2\16\uffff";
	static final String DFA7_specialS =
		"\24\uffff}>";
	static final String[] DFA7_transitionS = {
			"\1\1",
			"\1\2",
			"\1\4\5\uffff\1\4\11\uffff\1\4\22\uffff\1\4\6\uffff\1\4\5\uffff\1\4\4"+
			"\uffff\1\4\1\uffff\1\4\2\uffff\1\4\3\uffff\1\4\12\uffff\1\4\1\uffff\1"+
			"\4\1\uffff\1\3\1\5\5\uffff\2\4\7\uffff\1\4\1\uffff\1\4",
			"\1\6",
			"",
			"",
			"\1\10\21\uffff\1\7",
			"\1\11\6\uffff\1\10\21\uffff\1\7",
			"\1\12",
			"\1\4\5\uffff\1\4\11\uffff\1\4\22\uffff\1\4\6\uffff\1\4\5\uffff\1\4\4"+
			"\uffff\1\4\1\uffff\1\4\2\uffff\1\4\3\uffff\1\4\12\uffff\1\4\1\uffff\1"+
			"\4\2\uffff\1\5\5\uffff\2\4\7\uffff\1\4\1\uffff\1\4",
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

	static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
	static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
	static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
	static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
	static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
	static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
	static final short[][] DFA7_transition;

	static {
		int numStates = DFA7_transitionS.length;
		DFA7_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
		}
	}

	protected class DFA7 extends DFA {

		public DFA7(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 7;
			this.eot = DFA7_eot;
			this.eof = DFA7_eof;
			this.min = DFA7_min;
			this.max = DFA7_max;
			this.accept = DFA7_accept;
			this.special = DFA7_special;
			this.transition = DFA7_transition;
		}
		@Override
		public String getDescription() {
			return "89:1: alt[boolean outerMost] returns [CodeBlockForAlt altCodeBlock, List<SrcOp> ops] : ( ^( ALT ( elementOptions )? ( element )+ ) | ^( ALT ( elementOptions )? EPSILON ) );";
		}
	}

	public static final BitSet FOLLOW_block_in_dummy61 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_BLOCK_in_block84 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_OPTIONS_in_block88 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_alternative_in_block109 = new BitSet(new long[]{0x0000000000000008L,0x0000000000000200L});
	public static final BitSet FOLLOW_alt_in_alternative161 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ALT_in_alt191 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_alt193 = new BitSet(new long[]{0x4A10408000100410L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_element_in_alt198 = new BitSet(new long[]{0x4A10408000100418L,0x0000000A0300A004L});
	public static final BitSet FOLLOW_ALT_in_alt212 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_alt214 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
	public static final BitSet FOLLOW_EPSILON_in_alt217 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_labeledElement_in_element246 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_atom_in_element257 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_subrule_in_element267 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_element282 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SEMPRED_in_element297 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ACTION_in_element311 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_element313 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_SEMPRED_in_element325 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOptions_in_element327 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_labeledElement347 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledElement349 = new BitSet(new long[]{0x4210008000100000L,0x0000000A00000004L});
	public static final BitSet FOLLOW_atom_in_labeledElement351 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement364 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledElement366 = new BitSet(new long[]{0x4210008000100000L,0x0000000A00000004L});
	public static final BitSet FOLLOW_atom_in_labeledElement368 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_labeledElement379 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledElement381 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
	public static final BitSet FOLLOW_block_in_labeledElement383 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_PLUS_ASSIGN_in_labeledElement396 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_labeledElement398 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
	public static final BitSet FOLLOW_block_in_labeledElement400 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_OPTIONAL_in_subrule421 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_subrule425 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_CLOSURE_in_subrule441 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_subrule445 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_POSITIVE_CLOSURE_in_subrule456 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_block_in_subrule460 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_block_in_subrule476 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_SET_in_blockSet506 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_atom_in_blockSet508 = new BitSet(new long[]{0x4210008000100008L,0x0000000A00000004L});
	public static final BitSet FOLLOW_NOT_in_atom538 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_atom_in_atom542 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_range_in_atom552 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_DOT_in_atom567 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_atom569 = new BitSet(new long[]{0x4000000000000000L,0x0000000000000004L});
	public static final BitSet FOLLOW_terminal_in_atom571 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_DOT_in_atom579 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_atom581 = new BitSet(new long[]{0x0200000000000000L});
	public static final BitSet FOLLOW_ruleref_in_atom583 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_WILDCARD_in_atom594 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_WILDCARD_in_atom613 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_terminal_in_atom632 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ruleref_in_atom649 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_blockSet_in_atom661 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_RULE_REF_in_ruleref685 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_ruleref687 = new BitSet(new long[]{0x0000000000000008L,0x0000000000020000L});
	public static final BitSet FOLLOW_elementOptions_in_ruleref690 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_RANGE_in_range718 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range722 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_range726 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_terminal751 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_terminal766 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal780 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ARG_ACTION_in_terminal782 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000FFFFFFFFFL});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal796 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_TOKEN_REF_in_terminal812 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ELEMENT_OPTIONS_in_elementOptions836 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_elementOption_in_elementOptions838 = new BitSet(new long[]{0x0000000010000408L});
	public static final BitSet FOLLOW_ID_in_elementOption857 = new BitSet(new long[]{0x0000000000000002L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption868 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption870 = new BitSet(new long[]{0x0000000010000000L});
	public static final BitSet FOLLOW_ID_in_elementOption872 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption884 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption886 = new BitSet(new long[]{0x4000000000000000L});
	public static final BitSet FOLLOW_STRING_LITERAL_in_elementOption888 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption900 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption902 = new BitSet(new long[]{0x0000000000000010L});
	public static final BitSet FOLLOW_ACTION_in_elementOption904 = new BitSet(new long[]{0x0000000000000008L});
	public static final BitSet FOLLOW_ASSIGN_in_elementOption916 = new BitSet(new long[]{0x0000000000000004L});
	public static final BitSet FOLLOW_ID_in_elementOption918 = new BitSet(new long[]{0x0000000040000000L});
	public static final BitSet FOLLOW_INT_in_elementOption920 = new BitSet(new long[]{0x0000000000000008L});
}
