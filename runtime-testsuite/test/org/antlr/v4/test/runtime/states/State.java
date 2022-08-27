/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.states;

import org.antlr.v4.test.runtime.Stage;

public abstract class State {
	public final State previousState;

	public final Exception exception;

	public abstract Stage getStage();

	public boolean containsErrors() {
		return exception != null;
	}

	public String getErrorMessage() {
		String result = "State: " + getStage() + "; ";
		if (exception != null) {
			result += exception.toString();
			if ( exception.getCause()!=null ) {
				result += "\nCause:\n";
				result += exception.getCause().toString();
			}
		}
		return result;
	}

	public State(State previousState, Exception exception) {
		this.previousState = previousState;
		this.exception = exception;
	}
}
