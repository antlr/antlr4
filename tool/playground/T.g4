grammar T;

a
    : 'b'   #alt1
    | 'c'   #alt2
    ;

b : 'x' | 'y' {} ;

e : e '*' e
  | 'foo'
  ;
