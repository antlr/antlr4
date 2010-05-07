package org.antlr.v4.codegen;

import org.antlr.v4.codegen.src.OutputModelObject;
import org.antlr.v4.codegen.src.Parser;
import org.antlr.v4.codegen.src.ParserFile;
import org.antlr.v4.codegen.src.RuleFunction;
import org.antlr.v4.tool.Grammar;

import java.util.HashMap;
import java.util.Map;


/** */
public class ParserGenerator extends CodeGenerator {
	public static final Map<Class, String> modelToTemplateMap = new HashMap<Class, String>() {{
		put(ParserFile.class, "parserFile");
		put(Parser.class, "parser");
		put(RuleFunction.class, "parserFunction");
	}};

	public ParserGenerator(Grammar g) {
		super(g);
	}

	public OutputModelObject buildOutputModel() {
		Parser p = new Parser(g);
		ParserFile f = new ParserFile(p, getRecognizerFileName());

		return f;
	}
}
