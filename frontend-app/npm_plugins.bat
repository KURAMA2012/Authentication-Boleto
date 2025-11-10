@echo off
echo =========================================
echo Limpando node_modules e package-lock.json...
echo =========================================
rmdir /s /q node_modules
del /f /q package-lock.json

echo =========================================
echo Instalando dependencias do projeto...
echo =========================================
npm install --legacy-peer-deps

echo =========================================
echo Instalando plugins do Capacitor...
echo =========================================
npm install @capacitor/core @capacitor/cli --save --legacy-peer-deps
npm install @capacitor/android --save --legacy-peer-deps

REM Plugins nativos Capacitor
npm install @capacitor/camera --save --legacy-peer-deps
npm install @capacitor/clipboard --save --legacy-peer-deps
npm install @capacitor/filesystem --save --legacy-peer-deps
npm install @capacitor/browser --save --legacy-peer-deps
npm install @capacitor/splash-screen --save --legacy-peer-deps
npm install @capacitor/status-bar --save --legacy-peer-deps
npm install @capacitor/device --save --legacy-peer-deps

REM Plugins da comunidade
npm install @capacitor-community/barcode-scanner --save --legacy-peer-deps
npm install @capacitor-community/http --save --legacy-peer-deps
npm install @capacitor-community/fingerprint-aio --save --legacy-peer-deps
npm install @capacitor-community/document-viewer --save --legacy-peer-deps

echo =========================================
echo Instalando cordova-res para gerar ícones/splash...
echo =========================================
npm install -g cordova-res --legacy-peer-deps


echo =========================================
echo Instalação concluída! Agora rode 'ionic serve' ou 'npx cap open android'.
echo =========================================
pause
