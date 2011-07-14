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
    		(	alternative
    			{
		    	boolean outerMost = inContext("RULE BLOCK") || inContext("RULE BLOCK ALT_REWRITE");
		    	controller.finishAlternative($alternative.altCodeBlock, $alternative.ops, outerMost);
    			alts.add($alternative.altCodeBlock);
    			}
    		)+
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
	// set alt if outer ALT only
	if ( inContext("RULE BLOCK") && ((AltAST)$start).alt!=null ) {
		controller.setCurrentOuterMostAlt(((AltAST)$start).alt);
	}
}
    :	^(ALT_REWRITE
    		a=alternative
    		(	rewrite {$a.ops.add($rewrite.code);} // insert at end of alt's code
    		|
    		)
    		{$altCodeBlock=$a.altCodeBlock; $ops=$a.ops;}
    	 )

    |	^(ALT EPSILON) {$altCodeBlock = controller.epsilon();}

    |	{
    	List<SrcOp> elems = new ArrayList<SrcOp>();
    	boolean outerMost = inContext("RULE BLOCK") || inContext("RULE BLOCK ALT_REWRITE");
		$altCodeBlock = controller.alternative(controller.getCurrentOuterMostAlt(), outerMost);
		$altCodeBlock.ops = $ops = elems;
		controller.setCurrentBlock($altCodeBlock);
		}
		^( ALT ( element {if ($element.omos!=null) elems.addAll($element.omos);} )+ )
    ;

element returns [List<? extends SrcOp> omos]
	:	labeledElement					{$omos = $labeledElement.omos;}
	|	atom[null,null,false]			{$omos = $atom.omos;}
	|	subrule							{$omos = $subrule.omos;}
	|   ACTION							{$omos = controller.action($ACTION);}
	|   FORCED_ACTION					{$omos = controller.forcedAction($FORCED_ACTION);}
	|   SEMPRED							{$omos = controller.sempred($SEMPRED);}
	|	GATED_SEMPRED
	|	treeSpec
	;

labeledElement returns [List<? extends SrcOp> omos]
	:	^(ASSIGN ID atom[$ID,null,false] )			{$omos = $atom.omos;}
	|	^(PLUS_ASSIGN ID atom[$ID,null,false])		{$omos = $atom.omos;}
	|	^(ASSIGN ID block[$ID,null,null] )			{$omos = $block.omos;}
	|	^(PLUS_ASSIGN ID block[$ID,null,null])		{$omos = $block.omos;}
	;

treeSpec returns [SrcOp omo]
    : ^(TREE_BEGIN  (e=element )+)
    ;

subrule returns [List<? extends SrcOp> omos]
	:	^(astBlockSuffix block[null,null,$astBlockSuffix.start]) {$omos = $block.omos;}
	|	^(OPTIONAL block[null,$OPTIONAL,null])	{$omos = $block.omos;}
	|	^(CLOSURE block[null,$CLOSURE,null])	{$omos = $block.omos;}
	|	^(POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE,null])
										    	{$omos = $block.omos;}
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
    |	^(WILDCARD .)							{$omos = controller.wildcard($WILDCARD, $label);}
    |	WILDCARD								{$omos = controller.wildcard($WILDCARD, $label);}
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
    ;

elementOptions
    :	^(ELEMENT_OPTIONS elementOption+)
    ;

elementOption
    :	ID
    |   ^(ASSIGN ID ID)
    |   ^(ASSIGN ID STRING_LITERAL)
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
    :	^(ALT
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
	:	^(	(a=OPTIONAL|a=CLOSURE)
			^(	REWRITE_BLOCK
				{
				controller.codeBlockLevel++;
				if ( $a.getType()==OPTIONAL ) $op = controller.rewrite_optional($start);
				else $op = controller.rewrite_closure($start);
				CodeBlock save = controller.getCurrentBlock();
				controller.setCurrentBlock($op);
				}
				alt=rewriteTreeAlt
			)
		)
		{
		$op.addOps($alt.omos);
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
