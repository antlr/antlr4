/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.atn.PredictionMode;

public class RunOptions {
	public final String[] grammars;
	public final String[] slaveGrammars;
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
	public final String[] extraGenerationOptions;

	public static RunOptions createGenerationOptions(String[] grammars, String[] slaveGrammars, boolean useListener, boolean useVisitor,
													 String superClass, String[] extraGenerationOptions
	) {
		return new RunOptions(
				grammars,
				slaveGrammars,
				useListener,
				useVisitor,
				null,
				null,
				false,
				false,
				false,
				false,
				Stage.Generate,
				superClass,
				PredictionMode.LL,
				false,
				extraGenerationOptions
		);
	}

	public static RunOptions createCompilationOptions(String[] grammars, String[] slaveGrammars, boolean useListener, boolean useVisitor,
													  String superClass, String[] extraGenerationOptions) {
		return new RunOptions(
				grammars,
				slaveGrammars,
				useListener,
				useVisitor,
				null,
				null,
				false,
				false,
				false,
				false,
				Stage.Compile,
				superClass,
				PredictionMode.LL,
				false,
				extraGenerationOptions
		);
	}

	public RunOptions(String[] grammars, String[] slaveGrammars,
					  boolean useListener, boolean useVisitor, String startRuleName,
					  String input, boolean profile, boolean showDiagnosticErrors,
					  boolean traceATN, boolean showDFA, Stage endStage,
					  String superClass, PredictionMode predictionMode, boolean buildParseTree,
					  String[] extraGenerationOptions) {
		this.grammars = grammars;
		this.slaveGrammars = slaveGrammars;
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
		this.extraGenerationOptions = extraGenerationOptions;
	}
}
