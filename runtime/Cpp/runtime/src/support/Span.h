/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <cstddef>

#if __cplusplus >= 202002L
#include <version>
#endif

#if defined(__cpp_lib_span) && __cpp_lib_span >= 202002L
#include <span>
#define ANTLR4CPP_HAVE_STD_SPAN 1
#else
#include <array>
#include <iterator>
#include <limits>
#include <type_traits>
#include <vector>
#endif

#include "antlr4-common.h"

namespace antlrcpp {

#if ANTLR4CPP_HAVE_STD_SPAN

  inline constexpr size_t DYNAMIC_EXTENT = std::dynamic_extent;

  template <typename T, size_t Extent = DYNAMIC_EXTENT>
  using Span = std::span<T, Extent>;

#else

  inline constexpr size_t DYNAMIC_EXTENT = std::numeric_limits<size_t>::max();

  template <typename T, size_t Extent = DYNAMIC_EXTENT>
  class Span;

  template <typename T>
  class Span<T, DYNAMIC_EXTENT> final {
  public:
    static constexpr size_t extent = DYNAMIC_EXTENT;

    using element_type = T;
    using value_type = std::remove_cv_t<T>;
    using size_type = size_t;
    using difference_type = ptrdiff_t;
    using pointer = T*;
    using const_pointer = const T*;
    using reference = T&;
    using const_reference = const T&;
    using iterator = pointer;
    using reverse_iterator = std::reverse_iterator<iterator>;

    constexpr Span() noexcept : _base(nullptr), _size(0) {}

    constexpr Span(const Span&) noexcept = default;

    template <size_t N>
    constexpr Span(element_type (&array)[N]) noexcept : _base(std::data(array)), _size(N) {}

    template <typename U, size_t N>
    constexpr Span(std::array<U, N> &array) noexcept : _base(std::data(array)), _size(N) {}

    template <typename U, size_t N>
    constexpr Span(const std::array<U, N> &array) noexcept : _base(std::data(array)), _size(N) {}

    constexpr Span(pointer data, size_type size) noexcept : _base(data), _size(size) {}

    constexpr Span(std::vector<element_type> &vector) noexcept : _base(vector.data()), _size(vector.size()) {}

    constexpr Span& operator=(const Span&) noexcept = default;

    constexpr iterator begin() const noexcept { return _base; }

    constexpr iterator end() const noexcept { return _base + _size; }

    constexpr reverse_iterator rbegin() const noexcept { return reverse_iterator(end()); }

    constexpr reverse_iterator rend() const noexcept { return reverse_iterator(begin()); }

    constexpr reference front() const { return *_base; }

    constexpr reference back() const { return *(_base + _size - 1); }

    constexpr reference operator[](size_type index) const { return _base[index]; }

    constexpr pointer data() const noexcept { return _base; }

    constexpr size_type size() const noexcept { return _size; }

    constexpr size_type size_bytes() const noexcept { return _size * sizeof(T); }

    constexpr bool empty() const noexcept { return _size == 0; }

    template <size_t Count>
    constexpr Span<T, Count> first() const { return Span<T, Count>(_base); }

    constexpr Span<T> first(size_type count) const { return Span<T>(_base, count); }

    template <size_t Count>
    constexpr Span<T, Count> last() const {
      return Span<T, Count>(_base + _size - Count);
    }

    constexpr Span<T> last(size_type count) const { return Span<T>(_base + _size - count, count); }

  private:
    template <typename U, size_t OtherExtent> friend class Span;

    pointer _base;
    size_type _size;
  };

  template <typename T>
  class Span<T, 0> final {
  public:
    static constexpr size_t extent = 0;

    using element_type = T;
    using value_type = std::remove_cv_t<T>;
    using size_type = size_t;
    using difference_type = ptrdiff_t;
    using pointer = T*;
    using const_pointer = const T*;
    using reference = T&;
    using const_reference = const T&;
    using iterator = pointer;
    using reverse_iterator = std::reverse_iterator<iterator>;

    constexpr Span() noexcept : _base(nullptr) {}

    constexpr Span(const Span&) noexcept = default;

