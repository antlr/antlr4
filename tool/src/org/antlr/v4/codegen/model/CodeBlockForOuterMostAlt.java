package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Alternative;

/** The code associated with an outermost alternative overrule.
 *  Sometimes we might want to treat them differently in the
 *  code generation.
 */
public class CodeBlockForOuterMostAlt extends CodeBlockForAlt {
	public String altLabel;
	public Alternative alt;

	public CodeBlockForOuterMostAlt(OutputModelFactory factory, Alternative alt) {
		super(factory);
		this.alt = alt;
		altLabel = alt.ast.altLabel!=null ? alt.ast.altLabel.getText() : null;
	}
}
