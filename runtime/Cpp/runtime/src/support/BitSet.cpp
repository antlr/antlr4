#include "support/BitSet.h"

#include <algorithm>
#include <bitset>
#include <cstring>
#include <vector>

#ifdef _MSC_VER
#include <intrin.h>
#endif

#include "misc/MurmurHash.h"

using namespace antlr4::misc;
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
      (ANTLR4CPP_HAVE_BUILTIN(__builtin_popcount) && ANTLR4CPP_HAVE_BUILTIN(__builtin_popcountl) &&  \
      ANTLR4CPP_HAVE_BUILTIN(__builtin_popcountll))
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
  #elif (defined(__GNUC__) && !defined(__clang__)) ||                                      \
      (ANTLR4CPP_HAVE_BUILTIN(__builtin_ctz) && ANTLR4CPP_HAVE_BUILTIN(__builtin_ctzl) &&  \
      ANTLR4CPP_HAVE_BUILTIN(__builtin_ctzll))
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

}  // namespace

size_t BitSet::count() const noexcept {
  const auto *ptr = data();
  size_t result = 0;
  for (size_t index = 0; index < size(); index++) {
    result += popCount(ptr[index]);
  }
  return result;
}

bool BitSet::empty() const noexcept {
  const auto *ptr = data();
  for (size_t index = 0; index < size(); index++) {
    if (ptr[index]) {
      return false;
    }
  }
  return true;
}

std::optional<size_t> BitSet::find() const noexcept {
  const auto *ptr = data();
  for (size_t index = 0; index < size(); index++) {
    auto word = ptr[index];
    if (word) {
      return static_cast<size_t>(index * BITS_PER_WORD + countTrailingZeros(word));
    }
  }
  return std::nullopt;
}

bool BitSet::test(size_t bit) const noexcept {
  auto index = indexForBit(bit);
  return index < size() && (data()[index] & maskForBit(bit)) != 0;
}

void BitSet::set(size_t bit, bool value) {
  auto index = indexForBit(bit);
  if (index >= size()) {
    if (!value) {
      return;
    }
    _storage.resize(index + 1, 0);
  }
  if (value) {
    data()[index] |= maskForBit(bit);
  } else {
    data()[index] &= ~maskForBit(bit);
  }
}

BitSet &BitSet::operator|=(const BitSet &other) {
  if (this != std::addressof(other)) {
    auto capacity = bestFit(other.data(), other.size());
    if (capacity > _storage.capacity()) {
      _storage.resize(capacity, 0);
    }
    auto *dest = data();
    const auto *src = other.data();
    for (size_t index = 0; index < capacity; index++) {
      dest[index] |= src[index];
    }
  }
  return *this;
}

void BitSet::reset() {
  _storage.clear();
}

size_t BitSet::hashCode() const {
  return MurmurHash::hashCode(_storage.data(), bestFit(_storage.data(), _storage.size()));
}

bool BitSet::equals(const BitSet &other) const {
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

std::string BitSet::toString() const {
  std::vector<std::string> parts;
  parts.reserve(count());
  const auto *ptr = data();
  size_t capacity = bestFit(ptr, size());
  for (size_t index = 0; index < capacity; index++) {
    auto word = ptr[index];
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
