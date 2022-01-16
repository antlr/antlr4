/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.ast.GrammarAST;

/** */
public abstract class OutputModelObject {
	public final OutputModelFactory factory;
	public final GrammarAST ast;

	public OutputModelObject() {
		this(null);
	}

	public OutputModelObject(OutputModelFactory factory) { this(factory, null); }

	public OutputModelObject(OutputModelFactory factory, GrammarAST ast) {
		this.factory = factory;
		this.ast = ast;
	}
}
