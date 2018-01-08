/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.CodeBlock;
import org.antlr.v4.tool.ast.GrammarAST;

/** */
public abstract class SrcOp extends OutputModelObject {
	/** Used to create unique var names etc... */
	public int uniqueID; // TODO: do we need?

	/** All operations know in which block they live:
	 *
	 *  	CodeBlock, CodeBlockForAlt
	 *
	 *  Templates might need to know block nesting level or find
	 *  a specific declaration, etc...
	 */
	public CodeBlock enclosingBlock;

	public RuleFunction enclosingRuleRunction;

	public SrcOp(OutputModelFactory factory) { this(factory,null); }
	public SrcOp(OutputModelFactory factory, GrammarAST ast) {
		super(factory,ast);
		if ( ast!=null ) uniqueID = ast.token.getTokenIndex();
		enclosingBlock = factory.getCurrentBlock();
		enclosingRuleRunction = factory.getCurrentRuleFunction();
	}

	/** Walk upwards in model tree, looking for outer alt's code block */
	public CodeBlockForOuterMostAlt getOuterMostAltCodeBlock() {
		if ( this instanceof CodeBlockForOuterMostAlt ) {
			return (CodeBlockForOuterMostAlt)this;
		}
		CodeBlock p = enclosingBlock;
		while ( p!=null ) {
			if ( p instanceof CodeBlockForOuterMostAlt ) {
				return (CodeBlockForOuterMostAlt)p;
			}
			p = p.enclosingBlock;
		}
		return null;
	}

	/** Return label alt or return name of rule */
	public String getContextName() {
		CodeBlockForOuterMostAlt alt = getOuterMostAltCodeBlock();
		if ( alt!=null && alt.altLabel!=null ) return alt.altLabel;
		return enclosingRuleRunction.name;
	}
}
