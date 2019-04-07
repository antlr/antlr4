grammar TestIncrementalBasic;
options {
	incremental = true;
}
program: (identifier | digits)+;
identifier: IDENT;
digits: DIGITS;
// We deliberately put these on a hidden channel rather than skip - it helps
// make the cases weirder by making the parser's token indexes non-contiguous.
WS: [ \t\r\n\u000C]+ -> channel(HIDDEN);
IDENT: [A-Za-z]+;
DIGITS: [0-9]+;
