/*
 * [The "BSD license"]
 *  Copyright (c) 2012-2016 Terence Parr
 *  Copyright (c) 2012-2016 Sam Harwell
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
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
}

@members {
	public OutputModelController controller;
    public boolean hasLookaheadBlock;
    public SourceGenTriggers(TreeNodeStream input, OutputModelController controller) {
    	this(input);
    	this.controller = controller;
    }
}

dummy : block[null, null] ;

block[GrammarAST label, GrammarAST ebnfRoot] returns [List<? extends SrcOp> omos]
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
            Choice choice = controller.getEBNFBlock($ebnfRoot, alts);
            hasLookaheadBlock |= choice instanceof PlusBlock || choice instanceof StarBlock;
    	    $omos = DefaultOutputModelFactory.list(choice);
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
    :	a=alt[outerMost] {$altCodeBlock=$a.altCodeBlock; $ops=$a.ops;}
	;

alt[boolean outerMost] returns [CodeBlockForAlt altCodeBlock, List<SrcOp> ops]
@init {
	// set alt if outer ALT only (the only ones with alt field set to Alternative object)
	AltAST altAST = (AltAST)retval.start;
	if ( outerMost ) controller.setCurrentOuterMostAlt(altAST.alt);
}
	:	{
		List<SrcOp> elems = new ArrayList<SrcOp>();
		// TODO: shouldn't we pass $start to controller.alternative()?
		$altCodeBlock = controller.alternative(controller.getCurrentOuterMostAlt(), outerMost);
		$altCodeBlock.ops = $ops = elems;
		controller.setCurrentBlock($altCodeBlock);
		}
		^( ALT elementOptions? ( element {if ($element.omos!=null) elems.addAll($element.omos);} )+ )

	|	^(ALT elementOptions? EPSILON)
        {$altCodeBlock = controller.epsilon(controller.getCurrentOuterMostAlt(), outerMost);}
    ;

element returns [List<? extends SrcOp> omos]
	:	labeledElement					{$omos = $labeledElement.omos;}
	|	atom[null,false]			{$omos = $atom.omos;}
	|	subrule							{$omos = $subrule.omos;}
	|   ACTION							{$omos = controller.action((ActionAST)$ACTION);}
	|   SEMPRED							{$omos = controller.sempred((ActionAST)$SEMPRED);}
	|	^(ACTION elementOptions)		{$omos = controller.action((ActionAST)$ACTION);}
	|   ^(SEMPRED elementOptions)		{$omos = controller.sempred((ActionAST)$SEMPRED);}
	;

labeledElement returns [List<? extends SrcOp> omos]
	:	^(ASSIGN ID atom[$ID,false] )			{$omos = $atom.omos;}
	|	^(PLUS_ASSIGN ID atom[$ID,false])		{$omos = $atom.omos;}
	|	^(ASSIGN ID block[$ID,null] )			{$omos = $block.omos;}
	|	^(PLUS_ASSIGN ID block[$ID,null])		{$omos = $block.omos;}
	;

subrule returns [List<? extends SrcOp> omos]
	:	^(OPTIONAL b=block[null,$OPTIONAL])
		{
		$omos = $block.omos;
		}
	|	(	^(op=CLOSURE b=block[null,null])
		|	^(op=POSITIVE_CLOSURE b=block[null,null])
		)
		{
		List<CodeBlockForAlt> alts = new ArrayList<CodeBlockForAlt>();
		SrcOp blk = $b.omos.get(0);
		CodeBlockForAlt alt = new CodeBlockForAlt(controller.delegate);
		alt.addOp(blk);
		alts.add(alt);
		SrcOp loop = controller.getEBNFBlock($op, alts); // "star it"
        hasLookaheadBlock |= loop instanceof PlusBlock || loop instanceof StarBlock;
   	    $omos = DefaultOutputModelFactory.list(loop);
		}
	| 	block[null, null]					{$omos = $block.omos;}
    ;

blockSet[GrammarAST label, boolean invert] returns [List<SrcOp> omos]
    :	^(SET atom[label,invert]+) {$omos = controller.set($SET, $label, invert);}
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
atom[GrammarAST label, boolean invert] returns [List<SrcOp> omos]
	:	^(NOT a=atom[$label, true])		{$omos = $a.omos;}
	|	range[label]							{$omos = $range.omos;}
	|	^(DOT ID terminal[$label])
	|	^(DOT ID ruleref[$label])
    |	^(WILDCARD .)							{$omos = controller.wildcard($WILDCARD, $label);}
    |	WILDCARD								{$omos = controller.wildcard($WILDCARD, $label);}
    |   terminal[label]					{$omos = $terminal.omos;}
    |   ruleref[label]					{$omos = $ruleref.omos;}
	|	blockSet[$label, invert]		{$omos = $blockSet.omos;}
	;

ruleref[GrammarAST label] returns [List<SrcOp> omos]
    :	^(RULE_REF ARG_ACTION? elementOptions?)		{$omos = controller.ruleRef($RULE_REF, $label, $ARG_ACTION);}
    ;

range[GrammarAST label] returns [List<SrcOp> omos]
    :	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
    ;

terminal[GrammarAST label] returns [List<SrcOp> omos]
    :  ^(STRING_LITERAL .)			{$omos = controller.stringRef($STRING_LITERAL, $label);}
    |	STRING_LITERAL				{$omos = controller.stringRef($STRING_LITERAL, $label);}
    |	^(TOKEN_REF ARG_ACTION .)	{$omos = controller.tokenRef($TOKEN_REF, $label, $ARG_ACTION);}
    |	^(TOKEN_REF .)				{$omos = controller.tokenRef($TOKEN_REF, $label, null);}
    |	TOKEN_REF					{$omos = controller.tokenRef($TOKEN_REF, $label, null);}
    ;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption+)
    ;

elementOption
    :	ID
    |   ^(ASSIGN ID ID)
    |   ^(ASSIGN ID STRING_LITERAL)
    |   ^(ASSIGN ID ACTION)
    |   ^(ASSIGN ID INT)
    ;
