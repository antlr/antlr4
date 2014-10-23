parser grammar S;
a[int x] returns [int y] : B {<write("\"S.a\"")>;$y=1000;};
