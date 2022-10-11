/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** {@code public List<XContext> X() { }
 *  public XContext X(int i) { }}
 */
public class ContextRuleListGetterDecl extends ContextGetterDecl {
	public String ctxName;
	public ContextRuleListGetterDecl(OutputModelFactory factory, String name, String ctxName) {
		this(factory, name, ctxName, false);
	}

	public ContextRuleListGetterDecl(OutputModelFactory factory, String name, String ctxName, boolean signature) {
		super(factory, name, signature);
		this.ctxName = ctxName;
	}

	@Override
	public ContextGetterDecl getSignatureDecl() {
		return new ContextRuleListGetterDecl(factory, name, ctxName, true);
	}
}
