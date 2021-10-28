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
import java.util.Set;

public class PHPTarget extends Target {
	private static final String[] phpKeywords = {
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
		"__LINE__", "__METHOD__", "__NAMESPACE__", "__TRAIT__"
	};

	private final Set<String> badWords = new HashSet<String>();

	public PHPTarget(CodeGenerator gen) {
		super(gen, "PHP");

		targetCharValueEscape['$'] = "\\$";
	}

    @Override
    public String getVersion() {
        return "4.9.3";
    }

	@Override
	public String encodeIntAsCharEscape(int v) {
		if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
			throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
		}

		return String.format("\\u{%X}", v & 0xFFFF);
	}

    public Set<String> getBadWords() {
		if (badWords.isEmpty()) {
			addBadWords();
		}

		return badWords;
	}

	protected void addBadWords() {
		badWords.addAll(Arrays.asList(phpKeywords));
		badWords.add("rule");
		badWords.add("parserRule");
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
	public boolean supportsOverloadedMethods() {
		return false;
	}

	@Override
	protected void appendUnicodeEscapedCodePoint(int codePoint, StringBuilder sb) {
		UnicodeEscapes.appendPythonStyleEscapedCodePoint(codePoint, sb);
	}

   @Override
   public String getTargetStringLiteralFromANTLRStringLiteral(CodeGenerator generator, String literal, boolean addQuotes) {
	   String targetStringLiteral = super.getTargetStringLiteralFromANTLRStringLiteral(generator, literal, addQuotes);
	   targetStringLiteral = targetStringLiteral.replace("$", "\\$");

	   return targetStringLiteral;
   }
}
