﻿/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/LoopEndState.h"

using namespace antlr4::atn;

size_t LoopEndState::getStateType() {
  return LOOP_END;
}
