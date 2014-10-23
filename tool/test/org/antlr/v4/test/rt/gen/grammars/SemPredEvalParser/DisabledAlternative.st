grammar <grammarName>;
cppCompilationUnit : content+ EOF;
content: anything | {<false>}? .;
anything: ANY_CHAR;
ANY_CHAR: [_a-zA-Z0-9];
