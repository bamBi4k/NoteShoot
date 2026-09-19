$scriptPath = "E:\NoteShoot\make-icons.ps1"

$scriptContent = @'
param(
    [Parameter(Mandatory=$true)][string]$Foreground,
    [Parameter(Mandatory=$true)][string]$Background,
    [string]$ResPath = "E:\NoteShoot\app\src\main\res"
)

Add-Type -AssemblyName System.Drawing

if (-not (Test-Path -LiteralPath $Foreground)) { Write-Error "Foreground not found: $Foreground"; exit 1 }
if (-not (Test-Path -LiteralPath $Background)) { Write-Error "Background not found: $Background"; exit 1 }
if (-not (Test-Path -LiteralPath $ResPath))    { Write-Error "Res path not found: $ResPath"; exit 1 }

$densities = [ordered]@{
    "mdpi"    = 48
    "hdpi"    = 72
    "xhdpi"   = 96
    "xxhdpi"  = 144
    "xxxhdpi" = 192
}

$fgImg = [System.Drawing.Image]::FromFile((Resolve-Path -LiteralPath $Foreground).Path)
$bgImg = [System.Drawing.Image]::FromFile((Resolve-Path -LiteralPath $Background).Path)

Write-Host ""
Write-Host "Foreground: $Foreground ($($fgImg.Width)x$($fgImg.Height))" -ForegroundColor Cyan
Write-Host "Background: $Background ($($bgImg.Width)x$($bgImg.Height))" -ForegroundColor Cyan
Write-Host ""

foreach ($density in $densities.Keys) {
    $size = $densities[$density]
    $folder = Join-Path $ResPath "mipmap-$density"

    if (-not (Test-Path -LiteralPath $folder)) {
        New-Item -ItemType Directory -Path $folder -Force | Out-Null
    }

    # Composite: background fills the square, foreground at 66% centered
    $bmp = New-Object System.Drawing.Bitmap $size, $size
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

    $g.DrawImage($bgImg, 0, 0, $size, $size)

    $fgSize = [int]($size * 0.66)
    $offset = [int](($size - $fgSize) / 2)
    $g.DrawImage($fgImg, $offset, $offset, $fgSize, $fgSize)
    $g.Dispose()

    $bmp.Save((Join-Path $folder "ic_launcher.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Save((Join-Path $folder "ic_launcher_round.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()

    # Also save a foreground-only variant
    $fgbmp = New-Object System.Drawing.Bitmap $size, $size
    $fgg = [System.Drawing.Graphics]::FromImage($fgbmp)
    $fgg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $fgg.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $fgg.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $fgg.DrawImage($fgImg, $offset, $offset, $fgSize, $fgSize)
    $fgg.Dispose()
    $fgbmp.Save((Join-Path $folder "ic_launcher_foreground.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $fgbmp.Dispose()

    Write-Host "  $density -> ${size}x${size} OK" -ForegroundColor Green
}

# Copy originals into drawable for adaptive-icon XML
$drawable = Join-Path $ResPath "drawable"
if (-not (Test-Path -LiteralPath $drawable)) {
    New-Item -ItemType Directory -Path $drawable -Force | Out-Null
}
Copy-Item -LiteralPath $Foreground -Destination (Join-Path $drawable "ic_launcher_foreground.png") -Force
Copy-Item -LiteralPath $Background -Destination (Join-Path $drawable "ic_launcher_background.png") -Force

$fgImg.Dispose()
$bgImg.Dispose()

Write-Host ""
Write-Host "Done. Icons generated and sources copied to drawable/." -ForegroundColor Cyan
'@

Set-Content -Path $scriptPath -Value $scriptContent -Encoding UTF8

Write-Host "Wrote $scriptPath" -ForegroundColor Green