/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.states;

import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.GeneratedFile;
import org.antlr.v4.test.runtime.Stage;

import java.util.List;

import static org.antlr.v4.test.runtime.RuntimeTestUtils.joinLines;

public class GeneratedState extends State {
	@Override
	public Stage getStage() {
		return Stage.Generate;
	}

	public final ErrorQueue errorQueue;
	public final List<GeneratedFile> generatedFiles;

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

	public GeneratedState(ErrorQueue errorQueue, List<GeneratedFile>  generatedFiles, Exception exception) {
		super(null, exception);
		this.errorQueue = errorQueue;
		this.generatedFiles = generatedFiles;
	}
}
