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

## Debugging

The approach taken in this section is to configure the running program in the 
docker container to open a listening port and then wait for a debugger to
connect. (The alternative would be to have the JVM connect outwards seeking a
listening debugger.)


### Debugging Maven

This command will set up debugging for the `mvn` command on top of whatever the
pre-existing `MAVEN_OPTS` are. Note that since it does not begin with `export`, 
it only changes `MAVEN_OPTS` for the current run. 

    MAVEN_OPTS="$MAVEN_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005" mvn
    
Note that the command will pause, waiting for a connection from your IDE.    
    
### Debugging antlr4.sh

    ANTLR_JVM_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005" antlr4.sh
   
Note that the command will pause, waiting for a connection from your IDE.
  
### Changing the debugging port

If your IDE does not want to use 5005, you can easily change the port by 
altering `docker/go.sh` so that it is mapped to something different on localhost.
 

## Known issues

1. Not using `docker-compose` because it doesn't seamlessly support interactive stuff on Windows quite yet.
2. Files created by processes inside the Docker environment may set the wrong ownership. 
