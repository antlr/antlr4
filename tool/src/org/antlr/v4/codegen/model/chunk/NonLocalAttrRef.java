/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.chunk;

import org.antlr.v4.codegen.model.decl.StructDecl;

public class NonLocalAttrRef extends ActionChunk {
	public String ruleName;
	public String name;
	public int ruleIndex;

	public NonLocalAttrRef(StructDecl ctx, String ruleName, String name, int ruleIndex) {
		super(ctx);
		this.name = name;
		this.ruleName = ruleName;
		this.ruleIndex = ruleIndex;
	}

}
