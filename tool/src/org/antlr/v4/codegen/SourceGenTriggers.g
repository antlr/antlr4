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
tree grammar SourceGenTriggers;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
}

@header {
package org.antlr.v4.codegen;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.codegen.model.*;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.codegen.model.ast.*;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
}

@members {
	public OutputModelController controller;
    public SourceGenTriggers(TreeNodeStream input, OutputModelController controller) {
    	this(input);
    	this.controller = controller;
    }
}

dummy : block[null, null, null] ;

block[GrammarAST label, GrammarAST ebnfRoot, GrammarAST astOp] returns [List<? extends SrcOp> omos]
    :	^(	blk=BLOCK (^(OPTIONS .+))?
			{List<CodeBlockForAlt> alts = new ArrayList<CodeBlockForAlt>();}
    		( alternative {alts.add($alternative.altCodeBlock);} )+
    	)
    	{
    	if ( alts.size()==1 && ebnfRoot==null) return alts;
    	if ( ebnfRoot==null ) {
    	    $omos = DefaultOutputModelFactory.list(controller.getChoiceBlock((BlockAST)$blk, alts, $label));
    	}
    	else {
    	    $omos = DefaultOutputModelFactory.list(controller.getEBNFBlock($ebnfRoot, alts));
    	}
    	}
    ;

alternative returns [CodeBlockForAlt altCodeBlock, List<SrcOp> ops]
@init {
   	boolean outerMost = inContext("RULE BLOCK");
}
@after {
   	controller.finishAlternative($altCodeBlock, $ops, outerMost);
}
    :	^(ALT_REWRITE
    		a=alt[outerMost]
    		(	rewrite {$a.ops.add($rewrite.code);} // insert at end of alt's code
    		|
    		)
    		{$altCodeBlock=$a.altCodeBlock; $ops=$a.ops;}
    	 )
   	|	a=alt[outerMost] {$altCodeBlock=$a.altCodeBlock; $ops=$a.ops;}
	;

alt[boolean outerMost] returns [CodeBlockForAlt altCodeBlock, List<SrcOp> ops]
	:	{
		// set alt if outer ALT only (the only ones with alt field set to Alternative object)
		if ( outerMost ) controller.setCurrentOuterMostAlt(((AltAST)$start).alt);
    	List<SrcOp> elems = new ArrayList<SrcOp>();
		$altCodeBlock = controller.alternative(controller.getCurrentOuterMostAlt(), outerMost);
		$altCodeBlock.ops = $ops = elems;
		controller.setCurrentBlock($altCodeBlock);
		}
		^( ALT ( element {if ($element.omos!=null) elems.addAll($element.omos);} )+ )

	|	^(ALT EPSILON) {$altCodeBlock = controller.epsilon();}
    ;

element returns [List<? extends SrcOp> omos]
	:	labeledElement					{$omos = $labeledElement.omos;}
	|	atom[null,null,false]			{$omos = $atom.omos;}
	|	subrule							{$omos = $subrule.omos;}
	|   ACTION							{$omos = controller.action($ACTION);}
	|   SEMPRED							{$omos = controller.sempred($SEMPRED);}
	|	^(ACTION elementOptions)		{$omos = controller.action($ACTION);}
	|   ^(SEMPRED elementOptions)		{$omos = controller.sempred($SEMPRED);}
	|	treeSpec						{$omos = DefaultOutputModelFactory.list($treeSpec.treeMatch);}
	;

labeledElement returns [List<? extends SrcOp> omos]
	:	^(ASSIGN ID atom[$ID,null,false] )			{$omos = $atom.omos;}
	|	^(PLUS_ASSIGN ID atom[$ID,null,false])		{$omos = $atom.omos;}
	|	^(ASSIGN ID block[$ID,null,null] )			{$omos = $block.omos;}
	|	^(PLUS_ASSIGN ID block[$ID,null,null])		{$omos = $block.omos;}
	;

treeSpec returns [MatchTree treeMatch]
@init {
   	List<SrcOp> elems = new ArrayList<SrcOp>();
}
    :	^(TREE_BEGIN
       		(e=element {if ($e.omos!=null) elems.addAll($e.omos);})+
    	 )
    	{$treeMatch = controller.tree($TREE_BEGIN, elems);}
    ;

