package org.antlr.v4.test.tool;

import org.antlr.v4.test.runtime.RunOptions;
import org.antlr.v4.test.runtime.Stage;
import org.antlr.v4.test.runtime.java.JavaRunner;
import org.antlr.v4.test.runtime.states.ExecutedState;
import org.antlr.v4.test.runtime.states.GeneratedState;
import org.antlr.v4.test.runtime.states.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;
import static org.antlr.v4.test.runtime.RuntimeTestUtils.FileSeparator;
import static org.antlr.v4.test.tool.ToolTestUtils.createOptionsForJavaToolTests;
import static org.junit.jupiter.api.Assertions.*;

public class TestActionTemplates {
	@Test() void testIncorrectActionTemplateGroupExtension(@TempDir Path tempDir) {
		String actionTemplates = tempDir + FileSeparator + "Java.st";

		String grammar =
			"lexer grammar L;"+
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, actionTemplates);

		assertInstanceOf(GeneratedState.class, state);
		GeneratedState generated = (GeneratedState) state;
		assertTrue(generated.containsErrors());
		assertEquals(
			"State: Generate; \n" +
			"error(207):  error reading action templates file " + actionTemplates + ": " +
			"Group file names must end in .stg: " + actionTemplates + "\n",
			generated.getErrorMessage());
	}

	@Test() void testActionTemplateFileMissing(@TempDir Path tempDir) {
		String actionTemplates = tempDir + FileSeparator + "Java.stg";

		String grammar =
			"lexer grammar L;"+
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, actionTemplates);

		assertInstanceOf(GeneratedState.class, state);
		GeneratedState generated = (GeneratedState) state;
		assertTrue(generated.containsErrors());
		assertEquals(
			"State: Generate; \n" +
			"error(206):  cannot find action templates file " + actionTemplates + " given for L\n",
			generated.getErrorMessage());
	}

	@Test void testActionTemplateLexerAction(@TempDir Path tempDir) {
		writeActionTemplatesFile(tempDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String actionTemplates = tempDir + FileSeparator + "Java.stg";

		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {<writeln(\"I\")>} ;\n"+
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, actionTemplates);

		// Should have identical output to TestLexerActions.testActionExecutedInDFA
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,3:4='34',<1>,1:3]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";

		assertInstanceOf(ExecutedState.class, state);

		assertEquals(expecting, ((ExecutedState) state).output);
	}

	@Test void testActionTemplateHeader(@TempDir Path tempDir) {
		String sb =
			"normalizerImports() ::= <<\n" +
			"import java.text.Normalizer;\n" +
			"import java.text.Normalizer.Form;\n" +
			">>\n" +
			"normalize(s) ::= <<Normalizer.normalize(<s>, Form.NFKC)>>\n" +
			"getText() ::= <<getText()>>\n" +
			"setText(s) ::= <<setText(<s>);>>";

		writeActionTemplatesFile(tempDir, sb);

		String actionTemplates = tempDir + FileSeparator + "Java.stg";

		String grammar =
			"lexer grammar L;\n"+
			"@lexer::header {\n"+
			"<normalizerImports()>\n"+
			"}\n"+
			"ID : (ID_START ID_CONTINUE* | '_' ID_CONTINUE+) { <setText(normalize(getText()))> } ;\n"+
			"ID_START : [\\p{XID_Start}] ;\n"+
			"ID_CONTINUE: [\\p{XID_Continue}] ;\n"+
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "This _is \ufb01ne", tempDir, actionTemplates);

		String expecting =
			"[@0,0:3='This',<1>,1:0]\n"+
			"[@1,5:7='_is',<1>,1:5]\n"+
			"[@2,9:11='fine',<1>,1:9]\n"+
			"[@3,12:11='<EOF>',<-1>,1:12]\n";

		assertInstanceOf(ExecutedState.class, state);

		assertEquals(expecting, ((ExecutedState) state).output);
	}

	void writeActionTemplatesFile(Path tempDir, String template) {
		writeFile(tempDir.toString(), "Java.stg", template);
	}

	State execLexer(String grammarStr, String input, Path tempDir, String actionTemplates) {
		RunOptions runOptions = createOptionsForJavaToolTests("L.g4", grammarStr, null, "L",
			false, true, null, input,
			false, false, Stage.Execute, actionTemplates);
		try (JavaRunner runner = new JavaRunner(tempDir, false)) {
			return runner.run(runOptions);
		}
	}
}
