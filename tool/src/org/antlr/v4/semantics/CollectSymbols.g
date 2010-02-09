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

/** Collects rules, terminals, strings, actions, scopes etc... from AST
 *  Side-effects: None
 */
tree grammar CollectSymbols;
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
import java.util.Set;
import java.util.HashSet;
import org.stringtemplate.v4.misc.MultiMap;
}

@members {
Rule currentRule = null;
int currentAlt = 1; // 1..n
public List<Rule> rules = new ArrayList<Rule>();
public List<GrammarAST> rulerefs = new ArrayList<GrammarAST>();
public List<GrammarAST> terminals = new ArrayList<GrammarAST>();
public List<GrammarAST> tokenIDRefs = new ArrayList<GrammarAST>();
public List<GrammarAST> strings = new ArrayList<GrammarAST>();
public List<GrammarAST> tokensDefs = new ArrayList<GrammarAST>();
public List<GrammarAST> scopes = new ArrayList<GrammarAST>();
public List<GrammarAST> actions = new ArrayList<GrammarAST>();
Grammar g; // which grammar are we checking
public CollectSymbols(TreeNodeStream input, Grammar g) {
	this(input);
	this.g = g;
}
}

topdown
//@init {System.out.println("topdown: "+((Tree)input.LT(1)).getText());}
    :	globalScope
    |	action
    |	tokensSection
    |	rule
    |	ruleArg
    |	ruleReturns
    |	ruleScopeSpec
    |	ruleref
    |	rewriteElement // make sure we check this before terminal etc... 
    				   // want to match rewrite stuff all here
    |	terminal
    |	labeledElement
    |	setAlt
	;

bottomup
	:	finishRule
	;

globalScope
	:	{inContext("GRAMMAR")}? ^(SCOPE ID ACTION) {scopes.add($SCOPE);}
	;

action
	:	{inContext("GRAMMAR")}? ^(AT ID? ID ACTION)
		{actions.add($AT);}
	;

tokensSection
	:	{inContext("TOKENS")}?
		(	^(ASSIGN t=ID STRING_LITERAL)
			{terminals.add($t); tokenIDRefs.add($t);
			 tokensDefs.add($ASSIGN); strings.add($STRING_LITERAL);}
		|	t=ID
			{terminals.add($t); tokenIDRefs.add($t); tokensDefs.add($t);}
		)
	;

rule:   ^( RULE name=ID .+)
		{
		int numAlts = $RULE.getFirstChildWithType(BLOCK).getChildCount();
		Rule r = new Rule($name.text, (GrammarASTWithOptions)$RULE, numAlts);
		rules.add(r);
		currentRule = r;
		currentAlt = 1;
		}
    ;

setAlt
	:	{inContext("RULE BLOCK")}? ( ALT | ALT_REWRITE )
		{currentAlt = $start.getChildIndex()+1;}
	;
	
finishRule
	:	RULE {currentRule = null;}
	;

ruleArg
	:	{inContext("RULE")}? ARG_ACTION {currentRule.arg = $ARG_ACTION;}
	;
	
ruleReturns
	:	^(RETURNS ARG_ACTION) {currentRule.ret = $ARG_ACTION;}
	;

ruleScopeSpec
	:	{inContext("RULE")}?
		(	^(SCOPE ACTION)
		|	^(SCOPE ID+)
		)
	;

rewriteElement
//@init {System.out.println("rewriteElement: "+((Tree)input.LT(1)).getText());}
	:	
    	{inContext("RESULT ...")}? (TOKEN_REF|RULE_REF|STRING_LITERAL|LABEL)
		{currentRule.alt[currentAlt].rewriteElements.add($start);}
	;
	
labeledElement
@after {
LabelElementPair lp = new LabelElementPair(g, $id, $e, $start.getType());
currentRule.labelDefs.map($id.text, lp);
currentRule.alt[currentAlt].labelRefs.map($id.text, $id);
}
	:	{inContext("RULE ...")}?
		(	^(ASSIGN id=ID e=.)
		|	^(PLUS_ASSIGN id=ID e=.)
		)
	;
	
terminal
    :	{!inContext("TOKENS ASSIGN")}? STRING_LITERAL
    	{
    	terminals.add($start);
    	strings.add($STRING_LITERAL);
    	if ( currentRule!=null ) {
    		currentRule.alt[currentAlt].tokenRefs.map($STRING_LITERAL.text, $STRING_LITERAL);
    	}
    	}
    |	TOKEN_REF
    	{
    	terminals.add($TOKEN_REF);
    	tokenIDRefs.add($TOKEN_REF);
    	if ( currentRule!=null ) {
    		currentRule.alt[currentAlt].tokenRefs.map($TOKEN_REF.text, $TOKEN_REF);
    	}
    	}
    ;

ruleref
//@init {System.out.println("ruleref: "+((Tree)input.LT(1)).getText());}
    :	RULE_REF
    	{
    	rulerefs.add($RULE_REF);
    	if ( currentRule!=null ) {
    		currentRule.alt[currentAlt].ruleRefs.map($RULE_REF.text, $RULE_REF);
    	}
    	}
    ;