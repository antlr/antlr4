grammar <grammarName>;
cppCompilationUnit : content+ EOF;
content: anything | {<False()>}? .;
anything: ANY_CHAR;
ANY_CHAR: [_a-zA-Z0-9];
