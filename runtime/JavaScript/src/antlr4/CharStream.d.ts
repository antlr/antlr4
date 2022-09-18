export default class CharStream {

    index: number; // defined as property

    constructor(data: string);
    constructor(data: string, decodeToUnicodeCodePoints: boolean);
    seek(index: number) : void;
    consume() : void;
}
