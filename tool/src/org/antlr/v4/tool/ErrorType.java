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
    INVALID("<INVALID>", ErrorSeverity.ERROR),

	CANNOT_WRITE_FILE("cannot write file <arg>: <arg2>", ErrorSeverity.ERROR),
	CANNOT_CLOSE_FILE("", ErrorSeverity.ERROR),
	CANNOT_FIND_TOKENS_FILE("", ErrorSeverity.ERROR),
	ERROR_READING_TOKENS_FILE("", ErrorSeverity.ERROR),
	DIR_NOT_FOUND("directory not found: <arg>", ErrorSeverity.ERROR),
	OUTPUT_DIR_IS_FILE("output directory is a file: <arg>", ErrorSeverity.ERROR),
	CANNOT_OPEN_FILE("cannot find or open file: <arg><if(exception)>; reason: <exception><endif>", ErrorSeverity.ERROR),
	FILE_AND_GRAMMAR_NAME_DIFFER("grammar name <arg> and file name <arg2> differ", ErrorSeverity.ERROR),
//	FILENAME_EXTENSION_ERROR("", ErrorSeverity.ERROR),

	INTERNAL_ERROR("internal error: <arg> <arg2><if(exception)>: <exception><endif>\n" +
				   "<stackTrace; separator=\"\\n\">", ErrorSeverity.ERROR),
	INTERNAL_WARNING("", ErrorSeverity.ERROR),
	TOKENS_FILE_SYNTAX_ERROR("", ErrorSeverity.ERROR),
	CANNOT_GEN_DOT_FILE("", ErrorSeverity.ERROR),

	// Code generation errors
	MISSING_CODE_GEN_TEMPLATES("", ErrorSeverity.ERROR),
	CANNOT_CREATE_TARGET_GENERATOR("cannot create target <arg> code generator: <exception>", ErrorSeverity.ERROR),
	CODE_TEMPLATE_ARG_ISSUE("code generation template <arg> has missing, misnamed, or incomplete arg list; missing <arg2>", ErrorSeverity.ERROR),
	CODE_GEN_TEMPLATES_INCOMPLETE("missing code generation template <arg>", ErrorSeverity.ERROR),
	NO_MODEL_TO_TEMPLATE_MAPPING("no mapping to template name for output model class <arg>", ErrorSeverity.ERROR),

	// Grammar errors
	SYNTAX_ERROR("<arg>", ErrorSeverity.ERROR),
	RULE_REDEFINITION("rule <arg> redefinition", ErrorSeverity.ERROR),
	LEXER_RULES_NOT_ALLOWED("lexer rule <arg> not allowed in parser", ErrorSeverity.ERROR),
	PARSER_RULES_NOT_ALLOWED("parser rule <arg> not allowed in lexer", ErrorSeverity.ERROR),
    REPEATED_PREQUEL("repeated grammar prequel spec (option, token, or import); please merge", ErrorSeverity.ERROR),
	NO_TOKEN_DEFINITION("no lexer rule corresponding to token: <arg>", ErrorSeverity.ERROR),
	UNDEFINED_RULE_REF("reference to undefined rule: <arg>", ErrorSeverity.ERROR),
	LITERAL_NOT_ASSOCIATED_WITH_LEXER_RULE("", ErrorSeverity.ERROR),
	CANNOT_ALIAS_TOKENS("can't assign string value to token name <arg> in non-combined grammar", ErrorSeverity.ERROR),
    TOKEN_NAMES_MUST_START_UPPER("token names must start with an uppercase letter: <arg>", ErrorSeverity.ERROR),
	ATTRIBUTE_REF_NOT_IN_RULE("", ErrorSeverity.ERROR),
	INVALID_RULE_SCOPE_ATTRIBUTE_REF("", ErrorSeverity.ERROR),
	UNKNOWN_SIMPLE_ATTRIBUTE("unknown attribute reference <arg> in <arg2>", ErrorSeverity.ERROR),
	INVALID_RULE_PARAMETER_REF("cannot access rule <arg>'s parameter: <arg2>", ErrorSeverity.ERROR),
	UNKNOWN_RULE_ATTRIBUTE("unknown attribute <arg> for rule <arg2> in <arg3>", ErrorSeverity.ERROR),
    UNKNOWN_ATTRIBUTE_IN_SCOPE("attribute <arg> isn't a valid property in <arg2>", ErrorSeverity.ERROR),
	ISOLATED_RULE_REF("missing attribute access on rule reference <arg> in <arg2>", ErrorSeverity.ERROR),
	SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE("symbol <arg> conflicts with global dynamic scope with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RULE("label <arg> conflicts with rule with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_TOKEN("label <arg> conflicts with token with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RULE_SCOPE_ATTRIBUTE("label <arg> conflicts with rule <arg2>'s dynamically-scoped attribute with same name", ErrorSeverity.ERROR),
	LABEL_CONFLICTS_WITH_RULE_ARG_RETVAL("label <arg> conflicts with rule <arg2>'s return value or parameter with same name", ErrorSeverity.ERROR),
	ATTRIBUTE_CONFLICTS_WITH_RULE("rule <arg2>'s dynamically-scoped attribute <arg> conflicts with the rule name", ErrorSeverity.ERROR),
	ATTRIBUTE_CONFLICTS_WITH_RULE_ARG_RETVAL("rule <arg2>'s dynamically-scoped attribute <arg> conflicts with <arg2>'s return value or parameter", ErrorSeverity.ERROR),
	LABEL_TYPE_CONFLICT("label <arg> type mismatch with previous definition: <arg2>", ErrorSeverity.ERROR),
	ARG_RETVAL_CONFLICT("rule <arg2>'s argument <arg> conflicts a return value with same name", ErrorSeverity.ERROR),
	NONUNIQUE_REF("arg> is a non-unique reference", ErrorSeverity.ERROR),
	FORWARD_ELEMENT_REF("", ErrorSeverity.ERROR),
	MISSING_RULE_ARGS("missing parameter(s) on rule reference: <arg>", ErrorSeverity.ERROR),
	RULE_HAS_NO_ARGS("rule <arg> has no defined parameters", ErrorSeverity.ERROR),
	ARGS_ON_TOKEN_REF("token reference <arg> may not have parameters", ErrorSeverity.ERROR),
	RULE_REF_AMBIG_WITH_RULE_IN_ALT("", ErrorSeverity.ERROR),
	ILLEGAL_OPTION("illegal option <arg>", ErrorSeverity.WARNING),
	LIST_LABEL_INVALID_UNLESS_RETVAL_STRUCT("", ErrorSeverity.ERROR),
	REWRITE_ELEMENT_NOT_PRESENT_ON_LHS("reference to rewrite element <arg> not found to left of ->", ErrorSeverity.ERROR),
    UNDEFINED_TOKEN_REF_IN_REWRITE("token <arg> in rewrite is undefined", ErrorSeverity.ERROR),
	///UNDEFINED_LABEL_REF_IN_REWRITE("", ErrorSeverity.ERROR), use previous
	NO_GRAMMAR_START_RULE("", ErrorSeverity.ERROR),
	EMPTY_COMPLEMENT("", ErrorSeverity.ERROR),
	UNKNOWN_DYNAMIC_SCOPE("unknown dynamic scope: <arg> in <arg2>", ErrorSeverity.ERROR),
	UNKNOWN_DYNAMIC_SCOPE_ATTRIBUTE("unknown dynamically-scoped attribute for scope <arg>: <arg2> in <arg3>", ErrorSeverity.ERROR),
	ISOLATED_RULE_ATTRIBUTE("", ErrorSeverity.ERROR),
	INVALID_ACTION_SCOPE("", ErrorSeverity.ERROR),
    ACTION_REDEFINITION("redefinition of <arg> action", ErrorSeverity.ERROR),
    SCOPE_REDEFINITION("scope <arg> redefinition", ErrorSeverity.ERROR),
	INVALID_TEMPLATE_ACTION("invalid StringTemplate % shorthand syntax: '<arg>'", ErrorSeverity.ERROR),
	ARG_INIT_VALUES_ILLEGAL("", ErrorSeverity.ERROR),
	REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION("<if(arg)>rule <arg> uses <endif>rewrite syntax or operator with no output option", ErrorSeverity.ERROR),
	NO_RULES("<if(arg2.implicitLexerOwner)>implicitly generated <endif>grammar <arg> has no rules", ErrorSeverity.ERROR),
	WRITE_TO_READONLY_ATTR("", ErrorSeverity.ERROR),
	MISSING_AST_TYPE_IN_TREE_GRAMMAR("", ErrorSeverity.ERROR),
	REWRITE_FOR_MULTI_ELEMENT_ALT("with rewrite=true, alt <arg> not simple node or obvious tree element; text attribute for rule not guaranteed to be correct", ErrorSeverity.ERROR),
	RULE_INVALID_SET("", ErrorSeverity.ERROR),
	HETERO_ILLEGAL_IN_REWRITE_ALT("alts with rewrites can't use heterogeneous types left of ->", ErrorSeverity.ERROR),
	NO_SUCH_GRAMMAR_SCOPE("reference to undefined grammar in rule reference: <arg>.<arg2>", ErrorSeverity.ERROR),
	NO_SUCH_RULE_IN_SCOPE("rule <arg2> is not defined in grammar <arg>", ErrorSeverity.ERROR),
	TOKEN_STRING_REASSIGNMENT("cannot alias <arg>; string already assigned to <arg2>", ErrorSeverity.ERROR),
	TOKEN_NAME_REASSIGNMENT("cannot redefine <arg>; token name already <if(arg2)>assigned to <arg2><else>defined<endif>", ErrorSeverity.ERROR),
	TOKEN_VOCAB_IN_DELEGATE("tokenVocab option ignored in imported grammar <arg>", ErrorSeverity.ERROR),
	TOKEN_ALIAS_IN_DELEGATE("can't assign string to token name <arg> to string in imported grammar <arg2>", ErrorSeverity.ERROR),
	CANNOT_FIND_IMPORTED_FILE("can't find or load grammar <arg>", ErrorSeverity.ERROR),
	INVALID_IMPORT("<arg.typeString> grammar <arg.name> cannot import <arg2.typeString> grammar <arg2.name>", ErrorSeverity.ERROR),
	IMPORTED_TOKENS_RULE_EMPTY("", ErrorSeverity.ERROR),
	IMPORT_NAME_CLASH("<arg.typeString> grammar <arg.name> and imported <arg2.typeString> grammar <arg2.name> both generate <arg2.recognizerName>", ErrorSeverity.ERROR),
	AST_OP_WITH_NON_AST_OUTPUT_OPTION("AST operator with non-AST output option: <arg>", ErrorSeverity.ERROR),
	AST_OP_IN_ALT_WITH_REWRITE("rule <arg> alt <arg2> uses rewrite syntax and also an AST operator", ErrorSeverity.ERROR),
    WILDCARD_AS_ROOT("Wildcard invalid as root; wildcard can itself be a tree", ErrorSeverity.ERROR),
    CONFLICTING_OPTION_IN_TREE_FILTER("option <arg>=<arg2> conflicts with tree grammar filter mode", ErrorSeverity.ERROR),

	AMBIGUITY("", ErrorSeverity.ERROR),
	UNREACHABLE_ALTS("", ErrorSeverity.ERROR),

	// these next 3 can happen in recursion-limited LL("", *)
	//RECURSION_OVERFLOW("", ErrorSeverity.ERROR),
	LEFT_RECURSION_CYCLES("The following sets of rules are mutually left-recursive <arg:{c| [<c:{r|<r.name>}; separator=\", \">]}; separator=\" and \">", ErrorSeverity.ERROR),

	MODE_NOT_IN_LEXER("lexical modes are only allowed in lexer grammars", ErrorSeverity.ERROR),

	/** Documentation comment is unterminated */
    //UNTERMINATED_DOC_COMMENT("", ErrorSeverity.ERROR),

    // Dependency sorting errors
    //
    /** t1.g -> t2.g -> t3.g ->t1.g */
    CIRCULAR_DEPENDENCY("your grammars contain a circular dependency and cannot be sorted into a valid build order", ErrorSeverity.ERROR),

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

	public String msg;
    public ErrorSeverity severity;
    public Boolean abortsAnalysis;
    public Boolean abortsCodegen;

	ErrorType(String msg) {
		this(msg, ErrorSeverity.ERROR);
	}

	ErrorType(String msg, ErrorSeverity severity) {
		this(msg, severity, false);
	}

	ErrorType(String msg, ErrorSeverity severity, boolean abortsAnalysis) {
		this(msg, severity, abortsAnalysis, false);
	}

    ErrorType(String msg, ErrorSeverity severity, boolean abortsAnalysis, boolean abortsCodegen) {
		this.msg = msg;
        this.severity       = severity;
        this.abortsAnalysis = abortsAnalysis;
		this.abortsCodegen = abortsCodegen;
    }
}
