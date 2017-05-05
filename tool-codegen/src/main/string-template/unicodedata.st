unicodedata(propertyCodePointRanges, propertyAliases) ::= <<
package org.antlr.v4.unicode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Interval;

/**
 * Code-generated utility class mapping Unicode properties to Unicode code point ranges.
 */
public abstract class UnicodeData {
       private static final Map\<String, IntervalSet\> propertyCodePointRanges = new HashMap\<\>(<length(propertyCodePointRanges)>);
       private static final Map\<String, String\> propertyAliases = new HashMap\<\>(<length(propertyAliases)>);

       // Work around Java 64k bytecode method limit by splitting up static
       // initialization into one method per Unicode property

       <propertyCodePointRanges.keys:{ k | // Unicode code points with property "<k>"
static private void addProperty<i>() {
       List\<Interval\> intervals = Arrays.asList(
               <propertyCodePointRanges.(k).intervals:{ interval | Interval.of(<interval.a>, <interval.b>)}; separator=",\n">
       );
       IntervalSet codePointRanges = new IntervalSet(intervals);
       codePointRanges.setReadonly(true);
       propertyCodePointRanges.put("<k>".toLowerCase(Locale.US), codePointRanges);
\}}; separator="\n\n">

       // Property aliases
       static private void addPropertyAliases() {
              <propertyAliases.keys:{ k | propertyAliases.put("<k>".toLowerCase(Locale.US), "<propertyAliases.(k)>".toLowerCase(Locale.US)); }; separator="\n">
       }

       // Put it all together
       static {
              <propertyCodePointRanges.keys:{ k | addProperty<i>(); }; separator="\n">
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
