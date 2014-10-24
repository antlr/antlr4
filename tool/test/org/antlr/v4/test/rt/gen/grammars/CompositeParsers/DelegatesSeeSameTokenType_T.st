parser grammar S;
tokens { C, B, A } // reverse order
y : A {<writeln("\"T.y\"")>};
