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

public class PHPTarget extends Target {
	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		"abstract", "and", "array", "as",
		"break",
		"callable", "case", "catch", "class", "clone", "const", "continue",
		"declare", "default", "die", "do",
		"echo", "else", "elseif", "empty", "enddeclare", "endfor", "endforeach",
		"endif", "endswitch", "endwhile", "eval", "exit", "extends",
		"final", "finally", "for", "foreach", "function",
		"global", "goto",
		"if", "implements", "include", "include_once", "instanceof", "insteadof", "interface", "isset",
		"list",
		"namespace", "new",
		"or",
		"print", "private", "protected", "public",
		"require", "require_once", "return",
		"static", "switch",
		"throw", "trait", "try",
		"unset", "use",
		"var",
		"while",
		"xor",
		"yield",
		"__halt_compiler", "__CLASS__", "__DIR__", "__FILE__", "__FUNCTION__",
		"__LINE__", "__METHOD__", "__NAMESPACE__", "__TRAIT__",

		// misc
		"rule", "parserRule"
	));

	protected static final Map<Character, String> targetCharValueEscape;
	static {
		// https://www.php.net/manual/en/language.types.string.php
		HashMap<Character, String> map = new HashMap<>();
		addEscapedChar(map, '\n', 'n');
		addEscapedChar(map, '\r', 'r');
		addEscapedChar(map, '\t', 't');
		addEscapedChar(map, (char)0x000B, 'v');
		addEscapedChar(map, (char)0x001B, 'e');
		addEscapedChar(map, '\f', 'f');
		addEscapedChar(map, '\\');
		addEscapedChar(map, '$');
		addEscapedChar(map, '\"');
		targetCharValueEscape = map;
	}

	public PHPTarget(CodeGenerator gen) {
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
		result.registerRenderer(String.class, new StringRenderer(), true);

		return result;
	}

	@Override
	public boolean supportsOverloadedMethods() {
		return false;
	}

	@Override
	public String getTargetStringLiteralFromANTLRStringLiteral(CodeGenerator generator, String literal, boolean addQuotes,
															   boolean escapeSpecial) {
		String targetStringLiteral = super.getTargetStringLiteralFromANTLRStringLiteral(generator, literal, addQuotes, escapeSpecial);
		targetStringLiteral = targetStringLiteral.replace("$", "\\$");
		return targetStringLiteral;
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
