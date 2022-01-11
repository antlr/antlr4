#include "support/BitSet.h"

#include <algorithm>
#include <bitset>
#include <cstring>
#include <vector>

#ifdef _MSC_VER
#include <intrin.h>
#endif

using namespace antlrcpp;

namespace {

inline constexpr size_t BYTES_PER_WORD = sizeof(uintptr_t);
inline constexpr size_t BITS_PER_WORD = BYTES_PER_WORD * 8;

inline constexpr size_t indexForBit(size_t bit) { return bit / BITS_PER_WORD; }

inline constexpr uintptr_t maskForBit(size_t bit) {
  return static_cast<uintptr_t>(1) << (bit % BITS_PER_WORD);
}

// popcount
//
// Use __builtin_popcount and friends if available, otherwise fallback to std::bitset::count.
#if (defined(__GNUC__) && !defined(__clang__)) ||                                                  \
    (ANTLR4_HAVE_BUILTIN(__builtin_popcount) && ANTLR4_HAVE_BUILTIN(__builtin_popcountl) &&        \
     ANTLR4_HAVE_BUILTIN(__builtin_popcountll))
inline size_t popCount(unsigned int x) { return __builtin_popcount(x); }

inline size_t popCount(unsigned long x) { return __builtin_popcountl(x); }

inline size_t popCount(unsigned long long x) { return __builtin_popcountll(x); }
#else
inline size_t popCount(uintptr_t x) {
  std::bitset<BITS_PER_WORD> bits(x);
  return bits.count();
}
#endif

// ctz
//
// Use _BitScanReverse and friends for MSVC, use __builtin_ctz and friends if available, otherwise
// fallback to using de Bruijn.
#ifdef _MSC_VER
#if defined(_M_AMD64) || defined(_M_ARM64)
inline size_t countTrailingZeros(uint64_t x) {
  static_assert(sizeof(uint64_t) >= sizeof(uintptr_t),
                "sizeof(uint64_t) should be greater than or equal to sizeof(uintptr_t)");
  unsigned long index;
  return _BitScanReverse64(&index, x) ? 64 - static_cast<int>(index + 1) : 64;
}
#else
inline size_t countTrailingZeros(uint32_t x) {
  static_assert(sizeof(uint32_t) >= sizeof(uintptr_t),
                "sizeof(uint32_t) should be greater than or equal to sizeof(uintptr_t)");
  unsigned long index;
  return _BitScanReverse(&index, x) ? 32 - static_cast<int>(index + 1) : 32;
}
#endif
#elif (defined(__GNUC__) && !defined(__clang__)) ||                                                \
    (ANTLR4_HAVE_BUILTIN(__builtin_ctz) && ANTLR4_HAVE_BUILTIN(__builtin_ctzl) &&                  \
     ANTLR4_HAVE_BUILTIN(__builtin_ctzll))
inline size_t countTrailingZeros(unsigned int x) { return __builtin_ctz(x); }

inline size_t countTrailingZeros(unsigned long x) { return __builtin_ctzl(x); }

inline size_t countTrailingZeros(unsigned long long x) { return __builtin_ctzll(x); }
#else
// https://graphics.stanford.edu/~seander/bithacks.html#ZerosOnRightMultLookup

inline constexpr int DE_BRUIJN_TABLE[32] = {
    0,  1,  28, 2,  29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4,  8,
    31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6,  11, 5,  10, 9,
};

inline size_t countTrailingZeros(uint32_t x) {
  return x ? DE_BRUIJN_TABLE[((x & -x) * UINT32_C(0x077CB531)) >> 27] : 32;
}

inline size_t countTrailingZeros(uint64_t x) {
  size_t result = countTrailingZeros(static_cast<uint32_t>(x >> 32));
  return result != 32 ? result : countTrailingZeros(static_cast<uint32_t>(x)) + 32;
}
#endif

// Get the minimum number of elements required to represent all significant bits.
inline size_t bestFit(const uintptr_t *data, size_t size) {
  size_t index;
  for (index = size; index != 0; index--) {
    if (data[index - 1]) {
      break;
    }
  }
  return std::max(index, static_cast<size_t>(1));
}

} // namespace

BitSet::BitSet(const BitSet &other) {
  size_t capacity = bestFit(other.data(), other.size());
  if (capacity != 1) {
    // The other BitSet is using heap allocated storage, we need to duplicate instead of trivially
    // copying.
    uintptr_t *storage = new uintptr_t[capacity];
    std::memcpy(storage, other.data(), capacity * BYTES_PER_WORD);
    _storage = reinterpret_cast<uintptr_t>(storage);
  } else {
    std::memcpy(&_storage, other.data(), BYTES_PER_WORD);
  }
  _capacity = capacity;
}

BitSet::BitSet(BitSet &&other) noexcept : _storage(other._storage), _capacity(other._capacity) {
  other._storage = 0;
  other._capacity = 1;
}

BitSet::~BitSet() {
  if (_capacity != 1) {
    delete[] reinterpret_cast<uintptr_t *>(_storage);
  }
}

