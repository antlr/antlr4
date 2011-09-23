tree grammar UWalker;
options {tokenVocab=U;}

a : ^(DECL x+=ID y=INT) ;
