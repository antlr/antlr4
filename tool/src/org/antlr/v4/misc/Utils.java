/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.misc;

import org.antlr.v4.runtime.misc.Func1;
import org.antlr.v4.runtime.misc.Predicate;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.*;

/** */
public class Utils {
	public static final int INTEGER_POOL_MAX_VALUE = 1000;

	static Integer[] ints = new Integer[INTEGER_POOL_MAX_VALUE+1];

	/** Integer objects are immutable so share all Integers with the
	 *  same value up to some max size.  Use an array as a perfect hash.
	 *  Return shared object for 0..INTEGER_POOL_MAX_VALUE or a new
	 *  Integer object with x in it.  Java's autoboxing only caches up to 127.
	public static Integer integer(int x) {
		if ( x<0 || x>INTEGER_POOL_MAX_VALUE ) {
			return new Integer(x);
		}
		if ( ints[x]==null ) {
			ints[x] = new Integer(x);
		}
		return ints[x];
	}
	 */

    public static String stripFileExtension(String name) {
        if ( name==null ) return null;
        int lastDot = name.lastIndexOf('.');
        if ( lastDot<0 ) return name;
        return name.substring(0, lastDot);
    }

	public static String join(Object[] a, String separator) {
		StringBuilder buf = new StringBuilder();
		for (int i=0; i<a.length; i++) {
			Object o = a[i];
			buf.append(o.toString());
			if ( (i+1)<a.length ) {
				buf.append(separator);
			}
		}
		return buf.toString();
	}

	/** Given a source string, src,
		a string to replace, replacee,
		and a string to replace with, replacer,
		return a new string w/ the replacing done.
		You can use replacer==null to remove replacee from the string.

		This should be faster than Java's String.replaceAll as that one
		uses regex (I only want to play with strings anyway).
	*/
	public static String replace(String src, String replacee, String replacer) {
		StringBuilder result = new StringBuilder(src.length() + 50);
		int startIndex = 0;
		int endIndex = src.indexOf(replacee);
		while(endIndex != -1) {
			result.append(src.substring(startIndex,endIndex));
			if ( replacer!=null ) {
				result.append(replacer);
			}
			startIndex = endIndex + replacee.length();
			endIndex = src.indexOf(replacee,startIndex);
		}
		result.append(src.substring(startIndex,src.length()));
		return result.toString();
	}

	public static String sortLinesInString(String s) {
		String lines[] = s.split("\n");
		Arrays.sort(lines);
		List<String> linesL = Arrays.asList(lines);
		StringBuilder buf = new StringBuilder();
		for (String l : linesL) {
			buf.append(l);
			buf.append('\n');
		}
		return buf.toString();
	}

	public static <T extends GrammarAST> List<String> nodesToStrings(List<T> nodes) {
		if ( nodes == null ) return null;
		List<String> a = new ArrayList<String>();
		for (T t : nodes) a.add(t.getText());
		return a;
	}

//	public static <T> List<T> list(T... values) {
//		List<T> x = new ArrayList<T>(values.length);
//		for (T v : values) {
//			if ( v!=null ) x.add(v);
//		}
//		return x;
//	}

	public static int[] toIntArray(List<Integer> list) {
		if ( list==null ) return null;
		int[] a = new int[list.size()];
		for (int i=0; i<list.size(); i++) a[i] = list.get(i);
		return a;
	}

	public static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	public static String decapitalize(String s) {
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	public static char[] toCharArray(List<Integer> data) {
		if ( data==null ) return null;
		char[] cdata = new char[data.size()];
		for (int i=0; i<data.size(); i++) {
			cdata[i] = (char)(int)data.get(i);
		}
		return cdata;
	}

	/** apply methodName to list and return list of results. method has
	 *  no args.  This pulls data out of a list essentially.
	 */
	public static <From,To> List<To> select(List<From> list, Func1<From, To> selector) {
		if ( list==null ) return null;
		List<To> b = new ArrayList<To>();
		for (From f : list) {
			b.add(selector.eval(f));
		}
		return b;
	}

	/** Find exact object type or sublass of cl in list */
	public static <T> T find(List<?> ops, Class<T> cl) {
		for (Object o : ops) {
			if ( cl.isInstance(o) ) return cl.cast(o);
//			if ( o.getClass() == cl ) return o;
		}
		return null;
	}

	public static <T> int indexOf(List<? extends T> elems, Predicate<? super T> match) {
		for (int i=0; i<elems.size(); i++) {
			if ( match.eval(elems.get(i)) ) return i;
		}
		return -1;
	}

	public static <T> int lastIndexOf(List<? extends T> elems, Predicate<? super T> match) {
		for (int i=elems.size()-1; i>=0; i--) {
			if ( match.eval(elems.get(i)) ) return i;
		}
		return -1;
	}

	public static void setSize(List<?> list, int size) {
		if (size < list.size()) {
			list.subList(size, list.size()).clear();
		} else {
			while (size > list.size()) {
				list.add(null);
			}
		}
	}

}
