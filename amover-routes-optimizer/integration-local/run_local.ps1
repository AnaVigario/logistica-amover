<#
=====================================================================
 run_local.ps1  -  A-MoVeR: stack LOCAL para teste de integracao (one-click)
=====================================================================
 GARANTIAS DE SEGURANCA (por design):
   * NUNCA escreve na base de dados que esta a ser usada.
     O unico acesso a essa BD e um pg_dump  ->  SO LEITURA.
   * Os dados sao carregados APENAS no Postgres LOCAL (container amover-postgres).
   * NUNCA faz commit/push para o GitHub. So usa o repositorio local.
   * Tudo corre em contentores Docker locais; nada vai para producao.

 O QUE FAZ:
   1. Confirma que o Docker esta a correr.
   2. Garante o repo local (clona se faltar -- clone e leitura, nao altera o GitHub).
   3. Gera um docker-compose.override.yml que faz o stack usar o TEU motor.
   4. docker compose up -d --build  (Postgres + Keycloak + backend + frontend + otimizador, tudo local).
   5. (Opcional, -WithFreshData) Copia DADOS da BD atual (leitura) -> Postgres LOCAL.
   6. Corre testes de integracao e mostra PASS/FAIL.

 EXEMPLOS:
   # so levantar e testar (dados que o EF cria):
   .\run_local.ps1 -KcUser testuser -KcPass "MinhaPass123"

   # RECOMENDADO: clonar a BD a partir do .sql que o colega te enviou
   # (NAO se liga a maquina dele; so carrega no Postgres local):
   .\run_local.ps1 -DumpFile "D:\amover_data.sql" -KcUser testuser -KcPass "MinhaPass123"

   # (Alternativa) puxar dados direto de uma BD remota (leitura):
   .\run_local.ps1 -WithFreshData -SrcHost <ip> -SrcUser postgres -SrcDb amover-data `
                   -KcUser testuser -KcPass "MinhaPass123"
=====================================================================
#>
param(
    [string]$RepoPath   = "D:\logistica-amover",
    [string]$EnginePath = "D:\amover-routes-optimizer",
    [string]$RepoUrl    = "https://github.com/AnaVigario/logistica-amover.git",

    # --- Dados (opcional) ---
    # Opcao A (recomendada): -DumpFile  -> carrega o .sql que o colega te enviou.
    #   NAO se liga a maquina de ninguem; so escreve no Postgres LOCAL.
    [string]$DumpFile,
    # Opcao B: -WithFreshData -> faz pg_dump (leitura) direto a uma BD remota.
    [switch]$WithFreshData,
    [string]$SrcHost,
    [int]   $SrcPort = 5432,
    [string]$SrcUser,
    [string]$SrcDb,

    # --- Teste de integracao (opcional) ---
    [string]$KcUser,
    [string]$KcPass,
    [int]   $VehicleId = 1,
    [string]$TaskIds   = "1,2,3"
)

$ErrorActionPreference = "Stop"
function Step($m){ Write-Host "`n=== $m ===" -ForegroundColor Cyan }
function Ok($m){ Write-Host "  [OK] $m" -ForegroundColor Green }
function Warn($m){ Write-Host "  [!] $m" -ForegroundColor Yellow }
function Die($m){ Write-Host "  [X] $m" -ForegroundColor Red; exit 1 }

# docker compose v2 ("docker compose") com fallback para v1 ("docker-compose")
function Compose { param([Parameter(ValueFromRemainingArguments=$true)]$a)
    & docker compose @a; if ($LASTEXITCODE -ne 0) { & docker-compose @a }
}

# ---------------------------------------------------------------------
Step "1/6  Verificar Docker"
try { docker info *> $null; if ($LASTEXITCODE -ne 0) { throw } ; Ok "Docker a correr" }
catch { Die "O Docker Desktop nao esta a correr. Abre-o e tenta de novo." }

