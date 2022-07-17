// @ts-ignore
import { InputStream } from './InputStream';

declare class FileStream implements InputStream {
    constructor(fileName: string, decodeToUnicodeCodePoints: boolean);
}
