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

/** Triggers for defining rules, tokens, scopes, and actions.
 *  Side-effects: ...
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
}

@members {
Rule currentRule = null;
public List<Rule> rules = new ArrayList<Rule>();
public List<GrammarAST> terminals = new ArrayList<GrammarAST>();
public List<GrammarAST> aliases = new ArrayList<GrammarAST>();
public List<GrammarAST> scopes = new ArrayList<GrammarAST>();
public List<GrammarAST> actions = new ArrayList<GrammarAST>();
Grammar g; // which grammar are we checking
public CollectSymbols(TreeNodeStream input, Grammar g) {
	this(input);
	this.g = g;
}
}

topdown
    :	globalScope
    |	action
    |	tokenAlias
    |	rule
    |	ruleArg
    |	ruleReturns
    |	terminal
	;

bottomup
	:	finishRule
	;

globalScope
	:	{inContext("GRAMMAR")}? ^(SCOPE ID ACTION) {scopes.add($ID);}
	;

action
	:	{inContext("GRAMMAR")}? ^(AT sc=ID? ID ACTION)
		{actions.add($AT);}
	;

tokenAlias
	:	{inContext("TOKENS")}?
		(	^(ASSIGN t=ID STRING_LITERAL) {terminals.add($t); aliases.add($ASSIGN);}
		|	t=ID						  {terminals.add($t);}
		)
	;

rule:   ^( RULE name=ID .+)
		{
		Rule r = new Rule($name.text, (GrammarASTWithOptions)$RULE);
		rules.add(r);
		currentRule = r;
		}
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

terminal
    :	{!inContext("TOKENS ASSIGN")}? STRING_LITERAL	{terminals.add($start);}
    |	TOKEN_REF										{terminals.add($start);}
    ;

/*
ruleref
    :	^(ROOT RULE_REF ARG_ACTION?)
    |	^(BANG RULE_REF ARG_ACTION?)
    |	^(RULE_REF ARG_ACTION?)
    ;
*/
