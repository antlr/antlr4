import { ParseTreeVisitor } from "./ParseTreeVisitor.js";
import { TerminalNode } from "./TerminalNode.js";

export declare class ErrorNode extends TerminalNode {
    accept<T>(visitor: ParseTreeVisitor<T>): T;
}
