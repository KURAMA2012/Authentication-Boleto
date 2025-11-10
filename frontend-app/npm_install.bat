@echo off
title Setup - Ionic + Angular + Capacitor (Angular 17)


REM ============================================
REM Passo 1: Limpar node_modules e package-lock.json
REM ============================================
echo Limpando node_modules e package-lock.json...
IF EXIST node_modules rmdir /s /q node_modules
IF EXIST package-lock.json del /f /q package-lock.json



REM ============================================
REM Passo 2: Limpar cache do npm
REM ============================================
echo Limpando cache do npm...
call npm cache clean --force
IF errorlevel 1 goto :erro

REM ============================================
REM Passo 4: Instalar Ionic CLI localmente
REM ============================================
echo Instalando Ionic CLI localmente...
call npm install  --save-dev --legacy-peer-deps
IF errorlevel 1 goto :erro

:erro
echo =====================================================
echo  Ocorreu um erro durante a execução do script!
echo  Verifique os logs acima e tente novamente.
echo =====================================================
pause

:fim
