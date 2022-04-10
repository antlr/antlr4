/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
/*! https://mths.be/fromcodepoint v0.2.1 by @mathias */
if (!String.fromCodePoint) {
	(function() {
		const defineProperty = (function() {
			// IE 8 only supports `Object.defineProperty` on DOM elements
			let result;
			try {
				const object = {};
				const $defineProperty = Object.defineProperty;
				result = $defineProperty(object, object, object) && $defineProperty;
			} catch(error) {
				/* eslint no-empty: [ "off" ] */
			}
			return result;
		}());
		const stringFromCharCode = String.fromCharCode;
		const floor = Math.floor;
		const fromCodePoint = function(_) {
			const MAX_SIZE = 0x4000;
			const codeUnits = [];
			let highSurrogate;
			let lowSurrogate;
			let index = -1;
			const length = arguments.length;
			if (!length) {
				return '';
			}
			let result = '';
			while (++index < length) {
				let codePoint = Number(arguments[index]);
				if (
					!isFinite(codePoint) || // `NaN`, `+Infinity`, or `-Infinity`
					codePoint < 0 || // not a valid Unicode code point
					codePoint > 0x10FFFF || // not a valid Unicode code point
					floor(codePoint) !== codePoint // not an integer
				) {
					throw RangeError('Invalid code point: ' + codePoint);
				}
				if (codePoint <= 0xFFFF) { // BMP code point
					codeUnits.push(codePoint);
				} else { // Astral code point; split in surrogate halves
					// https://mathiasbynens.be/notes/javascript-encoding#surrogate-formulae
					codePoint -= 0x10000;
					highSurrogate = (codePoint >> 10) + 0xD800;
					lowSurrogate = (codePoint % 0x400) + 0xDC00;
					codeUnits.push(highSurrogate, lowSurrogate);
				}
				if (index + 1 === length || codeUnits.length > MAX_SIZE) {
					result += stringFromCharCode.apply(null, codeUnits);
					codeUnits.length = 0;
				}
			}
			return result;
		};
		if (defineProperty) {
			defineProperty(String, 'fromCodePoint', {
				'value': fromCodePoint,
				'configurable': true,
				'writable': true
			});
		} else {
			String.fromCodePoint = fromCodePoint;
		}
	}());
}
