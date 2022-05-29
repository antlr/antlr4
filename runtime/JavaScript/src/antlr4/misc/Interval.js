/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
/* stop is not included! */
export default class Interval {

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

Interval.INVALID_INTERVAL = new Interval(-1, -2);

