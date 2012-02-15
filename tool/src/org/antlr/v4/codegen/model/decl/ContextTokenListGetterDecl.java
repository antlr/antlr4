package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

/** public List<Token> X() { }
 *  public Token X(int i) { }
 */
public class ContextTokenListGetterDecl extends ContextGetterDecl {
	public ContextTokenListGetterDecl(OutputModelFactory factory, String name) {
		super(factory, name);
	}
}
