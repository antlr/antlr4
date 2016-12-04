/*
 * Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.testgen;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Sam Harwell
 */
public class LinesStringMap extends AbstractMap<String, Object> {

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			String str = (String)key;
			if (str.isEmpty()) {
				return Collections.singletonList(str);
			}

			ArrayList<String> result = new ArrayList<String>();
			int startIndex = 0;
			while (startIndex < str.length()) {
				int endIndex = str.indexOf('\n', startIndex);
				if (endIndex < 0) {
					result.add(str.substring(startIndex));
					break;
				}

				result.add(str.substring(startIndex, endIndex + 1));
				startIndex = endIndex + 1;
			}

			return result;
		}

		return super.get(key);
	}

	@Override
	public boolean containsKey(Object key) {
		return key instanceof String;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return Collections.emptySet();
	}

}
