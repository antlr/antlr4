package org.antlr.v4.test.runtime.python2;

import org.antlr.v4.test.runtime.python.BasePythonTest;
import org.stringtemplate.v4.ST;

public abstract class BasePython2Test extends BasePythonTest {

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
		if(debug)
			stSource += "    parser.addErrorListener(DiagnosticErrorListener())\n";
		if(trace)
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
