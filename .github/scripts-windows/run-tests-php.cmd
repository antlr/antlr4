REM C:\ProgramData\chocolatey\bin\choco.exe install php -y --package-parameters='"/DontAddToPath"'

git clone https://github.com/antlr/antlr-php-runtime.git
move antlr-php-runtime runtime\PHP

cd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=2 -Dtest=php.** test -Dantlr-php-php="C:\tools\php81\php.exe"
cd ..
