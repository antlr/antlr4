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

//		for (Rule r : g.rules.values()) {
//			for (int i=1; i<=r.numberOfAlts; i++) {
//				Alternative alt = r.alt[i];
//				for (String ref : alt.tokenRefs.keySet()) {
//					if ( alt.tokenRefsInActions.get(ref)!=null ) {
//						String label = gen.target.getImplicitTokenLabel(ast);
//						alt.implicitTokenLabels.put(, label);
//					}
//				}
//			}
//		}

		ST outputFileST = gen.generate();
		gen.write(outputFileST);

//		if ( g.isLexer() ) processLexer();
//		else if ( g.isParser() ) processParser();
	}

	void processParser() {
		CodeGenerator gen = new CodeGenerator(g);
		ST outputFileST = gen.generate();
		gen.write(outputFileST);

	}

	void processLexer() {
		CodeGenerator gen = new CodeGenerator(g);
		ST outputFileST = gen.generate();
		gen.write(outputFileST);

	}
}
