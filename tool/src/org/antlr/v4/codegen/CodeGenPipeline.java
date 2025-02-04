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
				ST lexer = gen.generateLexer(GenFileType.HEADER); // Header file if needed.
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(lexer, gen, GenFileType.HEADER);
				}
			}
			ST lexer = gen.generateLexer(GenFileType.SOURCE);
			if (g.tool.errMgr.getNumErrors() == errorCount) {
				writeRecognizer(lexer, gen, GenFileType.SOURCE);
			}
		}
		else {
			if (gen.getTarget().needsHeader()) {
				ST parser = gen.generateParser(GenFileType.HEADER);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, GenFileType.HEADER);
				}
			}
			if ( gen.getTarget().supportsSplitContext() && g.tool.gen_split_context ) {
				ST parser = gen.generateParser(GenFileType.SOURCE_PARSER);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, GenFileType.SOURCE_PARSER);
				}
				parser = gen.generateParser(GenFileType.SOURCE_CONTEXTS);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, GenFileType.SOURCE_CONTEXTS);
				}
			} else {
				ST parser = gen.generateParser(GenFileType.SOURCE);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen, GenFileType.SOURCE);
				}

			}

			if ( g.tool.gen_listener ) {
				if (gen.getTarget().needsHeader()) {
					ST listener = gen.generateListener(GenFileType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeListener(listener, GenFileType.HEADER);
					}
				}
				ST listener = gen.generateListener(GenFileType.SOURCE);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					gen.writeListener(listener, GenFileType.SOURCE);
				}

				if (gen.getTarget().needsHeader()) {
					ST baseListener = gen.generateBaseListener(GenFileType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeBaseListener(baseListener, GenFileType.HEADER);
					}
				}
				if (gen.getTarget().wantsBaseListener()) {
					ST baseListener = gen.generateBaseListener(GenFileType.SOURCE);
					if ( g.tool.errMgr.getNumErrors()==errorCount ) {
						gen.writeBaseListener(baseListener, GenFileType.SOURCE);
					}
				}
			}
			if ( g.tool.gen_visitor ) {
				if (gen.getTarget().needsHeader()) {
					ST visitor = gen.generateVisitor(GenFileType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeVisitor(visitor, GenFileType.HEADER);
					}
				}
				ST visitor = gen.generateVisitor(GenFileType.SOURCE);
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					gen.writeVisitor(visitor, GenFileType.SOURCE);
				}

				if (gen.getTarget().needsHeader()) {
					ST baseVisitor = gen.generateBaseVisitor(GenFileType.HEADER);
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeBaseVisitor(baseVisitor, GenFileType.HEADER);
					}
				}
				if (gen.getTarget().wantsBaseVisitor()) {
					ST baseVisitor = gen.generateBaseVisitor(GenFileType.SOURCE);
					if ( g.tool.errMgr.getNumErrors()==errorCount ) {
						gen.writeBaseVisitor(baseVisitor, GenFileType.SOURCE);
					}
				}
			}
		}
		gen.writeVocabFile();
	}

	protected void writeRecognizer(ST template, CodeGenerator gen, GenFileType fileType) {
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

		gen.writeRecognizer(template, fileType);
	}
}
