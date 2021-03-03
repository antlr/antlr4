#

cat ../../src/Atn/ParserATNSimulator.cs > ParserATNSimulator.save
cat ParserATNSimulator.save | sed 's/bool debug = false;/bool debug = true;/' > ../../src/Atn/ParserATNSimulator.cs
dotnet restore
dotnet build
dotnet run -input "1+2"
if [[ "$?" != "0" ]]
then
    echo "Issue 2693 test failed."
    exit 1
else
    echo "Test passed--did not crash."
fi
