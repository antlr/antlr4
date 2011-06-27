package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class ThrowNoViableAlt extends ThrowRecognitionException {
	public ThrowNoViableAlt(CoreOutputModelFactory factory, GrammarAST blkOrEbnfRootAST,
							IntervalSet expecting)
	{
		super(factory, blkOrEbnfRootAST, expecting);
	}
}
