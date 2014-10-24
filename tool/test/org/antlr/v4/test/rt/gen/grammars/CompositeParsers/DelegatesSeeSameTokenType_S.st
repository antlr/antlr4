parser grammar S;
tokens { A, B, C }
x : A {<writeln("\"S.x\"")>};
