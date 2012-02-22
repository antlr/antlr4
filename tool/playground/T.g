grammar T;
@members {
public static class LeafListener extends TBaseListener {
    public void exitA(TParser.EContext ctx) {
/*
      if (ctx.getChildCount()==3) {
        System.out.printf("%s %s %s",ctx.e(0).start.getText(),
                          ctx.e(1).start.getText(),ctx.e().get(0).start.getText());
      }
      else System.out.println(ctx.INT(0).start.getText());
*/
    }
  }}
s
@init {setBuildParseTree(true);}
@after {  System.out.println($r.ctx.toStringTree(this));  ParseTreeWalker walker = new ParseTreeWalker();
  walker.walk(new LeafListener(), $r.ctx);}
  : r=e ;
e : e op='*' e
  | e op='+' e
  | e '++'
  | INT
  ;
MULT: '*' ;
ADD : '+' ;
INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
