/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.tool;

import org.antlr.v4.Tool;

/**
 * A complex enumeration of all the error messages that the tool can issue.
 *
 * When adding error messages, also add a description of the message to the
 * Wiki with a location under the Wiki page
 * <a href="http://www.antlr.org/wiki/display/ANTLR4/Errors+Reported+by+the+ANTLR+Tool">Errors Reported by the ANTLR Tool</a>.
 *
 * TODO: add notion of single issuance of an error; some don't need to be repeated; AST_OP_IN_ALT_WITH_REWRITE and option issues
 *
 * @author Jim Idle <jimi@temporal-wave.com>, Terence Parr
 * @since 4.0
 */
public enum ErrorType {
    INVALID(0, "<INVALID>", ErrorSeverity.ERROR),

	// Tool errors
	CANNOT_WRITE_FILE(1, "cannot write file <arg>: <arg2>", ErrorSeverity.ERROR),
	INVALID_CMDLINE_ARG(2, "unknown command-line option <arg>", ErrorSeverity.ERROR),
	CANNOT_FIND_TOKENS_FILE(3, "cannot find tokens file <arg>", ErrorSeverity.ERROR),
	ERROR_READING_TOKENS_FILE(4, "cannot find tokens file <arg>: <arg2>", ErrorSeverity.ERROR),
	DIR_NOT_FOUND(5, "directory not found: <arg>", ErrorSeverity.ERROR),
	OUTPUT_DIR_IS_FILE(6, "output directory is a file: <arg>", ErrorSeverity.ERROR),
	CANNOT_OPEN_FILE(7, "cannot find or open file: <arg><if(exception)>; reason: <exception><endif>", ErrorSeverity.ERROR),
	FILE_AND_GRAMMAR_NAME_DIFFER(8, "grammar name <arg> and file name <arg2> differ", ErrorSeverity.ERROR),
	BAD_OPTION_SET_SYNTAX(9, "invalid -Dname=value syntax: <arg>", ErrorSeverity.ERROR),
	WARNING_TREATED_AS_ERROR(10, "warning treated as error", ErrorSeverity.ERROR),

	INTERNAL_ERROR(20, "internal error: <arg> <arg2><if(exception)>: <exception><endif>" +
				   "<stackTrace; separator=\"\\n\">", ErrorSeverity.ERROR),
	TOKENS_FILE_SYNTAX_ERROR(21, ".tokens file syntax error <arg>:<arg2>", ErrorSeverity.ERROR),

	// Code generation errors
	MISSING_CODE_GEN_TEMPLATES(30, "can't find code generation templates: <arg>", ErrorSeverity.ERROR),
	CANNOT_CREATE_TARGET_GENERATOR(31, "ANTLR cannot generate <arg> code as of version "+ Tool.getVersion(), ErrorSeverity.ERROR),
	CODE_TEMPLATE_ARG_ISSUE(32, "code generation template <arg> has missing, misnamed, or incomplete arg list; missing <arg2>", ErrorSeverity.ERROR),
	CODE_GEN_TEMPLATES_INCOMPLETE(33, "missing code generation template <arg>", ErrorSeverity.ERROR),
	NO_MODEL_TO_TEMPLATE_MAPPING(34, "no mapping to template name for output model class <arg>", ErrorSeverity.ERROR),

