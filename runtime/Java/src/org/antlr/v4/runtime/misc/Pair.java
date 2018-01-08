/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.misc;

import java.io.Serializable;

public class Pair<A,B> implements Serializable {
	public final A a;
	public final B b;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (!(obj instanceof Pair<?, ?>)) {
			return false;
		}

		Pair<?, ?> other = (Pair<?, ?>)obj;
		return ObjectEqualityComparator.INSTANCE.equals(a, other.a)
			&& ObjectEqualityComparator.INSTANCE.equals(b, other.b);
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize();
		hash = MurmurHash.update(hash, a);
		hash = MurmurHash.update(hash, b);
		return MurmurHash.finish(hash, 2);
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", a, b);
	}
}
