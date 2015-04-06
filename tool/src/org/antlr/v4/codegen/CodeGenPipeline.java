/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.gui.STViz;

import java.util.List;

public class CodeGenPipeline {
	Grammar g;

	public CodeGenPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		if ( !CodeGenerator.targetExists(g.getOptionString("language")) ) return;

		CodeGenerator gen = new CodeGenerator(g);
		IntervalSet idTypes = new IntervalSet();
		idTypes.add(ANTLRParser.ID);
		idTypes.add(ANTLRParser.RULE_REF);
		idTypes.add(ANTLRParser.TOKEN_REF);
		List<GrammarAST> idNodes = g.ast.getNodesWithType(idTypes);
		for (GrammarAST idNode : idNodes) {
			if ( gen.getTarget().grammarSymbolCausesIssueInGeneratedCode(idNode) ) {
				g.tool.errMgr.grammarError(ErrorType.USE_OF_BAD_WORD,
										   g.fileName, idNode.getToken(),
										   idNode.getText());
			}
		}

		// all templates are generated in memory to report the most complete
		// error information possible, but actually writing output files stops
		// after the first error is reported
		int errorCount = g.tool.errMgr.getNumErrors();

		if ( g.isLexer() ) {
			if (gen.getTarget().wantsRecognizerCodeFile()) {
				ST lexer = gen.generateLexer();
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(lexer, gen);
				}
			}

			if (gen.getTarget().wantsRecognizerHeaderFile()) {
				ST lexerHeader = gen.generateLexerHeader();
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					gen.writeRecognizerHeader(lexerHeader);
				}
			}
		}
		else {
			if (gen.getTarget().wantsRecognizerCodeFile()) {
				ST parser = gen.generateParser();
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					writeRecognizer(parser, gen);
				}
			}

			if (gen.getTarget().wantsRecognizerHeaderFile()) {
				ST parserHeader = gen.generateParserHeader();
				if (g.tool.errMgr.getNumErrors() == errorCount) {
					gen.writeRecognizerHeader(parserHeader);
				}
			}

			if ( g.tool.gen_listener ) {
				if (gen.getTarget().wantsListenerCodeFile()) {
					ST listener = gen.generateListener();
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeListener(listener);
					}
				}

				if (gen.getTarget().wantsListenerHeaderFile()) {
					ST listenerHeader = gen.generateListenerHeader();
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeListenerHeader(listenerHeader);
					}
				}

				if (gen.getTarget().wantsBaseListener()) {
					if (gen.getTarget().wantsBaseListenerCodeFile()) {
						ST baseListener = gen.generateBaseListener();
						if (g.tool.errMgr.getNumErrors() == errorCount) {
							gen.writeBaseListener(baseListener);
						}
					}

					if (gen.getTarget().wantsBaseListenerHeaderFile()){
						ST baseListenerHeader = gen.generateBaseListenerHeader();
						if (g.tool.errMgr.getNumErrors() == errorCount) {
							gen.writeBaseListenerHeader(baseListenerHeader);
						}
					}
				}
			}
			if ( g.tool.gen_visitor ) {
				if (gen.getTarget().wantsVisitorCodeFile()) {
					ST visitor = gen.generateVisitor();
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeVisitor(visitor);
					}
				}
				
				if (gen.getTarget().wantsVisitorHeaderFile()) {
					ST visitorHeader = gen.generateVisitorHeader();
					if (g.tool.errMgr.getNumErrors() == errorCount) {
						gen.writeVisitorHeader(visitorHeader);
					}
				}

				if (gen.getTarget().wantsBaseVisitor()) {
					if (gen.getTarget().wantsBaseVisitorCodeFile()) {
						ST baseVisitor = gen.generateBaseVisitor();
						if (g.tool.errMgr.getNumErrors() == errorCount) {
							gen.writeBaseVisitor(baseVisitor);
						}
					}
					
					if (gen.getTarget().wantsBaseVisitorHeaderFile()) {
						ST baseVisitorHeader = gen.generateBaseVisitorHeader();
						if (g.tool.errMgr.getNumErrors() == errorCount) {
							gen.writeBaseVisitorHeader(baseVisitorHeader);
						}
					}
				}
			}
		}
		gen.writeVocabFile();
	}

	protected void writeRecognizer(ST template, CodeGenerator gen) {
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

		gen.writeRecognizer(template);
	}
}
