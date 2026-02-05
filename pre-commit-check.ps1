$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot
mvn verify
exit $LASTEXITCODE
