/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class ProfileDescriptors {
	public static class NoLAProfile extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "{decision=0, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ID+ ;
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}


	public static class FatProfile extends BaseParserTestDescriptor {
		/**


;**************************************************************
;*
;*             C P / M   version   2 . 2
;*
;*   Reconstructed from memory image on February 27, 1981
;*
;*                by Clark A. Calkins
;*
;**************************************************************
;
;   Set memory limit here. This is the amount of contigeous
; ram starting from 0000. CP/M will reside at the end of this space.
;
MEM	EQU	62	;for a 62k system (TS802 TEST - WORKS OK).
;
IOBYTE	EQU	3	;i/o definition byte.
TDRIVE	EQU	4	;current drive name and user number.
ENTRY	EQU	5	;entry point for the cp/m bdos.
TFCB	EQU	5CH	;default file control block.
TBUFF	EQU	80H	;i/o buffer and command line storage.
TBASE	EQU	100H	;transiant program storage area.
;
;   Set control character equates.
;
CNTRLC	EQU	3	;control-c
CNTRLE	EQU	05H	;control-e
BS	EQU	08H	;backspace
TAB	EQU	09H	;tab
LF	EQU	0AH	;line feed
FF	EQU	0CH	;form feed
CR	EQU	0DH	;carriage return
CNTRLP	EQU	10H	;control-p
CNTRLR	EQU	12H	;control-r
CNTRLS	EQU	13H	;control-s
CNTRLU	EQU	15H	;control-u
CNTRLX	EQU	18H	;control-x
CNTRLZ	EQU	1AH	;control-z (end-of-file mark)
DEL	EQU	7FH	;rubout
;
;   Set origin for CP/M
;
	ORG	(MEM-7)*1024
;
CBASE	JMP	COMMAND	;execute command processor (ccp).
	JMP	CLEARBUF	;entry to empty input buffer before starting ccp.



*/
	    @CommentHasStringValue
	    public String input;

	    public String output = "{decision=0, contextSensitivities=0, errors=0, ambiguities=22, SLL_lookahead=46, SLL_ATNTransitions=4, SLL_DFATransitions=42, LL_Fallback=22, LL_lookahead=22, LL_ATNTransitions=22}, {decision=1, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=2, contextSensitivities=0, errors=0, ambiguities=21, SLL_lookahead=68, SLL_ATNTransitions=6, SLL_DFATransitions=62, LL_Fallback=21, LL_lookahead=21, LL_ATNTransitions=21}, {decision=3, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=4, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=5, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=6, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=7, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=8, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=9, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=10, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}, {decision=11, contextSensitivities=0, errors=0, ambiguities=0, SLL_lookahead=0, SLL_ATNTransitions=0, SLL_DFATransitions=0, LL_Fallback=0, LL_lookahead=0, LL_ATNTransitions=0}\n";

	    public String errors = null;
	    public String startRule = "prog";
	    public String grammarName = "Asm8080";

		/**
grammar Asm8080;

prog : (line? EOL) + ;
line : lbl? (instruction | directive)? comment? ;
instruction : opcode expressionlist? ;
opcode : OPCODE ;
register_ : REGISTER ;
directive : argument? assemblerdirective expressionlist ;
assemblerdirective : ASSEMBLER_DIRECTIVE ;
lbl : label ':'? ;
expressionlist : expression (',' expression)* ;
label : name ;
expression : multiplyingExpression (('+' | '-') multiplyingExpression)* ;
multiplyingExpression : argument (('*' | '/') argument)* ;
argument : number | register_ | dollar | name | string | ('(' expression ')') ;
dollar : '$' ;
string : STRING ;
name : NAME ;
number : NUMBER ;
comment : COMMENT ;

ASSEMBLER_DIRECTIVE : (O R G) | (E N D) | (E Q U) | (D B) | (D W) | (D S) | (I F) | (E N D I F) | (S E T) ;
REGISTER : 'A' | 'B' | 'C' | 'D' | 'E' | 'H' | 'L' | 'PC' | 'SP' ;
OPCODE : (M O V) | (M V I) | (L D A) | (S T A) | (L D A X) | (S T A X) | (L H L D) | (S H L D) | (L X I) | (P U S H) | (P O P) | (X T H L) | (S P H L) | (P C H L) | (X C H G) | (A D D) | (S U B) | (I N R) | (D C R) | (C M P) | (A N A) | (O R A) | (X R A) | (A D I) | (S U I) | (C P I) | (A N I) | (O R I) | (X R I) | (D A A) | (A D C) | (A C I) | (S B B) | (S B I) | (D A D) | (I N X) | (D C X) | (J M P) | (C A L L) | (R E T) | (R A L) | (R A R) | (R L C) | (R R C) | (I N) | (O U T) | (C M C) | (S T C) | (C M A) | (H L T) | (N O P) | (D I) | (E I) | (R S T) | (J N Z) | (J Z) | (J N C) | (J C) | (J P O) | (J P E) | (J P) | (J M) | (C N Z) | (C Z) | (C N C) | (C C) | (C P O) | (C P E) | (C P) | (C M) | (R N Z) | (R Z) | (R N C) | (R C) | (R P O) | (R P E) | (R P) | (R M) ;
fragment A : ('a' | 'A') ;
fragment B : ('b' | 'B') ;
fragment C : ('c' | 'C') ;
fragment D : ('d' | 'D') ;
fragment E : ('e' | 'E') ;
fragment F : ('f' | 'F') ;
fragment G : ('g' | 'G') ;
fragment H : ('h' | 'H') ;
fragment I : ('i' | 'I') ;
fragment J : ('j' | 'J') ;
fragment K : ('k' | 'K') ;
fragment L : ('l' | 'L') ;
fragment M : ('m' | 'M') ;
fragment N : ('n' | 'N') ;
fragment O : ('o' | 'O') ;
fragment P : ('p' | 'P') ;
fragment Q : ('q' | 'Q') ;
fragment R : ('r' | 'R') ;
fragment S : ('s' | 'S') ;
fragment T : ('t' | 'T') ;
fragment U : ('u' | 'U') ;
fragment V : ('v' | 'V') ;
fragment W : ('w' | 'W') ;
fragment X : ('x' | 'X') ;
fragment Y : ('y' | 'Y') ;
fragment Z : ('z' | 'Z') ;
NAME : [a-zA-Z] [a-zA-Z0-9."]* ;
NUMBER : '$'? [0-9a-fA-F] + ('H' | 'h')? ;
COMMENT : ';' ~ [\r\n]* -> skip ;
STRING : '\\u0027' ~'\\u0027'* '\\u0027' ;
EOL : [\r\n] + ;
WS : [ \t] -> skip ;

*/
	    @CommentHasStringValue
	    public String grammar;

	}
}
