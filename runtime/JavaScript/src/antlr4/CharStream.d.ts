export declare class CharStream {

    index: number; // defined as property
    size: number;// defined as property

    constructor(data: string);
    constructor(data: string, decodeToUnicodeCodePoints: boolean);
    reset(): void;
    consume(): void;
    LA(offset: number): number;
    LT(offset: number): number;
    mark(): number;
    release(marker: number): void;
    seek(index: number): void;
    getText(start: number, stop: number): string;
    toString(): string;
}
