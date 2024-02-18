package org.antlr.v4.misc;

import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.Objects;

public class CharParseResult {
	public enum Type {
		INVALID,
		CODE_POINT,
		PROPERTY
	};

	public final Type type;
	public final int codePoint;
	public final IntervalSet propertyIntervalSet;
	public final int startIndex;
	public final int length;

	public static CharParseResult createInvalid(int start, int stop) {
		return new CharParseResult(Type.INVALID, -1, IntervalSet.EMPTY_SET, start, stop - start);
	}

	public static CharParseResult createCodePoint(int codePoint, int start, int stop) {
		return new CharParseResult(Type.CODE_POINT, codePoint, IntervalSet.EMPTY_SET, start, stop - start);
	}

	public static CharParseResult createProperty(IntervalSet set, int start, int stop) {
		return new CharParseResult(Type.PROPERTY, -1, set, start, stop - start);
	}

	private CharParseResult(Type type, int codePoint, IntervalSet propertyIntervalSet, int startIndex, int length) {
		this.type = type;
		this.codePoint = codePoint;
		this.propertyIntervalSet = propertyIntervalSet;
		this.startIndex = Math.max(startIndex, 0);
		this.length = Math.max(length, 0);
	}

	public boolean isCodepoint() {
		return type == Type.CODE_POINT;
	}

	public int getEndIndex() { return startIndex + length; }

	@Override
	public String toString() {
		return String.format(
				"%s type=%s codePoint=%d propertyIntervalSet=%s parseLength=%d",
				super.toString(),
				type,
				codePoint,
				propertyIntervalSet,
				length);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CharParseResult)) {
			return false;
		}
		CharParseResult that = (CharParseResult) other;
		if (this == that) {
			return true;
		}
		return Objects.equals(this.type, that.type) &&
				Objects.equals(this.codePoint, that.codePoint) &&
				Objects.equals(this.propertyIntervalSet, that.propertyIntervalSet) &&
				Objects.equals(this.length, that.length);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, codePoint, propertyIntervalSet, length);
	}
}
