/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaScriptTarget extends Target {
	/** Source: https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Lexical_grammar */
	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		"break", "case", "class", "catch", "const", "continue", "debugger",
		"default", "delete", "do", "else", "export", "extends", "finally", "for",
		"function", "if", "import", "in", "instanceof", "let", "new", "return",
		"super", "switch", "this", "throw", "try", "typeof", "var", "void",
		"while", "with", "yield",

		//future reserved
		"enum", "await", "implements", "package", "protected", "static",
		"interface", "private", "public",

		//future reserved in older standards
		"abstract", "boolean", "byte", "char", "double", "final", "float",
		"goto", "int", "long", "native", "short", "synchronized", "transient",
		"volatile",

		//literals
		"null", "true", "false",

		// misc
		"rule", "parserRule"
	));

	public JavaScriptTarget(CodeGenerator gen) {
		super(gen);
	}

	@Override
	protected Set<String> getReservedWords() {
		return reservedWords;
	}

	@Override
	public int getInlineTestSetWordSize() {
		return 32;
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
	public boolean isATNSerializedAsInts() {
		return true;
	}
}
