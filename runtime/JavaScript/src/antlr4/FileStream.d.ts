import {CharStream} from "./CharStream.js";

export declare class FileStream extends CharStream {

    fileName: string;

    constructor(fileName: string, encoding?: string, decodeToUnicodeCodePoints?: boolean);

}
