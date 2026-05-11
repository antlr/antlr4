grammar Labels;
s : q=e
 | '{' blk '}'  
 ;
e returns [String v]
  : a=e op='*' b=e  {$v = "* ".to_owned() + $a.v + " " + $b.v;}  # mult
  | a=e '+' b=e     {$v = "+ ".to_owned() + $a.v + " " + $b.v;}     # add
  | INT             {$v = $INT.text.to_owned();}        # anInt
  | '(' x=e ')'     {$v = $x.v;}            # parens
  | x=e '++'        {$v = " ++".to_owned() + $x.v;}          # inc
  | x=e '--'        {$v = " --".to_owned() + $x.v;}              # dec
  | ID              {$v = $ID.text.to_owned();}               # anID
  ;
// Mutually recursive:
blk : s ;

ID : 'a'..'z'+ ;
INT : '0'..'9'+ ;
WS : (' '|'\n') -> skip ;