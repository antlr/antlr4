/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

const {Token} = require('./Token');

/* stop is not included! */
class Interval {
	constructor(start, stop) {
		this.start = start;
		this.stop = stop;
	}

	contains(item) {
		return item >= this.start && item < this.stop;
	}

	toString() {
		if(this.start===this.stop-1) {
			return this.start.toString();
		} else {
			return this.start.toString() + ".." + (this.stop-1).toString();
		}
	}

	get length(){
		return this.stop - this.start;
	}
}


class IntervalSet {
	constructor() {
		this.intervals = null;
		this.readOnly = false;
	}

	first(v) {
		if (this.intervals === null || this.intervals.length===0) {
			return Token.INVALID_TYPE;
		} else {
			return this.intervals[0].start;
		}
	}

	addOne(v) {
		this.addInterval(new Interval(v, v + 1));
	}

	addRange(l, h) {
		this.addInterval(new Interval(l, h + 1));
	}

	addInterval(v) {
		if (this.intervals === null) {
			this.intervals = [];
			this.intervals.push(v);
		} else {
			// find insert pos
			for (let k = 0; k < this.intervals.length; k++) {
				const i = this.intervals[k];
				// distinct range -> insert
				if (v.stop < i.start) {
					this.intervals.splice(k, 0, v);
					return;
				}
				// contiguous range -> adjust
				else if (v.stop === i.start) {
					this.intervals[k].start = v.start;
					return;
				}
				// overlapping range -> adjust and reduce
				else if (v.start <= i.stop) {
					this.intervals[k] = new Interval(Math.min(i.start, v.start), Math.max(i.stop, v.stop));
					this.reduce(k);
					return;
				}
			}
			// greater than any existing
			this.intervals.push(v);
		}
	}

	addSet(other) {
		if (other.intervals !== null) {
			for (let k = 0; k < other.intervals.length; k++) {
				const i = other.intervals[k];
				this.addInterval(new Interval(i.start, i.stop));
			}
		}
		return this;
	}

	reduce(k) {
		// only need to reduce if k is not the last
		if (k < this.intervalslength - 1) {
			const l = this.intervals[k];
			const r = this.intervals[k + 1];
			// if r contained in l
			if (l.stop >= r.stop) {
				this.intervals.pop(k + 1);
				this.reduce(k);
			} else if (l.stop >= r.start) {
				this.intervals[k] = new Interval(l.start, r.stop);
				this.intervals.pop(k + 1);
			}
		}
	}

	complement(start, stop) {
		const result = new IntervalSet();
		result.addInterval(new Interval(start,stop+1));
		for(let i=0; i<this.intervals.length; i++) {
			result.removeRange(this.intervals[i]);
		}
		return result;
	}

	contains(item) {
		if (this.intervals === null) {
			return false;
		} else {
			for (let k = 0; k < this.intervals.length; k++) {
				if(this.intervals[k].contains(item)) {
					return true;
				}
			}
			return false;
		}
	}

	removeRange(v) {
		if(v.start===v.stop-1) {
			this.removeOne(v.start);
		} else if (this.intervals!==null) {
			let k = 0;
			for(let n=0; n<this.intervals.length; n++) {
				const i = this.intervals[k];
				// intervals are ordered
				if (v.stop<=i.start) {
					return;
				}
				// check for including range, split it
				else if(v.start>i.start && v.stop<i.stop) {
					this.intervals[k] = new Interval(i.start, v.start);
					const x = new Interval(v.stop, i.stop);
					this.intervals.splice(k, 0, x);
					return;
				}
				// check for included range, remove it
				else if(v.start<=i.start && v.stop>=i.stop) {
					this.intervals.splice(k, 1);
					k = k - 1; // need another pass
				}
				// check for lower boundary
				else if(v.start<i.stop) {
					this.intervals[k] = new Interval(i.start, v.start);
				}
				// check for upper boundary
				else if(v.stop<i.stop) {
					this.intervals[k] = new Interval(v.stop, i.stop);
				}
				k += 1;
			}
		}
	}

	removeOne(v) {
		if (this.intervals !== null) {
			for (let k = 0; k < this.intervals.length; k++) {
				const i = this.intervals[k];
				// intervals is ordered
				if (v < i.start) {
					return;
				}
				// check for single value range
				else if (v === i.start && v === i.stop - 1) {
					this.intervals.splice(k, 1);
					return;
				}
				// check for lower boundary
				else if (v === i.start) {
					this.intervals[k] = new Interval(i.start + 1, i.stop);
					return;
				}
				// check for upper boundary
				else if (v === i.stop - 1) {
					this.intervals[k] = new Interval(i.start, i.stop - 1);
					return;
				}
				// split existing range
				else if (v < i.stop - 1) {
					const x = new Interval(i.start, v);
					i.start = v + 1;
					this.intervals.splice(k, 0, x);
					return;
				}
			}
		}
	}

	toString(literalNames, symbolicNames, elemsAreChar) {
		literalNames = literalNames || null;
		symbolicNames = symbolicNames || null;
		elemsAreChar = elemsAreChar || false;
		if (this.intervals === null) {
			return "{}";
		} else if(literalNames!==null || symbolicNames!==null) {
			return this.toTokenString(literalNames, symbolicNames);
		} else if(elemsAreChar) {
			return this.toCharString();
		} else {
			return this.toIndexString();
		}
	}

	toCharString() {
		const names = [];
		for (let i = 0; i < this.intervals.length; i++) {
			const v = this.intervals[i];
			if(v.stop===v.start+1) {
				if ( v.start===Token.EOF ) {
					names.push("<EOF>");
				} else {
					names.push("'" + String.fromCharCode(v.start) + "'");
				}
			} else {
				names.push("'" + String.fromCharCode(v.start) + "'..'" + String.fromCharCode(v.stop-1) + "'");
			}
		}
		if (names.length > 1) {
			return "{" + names.join(", ") + "}";
		} else {
			return names[0];
		}
	}

	toIndexString() {
		const names = [];
		for (let i = 0; i < this.intervals.length; i++) {
			const v = this.intervals[i];
			if(v.stop===v.start+1) {
				if ( v.start===Token.EOF ) {
					names.push("<EOF>");
				} else {
					names.push(v.start.toString());
				}
			} else {
				names.push(v.start.toString() + ".." + (v.stop-1).toString());
			}
		}
		if (names.length > 1) {
			return "{" + names.join(", ") + "}";
		} else {
			return names[0];
		}
	}

	toTokenString(literalNames, symbolicNames) {
		const names = [];
		for (let i = 0; i < this.intervals.length; i++) {
			const v = this.intervals[i];
			for (let j = v.start; j < v.stop; j++) {
				names.push(this.elementName(literalNames, symbolicNames, j));
			}
		}
		if (names.length > 1) {
			return "{" + names.join(", ") + "}";
		} else {
			return names[0];
		}
	}

	elementName(literalNames, symbolicNames, a) {
		if (a === Token.EOF) {
			return "<EOF>";
		} else if (a === Token.EPSILON) {
			return "<EPSILON>";
		} else {
			return literalNames[a] || symbolicNames[a];
		}
	}

	get length(){
		let len = 0;
		this.intervals.map(function(i) {len += i.length;});
		return len;
	}
}

module.exports = {
	Interval,
	IntervalSet
};
