/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public class ContextRuleListIndexedGetterDecl extends ContextRuleListGetterDecl {
	public ContextRuleListIndexedGetterDecl(OutputModelFactory factory, String name, String ctxName) {
		this(factory, name, ctxName, false);
	}

	public ContextRuleListIndexedGetterDecl(OutputModelFactory factory, String name, String ctxName, boolean signature) {
		super(factory, name, ctxName, signature);
	}

	public ContextRuleListIndexedGetterDecl(OutputModelFactory factory, String name, String escapedName, String ctxName,
											boolean signature) {
		super(factory, name, escapedName, ctxName, signature);
	}

	@Override
	public String getArgType() {
		return "int";
	}

	@Override
	public ContextGetterDecl getSignatureDecl() {
		return new ContextRuleListIndexedGetterDecl(factory, name, escapedName, ctxName, true);
	}
}
