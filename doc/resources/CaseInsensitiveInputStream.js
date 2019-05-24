//
/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
//

function CaseInsensitiveInputStream(stream, upper) {
    this._stream = stream;
    this._case = upper ? String.prototype.toUpperCase : String.prototype.toLowerCase;
}

Object.defineProperty(CaseInsensitiveInputStream.prototype, 'index', {
    get: function() {
        return this._stream.index;
    }
});

Object.defineProperty(CaseInsensitiveInputStream.prototype, 'size', {
    get: function() {
        return this._stream.size;
    }
});

CaseInsensitiveInputStream.prototype.LA = function (offset) {
    const cp = this._stream.LA(offset);
    if (cp <= 0) {
        return cp;
    }
    const c = this._case.call(String.fromCodePoint(cp));
    return c.codePointAt(0);
};

CaseInsensitiveInputStream.prototype.reset = function() {
    this._stream.reset();
};

CaseInsensitiveInputStream.prototype.consume = function() {
    this._stream.consume();
};

CaseInsensitiveInputStream.prototype.LT = function(offset) {
    return this._stream.LT(offset);
};

CaseInsensitiveInputStream.prototype.mark = function() {
    return this._stream.mark();
};

CaseInsensitiveInputStream.prototype.release = function(marker) {
    this._stream.release(marker);
};

CaseInsensitiveInputStream.prototype.seek = function(index) {
    this._stream.seek(index);
};

CaseInsensitiveInputStream.prototype.getText = function(start, stop) {
    return this._stream.getText(start, stop);
};

CaseInsensitiveInputStream.prototype.toString = function() {
    return this._stream.toString();
};

export default CaseInsensitiveInputStream;
