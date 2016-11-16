grammar redeclared;

s : A=ID B=ID
  | X=id Y=id
  ;

id : ID2;

ID : 'a';
ID2 : 'b';