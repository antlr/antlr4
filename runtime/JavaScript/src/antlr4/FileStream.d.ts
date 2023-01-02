import CharStream from "./CharStream";

declare class FileStream extends CharStream {

    fileName: string;

    constructor(fileName: string);
    constructor(fileName: string, decodeToUnicodeCodePoints: boolean);

}

export default FileStream;
