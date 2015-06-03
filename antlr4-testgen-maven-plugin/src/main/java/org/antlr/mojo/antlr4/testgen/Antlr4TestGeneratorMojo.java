/*
 [The "BSD license"]
 Copyright (c) 2014 Sam Harwell
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
package org.antlr.mojo.antlr4.testgen;

import org.antlr.v4.testgen.TestGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(
	name = "antlr4.testgen",
	defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
	requiresDependencyResolution = ResolutionScope.TEST,
	requiresProject = true)
public class Antlr4TestGeneratorMojo extends AbstractMojo {

	// This project uses UTF-8, but the plugin might be used in another project
	// which is not. Always load templates with UTF-8, but write using the
	// specified encoding.
	@Parameter(property = "project.build.sourceEncoding")
	private String encoding;

	@Parameter(property = "project", readonly = true)
	private MavenProject project;

	@Parameter(property = "runtimeTemplates", required = true)
	private File runtimeTemplates;

	@Parameter(defaultValue = "${project.build.directory}/generated-test-sources/antlr4-tests")
	private File outputDirectory;

	@Parameter
	private boolean visualize;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		TestGenerator testGenerator = new MavenTestGenerator(encoding, runtimeTemplates, outputDirectory, visualize);
		testGenerator.execute();

		if (project != null) {
			project.addTestCompileSourceRoot(outputDirectory.getPath());
		}
	}

	private class MavenTestGenerator extends TestGenerator {		

		public MavenTestGenerator(String encoding, File runtimeTemplates, File outputDirectory, boolean visualize) {
			super(encoding, runtimeTemplates, outputDirectory, visualize);
		}

		@Override
		protected void warn(String message) {
			getLog().warn(message);
		}

		@Override
		protected void error(String message, Throwable throwable) {
			getLog().error(message, throwable);
		}

	}
}
