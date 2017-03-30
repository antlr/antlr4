/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.ObjectEqualityComparator;

/**
 *
 * @author Sam Harwell
 */
public class OrderedATNConfigSet extends ATNConfigSet {

	public OrderedATNConfigSet() {
		this.configLookup = new LexerConfigHashSet();
	}

	public static class LexerConfigHashSet extends AbstractConfigHashSet {
		public LexerConfigHashSet() {
			super(ObjectEqualityComparator.INSTANCE);
		}
	}
}
