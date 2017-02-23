/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.unicode;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UCharacterCategory;
import com.ibm.icu.lang.UProperty;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.RangeValueIterator;

import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * StringTemplate controller used to generate parameters to feed
 * to {@code unicodedata.st} to code-generate {@code UnicodeData.java},
 * used by the tool for Unicode property escapes like {@code \\p\{Lu\}}.
 *
 * Uses ICU to iterate over Unicode character categories, properties,
 * and script codes, as well as aliases for those codes.
 *
 * This class exists in its own Maven module to avoid adding a
 * dependency from the tool onto the (large) ICU runtime.
 */
public abstract class UnicodeDataTemplateController {
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

	private static void addPropertyAliases(
			Map<String, String> propertyAliases,
			String propertyName,
			int property) {
		int nameChoice = UProperty.NameChoice.LONG;
		while (true) {
			String alias;
			try {
				alias = UCharacter.getPropertyName(property, nameChoice);
			} catch (IllegalArgumentException e) {
				// No more aliases.
				break;
			}
			assert alias != null;
			addPropertyAlias(propertyAliases, alias, propertyName);
			nameChoice++;
		}
	}

	private static void addPropertyAlias(
			Map<String, String> propertyAliases,
			String alias,
			String propertyName) {
		propertyAliases.put(alias, propertyName);
	}

	public static Map<String, Object> getProperties() {
		Map<String, IntervalSet> propertyCodePointRanges = new LinkedHashMap<>();
		addUnicodeCategoryCodesToCodePointRanges(propertyCodePointRanges);
		addUnicodeBinaryPropertyCodesToCodePointRanges(propertyCodePointRanges);
		addUnicodeScriptCodesToCodePointRanges(propertyCodePointRanges);
		addUnicodeBlocksToCodePointRanges(propertyCodePointRanges);

		Map<String, String> propertyAliases = new LinkedHashMap<>();
		addUnicodeCategoryCodesToNames(propertyAliases);
		addUnicodeBinaryPropertyCodesToNames(propertyAliases);
		addUnicodeScriptCodesToNames(propertyAliases);
		addUnicodeBlocksToNames(propertyAliases);

		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put("propertyCodePointRanges", propertyCodePointRanges);
		properties.put("propertyAliases", propertyAliases);
		return properties;
	}

	private static String getShortPropertyName(int property) {
		String propertyName = UCharacter.getPropertyName(property, UProperty.NameChoice.SHORT);
		// For some reason, a few properties only have long names.
		if (propertyName == null) {
			propertyName = UCharacter.getPropertyName(property, UProperty.NameChoice.LONG);
		}
		return propertyName;
	}

	private static void addUnicodeCategoryCodesToCodePointRanges(Map<String, IntervalSet> propertyCodePointRanges) {
		RangeValueIterator iter = UCharacter.getTypeIterator();
		RangeValueIterator.Element element = new RangeValueIterator.Element();
		while (iter.next(element)) {
			String categoryName = UCharacter.getPropertyValueName(
					UProperty.GENERAL_CATEGORY_MASK,
					1 << element.value,
					UProperty.NameChoice.SHORT);
			addIntervalForCategory(propertyCodePointRanges, categoryName, element.start, element.limit - 1);
			// Add short category so Ll, Lu, Lo, etc. all show up under L
			String shortCategoryName = categoryName.substring(0, 1);
			addIntervalForCategory(propertyCodePointRanges, shortCategoryName, element.start, element.limit - 1);
		}
	}

	private static void addUnicodeCategoryCodesToNames(Map<String, String> propertyAliases) {
		RangeValueIterator iter = UCharacter.getTypeIterator();
		RangeValueIterator.Element element = new RangeValueIterator.Element();
		while (iter.next(element)) {
			int generalCategoryMask = 1 << element.value;
			String categoryName = UCharacter.getPropertyValueName(
					UProperty.GENERAL_CATEGORY_MASK,
					generalCategoryMask,
					UProperty.NameChoice.SHORT);
			int nameChoice = UProperty.NameChoice.LONG;
			while (true) {
				String alias;
				try {
					alias = UCharacter.getPropertyValueName(
							UProperty.GENERAL_CATEGORY_MASK,
							generalCategoryMask,
							nameChoice);
				} catch (IllegalArgumentException e) {
					// No more aliases.
					break;
				}
				assert alias != null;
				addPropertyAlias(propertyAliases, alias, categoryName);
				nameChoice++;
			}
		}
		// Add short categories
		addPropertyAlias(propertyAliases, "Control", "C");
		addPropertyAlias(propertyAliases, "Letter", "L");
		addPropertyAlias(propertyAliases, "Number", "N");
		addPropertyAlias(propertyAliases, "Mark", "M");
		addPropertyAlias(propertyAliases, "Punctuation", "P");
		addPropertyAlias(propertyAliases, "Symbol", "S");
		addPropertyAlias(propertyAliases, "Space", "Z");
	}

