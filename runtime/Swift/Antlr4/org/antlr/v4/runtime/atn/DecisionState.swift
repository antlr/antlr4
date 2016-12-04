/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public class DecisionState: ATNState {
    public var decision: Int = -1
    public var nonGreedy: Bool = false
}
