package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Attribute;

import java.util.Collection;

/** */
public class DynamicScopeStruct extends StructDecl {
	public DynamicScopeStruct(OutputModelFactory factory, String name, Collection<Attribute> attrList) {
		super(factory, name, attrList);
	}
}
