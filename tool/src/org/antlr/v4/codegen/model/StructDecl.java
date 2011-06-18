package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.tool.Attribute;

import java.util.*;

/** */
public class StructDecl extends Decl {
	@ModelElement public List<Decl> attrs = new ArrayList<Decl>();
	@ModelElement public Collection<Attribute> ctorAttrs;

	public StructDecl(OutputModelFactory factory, String name, Collection<Attribute> attrList) {
		super(factory, name);
		for (Attribute a : attrList) {
			attrs.add(new AttributeDecl(factory, a.name, a.decl));
		}
	}

//	@Override
//	public List<String> getChildren() {
//		final List<String> sup = super.getChildren();
//		return new ArrayList<String>() {{
//			if ( sup!=null ) addAll(sup); add("attrs");
//		}};
//	}
}
