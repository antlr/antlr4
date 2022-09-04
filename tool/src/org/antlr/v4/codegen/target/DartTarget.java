/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;

import java.util.*;

public class DartTarget extends Target {
	protected static final Map<Character, String> targetCharValueEscape;
	static {
		HashMap<Character, String> map = new HashMap<>(defaultCharValueEscape);
		addEscapedChar(map, '$');
		targetCharValueEscape = map;
	}

	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		"abstract", "dynamic", "implements", "show",
		"as", "else", "import", "static",
		"assert", "enum", "in", "super",
		"async", "export", "interface", "switch",
		"await", "extends", "is", "sync",
		"break", "external", "library", "this",
		"case", "factory", "mixin", "throw",
		"catch", "false", "new", "true",
		"class", "final", "null", "try",
		"const", "finally", "on", "typedef",
		"continue", "for", "operator", "var",
		"covariant", "Function", "part", "void",
		"default", "get", "rethrow", "while",
		"deferred", "hide", "return", "with",
		"do", "if", "set", "yield",

		"rule", "parserRule"
	));

	public DartTarget(CodeGenerator gen) {
		super(gen);
	}

	@Override
	public Map<Character, String> getTargetCharValueEscape() {
		return targetCharValueEscape;
	}

	@Override
	public String getTargetStringLiteralFromANTLRStringLiteral(CodeGenerator generator, String literal, boolean addQuotes,
															   boolean escapeSpecial) {
		return super.getTargetStringLiteralFromANTLRStringLiteral(generator, literal, addQuotes, escapeSpecial).replace("$", "\\$");
	}

	public Set<String> getReservedWords() {
		return reservedWords;
	}

	@Override
	public boolean isATNSerializedAsInts() {
		return true;
	}

	@Override
	protected String escapeChar(int v) {
		return String.format("\\u{%X}", v);
	}
}
