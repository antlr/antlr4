/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "./InputStream"

export declare class FileStream extends InputStream {
    fileName: string

    constructor(fileName: string, decodeToUnicodeCodePoints?: boolean | null)
}
