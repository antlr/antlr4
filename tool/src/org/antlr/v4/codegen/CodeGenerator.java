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

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.model.OutputModelObject;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.antlr.v4.misc.Utils.Func1;

/** General controller for code gen.  Can instantiate sub generator(s).
 */
public class CodeGenerator {
	public static final String TEMPLATE_ROOT = "org/antlr/v4/tool/templates/codegen";
	public static final String VOCAB_FILE_EXTENSION = ".tokens";
	public final static String vocabFilePattern =
		"<tokens.keys:{t | <t>=<tokens.(t)>\n}>" +
		"<literals.keys:{t | <t>=<literals.(t)>\n}>";

	@NotNull
	public final Grammar g;
	@NotNull
	public final Tool tool;

	public Target target;
	public STGroup templates;

	public int lineWidth = 72;

	public CodeGenerator(@NotNull Grammar g) {
		this(g.tool, g, g.getOptionString("language", "Java"));
	}

	public CodeGenerator(@NotNull Tool tool, @NotNull Grammar g, String language) {
		this.g = g;
		this.tool = tool;
		loadLanguageTarget(language);
		loadTemplates(language);
	}

	void loadLanguageTarget(String language) {
		String targetName = "org.antlr.v4.codegen."+language+"Target";
		try {
			Class<? extends Target> c = Class.forName(targetName).asSubclass(Target.class);
			Constructor<? extends Target> ctor = c.getConstructor(CodeGenerator.class);
			target = ctor.newInstance(this);
		}
		catch (ClassNotFoundException cnfe) {
			target = new Target(this); // use default
		}
		catch (NoSuchMethodException nsme) {
			target = new Target(this); // use default
		}
		catch (InvocationTargetException ite) {
			tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
						 ite,
						 targetName);
		}
		catch (InstantiationException ie) {
			tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
						 ie,
						 targetName);
		}
		catch (IllegalAccessException cnfe) {
			tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
						 cnfe,
						 targetName);
		}
	}

	public void loadTemplates(String language) {
		try {
			templates = new STGroupFile(TEMPLATE_ROOT+"/"+language+"/"+language+".stg");
			templates.registerRenderer(Integer.class, new NumberRenderer());
			templates.registerRenderer(String.class, new StringRenderer());
		}
		catch (IllegalArgumentException iae) {
			tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
									iae,
						 			language);
		}
	}

	// CREATE TEMPLATES BY WALKING MODEL

	private OutputModelController createController() {
		OutputModelFactory factory = new ParserFactory(this);
		OutputModelController controller = new OutputModelController(factory);
		factory.setController(controller);
		return controller;
	}

	private ST walk(OutputModelObject outputModel) {
		OutputModelWalker walker = new OutputModelWalker(tool, templates);
		return walker.walk(outputModel);
	}

	public ST generateLexer() { return walk(createController().buildLexerOutputModel()); }
	public ST generateParser() { return walk(createController().buildParserOutputModel()); }
	public ST generateListener() { return walk(createController().buildListenerOutputModel()); }
	public ST generateBaseListener() { return walk(createController().buildBaseListenerOutputModel()); }
	public ST generateParseListener() { return walk(createController().buildParseListenerOutputModel()); }
	public ST generateBaseParseListener() { return walk(createController().buildBaseParseListenerOutputModel()); }
	public ST generateVisitor() { return walk(createController().buildVisitorOutputModel()); }
	public ST generateBaseVisitor() { return walk(createController().buildBaseVisitorOutputModel()); }

	/** Generate a token vocab file with all the token names/types.  For example:
	 *  ID=7
	 *  FOR=8
	 *  'for'=8
	 *
	 *  This is independent of the target language; used by antlr internally
	 */
	ST getTokenVocabOutput() {
		ST vocabFileST = new ST(vocabFilePattern);
		Map<String,Integer> tokens = new HashMap<String,Integer>();
		// make constants for the token names
		for (String t : g.tokenNameToTypeMap.keySet()) {
			int tokenType = g.tokenNameToTypeMap.get(t);
			if ( tokenType>=Token.MIN_USER_TOKEN_TYPE) {
				tokens.put(t, tokenType);
			}
		}
		vocabFileST.add("tokens", tokens);

		// now dump the strings
		Map<String,Integer> literals = new HashMap<String,Integer>();
		for (String literal : g.stringLiteralToTypeMap.keySet()) {
			int tokenType = g.stringLiteralToTypeMap.get(literal);
			if ( tokenType>=Token.MIN_USER_TOKEN_TYPE) {
				literals.put(literal, tokenType);
			}
		}
		vocabFileST.add("literals", literals);

		return vocabFileST;
	}

	public void writeRecognizer(ST outputFileST) {
		target.genFile(g, outputFileST, getRecognizerFileName());
	}

	public void writeListener(ST outputFileST) {
		target.genFile(g,outputFileST, getListenerFileName());
	}

	public void writeBaseListener(ST outputFileST) {
		target.genFile(g,outputFileST, getBaseListenerFileName());
	}

	public void writeParseListener(ST outputFileST) {
		target.genFile(g,outputFileST, getParseListenerFileName());
	}

	public void writeBaseParseListener(ST outputFileST) {
		target.genFile(g,outputFileST, getBaseParseListenerFileName());
	}

	public void writeVisitor(ST outputFileST) {
		target.genFile(g,outputFileST, getVisitorFileName());
	}

	public void writeBaseVisitor(ST outputFileST) {
		target.genFile(g,outputFileST, getBaseVisitorFileName());
	}

	public void writeHeaderFile() {
		String fileName = getHeaderFileName();
		if ( fileName==null ) return;
		if ( templates.isDefined("headerFile") ) {
			ST extST = templates.getInstanceOf("headerFileExtension");
			ST headerFileST = null;
			// TODO:  don't hide this header file generation here!
			target.genRecognizerHeaderFile(g,headerFileST,extST.render(lineWidth));
		}
	}

	public void writeVocabFile() {
		// write out the vocab interchange file; used by antlr,
		// does not change per target
		ST tokenVocabSerialization = getTokenVocabOutput();
		String fileName = getVocabFileName();
		if ( fileName!=null ) {
			write(tokenVocabSerialization, fileName);
		}
	}

