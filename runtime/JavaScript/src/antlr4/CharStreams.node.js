/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import CharStream from "./CharStream.js";
import FileStream from "./FileStream.js";

/**
 * Utility functions to create InputStreams from various sources.
 *
 * All returned InputStreams support the full range of Unicode
 * up to U+10FFFF (the default behavior of InputStream only supports
 * code points up to U+FFFF).
 */
export default {
  // Creates an InputStream from a string.
  fromString: function(str) {
    return new CharStream(str, true);
  },

  /**
   * Asynchronously creates an InputStream from a blob given the
   * encoding of the bytes in that blob (defaults to 'utf8' if
   * encoding is null).
   *
   * Invokes onLoad(result) on success, onError(error) on
   * failure.
   */
  fromBlob: function(blob, encoding, onLoad, onError) {
    const reader = new window.FileReader();
    reader.onload = function(e) {
      const is = new CharStream(e.target.result, true);
      onLoad(is);
    };
    reader.onerror = onError;
    reader.readAsText(blob, encoding);
  },

  /**
   * Creates an InputStream from a Buffer given the
   * encoding of the bytes in that buffer (defaults to 'utf8' if
   * encoding is null).
   */
  fromBuffer: function(buffer, encoding) {
    return new CharStream(buffer.toString(encoding), true);
  },

  /** Asynchronously creates an InputStream from a file on disk given
   * the encoding of the bytes in that file (defaults to 'utf8' if
   * encoding is null).
   *
   * Invokes callback(error, result) on completion.
   */
  fromPath: function(path, encoding, callback) {
    FileStream.fromPath(path, encoding, callback);
  },

  /**
   * Synchronously creates an InputStream given a path to a file
   * on disk and the encoding of the bytes in that file (defaults to
   * 'utf8' if encoding is null).
   */
  fromPathSync: function(path, encoding) {
    return new FileStream(path, encoding);
  }
};
