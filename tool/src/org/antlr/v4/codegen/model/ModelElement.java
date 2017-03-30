/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Indicate field of OutputModelObject is an element to be walked by
 *  OutputModelWalker.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelElement {
}
