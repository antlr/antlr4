/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
	public static final String vocabFilePattern =
		"<tokens.keys:{t | <t>=<tokens.(t)>\n}>" +
		"<literals.keys:{t | <t>=<literals.(t)>\n}>";

	public final Grammar g;

	public final Tool tool;

	public final String language;

	private Target target;

	public int lineWidth = 72;

	public static CodeGenerator create(Grammar g) {
		return create(g.tool, g, g.getLanguage());
	}

	public static CodeGenerator create(Tool tool, Grammar g, String language) {
		String targetName = "org.antlr.v4.codegen.target."+language+"Target";
		try {
			Class<? extends Target> c = Class.forName(targetName).asSubclass(Target.class);
			Constructor<? extends Target> ctor = c.getConstructor(CodeGenerator.class);
			CodeGenerator codeGenerator = new CodeGenerator(tool, g, language);
			codeGenerator.target = ctor.newInstance(codeGenerator);
			return codeGenerator;
		}
		catch (Exception e) {
			g.tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR, e, language);
			return null;
		}
	}

	private CodeGenerator(Tool tool, Grammar g, String language) {
		this.g = g;
		this.tool = tool;
		this.language = language;
	}

	public Target getTarget() {
		return target;
	}

	public STGroup getTemplates() {
		return target.getTemplates();
	}

	// CREATE TEMPLATES BY WALKING MODEL

	private OutputModelController createController() {
		OutputModelFactory factory = new ParserFactory(this);
		OutputModelController controller = new OutputModelController(factory);
		factory.setController(controller);
		return controller;
	}

	private ST walk(OutputModelObject outputModel, GenFileType fileType) {
		OutputModelWalker walker = new OutputModelWalker(tool, getTemplates());
		return walker.walk(outputModel, fileType);
	}

	public ST generateLexer() { return generateLexer(GenFileType.SOURCE); }
	public ST generateLexer(GenFileType fileType) { return walk(createController().buildLexerOutputModel(fileType), fileType); }

	public ST generateParser() { return generateParser(GenFileType.SOURCE); }
	public ST generateParser(GenFileType fileType) { return walk(createController().buildParserOutputModel(fileType), fileType); }

	public ST generateListener() { return generateListener(GenFileType.SOURCE); }
	public ST generateListener(GenFileType fileType) { return walk(createController().buildListenerOutputModel(fileType), fileType); }

	public ST generateBaseListener() { return generateBaseListener(GenFileType.SOURCE); }
	public ST generateBaseListener(GenFileType fileType) { return walk(createController().buildBaseListenerOutputModel(fileType), fileType); }

	public ST generateVisitor() { return generateVisitor(GenFileType.SOURCE); }
	public ST generateVisitor(GenFileType fileType) { return walk(createController().buildVisitorOutputModel(fileType), fileType); }

	public ST generateBaseVisitor() { return generateBaseVisitor(GenFileType.SOURCE); }
	public ST generateBaseVisitor(GenFileType fileType) { return walk(createController().buildBaseVisitorOutputModel(fileType), fileType); }

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

	public void writeRecognizer(ST outputFileST, GenFileType fileType) {
		target.genFile(g, outputFileST, getRecognizerFileName(fileType));
	}

	public void writeListener(ST outputFileST, GenFileType fileType) {
		target.genFile(g, outputFileST, getListenerFileName(fileType));
	}

	public void writeBaseListener(ST outputFileST, GenFileType fileType) {
		target.genFile(g, outputFileST, getBaseListenerFileName(fileType));
	}

	public void writeVisitor(ST outputFileST, GenFileType fileType) {
		target.genFile(g, outputFileST, getVisitorFileName(fileType));
	}

	public void writeBaseVisitor(ST outputFileST, GenFileType fileType) {
		target.genFile(g, outputFileST, getBaseVisitorFileName(fileType));
	}

	public void writeVocabFile() {
		// write out the vocab interchange file; used by antlr,
		// does not change per target
		ST tokenVocabSerialization = getTokenVocabOutput();
		String fileName = getVocabFileName();
		if ( fileName!=null ) {
			target.genFile(g, tokenVocabSerialization, fileName);
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

	public String getRecognizerFileName() { return getRecognizerFileName(GenFileType.SOURCE); }
	public String getListenerFileName() { return getListenerFileName(GenFileType.SOURCE); }
	public String getVisitorFileName() { return getVisitorFileName(GenFileType.SOURCE); }
	public String getBaseListenerFileName() { return getBaseListenerFileName(GenFileType.SOURCE); }
	public String getBaseVisitorFileName() { return getBaseVisitorFileName(GenFileType.SOURCE); }

	public String getRecognizerFileName(GenFileType fileType) { return target.getRecognizerFileName(fileType); }
	public String getListenerFileName(GenFileType fileType) { return target.getListenerFileName(fileType); }
	public String getVisitorFileName(GenFileType fileType) { return target.getVisitorFileName(fileType); }
	public String getBaseListenerFileName(GenFileType fileType) { return target.getBaseListenerFileName(fileType); }
	public String getBaseVisitorFileName(GenFileType fileType) { return target.getBaseVisitorFileName(fileType); }

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
