/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.mojo.antlr4;

import org.antlr.v4.Tool;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.tool.Grammar;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parses ANTLR 4 grammar files {@code *.g4} and transforms them into Java
 * source files.
 *
 * @author Sam Harwell
 */
@Mojo(
	name = "antlr4",
	defaultPhase = LifecyclePhase.GENERATE_SOURCES,
	requiresDependencyResolution = ResolutionScope.COMPILE,
	requiresProject = true)
public class Antlr4Mojo extends AbstractMojo {

    // First, let's deal with the options that the ANTLR tool itself
    // can be configured by.
    //

    /**
     * If set to true then the ANTLR tool will generate a description of the ATN
     * for each rule in <a href="http://www.graphviz.org">Dot format</a>.
     */
    @Parameter(property = "antlr4.atn", defaultValue = "false")
    protected boolean atn;

	/**
	 * specify grammar file encoding; e.g., euc-jp
	 */
	@Parameter(property = "project.build.sourceEncoding")
	protected String inputEncoding;

	/**
	 * specify output file encoding; defaults to source encoding
	 */
	@Parameter(property = "project.build.sourceEncoding")
	protected String outputEncoding;

	/**
	 * Generate parse tree listener interface and base class.
	 */
	@Parameter(property = "antlr4.listener", defaultValue = "true")
	protected boolean listener;

	/**
	 * Generate parse tree visitor interface and base class.
	 */
	@Parameter(property = "antlr4.visitor", defaultValue = "false")
	protected boolean visitor;

	/**
	 * Treat warnings as errors.
	 */
	@Parameter(property = "antlr4.treatWarningsAsErrors", defaultValue = "false")
	protected boolean treatWarningsAsErrors;

	/**
	 * Use the ATN simulator for all predictions.
	 */
	@Parameter(property = "antlr4.forceATN", defaultValue = "false")
	protected boolean forceATN;

	/**
	 * A list of grammar options to explicitly specify to the tool. These
	 * options are passed to the tool using the
	 * <code>-D&lt;option&gt;=&lt;value&gt;</code> syntax.
	 */
	@Parameter
	protected Map<String, String> options;

	/**
	 * A list of additional command line arguments to pass to the ANTLR tool.
	 */
	@Parameter
	protected List<String> arguments;

    /* --------------------------------------------------------------------
     * The following are Maven specific parameters, rather than specific
     * options that the ANTLR tool can use.
     */

	/**
	 * Provides an explicit list of all the grammars that should be included in
	 * the generate phase of the plugin. Note that the plugin is smart enough to
	 * realize that imported grammars should be included but not acted upon
	 * directly by the ANTLR Tool.
	 * <p>
	 * A set of Ant-like inclusion patterns used to select files from the source
	 * directory for processing. By default, the pattern
	 * <code>**&#47;*.g4</code> is used to select grammar files.
	 * </p>
	 */
    @Parameter
    protected Set<String> includes = new HashSet<String>();
    /**
     * A set of Ant-like exclusion patterns used to prevent certain files from
     * being processed. By default, this set is empty such that no files are
     * excluded.
     */
    @Parameter
    protected Set<String> excludes = new HashSet<String>();
    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

	/**
	 * Specifies whether sources are added to the {@code compile} or
	 * {@code test} scope.
	 */
	@Parameter(property = "antlr4.generateTestSources", defaultValue = "false")
	private boolean generateTestSources;

    /**
     * The directory where the ANTLR grammar files ({@code *.g4}) are located.
     */
	@Parameter(defaultValue = "${basedir}/src/main/antlr4")
    private File sourceDirectory;

    /**
     * Specify output directory where the Java files are generated.
     */
	@Parameter(defaultValue = "${project.build.directory}/generated-sources/antlr4")
    private File outputDirectory;

    /**
     * Specify location of imported grammars and tokens files.
     */
	@Parameter(defaultValue = "${basedir}/src/main/antlr4/imports")
    private File libDirectory;

