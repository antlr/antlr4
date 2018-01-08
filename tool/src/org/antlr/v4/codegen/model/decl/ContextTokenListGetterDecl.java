/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** {@code public List<Token> X() { }
 *  public Token X(int i) { }}
 */
public class ContextTokenListGetterDecl extends ContextGetterDecl {
	public ContextTokenListGetterDecl(OutputModelFactory factory, String name) {
		super(factory, name);
	}
}
