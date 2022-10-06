import CharStream from './CharStream';

export default class FileStream extends CharStream {

    index: number; // defined as property

    constructor(fileName: string);
    constructor(fileName: string, decodeToUnicodeCodePoints: boolean);

}