# ---------------------------------------------------------------------
Step "2/6  Repositorio local"
if (-not (Test-Path (Join-Path $RepoPath "docker-compose.yml"))) {
    if (Test-Path $RepoPath) { Die "$RepoPath existe mas nao tem docker-compose.yml. Aponta -RepoPath para o repo certo." }
    Warn "Repo nao encontrado. A clonar (so leitura, nao altera o GitHub)..."
    git clone $RepoUrl $RepoPath; if ($LASTEXITCODE -ne 0) { Die "git clone falhou." }
}
if (-not (Test-Path (Join-Path $EnginePath "Dockerfile.python"))) {
    Die "Nao encontrei o teu motor em $EnginePath (falta Dockerfile.python). Usa -EnginePath."
}
Ok "Repo: $RepoPath"
Ok "Motor: $EnginePath"

# ---------------------------------------------------------------------
Step "3/6  Ativar o teu motor no stack (override)"
# Caminho absoluto com barras '/', para o build context funcionar em qualquer layout.
$ctx = ($EnginePath -replace '\\','/')
$override = @"
# GERADO por run_local.ps1 -- nao editar a mao.
# Faz o servico routes-optimizer usar o teu motor (todos os algoritmos).
services:
  routes-optimizer:
    build:
      context: "$ctx"
      dockerfile: Dockerfile.python
"@
$overridePath = Join-Path $RepoPath "docker-compose.override.yml"
Set-Content -Path $overridePath -Value $override -Encoding UTF8
Ok "override escrito: $overridePath"

# ---------------------------------------------------------------------
Step "4/6  Levantar a stack (docker compose up -d --build)"
Push-Location $RepoPath
try { Compose up -d --build; if ($LASTEXITCODE -ne 0) { Die "docker compose up falhou. Ve 'docker compose logs'." } }
finally { Pop-Location }
Ok "Contentores a arrancar"

function Wait-Url($url, $name, $timeoutSec = 180) {
    $deadline = (Get-Date).AddSeconds($timeoutSec)
    while ((Get-Date) -lt $deadline) {
        try { Invoke-WebRequest -UseBasicParsing -TimeoutSec 5 -Uri $url *> $null; Ok "$name pronto"; return $true }
        catch { Start-Sleep -Seconds 4 }
    }
    Warn "$name nao respondeu a tempo ($url). Continua, mas verifica os logs."
    return $false
}
Step "    A aguardar pelos servicos"
Wait-Url "http://localhost:8080/realms/amover-realm/.well-known/openid-configuration" "Keycloak" 240 | Out-Null
Wait-Url "http://localhost:5000/health" "Otimizador" 120 | Out-Null
Wait-Url "http://localhost:5029/swagger/index.html" "Backend .NET" 240 | Out-Null

# ---------------------------------------------------------------------
Step "5/6  Dados (opcional)"
if ($DumpFile) {
    if (-not (Test-Path $DumpFile)) { Die "Ficheiro de dump nao encontrado: $DumpFile" }
    Warn "A carregar o dump fornecido APENAS no Postgres LOCAL. Nenhuma BD remota e tocada."
    docker cp "$DumpFile" amover-postgres:/tmp/amover_data.sql
    docker exec amover-postgres psql -U postgres -d amover-data -v ON_ERROR_STOP=0 -f /tmp/amover_data.sql
    Ok "Dados do ficheiro '$DumpFile' carregados no Postgres LOCAL."
}
elseif ($WithFreshData) {
    if (-not $SrcHost -or -not $SrcUser -or -not $SrcDb) { Die "-WithFreshData precisa de -SrcHost, -SrcUser e -SrcDb." }
    $sec = Read-Host "Password da BD ATUAL (so para LEITURA, $SrcUser@$SrcHost)" -AsSecureString
    $pw  = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($sec))

    # localhost da BD atual: dentro do container e preciso host.docker.internal
    $effHost = $SrcHost
    if ($SrcHost -in @("localhost","127.0.0.1")) { $effHost = "host.docker.internal"; Warn "SrcHost local -> uso host.docker.internal dentro do container" }

    Warn "pg_dump = SO LEITURA. A BD atual NAO e modificada."
    $dumpFile = Join-Path $RepoPath "amover_data.sql"
    # pg_dump corre num container postgres:17 e escreve o ficheiro no host (bind mount).
    docker run --rm -e PGPASSWORD=$pw -v "$($RepoPath -replace '\\','/'):/out" postgres:17 `
        pg_dump --data-only --no-owner --disable-triggers `
        -h $effHost -p $SrcPort -U $SrcUser -d $SrcDb -f /out/amover_data.sql
    if ($LASTEXITCODE -ne 0) { Die "pg_dump falhou (verifica host/porta/credenciais e que a BD aceita ligacoes)." }
    Ok "Dump (so dados) criado: $dumpFile"

    # Carregar APENAS no Postgres LOCAL.
    docker cp $dumpFile amover-postgres:/tmp/amover_data.sql
    docker exec amover-postgres psql -U postgres -d amover-data -v ON_ERROR_STOP=0 -f /tmp/amover_data.sql
    Ok "Dados carregados no Postgres LOCAL (amover-postgres). A BD atual ficou intacta."
} else {
    Warn "Sem -DumpFile nem -WithFreshData: usa os dados que o backend (EF) criar."
    Warn "Para clonar os dados do colega: pede-lhe o .sql e corre com  -DumpFile <ficheiro.sql>"
}

