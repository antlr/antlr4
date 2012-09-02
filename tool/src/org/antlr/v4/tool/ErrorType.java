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
//	FILENAME_EXTENSION_ERROR("", ErrorSeverity.ERROR),

	INTERNAL_ERROR(20, "internal error: <arg> <arg2><if(exception)>: <exception><endif>\n" +
				   "<stackTrace; separator=\"\\n\">", ErrorSeverity.ERROR),
	TOKENS_FILE_SYNTAX_ERROR(21, ".tokens file syntax error <arg>:<arg2>", ErrorSeverity.ERROR),
//	CANNOT_GEN_DOT_FILE("", ErrorSeverity.ERROR),

	// Code generation errors
	MISSING_CODE_GEN_TEMPLATES(30, "can't find code generation templates: <arg>", ErrorSeverity.ERROR),
	CANNOT_CREATE_TARGET_GENERATOR(31, "cannot create target <arg> code generator: <exception>", ErrorSeverity.ERROR),
	CODE_TEMPLATE_ARG_ISSUE(32, "code generation template <arg> has missing, misnamed, or incomplete arg list; missing <arg2>", ErrorSeverity.ERROR),
	CODE_GEN_TEMPLATES_INCOMPLETE(33, "missing code generation template <arg>", ErrorSeverity.ERROR),
	NO_MODEL_TO_TEMPLATE_MAPPING(34, "no mapping to template name for output model class <arg>", ErrorSeverity.ERROR),

	// Grammar errors
	SYNTAX_ERROR(50, "<arg>", ErrorSeverity.ERROR),
	RULE_REDEFINITION(51, "rule <arg> redefinition", ErrorSeverity.ERROR),
	LEXER_RULES_NOT_ALLOWED(52, "lexer rule <arg> not allowed in parser", ErrorSeverity.ERROR),
	PARSER_RULES_NOT_ALLOWED(53, "parser rule <arg> not allowed in lexer", ErrorSeverity.ERROR),
    REPEATED_PREQUEL(54, "repeated grammar prequel spec (option, token, or import); please merge", ErrorSeverity.ERROR),
	NO_TOKEN_DEFINITION(55, "no lexer rule corresponding to token: <arg>", ErrorSeverity.ERROR),
	UNDEFINED_RULE_REF(56, "reference to undefined rule: <arg>", ErrorSeverity.ERROR),
	UNDEFINED_RULE_IN_NONLOCAL_REF(57, "reference to undefined rule in nonlocal ref: <arg>::<arg2>", ErrorSeverity.ERROR),
	LITERAL_NOT_ASSOCIATED_WITH_LEXER_RULE(58, "", ErrorSeverity.ERROR),
	CANNOT_ALIAS_TOKENS(59, "can't assign string value to token name <arg> in non-combined grammar", ErrorSeverity.ERROR),
    TOKEN_NAMES_MUST_START_UPPER(60, "token names must start with an uppercase letter: <arg>", ErrorSeverity.ERROR),
	ATTRIBUTE_REF_NOT_IN_RULE(61, "", ErrorSeverity.ERROR),
	INVALID_RULE_SCOPE_ATTRIBUTE_REF(62, "", ErrorSeverity.ERROR),
	UNKNOWN_SIMPLE_ATTRIBUTE(63, "unknown attribute reference <arg> in <arg2>", ErrorSeverity.ERROR),
	INVALID_RULE_PARAMETER_REF(64, "cannot access rule <arg>'s parameter: <arg2>", ErrorSeverity.ERROR),
	UNKNOWN_RULE_ATTRIBUTE(65, "unknown attribute <arg> for rule <arg2> in <arg3>", ErrorSeverity.ERROR),
    UNKNOWN_ATTRIBUTE_IN_SCOPE(66, "attribute <arg> isn't a valid property in <arg2>", ErrorSeverity.ERROR),
	ISOLATED_RULE_REF(67, "missing attribute access on rule reference <arg> in <arg2>", ErrorSeverity.ERROR),
	SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE(68, "", ErrorSeverity.ERROR), // not used
	LABEL_CONFLICTS_WITH_RULE(69, "label <arg> conflicts with rule with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_TOKEN(70, "label <arg> conflicts with token with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RULE_ARG_RETVAL(72, "label <arg> conflicts with rule <arg2>'s return value or parameter with same name", ErrorSeverity.ERROR),
	ATTRIBUTE_CONFLICTS_WITH_RULE(73, "rule <arg2>'s dynamically-scoped attribute <arg> conflicts with the rule name", ErrorSeverity.ERROR),
	ATTRIBUTE_CONFLICTS_WITH_RULE_ARG_RETVAL(74, "rule <arg2>'s dynamically-scoped attribute <arg> conflicts with <arg2>'s return value or parameter", ErrorSeverity.ERROR),
	LABEL_TYPE_CONFLICT(75, "label <arg> type mismatch with previous definition: <arg2>", ErrorSeverity.ERROR),
	ARG_RETVAL_CONFLICT(76, "rule <arg2>'s argument <arg> conflicts a return value with same name", ErrorSeverity.ERROR),
	NONUNIQUE_REF(77, "", ErrorSeverity.ERROR), // not used
	FORWARD_ELEMENT_REF(78, "", ErrorSeverity.ERROR),
	MISSING_RULE_ARGS(79, "missing parameter(s) on rule reference: <arg>", ErrorSeverity.ERROR),
	RULE_HAS_NO_ARGS(80, "rule <arg> has no defined parameters", ErrorSeverity.ERROR),
	ARGS_ON_TOKEN_REF(81, "token reference <arg> may not have parameters", ErrorSeverity.ERROR),
	RULE_REF_AMBIG_WITH_RULE_IN_ALT(82, "", ErrorSeverity.ERROR),
	ILLEGAL_OPTION(83, "illegal option <arg>", ErrorSeverity.WARNING),
	ILLEGAL_OPTION_VALUE(84, "illegal option value <arg>=<arg2>", ErrorSeverity.WARNING),
	LIST_LABEL_INVALID_UNLESS_RETVAL_STRUCT(85, "", ErrorSeverity.ERROR),
	REWRITE_ELEMENT_NOT_PRESENT_ON_LHS(86, "", ErrorSeverity.ERROR),
    UNDEFINED_TOKEN_REF_IN_REWRITE(87, "", ErrorSeverity.ERROR),
	NO_GRAMMAR_START_RULE(88, "", ErrorSeverity.ERROR),
	EMPTY_COMPLEMENT(89, "empty complement", ErrorSeverity.ERROR),
	UNKNOWN_DYNAMIC_SCOPE(90, "unknown dynamic scope: <arg> in <arg2>", ErrorSeverity.ERROR),
	UNKNOWN_DYNAMIC_SCOPE_ATTRIBUTE(91, "unknown dynamically-scoped attribute for scope <arg>: <arg2> in <arg3>", ErrorSeverity.ERROR),
	ISOLATED_RULE_ATTRIBUTE(92, "", ErrorSeverity.ERROR),
	INVALID_ACTION_SCOPE(93, "", ErrorSeverity.ERROR),
    ACTION_REDEFINITION(94, "redefinition of <arg> action", ErrorSeverity.ERROR),
    SCOPE_REDEFINITION(95, "scope <arg> redefinition", ErrorSeverity.ERROR),
	INVALID_TEMPLATE_ACTION(96, "", ErrorSeverity.ERROR),
	ARG_INIT_VALUES_ILLEGAL(97, "", ErrorSeverity.ERROR),
	REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION(98, "", ErrorSeverity.ERROR),
	NO_RULES(99, "<if(arg2.implicitLexerOwner)>implicitly generated <endif>grammar <arg> has no rules", ErrorSeverity.ERROR),
	WRITE_TO_READONLY_ATTR(100, "", ErrorSeverity.ERROR),
	MISSING_AST_TYPE_IN_TREE_GRAMMAR(101, "", ErrorSeverity.ERROR),
	REWRITE_FOR_MULTI_ELEMENT_ALT(102, "", ErrorSeverity.ERROR),
	RULE_INVALID_SET(103, "", ErrorSeverity.ERROR),
	HETERO_ILLEGAL_IN_REWRITE_ALT(104, "", ErrorSeverity.ERROR),
	NO_SUCH_GRAMMAR_SCOPE(105, "reference to undefined grammar in rule reference: <arg>.<arg2>", ErrorSeverity.ERROR),
	NO_SUCH_RULE_IN_SCOPE(106, "rule <arg2> is not defined in grammar <arg>", ErrorSeverity.ERROR),
	TOKEN_STRING_REASSIGNMENT(107, "cannot alias <arg> in tokens {}; string already assigned to <arg2>", ErrorSeverity.ERROR),
	TOKEN_NAME_REASSIGNMENT(108, "cannot redefine <arg>; token name already <if(arg2)>assigned to <arg2><else>defined<endif>", ErrorSeverity.ERROR),
	//TOKEN_VOCAB_IN_DELEGATE(, "tokenVocab option ignored in imported grammar <arg>", ErrorSeverity.ERROR),
	OPTIONS_IN_DELEGATE(109, "options ignored in imported grammar <arg>", ErrorSeverity.WARNING),
//	TOKEN_ALIAS_IN_DELEGATE(, "can't assign string to token name <arg> to string in imported grammar <arg2>", ErrorSeverity.ERROR),
	CANNOT_FIND_IMPORTED_GRAMMAR(110, "can't find or load grammar <arg> from <arg2>", ErrorSeverity.ERROR),
	INVALID_IMPORT(111, "<arg.typeString> grammar <arg.name> cannot import <arg2.typeString> grammar <arg2.name>", ErrorSeverity.ERROR),
	IMPORTED_TOKENS_RULE_EMPTY(112, "", ErrorSeverity.ERROR),
	IMPORT_NAME_CLASH(113, "<arg.typeString> grammar <arg.name> and imported <arg2.typeString> grammar <arg2.name> both generate <arg2.recognizerName>", ErrorSeverity.ERROR),
	AST_OP_WITH_NON_AST_OUTPUT_OPTION(114, " <arg>", ErrorSeverity.ERROR),
	AST_OP_IN_ALT_WITH_REWRITE(115, "", ErrorSeverity.ERROR),
    WILDCARD_AS_ROOT(116, "", ErrorSeverity.ERROR),
    CONFLICTING_OPTION_IN_TREE_FILTER(117, "", ErrorSeverity.ERROR),
	ALL_OPS_NEED_SAME_ASSOC(118, "all operators of alt <arg> of left-recursive rule must have same associativity", ErrorSeverity.WARNING),
	LEFT_RECURSION_CYCLES(119, "The following sets of rules are mutually left-recursive <arg:{c| [<c:{r|<r.name>}; separator=\", \">]}; separator=\" and \">", ErrorSeverity.ERROR),
	MODE_NOT_IN_LEXER(120, "lexical modes are only allowed in lexer grammars", ErrorSeverity.ERROR),
	CANNOT_FIND_ATTRIBUTE_NAME_IN_DECL(121, "cannot find an attribute name in attribute declaration", ErrorSeverity.ERROR),
	RULE_WITH_TOO_FEW_ALT_LABELS(122, "rule <arg>: must label all alternatives or none", ErrorSeverity.ERROR),
	ALT_LABEL_REDEF(123, "rule alt label <arg> redefined in rule <arg2>, originally in <arg3>", ErrorSeverity.ERROR),
	ALT_LABEL_CONFLICTS_WITH_RULE(124, "rule alt label <arg> conflicts with rule <arg2>", ErrorSeverity.ERROR),
	IMPLICIT_TOKEN_DEFINITION(125, "implicit definition of token <arg> in parser", ErrorSeverity.WARNING),
	IMPLICIT_STRING_DEFINITION(126, "cannot create implicit token for string literal <arg> in non-combined grammar", ErrorSeverity.ERROR),
//	ALIAS_REASSIGNMENT(127, "token literal <arg> aliased to new token name <arg2>", ErrorSeverity.WARNING),

	/** Documentation comment is unterminated */
    //UNTERMINATED_DOC_COMMENT(, "", ErrorSeverity.ERROR),

    // Dependency sorting errors
    //
    /** t1.g4 -> t2.g4 -> t3.g4 ->t1.g4 */
    CIRCULAR_DEPENDENCY(130, "your grammars contain a circular dependency and cannot be sorted into a valid build order", ErrorSeverity.ERROR),

    // Simple informational messages
    //
    /** A standby generic message that jsut spits out the arguments it is given */
//    GENERIC_INFO("", ErrorSeverity.INFO, false, false),
//    /** How to print out the version of the ANTLR tool that we are */
//    ANTLR_VERSION("", ErrorSeverity.INFO, false, false),
//
//    // Command line tool errors/warnings
//    /** -fo option was incorrectly formed */
//    MISSING_OUTPUT_FO("", ErrorSeverity.WARNING, false, false),
//    /** -lib option is missing a directory argument */
//    MISSING_LIBDIR("", ErrorSeverity.WARNING, false, false),
//    /** -format option was not given the name of a message format */
//    MISSING_FORMAT("", ErrorSeverity.WARNING, false, false),
//    /** Max state count missing from the option */
//    MISSING_MAXSTATES("", ErrorSeverity.WARNING, false, false),
//    /** Max labels in a switch argument is missing */
//    MISSING_MAXSWITCH("", ErrorSeverity.WARNING, false, false),
//    /** Min labels in a switch argument is missing */
//    MISSING_MINSWITCH("", ErrorSeverity.WARNING, false, false),
//    /** Missing recursion limit argument */
//    MISSING_MAXRECUR("", ErrorSeverity.WARNING, false, false),
//    /** Missing max edges argument */
//    MISSING_MAXEDGE("", ErrorSeverity.WARNING, false, false),
//    /** Misng ms timeout argument */
//    MISSING_MAXTIME("", ErrorSeverity.WARNING, false, false),
//
//    // Help messages
//    HELP_USAGE("", ErrorSeverity.INFO, false, false),
//    HELP_EXTENDED("", ErrorSeverity.INFO, false, false),

    ;

	public final String msg;
    public final int code; // unique, deterministic unchanging error code once we release
    public final ErrorSeverity severity;
    public final Boolean abortsAnalysis;
    public final Boolean abortsCodegen;

	ErrorType(int code, String msg, ErrorSeverity severity) {
        this.code = code;
		this.msg = msg;
        this.severity = severity;
		this.abortsAnalysis = false;
		this.abortsCodegen = false;
	}

//	ErrorType(String msg, ErrorSeverity severity, boolean abortsAnalysis) {
//		this(msg, severity, abortsAnalysis, false);
//	}

//    ErrorType(String msg, ErrorSeverity severity, boolean abortsAnalysis, boolean abortsCodegen) {
//		this.msg = msg;
//        this.severity       = severity;
//        this.abortsAnalysis = abortsAnalysis;
//		this.abortsCodegen = abortsCodegen;
//    }
}
