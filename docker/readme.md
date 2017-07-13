The code in this folder exists for contributors to set up a local development
environment which can be made consistent across various desktops and laptops. 

Note that the default Dockerfile only provides OpenJDK 7 and Maven, but not 
the dependencies needed for other targets such as C++ or Python.

## Example workflow

### Initial setup

These steps assume the current version is 4.7.1,  you may need to search-and-replace as appropriate.

1. Start docker environment and begin a new terminal with: `docker/go.sh`. This should drop you into the `/opt/project` folder.
2. Check that everything is in a good initial state with  `mvn install`, you may need to use the argument `-DskipTests=true` if tests are being flaky.
3. Check that `antlr4.sh` runs and shows usage information.

### Example tweaking loop for code-generation

Using the Java runtime just as a stable example.

1. Find a temporary directory and put in a grammar file to test, e.g. `Hello.g4`.
2. Edit the StringTemplate file in `tool/resources/org/antlr/v4/tool/templates/codegen/Java/java.stg`
3. Rebuild just the tool: `pushd /opt/project/tool/; mvn package -DskipTests=true; popd;`
4. Run the changed tool against your grammar file: `antlr4.sh Hello.g4 -package Example -Dlanguage=java`
5. Inspect the generated `*.java` files and see what comes up as a syntax error.

Known issues:

1. Not using `docker-compose` because it doesn't seamlessly support interactive stuff on Windows quite yet.
2. Files created by processes inside the Docker environment may set the wrong ownership. 
