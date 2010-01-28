/*
 [The "BSD license"]
 Copyright (c) 2010 Terence Parr
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

/** Check the basic semantics of the input.  We check for:

	FILE_AND_GRAMMAR_NAME_DIFFER
	RULE_REDEFINITION(MessageSeverity.ERROR, true, true),
	LEXER_RULES_NOT_ALLOWED(MessageSeverity.ERROR, true, true),
	PARSER_RULES_NOT_ALLOWED(MessageSeverity.ERROR, true, true),
	UNDEFINED_RULE_REF(MessageSeverity.ERROR, true, true),
	CANNOT_ALIAS_TOKENS_IN_LEXER(MessageSeverity.ERROR, true, true),
	INVALID_RULE_PARAMETER_REF(MessageSeverity.ERROR, true, true),
	SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE(MessageSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_RULE(MessageSeverity.ERROR, true, true),
	LABEL_CONFLICTS_WITH_TOKEN(MessageSeverity.ERROR, true, true),
	LABEL_TYPE_CONFLICT(MessageSeverity.ERROR, true, true),
	MISSING_RULE_ARGS(MessageSeverity.ERROR, true, true),
	RULE_HAS_NO_ARGS(MessageSeverity.ERROR, true, true),
	ARGS_ON_TOKEN_REF(MessageSeverity.ERROR, true, true),
	ILLEGAL_OPTION(MessageSeverity.ERROR, true, true),
	UNDEFINED_TOKEN_REF_IN_REWRITE(MessageSeverity.ERROR, true, true),
	REWRITE_ELEMENT_NOT_PRESENT_ON_LHS(MessageSeverity.ERROR, true, true),
	UNDEFINED_LABEL_REF_IN_REWRITE(MessageSeverity.ERROR, true, true),
	EMPTY_COMPLEMENT(MessageSeverity.ERROR, true, true),
	ACTION_REDEFINITION(MessageSeverity.ERROR, true, true),
	REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION(MessageSeverity.ERROR, true, true),
	NO_RULES(MessageSeverity.ERROR, true, true),
	REWRITE_FOR_MULTI_ELEMENT_ALT(MessageSeverity.ERROR, true, true),
	RULE_INVALID_SET(MessageSeverity.ERROR, true, true),
	HETERO_ILLEGAL_IN_REWRITE_ALT(MessageSeverity.ERROR, true, true),
	NO_SUCH_RULE_IN_SCOPE(MessageSeverity.ERROR, true, true),
	TOKEN_ALIAS_CONFLICT(MessageSeverity.ERROR, true, true),
	TOKEN_ALIAS_REASSIGNMENT(MessageSeverity.ERROR, true, true),
	TOKEN_VOCAB_IN_DELEGATE(MessageSeverity.ERROR, true, true),
	INVALID_IMPORT(MessageSeverity.ERROR, true, true),
	IMPORTED_TOKENS_RULE_EMPTY(MessageSeverity.ERROR, true, true),
	IMPORT_NAME_CLASH(MessageSeverity.ERROR, true, true),
	AST_OP_WITH_NON_AST_OUTPUT_OPTION(MessageSeverity.ERROR, true, true),
	AST_OP_IN_ALT_WITH_REWRITE(MessageSeverity.ERROR, true, true),
    WILDCARD_AS_ROOT(MessageSeverity.ERROR, true, true),
    CONFLICTING_OPTION_IN_TREE_FILTER(MessageSeverity.ERROR, true, true),
 * 
 */
tree grammar BasicSemanticsChecker;
options {
	language      = Java;
	tokenVocab    = ANTLRParser;
	ASTLabelType  = ASTGrammar;
	filter        = true;
}

