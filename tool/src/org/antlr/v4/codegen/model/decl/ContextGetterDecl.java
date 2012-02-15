package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;

public abstract class ContextGetterDecl extends Decl {
	public ContextGetterDecl(OutputModelFactory factory, String name) {
		super(factory, name);
	}

	/** Not used for output; just used to distinguish between decl types
	 *  to avoid dups.
	 */
	public String getArgType() { return ""; }; // assume no args

	@Override
	public int hashCode() {
		return name.hashCode() + getArgType().hashCode();
	}

	/** Make sure that a getter does not equal a label. X() and X are ok.
	 *  OTOH, treat X() with two diff return values as the same.  Treat
	 *  two X() with diff args as different.
	 */
	@Override
	public boolean equals(Object obj) {
		if ( obj==null ) return false;
		// A() and label A are different
		if ( !(obj instanceof ContextGetterDecl) ) return false;
		if ( this==obj ) return true;
		if ( this.hashCode() != obj.hashCode() ) return false;
		return
			name.equals(((Decl) obj).name) &&
				getArgType().equals(((ContextGetterDecl) obj).getArgType());
	}
}
