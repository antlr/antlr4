/*
 * Copyright 20162022 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.misc.CharSupport;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TypeScriptTarget extends Target {

	/* source: https://github.com/microsoft/TypeScript/blob/fad889283e710ee947e8412e173d2c050107a3c1/src/compiler/scanner.ts */
	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
			"any",
			"as",
			"boolean",
			"break",
			"case",
			"catch",
			"class",
			"continue",
			"const",
			"constructor",
			"debugger",
			"declare",
			"default",
			"delete",
			"do",
			"else",
			"enum",
			"export",
			"extends",
			"false",
			"finally",
			"for",
			"from",
			"function",
			"get",
			"if",
			"implements",
			"import",
			"in",
			"instanceof",
			"interface",
			"let",
			"module",
			"new",
			"null",
			"number",
			"package",
			"private",
			"protected",
			"public",
			"require",
			"return",
			"set",
			"static",
			"string",
			"super",
			"switch",
			"symbol",
			"this",
			"throw",
			"true",
			"try",
			"type",
			"typeof",
			"var",
			"void",
			"while",
			"with",
			"yield",
			"of"
	));

	public TypeScriptTarget(CodeGenerator gen) {
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
		return true;
	}

	@Override
	public boolean isATNSerializedAsInts() {
		return true;
	}

}
