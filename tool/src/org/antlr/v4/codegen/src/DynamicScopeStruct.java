package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Rule;

/** */
public class DynamicScopeStruct extends StructDecl {
	public DynamicScopeStruct(OutputModelFactory factory, Rule r) {
		super(factory, factory.getDynamicScopeStructName(r.name), r.scope.attributes.values());
	}
}
