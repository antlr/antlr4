/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import InputStream from './InputStream.js';
import CharStream from './CharStream.js';
const isNode =
	typeof process !== "undefined" &&
	process.versions != null &&
	process.versions.node != null;
import fs from 'fs';

/**
 * This is an InputStream that is loaded from a file all at once
 * when you construct the object.
 */
export default class FileStream extends InputStream {

	static fromPath(path, encoding, callback) {
		if(!isNode)
			throw new Error("FileStream is only available when running in Node!");
		fs.readFile(path, encoding, function(err, data) {
			let is = null;
			if (data !== null) {
				is = new CharStream(data, true);
			}
			callback(err, is);
		});

	}

	constructor(fileName, encoding, decodeToUnicodeCodePoints) {
		if(!isNode)
			throw new Error("FileStream is only available when running in Node!");
		const data = fs.readFileSync(fileName, encoding || "utf-8" );
		super(data, decodeToUnicodeCodePoints);
		this.fileName = fileName;
	}
}
