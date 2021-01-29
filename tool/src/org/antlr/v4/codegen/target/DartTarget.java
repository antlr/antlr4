/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DartTarget extends Target {

	/**
	 * The Java target can cache the code generation templates.
	 */
	private static final ThreadLocal<STGroup> targetTemplates = new ThreadLocal<STGroup>();

	protected static final String[] javaKeywords = {
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
	};

	/// Avoid grammar symbols in this set to prevent conflicts in gen'd code.
	protected final Set<String> badWords = new HashSet<String>();

	public DartTarget(CodeGenerator gen) {
		super(gen, "Dart");

		targetCharValueEscape['$'] = "\\$";
	}

	@Override
	public String getTargetStringLiteralFromANTLRStringLiteral(CodeGenerator generator, String literal, boolean addQuotes) {
		return super.getTargetStringLiteralFromANTLRStringLiteral(generator, literal, addQuotes).replace("$", "\\$");
	}

	@Override
	public String getVersion() {
		return Tool.VERSION; // Java and tool versions move in lock step
	}

	public Set<String> getBadWords() {
		if (badWords.isEmpty()) {
			addBadWords();
		}

		return badWords;
	}

	protected void addBadWords() {
		badWords.addAll(Arrays.asList(javaKeywords));
		badWords.add("rule");
		badWords.add("parserRule");
	}

	@Override
	public int getSerializedATNSegmentLimit() {
		// 65535 is the class file format byte limit for a UTF-8 encoded string literal
		// 3 is the maximum number of bytes it takes to encode a value in the range 0-0xFFFF
		return 65535 / 3;
	}

	@Override
	protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
		return getBadWords().contains(idNode.getText());
	}

	@Override
	protected STGroup loadTemplates() {
		STGroup result = super.loadTemplates();
		result.registerRenderer(String.class, new StringRenderer(), true);

		return result;
	}

	@Override
	public String encodeIntAsCharEscape(int v) {
		if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
			throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
		}

		return String.format("\\u{%X}", v & 0xFFFF);
	}

	@Override
	protected void appendUnicodeEscapedCodePoint(int codePoint, StringBuilder sb) {
		UnicodeEscapes.appendJavaStyleEscapedCodePoint(codePoint, sb);
	}
}
