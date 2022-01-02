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

	clone() {
		return new Interval(this.start, this.stop);
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

	addInterval(toAdd) {
		if (this.intervals === null) {
			this.intervals = [];
			this.intervals.push(toAdd.clone());
		} else {
			// find insert pos
			for (let pos = 0; pos < this.intervals.length; pos++) {
				const existing = this.intervals[pos];
				// distinct range -> insert
				if (toAdd.stop < existing.start) {
					this.intervals.splice(pos, 0, toAdd);
					return;
				}
				// contiguous range -> adjust
				else if (toAdd.stop === existing.start) {
					this.intervals[pos] = new Interval(toAdd.start, existing.stop)
					return;
				}
				// overlapping range -> adjust and reduce
				else if (toAdd.start <= existing.stop) {
					this.intervals[pos] = new Interval(Math.min(existing.start, toAdd.start), Math.max(existing.stop, toAdd.stop));
					this.reduce(pos);
					return;
				}
			}
			// greater than any existing
			this.intervals.push(toAdd.clone());
		}
	}

	addSet(other) {
		if (other.intervals !== null) {
			other.intervals.forEach( toAdd => this.addInterval(toAdd), this);
		}
		return this;
	}

	reduce(pos) {
		// only need to reduce if pos is not the last
		if (pos < this.intervals.length - 1) {
			const current = this.intervals[pos];
			const next = this.intervals[pos + 1];
			// if next contained in current
			if (current.stop >= next.stop) {
				this.intervals.splice(pos + 1, 1);
				this.reduce(pos);
			} else if (current.stop >= next.start) {
				this.intervals[pos] = new Interval(current.start, next.stop);
				this.intervals.splice(pos + 1, 1);
			}
		}
	}

	complement(start, stop) {
		const result = new IntervalSet();
		result.addInterval(new Interval(start, stop + 1));
		if(this.intervals !== null)
			this.intervals.forEach(toRemove => result.removeRange(toRemove));
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

	removeRange(toRemove) {
		if(toRemove.start===toRemove.stop-1) {
			this.removeOne(toRemove.start);
		} else if (this.intervals !== null) {
			let pos = 0;
			for(let n=0; n<this.intervals.length; n++) {
				const existing = this.intervals[pos];
				// intervals are ordered
				if (toRemove.stop<=existing.start) {
					return;
				}
				// check for including range, split it
				else if(toRemove.start>existing.start && toRemove.stop<existing.stop) {
					this.intervals[pos] = new Interval(existing.start, toRemove.start);
					const x = new Interval(toRemove.stop, existing.stop);
					this.intervals.splice(pos, 0, x);
					return;
				}
				// check for included range, remove it
				else if(toRemove.start<=existing.start && toRemove.stop>=existing.stop) {
					this.intervals.splice(pos, 1);
					pos = pos - 1; // need another pass
				}
				// check for lower boundary
				else if(toRemove.start<existing.stop) {
					this.intervals[pos] = new Interval(existing.start, toRemove.start);
				}
				// check for upper boundary
				else if(toRemove.stop<existing.stop) {
					this.intervals[pos] = new Interval(toRemove.stop, existing.stop);
				}
				pos += 1;
			}
		}
	}

	removeOne(value) {
		if (this.intervals !== null) {
			for (let i = 0; i < this.intervals.length; i++) {
				const existing = this.intervals[i];
				// intervals are ordered
				if (value < existing.start) {
					return;
				}
				// check for single value range
				else if (value === existing.start && value === existing.stop - 1) {
					this.intervals.splice(i, 1);
					return;
				}
				// check for lower boundary
				else if (value === existing.start) {
					this.intervals[i] = new Interval(existing.start + 1, existing.stop);
					return;
				}
				// check for upper boundary
				else if (value === existing.stop - 1) {
					this.intervals[i] = new Interval(existing.start, existing.stop - 1);
					return;
				}
				// split existing range
				else if (value < existing.stop - 1) {
					const replace = new Interval(existing.start, value);
					existing.start = value + 1;
					this.intervals.splice(i, 0, replace);
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
			const existing = this.intervals[i];
			if(existing.stop===existing.start+1) {
				if ( existing.start===Token.EOF ) {
					names.push("<EOF>");
				} else {
					names.push("'" + String.fromCharCode(existing.start) + "'");
				}
			} else {
				names.push("'" + String.fromCharCode(existing.start) + "'..'" + String.fromCharCode(existing.stop-1) + "'");
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
			const existing = this.intervals[i];
			if(existing.stop===existing.start+1) {
				if ( existing.start===Token.EOF ) {
					names.push("<EOF>");
				} else {
					names.push(existing.start.toString());
				}
			} else {
				names.push(existing.start.toString() + ".." + (existing.stop-1).toString());
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
			const existing = this.intervals[i];
			for (let j = existing.start; j < existing.stop; j++) {
				names.push(this.elementName(literalNames, symbolicNames, j));
			}
		}
		if (names.length > 1) {
			return "{" + names.join(", ") + "}";
		} else {
			return names[0];
		}
	}

	elementName(literalNames, symbolicNames, token) {
		if (token === Token.EOF) {
			return "<EOF>";
		} else if (token === Token.EPSILON) {
			return "<EPSILON>";
		} else {
			return literalNames[token] || symbolicNames[token];
		}
	}

	get length(){
		return this.intervals.map( interval => interval.length ).reduce((acc, val) => acc + val);
	}
}

module.exports = {
	Interval,
	IntervalSet
};
