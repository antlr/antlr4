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
	public int codeBlockLevel = -1;
	public int treeLevel = -1;
	public OutputModelController controller;
    public SourceGenTriggers(TreeNodeStream input, OutputModelController controller) {
    	this(input);
    	this.controller = controller;
    }
}

dummy : block[null, null] ;

block[GrammarAST label, GrammarAST ebnfRoot] returns [List<? extends SrcOp> omos]
    :	^(	blk=BLOCK (^(OPTIONS .+))?
			{List<CodeBlockForAlt> alts = new ArrayList<CodeBlockForAlt>();}
    		(	alternative
    			{
		    	controller.finishAlternative($alternative.altCodeBlock, $alternative.ops);
    			alts.add($alternative.altCodeBlock);
    			}
    		)+
    	)
    	{
    	if ( alts.size()==1 && ebnfRoot==null) return alts;
    	if ( ebnfRoot==null ) {
    	    $omos = DefaultOutputModelFactory.list(controller.getChoiceBlock((BlockAST)$blk, alts));
    	}
    	else {
    	    $omos = DefaultOutputModelFactory.list(controller.getEBNFBlock($ebnfRoot, alts));
    	}
    	}
    ;

/*
alternative returns [CodeBlockForAlt altCodeBlock]
@init {
	// set alt if outer ALT only
	if ( inContext("RULE BLOCK") && ((AltAST)$start).alt!=null ) controller.setCurrentAlt(((AltAST)$start).alt);
}
	:	alternative_with_rewrite {$altCodeBlock = $alternative_with_rewrite.altCodeBlock;}

	|	^(ALT EPSILON) {$altCodeBlock = controller.epsilon();}

    |	{
    	List<SrcOp> elems = new ArrayList<SrcOp>();
		$altCodeBlock = controller.alternative(controller.getCurrentAlt());
		$ops = elems;
		controller.setCurrentBlock($altCodeBlock);
		}
		^( ALT ( element {if ($element.omos!=null) elems.addAll($element.omos);} )+ )
	;
	
alternative_with_rewrite returns [CodeBlockForAlt altCodeBlock]
	:	^(ALT_REWRITE
    		a=alternative
    		(	rewrite {$a.ops.add($rewrite.code);} // insert at end of alt's code
    		|
    		)
    		{$altCodeBlock=$a.altCodeBlock; $ops=$a.ops;}
    	 )
	;
*/
	
alternative returns [CodeBlockForAlt altCodeBlock, List<SrcOp> ops]
@init {
	// set alt if outer ALT only
	if ( inContext("RULE BLOCK") && ((AltAST)$start).alt!=null ) controller.setCurrentAlt(((AltAST)$start).alt);
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
		$altCodeBlock = controller.alternative(controller.getCurrentAlt());
		$ops = elems;
		controller.setCurrentBlock($altCodeBlock);
		}
		^( ALT ( element {if ($element.omos!=null) elems.addAll($element.omos);} )+ )
    ;

element returns [List<? extends SrcOp> omos]
	:	labeledElement					{$omos = $labeledElement.omos;}
	|	atom[null]						{$omos = $atom.omos;}
	|	ebnf							{$omos = $ebnf.omos;}
	|   ACTION							{$omos = controller.action($ACTION);}
	|   FORCED_ACTION					{$omos = controller.forcedAction($FORCED_ACTION);}
	|   SEMPRED							{$omos = controller.sempred($SEMPRED);}
	|	GATED_SEMPRED
	|	treeSpec
	;

labeledElement returns [List<? extends SrcOp> omos]
	:	^(ASSIGN ID atom[$ID] )				{$omos = $atom.omos;}
	|	^(ASSIGN ID block[$ID,null])		{$omos = $block.omos;}
	|	^(PLUS_ASSIGN ID atom[$ID])			{$omos = $atom.omos;}
	|	^(PLUS_ASSIGN ID block[$ID,null])	{$omos = $block.omos;}
	;

treeSpec returns [SrcOp omo]
    : ^(TREE_BEGIN  (e=element )+)
    ;

