/*
 [The "BSD licence"]
 Copyright (c) 2005-2008 Terence Parr
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
 * <a href="http://www.antlr.org/wiki/display/ANTLR3/Errors+Reported+by+the+ANTLR+Tool">Errors Reported by the ANTLR Tool</a>.
 *
 * TODO: add notion of single issuance of an error; some don't need to be repeated; AST_OP_IN_ALT_WITH_REWRITE and option issues
 *
 * @author Jim Idle <jimi@temporal-wave.com>
 * @since 4.0
 */
public enum ErrorType {
    INVALID(ErrorSeverity.ERROR,true,true),
    
    // TODO: set all of the true, true appropriately
	CANNOT_WRITE_FILE(ErrorSeverity.ERROR, true, true),
	CANNOT_CLOSE_FILE(ErrorSeverity.ERROR, true, true),
	CANNOT_FIND_TOKENS_FILE(ErrorSeverity.ERROR, true, true),
	ERROR_READING_TOKENS_FILE(ErrorSeverity.ERROR, true, true),
	DIR_NOT_FOUND(ErrorSeverity.ERROR, true, true),
	OUTPUT_DIR_IS_FILE(ErrorSeverity.ERROR, true, true),
	CANNOT_OPEN_FILE(ErrorSeverity.ERROR, true, true),
	FILE_AND_GRAMMAR_NAME_DIFFER(ErrorSeverity.ERROR, true, true),
	FILENAME_EXTENSION_ERROR(ErrorSeverity.ERROR, true, true),

	INTERNAL_ERROR(ErrorSeverity.ERROR, true, true),
	INTERNAL_WARNING(ErrorSeverity.ERROR, true, true),
	TOKENS_FILE_SYNTAX_ERROR(ErrorSeverity.ERROR, true, true),
	CANNOT_GEN_DOT_FILE(ErrorSeverity.ERROR, true, true),

	// Code generation errors
	MISSING_CODE_GEN_TEMPLATES(ErrorSeverity.ERROR, false, true),
	CANNOT_CREATE_TARGET_GENERATOR(ErrorSeverity.ERROR, false, true),
	CODE_TEMPLATE_ARG_ISSUE(ErrorSeverity.ERROR, false, true),

