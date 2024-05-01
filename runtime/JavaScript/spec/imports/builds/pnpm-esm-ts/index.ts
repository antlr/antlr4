import { CharStream, ErrorListener } from "antlr4";

const cs = new CharStream("OK");

class MyErrorListener<T> extends ErrorListener<T> {

}

console.log(cs.toString());