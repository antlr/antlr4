package org.antlr.v4.codegen.model.chunk;

import org.antlr.v4.codegen.model.decl.StructDecl;

public abstract class SymbolRefChunk extends ActionChunk {
	public final String name;
	public final String escapedName;

	public SymbolRefChunk(StructDecl ctx, String name, String escapedName) {
		super(ctx);
		this.name = name;
		this.escapedName = escapedName;
	}
}
