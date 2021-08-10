import 'dart:html';
import 'package:antlr4/antlr4.dart';
import 'package:web_simple/hello.dart';

var output = '';

class TreeShapeListener implements ParseTreeListener {
  @override
  void enterEveryRule(ParserRuleContext ctx) {
    output = '$output${ctx.text}';
    querySelector('#output')?.text = output;
  }

  @override
  void exitEveryRule(ParserRuleContext node) {}

  @override
  void visitErrorNode(ErrorNode node) {}

  @override
  void visitTerminal(TerminalNode node) {}
}

void main() async {
  HelloLexer.checkVersion();
  HelloParser.checkVersion();

  final lexer = HelloLexer(InputStream.fromString('hello world'));
  final tokens = CommonTokenStream(lexer);
  final parser = HelloParser(tokens);

  parser.addErrorListener(DiagnosticErrorListener());
  parser.buildParseTree = true;
  final tree = parser.r();
  ParseTreeWalker.DEFAULT.walk(TreeShapeListener(), tree);
}