# ---------------------------------------------------------------------
Step "6/6  Testes de integracao"
$pass = $true

# (a) Otimizador direto -- /health
try { $h = Invoke-RestMethod "http://localhost:5000/health"; Ok "Otimizador /health: $($h.status)" }
catch { $pass=$false; Warn "Otimizador /health falhou: $($_.Exception.Message)" }

# (b) Otimizador -- /optimize no formato legado (o mesmo que o .NET envia)
try {
    $body = '{"nodes":[{"id":0,"x":41.5454,"y":-8.4265,"demand":0},{"id":101,"x":41.5510,"y":-8.4200,"demand":1},{"id":102,"x":41.5380,"y":-8.4310,"demand":1}],"vehicles":[{"capacity":9999,"battery_kwh":9999.0}]}'
    $r = Invoke-RestMethod -Method Post -Uri "http://localhost:5000/optimize" -Body $body -ContentType "application/json"
    Ok "Otimizador /optimize: route=$($r.route -join ',')  algoritmo=$($r.algorithm_used)"
} catch { $pass=$false; Warn "Otimizador /optimize falhou: $($_.Exception.Message)" }

# (c) Fluxo completo via backend .NET (precisa de utilizador/password do Keycloak)
if ($KcUser -and $KcPass) {
    try {
        $tokBody = @{ client_id="amover-api"; grant_type="password"; username=$KcUser; password=$KcPass }
        $tok = Invoke-RestMethod -Method Post -ContentType "application/x-www-form-urlencoded" `
               -Uri "http://localhost:8080/realms/amover-realm/protocol/openid-connect/token" -Body $tokBody
        Ok "Token Keycloak obtido"
        $hdr = @{ Authorization = "Bearer $($tok.access_token)" }
        $ids = @($TaskIds.Split(",") | ForEach-Object { [int]$_ })
        $opt = @{ vehicleId=$VehicleId; date=(Get-Date -Format 'yyyy-MM-dd'); taskIds=$ids } | ConvertTo-Json
        $resp = Invoke-RestMethod -Method Post -Headers $hdr -ContentType "application/json" `
                -Uri "http://localhost:5029/api/route/optimize-for-vehicle" -Body $opt
        Ok "Backend optimize-for-vehicle: $($resp.message)"
        $route = Invoke-RestMethod -Headers $hdr -Uri "http://localhost:5029/api/route?vehicleId=$VehicleId&date=$(Get-Date -Format 'yyyy-MM-dd')"
        Ok "Backend GET /api/route devolveu $((@($route)).Count) grupo(s) de rota"
    } catch { $pass=$false; Warn "Fluxo backend falhou: $($_.Exception.Message)  (utilizador tem password+role? ver Keycloak)" }
} else {
    Warn "Sem -KcUser/-KcPass: saltei o teste via backend. Define a password no Keycloak (admin/admin) e corre de novo."
}

Write-Host ""
if ($pass) { Write-Host "RESULTADO: integracao LOCAL a funcionar." -ForegroundColor Green }
else { Write-Host "RESULTADO: ha falhas acima. Logs uteis:" -ForegroundColor Yellow }
Write-Host "  docker logs amover-routes-optimizer --tail 40"
Write-Host "  docker logs amover-backend --tail 40"
Write-Host "  docker compose -f `"$RepoPath\docker-compose.yml`" ps"
Write-Host "`nPara desligar tudo:  cd `"$RepoPath`"; docker compose down       (apagar dados locais: down -v)"
