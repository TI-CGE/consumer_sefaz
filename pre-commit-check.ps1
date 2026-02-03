$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
Set-Location $ProjectRoot

Write-Host "Verificacoes de qualidade: Checkstyle, SpotBugs, PMD" -ForegroundColor Cyan
Write-Host ""

$mvn = "mvn"
if ($env:MAVEN_HOME) {
    $mvn = Join-Path $env:MAVEN_HOME "bin\mvn.cmd"
}
if (-not (Get-Command $mvn -ErrorAction SilentlyContinue)) {
    $mvn = "mvnw.cmd"
    if (-not (Test-Path (Join-Path $ProjectRoot $mvn))) {
        Write-Error "Maven (mvn ou mvnw.cmd) nao encontrado."
        exit 1
    }
    $mvn = ".\mvnw.cmd"
}

& $mvn compile checkstyle:check spotbugs:check pmd:check
$exitCode = $LASTEXITCODE
if ($exitCode -eq 0) {
    Write-Host ""
    Write-Host "Todas as verificacoes passaram." -ForegroundColor Green
}
else {
    Write-Host ""
    Write-Host "Verificacoes falharam. Corrija os problemas acima." -ForegroundColor Red
}
exit $exitCode
