@echo off
REM Script para subir Spring Boot com Swagger UI
REM =============================================

echo.
echo ===================================================
echo  Desafio Fullstack Integrado - Spring Boot
echo ===================================================
echo.

setlocal enabledelayedexpansion

cd /d "d:\desafio\DesafioFullstackIntegrado\backend-module"

echo [1/2] Compilando e instalando dependencias...
echo.

call mvn clean install -DskipTests

if errorlevel 1 (
    echo.
    echo [ERRO] Falha na compilacao!
    echo.
    pause
    exit /b 1
)

echo.
echo [2/2] Iniciando Spring Boot...
echo.
echo Aguarde alguns segundos para o servidor iniciar...
echo.
echo URLs disponiveis:
echo - API:              http://localhost:8080/api/v1/beneficios
echo - Swagger UI:       http://localhost:8080/swagger-ui.html
echo - H2 Console:       http://localhost:8080/h2-console
echo - API Docs (JSON):  http://localhost:8080/v3/api-docs
echo.
echo ===================================================
echo.

call mvn spring-boot:run

pause
