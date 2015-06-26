// $ANTLR 3.5.2 /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g 2015-06-23 21:59:55

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

/** Read in an ANTLR grammar and build an AST.  Try not to do
 *  any actions, just build the tree.
 *
 *  The phases are:
 *
 *		A3Lexer.g (this file)
 *              A3Parser.g
 *              A3Verify.g (derived from A3Walker.g)
 *		assign.types.g
 *		define.g
 *		buildnfa.g
 *		antlr.print.g (optional)
 *		codegen.g
 *
 *  Terence Parr
 *  University of San Francisco
 *  2005
 *  Jim Idle (this v3 grammar)
 *  Temporal Wave LLC
 *  2009
 */
@SuppressWarnings("all")
public class ANTLRLexer extends Lexer {
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

		public static final int COMMENTS_CHANNEL = 2;

	    public CommonTokenStream tokens; // track stream we push to; need for context info
	    public boolean isLexerRule = false;

		public void grammarError(ErrorType etype, org.antlr.runtime.Token token, Object... args) { }

		/** scan backwards from current point in this.tokens list
		 *  looking for the start of the rule or subrule.
		 *  Return token or null if for some reason we can't find the start.
		 */
		public Token getRuleOrSubruleStartToken() {
		    if ( tokens==null ) return null;
			int i = tokens.index();
	        int n = tokens.size();
	        if ( i>=n ) i = n-1; // seems index == n as we lex
			while ( i>=0 && i<n) {
				int ttype = tokens.get(i).getType();
				if ( ttype == LPAREN || ttype == TOKEN_REF || ttype == RULE_REF ) {
					return tokens.get(i);
				}
				i--;
			}
			return null;
		}


	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public ANTLRLexer() {} 
	public ANTLRLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}
	public ANTLRLexer(CharStream input, RecognizerSharedState state) {
		super(input,state);
	}
	@Override public String getGrammarFileName() { return "/Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g"; }

	// $ANTLR start "DOC_COMMENT"
	public final void mDOC_COMMENT() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:191:22: ()
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:191:24: 
			{
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOC_COMMENT"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;


				// Record the start line and offsets as if we need to report an
				// unterminated comment, then we want to show the start of the comment
				// we think is broken, not the end, where people will have to try and work
				// it out themselves.
				//
				int startLine = state.tokenStartLine;
				int offset    = getCharPositionInLine();

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:203:5: ( '/' ( '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* ) | '*' ({...}? => '*' |{...}? =>) ({...}? . )* ( '*/' |) |) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:206:7: '/' ( '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* ) | '*' ({...}? => '*' |{...}? =>) ({...}? . )* ( '*/' |) |)
			{
			match('/'); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:208:7: ( '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* ) | '*' ({...}? => '*' |{...}? =>) ({...}? . )* ( '*/' |) |)
			int alt6=3;
			switch ( input.LA(1) ) {
			case '/':
				{
				alt6=1;
				}
				break;
			case '*':
				{
				alt6=2;
				}
				break;
			default:
				alt6=3;
			}
			switch (alt6) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:214:11: '/' ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* )
					{
					match('/'); if (state.failed) return;
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:215:13: ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* )
					int alt2=2;
					alt2 = dfa2.predict(input);
					switch (alt2) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:216:17: ( ' $ANTLR' )=> ' $ANTLR' SRC
							{
							match(" $ANTLR"); if (state.failed) return;

							mSRC(); if (state.failed) return;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:217:17: (~ ( NLCHARS ) )*
							{
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:217:17: (~ ( NLCHARS ) )*
							loop1:
							while (true) {
								int alt1=2;
								int LA1_0 = input.LA(1);
								if ( ((LA1_0 >= '\u0000' && LA1_0 <= '\t')||(LA1_0 >= '\u000B' && LA1_0 <= '\f')||(LA1_0 >= '\u000E' && LA1_0 <= '\uFFFF')) ) {
									alt1=1;
								}

								switch (alt1) {
								case 1 :
									// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
									{
									if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
										input.consume();
										state.failed=false;
									}
									else {
										if (state.backtracking>0) {state.failed=true; return;}
										MismatchedSetException mse = new MismatchedSetException(null,input);
										recover(mse);
										throw mse;
									}
									}
									break;

								default :
									break loop1;
								}
							}

							}
							break;

					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:224:12: '*' ({...}? => '*' |{...}? =>) ({...}? . )* ( '*/' |)
					{
					match('*'); if (state.failed) return;
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:224:16: ({...}? => '*' |{...}? =>)
					int alt3=2;
					int LA3_0 = input.LA(1);
					if ( (LA3_0=='*') && ((( input.LA(2) != '/')||( true )))) {
						int LA3_1 = input.LA(2);
						if ( (( input.LA(2) != '/')) ) {
							alt3=1;
						}
						else if ( (( true )) ) {
							alt3=2;
						}

						else {
							if (state.backtracking>0) {state.failed=true; return;}
							int nvaeMark = input.mark();
							try {
								input.consume();
								NoViableAltException nvae =
									new NoViableAltException("", 3, 1, input);
								throw nvae;
							} finally {
								input.rewind(nvaeMark);
							}
						}

					}

					switch (alt3) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:225:17: {...}? => '*'
							{
							if ( !(( input.LA(2) != '/')) ) {
								if (state.backtracking>0) {state.failed=true; return;}
								throw new FailedPredicateException(input, "COMMENT", " input.LA(2) != '/'");
							}
							match('*'); if (state.failed) return;
							if ( state.backtracking==0 ) { _type = DOC_COMMENT; }
							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:226:17: {...}? =>
							{
							if ( !(( true )) ) {
								if (state.backtracking>0) {state.failed=true; return;}
								throw new FailedPredicateException(input, "COMMENT", " true ");
							}
							}
							break;

					}

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:231:16: ({...}? . )*
					loop4:
					while (true) {
						int alt4=2;
						int LA4_0 = input.LA(1);
						if ( (LA4_0=='*') ) {
							int LA4_1 = input.LA(2);
							if ( (LA4_1=='/') ) {
								int LA4_4 = input.LA(3);
								if ( ((    !(input.LA(1) == '*' && input.LA(2) == '/') )) ) {
									alt4=1;
								}

							}
							else {
								alt4=1;
							}

						}
						else if ( ((LA4_0 >= '\u0000' && LA4_0 <= ')')||(LA4_0 >= '+' && LA4_0 <= '\uFFFF')) ) {
							alt4=1;
						}

						switch (alt4) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:235:20: {...}? .
							{
							if ( !((    !(input.LA(1) == '*' && input.LA(2) == '/') )) ) {
								if (state.backtracking>0) {state.failed=true; return;}
								throw new FailedPredicateException(input, "COMMENT", "    !(input.LA(1) == '*' && input.LA(2) == '/') ");
							}
							matchAny(); if (state.failed) return;
							}
							break;

						default :
							break loop4;
						}
					}

					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:242:13: ( '*/' |)
					int alt5=2;
					int LA5_0 = input.LA(1);
					if ( (LA5_0=='*') ) {
						alt5=1;
					}

					else {
						alt5=2;
					}

					switch (alt5) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:248:18: '*/'
							{
							match("*/"); if (state.failed) return;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:252:18: 
							{
							if ( state.backtracking==0 ) {
							                   // ErrorManager.msg(Msg.UNTERMINATED_DOC_COMMENT, startLine, offset, state.tokenStartCharPositionInLine, startLine, offset, state.tokenStartCharPositionInLine, (Object)null);
							                 }
							}
							break;

					}

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:260:12: 
					{
					if ( state.backtracking==0 ) {
					           	 // TODO: Insert error message relative to comment start
					             //
					           }
					}
					break;

			}

			if ( state.backtracking==0 ) {
			         // We do not wish to pass the comments in to the parser. If you are
			         // writing a formatter then you will want to preserve the comments off
			         // channel, but could just skip and save token space if not.
			         //
			         _channel=COMMENTS_CHANNEL;
			       }
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMENT"

	// $ANTLR start "ARG_OR_CHARSET"
	public final void mARG_OR_CHARSET() throws RecognitionException {
		try {
			int _type = ARG_OR_CHARSET;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:276:5: ({...}? => LEXER_CHAR_SET |{...}? => ARG_ACTION )
			int alt7=2;
			int LA7_0 = input.LA(1);
			if ( (LA7_0=='[') && (((!isLexerRule)||(isLexerRule)))) {
				int LA7_1 = input.LA(2);
				if ( ((isLexerRule)) ) {
					alt7=1;
				}
				else if ( ((!isLexerRule)) ) {
					alt7=2;
				}

				else {
					if (state.backtracking>0) {state.failed=true; return;}
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 7, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			switch (alt7) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:276:9: {...}? => LEXER_CHAR_SET
					{
					if ( !((isLexerRule)) ) {
						if (state.backtracking>0) {state.failed=true; return;}
						throw new FailedPredicateException(input, "ARG_OR_CHARSET", "isLexerRule");
					}
					mLEXER_CHAR_SET(); if (state.failed) return;

					if ( state.backtracking==0 ) {_type=LEXER_CHAR_SET;}
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:277:9: {...}? => ARG_ACTION
					{
					if ( !((!isLexerRule)) ) {
						if (state.backtracking>0) {state.failed=true; return;}
						throw new FailedPredicateException(input, "ARG_OR_CHARSET", "!isLexerRule");
					}
					mARG_ACTION(); if (state.failed) return;

					if ( state.backtracking==0 ) {
					        _type=ARG_ACTION;
					        // Set the token text to our gathered string minus outer [ ]
					        String t = getText();
					        t = t.substring(1,t.length()-1);
					        setText(t);
					        }
					}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ARG_OR_CHARSET"

	// $ANTLR start "LEXER_CHAR_SET"
	public final void mLEXER_CHAR_SET() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:289:2: ( '[' ( '\\\\' ~ ( '\\r' | '\\n' ) |~ ( '\\r' | '\\n' | '\\\\' | ']' ) )* ']' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:289:4: '[' ( '\\\\' ~ ( '\\r' | '\\n' ) |~ ( '\\r' | '\\n' | '\\\\' | ']' ) )* ']'
			{
			match('['); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:290:3: ( '\\\\' ~ ( '\\r' | '\\n' ) |~ ( '\\r' | '\\n' | '\\\\' | ']' ) )*
			loop8:
			while (true) {
				int alt8=3;
				int LA8_0 = input.LA(1);
				if ( (LA8_0=='\\') ) {
					alt8=1;
				}
				else if ( ((LA8_0 >= '\u0000' && LA8_0 <= '\t')||(LA8_0 >= '\u000B' && LA8_0 <= '\f')||(LA8_0 >= '\u000E' && LA8_0 <= '[')||(LA8_0 >= '^' && LA8_0 <= '\uFFFF')) ) {
					alt8=2;
				}

				switch (alt8) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:290:5: '\\\\' ~ ( '\\r' | '\\n' )
					{
					match('\\'); if (state.failed) return;
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:291:5: ~ ( '\\r' | '\\n' | '\\\\' | ']' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '[')||(input.LA(1) >= '^' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop8;
				}
			}

			match(']'); if (state.failed) return;
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LEXER_CHAR_SET"

	// $ANTLR start "ARG_ACTION"
	public final void mARG_ACTION() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:306:2: ( '[' ( ARG_ACTION | ( '\"' )=> ACTION_STRING_LITERAL | ( '\\'' )=> ACTION_CHAR_LITERAL |~ ( '[' | ']' ) )* ']' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:306:4: '[' ( ARG_ACTION | ( '\"' )=> ACTION_STRING_LITERAL | ( '\\'' )=> ACTION_CHAR_LITERAL |~ ( '[' | ']' ) )* ']'
			{
			match('['); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:307:10: ( ARG_ACTION | ( '\"' )=> ACTION_STRING_LITERAL | ( '\\'' )=> ACTION_CHAR_LITERAL |~ ( '[' | ']' ) )*
			loop9:
			while (true) {
				int alt9=5;
				int LA9_0 = input.LA(1);
				if ( (LA9_0=='[') ) {
					alt9=1;
				}
				else if ( (LA9_0=='\"') ) {
					int LA9_3 = input.LA(2);
					if ( (synpred2_ANTLRLexer()) ) {
						alt9=2;
					}
					else if ( (true) ) {
						alt9=4;
					}

				}
				else if ( (LA9_0=='\'') ) {
					int LA9_4 = input.LA(2);
					if ( (synpred3_ANTLRLexer()) ) {
						alt9=3;
					}
					else if ( (true) ) {
						alt9=4;
					}

				}
				else if ( ((LA9_0 >= '\u0000' && LA9_0 <= '!')||(LA9_0 >= '#' && LA9_0 <= '&')||(LA9_0 >= '(' && LA9_0 <= 'Z')||LA9_0=='\\'||(LA9_0 >= '^' && LA9_0 <= '\uFFFF')) ) {
					alt9=4;
				}

				switch (alt9) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:308:14: ARG_ACTION
					{
					mARG_ACTION(); if (state.failed) return;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:310:14: ( '\"' )=> ACTION_STRING_LITERAL
					{
					mACTION_STRING_LITERAL(); if (state.failed) return;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:312:14: ( '\\'' )=> ACTION_CHAR_LITERAL
					{
					mACTION_CHAR_LITERAL(); if (state.failed) return;

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:314:14: ~ ( '[' | ']' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= 'Z')||input.LA(1)=='\\'||(input.LA(1) >= '^' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop9;
				}
			}

			match(']'); if (state.failed) return;
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ARG_ACTION"

	// $ANTLR start "ACTION"
	public final void mACTION() throws RecognitionException {
		try {
			int _type = ACTION;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:332:2: ( NESTED_ACTION ( '?' ( ( ( WSNLCHARS )* '=>' )=> ( WSNLCHARS )* '=>' )? )? )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:332:4: NESTED_ACTION ( '?' ( ( ( WSNLCHARS )* '=>' )=> ( WSNLCHARS )* '=>' )? )?
			{
			mNESTED_ACTION(); if (state.failed) return;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:333:3: ( '?' ( ( ( WSNLCHARS )* '=>' )=> ( WSNLCHARS )* '=>' )? )?
			int alt12=2;
			int LA12_0 = input.LA(1);
			if ( (LA12_0=='?') ) {
				alt12=1;
			}
			switch (alt12) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:333:5: '?' ( ( ( WSNLCHARS )* '=>' )=> ( WSNLCHARS )* '=>' )?
					{
					match('?'); if (state.failed) return;
					if ( state.backtracking==0 ) {_type = SEMPRED;}
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:334:4: ( ( ( WSNLCHARS )* '=>' )=> ( WSNLCHARS )* '=>' )?
					int alt11=2;
					int LA11_0 = input.LA(1);
					if ( ((LA11_0 >= '\t' && LA11_0 <= '\n')||(LA11_0 >= '\f' && LA11_0 <= '\r')||LA11_0==' ') && (synpred4_ANTLRLexer())) {
						alt11=1;
					}
					else if ( (LA11_0=='=') && (synpred4_ANTLRLexer())) {
						alt11=1;
					}
					switch (alt11) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:334:6: ( ( WSNLCHARS )* '=>' )=> ( WSNLCHARS )* '=>'
							{
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:334:27: ( WSNLCHARS )*
							loop10:
							while (true) {
								int alt10=2;
								int LA10_0 = input.LA(1);
								if ( ((LA10_0 >= '\t' && LA10_0 <= '\n')||(LA10_0 >= '\f' && LA10_0 <= '\r')||LA10_0==' ') ) {
									alt10=1;
								}

								switch (alt10) {
								case 1 :
									// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
									{
									if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
										input.consume();
										state.failed=false;
									}
									else {
										if (state.backtracking>0) {state.failed=true; return;}
										MismatchedSetException mse = new MismatchedSetException(null,input);
										recover(mse);
										throw mse;
									}
									}
									break;

								default :
									break loop10;
								}
							}

							match("=>"); if (state.failed) return;

							if ( state.backtracking==0 ) {
											Token t = new CommonToken(input, state.type, state.channel, state.tokenStartCharIndex, getCharIndex()-1);
											t.setLine(state.tokenStartLine);
											t.setText(state.text);
											t.setCharPositionInLine(state.tokenStartCharPositionInLine);
											grammarError(ErrorType.V3_GATED_SEMPRED, t);
											}
							}
							break;

					}

					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ACTION"

	// $ANTLR start "NESTED_ACTION"
	public final void mNESTED_ACTION() throws RecognitionException {
		try {


				// Record the start line and offsets as if we need to report an
				// unterminated block, then we want to show the start of the comment
				// we think is broken, not the end, where people will have to try and work
				// it out themselves.
				//
				int startLine = getLine();
				int offset    = getCharPositionInLine();

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:370:5: ( '{' ( NESTED_ACTION | ACTION_CHAR_LITERAL | COMMENT | ACTION_STRING_LITERAL | ACTION_ESC |~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' ) )* ( '}' |) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:372:4: '{' ( NESTED_ACTION | ACTION_CHAR_LITERAL | COMMENT | ACTION_STRING_LITERAL | ACTION_ESC |~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' ) )* ( '}' |)
			{
			match('{'); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:373:7: ( NESTED_ACTION | ACTION_CHAR_LITERAL | COMMENT | ACTION_STRING_LITERAL | ACTION_ESC |~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' ) )*
			loop13:
			while (true) {
				int alt13=7;
				int LA13_0 = input.LA(1);
				if ( (LA13_0=='{') ) {
					alt13=1;
				}
				else if ( (LA13_0=='\'') ) {
					alt13=2;
				}
				else if ( (LA13_0=='/') ) {
					alt13=3;
				}
				else if ( (LA13_0=='\"') ) {
					alt13=4;
				}
				else if ( (LA13_0=='\\') ) {
					alt13=5;
				}
				else if ( ((LA13_0 >= '\u0000' && LA13_0 <= '!')||(LA13_0 >= '#' && LA13_0 <= '&')||(LA13_0 >= '(' && LA13_0 <= '.')||(LA13_0 >= '0' && LA13_0 <= '[')||(LA13_0 >= ']' && LA13_0 <= 'z')||LA13_0=='|'||(LA13_0 >= '~' && LA13_0 <= '\uFFFF')) ) {
					alt13=6;
				}

				switch (alt13) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:388:8: NESTED_ACTION
					{
					mNESTED_ACTION(); if (state.failed) return;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:392:11: ACTION_CHAR_LITERAL
					{
					mACTION_CHAR_LITERAL(); if (state.failed) return;

					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:397:11: COMMENT
					{
					mCOMMENT(); if (state.failed) return;

					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:401:11: ACTION_STRING_LITERAL
					{
					mACTION_STRING_LITERAL(); if (state.failed) return;

					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:405:8: ACTION_ESC
					{
					mACTION_ESC(); if (state.failed) return;

					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:410:8: ~ ( '\\\\' | '\"' | '\\'' | '/' | '{' | '}' )
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= 'z')||input.LA(1)=='|'||(input.LA(1) >= '~' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop13;
				}
			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:414:2: ( '}' |)
			int alt14=2;
			int LA14_0 = input.LA(1);
			if ( (LA14_0=='}') ) {
				alt14=1;
			}

			else {
				alt14=2;
			}

			switch (alt14) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:417:6: '}'
					{
					match('}'); if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:422:6: 
					{
					if ( state.backtracking==0 ) {
						        // TODO: Report imbalanced {}
						        System.out.println("Block starting  at line " + startLine + " offset " + (offset+1) + " contains imbalanced {} or is missing a }");
						    }
					}
					break;

			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NESTED_ACTION"

	// $ANTLR start "OPTIONS"
	public final void mOPTIONS() throws RecognitionException {
		try {
			int _type = OPTIONS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:439:14: ( 'options' ( WSNLCHARS )* '{' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:439:16: 'options' ( WSNLCHARS )* '{'
			{
			match("options"); if (state.failed) return;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:439:27: ( WSNLCHARS )*
			loop15:
			while (true) {
				int alt15=2;
				int LA15_0 = input.LA(1);
				if ( ((LA15_0 >= '\t' && LA15_0 <= '\n')||(LA15_0 >= '\f' && LA15_0 <= '\r')||LA15_0==' ') ) {
					alt15=1;
				}

				switch (alt15) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop15;
				}
			}

			match('{'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OPTIONS"

	// $ANTLR start "TOKENS_SPEC"
	public final void mTOKENS_SPEC() throws RecognitionException {
		try {
			int _type = TOKENS_SPEC;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:440:14: ( 'tokens' ( WSNLCHARS )* '{' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:440:16: 'tokens' ( WSNLCHARS )* '{'
			{
			match("tokens"); if (state.failed) return;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:440:27: ( WSNLCHARS )*
			loop16:
			while (true) {
				int alt16=2;
				int LA16_0 = input.LA(1);
				if ( ((LA16_0 >= '\t' && LA16_0 <= '\n')||(LA16_0 >= '\f' && LA16_0 <= '\r')||LA16_0==' ') ) {
					alt16=1;
				}

				switch (alt16) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop16;
				}
			}

			match('{'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TOKENS_SPEC"

	// $ANTLR start "CHANNELS"
	public final void mCHANNELS() throws RecognitionException {
		try {
			int _type = CHANNELS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:441:14: ( 'channels' ( WSNLCHARS )* '{' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:441:16: 'channels' ( WSNLCHARS )* '{'
			{
			match("channels"); if (state.failed) return;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:441:27: ( WSNLCHARS )*
			loop17:
			while (true) {
				int alt17=2;
				int LA17_0 = input.LA(1);
				if ( ((LA17_0 >= '\t' && LA17_0 <= '\n')||(LA17_0 >= '\f' && LA17_0 <= '\r')||LA17_0==' ') ) {
					alt17=1;
				}

				switch (alt17) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop17;
				}
			}

			match('{'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CHANNELS"

	// $ANTLR start "IMPORT"
	public final void mIMPORT() throws RecognitionException {
		try {
			int _type = IMPORT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:443:14: ( 'import' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:443:16: 'import'
			{
			match("import"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "IMPORT"

	// $ANTLR start "FRAGMENT"
	public final void mFRAGMENT() throws RecognitionException {
		try {
			int _type = FRAGMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:444:14: ( 'fragment' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:444:16: 'fragment'
			{
			match("fragment"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FRAGMENT"

	// $ANTLR start "LEXER"
	public final void mLEXER() throws RecognitionException {
		try {
			int _type = LEXER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:445:14: ( 'lexer' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:445:16: 'lexer'
			{
			match("lexer"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LEXER"

	// $ANTLR start "PARSER"
	public final void mPARSER() throws RecognitionException {
		try {
			int _type = PARSER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:446:14: ( 'parser' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:446:16: 'parser'
			{
			match("parser"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PARSER"

	// $ANTLR start "GRAMMAR"
	public final void mGRAMMAR() throws RecognitionException {
		try {
			int _type = GRAMMAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:447:14: ( 'grammar' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:447:16: 'grammar'
			{
			match("grammar"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GRAMMAR"

	// $ANTLR start "TREE_GRAMMAR"
	public final void mTREE_GRAMMAR() throws RecognitionException {
		try {
			int _type = TREE_GRAMMAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:448:14: ( 'tree' ( WSNLCHARS )* 'grammar' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:448:16: 'tree' ( WSNLCHARS )* 'grammar'
			{
			match("tree"); if (state.failed) return;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:448:23: ( WSNLCHARS )*
			loop18:
			while (true) {
				int alt18=2;
				int LA18_0 = input.LA(1);
				if ( ((LA18_0 >= '\t' && LA18_0 <= '\n')||(LA18_0 >= '\f' && LA18_0 <= '\r')||LA18_0==' ') ) {
					alt18=1;
				}

				switch (alt18) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop18;
				}
			}

			match("grammar"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "TREE_GRAMMAR"

	// $ANTLR start "PROTECTED"
	public final void mPROTECTED() throws RecognitionException {
		try {
			int _type = PROTECTED;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:449:14: ( 'protected' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:449:16: 'protected'
			{
			match("protected"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PROTECTED"

	// $ANTLR start "PUBLIC"
	public final void mPUBLIC() throws RecognitionException {
		try {
			int _type = PUBLIC;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:450:14: ( 'public' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:450:16: 'public'
			{
			match("public"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PUBLIC"

	// $ANTLR start "PRIVATE"
	public final void mPRIVATE() throws RecognitionException {
		try {
			int _type = PRIVATE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:451:14: ( 'private' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:451:16: 'private'
			{
			match("private"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PRIVATE"

	// $ANTLR start "RETURNS"
	public final void mRETURNS() throws RecognitionException {
		try {
			int _type = RETURNS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:452:14: ( 'returns' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:452:16: 'returns'
			{
			match("returns"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RETURNS"

	// $ANTLR start "LOCALS"
	public final void mLOCALS() throws RecognitionException {
		try {
			int _type = LOCALS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:453:14: ( 'locals' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:453:16: 'locals'
			{
			match("locals"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LOCALS"

	// $ANTLR start "THROWS"
	public final void mTHROWS() throws RecognitionException {
		try {
			int _type = THROWS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:454:14: ( 'throws' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:454:16: 'throws'
			{
			match("throws"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "THROWS"

	// $ANTLR start "CATCH"
	public final void mCATCH() throws RecognitionException {
		try {
			int _type = CATCH;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:455:14: ( 'catch' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:455:16: 'catch'
			{
			match("catch"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "CATCH"

	// $ANTLR start "FINALLY"
	public final void mFINALLY() throws RecognitionException {
		try {
			int _type = FINALLY;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:456:14: ( 'finally' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:456:16: 'finally'
			{
			match("finally"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "FINALLY"

	// $ANTLR start "MODE"
	public final void mMODE() throws RecognitionException {
		try {
			int _type = MODE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:457:14: ( 'mode' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:457:16: 'mode'
			{
			match("mode"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "MODE"

	// $ANTLR start "COLON"
	public final void mCOLON() throws RecognitionException {
		try {
			int _type = COLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:464:14: ( ':' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:464:16: ':'
			{
			match(':'); if (state.failed) return;
			if ( state.backtracking==0 ) {
			               // scan backwards, looking for a RULE_REF or TOKEN_REF.
			               // which would indicate the start of a rule definition.
			               // If we see a LPAREN, then it's the start of the subrule.
			               // this.tokens is the token string we are pushing into, so
			               // just loop backwards looking for a rule definition. Then
			               // we set isLexerRule.
			               Token t = getRuleOrSubruleStartToken();
			               if ( t!=null ) {
			                    if ( t.getType()==RULE_REF ) isLexerRule = false;
			                    else if ( t.getType()==TOKEN_REF ) isLexerRule = true;
			                    // else must be subrule; don't alter context
			               }
			               }
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COLON"

	// $ANTLR start "COLONCOLON"
	public final void mCOLONCOLON() throws RecognitionException {
		try {
			int _type = COLONCOLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:480:14: ( '::' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:480:16: '::'
			{
			match("::"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COLONCOLON"

	// $ANTLR start "COMMA"
	public final void mCOMMA() throws RecognitionException {
		try {
			int _type = COMMA;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:481:14: ( ',' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:481:16: ','
			{
			match(','); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMMA"

	// $ANTLR start "SEMI"
	public final void mSEMI() throws RecognitionException {
		try {
			int _type = SEMI;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:482:14: ( ';' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:482:16: ';'
			{
			match(';'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SEMI"

	// $ANTLR start "LPAREN"
	public final void mLPAREN() throws RecognitionException {
		try {
			int _type = LPAREN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:483:14: ( '(' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:483:16: '('
			{
			match('('); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LPAREN"

	// $ANTLR start "RPAREN"
	public final void mRPAREN() throws RecognitionException {
		try {
			int _type = RPAREN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:484:14: ( ')' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:484:16: ')'
			{
			match(')'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RPAREN"

	// $ANTLR start "RARROW"
	public final void mRARROW() throws RecognitionException {
		try {
			int _type = RARROW;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:485:14: ( '->' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:485:16: '->'
			{
			match("->"); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RARROW"

	// $ANTLR start "LT"
	public final void mLT() throws RecognitionException {
		try {
			int _type = LT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:486:14: ( '<' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:486:16: '<'
			{
			match('<'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "LT"

	// $ANTLR start "GT"
	public final void mGT() throws RecognitionException {
		try {
			int _type = GT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:487:14: ( '>' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:487:16: '>'
			{
			match('>'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "GT"

	// $ANTLR start "ASSIGN"
	public final void mASSIGN() throws RecognitionException {
		try {
			int _type = ASSIGN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:488:14: ( '=' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:488:16: '='
			{
			match('='); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ASSIGN"

	// $ANTLR start "QUESTION"
	public final void mQUESTION() throws RecognitionException {
		try {
			int _type = QUESTION;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:489:14: ( '?' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:489:16: '?'
			{
			match('?'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "QUESTION"

	// $ANTLR start "SYNPRED"
	public final void mSYNPRED() throws RecognitionException {
		try {
			int _type = SYNPRED;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:490:14: ( '=>' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:490:16: '=>'
			{
			match("=>"); if (state.failed) return;

			if ( state.backtracking==0 ) {
						    Token t = new CommonToken(input, state.type, state.channel,
						                              state.tokenStartCharIndex, getCharIndex()-1);
							t.setLine(state.tokenStartLine);
							t.setText(state.text);
							t.setCharPositionInLine(state.tokenStartCharPositionInLine);
							grammarError(ErrorType.V3_SYNPRED, t);
			                _channel=HIDDEN;
							}
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SYNPRED"

	// $ANTLR start "STAR"
	public final void mSTAR() throws RecognitionException {
		try {
			int _type = STAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:501:14: ( '*' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:501:16: '*'
			{
			match('*'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STAR"

	// $ANTLR start "PLUS"
	public final void mPLUS() throws RecognitionException {
		try {
			int _type = PLUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:502:14: ( '+' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:502:16: '+'
			{
			match('+'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PLUS"

	// $ANTLR start "PLUS_ASSIGN"
	public final void mPLUS_ASSIGN() throws RecognitionException {
		try {
			int _type = PLUS_ASSIGN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:503:14: ( '+=' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:503:16: '+='
			{
			match("+="); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "PLUS_ASSIGN"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:504:14: ( '|' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:504:16: '|'
			{
			match('|'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "OR"

	// $ANTLR start "DOLLAR"
	public final void mDOLLAR() throws RecognitionException {
		try {
			int _type = DOLLAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:505:14: ( '$' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:505:16: '$'
			{
			match('$'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOLLAR"

	// $ANTLR start "DOT"
	public final void mDOT() throws RecognitionException {
		try {
			int _type = DOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:506:11: ( '.' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:506:13: '.'
			{
			match('.'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "DOT"

	// $ANTLR start "RANGE"
	public final void mRANGE() throws RecognitionException {
		try {
			int _type = RANGE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:507:14: ( '..' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:507:16: '..'
			{
			match(".."); if (state.failed) return;

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RANGE"

	// $ANTLR start "AT"
	public final void mAT() throws RecognitionException {
		try {
			int _type = AT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:508:14: ( '@' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:508:16: '@'
			{
			match('@'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "AT"

	// $ANTLR start "POUND"
	public final void mPOUND() throws RecognitionException {
		try {
			int _type = POUND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:509:14: ( '#' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:509:16: '#'
			{
			match('#'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "POUND"

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:510:14: ( '~' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:510:16: '~'
			{
			match('~'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NOT"

	// $ANTLR start "RBRACE"
	public final void mRBRACE() throws RecognitionException {
		try {
			int _type = RBRACE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:511:14: ( '}' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:511:16: '}'
			{
			match('}'); if (state.failed) return;
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "RBRACE"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			CommonToken a=null;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:514:6: (a= NameStartChar ( NameChar )* )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:514:8: a= NameStartChar ( NameChar )*
			{
			int aStart2935 = getCharIndex();
			int aStartLine2935 = getLine();
			int aStartCharPos2935 = getCharPositionInLine();
			mNameStartChar(); if (state.failed) return;
			a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart2935, getCharIndex()-1);
			a.setLine(aStartLine2935);
			a.setCharPositionInLine(aStartCharPos2935);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:514:24: ( NameChar )*
			loop19:
			while (true) {
				int alt19=2;
				int LA19_0 = input.LA(1);
				if ( ((LA19_0 >= '0' && LA19_0 <= '9')||(LA19_0 >= 'A' && LA19_0 <= 'Z')||LA19_0=='_'||(LA19_0 >= 'a' && LA19_0 <= 'z')||LA19_0=='\u00B7'||(LA19_0 >= '\u00C0' && LA19_0 <= '\u00D6')||(LA19_0 >= '\u00D8' && LA19_0 <= '\u00F6')||(LA19_0 >= '\u00F8' && LA19_0 <= '\u037D')||(LA19_0 >= '\u037F' && LA19_0 <= '\u1FFF')||(LA19_0 >= '\u200C' && LA19_0 <= '\u200D')||(LA19_0 >= '\u203F' && LA19_0 <= '\u2040')||(LA19_0 >= '\u2070' && LA19_0 <= '\u218F')||(LA19_0 >= '\u2C00' && LA19_0 <= '\u2FEF')||(LA19_0 >= '\u3001' && LA19_0 <= '\uD7FF')||(LA19_0 >= '\uF900' && LA19_0 <= '\uFDCF')||(LA19_0 >= '\uFDF0' && LA19_0 <= '\uFEFE')||(LA19_0 >= '\uFF00' && LA19_0 <= '\uFFFD')) ) {
					alt19=1;
				}

				switch (alt19) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='\u00B7'||(input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6')||(input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6')||(input.LA(1) >= '\u00F8' && input.LA(1) <= '\u037D')||(input.LA(1) >= '\u037F' && input.LA(1) <= '\u1FFF')||(input.LA(1) >= '\u200C' && input.LA(1) <= '\u200D')||(input.LA(1) >= '\u203F' && input.LA(1) <= '\u2040')||(input.LA(1) >= '\u2070' && input.LA(1) <= '\u218F')||(input.LA(1) >= '\u2C00' && input.LA(1) <= '\u2FEF')||(input.LA(1) >= '\u3001' && input.LA(1) <= '\uD7FF')||(input.LA(1) >= '\uF900' && input.LA(1) <= '\uFDCF')||(input.LA(1) >= '\uFDF0' && input.LA(1) <= '\uFEFE')||(input.LA(1) >= '\uFF00' && input.LA(1) <= '\uFFFD') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop19;
				}
			}

			if ( state.backtracking==0 ) {
							if ( Grammar.isTokenName((a!=null?a.getText():null)) ) _type = TOKEN_REF;
							else _type = RULE_REF;
							}
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ID"

	// $ANTLR start "NameChar"
	public final void mNameChar() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:522:13: ( NameStartChar | '0' .. '9' | '_' | '\\u00B7' | '\\u0300' .. '\\u036F' | '\\u203F' .. '\\u2040' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||input.LA(1)=='\u00B7'||(input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6')||(input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6')||(input.LA(1) >= '\u00F8' && input.LA(1) <= '\u037D')||(input.LA(1) >= '\u037F' && input.LA(1) <= '\u1FFF')||(input.LA(1) >= '\u200C' && input.LA(1) <= '\u200D')||(input.LA(1) >= '\u203F' && input.LA(1) <= '\u2040')||(input.LA(1) >= '\u2070' && input.LA(1) <= '\u218F')||(input.LA(1) >= '\u2C00' && input.LA(1) <= '\u2FEF')||(input.LA(1) >= '\u3001' && input.LA(1) <= '\uD7FF')||(input.LA(1) >= '\uF900' && input.LA(1) <= '\uFDCF')||(input.LA(1) >= '\uFDF0' && input.LA(1) <= '\uFEFE')||(input.LA(1) >= '\uFF00' && input.LA(1) <= '\uFFFD') ) {
				input.consume();
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NameChar"

	// $ANTLR start "NameStartChar"
	public final void mNameStartChar() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:532:13: ( 'A' .. 'Z' | 'a' .. 'z' | '\\u00C0' .. '\\u00D6' | '\\u00D8' .. '\\u00F6' | '\\u00F8' .. '\\u02FF' | '\\u0370' .. '\\u037D' | '\\u037F' .. '\\u1FFF' | '\\u200C' .. '\\u200D' | '\\u2070' .. '\\u218F' | '\\u2C00' .. '\\u2FEF' | '\\u3001' .. '\\uD7FF' | '\\uF900' .. '\\uFDCF' | '\\uFDF0' .. '\\uFEFE' | '\\uFF00' .. '\\uFFFD' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z')||(input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6')||(input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6')||(input.LA(1) >= '\u00F8' && input.LA(1) <= '\u02FF')||(input.LA(1) >= '\u0370' && input.LA(1) <= '\u037D')||(input.LA(1) >= '\u037F' && input.LA(1) <= '\u1FFF')||(input.LA(1) >= '\u200C' && input.LA(1) <= '\u200D')||(input.LA(1) >= '\u2070' && input.LA(1) <= '\u218F')||(input.LA(1) >= '\u2C00' && input.LA(1) <= '\u2FEF')||(input.LA(1) >= '\u3001' && input.LA(1) <= '\uD7FF')||(input.LA(1) >= '\uF900' && input.LA(1) <= '\uFDCF')||(input.LA(1) >= '\uFDF0' && input.LA(1) <= '\uFEFE')||(input.LA(1) >= '\uFF00' && input.LA(1) <= '\uFFFD') ) {
				input.consume();
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NameStartChar"

	// $ANTLR start "ACTION_CHAR_LITERAL"
	public final void mACTION_CHAR_LITERAL() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:566:2: ( '\\'' ( ( '\\\\' )=> ACTION_ESC |~ '\\'' )* '\\'' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:566:4: '\\'' ( ( '\\\\' )=> ACTION_ESC |~ '\\'' )* '\\''
			{
			match('\''); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:566:9: ( ( '\\\\' )=> ACTION_ESC |~ '\\'' )*
			loop20:
			while (true) {
				int alt20=3;
				int LA20_0 = input.LA(1);
				if ( (LA20_0=='\\') ) {
					int LA20_2 = input.LA(2);
					if ( (LA20_2=='\'') ) {
						int LA20_4 = input.LA(3);
						if ( (LA20_4=='\'') && (synpred5_ANTLRLexer())) {
							alt20=1;
						}
						else if ( (LA20_4=='\\') && (synpred5_ANTLRLexer())) {
							alt20=1;
						}
						else if ( ((LA20_4 >= '\u0000' && LA20_4 <= '&')||(LA20_4 >= '(' && LA20_4 <= '[')||(LA20_4 >= ']' && LA20_4 <= '\uFFFF')) && (synpred5_ANTLRLexer())) {
							alt20=1;
						}
						else {
							alt20=2;
						}

					}
					else if ( (LA20_2=='\\') ) {
						int LA20_5 = input.LA(3);
						if ( (synpred5_ANTLRLexer()) ) {
							alt20=1;
						}
						else if ( (true) ) {
							alt20=2;
						}

					}
					else if ( ((LA20_2 >= '\u0000' && LA20_2 <= '&')||(LA20_2 >= '(' && LA20_2 <= '[')||(LA20_2 >= ']' && LA20_2 <= '\uFFFF')) ) {
						int LA20_6 = input.LA(3);
						if ( (synpred5_ANTLRLexer()) ) {
							alt20=1;
						}
						else if ( (true) ) {
							alt20=2;
						}

					}

				}
				else if ( ((LA20_0 >= '\u0000' && LA20_0 <= '&')||(LA20_0 >= '(' && LA20_0 <= '[')||(LA20_0 >= ']' && LA20_0 <= '\uFFFF')) ) {
					alt20=2;
				}

				switch (alt20) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:566:10: ( '\\\\' )=> ACTION_ESC
					{
					mACTION_ESC(); if (state.failed) return;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:566:31: ~ '\\''
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop20;
				}
			}

			match('\''); if (state.failed) return;
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ACTION_CHAR_LITERAL"

	// $ANTLR start "ACTION_STRING_LITERAL"
	public final void mACTION_STRING_LITERAL() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:576:2: ( '\"' ( ( '\\\\' )=> ACTION_ESC |~ '\"' )* '\"' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:576:4: '\"' ( ( '\\\\' )=> ACTION_ESC |~ '\"' )* '\"'
			{
			match('\"'); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:576:8: ( ( '\\\\' )=> ACTION_ESC |~ '\"' )*
			loop21:
			while (true) {
				int alt21=3;
				int LA21_0 = input.LA(1);
				if ( (LA21_0=='\\') ) {
					int LA21_2 = input.LA(2);
					if ( (LA21_2=='\"') ) {
						int LA21_4 = input.LA(3);
						if ( (LA21_4=='\"') && (synpred6_ANTLRLexer())) {
							alt21=1;
						}
						else if ( (LA21_4=='\\') && (synpred6_ANTLRLexer())) {
							alt21=1;
						}
						else if ( ((LA21_4 >= '\u0000' && LA21_4 <= '!')||(LA21_4 >= '#' && LA21_4 <= '[')||(LA21_4 >= ']' && LA21_4 <= '\uFFFF')) && (synpred6_ANTLRLexer())) {
							alt21=1;
						}
						else {
							alt21=2;
						}

					}
					else if ( (LA21_2=='\\') ) {
						int LA21_5 = input.LA(3);
						if ( (synpred6_ANTLRLexer()) ) {
							alt21=1;
						}
						else if ( (true) ) {
							alt21=2;
						}

					}
					else if ( ((LA21_2 >= '\u0000' && LA21_2 <= '!')||(LA21_2 >= '#' && LA21_2 <= '[')||(LA21_2 >= ']' && LA21_2 <= '\uFFFF')) ) {
						int LA21_6 = input.LA(3);
						if ( (synpred6_ANTLRLexer()) ) {
							alt21=1;
						}
						else if ( (true) ) {
							alt21=2;
						}

					}

				}
				else if ( ((LA21_0 >= '\u0000' && LA21_0 <= '!')||(LA21_0 >= '#' && LA21_0 <= '[')||(LA21_0 >= ']' && LA21_0 <= '\uFFFF')) ) {
					alt21=2;
				}

				switch (alt21) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:576:9: ( '\\\\' )=> ACTION_ESC
					{
					mACTION_ESC(); if (state.failed) return;

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:576:30: ~ '\"'
					{
					if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '\uFFFF') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					break loop21;
				}
			}

			match('\"'); if (state.failed) return;
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ACTION_STRING_LITERAL"

	// $ANTLR start "ACTION_ESC"
	public final void mACTION_ESC() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:586:2: ( '\\\\' . )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:586:4: '\\\\' .
			{
			match('\\'); if (state.failed) return;
			matchAny(); if (state.failed) return;
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ACTION_ESC"

	// $ANTLR start "INT"
	public final void mINT() throws RecognitionException {
		try {
			int _type = INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:594:5: ( ( '0' .. '9' )+ )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:594:7: ( '0' .. '9' )+
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:594:7: ( '0' .. '9' )+
			int cnt22=0;
			loop22:
			while (true) {
				int alt22=2;
				int LA22_0 = input.LA(1);
				if ( ((LA22_0 >= '0' && LA22_0 <= '9')) ) {
					alt22=1;
				}

				switch (alt22) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt22 >= 1 ) break loop22;
					if (state.backtracking>0) {state.failed=true; return;}
					EarlyExitException eee = new EarlyExitException(22, input);
					throw eee;
				}
				cnt22++;
			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "INT"

	// $ANTLR start "SRC"
	public final void mSRC() throws RecognitionException {
		try {
			CommonToken file=null;
			CommonToken line=null;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:606:5: ( 'src' ( WSCHARS )+ file= ACTION_STRING_LITERAL ( WSCHARS )+ line= INT )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:606:7: 'src' ( WSCHARS )+ file= ACTION_STRING_LITERAL ( WSCHARS )+ line= INT
			{
			match("src"); if (state.failed) return;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:606:13: ( WSCHARS )+
			int cnt23=0;
			loop23:
			while (true) {
				int alt23=2;
				int LA23_0 = input.LA(1);
				if ( (LA23_0=='\t'||LA23_0=='\f'||LA23_0==' ') ) {
					alt23=1;
				}

				switch (alt23) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt23 >= 1 ) break loop23;
					if (state.backtracking>0) {state.failed=true; return;}
					EarlyExitException eee = new EarlyExitException(23, input);
					throw eee;
				}
				cnt23++;
			}

			int fileStart3507 = getCharIndex();
			int fileStartLine3507 = getLine();
			int fileStartCharPos3507 = getCharPositionInLine();
			mACTION_STRING_LITERAL(); if (state.failed) return;
			file = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, fileStart3507, getCharIndex()-1);
			file.setLine(fileStartLine3507);
			file.setCharPositionInLine(fileStartCharPos3507);

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:606:49: ( WSCHARS )+
			int cnt24=0;
			loop24:
			while (true) {
				int alt24=2;
				int LA24_0 = input.LA(1);
				if ( (LA24_0=='\t'||LA24_0=='\f'||LA24_0==' ') ) {
					alt24=1;
				}

				switch (alt24) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt24 >= 1 ) break loop24;
					if (state.backtracking>0) {state.failed=true; return;}
					EarlyExitException eee = new EarlyExitException(24, input);
					throw eee;
				}
				cnt24++;
			}

			int lineStart3514 = getCharIndex();
			int lineStartLine3514 = getLine();
			int lineStartCharPos3514 = getCharPositionInLine();
			mINT(); if (state.failed) return;
			line = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, lineStart3514, getCharIndex()-1);
			line.setLine(lineStartLine3514);
			line.setCharPositionInLine(lineStartCharPos3514);

			if ( state.backtracking==0 ) {
			         // TODO: Add target specific code to change the source file name and current line number
			         //
			      }
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "SRC"

	// $ANTLR start "STRING_LITERAL"
	public final void mSTRING_LITERAL() throws RecognitionException {
		try {
			int _type = STRING_LITERAL;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:621:5: ( '\\'' ( ( ESC_SEQ |~ ( '\\\\' | '\\'' | '\\r' | '\\n' ) ) )* ( '\\'' |) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:621:8: '\\'' ( ( ESC_SEQ |~ ( '\\\\' | '\\'' | '\\r' | '\\n' ) ) )* ( '\\'' |)
			{
			match('\''); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:621:13: ( ( ESC_SEQ |~ ( '\\\\' | '\\'' | '\\r' | '\\n' ) ) )*
			loop26:
			while (true) {
				int alt26=2;
				int LA26_0 = input.LA(1);
				if ( ((LA26_0 >= '\u0000' && LA26_0 <= '\t')||(LA26_0 >= '\u000B' && LA26_0 <= '\f')||(LA26_0 >= '\u000E' && LA26_0 <= '&')||(LA26_0 >= '(' && LA26_0 <= '\uFFFF')) ) {
					alt26=1;
				}

				switch (alt26) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:621:15: ( ESC_SEQ |~ ( '\\\\' | '\\'' | '\\r' | '\\n' ) )
					{
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:621:15: ( ESC_SEQ |~ ( '\\\\' | '\\'' | '\\r' | '\\n' ) )
					int alt25=2;
					int LA25_0 = input.LA(1);
					if ( (LA25_0=='\\') ) {
						alt25=1;
					}
					else if ( ((LA25_0 >= '\u0000' && LA25_0 <= '\t')||(LA25_0 >= '\u000B' && LA25_0 <= '\f')||(LA25_0 >= '\u000E' && LA25_0 <= '&')||(LA25_0 >= '(' && LA25_0 <= '[')||(LA25_0 >= ']' && LA25_0 <= '\uFFFF')) ) {
						alt25=2;
					}

					else {
						if (state.backtracking>0) {state.failed=true; return;}
						NoViableAltException nvae =
							new NoViableAltException("", 25, 0, input);
						throw nvae;
					}

					switch (alt25) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:621:17: ESC_SEQ
							{
							mESC_SEQ(); if (state.failed) return;

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:621:27: ~ ( '\\\\' | '\\'' | '\\r' | '\\n' )
							{
							if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
								input.consume();
								state.failed=false;
							}
							else {
								if (state.backtracking>0) {state.failed=true; return;}
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							}
							break;

					}

					}
					break;

				default :
					break loop26;
				}
			}

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:622:8: ( '\\'' |)
			int alt27=2;
			int LA27_0 = input.LA(1);
			if ( (LA27_0=='\'') ) {
				alt27=1;
			}

			else {
				alt27=2;
			}

			switch (alt27) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:622:13: '\\''
					{
					match('\''); if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:624:13: 
					{
					if ( state.backtracking==0 ) {
					            Token t = new CommonToken(input, state.type, state.channel, state.tokenStartCharIndex, getCharIndex()-1);
					            t.setLine(state.tokenStartLine);
					            t.setText(state.text);
					            t.setCharPositionInLine(state.tokenStartCharPositionInLine);
					            grammarError(ErrorType.UNTERMINATED_STRING_LITERAL, t);
					            }
					}
					break;

			}

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "STRING_LITERAL"

	// $ANTLR start "HEX_DIGIT"
	public final void mHEX_DIGIT() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:637:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
			{
			if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
				input.consume();
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "HEX_DIGIT"

	// $ANTLR start "ESC_SEQ"
	public final void mESC_SEQ() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:644:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | UNICODE_ESC |) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:644:7: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | UNICODE_ESC |)
			{
			match('\\'); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:645:9: ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' | UNICODE_ESC |)
			int alt28=10;
			switch ( input.LA(1) ) {
			case 'b':
				{
				alt28=1;
				}
				break;
			case 't':
				{
				alt28=2;
				}
				break;
			case 'n':
				{
				alt28=3;
				}
				break;
			case 'f':
				{
				alt28=4;
				}
				break;
			case 'r':
				{
				alt28=5;
				}
				break;
			case '\"':
				{
				alt28=6;
				}
				break;
			case '\'':
				{
				alt28=7;
				}
				break;
			case '\\':
				{
				alt28=8;
				}
				break;
			case 'u':
				{
				alt28=9;
				}
				break;
			default:
				alt28=10;
			}
			switch (alt28) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:9: 'b'
					{
					match('b'); if (state.failed) return;
					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:13: 't'
					{
					match('t'); if (state.failed) return;
					}
					break;
				case 3 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:17: 'n'
					{
					match('n'); if (state.failed) return;
					}
					break;
				case 4 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:21: 'f'
					{
					match('f'); if (state.failed) return;
					}
					break;
				case 5 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:25: 'r'
					{
					match('r'); if (state.failed) return;
					}
					break;
				case 6 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:29: '\\\"'
					{
					match('\"'); if (state.failed) return;
					}
					break;
				case 7 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:34: '\\''
					{
					match('\''); if (state.failed) return;
					}
					break;
				case 8 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:649:39: '\\\\'
					{
					match('\\'); if (state.failed) return;
					}
					break;
				case 9 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:653:12: UNICODE_ESC
					{
					mUNICODE_ESC(); if (state.failed) return;

					}
					break;
				case 10 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:657:12: 
					{
					if ( state.backtracking==0 ) {
					                Token t = new CommonToken(input, state.type, state.channel, getCharIndex()-1, getCharIndex());
					                t.setText(t.getText());
					                t.setLine(input.getLine());
					                t.setCharPositionInLine(input.getCharPositionInLine()-1);
					                grammarError(ErrorType.INVALID_ESCAPE_SEQUENCE, t);
					    	      }
					}
					break;

			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ESC_SEQ"

	// $ANTLR start "UNICODE_ESC"
	public final void mUNICODE_ESC() throws RecognitionException {
		try {


				// Flag to tell us whether we have a valid number of
				// hex digits in the escape sequence
				//
				int	hCount = 0;

			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:677:5: ( 'u' ( ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |) |) ) |) )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:677:9: 'u' ( ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |) |) ) |)
			{
			match('u'); if (state.failed) return;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:686:6: ( ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |) |) ) |)
			int alt32=2;
			int LA32_0 = input.LA(1);
			if ( ((LA32_0 >= '0' && LA32_0 <= '9')||(LA32_0 >= 'A' && LA32_0 <= 'F')||(LA32_0 >= 'a' && LA32_0 <= 'f')) ) {
				alt32=1;
			}

			else {
				alt32=2;
			}

			switch (alt32) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:687:9: ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |) |) )
					{
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:687:9: ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |) |) )
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:688:12: HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |) |)
					{
					mHEX_DIGIT(); if (state.failed) return;

					if ( state.backtracking==0 ) { hCount++; }
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:689:14: ( HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |) |)
					int alt31=2;
					int LA31_0 = input.LA(1);
					if ( ((LA31_0 >= '0' && LA31_0 <= '9')||(LA31_0 >= 'A' && LA31_0 <= 'F')||(LA31_0 >= 'a' && LA31_0 <= 'f')) ) {
						alt31=1;
					}

					else {
						alt31=2;
					}

					switch (alt31) {
						case 1 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:690:19: HEX_DIGIT ( HEX_DIGIT ( HEX_DIGIT |) |)
							{
							mHEX_DIGIT(); if (state.failed) return;

							if ( state.backtracking==0 ) { hCount++; }
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:691:16: ( HEX_DIGIT ( HEX_DIGIT |) |)
							int alt30=2;
							int LA30_0 = input.LA(1);
							if ( ((LA30_0 >= '0' && LA30_0 <= '9')||(LA30_0 >= 'A' && LA30_0 <= 'F')||(LA30_0 >= 'a' && LA30_0 <= 'f')) ) {
								alt30=1;
							}

							else {
								alt30=2;
							}

							switch (alt30) {
								case 1 :
									// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:692:21: HEX_DIGIT ( HEX_DIGIT |)
									{
									mHEX_DIGIT(); if (state.failed) return;

									if ( state.backtracking==0 ) { hCount++; }
									// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:693:21: ( HEX_DIGIT |)
									int alt29=2;
									int LA29_0 = input.LA(1);
									if ( ((LA29_0 >= '0' && LA29_0 <= '9')||(LA29_0 >= 'A' && LA29_0 <= 'F')||(LA29_0 >= 'a' && LA29_0 <= 'f')) ) {
										alt29=1;
									}

									else {
										alt29=2;
									}

									switch (alt29) {
										case 1 :
											// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:696:25: HEX_DIGIT
											{
											mHEX_DIGIT(); if (state.failed) return;

											if ( state.backtracking==0 ) { hCount++; }
											}
											break;
										case 2 :
											// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:699:21: 
											{
											}
											break;

									}

									}
									break;
								case 2 :
									// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:702:17: 
									{
									}
									break;

							}

							}
							break;
						case 2 :
							// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:705:11: 
							{
							}
							break;

					}

					}

					}
					break;
				case 2 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:708:6: 
					{
					}
					break;

			}

			if ( state.backtracking==0 ) {
			    		if (hCount != 4) {
			                Token t = new CommonToken(input, state.type, state.channel, getCharIndex()-3-hCount, getCharIndex()-1);
			                t.setText(t.getText());
			                t.setLine(input.getLine());
			                t.setCharPositionInLine(input.getCharPositionInLine()-hCount-2);
			                grammarError(ErrorType.INVALID_ESCAPE_SEQUENCE, t);
			    		}
			    	}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "UNICODE_ESC"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:731:5: ( ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+ )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:731:7: ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+
			{
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:731:7: ( ' ' | '\\t' | '\\r' | '\\n' | '\\f' )+
			int cnt33=0;
			loop33:
			while (true) {
				int alt33=2;
				int LA33_0 = input.LA(1);
				if ( ((LA33_0 >= '\t' && LA33_0 <= '\n')||(LA33_0 >= '\f' && LA33_0 <= '\r')||LA33_0==' ') ) {
					alt33=1;
				}

				switch (alt33) {
				case 1 :
					// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
					{
					if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
						input.consume();
						state.failed=false;
					}
					else {
						if (state.backtracking>0) {state.failed=true; return;}
						MismatchedSetException mse = new MismatchedSetException(null,input);
						recover(mse);
						throw mse;
					}
					}
					break;

				default :
					if ( cnt33 >= 1 ) break loop33;
					if (state.backtracking>0) {state.failed=true; return;}
					EarlyExitException eee = new EarlyExitException(33, input);
					throw eee;
				}
				cnt33++;
			}

			if ( state.backtracking==0 ) {_channel=HIDDEN;}
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WS"

	// $ANTLR start "NLCHARS"
	public final void mNLCHARS() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:746:5: ( '\\n' | '\\r' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
			{
			if ( input.LA(1)=='\n'||input.LA(1)=='\r' ) {
				input.consume();
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NLCHARS"

	// $ANTLR start "WSCHARS"
	public final void mWSCHARS() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:754:5: ( ' ' | '\\t' | '\\f' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
			{
			if ( input.LA(1)=='\t'||input.LA(1)=='\f'||input.LA(1)==' ' ) {
				input.consume();
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WSCHARS"

	// $ANTLR start "WSNLCHARS"
	public final void mWSNLCHARS() throws RecognitionException {
		try {
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:763:5: ( ' ' | '\\t' | '\\f' | '\\n' | '\\r' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
			{
			if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
				input.consume();
				state.failed=false;
			}
			else {
				if (state.backtracking>0) {state.failed=true; return;}
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "WSNLCHARS"

	// $ANTLR start "UnicodeBOM"
	public final void mUnicodeBOM() throws RecognitionException {
		try {
			int _type = UnicodeBOM;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:772:5: ( '\\uFEFF' )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:772:9: '\\uFEFF'
			{
			match('\uFEFF'); if (state.failed) return;
			if ( state.backtracking==0 ) {skip();}
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "UnicodeBOM"

	// $ANTLR start "ERRCHAR"
	public final void mERRCHAR() throws RecognitionException {
		try {
			int _type = ERRCHAR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:787:5: ( . )
			// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:787:7: .
			{
			matchAny(); if (state.failed) return;
			if ( state.backtracking==0 ) {
			         Token t = new CommonToken(input, state.type, state.channel, state.tokenStartCharIndex, getCharIndex()-1);
			         t.setLine(state.tokenStartLine);
			         t.setText(state.text);
			         t.setCharPositionInLine(state.tokenStartCharPositionInLine);
			         String msg = getTokenErrorDisplay(t) + " came as a complete surprise to me";
			         grammarError(ErrorType.SYNTAX_ERROR, t, msg);
			         state.syntaxErrors++;
			         skip();
			      }
			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ERRCHAR"

	@Override
	public void mTokens() throws RecognitionException {
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:8: ( COMMENT | ARG_OR_CHARSET | ACTION | OPTIONS | TOKENS_SPEC | CHANNELS | IMPORT | FRAGMENT | LEXER | PARSER | GRAMMAR | TREE_GRAMMAR | PROTECTED | PUBLIC | PRIVATE | RETURNS | LOCALS | THROWS | CATCH | FINALLY | MODE | COLON | COLONCOLON | COMMA | SEMI | LPAREN | RPAREN | RARROW | LT | GT | ASSIGN | QUESTION | SYNPRED | STAR | PLUS | PLUS_ASSIGN | OR | DOLLAR | DOT | RANGE | AT | POUND | NOT | RBRACE | ID | INT | STRING_LITERAL | WS | UnicodeBOM | ERRCHAR )
		int alt34=50;
		alt34 = dfa34.predict(input);
		switch (alt34) {
			case 1 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:10: COMMENT
				{
				mCOMMENT(); if (state.failed) return;

				}
				break;
			case 2 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:18: ARG_OR_CHARSET
				{
				mARG_OR_CHARSET(); if (state.failed) return;

				}
				break;
			case 3 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:33: ACTION
				{
				mACTION(); if (state.failed) return;

				}
				break;
			case 4 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:40: OPTIONS
				{
				mOPTIONS(); if (state.failed) return;

				}
				break;
			case 5 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:48: TOKENS_SPEC
				{
				mTOKENS_SPEC(); if (state.failed) return;

				}
				break;
			case 6 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:60: CHANNELS
				{
				mCHANNELS(); if (state.failed) return;

				}
				break;
			case 7 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:69: IMPORT
				{
				mIMPORT(); if (state.failed) return;

				}
				break;
			case 8 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:76: FRAGMENT
				{
				mFRAGMENT(); if (state.failed) return;

				}
				break;
			case 9 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:85: LEXER
				{
				mLEXER(); if (state.failed) return;

				}
				break;
			case 10 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:91: PARSER
				{
				mPARSER(); if (state.failed) return;

				}
				break;
			case 11 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:98: GRAMMAR
				{
				mGRAMMAR(); if (state.failed) return;

				}
				break;
			case 12 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:106: TREE_GRAMMAR
				{
				mTREE_GRAMMAR(); if (state.failed) return;

				}
				break;
			case 13 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:119: PROTECTED
				{
				mPROTECTED(); if (state.failed) return;

				}
				break;
			case 14 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:129: PUBLIC
				{
				mPUBLIC(); if (state.failed) return;

				}
				break;
			case 15 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:136: PRIVATE
				{
				mPRIVATE(); if (state.failed) return;

				}
				break;
			case 16 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:144: RETURNS
				{
				mRETURNS(); if (state.failed) return;

				}
				break;
			case 17 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:152: LOCALS
				{
				mLOCALS(); if (state.failed) return;

				}
				break;
			case 18 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:159: THROWS
				{
				mTHROWS(); if (state.failed) return;

				}
				break;
			case 19 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:166: CATCH
				{
				mCATCH(); if (state.failed) return;

				}
				break;
			case 20 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:172: FINALLY
				{
				mFINALLY(); if (state.failed) return;

				}
				break;
			case 21 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:180: MODE
				{
				mMODE(); if (state.failed) return;

				}
				break;
			case 22 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:185: COLON
				{
				mCOLON(); if (state.failed) return;

				}
				break;
			case 23 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:191: COLONCOLON
				{
				mCOLONCOLON(); if (state.failed) return;

				}
				break;
			case 24 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:202: COMMA
				{
				mCOMMA(); if (state.failed) return;

				}
				break;
			case 25 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:208: SEMI
				{
				mSEMI(); if (state.failed) return;

				}
				break;
			case 26 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:213: LPAREN
				{
				mLPAREN(); if (state.failed) return;

				}
				break;
			case 27 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:220: RPAREN
				{
				mRPAREN(); if (state.failed) return;

				}
				break;
			case 28 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:227: RARROW
				{
				mRARROW(); if (state.failed) return;

				}
				break;
			case 29 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:234: LT
				{
				mLT(); if (state.failed) return;

				}
				break;
			case 30 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:237: GT
				{
				mGT(); if (state.failed) return;

				}
				break;
			case 31 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:240: ASSIGN
				{
				mASSIGN(); if (state.failed) return;

				}
				break;
			case 32 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:247: QUESTION
				{
				mQUESTION(); if (state.failed) return;

				}
				break;
			case 33 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:256: SYNPRED
				{
				mSYNPRED(); if (state.failed) return;

				}
				break;
			case 34 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:264: STAR
				{
				mSTAR(); if (state.failed) return;

				}
				break;
			case 35 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:269: PLUS
				{
				mPLUS(); if (state.failed) return;

				}
				break;
			case 36 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:274: PLUS_ASSIGN
				{
				mPLUS_ASSIGN(); if (state.failed) return;

				}
				break;
			case 37 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:286: OR
				{
				mOR(); if (state.failed) return;

				}
				break;
			case 38 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:289: DOLLAR
				{
				mDOLLAR(); if (state.failed) return;

				}
				break;
			case 39 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:296: DOT
				{
				mDOT(); if (state.failed) return;

				}
				break;
			case 40 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:300: RANGE
				{
				mRANGE(); if (state.failed) return;

				}
				break;
			case 41 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:306: AT
				{
				mAT(); if (state.failed) return;

				}
				break;
			case 42 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:309: POUND
				{
				mPOUND(); if (state.failed) return;

				}
				break;
			case 43 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:315: NOT
				{
				mNOT(); if (state.failed) return;

				}
				break;
			case 44 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:319: RBRACE
				{
				mRBRACE(); if (state.failed) return;

				}
				break;
			case 45 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:326: ID
				{
				mID(); if (state.failed) return;

				}
				break;
			case 46 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:329: INT
				{
				mINT(); if (state.failed) return;

				}
				break;
			case 47 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:333: STRING_LITERAL
				{
				mSTRING_LITERAL(); if (state.failed) return;

				}
				break;
			case 48 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:348: WS
				{
				mWS(); if (state.failed) return;

				}
				break;
			case 49 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:351: UnicodeBOM
				{
				mUnicodeBOM(); if (state.failed) return;

				}
				break;
			case 50 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:1:362: ERRCHAR
				{
				mERRCHAR(); if (state.failed) return;

				}
				break;

		}
	}

	// $ANTLR start synpred1_ANTLRLexer
	public final void synpred1_ANTLRLexer_fragment() throws RecognitionException {
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:216:17: ( ' $ANTLR' )
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:216:18: ' $ANTLR'
		{
		match(" $ANTLR"); if (state.failed) return;

		}

	}
	// $ANTLR end synpred1_ANTLRLexer

	// $ANTLR start synpred2_ANTLRLexer
	public final void synpred2_ANTLRLexer_fragment() throws RecognitionException {
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:310:14: ( '\"' )
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:310:15: '\"'
		{
		match('\"'); if (state.failed) return;
		}

	}
	// $ANTLR end synpred2_ANTLRLexer

	// $ANTLR start synpred3_ANTLRLexer
	public final void synpred3_ANTLRLexer_fragment() throws RecognitionException {
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:312:14: ( '\\'' )
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:312:15: '\\''
		{
		match('\''); if (state.failed) return;
		}

	}
	// $ANTLR end synpred3_ANTLRLexer

	// $ANTLR start synpred4_ANTLRLexer
	public final void synpred4_ANTLRLexer_fragment() throws RecognitionException {
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:334:6: ( ( WSNLCHARS )* '=>' )
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:334:7: ( WSNLCHARS )* '=>'
		{
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:334:7: ( WSNLCHARS )*
		loop35:
		while (true) {
			int alt35=2;
			int LA35_0 = input.LA(1);
			if ( ((LA35_0 >= '\t' && LA35_0 <= '\n')||(LA35_0 >= '\f' && LA35_0 <= '\r')||LA35_0==' ') ) {
				alt35=1;
			}

			switch (alt35) {
			case 1 :
				// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:
				{
				if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
					input.consume();
					state.failed=false;
				}
				else {
					if (state.backtracking>0) {state.failed=true; return;}
					MismatchedSetException mse = new MismatchedSetException(null,input);
					recover(mse);
					throw mse;
				}
				}
				break;

			default :
				break loop35;
			}
		}

		match("=>"); if (state.failed) return;

		}

	}
	// $ANTLR end synpred4_ANTLRLexer

	// $ANTLR start synpred5_ANTLRLexer
	public final void synpred5_ANTLRLexer_fragment() throws RecognitionException {
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:566:10: ( '\\\\' )
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:566:11: '\\\\'
		{
		match('\\'); if (state.failed) return;
		}

	}
	// $ANTLR end synpred5_ANTLRLexer

	// $ANTLR start synpred6_ANTLRLexer
	public final void synpred6_ANTLRLexer_fragment() throws RecognitionException {
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:576:9: ( '\\\\' )
		// /Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/parse/ANTLRLexer.g:576:10: '\\\\'
		{
		match('\\'); if (state.failed) return;
		}

	}
	// $ANTLR end synpred6_ANTLRLexer

	public final boolean synpred5_ANTLRLexer() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred5_ANTLRLexer_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred4_ANTLRLexer() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred4_ANTLRLexer_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred3_ANTLRLexer() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred3_ANTLRLexer_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred1_ANTLRLexer() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred1_ANTLRLexer_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred2_ANTLRLexer() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred2_ANTLRLexer_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}
	public final boolean synpred6_ANTLRLexer() {
		state.backtracking++;
		int start = input.mark();
		try {
			synpred6_ANTLRLexer_fragment(); // can never throw exception
		} catch (RecognitionException re) {
			System.err.println("impossible: "+re);
		}
		boolean success = !state.failed;
		input.rewind(start);
		state.backtracking--;
		state.failed=false;
		return success;
	}


	protected DFA2 dfa2 = new DFA2(this);
	protected DFA34 dfa34 = new DFA34(this);
	static final String DFA2_eotS =
		"\2\2\1\uffff\16\2\1\uffff\3\2\1\uffff\2\2\2\uffff";
	static final String DFA2_eofS =
		"\32\uffff";
	static final String DFA2_minS =
		"\1\40\1\44\1\uffff\1\101\1\116\1\124\1\114\1\122\1\163\1\162\1\143\2\11"+
		"\3\0\1\11\1\uffff\3\0\1\uffff\1\11\3\0";
	static final String DFA2_maxS =
		"\1\40\1\44\1\uffff\1\101\1\116\1\124\1\114\1\122\1\163\1\162\1\143\1\40"+
		"\1\42\3\uffff\1\40\1\uffff\3\uffff\1\uffff\1\71\1\uffff\2\0";
	static final String DFA2_acceptS =
		"\2\uffff\1\2\16\uffff\1\1\3\uffff\1\1\4\uffff";
	static final String DFA2_specialS =
		"\15\uffff\1\2\1\3\1\5\2\uffff\1\7\1\6\1\10\2\uffff\1\1\1\4\1\0}>";
	static final String[] DFA2_transitionS = {
			"\1\1",
			"\1\3",
			"",
			"\1\4",
			"\1\5",
			"\1\6",
			"\1\7",
			"\1\10",
			"\1\11",
			"\1\12",
			"\1\13",
			"\1\14\2\uffff\1\14\23\uffff\1\14",
			"\1\14\2\uffff\1\14\23\uffff\1\14\1\uffff\1\15",
			"\12\17\1\21\2\17\1\21\24\17\1\20\71\17\1\16\uffa3\17",
			"\12\24\1\25\2\24\1\25\24\24\1\22\71\24\1\23\uffa3\24",
			"\12\17\1\21\2\17\1\21\24\17\1\20\71\17\1\16\uffa3\17",
			"\1\26\2\uffff\1\26\23\uffff\1\26",
			"",
			"\11\17\1\27\1\21\1\17\1\27\1\21\22\17\1\27\1\17\1\20\71\17\1\16\uffa3"+
			"\17",
			"\12\24\1\25\2\24\1\25\24\24\1\22\71\24\1\23\uffa3\24",
			"\12\17\1\21\2\17\1\21\24\17\1\20\71\17\1\16\uffa3\17",
			"",
			"\1\26\2\uffff\1\26\23\uffff\1\26\17\uffff\12\30",
			"\11\17\1\27\1\21\1\17\1\27\1\21\22\17\1\27\1\17\1\20\15\17\12\31\42"+
			"\17\1\16\uffa3\17",
			"\1\uffff",
			"\1\uffff"
	};

	static final short[] DFA2_eot = DFA.unpackEncodedString(DFA2_eotS);
	static final short[] DFA2_eof = DFA.unpackEncodedString(DFA2_eofS);
	static final char[] DFA2_min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
	static final char[] DFA2_max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
	static final short[] DFA2_accept = DFA.unpackEncodedString(DFA2_acceptS);
	static final short[] DFA2_special = DFA.unpackEncodedString(DFA2_specialS);
	static final short[][] DFA2_transition;

	static {
		int numStates = DFA2_transitionS.length;
		DFA2_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA2_transition[i] = DFA.unpackEncodedString(DFA2_transitionS[i]);
		}
	}

	protected class DFA2 extends DFA {

		public DFA2(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 2;
			this.eot = DFA2_eot;
			this.eof = DFA2_eof;
			this.min = DFA2_min;
			this.max = DFA2_max;
			this.accept = DFA2_accept;
			this.special = DFA2_special;
			this.transition = DFA2_transition;
		}
		@Override
		public String getDescription() {
			return "215:13: ( ( ' $ANTLR' )=> ' $ANTLR' SRC | (~ ( NLCHARS ) )* )";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA2_25 = input.LA(1);
						 
						int index2_25 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred1_ANTLRLexer()) ) {s = 21;}
						else if ( (true) ) {s = 2;}
						 
						input.seek(index2_25);
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA2_23 = input.LA(1);
						 
						int index2_23 = input.index();
						input.rewind();
						s = -1;
						if ( (LA2_23=='\"') ) {s = 16;}
						else if ( (LA2_23=='\\') ) {s = 14;}
						else if ( ((LA2_23 >= '0' && LA2_23 <= '9')) ) {s = 25;}
						else if ( (LA2_23=='\t'||LA2_23=='\f'||LA2_23==' ') ) {s = 23;}
						else if ( ((LA2_23 >= '\u0000' && LA2_23 <= '\b')||LA2_23=='\u000B'||(LA2_23 >= '\u000E' && LA2_23 <= '\u001F')||LA2_23=='!'||(LA2_23 >= '#' && LA2_23 <= '/')||(LA2_23 >= ':' && LA2_23 <= '[')||(LA2_23 >= ']' && LA2_23 <= '\uFFFF')) ) {s = 15;}
						else if ( (LA2_23=='\n'||LA2_23=='\r') && (synpred1_ANTLRLexer())) {s = 17;}
						else s = 2;
						 
						input.seek(index2_23);
						if ( s>=0 ) return s;
						break;

					case 2 : 
						int LA2_13 = input.LA(1);
						 
						int index2_13 = input.index();
						input.rewind();
						s = -1;
						if ( (LA2_13=='\\') ) {s = 14;}
						else if ( ((LA2_13 >= '\u0000' && LA2_13 <= '\t')||(LA2_13 >= '\u000B' && LA2_13 <= '\f')||(LA2_13 >= '\u000E' && LA2_13 <= '!')||(LA2_13 >= '#' && LA2_13 <= '[')||(LA2_13 >= ']' && LA2_13 <= '\uFFFF')) ) {s = 15;}
						else if ( (LA2_13=='\"') ) {s = 16;}
						else if ( (LA2_13=='\n'||LA2_13=='\r') && (synpred1_ANTLRLexer())) {s = 17;}
						else s = 2;
						 
						input.seek(index2_13);
						if ( s>=0 ) return s;
						break;

					case 3 : 
						int LA2_14 = input.LA(1);
						 
						int index2_14 = input.index();
						input.rewind();
						s = -1;
						if ( (LA2_14=='\"') ) {s = 18;}
						else if ( (LA2_14=='\\') ) {s = 19;}
						else if ( ((LA2_14 >= '\u0000' && LA2_14 <= '\t')||(LA2_14 >= '\u000B' && LA2_14 <= '\f')||(LA2_14 >= '\u000E' && LA2_14 <= '!')||(LA2_14 >= '#' && LA2_14 <= '[')||(LA2_14 >= ']' && LA2_14 <= '\uFFFF')) ) {s = 20;}
						else if ( (LA2_14=='\n'||LA2_14=='\r') && (synpred1_ANTLRLexer())) {s = 21;}
						else s = 2;
						 
						input.seek(index2_14);
						if ( s>=0 ) return s;
						break;

					case 4 : 
						int LA2_24 = input.LA(1);
						 
						int index2_24 = input.index();
						input.rewind();
						s = -1;
						if ( (synpred1_ANTLRLexer()) ) {s = 21;}
						else if ( (true) ) {s = 2;}
						 
						input.seek(index2_24);
						if ( s>=0 ) return s;
						break;

					case 5 : 
						int LA2_15 = input.LA(1);
						 
						int index2_15 = input.index();
						input.rewind();
						s = -1;
						if ( (LA2_15=='\"') ) {s = 16;}
						else if ( (LA2_15=='\\') ) {s = 14;}
						else if ( ((LA2_15 >= '\u0000' && LA2_15 <= '\t')||(LA2_15 >= '\u000B' && LA2_15 <= '\f')||(LA2_15 >= '\u000E' && LA2_15 <= '!')||(LA2_15 >= '#' && LA2_15 <= '[')||(LA2_15 >= ']' && LA2_15 <= '\uFFFF')) ) {s = 15;}
						else if ( (LA2_15=='\n'||LA2_15=='\r') && (synpred1_ANTLRLexer())) {s = 17;}
						else s = 2;
						 
						input.seek(index2_15);
						if ( s>=0 ) return s;
						break;

					case 6 : 
						int LA2_19 = input.LA(1);
						 
						int index2_19 = input.index();
						input.rewind();
						s = -1;
						if ( (LA2_19=='\"') ) {s = 18;}
						else if ( (LA2_19=='\\') ) {s = 19;}
						else if ( ((LA2_19 >= '\u0000' && LA2_19 <= '\t')||(LA2_19 >= '\u000B' && LA2_19 <= '\f')||(LA2_19 >= '\u000E' && LA2_19 <= '!')||(LA2_19 >= '#' && LA2_19 <= '[')||(LA2_19 >= ']' && LA2_19 <= '\uFFFF')) ) {s = 20;}
						else if ( (LA2_19=='\n'||LA2_19=='\r') && (synpred1_ANTLRLexer())) {s = 21;}
						else s = 2;
						 
						input.seek(index2_19);
						if ( s>=0 ) return s;
						break;

					case 7 : 
						int LA2_18 = input.LA(1);
						 
						int index2_18 = input.index();
						input.rewind();
						s = -1;
						if ( (LA2_18=='\"') ) {s = 16;}
						else if ( (LA2_18=='\\') ) {s = 14;}
						else if ( (LA2_18=='\t'||LA2_18=='\f'||LA2_18==' ') ) {s = 23;}
						else if ( ((LA2_18 >= '\u0000' && LA2_18 <= '\b')||LA2_18=='\u000B'||(LA2_18 >= '\u000E' && LA2_18 <= '\u001F')||LA2_18=='!'||(LA2_18 >= '#' && LA2_18 <= '[')||(LA2_18 >= ']' && LA2_18 <= '\uFFFF')) ) {s = 15;}
						else if ( (LA2_18=='\n'||LA2_18=='\r') && (synpred1_ANTLRLexer())) {s = 17;}
						else s = 2;
						 
						input.seek(index2_18);
						if ( s>=0 ) return s;
						break;

					case 8 : 
						int LA2_20 = input.LA(1);
						 
						int index2_20 = input.index();
						input.rewind();
						s = -1;
						if ( (LA2_20=='\"') ) {s = 16;}
						else if ( (LA2_20=='\\') ) {s = 14;}
						else if ( ((LA2_20 >= '\u0000' && LA2_20 <= '\t')||(LA2_20 >= '\u000B' && LA2_20 <= '\f')||(LA2_20 >= '\u000E' && LA2_20 <= '!')||(LA2_20 >= '#' && LA2_20 <= '[')||(LA2_20 >= ']' && LA2_20 <= '\uFFFF')) ) {s = 15;}
						else if ( (LA2_20=='\n'||LA2_20=='\r') && (synpred1_ANTLRLexer())) {s = 17;}
						else s = 2;
						 
						input.seek(index2_20);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 2, _s, input);
			error(nvae);
			throw nvae;
		}
	}

	static final String DFA34_eotS =
		"\2\uffff\1\46\1\uffff\12\54\1\76\4\uffff\1\46\2\uffff\1\107\2\uffff\1"+
		"\113\2\uffff\1\117\16\uffff\1\54\1\uffff\20\54\33\uffff\43\54\1\u008e"+
		"\2\54\1\uffff\3\54\1\u0094\3\54\1\u0098\7\54\1\uffff\3\54\1\u00a3\1\54"+
		"\1\uffff\1\u00a5\2\54\1\uffff\1\u00a8\1\u00a9\2\54\1\u00ac\3\54\1\uffff"+
		"\1\54\1\uffff\1\54\1\uffff\1\54\1\u00b3\2\uffff\1\54\1\u00b5\1\uffff\1"+
		"\u00b6\1\u00b7\1\uffff\2\54\1\u00ba\1\uffff\1\54\3\uffff\1\54\2\uffff"+
		"\1\u00bd\1\54\1\uffff\1\176";
	static final String DFA34_eofS =
		"\u00bf\uffff";
	static final String DFA34_minS =
		"\1\0\1\uffff\1\0\1\uffff\1\160\1\150\1\141\1\155\1\151\1\145\1\141\1\162"+
		"\1\145\1\157\1\72\4\uffff\1\76\2\uffff\1\76\2\uffff\1\75\2\uffff\1\56"+
		"\16\uffff\1\164\1\uffff\1\153\1\145\1\162\1\141\1\164\1\160\1\141\1\156"+
		"\1\170\1\143\1\162\1\151\1\142\1\141\1\164\1\144\33\uffff\1\151\2\145"+
		"\1\157\1\156\1\143\1\157\1\147\1\141\1\145\1\141\1\163\1\164\1\166\1\154"+
		"\1\155\1\165\1\145\1\157\1\156\1\11\1\167\1\156\1\150\1\162\1\155\1\154"+
		"\1\162\1\154\2\145\1\141\1\151\1\155\1\162\1\60\1\156\1\163\1\uffff\1"+
		"\162\1\163\1\145\1\60\1\164\1\145\1\154\1\60\1\163\1\162\1\143\1\164\1"+
		"\143\1\141\1\156\1\uffff\1\163\1\11\1\141\1\60\1\154\1\uffff\1\60\1\156"+
		"\1\171\1\uffff\2\60\1\164\1\145\1\60\1\162\1\163\1\11\1\uffff\1\155\1"+
		"\uffff\1\163\1\uffff\1\164\1\60\2\uffff\1\145\1\60\1\uffff\2\60\1\uffff"+
		"\1\155\1\11\1\60\1\uffff\1\144\3\uffff\1\141\2\uffff\1\60\1\162\1\uffff"+
		"\1\60";
	static final String DFA34_maxS =
		"\1\uffff\1\uffff\1\uffff\1\uffff\1\160\1\162\1\150\1\155\1\162\1\157\1"+
		"\165\1\162\1\145\1\157\1\72\4\uffff\1\76\2\uffff\1\76\2\uffff\1\75\2\uffff"+
		"\1\56\16\uffff\1\164\1\uffff\1\153\1\145\1\162\1\141\1\164\1\160\1\141"+
		"\1\156\1\170\1\143\1\162\1\157\1\142\1\141\1\164\1\144\33\uffff\1\151"+
		"\2\145\1\157\1\156\1\143\1\157\1\147\1\141\1\145\1\141\1\163\1\164\1\166"+
		"\1\154\1\155\1\165\1\145\1\157\1\156\1\147\1\167\1\156\1\150\1\162\1\155"+
		"\1\154\1\162\1\154\2\145\1\141\1\151\1\155\1\162\1\ufffd\1\156\1\163\1"+
		"\uffff\1\162\1\163\1\145\1\ufffd\1\164\1\145\1\154\1\ufffd\1\163\1\162"+
		"\1\143\1\164\1\143\1\141\1\156\1\uffff\1\163\1\173\1\141\1\ufffd\1\154"+
		"\1\uffff\1\ufffd\1\156\1\171\1\uffff\2\ufffd\1\164\1\145\1\ufffd\1\162"+
		"\1\163\1\173\1\uffff\1\155\1\uffff\1\163\1\uffff\1\164\1\ufffd\2\uffff"+
		"\1\145\1\ufffd\1\uffff\2\ufffd\1\uffff\1\155\1\173\1\ufffd\1\uffff\1\144"+
		"\3\uffff\1\141\2\uffff\1\ufffd\1\162\1\uffff\1\ufffd";
	static final String DFA34_acceptS =
		"\1\uffff\1\1\1\uffff\1\3\13\uffff\1\30\1\31\1\32\1\33\1\uffff\1\35\1\36"+
		"\1\uffff\1\40\1\42\1\uffff\1\45\1\46\1\uffff\1\51\1\52\1\53\1\54\1\55"+
		"\1\56\1\57\1\60\1\61\1\62\1\1\2\2\1\3\1\uffff\1\55\20\uffff\1\27\1\26"+
		"\1\30\1\31\1\32\1\33\1\34\1\35\1\36\1\41\1\37\1\40\1\42\1\44\1\43\1\45"+
		"\1\46\1\50\1\47\1\51\1\52\1\53\1\54\1\56\1\57\1\60\1\61\46\uffff\1\14"+
		"\17\uffff\1\25\5\uffff\1\23\3\uffff\1\11\10\uffff\1\5\1\uffff\1\22\1\uffff"+
		"\1\7\2\uffff\1\21\1\12\2\uffff\1\16\2\uffff\1\4\3\uffff\1\24\1\uffff\1"+
		"\17\1\13\1\20\1\uffff\1\6\1\10\2\uffff\1\15\1\uffff";
	static final String DFA34_specialS =
		"\1\0\1\uffff\1\1\u00bc\uffff}>";
	static final String[] DFA34_transitionS = {
			"\11\46\2\44\1\46\2\44\22\46\1\44\2\46\1\36\1\33\2\46\1\43\1\21\1\22\1"+
			"\30\1\31\1\17\1\23\1\34\1\1\12\42\1\16\1\20\1\24\1\26\1\25\1\27\1\35"+
			"\32\41\1\2\5\46\2\41\1\6\2\41\1\10\1\13\1\41\1\7\2\41\1\11\1\15\1\41"+
			"\1\4\1\12\1\41\1\14\1\41\1\5\6\41\1\3\1\32\1\40\1\37\101\46\27\41\1\46"+
			"\37\41\1\46\u0208\41\160\46\16\41\1\46\u1c81\41\14\46\2\41\142\46\u0120"+
			"\41\u0a70\46\u03f0\41\21\46\ua7ff\41\u2100\46\u04d0\41\40\46\u010f\41"+
			"\1\45\u00fe\41\2\46",
			"",
			"\12\50\1\51\2\50\1\51\ufff2\50",
			"",
			"\1\53",
			"\1\57\6\uffff\1\55\2\uffff\1\56",
			"\1\61\6\uffff\1\60",
			"\1\62",
			"\1\64\10\uffff\1\63",
			"\1\65\11\uffff\1\66",
			"\1\67\20\uffff\1\70\2\uffff\1\71",
			"\1\72",
			"\1\73",
			"\1\74",
			"\1\75",
			"",
			"",
			"",
			"",
			"\1\103",
			"",
			"",
			"\1\106",
			"",
			"",
			"\1\112",
			"",
			"",
			"\1\116",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\130",
			"",
			"\1\131",
			"\1\132",
			"\1\133",
			"\1\134",
			"\1\135",
			"\1\136",
			"\1\137",
			"\1\140",
			"\1\141",
			"\1\142",
			"\1\143",
			"\1\145\5\uffff\1\144",
			"\1\146",
			"\1\147",
			"\1\150",
			"\1\151",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"\1\152",
			"\1\153",
			"\1\154",
			"\1\155",
			"\1\156",
			"\1\157",
			"\1\160",
			"\1\161",
			"\1\162",
			"\1\163",
			"\1\164",
			"\1\165",
			"\1\166",
			"\1\167",
			"\1\170",
			"\1\171",
			"\1\172",
			"\1\173",
			"\1\174",
			"\1\175",
			"\2\176\1\uffff\2\176\22\uffff\1\176\106\uffff\1\177",
			"\1\u0080",
			"\1\u0081",
			"\1\u0082",
			"\1\u0083",
			"\1\u0084",
			"\1\u0085",
			"\1\u0086",
			"\1\u0087",
			"\1\u0088",
			"\1\u0089",
			"\1\u008a",
			"\1\u008b",
			"\1\u008c",
			"\1\u008d",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u008f",
			"\1\u0090",
			"",
			"\1\u0091",
			"\1\u0092",
			"\1\u0093",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u0095",
			"\1\u0096",
			"\1\u0097",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u0099",
			"\1\u009a",
			"\1\u009b",
			"\1\u009c",
			"\1\u009d",
			"\1\u009e",
			"\1\u009f",
			"",
			"\1\u00a0",
			"\2\u00a1\1\uffff\2\u00a1\22\uffff\1\u00a1\132\uffff\1\u00a1",
			"\1\u00a2",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u00a4",
			"",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u00a6",
			"\1\u00a7",
			"",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u00aa",
			"\1\u00ab",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u00ad",
			"\1\u00ae",
			"\2\u00af\1\uffff\2\u00af\22\uffff\1\u00af\132\uffff\1\u00af",
			"",
			"\1\u00b0",
			"",
			"\1\u00b1",
			"",
			"\1\u00b2",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"",
			"",
			"\1\u00b4",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"",
			"\1\u00b8",
			"\2\u00b9\1\uffff\2\u00b9\22\uffff\1\u00b9\132\uffff\1\u00b9",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"",
			"\1\u00bb",
			"",
			"",
			"",
			"\1\u00bc",
			"",
			"",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54",
			"\1\u00be",
			"",
			"\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\74\uffff\1\54\10\uffff"+
			"\27\54\1\uffff\37\54\1\uffff\u0286\54\1\uffff\u1c81\54\14\uffff\2\54"+
			"\61\uffff\2\54\57\uffff\u0120\54\u0a70\uffff\u03f0\54\21\uffff\ua7ff"+
			"\54\u2100\uffff\u04d0\54\40\uffff\u010f\54\1\uffff\u00fe\54"
	};

	static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
	static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
	static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
	static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
	static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
	static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
	static final short[][] DFA34_transition;

	static {
		int numStates = DFA34_transitionS.length;
		DFA34_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
		}
	}

	protected class DFA34 extends DFA {

		public DFA34(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 34;
			this.eot = DFA34_eot;
			this.eof = DFA34_eof;
			this.min = DFA34_min;
			this.max = DFA34_max;
			this.accept = DFA34_accept;
			this.special = DFA34_special;
			this.transition = DFA34_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( COMMENT | ARG_OR_CHARSET | ACTION | OPTIONS | TOKENS_SPEC | CHANNELS | IMPORT | FRAGMENT | LEXER | PARSER | GRAMMAR | TREE_GRAMMAR | PROTECTED | PUBLIC | PRIVATE | RETURNS | LOCALS | THROWS | CATCH | FINALLY | MODE | COLON | COLONCOLON | COMMA | SEMI | LPAREN | RPAREN | RARROW | LT | GT | ASSIGN | QUESTION | SYNPRED | STAR | PLUS | PLUS_ASSIGN | OR | DOLLAR | DOT | RANGE | AT | POUND | NOT | RBRACE | ID | INT | STRING_LITERAL | WS | UnicodeBOM | ERRCHAR );";
		}
		@Override
		public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch ( s ) {
					case 0 : 
						int LA34_0 = input.LA(1);
						s = -1;
						if ( (LA34_0=='/') ) {s = 1;}
						else if ( (LA34_0=='[') ) {s = 2;}
						else if ( (LA34_0=='{') ) {s = 3;}
						else if ( (LA34_0=='o') ) {s = 4;}
						else if ( (LA34_0=='t') ) {s = 5;}
						else if ( (LA34_0=='c') ) {s = 6;}
						else if ( (LA34_0=='i') ) {s = 7;}
						else if ( (LA34_0=='f') ) {s = 8;}
						else if ( (LA34_0=='l') ) {s = 9;}
						else if ( (LA34_0=='p') ) {s = 10;}
						else if ( (LA34_0=='g') ) {s = 11;}
						else if ( (LA34_0=='r') ) {s = 12;}
						else if ( (LA34_0=='m') ) {s = 13;}
						else if ( (LA34_0==':') ) {s = 14;}
						else if ( (LA34_0==',') ) {s = 15;}
						else if ( (LA34_0==';') ) {s = 16;}
						else if ( (LA34_0=='(') ) {s = 17;}
						else if ( (LA34_0==')') ) {s = 18;}
						else if ( (LA34_0=='-') ) {s = 19;}
						else if ( (LA34_0=='<') ) {s = 20;}
						else if ( (LA34_0=='>') ) {s = 21;}
						else if ( (LA34_0=='=') ) {s = 22;}
						else if ( (LA34_0=='?') ) {s = 23;}
						else if ( (LA34_0=='*') ) {s = 24;}
						else if ( (LA34_0=='+') ) {s = 25;}
						else if ( (LA34_0=='|') ) {s = 26;}
						else if ( (LA34_0=='$') ) {s = 27;}
						else if ( (LA34_0=='.') ) {s = 28;}
						else if ( (LA34_0=='@') ) {s = 29;}
						else if ( (LA34_0=='#') ) {s = 30;}
						else if ( (LA34_0=='~') ) {s = 31;}
						else if ( (LA34_0=='}') ) {s = 32;}
						else if ( ((LA34_0 >= 'A' && LA34_0 <= 'Z')||(LA34_0 >= 'a' && LA34_0 <= 'b')||(LA34_0 >= 'd' && LA34_0 <= 'e')||LA34_0=='h'||(LA34_0 >= 'j' && LA34_0 <= 'k')||LA34_0=='n'||LA34_0=='q'||LA34_0=='s'||(LA34_0 >= 'u' && LA34_0 <= 'z')||(LA34_0 >= '\u00C0' && LA34_0 <= '\u00D6')||(LA34_0 >= '\u00D8' && LA34_0 <= '\u00F6')||(LA34_0 >= '\u00F8' && LA34_0 <= '\u02FF')||(LA34_0 >= '\u0370' && LA34_0 <= '\u037D')||(LA34_0 >= '\u037F' && LA34_0 <= '\u1FFF')||(LA34_0 >= '\u200C' && LA34_0 <= '\u200D')||(LA34_0 >= '\u2070' && LA34_0 <= '\u218F')||(LA34_0 >= '\u2C00' && LA34_0 <= '\u2FEF')||(LA34_0 >= '\u3001' && LA34_0 <= '\uD7FF')||(LA34_0 >= '\uF900' && LA34_0 <= '\uFDCF')||(LA34_0 >= '\uFDF0' && LA34_0 <= '\uFEFE')||(LA34_0 >= '\uFF00' && LA34_0 <= '\uFFFD')) ) {s = 33;}
						else if ( ((LA34_0 >= '0' && LA34_0 <= '9')) ) {s = 34;}
						else if ( (LA34_0=='\'') ) {s = 35;}
						else if ( ((LA34_0 >= '\t' && LA34_0 <= '\n')||(LA34_0 >= '\f' && LA34_0 <= '\r')||LA34_0==' ') ) {s = 36;}
						else if ( (LA34_0=='\uFEFF') ) {s = 37;}
						else if ( ((LA34_0 >= '\u0000' && LA34_0 <= '\b')||LA34_0=='\u000B'||(LA34_0 >= '\u000E' && LA34_0 <= '\u001F')||(LA34_0 >= '!' && LA34_0 <= '\"')||(LA34_0 >= '%' && LA34_0 <= '&')||(LA34_0 >= '\\' && LA34_0 <= '`')||(LA34_0 >= '\u007F' && LA34_0 <= '\u00BF')||LA34_0=='\u00D7'||LA34_0=='\u00F7'||(LA34_0 >= '\u0300' && LA34_0 <= '\u036F')||LA34_0=='\u037E'||(LA34_0 >= '\u2000' && LA34_0 <= '\u200B')||(LA34_0 >= '\u200E' && LA34_0 <= '\u206F')||(LA34_0 >= '\u2190' && LA34_0 <= '\u2BFF')||(LA34_0 >= '\u2FF0' && LA34_0 <= '\u3000')||(LA34_0 >= '\uD800' && LA34_0 <= '\uF8FF')||(LA34_0 >= '\uFDD0' && LA34_0 <= '\uFDEF')||(LA34_0 >= '\uFFFE' && LA34_0 <= '\uFFFF')) ) {s = 38;}
						if ( s>=0 ) return s;
						break;

					case 1 : 
						int LA34_2 = input.LA(1);
						 
						int index34_2 = input.index();
						input.rewind();
						s = -1;
						if ( ((LA34_2 >= '\u0000' && LA34_2 <= '\t')||(LA34_2 >= '\u000B' && LA34_2 <= '\f')||(LA34_2 >= '\u000E' && LA34_2 <= '\uFFFF')) && (((!isLexerRule)||(isLexerRule)))) {s = 40;}
						else if ( (LA34_2=='\n'||LA34_2=='\r') && ((!isLexerRule))) {s = 41;}
						else s = 38;
						 
						input.seek(index34_2);
						if ( s>=0 ) return s;
						break;
			}
			if (state.backtracking>0) {state.failed=true; return -1;}
			NoViableAltException nvae =
				new NoViableAltException(getDescription(), 34, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}
