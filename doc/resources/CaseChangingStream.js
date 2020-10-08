//
/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
//

function CaseChangingStream(stream, upper) {
	this._stream = stream;
	this._upper = upper;
}

CaseChangingStream.prototype.LA = function(offset) {
	var c = this._stream.LA(offset);
	if (c <= 0) {
		return c;
	}
	return String.fromCodePoint(c)[this._upper ? "toUpperCase" : "toLowerCase"]().codePointAt(0);
};

CaseChangingStream.prototype.reset = function() {
	return this._stream.reset();
};

CaseChangingStream.prototype.consume = function() {
	return this._stream.consume();
};

CaseChangingStream.prototype.LT = function(offset) {
	return this._stream.LT(offset);
};

CaseChangingStream.prototype.mark = function() {
	return this._stream.mark();
};

CaseChangingStream.prototype.release = function(marker) {
	return this._stream.release(marker);
};

CaseChangingStream.prototype.seek = function(_index) {
	return this._stream.seek(_index);
};

CaseChangingStream.prototype.getText = function(start, stop) {
	return this._stream.getText(start, stop);
};

CaseChangingStream.prototype.toString = function() {
	return this._stream.toString();
};

Object.defineProperty(CaseChangingStream.prototype, "index", {
	get: function() {
		return this._stream.index;
	}
});

Object.defineProperty(CaseChangingStream.prototype, "size", {
	get: function() {
		return this._stream.size;
	}
});

exports.CaseChangingStream = CaseChangingStream;
