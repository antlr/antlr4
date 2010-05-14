package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Rule;

/** */
public class ReturnValueStruct extends StructDecl {
	public ReturnValueStruct(OutputModelFactory factory, Rule r) {
		super(factory, factory.getReturnStructName(r.name), r.retvals.attributes.values());
	}
}