subrule returns [List<? extends SrcOp> omos]
	:	^(astBlockSuffix block[null,null,$astBlockSuffix.start]) {$omos = $block.omos;}
	|	^(OPTIONAL b=block[null,$OPTIONAL,null])
		{
		$omos = $block.omos;
		}
	|	(	^(op=CLOSURE b=block[null,null,null])
		|	^(op=POSITIVE_CLOSURE b=block[null,null,null])
		)
		{
		List<CodeBlockForAlt> alts = new ArrayList<CodeBlockForAlt>();
		SrcOp blk = $b.omos.get(0);
		CodeBlockForAlt alt = new CodeBlockForAlt(controller.delegate);
		alt.addOp(blk);
		alts.add(alt);
		SrcOp loop = controller.getEBNFBlock($op, alts); // "star it"
   	    $omos = DefaultOutputModelFactory.list(loop);
		}
	| 	block[null, null,null]					{$omos = $block.omos;}
    ;

astBlockSuffix
    : ROOT
    | IMPLIES
    | BANG
    ;

blockSet[GrammarAST label, GrammarAST astOp, boolean invert] returns [List<SrcOp> omos]
    :	^(SET atom[null,null,false]+) {$omos = controller.set($SET, $label, $astOp, invert);}
    ;

/*
setElement
	:	STRING_LITERAL
	|	TOKEN_REF
	|	^(RANGE STRING_LITERAL STRING_LITERAL)
	;
*/

// TODO: combine ROOT/BANG into one then just make new op ref'ing return value of atom/terminal...
// TODO: same for NOT
atom[GrammarAST label, GrammarAST astOp, boolean invert] returns [List<SrcOp> omos]
	:	^(op=(ROOT|BANG) a=atom[$label, $op, $invert] )	{$omos = $a.omos;}
	|	^(NOT a=atom[$label, $astOp, true])		{$omos = $a.omos;}
	|	range[label]							{$omos = $range.omos;}
	|	^(DOT ID terminal[$label, null])
	|	^(DOT ID ruleref[$label, null])
    |	^(WILDCARD .)							{$omos = controller.wildcard($WILDCARD, $label, $astOp);}
    |	WILDCARD								{$omos = controller.wildcard($WILDCARD, $label, $astOp);}
    |   terminal[label, $astOp]					{$omos = $terminal.omos;}
    |   ruleref[label, $astOp]					{$omos = $ruleref.omos;}
	|	blockSet[$label, $astOp, invert]		{$omos = $blockSet.omos;}
	;

ruleref[GrammarAST label, GrammarAST astOp] returns [List<SrcOp> omos]
    :	^(RULE_REF ARG_ACTION?)		{$omos = controller.ruleRef($RULE_REF, $label, $ARG_ACTION, $astOp);}
    ;

range[GrammarAST label] returns [List<SrcOp> omos]
    :	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
    ;

terminal[GrammarAST label, GrammarAST astOp] returns [List<SrcOp> omos]
    :  ^(STRING_LITERAL .)			{$omos = controller.stringRef($STRING_LITERAL, $label, $astOp);}
    |	STRING_LITERAL				{$omos = controller.stringRef($STRING_LITERAL, $label, $astOp);}
    |	^(TOKEN_REF ARG_ACTION .)	{$omos = controller.tokenRef($TOKEN_REF, $label, $ARG_ACTION, $astOp);}
    |	^(TOKEN_REF .)				{$omos = controller.tokenRef($TOKEN_REF, $label, null, $astOp);}
    |	TOKEN_REF					{$omos = controller.tokenRef($TOKEN_REF, $label, null, $astOp);}
    |	DOWN_TOKEN					{$omos = controller.tokenRef($DOWN_TOKEN, null, null, null);}
    |	UP_TOKEN					{$omos = controller.tokenRef($UP_TOKEN, null, null, null);}
    ;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption+)
    ;

elementOption
    :	ID
    |   ^(ASSIGN ID ID)
    |   ^(ASSIGN ID STRING_LITERAL)
    |   ^(ASSIGN ID DOUBLE_QUOTE_STRING_LITERAL)
    ;

// R E W R I T E  S T U F F

rewrite returns [Rewrite code]
	:	{
		controller.treeLevel = 0;
		controller.codeBlockLevel++;
		$code = controller.treeRewrite($start);
		CodeBlock save = controller.getCurrentBlock();
		controller.setCurrentBlock($code);
		}
		(	(p=predicatedRewrite {$code.alts.add($p.alt);})+
			r=nakedRewrite					{$code.alts.add($r.alt);}
		|	r=nakedRewrite					{$code.alts.add($r.alt);}
		)
		{
		controller.setCurrentBlock(save);
		controller.codeBlockLevel--;
		}
	;

predicatedRewrite returns [RewriteChoice alt]
	:	^(ST_RESULT SEMPRED rewriteSTAlt)
	|	^(RESULT SEMPRED rewriteTreeAlt)	{$alt = controller.rewrite_choice((PredAST)$SEMPRED, $rewriteTreeAlt.omos);}
	;

