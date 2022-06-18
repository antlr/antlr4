/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.states;

import org.antlr.v4.test.runtime.Stage;

public class CompiledState extends State {
	@Override
	public Stage getStage() {
		return Stage.Compile;
	}

	public CompiledState(GeneratedState previousState, Exception exception) {
		super(previousState, exception);
	}
}
