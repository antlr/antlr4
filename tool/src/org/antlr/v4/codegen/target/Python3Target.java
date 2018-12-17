/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author Eric Vergnaud
 */
public class Python3Target extends Target {
	protected static final String[] python3Keywords = {
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
		"True", "False", "None"
	};

	public Python3Target(CodeGenerator gen) {
		super(gen, "Python3");
	}

	@Override
	public int getSerializedATNSegmentLimit() {
		// set to something stupid to avoid segmentation
		return Integer.MAX_VALUE;
	}

	@Override
	protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
		return getBadWords().contains(idNode.getText());
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

	@Override
	public String getVersion() {
		return "4.7.2";
	}

	/** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
	protected final Set<String> badWords = new HashSet<String>();

	public Set<String> getBadWords() {
		if (badWords.isEmpty()) {
			addBadWords();
		}

		return badWords;
	}

	protected void addBadWords() {
		badWords.addAll(Arrays.asList(python3Keywords));
		badWords.add("rule");
		badWords.add("parserRule");
	}

	@Override
	protected void appendUnicodeEscapedCodePoint(int codePoint, StringBuilder sb) {
		UnicodeEscapes.appendPythonStyleEscapedCodePoint(codePoint, sb);
	}
}
