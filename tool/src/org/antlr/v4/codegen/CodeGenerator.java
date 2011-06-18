package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.OutputModelObject;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.*;

import java.io.*;
import java.util.*;

/** General controller for code gen.  Can instantiate sub generator(s).
 */
public class CodeGenerator {
	public static boolean LAUNCH_ST_INSPECTOR = false;
	public static final String TEMPLATE_ROOT = "org/antlr/v4/tool/templates/codegen";
	public static final String VOCAB_FILE_EXTENSION = ".tokens";
	public final static String vocabFilePattern =
		"<tokens.keys:{t | <t>=<tokens.(t)>\n}>" +
		"<literals.keys:{t | <t>=<literals.(t)>\n}>";

	public Grammar g;
	public Target target;
	public STGroup templates;

	public int lineWidth = 72;

	public CodeGenerator(Grammar g) {
		this.g = g;
		String language = g.getOption("language", "Java");
		loadLanguageTarget(language);
		loadTemplates(language);
	}

	void loadLanguageTarget(String language) {
		String targetName = "org.antlr.v4.codegen."+language+"Target";
		try {
			Class c = Class.forName(targetName);
			target = (Target)c.newInstance();
		}
		catch (ClassNotFoundException cnfe) {
			target = new Target(); // use default
		}
		catch (InstantiationException ie) {
			g.tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
						 targetName,
						 ie);
		}
		catch (IllegalAccessException cnfe) {
			g.tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
						 targetName,
						 cnfe);
		}
	}

	public void loadTemplates(String language) {
		try {
			templates = new STGroupFile(TEMPLATE_ROOT+"/"+language+"/"+language+".stg");
			templates.registerRenderer(Integer.class, new NumberRenderer());
		}
		catch (IllegalArgumentException iae) {
			g.tool.errMgr.toolError(ErrorType.CANNOT_CREATE_TARGET_GENERATOR,
						 language);
		}

//		if ( EMIT_TEMPLATE_DELIMITERS ) {
//			templates.emitDebugStartStopStrings(true);
//			templates.doNotEmitDebugStringsForTemplate("codeFileExtension");
//			templates.doNotEmitDebugStringsForTemplate("headerFileExtension");
//		}
	}

	public ST generate() {
		OutputModelFactory factory;
		if ( g.isParser() || g.isCombined() || g.isTreeGrammar() ) {
			factory = new ParserFactory(this);
		}
		else {
			factory = new LexerFactory(this);
		}

		OutputModelWalker walker = new OutputModelWalker(g.tool, templates);
		OutputModelObject outputModel = factory.buildOutputModel();
		ST st = walker.walk(outputModel);

		if (CodeGenerator.LAUNCH_ST_INSPECTOR) {
			st.inspect();
			//if ( templates.isDefined("headerFile") ) headerFileST.inspect();
		}

		return st;
	}

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
			if ( tokenType>=Token.MIN_TOKEN_TYPE ) {
				tokens.put(t, Utils.integer(tokenType));
			}
		}
		vocabFileST.add("tokens", tokens);

		// now dump the strings
		Map<String,Integer> literals = new HashMap<String,Integer>();
		for (String literal : g.stringLiteralToTypeMap.keySet()) {
			int tokenType = g.stringLiteralToTypeMap.get(literal);
			if ( tokenType>=Token.MIN_TOKEN_TYPE ) {
				literals.put(literal, Utils.integer(tokenType));
			}
		}
		vocabFileST.add("literals", literals);

		return vocabFileST;
	}

	public void write(ST outputFileST) {
		// WRITE FILES
		try {
			target.genRecognizerFile(this,g,outputFileST);
			if ( templates.isDefined("headerFile") ) {
				ST extST = templates.getInstanceOf("headerFileExtension");
				ST headerFileST = null;
				target.genRecognizerHeaderFile(this,g,headerFileST,extST.render(lineWidth));
			}
			// write out the vocab interchange file; used by antlr,
			// does not change per target
			ST tokenVocabSerialization = getTokenVocabOutput();
			String vocabFileName = getVocabFileName();
			if ( vocabFileName!=null ) {
				write(tokenVocabSerialization, vocabFileName);
			}
		}
		catch (IOException ioe) {
			g.tool.errMgr.toolError(ErrorType.CANNOT_WRITE_FILE,
						 getVocabFileName(),
						 ioe);
		}
	}

	public void write(ST code, String fileName) throws IOException {
		long start = System.currentTimeMillis();
		Writer w = g.tool.getOutputFile(g, fileName);
		STWriter wr = new AutoIndentWriter(w);
		wr.setLineWidth(lineWidth);
		code.write(wr);
		w.close();
		long stop = System.currentTimeMillis();
		System.out.println("render time for "+fileName+": "+(int)(stop-start)+"ms");
	}

	/** Generate TParser.java and TLexer.java from T.g if combined, else
	 *  just use T.java as output regardless of type.
	 */
	public String getRecognizerFileName() {
		ST extST = templates.getInstanceOf("codeFileExtension");
		String recognizerName = g.getRecognizerName();
		return recognizerName+extST.render();
	}

	/** What is the name of the vocab file generated for this grammar?
	 *  Returns null if no .tokens file should be generated.
	 */
	public String getVocabFileName() {
//		if ( g.isBuiltFromString() ) {
//			return null;
//		}
		return g.name+VOCAB_FILE_EXTENSION;
	}

}
