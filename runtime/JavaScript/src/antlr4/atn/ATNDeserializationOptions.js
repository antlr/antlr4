/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

export default class ATNDeserializationOptions {
	constructor(copyFrom) {
		if(copyFrom===undefined) {
			copyFrom = null;
		}
		this.readOnly = false;
		this.verifyATN = copyFrom===null ? true : copyFrom.verifyATN;
		this.generateRuleBypassTransitions = copyFrom===null ? false : copyFrom.generateRuleBypassTransitions;
	}
}

ATNDeserializationOptions.defaultOptions = new ATNDeserializationOptions();
ATNDeserializationOptions.defaultOptions.readOnly = true;

