grammar CSV;

@tokenfactory{
pub type LocalTokenFactory<'input> = dbt_antlr4::token_factory::ArenaCommonFactory<'input>;
}

csvFile: hdr row+ ;
hdr : row ;

row : field (',' field)* '\r'? '\n';

field
    : TEXT
    | STRING 
    |
    ;

WS     : [ ]+ -> channel(HIDDEN);
TEXT   : ~[ ,\n\r"]+ ;
STRING : '"' ('""'|~'"')* '"' ; // quote-quote is an escaped quote
