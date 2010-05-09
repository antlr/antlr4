package org.antlr.v4.codegen;

import org.antlr.v4.codegen.src.OutputModelObject;
import org.antlr.v4.codegen.src.Parser;
import org.antlr.v4.codegen.src.ParserFile;
import org.antlr.v4.tool.Grammar;

/** */
public class ParserGenerator extends CodeGenerator {
//	public static final Map<Class, String> modelToTemplateMap = new HashMap<Class, String>() {{
//		put(ParserFile.class, "parserFile");
//		put(Parser.class, "parser");
//		put(RuleFunction.class, "parserFunction");
//		put(DFADef.class, "DFA");
//		put(CodeBlock.class, "codeBlock");
//		put(LL1Choice.class, "switch");
//		put(MatchToken.class, "matchToken");
//	}};

	public ParserGenerator(Grammar g) {
		super(g);
	}

	public OutputModelObject buildOutputModel() {
		ParserFile pf = new ParserFile(this, getRecognizerFileName());
		outputModel = pf;
		pf.parser = new Parser(this, pf); // side-effect: fills pf dfa and bitset defs
		// at this point, model is built
		return outputModel;
	}
}
