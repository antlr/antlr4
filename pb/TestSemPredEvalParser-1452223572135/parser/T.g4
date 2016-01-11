grammar T;
s : {p.Interpreter.SetPredictionMode(antlr4.PredictionModeLLExactAmbigDetection);} a ';' a; // do 2x: once in ATN, next in DFA
a : ID {fmt.Println("alt 1")}
  | ID {fmt.Println("alt 2")}
  | {false}? ID {fmt.Println("alt 3")}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;