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

KeyPrinter = function() {
     ArithmeticListener.call(this); // inherit default listener
     return this;
};

// inherit default listener
KeyPrinter.prototype = Object.create(ArithmeticListener.prototype);
KeyPrinter.prototype.constructor = KeyPrinter;

// override default listener behavior
KeyPrinter.prototype.enterAtom = function(ctx) {
    console.log("Oh, a atom!");
};

KeyPrinter.prototype.enterExpression = function(ctx) {
    console.log("Oh, an expression!");
};

var tree = p.equation();



var printer = new KeyPrinter();
antlr4.tree.ParseTreeWalker.DEFAULT.walk(printer, tree);

console.log( tree.children[0].children[0].children[0].children[0].children[0].children[0].children[0].symbol.column );
