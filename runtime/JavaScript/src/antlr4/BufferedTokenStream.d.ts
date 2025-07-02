import { TokenStream } from './TokenStream';
import { TokenSource } from './TokenSource';
import { Token } from './Token';

export declare class BufferedTokenStream extends TokenStream {

    tokenSource: TokenSource;
    tokens: Token[];
    index: number;
    fetchedEof: boolean;

    constructor(source: TokenSource);
    setTokenSource(tokenSource: TokenSource): void;
    mark(): number;
    release(marker: number): void;
    reset(): void;
    seek(index: number): void;
    consume(): void;
    sync(i: number): boolean;
    fetch(n: number): number;
    LB(k: number): Token;
    nextTokenOnChannel(i: number, channel: number): number;
    previousTokenOnChannel(i: number, channel: number): number;

    protected lazyInit(): void;
    protected adjustSeekIndex(i: number): number;
}
