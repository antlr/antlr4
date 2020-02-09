//
/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
//

//
//  This is an InputStream that is loaded from a file all at once
//  when you construct the object.
//
const {InputStream} = require('./InputStream');
const fs = require("fs");

function FileStream(fileName, decodeToUnicodeCodePoints) {
	const data = fs.readFileSync(fileName, "utf8");
	InputStream.call(this, data, decodeToUnicodeCodePoints);
	this.fileName = fileName;
	return this;
}

FileStream.prototype = Object.create(InputStream.prototype);
FileStream.prototype.constructor = FileStream;

module.exports = {FileStream}
