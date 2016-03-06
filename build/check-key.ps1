param(
	[string]$Assembly,
	[string]$ExpectedKey,
	[string]$Build = $null
)

function Get-PublicKeyToken() {
	param([string]$assembly = $null)
	if ($assembly) {
		$bytes = $null
		$bytes = [System.Reflection.Assembly]::ReflectionOnlyLoadFrom($assembly).GetName().GetPublicKeyToken()
		if ($bytes) {
			$key = ""
			for ($i=0; $i -lt $bytes.Length; $i++) {
				$key += "{0:x2}" -f $bytes[$i]
			}

			$key
		}
	}
}

if (-not $Build) {
	$Build = $Assembly
}

$actual = Get-PublicKeyToken -assembly $Assembly
if ($actual -ne $ExpectedKey) {
	$host.ui.WriteErrorLine("Invalid publicKeyToken for '$Build'; expected '$ExpectedKey' but found '$actual'")
	exit 1
}
