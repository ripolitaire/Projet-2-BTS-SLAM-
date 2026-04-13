param(
  [Parameter(Mandatory = $true)]
  [string]$Path
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Convert-PdfLiteralString {
  param([Parameter(Mandatory = $true)][string]$s)

  $out = New-Object System.Text.StringBuilder
  for ($i = 0; $i -lt $s.Length; $i++) {
    $ch = $s[$i]
    if ($ch -ne '\') {
      [void]$out.Append($ch)
      continue
    }

    if ($i -ge $s.Length - 1) { break }
    $i++
    $esc = $s[$i]
    switch ($esc) {
      '\' { [void]$out.Append('\') }
      '(' { [void]$out.Append('(') }
      ')' { [void]$out.Append(')') }
      'n' { [void]$out.Append("`n") }
      'r' { [void]$out.Append("`r") }
      't' { [void]$out.Append("`t") }
      'b' { [void]$out.Append([char]8) }
      'f' { [void]$out.Append([char]12) }
      default {
        if ($esc -match '[0-7]') {
          $oct = [string]$esc
          for ($j = 0; $j -lt 2 -and ($i + 1) -lt $s.Length; $j++) {
            $peek = $s[$i + 1]
            if ($peek -match '[0-7]') {
              $oct += $peek
              $i++
            } else {
              break
            }
          }
          try {
            $val = [Convert]::ToInt32($oct, 8)
            [void]$out.Append([char]$val)
          } catch {
            [void]$out.Append($oct)
          }
        } else {
          [void]$out.Append($esc)
        }
      }
    }
  }

  return $out.ToString()
}

function Try-Inflate {
  param([byte[]]$Data)
  try {
    $ms = New-Object IO.MemoryStream(,$Data)
    try {
      $ds = New-Object IO.Compression.DeflateStream($ms, [IO.Compression.CompressionMode]::Decompress)
      try {
        $outMs = New-Object IO.MemoryStream
        $ds.CopyTo($outMs)
        return ,$outMs.ToArray()
      } finally {
        $outMs.Dispose()
      }
    } finally {
      $ds.Dispose()
      $ms.Dispose()
    }
  } catch {
    return $null
  }
}

function Inflate-Zlib {
  param([byte[]]$Data)
  $out = Try-Inflate -Data $Data
  if ($out) { return ,$out }
  if ($Data.Length -gt 6) {
    $trim = $Data[2..($Data.Length - 1)]
    $out2 = Try-Inflate -Data $trim
    if ($out2) { return ,$out2 }
  }
  throw "Failed to inflate Flate stream (len=$($Data.Length))."
}

function Extract-TextFromContentStream {
  param([Parameter(Mandatory = $true)][byte[]]$StreamBytes)

  $enc = [Text.Encoding]::GetEncoding('ISO-8859-1')
  $s = $enc.GetString($StreamBytes)

  $sb = New-Object System.Text.StringBuilder

  $m1 = [regex]::Matches($s, '\((?<txt>(?:\\.|[^\\)])*)\)\s*Tj')
  foreach ($m in $m1) {
    $t = Convert-PdfLiteralString $m.Groups['txt'].Value
    if ($t.Trim().Length -gt 0) { [void]$sb.AppendLine($t) }
  }

  $m2 = [regex]::Matches($s, '\[(?<arr>.*?)\]\s*TJ', [System.Text.RegularExpressions.RegexOptions]::Singleline)
  foreach ($m in $m2) {
    $arr = $m.Groups['arr'].Value
    $parts = [regex]::Matches($arr, '\((?<txt>(?:\\.|[^\\)])*)\)')
    if ($parts.Count -eq 0) { continue }
    $line = New-Object System.Text.StringBuilder
    foreach ($p in $parts) {
      $t = Convert-PdfLiteralString $p.Groups['txt'].Value
      [void]$line.Append($t)
    }
    $lineStr = $line.ToString()
    if ($lineStr.Trim().Length -gt 0) { [void]$sb.AppendLine($lineStr) }
  }

  return $sb.ToString()
}

if (-not (Test-Path -LiteralPath $Path)) {
  throw "File not found: $Path"
}

$pdfBytes = [IO.File]::ReadAllBytes($Path)
$raw = [Text.Encoding]::ASCII.GetString($pdfBytes)

$allText = New-Object System.Text.StringBuilder

$pos = 0
while ($true) {
  $idx = $raw.IndexOf("stream", $pos, [System.StringComparison]::Ordinal)
  if ($idx -lt 0) { break }

  $end = $raw.IndexOf("endstream", $idx, [System.StringComparison]::Ordinal)
  if ($end -lt 0) { break }

  $dictStart = $raw.LastIndexOf("<<", $idx, [System.StringComparison]::Ordinal)
  $dictEnd = $raw.LastIndexOf(">>", $idx, [System.StringComparison]::Ordinal)
  $dict = $null
  if ($dictStart -ge 0 -and $dictEnd -ge $dictStart) {
    $dict = $raw.Substring($dictStart, $dictEnd - $dictStart + 2)
  }

  $isFlate = $false
  if ($dict) {
    if ($dict -match '/FlateDecode') { $isFlate = $true }
  }

  $dataStart = $idx + 6
  if ($dataStart -lt $pdfBytes.Length -and $pdfBytes[$dataStart] -eq 13 -and $pdfBytes[$dataStart + 1] -eq 10) {
    $dataStart += 2
  } elseif ($dataStart -lt $pdfBytes.Length -and $pdfBytes[$dataStart] -eq 10) {
    $dataStart += 1
  }

  $dataEnd = $end
  while ($dataEnd -gt 0 -and ($pdfBytes[$dataEnd - 1] -eq 13 -or $pdfBytes[$dataEnd - 1] -eq 10)) {
    $dataEnd--
  }

  if ($dataEnd -gt $dataStart -and $dataEnd -le $pdfBytes.Length) {
    $streamData = $pdfBytes[$dataStart..($dataEnd - 1)]
    try {
      $decoded = $streamData
      if ($isFlate) {
        $decoded = Inflate-Zlib -Data $streamData
      }
      $text = Extract-TextFromContentStream -StreamBytes $decoded
      if ($text.Trim().Length -gt 0) {
        [void]$allText.AppendLine($text)
      }
    } catch {
      # ignore stream decode errors
    }
  }

  $pos = $end + 9
}

$allText.ToString()
