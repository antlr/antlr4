package org.antlr.v4.test.tool;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.ast.GrammarAST;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestUtils {
	@Test
	public void testStripFileExtension() {
		Assert.assertNull(Utils.stripFileExtension(null));
		Assert.assertEquals("foo", Utils.stripFileExtension("foo"));
		Assert.assertEquals("foo", Utils.stripFileExtension("foo.txt"));
	}

	@Test
	public void testJoin() {
		Assert.assertEquals("foobbar",
			Utils.join(new String[]{"foo", "bar"}, "b"));
		Assert.assertEquals("foo,bar",
			Utils.join(new String[]{"foo", "bar"}, ","));
	}

	@Test
	public void testSortLinesInString() {
		Assert.assertEquals("bar\nbaz\nfoo\n",
			Utils.sortLinesInString("foo\nbar\nbaz"));
	}

	@Test
	public void testNodesToStrings() {
		ArrayList<GrammarAST> values = new ArrayList<>();
		values.add(new GrammarAST(Token.EOR_TOKEN_TYPE));
		values.add(new GrammarAST(Token.DOWN));
		values.add(new GrammarAST(Token.UP));

		Assert.assertNull(Utils.nodesToStrings(null));
		Assert.assertNotNull(Utils.nodesToStrings(values));
	}

	@Test
	public void testCapitalize() {
		Assert.assertEquals("Foo", Utils.capitalize("foo"));
	}

	@Test
	public void testDecapitalize() {
		Assert.assertEquals("fOO", Utils.decapitalize("FOO"));
	}

	@Test
	public void testSelect() {
		ArrayList<String> strings = new ArrayList<>();
		strings.add("foo");
		strings.add("bar");

		Utils.Func1<String, String> func1 = new Utils.Func1() {
			@Override
			public Object exec(Object arg1) {
				return "baz";
			}
		};

		ArrayList<String> retval = new ArrayList<>();
		retval.add("baz");
		retval.add("baz");

		Assert.assertEquals(retval, Utils.select(strings, func1));
		Assert.assertNull(Utils.select(null, null));
	}

	@Test
	public void testFind() {
		ArrayList<String> strings = new ArrayList<>();
		strings.add("foo");
		strings.add("bar");
		Assert.assertEquals("foo", Utils.find(strings, String.class));

		Assert.assertNull(Utils.find(new ArrayList<>(), String.class));
	}

	@Test
	public void testIndexOf() {
		ArrayList<String> strings = new ArrayList<>();
		strings.add("foo");
		strings.add("bar");
		Utils.Filter filter = new Utils.Filter() {
			@Override
			public boolean select(Object o) {
				return true;
			}
		};
		Assert.assertEquals(0, Utils.indexOf(strings, filter));
		Assert.assertEquals(-1, Utils.indexOf(new ArrayList<>(), null));
	}

	@Test
	public void testLastIndexOf() {
		ArrayList<String> strings = new ArrayList<>();
		strings.add("foo");
		strings.add("bar");
		Utils.Filter filter = new Utils.Filter() {
			@Override
			public boolean select(Object o) {
				return true;
			}
		};
		Assert.assertEquals(1, Utils.lastIndexOf(strings, filter));
		Assert.assertEquals(-1, Utils.lastIndexOf(new ArrayList<>(), null));
	}

	@Test
	public void testSetSize() {
		ArrayList<String> strings = new ArrayList<>();
		strings.add("foo");
		strings.add("bar");
		strings.add("baz");
		Assert.assertEquals(3, strings.size());

		Utils.setSize(strings, 2);
		Assert.assertEquals(2, strings.size());

		Utils.setSize(strings, 4);
		Assert.assertEquals(4, strings.size());
	}
}
