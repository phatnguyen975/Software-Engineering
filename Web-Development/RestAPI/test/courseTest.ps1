class CourseTest {
  [string]$baseUrl
  [string]$rootPath
  [string]$outputDir
  [string]$apiKey

  CourseTest([string]$baseUrl, [string]$apiKey) {
    $this.baseUrl = $baseUrl
    $this.apiKey = $apiKey
    $this.rootPath = Split-Path -Parent $PSCommandPath
    $this.outputDir = Join-Path $this.rootPath "response/course"

    if (-not (Test-Path $this.outputDir)) {
      New-Item -ItemType Directory -Path $this.outputDir | Out-Null
    }
  }

  [void] ShowResponse([string]$response, [string]$outputFile) {
    try {
      $parsed = $response | ConvertFrom-Json
      $pretty = $parsed | ConvertTo-Json -Depth 10
      Write-Host "`n$pretty" -ForegroundColor Green
      $pretty | Out-File -FilePath $outputFile -Encoding utf8
    }
    catch {
      Write-Host "`n$response" -ForegroundColor Green
      $response | Out-File -FilePath $outputFile -Encoding utf8
    }

    $relativePath = (Resolve-Path $outputFile).Path.Substring($this.rootPath.Length + 1)
    Write-Host "`n✅ Response saved to $relativePath" -ForegroundColor Yellow
  }

  ##################################################
  # [GET] /courses?offset=&limit=
  ##################################################
  [void] GetCourses() {
    Write-Host "`n--- Get All Courses ---"

    $offset = Read-Host "Enter offset"
    $limit = Read-Host "Enter limit"

    if (-not $offset) { $offset = 0 }
    if (-not $limit) { $limit = 7 }

    $url = "$($this.baseUrl)/courses?offset=$offset&limit=$limit"

    Write-Host "📡 GET $url" -ForegroundColor Yellow

    $response = curl -s -X GET $url `
      -H "x-api-key: $($this.apiKey)"

    $outputFile = Join-Path $this.outputDir "getCourses.json"
    $this.ShowResponse($response, $outputFile)
  }

  ##################################################
  # [POST] /courses/create
  ##################################################
  [void] CreateCourse() {
    Write-Host "`n--- Create Course ---"

    $name = Read-Host "Enter name"
    $description = Read-Host "Enter description (optional)"
    $courseImage = Read-Host "Enter course image (URL)"
    $instructor = Read-Host "Enter instructor name"
    $price = Read-Host "Enter price"
    $rating = Read-Host "Enter rating"

    $body = @{}
    if ($name) { $body.name = $name }
    if ($description) { $body.description = $description }
    if ($courseImage) { $body.courseImage = $courseImage }
    if ($instructor) { $body.instructor = $instructor }
    if ($price) { $body.price = [double]$price }
    if ($rating) { $body.rating = [double]$rating }

    $data = $body | ConvertTo-Json

    $url = "$($this.baseUrl)/courses/create"

    Write-Host "`n📡 POST $url" -ForegroundColor Yellow

    $response = curl -s -X POST $url `
      -H "Content-Type: application/json" `
      -H "x-api-key: $($this.apiKey)" `
      -d $data

    $outputFile = Join-Path $this.outputDir "createCourse.json"
    $this.ShowResponse($response, $outputFile)
  }

  ##################################################
  # MENU
  ##################################################
  [void] Run() {
    Write-Host "--- Course API Tests ---"
    Write-Host "[1] Create Course"
    Write-Host "[2] Get All Courses"

    $choice = Read-Host "Select a test"

    switch ($choice) {
      1 { $this.CreateCourse() }
      2 { $this.GetCourses() }
      default { Write-Host "❌ Invalid choice" }
    }
  }
}

$baseUrl = "http://localhost:3000/api"
$apiKey = Read-Host "Enter API_KEY"

$tester = [CourseTest]::new($baseUrl, $apiKey)
$tester.Run()