BitSet &BitSet::operator=(const BitSet &other) {
  if (this != &other) {
    size_t capacity = bestFit(other.data(), other.size());
    if (capacity <= _capacity) {
      std::memcpy(data(), other.data(), capacity * BYTES_PER_WORD);
      std::memset(data() + capacity, '\0', (_capacity - capacity) * BYTES_PER_WORD);
    } else {
      uintptr_t *storage = new uintptr_t[capacity];
      std::memcpy(storage, other.data(), capacity * BYTES_PER_WORD);
      if (_capacity != 1) {
        delete[] reinterpret_cast<uintptr_t *>(_storage);
      }
      _storage = reinterpret_cast<uintptr_t>(storage);
      _capacity = capacity;
    }
  }
  return *this;
}

BitSet &BitSet::operator=(BitSet &&other) noexcept {
  if (this != &other) {
    if (_capacity != 1) {
      delete[] reinterpret_cast<uintptr_t *>(_storage);
    }
    _storage = other._storage;
    _capacity = other._capacity;
    other._storage = 0;
    other._capacity = 1;
  }
  return *this;
}

size_t BitSet::count() const noexcept {
  const uintptr_t *ptr = data();
  size_t result = 0;
  for (size_t index = 0; index < size(); index++) {
    result += popCount(ptr[index]);
  }
  return result;
}

bool BitSet::empty() const noexcept {
  const uintptr_t *ptr = data();
  for (size_t index = 0; index < size(); index++) {
    if (ptr[index]) {
      return false;
    }
  }
  return true;
}

std::optional<size_t> BitSet::find() const noexcept {
  const uintptr_t *ptr = data();
  for (size_t index = 0; index < size(); index++) {
    uintptr_t word = ptr[index];
    if (word) {
      return static_cast<size_t>(index * BITS_PER_WORD + countTrailingZeros(word));
    }
  }
  return std::nullopt;
}

bool BitSet::test(size_t bit) const noexcept {
  size_t index = indexForBit(bit);
  return index < size() && (data()[index] & maskForBit(bit)) != 0;
}

void BitSet::set(size_t bit, bool value) {
  size_t index = indexForBit(bit);
  if (index >= size()) {
    if (!value) {
      return;
    }
    resize(index + 1);
  }
  if (value) {
    data()[index] |= maskForBit(bit);
  } else {
    data()[index] &= ~maskForBit(bit);
  }
}

BitSet &BitSet::operator|=(const BitSet &other) {
  if (this != &other) {
    size_t capacity = bestFit(other.data(), other.size());
    if (capacity > _capacity) {
      resize(capacity);
    }
    uintptr_t *dest = data();
    const uintptr_t *src = other.data();
    for (size_t index = 0; index < capacity; index++) {
      dest[index] |= src[index];
    }
  }
  return *this;
}

void BitSet::reset() {
  if (_capacity != 1) {
    delete[] reinterpret_cast<uintptr_t *>(_storage);
  }
  _storage = 0;
  _capacity = 1;
}

bool BitSet::operator==(const BitSet &other) const noexcept {
  size_t thisSize = size();
  size_t otherSize = other.size();
  size_t minSize = std::min(thisSize, otherSize);
  if (std::memcmp(data(), other.data(), minSize * BYTES_PER_WORD) != 0) {
    return false;
  }
  thisSize -= minSize;
  otherSize -= minSize;
  if (thisSize != 0) {
    const uintptr_t *ptr = data() + minSize;
    while (thisSize != 0) {
      if (*ptr) {
        return false;
      }
      ptr++;
      thisSize--;
    }
  } else if (otherSize != 0) {
    const uintptr_t *ptr = other.data() + minSize;
    while (otherSize != 0) {
      if (*ptr) {
        return false;
      }
      ptr++;
      otherSize--;
    }
  }
  return true;
}

void BitSet::resize(size_t newSize) {
  if (newSize <= _capacity) {
    return;
  }
  uintptr_t *storage = new uintptr_t[newSize];
  std::memcpy(storage, data(), size() * BYTES_PER_WORD);
  if (_capacity != 1) {
    delete[] reinterpret_cast<uintptr_t *>(_storage);
  }
  _storage = reinterpret_cast<uintptr_t>(storage);
  _capacity = newSize;
}

std::string BitSet::toString() const {
  std::vector<std::string> parts;
  parts.reserve(count());
  const uintptr_t *ptr = data();
  size_t capacity = bestFit(ptr, size());
  for (size_t index = 0; index < capacity; index++) {
    uintptr_t word = ptr[index];
    while (word) {
      size_t count = countTrailingZeros(word);
      parts.push_back(std::to_string(index * BITS_PER_WORD + count));
      word &= ~(static_cast<uintptr_t>(1) << count);
    }
  }
  size_t sizeNeeded = 2; // '{' and '}'
  for (const auto &part : parts) {
    sizeNeeded += part.size();
  }
  if (!parts.empty()) {
    sizeNeeded += (parts.size() - 1) * 2; // ', ' for each except first
  }
  std::string result;
  result.reserve(sizeNeeded);
  result.push_back('{');
  if (!parts.empty()) {
    auto iter = parts.begin();
    result.append(*iter++);
    for (; iter != parts.end(); iter++) {
      result.append(", ");
      result.append(*iter);
    }
  }
  result.push_back('}');
  return result;
}
