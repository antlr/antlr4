/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.chunk;

import org.antlr.v4.codegen.model.decl.StructDecl;

public class LabelRef extends SymbolRefChunk {
	public LabelRef(StructDecl ctx, String name, String escapedName) {
		super(ctx, name, escapedName);
	}
}
