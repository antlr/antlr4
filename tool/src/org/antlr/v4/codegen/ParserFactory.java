package org.antlr.v4.codegen;

import org.antlr.v4.codegen.src.*;

import java.util.Stack;

/** */
public class ParserFactory extends OutputModelFactory {
//	public static final Map<Class, String> modelToTemplateMap = new HashMap<Class, String>() {{
//		put(ParserFile.class, "parserFile");
//		put(Parser.class, "parser");
//		put(RuleFunction.class, "parserFunction");
//		put(DFADef.class, "DFA");
//		put(CodeBlock.class, "codeBlock");
//		put(LL1Choice.class, "switch");
//		put(MatchToken.class, "matchToken");
//	}};

	// Context ptrs
	ParserFile file;
	Parser parser;
	Stack<RuleFunction> currentRule;

	public ParserFactory(CodeGenerator gen) {
		super(gen);
	}

	public OutputModelObject buildOutputModel() {
		root = file = new ParserFile(this, gen.getRecognizerFileName());
		file.parser = new Parser(this, file);

		// side-effect: fills pf dfa and bitset defs
		return file;
	}

	public ParserFile outputFile(String fileName) {
		return new ParserFile(this, fileName);
	}

	public Parser parser(ParserFile pf) {
		return new Parser(this, pf);
	}

	public void defineBitSet(BitSetDef b) { file.defineBitSet(b); }
}
