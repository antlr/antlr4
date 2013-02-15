# Semi-automatic conversion with Sharpen

The update process for keeping the C# port up-to-date uses several steps, taking
advantage of automatic translation features provided by Sharpen.

1. Changes are made to the reference (Java) version of ANTLR 4. Since the C# port
   is based on the optimized release of ANTLR 4, this means commits were pushed to
   the antlr4/optimized branch.
2. The changes are merged into the antlr4/sharpen branch of the ANTLR 4 repository,
   and any changes to the Java code necessary for successful translation are kept
   in this branch.
3. In the antlr4cs/sharpen branch (of the C# port repository), the submodule
   reference/antlr4 is updated to include the latest code from item 2.
4. The configuration file `sharpen-all-options.txt` is updated as necessary for
   the translation.
5. The code is translated to update the antlr4cs/sharpen branch.
6. The antlr4cs/sharpen branch is merged back into antlr4cs/master, which contains
   the compilable code. Git's internal merge abilities allow us to continually
   maintain the extensive patches necessary to produce a proper binary from the
   output of Sharpen.

## Setting Up Sharpen

1. In Eclipse, Import... -> General -> Existing Projects into Workspace
2. Import `${antlr4cs}/reference/sharpen/sharpen.core`, and do not check the option
   to copy the project into the workspace.
3. Create a new run configuration based on "Eclipse Application"
   * Main -> Program to Run -> Run an application: sharpen.core.application
   * Arguments -> configure according to the "Sharpen options" section

## Sharpen options

**Custom Variables**

* `${antlr4cs}` - the path where the ANTLR 4 C# port is checked out locally
* `${m2_home}` - the path to your Maven configuration folder, e.g. `~/.m2`

**Program arguments**

	-os ${target.os} -ws ${target.ws} -arch ${target.arch} -nl ${target.nl} -consoleLog
	-data ${antlr4cs}/reference -srcFolder runtime/Java/src
	antlr4
	-header ${antlr4cs}/reference/sharpen-header.txt
	-cp ${m2_home}/repository/org/abego/treelayout/org.abego.treelayout.core/1.0.1/org.abego.treelayout.core-1.0.1.jar
	@sharpen-all-options.txt

**VM arguments**

	-Dosgi.requiredJavaVersion=1.5 -Xms1g -Xmx2g -XX:+UseG1GC -XX:+TieredCompilation -XX:MaxPermSize=384m

**Working directory**

	${antlr4cs}/reference
