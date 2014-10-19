grammar <grammarName>;
@members {this.i=0}
s : ({<AddMember("i","1")>\n<PlusMember("\"i=\"","i"):writeln()>} a)+ ;
a : {<ModMemberEquals("i","2","0")>}? ID {<writeln("\"alt 1\"")>}
  | {<ModMemberNotEquals("i","2","0")>}? ID {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
