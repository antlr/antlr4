package org.antlr.v4.codegen;

import org.antlr.v4.codegen.model.*;

/** */
public class LexerFactory extends DefaultOutputModelFactory {
	public LexerFactory(CodeGenerator gen) { super(gen); }

	public OutputModelObject buildOutputModel(OutputModelController controller) {
		LexerFile file = new LexerFile(this, getGenerator().getRecognizerFileName());
		setRoot(file);
		file.lexer = new Lexer(this, file);
		return file;
	}
}
