/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Pair;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParseTreePatternMatcher {
	protected Class<? extends Lexer> lexerClass;
	protected Class<? extends Parser> parserClass;

	protected String start = "<", stop=">";
	protected String escape = "\\"; // e.g., \< and \> must escape BOTH!

	public static class Pattern {
		protected String pattern;

		public Pattern(String pattern) {
			this.pattern = pattern;
		}

		public boolean matches(ParseTree t) {
			return false;
		}
	}

	public static final Pattern WildcardPattern =
		new Pattern("...") {
			public boolean matches(ParseTree t) {
				return true;
			}
		};

	public static class Match {
		protected ParseTree subtree;
		protected List<Pair<String,? extends ParseTree>> labels;
	}

	public static class MatchIterator implements Iterator<Match> {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Match next() {
			return null;
		}

		@Override
		public void remove() {
		}
	}

	public ParseTreePatternMatcher() { }

	public ParseTreePatternMatcher(Class<? extends Lexer> lexerClass,
								   Class<? extends Parser> parserClass)
	{
		this.lexerClass = lexerClass;
		this.parserClass = parserClass;
	}

	public void setDelimiters(String start, String stop, String escapeLeft) {
		this.start = start;
		this.stop = stop;
		this.escape = escapeLeft;
	}

	public ParseTree compilePattern(String pattern) {
		Parser parser = null;
		Lexer lexer = null;
		try {
			Class<? extends Lexer> c = lexerClass.asSubclass(Lexer.class);
			Constructor<? extends Lexer> ctor = c.getConstructor(CharStream.class);
			lexer = ctor.newInstance((CharStream)null);
		}
		catch (Exception cnfe) {
			System.err.println("what?---------------");
		}

		try {
			Class<? extends Parser> c = parserClass.asSubclass(Parser.class);
			Constructor<? extends Parser> ctor = c.getConstructor(TokenStream.class);
			TokenStream tokens = new CommonTokenStream(lexer);
			parser = ctor.newInstance(tokens);
		}
		catch (Exception cnfe) {
			System.err.println("what?---------------");
		}
		// split pattern into chunks: sea (raw input) and islands (<ID>, <expr>)
//		ANTLRInputStream in = new ANTLRInputStream(new StringReader("foo"));
		return null;
	}

	/** Split "<ID> = <e:expr> ;" into 4 chunks */
	protected List<String> split(String pattern) {
		int p = 0;
		int n = pattern.length();
		List<String> chunks = new ArrayList<String>();
		StringBuffer buf = new StringBuffer();
		// find all start and stop indexes first, then collect
		List<Integer> starts = new ArrayList<Integer>();
		List<Integer> stops = new ArrayList<Integer>();
		while ( p<n ) {
			if ( p == pattern.indexOf(escape+start,p) ) {
				p += escape.length() + start.length();
			}
			else if ( p == pattern.indexOf(escape+stop,p) ) {
				p += escape.length() + stop.length();
			}
			else if ( p == pattern.indexOf(start,p) ) {
				starts.add(p);
				p += start.length();
			}
			else if ( p == pattern.indexOf(stop,p) ) {
				stops.add(p);
				p += stop.length();
			}
			else {
				p++;
			}
		}

//		System.out.println("");
//		System.out.println(starts);
//		System.out.println(stops);
		if ( starts.size() > stops.size() ) {
			throw new IllegalArgumentException("unterminated tag in pattern: "+pattern);
		}

		if ( starts.size() < stops.size() ) {
			throw new IllegalArgumentException("missing start tag in pattern: "+pattern);
		}

		int ntags = starts.size();
		for (int i=0; i<ntags; i++) {
			if ( starts.get(i)>=stops.get(i) ) {
				throw new IllegalArgumentException("tag delimiters out of order in pattern: "+pattern);
			}
		}

		// collect into chunks now
		if ( ntags==0 ) {
			chunks.add(pattern.substring(0, n));
		}

		if ( ntags>0 && starts.get(0)>0 ) { // copy text up to first tag into chunks
			chunks.add(pattern.substring(0, starts.get(0)));
		}
		for (int i=0; i<ntags; i++) {
			// copy inside of <tag>
			String tag = pattern.substring(starts.get(i) + start.length(), stops.get(i));
			chunks.add(tag);
			if ( i+1 < ntags ) {
				// copy from end of <tag> to start of next
				String text = pattern.substring(stops.get(i) + stop.length(), starts.get(i + 1));
				chunks.add(text);
			}
		}
		if ( ntags>0 ) {
			int endOfLastTag = stops.get(ntags - 1) + stop.length();
			if ( endOfLastTag < n-1 ) { // copy text from end of last tag to end
				String text = pattern.substring(endOfLastTag+stop.length(), n);
				chunks.add(text);
			}
		}

		// strip out the escape sequences
		for (int i=0; i<chunks.size(); i++) {
			String chunk = chunks.get(i).replace(escape, "");
			chunks.set(i, chunk);
		}

		return chunks;
	}

	public static void main(String[] args) {
		// tests
		ParseTreePatternMatcher p = new ParseTreePatternMatcher();
		System.out.println( p.split("<ID> = <expr> ;") );
		System.out.println( p.split(" <ID> = <expr>") );
		System.out.println( p.split("<ID> = <expr>") );
		System.out.println( p.split("<expr>") );
		System.out.println(p.split("\\<x\\> foo"));
		System.out.println(p.split("foo \\<x\\> bar <tag>"));
//		System.out.println( p.split(">expr<") );

		p.setDelimiters("<<", ">>", "$");
		System.out.println(p.split("<<ID>> = <<expr>> ;$<< ick $>>"));

	}

//	protected List<String> split____(String pattern) {
//		int p = 0;
//		int n = pattern.length();
//		List<String> chunks = new ArrayList<String>();
//		StringBuffer buf = new StringBuffer();
//		while ( p<n ) {
//			int nextEsc = pattern.indexOf(escape, p);
//			if ( p == nextEsc ) { // found escape right now
//			}
//			else if ( nextEsc > p ) { // an esc exists ahead
//				chunks.add(pattern.substring(p,nextStart));
//				p = nextStart; // jump to next tag
//			}
//			else {
//
//			}
//			int nextStart = pattern.indexOf(start, p);
//			int esclen = escape.length();
//			if ( pattern.substring(p-esclen,p).equals(escape) ) {
//				// it's escape+start, skip
//
//			}
//			if ( p == nextStart ) { // found start of tag
//				// consume <tag>, scan for stop sequence
//				int nextStop = pattern.indexOf(stop, p);
//				if ( nextStop == -1 ) {
//					System.err.println("what?---------------");
//				}
//				chunks.add(pattern.substring(p+start.length(),nextStop));
//				p = nextStop + stop.length();
//			}
//			else if ( nextStart > p ) { // another tag exists
//				chunks.add(pattern.substring(p,nextStart));
//				p = nextStart; // jump to next tag
//			}
//			else {
//				// no next tag, return rest of string as chunk
//				chunks.add(pattern.substring(p,n));
//				break;
//			}
//		}
//		return chunks;
//	}

	public MatchIterator findAll(ParseTree t, int ruleIndex, String pattern) {
		ParseTreeWalker walker = new ParseTreeWalker();
		return null;
	}

	// preorder
	protected List<ParseTree> findAll(ParseTree t, Pattern pattern) {
		List<ParseTree> subtrees = new ArrayList<ParseTree>();
		findAll_(t, pattern, subtrees);
		return subtrees;
	}

	protected void findAll_(ParseTree t, Pattern pattern, List<ParseTree> subtrees) {
		if ( pattern.matches(t) ) {
			subtrees.add(t);
		}
		if ( t instanceof RuleNode ) {
			RuleNode r = (RuleNode)t;
			int n = r.getChildCount();
			for (int i = 0; i<n; i++) {
				findAll(r.getChild(i), pattern);
			}
		}
	}
}
