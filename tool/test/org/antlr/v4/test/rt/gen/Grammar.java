package org.antlr.v4.test.rt.gen;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Grammar {

	public String fileName;
	public String grammarName;
	public String[] lines;
	public ST template;

	public Grammar(String fileName, String grammarName) {
		this.fileName = fileName;
		this.grammarName = grammarName;
	}

	public void load(File grammarDir) throws Exception {
		template = loadGrammar(grammarDir, fileName);
	}

	protected ST loadGrammar(File grammarDir, String grammarFileName) throws Exception {
		File file = new File(grammarDir, grammarFileName + ".st");
		InputStream input = new FileInputStream(file);
		try {
			byte[] data = new byte[(int)file.length()];
			int next = 0;
			while(input.available()>0) {
				int read = input.read(data, next, data.length - next);
				next += read;
			}
			String s = new String(data);
			return new ST(s);
		} finally {
			input.close();
		}
	}

	public void generate(STGroup group) {
		template.add("grammarName", grammarName);
		template.groupThatCreatedThisInstance = group; // so templates get interpreted
		lines = template.render().split("\n");
		for(int i=0;i<lines.length;i++)
			lines[i] = Generator.escape(lines[i]);
	}

}
