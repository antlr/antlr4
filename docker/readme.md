The code in this folder exists for contributors to set up a local development
environment which can be made consistent across various desktops and laptops. 

Note that the default Dockerfile only provides OpenJDK 7 and Maven, but not 
the dependencies needed for other targets such as C++ or Python.

## Example workflow

### Initial setup

These steps assume the current version is 4.7.1,  you may need to search-and-replace as appropriate.

1. Start docker environment and begin a new terminal with: `docker/go.sh`
2. Create everything once with: `cd /opt/project/; mvn package -DskipTests=true;`
3. Set up aliases: `alias antlr4='java -Xmx500M -cp "/opt/project/tool/target/antlr4-4.7.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.Tool'`
4. Set up aliases: `alias grun='java -cp "/opt/project/tool/target/antlr4-4.7.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.gui.TestRig'`

### Example tweaking loop for new runtimes

Using the Java runtime just as a stable example.

1. Find a temporary directory and put in a grammar file to test, e.g. `Hello.g4`.
2. Edit the StringTemplate file in `tool/resources/org/antlr/v4/tool/templates/codegen/Java/java.stg`
3. Rebuild just the tool: `pushd /opt/project/tool/; mvn package -DskipTests=true; popd;`
4. Run the changed tool against your grammar file: `antlr4 Hello.g4 -package Example -Dlanguage=java`
5. Inspect the generated `*.java` files and see what comes up as a syntax error.

Known issues:

1. Not using `docker-compose` because it doesn't seamlessly support interactive stuff on Windows quite yet.
2. Files created by processes inside the Docker environment may set the wrong ownership. 
