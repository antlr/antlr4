tree grammar SourceGenTriggers;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
//	superClass   = NFABytecodeGenerator;
}

@header {
package org.antlr.v4.codegen;
import org.antlr.v4.codegen.src.*;
import org.antlr.v4.tool.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
}

@members {
// TODO: identical grammar to NFABytecodeTriggers; would be nice to combine
	public CodeGenerator gen;
    public SourceGenTriggers(TreeNodeStream input, CodeGenerator gen) {
    	this(input);
    	this.gen = gen;
    }
}

block returns [CodeBlock omo]
    :	^(	BLOCK (^(OPTIONS .+))?
		{List<CodeBlock> alts = new ArrayList<CodeBlock>();}
    		( alternative {alts.add($alternative.omo);} )+
    	)
    	{
    	Choice c = new LL1Choice(gen, alts); // TODO: assumes LL1
		$omo = new CodeBlock(gen, c);
    	}
    ;

alternative returns [CodeBlock omo]
@init {List<SrcOp> elems = new ArrayList<SrcOp>();}
    :	^(ALT_REWRITE a=alternative .)	
    |	^(ALT EPSILON) {$omo = new CodeBlock(gen);}
    |   ^( ALT ( element {elems.add($element.omo);} )+ ) {$omo = new CodeBlock(gen, elems);}
    ;

element returns [SrcOp omo]
	:	labeledElement				
	|	atom						{$omo = $atom.omo;}
	|	ebnf						
	|   ACTION						
	|   SEMPRED					
	|	GATED_SEMPRED	
	|	treeSpec					
	;
	
labeledElement returns [SrcOp omo]
	:	^(ASSIGN ID  atom  )
	|	^(ASSIGN ID block)			
	|	^(PLUS_ASSIGN ID atom)		
	|	^(PLUS_ASSIGN ID block)		
	;

treeSpec returns [SrcOp omo]
    : ^(TREE_BEGIN  (e=element )+)	
    ;

ebnf returns [SrcOp omo]
	:	^(astBlockSuffix block)		
	|	^(OPTIONAL block)			
		
	|	^(CLOSURE block)			
		
	|	^(POSITIVE_CLOSURE block)
		
	| 	block						
    ;

astBlockSuffix
    : ROOT
    | IMPLIES
    | BANG
    ;

atom returns [SrcOp omo]
	:	^(ROOT range)			
	|	^(BANG range)			
	|	^(ROOT notSet)			
	|	^(BANG notSet)			
	|	notSet					
	|	range					
	|	^(DOT ID terminal)		
	|	^(DOT ID ruleref)		
    |	^(WILDCARD .)				
    |	WILDCARD				
    |   terminal				{$omo = $terminal.omo;}
    |   ruleref					{$omo = $ruleref.omo;}
    ;

notSet returns [SrcOp omo]
    : ^(NOT terminal)		
    | ^(NOT block)			
    ;

ruleref returns [SrcOp omo]
    :	^(ROOT ^(RULE_REF ARG_ACTION?))	
    |	^(BANG ^(RULE_REF ARG_ACTION?))	
    |	^(RULE_REF ARG_ACTION?)			
    ;

range returns [SrcOp omo]
    :	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
    	
    ;

terminal returns [MatchToken omo]
    :  ^(STRING_LITERAL .)			
    |	STRING_LITERAL				
    |	^(TOKEN_REF ARG_ACTION .)	
    |	^(TOKEN_REF .)				
    |	TOKEN_REF					{$omo = new MatchToken(gen, (TerminalAST)$TOKEN_REF);}
    |	^(ROOT terminal)			
    |	^(BANG terminal)			
    ;
