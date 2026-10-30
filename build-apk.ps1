$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$gradle = Get-Command gradle -ErrorAction SilentlyContinue
if (-not $gradle) {
    throw 'Gradle is not installed or not on PATH. Install Android Studio (including JDK 17) and Gradle, then rerun this script.'
}

Push-Location $projectRoot
try {
    & $gradle.Source --no-daemon :app:assembleDebug
    if ($LASTEXITCODE -ne 0) { throw "Gradle build failed with exit code $LASTEXITCODE" }
    $apk = Join-Path $projectRoot 'app\build\outputs\apk\debug\app-debug.apk'
    Write-Host "APK ready: $apk"
} finally {
    Pop-Location
}
