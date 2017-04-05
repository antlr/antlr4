/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool;

/** the various kinds of labels. t=type, id=ID, types+=type ids+=ID */
public enum LabelType {
    RULE_LABEL,
    TOKEN_LABEL,
    RULE_LIST_LABEL,
    TOKEN_LIST_LABEL,
    LEXER_STRING_LABEL;         // used in lexer for x='a'
}
