// The lexer will create rules to match letters a, b, c.
// The associated token types A, B, C must have the same value
// and all import'd parsers.  Since ANTLR regenerates all imports
// for use with the delegator M, it can generate the same token type
// mapping in each parser:
// public static final int C=6;
// public static final int EOF=-1;
// public static final int B=5;
// public static final int WS=7;
// public static final int A=4;
grammar M;
import S,T;
s : x y ; // matches AA, which should be 'aa'
B : 'b' ; // another order: B, A, C
A : 'a' ; 
C : 'c' ; 
WS : (' '|'\n') -> skip ;
