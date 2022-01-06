REM C:\ProgramData\chocolatey\bin\choco.exe install php --package-parameters='"/DontAddToPath""/ThreadSafe""/InstallDir:C:\Program Files\php\"'

git clone https://github.com/antlr/antlr-php-runtime.git
move antlr-php-runtime runtime\PHP

cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=4 -Dtest=php.** test -Dantlr-php-php="C:\Program Files\php\php.exe"
cd ..
