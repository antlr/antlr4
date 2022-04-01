/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "Token.h"

#include "Vocabulary.h"
#include "VocabularyImpl.h"

using namespace antlr4;

const Vocabulary& Vocabulary::empty() {
  static const VocabularyImpl instance;
  return instance;
}
