/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.atn.PredictionMode;

import java.net.URI;
import java.util.Arrays;

/** This object represents all the information we need about a single test and is the
 * in-memory representation of a descriptor file
 */
public class RuntimeTestDescriptor {
	/** Return a string representing the name of the target currently testing
	 *  this descriptor.
	 *  Multiple instances of the same descriptor class
	 *  can be created to test different targets.
	 */
	public final String name;

	public final String notes;

	/** Parser input. Return "" if not input should be provided to the parser or lexer. */
	public final String input;

	/** Output from executing the parser. Return null if no output is expected. */
	public final String output;

	/** Parse errors Return null if no errors are expected. */
	public final String errors;

	/** The rule at which parsing should start */
	public final String startRule;

	public final String[] grammars;

	/** List of grammars imported into the grammar */
	public final String[] slaveGrammars;

	/** For lexical tests, dump the DFA of the default lexer mode to stdout */
	public final boolean showDFA;

	/** For parsing, engage the DiagnosticErrorListener, dumping results to stderr */
	public final boolean showDiagnosticErrors;

	public final boolean traceATN;

	public final PredictionMode predictionMode;

	public final boolean buildParseTree;

	public final String[] skipTargets;

	public final URI uri;

	public RuntimeTestDescriptor(String name, String notes,
								 String input, String output, String errors,
								 String startRule, String[] grammars, String[] slaveGrammars,
								 boolean showDiagnosticErrors, boolean traceATN, boolean showDFA, PredictionMode predictionMode,
								 boolean buildParseTree, String[] skipTargets, URI uri) {
		this.name = name;
		this.notes = notes;
		this.input = input;
		this.output = output;
		this.errors = errors;
		this.startRule = startRule;
		this.grammars = grammars;
		this.slaveGrammars = slaveGrammars == null ? new String[0] : slaveGrammars;
		this.showDFA = showDFA;
		this.showDiagnosticErrors = showDiagnosticErrors;
		this.traceATN = traceATN;
		this.predictionMode = predictionMode;
		this.buildParseTree = buildParseTree;
		this.skipTargets = skipTargets != null ? skipTargets : new String[0];
		this.uri = uri;
	}

	/** Return true if this test should be ignored for the indicated target */
	public boolean ignore(String targetName) {
		return Arrays.asList(skipTargets).contains(targetName);
	}

	@Override
	public String toString() {
		return name;
	}
}
