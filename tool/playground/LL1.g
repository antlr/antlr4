grammar LL1;

b : B | C ;

c1 : A? B ;

c2 : (B|C)? D ;

d1 : A* B ;

d2 : ({true}? B | {true}? A)* D {System.out.println("works!");} ;

e1 : A+ B ;
e2 : (B|A)+ D ;