    /**
     * The directory where build status information is located.
     */
    @Parameter(defaultValue = "${project.build.directory}/maven-status/antlr4", readonly=true)
    private File statusDirectory;

	@Component
	private BuildContext buildContext;

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public File getLibDirectory() {
        return libDirectory;
    }

    void addSourceRoot(File outputDir) {
		if (generateTestSources) {
			project.addTestCompileSourceRoot(outputDir.getPath());
		}
		else {
			project.addCompileSourceRoot(outputDir.getPath());
		}
    }

    /**
     * An instance of the ANTLR tool build
     */
    protected Tool tool;

    /**
     * The main entry point for this Mojo, it is responsible for converting
     * ANTLR 4.x grammars into the target language specified by the grammar.
     *
     * @exception MojoExecutionException if a configuration or grammar error causes
     * the code generation process to fail
     * @exception MojoFailureException if an instance of the ANTLR 4 {@link Tool}
     * cannot be created
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Log log = getLog();

		outputEncoding = validateEncoding(outputEncoding);

        if (log.isDebugEnabled()) {
            for (String e : excludes) {
                log.debug("ANTLR: Exclude: " + e);
            }

            for (String e : includes) {
                log.debug("ANTLR: Include: " + e);
            }

            log.debug("ANTLR: Output: " + outputDirectory);
            log.debug("ANTLR: Library: " + libDirectory);
        }

		if (!sourceDirectory.isDirectory()) {
			log.info("No ANTLR 4 grammars to compile in " + sourceDirectory.getAbsolutePath());
			return;
		}

        // Ensure that the output directory path is all in tact so that
        // ANTLR can just write into it.
        //
        File outputDir = getOutputDirectory();

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        GrammarDependencies dependencies = new GrammarDependencies(sourceDirectory, libDirectory, arguments, getDependenciesStatusFile(), getLog());

		// Now pick up all the files and process them with the Tool
		//

		List<List<String>> argumentSets;
        Set<File> grammarFiles;
        Set<File> importGrammarFiles;
        try {
			List<String> args = getCommandArguments();
            grammarFiles = getGrammarFiles(sourceDirectory);
            importGrammarFiles = getImportFiles(sourceDirectory);
            argumentSets = processGrammarFiles(args, grammarFiles, dependencies, sourceDirectory);
        } catch (Exception e) {
            log.error(e);
            throw new MojoExecutionException("Fatal error occured while evaluating the names of the grammar files to analyze", e);
        }

		log.debug("Output directory base will be " + outputDirectory.getAbsolutePath());
		log.info("ANTLR 4: Processing source directory " + sourceDirectory.getAbsolutePath());
		for (List<String> args : argumentSets) {
			try {
				// Create an instance of the ANTLR 4 build tool
				tool = new CustomTool(args.toArray(new String[args.size()]));
			} catch (Exception e) {
				log.error("The attempt to create the ANTLR 4 build tool failed, see exception report for details", e);
				throw new MojoFailureException("Error creating an instanceof the ANTLR tool.", e);
			}

            try {
                dependencies.analyze(grammarFiles, importGrammarFiles, tool);
            } catch (Exception e) {
                log.error("Dependency analysis failed, see exception report for details",
                    e);
                throw new MojoFailureException("Dependency analysis failed.", e);
            }

			// Set working directory for ANTLR to be the base source directory
			tool.inputDirectory = sourceDirectory;

			tool.processGrammarsOnCommandLine();

			// If any of the grammar files caused errors but did nto throw exceptions
			// then we should have accumulated errors in the counts
			if (tool.getNumErrors() > 0) {
				throw new MojoExecutionException("ANTLR 4 caught " + tool.getNumErrors() + " build errors.");
			}
		}

        if (project != null) {
            // Tell Maven that there are some new source files underneath the output directory.
            addSourceRoot(this.getOutputDirectory());
        }

        try {
            dependencies.save();
        } catch (IOException ex) {
            log.warn("Could not save grammar dependency status", ex);
        }
    }

	private List<String> getCommandArguments() {
		List<String> args = new ArrayList<String>();

		if (getOutputDirectory() != null) {
			args.add("-o");
			args.add(outputDirectory.getAbsolutePath());
		}

		// Where do we want ANTLR to look for .tokens and import grammars?
		if (getLibDirectory() != null && getLibDirectory().isDirectory()) {
			args.add("-lib");
			args.add(libDirectory.getAbsolutePath());
		}

        // Next we need to set the options given to us in the pom into the
        // tool instance we have created.
		if (atn) {
			args.add("-atn");
		}

		if ( inputEncoding!=null && !inputEncoding.isEmpty()) {
			args.add("-encoding");
			outputEncoding = inputEncoding;
			args.add(inputEncoding);
		}

		if (listener) {
			args.add("-listener");
		}
		else {
			args.add("-no-listener");
		}

		if (visitor) {
			args.add("-visitor");
		}
		else {
			args.add("-no-visitor");
		}

		if (treatWarningsAsErrors) {
			args.add("-Werror");
		}

		if (forceATN) {
			args.add("-Xforce-atn");
		}

		if (options != null) {
			for (Map.Entry<String, String> option : options.entrySet()) {
				args.add(String.format("-D%s=%s", option.getKey(), option.getValue()));
			}
		}

		if (arguments != null) {
			args.addAll(arguments);
		}

		return args;
	}

    /**
     *
     * @param sourceDirectory
     * @exception InclusionScanException
     */
    private List<List<String>> processGrammarFiles(
        List<String> args,
        Set<File> grammarFiles,
        GrammarDependencies dependencies,
        File sourceDirectory) throws InclusionScanException, IOException {

        // We don't want the plugin to run for every grammar, regardless of whether
        // it's changed since the last compilation. Check the mtime of the tokens vs
        // the grammar file mtime to determine whether we even need to execute.
        Set<File> grammarFilesToProcess = new HashSet<File>();

        for (File grammarFile : grammarFiles) {
            String tokensFileName = grammarFile.getName().split("\\.")[0] + ".tokens";
            File outputFile = new File(outputDirectory, tokensFileName);
            if ( (! outputFile.exists()) ||
                 outputFile.lastModified() <= grammarFile.lastModified() ||
                 dependencies.isDependencyChanged(grammarFile)) {
                grammarFilesToProcess.add(grammarFile);
            }
        }

        grammarFiles = grammarFilesToProcess;

        if (grammarFiles.isEmpty()) {
            getLog().info("No grammars to process");
			return Collections.emptyList();
		}

		MultiMap<String, File> grammarFileByFolder = new MultiMap<String, File>();
		// Iterate each grammar file we were given and add it into the tool's list of
		// grammars to process.
		for (File grammarFile : grammarFiles) {
			buildContext.refresh(grammarFile);
			buildContext.removeMessages(grammarFile);

			getLog().debug("Grammar file '" + grammarFile.getPath() + "' detected.");

			String relPathBase = MojoUtils.findSourceSubdir(sourceDirectory, grammarFile);
			String relPath = relPathBase + grammarFile.getName();
			getLog().debug("  ... relative path is: " + relPath);

			grammarFileByFolder.map(relPathBase, grammarFile);
		}

		List<List<String>> result = new ArrayList<List<String>>();
		for (Map.Entry<String, List<File>> entry : grammarFileByFolder.entrySet()) {
			List<String> folderArgs = new ArrayList<String>(args);
			if (!folderArgs.contains("-package") && !entry.getKey().isEmpty()) {
				folderArgs.add("-package");
				folderArgs.add(getPackageName(entry.getKey()));
			}

			for (File file : entry.getValue()) {
				folderArgs.add(entry.getKey() + file.getName());
			}

			result.add(folderArgs);
		}

		return result;
	}

