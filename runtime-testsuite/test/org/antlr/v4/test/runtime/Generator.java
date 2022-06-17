/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRMessage;
import org.antlr.v4.tool.DefaultToolListener;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.antlr.v4.test.runtime.FileUtils.writeFile;

public class Generator {
	/** Write a grammar to tmpdir and run antlr */
	public static ErrorQueue antlrOnString(String workdir,
										   String targetName,
										   String grammarFileName,
										   String grammarStr,
										   boolean defaultListener,
										   String... extraOptions)
	{
		FileUtils.mkdir(workdir);
		writeFile(workdir, grammarFileName, grammarStr);
		return antlrOnString(workdir, targetName, grammarFileName, defaultListener, extraOptions);
	}

	/** Run ANTLR on stuff in workdir and error queue back */
	public static ErrorQueue antlrOnString(String workdir,
										   String targetName,
										   String grammarFileName,
										   boolean defaultListener,
										   String... extraOptions)
	{
		final List<String> options = new ArrayList<>();
		Collections.addAll(options, extraOptions);
		if ( targetName!=null ) {
			options.add("-Dlanguage="+targetName);
		}
		if ( !options.contains("-o") ) {
			options.add("-o");
			options.add(workdir);
		}
		if ( !options.contains("-lib") ) {
			options.add("-lib");
			options.add(workdir);
		}
		if ( !options.contains("-encoding") ) {
			options.add("-encoding");
			options.add("UTF-8");
		}
		options.add(new File(workdir,grammarFileName).toString());

		final String[] optionsA = new String[options.size()];
		options.toArray(optionsA);
		Tool antlr = new Tool(optionsA);
		ErrorQueue equeue = new ErrorQueue(antlr);
		antlr.addListener(equeue);
		if (defaultListener) {
			antlr.addListener(new DefaultToolListener(antlr));
		}
		antlr.processGrammarsOnCommandLine();

		List<String> errors = new ArrayList<>();

		if ( !defaultListener && !equeue.errors.isEmpty() ) {
			for (int i = 0; i < equeue.errors.size(); i++) {
				ANTLRMessage msg = equeue.errors.get(i);
				ST msgST = antlr.errMgr.getMessageTemplate(msg);
				errors.add(msgST.render());
			}
		}
		if ( !defaultListener && !equeue.warnings.isEmpty() ) {
			for (int i = 0; i < equeue.warnings.size(); i++) {
				ANTLRMessage msg = equeue.warnings.get(i);
				// antlrToolErrors.append(msg); warnings are hushed
			}
		}

		return equeue;
	}
}
