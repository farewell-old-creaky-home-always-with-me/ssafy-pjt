param(
    [string] $BaseUrl = "http://localhost:8080",
    [string] $JwtSecret = $env:JWT_SECRET,
    [long] $AdminMemberId = 1,
    [long] $UserMemberId = 2
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($JwtSecret)) {
    $JwtSecret = "test-jwt-secret-key-for-ssafy-home-project-2026"
}

function ConvertTo-Base64Url {
    param([byte[]] $Bytes)

    return [Convert]::ToBase64String($Bytes).TrimEnd("=").Replace("+", "-").Replace("/", "_")
}

function New-SmokeJwt {
    param(
        [long] $MemberId,
        [bool] $IsAdmin
    )

    $now = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
    $headerJson = '{"alg":"HS256","typ":"JWT"}'
    $payloadJson = @{
        sub = [string] $MemberId
        isAdmin = $IsAdmin
        iat = $now
        exp = $now + 3600
    } | ConvertTo-Json -Compress

    $header = ConvertTo-Base64Url ([Text.Encoding]::UTF8.GetBytes($headerJson))
    $payload = ConvertTo-Base64Url ([Text.Encoding]::UTF8.GetBytes($payloadJson))
    $unsignedToken = "$header.$payload"

    $hmac = New-Object System.Security.Cryptography.HMACSHA256 -ArgumentList (, [Text.Encoding]::UTF8.GetBytes($JwtSecret))
    $signature = ConvertTo-Base64Url ($hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($unsignedToken)))

    return "$unsignedToken.$signature"
}

function Invoke-SmokeRequest {
    param(
        [string] $Method,
        [string] $Path,
        [string] $Token,
        [string] $Body
    )

    $headers = @{}
    if (-not [string]::IsNullOrWhiteSpace($Token)) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $request = @{
        Uri = "$BaseUrl$Path"
        Method = $Method
        Headers = $headers
        UseBasicParsing = $true
    }

    if ($Body) {
        $request["ContentType"] = "application/json"
        $request["Body"] = $Body
    }

    try {
        $response = Invoke-WebRequest @request
        return @{
            StatusCode = [int] $response.StatusCode
            Content = $response.Content
        }
    } catch {
        $statusCode = 0
        $content = ""

        if ($_.Exception.Response) {
            $statusCode = [int] $_.Exception.Response.StatusCode
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $content = $reader.ReadToEnd()
            } catch {
                $content = ""
            }
        }

        return @{
            StatusCode = $statusCode
            Content = $content
        }
    }
}

function Assert-Status {
    param(
        [string] $Name,
        [hashtable] $Response,
        [int[]] $Expected
    )

    if ($Expected -notcontains $Response.StatusCode) {
        throw "$Name failed. expected=$($Expected -join ',') actual=$($Response.StatusCode) body=$($Response.Content)"
    }

    Write-Host "[PASS] $Name -> $($Response.StatusCode)"
}

$adminToken = New-SmokeJwt -MemberId $AdminMemberId -IsAdmin $true
$userToken = New-SmokeJwt -MemberId $UserMemberId -IsAdmin $false
$noticeBody = @{
    title = "Gateway smoke test"
    content = "Created by scripts/gateway-smoke-test.ps1"
} | ConvertTo-Json -Compress

Assert-Status "JWT missing returns 401" `
    (Invoke-SmokeRequest -Method "GET" -Path "/api/notices" -Token $null) `
    @(401)

Assert-Status "Invalid JWT returns 401" `
    (Invoke-SmokeRequest -Method "GET" -Path "/api/notices" -Token "invalid-token") `
    @(401)

Assert-Status "User JWT cannot access /api/admin/**" `
    (Invoke-SmokeRequest -Method "POST" -Path "/api/admin/batch/region-codes" -Token $userToken) `
    @(403)

Assert-Status "GET /api/notices routes to main-service" `
    (Invoke-SmokeRequest -Method "GET" -Path "/api/notices?page=1&size=5" -Token $userToken) `
    @(200)

Assert-Status "GET /api/houses routes to main-service" `
    (Invoke-SmokeRequest -Method "GET" -Path "/api/houses?regionCode=1168010100&page=1&size=5" -Token $userToken) `
    @(200)

Assert-Status "User JWT cannot write notices" `
    (Invoke-SmokeRequest -Method "POST" -Path "/api/notices" -Token $userToken -Body $noticeBody) `
    @(403)

$createResponse = Invoke-SmokeRequest -Method "POST" -Path "/api/notices" -Token $adminToken -Body $noticeBody
Assert-Status "Admin JWT can write notices through admin-service" $createResponse @(201)

$createdNotice = $createResponse.Content | ConvertFrom-Json
if ($createdNotice.noticeId) {
    Assert-Status "Admin JWT can delete created notice through admin-service" `
        (Invoke-SmokeRequest -Method "DELETE" -Path "/api/notices/$($createdNotice.noticeId)" -Token $adminToken) `
        @(204)
}

Write-Host "Gateway smoke test completed."
