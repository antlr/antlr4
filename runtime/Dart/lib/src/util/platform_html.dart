import 'dart:html';

/// Creates a new independent [Stream] for the contents of the file on [path].
Stream<List<int>> readStream(String path) => throw UnsupportedError(
    'dart:html does not support read stream from a file');

/// Prints a string representation of the object.
void stdoutWrite(Object? object) => throw UnsupportedError(
    'dart:html does not support write without a newline');

/// Prints a string representation of the [object] to the error stream.
///
/// Each call appends an additional newline to the object's string
/// representation.
void stderrWriteln(Object? object) => window.console.error(object);
