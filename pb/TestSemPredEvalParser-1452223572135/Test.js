var antlr4 = require('antlr4/index');
var TLexer = require('./parser/TLexer');
var TParser = require('./parser/TParser');
var TListener = require('./parser/TListener').TListener;
// var TVisitor = require('./parser/TVisitor').TVisitor;

function TreeShapeListener() {
	antlr4.tree.ParseTreeListener.call(this);
	return this;
}

TreeShapeListener.prototype = Object.create(antlr4.tree.ParseTreeListener.prototype);
TreeShapeListener.prototype.constructor = TreeShapeListener;

TreeShapeListener.prototype.enterEveryRule = function(ctx) {
	for(var i=0;i<ctx.getChildCount; i++) {
		var child = ctx.getChild(i);
       var parent = child.parentCtx;
       if(parent.getRuleContext() !== ctx || !(parent instanceof antlr4.tree.RuleNode)) {
           throw "Invalid parse tree shape detected.";
		}
	}
};

function main(argv) {
    var input = new antlr4.FileStream(argv[2]);
    var lexer = new TLexer.TLexer(input);
    var stream = new antlr4.CommonTokenStream(lexer);
	var parser = new TParser.TParser(stream);
    parser.buildParseTrees = true;
    parser.addErrorListener(new antlr4.error.DiagnosticErrorListener());
	printer = function() {
		this.println = function(s) { console.log(s); }
		this.print = function(s) { console.log("HI"); process.stdout.write(s); }
		return this;
	};
    parser.printer = new printer();
    var tree = parser.s();
    antlr4.tree.ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);
}

main(process.argv);
