/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.chunk;

import org.antlr.v4.codegen.model.decl.StructDecl;

import java.util.List;

public class SetNonLocalAttr extends SetAttr {
	public String ruleName;
	public int ruleIndex;

	public SetNonLocalAttr(StructDecl ctx,
						   String ruleName, String name, String escapedName, int ruleIndex,
						   List<ActionChunk> rhsChunks)
	{
		super(ctx, name, escapedName, rhsChunks);
		this.ruleName = ruleName;
		this.ruleIndex = ruleIndex;
	}
}
