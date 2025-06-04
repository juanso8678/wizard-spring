// ============================================================================
// ESTADO GLOBAL - WIZARD Admin Panel (Zustand)
// ============================================================================

import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import { storage, WIZARD_CONSTANTS } from '@/utils';
import type { User, Organization } from '@/types';

// ============================================================================
// TIPOS DEL ESTADO
// ============================================================================

interface AuthState {
  // Estado de autenticación
  user: User | null;
  isAuthenticated: boolean;
  token: string | null;
  organizationId: string | null;
  
  // Acciones
  setUser: (user: User | null) => void;
  setToken: (token: string | null) => void;
  setOrganizationId: (orgId: string | null) => void;
  login: (user: User, token: string, orgId?: string) => void;
  logout: () => void;
  updateUser: (userData: Partial<User>) => void;
}

interface UIState {
  // Estado de la interfaz
  sidebarCollapsed: boolean;
  theme: 'light' | 'dark';
  loading: boolean;
  notifications: Notification[];
  
  // Acciones
  toggleSidebar: () => void;
  setSidebarCollapsed: (collapsed: boolean) => void;
  setTheme: (theme: 'light' | 'dark') => void;
  setLoading: (loading: boolean) => void;
  addNotification: (notification: Notification) => void;
  removeNotification: (id: string) => void;
  clearNotifications: () => void;
}

interface DataState {
  // Cache de datos
  organizations: Organization[];
  selectedOrganization: Organization | null;
  
  // Acciones
  setOrganizations: (organizations: Organization[]) => void;
  setSelectedOrganization: (organization: Organization | null) => void;
  updateOrganization: (id: string, data: Partial<Organization>) => void;
  addOrganization: (organization: Organization) => void;
  removeOrganization: (id: string) => void;
}

interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message?: string;
  timestamp: number;
  read: boolean;
}

// ============================================================================
// STORE DE AUTENTICACIÓN
// ============================================================================

export const useAuthStore = create<AuthState>()(
  devtools(
    persist(
      (set, get) => ({
        // Estado inicial
        user: null,
        isAuthenticated: false,
        token: null,
        organizationId: null,

        // Acciones
        setUser: (user) => set({ user }),
        
        setToken: (token) => set({ token }),
        
        setOrganizationId: (organizationId) => set({ organizationId }),

        login: (user, token, orgId) => {
          set({
            user,
            token,
            organizationId: orgId || null,
            isAuthenticated: true
          });
          
          // Guardar en localStorage
          storage.set(WIZARD_CONSTANTS.STORAGE_KEYS.TOKEN, token);
          storage.setObject(WIZARD_CONSTANTS.STORAGE_KEYS.USER, user);
          if (orgId) {
            storage.set(WIZARD_CONSTANTS.STORAGE_KEYS.ORG_ID, orgId);
          }
        },

        logout: () => {
          set({
            user: null,
            token: null,
            organizationId: null,
            isAuthenticated: false
          });
          
          // Limpiar localStorage
          storage.remove(WIZARD_CONSTANTS.STORAGE_KEYS.TOKEN);
          storage.remove(WIZARD_CONSTANTS.STORAGE_KEYS.USER);
          storage.remove(WIZARD_CONSTANTS.STORAGE_KEYS.ORG_ID);
        },

        updateUser: (userData) => {
          const currentUser = get().user;
          if (currentUser) {
            const updatedUser = { ...currentUser, ...userData };
            set({ user: updatedUser });
            storage.setObject(WIZARD_CONSTANTS.STORAGE_KEYS.USER, updatedUser);
          }
        }
      }),
      {
        name: 'wizard-auth-storage',
        partialize: (state) => ({
          user: state.user,
          token: state.token,
          organizationId: state.organizationId,
          isAuthenticated: state.isAuthenticated
        })
      }
    ),
    { name: 'AuthStore' }
  )
);

// ============================================================================
// STORE DE UI
// ============================================================================

