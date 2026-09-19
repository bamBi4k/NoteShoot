<#
.SYNOPSIS
    Safely resets the app icon resources in an Android Studio project.

.DESCRIPTION
    Deletes only icon-related files and directories under res/:
      - mipmap-* directories (all density variants)
      - drawable/adaptive-foreground.png
      - adaptive-foreground.png at res/ root

    Everything else in res/ is left untouched.

.PARAMETER Force
    Skip the confirmation prompt. Use with caution.

.EXAMPLE
    .\reset-icons.ps1
    Prompts to confirm before deleting.

.EXAMPLE
    .\reset-icons.ps1 -Force
    Deletes without prompting.
#>

[CmdletBinding()]
param(
    [switch]$Force
)

# ---- Config ----
$ResPath = "E:\NoteShoot\app\src\main\res"

# ---- Sanity checks ----
if (-not (Test-Path -LiteralPath $ResPath)) {
    Write-Host "ERROR: res path not found: $ResPath" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Target: $ResPath" -ForegroundColor Cyan
Write-Host ""

# ---- Collect what we would delete ----
$targets = @()

# All mipmap-* directories (any density)
$mipmapDirs = Get-ChildItem -LiteralPath $ResPath -Directory -Filter "mipmap-*" -ErrorAction SilentlyContinue
foreach ($d in $mipmapDirs) { $targets += $d }

# Specific files inside drawable/
$drawableAdaptive = Join-Path $ResPath "drawable\adaptive-foreground.png"
if (Test-Path -LiteralPath $drawableAdaptive) {
    $targets += Get-Item -LiteralPath $drawableAdaptive
}

# Root-level stray file (from your screenshot)
$rootAdaptive = Join-Path $ResPath "adaptive-foreground.png"
if (Test-Path -LiteralPath $rootAdaptive) {
    $targets += Get-Item -LiteralPath $rootAdaptive
}

# ---- Report ----
if ($targets.Count -eq 0) {
    Write-Host "Nothing to delete. No icon resources found." -ForegroundColor Yellow
    exit 0
}

Write-Host "The following will be DELETED:" -ForegroundColor Yellow
Write-Host ""
foreach ($t in $targets) {
    $type = if ($t.PSIsContainer) { "[DIR] " } else { "[FILE]" }
    Write-Host "  $type $($t.FullName)" -ForegroundColor White
}
Write-Host ""
Write-Host "Total: $($targets.Count) item(s)" -ForegroundColor Yellow
Write-Host ""

# ---- Confirm ----
if (-not $Force) {
    $confirm = Read-Host "Proceed with deletion? Type YES to confirm"
    if ($confirm -ne "YES") {
        Write-Host "Cancelled. Nothing was deleted." -ForegroundColor Cyan
        exit 0
    }
}

# ---- Delete ----
$deleted = 0
$failed = 0

foreach ($t in $targets) {
    try {
        Remove-Item -LiteralPath $t.FullName -Recurse -Force -ErrorAction Stop
        Write-Host "  Deleted: $($t.FullName)" -ForegroundColor Green
        $deleted++
    }
    catch {
        Write-Host "  FAILED:  $($t.FullName) -- $($_.Exception.Message)" -ForegroundColor Red
        $failed++
    }
}

Write-Host ""
Write-Host "Done. Deleted: $deleted, Failed: $failed" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps in Android Studio:" -ForegroundColor Yellow
Write-Host "  1. Right-click res/ -> New -> Image Asset"
Write-Host "  2. Studio will recreate mipmap-* folders with your new icon"
Write-Host ""