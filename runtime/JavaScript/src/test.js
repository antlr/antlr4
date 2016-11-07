var antlr4 = require('./antlr4/index');
var DesignScriptLexer = require('./DesignScriptLexer');
var DesignScriptParser = require('./DesignScriptParser');
var DesignScriptListener = require('./DesignScriptListener').DesignScriptListener;
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
	PORT_DEBUG =true 
    var input = new antlr4.FileStream(argv[2]);
    var lexer = new DesignScriptLexer.DesignScriptLexer(input);
    var stream = new antlr4.CommonTokenStream(lexer);
	var parser = new DesignScriptParser.DesignScriptParser(stream);
    parser.buildParseTrees = true;
	printer = function() {
		this.println = function(s) { console.log(s); }
		this.print = function(s) { process.stdout.write(s); }
		return this;
	};
    parser.printer = new printer();
    var tree = parser.program();
    antlr4.tree.ParseTreeWalker.DEFAULT.walk(new TreeShapeListener(), tree);
}

main(process.argv);
