parser grammar S;
tokens { C, B, A } // reverse order
y : A {<write("\"T.y\"")>};
