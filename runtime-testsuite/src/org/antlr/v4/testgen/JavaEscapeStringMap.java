/*
 * [The "BSD license"]
 *  Copyright (c) 2015 Terence Parr
 *  Copyright (c) 2015 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.testgen;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Sam Harwell
 */
public class JavaEscapeStringMap extends AbstractMap<String, Object> {

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			String str = (String)key;
			StringBuilder builder = new StringBuilder(str.length());
			for (int i = 0; i < str.length(); i++) {
				switch (str.charAt(i)) {
				case '\\':
					builder.append("\\\\");
					break;

				case '\r':
					// normalize \r\n to just \n
					break;

				case '\n':
					builder.append("\\n");
					break;

				case '"':
					builder.append("\\\"");
					break;

				default:
					builder.append(str.charAt(i));
					break;
				}
			}

			return builder.toString();
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
