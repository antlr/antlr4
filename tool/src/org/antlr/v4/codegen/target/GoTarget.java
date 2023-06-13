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
		"rule", "parserRule", "action",

		// the use of start or stop abd others as a label name will cause the generation of a GetStart() or GetStop() method, which
		// then clashes with the GetStart() or GetStop() method that is generated by the code gen for the rule. So, we need to
		// convert it. This is not ideal as it will still probably confuse authors of parse listeners etc. but the code will
		// compile. This is a proof of Hyrum's law.
		"start", "stop", "exception"
	));

	public GoTarget(CodeGenerator gen) {
		super(gen);
	}

	@Override
	protected Set<String> getReservedWords() {
		return reservedWords;
	}

	@Override
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

	@Override
	public String getListenerFileName(boolean header) {
		return gen.g.name.toLowerCase()+"_listener.go";
	}

	@Override
	public String getVisitorFileName(boolean header) {
		return gen.g.name.toLowerCase()+"_visitor.go";
	}

	@Override
	public String getBaseListenerFileName(boolean header) {
		return gen.g.name.toLowerCase()+"_base_listener.go";
	}

	@Override
	public String getBaseVisitorFileName(boolean header) {
		return gen.g.name.toLowerCase()+"_base_visitor.go";
	}
}
