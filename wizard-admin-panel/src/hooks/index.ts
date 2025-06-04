// ============================================================================
// CUSTOM HOOKS - WIZARD Admin Panel
// ============================================================================

import { useState, useEffect, useCallback, useMemo } from 'react';
import { useRequest } from 'ahooks';
import { message } from 'antd';
import { apiService } from '@/services/api';
import { storage, WIZARD_CONSTANTS, notify } from '@/utils';
import type {
  User,
  Organization,
  Role,
  Permission,
  Module,
  Sede,
  DicomNode,
  Study,
  StudySearchCriteria,
  UserRole,
  UserSede
} from '@/types';

// ============================================================================
// HOOK DE AUTENTICACIÓN
// ============================================================================

export const useAuth = () => {
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verificar si hay token guardado al cargar
    const token = storage.get(WIZARD_CONSTANTS.STORAGE_KEYS.TOKEN);
    const savedUser = storage.getObject<User>(WIZARD_CONSTANTS.STORAGE_KEYS.USER);
    
    if (token && savedUser) {
      setUser(savedUser);
      setIsAuthenticated(true);
    }
    
    setLoading(false);
  }, []);

  const login = useCallback(async (usernameOrEmail: string, password: string) => {
    try {
      setLoading(true);
      const response = await apiService.login({ usernameOrEmail, password });
      
      // Aquí podrías hacer una llamada adicional para obtener info del usuario
      // Por ahora usamos datos básicos del token
      const userData: User = {
        id: 'temp-id', // Se actualizará con datos reales
        email: usernameOrEmail,
        username: usernameOrEmail,
        name: 'Usuario',
        role: 'USER',
        activo: true
      };
      
      setUser(userData);
      setIsAuthenticated(true);
      storage.setObject(WIZARD_CONSTANTS.STORAGE_KEYS.USER, userData);
      
      return response;
    } catch (error) {
      setIsAuthenticated(false);
      setUser(null);
      throw error;
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    setIsAuthenticated(false);
    apiService.logout();
  }, []);

  const updateUser = useCallback((userData: Partial<User>) => {
    if (user) {
      const updatedUser = { ...user, ...userData };
      setUser(updatedUser);
      storage.setObject(WIZARD_CONSTANTS.STORAGE_KEYS.USER, updatedUser);
    }
  }, [user]);

  return {
    user,
    isAuthenticated,
    loading,
    login,
    logout,
    updateUser
  };
};

// ============================================================================
// HOOK PARA ORGANIZACIONES
// ============================================================================

