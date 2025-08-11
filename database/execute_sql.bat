@echo off
echo Executando script SQL para recriar tabela base_despesa_licitacao...

set PGPASSWORD=setc@2025

cd /d "%~dp0"
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h 172.28.65.26 -p 5432 -U gideon -d "setc@bd" -f "recreate_base_despesa_licitacao_table.sql"

echo.
echo Script executado!
pause
