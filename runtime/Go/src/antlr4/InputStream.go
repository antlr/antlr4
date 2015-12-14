package antlr

var Token = require('./Token').Token;

// Vacuum all input from a string and then treat it like a buffer.

function _loadString(stream) {
	stream._index = 0;
	stream.data = [];
	for (var i = 0; i < stream.strdata.length; i++) {
		stream.data.push(stream.strdata.charCodeAt(i));
	}
	stream._size = stream.data.length;
}

function InputStream(data) {
	this.name = "<empty>";
	this.strdata = data;
	_loadString(this);
	return this;
}

Object.defineProperty(InputStream.prototype, "index", {
	get : function() {
		return this._index;
	}
});

Object.defineProperty(InputStream.prototype, "size", {
	get : function() {
		return this._size;
	}
});

// Reset the stream so that it's in the same state it was
// when the object was created *except* the data array is not
// touched.
//
func (this *InputStream) reset() {
	this._index = 0;
}

func (this *InputStream) consume() {
	if (this._index >= this._size) {
		// assert this.LA(1) == Token.EOF
		throw ("cannot consume EOF");
	}
	this._index += 1;
}

func (this *InputStream) LA(offset) {
	if (offset == 0) {
		return 0; // undefined
	}
	if (offset < 0) {
		offset += 1; // e.g., translate LA(-1) to use offset=0
	}
	var pos = this._index + offset - 1;
	if (pos < 0 || pos >= this._size) { // invalid
		return Token.EOF;
	}
	return this.data[pos];
}

func (this *InputStream) LT(offset) {
	return this.LA(offset);
}

// mark/release do nothing; we have entire buffer
func (this *InputStream) mark() {
	return -1;
}

func (this *InputStream) release(marker) {
}

// consume() ahead until p==_index; can't just set p=_index as we must
// update line and column. If we seek backwards, just set p
//
func (this *InputStream) seek(_index) {
	if (_index <= this._index) {
		this._index = _index; // just jump; don't update stream state (line,
								// ...)
		return;
	}
	// seek forward
	this._index = Math.min(_index, this._size);
}

func (this *InputStream) getText(start, stop) {
	if (stop >= this._size) {
		stop = this._size - 1;
	}
	if (start >= this._size) {
		return "";
	} else {
		return this.strdata.slice(start, stop + 1);
	}
}

func (this *InputStream) toString() {
	return this.strdata;
}


