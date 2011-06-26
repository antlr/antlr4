package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.ModelElement;
import org.antlr.v4.tool.Attribute;

import java.util.*;

/** */
public class StructDecl extends Decl {
	@ModelElement public List<Decl> attrs = new ArrayList<Decl>();
	@ModelElement public Collection<Attribute> ctorAttrs;

	public StructDecl(OutputModelFactory factory) {
		super(factory, null);
	}

	public StructDecl(OutputModelFactory factory, String name) {
		super(factory, name);
	}

	public void addDecl(Decl d) { attrs.add(d); }

	public void addDecl(Attribute a) {
		addDecl(new AttributeDecl(factory, a.name, a.decl));
	}

	public void addDecls(Collection<Attribute> attrList) {
		for (Attribute a : attrList) addDecl(a);
	}

	public boolean isEmpty() { return attrs.size()==0; }
}
