grammar <grammarName>;
start : statement+ ;
statement : 'x' | ifStatement;
ifStatement : 'if' 'y' statement ('else' statement)?? {
<writeln("$text")>
};
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> channel(HIDDEN);
