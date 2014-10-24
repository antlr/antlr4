grammar <grammarName>;
@members {<InitBooleanMember("enumKeyword",true)>}
primary
    :   ID {<writeln("\"ID \"+$ID.text")>}
    |   {!<GetMember("enumKeyword")>}? 'enum' {<writeln("\"enum\"")>}
    ;
ID : [a-z]+ ;
WS : [ \t\n\r]+ -> skip ;
