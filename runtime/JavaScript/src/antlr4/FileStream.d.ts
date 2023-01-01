import {CharStream} from "./CharStream";

export declare class FileStream extends CharStream {

    fileName: string;

    constructor(fileName: string);
    constructor(fileName: string, decodeToUnicodeCodePoints: boolean);

}
