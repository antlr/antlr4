import {CharStream} from "./CharStream";

export declare class CharStreams {
    static fromString(data: string, decodeToUnicodeCodePoints?: boolean): CharStream;
    static fromBuffer(buffer: Buffer, encoding?: string): CharStream;
    static fromBlob(blob: Blob, encoding: string, onLoad: (stream: CharStream) => void, onError: (error: Error) => void): void;
}
