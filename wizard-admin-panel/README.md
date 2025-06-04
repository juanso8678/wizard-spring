# 🏥 WIZARD PACS System - Panel de Administración

Panel de administración moderno y completo para el sistema **WIZARD PACS**, desarrollado con React + TypeScript + Ant Design.

## 📋 Características Principales

### 🔐 **Autenticación y Seguridad**
- Login seguro con JWT
- Sistema de roles y permisos granular
- Multi-tenancy por organizaciones
- Interceptores automáticos de API

### 👥 **Gestión de Usuarios**
- CRUD completo de usuarios
- Asignación de roles y permisos
- Gestión de organizaciones y sedes
- Control de acceso por tenant

### 🏥 **Sistema PACS Completo**
- **Nodos DICOM**: Gestión de AE Titles y conectividad
- **Estudios**: Búsqueda avanzada y gestión de estudios DICOM
- **Visor Web**: Visualización segura de imágenes médicas
- **PACS Engine**: Monitoreo del motor DCM4CHEE integrado

### 📊 **Dashboard y Reportes**
- Dashboard ejecutivo con métricas clave
- Estadísticas por modalidad
- Estado en tiempo real de nodos DICOM
- Actividad reciente del sistema

### 🎨 **Interfaz Moderna**
- Diseño responsivo con Ant Design
- Tema claro/oscuro
- Navegación intuitiva
- Componentes reutilizables

## 🚀 Instalación Rápida

### **Prerrequisitos**
- Node.js 18+ 
- npm o yarn
- Backend WIZARD PACS corriendo en `http://localhost:8080`

### **1. Crear el proyecto**
```bash
# Crear proyecto con Vite
npm create vite@latest wizard-admin-panel -- --template react-ts
cd wizard-admin-panel
```

### **2. Instalar dependencias**
```bash
# Dependencias principales
npm install antd @ant-design/icons react-router-dom axios dayjs ahooks zustand

# Dependencias de desarrollo
npm install --save-dev @types/node
```

### **3. Configurar estructura de carpetas**
```bash
# Crear toda la estructura
mkdir -p src/{components/{layout,forms,tables,common},pages/{auth,dashboard,users,organizations,pacs/{nodes,studies},admin},services,hooks,utils,types,assets/{images,styles},store}

# Crear archivos base
touch src/types/index.ts
touch src/utils/index.ts
touch src/services/api.ts
```

### **4. Copiar archivos del proyecto**
Copia todos los archivos proporcionados en esta documentación a sus respectivas ubicaciones:

- `src/types/index.ts` - Tipos TypeScript
- `src/services/api.ts` - Servicio API centralizado
- `src/utils/index.ts` - Utilidades del proyecto
- `src/hooks/index.ts` - Custom hooks
- `src/store/index.ts` - Estado global (Zustand)
- `src/components/layout/MainLayout.tsx` - Layout principal
- `src/pages/auth/LoginPage.tsx` - Página de login
- `src/pages/dashboard/DashboardPage.tsx` - Dashboard
- `src/components/common/LoadingPage.tsx` - Página de carga
- `src/components/common/NotFoundPage.tsx` - Página 404
- `src/App.tsx` - Configuración de routing
- `src/main.tsx` - Punto de entrada
- `src/assets/styles/global.css` - Estilos globales
- `vite.config.ts` - Configuración Vite
- `tsconfig.json` - Configuración TypeScript
- `index.html` - HTML principal
- `.eslintrc.cjs` - Configuración ESLint

### **5. Ejecutar el proyecto**
```bash
# Desarrollo
npm run dev

# Compilar para producción
npm run build

# Preview de producción
npm run preview
```

## 🌐 URLs del Sistema

- **Frontend**: http://localhost:3000
- **Login**: http://localhost:3000/login
- **Dashboard**: http://localhost:3000/dashboard
- **Backend API**: http://localhost:8080/api

## 🔑 Credenciales de Prueba

Para el entorno de desarrollo:
- **Usuario**: `admin@wizard.es`
- **Contraseña**: `admin123`

## 📁 Estructura del Proyecto

