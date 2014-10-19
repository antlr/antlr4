grammar <grammarName>;
a : C {<InputText():writeln()>} ;
fragment A : '1' | '2';
fragment B : '3' '4';
C : A | B;