	// Grammar errors
	SYNTAX_ERROR(50, "<arg>", ErrorSeverity.ERROR),
	RULE_REDEFINITION(51, "rule <arg> redefinition (ignoring); previous at line <arg2>", ErrorSeverity.ERROR),
	LEXER_RULES_NOT_ALLOWED(52, "lexer rule <arg> not allowed in parser", ErrorSeverity.ERROR),
	PARSER_RULES_NOT_ALLOWED(53, "parser rule <arg> not allowed in lexer", ErrorSeverity.ERROR),
    REPEATED_PREQUEL(54, "repeated grammar prequel spec (option, token, or import); please merge", ErrorSeverity.ERROR),
	UNDEFINED_RULE_REF(56, "reference to undefined rule: <arg>", ErrorSeverity.ERROR),
	UNDEFINED_RULE_IN_NONLOCAL_REF(57, "reference to undefined rule in nonlocal ref: <arg>::<arg2>", ErrorSeverity.ERROR),
    TOKEN_NAMES_MUST_START_UPPER(60, "token names must start with an uppercase letter: <arg>", ErrorSeverity.ERROR),
	UNKNOWN_SIMPLE_ATTRIBUTE(63, "unknown attribute reference <arg> in <arg2>", ErrorSeverity.ERROR),
	INVALID_RULE_PARAMETER_REF(64, "cannot access rule <arg>'s parameter: <arg2>", ErrorSeverity.ERROR),
	UNKNOWN_RULE_ATTRIBUTE(65, "unknown attribute <arg> for rule <arg2> in <arg3>", ErrorSeverity.ERROR),
    UNKNOWN_ATTRIBUTE_IN_SCOPE(66, "attribute <arg> isn't a valid property in <arg2>", ErrorSeverity.ERROR),
	ISOLATED_RULE_REF(67, "missing attribute access on rule reference <arg> in <arg2>", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RULE(69, "label <arg> conflicts with rule with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_TOKEN(70, "label <arg> conflicts with token with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RULE_ARG_RETVAL(72, "label <arg> conflicts with rule <arg2>'s return value or parameter with same name", ErrorSeverity.ERROR),
	LABEL_TYPE_CONFLICT(75, "label <arg> type mismatch with previous definition: <arg2>", ErrorSeverity.ERROR),
	ARG_RETVAL_CONFLICT(76, "rule <arg2>'s argument <arg> conflicts a return value with same name", ErrorSeverity.ERROR),
	MISSING_RULE_ARGS(79, "missing parameter(s) on rule reference: <arg>", ErrorSeverity.ERROR),
	RULE_HAS_NO_ARGS(80, "rule <arg> has no defined parameters", ErrorSeverity.ERROR),
	ILLEGAL_OPTION(83, "illegal option <arg>", ErrorSeverity.WARNING),
	ILLEGAL_OPTION_VALUE(84, "illegal option value <arg>=<arg2>", ErrorSeverity.WARNING),
    ACTION_REDEFINITION(94, "redefinition of <arg> action", ErrorSeverity.ERROR),
	NO_RULES(99, "<if(arg2.implicitLexerOwner)>implicitly generated <endif>grammar <arg> has no rules", ErrorSeverity.ERROR),
	NO_SUCH_GRAMMAR_SCOPE(105, "reference to undefined grammar in rule reference: <arg>.<arg2>", ErrorSeverity.ERROR),
	NO_SUCH_RULE_IN_SCOPE(106, "rule <arg2> is not defined in grammar <arg>", ErrorSeverity.ERROR),
	TOKEN_NAME_REASSIGNMENT(108, "token name <arg> is already defined", ErrorSeverity.WARNING),
	OPTIONS_IN_DELEGATE(109, "options ignored in imported grammar <arg>", ErrorSeverity.WARNING),
	CANNOT_FIND_IMPORTED_GRAMMAR(110, "can't find or load grammar <arg> from <arg2>", ErrorSeverity.ERROR),
	INVALID_IMPORT(111, "<arg.typeString> grammar <arg.name> cannot import <arg2.typeString> grammar <arg2.name>", ErrorSeverity.ERROR),
	IMPORT_NAME_CLASH(113, "<arg.typeString> grammar <arg.name> and imported <arg2.typeString> grammar <arg2.name> both generate <arg2.recognizerName>", ErrorSeverity.ERROR),
	ALL_OPS_NEED_SAME_ASSOC(118, "all operators of alt <arg> of left-recursive rule must have same associativity", ErrorSeverity.WARNING),
	LEFT_RECURSION_CYCLES(119, "The following sets of rules are mutually left-recursive <arg:{c| [<c:{r|<r.name>}; separator=\", \">]}; separator=\" and \">", ErrorSeverity.ERROR),
	MODE_NOT_IN_LEXER(120, "lexical modes are only allowed in lexer grammars", ErrorSeverity.ERROR),
	CANNOT_FIND_ATTRIBUTE_NAME_IN_DECL(121, "cannot find an attribute name in attribute declaration", ErrorSeverity.ERROR),
	RULE_WITH_TOO_FEW_ALT_LABELS(122, "rule <arg>: must label all alternatives or none", ErrorSeverity.ERROR),
	ALT_LABEL_REDEF(123, "rule alt label <arg> redefined in rule <arg2>, originally in <arg3>", ErrorSeverity.ERROR),
	ALT_LABEL_CONFLICTS_WITH_RULE(124, "rule alt label <arg> conflicts with rule <arg2>", ErrorSeverity.ERROR),
	IMPLICIT_TOKEN_DEFINITION(125, "implicit definition of token <arg> in parser", ErrorSeverity.WARNING),
	IMPLICIT_STRING_DEFINITION(126, "cannot create implicit token for string literal <arg> in non-combined grammar", ErrorSeverity.ERROR),
	ATTRIBUTE_IN_LEXER_ACTION(128, "attribute references not allowed in lexer actions: $<arg>", ErrorSeverity.ERROR),
	LABEL_BLOCK_NOT_A_SET(130, "label <arg> assigned to a block which is not a set", ErrorSeverity.ERROR),
	EXPECTED_NON_GREEDY_WILDCARD_BLOCK(131, "greedy block ()<arg> contains wildcard; the non-greedy syntax ()<arg>? may be preferred", ErrorSeverity.WARNING),
	LEXER_ACTION_PLACEMENT_ISSUE(132, "action in lexer rule <arg> must be last element of single outermost alt", ErrorSeverity.ERROR),
	LEXER_COMMAND_PLACEMENT_ISSUE(133, "->command in lexer rule <arg> must be last element of single outermost alt", ErrorSeverity.ERROR),
	USE_OF_BAD_WORD(134, "symbol <arg> conflicts with generated code in target language or runtime", ErrorSeverity.ERROR),

	// Backward incompatibility errors
	V3_TREE_GRAMMAR(200, "tree grammars are not supported in ANTLR v4", ErrorSeverity.ERROR_ONE_OFF),
	V3_LEXER_LABEL(201, "labels in lexer rules are not supported in ANTLR v4; " +
		"actions cannot reference elements of lexical rules but you can use " +
		"getText() to get the entire text matched for the rule", ErrorSeverity.WARNING),

    // Dependency sorting errors

    /** t1.g4 -> t2.g4 -> t3.g4 ->t1.g4 */
    //CIRCULAR_DEPENDENCY(200, "your grammars contain a circular dependency and cannot be sorted into a valid build order", ErrorSeverity.ERROR),
	;

	public final String msg;
    public final int code; // unique, deterministic unchanging error code once we release
    public final ErrorSeverity severity;

	ErrorType(int code, String msg, ErrorSeverity severity) {
        this.code = code;
		this.msg = msg;
        this.severity = severity;
	}
}
