@echo off
cd /d C:\raj\Desenvs\appDiretor
echo === Gerando recursos para Android ===
npx cordova-res android --skip-config --copy
echo === Concluído! ===
pause
