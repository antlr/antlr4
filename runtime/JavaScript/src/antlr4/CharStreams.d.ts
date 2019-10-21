/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "./InputStream"
import { readFileSync } from "fs"

export namespace CharStreams {
    export function fromString(str: string): InputStream
    export function fromBlob(
        blob: Blob,
        encoding: string,
        onLoad: (inputStream: InputStream) => void,
        onError: (ev: ProgressEvent<FileReader>) => any
    ): void
    export function fromBuffer(buffer: Buffer, encoding: string): InputStream
    export function fromPath(
        path: string | URL,
        encoding: string,
        callback: (error: Error, inputStream: InputStream | null) => void
    ): void
    export function fromPathSync(path: string | URL, encoding: string): InputStream
}
