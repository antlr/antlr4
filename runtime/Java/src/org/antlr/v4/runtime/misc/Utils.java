/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {
    // Seriously: why isn't this built in to java? ugh!
    public static <T> String join(Iterator<T> iter, String separator) {
        StringBuilder buf = new StringBuilder();
        while ( iter.hasNext() ) {
            buf.append(iter.next());
            if ( iter.hasNext() ) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

	public static <T> String join(T[] array, String separator) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i < array.length - 1) {
				builder.append(separator);
			}
		}

		return builder.toString();
	}

	public static int numNonnull(Object[] data) {
		int n = 0;
		if ( data == null ) return n;
		for (Object o : data) {
			if ( o!=null ) n++;
		}
		return n;
	}

	public  static <T> void removeAllElements(Collection<T> data, T value) {
		if ( data==null ) return;
		while ( data.contains(value) ) data.remove(value);
	}

	public static String escapeWhitespace(String s, boolean escapeSpaces) {
		StringBuilder buf = new StringBuilder();
		for (char c : s.toCharArray()) {
			if ( c==' ' && escapeSpaces ) buf.append('\u00B7');
			else if ( c=='\t' ) buf.append("\\t");
			else if ( c=='\n' ) buf.append("\\n");
			else if ( c=='\r' ) buf.append("\\r");
			else buf.append(c);
		}
		return buf.toString();
	}

	public static void writeFile(String fileName, String content) throws IOException {
		writeFile(fileName, content, null);
	}

	public static void writeFile(String fileName, String content, String encoding) throws IOException {
		File f = new File(fileName);
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw;
		if (encoding != null) {
			osw = new OutputStreamWriter(fos, encoding);
		}
		else {
			osw = new OutputStreamWriter(fos);
		}

		try {
			osw.write(content);
		}
		finally {
			osw.close();
		}
	}


	public static char[] readFile(String fileName) throws IOException {
		return readFile(fileName, null);
	}


	public static char[] readFile(String fileName, String encoding) throws IOException {
		File f = new File(fileName);
		int size = (int)f.length();
		InputStreamReader isr;
		FileInputStream fis = new FileInputStream(fileName);
		if ( encoding!=null ) {
			isr = new InputStreamReader(fis, encoding);
		}
		else {
			isr = new InputStreamReader(fis);
		}
		char[] data = null;
		try {
			data = new char[size];
			int n = isr.read(data);
			if (n < data.length) {
				data = Arrays.copyOf(data, n);
			}
		}
		finally {
			isr.close();
		}
		return data;
	}

	/** Convert array of strings to string&rarr;index map. Useful for
	 *  converting rulenames to name&rarr;ruleindex map.
	 */
	public static Map<String, Integer> toMap(String[] keys) {
		Map<String, Integer> m = new HashMap<String, Integer>();
		for (int i=0; i<keys.length; i++) {
			m.put(keys[i], i);
		}
		return m;
	}

	public static char[] toCharArray(IntegerList data) {
		if ( data==null ) return null;
		return data.toCharArray();
	}

	public static IntervalSet toSet(BitSet bits) {
		IntervalSet s = new IntervalSet();
		int i = bits.nextSetBit(0);
		while ( i >= 0 ) {
			s.add(i);
			i = bits.nextSetBit(i+1);
		}
		return s;
	}

	/** @since 4.6 */
	public static String expandTabs(String s, int tabSize) {
		if ( s==null ) return null;
		StringBuilder buf = new StringBuilder();
		int col = 0;
		for (int i = 0; i<s.length(); i++) {
			char c = s.charAt(i);
			switch ( c ) {
				case '\n' :
					col = 0;
					buf.append(c);
					break;
				case '\t' :
					int n = tabSize-col%tabSize;
					col+=n;
					buf.append(spaces(n));
					break;
				default :
					col++;
					buf.append(c);
					break;
			}
		}
		return buf.toString();
	}

	/** @since 4.6 */
	public static String spaces(int n) {
		return sequence(n, " ");
	}

	/** @since 4.6 */
	public static String newlines(int n) {
		return sequence(n, "\n");
	}

	/** @since 4.6 */
	public static String sequence(int n, String s) {
		StringBuilder buf = new StringBuilder();
		for (int sp=1; sp<=n; sp++) buf.append(s);
		return buf.toString();
	}

	/** @since 4.6 */
	public static int count(String s, char x) {
		int n = 0;
		for (int i = 0; i<s.length(); i++) {
			if ( s.charAt(i)==x ) {
				n++;
			}
		}
		return n;
	}
}
