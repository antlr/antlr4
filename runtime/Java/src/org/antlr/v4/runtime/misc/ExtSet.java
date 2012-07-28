package org.antlr.v4.runtime.misc;

import java.util.Set;

public interface ExtSet<T> extends Set<T> {
	/** Add o to set if not there; return existing value if already there.
	 *  Absorb is used as synonym for add.  Need to "fix" Set to be smarter.
	 */
	public T absorb(T o);
}
