/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.automata;

import org.antlr.runtime.CommonToken;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.NotSetTransition;
import org.antlr.v4.runtime.atn.RangeTransition;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.runtime.atn.TokensStartState;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.List;
import java.util.Set;

public class LexerATNFactory extends ParserATNFactory {
	public STGroup codegenTemplates;
	public LexerATNFactory(LexerGrammar g) {
		super(g);
		// use codegen to get correct language templates for lexer commands
		String language = g.getOptionString("language");
		CodeGenerator gen = new CodeGenerator(g.tool, null, language);
		codegenTemplates = gen.getTemplates();
	}

	@Override
	public ATN createATN() {
		// BUILD ALL START STATES (ONE PER MODE)
		Set<String> modes = ((LexerGrammar) g).modes.keySet();
		for (String modeName : modes) {
			// create s0, start state; implied Tokens rule node
			TokensStartState startState =
				newState(TokensStartState.class, null);
			atn.modeNameToStartState.put(modeName, startState);
			atn.modeToStartState.add(startState);
			atn.defineDecisionState(startState);
		}

		// INIT ACTION, RULE->TOKEN_TYPE MAP
		atn.ruleToTokenType = new int[g.rules.size()];
		atn.ruleToActionIndex = new int[g.rules.size()];
		for (Rule r : g.rules.values()) {
			atn.ruleToTokenType[r.index] = g.getTokenType(r.name);
			atn.ruleToActionIndex[r.index] = r.actionIndex;
		}

		// CREATE ATN FOR EACH RULE
		_createATN(g.rules.values());

		// LINK MODE START STATE TO EACH TOKEN RULE
		for (String modeName : modes) {
			List<Rule> rules = ((LexerGrammar)g).modes.get(modeName);
			TokensStartState startState = atn.modeNameToStartState.get(modeName);
			for (Rule r : rules) {
				if ( !r.isFragment() ) {
					RuleStartState s = atn.ruleToStartState[r.index];
					epsilon(startState, s);
				}
			}
		}

		ATNOptimizer.optimize(g, atn);
		return atn;
	}

	@Override
	public Handle action(ActionAST action) {
		ATNState left = newState(action);
		ATNState right = newState(action);
		boolean isCtxDependent = false;
		int actionIndex = g.lexerActions.get(action);
		ActionTransition a =
			new ActionTransition(right, currentRule.index, actionIndex, isCtxDependent);
		left.addTransition(a);
		action.atnState = left;
		Handle h = new Handle(left, right);
		return h;
	}

	@Override
	public Handle action(String action) {
        // define action AST for this rule as if we had found in grammar
        ActionAST ast =	new ActionAST(new CommonToken(ANTLRParser.ACTION, action));
		currentRule.defineActionInAlt(currentOuterAlt, ast);
		return action(ast);
	}

	@Override
	public Handle lexerAltCommands(Handle alt, Handle cmds) {
		Handle h = new Handle(alt.left, cmds.right);
		epsilon(alt.right, cmds.left);
		return h;
	}

	@Override
	public String lexerCallCommand(GrammarAST ID, GrammarAST arg) {
		ST cmdST = codegenTemplates.getInstanceOf("Lexer" +
												  CharSupport.capitalize(ID.getText())+
												  "Command");
		cmdST.add("arg", arg.getText());
		return cmdST.render();
	}

	@Override
	public String lexerCommand(GrammarAST ID) {
		ST cmdST = codegenTemplates.getInstanceOf("Lexer" +
												  CharSupport.capitalize(ID.getText())+
												  "Command");
		return cmdST.render();
	}

	@Override
	public Handle range(GrammarAST a, GrammarAST b) {
		ATNState left = newState(a);
		ATNState right = newState(b);
		int t1 = CharSupport.getCharValueFromGrammarCharLiteral(a.getText());
		int t2 = CharSupport.getCharValueFromGrammarCharLiteral(b.getText());
		left.addTransition(new  RangeTransition(right, t1, t2));
		a.atnState = left;
		b.atnState = left;
		return new Handle(left, right);
	}

