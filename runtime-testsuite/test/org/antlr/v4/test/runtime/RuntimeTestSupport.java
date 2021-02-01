/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import java.io.File;

/** This interface describes functionality needed to execute a runtime test.
 *  Unfortunately the Base*Test.java files are big junk drawers. This is
 *  an attempt to make it more obvious what new target implementers have to
 *  implement.
 *
 *  @since 4.6
 */
public interface RuntimeTestSupport {

	// dir containing grammar input and output
	File getTempParserDir();
	String getTempParserDirPath();

	// dir containing test input and output
	File getTempTestDir();
	String getTempDirPath();
	void eraseTempDir();

    void testSetUp() throws Exception;
	void testTearDown() throws Exception;

	void beforeTest(RuntimeTestDescriptor descriptor);
	void afterTest(RuntimeTestDescriptor descriptor);

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
