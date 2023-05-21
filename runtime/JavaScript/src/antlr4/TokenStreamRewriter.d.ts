import { CommonTokenStream } from "./CommonTokenStream";
import { Token } from "./Token";
import { Interval } from "./misc/Interval";

type Rewrites = Array<RewriteOperation | undefined>;
type Text = unknown;

export declare class TokenStreamRewriter {
    static DEFAULT_PROGRAM_NAME: string;
    constructor(tokens: CommonTokenStream);
    getTokenStream(): CommonTokenStream;
    insertAfter(token: Token, text: Text, programName?: string): void;
    insertAfter(index: number, text: Text, programName?: string): void;
    insertBefore(token: Token, text: Text, programName?: string): void;
    insertBefore(index: number, text: Text, programName?: string): void;
    replaceSingle(token: Token, text: Text, programName?: string): void;
    replaceSingle(index: number, text: Text, programName?: string): void;
    replace(from: Token | number, to: Token | number, text: Text, programName?: string): void;
    delete(from: number | Token, to: number | Token, programName?: string): void;
    getProgram(name: string): Rewrites;
    initializeProgram(name: string): Rewrites;
    getText(): string;
    getText(program: string): string;
    getText(interval: Interval, programName?: string): string;
    reduceToSingleOperationPerIndex(rewrites: Rewrites): Map<number, RewriteOperation>;
    catOpText(a: Text, b: Text): string;
    getKindOfOps(rewrites: Rewrites, kind: any, before: number): RewriteOperation[];
}


declare class RewriteOperation {
    constructor(tokens: CommonTokenStream, index: number, instructionIndex: number, text: Text);
    tokens: CommonTokenStream;
    instructionIndex: number;
    index: number;
    text: Text;
    toString(): string;
}
