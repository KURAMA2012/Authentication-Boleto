import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.authentication.boleto',
  appName: 'Authentication Boleto',
  webDir: 'www',
  bundledWebRuntime: false,
  plugins: {
    SplashScreen: {
      launchShowDuration: 3000,
      showSpinner: true,
      splashFullScreen: true,
      splashImmersive: true
    },
    Keyboard: {
      resize: 'ionic'
    }
  },
  server: {
    allowNavigation: [
      'fonts.googleapis.com',
      'fonts.gstatic.com'
    ],
    androidScheme: 'http'
  }
};

export default config;
