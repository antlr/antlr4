/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.go;

import java.io.File;
import java.io.FilenameFilter;

public class GoFileFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return name.endsWith(".go");
    }
}