//	public void write(ST outputFileST) {
//		// WRITE FILES
//		String fileName = "unknown";
//		try {
//			fileName = getRecognizerFileName();
//			target.genRecognizerFile(g,outputFileST);
//			writeHeaderFile();
//			// write out the vocab interchange file; used by antlr,
//			// does not change per target
//			ST tokenVocabSerialization = getTokenVocabOutput();
//			fileName = getVocabFileName();
//			if ( fileName!=null ) {
//				write(tokenVocabSerialization, fileName);
//			}
//		}
//		catch (IOException ioe) {
//			tool.errMgr.toolError(ErrorType.CANNOT_WRITE_FILE,
//									ioe,
//									fileName);
//		}
//	}

	public void write(ST code, String fileName) {
		try {
			long start = System.currentTimeMillis();
			Writer w = tool.getOutputFileWriter(g, fileName);
			STWriter wr = new AutoIndentWriter(w);
			wr.setLineWidth(lineWidth);
			code.write(wr);
			w.close();
			long stop = System.currentTimeMillis();
		}
		catch (IOException ioe) {
			tool.errMgr.toolError(ErrorType.CANNOT_WRITE_FILE,
								  ioe,
								  fileName);
		}
	}

	/** Generate TParser.java and TLexer.java from T.g4 if combined, else
	 *  just use T.java as output regardless of type.
	 */
	public String getRecognizerFileName() {
		ST extST = templates.getInstanceOf("codeFileExtension");
		String recognizerName = g.getRecognizerName();
		return recognizerName+extST.render();
	}

	/** A given grammar T, return the listener name such as
	 *  TListener.java, if we're using the Java target.
 	 */
	public String getListenerFileName() {
		assert g.name != null;
		ST extST = templates.getInstanceOf("codeFileExtension");
		String listenerName = g.name + "Listener";
		return listenerName+extST.render();
	}

	/** A given grammar T, return the visitor name such as
	 *  TVisitor.java, if we're using the Java target.
 	 */
	public String getVisitorFileName() {
		assert g.name != null;
		ST extST = templates.getInstanceOf("codeFileExtension");
		String listenerName = g.name + "Visitor";
		return listenerName+extST.render();
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseListenerFileName() {
		assert g.name != null;
		ST extST = templates.getInstanceOf("codeFileExtension");
		String listenerName = g.name + "BaseListener";
		return listenerName+extST.render();
	}

	public String getParseListenerFileName() {
		assert g.name != null;
		ST extST = templates.getInstanceOf("codeFileExtension");
		String listenerName = g.name + "ParseListener";
		return listenerName+extST.render();
	}

	public String getBaseParseListenerFileName() {
		assert g.name != null;
		ST extST = templates.getInstanceOf("codeFileExtension");
		String listenerName = g.name + "BaseParseListener";
		return listenerName+extST.render();
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseVisitorFileName() {
		assert g.name != null;
		ST extST = templates.getInstanceOf("codeFileExtension");
		String listenerName = g.name + "BaseVisitor";
		return listenerName+extST.render();
	}

	/** What is the name of the vocab file generated for this grammar?
	 *  Returns null if no .tokens file should be generated.
	 */
	public String getVocabFileName() {
		return g.name+VOCAB_FILE_EXTENSION;
	}

	public String getHeaderFileName() {
		ST extST = templates.getInstanceOf("headerFileExtension");
		if ( extST==null ) return null;
		String recognizerName = g.getRecognizerName();
		return recognizerName+extST.render();
	}
}
