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
		addUnicodeIntPropertyCodesToCodePointRanges(propertyCodePointRanges);
		addTR35ExtendedPictographicPropertyCodesToCodePointRanges(propertyCodePointRanges);
		addEmojiPresentationPropertyCodesToCodePointRanges(propertyCodePointRanges);

		Map<String, String> propertyAliases = new LinkedHashMap<>();
		addUnicodeCategoryCodesToNames(propertyAliases);
		addUnicodeBinaryPropertyCodesToNames(propertyAliases);
		addUnicodeScriptCodesToNames(propertyAliases);
		addUnicodeBlocksToNames(propertyAliases);
		addUnicodeIntPropertyCodesToNames(propertyAliases);
		propertyAliases.put("EP", "Extended_Pictographic");

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
			addUnicodeSetToIntervalSet(set, intervalSet);
		}
	}

	private static void addUnicodeSetToIntervalSet(UnicodeSet unicodeSet, IntervalSet intervalSet) {
		for (UnicodeSet.EntryRange range : unicodeSet.ranges()) {
			intervalSet.add(range.codepoint, range.codepointEnd);
		}
	}

	private static void addUnicodeIntPropertyCodesToCodePointRanges(Map<String, IntervalSet> propertyCodePointRanges) {
		for (int property = UProperty.INT_START;
		     property < UProperty.INT_LIMIT;
		     property++) {
			String propertyName = getShortPropertyName(property);
			addIntPropertyRanges(property, propertyName + "=", propertyCodePointRanges);
		}
	}

	private static void addTR35ExtendedPictographicPropertyCodesToCodePointRanges(Map<String, IntervalSet> propertyCodePointRanges) {
		IntervalSet set = new IntervalSet();
		// Generated using scripts/parse-extended-pictographic/parse.py
		set.add(0x1F774, 0x1F77F);
		set.add(0x2700, 0x2701);
		set.add(0x2703, 0x2704);
		set.add(0x270E);
		set.add(0x2710, 0x2711);
		set.add(0x2765, 0x2767);
		set.add(0x1F030, 0x1F093);
		set.add(0x1F094, 0x1F09F);
		set.add(0x1F10D, 0x1F10F);
		set.add(0x1F12F);
		set.add(0x1F16C, 0x1F16F);
		set.add(0x1F1AD, 0x1F1E5);
		set.add(0x1F260, 0x1F265);
		set.add(0x1F203, 0x1F20F);
		set.add(0x1F23C, 0x1F23F);
		set.add(0x1F249, 0x1F24F);
		set.add(0x1F252, 0x1F25F);
		set.add(0x1F266, 0x1F2FF);
		set.add(0x1F7D5, 0x1F7FF);
		set.add(0x1F000, 0x1F003);
		set.add(0x1F005, 0x1F02B);
		set.add(0x1F02C, 0x1F02F);
		set.add(0x1F322, 0x1F323);
		set.add(0x1F394, 0x1F395);
		set.add(0x1F398);
		set.add(0x1F39C, 0x1F39D);
		set.add(0x1F3F1, 0x1F3F2);
		set.add(0x1F3F6);
		set.add(0x1F4FE);
		set.add(0x1F53E, 0x1F548);
		set.add(0x1F54F);
		set.add(0x1F568, 0x1F56E);
		set.add(0x1F571, 0x1F572);
		set.add(0x1F57B, 0x1F586);
		set.add(0x1F588, 0x1F589);
		set.add(0x1F58E, 0x1F58F);
		set.add(0x1F591, 0x1F594);
		set.add(0x1F597, 0x1F5A3);
		set.add(0x1F5A6, 0x1F5A7);
		set.add(0x1F5A9, 0x1F5B0);
		set.add(0x1F5B3, 0x1F5BB);
		set.add(0x1F5BD, 0x1F5C1);
		set.add(0x1F5C5, 0x1F5D0);
		set.add(0x1F5D4, 0x1F5DB);
		set.add(0x1F5DF, 0x1F5E0);
		set.add(0x1F5E2);
		set.add(0x1F5E4, 0x1F5E7);
		set.add(0x1F5E9, 0x1F5EE);
		set.add(0x1F5F0, 0x1F5F2);
		set.add(0x1F5F4, 0x1F5F9);
		set.add(0x2605);
		set.add(0x2607, 0x260D);
		set.add(0x260F, 0x2610);
		set.add(0x2612);
		set.add(0x2616, 0x2617);
		set.add(0x2619, 0x261C);
		set.add(0x261E, 0x261F);
		set.add(0x2621);
		set.add(0x2624, 0x2625);
		set.add(0x2627, 0x2629);
		set.add(0x262B, 0x262D);
		set.add(0x2630, 0x2637);
		set.add(0x263B, 0x2647);
		set.add(0x2654, 0x265F);
		set.add(0x2661, 0x2662);
		set.add(0x2664);
		set.add(0x2667);
		set.add(0x2669, 0x267A);
		set.add(0x267C, 0x267E);
		set.add(0x2680, 0x2691);
		set.add(0x2695);
		set.add(0x2698);
		set.add(0x269A);
		set.add(0x269D, 0x269F);
		set.add(0x26A2, 0x26A9);
		set.add(0x26AC, 0x26AF);
		set.add(0x26B2, 0x26BC);
		set.add(0x26BF, 0x26C3);
		set.add(0x26C6, 0x26C7);
		set.add(0x26C9, 0x26CD);
		set.add(0x26D0);
		set.add(0x26D2);
		set.add(0x26D5, 0x26E8);
		set.add(0x26EB, 0x26EF);
		set.add(0x26F6);
		set.add(0x26FB, 0x26FC);
		set.add(0x26FE, 0x26FF);
		set.add(0x2388);
		set.add(0x1FA00, 0x1FFFD);
		set.add(0x1F0A0, 0x1F0AE);
		set.add(0x1F0B1, 0x1F0BF);
		set.add(0x1F0C1, 0x1F0CF);
		set.add(0x1F0D1, 0x1F0F5);
		set.add(0x1F0AF, 0x1F0B0);
		set.add(0x1F0C0);
		set.add(0x1F0D0);
		set.add(0x1F0F6, 0x1F0FF);
		set.add(0x1F80C, 0x1F80F);
		set.add(0x1F848, 0x1F84F);
		set.add(0x1F85A, 0x1F85F);
		set.add(0x1F888, 0x1F88F);
		set.add(0x1F8AE, 0x1F8FF);
		set.add(0x1F900, 0x1F90B);
		set.add(0x1F91F);
		set.add(0x1F928, 0x1F92F);
		set.add(0x1F931, 0x1F932);
		set.add(0x1F94C);
		set.add(0x1F95F, 0x1F96B);
		set.add(0x1F992, 0x1F997);
		set.add(0x1F9D0, 0x1F9E6);
		set.add(0x1F90C, 0x1F90F);
		set.add(0x1F93F);
		set.add(0x1F94D, 0x1F94F);
		set.add(0x1F96C, 0x1F97F);
		set.add(0x1F998, 0x1F9BF);
		set.add(0x1F9C1, 0x1F9CF);
		set.add(0x1F9E7, 0x1F9FF);
		set.add(0x1F6C6, 0x1F6CA);
		set.add(0x1F6D3, 0x1F6D4);
		set.add(0x1F6E6, 0x1F6E8);
		set.add(0x1F6EA);
		set.add(0x1F6F1, 0x1F6F2);
		set.add(0x1F6F7, 0x1F6F8);
		set.add(0x1F6D5, 0x1F6DF);
		set.add(0x1F6ED, 0x1F6EF);
		set.add(0x1F6F9, 0x1F6FF);
		propertyCodePointRanges.put("Extended_Pictographic", set);

		UnicodeSet emojiRKUnicodeSet = new UnicodeSet("[\\p{GCB=Regional_Indicator}\\*#0-9\\u00a9\\u00ae\\u2122\\u3030\\u303d]");
		IntervalSet emojiRKIntervalSet = new IntervalSet();
		addUnicodeSetToIntervalSet(emojiRKUnicodeSet, emojiRKIntervalSet);
		propertyCodePointRanges.put("EmojiRK", emojiRKIntervalSet);

		UnicodeSet emojiNRKUnicodeSet = new UnicodeSet("[\\p{Emoji=Yes}]");
		emojiNRKUnicodeSet.removeAll(emojiRKUnicodeSet);
		IntervalSet emojiNRKIntervalSet = new IntervalSet();
		addUnicodeSetToIntervalSet(emojiNRKUnicodeSet, emojiNRKIntervalSet);
		propertyCodePointRanges.put("EmojiNRK", emojiNRKIntervalSet);
	}

	private static void addEmojiPresentationPropertyCodesToCodePointRanges(Map<String, IntervalSet> propertyCodePointRanges) {
		UnicodeSet emojiDefaultUnicodeSet = new UnicodeSet("[[\\p{Emoji=Yes}]&[\\p{Emoji_Presentation=Yes}]]");
		IntervalSet emojiDefaultIntervalSet = new IntervalSet();
		addUnicodeSetToIntervalSet(emojiDefaultUnicodeSet, emojiDefaultIntervalSet);
		propertyCodePointRanges.put("EmojiPresentation=EmojiDefault", emojiDefaultIntervalSet);

		UnicodeSet textDefaultUnicodeSet = new UnicodeSet("[[\\p{Emoji=Yes}]&[\\p{Emoji_Presentation=No}]]");
		IntervalSet textDefaultIntervalSet = new IntervalSet();
		addUnicodeSetToIntervalSet(textDefaultUnicodeSet, textDefaultIntervalSet);
		propertyCodePointRanges.put("EmojiPresentation=TextDefault", textDefaultIntervalSet);

		UnicodeSet textUnicodeSet = new UnicodeSet("[\\p{Emoji=No}]");
		IntervalSet textIntervalSet = new IntervalSet();
		addUnicodeSetToIntervalSet(textUnicodeSet, textIntervalSet);
		propertyCodePointRanges.put("EmojiPresentation=Text", textIntervalSet);
        }

	private static void addIntPropertyAliases(int property, String namePrefix, Map<String, String> propertyAliases) {
		String propertyName = getShortPropertyName(property);
		for (int propertyValue = UCharacter.getIntPropertyMinValue(property);
		     propertyValue <= UCharacter.getIntPropertyMaxValue(property);
		     propertyValue++) {
			String aliasTarget = propertyName + "=" + UCharacter.getPropertyValueName(property, propertyValue, UProperty.NameChoice.SHORT);
			int nameChoice = UProperty.NameChoice.SHORT;
			String alias;
			while (true) {
				try {
					alias = namePrefix + UCharacter.getPropertyValueName(property, propertyValue, nameChoice);
				} catch (IllegalArgumentException e) {
					// No more aliases.
					break;
				}
				assert alias != null;
				addPropertyAlias(propertyAliases, alias, aliasTarget);
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

	private static void addUnicodeIntPropertyCodesToNames(Map<String, String> propertyAliases) {
		for (int property = UProperty.INT_START;
		     property < UProperty.INT_LIMIT;
		     property++) {
			int nameChoice = UProperty.NameChoice.SHORT + 1;
			while (true) {
				String propertyNameAlias;
				try {
					propertyNameAlias = UCharacter.getPropertyName(property, nameChoice);
				} catch (IllegalArgumentException e) {
					// No more aliases.
					break;
				}
				addIntPropertyAliases(property, propertyNameAlias + "=", propertyAliases);
				nameChoice++;
			}
		}
	}
}
