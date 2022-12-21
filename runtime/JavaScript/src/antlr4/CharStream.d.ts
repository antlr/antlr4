export declare class CharStream {

    index: number; // defined as property

    constructor(data: string);
    constructor(data: string, decodeToUnicodeCodePoints: boolean);
    seek(index: number): void;
    consume(): void;
    getText(start: number, stop: number): string;
    LA(offset: number): number;
}
