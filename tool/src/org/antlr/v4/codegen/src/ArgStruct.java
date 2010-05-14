package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Rule;

/** */
public class ArgStruct extends StructDecl {
	public ArgStruct(OutputModelFactory factory, Rule r) {
		super(factory, factory.getArgStructName(r.name), r.args.attributes.values());
	}
}
