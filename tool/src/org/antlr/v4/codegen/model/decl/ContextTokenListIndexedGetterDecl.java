/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public class ContextTokenListIndexedGetterDecl extends ContextTokenListGetterDecl {
	public ContextTokenListIndexedGetterDecl(OutputModelFactory factory, String name) {
		this(factory, name, false);
	}

	public ContextTokenListIndexedGetterDecl(OutputModelFactory factory, String name, boolean signature) {
		super(factory, name, signature);
	}

	@Override
	public String getArgType() {
		return "int";
	}

	@Override
	public ContextGetterDecl getSignatureDecl() {
		return new ContextTokenListIndexedGetterDecl(factory, name, true);
	}
}