	private static void addUnicodeBinaryPropertyCodesToCodePointRanges(Map<String, IntervalSet> propertyCodePointRanges) {
		for (int property = UProperty.BINARY_START;
		     property < UProperty.BINARY_LIMIT;
		     property++) {
			String propertyName = getShortPropertyName(property);
			IntervalSet intervalSet = new IntervalSet();
			UnicodeSet unicodeSet = new UnicodeSet();
			unicodeSet.applyIntPropertyValue(property, 1);
			for (UnicodeSet.EntryRange range : unicodeSet.ranges()) {
				intervalSet.add(range.codepoint, range.codepointEnd);
			}
			propertyCodePointRanges.put(propertyName, intervalSet);
		}
	}

	private static void addUnicodeBinaryPropertyCodesToNames(Map<String, String> propertyAliases) {
		for (int property = UProperty.BINARY_START;
		     property < UProperty.BINARY_LIMIT;
		     property++) {
			String propertyName = getShortPropertyName(property);
			addPropertyAliases(propertyAliases, propertyName, property);
		}
	}

	private static void addIntPropertyRanges(int property, String namePrefix, Map<String, IntervalSet> propertyCodePointRanges) {
		for (int propertyValue = UCharacter.getIntPropertyMinValue(property);
		     propertyValue <= UCharacter.getIntPropertyMaxValue(property);
		     propertyValue++) {
			UnicodeSet set = new UnicodeSet();
			set.applyIntPropertyValue(property, propertyValue);
			String propertyName = namePrefix + UCharacter.getPropertyValueName(property, propertyValue, UProperty.NameChoice.SHORT);
			IntervalSet intervalSet = propertyCodePointRanges.get(propertyName);
			if (intervalSet == null) {
				intervalSet = new IntervalSet();
				propertyCodePointRanges.put(propertyName, intervalSet);
			}
			for (UnicodeSet.EntryRange range : set.ranges()) {
				intervalSet.add(range.codepoint, range.codepointEnd);
			}
		}
	}

	private static void addUnicodeScriptCodesToCodePointRanges(Map<String, IntervalSet> propertyCodePointRanges) {
		addIntPropertyRanges(UProperty.SCRIPT, "", propertyCodePointRanges);
	}

	private static void addUnicodeBlocksToCodePointRanges(Map<String, IntervalSet> propertyCodePointRanges) {
		addIntPropertyRanges(UProperty.BLOCK, "In", propertyCodePointRanges);
	}

	private static void addIntPropertyAliases(int property, String namePrefix, Map<String, String> propertyAliases) {
		for (int propertyValue = UCharacter.getIntPropertyMinValue(property);
		     propertyValue <= UCharacter.getIntPropertyMaxValue(property);
		     propertyValue++) {
			String propertyName = namePrefix + UCharacter.getPropertyValueName(property, propertyValue, UProperty.NameChoice.SHORT);
			int nameChoice = UProperty.NameChoice.LONG;
			String alias;
			while (true) {
				try {
					alias = namePrefix + UCharacter.getPropertyValueName(property, propertyValue, nameChoice);
				} catch (IllegalArgumentException e) {
					// No more aliases.
					break;
				}
				assert alias != null;
				addPropertyAlias(propertyAliases, alias, propertyName);
				nameChoice++;
			}
		}
	}

	private static void addUnicodeScriptCodesToNames(Map<String, String> propertyAliases) {
		addIntPropertyAliases(UProperty.SCRIPT, "", propertyAliases);
	}

	private static void addUnicodeBlocksToNames(Map<String, String> propertyAliases) {
		addIntPropertyAliases(UProperty.BLOCK, "In", propertyAliases);
	}
}
