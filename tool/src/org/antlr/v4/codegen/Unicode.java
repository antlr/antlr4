/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacterCategory;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.RangeValueIterator;

import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Utility class for calculating {@link IntervalSet}s for various
 * Unicode categories and properties.
 */
public abstract class Unicode {
	private static void addIntervalForCategory(
			Map<String, IntervalSet> categoryMap,
			String categoryName,
			int start,
			int finish) {
		IntervalSet intervalSet = categoryMap.get(categoryName);
		if (intervalSet == null) {
			intervalSet = new IntervalSet();
			categoryMap.put(categoryName, intervalSet);
		}
		intervalSet.add(start, finish);
	}

	private static String getShortPropertyName(int property) {
		String propertyName = UCharacter.getPropertyName(property, UProperty.NameChoice.SHORT);
		// For some reason, a few properties only have long names.
		if (propertyName == null) {
			propertyName = UCharacter.getPropertyName(property, UProperty.NameChoice.LONG);
		}
		return propertyName;
	}

	/**
	 * Returns a map of (Unicode general category code: [0-4, 10-20, 5000-6000], ...)
	 * pairs mapping Unicode general category codes to the {@link IntervalSet} containing
	 * the Unicode code points in that general category.
	 *
	 * Note that a code point belongs to exactly one general category.
	 *
	 * {@see http://unicode.org/reports/tr44/#General_Category_Values}
	 */
	public static Map<String, IntervalSet> getUnicodeCategoryCodesToCodePointRanges() {
		Map<String, IntervalSet> result = new LinkedHashMap<>();
		RangeValueIterator iter = UCharacter.getTypeIterator();
		RangeValueIterator.Element element = new RangeValueIterator.Element();
		while (iter.next(element)) {
			String categoryName = UCharacter.getPropertyValueName(
					UProperty.GENERAL_CATEGORY_MASK,
					1 << element.value,
					UProperty.NameChoice.SHORT);
			addIntervalForCategory(result, categoryName, element.start, element.limit - 1);
			// Add short category so Ll, Lu, Lo, etc. all show up under L
			String shortCategoryName = categoryName.substring(0, 1);
			addIntervalForCategory(result, shortCategoryName, element.start, element.limit - 1);
		}
		return result;
	}

	/**
	 * Returns a map of (Unicode general category code: name, ...) pairs
	 * mapping Unicode general category codes to their human-readable names.
	 *
	 * {@see http://unicode.org/reports/tr44/#General_Category_Values}
	 */
	public static Map<String, String> getUnicodeCategoryCodesToNames() {
		Map<String, String> result = new LinkedHashMap<>();
		RangeValueIterator iter = UCharacter.getTypeIterator();
		RangeValueIterator.Element element = new RangeValueIterator.Element();
		while (iter.next(element)) {
			String categoryName = UCharacter.getPropertyValueName(
					UProperty.GENERAL_CATEGORY_MASK,
					1 << element.value,
					UProperty.NameChoice.SHORT);
			String longCategoryName = UCharacter.getPropertyValueName(
					UProperty.GENERAL_CATEGORY_MASK,
					1 << element.value,
					UProperty.NameChoice.LONG);
			result.put(categoryName, longCategoryName);
		}
		// Add short categories
		result.put("C", "Control");
		result.put("L", "Letter");
		result.put("N", "Number");
		result.put("M", "Mark");
		result.put("P", "Punctuation");
		result.put("S", "Symbol");
		result.put("Z", "Space");
		return result;
	}

	/**
	 * Returns a map of (Unicode binary property code: [0-4, 10-20, 5000-6000], ...)
	 * pairs mapping Unicode binary property codes to the {@link IntervalSet} containing
	 * the Unicode code points which have that binary property set to a true value.
	 *
	 * {@see http://unicode.org/reports/tr44/#Property_List_Table}
	 */
	public static Map<String, IntervalSet> getUnicodeBinaryPropertyCodesToCodePointRanges() {
		Map<String, IntervalSet> result = new LinkedHashMap<>();
		for (int property = UProperty.BINARY_START;
		     property < UProperty.BINARY_LIMIT;
		     property++) {
			String propertyName = getShortPropertyName(property);
			IntervalSet intervalSet = new IntervalSet();
			result.put(propertyName, intervalSet);
			UnicodeSet set = new UnicodeSet();
			set.applyIntPropertyValue(property, 1);
			for (UnicodeSet.EntryRange range : set.ranges()) {
				intervalSet.add(range.codepoint, range.codepointEnd);
			}
		}
		return result;
	}

	/**
	 * Returns a map of (Unicode general category code: name, ...) pairs
	 * mapping Unicode binary property codes to their human-readable names.
	 *
	 * {@see http://unicode.org/reports/tr44/#Property_List_Table}
	 */
	public static Map<String, String> getUnicodeBinaryPropertyCodesToNames() {
		Map<String, String> result = new LinkedHashMap<>();
		for (int property = UProperty.BINARY_START;
		     property < UProperty.BINARY_LIMIT;
		     property++) {
			String propertyName = getShortPropertyName(property);
			String longPropertyName = UCharacter.getPropertyName(property, UProperty.NameChoice.LONG);
			result.put(propertyName, longPropertyName);
		}
		return result;
	}

	/**
	 * Returns a map of (Unicode script code: [0-4, 10-20, 5000-6000], ...)
	 * pairs mapping Unicode script codes to the {@link IntervalSet} containing
	 * the Unicode code points which use that script.
	 *
	 * Note that some code points belong to multiple scripts.
	 *
	 * {@see https://en.wikipedia.org/wiki/Script_(Unicode)#Table_of_scripts_in_Unicode}
	 */
	public static Map<String, IntervalSet> getUnicodeScriptCodesToCodePointRanges() {
		Map<String, IntervalSet> result = new LinkedHashMap<>();
		for (int script = UCharacter.getIntPropertyMinValue(UProperty.SCRIPT);
		     script <= UCharacter.getIntPropertyMaxValue(UProperty.SCRIPT);
		     script++) {
			UnicodeSet set = new UnicodeSet();
			set.applyIntPropertyValue(UProperty.SCRIPT, script);
			String scriptName = UCharacter.getPropertyValueName(UProperty.SCRIPT, script, UProperty.NameChoice.SHORT);
			IntervalSet intervalSet = result.get(scriptName);
			if (intervalSet == null) {
				intervalSet = new IntervalSet();
				result.put(scriptName, intervalSet);
			}
			for (UnicodeSet.EntryRange range : set.ranges()) {
				intervalSet.add(range.codepoint, range.codepointEnd);
			}
		}
		return result;
	}

	/**
	 * Returns a map of (Unicode script code: name, ...) pairs
	 * mapping Unicode script codes to their human-readable names.
	 *
	 * {@see https://en.wikipedia.org/wiki/Script_(Unicode)#Table_of_scripts_in_Unicode}
	 */
	public static Map<String, String> getUnicodeScriptCodesToNames() {
		Map<String, String> result = new LinkedHashMap<>();
		for (int script = UCharacter.getIntPropertyMinValue(UProperty.SCRIPT);
		     script <= UCharacter.getIntPropertyMaxValue(UProperty.SCRIPT);
		     script++) {
			String propertyName = UCharacter.getPropertyValueName(UProperty.SCRIPT, script, UProperty.NameChoice.SHORT);
			String longPropertyName = UCharacter.getPropertyValueName(UProperty.SCRIPT, script, UProperty.NameChoice.LONG);
			result.put(propertyName, longPropertyName);
		}
		return result;
	}
}
