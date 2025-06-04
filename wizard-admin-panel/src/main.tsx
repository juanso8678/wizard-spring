// ============================================================================
// MAIN.TSX - Punto de entrada de la aplicación WIZARD
// ============================================================================

import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// Estilos globales
import './assets/styles/global.css';

// Configuración de desarrollo
if (process.env.NODE_ENV === 'development') {
  console.log('🚀 WIZARD Admin Panel starting in development mode');
  console.log('📊 Dashboard URL: http://localhost:3000/dashboard');
  console.log('🔐 Login URL: http://localhost:3000/login');
  console.log('🏥 PACS Backend: http://localhost:8080/api');
}

// Crear root y renderizar aplicación
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);