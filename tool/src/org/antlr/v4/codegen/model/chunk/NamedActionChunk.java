package org.antlr.v4.codegen.model.chunk;

import org.antlr.v4.codegen.model.decl.StructDecl;

public abstract class NamedActionChunk extends ActionChunk {
	public final String name;
	public final String escapedName;

	public NamedActionChunk(StructDecl ctx, String name, String escapedName) {
		super(ctx);
		this.name = name;
		this.escapedName = escapedName;
	}
}
