/*
 * Copyright (c) 2012 The ANTLR Project Authors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.parse;

/** Used to throw us out of deeply nested element back to end of a rule's
 *  alt list. Note it's not under RecognitionException.
 */
public class ResyncToEndOfRuleBlock extends RuntimeException {
}
