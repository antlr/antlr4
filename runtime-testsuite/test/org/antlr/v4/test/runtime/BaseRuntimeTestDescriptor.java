/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** An abstract but mostly complete test descriptor that pulls values
 *  for the various runtime test descriptor methods such as {@link #getInput()}
 *  from fields using reflection.
 *
 *  @since 4.6
 */
public abstract class BaseRuntimeTestDescriptor implements RuntimeTestDescriptor {
	protected String targetName;

	@Override
	public String getTestName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public String getInput() {
		try {
			Field f = this.getClass().getField("input");
			return stringIndentation((String)f.get(this));
		}
		catch (Exception nsfe) {
			; // we are optional
		}
		return "";
	}

	@Override
	public String getOutput() {
		try {
			Field f = this.getClass().getField("output");
			String s = stringIndentation((String)f.get(this));
			if ( s.length()==0 ) return null;
			return s;
		}
		catch (Exception nsfe) {
			; // we are optional
		}
		return null;
	}

	@Override
	public String getErrors() {
		try {
			Field f = this.getClass().getField("errors");
			String s = stringIndentation((String)f.get(this));
			if ( s.length()==0 ) return null;
			return s;
		}
		catch (Exception nsfe) {
			; // we are optional
		}
		return null;
	}

	@Override
	public String getANTLRToolErrors() {
		try {
			Field f = this.getClass().getField("toolErrors");
			String s = stringIndentation((String)f.get(this));
			if ( s.length()==0 ) return null;
			return s;
		}
		catch (Exception nsfe) {
			; // we are optional
		}
		return null;
	}

	@Override
	public String getStartRule() {
		try {
			Field f = this.getClass().getField("startRule");
			return (String)f.get(this);
		}
		catch (Exception nsfe) {
			System.err.println("No start rule specified for test "+getTestName());
		}
		return null;
	}

	@Override
	public Pair<String, String> getGrammar() {
		String grammarName = null;
		try {
			Field f = this.getClass().getField("grammarName");
			grammarName = (String)f.get(this);
		}
		catch (Exception nsfe) {
			System.err.println("No grammar name specified for test "+getTestName());
		}
		String grammar = rawGetGrammar();
		return new Pair<String,String>(grammarName,grammar);
	}

	private String rawGetGrammar() {
		String grammar = null;
		try {
			Field f = this.getClass().getField("grammar");
			grammar = (String)f.get(this);
		}
		catch (Exception nsfe) {
			System.err.println("No start rule specified for test "+getTestName());
		}
		grammar = stringIndentation(grammar);
		return grammar;
	}

	/** strip indentation; use first line's indent as prefix to strip */
	public static String stringIndentation(String s) {
		if ( s==null ) return "";
		if ( s.equals("\n") ) return s;
		s = Utils.expandTabs(s, 4);
		String lines[] = s.split("\\r?\\n");
		String first = lines[0];
		Pattern wspat = Pattern.compile("^\\s+");
		Matcher matcher = wspat.matcher(first);
		if ( matcher.find() ) {
			String indent = matcher.group(0);
			s = s.replace(indent, ""); // wack first indent
			s = s.replaceAll("\\n"+indent, "\n"); // wack the others
		}
		return s;
	}

	@Override
	public List<Pair<String, String>> getSlaveGrammars() {
		return null;
	}

	@Override
	public String getTarget() {
		return targetName;
	}

	@Override
	public void setTarget(String targetName) {
		this.targetName = targetName;
	}

	@Override
	public boolean showDFA() {
		return false;
	}

	@Override
	public boolean showDiagnosticErrors() {
		return false;
	}

	@Override
	public boolean ignore(String targetName) {
		return false;
	}

	@Override
	public String toString() {
		return getTarget()+":"+getTestName();
	}
}
