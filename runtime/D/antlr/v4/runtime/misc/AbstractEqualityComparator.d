/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.AbstractEqualityComparator;

import antlr.v4.runtime.misc.EqualityComparator;

/**
 * This abstract base class is provided so performance-critical applications can
 * use virtual- instead of interface-dispatch when calling comparator methods.
 */
abstract class AbstractEqualityComparator(T) : EqualityComparator!T
{

}
