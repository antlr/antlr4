/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.tool;

import org.antlr.v4.Tool;

/**
 * A complex enumeration of all the error messages that the tool can issue.
 * <p/>
 * When adding error messages, also add a description of the message to the
 * Wiki with a location under the Wiki page
 * <a href="http://www.antlr.org/wiki/display/ANTLR4/Errors+Reported+by+the+ANTLR+Tool">Errors Reported by the ANTLR Tool</a>.
 *
 * @author Jim Idle <jimi@temporal-wave.com>, Terence Parr
 * @since 4.0
 */
public enum ErrorType {
	// Tool errors
	CANNOT_WRITE_FILE(1, "cannot write file '<arg>': <arg2>", ErrorSeverity.ERROR),
	INVALID_CMDLINE_ARG(2, "unknown command-line option '<arg>'", ErrorSeverity.ERROR),
	CANNOT_FIND_TOKENS_FILE(3, "cannot find tokens file '<arg>'", ErrorSeverity.WARNING),
	ERROR_READING_TOKENS_FILE(4, "cannot find tokens file '<arg>': <arg2>", ErrorSeverity.ERROR),
	DIR_NOT_FOUND(5, "directory not found: <arg>", ErrorSeverity.ERROR),
	OUTPUT_DIR_IS_FILE(6, "output directory is a file: <arg>", ErrorSeverity.ERROR),
	CANNOT_OPEN_FILE(7, "cannot find or open file: <arg><if(exception&&verbose)>; reason: <exception><endif>", ErrorSeverity.ERROR),
	FILE_AND_GRAMMAR_NAME_DIFFER(8, "grammar name '<arg>' and file name '<arg2>' differ", ErrorSeverity.ERROR),
	BAD_OPTION_SET_SYNTAX(9, "invalid -Dname=value syntax: '<arg>'", ErrorSeverity.ERROR),
	WARNING_TREATED_AS_ERROR(10, "warning treated as error", ErrorSeverity.ERROR_ONE_OFF),

	INTERNAL_ERROR(20, "internal error: <arg> <arg2><if(exception&&verbose)>: <exception>" +
				   "<stackTrace; separator=\"\\n\"><endif>", ErrorSeverity.ERROR),
	TOKENS_FILE_SYNTAX_ERROR(21, ".tokens file syntax error <arg>:<arg2>", ErrorSeverity.ERROR),
	STRING_TEMPLATE_WARNING(22, "template error: <arg> <arg2><if(exception&&verbose)>: <exception>" +
				   "<stackTrace; separator=\"\\n\"><endif>", ErrorSeverity.WARNING),

	// Code generation errors
	MISSING_CODE_GEN_TEMPLATES(30, "can't find code generation templates: <arg>", ErrorSeverity.ERROR),
	CANNOT_CREATE_TARGET_GENERATOR(31, "ANTLR cannot generate '<arg>' code as of version "+ Tool.VERSION, ErrorSeverity.ERROR),
	CODE_TEMPLATE_ARG_ISSUE(32, "code generation template '<arg>' has missing, misnamed, or incomplete arg list; missing '<arg2>'", ErrorSeverity.ERROR),
	CODE_GEN_TEMPLATES_INCOMPLETE(33, "missing code generation template '<arg>'", ErrorSeverity.ERROR),
	NO_MODEL_TO_TEMPLATE_MAPPING(34, "no mapping to template name for output model class '<arg>'", ErrorSeverity.ERROR),

