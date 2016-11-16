package org.antlr.v4.test.runtime;

/** This interface acts like a tag on a Base*Test class that wants
 *  to use its own assertEquals() instead of jUnit's.
 */
public interface RuntimeTestAssert {
	void assertEqualStrings(String expected, String actual);
}
