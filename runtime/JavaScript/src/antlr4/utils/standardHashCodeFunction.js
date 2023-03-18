/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { stringHashCode } from "./stringHashCode.js";

export default function standardHashCodeFunction(a) {
    return a ? typeof a === 'string' ? stringHashCode(a) : a.hashCode() : -1;
}
