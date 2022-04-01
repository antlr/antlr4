/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <cstddef>
#include <cstdint>
#include <functional>
#include <optional>
#include <string>

#include "antlr4-common.h"
#include "support/InlinedVector.h"

namespace antlrcpp {

  class ANTLR4CPP_PUBLIC BitSet;

  class ANTLR4CPP_PUBLIC BitReference final {
  public:
    BitReference &operator=(const BitReference &other) noexcept;

    BitReference &operator=(bool value) noexcept;

    operator bool() const noexcept;

  private:
    friend class BitSet;

    BitReference() = delete;

    BitReference(const BitReference &) = delete;

    BitReference(BitReference &&) = delete;

    constexpr BitReference(BitSet *bits, size_t bit) : _bits(bits), _bit(bit) {}

    BitReference &operator=(BitReference &&) = delete;

    BitSet *_bits;
    size_t _bit;
  };

  // BitSet is a dynamically resizable set of bits. It serves a similar purpose to std::bitset and
  // std::vector<bool>, but only implements featured needed by ANTLR.
  class ANTLR4CPP_PUBLIC BitSet final {
  public:
    BitSet() = default;

    BitSet(const BitSet&) = default;

    BitSet(BitSet&&) = default;

    BitSet& operator=(const BitSet&) = default;

    BitSet& operator=(BitSet&&) = default;

    bool operator[](size_t bit) const noexcept { return test(bit); }

    BitReference operator[](size_t bit) noexcept { return BitReference(this, bit); }

    // Returns the number of bits set to true, a.k.a. popcount.
    size_t count() const noexcept;

    // True if no bits are set to true, false otherwise.
    bool empty() const noexcept;

    // Find the first set bit, a.k.a. FFS.
    std::optional<size_t> find() const noexcept;

    bool test(size_t bit) const noexcept;

    void set(size_t bit, bool value = true);

    void clear(size_t bit) { set(bit, false); }

    // Performs a bitwise OR with `other` and assigns the result to `this`.
    BitSet &operator|=(const BitSet &other);

    void reset();

    size_t hashCode() const;

    bool equals(const BitSet &other) const;

    std::string toString() const;

  private:
    uintptr_t* data() noexcept { return _storage.data(); }

    const uintptr_t* data() const noexcept { return _storage.data(); }

    size_t size() const noexcept { return _storage.size(); }

    InlinedVector<uintptr_t> _storage;
  };

  inline bool operator==(const BitSet &lhs, const BitSet &rhs) {
    return lhs.equals(rhs);
  }

  inline bool operator!=(const BitSet &lhs, const BitSet &rhs) {
    return !operator==(lhs, rhs);
  }

  inline BitReference &BitReference::operator=(const BitReference &other) noexcept {
    _bits->set(_bit, static_cast<bool>(other));
    return *this;
  }

  inline BitReference &BitReference::operator=(bool value) noexcept {
    _bits->set(_bit, value);
    return *this;
  }

  inline BitReference::operator bool() const noexcept { return _bits->test(_bit); }

} // namespace antlrcpp

namespace std {

  template <>
  struct hash<::antlrcpp::BitSet> {
    size_t operator()(const ::antlrcpp::BitSet &bitSet) const {
      return bitSet.hashCode();
    }
  };

}  // namespace std