```
wizard-admin-panel/
├── 📁 public/
├── 📁 src/
│   ├── 📁 assets/
│   │   ├── 📁 images/
│   │   └── 📁 styles/
│   │       └── global.css
│   ├── 📁 components/
│   │   ├── 📁 common/
│   │   │   ├── LoadingPage.tsx
│   │   │   └── NotFoundPage.tsx
│   │   ├── 📁 forms/           # Formularios especializados
│   │   ├── 📁 layout/
│   │   │   └── MainLayout.tsx
│   │   └── 📁 tables/          # Tablas con funcionalidades
│   ├── 📁 hooks/
│   │   └── index.ts            # Custom hooks
│   ├── 📁 pages/
│   │   ├── 📁 admin/           # Administración del sistema
│   │   ├── 📁 auth/
│   │   │   └── LoginPage.tsx
│   │   ├── 📁 dashboard/
│   │   │   └── DashboardPage.tsx
│   │   ├── 📁 organizations/   # Gestión de organizaciones
│   │   ├── 📁 pacs/           # Sistema PACS
│   │   │   ├── 📁 nodes/      # Nodos DICOM
│   │   │   └── 📁 studies/    # Estudios DICOM
│   │   └── 📁 users/          # Gestión de usuarios
│   ├── 📁 services/
│   │   └── api.ts             # Servicio API centralizado
│   ├── 📁 store/
│   │   └── index.ts           # Estado global (Zustand)
│   ├── 📁 types/
│   │   └── index.ts           # Tipos TypeScript
│   ├── 📁 utils/
│   │   └── index.ts           # Utilidades y helpers
│   ├── App.tsx                # Configuración de routing
│   └── main.tsx               # Punto de entrada
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 🛠️ Tecnologías Utilizadas

### **Frontend Core**
- **React 18** - Biblioteca UI
- **TypeScript** - Tipado estático
- **Vite** - Build tool moderno
- **React Router Dom** - Routing

### **UI & Styling**
- **Ant Design 5** - Biblioteca de componentes
- **CSS Variables** - Sistema de diseño
- **Responsive Design** - Adaptable a móviles

### **Estado y Datos**
- **Zustand** - Gestión de estado global
- **Axios** - Cliente HTTP
- **ahooks** - React hooks utilitarios

### **Desarrollo**
- **ESLint** - Linting
- **TypeScript** - Tipado
- **Hot Reload** - Desarrollo rápido

## 🔌 Integración con Backend

El frontend se comunica con el backend WIZARD PACS a través de:

### **Endpoints Principales**
- `POST /api/auth/login` - Autenticación
- `GET /api/users` - Gestión de usuarios
- `GET /api/organizations` - Organizaciones
- `GET /api/pacs/dicom-nodes` - Nodos DICOM
- `GET /api/pacs/studies` - Estudios DICOM
- `GET /api/pacs/engine/status` - Estado PACS Engine

### **Características de Integración**
- **Headers automáticos**: Authorization, X-Organization-Id
- **Interceptores**: Manejo de errores, refresh de tokens
- **Multi-tenancy**: Filtrado automático por organización
- **Reintentos**: Configuración de timeout y reintentos

## 🎯 Funcionalidades Implementadas

### ✅ **Listas para Usar**
- [x] Sistema de autenticación completo
- [x] Dashboard con métricas reales
- [x] Layout responsivo y navegación
- [x] Gestión de estado global
- [x] Servicios API configurados
- [x] Manejo de errores y loading
- [x] Tipos TypeScript completos
- [x] Utilidades y helpers

### 🚧 **Próximas Fases** (Placeholders Creados)
- [ ] **Gestión de Usuarios** - CRUD completo de usuarios
- [ ] **Roles y Permisos** - Asignación granular
- [ ] **Organizaciones & Sedes** - Administración multi-tenant
- [ ] **Nodos DICOM** - Gestión de AE Titles
- [ ] **Estudios DICOM** - Búsqueda y gestión avanzada
- [ ] **Visor Web** - Visualización de imágenes
- [ ] **PACS Engine** - Monitoreo y configuración
- [ ] **Reportes** - Estadísticas y análisis

## 🔧 Configuración Avanzada

### **Variables de Entorno**
```bash
# .env.local
VITE_API_BASE_URL=http://localhost:8080/api
VITE_PACS_ENGINE_URL=http://wizard-pacs-engine:8080
VITE_APP_VERSION=1.0.0
```

### **Proxy de Desarrollo**
El `vite.config.ts` incluye configuración de proxy para desarrollo:
```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### **Build para Producción**
```bash
# Compilar
npm run build

# El build genera la carpeta 'dist' lista para servir
# Servir con cualquier servidor web estático
```

## 🐛 Solución de Problemas

### **Error de conexión al backend**
```bash
# Verificar que el backend esté corriendo
curl http://localhost:8080/api/ping

# Verificar proxy en vite.config.ts
```

### **Errores de TypeScript**
```bash
# Limpiar cache y reinstalar
rm -rf node_modules package-lock.json
npm install
```

### **Problemas de CORS**
```bash
# Verificar configuración CORS en el backend Spring Boot
# O usar el proxy configurado en Vite
```

## 📚 Próximos Pasos

### **Fase 1: Gestión de Usuarios** (Siguiente)
1. Crear `UserListPage.tsx` con tabla de usuarios
2. Implementar `UserFormModal.tsx` para CRUD
3. Agregar `RoleAssignmentModal.tsx`
4. Conectar con APIs existentes

### **Fase 2: Sistema PACS**
1. Página de nodos DICOM con tests de conectividad
2. Búsqueda avanzada de estudios
3. Integración con visor web
4. Monitoreo del PACS Engine

### **Fase 3: Funcionalidades Avanzadas**
1. Sistema de notificaciones en tiempo real
2. Reportes y dashboards personalizables
3. Configuración de tema y preferencias
4. PWA y funcionalidades offline

## 🤝 Contribuir

Este proyecto sigue las mejores prácticas de desarrollo:
- **Código limpio** y bien documentado
- **Componentes reutilizables**
- **Tipado fuerte** con TypeScript
- **Estilo consistente** con ESLint
- **Arquitectura escalable**

## 📄 Licencia

© 2024 Wizard Healthcare Solutions. Todos los derechos reservados.

---

## 🚀 ¡Proyecto Listo!

El **WIZARD Admin Panel** está configurado y listo para usar. El dashboard funcional te mostrará datos reales del backend, y tienes toda la estructura preparada para implementar las funcionalidades restantes.

**¡Ejecuta `npm run dev` y comienza a trabajar!** 🎉