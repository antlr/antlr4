import 'dart:io';

Stream<List<int>> readStream(String path) => File(path).openRead();
