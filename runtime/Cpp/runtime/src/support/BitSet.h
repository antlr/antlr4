/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <cstddef>
#include <cstdint>
#include <optional>
#include <string>

#include "antlr4-common.h"

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
    constexpr BitSet() = default;

    BitSet(const BitSet &other);

    BitSet(BitSet &&other) noexcept;

    ~BitSet();

    BitSet &operator=(const BitSet &other);

    BitSet &operator=(BitSet &&other) noexcept;

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

    std::string toString() const;

    bool operator==(const BitSet &other) const noexcept;

    bool operator!=(const BitSet &other) const noexcept { return !operator==(other); }

  private:
    uintptr_t *data() noexcept {
      return _capacity == 1 ? &_storage : reinterpret_cast<uintptr_t *>(_storage);
    }

    const uintptr_t *data() const noexcept {
      return _capacity == 1 ? &_storage : reinterpret_cast<uintptr_t *>(_storage);
    }

    constexpr size_t size() const noexcept { return _capacity; }

    void resize(size_t newSize);

    // When _capacity is 1, the BitSet is stored inline in _storage. When the _capacity is not 1,
    // then _storage is actually a pointer to a continugous block of uintptr_t that has _capacity
    // elements. _capacity should never be 0.
    uintptr_t _storage = 0;
    size_t _capacity = 1;
  };

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
