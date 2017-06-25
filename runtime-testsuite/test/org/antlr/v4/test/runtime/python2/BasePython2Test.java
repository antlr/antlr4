/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.python.BasePythonTest;
import org.stringtemplate.v4.ST;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.writeFile;

public class BasePython2Test extends BasePythonTest {

	@Override
	protected String getLanguage() {
		return "Python2";
	}

	@Override
	protected String getPythonExecutable() {
		return "python2.7";
	}

	@Override
	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"from __future__ import print_function\n"
						+ "import sys\n"
						+ "import codecs\n"
						+ "from antlr4 import *\n"
						+ "from <lexerName> import <lexerName>\n"
						+ "\n"
						+ "def main(argv):\n"
						+ "    input = FileStream(argv[1], encoding='utf-8', errors='replace')\n"
						+ "    with codecs.open(argv[2], 'w', 'utf-8', 'replace') as output:\n"
						+ "        lexer = <lexerName>(input, output)\n"
						+ "        stream = CommonTokenStream(lexer)\n"
						+ "        stream.fill()\n"
						+ "        [ print(unicode(t), file=output) for t in stream.tokens ]\n"
						+ (showDFA ? "        print(lexer._interp.decisionToDFA[Lexer.DEFAULT_MODE].toLexerString(), end='', file=output)\n"
								: "") + "\n" + "if __name__ == '__main__':\n"
						+ "    main(sys.argv)\n" + "\n");
		outputFileST.add("lexerName", lexerName);
		writeFile(tmpdir, "Test.py", outputFileST.render());
	}

	@Override
	protected void writeParserTestFile(String parserName, String lexerName,
			String listenerName, String visitorName,
			String parserStartRuleName, boolean debug, boolean trace) {
		if(!parserStartRuleName.endsWith(")"))
			parserStartRuleName += "()";
		ST outputFileST = new ST(
				"import sys\n"
						+ "import codecs\n"
						+ "from antlr4 import *\n"
						+ "from <lexerName> import <lexerName>\n"
						+ "from <parserName> import <parserName>\n"
						+ "from <listenerName> import <listenerName>\n"
						+ "from <visitorName> import <visitorName>\n"
						+ "\n"
						+ "class TreeShapeListener(ParseTreeListener):\n"
						+ "\n"
						+ "    def visitTerminal(self, node):\n"
						+ "        pass\n"
						+ "\n"
						+ "    def visitErrorNode(self, node):\n"
						+ "        pass\n"
						+ "\n"
						+ "    def exitEveryRule(self, ctx):\n"
						+ "        pass\n"
						+ "\n"
						+ "    def enterEveryRule(self, ctx):\n"
						+ "        for child in ctx.getChildren():\n"
						+ "            parent = child.parentCtx\n"
						+ "            if not isinstance(parent, RuleNode) or parent.getRuleContext() != ctx:\n"
						+ "                raise IllegalStateException(\"Invalid parse tree shape detected.\")\n"
						+ "\n"
						+ "def main(argv):\n"
						+ "    input = FileStream(argv[1], encoding='utf-8', errors='replace')\n"
						+ "    with codecs.open(argv[2], 'w', 'utf-8', 'replace') as output:\n"
						+ "        lexer = <lexerName>(input, output)\n"
						+ "        stream = CommonTokenStream(lexer)\n"
						+ "<createParser>"
						+ "        parser.buildParseTrees = True\n"
						+ "        tree = parser.<parserStartRuleName>\n"
						+ "        ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree)\n"
						+ "\n" + "if __name__ == '__main__':\n"
						+ "    main(sys.argv)\n" + "\n");
		String stSource = "        parser = <parserName>(stream, output)\n";
		if(debug)
			stSource += "        parser.addErrorListener(DiagnosticErrorListener())\n";
		if(trace)
			stSource += "        parser.setTrace(True)\n";
		ST createParserST = new ST(stSource);
		outputFileST.add("createParser", createParserST);
		outputFileST.add("parserName", parserName);
		outputFileST.add("lexerName", lexerName);
		outputFileST.add("listenerName", listenerName);
		outputFileST.add("visitorName", visitorName);
		outputFileST.add("parserStartRuleName", parserStartRuleName);
		writeFile(tmpdir, "Test.py", outputFileST.render());
	}
}
