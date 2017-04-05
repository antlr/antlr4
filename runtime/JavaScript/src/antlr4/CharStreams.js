//
/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
//

var InputStream = require('./InputStream').InputStream;

var isNodeJs = typeof window === 'undefined' && typeof importScripts === 'undefined';
var fs = isNodeJs ? require("fs") : null;

// Utility functions to create InputStreams from various sources.
//
// All returned InputStreams support the full range of Unicode
// up to U+10FFFF (the default behavior of InputStream only supports
// code points up to U+FFFF).
var CharStreams = {
  // Creates an InputStream from a string.
  fromString: function(str) {
    return InputStream(str, true);
  },

  // Asynchronously creates an InputStream from a blob given the
  // encoding of the bytes in that blob (defaults to 'utf8' if
  // encoding is null).
  //
  // Invokes onLoad(result) on success, onError(error) on
  // failure.
  fromBlob: function(blob, encoding, onLoad, onError) {
    var reader = FileReader();
    reader.onload = function(e) {
      var is = InputStream(e.target.result, true);
      onLoad(is);
    };
    reader.onerror = onError;
    reader.readAsText(blob, encoding);
  },

  // Creates an InputStream from a Buffer given the
  // encoding of the bytes in that buffer (defaults to 'utf8' if
  // encoding is null).
  fromBuffer: function(buffer, encoding) {
    return InputStream(buffer.toString(encoding), true);
  },

  // Asynchronously creates an InputStream from a file on disk given
  // the encoding of the bytes in that file (defaults to 'utf8' if
  // encoding is null).
  //
  // Invokes callback(error, result) on completion.
  fromPath: function(path, encoding, callback) {
    fs.readFile(path, encoding, function(err, data) {
      var is = null;
      if (data !== null) {
        is = InputStream(data, true);
      }
      callback(err, is);
    });
  },

  // Synchronously creates an InputStream given a path to a file
  // on disk and the encoding of the bytes in that file (defaults to
  // 'utf8' if encoding is null).
  fromPathSync: function(path, encoding) {
    var data = fs.readFileSync(path, encoding);
    return InputStream(data, true);
  }
};

exports.CharStreams = CharStreams;
