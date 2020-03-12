/*
 * This file is free and unencumbered software released into the public domain.

 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this file, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.

 * In jurisdictions that recognize copyright laws, the author or authors
 * of this file dedicate any and all copyright interest in the
 * file to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * file under copyright law.

 * THE FILE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE FILE OR THE USE OR
 * OTHER DEALINGS IN THE FILE.

 * For more information, please refer to <https://unlicense.org/>
 */

package org.antlr.v4.codegen.inMemoryResult;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.NoSuchElementException;


/** Just a struct storing the results of code generation */
public class InMemoryCodeGenResult implements Iterable<InMemoryFilesWithRole>{
	public ArrayList<InMemoryFile> lexer, parser, listener, baseListener, visitor, baseVisitor, DFAGraph;

	public DataFiles mainData, lexerData;
	public InMemoryCodeGenResult(){
		this.mainData = new DataFiles();
		this.lexerData = new DataFiles();
	}

	public class InMemoryCodeGenResultIterator implements Iterator<InMemoryFilesWithRole>{
		int curPropNo = 0;
		int indexInCurProp = 0;
		public static final int size = 8; // Role.STOP.ordinal();

		private InMemoryCodeGenResult parent;

		public InMemoryCodeGenResultIterator(InMemoryCodeGenResult parent){
			this.parent = parent;
		}

		@Override
		public boolean hasNext() {
			return curPropNo < size;
		}

		public ArrayList<InMemoryFile> dataFilesToArrayList(DataFiles df){
			if(df != null){
				return df.toArrayList();
			} else {
				return null;
			}
		}

		@Override
		public InMemoryFilesWithRole next() throws NoSuchElementException {
			ArrayList<InMemoryFile> curProp = null;

			Role role = Role.values()[curPropNo];
			switch(role){
				case lexer:
					curProp = parent.lexer;
				break;
				case parser:
					curProp = parent.parser;
				break;
				case listener:
					curProp = parent.listener;
				break;
				case visitor:
					curProp = parent.visitor;
				break;
				case baseListener:
					curProp = parent.baseListener;
				break;
				case baseVisitor:
					curProp = parent.baseVisitor;
				break;
				case DFAGraph:
					curProp = parent.DFAGraph;
				break;
				case lexerData:
					curProp = dataFilesToArrayList(parent.lexerData);
				break;
				case mainData:
					curProp = dataFilesToArrayList(parent.mainData);
				break;
				default:
					throw new NoSuchElementException();
			}
			++curPropNo;
			return new InMemoryFilesWithRole(role, curProp);
		}
	}

	@Override
	public Iterator<InMemoryFilesWithRole> iterator(){
		return new InMemoryCodeGenResultIterator(this);
	}

	public void clear(){
		mainData = lexerData = null;
		parser = lexer = listener = baseListener = visitor = baseVisitor = DFAGraph = null;
	}
}
