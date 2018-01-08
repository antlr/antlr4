/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.Tool;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.tool.ast.GrammarRootAST;

/** */
public class LexerGrammar extends Grammar {
	public static final String DEFAULT_MODE_NAME = "DEFAULT_MODE";

	/** The grammar from which this lexer grammar was derived (if implicit) */
    public Grammar implicitLexerOwner;

	/** DEFAULT_MODE rules are added first due to grammar syntax order */
	public MultiMap<String, Rule> modes;

	public LexerGrammar(Tool tool, GrammarRootAST ast) {
		super(tool, ast);
	}

	public LexerGrammar(String grammarText) throws RecognitionException {
		super(grammarText);
	}

	public LexerGrammar(String grammarText, ANTLRToolListener listener) throws RecognitionException {
		super(grammarText, listener);
	}

	public LexerGrammar(String fileName, String grammarText, ANTLRToolListener listener) throws RecognitionException {
		super(fileName, grammarText, listener);
	}

	@Override
	public boolean defineRule(Rule r) {
		if (!super.defineRule(r)) {
			return false;
		}

		if ( modes==null ) modes = new MultiMap<String, Rule>();
		modes.map(r.mode, r);
		return true;
	}

	@Override
	public boolean undefineRule(Rule r) {
		if (!super.undefineRule(r)) {
			return false;
		}

		boolean removed = modes.get(r.mode).remove(r);
		assert removed;
		return true;
	}
}
