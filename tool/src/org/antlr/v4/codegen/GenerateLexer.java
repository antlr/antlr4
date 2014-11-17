package org.antlr.v4.codegen;

import org.antlr.v4.Tool;

public class GenerateLexer {

	// used to generate lexer cs
	public static void main(String[] args) {
		String[] options = {
			"-Dlanguage=CSharp",
			"/Users/ericvergnaud/Development/antlr4/antlr/antlr4-csharp/runtime/CSharp/Antlr4.Runtime/Tree/Xpath/XPathLexer.g4"
		};
		Tool antlr = new Tool(options);
		antlr.processGrammarsOnCommandLine();
	}

}
