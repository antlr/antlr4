/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
export default function standardEqualsFunction(a, b) {
    return a && a.equals ? a.equals(b) : a===b;
}
