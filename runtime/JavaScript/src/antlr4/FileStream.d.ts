// @ts-ignore
import { CharStream } from './CharStream';

export default class FileStream implements CharStream {

    index: number; // defined as property

    constructor(fileName: string, decodeToUnicodeCodePoints: boolean);
    seek(index: number) : void;
    consume() : void;
}
