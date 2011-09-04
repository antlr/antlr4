/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime.debug;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TreeAdaptor;

import java.io.*;
import java.net.*;

/** A proxy debug event listener that forwards events over a socket to
 *  a debugger (or any other listener) using a simple text-based protocol;
 *  one event per line.  ANTLRWorks listens on server socket with a
 *  RemoteDebugEventSocketListener instance.  These two objects must therefore
 *  be kept in sync.  New events must be handled on both sides of socket.
 */
public class DebugEventSocketProxy extends BlankDebugEventListener {
	public static final int DEFAULT_DEBUGGER_PORT = 49100; // was 49153
	protected int port = DEFAULT_DEBUGGER_PORT;
	protected ServerSocket serverSocket;
	protected Socket socket;
	protected String grammarFileName;
	protected PrintWriter out;
	protected BufferedReader in;

	/** Who am i debugging? */
	protected BaseRecognizer recognizer;

	/** Almost certainly the recognizer will have adaptor set, but
	 *  we don't know how to cast it (Parser or TreeParser) to get
	 *  the adaptor field.  Must be set with a constructor. :(
	 */
	protected TreeAdaptor adaptor;

	public DebugEventSocketProxy(BaseRecognizer recognizer, TreeAdaptor adaptor) {
		this(recognizer, DEFAULT_DEBUGGER_PORT, adaptor);
	}

	public DebugEventSocketProxy(BaseRecognizer recognizer, int port, TreeAdaptor adaptor) {
		this.grammarFileName = recognizer.getGrammarFileName();
		this.adaptor = adaptor;
		this.port = port;
	}

	public void handshake() throws IOException {
		if ( serverSocket==null ) {
			serverSocket = new ServerSocket(port);
			socket = serverSocket.accept();
			socket.setTcpNoDelay(true);
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF8");
			out = new PrintWriter(new BufferedWriter(osw));
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF8");
			in = new BufferedReader(isr);
			out.println("ANTLR "+ DebugEventListener.PROTOCOL_VERSION);
			out.println("grammar \""+ grammarFileName);
			out.flush();
			ack();
		}
	}

	public void commence() {
		// don't bother sending event; listener will trigger upon connection
	}

