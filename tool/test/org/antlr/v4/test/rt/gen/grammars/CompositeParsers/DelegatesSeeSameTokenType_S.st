parser grammar S;
tokens { A, B, C }
x : A {<write("\"S.x\"")>};
