/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

library antlr4;

export 'src/atn/atn.dart';
export 'src/dfa/dfa.dart';
export 'src/tree/tree.dart';
export 'src/error/error.dart';
export 'src/rule_context.dart';
export 'src/input_stream.dart';
export 'src/token_stream.dart';
export 'src/lexer.dart';
export 'src/parser.dart';
export 'src/parser_rule_context.dart';
export 'src/vocabulary.dart';
export 'src/runtime_meta_data.dart';
export 'src/token.dart';
export 'src/prediction_context.dart';
export 'src/recognizer.dart';
export 'src/interval_set.dart';

import 'src/util/platform_stub.dart'
    if (dart.library.io) 'src/util/platform_io.dart';

/// Hack to workaround not being able to access stdout in tests.
void TEST_platformStdoutWrite(Object? object) => stdoutWrite(object);
