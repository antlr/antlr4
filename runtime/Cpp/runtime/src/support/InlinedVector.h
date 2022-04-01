/* Copyright (c) 2012-2021 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <algorithm>
#include <cassert>
#include <cstddef>
#include <iterator>
#include <limits>
#include <memory>
#include <stdexcept>
#include <type_traits>
#include <utility>

namespace antlrcpp {

  template <typename T>
  struct InlinedVectorRemoteStorage final {
    T *data;
    size_t size;
  };

  template <typename T,
            size_t N = (sizeof(InlinedVectorRemoteStorage<T>) / sizeof(T)),
            typename Allocator = std::allocator<T>>
  class InlinedVector;

  template <typename T, size_t N, typename Allocator>
  class InlinedVector final {
  private:
    union Storage final {
      Storage() {}

      InlinedVectorRemoteStorage<T> remote;
      T local[N];
    };

  public:
    using value_type = T;
    using allocator_type = Allocator;
    using size_type = size_t;
    using difference_type = ptrdiff_t;
    using reference = value_type&;
    using const_reference = const value_type&;
    using pointer = value_type*;
    using const_pointer = const value_type*;
    using iterator = pointer;
    using const_iterator = const_pointer;
    using reverse_iterator = std::reverse_iterator<iterator>;
    using const_reverse_iterator = std::reverse_iterator<const_iterator>;

    InlinedVector() = default;

    InlinedVector(const InlinedVector<T, N, Allocator> &other) {
      copy_from(other);
    }

    InlinedVector(InlinedVector<T, N, Allocator> &&other) {
      move_from(std::forward<InlinedVector<T, N, Allocator>>(other));
    }

    ~InlinedVector() {
      destroy();
    }

    InlinedVector<T, N, Allocator>& operator=(const InlinedVector<T, N, Allocator> &other) {
      if (this != std::addressof(other)) {
        if (capacity() >= other.size()) {
          if constexpr (!std::is_trivially_destructible_v<value_type>) {
            std::destroy_n(data(), size());
          }
          std::uninitialized_copy_n(other.data(), other.size(), data());
          size() = other.size();
          _allocator = other._allocator;
        } else {
          destroy();
          copy_from(other);
        }
      }
      return *this;
    }

    InlinedVector<T, N, Allocator>& operator=(InlinedVector<T, N, Allocator> &&other) {
      if (this != std::addressof(other)) {
        destroy();
        move_from(std::forward<InlinedVector<T, N, Allocator>>(other));
      }
      return *this;
    }

    reference at(size_type index) {
      if (index >= size()) {
        throw std::out_of_range("index out of range");
      }
      return (*this)[index];
    }

    const_reference at(size_type index) const {
      if (index >= size()) {
        throw std::out_of_range("index out of range");
      }
      return (*this)[index];
    }

    reference operator[](size_type index) noexcept { return data()[index]; }

    const_reference operator[](size_type index) const noexcept { return data()[index]; }

    reference front() { return at(0); }

    const_reference front() const { return at(0); }

    reference back() { return at(size() - 1); }

    const_reference back() const { return at(size() - 1); }

    iterator begin() { return data(); }

    const_iterator begin() const { return data(); }

    const_iterator cbegin() const { return data(); }

    iterator end() { return data() + size(); }

    const_iterator end() const { return data() + size(); }

    const_iterator cend() const { return data() + size(); }

    reverse_iterator rbegin() { return reverse_iterator(end()); }

    const_reverse_iterator rbegin() const { return const_reverse_iterator(end()); }

    const_reverse_iterator crbegin() const { return const_reverse_iterator(cend()); }

    reverse_iterator rend() { return reverse_iterator(begin()); }

    const_reverse_iterator rend() const { return const_reverse_iterator(begin()); }

    const_reverse_iterator crend() const { return const_reverse_iterator(cbegin()); }

    pointer data() noexcept { return inlined() ? inlined_data() : allocated_data(); }

    const_pointer data() const noexcept { return inlined() ? inlined_data() : allocated_data(); }

    constexpr bool empty() const noexcept { return size() == 0; }

    constexpr size_type size() const noexcept { return inlined() ? inlined_size() : allocated_size(); }

    constexpr size_type size_bytes() const noexcept { return size() * sizeof(value_type); }

    constexpr size_type max_size() const noexcept {
      return std::numeric_limits<size_type>::max() / sizeof(value_type);
    }

    constexpr size_type capacity() const noexcept { return std::max(_capacity, N); }

    void push_back(const value_type &value) {
      maybe_grow();
      ::new (static_cast<void*>(data() + size())) value_type(value);
      ++size();
    }

    void push_back(value_type&& value) {
      maybe_grow();
      ::new (static_cast<void*>(data() + size())) value_type(std::forward<value_type>(value));
      ++size();
    }

    template <typename... Args>
    reference emplace_back(Args&&... args) {
      maybe_grow();
      pointer target = data() + size();
      ::new (static_cast<void*>(target)) value_type(std::forward<Args>(args)...);
      ++size();
      return *target;
    }

    void pop_back() {
      assert(!empty());
      if constexpr (!std::is_trivially_destructible_v<value_type>) {
        std::destroy_at(data() + size() - 1);
      }
      --size();
    }

    void reserve(size_type new_capacity) {
      if (capacity() >= new_capacity) {
        return;
      }
      size_type to_capacity = std::max(capacity() * 2, new_capacity);
      pointer to_data = std::allocator_traits<allocator_type>::allocate(_allocator, to_capacity);
      size_type to_size = size();
      std::uninitialized_move_n(data(), to_size, to_data);
      if (allocated()) {
        std::allocator_traits<allocator_type>::deallocate(_allocator, allocated_data(), _capacity);
      }
      _capacity = to_capacity;
      allocated_size() = to_size;
      allocated_data() = to_data;
    }

    void resize(size_type new_size, const value_type &value) {
      if (size() == new_size) {
        return;
      }
      if (new_size < size()) {
        if constexpr (!std::is_trivially_destructible_v<value_type>) {
          std::destroy_n(data() + new_size, size() - new_size);
        }
        size() = new_size;
        return;
      }
      reserve(new_size);
      std::uninitialized_fill_n(data() + size(), new_size - size(), value);
      size() = new_size;
    }

    void shrink_to_fit() {
      if (size() < capacity() && allocated()) {
        if (size() <= N) {
          pointer from_data = allocated_data();
          size_type from_size = allocated_size();
          std::uninitialized_move_n(from_data, from_size, inlined_data());
          size_type from_capacity = _capacity;
          _capacity = from_size;
          std::allocator_traits<allocator_type>::deallocate(_allocator, from_data, from_capacity);
        } else {
          pointer to_data;
          try {
            to_data = std::allocator_traits<allocator_type>::allocate(_allocator, size());
          } catch (const std::bad_alloc&) {
            return;
          }
          try {
            std::uninitialized_move_n(allocated_data(), allocated_size(), to_data);
          } catch (...) {
            std::allocator_traits<allocator_type>::deallocate(_allocator, to_data, size());
            throw;
          }
          pointer from_data = allocated_data();
          size_type from_capacity = _capacity;
          allocated_data() = to_data;
          _capacity = size();
          std::allocator_traits<allocator_type>::deallocate(_allocator, from_data, from_capacity);
        }
      }
    }

    void clear() {
      if constexpr (!std::is_trivially_destructible_v<value_type>) {
        std::destroy_n(data(), size());
      }
      size() = 0;
    }

  private:
    size_type& inlined_size() noexcept { return _capacity; }

    size_type inlined_size() const noexcept { return _capacity; }

    size_type& allocated_size() noexcept { return _storage.remote.size; }

    size_type allocated_size() const noexcept { return _storage.remote.size; }

    size_type& size() noexcept { return inlined() ? inlined_size() : allocated_size(); }

    constexpr bool inlined() const noexcept { return _capacity <= N; }

    constexpr bool allocated() const noexcept { return !inlined(); }

    constexpr pointer inlined_data() noexcept { return _storage.local; }

    constexpr const_pointer inlined_data() const noexcept { return _storage.local; }

    constexpr pointer& allocated_data() noexcept { return _storage.remote.data; }

    constexpr const_pointer allocated_data() const noexcept { return _storage.remote.data; }

    void maybe_grow() {
      reserve(size() + 1);
    }

    void destroy() {
      if constexpr (!std::is_trivially_destructible_v<value_type>) {
        std::destroy_n(data(), size());
      }
      if (allocated()) {
        std::allocator_traits<allocator_type>::deallocate(_allocator, allocated_data(), _capacity);
      }
      _capacity = 0;
    }

    void copy_from(const InlinedVector<T, N, Allocator> &other) {
      _allocator = other._allocator;
      if (other.size() <= N) {
        std::uninitialized_copy_n(other.data(), other.size(), inlined_data());
        _capacity = other.size();
      } else {
        allocated_data() = std::allocator_traits<allocator_type>::allocate(_allocator, other.size());
        allocated_size() = other.size();
        _capacity = other.size();
        try {
          std::uninitialized_copy_n(other.data(), other.size(), allocated_data());
        } catch (...) {
          _capacity = 0;
          std::allocator_traits<allocator_type>::deallocate(_allocator, allocated_data(), allocated_size());
          throw;
        }
      }
    }

    void move_from(InlinedVector<T, N, Allocator> &&other) {
      _allocator = std::move(other._allocator);
      if (other.allocated()) {
        _capacity = other._capacity;
        allocated_size() = other.allocated_size();
        allocated_data() = other.allocated_data();
        other._capacity = 0;
        other.allocated_size() = 0;
        other.allocated_data() = nullptr;
      } else {
        std::uninitialized_move_n(other.inlined_data(), other.inlined_size(), inlined_data());
        _capacity = other._capacity;
        other._capacity = 0;
      }
    }

    size_t _capacity = 0;
    Storage _storage;
    allocator_type _allocator;
  };

  template <typename T, size_t N1, typename Allocator1, size_t N2, typename Allocator2>
  inline bool operator==(const InlinedVector<T, N1, Allocator1> &lhs, const InlinedVector<T, N2, Allocator2> &rhs) {
    return lhs.size() == rhs.size() && std::equal(lhs.begin(), lhs.end(), rhs.begin());
  }

  template <typename T, size_t N1, typename Allocator1, size_t N2, typename Allocator2>
  inline bool operator!=(const InlinedVector<T, N1, Allocator1> &lhs, const InlinedVector<T, N2, Allocator2> &rhs) {
    return !operator==(lhs, rhs);
  }

} // namespace antlrcpp
