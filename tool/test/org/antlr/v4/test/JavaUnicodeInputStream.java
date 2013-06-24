/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;

/**
 *
 * @author Sam Harwell
 */
public class JavaUnicodeInputStream implements CharStream {
	@NotNull
	private final CharStream source;
	private final IntegerList escapeIndexes = new IntegerList();
	private final IntegerList escapeCharacters = new IntegerList();
	private final IntegerList escapeIndirectionLevels = new IntegerList();

	private int escapeListIndex;
	private int range;
	private int slashCount;

	private int la1;

	public JavaUnicodeInputStream(@NotNull CharStream source) {
		if (source == null) {
			throw new NullPointerException("source");
		}

		this.source = source;
		this.la1 = source.LA(1);
	}

	@Override
	public int size() {
		return source.size();
	}

	@Override
	public int index() {
		return source.index();
	}

	@Override
	public String getSourceName() {
		return source.getSourceName();
	}

	@Override
	public String getText(Interval interval) {
		return source.getText(interval);
	}

	@Override
	public void consume() {
		if (la1 != '\\') {
			source.consume();
			la1 = source.LA(1);
			range = Math.max(range, source.index());
			slashCount = 0;
			return;
		}

		// make sure the next character has been processed
		this.LA(1);

		if (escapeListIndex >= escapeIndexes.size() || escapeIndexes.get(escapeListIndex) != index()) {
			source.consume();
			slashCount++;
		}
		else {
			int indirectionLevel = escapeIndirectionLevels.get(escapeListIndex);
			for (int i = 0; i < 6 + indirectionLevel; i++) {
				source.consume();
			}

			escapeListIndex++;
			slashCount = 0;
		}

		la1 = source.LA(1);
		assert range >= index();
	}

	@Override
	public int LA(int i) {
		if (i == 1 && la1 != '\\') {
			return la1;
		}

		if (i <= 0) {
			int desiredIndex = index() + i;
			for (int j = escapeListIndex - 1; j >= 0; j--) {
				if (escapeIndexes.get(j) + 6 + escapeIndirectionLevels.get(j) > desiredIndex) {
					desiredIndex -= 5 + escapeIndirectionLevels.get(j);
				}

				if (escapeIndexes.get(j) == desiredIndex) {
					return escapeCharacters.get(j);
				}
			}

			return source.LA(desiredIndex - index());
		}
		else {
			int desiredIndex = index() + i - 1;
			for (int j = escapeListIndex; j < escapeIndexes.size(); j++) {
				if (escapeIndexes.get(j) == desiredIndex) {
					return escapeCharacters.get(j);
				}
				else if (escapeIndexes.get(j) < desiredIndex) {
					desiredIndex += 5 + escapeIndirectionLevels.get(j);
				}
				else {
					return source.LA(desiredIndex - index() + 1);
				}
			}

			int[] currentIndex = { index() };
			int[] slashCountPtr = { slashCount };
			int[] indirectionLevelPtr = { 0 };
			for (int j = 0; j < i; j++) {
				int previousIndex = currentIndex[0];
				int c = readCharAt(currentIndex, slashCountPtr, indirectionLevelPtr);
				if (currentIndex[0] > range) {
					if (currentIndex[0] - previousIndex > 1) {
						escapeIndexes.add(previousIndex);
						escapeCharacters.add(c);
						escapeIndirectionLevels.add(indirectionLevelPtr[0]);
					}

					range = currentIndex[0];
				}

				if (j == i - 1) {
					return c;
				}
			}

			throw new IllegalStateException("shouldn't be reachable");
		}
	}

	@Override
	public int mark() {
		return source.mark();
	}

	@Override
	public void release(int marker) {
		source.release(marker);
	}

	@Override
	public void seek(int index) {
		if (index > range) {
			throw new UnsupportedOperationException();
		}

		source.seek(index);
		la1 = source.LA(1);

		slashCount = 0;
		while (source.LA(-slashCount - 1) == '\\') {
			slashCount++;
		}

		escapeListIndex = escapeIndexes.binarySearch(source.index());
		if (escapeListIndex < 0) {
			escapeListIndex = -escapeListIndex - 1;
		}
	}

	private static boolean isHexDigit(int c) {
		return c >= '0' && c <= '9'
			|| c >= 'a' && c <= 'f'
			|| c >= 'A' && c <= 'F';
	}

	private static int hexValue(int c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		}

		if (c >= 'a' && c <= 'f') {
			return c - 'a' + 10;
		}

		if (c >= 'A' && c <= 'F') {
			return c - 'A' + 10;
		}

		throw new IllegalArgumentException("c");
	}

	private int readCharAt(int[] nextIndexPtr, int[] slashCountPtr, int[] indirectionLevelPtr) {
		assert nextIndexPtr != null && nextIndexPtr.length == 1;
		assert slashCountPtr != null && slashCountPtr.length == 1;
		assert indirectionLevelPtr != null && indirectionLevelPtr.length == 1;

		boolean blockUnicodeEscape = (slashCountPtr[0] % 2) != 0;

		int c0 = source.LA(nextIndexPtr[0] - index() + 1);
		if (c0 == '\\') {
			slashCountPtr[0]++;

			if (!blockUnicodeEscape) {
				int c1 = source.LA(nextIndexPtr[0] - index() + 2);
				if (c1 == 'u') {
					int c2 = source.LA(nextIndexPtr[0] - index() + 3);
					indirectionLevelPtr[0] = 0;
					while (c2 == 'u') {
						indirectionLevelPtr[0]++;
						c2 = source.LA(nextIndexPtr[0] - index() + 3 + indirectionLevelPtr[0]);
					}

					int c3 = source.LA(nextIndexPtr[0] - index() + 4 + indirectionLevelPtr[0]);
					int c4 = source.LA(nextIndexPtr[0] - index() + 5 + indirectionLevelPtr[0]);
					int c5 = source.LA(nextIndexPtr[0] - index() + 6 + indirectionLevelPtr[0]);
					if (isHexDigit(c2) && isHexDigit(c3) && isHexDigit(c4) && isHexDigit(c5)) {
						int value = hexValue(c2);
						value = (value << 4) + hexValue(c3);
						value = (value << 4) + hexValue(c4);
						value = (value << 4) + hexValue(c5);

						nextIndexPtr[0] += 6 + indirectionLevelPtr[0];
						slashCountPtr[0] = 0;
						return value;
					}
				}
			}
		}

		nextIndexPtr[0]++;
		return c0;
	}
}
