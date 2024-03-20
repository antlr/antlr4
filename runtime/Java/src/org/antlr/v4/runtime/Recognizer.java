/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.misc.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Recognizer<Symbol, ATNInterpreter extends ATNSimulator> {
	public static final int EOF=-1;

	private static final Map<Vocabulary, Map<String, Integer>> tokenTypeMapCache =
		new WeakHashMap<Vocabulary, Map<String, Integer>>();
	private static final Map<String[], Map<String, Integer>> ruleIndexMapCache =
		new WeakHashMap<String[], Map<String, Integer>>();


	private List<ANTLRErrorListener> _listeners =
		new CopyOnWriteArrayList<ANTLRErrorListener>() {{
			add(ConsoleErrorListener.INSTANCE);
		}};

	protected ATNInterpreter _interp;

	private int _stateNumber = -1;

	/** Used to print out token names like ID during debugging and
	 *  error reporting.  The generated parsers implement a method
	 *  that overrides this to point to their String[] tokenNames.
	 *
	 * @deprecated Use {@link #getVocabulary()} instead.
	 */
	@Deprecated
	public abstract String[] getTokenNames();

	public abstract String[] getRuleNames();

	/**
	 * Get the vocabulary used by the recognizer.
	 *
	 * @return A {@link Vocabulary} instance providing information about the
	 * vocabulary used by the grammar.
	 */
	@SuppressWarnings("deprecation")
	public Vocabulary getVocabulary() {
		return VocabularyImpl.fromTokenNames(getTokenNames());
	}

	/**
	 * Get a map from token names to token types.
	 *
	 * <p>Used for XPath and tree pattern compilation.</p>
	 */
	public Map<String, Integer> getTokenTypeMap() {
		Vocabulary vocabulary = getVocabulary();
		synchronized (tokenTypeMapCache) {
			Map<String, Integer> result = tokenTypeMapCache.get(vocabulary);
			if (result == null) {
				result = new HashMap<String, Integer>();
				for (int i = 0; i <= getATN().maxTokenType; i++) {
					String literalName = vocabulary.getLiteralName(i);
					if (literalName != null) {
						result.put(literalName, i);
					}

					String symbolicName = vocabulary.getSymbolicName(i);
					if (symbolicName != null) {
						result.put(symbolicName, i);
					}
				}

				result.put("EOF", Token.EOF);
				result = Collections.unmodifiableMap(result);
				tokenTypeMapCache.put(vocabulary, result);
			}

			return result;
		}
	}

	/**
	 * Get a map from rule names to rule indexes.
	 *
	 * <p>Used for XPath and tree pattern compilation.</p>
	 */
	public Map<String, Integer> getRuleIndexMap() {
		String[] ruleNames = getRuleNames();
		if (ruleNames == null) {
			throw new UnsupportedOperationException("The current recognizer does not provide a list of rule names.");
		}

		synchronized (ruleIndexMapCache) {
			Map<String, Integer> result = ruleIndexMapCache.get(ruleNames);
			if (result == null) {
				result = Collections.unmodifiableMap(Utils.toMap(ruleNames));
				ruleIndexMapCache.put(ruleNames, result);
			}

			return result;
		}
	}

	public int getTokenType(String tokenName) {
		Integer ttype = getTokenTypeMap().get(tokenName);
		if ( ttype!=null ) return ttype;
		return Token.INVALID_TYPE;
	}

	/**
	 * If this recognizer was generated, it will have a serialized ATN
	 * representation of the grammar.
	 *
	 * <p>For interpreters, we don't know their serialized ATN despite having
	 * created the interpreter from it.</p>
	 */
	public String getSerializedATN() {
		throw new UnsupportedOperationException("there is no serialized ATN");
	}

	/** For debugging and other purposes, might want the grammar name.
	 *  Have ANTLR generate an implementation for this method.
	 */
	public abstract String getGrammarFileName();

	/**
	 * Get the {@link ATN} used by the recognizer for prediction.
	 *
	 * @return The {@link ATN} used by the recognizer for prediction.
	 */
	public abstract ATN getATN();

	/**
	 * Get the ATN interpreter used by the recognizer for prediction.
	 *
	 * @return The ATN interpreter used by the recognizer for prediction.
	 */
	public ATNInterpreter getInterpreter() {
		return _interp;
	}

	/** If profiling during the parse/lex, this will return DecisionInfo records
	 *  for each decision in recognizer in a ParseInfo object.
	 *
	 * @since 4.3
	 */
	public ParseInfo getParseInfo() {
		return null;
	}

	/**
	 * Set the ATN interpreter used by the recognizer for prediction.
	 *
	 * @param interpreter The ATN interpreter used by the recognizer for
	 * prediction.
	 */
	public void setInterpreter(ATNInterpreter interpreter) {
		_interp = interpreter;
	}

	/** What is the error header, normally line/character position information? */
	public String getErrorHeader(RecognitionException e) {
		int line = e.getOffendingToken().getLine();
		int charPositionInLine = e.getOffendingToken().getCharPositionInLine();
		return "line "+line+":"+charPositionInLine;
	}

	/** How should a token be displayed in an error message? The default
	 *  is to display just the text, but during development you might
	 *  want to have a lot of information spit out.  Override in that case
	 *  to use t.toString() (which, for CommonToken, dumps everything about
	 *  the token). This is better than forcing you to override a method in
	 *  your token objects because you don't have to go modify your lexer
	 *  so that it creates a new Java type.
	 *
	 * @deprecated This method is not called by the ANTLR 4 Runtime. Specific
	 * implementations of {@link ANTLRErrorStrategy} may provide a similar
	 * feature when necessary. For example, see
	 * {@link DefaultErrorStrategy#getTokenErrorDisplay}.
	 */
	@Deprecated
	public String getTokenErrorDisplay(Token t) {
		if ( t==null ) return "<no token>";
		String s = t.getText();
		if ( s==null ) {
			if ( t.getType()==Token.EOF ) {
				s = "<EOF>";
			}
			else {
				s = "<"+t.getType()+">";
			}
		}
		s = s.replace("\n","\\n");
		s = s.replace("\r","\\r");
		s = s.replace("\t","\\t");
		return "'"+s+"'";
	}

	/**
	 * @exception NullPointerException if {@code listener} is {@code null}.
	 */
	public void addErrorListener(ANTLRErrorListener listener) {
		if (listener == null) {
			throw new NullPointerException("listener cannot be null.");
		}

		_listeners.add(listener);
	}

	public void removeErrorListener(ANTLRErrorListener listener) {
		_listeners.remove(listener);
	}

	public void removeErrorListeners() {
		_listeners.clear();
	}


	public List<? extends ANTLRErrorListener> getErrorListeners() {
		return _listeners;
	}

	public ANTLRErrorListener getErrorListenerDispatch() {
		return new ProxyErrorListener(getErrorListeners());
	}

	// subclass needs to override these if there are sempreds or actions
	// that the ATN interp needs to execute
	public boolean sempred(RuleContext _localctx, int ruleIndex, int actionIndex) {
		return true;
	}

	public boolean precpred(RuleContext localctx, int precedence) {
		return true;
	}

	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
	}

	public final int getState() {
		return _stateNumber;
	}

	/** Indicate that the recognizer has changed internal state that is
	 *  consistent with the ATN state passed in.  This way we always know
	 *  where we are in the ATN as the parser goes along. The rule
	 *  context objects form a stack that lets us see the stack of
	 *  invoking rules. Combine this and we have complete ATN
	 *  configuration information.
	 */
	public final void setState(int atnState) {
//		System.err.println("setState "+atnState);
		_stateNumber = atnState;
//		if ( traceATNStates ) _ctx.trace(atnState);
	}

	public abstract IntStream getInputStream();

	public abstract void setInputStream(IntStream input);


	public abstract TokenFactory<?> getTokenFactory();

	public abstract void setTokenFactory(TokenFactory<?> input);
}
