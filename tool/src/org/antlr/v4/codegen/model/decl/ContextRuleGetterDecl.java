/*
 * Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** {@code public XContext X() { }} */
public class ContextRuleGetterDecl extends ContextGetterDecl {
	public String ctxName;
	public ContextRuleGetterDecl(OutputModelFactory factory, String name, String ctxName) {
		super(factory, name);
		this.ctxName = ctxName;
	}
}
