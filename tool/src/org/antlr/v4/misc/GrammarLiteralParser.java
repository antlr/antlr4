/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.misc;

import org.antlr.v4.runtime.misc.CharSupport;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.unicode.UnicodeData;

/**
 * Utility class to parse escapes like:
 *   A
 *   \\n
 *   \\uABCD
 *   \\u{10ABCD}
 *   \\p{Foo}
 *   \\P{Bar}
 *   \\p{Baz=Blech}
 *   \\P{Baz=Blech}
 */
public final class GrammarLiteralParser {
	public static String parseStringFromStringLiteral(String s) {
		StringBuilder buf = new StringBuilder(s.length());
		int index = 1;
		int endIndex = s.length() - 1;
		while (index < endIndex) {
			CharParseResult escapeParseResult = parseNextChar(s, index, endIndex, true);
			if (!escapeParseResult.isCodepoint()) {
				return null;
			}
			buf.appendCodePoint(escapeParseResult.codePoint);
			index += escapeParseResult.length;
		}
		return buf.toString();
	}

	public static CharParseResult parseCharFromStringLiteral(String s) {
		return parseChar(s, true, true);
	}

	/** Given a literal like (the 3 char sequence with single quotes) 'a',
	 *  return the int value of 'a'. Convert escape sequences here also.
	 *  Return INVALID if not single char.
	 */
	public static CharParseResult parseChar(String s, boolean isStringLiteral, boolean isQuoted) {
		if (s == null) {
			return CharParseResult.createInvalid(0, 0);
		}

		int startIndex, endIndex;
		if (isQuoted) {
			startIndex = 1;
			endIndex = s.length() - 1;
		} else {
			startIndex = 0;
			endIndex = s.length();
		}
		CharParseResult result = parseNextChar(s, startIndex, endIndex, isStringLiteral);
		if (result.type == CharParseResult.Type.INVALID) {
			return result;
		}
		// Disallow multiple chars
		if (result.startIndex + result.length != endIndex) {
			return CharParseResult.createInvalid(result.startIndex, result.startIndex + result.length);
		}
		return result;
	}

	/**
	 * Parses a single char or escape sequence (x or \\t or \\u1234) starting at {@code startIndex}.
	 * Returns a type of INVALID if no valid char or escape sequence were found, a Result otherwise.
	 */
	public static CharParseResult parseNextChar(String s, int startIndex, int endIndex, boolean isStringLiteral) {
		int offset = startIndex;
		if (offset + 1 > endIndex) {
			return CharParseResult.createInvalid(startIndex, endIndex);
		}

		int firstCodePoint = s.codePointAt(offset);
		if (firstCodePoint != '\\') {
			offset += Character.charCount(firstCodePoint);
			return CharParseResult.createCodePoint(firstCodePoint, startIndex, offset);
		}

		// Move past backslash
		offset++;
		if (offset + 1 > endIndex) {
			return CharParseResult.createInvalid(startIndex, endIndex);
		}
		int escaped = s.codePointAt(offset);
		// Move past escaped code point
		offset += Character.charCount(escaped);
		if (escaped == 'u') {
			// \\u{1} is the shortest we support
			if (offset + 3 > endIndex) {
				return CharParseResult.createInvalid(startIndex, endIndex);
			}
			int hexStartOffset;
			int hexEndOffset; // appears to be exclusive
			if (s.codePointAt(offset) == '{') {
				hexStartOffset = offset + 1;
				hexEndOffset = s.indexOf('}', hexStartOffset);
				if (hexEndOffset == -1 || hexEndOffset >= endIndex) {
					return CharParseResult.createInvalid(startIndex, endIndex);
				}
				offset = hexEndOffset + 1;
			}
			else {
				if (offset + 4 > endIndex) {
					return CharParseResult.createInvalid(startIndex, endIndex);
				}
				hexStartOffset = offset;
				hexEndOffset = offset + 4;
				offset = hexEndOffset;
			}
			int codePointValue = parseHexValue(s, hexStartOffset, hexEndOffset);
			if (codePointValue == -1 || codePointValue > Character.MAX_CODE_POINT) {
				return CharParseResult.createInvalid(startIndex, Math.min(startIndex + 6, endIndex));
			}
			return CharParseResult.createCodePoint(codePointValue, startIndex, offset);
		}
		else if (escaped == 'p' || escaped == 'P') {
			// \p{L} is the shortest we support
			if (offset + 3 > endIndex) {
				return CharParseResult.createInvalid(startIndex, endIndex);
			}
			if (s.codePointAt(offset) != '{') {
				return CharParseResult.createInvalid(startIndex, offset);
			}
			int openBraceOffset = offset;
			int closeBraceOffset = s.indexOf('}', openBraceOffset);
			if (closeBraceOffset == -1 || closeBraceOffset >= endIndex) {
				return CharParseResult.createInvalid(startIndex, endIndex);
			}
			String propertyName = s.substring(openBraceOffset + 1, closeBraceOffset);
			IntervalSet propertyIntervalSet = UnicodeData.getPropertyCodePoints(propertyName);
			offset = closeBraceOffset + 1;
			if (propertyIntervalSet == null || propertyIntervalSet.isNil()) {
				return CharParseResult.createInvalid(startIndex, offset);
			}
			if (escaped == 'P') {
				propertyIntervalSet = propertyIntervalSet.complement(IntervalSet.COMPLETE_CHAR_SET);
			}
			return CharParseResult.createProperty(propertyIntervalSet, startIndex, offset);
		}
		else {
			Character codePoint = CharSupport.EscapedCharValue.get((char) escaped);
			if (codePoint == null) {
				boolean isEscapedChar;
				if (isStringLiteral) {
					isEscapedChar = escaped == '\'';
				}
				else {
					isEscapedChar = escaped == ']' || escaped == '-';
				}

				if (isEscapedChar) {
					codePoint = (char) escaped;
				}
				else {
					return CharParseResult.createInvalid(startIndex, offset);
				}
			}
			return CharParseResult.createCodePoint(codePoint, startIndex, offset);
		}
	}

	public static int parseHexValue(String s, int startIndex, int endIndex) {
		try {
			return Integer.parseInt(s, startIndex, endIndex, 16);
		}
		catch (Exception e) {
			return -1;
		}
	}
}
