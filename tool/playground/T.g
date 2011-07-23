grammar T;
//options {output=AST;}

a : a PLUS a
  | INT
  ;

/*
a : a_[0] ;
a_[int _p] : a_primary ( {$_p <= 2}? PLUS a{} )*
    ;
a_primary : INT ;
*/
/*
a : a_[0] ;
a_[int _p] : a_primary ( {_p <= 2}? B )* ;
a_primary : A ;
*/
