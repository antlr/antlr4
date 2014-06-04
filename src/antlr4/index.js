exports.atn = require('./atn');
exports.dfa = require('./dfa');
exports.FileStream = require('./FileStream').FileStream;
exports.CommonTokenStream = require('./CommonTokenStream').CommonTokenStream;
exports.Lexer = require('./Lexer').Lexer;
var pc = require('./PredictionContext');
exports.PredictionContextCache = pc.PredictionContextCache;