    constexpr Span& operator=(const Span&) noexcept = default;

    constexpr iterator begin() const noexcept { return _base; }

    constexpr iterator end() const noexcept { return _base; }

    constexpr reverse_iterator rbegin() const noexcept { return reverse_iterator(end()); }

    constexpr reverse_iterator rend() const noexcept { return reverse_iterator(begin()); }

    constexpr reference front() const { return *_base; }

    constexpr reference back() const { return *(_base - 1); }

    constexpr reference operator[](size_type index) const { return _base[index]; }

    constexpr pointer data() const noexcept { return _base; }

    constexpr size_type size() const noexcept { return 0; }

    constexpr size_type size_bytes() const noexcept { return 0; }

    constexpr bool empty() const noexcept { return true; }

    template <size_t Count>
    constexpr Span<T, Count> first() const { return Span<T, Count>(_base); }

    constexpr Span<T> first(size_type count) const { return Span<T>(_base, count); }

    template <size_t Count>
    constexpr Span<T, Count> last() const {
      return Span<T, Count>(_base - Count);
    }

    constexpr Span<T> last(size_type count) const { return Span<T>(_base - count, count); }

  private:
    template <typename U, size_t OtherExtent> friend class Span;

    pointer _base;
  };

  template <typename T, size_t Extent>
  class Span final {
  public:
    static constexpr size_t extent = Extent;

    using element_type = T;
    using value_type = std::remove_cv_t<T>;
    using size_type = size_t;
    using difference_type = ptrdiff_t;
    using pointer = T*;
    using const_pointer = const T*;
    using reference = T&;
    using const_reference = const T&;
    using iterator = pointer;
    using reverse_iterator = std::reverse_iterator<iterator>;

    constexpr Span(const Span&) noexcept = default;

    constexpr Span& operator=(const Span&) noexcept = default;

    constexpr iterator begin() const noexcept { return _base; }

    constexpr iterator end() const noexcept { return _base + extent; }

    constexpr reverse_iterator rbegin() const noexcept { return reverse_iterator(end()); }

    constexpr reverse_iterator rend() const noexcept { return reverse_iterator(begin()); }

    constexpr reference front() const {
      static_assert((extent != 0), "extent must not be 0 for front()");
      return *_base;
    }

    constexpr reference back() const {
      static_assert((extent != 0), "extent must not be 0 for back()");
      return *(_base + extent - 1);
    }

    constexpr reference operator[](size_type index) const { return _base[index]; }

    constexpr pointer data() const noexcept { return _base; }

    constexpr size_type size() const noexcept { return extent; }

    constexpr size_type size_bytes() const noexcept { return extent * sizeof(T); }

    constexpr bool empty() const noexcept { return extent == 0; }

    template <size_t Count>
    constexpr Span<T, Count> first() const { return Span<T, Count>(_base); }

    constexpr Span<T> first(size_type count) const { return Span<T>(_base, count); }

    template <size_t Count>
    constexpr Span<T, Count> last() const {
      return Span<T, Count>(_base + extent - Count);
    }

    constexpr Span<T> last(size_type count) const { return Span<T>(_base + extent - count, count); }

  private:
    template <typename U, size_t OtherExtent> friend class Span;

    Span() = delete;

    pointer _base;
  };

  template <typename T1, typename T2, size_t N>
  bool operator<(const std::vector<T1> &lhs, Span<T2, N> rhs) {
    return std::lexicographical_compare(lhs.begin(), lhs.end(), rhs.begin(), rhs.end());
  }

  template <typename T1, typename T2, size_t N>
  bool operator<(Span<T1, N> lhs, const std::vector<T2> &rhs) {
    return std::lexicographical_compare(lhs.begin(), lhs.end(), rhs.begin(), rhs.end());
  }

  template <typename T1, typename T2, size_t N1, size_t N2>
  bool operator<(Span<T1, N1> lhs, Span<T2, N2> rhs) {
    return std::lexicographical_compare(lhs.begin(), lhs.end(), rhs.begin(), rhs.end());
  }

#endif

} // namespace antlrcpp
