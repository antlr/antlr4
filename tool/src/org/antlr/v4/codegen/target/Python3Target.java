/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.util.*;

public class Python3Target extends Target {
	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		"abs", "all", "and", "any", "apply", "as", "assert",
		"bin", "bool", "break", "buffer", "bytearray",
		"callable", "chr", "class", "classmethod", "coerce", "compile", "complex", "continue",
		"def", "del", "delattr", "dict", "dir", "divmod",
		"elif", "else", "enumerate", "eval", "execfile", "except",
		"file", "filter", "finally", "float", "for", "format", "from", "frozenset",
		"getattr", "global", "globals",
		"hasattr", "hash", "help", "hex",
		"id", "if", "import", "in", "input", "int", "intern", "is", "isinstance", "issubclass", "iter",
		"lambda", "len", "list", "locals",
		"map", "max", "min", "memoryview",
		"next", "nonlocal", "not",
		"object", "oct", "open", "or", "ord",
		"pass", "pow", "print", "property",
		"raise", "range", "raw_input", "reduce", "reload", "repr", "return", "reversed", "round",
		"set", "setattr", "slice", "sorted", "staticmethod", "str", "sum", "super",
		"try", "tuple", "type",
		"unichr", "unicode",
		"vars",
		"with", "while",
		"yield",
		"zip",
		"__import__",
		"True", "False", "None",

		// misc
		"rule", "parserRule"
	));

	protected static final Map<Character, String> targetCharValueEscape;
	static {
		// https://docs.python.org/3/reference/lexical_analysis.html#string-and-bytes-literals
		HashMap<Character, String> map = new HashMap<>();
		addEscapedChar(map, '\\');
		addEscapedChar(map, '\'');
		addEscapedChar(map, '\"');
		addEscapedChar(map, (char)0x0007, 'a');
		addEscapedChar(map, (char)0x0008, 'b');
		addEscapedChar(map, '\f', 'f');
		addEscapedChar(map, '\n', 'n');
		addEscapedChar(map, '\r', 'r');
		addEscapedChar(map, '\t', 't');
		addEscapedChar(map, (char)0x000B, 'v');
		targetCharValueEscape = map;
	}

	public Python3Target(CodeGenerator gen) {
		super(gen);
	}

	@Override
	public Map<Character, String> getTargetCharValueEscape() {
		return targetCharValueEscape;
	}

	@Override
	protected Set<String> getReservedWords() {
		return reservedWords;
	}

	@Override
	protected STGroup loadTemplates() {
		STGroup result = super.loadTemplates();
		result.registerRenderer(String.class, new PythonStringRenderer(), true);
		return result;
	}

	protected static class PythonStringRenderer extends StringRenderer {

		@Override
		public String toString(Object o, String formatString, Locale locale) {
			return super.toString(o, formatString, locale);
		}
	}

	@Override
	public boolean wantsBaseListener() {
		return false;
	}

	@Override
	public boolean wantsBaseVisitor() {
		return false;
	}

	@Override
	public boolean supportsOverloadedMethods() {
		return false;
	}
}