export const useUIStore = create<UIState>()(
  devtools(
    persist(
      (set, get) => ({
        // Estado inicial
        sidebarCollapsed: false,
        theme: 'light',
        loading: false,
        notifications: [],

        // Acciones
        toggleSidebar: () => {
          const collapsed = !get().sidebarCollapsed;
          set({ sidebarCollapsed: collapsed });
          storage.set(WIZARD_CONSTANTS.STORAGE_KEYS.SIDEBAR_COLLAPSED, collapsed.toString());
        },

        setSidebarCollapsed: (collapsed) => {
          set({ sidebarCollapsed: collapsed });
          storage.set(WIZARD_CONSTANTS.STORAGE_KEYS.SIDEBAR_COLLAPSED, collapsed.toString());
        },

        setTheme: (theme) => {
          set({ theme });
          storage.set(WIZARD_CONSTANTS.STORAGE_KEYS.THEME, theme);
        },

        setLoading: (loading) => set({ loading }),

        addNotification: (notification) => {
          const notifications = get().notifications;
          set({
            notifications: [
              {
                ...notification,
                id: notification.id || Date.now().toString(),
                timestamp: notification.timestamp || Date.now(),
                read: false
              },
              ...notifications
            ]
          });
        },

        removeNotification: (id) => {
          const notifications = get().notifications;
          set({
            notifications: notifications.filter(n => n.id !== id)
          });
        },

        clearNotifications: () => set({ notifications: [] })
      }),
      {
        name: 'wizard-ui-storage',
        partialize: (state) => ({
          sidebarCollapsed: state.sidebarCollapsed,
          theme: state.theme
        })
      }
    ),
    { name: 'UIStore' }
  )
);

// ============================================================================
// STORE DE DATOS
// ============================================================================

export const useDataStore = create<DataState>()(
  devtools(
    (set, get) => ({
      // Estado inicial
      organizations: [],
      selectedOrganization: null,

      // Acciones
      setOrganizations: (organizations) => set({ organizations }),

      setSelectedOrganization: (organization) => set({ selectedOrganization: organization }),

      updateOrganization: (id, data) => {
        const organizations = get().organizations;
        const updated = organizations.map(org =>
          org.id === id ? { ...org, ...data } : org
        );
        set({ organizations: updated });
        
        // Actualizar también si es la organización seleccionada
        const selected = get().selectedOrganization;
        if (selected && selected.id === id) {
          set({ selectedOrganization: { ...selected, ...data } });
        }
      },

      addOrganization: (organization) => {
        const organizations = get().organizations;
        set({ organizations: [...organizations, organization] });
      },

      removeOrganization: (id) => {
        const organizations = get().organizations;
        set({ organizations: organizations.filter(org => org.id !== id) });
        
        // Limpiar selección si era la organización seleccionada
        const selected = get().selectedOrganization;
        if (selected && selected.id === id) {
          set({ selectedOrganization: null });
        }
      }
    }),
    { name: 'DataStore' }
  )
);

// ============================================================================
// HOOKS PERSONALIZADOS PARA LOS STORES
// ============================================================================

// Hook para autenticación
export const useAuth = () => {
  const {
    user,
    isAuthenticated,
    token,
    organizationId,
    login,
    logout,
    updateUser,
    setOrganizationId
  } = useAuthStore();

  return {
    user,
    isAuthenticated,
    token,
    organizationId,
    login,
    logout,
    updateUser,
    setOrganizationId,
    
    // Helpers adicionales
    isAdmin: user?.role === WIZARD_CONSTANTS.SYSTEM_ROLES.SUPER_ADMIN || 
             user?.role === WIZARD_CONSTANTS.SYSTEM_ROLES.ADMIN_ORGANIZACION,
    isSuperAdmin: user?.role === WIZARD_CONSTANTS.SYSTEM_ROLES.SUPER_ADMIN,
    hasOrganization: !!organizationId
  };
};

// Hook para UI
export const useUI = () => {
  const {
    sidebarCollapsed,
    theme,
    loading,
    notifications,
    toggleSidebar,
    setSidebarCollapsed,
    setTheme,
    setLoading,
    addNotification,
    removeNotification,
    clearNotifications
  } = useUIStore();

  return {
    sidebarCollapsed,
    theme,
    loading,
    notifications,
    toggleSidebar,
    setSidebarCollapsed,
    setTheme,
    setLoading,
    addNotification,
    removeNotification,
    clearNotifications,
    
    // Helpers adicionales
    unreadNotifications: notifications.filter(n => !n.read).length,
    latestNotifications: notifications.slice(0, 5)
  };
};

