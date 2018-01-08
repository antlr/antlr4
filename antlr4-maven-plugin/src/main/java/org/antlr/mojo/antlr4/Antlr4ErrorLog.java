/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.mojo.antlr4;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.ANTLRToolListener;
import org.apache.maven.plugin.logging.Log;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.stringtemplate.v4.ST;

import java.io.File;

/**
 * This implementation of {@link ANTLRToolListener} reports messages to the
 * {@link Log} instance provided by Maven.
 *
 * @author Sam Harwell
 */
public class Antlr4ErrorLog implements ANTLRToolListener {

    private final Tool tool;
    private final BuildContext buildContext;
    private final Log log;

    /**
     * Creates an instance of {@link Antlr4ErrorLog}.
     *
     * @param log The Maven log
     */
    public Antlr4ErrorLog(Tool tool, BuildContext buildContext, Log log) {
        this.tool = tool;
        this.buildContext = buildContext;
        this.log = log;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation passes the message to the Maven log.
     * </p>
     * @param message The message to send to Maven
     */
    @Override
    public void info(String message) {
        if (tool.errMgr.formatWantsSingleLineMessage()) {
            message = message.replace('\n', ' ');
        }
        log.info(message);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation passes the message to the Maven log.
     * </p>
     * @param message The message to send to Maven.
     */
    @Override
    public void error(ANTLRMessage message) {
        ST msgST = tool.errMgr.getMessageTemplate(message);
        String outputMsg = msgST.render();
        if (tool.errMgr.formatWantsSingleLineMessage()) {
            outputMsg = outputMsg.replace('\n', ' ');
        }

        log.error(outputMsg);

        if (message.fileName != null) {
            String text = message.getMessageTemplate(false).render();
            buildContext.addMessage(new File(message.fileName), message.line, message.charPosition, text, BuildContext.SEVERITY_ERROR, message.getCause());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation passes the message to the Maven log.
     * </p>
     * @param message
     */
    @Override
    public void warning(ANTLRMessage message) {
        ST msgST = tool.errMgr.getMessageTemplate(message);
        String outputMsg = msgST.render();
        if (tool.errMgr.formatWantsSingleLineMessage()) {
            outputMsg = outputMsg.replace('\n', ' ');
        }

        log.warn(outputMsg);

        if (message.fileName != null) {
            String text = message.getMessageTemplate(false).render();
            buildContext.addMessage(new File(message.fileName), message.line, message.charPosition, text, BuildContext.SEVERITY_WARNING, message.getCause());
        }
    }
}
