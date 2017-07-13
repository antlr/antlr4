# Local Docker Development

Note: The default [Dockerfile](docker/Dockerfile) only provides OpenJDK 7 and Maven, but not 
the dependencies needed for other runtime targets, such as C++, Python, etc.

## Example workflow

### Initial setup

1. Install Docker to your local machine. If on Windows, run the Docker Quickstart Terminal.
2. Navigate to your project folder and run the script `docker/go.sh`. This should build a Docker image, start a temporary container, and provide you a command prompt inside that container. Your project directory will be mapped to `/opt/project/`. 
3. Check that everything is in a good initial state with  `mvn -DskipTests=true install`. The first run may involve many Maven downloads.
4. Assuming the JAR files build successfully, check that the command `antlr4.sh` shows usage information.

### Example tweaking loop for code-generation

Using the Java runtime just as a stable example:

1. Find a temporary directory and put in a grammar file you wish to test against, e.g. `Hello.g4`.
2. Edit the StringTemplate file in `tool/resources/org/antlr/v4/tool/templates/codegen/Java/java.stg`
3. Rebuild just the tool with its new templates: `pushd /opt/project/tool/; mvn install -DskipTests=true; popd;`
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

1. While `docker-compose` can add convenience, it doesn't (yet) seamlessly support interactive containers on Windows.
2. On Linux, files created inside `/opt/project` from container may have the wrong ownership. 
