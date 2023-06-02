/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.atn.PredictionMode;

public class RunOptions {
	public final String grammarFileName;
	public final String grammarStr;
	public final String parserName;
	public final String lexerName;
	public final String grammarName;
	public final boolean useListener;
	public final boolean useVisitor;
	public final String startRuleName;
	public final String input;
	public final boolean profile;
	public final boolean showDiagnosticErrors;
	public final boolean traceATN;
	public final boolean showDFA;
	public final Stage endStage;
	public final String superClass;
	public final PredictionMode predictionMode;
	public final boolean buildParseTree;

	public RunOptions(String grammarFileName, String grammarStr, String parserName, String lexerName,
					  boolean useListener, boolean useVisitor, String startRuleName,
					  String input, boolean profile, boolean showDiagnosticErrors,
					  boolean traceATN, boolean showDFA, Stage endStage,
					  String language, String superClass, PredictionMode predictionMode, boolean buildParseTree) {
		this.grammarFileName = grammarFileName;
		this.grammarStr = grammarStr;
		this.parserName = parserName;
		this.lexerName = lexerName;
		String grammarName = null;
		boolean isCombinedGrammar = lexerName != null && parserName != null || language.equals("Go");
		if (isCombinedGrammar) {
			if (parserName != null) {
				grammarName = parserName.endsWith("Parser")
					? parserName.substring(0, parserName.length() - "Parser".length())
					: parserName;
			}
			else if (lexerName != null) {
				grammarName = lexerName.endsWith("Lexer")
					? lexerName.substring(0, lexerName.length() - "Lexer".length())
					: lexerName;
			}
		}
		else {
			if (parserName != null) {
				grammarName = parserName;
			}
			else {
				grammarName = lexerName;
			}
		}
		this.grammarName = grammarName;
		this.useListener = useListener;
		this.useVisitor = useVisitor;
		this.startRuleName = startRuleName;
		this.input = input;
		this.profile = profile;
		this.showDiagnosticErrors = showDiagnosticErrors;
		this.traceATN = traceATN;
		this.showDFA = showDFA;
		this.endStage = endStage;
		this.superClass = superClass;
		this.predictionMode = predictionMode;
		this.buildParseTree = buildParseTree;
	}
}
