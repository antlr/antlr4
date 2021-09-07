/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

/// Convert array of strings to string&rarr;index map. Useful for
///  converting rulenames to name&rarr;ruleindex map.
Map<String, int> toMap(List<String> keys) {
  final m = <String, int>{};
  for (var i = 0; i < keys.length; i++) {
    m[keys[i]] = i;
  }
  return m;
}

String arrayToString(a) {
  return '[' + a.join(', ') + ']';
}

String escapeWhitespace(String s, [bool escapeSpaces = false]) {
  if (escapeSpaces) s = s.replaceAll(' ', '\u00B7');
  s = s.replaceAll('\n', r'\n');
  s = s.replaceAll('\r', r'\r');
  s = s.replaceAll('\t', r'\t');
  return s;
}

bool isLowerCase(String s) => s.toLowerCase() == s;

bool isUpperCase(String s) => s.toUpperCase() == s;
