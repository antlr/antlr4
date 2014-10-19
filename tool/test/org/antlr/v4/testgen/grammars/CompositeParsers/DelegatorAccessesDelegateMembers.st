grammar M; // uses no rules from the import
import S;
s : 'b'{<Invoke_foo()>}; // gS is import pointer
WS : (' '|'\n') -> skip ;
