import {SyntaxTree} from "./SyntaxTree.js";
import {ParseTreeVisitor} from "./ParseTreeVisitor";

export declare abstract class ParseTree extends SyntaxTree {
    getText(): string;
    abstract accept<T>(visitor: ParseTreeVisitor<T>): T;
}