	// Grammar errors
	SYNTAX_ERROR(50, "syntax error: <arg>", ErrorSeverity.ERROR),
	RULE_REDEFINITION(51, "rule '<arg>' redefinition; previous at line <arg2>", ErrorSeverity.ERROR),
	LEXER_RULES_NOT_ALLOWED(52, "lexer rule '<arg>' not allowed in parser", ErrorSeverity.ERROR),
	PARSER_RULES_NOT_ALLOWED(53, "parser rule '<arg>' not allowed in lexer", ErrorSeverity.ERROR),
    REPEATED_PREQUEL(54, "repeated grammar prequel spec (option, token, or import); please merge", ErrorSeverity.ERROR),
	UNDEFINED_RULE_REF(56, "reference to undefined rule: <arg>", ErrorSeverity.ERROR),
	UNDEFINED_RULE_IN_NONLOCAL_REF(57, "reference to undefined rule '<arg>' in non-local ref '<arg3>'", ErrorSeverity.ERROR),
    TOKEN_NAMES_MUST_START_UPPER(60, "token names must start with an uppercase letter: <arg>", ErrorSeverity.ERROR),
	UNKNOWN_SIMPLE_ATTRIBUTE(63, "unknown attribute reference '<arg>' in '<arg2>'", ErrorSeverity.ERROR),
	INVALID_RULE_PARAMETER_REF(64, "parameter '<arg>' of rule '<arg2>' is not accessible in this scope: <arg3>", ErrorSeverity.ERROR),
	UNKNOWN_RULE_ATTRIBUTE(65, "unknown attribute '<arg>' for rule '<arg2>' in '<arg3>'", ErrorSeverity.ERROR),
    UNKNOWN_ATTRIBUTE_IN_SCOPE(66, "attribute '<arg>' isn't a valid property in '<arg2>'", ErrorSeverity.ERROR),
	ISOLATED_RULE_REF(67, "missing attribute access on rule reference '<arg>' in '<arg2>'", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RULE(69, "label '<arg>' conflicts with rule with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_TOKEN(70, "label '<arg>' conflicts with token with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_ARG(72, "label '<arg>' conflicts with parameter with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RETVAL(73, "label '<arg>' conflicts with return value with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_LOCAL(74, "label '<arg>' conflicts with local with same name", ErrorSeverity.ERROR),
	LABEL_TYPE_CONFLICT(75, "label '<arg>' type mismatch with previous definition: <arg2>", ErrorSeverity.ERROR),
	RETVAL_CONFLICTS_WITH_ARG(76, "return value '<arg>' conflicts with parameter with same name", ErrorSeverity.ERROR),
	MISSING_RULE_ARGS(79, "missing arguments(s) on rule reference: <arg>", ErrorSeverity.ERROR),
	RULE_HAS_NO_ARGS(80, "rule '<arg>' has no defined parameters", ErrorSeverity.ERROR),
	ILLEGAL_OPTION(83, "unsupported option '<arg>'", ErrorSeverity.WARNING),
	ILLEGAL_OPTION_VALUE(84, "unsupported option value '<arg>=<arg2>'", ErrorSeverity.WARNING),
    ACTION_REDEFINITION(94, "redefinition of '<arg>' action", ErrorSeverity.ERROR),
	NO_RULES(99, "<if(arg2.implicitLexerOwner)>implicitly generated <endif>grammar '<arg>' has no rules", ErrorSeverity.ERROR),
	NO_SUCH_GRAMMAR_SCOPE(105, "reference to undefined grammar in rule reference: <arg>.<arg2>", ErrorSeverity.ERROR),
	NO_SUCH_RULE_IN_SCOPE(106, "rule '<arg2>' is not defined in grammar '<arg>'", ErrorSeverity.ERROR),
	TOKEN_NAME_REASSIGNMENT(108, "token name '<arg>' is already defined", ErrorSeverity.WARNING),
	OPTIONS_IN_DELEGATE(109, "options ignored in imported grammar '<arg>'", ErrorSeverity.WARNING),
	CANNOT_FIND_IMPORTED_GRAMMAR(110, "can't find or load grammar '<arg>' from '<arg2>'", ErrorSeverity.ERROR),
	INVALID_IMPORT(111, "<arg.typeString> grammar '<arg.name>' cannot import <arg2.typeString> grammar '<arg2.name>'", ErrorSeverity.ERROR),
	IMPORT_NAME_CLASH(113, "<arg.typeString> grammar '<arg.name>' and imported <arg2.typeString> grammar '<arg2.name>' both generate '<arg2.recognizerName>'", ErrorSeverity.ERROR),
	ALL_OPS_NEED_SAME_ASSOC(118, "all operators of alt '<arg>' of left-recursive rule must have same associativity", ErrorSeverity.WARNING),
	LEFT_RECURSION_CYCLES(119, "The following sets of rules are mutually left-recursive <arg:{c| [<c:{r|<r.name>}; separator=\", \">]}; separator=\" and \">", ErrorSeverity.ERROR),
	MODE_NOT_IN_LEXER(120, "lexical modes are only allowed in lexer grammars", ErrorSeverity.ERROR),
	CANNOT_FIND_ATTRIBUTE_NAME_IN_DECL(121, "cannot find an attribute name in attribute declaration", ErrorSeverity.ERROR),
	RULE_WITH_TOO_FEW_ALT_LABELS(122, "rule '<arg>': must label all alternatives or none", ErrorSeverity.ERROR),
	ALT_LABEL_REDEF(123, "rule alt label '<arg>' redefined in rule '<arg2>', originally in rule '<arg3>'", ErrorSeverity.ERROR),
	ALT_LABEL_CONFLICTS_WITH_RULE(124, "rule alt label '<arg>' conflicts with rule '<arg2>'", ErrorSeverity.ERROR),
	IMPLICIT_TOKEN_DEFINITION(125, "implicit definition of token '<arg>' in parser", ErrorSeverity.WARNING),
	IMPLICIT_STRING_DEFINITION(126, "cannot create implicit token for string literal in non-combined grammar: <arg>", ErrorSeverity.ERROR),
	ATTRIBUTE_IN_LEXER_ACTION(128, "attribute references not allowed in lexer actions: $<arg>", ErrorSeverity.ERROR),
	LABEL_BLOCK_NOT_A_SET(130, "label '<arg>' assigned to a block which is not a set", ErrorSeverity.ERROR),
	EXPECTED_NON_GREEDY_WILDCARD_BLOCK(131, "greedy block ()<arg> contains wildcard; the non-greedy syntax ()<arg>? may be preferred", ErrorSeverity.WARNING),
	LEXER_ACTION_PLACEMENT_ISSUE(132, "action in lexer rule '<arg>' must be last element of single outermost alt", ErrorSeverity.ERROR),
	LEXER_COMMAND_PLACEMENT_ISSUE(133, "->command in lexer rule '<arg>' must be last element of single outermost alt", ErrorSeverity.ERROR),
	USE_OF_BAD_WORD(134, "symbol '<arg>' conflicts with generated code in target language or runtime", ErrorSeverity.ERROR),
	UNSUPPORTED_REFERENCE_IN_LEXER_SET(134, "rule reference '<arg>' is not currently supported in a set", ErrorSeverity.ERROR),
	ASSIGNMENT_TO_LIST_LABEL(135, "cannot assign a value to list label '<arg>'", ErrorSeverity.ERROR),
	RETVAL_CONFLICTS_WITH_RULE(136, "return value '<arg>' conflicts with rule with same name", ErrorSeverity.ERROR),
	RETVAL_CONFLICTS_WITH_TOKEN(137, "return value '<arg>' conflicts with token with same name", ErrorSeverity.ERROR),
	ARG_CONFLICTS_WITH_RULE(138, "parameter '<arg>' conflicts with rule with same name", ErrorSeverity.ERROR),
	ARG_CONFLICTS_WITH_TOKEN(139, "parameter '<arg>' conflicts with token with same name", ErrorSeverity.ERROR),
	LOCAL_CONFLICTS_WITH_RULE(140, "local '<arg>' conflicts with rule with same name", ErrorSeverity.ERROR),
	LOCAL_CONFLICTS_WITH_TOKEN(141, "local '<arg>' conflicts with rule token same name", ErrorSeverity.ERROR),
	LOCAL_CONFLICTS_WITH_ARG(142, "local '<arg>' conflicts with parameter with same name", ErrorSeverity.ERROR),
	LOCAL_CONFLICTS_WITH_RETVAL(143, "local '<arg>' conflicts with return value with same name", ErrorSeverity.ERROR),
	INVALID_LITERAL_IN_LEXER_SET(144, "multi-character literals are not allowed in lexer sets: <arg>", ErrorSeverity.ERROR),
	MODE_WITHOUT_RULES(145, "lexer mode '<arg>' must contain at least one non-fragment rule", ErrorSeverity.ERROR),
	/**
	 * All non-fragment lexer rules must match at least one character.
	 * <p/>
	 * The following example shows this error.
	 *
	 * <pre>
	 * Whitespace : [ \t]+;  // ok
	 * Whitespace : [ \t];   // ok
	 *
	 * fragment WS : [ \t]*; // ok
	 *
	 * Whitespace : [ \t]*;  // error 146
	 * </pre>
	 */
	EPSILON_TOKEN(146, "non-fragment lexer rule '<arg>' can match the empty string", ErrorSeverity.WARNING),
	/**
	 * Left-recursive rules must contain at least one alternative which is not
	 * left recursive.
	 * <p/>
	 * The following rule produces this error.
	 *
	 * <pre>
	 * // error 147:
	 * a : a ID
	 *   | a INT
	 *   ;
	 * </pre>
	 */
	NO_NON_LR_ALTS(147, "left recursive rule '<arg>' must contain an alternative which is not left recursive", ErrorSeverity.ERROR),
	/**
	 * In left-recursive rules, all left-recursive alternatives must match at
	 * least one symbol following the recursive rule invocation.
	 * <p/>
	 * The following rule produces this error.
	 *
	 * <pre>
	 * a : ID    // ok        (alternative is not left recursive)
	 *   | a INT // ok        (a must be follow by INT)
	 *   | a ID? // error 148 (the ID following a is optional)
	 *   ;
	 * </pre>
	 */
	EPSILON_LR_FOLLOW(148, "left recursive rule '<arg>' contains a left recursive alternative which can be followed by the empty string", ErrorSeverity.ERROR),
	/**
	 * Each lexer command requires an explicit implementation in the target
	 * templates. This error indicates that the command was incorrectly written
	 * or is not supported by the current target.
	 * <p/>
	 * The following rule produces this error.
	 *
	 * <pre>
	 * X : 'foo' -> type(Foo);  // ok
	 * Y : 'foo' -> token(Foo); // error 149 (token is not a supported lexer command)
	 * </pre>
	 */
	INVALID_LEXER_COMMAND(149, "lexer command '<arg>' does not exist or is not supported by the current target", ErrorSeverity.ERROR),
	/**
	 * Some lexer commands require an argument.
	 * <p/>
	 * The following rule produces this error.
	 *
	 * <pre>
	 * X : 'foo' -> type(Foo); // ok
	 * Y : 'foo' -> type;      // error 150 (the type command requires an argument)
	 * </pre>
	 */
	MISSING_LEXER_COMMAND_ARGUMENT(150, "missing argument for lexer command '<arg>'", ErrorSeverity.ERROR),
	/**
	 * A lexer command which does not take parameters was invoked with an
	 * argument.
	 * <p/>
	 * The following rule produces this error.
	 *
	 * <pre>
	 * X : 'foo' -> popMode;    // ok
	 * Y : 'foo' -> popMode(A); // error 151 (the popMode command does not take an argument)
	 * </pre>
	 */
	UNWANTED_LEXER_COMMAND_ARGUMENT(151, "lexer command '<arg>' does not take any arguments", ErrorSeverity.ERROR),
	/**
	 * The parser contains an unterminated string literal.
	 * <p/>
	 * The following rule produces this error.
	 *
	 * <pre>
	 * x : 'x'; // ok
	 * y : 'y;  // error 152
	 * </pre>
	 */
	UNTERMINATED_STRING_LITERAL(152, "unterminated string literal", ErrorSeverity.ERROR),
	/**
	 * A rule contains a closure ({@code (...)*}) or positive closure
	 * ({@code (...)+}) around an empty alternative.
	 * <p/>
	 * The following rule produces this error.
	 *
	 * <pre>
	 * x  : ;
	 * y  : x+;                                // error 153
	 * z1 : ('foo' | 'bar'? 'bar2'?)*;         // error 153
	 * z2 : ('foo' | 'bar' 'bar2'? | 'bar2')*; // ok
	 * </pre>
	 */
	EPSILON_CLOSURE(153, "rule '<arg>' contains a closure with at least one alternative that can match an empty string", ErrorSeverity.ERROR),
	/**
	 * A rule contains an optional block ({@code (...)?}) around an empty
	 * alternative.
	 * <p/>
	 * The following rule produces this warning.
	 *
	 * <pre>
	 * x  : ;
	 * y  : x?;                                // warning 154
	 * z1 : ('foo' | 'bar'? 'bar2'?)?;         // warning 154
	 * z2 : ('foo' | 'bar' 'bar2'? | 'bar2')?; // ok
	 * </pre>
	 */
	EPSILON_OPTIONAL(154, "rule '<arg>' contains an optional block with at least one alternative that can match an empty string", ErrorSeverity.WARNING),

	// Backward incompatibility errors
	V3_TREE_GRAMMAR(200, "tree grammars are not supported in ANTLR 4", ErrorSeverity.ERROR),
	V3_LEXER_LABEL(201, "labels in lexer rules are not supported in ANTLR 4; " +
		"actions cannot reference elements of lexical rules but you can use " +
		"getText() to get the entire text matched for the rule", ErrorSeverity.WARNING),
	V3_TOKENS_SYNTAX(202, "'tokens {A; B;}' syntax is now 'tokens {A, B}' in ANTLR 4", ErrorSeverity.WARNING),
	V3_ASSIGN_IN_TOKENS(203, "assignments in tokens{} are not supported in ANTLR 4; use lexical rule '<arg> : <arg2>;' instead", ErrorSeverity.ERROR),
	V3_GATED_SEMPRED(204, "{...}?=> explicitly gated semantic predicates are deprecated in ANTLR 4; use {...}? instead", ErrorSeverity.WARNING),
	V3_SYNPRED(205, "(...)=> syntactic predicates are not supported in ANTLR 4", ErrorSeverity.ERROR),

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
