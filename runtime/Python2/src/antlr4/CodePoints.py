#
# Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
# Use of this file is governed by the BSD 3-clause license that
# can be found in the LICENSE.txt file in the project root.
#

from __future__ import unicode_literals
import sys

def is_leading_surrogate(code_unit):
    return 0xD800 <= code_unit <= 0xDBFF

def is_trailing_surrogate(code_unit):
    return 0xDC00 <= code_unit <= 0xDFFF

def decode_surrogate_pair(leading, trailing):
    return ((leading - 0xD800) << 10) + (trailing - 0xDC00) + 0x10000

def _from_unicode(unistr):
    return (ord(c) for c in unistr)

def _from_utf16(unistr):
    assert sys.maxunicode == 0xFFFF
    leading_surrogate = -1
    for utf16 in unistr:
        code_unit = ord(utf16)
        if leading_surrogate == -1:
            if is_leading_surrogate(code_unit):
                leading_surrogate = code_unit
            else:
                yield code_unit
        else:
            if is_trailing_surrogate(code_unit):
                # Valid surrogate pair
                code_point = decode_surrogate_pair(leading_surrogate, code_unit)
                yield code_point
                leading_surrogate = -1
            else:
                # Leading surrogate without trailing surrogate
                yield leading_surrogate
                if is_leading_surrogate(code_unit):
                    leading_surrogate = code_unit
                else:
                    yield code_point
                    leading_surrogate = -1
    # Dangling surrogate at end of input
    if leading_surrogate != -1:
        yield leading_surrogate

def _to_utf16(code_points):
    for code_point in code_points:
        if code_point <= 0xFFFF:
            yield unichr(code_point)
        else:
            base = code_point - 0x10000
            high_surrogate = (base >> 10) + 0xD800
            low_surrogate = (base & 0x3FF) + 0xDC00
            yield unichr(high_surrogate)
            yield unichr(low_surrogate)

def _to_chars(code_points):
    return (unichr(cp) for cp in code_points)

if sys.maxunicode == 0xFFFF:
    from_unicode = _from_utf16
    to_chars = _to_utf16
else:
    assert sys.maxunicode == 0x10FFFF
    from_unicode = _from_unicode
    to_chars = _to_chars

def to_unicode(code_points):
    return u''.join(to_chars(code_points))
