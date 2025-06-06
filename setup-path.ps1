# WiX PATH Setup Script
Write-Host "========================================" -ForegroundColor Green
Write-Host "       WiX PATH Setup Script" -ForegroundColor Green  
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Prüfen ob als Administrator ausgeführt
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")

if (-not $isAdmin) {
    Write-Host "❌ FEHLER: Script muss als Administrator ausgeführt werden!" -ForegroundColor Red
    Write-Host ""
    Write-Host "LÖSUNG:" -ForegroundColor Yellow
    Write-Host "1. PowerShell als Administrator öffnen:" -ForegroundColor Yellow
    Write-Host "   Windows-Taste + X → 'Windows PowerShell (Administrator)'" -ForegroundColor Yellow
    Write-Host "2. Script erneut ausführen" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Drücke Enter zum Beenden"
    exit 1
}

Write-Host "✓ Administrator-Rechte vorhanden" -ForegroundColor Green

# Mögliche WiX-Pfade prüfen
$wixPaths = @(
    "C:\Program Files (x86)\WiX Toolset v3.11\bin",
    "C:\Program Files\WiX Toolset v3.11\bin",
    "C:\Program Files (x86)\WiX Toolset v3.10\bin",
    "C:\Program Files\WiX Toolset v3.10\bin"
)

$wixPath = $null
foreach ($path in $wixPaths) {
    if (Test-Path $path) {
        $wixPath = $path
        Write-Host "✓ WiX gefunden in: $path" -ForegroundColor Green
        break
    }
}

if (-not $wixPath) {
    Write-Host "❌ FEHLER: WiX Toolset nicht gefunden!" -ForegroundColor Red
    Write-Host ""
    Write-Host "LÖSUNG:" -ForegroundColor Yellow
    Write-Host "1. WiX 3.11 herunterladen von:" -ForegroundColor Yellow
    Write-Host "   https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm" -ForegroundColor Yellow
    Write-Host "2. wix311.exe installieren" -ForegroundColor Yellow
    Write-Host "3. Script erneut ausführen" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Drücke Enter zum Beenden"
    exit 1
}

# Aktuelle PATH-Variable abrufen
$currentPath = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::Machine)

# Prüfen ob WiX bereits in PATH ist
if ($currentPath -like "*$wixPath*") {
    Write-Host "✓ WiX ist bereits in der PATH-Variable!" -ForegroundColor Green
} else {
    Write-Host "➤ Füge WiX zur PATH-Variable hinzu..." -ForegroundColor Yellow
    
    # WiX zur PATH-Variable hinzufügen
    $newPath = $currentPath + ";" + $wixPath
    [Environment]::SetEnvironmentVariable("Path", $newPath, [EnvironmentVariableTarget]::Machine)
    
    Write-Host "✓ WiX erfolgreich zur PATH-Variable hinzugefügt!" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "           ERFOLGREICH!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "WiX Toolset ist jetzt verfügbar!" -ForegroundColor Green
Write-Host ""
Write-Host "WICHTIG:" -ForegroundColor Yellow
Write-Host "- Öffne eine NEUE PowerShell/CMD für Tests" -ForegroundColor Yellow
Write-Host "- Teste mit: candle /?" -ForegroundColor Yellow
Write-Host ""
Write-Host "Bereit für JPackage Installer-Erstellung! 🚀" -ForegroundColor Green
Write-Host ""
Read-Host "Drücke Enter zum Beenden"