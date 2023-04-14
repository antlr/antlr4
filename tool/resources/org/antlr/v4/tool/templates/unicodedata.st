unicodedata(rawPropertyCodePointRanges, propertyAliases) ::= <<
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
       private static final Map\<String, String\> propertyAliases = new HashMap\<\>(<length(propertyAliases)>);

       // Work around Java 64k bytecode method limit by splitting up static
       // initialization into one method per Unicode property

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

       static private class PropertyAliasesAdder {
<propertyAliases.keys:{ k |
               // Property aliases <i>
               static private void addPropertyAliases<i>() {
                      propertyAliases.put("<k>".toLowerCase(Locale.US), "<propertyAliases.(k)>".toLowerCase(Locale.US));
               \}
}; separator="\n">

               // Property aliases all
               static private void addPropertyAliasesAll() {
<propertyAliases.keys:{ k | PropertyAliasesAdder.addPropertyAliases<i>();}; separator="\n">
               }
       }

       // Property aliases
       static private void addPropertyAliases() {
              PropertyAliasesAdder.addPropertyAliasesAll();
       }

       // Put it all together
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
