package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;

/** The code associated with an outermost alternative overrule.
 *  Sometimes we might want to treat them differently in the
 *  code generation.
 */
public class CodeBlockForOuterMostAlt extends CodeBlockForAlt {
	public int altNum;

	public CodeBlockForOuterMostAlt(OutputModelFactory factory, int altNum) {
		super(factory);
		this.altNum = altNum;
	}
}
