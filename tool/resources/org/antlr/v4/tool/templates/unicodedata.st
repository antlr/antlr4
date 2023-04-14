unicodedata(rawPropertyCodePointRanges, rawPropertyAliases) ::= <<
package org.antlr.v4.unicode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Interval;

/**
 * Code-generated utility class mapping Unicode properties to Unicode code point ranges.
 */
public abstract class UnicodeData {
	private static final Map\<String, IntervalSet\> propertyCodePointRanges = new HashMap\<\>(<length(rawPropertyCodePointRanges)>);
	private static final Map\<String, String\> propertyAliases = new HashMap\<\>(<length(rawPropertyAliases)> / 2);

	private static void addProperty(String propertyName, int[] rawIntervals) {
		List\<Interval> intervals = new ArrayList\<>(rawIntervals.length / 2);
		for (int i = 0; i \< rawIntervals.length; i += 2) {
			intervals.add(new Interval(rawIntervals[i], rawIntervals[i + 1]));
		}
		IntervalSet result = new IntervalSet(intervals);
		result.setReadonly(true);
		propertyCodePointRanges.put(propertyName, result);
	}

	<rawPropertyCodePointRanges.keys:{ k |
static private void addProperty<i>() { addProperty("<k>", new int[] { <rawPropertyCodePointRanges.(k):{j | <j>}; separator=",", wrap> \}); \}}; separator="\n">

	static private void addPropertyAliases() {
		String[] rawAliases = new String[] { <rawPropertyAliases:{k | "<k>"}; separator=",", wrap> };
		for (int i = 0; i \< rawAliases.length; i += 2) {
			propertyAliases.put(rawAliases[i], rawAliases[i + 1]);
		}
	}

	static {
		<rawPropertyCodePointRanges.keys:{ k | addProperty<i>();}; separator="\n">
		addPropertyAliases();
	}

	private static String normalize(String propertyCodeOrAlias) {
		return propertyCodeOrAlias.toLowerCase(Locale.US).replace('-', '_');
	}

	/**
	 * Given a Unicode property (general category code, binary property name, or script name),
	 * returns the {@link IntervalSet} of Unicode code point ranges which have that property.
	 */
	public static IntervalSet getPropertyCodePoints(String propertyCodeOrAlias) {
		String normalizedPropertyCodeOrAlias = normalize(propertyCodeOrAlias);
		IntervalSet result = propertyCodePointRanges.get(normalizedPropertyCodeOrAlias);
		if (result == null) {
			String propertyCode = propertyAliases.get(normalizedPropertyCodeOrAlias);
			result = propertyCodePointRanges.get(propertyCode);
		}
		return result;
	}
}
>>
