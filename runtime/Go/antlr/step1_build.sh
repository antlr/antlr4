mkdir antlr4-go-step1-wxio
cd antlr4-go-step1-wxio/
git clone ../antlr4-wxio .

export PATH=~/dev/apache-maven-3.3.9/bin:$PATH
export JAVA_HOME=`/usr/libexec/java_home`
mvn install -DskipTests
mkdir runtime/Go/antlr/lib/
cp tool/target/*.jar runtime/Go/antlr/lib/
git add -f runtime/Go/antlr/lib/
git commit -m "addin jars"
git filter-branch --prune-empty --subdirectory-filter runtime/Go/antlr master

