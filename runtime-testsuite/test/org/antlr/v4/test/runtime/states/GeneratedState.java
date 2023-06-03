/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.states;

import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.GeneratedFile;
import org.antlr.v4.test.runtime.GrammarFile;
import org.antlr.v4.test.runtime.Stage;

import java.util.List;

import static org.antlr.v4.test.runtime.RuntimeTestUtils.joinLines;

public class GeneratedState extends State {
	@Override
	public Stage getStage() {
		return Stage.Generate;
	}

	public final List<GrammarFile> grammarFiles;
	public final ErrorQueue errorQueue;
	public final String lexerName;
	public final String parserName;
	public final List<GeneratedFile> generatedFiles;

	public GrammarFile getMainGrammarFile() {
		return grammarFiles.get(0);
	}

	@Override
	public boolean containsErrors() {
		return errorQueue.errors.size() > 0 || super.containsErrors();
	}

	public String getErrorMessage() {
		String result = super.getErrorMessage();

		if (errorQueue.errors.size() > 0) {
			result = joinLines(result, errorQueue.toString(true));
		}

		return result;
	}

	public GeneratedState(List<GrammarFile> grammarFiles, ErrorQueue errorQueue, String lexerName, String parserName,
						  List<GeneratedFile> generatedFiles, Exception exception
	) {
		super(null, exception);
		this.grammarFiles = grammarFiles;
		this.errorQueue = errorQueue;
		this.lexerName = lexerName;
		this.parserName = parserName;
		this.generatedFiles = generatedFiles;
	}
}
