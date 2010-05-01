tree grammar NFABytecodeTriggers;
options {
	language     = Java;
	tokenVocab   = ANTLRParser;
	ASTLabelType = GrammarAST;
	superClass   = NFABytecodeGenerator;
}

@header {
package org.antlr.v4.codegen;
import org.antlr.v4.tool.GrammarAST;
}

/*
e1 | e2 | e3:
	split 3, L1, L2, L3
L1:	e1
	jmp END
L2:	e2
	jmp END
L3:	e3
END:
*/
block
    :	^(	BLOCK (^(OPTIONS .+))?
    		{
    		GrammarAST firstAlt = (GrammarAST)input.LT(1);
    		int i = firstAlt.getChildIndex();    		
			int nAlts = $start.getChildCount() - i;
    		System.out.println("alts "+nAlts);
    		List<JumpInstr> jumps = new ArrayList<JumpInstr>();
    		SplitInstr S = null;
    		if ( nAlts>1 ) {
	    		S = new SplitInstr(nAlts);
	    		emit(S);
	    		S.addrs.add(ip);
    		}
    		int alt = 1;
    		}
    		(	alternative
    			{
    			if ( alt < nAlts ) {
	    			JumpInstr J = new JumpInstr();
	    			jumps.add(J);
	    			emit(J);
	    			S.addrs.add(ip);
    			}
    			alt++;
    			}
    		)+
    		{
    		int END = ip;
    		for (JumpInstr J : jumps) J.target = END;
    		}
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
	:	^(ASSIGN ID atom)			
	|	^(ASSIGN ID block)			
	|	^(PLUS_ASSIGN ID atom)		
	|	^(PLUS_ASSIGN ID block)		
	;

treeSpec
    : ^(TREE_BEGIN  (e=element )+)	
    ;

ebnf
	:	^(astBlockSuffix block)		
	|	{
	   	SplitInstr S = new SplitInstr(2);
		emit(S);
   		S.addrs.add(ip);
		}
		^(OPTIONAL block)			
		{
   		S.addrs.add(ip);
		}
	|	{
		int start=ip;
	   	SplitInstr S = new SplitInstr(2);
		emit(S);
		int blk = ip;
		}
		^(CLOSURE block)			
		{
	    JumpInstr J = new JumpInstr();
	    emit(J);
	    J.target = start;
   		S.addrs.add(blk);
	    S.addrs.add(ip); // reverse for nongreedy
		}
	|	{int start=ip;} ^(POSITIVE_CLOSURE block)
		{
   		SplitInstr S = new SplitInstr(2);
		emit(S);
		int stop = ip;
   		S.addrs.add(start);
   		S.addrs.add(stop);
		}
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
    |	^(WILDCARD .)		{emit(new WildcardInstr($WILDCARD.token));}		
    |	WILDCARD			{emit(new WildcardInstr($WILDCARD.token));}	
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
    	{emit(new RangeInstr($a.token, $b.token));}
    ;

terminal
    :  ^(STRING_LITERAL .)			{emitString($STRING_LITERAL.token);}
    |	STRING_LITERAL				{emitString($STRING_LITERAL.token);}
    |	^(TOKEN_REF ARG_ACTION .)	
    |	^(TOKEN_REF .)				
    |	TOKEN_REF					
    |	^(ROOT terminal)			
    |	^(BANG terminal)			
    ;