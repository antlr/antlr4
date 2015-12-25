// Generated from Arithmetic.g4 by ANTLR 4.5.1
// jshint ignore: start
var antlr4 = require('./antlr4/index');

// This class defines a complete listener for a parse tree produced by ArithmeticParser.
function ArithmeticListener() {
	antlr4.tree.ParseTreeListener.call(this);
	return this;
}

ArithmeticListener.prototype = Object.create(antlr4.tree.ParseTreeListener.prototype);
ArithmeticListener.prototype.constructor = ArithmeticListener;

// Enter a parse tree produced by ArithmeticParser#equation.
ArithmeticListener.prototype.enterEquation = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#equation.
ArithmeticListener.prototype.exitEquation = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#expression.
ArithmeticListener.prototype.enterExpression = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#expression.
ArithmeticListener.prototype.exitExpression = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#multiplyingExpression.
ArithmeticListener.prototype.enterMultiplyingExpression = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#multiplyingExpression.
ArithmeticListener.prototype.exitMultiplyingExpression = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#powExpression.
ArithmeticListener.prototype.enterPowExpression = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#powExpression.
ArithmeticListener.prototype.exitPowExpression = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#atom.
ArithmeticListener.prototype.enterAtom = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#atom.
ArithmeticListener.prototype.exitAtom = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#scientific.
ArithmeticListener.prototype.enterScientific = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#scientific.
ArithmeticListener.prototype.exitScientific = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#relop.
ArithmeticListener.prototype.enterRelop = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#relop.
ArithmeticListener.prototype.exitRelop = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#number.
ArithmeticListener.prototype.enterNumber = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#number.
ArithmeticListener.prototype.exitNumber = function(ctx) {
};


// Enter a parse tree produced by ArithmeticParser#variable.
ArithmeticListener.prototype.enterVariable = function(ctx) {
};

// Exit a parse tree produced by ArithmeticParser#variable.
ArithmeticListener.prototype.exitVariable = function(ctx) {
};



exports.ArithmeticListener = ArithmeticListener;