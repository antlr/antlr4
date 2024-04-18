const { CharStream } = require("antlr4");

const cs = new CharStream("OK");

console.log(cs.toString());