/*
 * Copyright 2019 The ANTLR Project. All rights reserved.
 * Licensed under the BSD-3-Clause license. See LICENSE file in the project root for license information.
 */
package org.antlr.v4.runtime;

/**
 * Definition of a token change:
 * 
 * ADDED = A new token that did not exist before
 * 
 * CHANGED = A token that was in the stream before but changed in some way.
 * 
 * REMOVED = A token that no longer exists in the stream.
 *
 */
public enum TokenChangeType {
    ADDED, CHANGED, REMOVED,
};