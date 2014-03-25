. .\version.ps1

If ($AntlrVersion.EndsWith('-dev')) {
	Write-Host "Cannot push development version '$AntlrVersion' to NuGet."
	Exit 1
}

..\runtime\CSharp\.nuget\NuGet.exe push ".\nuget\Antlr4.Runtime.$AntlrVersion.nupkg"
..\runtime\CSharp\.nuget\NuGet.exe push ".\nuget\Antlr4.$AntlrVersion.nupkg"
..\runtime\CSharp\.nuget\NuGet.exe push ".\nuget\Antlr4.VS2008.$AntlrVersion.nupkg"
