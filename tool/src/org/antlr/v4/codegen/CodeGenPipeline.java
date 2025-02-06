/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.codegen;

import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.gui.STViz;

public class CodeGenPipeline {
	final Grammar g;
	final CodeGenerator gen;

	public CodeGenPipeline(Grammar g, CodeGenerator gen) {
		this.g = g;
		this.gen = gen;
	}

	public void process() {
		// all templates are generated in memory to report the most complete
		// error information possible, but actually writing output files stops
		// after the first error is reported
		int errorCount = g.tool.errMgr.getNumErrors();

		if ( g.isLexer() ) {
			if (gen.getTarget().needsHeader()) {
				ST lexer = gen.generateLexer(SourceType.HEADER); // Header file if needed.
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(lexer, gen, SourceType.HEADER);
				}
			}
			ST lexer = gen.generateLexer(SourceType.SOURCE);
			if (g.tool.errMgr.getNumErrors() == errorCount) {
				writeRecognizer(lexer, gen, SourceType.SOURCE);
			}
		}
		else {
			if (gen.getTarget().needsHeader()) {
				ST parser = gen.generateParser(SourceType.HEADER);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, SourceType.HEADER);
				}
			}
			if ( gen.getTarget().supportsSplitParser() && g.tool.gen_split_parser) {
				ST parser = gen.generateParser(SourceType.SOURCE_LEAN);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, SourceType.SOURCE_LEAN);
				}
				parser = gen.generateParser(SourceType.SOURCE_CONTEXTS);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, SourceType.SOURCE_CONTEXTS);
				}
				parser = gen.generateParser(SourceType.SOURCE_DFA);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, SourceType.SOURCE_DFA);
				}
			} else {
				ST parser = gen.generateParser(SourceType.SOURCE);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, SourceType.SOURCE);
				}

			}

			SourceType sourceType = gen.getTarget().supportsSplitParser() && g.tool.gen_split_parser ? SourceType.SOURCE_LEAN : SourceType.SOURCE;

			if ( g.tool.gen_listener ) {
				if (gen.getTarget().needsHeader()) {
					ST listener = gen.generateListener(SourceType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeListener(listener, SourceType.HEADER);
					}
				}
				ST listener = gen.generateListener(sourceType);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					gen.writeListener(listener, sourceType);
				}

				if (gen.getTarget().needsHeader()) {
					ST baseListener = gen.generateBaseListener(SourceType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeBaseListener(baseListener, SourceType.HEADER);
					}
				}
				if (gen.getTarget().wantsBaseListener()) {
					ST baseListener = gen.generateBaseListener(sourceType);
					if ( g.tool.errMgr.getNumErrors()==errorCount ) {
						gen.writeBaseListener(baseListener, sourceType);
					}
				}
			}
			if ( g.tool.gen_visitor ) {
				if (gen.getTarget().needsHeader()) {
					ST visitor = gen.generateVisitor(SourceType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeVisitor(visitor, SourceType.HEADER);
					}
				}
				ST visitor = gen.generateVisitor(sourceType);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					gen.writeVisitor(visitor, sourceType);
				}

				if (gen.getTarget().needsHeader()) {
					ST baseVisitor = gen.generateBaseVisitor(SourceType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeBaseVisitor(baseVisitor, SourceType.HEADER);
					}
				}
				if (gen.getTarget().wantsBaseVisitor()) {
					ST baseVisitor = gen.generateBaseVisitor(sourceType);
					if ( g.tool.errMgr.getNumErrors()==errorCount ) {
						gen.writeBaseVisitor(baseVisitor, sourceType);
					}
				}
			}
		}
		gen.writeVocabFile();
	}

	protected void writeRecognizer(ST template, CodeGenerator gen, SourceType sourceType) {
		if ( g.tool.launch_ST_inspector ) {
			STViz viz = template.inspect();
			if (g.tool.ST_inspector_wait_for_close) {
				try {
					viz.waitForClose();
				}
				catch (InterruptedException ex) {
					g.tool.errMgr.toolError(ErrorType.INTERNAL_ERROR, ex);
				}
			}
		}

		gen.writeRecognizer(template, sourceType);
	}
}
