/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** {@code public Token X() { }} */
public class ContextTokenGetterDecl extends ContextGetterDecl {
	public boolean optional;

	public ContextTokenGetterDecl(OutputModelFactory factory, String name, boolean optional) {
		super(factory, name);
		this.optional = optional;
	}
}
