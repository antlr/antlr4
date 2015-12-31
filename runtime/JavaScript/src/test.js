PORT_DEBUG = false

var antlr4 = require("./antlr4/index"),
    tree = antlr4.tree
    ArithmeticLexer = require("./ArithmeticLexer").ArithmeticLexer,
    ArithmeticParser = require("./ArithmeticParser").ArithmeticParser,
    ArithmeticListener = require("./ArithmeticListener").ArithmeticListener;

var a = new antlr4.FileStream("foo.txt");
var l = new ArithmeticLexer(a);
var s = new antlr4.CommonTokenStream(l, 0);
var p = new ArithmeticParser(s);
p.buildParseTrees = true;

//KeyPrinter = function() {
//     ArithmeticListener.call(this); // inherit default listener
//     return this;
//};
//
//// inherit default listener
//KeyPrinter.prototype = Object.create(ArithmeticListener.prototype);
//KeyPrinter.prototype.constructor = KeyPrinter;
//
//// override default listener behavior
//KeyPrinter.prototype.exitAtom = function(ctx) {
//
//    console.log("Oh, a atom!", ctx.start.source[1].strdata[ctx.start.start]);
//};
//
//KeyPrinter.prototype.exitExpression = function(ctx) {
//
//    console.log("Oh, an expression!", ctx);
//    throw new Error();
//};

var tree = p.equation();

//var printer = new KeyPrinter();
//antlr4.tree.ParseTreeWalker.DEFAULT.walk(printer, tree);

//console.log( tree.children[0].children[0].children[0].children );


