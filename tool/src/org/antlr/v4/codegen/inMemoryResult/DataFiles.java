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

public class DataFiles implements Iterable<InMemoryFile>{
	public InMemoryFile interp, tokens;

	public class DataFilesResultIterator implements Iterator<InMemoryFile>{
		int state = 0;
		public static final int size = 2;

		private DataFiles parent;

		public DataFilesResultIterator(DataFiles parent){
			this.parent = parent;
		}

		@Override
		public boolean hasNext() {
			return state < size;
		}

		@Override
		public InMemoryFile next() throws NoSuchElementException {
			int prevState = state;
			state += 1;
			switch(prevState){
				case 0:
					return parent.interp;
				case 1:
					return parent.tokens;
				default:
					state = prevState;
					throw new NoSuchElementException();
			}
		}
	}

	public ArrayList<InMemoryFile> toArrayList(){
		ArrayList<InMemoryFile> res = new ArrayList<InMemoryFile>();
		for (InMemoryFile el : this){
			res.add(el);
		}
		return res;
	}

	@Override
	public Iterator<InMemoryFile> iterator(){
		return new DataFilesResultIterator(this);
	}

	public void clear(){
		interp = tokens = null;
	}
}
