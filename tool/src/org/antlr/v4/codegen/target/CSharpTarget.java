/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.tool.ErrorType;
import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.misc.STMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CSharpTarget extends Target {
	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		"abstract",
		"as",
		"base",
		"bool",
		"break",
		"byte",
		"case",
		"catch",
		"char",
		"checked",
		"class",
		"const",
		"continue",
		"decimal",
		"default",
		"delegate",
		"do",
		"double",
		"else",
		"enum",
		"event",
		"explicit",
		"extern",
		"false",
		"finally",
		"fixed",
		"float",
		"for",
		"foreach",
		"goto",
		"if",
		"implicit",
		"in",
		"int",
		"interface",
		"internal",
		"is",
		"lock",
		"long",
		"namespace",
		"new",
		"null",
		"object",
		"operator",
		"out",
		"override",
		"params",
		"private",
		"protected",
		"public",
		"readonly",
		"ref",
		"return",
		"sbyte",
		"sealed",
		"short",
		"sizeof",
		"stackalloc",
		"static",
		"string",
		"struct",
		"switch",
		"this",
		"throw",
		"true",
		"try",
		"typeof",
		"uint",
		"ulong",
		"unchecked",
		"unsafe",
		"ushort",
		"using",
		"virtual",
		"values",
		"void",
		"volatile",
		"while"
	));

	public CSharpTarget(CodeGenerator gen) {
		super(gen);
		targetCharValueEscape[0] = "\\0";
		targetCharValueEscape[0x0007] = "\\a";
		targetCharValueEscape[0x000B] = "\\v";
	}

	@Override
	protected Set<String> getReservedWords() {
		return reservedWords;
	}

	@Override
	protected String escapeWord(String word) {
		return "@" + word;
	}

	@Override
	public String encodeIntAsCharEscape(int v) {
		if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
			throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
		}

		String formatted;
		if (v >= 0 && v < targetCharValueEscape.length && targetCharValueEscape[v] != null) {
			formatted = targetCharValueEscape[v];
		}
		else if (v >= 0x20 && v < 127 && (v < '0' || v > '9') && (v < 'a' || v > 'f') && (v < 'A' || v > 'F')) {
			formatted = Character.toString((char)v);
		}
		else {
			formatted = String.format("\\x%X", v & 0xFFFF);
		}

		return "'" + formatted + "'";
	}

	@Override
	protected STGroup loadTemplates() {
		// override the superclass behavior to put all C# templates in the same folder
		STGroup result = new STGroupFile(CodeGenerator.TEMPLATE_ROOT+"/CSharp/"+ getLanguage()+STGroup.GROUP_FILE_EXTENSION);
		result.registerRenderer(Integer.class, new NumberRenderer());
		result.registerRenderer(String.class, new StringRenderer());
		result.setListener(new STErrorListener() {
			@Override
			public void compileTimeError(STMessage msg) {
				reportError(msg);
			}

			@Override
			public void runTimeError(STMessage msg) {
				reportError(msg);
			}

			@Override
			public void IOError(STMessage msg) {
				reportError(msg);
			}

			@Override
			public void internalError(STMessage msg) {
				reportError(msg);
			}

			private void reportError(STMessage msg) {
				getCodeGenerator().tool.errMgr.toolError(ErrorType.STRING_TEMPLATE_WARNING, msg.cause, msg.toString());
			}
		});

		return result;
	}
}
