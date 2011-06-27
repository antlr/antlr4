package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class ThrowNoViableAlt extends ThrowRecognitionException {
	public ThrowNoViableAlt(OutputModelFactory factory, GrammarAST blkOrEbnfRootAST,
							IntervalSet expecting)
	{
		super(factory, blkOrEbnfRootAST, expecting);
	}
}
