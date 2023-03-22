import {CharStream} from "./CharStream";

export declare class CharStreams {
    static fromString(data: string, decodeToUnicodeCodePoints?: boolean): CharStream;
    static fromBuffer(buffer: Buffer, encoding?: string): CharStream;
    static fromBlob(blob: Blob, encoding: string, onLoad: (stream: CharStream) => void, onError: (error: Error) => void): void;
    static fromPath(path: string, encoding: string, callback: (err: Error, stream: CharStream) => void): void;
    static fromPathSync(path: string, encoding: string): CharStream;
}
