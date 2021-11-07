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

#include <algorithm>

#include "Guid.h"

#ifdef GUID_LIBUUID
#include <uuid/uuid.h>
#endif

#ifdef GUID_CFUUID
#include <CoreFoundation/CFUUID.h>
#endif

#ifdef GUID_WINDOWS
#include <objbase.h>
#endif

#ifdef GUID_ANDROID
#include <jni.h>
#endif

namespace antlrcpp {

// overload << so that it's easy to convert to a string
std::ostream &operator<<(std::ostream &s, const Guid &guid) {
  return s << std::hex << std::setfill('0')
    << std::setw(2) << static_cast<int>(guid.bytes_[0])
    << std::setw(2) << static_cast<int>(guid.bytes_[1])
    << std::setw(2) << static_cast<int>(guid.bytes_[2])
    << std::setw(2) << static_cast<int>(guid.bytes_[3])
    << "-"
    << std::setw(2) << static_cast<int>(guid.bytes_[4])
    << std::setw(2) << static_cast<int>(guid.bytes_[5])
    << "-"
    << std::setw(2) << static_cast<int>(guid.bytes_[6])
    << std::setw(2) << static_cast<int>(guid.bytes_[7])
    << "-"
    << std::setw(2) << static_cast<int>(guid.bytes_[8])
    << std::setw(2) << static_cast<int>(guid.bytes_[9])
    << "-"
    << std::setw(2) << static_cast<int>(guid.bytes_[10])
    << std::setw(2) << static_cast<int>(guid.bytes_[11])
    << std::setw(2) << static_cast<int>(guid.bytes_[12])
    << std::setw(2) << static_cast<int>(guid.bytes_[13])
    << std::setw(2) << static_cast<int>(guid.bytes_[14])
    << std::setw(2) << static_cast<int>(guid.bytes_[15]);
}

// create a guid from vector of bytes
Guid::Guid(const std::vector<uint8_t> &bytes) {
  std::memcpy(bytes_.data(), bytes.data(), std::min(bytes.size(), bytes_.size()));
}

Guid::Guid(const std::array<uint8_t, 16> &bytes) : bytes_(bytes) {}

// create a guid from array of bytes
Guid::Guid(const uint8_t *bytes) {
  std::memcpy(bytes_.data(), bytes, 16);
}

// create a guid from array of words
Guid::Guid(const uint16_t *bytes, bool reverse) {
  size_t j = 0;
  if (reverse) {
    for (size_t i = 8; i > 0; --i) {
      bytes_[j++] = static_cast<uint8_t>(bytes[i - 1] >> 8);
      bytes_[j++] = static_cast<uint8_t>(bytes[i - 1] & 0xFF);
    }
  } else {
    for (size_t i = 0; i < 8; ++i) {
      bytes_[j++] = static_cast<uint8_t>(bytes[i] & 0xFF);
      bytes_[j++] = static_cast<uint8_t>(bytes[i] >> 8);
    }
  }
}

namespace {

// converts a single hex char to a number (0 - 15)
uint8_t hexDigitToChar(char ch) {
  if (ch > 47 && ch < 58)
    return (uint8_t)(ch - 48);

  if (ch > 96 && ch < 103)
    return (uint8_t)(ch - 87);

  if (ch > 64 && ch < 71)
    return (uint8_t)(ch - 55);

  return 0;
}

// converts the two hexadecimal characters to an unsigned char (a byte)
uint8_t hexPairToChar(char a, char b) {
  return hexDigitToChar(a) * 16 + hexDigitToChar(b);
}

}

// create a guid from string
Guid::Guid(const std::string &fromString) {
  char charOne = 0, charTwo;
  bool lookingForFirstChar = true;

  size_t i = 0;
  for (const char &ch : fromString)
  {
    if (ch == '-')
      continue;

    if (lookingForFirstChar)
    {
      charOne = ch;
      lookingForFirstChar = false;
    }
    else
    {
      charTwo = ch;
      bytes_[i++] = hexPairToChar(charOne, charTwo);
      lookingForFirstChar = true;
    }
  }
}

std::string Guid::toString() const {
  std::stringstream os;
  os << *this;
  return os.str();
}

// This is the linux friendly implementation, but it could work on other
// systems that have libuuid available
#ifdef GUID_LIBUUID
Guid GuidGenerator::newGuid()
{
  uuid_t id;
  uuid_generate(id);
  return id;
}
#endif

// this is the mac and ios version
#ifdef GUID_CFUUID
Guid GuidGenerator::newGuid()
{
  auto newId = CFUUIDCreate(NULL);
  auto bytes = CFUUIDGetUUIDBytes(newId);
  CFRelease(newId);

  const unsigned char byteArray[16] =
  {
    bytes.byte0,
    bytes.byte1,
    bytes.byte2,
    bytes.byte3,
    bytes.byte4,
    bytes.byte5,
    bytes.byte6,
    bytes.byte7,
    bytes.byte8,
    bytes.byte9,
    bytes.byte10,
    bytes.byte11,
    bytes.byte12,
    bytes.byte13,
    bytes.byte14,
    bytes.byte15
  };
  return byteArray;
}
#endif

// obviously this is the windows version
#ifdef GUID_WINDOWS
Guid GuidGenerator::newGuid()
{
  GUID newId;
  CoCreateGuid(&newId);

  const unsigned char bytes[16] =
  {
    (newId.Data1 >> 24) & 0xFF,
    (newId.Data1 >> 16) & 0xFF,
    (newId.Data1 >> 8) & 0xFF,
    (newId.Data1) & 0xff,

    (newId.Data2 >> 8) & 0xFF,
    (newId.Data2) & 0xff,

    (newId.Data3 >> 8) & 0xFF,
    (newId.Data3) & 0xFF,

    newId.Data4[0],
    newId.Data4[1],
    newId.Data4[2],
    newId.Data4[3],
    newId.Data4[4],
    newId.Data4[5],
    newId.Data4[6],
    newId.Data4[7]
  };

  return bytes;
}
#endif

// android version that uses a call to a java api
#ifdef GUID_ANDROID
GuidGenerator::GuidGenerator(JNIEnv *env)
{
  _env = env;
  _uuidClass = env->FindClass("java/util/UUID");
  _newGuidMethod = env->GetStaticMethodID(_uuidClass, "randomUUID", "()Ljava/util/UUID;");
  _mostSignificantBitsMethod = env->GetMethodID(_uuidClass, "getMostSignificantBits", "()J");
  _leastSignificantBitsMethod = env->GetMethodID(_uuidClass, "getLeastSignificantBits", "()J");
}

Guid GuidGenerator::newGuid()
{
  jobject javaUuid = _env->CallStaticObjectMethod(_uuidClass, _newGuidMethod);
  jlong mostSignificant = _env->CallLongMethod(javaUuid, _mostSignificantBitsMethod);
  jlong leastSignificant = _env->CallLongMethod(javaUuid, _leastSignificantBitsMethod);

  unsigned char bytes[16] =
  {
    (mostSignificant >> 56) & 0xFF,
    (mostSignificant >> 48) & 0xFF,
    (mostSignificant >> 40) & 0xFF,
    (mostSignificant >> 32) & 0xFF,
    (mostSignificant >> 24) & 0xFF,
    (mostSignificant >> 16) & 0xFF,
    (mostSignificant >> 8) & 0xFF,
    (mostSignificant) & 0xFF,
    (leastSignificant >> 56) & 0xFF,
    (leastSignificant >> 48) & 0xFF,
    (leastSignificant >> 40) & 0xFF,
    (leastSignificant >> 32) & 0xFF,
    (leastSignificant >> 24) & 0xFF,
    (leastSignificant >> 16) & 0xFF,
    (leastSignificant >> 8) & 0xFF,
    (leastSignificant) & 0xFF,
  };
  return bytes;
}
#endif

}