// Include the copyright in this source and also the generated source
@header {
/*
 [The "BSD license"]
 Copyright (c) 2010 Terence Parr
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
package org.antlr.v4.parse;
import org.antlr.v4.tool.*;
}

topdown
	:
	;

grammarSpec
    :   ^(grammarType ID DOC_COMMENT? prequelConstruct* rules)
	;
	
grammarType
    :   LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR 
    ;

prequelConstruct
	:   optionsSpec
    |   delegateGrammars
    |   tokensSpec
    |   attrScope
    |   action
    ;

optionsSpec
	:	^(OPTIONS option+)
    ;

option
    :   ^(ASSIGN ID optionValue)
    ;

optionValue
    :   ID
    |   STRING_LITERAL
    |   CHAR_LITERAL
    |   INT
    |   STAR
    ;

delegateGrammars
	:   ^(IMPORT delegateGrammar+)
	;

delegateGrammar
    :   ^(ASSIGN ID ID)
    |   ID
    ;

tokensSpec
	:   ^(TOKENS tokenSpec+)
	;

tokenSpec
	:	^(ASSIGN TOKEN_REF STRING_LITERAL)
    |   ^(ASSIGN TOKEN_REF CHAR_LITERAL)
	|   TOKEN_REF
	|	RULE_REF
	;

attrScope
	:	^(SCOPE ID ACTION)
	;

action
	:	^(AT ID? ID ACTION)
	;

rules
    : ^(RULES rule*)
    ;

rule:   ^( RULE ID DOC_COMMENT? ruleModifiers? ARG_ACTION?
      	   ruleReturns? rulePrequel* altListAsBlock exceptionGroup
      	 )
    ;

exceptionGroup
    : exceptionHandler* finallyClause?
    ;

exceptionHandler
	: ^(CATCH ARG_ACTION ACTION)
	;

finallyClause
	: ^(FINALLY ACTION)
	;

rulePrequel
    :   throwsSpec
    |   ruleScopeSpec
    |   optionsSpec
    |   ruleAction
    ;

ruleReturns
	: ^(RETURNS ARG_ACTION)
	;
throwsSpec
    : ^(THROWS ID+)
    ;

ruleScopeSpec
	:	^(SCOPE ACTION)
	|	^(SCOPE ID+)
	;

ruleAction
	:	^(AT ID ACTION)
	;

ruleModifiers
    : ^(RULEMODIFIERS ruleModifier+)
    ;

ruleModifier
    : PUBLIC
    | PRIVATE
    | PROTECTED
    | FRAGMENT
    ;

altList
    : alternative+
    ;

altListAsBlock
    : ^(BLOCK altList)
    ;

alternative
    :	^(ALT_REWRITE alternative rewrite)
    |	^(ALT EPSILON)
    |   elements
    ;

elements
    : ^(ALT element+)
    ;

element
	:	labeledElement
	|	atom
	|	ebnf
	|   ACTION
	|   SEMPRED
	|	GATED_SEMPRED
	|	treeSpec
	;
	
labeledElement
	:	^(ASSIGN ID (atom|block))
	|	^(PLUS_ASSIGN ID (atom|block))
	;

treeSpec
    : ^(TREE_BEGIN element+)
    ;

ebnf:	^(blockSuffix block)
	| 	block
    ;

blockSuffix
    : ebnfSuffix
    | ROOT
    | IMPLIES
    | BANG
    ;

ebnfSuffix
	:	OPTIONAL
  	|	CLOSURE
   	|	POSITIVE_CLOSURE
	;
	
atom:	^(ROOT range)
	|	^(BANG range)
	|	^(ROOT notSet)
	|	^(BANG notSet)
	|	range
	|	^(DOT ID terminal)
	|	^(DOT ID ruleref)
    |   terminal
    |   ruleref
    ;

notSet
    : ^(NOT notTerminal)
    | ^(NOT block)
    ;

notTerminal
    : CHAR_LITERAL
    | TOKEN_REF
    | STRING_LITERAL
    ;

block
    :	^(BLOCK optionsSpec? ruleAction* ACTION? altList)
    ;

ruleref
    :	^(ROOT RULE_REF ARG_ACTION?)
    |	^(BANG RULE_REF ARG_ACTION?)
    |	^(RULE_REF ARG_ACTION?)
    ;

range
    : ^(RANGE rangeElement rangeElement)
    ;

rangeElement
    : CHAR_LITERAL
    | STRING_LITERAL
    | RULE_REF
    | TOKEN_REF
    ;

terminal
    :   ^(CHAR_LITERAL elementOptions)
    |   CHAR_LITERAL
    |	^(STRING_LITERAL elementOptions)
    |	STRING_LITERAL
    |	^(TOKEN_REF elementOptions)
    |	TOKEN_REF
    |	^(WILDCARD elementOptions)
    |	WILDCARD
    |	^(ROOT terminal)
    |	^(BANG terminal)
    ;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption+)
    ;

elementOption
    :	ID
    |   ^(ASSIGN ID ID)
    |   ^(ASSIGN ID STRING_LITERAL)
    ;

rewrite
	:	predicatedRewrite* nakedRewrite
	;

predicatedRewrite
	:	^(ST_RESULT SEMPRED rewriteAlt)
	|	^(RESULT SEMPRED rewriteAlt)
	;
	
nakedRewrite
	:	^(ST_RESULT rewriteAlt)
	|	^(RESULT rewriteAlt)
	;
	
rewriteAlt
    :	rewriteTemplate
    |	rewriteTreeAlt
    |	ETC
    |	EPSILON
    ;
	
rewriteTreeAlt
    :	^(ALT rewriteTreeElement+)
    ;

rewriteTreeElement
	:	rewriteTreeAtom
	|	rewriteTree
	|   rewriteTreeEbnf
	;

rewriteTreeAtom
    :   CHAR_LITERAL
	|   ^(TOKEN_REF ARG_ACTION)
	|   TOKEN_REF
    |   RULE_REF
	|   STRING_LITERAL
	|   LABEL
	|	ACTION
	;

rewriteTreeEbnf
	:	^(ebnfSuffix ^(BLOCK rewriteTreeAlt))
	;
rewriteTree
	:	^(TREE_BEGIN rewriteTreeAtom rewriteTreeElement* )
	;

rewriteTemplate
	:	^(TEMPLATE rewriteTemplateArgs? DOUBLE_QUOTE_STRING_LITERAL)
	|	^(TEMPLATE rewriteTemplateArgs? DOUBLE_ANGLE_STRING_LITERAL)
	|	rewriteTemplateRef
	|	rewriteIndirectTemplateHead
	|	ACTION
	;

rewriteTemplateRef
	:	^(TEMPLATE ID rewriteTemplateArgs?)
	;

rewriteIndirectTemplateHead
	:	^(TEMPLATE ACTION rewriteTemplateArgs?)
	;

rewriteTemplateArgs
	:	^(ARGLIST rewriteTemplateArg+)
	;

rewriteTemplateArg
	:   ^(ARG ID ACTION)
	;
