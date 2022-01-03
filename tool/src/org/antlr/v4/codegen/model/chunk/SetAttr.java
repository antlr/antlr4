/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.chunk;

import org.antlr.v4.codegen.model.ModelElement;
import org.antlr.v4.codegen.model.decl.StructDecl;

import java.util.List;

/** */
public class SetAttr extends NamedActionChunk {
	@ModelElement public List<ActionChunk> rhsChunks;

	public SetAttr(StructDecl ctx, String name, String escapedName, List<ActionChunk> rhsChunks) {
		super(ctx, name, escapedName);
		this.rhsChunks = rhsChunks;
	}
}
