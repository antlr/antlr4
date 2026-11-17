package org.antlr.v4.semantics;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.ST;

import java.util.Collection;
import java.util.Objects;

public class ActionErrorManager extends ErrorManager {
	private final Token actionToken;
	private final ErrorManager delegate;

	public ActionErrorManager(Token actionToken, ErrorManager delegate) {
		super(delegate.tool);
		this.delegate = Objects.requireNonNull(delegate, "ErrorManager delegate cannot be null");
		this.actionToken = Objects.requireNonNull(actionToken, "Token actionToken cannot be null");
	}

	@Override
	public void grammarError(ErrorType etype, String fileName, Token token, Object... args) {
		delegate.grammarError(etype, fileName, resolveTokenWithGrammarAbsolutePosition(token), args);
	}

	private Token resolveTokenWithGrammarAbsolutePosition(Token token) {
		if (token instanceof CommonToken && actionToken instanceof CommonToken) {
			CommonToken absolutePositionToken = new CommonToken(token);
			CommonToken ruleToken = (CommonToken) actionToken;
			absolutePositionToken.setStartIndex(ruleToken.getStartIndex() + absolutePositionToken.getStartIndex());
			absolutePositionToken.setStopIndex(ruleToken.getStartIndex() + absolutePositionToken.getStopIndex());
			return absolutePositionToken;
		}
		return token;
	}

	@Override
	public void resetErrorState() {
		delegate.resetErrorState();
	}

	@Override
	public ST getMessageTemplate(ANTLRMessage msg) {
		return delegate.getMessageTemplate(msg);
	}

	@Override
	public ST getLocationFormat() {
		return delegate.getLocationFormat();
	}

	@Override
	public ST getReportFormat(ErrorSeverity severity) {
		return delegate.getReportFormat(severity);
	}

	@Override
	public ST getMessageFormat() {
		return delegate.getMessageFormat();
	}

	@Override
	public boolean formatWantsSingleLineMessage() {
		return delegate.formatWantsSingleLineMessage();
	}

	@Override
	public void info(String msg) {
		delegate.info(msg);
	}

	@Override
	public void syntaxError(ErrorType etype, String fileName, Token token, RecognitionException antlrException, Object... args) {
		delegate.syntaxError(etype, fileName, token, antlrException, args);
	}

	@Override
	public void toolError(ErrorType errorType, Object... args) {
		delegate.toolError(errorType, args);
	}

	@Override
	public void toolError(ErrorType errorType, Throwable e, Object... args) {
		delegate.toolError(errorType, e, args);
	}

	@Override
	public void leftRecursionCycles(String fileName, Collection<? extends Collection<Rule>> cycles) {
		delegate.leftRecursionCycles(fileName, cycles);
	}

	@Override
	public int getNumErrors() {
		return delegate.getNumErrors();
	}

	@Override
	public void emit(ErrorType etype, ANTLRMessage msg) {
		delegate.emit(etype, msg);
	}

	@Override
	public void setFormat(String formatName) {
		delegate.setFormat(formatName);
	}

	@Override
	protected boolean verifyFormat() {
		return super.verifyFormat();
	}

	@Override
	public void panic(ErrorType errorType, Object... args) {
		delegate.panic(errorType, args);
	}
}
