var antlr4 = require("./antlr4/index"),
    ArithmeticLexer = require("./ArithmeticLexer").ArithmeticLexer,
    ArithmeticParser = require("./ArithmeticParser").ArithmeticParser;


var a = new antlr4.FileStream("foo.txt");
var l = new ArithmeticLexer(a);
var s = new antlr4.CommonTokenStream(l, 0);
var p = new ArithmeticParser(s);

p.equation();