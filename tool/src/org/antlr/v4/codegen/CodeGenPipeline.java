/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen;

import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.ST;

import java.util.List;

public class CodeGenPipeline {
	Grammar g;

	public CodeGenPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		CodeGenerator gen = new CodeGenerator(g);

		IntervalSet idTypes = new IntervalSet();
		idTypes.add(ANTLRParser.ID);
		idTypes.add(ANTLRParser.RULE_REF);
		idTypes.add(ANTLRParser.TOKEN_REF);
		List<GrammarAST> idNodes = g.ast.getNodesWithType(idTypes);
		for (GrammarAST idNode : idNodes) {
			if ( gen.target.grammarSymbolCausesIssueInGeneratedCode(idNode) ) {
				g.tool.errMgr.grammarError(ErrorType.USE_OF_BAD_WORD,
										   g.fileName, idNode.getToken(),
										   idNode.getText());
			}
		}

		if ( gen.templates==null ) return;

		if ( g.isLexer() ) {
			ST lexer = gen.generateLexer();
			if ( g.tool.launch_ST_inspector ) lexer.inspect();
			gen.writeRecognizer(lexer);
		}
		else {
			ST parser = gen.generateParser();
			if ( g.tool.launch_ST_inspector ) parser.inspect();
			gen.writeRecognizer(parser);
			if ( g.tool.gen_listener ) {
				gen.writeListener(gen.generateListener());
				gen.writeBaseListener(gen.generateBaseListener());
			}
			if ( g.tool.gen_visitor ) {
				gen.writeVisitor(gen.generateVisitor());
				gen.writeBaseVisitor(gen.generateBaseVisitor());
			}
			gen.writeHeaderFile();
		}
		gen.writeVocabFile();
	}
}