nakedRewrite returns [RewriteChoice alt]
	:	^(ST_RESULT rewriteSTAlt)
	|	^(RESULT rewriteTreeAlt)			{$alt = controller.rewrite_choice(null, $rewriteTreeAlt.omos);}
	;

rewriteTreeAlt returns [List<SrcOp> omos]
    :	^(REWRITE_SEQ
    		{List<SrcOp> elems = new ArrayList<SrcOp>();}
    		( rewriteTreeElement {elems.addAll($rewriteTreeElement.omos);} )+
    	)
    	{$omos = elems;}
    |	ETC
    |	EPSILON								{$omos = controller.rewrite_epsilon($EPSILON);}
    ;

rewriteTreeElement returns [List<SrcOp> omos]
	:	rewriteTreeAtom[false]				{$omos = $rewriteTreeAtom.omos;}
	|	rewriteTree							{$omos = $rewriteTree.omos;}
	|   rewriteTreeEbnf						{$omos = DefaultOutputModelFactory.list($rewriteTreeEbnf.op);}
	;

rewriteTreeAtom[boolean isRoot] returns [List<SrcOp> omos]
    :   ^(TOKEN_REF elementOptions ARG_ACTION) {$omos = controller.rewrite_tokenRef($TOKEN_REF, $isRoot, (ActionAST)$ARG_ACTION);}
    |   ^(TOKEN_REF elementOptions)			{$omos = controller.rewrite_tokenRef($TOKEN_REF, $isRoot, null);}
    |   ^(TOKEN_REF ARG_ACTION)				{$omos = controller.rewrite_tokenRef($TOKEN_REF, $isRoot, (ActionAST)$ARG_ACTION);}
	|   TOKEN_REF							{$omos = controller.rewrite_tokenRef($TOKEN_REF, $isRoot, null);}
    |   RULE_REF							{$omos = controller.rewrite_ruleRef($RULE_REF, $isRoot);}
	|   ^(STRING_LITERAL elementOptions)	{$omos = controller.rewrite_stringRef($STRING_LITERAL, $isRoot);}
	|   STRING_LITERAL						{$omos = controller.rewrite_stringRef($STRING_LITERAL, $isRoot);}
	|   LABEL								{$omos = controller.rewrite_labelRef($LABEL, $isRoot);}
	|	ACTION								{$omos = controller.rewrite_action((ActionAST)$ACTION, $isRoot);}
	;

rewriteTreeEbnf returns [CodeBlock op]
	:	^(	(a=OPTIONAL|a=CLOSURE|a=POSITIVE_CLOSURE)
			^(	REWRITE_BLOCK
				{
				controller.codeBlockLevel++;
				if ( $a.getType()==OPTIONAL ) $op = controller.rewrite_optional($start);
				else $op = controller.rewrite_closure($start);
				CodeBlock save = controller.getCurrentBlock();
				controller.setCurrentBlock($op);
				}
				talt=rewriteTreeAlt
			)
		)
		{
		$op.addOps($talt.omos);
		controller.setCurrentBlock(save);
		controller.codeBlockLevel--;
		}
	;

rewriteTree returns [List<SrcOp> omos]
	:	{
		controller.treeLevel++;
		List<SrcOp> elems = new ArrayList<SrcOp>();
		RewriteTreeStructure t = controller.rewrite_treeStructure($start);
		}
		^(	TREE_BEGIN
			rewriteTreeAtom[true] {elems.addAll($rewriteTreeAtom.omos);}
			( rewriteTreeElement {elems.addAll($rewriteTreeElement.omos);} )*
		 )
		{
		t.ops = elems;
		$omos = DefaultOutputModelFactory.list(t);
		controller.treeLevel--;
		}
	;

rewriteSTAlt returns [List<SrcOp> omos]
    :	rewriteTemplate
    |	ETC
    |	EPSILON
    ;

rewriteTemplate returns [List<SrcOp> omos]
	:	^(TEMPLATE rewriteTemplateArgs? DOUBLE_QUOTE_STRING_LITERAL)
	|	^(TEMPLATE rewriteTemplateArgs? DOUBLE_ANGLE_STRING_LITERAL)
	|	rewriteTemplateRef
	|	rewriteIndirectTemplateHead
	|	ACTION
	;

rewriteTemplateRef returns [List<SrcOp> omos]
	:	^(TEMPLATE ID rewriteTemplateArgs?)
	;

rewriteIndirectTemplateHead returns [List<SrcOp> omos]
	:	^(TEMPLATE ACTION rewriteTemplateArgs?)
	;

rewriteTemplateArgs returns [List<SrcOp> omos]
	:	^(ARGLIST rewriteTemplateArg+)
	;

rewriteTemplateArg returns [List<SrcOp> omos]
	:   ^(ARG ID ACTION)
	;
