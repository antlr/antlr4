grammar T;
@members {
public static class LeafListener extends TBaseListener {
    public void exitCall(TParser.CallContext ctx) {
        System.out.printf("%s %s",ctx.e().start.getText(),
                          ctx.eList());
    }
  }    public void exitInt(TParser.IntContext ctx) {
      System.out.println(ctx.INT().getText());
    }
}
s
@init {setBuildParseTree(true);}
@after {  System.out.println($r.ctx.toStringTree(this));  ParseTreeWalker walker = new ParseTreeWalker();
  walker.walk(new LeafListener(), $r.ctx);}
  : r=e ;
e : e '(' eList ')' -> Call
  | INT             -> Int
  ;     
eList : e (',' e)* ;
MULT: '*' ;
ADD : '+' ;
INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
