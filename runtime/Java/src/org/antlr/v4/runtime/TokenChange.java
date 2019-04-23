/*
 * Copyright 2019 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.runtime;

/**
 * Classes that represents a change to a single token
 * 
 * For change type ADDED, newToken is required.
 * 
 * For change type REMOVED, oldToken is required.
 * 
 * For change type CHANGED, oldToken and newToken are required.
 * 
 * Token changes may *not* overlap. You also need to account for hidden tokens
 * (but not *skipped* ones).
 */
public class TokenChange {
    TokenChangeType changeType;
    CommonToken oldToken;
    CommonToken newToken;
    TokenChange(TokenChangeType changeType,CommonToken oldToken, CommonToken newToken ) {
        this.changeType = changeType;
        this.oldToken = oldToken;
        this.newToken = newToken;
    }
}
