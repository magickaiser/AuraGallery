# deploy.ps1
# AuraGallery - Deploy script: Build APK + Install on emulator + physical device
param(
    [switch]$buildOnly,
    [switch]$emulator,
    [switch]$device,
    [switch]$all
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot
$SdkPath = "$env:LOCALAPPDATA\Android\Sdk"
$AdbPath = "$SdkPath\platform-tools\adb.exe"
$EmulatorPath = "$SdkPath\emulator\emulator.exe"
$ApkPath = "$ProjectRoot\app\build\outputs\apk\debug\app-debug.apk"

Write-Host "=== AuraGallery Deploy ===" -ForegroundColor Cyan

# Step 1: Build APK
Write-Host "`n[1/3] Building APK..." -ForegroundColor Yellow
Set-Location $ProjectRoot

& "$ProjectRoot\gradlew.bat" assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Build failed!" -ForegroundColor Red
    exit 1
}
Write-Host "BUILD OK: $ApkPath" -ForegroundColor Green

if ($buildOnly) {
    Write-Host "`nBuild-only mode. Done!" -ForegroundColor Green
    exit 0
}

# Step 2: Install on emulator
if ($emulator -or $all) {
    Write-Host "`n[2/3] Installing on emulator (Pixel 6 API 35)..." -ForegroundColor Yellow

    # Check if emulator is running
    $emuRunning = & $AdbPath devices | Select-String "emulator"
    if (-not $emuRunning) {
        Write-Host "Launching emulator..." -ForegroundColor Yellow
        Start-Process $EmulatorPath -ArgumentList "-avd Pixel_6_API_35" -NoNewWindow
        Write-Host "Waiting for emulator to boot (60s max)..." -ForegroundColor Yellow
        & $AdbPath wait-for-device
        Start-Sleep -Seconds 15
    }

    & $AdbPath -e install -r $ApkPath
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Installed on emulator!" -ForegroundColor Green
    } else {
        Write-Host "WARNING: Emulator install failed." -ForegroundColor Yellow
    }
}

# Step 3: Install on physical device
if ($device -or $all) {
    Write-Host "`n[3/3] Installing on physical device..." -ForegroundColor Yellow

    $devices = & $AdbPath devices | Select-String -Pattern "\tdevice$"
    $physicalDevices = $devices | Where-Object { $_ -notmatch "emulator" }

    if (-not $physicalDevices) {
        Write-Host "WARNING: No physical device connected!" -ForegroundColor Yellow
    } else {
        foreach ($dev in $physicalDevices) {
            $serial = ($dev -split "\t")[0]
            Write-Host "Installing on $serial..." -ForegroundColor Yellow
            & $AdbPath -s $serial install -r $ApkPath
            if ($LASTEXITCODE -eq 0) {
                Write-Host "Installed on $serial!" -ForegroundColor Green
            }
        }
    }
}

Write-Host "`n=== Deploy Complete ===" -ForegroundColor Cyan
