// import Lexer from './Lexer';

// @ts-ignore
import { BufferedTokenStream } from './BufferedTokenStream';

declare class CommonTokenStream implements BufferedTokenStream {
    constructor(lexer: any, channel: number);
}