	// Grammar errors
	SYNTAX_ERROR(ErrorSeverity.ERROR, true, true),
	RULE_REDEFINITION(ErrorSeverity.ERROR, true, true),
	LEXER_RULES_NOT_ALLOWED(ErrorSeverity.ERROR, true, true),
	PARSER_RULES_NOT_ALLOWED(ErrorSeverity.ERROR, true, true),
    REPEATED_PREQUEL(ErrorSeverity.ERROR, true, true),
	NO_TOKEN_DEFINITION(ErrorSeverity.ERROR, true, true),
	UNDEFINED_RULE_REF(ErrorSeverity.ERROR, true, true),
	LITERAL_NOT_ASSOCIATED_WITH_LEXER_RULE(ErrorSeverity.ERROR, true, true),
	CANNOT_ALIAS_TOKENS(ErrorSeverity.ERROR, true, true),
    TOKEN_NAMES_MUST_START_UPPER(ErrorSeverity.ERROR, true, true),
	ATTRIBUTE_REF_NOT_IN_RULE(ErrorSeverity.ERROR, true, true),
	INVALID_RULE_SCOPE_ATTRIBUTE_REF(ErrorSeverity.ERROR, true, true),
	UNKNOWN_SIMPLE_ATTRIBUTE(ErrorSeverity.ERROR, true, true),
	INVALID_RULE_PARAMETER_REF(ErrorSeverity.ERROR, true, true),
	UNKNOWN_RULE_ATTRIBUTE(ErrorSeverity.ERROR, true, true),
    UNKNOWN_ATTRIBUTE_IN_SCOPE(ErrorSeverity.ERROR, true, true),
	ISOLATED_RULE_REF(ErrorSeverity.ERROR, true, true),
	SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE(ErrorSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_RULE(ErrorSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_TOKEN(ErrorSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_RULE_SCOPE_ATTRIBUTE(ErrorSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_RULE_ARG_RETVAL(ErrorSeverity.ERROR, true, true),
	ATTRIBUTE_CONFLICTS_WITH_RULE(ErrorSeverity.ERROR, true, true),
	ATTRIBUTE_CONFLICTS_WITH_RULE_ARG_RETVAL(ErrorSeverity.ERROR, true, true),
	LABEL_TYPE_CONFLICT(ErrorSeverity.ERROR, true, true),
	ARG_RETVAL_CONFLICT(ErrorSeverity.ERROR, true, true),
	NONUNIQUE_REF(ErrorSeverity.ERROR, true, true),
	FORWARD_ELEMENT_REF(ErrorSeverity.ERROR, true, true),
	MISSING_RULE_ARGS(ErrorSeverity.ERROR, true, true),
	RULE_HAS_NO_ARGS(ErrorSeverity.ERROR, true, true),
	ARGS_ON_TOKEN_REF(ErrorSeverity.ERROR, true, true),
	RULE_REF_AMBIG_WITH_RULE_IN_ALT(ErrorSeverity.ERROR, true, true),
	ILLEGAL_OPTION(ErrorSeverity.ERROR, true, true),
	LIST_LABEL_INVALID_UNLESS_RETVAL_STRUCT(ErrorSeverity.ERROR, true, true),
	REWRITE_ELEMENT_NOT_PRESENT_ON_LHS(ErrorSeverity.ERROR, true, true),
    //UNDEFINED_TOKEN_REF_IN_REWRITE(ErrorSeverity.ERROR, true, true),
	///UNDEFINED_LABEL_REF_IN_REWRITE(ErrorSeverity.ERROR, true, true), use previous
	NO_GRAMMAR_START_RULE(ErrorSeverity.ERROR, true, true),
	EMPTY_COMPLEMENT(ErrorSeverity.ERROR, true, true),
	UNKNOWN_DYNAMIC_SCOPE(ErrorSeverity.ERROR, true, true),
	UNKNOWN_DYNAMIC_SCOPE_ATTRIBUTE(ErrorSeverity.ERROR, true, true),
	ISOLATED_RULE_ATTRIBUTE(ErrorSeverity.ERROR, true, true),
	INVALID_ACTION_SCOPE(ErrorSeverity.ERROR, true, true),
    ACTION_REDEFINITION(ErrorSeverity.ERROR, true, true),
    SCOPE_REDEFINITION(ErrorSeverity.ERROR, true, true),
	INVALID_TEMPLATE_ACTION(ErrorSeverity.ERROR, true, true),
	ARG_INIT_VALUES_ILLEGAL(ErrorSeverity.ERROR, true, true),
	REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION(ErrorSeverity.ERROR, true, true),
	NO_RULES(ErrorSeverity.ERROR, true, true),
	WRITE_TO_READONLY_ATTR(ErrorSeverity.ERROR, true, true),
	MISSING_AST_TYPE_IN_TREE_GRAMMAR(ErrorSeverity.ERROR, true, true),
	REWRITE_FOR_MULTI_ELEMENT_ALT(ErrorSeverity.ERROR, true, true),
	RULE_INVALID_SET(ErrorSeverity.ERROR, true, true),
	HETERO_ILLEGAL_IN_REWRITE_ALT(ErrorSeverity.ERROR, true, true),
	NO_SUCH_GRAMMAR_SCOPE(ErrorSeverity.ERROR, true, true),
	NO_SUCH_RULE_IN_SCOPE(ErrorSeverity.ERROR, true, true),
	TOKEN_ALIAS_CONFLICT(ErrorSeverity.ERROR, true, true),
	TOKEN_ALIAS_REASSIGNMENT(ErrorSeverity.ERROR, true, true),
	TOKEN_VOCAB_IN_DELEGATE(ErrorSeverity.ERROR, true, true),
	TOKEN_ALIAS_IN_DELEGATE(ErrorSeverity.ERROR, true, true),
	INVALID_IMPORT(ErrorSeverity.ERROR, true, true),
	IMPORTED_TOKENS_RULE_EMPTY(ErrorSeverity.ERROR, true, true),
	IMPORT_NAME_CLASH(ErrorSeverity.ERROR, true, true),
	AST_OP_WITH_NON_AST_OUTPUT_OPTION(ErrorSeverity.ERROR, true, true),
	AST_OP_IN_ALT_WITH_REWRITE(ErrorSeverity.ERROR, true, true),
    WILDCARD_AS_ROOT(ErrorSeverity.ERROR, true, true),
    CONFLICTING_OPTION_IN_TREE_FILTER(ErrorSeverity.ERROR, true, true),

	AMBIGUITY(ErrorSeverity.ERROR, true, true),
	UNREACHABLE_ALTS(ErrorSeverity.ERROR, true, true),
	//MULTIPLE_RECURSIVE_ALTS(ErrorSeverity.ERROR, true, true),
	INSUFFICIENT_PREDICATES(ErrorSeverity.ERROR, true, true),

	// these next 3 can happen in recursion-limited LL(*)
	//RECURSION_OVERFLOW(ErrorSeverity.ERROR, true, true),
	LEFT_RECURSION_CYCLES(ErrorSeverity.ERROR, true, true),
	ANALYSIS_TIMEOUT(ErrorSeverity.ERROR, true, true),

	MODE_NOT_IN_LEXER(ErrorSeverity.ERROR, true, true),

	/** Documentation comment is unterminated */
    //UNTERMINATED_DOC_COMMENT(ErrorSeverity.ERROR, true, true),

    // Dependency sorting errors
    //
    /** t1.g -> t2.g -> t3.g ->t1.g */
    CIRCULAR_DEPENDENCY(ErrorSeverity.ERROR, true, true),

    // Simple informational messages
    //
    /** A standby generic message that jsut spits out the arguments it is given */
//    GENERIC_INFO(ErrorSeverity.INFO, false, false),
//    /** How to print out the version of the ANTLR tool that we are */
//    ANTLR_VERSION(ErrorSeverity.INFO, false, false),
//
//    // Command line tool errors/warnings
//    /** -fo option was incorrectly formed */
//    MISSING_OUTPUT_FO(ErrorSeverity.WARNING, false, false),
//    /** -lib option is missing a directory argument */
//    MISSING_LIBDIR(ErrorSeverity.WARNING, false, false),
//    /** -format option was not given the name of a message format */
//    MISSING_FORMAT(ErrorSeverity.WARNING, false, false),
//    /** Max state count missing from the option */
//    MISSING_MAXSTATES(ErrorSeverity.WARNING, false, false),
//    /** Max labels in a switch argument is missing */
//    MISSING_MAXSWITCH(ErrorSeverity.WARNING, false, false),
//    /** Min labels in a switch argument is missing */
//    MISSING_MINSWITCH(ErrorSeverity.WARNING, false, false),
//    /** Missing recursion limit argument */
//    MISSING_MAXRECUR(ErrorSeverity.WARNING, false, false),
//    /** Missing max edges argument */
//    MISSING_MAXEDGE(ErrorSeverity.WARNING, false, false),
//    /** Misng ms timeout argument */
//    MISSING_MAXTIME(ErrorSeverity.WARNING, false, false),
//
//    // Help messages
//    HELP_USAGE(ErrorSeverity.INFO, false, false),
//    HELP_EXTENDED(ErrorSeverity.INFO, false, false),

    ;

    /**
     * Local storage for the severity level of the message
     */
    private ErrorSeverity severity;

    /**
     * Returns the severity level of this message
     * @return
     */
    public ErrorSeverity getSeverity() {
        return severity;
    }

    /**
     * Internal storage for the flag that indicates whether this particular message
     * should abort the analysis phase or not.
     */
    private Boolean abortsAnalysis;

    /**
     * Indicates whether the raising of this error messsage should abort the
     * analysis phase (or prevent it from starting).
     *
     * @return true if this message should abort the analysis phase
     */
    public Boolean abortsAnalysis() {
        return abortsAnalysis;
    }

    /**
     * Indicates whether the raising of this error message aborts code
     * generation or not.
     */
    private Boolean abortsCodegen;

    /**
     * Indicates whether the raising of this error message aborts code
     * generation or not.
     *
     * @return true if this message should abort code generation
     */
    public Boolean abortsCodegen() {
        return abortsCodegen;
    }

    /**
     * Local constructor produces an instance of the entries in this Enum
     */
    private ErrorType(ErrorSeverity severity, boolean abortsAnalysis, boolean abortsCodegen) {
        this.severity       = severity;
        this.abortsAnalysis = abortsAnalysis;

    }
}
