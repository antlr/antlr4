/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.states;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.RuntimeRunner;

import java.lang.reflect.InvocationTargetException;

public class JavaCompiledState extends CompiledState {
	public final ClassLoader loader;
	public final Class<? extends Lexer> lexer;
	public final Class<? extends Parser> parser;

	public JavaCompiledState(GeneratedState previousState,
							 ClassLoader loader,
							 Class<? extends Lexer> lexer,
							 Class<? extends Parser> parser,
							 Exception exception
	) {
		super(previousState, exception);
		this.loader = loader;
		this.lexer = lexer;
		this.parser = parser;
	}

	public Pair<Lexer, Parser> initializeDummyLexerAndParser()
			throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		return initializeLexerAndParser("");
	}

	public Pair<Lexer, Parser> initializeLexerAndParser(String input)
			throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Lexer lexer = initializeLexer(input);
		Parser parser = initializeParser(new CommonTokenStream(lexer));
		return new Pair<>(lexer, parser);
	}

	public Lexer initializeLexer(String input)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		CharStream inputString = CharStreams.fromString(input, RuntimeRunner.InputFileName);
		return lexer.getConstructor(CharStream.class).newInstance(inputString);
	}

	public Parser initializeParser(CommonTokenStream tokenStream)
			throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		return parser.getConstructor(TokenStream.class).newInstance(tokenStream);
	}
}
