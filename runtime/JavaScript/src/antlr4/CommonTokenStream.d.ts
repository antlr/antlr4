import Lexer from './Lexer';

import BufferedTokenStream from './BufferedTokenStream';

export default class CommonTokenStream implements BufferedTokenStream {
    // properties
    tokens: string[];
    // methods
    constructor(lexer: Lexer);
    constructor(lexer: Lexer, channel: number);
    fill(): void;
}
