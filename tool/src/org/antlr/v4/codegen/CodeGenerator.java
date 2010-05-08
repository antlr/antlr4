package org.antlr.v4.codegen;

import org.antlr.v4.codegen.src.OutputModelObject;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.stringtemplate.v4.*;

import java.io.IOException;
import java.io.Writer;

/** */
public abstract class CodeGenerator {
	public static final String TEMPLATE_ROOT = "org/antlr/v4/tool/templates/codegen";
	public static final String VOCAB_FILE_EXTENSION = ".tokens";
	public final static String vocabFilePattern =
		"<tokens:{<attr.name>=<attr.type>\n}>" +
		"<literals:{<attr.name>=<attr.type>\n}>";

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

	/** The parser, tree parser, etc... variants know to build the model */
	public abstract OutputModelObject buildOutputModel();

	public void write() {
		OutputModelObject root = buildOutputModel();

		OutputModelWalker walker = new OutputModelWalker(g.tool, templates,
														 ParserGenerator.modelToTemplateMap);
		ST outputFileST = walker.walk(root);
		
		// WRITE FILES
		try {
			target.genRecognizerFile(this,g,outputFileST);
			if ( templates.isDefined("headerFile") ) {
				ST extST = templates.getInstanceOf("headerFileExtension");
				ST headerFileST = null;
				target.genRecognizerHeaderFile(this,g,headerFileST,extST.render());
			}
//			// write out the vocab interchange file; used by antlr,
//			// does not change per target
//			ST tokenVocabSerialization = genTokenVocabOutput();
//			String vocabFileName = getVocabFileName();
//			if ( vocabFileName!=null ) {
//				write(tokenVocabSerialization, vocabFileName);
//			}
			//System.out.println(outputFileST.getDOTForDependencyGraph(false));
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
		// Write the output to a StringWriter
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
