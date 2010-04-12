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

/** Triggers for the basic semantics of the input.  Side-effects:
 *  Set token, block, rule options in the tree.  Load field option
 *  with grammar options. Only legal options are set.
 */
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
// TODO: SHOULD we fix up grammar AST to remove errors?  Like kill refs to bad rules?
// that is, rewrite tree?  maybe all passes are filters until code gen, which needs
// tree grammar. 'course we won't try codegen if errors.
public String name;
GrammarASTWithOptions root;
Grammar g; // which grammar are we checking
BasicSemanticChecks checker;
public BasicSemanticTriggers(TreeNodeStream input, Grammar g) {
	this(input);
	this.g = g;
	checker = new BasicSemanticChecks(g);
}
}

topdown  // do these on way down so options and such are set first
	:	grammarSpec
	|	rules
	|	option
	|	rule
	|	tokenAlias
	|	rewrite
	;
	
bottomup // do these "inside to outside" of expressions.
	:	multiElementAltInTreeGrammar
	|	astOps
	|	ruleref
	|	tokenRefWithArgs
	|	elementOption
	|	checkGrammarOptions // do after we see everything
	|	wildcardRoot
	;

grammarSpec
    :   ^(	GRAMMAR ID DOC_COMMENT? 
	    	{
	    	name = $ID.text;
	    	checker.checkGrammarName($ID.token);
	    	root = (GrammarRootAST)$start;
	    	}
    		prequelConstructs ^(RULES .*)
    	)
	;

checkGrammarOptions // when we get back to root
	:	GRAMMAR
		{checker.checkTreeFilterOptions((GrammarRootAST)$GRAMMAR,
										root.getOptions());}
	;

/*
grammarType
@init {gtype = $start.getType(); root = (GrammarASTWithOptions)$start;}
    :   LEXER_GRAMMAR | PARSER_GRAMMAR | TREE_GRAMMAR | COMBINED_GRAMMAR 
    ;
    */

prequelConstructs
	:	(	^(o+=OPTIONS .+)
		|	^(i+=IMPORT delegateGrammar+)
		|	^(t+=TOKENS .+)
		)*
		{checker.checkNumPrequels($o, $i, $t);}
	;
	
delegateGrammar
    :   (	^(ASSIGN ID id=ID)
	    |   id=ID
	    )
	    {checker.checkImport($id.token);}
    ;

rules : RULES {checker.checkNumRules($RULES);} ;

option // TODO: put in grammar, or rule, or block
    :   {inContext("OPTIONS")}? ^(ASSIGN o=ID optionValue)
    	{
   	    GrammarAST parent = (GrammarAST)$start.getParent();   // OPTION
   		GrammarAST parentWithOptionKind = (GrammarAST)parent.getParent();
    	boolean ok = checker.checkOptions(parentWithOptionKind,
    									  $ID.token, $optionValue.v);
		//  store options into XXX_GRAMMAR, RULE, BLOCK nodes
    	if ( ok ) {
    		((GrammarASTWithOptions)parentWithOptionKind).setOption($o.text, $optionValue.v); 
    	}
    	}
    ;

optionValue returns [String v]
@init {$v = $start.token.getText();}
    :   ID
    |   STRING_LITERAL
    |   INT
    |   STAR
    ;

rule:   ^( RULE r=ID .*) {checker.checkInvalidRuleDef($r.token);}
    ;

ruleref
    :	RULE_REF {checker.checkInvalidRuleRef($RULE_REF.token);}
    ;

tokenAlias
	:	{inContext("TOKENS")}? ^(ASSIGN ID STRING_LITERAL)
		{checker.checkTokenAlias($ID.token);}
	;

tokenRefWithArgs
	:	{!inContext("RESULT ...")}? // if not on right side of ->
    	^(TOKEN_REF ARG_ACTION)
		{checker.checkTokenArgs($TOKEN_REF.token);}
	;
	
elementOption
    :	{!inContext("RESULT ...")}? // not on right side of ->
    	^(	ELEMENT_OPTIONS
	    	(	^(ASSIGN o=ID value=ID)
		   	|   ^(ASSIGN o=ID value=STRING_LITERAL)
 		   	|	o=ID
		   	)
		)
	   	{
    	boolean ok = checker.checkTokenOptions((GrammarAST)$o.getParent(),
    										   $o.token, $value.text);
    	if ( ok ) {
			if ( value!=null ) {
	    		TerminalAST terminal = (TerminalAST)$start.getParent();
	    		terminal.setOption($o.text, $value.text);
    		}
    		else {
	    		TerminalAST terminal = (TerminalAST)$start.getParent();
	    		terminal.setOption(TerminalAST.defaultTokenOption, $o.text);
    		}
    	}
    	}
    ;

// (ALT_REWRITE (ALT A B)   ^( ALT ^( A B ) ) or ( ALT A )
multiElementAltInTreeGrammar
	:	{inContext("ALT_REWRITE")}?
		^( ALT ~(SEMPRED|ACTION) ~(SEMPRED|ACTION)+ ) // > 1 element at outer level
		{
		int altNum = $start.getParent().getChildIndex() + 1; // alts are 1..n
		GrammarAST firstNode = (GrammarAST)$start.getChild(0);
		checker.checkRewriteForMultiRootAltInTreeGrammar(root.getOptions(),
														 firstNode.token,
														 altNum);
		}
	;

// Check stuff like (^ A) (! r)
astOps
	:	^(ROOT el=.) {checker.checkASTOps(root.getOptions(), $start, $el);}
	|	^(BANG el=.) {checker.checkASTOps(root.getOptions(), $start, $el);}
	;

rewrite
	:	(RESULT|ST_RESULT)
		{checker.checkRewriteOk(root.getOptions(),$start);}
	;
	
wildcardRoot
    :	^(TREE_BEGIN WILDCARD .*)
    	{checker.checkWildcardRoot($WILDCARD.token);}
    ;