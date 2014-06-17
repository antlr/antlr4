/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.codegen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Eric Vergnaud
 */
public class Python3Target extends AbstractPythonTarget {

	protected static final String[] python3Keywords = {
		"abs", "all", "any", "apply", "as", 
		"bin", "bool", "buffer", "bytearray", 
		"callable", "chr", "classmethod", "coerce", "compile", "complex", 
		"delattr", "dict", "dir", "divmod", 
		"enumerate", "eval", "execfile",
		"file", "filter", "float", "format", "frozenset", 
		"getattr", "globals", 
		"hasattr", "hash", "help", "hex", 
		"id", "input", "int", "intern", "isinstance", "issubclass", "iter", 
		"len", "list", "locals", 
		"map", "max", "min", "next", 
		"memoryview", 
		"object", "oct", "open", "ord", 
		"pow", "print", "property", 
		"range", "raw_input", "reduce", "reload", "repr", "reversed", "round", 
		"set", "setattr", "slice", "sorted", "staticmethod", "str", "sum", "super",
		"tuple", "type", 
		"unichr", "unicode",
		"vars",
		"with",
		"zip",
		"__import__",
		"True", "False", "None"
	};

	@Override
	public String getVersion() {
		return "4.4";
	}

	/** Avoid grammar symbols in this set to prevent conflicts in gen'd code. */
	protected final Set<String> badWords = new HashSet<String>();

	public Python3Target(CodeGenerator gen) {
		super(gen, "Python3");
	}

	public Set<String> getBadWords() {
		if (badWords.isEmpty()) {
			addBadWords();
		}

		return badWords;
	}

	protected void addBadWords() {
		badWords.addAll(Arrays.asList(python3Keywords));
		badWords.add("rule");
		badWords.add("parserRule");
	}


}
