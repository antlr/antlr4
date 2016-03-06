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

package org.antlr.v4.runtime.misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogManager {
    protected static class Record {
		long timestamp;
		StackTraceElement location;
		String component;
		String msg;
		public Record() {
			timestamp = System.currentTimeMillis();
			location = new Throwable().getStackTrace()[0];
		}

		@Override
		public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(timestamp)));
            buf.append(" ");
            buf.append(component);
            buf.append(" ");
            buf.append(location.getFileName());
            buf.append(":");
            buf.append(location.getLineNumber());
            buf.append(" ");
            buf.append(msg);
            return buf.toString();
		}
	}

	protected List<Record> records;

	public void log(String component, String msg) {
		Record r = new Record();
		r.component = component;
		r.msg = msg;
		if ( records==null ) {
			records = new ArrayList<Record>();
		}
		records.add(r);
	}

    public void log(String msg) { log(null, msg); }

    public void save(String filename) throws IOException {
        FileWriter fw = new FileWriter(filename);
        BufferedWriter bw = new BufferedWriter(fw);
        try {
            bw.write(toString());
        }
        finally {
            bw.close();
        }
    }

    public String save() throws IOException {
        //String dir = System.getProperty("java.io.tmpdir");
        String dir = ".";
        String defaultFilename =
            dir + "/antlr-" +
            new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date()) + ".log";
        save(defaultFilename);
        return defaultFilename;
    }

    @Override
    public String toString() {
        if ( records==null ) return "";
        String nl = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder();
        for (Record r : records) {
            buf.append(r);
            buf.append(nl);
        }
        return buf.toString();
    }

    public static void main(String[] args) throws IOException {
        LogManager mgr = new LogManager();
        mgr.log("atn", "test msg");
        mgr.log("dfa", "test msg 2");
        System.out.println(mgr);
        mgr.save();
    }
}
