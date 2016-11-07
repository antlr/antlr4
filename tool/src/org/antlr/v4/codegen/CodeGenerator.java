/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2016 Mike Lischke
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

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.model.OutputModelObject;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STWriter;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

/** General controller for code gen.  Can instantiate sub generator(s).
 */
public class CodeGenerator {
	public static final String TEMPLATE_ROOT = "org/antlr/v4/tool/templates/codegen";
	public static final String VOCAB_FILE_EXTENSION = ".tokens";
	public static final String DEFAULT_LANGUAGE = "Java";
	public static final String vocabFilePattern =
		"<tokens.keys:{t | <t>=<tokens.(t)>\n}>" +
		"<literals.keys:{t | <t>=<literals.(t)>\n}>";


	public final Grammar g;

	public final Tool tool;

	public final String language;

	private Target target;

	public int lineWidth = 72;

	private CodeGenerator(String language) {
		this.g = null;
		this.tool = null;
		this.language = language;
	}

	public CodeGenerator(Grammar g) {
		this(g.tool, g, g.getOptionString("language"));
	}

	public CodeGenerator(Tool tool, Grammar g, String language) {
		this.g = g;
		this.tool = tool;
		this.language = language != null ? language : DEFAULT_LANGUAGE;
	}

	public static boolean targetExists(String language) {
		String targetName = "org.antlr.v4.codegen.target."+language+"Target";
		try {
			Class<? extends Target> c = Class.forName(targetName).asSubclass(Target.class);
			Constructor<? extends Target> ctor = c.getConstructor(CodeGenerator.class);
			CodeGenerator gen = new CodeGenerator(language);
			Target target = ctor.newInstance(gen);
			return target.templatesExist();
		}
		catch (Exception e) { // ignore errors; we're detecting presence only
		}
		return false;
	}


	public Target getTarget() {
		if ( target == null && targetExists(language) ) {
			loadLanguageTarget(language);
		}
		return target;
	}


	public STGroup getTemplates() {
		Target t = getTarget();
		return t==null ? null : t.getTemplates();
	}

