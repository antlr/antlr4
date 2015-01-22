/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.antlr.mojo.antlr4.testgen;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Sam Harwell
 */
public class StrlenStringMap extends AbstractMap<String, Object> {

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			String str = (String)key;
			return str.length();
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