export const useOrganizations = () => {
  const {
    data: organizations,
    loading,
    error,
    refresh,
    run: loadOrganizations
  } = useRequest(
    () => apiService.getOrganizations(),
    {
      manual: false,
      onError: (error) => {
        notify.error(`Error cargando organizaciones: ${error.message}`);
      }
    }
  );

  const createOrganization = useCallback(async (orgData: any) => {
    try {
      const newOrg = await apiService.createOrganization(orgData);
      notify.success('Organización creada exitosamente');
      refresh();
      return newOrg;
    } catch (error: any) {
      notify.error(`Error creando organización: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const updateOrganization = useCallback(async (id: string, orgData: any) => {
    try {
      const updatedOrg = await apiService.updateOrganization(id, orgData);
      notify.success('Organización actualizada exitosamente');
      refresh();
      return updatedOrg;
    } catch (error: any) {
      notify.error(`Error actualizando organización: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const deleteOrganization = useCallback(async (id: string) => {
    try {
      await apiService.deleteOrganization(id);
      notify.success('Organización eliminada exitosamente');
      refresh();
    } catch (error: any) {
      notify.error(`Error eliminando organización: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  return {
    organizations: organizations || [],
    loading,
    error,
    refresh,
    createOrganization,
    updateOrganization,
    deleteOrganization
  };
};

// ============================================================================
// HOOK PARA USUARIOS
// ============================================================================

export const useUsers = () => {
  const {
    data: users,
    loading,
    error,
    refresh,
    run: loadUsers
  } = useRequest(
    () => apiService.getUsers(),
    {
      manual: false,
      onError: (error) => {
        notify.error(`Error cargando usuarios: ${error.message}`);
      }
    }
  );

  const createUser = useCallback(async (userData: Partial<User>) => {
    try {
      const newUser = await apiService.createUser(userData);
      notify.success('Usuario creado exitosamente');
      refresh();
      return newUser;
    } catch (error: any) {
      notify.error(`Error creando usuario: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const deleteUser = useCallback(async (id: string) => {
    try {
      await apiService.deleteUser(id);
      notify.success('Usuario eliminado exitosamente');
      refresh();
    } catch (error: any) {
      notify.error(`Error eliminando usuario: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  return {
    users: users || [],
    loading,
    error,
    refresh,
    createUser,
    deleteUser
  };
};

// ============================================================================
// HOOK PARA ROLES Y PERMISOS
// ============================================================================

export const useRoles = () => {
  const {
    data: roles,
    loading: rolesLoading,
    error: rolesError,
    refresh: refreshRoles
  } = useRequest(() => apiService.getRoles(), { manual: false });

  const {
    data: permissions,
    loading: permissionsLoading,
    error: permissionsError
  } = useRequest(() => apiService.getPermissions(), { manual: false });

  const {
    data: modules,
    loading: modulesLoading,
    error: modulesError
  } = useRequest(() => apiService.getModules(), { manual: false });

  const createRole = useCallback(async (roleData: any) => {
    try {
      const newRole = await apiService.createRole(roleData);
      notify.success('Rol creado exitosamente');
      refreshRoles();
      return newRole;
    } catch (error: any) {
      notify.error(`Error creando rol: ${error.message}`);
      throw error;
    }
  }, [refreshRoles]);

  const updateRole = useCallback(async (id: string, roleData: any) => {
    try {
      const updatedRole = await apiService.updateRole(id, roleData);
      notify.success('Rol actualizado exitosamente');
      refreshRoles();
      return updatedRole;
    } catch (error: any) {
      notify.error(`Error actualizando rol: ${error.message}`);
      throw error;
    }
  }, [refreshRoles]);

  const deleteRole = useCallback(async (id: string) => {
    try {
      await apiService.deleteRole(id);
      notify.success('Rol eliminado exitosamente');
      refreshRoles();
    } catch (error: any) {
      notify.error(`Error eliminando rol: ${error.message}`);
      throw error;
    }
  }, [refreshRoles]);

  const assignPermissions = useCallback(async (roleId: string, permissionIds: string[]) => {
    try {
      const updatedRole = await apiService.assignPermissionsToRole(roleId, permissionIds);
      notify.success('Permisos asignados exitosamente');
      refreshRoles();
      return updatedRole;
    } catch (error: any) {
      notify.error(`Error asignando permisos: ${error.message}`);
      throw error;
    }
  }, [refreshRoles]);

  return {
    roles: roles || [],
    permissions: permissions || [],
    modules: modules || [],
    loading: rolesLoading || permissionsLoading || modulesLoading,
    error: rolesError || permissionsError || modulesError,
    refreshRoles,
    createRole,
    updateRole,
    deleteRole,
    assignPermissions
  };
};

// ============================================================================
// HOOK PARA SEDES
// ============================================================================

export const useSedes = () => {
  const {
    data: sedes,
    loading,
    error,
    refresh,
    run: loadSedes
  } = useRequest(
    () => apiService.getSedes(),
    {
      manual: false,
      onError: (error) => {
        notify.error(`Error cargando sedes: ${error.message}`);
      }
    }
  );

  const createSede = useCallback(async (sedeData: any) => {
    try {
      const newSede = await apiService.createSede(sedeData);
      notify.success('Sede creada exitosamente');
      refresh();
      return newSede;
    } catch (error: any) {
      notify.error(`Error creando sede: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const updateSede = useCallback(async (id: string, sedeData: any) => {
    try {
      const updatedSede = await apiService.updateSede(id, sedeData);
      notify.success('Sede actualizada exitosamente');
      refresh();
      return updatedSede;
    } catch (error: any) {
      notify.error(`Error actualizando sede: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const deleteSede = useCallback(async (id: string) => {
    try {
      await apiService.deleteSede(id);
      notify.success('Sede eliminada exitosamente');
      refresh();
    } catch (error: any) {
      notify.error(`Error eliminando sede: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  return {
    sedes: sedes || [],
    loading,
    error,
    refresh,
    createSede,
    updateSede,
    deleteSede
  };
};

// ============================================================================
// HOOK PARA NODOS DICOM
// ============================================================================

export const useDicomNodes = () => {
  const {
    data: nodes,
    loading,
    error,
    refresh,
    run: loadNodes
  } = useRequest(
    () => apiService.getDicomNodes(),
    {
      manual: false,
      onError: (error) => {
        notify.error(`Error cargando nodos DICOM: ${error.message}`);
      }
    }
  );

  const createNode = useCallback(async (nodeData: any) => {
    try {
      const newNode = await apiService.createDicomNode(nodeData);
      notify.success('Nodo DICOM creado exitosamente');
      refresh();
      return newNode;
    } catch (error: any) {
      notify.error(`Error creando nodo: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const updateNode = useCallback(async (id: string, nodeData: any) => {
    try {
      const updatedNode = await apiService.updateDicomNode(id, nodeData);
      notify.success('Nodo DICOM actualizado exitosamente');
      refresh();
      return updatedNode;
    } catch (error: any) {
      notify.error(`Error actualizando nodo: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const deleteNode = useCallback(async (id: string) => {
    try {
      await apiService.deleteDicomNode(id);
      notify.success('Nodo DICOM eliminado exitosamente');
      refresh();
    } catch (error: any) {
      notify.error(`Error eliminando nodo: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  const performEcho = useCallback(async (nodeId: string) => {
    try {
      const result = await apiService.performEcho(nodeId);
      if (result.success) {
        notify.success('C-ECHO exitoso');
      } else {
        notify.error('C-ECHO falló');
      }
      return result;
    } catch (error: any) {
      notify.error(`Error en C-ECHO: ${error.message}`);
      throw error;
    }
  }, []);

  const performBatchEcho = useCallback(async () => {
    try {
      const result = await apiService.performBatchEcho();
      notify.info(`Batch Echo completado: ${result.successCount}/${result.totalTested} exitosos`);
      return result;
    } catch (error: any) {
      notify.error(`Error en Batch Echo: ${error.message}`);
      throw error;
    }
  }, []);

  const toggleNodeStatus = useCallback(async (id: string) => {
    try {
      const updatedNode = await apiService.toggleDicomNodeStatus(id);
      notify.success(`Nodo ${updatedNode.active ? 'activado' : 'desactivado'}`);
      refresh();
      return updatedNode;
    } catch (error: any) {
      notify.error(`Error cambiando estado: ${error.message}`);
      throw error;
    }
  }, [refresh]);

  return {
    nodes: nodes || [],
    loading,
    error,
    refresh,
    createNode,
    updateNode,
    deleteNode,
    performEcho,
    performBatchEcho,
    toggleNodeStatus
  };
};

// ============================================================================
// HOOK PARA ESTUDIOS PACS
// ============================================================================

export const useStudies = () => {
  const [studies, setStudies] = useState<Study[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchCriteria, setSearchCriteria] = useState<StudySearchCriteria>({});

  const searchStudies = useCallback(async (criteria: StudySearchCriteria) => {
    try {
      setLoading(true);
      setSearchCriteria(criteria);
      const results = await apiService.searchStudies(criteria);
      setStudies(results);
      return results;
    } catch (error: any) {
      notify.error(`Error buscando estudios: ${error.message}`);
      throw error;
    } finally {
      setLoading(false);
    }
  }, []);

  const loadAllStudies = useCallback(async () => {
    try {
      setLoading(true);
      const results = await apiService.getStudies();
      setStudies(results);
      return results;
    } catch (error: any) {
      notify.error(`Error cargando estudios: ${error.message}`);
      throw error;
    } finally {
      setLoading(false);
    }
  }, []);

  const getStudiesByPatient = useCallback(async (patientId: string) => {
    try {
      setLoading(true);
      const results = await apiService.getStudiesByPatient(patientId);
      setStudies(results);
      return results;
    } catch (error: any) {
      notify.error(`Error cargando estudios del paciente: ${error.message}`);
      throw error;
    } finally {
      setLoading(false);
    }
  }, []);

  const deleteStudy = useCallback(async (id: string) => {
    try {
      await apiService.deleteStudy(id);
      notify.success('Estudio eliminado exitosamente');
      // Recargar estudios con los criterios actuales
      if (Object.keys(searchCriteria).length > 0) {
        await searchStudies(searchCriteria);
      } else {
        await loadAllStudies();
      }
    } catch (error: any) {
      notify.error(`Error eliminando estudio: ${error.message}`);
      throw error;
    }
  }, [searchCriteria, searchStudies, loadAllStudies]);

  const reprocessStudy = useCallback(async (id: string) => {
    try {
      const reprocessedStudy = await apiService.reprocessStudy(id);
      notify.success('Estudio reprocesado exitosamente');
      // Actualizar la lista
      setStudies(prev => prev.map(study => 
        study.id === id ? reprocessedStudy : study
      ));
      return reprocessedStudy;
    } catch (error: any) {
      notify.error(`Error reprocesando estudio: ${error.message}`);
      throw error;
    }
  }, []);

  const launchViewer = useCallback(async (studyInstanceUID: string) => {
    try {
      const viewerResponse = await apiService.launchViewerForStudy(studyInstanceUID);
      // Abrir en nueva ventana
      window.open(viewerResponse.viewerUrl, '_blank');
      return viewerResponse;
    } catch (error: any) {
      notify.error(`Error lanzando visor: ${error.message}`);
      throw error;
    }
  }, []);

  return {
    studies,
    loading,
    searchCriteria,
    searchStudies,
    loadAllStudies,
    getStudiesByPatient,
    deleteStudy,
    reprocessStudy,
    launchViewer
  };
};

// ============================================================================
// HOOK PARA GESTIÓN DE ESTADO LOCAL
// ============================================================================

export const useLocalStorage = <T>(key: string, initialValue: T) => {
  const [storedValue, setStoredValue] = useState<T>(() => {
    try {
      const item = storage.get(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(`Error loading ${key} from localStorage:`, error);
      return initialValue;
    }
  });

  const setValue = useCallback((value: T | ((val: T) => T)) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      storage.setObject(key, valueToStore);
    } catch (error) {
      console.error(`Error saving ${key} to localStorage:`, error);
    }
  }, [key, storedValue]);

  return [storedValue, setValue] as const;
};

// ============================================================================
// HOOK PARA DEBOUNCE
// ============================================================================

export const useDebounce = <T>(value: T, delay: number): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};

// ============================================================================
// HOOK PARA TABLA CON PAGINACIÓN Y FILTROS
// ============================================================================

export const useTable = <T>(
  dataSource: T[],
  options?: {
    pageSize?: number;
    searchFields?: (keyof T)[];
  }
) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(options?.pageSize || WIZARD_CONSTANTS.DEFAULT_PAGE_SIZE);
  const [searchText, setSearchText] = useState('');
  const [sortConfig, setSortConfig] = useState<{
    key: keyof T;
    direction: 'asc' | 'desc';
  } | null>(null);

  // Filtrar datos por búsqueda
  const filteredData = useMemo(() => {
    if (!searchText || !options?.searchFields) return dataSource;
    
    return dataSource.filter(item =>
      options.searchFields!.some(field => {
        const value = item[field];
        return value?.toString().toLowerCase().includes(searchText.toLowerCase());
      })
    );
  }, [dataSource, searchText, options?.searchFields]);

  // Ordenar datos
  const sortedData = useMemo(() => {
    if (!sortConfig) return filteredData;
    
    return [...filteredData].sort((a, b) => {
      const aValue = a[sortConfig.key];
      const bValue = b[sortConfig.key];
      
      if (aValue < bValue) return sortConfig.direction === 'asc' ? -1 : 1;
      if (aValue > bValue) return sortConfig.direction === 'asc' ? 1 : -1;
      return 0;
    });
  }, [filteredData, sortConfig]);

  // Paginar datos
  const paginatedData = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return sortedData.slice(startIndex, startIndex + pageSize);
  }, [sortedData, currentPage, pageSize]);

  const handleSort = useCallback((key: keyof T) => {
    setSortConfig(current => {
      if (current?.key === key) {
        return {
          key,
          direction: current.direction === 'asc' ? 'desc' : 'asc'
        };
      }
      return { key, direction: 'asc' };
    });
  }, []);

  const handlePageChange = useCallback((page: number, size?: number) => {
    setCurrentPage(page);
    if (size) setPageSize(size);
  }, []);

  const handleSearch = useCallback((text: string) => {
    setSearchText(text);
    setCurrentPage(1); // Reset to first page when searching
  }, []);

  return {
    data: paginatedData,
    pagination: {
      current: currentPage,
      pageSize,
      total: sortedData.length,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total: number, range: [number, number]) =>
        `${range[0]}-${range[1]} de ${total} elementos`,
      onChange: handlePageChange
    },
    searchText,
    sortConfig,
    handleSort,
    handleSearch,
    totalFiltered: filteredData.length,
    totalOriginal: dataSource.length
  };
};

// ============================================================================
// EXPORTAR TODOS LOS HOOKS
// ============================================================================

export default {
  useAuth,
  useOrganizations,
  useUsers,
  useRoles,
  useSedes,
  useDicomNodes,
  useStudies,
  useLocalStorage,
  useDebounce,
  useTable
};