	protected void loadLanguageTarget(String language) {
		String targetName = "org.antlr.v4.codegen.target."+language+"Target";
		try {
			Class<? extends Target> c = Class.forName(targetName).asSubclass(Target.class);
			Constructor<? extends Target> ctor = c.getConstructor(CodeGenerator.class);
			target = ctor.newInstance(this);
		}
		catch (Exception e) {
			tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
						 e,
						 targetName);
		}
	}

	// CREATE TEMPLATES BY WALKING MODEL

	private OutputModelController createController() {
		OutputModelFactory factory = new ParserFactory(this);
		OutputModelController controller = new OutputModelController(factory);
		factory.setController(controller);
		return controller;
	}

	private ST walk(OutputModelObject outputModel, boolean header) {
		OutputModelWalker walker = new OutputModelWalker(tool, getTemplates());
		return walker.walk(outputModel, header);
	}

	public ST generateLexer() { return generateLexer(false); }
	public ST generateLexer(boolean header) { return walk(createController().buildLexerOutputModel(header), header); }

	public ST generateParser() { return generateParser(false); }
	public ST generateParser(boolean header) { return walk(createController().buildParserOutputModel(header), header); }

	public ST generateListener() { return generateListener(false); }
	public ST generateListener(boolean header) { return walk(createController().buildListenerOutputModel(header), header); }

	public ST generateBaseListener() { return generateBaseListener(false); }
	public ST generateBaseListener(boolean header) { return walk(createController().buildBaseListenerOutputModel(header), header); }

	public ST generateVisitor() { return generateVisitor(false); }
	public ST generateVisitor(boolean header) { return walk(createController().buildVisitorOutputModel(header), header); }

	public ST generateBaseVisitor() { return generateBaseVisitor(false); }
	public ST generateBaseVisitor(boolean header) { return walk(createController().buildBaseVisitorOutputModel(header), header); }

	/** Generate a token vocab file with all the token names/types.  For example:
	 *  ID=7
	 *  FOR=8
	 *  'for'=8
	 *
	 *  This is independent of the target language; used by antlr internally
	 */
	ST getTokenVocabOutput() {
		ST vocabFileST = new ST(vocabFilePattern);
		Map<String,Integer> tokens = new LinkedHashMap<String,Integer>();
		// make constants for the token names
		for (String t : g.tokenNameToTypeMap.keySet()) {
			int tokenType = g.tokenNameToTypeMap.get(t);
			if ( tokenType>=Token.MIN_USER_TOKEN_TYPE) {
				tokens.put(t, tokenType);
			}
		}
		vocabFileST.add("tokens", tokens);

		// now dump the strings
		Map<String,Integer> literals = new LinkedHashMap<String,Integer>();
		for (String literal : g.stringLiteralToTypeMap.keySet()) {
			int tokenType = g.stringLiteralToTypeMap.get(literal);
			if ( tokenType>=Token.MIN_USER_TOKEN_TYPE) {
				literals.put(literal, tokenType);
			}
		}
		vocabFileST.add("literals", literals);

		return vocabFileST;
	}

	public void writeRecognizer(ST outputFileST, boolean header) {
		getTarget().genFile(g, outputFileST, getRecognizerFileName(header));
	}

	public void writeListener(ST outputFileST, boolean header) {
		getTarget().genFile(g, outputFileST, getListenerFileName(header));
	}

	public void writeBaseListener(ST outputFileST, boolean header) {
		getTarget().genFile(g, outputFileST, getBaseListenerFileName(header));
	}

	public void writeVisitor(ST outputFileST, boolean header) {
		getTarget().genFile(g, outputFileST, getVisitorFileName(header));
	}

	public void writeBaseVisitor(ST outputFileST, boolean header) {
		getTarget().genFile(g, outputFileST, getBaseVisitorFileName(header));
	}

	public void writeVocabFile() {
		// write out the vocab interchange file; used by antlr,
		// does not change per target
		ST tokenVocabSerialization = getTokenVocabOutput();
		String fileName = getVocabFileName();
		if ( fileName!=null ) {
			getTarget().genFile(g, tokenVocabSerialization, fileName);
		}
	}

	public void write(ST code, String fileName) {
		try {
//			long start = System.currentTimeMillis();
			Writer w = tool.getOutputFileWriter(g, fileName);
			STWriter wr = new AutoIndentWriter(w);
			wr.setLineWidth(lineWidth);
			code.write(wr);
			w.close();
//			long stop = System.currentTimeMillis();
		}
		catch (IOException ioe) {
			tool.errMgr.toolError(ErrorType.CANNOT_WRITE_FILE,
								  ioe,
								  fileName);
		}
	}

	public String getRecognizerFileName() { return getRecognizerFileName(false); }
	public String getListenerFileName() { return getListenerFileName(false); }
	public String getVisitorFileName() { return getVisitorFileName(false); }
	public String getBaseListenerFileName() { return getBaseListenerFileName(false); }
	public String getBaseVisitorFileName() { return getBaseVisitorFileName(false); }

	public String getRecognizerFileName(boolean header) { return getTarget().getRecognizerFileName(header); }
	public String getListenerFileName(boolean header) { return getTarget().getListenerFileName(header); }
	public String getVisitorFileName(boolean header) { return getTarget().getVisitorFileName(header); }
	public String getBaseListenerFileName(boolean header) { return getTarget().getBaseListenerFileName(header); }
	public String getBaseVisitorFileName(boolean header) { return getTarget().getBaseVisitorFileName(header); }

	/** What is the name of the vocab file generated for this grammar?
	 *  Returns null if no .tokens file should be generated.
	 */
	public String getVocabFileName() {
		return g.name+VOCAB_FILE_EXTENSION;
	}

	public String getHeaderFileName() {
		ST extST = getTemplates().getInstanceOf("headerFileExtension");
		if ( extST==null ) return null;
		String recognizerName = g.getRecognizerName();
		return recognizerName+extST.render();
	}

}
