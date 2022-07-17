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
	public String getTargetStringLiteralFromANTLRStringLiteral(
		CodeGenerator generator,
		String literal, boolean addQuotes)
	{
		StringBuilder sb = new StringBuilder();
		String is = literal;

		if ( addQuotes ) sb.append('"');

		for (int i = 1; i < is.length() - 1; ) {
			int codePoint = is.codePointAt(i);
			int toAdvance = Character.charCount(codePoint);
			if  (codePoint == '\\') {
				// Anything escaped is what it is! We assume that
				// people know how to escape characters correctly. However
				// we catch anything that does not need an escape in Java (which
				// is what the default implementation is dealing with and remove
				// the escape. The C target does this for instance.
				//
				int escapedCodePoint = is.codePointAt(i + toAdvance);
				toAdvance++;
				switch (escapedCodePoint) {
					// Pass through any escapes that Java also needs
					//
					case    'n':
					case    'r':
					case    't':
					case    'b':
					case    'f':
					case    '\\':
						// Pass the escape through
						sb.append('\\');
						sb.appendCodePoint(escapedCodePoint);
						break;

					case    'u':    // Either unnnn or u{nnnnnn}
						if (is.charAt(i + toAdvance) == '{') {
							while (is.charAt(i + toAdvance) != '}') {
								toAdvance++;
							}

							toAdvance++;
						} else {
							toAdvance += 4;
						}

						if (i + toAdvance <= is.length()) {
							// we might have an invalid \\uAB or something
							String fullEscape = is.substring(i, i + toAdvance);
							appendUnicodeEscapedCodePoint(
								CharSupport.getCharValueFromCharInGrammarLiteral(fullEscape),
								sb);
						}

						break;

					default:
						if (shouldUseUnicodeEscapeForCodePointInDoubleQuotedString(escapedCodePoint)) {
							appendUnicodeEscapedCodePoint(escapedCodePoint, sb);
						} else {
							sb.appendCodePoint(escapedCodePoint);
						}

						break;
				}

				// Go past the \ character
				i++;
			} else {
				if (codePoint == 0x22) {
					// ANTLR doesn't escape " in literal strings, but every other language needs to do so.
					sb.append("\\\"");
				} else if (shouldUseUnicodeEscapeForCodePointInDoubleQuotedString(codePoint)) {
					appendUnicodeEscapedCodePoint(codePoint, sb);
				} else {
					sb.appendCodePoint(codePoint);
				}
			}

			i += toAdvance;
		}

		if ( addQuotes ) sb.append('"');

		return sb.toString();
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
