package org.antlr.v4.test.runtime;

public interface SingleTest {
	void testSetUp() throws Exception;
	void testTearDown() throws Exception;
	void eraseTempDir();

	String getTmpDir();

	String getStdout();
	String getParseErrors();
	String getANTLRToolErrors();

	String execLexer(String grammarFileName,
	                 String grammarStr,
	                 String lexerName,
	                 String input,
	                 boolean showDFA);

	String execParser(String grammarFileName,
	                  String grammarStr,
	                  String parserName,
	                  String lexerName,
	                  String listenerName,
	                  String visitorName,
	                  String startRuleName,
	                  String input,
	                  boolean showDiagnosticErrors);
}
