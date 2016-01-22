PORT_DEBUG = false

var antlr4 = require("./antlr4/index"),
    tree = antlr4.tree
    JSONLexer = require("./JSONLexer").JSONLexer,
    JSONParser = require("./JSONParser").JSONParser,
    JSONListener = require("./JSONListener").JSONListener;

var a = new antlr4.FileStream("foo.txt");
var l = new JSONLexer(a);
var s = new antlr4.CommonTokenStream(l, 0);
var p = new JSONParser(s);
p.buildParseTrees = true;

KeyPrinter = function() {
     JSONListener.call(this); // inherit default listener
     return this;
};

// inherit default listener
KeyPrinter.prototype = Object.create(JSONListener.prototype);
KeyPrinter.prototype.constructor = KeyPrinter;

// override default listener behavior
KeyPrinter.prototype.enterValue = function(ctx) {
    console.log(ctx.start.text);
};

var tree = p.json();

var printer = new KeyPrinter();
antlr4.tree.ParseTreeWalker.DEFAULT.walk(printer, tree);
