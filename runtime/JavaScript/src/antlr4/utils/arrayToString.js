/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import valueToString from "./valueToString.js";

export default function arrayToString(a) {
    return Array.isArray(a) ? ("[" + a.map(valueToString).join(", ") + "]") : "null";
}
