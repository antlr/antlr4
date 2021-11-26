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

public class JavaTarget extends Target {
	/**
	 * The Java target can cache the code generation templates.
	 */
	private static final ThreadLocal<STGroup> targetTemplates = new ThreadLocal<STGroup>();

	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		"abstract", "assert", "boolean", "break", "byte", "case", "catch",
		"char", "class", "const", "continue", "default", "do", "double", "else",
		"enum", "extends", "false", "final", "finally", "float", "for", "goto",
		"if", "implements", "import", "instanceof", "int", "interface",
		"long", "native", "new", "null", "package", "private", "protected",
		"public", "return", "short", "static", "strictfp", "super", "switch",
		"synchronized", "this", "throw", "throws", "transient", "true", "try",
		"void", "volatile", "while",

		// misc
		"rule", "parserRule"
	));

	public JavaTarget(CodeGenerator gen) {
		super(gen);
	}

	@Override
    public Set<String> getReservedWords() {
		return reservedWords;
	}

	@Override
	public int getSerializedATNSegmentLimit() {
		// 65535 is the class file format byte limit for a UTF-8 encoded string literal
		// 3 is the maximum number of bytes it takes to encode a value in the range 0-0xFFFF
		return 65535 / 3;
	}

	@Override
	public boolean isATNSerializedAsInts() {
		return false;
	}
}
