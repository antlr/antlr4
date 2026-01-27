grammar ReferenceToATN;

@tokenfactory{
pub type LocalTokenFactory<'input> = dbt_antlr4::token_factory::OwningTokenFactory; // need single quote here '
}

a : (ID|ATN)* ATN? {println!("{}",$text);};
ID : 'a'..'z'+ ;
ATN : '0'..'9'+;
WS : (' '|'\n') -> skip ;
