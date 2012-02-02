/** Simple statically-typed programming language with methods and variables
 *  taken from "Language Implementation Patterns" book.
 */
grammar E;

// START: file
file:   (methodDecl | varDecl)+ ;
// END: file

// START: var
varDecl
    :   type ID ('=' expr)? ';'
    ;
type:   'float' | 'int' | 'void' ; // user-defined types
// END: var

// START: method
methodDecl
    :   type ID '(' formalParameters? ')' block // "void f(int x) {...}"
    ;

formalParameters
    :   type ID (',' type ID)*
    ;
// END: method

// START: stat
block:  '{' stat* '}' ;   // possibly empty statement block

stat:   block
    |   varDecl
    |   'if' expr 'then' stat ('else' stat)?
    |   'return' expr? ';' 
    |   expr '=' expr ';' // assignment
    |   expr ';'          // func call
    ;
// END: stat

/* expr below becomes the following non-left recursive rule:
expr[int _p]
    :   ( '-' expr[6]
        | '!' expr[5]
        | ID
        | INT
        | '(' expr ')'
        )
        ( {8 >= $_p}? '*' expr[9]
        | {7 >= $_p}? ('+'|'-') expr[8]
        | {4 >= $_p}? '==' expr[5]
        | {10 >= $_p}? '[' expr ']'
        | {9 >= $_p}? '(' exprList? ')'
        )*
    ;
*/

// START: expr
expr:   expr '[' expr ']'       // array index like a[i], a[i][j]
    |   expr '(' exprList? ')'  // func call like f(), f(x), f(1,2)
    |   expr '*' expr
    |   expr ('+'|'-') expr
    |   '-' expr                // unary minus
    |   '!' expr                // boolean not
    |   expr '==' expr          // equality comparison (lowest priority op)
    |   ID                      // variable reference
    |   INT
    |   '(' expr ')' 
    ;

exprList : expr (',' expr)* ;   // arg list
// END: expr

ID  :   LETTER (LETTER | [0-9])* ;
fragment
LETTER : [a-zA-Z] ;

INT :   [0-9]+ ;

WS  :   [ \t\n\r]+ -> skip ;

SL_COMMENT
    :   '//' .* '\n' -> skip
    ;
