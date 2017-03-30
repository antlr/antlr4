/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model.decl;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.runtime.misc.MurmurHash;

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
		int hash = MurmurHash.initialize();
		hash = MurmurHash.update(hash, name);
		hash = MurmurHash.update(hash, getArgType());
		hash = MurmurHash.finish(hash, 2);
		return hash;
	}

	/** Make sure that a getter does not equal a label. X() and X are ok.
	 *  OTOH, treat X() with two diff return values as the same.  Treat
	 *  two X() with diff args as different.
	 */
	@Override
	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		// A() and label A are different
		if ( !(obj instanceof ContextGetterDecl) ) return false;
		return
			name.equals(((Decl) obj).name) &&
				getArgType().equals(((ContextGetterDecl) obj).getArgType());
	}
}
