// Generated from JSON.g4 by ANTLR 4.5.1
// jshint ignore: start
var antlr4 = require('./antlr4/index');

// This class defines a complete listener for a parse tree produced by JSONParser.
function JSONListener() {
	antlr4.tree.ParseTreeListener.call(this);
	return this;
}

JSONListener.prototype = Object.create(antlr4.tree.ParseTreeListener.prototype);
JSONListener.prototype.constructor = JSONListener;

// Enter a parse tree produced by JSONParser#json.
JSONListener.prototype.enterJson = function(ctx) {
};

// Exit a parse tree produced by JSONParser#json.
JSONListener.prototype.exitJson = function(ctx) {
};


// Enter a parse tree produced by JSONParser#object.
JSONListener.prototype.enterObject = function(ctx) {
};

// Exit a parse tree produced by JSONParser#object.
JSONListener.prototype.exitObject = function(ctx) {
};


// Enter a parse tree produced by JSONParser#pair.
JSONListener.prototype.enterPair = function(ctx) {
};

// Exit a parse tree produced by JSONParser#pair.
JSONListener.prototype.exitPair = function(ctx) {
};


// Enter a parse tree produced by JSONParser#array.
JSONListener.prototype.enterArray = function(ctx) {
};

// Exit a parse tree produced by JSONParser#array.
JSONListener.prototype.exitArray = function(ctx) {
};


// Enter a parse tree produced by JSONParser#value.
JSONListener.prototype.enterValue = function(ctx) {
};

// Exit a parse tree produced by JSONParser#value.
JSONListener.prototype.exitValue = function(ctx) {
};



exports.JSONListener = JSONListener;