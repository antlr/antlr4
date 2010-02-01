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

/** Check the basic semantics of the input.  We check for: */
tree grammar BasicSemanticTriggers;
options {
	language      = Java;
	tokenVocab    = ANTLRParser;
	ASTLabelType  = GrammarAST;
	filter        = true;
	superClass	  = 'org.antlr.v4.runtime.tree.TreeFilter';
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
package org.antlr.v4.semantics;
import org.antlr.v4.tool.*;
}

@members {
public String name;
public String fileName;
public Map<String,String> options = new HashMap<String,String>();
protected int gtype;
//Grammar g; // which grammar are we checking
public BasicSemanticTriggers(TreeNodeStream input, String fileName) {
	this(input);
	this.fileName = fileName;
}
}

// TODO: SHOULD we fix up grammar AST to remove errors?  Like kill refs to bad rules?
// that is, rewrite tree?  maybe all passes are filters until code gen, which needs
// tree grammar. 'course we won't try codegen if errors.
topdown
	:	grammarSpec
	|	option
	|	rule
	|	ruleref
	|	tokenAlias
	|	tokenRef
	;

grammarSpec
    :   ^(grammarType ID .*)
    	{
    	name = $ID.text;
    	BasicSemanticChecks.checkGrammarName($ID.token);
    	}
	;
	
grammarType
@init {gtype = $start.getType();}
    :   LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR 
    ;

option
    :   {inContext("OPTIONS")}? ^('=' o=ID optionValue)
    	{options.put($o.text, $optionValue.v);}
    ;

optionValue returns [String v]
@init {$v = $start.token.getText();}
    :   ID
    |   STRING_LITERAL
    |   INT
    |   STAR
    ;

rule:   ^( RULE r=ID .*) {BasicSemanticChecks.checkInvalidRuleDef(gtype, $r.token);}
    ;

ruleref
    :	RULE_REF {BasicSemanticChecks.checkInvalidRuleRef(gtype, $RULE_REF.token);}
    ;

tokenAlias
	:	{inContext("TOKENS")}? ^(ASSIGN TOKEN_REF STRING_LITERAL)
		{BasicSemanticChecks.checkTokenAlias(gtype, $TOKEN_REF.token);}
	;

tokenRef
	:	^(TOKEN_REF ARG_ACTION .*)
		{BasicSemanticChecks.checkTokenArgs(gtype, $TOKEN_REF.token);}
	;