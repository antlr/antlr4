/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.Grammar;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GoTarget extends Target {
	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		// keywords
		"break", "default", "func", "interface", "select",
		"case", "defer", "go", "map", "struct",
		"chan", "else", "goto", "package", "switch",
		"const", "fallthrough", "if", "range", "type",
		"continue", "for", "import", "return", "var",

		// predeclared identifiers https://golang.org/ref/spec#Predeclared_identifiers
		"bool", "byte", "complex64", "complex128", "error", "float32", "float64",
		"int", "int8", "int16", "int32", "int64", "rune", "string",
		"uint", "uint8", "uint16", "uint32", "uint64", "uintptr",
		"true", "false", "iota", "nil",
		"append", "cap", "close", "complex", "copy", "delete", "imag", "len",
		"make", "new", "panic", "print", "println", "real", "recover",
		"string",

		// interface definition of RuleContext from runtime/Go/antlr/rule_context.go
		"Accept", "GetAltNumber", "GetBaseRuleContext", "GetChild", "GetChildCount",
		"GetChildren", "GetInvokingState", "GetParent", "GetPayload", "GetRuleContext",
		"GetRuleIndex", "GetSourceInterval", "GetText", "IsEmpty", "SetAltNumber",
		"SetInvokingState", "SetParent", "String",

		// misc
		"rule", "parserRule", "action"
	));

	public GoTarget(CodeGenerator gen) {
		super(gen);
	}

	@Override
	protected Set<String> getReservedWords() {
		return reservedWords;
	}

	public String getRecognizerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g!=null;
		String name;
		switch ( g.getType()) {
			case ANTLRParser.PARSER:
				name = g.name.endsWith("Parser") ? g.name.substring(0, g.name.length()-6) : g.name;
				return name.toLowerCase()+"_parser.go";
			case ANTLRParser.LEXER:
				name = g.name.endsWith("Lexer") ? g.name.substring(0, g.name.length()-5) : g.name; // trim off "lexer"
				return name.toLowerCase()+"_lexer.go";
			case ANTLRParser.COMBINED:
				return g.name.toLowerCase()+"_parser.go";
			default :
				return "INVALID_FILE_NAME";
		}
	}

	/** A given grammar T, return the listener name such as
	 *  TListener.java, if we're using the Java target.
 	 */
	public String getListenerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_listener.go";
	}

	/** A given grammar T, return the visitor name such as
	 *  TVisitor.java, if we're using the Java target.
 	 */
	public String getVisitorFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_visitor.go";
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseListenerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_base_listener.go";
	}

	/** A given grammar T, return a blank listener implementation
	 *  such as TBaseListener.java, if we're using the Java target.
 	 */
	public String getBaseVisitorFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_base_visitor.go";
	}
}
