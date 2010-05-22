package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class ThrowRecognitionException extends SrcOp {
	public int decision;
	public String grammarFile;
	public int grammarLine;
	public int grammarCharPosInLine;
	public BitSetDecl expecting;

	public ThrowRecognitionException(OutputModelFactory factory, GrammarAST ast, IntervalSet expecting) {
		super(factory, ast);
//		this.decision = ((BlockStartState)ast.nfaState).decision;
		grammarLine = ast.getLine();
		grammarLine = ast.getCharPositionInLine();
		grammarFile = factory.g.fileName;
		this.expecting = factory.createExpectingBitSet(ast, decision, expecting);
		factory.defineBitSet(this.expecting);
	}
}
