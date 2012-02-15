package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** public List<XContext> X() { }
 *  public XContext X(int i) { }
 */
public class ContextRuleListGetterDecl extends ContextGetterDecl {
	public String ctxName;
	public ContextRuleListGetterDecl(OutputModelFactory factory, String name, String ctxName) {
		super(factory, name);
		this.ctxName = ctxName;
	}
}