// Hook para datos
export const useData = () => {
  const {
    organizations,
    selectedOrganization,
    setOrganizations,
    setSelectedOrganization,
    updateOrganization,
    addOrganization,
    removeOrganization
  } = useDataStore();

  return {
    organizations,
    selectedOrganization,
    setOrganizations,
    setSelectedOrganization,
    updateOrganization,
    addOrganization,
    removeOrganization,
    
    // Helpers adicionales
    getOrganizationById: (id: string) => organizations.find(org => org.id === id),
    organizationCount: organizations.length
  };
};

// ============================================================================
// ACCIONES GLOBALES
// ============================================================================

// Función para inicializar la aplicación
export const initializeApp = async () => {
  // Cargar datos del localStorage
  const savedTheme = storage.get(WIZARD_CONSTANTS.STORAGE_KEYS.THEME) as 'light' | 'dark';
  const savedCollapsed = storage.get(WIZARD_CONSTANTS.STORAGE_KEYS.SIDEBAR_COLLAPSED) === 'true';
  
  if (savedTheme) {
    useUIStore.getState().setTheme(savedTheme);
  }
  
  useUIStore.getState().setSidebarCollapsed(savedCollapsed);
  
  // Verificar autenticación
  const token = storage.get(WIZARD_CONSTANTS.STORAGE_KEYS.TOKEN);
  const user = storage.getObject<User>(WIZARD_CONSTANTS.STORAGE_KEYS.USER);
  const orgId = storage.get(WIZARD_CONSTANTS.STORAGE_KEYS.ORG_ID);
  
  if (token && user) {
    useAuthStore.getState().login(user, token, orgId || undefined);
  }
};

// Función para limpiar todos los datos
export const clearAllData = () => {
  useAuthStore.getState().logout();
  useUIStore.getState().clearNotifications();
  useDataStore.getState().setOrganizations([]);
  useDataStore.getState().setSelectedOrganization(null);
};

// ============================================================================
// SELECTORES AVANZADOS
// ============================================================================

// Selector para obtener permisos del usuario actual
export const useUserPermissions = () => {
  const user = useAuthStore(state => state.user);
  
  // Por ahora retornamos permisos básicos basados en el rol
  // TODO: Implementar la lógica real de permisos desde la API
  const permissions = React.useMemo(() => {
    if (!user) return [];
    
    switch (user.role) {
      case WIZARD_CONSTANTS.SYSTEM_ROLES.SUPER_ADMIN:
        return ['SUPER_ADMIN_ACCESS'];
      case WIZARD_CONSTANTS.SYSTEM_ROLES.ADMIN_ORGANIZACION:
        return ['ADMIN_ACCESS', 'USER_VIEW', 'USER_CREATE', 'USER_EDIT'];
      default:
        return ['USER_VIEW'];
    }
  }, [user]);
  
  return {
    permissions,
    hasPermission: (permission: string) => permissions.includes(permission) || permissions.includes('SUPER_ADMIN_ACCESS'),
    hasAnyPermission: (requiredPermissions: string[]) => 
      requiredPermissions.some(p => permissions.includes(p)) || permissions.includes('SUPER_ADMIN_ACCESS'),
    hasAllPermissions: (requiredPermissions: string[]) =>
      requiredPermissions.every(p => permissions.includes(p)) || permissions.includes('SUPER_ADMIN_ACCESS')
  };
};

// Selector para el estado de carga global
export const useGlobalLoading = () => {
  const loading = useUIStore(state => state.loading);
  return loading;
};

// ============================================================================
// EXPORTAR STORES Y UTILIDADES
// ============================================================================

export default {
  useAuthStore,
  useUIStore,
  useDataStore,
  useAuth,
  useUI,
  useData,
  useUserPermissions,
  useGlobalLoading,
  initializeApp,
  clearAllData
};