/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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
package org.antlr.v4.test.tool;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.automata.ATNFactory;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.semantics.SemanticPipeline;
import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.test.runtime.java.ErrorQueue;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.InstanceScope;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STWriter;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class TestCodeGeneration extends BaseTest {
	@Test public void testArgDecl() throws Exception { // should use template not string
		/*ErrorQueue equeue = */new ErrorQueue();
		String g =
				"grammar T;\n" +
				"a[int xyz] : 'a' ;\n";
		List<String> evals = getEvalInfoForString(g, "int xyz");
		System.out.println(evals);
		for (int i = 0; i < evals.size(); i++) {
			String eval = evals.get(i);
			assertFalse("eval should not be POJO: "+eval, eval.startsWith("<pojo:"));
		}
	}

	/** Add tags around each attribute/template/value write */
	public static class DebugInterpreter extends Interpreter {
		List<String> evals = new ArrayList<String>();
		ErrorManager myErrMgrCopy;
		int tab = 0;
		public DebugInterpreter(STGroup group, ErrorManager errMgr, boolean debug) {
			super(group, errMgr, debug);
			myErrMgrCopy = errMgr;
		}

		@Override
		protected int writeObject(STWriter out, InstanceScope scope, Object o, String[] options) {
			if ( o instanceof ST ) {
				String name = ((ST)o).getName();
				name = name.substring(1);
				if ( !name.startsWith("_sub") ) {
					try {
						out.write("<ST:" + name + ">");
						evals.add("<ST:" + name + ">");
						int r = super.writeObject(out, scope, o, options);
						out.write("</ST:" + name + ">");
						evals.add("</ST:" + name + ">");
						return r;
					} catch (IOException ioe) {
						myErrMgrCopy.IOError(scope.st, ErrorType.WRITE_IO_ERROR, ioe);
					}
				}
			}
			return super.writeObject(out, scope, o, options);
		}

		@Override
		protected int writePOJO(STWriter out, InstanceScope scope, Object o, String[] options) throws IOException {
			Class<?> type = o.getClass();
			String name = type.getSimpleName();
			out.write("<pojo:"+name+">"+o.toString()+"</pojo:"+name+">");
			evals.add("<pojo:" + name + ">" + o.toString() + "</pojo:" + name + ">");
			return super.writePOJO(out, scope, o, options);
		}

		public void indent(STWriter out) throws IOException {
			for (int i=1; i<=tab; i++) {
				out.write("\t");
			}
		}
	}

	public List<String> getEvalInfoForString(String grammarString, String pattern) throws RecognitionException {
		ErrorQueue equeue = new ErrorQueue();
		Grammar g = new Grammar(grammarString);
		List<String> evals = new ArrayList<String>();
		if ( g.ast!=null && !g.ast.hasErrors ) {
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();

			ATNFactory factory = new ParserATNFactory(g);
			if (g.isLexer()) factory = new LexerATNFactory((LexerGrammar) g);
			g.atn = factory.createATN();

			CodeGenerator gen = new CodeGenerator(g);
			ST outputFileST = gen.generateParser();

//			STViz viz = outputFileST.inspect();
//			try {
//				viz.waitForClose();
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}

			boolean debug = false;
			DebugInterpreter interp =
					new DebugInterpreter(outputFileST.groupThatCreatedThisInstance,
							outputFileST.impl.nativeGroup.errMgr,
							debug);
			InstanceScope scope = new InstanceScope(null, outputFileST);
			StringWriter sw = new StringWriter();
			AutoIndentWriter out = new AutoIndentWriter(sw);
			interp.exec(out, scope);

			for (String e : interp.evals) {
				if (e.contains(pattern)) {
					evals.add(e);
				}
			}
		}
		if ( equeue.size()>0 ) {
			System.err.println(equeue.toString());
		}
		return evals;
	}
}
