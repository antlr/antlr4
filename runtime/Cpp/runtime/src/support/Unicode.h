/* Copyright (c) 2021 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

namespace antlrcpp {

  class Unicode final {
  public:
    static constexpr char32_t REPLACEMENT_CHARACTER = 0xfffd;

    static constexpr bool isValid(char32_t codePoint) {
      return codePoint < 0xd800 || (codePoint > 0xdfff && codePoint <= 0x10ffff);
    }

  private:
    Unicode() = delete;

    Unicode(const Unicode&) = delete;

    Unicode& operator=(const Unicode&) = delete;
  };

}
