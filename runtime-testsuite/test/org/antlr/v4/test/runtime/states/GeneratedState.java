/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.states;

import org.antlr.v4.test.runtime.ErrorQueue;
import org.antlr.v4.test.runtime.Stage;
import org.antlr.v4.tool.ANTLRMessage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneratedState extends State {
	@Override
	public Stage getStage() {
		return Stage.Generate;
	}

	public final ErrorQueue errorQueue;
	public final List<String> generatedFiles;

	@Override
	public boolean containsErrors() {
		return errorQueue.errors.size() > 0 || super.containsErrors();
	}

	public String getErrorMessage() {
		if (exception != null) {
			return exception.toString();
		}

		if (errorQueue.errors.size() > 0) {
			List<String> errors = errorQueue.errors.stream().map(ANTLRMessage::toString).collect(Collectors.toList());
			return String.join("\n", errors);
		}

		return null;
	}

	public GeneratedState(ErrorQueue errorQueue, List<String> generatedFiles, Exception exception) {
		super(null, exception);
		this.errorQueue = errorQueue;
		this.generatedFiles = generatedFiles;
	}
}
