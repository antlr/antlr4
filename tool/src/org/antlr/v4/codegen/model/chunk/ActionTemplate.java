package org.antlr.v4.codegen.model.chunk;

import org.antlr.v4.codegen.model.decl.StructDecl;
import org.stringtemplate.v4.ST;

public class ActionTemplate extends ActionChunk {
	public ST st;

	public ActionTemplate(StructDecl ctx, ST st) {
		super(ctx);
		this.st = st;
	}
}
