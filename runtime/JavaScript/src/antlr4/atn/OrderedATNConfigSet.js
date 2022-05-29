/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import ATNConfigSet from "./ATNConfigSet.js";
import HashSet from "../misc/HashSet.js";

export default class OrderedATNConfigSet extends ATNConfigSet {
    constructor() {
        super();
        this.configLookup = new HashSet();
    }
}
