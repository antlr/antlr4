/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.automata;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.misc.EscapeSequenceParsing;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.CodePointTransitions;
import org.antlr.v4.runtime.atn.LexerAction;
import org.antlr.v4.runtime.atn.LexerChannelAction;
import org.antlr.v4.runtime.atn.LexerCustomAction;
import org.antlr.v4.runtime.atn.LexerModeAction;
import org.antlr.v4.runtime.atn.LexerMoreAction;
import org.antlr.v4.runtime.atn.LexerPopModeAction;
import org.antlr.v4.runtime.atn.LexerPushModeAction;
import org.antlr.v4.runtime.atn.LexerSkipAction;
import org.antlr.v4.runtime.atn.LexerTypeAction;
import org.antlr.v4.runtime.atn.NotSetTransition;
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
import org.antlr.v4.tool.ast.RangeAST;
import org.antlr.v4.tool.ast.TerminalAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LexerATNFactory extends ParserATNFactory {
	public STGroup codegenTemplates;

	/**
	 * Provides a map of names of predefined constants which are likely to
	 * appear as the argument for lexer commands. These names would be resolved
	 * by the Java compiler for lexer commands that are translated to embedded
	 * actions, but are required during code generation for creating
	 * {@link LexerAction} instances that are usable by a lexer interpreter.
	 */
	public static final Map<String, Integer> COMMON_CONSTANTS = new HashMap<String, Integer>();
	static {
		COMMON_CONSTANTS.put("HIDDEN", Lexer.HIDDEN);
		COMMON_CONSTANTS.put("DEFAULT_TOKEN_CHANNEL", Lexer.DEFAULT_TOKEN_CHANNEL);
		COMMON_CONSTANTS.put("DEFAULT_MODE", Lexer.DEFAULT_MODE);
		COMMON_CONSTANTS.put("SKIP", Lexer.SKIP);
		COMMON_CONSTANTS.put("MORE", Lexer.MORE);
		COMMON_CONSTANTS.put("EOF", Lexer.EOF);
		COMMON_CONSTANTS.put("MAX_CHAR_VALUE", Lexer.MAX_CHAR_VALUE);
		COMMON_CONSTANTS.put("MIN_CHAR_VALUE", Lexer.MIN_CHAR_VALUE);
	}

	private List<String> ruleCommands = new ArrayList<String>();

	/**
	 * Maps from an action index to a {@link LexerAction} object.
	 */
	protected Map<Integer, LexerAction> indexToActionMap = new HashMap<Integer, LexerAction>();
	/**
	 * Maps from a {@link LexerAction} object to the action index.
	 */
	protected Map<LexerAction, Integer> actionToIndexMap = new HashMap<LexerAction, Integer>();

	public LexerATNFactory(LexerGrammar g) {
		super(g);
		// use codegen to get correct language templates for lexer commands
		String language = g.getOptionString("language");
		CodeGenerator gen = new CodeGenerator(g.tool, null, language);
		codegenTemplates = gen.getTemplates();
	}

	public static Set<String> getCommonConstants() {
		return COMMON_CONSTANTS.keySet();
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
		for (Rule r : g.rules.values()) {
			atn.ruleToTokenType[r.index] = g.getTokenType(r.name);
		}

		// CREATE ATN FOR EACH RULE
		_createATN(g.rules.values());

		atn.lexerActions = new LexerAction[indexToActionMap.size()];
		for (Map.Entry<Integer, LexerAction> entry : indexToActionMap.entrySet()) {
			atn.lexerActions[entry.getKey()] = entry.getValue();
		}

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
	public Handle rule(GrammarAST ruleAST, String name, Handle blk) {
		ruleCommands.clear();
		return super.rule(ruleAST, name, blk);
	}

	@Override
	public Handle action(ActionAST action) {
		int ruleIndex = currentRule.index;
		int actionIndex = g.lexerActions.get(action);
		LexerCustomAction lexerAction = new LexerCustomAction(ruleIndex, actionIndex);
		return action(action, lexerAction);
	}

	protected int getLexerActionIndex(LexerAction lexerAction) {
		Integer lexerActionIndex = actionToIndexMap.get(lexerAction);
		if (lexerActionIndex == null) {
			lexerActionIndex = actionToIndexMap.size();
			actionToIndexMap.put(lexerAction, lexerActionIndex);
			indexToActionMap.put(lexerActionIndex, lexerAction);
		}

		return lexerActionIndex;
	}

	@Override
	public Handle action(String action) {
		if (action.trim().isEmpty()) {
			ATNState left = newState(null);
			ATNState right = newState(null);
			epsilon(left, right);
			return new Handle(left, right);
		}

		// define action AST for this rule as if we had found in grammar
        ActionAST ast =	new ActionAST(new CommonToken(ANTLRParser.ACTION, action));
		currentRule.defineActionInAlt(currentOuterAlt, ast);
		return action(ast);
	}

	protected Handle action(GrammarAST node, LexerAction lexerAction) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		boolean isCtxDependent = false;
		int lexerActionIndex = getLexerActionIndex(lexerAction);
		ActionTransition a =
			new ActionTransition(right, currentRule.index, lexerActionIndex, isCtxDependent);
		left.addTransition(a);
		node.atnState = left;
		Handle h = new Handle(left, right);
		return h;
	}

	@Override
	public Handle lexerAltCommands(Handle alt, Handle cmds) {
		Handle h = new Handle(alt.left, cmds.right);
		epsilon(alt.right, cmds.left);
		return h;
	}

	@Override
	public Handle lexerCallCommand(GrammarAST ID, GrammarAST arg) {
		LexerAction lexerAction = createLexerAction(ID, arg);
		if (lexerAction != null) {
			return action(ID, lexerAction);
		}

		// fall back to standard action generation for the command
		ST cmdST = codegenTemplates.getInstanceOf("Lexer" +
												  CharSupport.capitalize(ID.getText())+
												  "Command");
		if (cmdST == null) {
			g.tool.errMgr.grammarError(ErrorType.INVALID_LEXER_COMMAND, g.fileName, ID.token, ID.getText());
			return epsilon(ID);
		}

		if (cmdST.impl.formalArguments == null || !cmdST.impl.formalArguments.containsKey("arg")) {
			g.tool.errMgr.grammarError(ErrorType.UNWANTED_LEXER_COMMAND_ARGUMENT, g.fileName, ID.token, ID.getText());
			return epsilon(ID);
		}

		cmdST.add("arg", arg.getText());
		cmdST.add("grammar", arg.g);
		return action(cmdST.render());
	}

	@Override
	public Handle lexerCommand(GrammarAST ID) {
		LexerAction lexerAction = createLexerAction(ID, null);
		if (lexerAction != null) {
			return action(ID, lexerAction);
		}

		// fall back to standard action generation for the command
		ST cmdST = codegenTemplates.getInstanceOf("Lexer" +
				CharSupport.capitalize(ID.getText()) +
				"Command");
		if (cmdST == null) {
			g.tool.errMgr.grammarError(ErrorType.INVALID_LEXER_COMMAND, g.fileName, ID.token, ID.getText());
			return epsilon(ID);
		}

		if (cmdST.impl.formalArguments != null && cmdST.impl.formalArguments.containsKey("arg")) {
			g.tool.errMgr.grammarError(ErrorType.MISSING_LEXER_COMMAND_ARGUMENT, g.fileName, ID.token, ID.getText());
			return epsilon(ID);
		}

		return action(cmdST.render());
	}

	@Override
	public Handle range(GrammarAST a, GrammarAST b) {
		ATNState left = newState(a);
		ATNState right = newState(b);
		int t1 = CharSupport.getCharValueFromGrammarCharLiteral(a.getText());
		int t2 = CharSupport.getCharValueFromGrammarCharLiteral(b.getText());
		checkRange(a, b, t1, t2);
		left.addTransition(CodePointTransitions.createWithCodePointRange(right, t1, t2));
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
				if (checkRange((GrammarAST) t.getChild(0), (GrammarAST) t.getChild(1), a, b)) {
					checkSetCollision(associatedAST, set, a, b);
					set.add(a,b);
				}
			}
			else if ( t.getType()==ANTLRParser.LEXER_CHAR_SET ) {
				set.addAll(getSetFromCharSetLiteral(t));
			}
			else if ( t.getType()==ANTLRParser.STRING_LITERAL ) {
				int c = CharSupport.getCharValueFromGrammarCharLiteral(t.getText());
				if ( c != -1 ) {
					checkSetCollision(associatedAST, set, c);
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
				transition = CodePointTransitions.createWithCodePointRange(right, interval.a, interval.b);
			}
			else {
				transition = new SetTransition(right, set);
			}

			left.addTransition(transition);
		}
		associatedAST.atnState = left;
		return new Handle(left, right);
	}

	protected boolean checkRange(GrammarAST leftNode, GrammarAST rightNode, int leftValue, int rightValue) {
		boolean result = true;
		if (leftValue == -1) {
			result = false;
			g.tool.errMgr.grammarError(ErrorType.INVALID_LITERAL_IN_LEXER_SET,
					g.fileName, leftNode.getToken(), leftNode.getText());
		}
		if (rightValue == -1) {
			result = false;
			g.tool.errMgr.grammarError(ErrorType.INVALID_LITERAL_IN_LEXER_SET,
					g.fileName, rightNode.getToken(), rightNode.getText());
		}
		if (!result) return result;

		if (rightValue < leftValue) {
			g.tool.errMgr.grammarError(ErrorType.EMPTY_STRINGS_AND_SETS_NOT_ALLOWED,
					g.fileName, leftNode.parent.getToken(), leftNode.getText() + ".." + rightNode.getText());
		}
		return result;
	}

	/** For a lexer, a string is a sequence of char to match.  That is,
	 *  "fog" is treated as 'f' 'o' 'g' not as a single transition in
	 *  the DFA.  Machine== o-'f'-&gt;o-'o'-&gt;o-'g'-&gt;o and has n+1 states
	 *  for n characters.
	 */
	@Override
	public Handle stringLiteral(TerminalAST stringLiteralAST) {
		String chars = stringLiteralAST.getText();
		ATNState left = newState(stringLiteralAST);
		ATNState right;
		String s = CharSupport.getStringFromGrammarStringLiteral(chars);
		if (s == null) {
			// the lexer will already have given an error
			return new Handle(left, left);
		}

		int n = s.length();
		ATNState prev = left;
		right = null;
		for (int i = 0; i < n; ) {
			right = newState(stringLiteralAST);
			int codePoint = s.codePointAt(i);
			prev.addTransition(CodePointTransitions.createWithCodePoint(right, codePoint));
			prev = right;
			i += Character.charCount(codePoint);
		}
		stringLiteralAST.atnState = left;
		return new Handle(left, right);
	}

	/** [Aa\t \u1234a-z\]\p{Letter}\-] char sets */
	@Override
	public Handle charSetLiteral(GrammarAST charSetAST) {
		ATNState left = newState(charSetAST);
		ATNState right = newState(charSetAST);
		IntervalSet set = getSetFromCharSetLiteral(charSetAST);
		left.addTransition(new SetTransition(right, set));
		charSetAST.atnState = left;
		return new Handle(left, right);
	}

	private static class CharSetParseState {
		enum Mode {
			NONE,
			ERROR,
			PREV_CODE_POINT,
			PREV_PROPERTY
		}

		public static final CharSetParseState NONE = new CharSetParseState(Mode.NONE, false, -1, IntervalSet.EMPTY_SET);
		public static final CharSetParseState ERROR = new CharSetParseState(Mode.ERROR, false, -1, IntervalSet.EMPTY_SET);

		public final Mode mode;
		public final boolean inRange;
		public final int prevCodePoint;
		public final IntervalSet prevProperty;

		public CharSetParseState(
				Mode mode,
				boolean inRange,
				int prevCodePoint,
				IntervalSet prevProperty) {
			this.mode = mode;
			this.inRange = inRange;
			this.prevCodePoint = prevCodePoint;
			this.prevProperty = prevProperty;
		}

		@Override
		public String toString() {
			return String.format(
					"%s mode=%s inRange=%s prevCodePoint=%d prevProperty=%s",
					super.toString(),
					mode,
					inRange,
					prevCodePoint,
					prevProperty);
		}

		@Override
		public boolean equals(Object other) {
			if (!(other instanceof CharSetParseState)) {
				return false;
			}
			CharSetParseState that = (CharSetParseState) other;
			if (this == that) {
				return true;
			}
			return Objects.equals(this.mode, that.mode) &&
				Objects.equals(this.inRange, that.inRange) &&
				Objects.equals(this.prevCodePoint, that.prevCodePoint) &&
				Objects.equals(this.prevProperty, that.prevProperty);
		}

		@Override
		public int hashCode() {
			return Objects.hash(mode, inRange, prevCodePoint, prevProperty);
		}
	}

	public IntervalSet getSetFromCharSetLiteral(GrammarAST charSetAST) {
		String chars = charSetAST.getText();
		chars = chars.substring(1, chars.length() - 1);
		IntervalSet set = new IntervalSet();

		if (chars.length() == 0) {
			g.tool.errMgr.grammarError(ErrorType.EMPTY_STRINGS_AND_SETS_NOT_ALLOWED,
					g.fileName, charSetAST.getToken(), "[]");
			return set;
		}

		CharSetParseState state = CharSetParseState.NONE;

		int n = chars.length();
		for (int i = 0; i < n; ) {
			if (state.mode == CharSetParseState.Mode.ERROR) {
				return new IntervalSet();
			}
			int c = chars.codePointAt(i);
			int offset = Character.charCount(c);
			if (c == '\\') {
				EscapeSequenceParsing.Result escapeParseResult =
					EscapeSequenceParsing.parseEscape(chars, i);
				switch (escapeParseResult.type) {
					case INVALID:
						String invalid = chars.substring(escapeParseResult.startOffset,
						                                 escapeParseResult.startOffset+escapeParseResult.parseLength);
						g.tool.errMgr.grammarError(ErrorType.INVALID_ESCAPE_SEQUENCE,
						                           g.fileName, charSetAST.getToken(), invalid);
						state = CharSetParseState.ERROR;
						break;
					case CODE_POINT:
						state = applyPrevStateAndMoveToCodePoint(charSetAST, set, state, escapeParseResult.codePoint);
						break;
					case PROPERTY:
						state = applyPrevStateAndMoveToProperty(charSetAST, set, state, escapeParseResult.propertyIntervalSet);
						break;
				}
				offset = escapeParseResult.parseLength;
			}
			else if (c == '-' && !state.inRange && i != 0 && i != n - 1) {
				if (state.mode == CharSetParseState.Mode.PREV_PROPERTY) {
					g.tool.errMgr.grammarError(ErrorType.UNICODE_PROPERTY_NOT_ALLOWED_IN_RANGE,
							g.fileName, charSetAST.getToken(), charSetAST.getText());
					state = CharSetParseState.ERROR;
				}
				else {
					state = new CharSetParseState(state.mode, true, state.prevCodePoint, state.prevProperty);
				}
			}
			else {
				state = applyPrevStateAndMoveToCodePoint(charSetAST, set, state, c);
			}
			i += offset;
		}
		if (state.mode == CharSetParseState.Mode.ERROR) {
			return new IntervalSet();
		}
		// Whether or not we were in a range, we'll add the last code point found to the set.
		applyPrevState(charSetAST, set, state);
		return set;
	}

	private CharSetParseState applyPrevStateAndMoveToCodePoint(
			GrammarAST charSetAST,
			IntervalSet set,
			CharSetParseState state,
			int codePoint) {
		if (state.inRange) {
			if (state.prevCodePoint > codePoint) {
				g.tool.errMgr.grammarError(
						ErrorType.EMPTY_STRINGS_AND_SETS_NOT_ALLOWED,
						g.fileName,
						charSetAST.getToken(),
						CharSupport.getRangeEscapedString(state.prevCodePoint, codePoint));
			}
			checkSetCollision(charSetAST, set, state.prevCodePoint, codePoint);
			set.add(state.prevCodePoint, codePoint);
			state = CharSetParseState.NONE;
		}
		else {
			applyPrevState(charSetAST, set, state);
			state = new CharSetParseState(
					CharSetParseState.Mode.PREV_CODE_POINT,
					false,
					codePoint,
					IntervalSet.EMPTY_SET);
		}
		return state;
	}

	private CharSetParseState applyPrevStateAndMoveToProperty(
			GrammarAST charSetAST,
			IntervalSet set,
			CharSetParseState state,
			IntervalSet property) {
		if (state.inRange) {
			g.tool.errMgr.grammarError(ErrorType.UNICODE_PROPERTY_NOT_ALLOWED_IN_RANGE,
						   g.fileName, charSetAST.getToken(), charSetAST.getText());
			return CharSetParseState.ERROR;
		}
		else {
			applyPrevState(charSetAST, set, state);
			state = new CharSetParseState(
					CharSetParseState.Mode.PREV_PROPERTY,
					false,
					-1,
					property);
		}
		return state;
	}

	private void applyPrevState(GrammarAST charSetAST, IntervalSet set, CharSetParseState state) {
		switch (state.mode) {
			case NONE:
			case ERROR:
				break;
			case PREV_CODE_POINT:
				checkSetCollision(charSetAST, set, state.prevCodePoint);
				set.add(state.prevCodePoint);
				break;
			case PREV_PROPERTY:
				set.addAll(state.prevProperty);
				break;
		}
	}

	protected void checkSetCollision(GrammarAST ast, IntervalSet set, int el) {
		checkSetCollision(ast, set, el, el);
	}

	protected void checkSetCollision(GrammarAST ast, IntervalSet set, int a, int b) {
		for (int i = a; i <= b; i++) {
			if (set.contains(i)) {
				String setText;
				if (ast.getChildren() == null) {
					setText = ast.getText();
				}
				else {
					StringBuilder sb = new StringBuilder();
					for (Object child : ast.getChildren()) {
						if (child instanceof RangeAST) {
							sb.append(((RangeAST) child).getChild(0).getText());
							sb.append("..");
							sb.append(((RangeAST) child).getChild(1).getText());
						}
						else {
							sb.append(((GrammarAST)child).getText());
						}
						sb.append(" | ");
					}
					sb.replace(sb.length() - 3, sb.length(), "");
					setText = sb.toString();
				}
				g.tool.errMgr.grammarError(ErrorType.CHARACTERS_COLLISION_IN_SET, g.fileName, ast.getToken(),
						CharSupport.getRangeEscapedString(a, b), setText);
				break;
			}
		}
	}

	@Override
	public Handle tokenRef(TerminalAST node) {
		// Ref to EOF in lexer yields char transition on -1
		if (node.getText().equals("EOF") ) {
			ATNState left = newState(node);
			ATNState right = newState(node);
			left.addTransition(new AtomTransition(right, IntStream.EOF));
			return new Handle(left, right);
		}
		return _ruleRef(node);
	}

	private LexerAction createLexerAction(GrammarAST ID, GrammarAST arg) {
		String command = ID.getText();
		checkCommands(command, ID.getToken());

		if ("skip".equals(command) && arg == null) {
			return LexerSkipAction.INSTANCE;
		}
		else if ("more".equals(command) && arg == null) {
			return LexerMoreAction.INSTANCE;
		}
		else if ("popMode".equals(command) && arg == null) {
			return LexerPopModeAction.INSTANCE;
		}
		else if ("mode".equals(command) && arg != null) {
			String modeName = arg.getText();
			Integer mode = getModeConstantValue(modeName, arg.getToken());
			if (mode == null) {
				return null;
			}

			return new LexerModeAction(mode);
		}
		else if ("pushMode".equals(command) && arg != null) {
			String modeName = arg.getText();
			Integer mode = getModeConstantValue(modeName, arg.getToken());
			if (mode == null) {
				return null;
			}

			return new LexerPushModeAction(mode);
		}
		else if ("type".equals(command) && arg != null) {
			String typeName = arg.getText();
			Integer type = getTokenConstantValue(typeName, arg.getToken());
			if (type == null) {
				return null;
			}

			return new LexerTypeAction(type);
		}
		else if ("channel".equals(command) && arg != null) {
			String channelName = arg.getText();
			Integer channel = getChannelConstantValue(channelName, arg.getToken());
			if (channel == null) {
				return null;
			}

			return new LexerChannelAction(channel);
		}
		else {
			return null;
		}
	}

	private void checkCommands(String command, Token commandToken) {
		// Command combinations list: https://github.com/antlr/antlr4/issues/1388#issuecomment-263344701
		if (!command.equals("pushMode") && !command.equals("popMode")) {
			if (ruleCommands.contains(command)) {
				g.tool.errMgr.grammarError(ErrorType.DUPLICATED_COMMAND, g.fileName, commandToken, command);
			}

			if (!ruleCommands.equals("mode")) {
				String firstCommand = null;

				if (command.equals("skip")) {
					if (ruleCommands.contains("more")) {
						firstCommand = "more";
					}
					else if (ruleCommands.contains("type")) {
						firstCommand = "type";
					}
					else if (ruleCommands.contains("channel")) {
						firstCommand = "channel";
					}
				}
				else if (command.equals("more")) {
					if (ruleCommands.contains("skip")) {
						firstCommand = "skip";
					}
					else if (ruleCommands.contains("type")) {
						firstCommand = "type";
					}
					else if (ruleCommands.contains("channel")) {
						firstCommand = "channel";
					}
				}
				else if (command.equals("type") || command.equals("channel")) {
					if (ruleCommands.contains("more")) {
						firstCommand = "more";
					}
					else if (ruleCommands.contains("skip")) {
						firstCommand = "skip";
					}
				}

				if (firstCommand != null) {
					g.tool.errMgr.grammarError(ErrorType.INCOMPATIBLE_COMMANDS, g.fileName, commandToken, firstCommand, command);
				}
			}
		}

		ruleCommands.add(command);
	}

	private Integer getModeConstantValue(String modeName, Token token) {
		if (modeName == null) {
			return null;
		}

		if (modeName.equals("DEFAULT_MODE")) {
			return Lexer.DEFAULT_MODE;
		}
		if (COMMON_CONSTANTS.containsKey(modeName)) {
			g.tool.errMgr.grammarError(ErrorType.MODE_CONFLICTS_WITH_COMMON_CONSTANTS, g.fileName, token, token.getText());
			return null;
		}

		List<String> modeNames = new ArrayList<String>(((LexerGrammar)g).modes.keySet());
		int mode = modeNames.indexOf(modeName);
		if (mode >= 0) {
			return mode;
		}

		try {
			return Integer.parseInt(modeName);
		} catch (NumberFormatException ex) {
			g.tool.errMgr.grammarError(ErrorType.CONSTANT_VALUE_IS_NOT_A_RECOGNIZED_MODE_NAME, g.fileName, token, token.getText());
			return null;
		}
	}

	private Integer getTokenConstantValue(String tokenName, Token token) {
		if (tokenName == null) {
			return null;
		}

		if (tokenName.equals("EOF")) {
			return Lexer.EOF;
		}
		if (COMMON_CONSTANTS.containsKey(tokenName)) {
			g.tool.errMgr.grammarError(ErrorType.TOKEN_CONFLICTS_WITH_COMMON_CONSTANTS, g.fileName, token, token.getText());
			return null;
		}

		int tokenType = g.getTokenType(tokenName);
		if (tokenType != org.antlr.v4.runtime.Token.INVALID_TYPE) {
			return tokenType;
		}

		try {
			return Integer.parseInt(tokenName);
		} catch (NumberFormatException ex) {
			g.tool.errMgr.grammarError(ErrorType.CONSTANT_VALUE_IS_NOT_A_RECOGNIZED_TOKEN_NAME, g.fileName, token, token.getText());
			return null;
		}
	}

	private Integer getChannelConstantValue(String channelName, Token token) {
		if (channelName == null) {
			return null;
		}

		if (channelName.equals("HIDDEN")) {
			return Lexer.HIDDEN;
		}
		if (channelName.equals("DEFAULT_TOKEN_CHANNEL")) {
			return Lexer.DEFAULT_TOKEN_CHANNEL;
		}
		if (COMMON_CONSTANTS.containsKey(channelName)) {
			g.tool.errMgr.grammarError(ErrorType.CHANNEL_CONFLICTS_WITH_COMMON_CONSTANTS, g.fileName, token, token.getText());
			return null;
		}

		int channelValue = g.getChannelValue(channelName);
		if (channelValue >= org.antlr.v4.runtime.Token.MIN_USER_CHANNEL_VALUE) {
			return channelValue;
		}

		try {
			return Integer.parseInt(channelName);
		} catch (NumberFormatException ex) {
			g.tool.errMgr.grammarError(ErrorType.CONSTANT_VALUE_IS_NOT_A_RECOGNIZED_CHANNEL_NAME, g.fileName, token, token.getText());
			return null;
		}
	}
}
