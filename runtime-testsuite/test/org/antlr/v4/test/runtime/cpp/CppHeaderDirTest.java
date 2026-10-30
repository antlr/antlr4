/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.cpp;

import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.test.runtime.RunOptions;
import org.antlr.v4.test.runtime.Stage;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.State;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Generated headers in their own directory, which is the only extra include path the build gets. */
public class CppHeaderDirTest {
	private static final String GRAMMAR =
		"grammar T;\n" +
		"s : ID EOF {std::cout << $ID.text << std::endl;} ;\n" +
		"ID : [a-z]+ ;\n" +
		"WS : [ \\t\\r\\n]+ -> skip ;\n";

	@Test
	public void testParserListenerAndVisitorBuildWithHeadersInSeparateDirectory() {
		try (CppRunner runner = new CppRunner("include")) {
			RunOptions runOptions = new RunOptions("T.g4", GRAMMAR, "TParser", "TLexer", true, true, "s",
				"abc", false, false, false, false, Stage.Execute, "Cpp", null, PredictionMode.LL, true);

			State state = runner.run(runOptions);

			Path sources = Paths.get(runner.getTempDirPath());
			Path headers = sources.resolve("include");
			for (String name : new String[] {"TLexer", "TParser", "TListener", "TBaseListener", "TVisitor", "TBaseVisitor"}) {
				assertTrue(Files.exists(headers.resolve(name + ".h")), name + ".h");
				assertFalse(Files.exists(sources.resolve(name + ".h")), name + ".h");
				assertTrue(Files.exists(sources.resolve(name + ".cpp")), name + ".cpp");
			}
			assertFalse(state.containsErrors(), state.getErrorMessage());
			assertInstanceOf(ExecutedState.class, state);
			assertEquals("abc\n", ((ExecutedState) state).output);
		}
	}
}