	@Override
	public Handle set(GrammarAST associatedAST, List<GrammarAST> alts, boolean invert) {
		ATNState left = newState(associatedAST);
		ATNState right = newState(associatedAST);
		IntervalSet set = new IntervalSet();
		for (GrammarAST t : alts) {
			if ( t.getType()==ANTLRParser.RANGE ) {
				int a = CharSupport.getCharValueFromGrammarCharLiteral(t.getChild(0).getText());
				int b = CharSupport.getCharValueFromGrammarCharLiteral(t.getChild(1).getText());
				set.add(a, b);
			}
			else if ( t.getType()==ANTLRParser.LEXER_CHAR_SET ) {
				set.addAll(getSetFromCharSetLiteral(t));
			}
			else if ( t.getType()==ANTLRParser.STRING_LITERAL ) {
				int c = CharSupport.getCharValueFromGrammarCharLiteral(t.getText());
				if ( c != -1 ) {
					set.add(c);
				}
				else {
					g.tool.errMgr.grammarError(ErrorType.INVALID_LITERAL_IN_LEXER_SET,
											   g.fileName, t.getToken(), t.getText());

				}
			}
			else if ( t.getType()==ANTLRParser.TOKEN_REF ) {
				g.tool.errMgr.grammarError(ErrorType.UNSUPPORTED_REFERENCE_IN_LEXER_SET,
										   g.fileName, t.getToken(), t.getText());
			}
		}
		if ( invert ) {
			left.addTransition(new NotSetTransition(right, set));
		}
		else {
			Transition transition;
			if (set.getIntervals().size() == 1) {
				Interval interval = set.getIntervals().get(0);
				transition = new RangeTransition(right, interval.a, interval.b);
			} else {
				transition = new SetTransition(right, set);
			}

			left.addTransition(transition);
		}
		associatedAST.atnState = left;
		return new Handle(left, right);
	}

	/** For a lexer, a string is a sequence of char to match.  That is,
	 *  "fog" is treated as 'f' 'o' 'g' not as a single transition in
	 *  the DFA.  Machine== o-'f'->o-'o'->o-'g'->o and has n+1 states
	 *  for n characters.
	 */
	@Override
	public Handle stringLiteral(TerminalAST stringLiteralAST) {
		String chars = stringLiteralAST.getText();
		chars = CharSupport.getStringFromGrammarStringLiteral(chars);
		int n = chars.length();
		ATNState left = newState(stringLiteralAST);
		ATNState prev = left;
		ATNState right = null;
		for (int i=0; i<n; i++) {
			right = newState(stringLiteralAST);
			prev.addTransition(new AtomTransition(right, chars.charAt(i)));
			prev = right;
		}
		stringLiteralAST.atnState = left;
		return new Handle(left, right);
	}

	/** [Aa\t \u1234a-z\]\-] char sets */
	@Override
	public Handle charSetLiteral(GrammarAST charSetAST) {
		ATNState left = newState(charSetAST);
		ATNState right = newState(charSetAST);
		IntervalSet set = getSetFromCharSetLiteral(charSetAST);
		left.addTransition(new SetTransition(right, set));
		charSetAST.atnState = left;
		return new Handle(left, right);
	}

	public IntervalSet getSetFromCharSetLiteral(GrammarAST charSetAST) {
		String chars = charSetAST.getText();
		chars = chars.substring(1, chars.length()-1);
		String cset = '"'+ chars +'"';
		IntervalSet set = new IntervalSet();

		// unescape all valid escape char like \n, leaving escaped dashes as '\-'
		// so we can avoid seeing them as '-' range ops.
		chars = CharSupport.getStringFromGrammarStringLiteral(cset);
		// now make x-y become set of char
		int n = chars.length();
		for (int i=0; i< n; i++) {
			int c = chars.charAt(i);
			if ( c=='\\' && (i+1)<n && chars.charAt(i+1)=='-' ) { // \-
				set.add('-');
				i++;
			}
			else if ( (i+2)<n && chars.charAt(i+1)=='-' ) { // range x-y
				int x = c;
				int y = chars.charAt(i+2);
				if ( x<=y ) set.add(x,y);
				i+=2;
			}
			else {
				set.add(c);
			}
		}
		return set;
	}

	@Override
	public Handle tokenRef(TerminalAST node) {
		// Ref to EOF in lexer yields char transition on -1
		if ( node.getText().equals("EOF") ) {
			ATNState left = newState(node);
			ATNState right = newState(node);
			left.addTransition(new AtomTransition(right, IntStream.EOF));
			return new Handle(left, right);
		}
		return _ruleRef(node);
	}
}