    private Set<File> getImportFiles(File sourceDirectory) throws InclusionScanException {
        if (!libDirectory.exists()) return Collections.emptySet();

        Set<String> includes = new HashSet<String>();
        includes.add("*.g4");
        includes.add("*.tokens");

        SourceInclusionScanner scan = new SimpleSourceInclusionScanner(includes,
                Collections.<String>emptySet());
        scan.addSourceMapping(new SuffixMapping("G4", "g4"));

        return scan.getIncludedSources(libDirectory, null);
    }

	private Set<File> getGrammarFiles(File sourceDirectory) throws InclusionScanException
	{
		// Which files under the source set should we be looking for as grammar files
		SourceMapping mapping = new SuffixMapping("g4", Collections.<String>emptySet());

		// What are the sets of includes (defaulted or otherwise).
		Set<String> includes = getIncludesPatterns();

		// Now, to the excludes, we need to add the imports directory
		// as this is autoscanned for imported grammars and so is auto-excluded from the
		// set of grammar fields we should be analyzing.
		excludes.add("imports/**");

		SourceInclusionScanner scan = new SimpleSourceInclusionScanner(includes, excludes);
		scan.addSourceMapping(mapping);

		return scan.getIncludedSources(sourceDirectory, null);
	}

	private static String getPackageName(String relativeFolderPath) {
		if (relativeFolderPath.contains("..")) {
			throw new UnsupportedOperationException("Cannot handle relative paths containing '..'");
		}

		List<String> parts = new ArrayList<String>(Arrays.asList(relativeFolderPath.split("[/\\\\\\.]+")));
		while (parts.remove("")) {
			// intentionally blank
		}

		return Utils.join(parts.iterator(), ".");
	}