ebnf returns [List<? extends SrcOp> omos]
	:	^(astBlockSuffix block[null,null])
	|	^(OPTIONAL block[null,$OPTIONAL])	{$omos = $block.omos;}
	|	^(CLOSURE block[null,$CLOSURE])		{$omos = $block.omos;}
	|	^(POSITIVE_CLOSURE block[null,$POSITIVE_CLOSURE])
										    {$omos = $block.omos;}
	| 	block[null, null]					{$omos = $block.omos;}
    ;

astBlockSuffix
    : ROOT
    | IMPLIES
    | BANG
    ;

// TODO: combine ROOT/BANG into one then just make new op ref'ing return value of atom/terminal...
// TODO: same for NOT
atom[GrammarAST label] returns [List<SrcOp> omos]
	:	^(ROOT notSet[label])
	|	^(BANG notSet[label])		{$omos = $notSet.omos;}
	|	notSet[label]
	|	range[label]				{$omos = $range.omos;}
	|	^(DOT ID terminal[label])
	|	^(DOT ID ruleref[label])
    |	^(WILDCARD .)
    |	WILDCARD
    |	^(ROOT terminal[label])		{$omos = controller.rootToken($terminal.omos);}
    |	^(BANG terminal[label])		{$omos = $terminal.omos;}
    |   terminal[label]				{$omos = $terminal.omos;}
    |	^(ROOT ruleref[label])		{$omos = controller.rootRule($ruleref.omos);}
    |	^(BANG ruleref[label])		{$omos = $ruleref.omos;}
    |   ruleref[label]				{$omos = $ruleref.omos;}
    ;

notSet[GrammarAST label] returns [List<SrcOp> omos]
    : ^(NOT terminal[label])
    | ^(NOT block[label,null])
    ;

ruleref[GrammarAST label] returns [List<SrcOp> omos]
    :	^(RULE_REF ARG_ACTION?)		{$omos = controller.ruleRef($RULE_REF, $label, $ARG_ACTION);}
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
    ;

// R E W R I T E  S T U F F

rewrite returns [Rewrite code]
	:	{
		treeLevel = 0;
		codeBlockLevel++;
		$code = controller.treeRewrite($start);
		CodeBlock save = controller.getCurrentBlock();
		controller.setCurrentBlock($code);
		}
		predicatedRewrite* nakedRewrite 	
		{
		$code.ops = $nakedRewrite.omos;
		controller.setCurrentBlock(save);
		codeBlockLevel--;
		}
	;

predicatedRewrite returns [List<SrcOp> omos]
	:	^(ST_RESULT SEMPRED rewriteSTAlt)
	|	^(RESULT SEMPRED rewriteTreeAlt)
	;

nakedRewrite returns [List<SrcOp> omos]
	:	^(ST_RESULT rewriteSTAlt)
	|	^(RESULT rewriteTreeAlt)			{$omos = $rewriteTreeAlt.omos;}
	;

rewriteTreeAlt returns [List<SrcOp> omos]
    :	^(ALT
    		{List<SrcOp> elems = new ArrayList<SrcOp>();}
    		( rewriteTreeElement {elems.addAll($rewriteTreeElement.omos);} )+
    	)
    	{$omos = elems;}
    |	ETC
    |	EPSILON
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
	|   LABEL
	|	ACTION
	;

rewriteTreeEbnf returns [CodeBlock op]
	:	^(	(a=OPTIONAL|a=CLOSURE)
			^(	REWRITE_BLOCK
				{
				codeBlockLevel++;
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
		codeBlockLevel--;
		}
	;
	
rewriteTree returns [List<SrcOp> omos]
	:	{
//		codeBlockLevel++;
		treeLevel++;
		List<SrcOp> elems = new ArrayList<SrcOp>();
		RewriteTreeStructure t = controller.rewrite_tree($start);
//		CodeBlock save = controller.getCurrentBlock();
//		controller.setCurrentBlock(t);
		}
		^(	TREE_BEGIN
			rewriteTreeAtom[true] {elems.addAll($rewriteTreeAtom.omos);}
			( rewriteTreeElement {elems.addAll($rewriteTreeElement.omos);} )*
		 )
		{
		t.ops = elems;
		$omos = DefaultOutputModelFactory.list(t);
//		controller.setCurrentBlock(save);
		treeLevel--;
//		codeBlockLevel--;
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
