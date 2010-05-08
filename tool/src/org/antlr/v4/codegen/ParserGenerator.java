package org.antlr.v4.codegen;

import org.antlr.v4.codegen.src.*;
import org.antlr.v4.tool.Grammar;

import java.util.HashMap;
import java.util.Map;


/** */
public class ParserGenerator extends CodeGenerator {
	public static final Map<Class, String> modelToTemplateMap = new HashMap<Class, String>() {{
		put(ParserFile.class, "parserFile");
		put(Parser.class, "parser");
		put(RuleFunction.class, "parserFunction");
		put(DFADef.class, "DFA");
		put(CodeBlock.class, "codeBlock");
		put(LL1Choice.class, "switch");
		put(MatchToken.class, "matchToken");
	}};

	public ParserGenerator(Grammar g) {
		super(g);
	}

	public OutputModelObject buildOutputModel() {
		Parser p = new Parser(this);
		return new ParserFile(this, p, getRecognizerFileName());
	}
}
