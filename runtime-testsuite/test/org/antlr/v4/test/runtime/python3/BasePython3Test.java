/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.test.runtime.python3;

import org.antlr.v4.test.runtime.python.BasePythonTest;
import org.stringtemplate.v4.ST;

public abstract class BasePython3Test extends BasePythonTest {

	@Override
	protected String getLanguage() {
		return "Python3";
	}

	@Override
	protected String getPythonExecutable() {
		return "python3.4";
	}

	@Override
	protected void writeLexerTestFile(String lexerName, boolean showDFA) {
		ST outputFileST = new ST(
				"import sys\n"
						+ "from antlr4 import *\n"
						+ "from <lexerName> import <lexerName>\n"
						+ "\n"
						+ "def main(argv):\n"
						+ "    input = FileStream(argv[1])\n"
						+ "    lexer = <lexerName>(input)\n"
						+ "    stream = CommonTokenStream(lexer)\n"
						+ "    stream.fill()\n"
						+ "    [ print(str(t)) for t in stream.tokens ]\n"
						+ (showDFA ? "    print(lexer._interp.decisionToDFA[Lexer.DEFAULT_MODE].toLexerString(), end='')\n"
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
						+ "from antlr4 import *\n"
						+ "from <lexerName> import <lexerName>\n"
						+ "from <parserName> import <parserName>\n"
						+ "from <listenerName> import <listenerName>\n"
						+ "from <visitorName> import <visitorName>\n"
						+ "\n"
						+ "class TreeShapeListener(ParseTreeListener):\n"
						+ "\n"
						+ "    def visitTerminal(self, node:TerminalNode):\n"
						+ "        pass\n"
						+ "\n"
						+ "    def visitErrorNode(self, node:ErrorNode):\n"
						+ "        pass\n"
						+ "\n"
						+ "    def exitEveryRule(self, ctx:ParserRuleContext):\n"
						+ "        pass\n"
						+ "\n"
						+ "    def enterEveryRule(self, ctx:ParserRuleContext):\n"
						+ "        for child in ctx.getChildren():\n"
						+ "            parent = child.parentCtx\n"
						+ "            if not isinstance(parent, RuleNode) or parent.getRuleContext() != ctx:\n"
						+ "                raise IllegalStateException(\"Invalid parse tree shape detected.\")\n"
						+ "\n"
						+ "def main(argv):\n"
						+ "    input = FileStream(argv[1])\n"
						+ "    lexer = <lexerName>(input)\n"
						+ "    stream = CommonTokenStream(lexer)\n"
						+ "<createParser>"
						+ "    parser.buildParseTrees = True\n"
						+ "    tree = parser.<parserStartRuleName>\n"
						+ "    ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree)\n"
						+ "\n" + "if __name__ == '__main__':\n"
						+ "    main(sys.argv)\n" + "\n");
		String stSource = "    parser = <parserName>(stream)\n";
		if (debug)
			stSource += "    parser.addErrorListener(DiagnosticErrorListener())\n";
		if (trace)
			stSource += "    parser.setTrace(True)\n";
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
