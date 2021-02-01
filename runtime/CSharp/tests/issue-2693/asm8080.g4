/*
BSD License

Copyright (c) 2018, Tom Everett
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of Tom Everett nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/*
* http://fms.komkon.org/comp/CPUs/8080.txt
*/

grammar asm8080;

prog
   : (line? EOL) +
   ;

line
   : lbl? (instruction | directive)? comment?
   ;

instruction
   : opcode expressionlist?
   ;

opcode
   : OPCODE
   ;

register_
   : REGISTER
   ;

directive
   : argument? assemblerdirective expressionlist
   ;

assemblerdirective
   : ASSEMBLER_DIRECTIVE
   ;

lbl
   : label ':'?
   ;

expressionlist
   : expression (',' expression)*
   ;

label
   : name
   ;

expression
   : multiplyingExpression (('+' | '-') multiplyingExpression)*
   ;

multiplyingExpression
   : argument (('*' | '/') argument)*
   ;

argument
   : number
   | register_
   | dollar
   | name
   | string
   | ('(' expression ')')
   ;

dollar
   : '$'
   ;

string
   : STRING
   ;

name
   : NAME
   ;

number
   : NUMBER
   ;

comment
   : COMMENT
   ;


ASSEMBLER_DIRECTIVE
   : (O R G) | (E N D) | (E Q U) | (D B) | (D W) | (D S) | (I F) | (E N D I F) | (S E T)
   ;


REGISTER
   : 'A' | 'B' | 'C' | 'D' | 'E' | 'H' | 'L' | 'PC' | 'SP'
   ;


OPCODE
   : (M O V) | (M V I) | (L D A) | (S T A) | (L D A X) | (S T A X) | (L H L D) | (S H L D) | (L X I) | (P U S H) | (P O P) | (X T H L) | (S P H L) | (P C H L) | (X C H G) | (A D D) | (S U B) | (I N R) | (D C R) | (C M P) | (A N A) | (O R A) | (X R A) | (A D I) | (S U I) | (C P I) | (A N I) | (O R I) | (X R I) | (D A A) | (A D C) | (A C I) | (S B B) | (S B I) | (D A D) | (I N X) | (D C X) | (J M P) | (C A L L) | (R E T) | (R A L) | (R A R) | (R L C) | (R R C) | (I N) | (O U T) | (C M C) | (S T C) | (C M A) | (H L T) | (N O P) | (D I) | (E I) | (R S T) | (J N Z) | (J Z) | (J N C) | (J C) | (J P O) | (J P E) | (J P) | (J M) | (C N Z) | (C Z) | (C N C) | (C C) | (C P O) | (C P E) | (C P) | (C M) | (R N Z) | (R Z) | (R N C) | (R C) | (R P O) | (R P E) | (R P) | (R M)
   ;


fragment A
   : ('a' | 'A')
   ;


fragment B
   : ('b' | 'B')
   ;


fragment C
   : ('c' | 'C')
   ;


fragment D
   : ('d' | 'D')
   ;


fragment E
   : ('e' | 'E')
   ;


fragment F
   : ('f' | 'F')
   ;


fragment G
   : ('g' | 'G')
   ;


fragment H
   : ('h' | 'H')
   ;


fragment I
   : ('i' | 'I')
   ;


fragment J
   : ('j' | 'J')
   ;


fragment K
   : ('k' | 'K')
   ;


fragment L
   : ('l' | 'L')
   ;


fragment M
   : ('m' | 'M')
   ;


fragment N
   : ('n' | 'N')
   ;


fragment O
   : ('o' | 'O')
   ;


fragment P
   : ('p' | 'P')
   ;


fragment Q
   : ('q' | 'Q')
   ;


fragment R
   : ('r' | 'R')
   ;


fragment S
   : ('s' | 'S')
   ;


fragment T
   : ('t' | 'T')
   ;


fragment U
   : ('u' | 'U')
   ;


fragment V
   : ('v' | 'V')
   ;


fragment W
   : ('w' | 'W')
   ;


fragment X
   : ('x' | 'X')
   ;


fragment Y
   : ('y' | 'Y')
   ;


fragment Z
   : ('z' | 'Z')
   ;


NAME
   : [a-zA-Z] [a-zA-Z0-9."]*
   ;


NUMBER
   : '$'? [0-9a-fA-F] + ('H' | 'h')?
   ;


COMMENT
   : ';' ~ [\r\n]* -> skip
   ;


STRING
   : '\u0027' ~'\u0027'* '\u0027'
   ;


EOL
   : [\r\n] +
   ;


WS
   : [ \t] -> skip
   ;
