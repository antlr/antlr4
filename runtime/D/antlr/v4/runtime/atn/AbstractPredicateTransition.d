/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 *
 */

module antlr.v4.runtime.atn.AbstractPredicateTransition;

import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.ATNState;

/**
 * TODO add class description
 */
class AbstractPredicateTransition : Transition
{

    public this(ATNState target)
    {
        super(target);
    }

}