    public Set<String> getIncludesPatterns() {
        if (includes == null || includes.isEmpty()) {
            return Collections.singleton("**/*.g4");
        }
        return includes;
    }

    private File getDependenciesStatusFile() {
        File statusFile = new File(statusDirectory, "dependencies.ser");

        if (!statusFile.getParentFile().exists()) {
            statusFile.getParentFile().mkdirs();
        }

        return statusFile;
    }

	private final class CustomTool extends Tool {

		public CustomTool(String[] args) {
			super(args);
			addListener(new Antlr4ErrorLog(this, buildContext, getLog()));
		}

		@Override
		public void process(Grammar g, boolean gencode) {
			getLog().info("Processing grammar: " + g.fileName);
			super.process(g, gencode);
		}

		@Override
		public Writer getOutputFileWriter(Grammar g, String fileName) throws IOException {
			if (outputDirectory == null) {
				return new StringWriter();
			}
			// output directory is a function of where the grammar file lives
			// for subdir/T.g4, you get subdir here.  Well, depends on -o etc...
			// But, if this is a .tokens file, then we force the output to
			// be the base output directory (or current directory if there is not a -o)
			//
			File outputDir;
			if ( fileName.endsWith(CodeGenerator.VOCAB_FILE_EXTENSION) ) {
				outputDir = new File(outputDirectory);
			}
			else {
				outputDir = getOutputDirectory(g.fileName);
			}

			File outputFile = new File(outputDir, fileName);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}

			URI relativePath = project.getBasedir().toURI().relativize(outputFile.toURI());
			getLog().debug("  Writing file: " + relativePath);
			OutputStream outputStream = buildContext.newFileOutputStream(outputFile);
			if ( outputEncoding!=null && !outputEncoding.isEmpty()) {
				return new BufferedWriter(new OutputStreamWriter(outputStream, outputEncoding));
			}
			else {
				return new BufferedWriter(new OutputStreamWriter(outputStream));
			}
		}
	}

	/**
	 * Validates the given encoding.
	 *
	 * @return  the validated encoding. If {@code null} was provided, returns the platform default encoding.
	 */
	private String validateEncoding(String encoding) {
		return (encoding == null) ? Charset.defaultCharset().name() : Charset.forName(encoding.trim()).name();
	}
}
