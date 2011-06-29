package org.antlr.v4.codegen;

import org.antlr.v4.tool.Grammar;
import org.stringtemplate.v4.ST;

public class CodeGenPipeline {
	Grammar g;
	public CodeGenPipeline(Grammar g) {
		this.g = g;
	}
	public void process() {
		CodeGenerator gen = new CodeGenerator(g);

		ST outputFileST = gen.generate();
		gen.write(outputFileST);
	}
}
