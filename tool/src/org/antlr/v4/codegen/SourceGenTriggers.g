tree grammar SourceGenTriggers;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
//	superClass   = NFABytecodeGenerator;
}

@header {
package org.antlr.v4.codegen;
import org.antlr.v4.codegen.nfa.*;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.GrammarASTWithOptions;
import org.antlr.v4.tool.LexerGrammar;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
}

block
    :	^(	BLOCK (^(OPTIONS .+))?
    		(	alternative    			
    		)+
    	)
    ;

alternative
    :	^(ALT_REWRITE a=alternative .)	
    |	^(ALT EPSILON)					
    |   ^(ALT (e=element )+)    									
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
	:	^(ASSIGN ID  atom  )
	|	^(ASSIGN ID block)			
	|	^(PLUS_ASSIGN ID atom)		
	|	^(PLUS_ASSIGN ID block)		
	;

treeSpec
    : ^(TREE_BEGIN  (e=element )+)	
    ;

ebnf
@init {
	GrammarASTWithOptions blk = (GrammarASTWithOptions)$start.getChild(0);
	String greedyOption = blk.getOption("greedy");
}
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

atom
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
    |   terminal				
    |   ruleref					
    ;

notSet
    : ^(NOT terminal)		
    | ^(NOT block)			
    ;

ruleref
    :	^(ROOT ^(RULE_REF ARG_ACTION?))	
    |	^(BANG ^(RULE_REF ARG_ACTION?))	
    |	^(RULE_REF ARG_ACTION?)			
    ;

range
    :	^(RANGE a=STRING_LITERAL b=STRING_LITERAL)
    	
    ;

terminal
    :  ^(STRING_LITERAL .)			
    |	STRING_LITERAL				
    |	^(TOKEN_REF ARG_ACTION .)	
    |	^(TOKEN_REF .)				
    |	TOKEN_REF					
    |	^(ROOT terminal)			
    |	^(BANG terminal)			
    ;