	public void terminate() {
		transmit("terminate");
		out.close();
		try {
			socket.close();
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
	}

	protected void ack() {
		try {
			in.readLine();
		}
		catch (IOException ioe) {
			ioe.printStackTrace(System.err);
		}
	}

	protected void transmit(String event) {
		out.println(event);
		out.flush();
		ack();
	}

	public void enterRule(String grammarFileName, String ruleName) {
		transmit("enterRule\t"+grammarFileName+"\t"+ruleName);
	}

	public void enterAlt(int alt) {
		transmit("enterAlt\t"+alt);
	}

	public void exitRule(String grammarFileName, String ruleName) {
		transmit("exitRule\t"+grammarFileName+"\t"+ruleName);
	}

	public void enterSubRule(int decisionNumber) {
		transmit("enterSubRule\t"+decisionNumber);
	}

	public void exitSubRule(int decisionNumber) {
		transmit("exitSubRule\t"+decisionNumber);
	}

	public void enterDecision(int decisionNumber, boolean couldBacktrack) {
		transmit("enterDecision\t"+decisionNumber+"\t"+couldBacktrack);
	}

	public void exitDecision(int decisionNumber) {
		transmit("exitDecision\t"+decisionNumber);
	}

	public void consumeToken(Token t) {
		String buf = serializeToken(t);
		transmit("consumeToken\t"+buf);
	}

	public void consumeHiddenToken(Token t) {
		String buf = serializeToken(t);
		transmit("consumeHiddenToken\t"+buf);
	}

	public void LT(int i, Token t) {
        if(t != null)
            transmit("LT\t"+i+"\t"+serializeToken(t));
	}

	public void mark(int i) {
		transmit("mark\t"+i);
	}

	public void release(int i) {
		transmit("rewind\t"+i);
	}

	public void rewind() {
		transmit("rewind");
	}

	public void beginBacktrack(int level) {
		transmit("beginBacktrack\t"+level);
	}

	public void endBacktrack(int level, boolean successful) {
		transmit("endBacktrack\t"+level+"\t"+(successful?TRUE:FALSE));
	}

	public void location(int line, int pos) {
		transmit("location\t"+line+"\t"+pos);
	}

	public void recognitionException(RecognitionException e) {
		StringBuffer buf = new StringBuffer(50);
		buf.append("exception\t");
		buf.append(e.getClass().getName());
		// dump only the data common to all exceptions for now
		buf.append("\t");
		buf.append(e.index);
		buf.append("\t");
		buf.append(e.line);
		buf.append("\t");
		buf.append(e.charPositionInLine);
		transmit(buf.toString());
	}

	public void beginResync() {
		transmit("beginResync");
	}

	public void endResync() {
		transmit("endResync");
	}

	public void semanticPredicate(boolean result, String predicate) {
		StringBuffer buf = new StringBuffer(50);
		buf.append("semanticPredicate\t");
		buf.append(result);
		serializeText(buf, predicate);
		transmit(buf.toString());
	}

	// A S T  P a r s i n g  E v e n t s

	public void consumeNode(Object t) {
		StringBuffer buf = new StringBuffer(50);
		buf.append("consumeNode");
		serializeNode(buf, t);
		transmit(buf.toString());
	}

	public void LT(int i, Object t) {
		int ID = adaptor.getUniqueID(t);
		String text = adaptor.getText(t);
		int type = adaptor.getType(t);
		StringBuffer buf = new StringBuffer(50);
		buf.append("LN\t"); // lookahead node; distinguish from LT in protocol
		buf.append(i);
		serializeNode(buf, t);
		transmit(buf.toString());
	}

	protected void serializeNode(StringBuffer buf, Object t) {
		int ID = adaptor.getUniqueID(t);
		String text = adaptor.getText(t);
		int type = adaptor.getType(t);
		buf.append("\t");
		buf.append(ID);
		buf.append("\t");
		buf.append(type);
		Token token = adaptor.getToken(t);
		int line = -1;
		int pos = -1;
		if ( token!=null ) {
			line = token.getLine();
			pos = token.getCharPositionInLine();
		}
		buf.append("\t");
		buf.append(line);
		buf.append("\t");
		buf.append(pos);
		int tokenIndex = adaptor.getTokenStartIndex(t);
		buf.append("\t");
		buf.append(tokenIndex);
		serializeText(buf, text);
	}


	// A S T  E v e n t s

	public void nilNode(Object t) {
		int ID = adaptor.getUniqueID(t);
		transmit("nilNode\t"+ID);
	}

	public void errorNode(Object t) {
		int ID = adaptor.getUniqueID(t);
		String text = t.toString();
		StringBuffer buf = new StringBuffer(50);
		buf.append("errorNode\t");
		buf.append(ID);
		buf.append("\t");
		buf.append(Token.INVALID_TYPE);
		serializeText(buf, text);
		transmit(buf.toString());
	}

	public void createNode(Object t) {
		int ID = adaptor.getUniqueID(t);
		String text = adaptor.getText(t);
		int type = adaptor.getType(t);
		StringBuffer buf = new StringBuffer(50);
		buf.append("createNodeFromTokenElements\t");
		buf.append(ID);
		buf.append("\t");
		buf.append(type);
		serializeText(buf, text);
		transmit(buf.toString());
	}

	public void createNode(Object node, Token token) {
		int ID = adaptor.getUniqueID(node);
		int tokenIndex = token.getTokenIndex();
		transmit("createNode\t"+ID+"\t"+tokenIndex);
	}

	public void becomeRoot(Object newRoot, Object oldRoot) {
		int newRootID = adaptor.getUniqueID(newRoot);
		int oldRootID = adaptor.getUniqueID(oldRoot);
		transmit("becomeRoot\t"+newRootID+"\t"+oldRootID);
	}

	public void addChild(Object root, Object child) {
		int rootID = adaptor.getUniqueID(root);
		int childID = adaptor.getUniqueID(child);
		transmit("addChild\t"+rootID+"\t"+childID);
	}

	public void setTokenBoundaries(Object t, int tokenStartIndex, int tokenStopIndex) {
		int ID = adaptor.getUniqueID(t);
		transmit("setTokenBoundaries\t"+ID+"\t"+tokenStartIndex+"\t"+tokenStopIndex);
	}


    // support

    public void setTreeAdaptor(TreeAdaptor adaptor) { this.adaptor = adaptor; }
    public TreeAdaptor getTreeAdaptor() { return adaptor; }

    protected String serializeToken(Token t) {
        StringBuffer buf = new StringBuffer(50);
        buf.append(t.getTokenIndex()); buf.append('\t');
        buf.append(t.getType()); buf.append('\t');
        buf.append(t.getChannel()); buf.append('\t');
        buf.append(t.getLine()); buf.append('\t');
		buf.append(t.getCharPositionInLine());
		serializeText(buf, t.getText());
		return buf.toString();
	}

	protected void serializeText(StringBuffer buf, String text) {
		buf.append("\t\"");
		if ( text==null ) {
			text = "";
		}
		// escape \n and \r all text for token appears to exist on one line
		// this escape is slow but easy to understand
		text = escapeNewlines(text);
		buf.append(text);
	}

	protected String escapeNewlines(String txt) {
		txt = txt.replaceAll("%","%25");   // escape all escape char ;)
		txt = txt.replaceAll("\n","%0A");  // escape \n
		txt = txt.replaceAll("\r","%0D");  // escape \r
		return txt;
	}
}

