/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import CharStream from './CharStream.js';

/**
 * @deprecated Use CharStream instead
*/
export default class InputStream extends CharStream {
	constructor(data, decodeToUnicodeCodePoints) {
		super(data, decodeToUnicodeCodePoints);
	}
}
