grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : expr EOF;
expr:
    a=expr '*' a=expr #Factor
    | b+=expr (',' b+=expr)* '>>' c=expr #Send
    | ID #JustId //semantic check on modifiers
;

ID  : ('a'..'z'|'A'..'Z'|'_')
      ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
;

WS : [ \t\n]+ -> skip ;
