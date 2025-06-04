// ============================================================================
// APP PRINCIPAL - WIZARD Admin Panel
// ============================================================================

import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider, theme, App as AntdApp } from 'antd';
import esES from 'antd/locale/es_ES';
import dayjs from 'dayjs';
import 'dayjs/locale/es';

// Configuración de dayjs
dayjs.locale('es');

// Importar componentes
import MainLayout from '@/components/layout/MainLayout';
import LoginPage from '@/pages/auth/LoginPage';
import DashboardPage from '@/pages/dashboard/DashboardPage';

// Páginas placeholder (las crearemos después)
import LoadingPage from '@/components/common/LoadingPage';
import NotFoundPage from '@/components/common/NotFoundPage';

// Store y hooks
import { useAuth, useUI, initializeApp } from '@/store';

// ============================================================================
// COMPONENTE DE RUTA PROTEGIDA
// ============================================================================

interface ProtectedRouteProps {
  children: React.ReactNode;
  requireAuth?: boolean;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requireAuth = true 
}) => {
  const { isAuthenticated } = useAuth();

  if (requireAuth && !isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!requireAuth && isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};

// ============================================================================
// PÁGINAS PLACEHOLDER SIMPLES
// ============================================================================

const PlaceholderPage: React.FC<{ title: string; description?: string }> = ({ 
  title, 
  description 
}) => (
  <div style={{ 
    padding: '50px', 
    textAlign: 'center',
    background: '#f9f9f9',
    borderRadius: '8px',
    border: '2px dashed #d9d9d9'
  }}>
    <h2>{title}</h2>
    {description && <p style={{ color: '#666' }}>{description}</p>}
    <p style={{ fontSize: '14px', color: '#999', marginTop: '20px' }}>
      Esta página será implementada en las siguientes fases del desarrollo.
    </p>
  </div>
);

// ============================================================================
// CONFIGURACIÓN DE RUTAS
// ============================================================================

const AppRoutes: React.FC = () => {
  return (
    <Routes>
      {/* Ruta de login */}
      <Route 
        path="/login" 
        element={
          <ProtectedRoute requireAuth={false}>
            <LoginPage />
          </ProtectedRoute>
        } 
      />

      {/* Rutas protegidas con layout */}
      <Route 
        path="/" 
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        {/* Dashboard */}
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />

        {/* Gestión de Usuarios */}
        <Route 
          path="users" 
          element={
            <PlaceholderPage 
              title="Gestión de Usuarios" 
              description="Lista y administración de usuarios del sistema"
            />
          } 
        />
        <Route 
          path="roles" 
          element={
            <PlaceholderPage 
              title="Roles y Permisos" 
              description="Configuración de roles y asignación de permisos"
            />
          } 
        />
        <Route 
          path="user-assignments" 
          element={
            <PlaceholderPage 
              title="Asignaciones de Usuario" 
              description="Asignación de roles y sedes a usuarios"
            />
          } 
        />

        {/* Organizaciones */}
        <Route 
          path="organizations" 
          element={
            <PlaceholderPage 
              title="Gestión de Organizaciones" 
              description="Administración de organizaciones y sus configuraciones"
            />
          } 
        />
        <Route 
          path="sedes" 
          element={
            <PlaceholderPage 
              title="Gestión de Sedes" 
              description="Administración de sedes vinculadas a organizaciones"
            />
          } 
        />

        {/* Sistema PACS */}
        <Route path="pacs">
          <Route 
            path="nodes" 
            element={
              <PlaceholderPage 
                title="Nodos DICOM" 
                description="Gestión de AE Titles y conectividad DICOM"
              />
            } 
          />
          <Route 
            path="studies" 
            element={
              <PlaceholderPage 
                title="Estudios DICOM" 
                description="Búsqueda y gestión de estudios médicos"
              />
            } 
          />
          <Route 
            path="viewer" 
            element={
              <PlaceholderPage 
                title="Visor Web DICOM" 
                description="Visualización de imágenes médicas en el navegador"
              />
            } 
          />
        </Route>

        {/* Administración del Sistema */}
        <Route path="admin">
          <Route 
            path="pacs-engine" 
            element={
              <PlaceholderPage 
                title="PACS Engine" 
                description="Monitoreo y configuración del motor PACS"
              />
            } 
          />
          <Route 
            path="config" 
            element={
              <PlaceholderPage 
                title="Configuración del Sistema" 
                description="Parámetros generales y configuración avanzada"
              />
            } 
          />
          <Route 
            path="logs" 
            element={
              <PlaceholderPage 
                title="Logs del Sistema" 
                description="Registro de eventos y errores del sistema"
              />
            } 
          />
        </Route>

        {/* Reportes */}
        <Route path="reports">
          <Route 
            path="studies" 
            element={
              <PlaceholderPage 
                title="Estadísticas de Estudios" 
                description="Reportes y métricas sobre estudios DICOM"
              />
            } 
          />
          <Route 
            path="usage" 
            element={
              <PlaceholderPage 
                title="Uso del Sistema" 
                description="Análisis de uso y rendimiento del sistema"
              />
            } 
          />
        </Route>

        {/* Perfil y configuración personal */}
        <Route 
          path="profile" 
          element={
            <PlaceholderPage 
              title="Mi Perfil" 
              description="Configuración de la cuenta personal"
            />
          } 
        />
        <Route 
          path="settings" 
          element={
            <PlaceholderPage 
              title="Configuración Personal" 
              description="Preferencias y configuración de la interfaz"
            />
          } 
        />
      </Route>

      {/* Página 404 */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
};

// ============================================================================
// COMPONENTE PRINCIPAL DE LA APLICACIÓN
// ============================================================================

const App: React.FC = () => {
  const { theme: appTheme } = useUI();
  const { isAuthenticated } = useAuth();

  // Inicializar la aplicación
  useEffect(() => {
    initializeApp();
  }, []);

  // Configuración del tema de Ant Design
  const themeConfig = {
    algorithm: appTheme === 'dark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
    token: {
      colorPrimary: '#1890ff',
      colorSuccess: '#52c41a',
      colorWarning: '#faad14',
      colorError: '#ff4d4f',
      colorInfo: '#1890ff',
      borderRadius: 6,
      wireframe: false,
    },
    components: {
      Layout: {
        headerBg: appTheme === 'dark' ? '#001529' : '#ffffff',
        siderBg: appTheme === 'dark' ? '#001529' : '#ffffff',
        bodyBg: appTheme === 'dark' ? '#141414' : '#f0f2f5',
      },
      Menu: {
        itemBg: 'transparent',
        itemSelectedBg: appTheme === 'dark' ? '#1890ff' : '#e6f7ff',
        itemActiveBg: appTheme === 'dark' ? '#1890ff' : '#e6f7ff',
      },
      Card: {
        headerBg: 'transparent',
      },
    },
  };

  return (
    <ConfigProvider 
      locale={esES}
      theme={themeConfig}
    >
      <AntdApp style={{ minHeight: '100vh' }}>
        <Router>
          <div className="wizard-app">
            <AppRoutes />
          </div>
        </Router>
      </AntdApp>
    </ConfigProvider>
  );
};

export default App;