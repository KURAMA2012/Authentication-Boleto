@echo off
echo ============================================
echo Build Angular em modo producao
echo ============================================

REM Build Angular em produção
ng build --configuration production
IF %ERRORLEVEL% NEQ 0 (
    echo Erro no build do Angular!
    pause
    exit /b %ERRORLEVEL%
)

echo ============================================
echo Sincronizando com Capacitor
echo ============================================

REM Sincroniza Android/iOS com Capacitor
npx cap sync
IF %ERRORLEVEL% NEQ 0 (
    echo Erro ao sincronizar com Capacitor!
    pause
    exit /b %ERRORLEVEL%
)

echo ============================================
echo Build e Sync concluídos com sucesso!
echo ============================================
pause
