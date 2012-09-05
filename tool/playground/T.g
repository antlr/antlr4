grammar T;

options
{
 output=AST;
 backtrack=true;
}

Integer :  '0' .. '9';
    
 
myID : Integer*;
  
public json : myID+   -> ^(myID); 