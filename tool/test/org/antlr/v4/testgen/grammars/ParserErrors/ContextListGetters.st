grammar <grammarName>;
@parser::members{
<declareContextListGettersFunction()>
}
s : (a | b)+;
a : 'a' {<write("'a'")>};
b : 'b' {<write("'b'")>};
;
