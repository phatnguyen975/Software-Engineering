class ProductTest {
  [string]$baseUrl
  [string]$rootPath
  [string]$outputDir

  ProductTest([string]$baseUrl) {
    $this.baseUrl = $baseUrl
    $this.rootPath = Split-Path -Parent $PSCommandPath
    $this.outputDir = Join-Path $this.rootPath "response\product"

    if (-not (Test-Path $this.outputDir)) {
      New-Item -ItemType Directory -Path $this.outputDir | Out-Null
    }
  }

  [void] ShowResponse([string]$response, [string]$outputFile) {
    try {
      # Convert raw JSON to object
      $parsed = $response | ConvertFrom-Json
      # Pretty print JSON
      $pretty = $parsed | ConvertTo-Json -Depth 5

      # Show on terminal
      Write-Host "`n$pretty" -ForegroundColor Green

      # Save pretty JSON to file
      $pretty | Out-File -FilePath $outputFile -Encoding utf8
    }
    catch {
      Write-Host "`n$response" -ForegroundColor Green
      $response | Out-File -FilePath $outputFile -Encoding utf8
    }

    $relativePath = (Resolve-Path $outputFile).Path.Substring($this.rootPath.Length + 1)
    Write-Host "`n✅ Response saved to $relativePath" -ForegroundColor Yellow
  }

  ##############################################
  # [GET] /api/products?page=&limit=&q=
  ##############################################
  [void] GetProducts() {
    Write-Host "`n--- Get Products ---"

    $page = Read-Host "Enter page (optional, press Enter to skip)"
    $limit = Read-Host "Enter limit (optional, press Enter to skip)"
    $q = Read-Host "Enter search query q (optional, press Enter to skip)"

    $query = @()

    if ($page)  { $query += "page=$page" }
    if ($limit) { $query += "limit=$limit" }
    if ($q)     { $query += "q=$q" }

    $queryString = ""
    if ($query.Count -gt 0) {
      $queryString = "?" + ($query -join "&")
    }

    $url = "$($this.baseUrl)/products$queryString"

    Write-Host "`n📡 GET $url" -ForegroundColor Yellow

    $response = curl -s -X GET $url

    $outputFile = Join-Path $this.outputDir "getProducts.json"
    $this.ShowResponse($response, $outputFile)
  }

  ##############################################
  # [GET] /api/products/:id
  ##############################################
  [void] GetProductById() {
    Write-Host "`n--- Get Product By ID ---"
    $id = Read-Host "Enter product ID"

    $url = "$($this.baseUrl)/products/$id"

    Write-Host "`n📡 GET $url" -ForegroundColor Yellow

    $response = curl -s -X GET $url

    $outputFile = Join-Path $this.outputDir "getProductById.json"
    $this.ShowResponse($response, $outputFile)
  }

  ##############################################
  # [POST] /api/products
  ##############################################
  [void] CreateProduct() {
    Write-Host "`n--- Create Product ---"
    $name = Read-Host "Enter product name"
    $description = Read-Host "Enter product description"
    $price = Read-Host "Enter product price (number only)"

    $data = @{
      name = $name
      description = $description
      price = [double]$price
    } | ConvertTo-Json

    $url = "$($this.baseUrl)/products"

    Write-Host "`n📡 POST $url" -ForegroundColor Yellow

    $response = curl -s -X POST $url `
      -H "Content-Type: application/json" `
      -d $data

    $outputFile = Join-Path $this.outputDir "createProduct.json"
    $this.ShowResponse($response, $outputFile)
  }

  ##############################################
  # [PUT] /api/products/:id
  ##############################################
  [void] UpdateProduct() {
    Write-Host "`n--- Update Product ---"

    $id = Read-Host "Enter product ID"

    $name = Read-Host "Enter name (optional)"
    $description = Read-Host "Enter description (optional)"
    $price = Read-Host "Enter price (optional)"

    $payload = @{}

    if ($name)        { $payload.name = $name }
    if ($description) { $payload.description = $description }
    if ($price)       { $payload.price = [double]$price }

    if ($payload.Count -eq 0) {
      Write-Host "❌ No fields to update"
      return
    }

    $data = $payload | ConvertTo-Json

    $url = "$($this.baseUrl)/products/$id"

    Write-Host "`n📡 PUT $url" -ForegroundColor Yellow

    $response = curl -s -X PUT $url `
      -H "Content-Type: application/json" `
      -d $data

    $outputFile = Join-Path $this.outputDir "updateProduct.json"
    $this.ShowResponse($response, $outputFile)
  }

  ##############################################
  # [DELETE] /api/products/:id
  ##############################################
  [void] DeleteProduct() {
    Write-Host "`n--- Delete Product ---"
    $id = Read-Host "Enter product ID"

    $url = "$($this.baseUrl)/products/$id"

    Write-Host "`n📡 DELETE $url" -ForegroundColor Yellow

    $response = curl -s -X DELETE $url

    $outputFile = Join-Path $this.outputDir "deleteProduct.json"
    $this.ShowResponse($response, $outputFile)
  }

  ##############################################
  # MAIN MENU
  ##############################################
  [void] Run() {
    Write-Host "--- Product API Tests ---"
    Write-Host "[1] Get Products"
    Write-Host "[2] Get Product By ID"
    Write-Host "[3] Create Product"
    Write-Host "[4] Update Product"
    Write-Host "[5] Delete Product"

    $choice = Read-Host "Select a test"

    switch ($choice) {
      1 { $this.GetProducts() }
      2 { $this.GetProductById() }
      3 { $this.CreateProduct() }
      4 { $this.UpdateProduct() }
      5 { $this.DeleteProduct() }
      default { Write-Host "❌ Invalid choice" }
    }
  }
}

$baseUrl = "http://localhost:4000/api"
$tester = [ProductTest]::new($baseUrl)
$tester.Run()
