/*
 The MIT License (MIT)

 Copyright (c) 2014 Graeme Hill (http://graemehill.ca)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */
#pragma once

#include <array>
#include <cstdint>
#include <cstring>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

#ifdef GUID_ANDROID
#include <jni.h>
#endif

namespace antlrcpp {

// Class to represent a GUID/UUID. Each instance acts as a wrapper around a
// 16 byte value that can be passed around by value. It also supports
// conversion to string (via the stream operator <<) and conversion from a
// string via constructor.
class Guid final {
public:
  using size_type = typename std::array<uint8_t, 16>::size_type;
  using pointer = typename std::array<uint8_t, 16>::pointer;
  using const_pointer = typename std::array<uint8_t, 16>::const_pointer;
  using iterator = typename std::array<uint8_t, 16>::iterator;
  using reverse_iterator = typename std::array<uint8_t, 16>::reverse_iterator;
  using const_iterator = typename std::array<uint8_t, 16>::const_iterator;
  using const_reverse_iterator = typename std::array<uint8_t, 16>::const_reverse_iterator;

  // create a guid from vector of bytes
  Guid(const std::vector<uint8_t> &bytes);

  explicit Guid(const std::array<uint8_t, 16> &bytes);

  // create a guid from array of bytes
  Guid(const uint8_t *bytes);

  // Create a guid from array of words.
  Guid(const uint16_t *bytes, bool reverse);

  // create a guid from string
  Guid(const std::string &fromString);

  // create empty guid
  Guid() = default;

  // copy constructor
  Guid(const Guid &other) = default;

  // overload assignment operator
  Guid &operator=(const Guid &other) = default;

  // overload equality and inequality operator
  friend bool operator==(const Guid &lhs, const Guid &rhs) {
    return std::memcmp(lhs.data(), rhs.data(), 16) == 0;
  }

  friend bool operator!=(const Guid &lhs, const Guid &rhs) {
    return !operator==(lhs, rhs);
  }

  // make the << operator a friend so it can access bytes_
  friend std::ostream &operator<<(std::ostream &s, const Guid &guid);

  pointer data() { return bytes_.data(); }

  const_pointer data() const { return bytes_.data(); }

  size_type size() const { return bytes_.size(); }

  std::string toString() const;

  iterator begin() { return bytes_.begin(); }

  iterator end() { return bytes_.end(); }

  reverse_iterator rbegin() { return bytes_.rbegin(); }

  reverse_iterator rend() { return bytes_.rend(); }

  const_iterator cbegin() const { return bytes_.begin(); }

  const_iterator cend() const { return bytes_.end(); }

  const_reverse_iterator crbegin() const { return bytes_.rbegin(); }

  const_reverse_iterator crend() const { return bytes_.rend(); }

  const_iterator begin() const { return bytes_.begin(); }

  const_iterator end() const { return bytes_.end(); }

  const_reverse_iterator rbegin() const { return bytes_.rbegin(); }

  const_reverse_iterator rend() const { return bytes_.rend(); }

private:
  // actual data
  std::array<uint8_t, 16> bytes_;
};

// Class that can create new guids. The only reason this exists instead of
// just a global "newGuid" function is because some platforms will require
// that there is some attached context. In the case of android, we need to
// know what JNIEnv is being used to call back to Java, but the newGuid()
// function would no longer be cross-platform if we parameterized the android
// version. Instead, construction of the GuidGenerator may be different on
// each platform, but the use of newGuid is uniform.
class GuidGenerator final {
public:
#ifdef GUID_ANDROID
  GuidGenerator(JNIEnv *env);
#else
  GuidGenerator() { }
#endif

  Guid newGuid();

#ifdef GUID_ANDROID
private:
  JNIEnv *_env;
  jclass _uuidClass;
  jmethodID _newGuidMethod;
  jmethodID _mostSignificantBitsMethod;
  jmethodID _leastSignificantBitsMethod;
#endif
};

}
