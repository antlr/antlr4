grammar <grammarName>;
@parser::members{
<DeclareContextListGettersFunction()>
}
s : (a | b)+;
a : 'a' {<write("'a'")>};
b : 'b' {<write("'b'")>};
