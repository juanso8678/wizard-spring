// ============================================================================
// API SERVICE - WIZARD Admin Panel
// ============================================================================

import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { message } from 'antd';
import type {
  LoginRequest,
  JwtResponse,
  User,
  Organization,
  OrganizationRequest,
  Role,
  RoleRequest,
  Permission,
  Module,
  Sede,
  SedeRequest,
  UserRole,
  UserSede,
  DicomNode,
  DicomNodeRequest,
  Study,
  StudySearchCriteria,
  ViewerRequest,
  ViewerResponse,
  StudyStats,
  ApiError
} from '@/types';

// ============================================================================
// CONFIGURACIÓN BASE
// ============================================================================

class ApiService {
  private api: AxiosInstance;
  private token: string | null = null;

  constructor() {
    this.api = axios.create({
      baseURL: '/api',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
    this.loadTokenFromStorage();
  }

  // ============================================================================
  // CONFIGURACIÓN DE INTERCEPTORS
  // ============================================================================

  private setupInterceptors() {
    // Request interceptor - agregar token automáticamente
    this.api.interceptors.request.use(
      (config) => {
        if (this.token) {
          config.headers.Authorization = `Bearer ${this.token}`;
        }
        
        // Agregar organization ID si está disponible
        const orgId = this.getOrganizationId();
        if (orgId) {
          config.headers['X-Organization-Id'] = orgId;
        }

        console.log(`🔍 API Request: ${config.method?.toUpperCase()} ${config.url}`);
        return config;
      },
      (error) => {
        console.error('❌ Request Error:', error);
        return Promise.reject(error);
      }
    );

    // Response interceptor - manejo de errores globales
    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        console.log(`✅ API Response: ${response.status} ${response.config.url}`);
        return response;
      },
      (error) => {
        console.error('❌ API Error:', error);
        
        if (error.response) {
          const apiError: ApiError = {
            status: error.response.status,
            message: error.response.data?.message || error.message,
            timestamp: Date.now()
          };

          // Manejar errores específicos
          switch (error.response.status) {
            case 401:
              message.error('Sesión expirada. Por favor, inicia sesión nuevamente.');
              this.logout();
              break;
            case 403:
              message.error('No tienes permisos para realizar esta acción.');
              break;
            case 404:
              message.error('Recurso no encontrado.');
              break;
            case 500:
              message.error('Error interno del servidor. Contacta al administrador.');
              break;
            default:
              message.error(apiError.message || 'Error inesperado en la comunicación.');
          }

          return Promise.reject(apiError);
        }

        // Error de red
        message.error('Error de conexión. Verifica tu conectividad.');
        return Promise.reject({
          status: 0,
          message: 'Error de conexión',
          timestamp: Date.now()
        });
      }
    );
  }

  // ============================================================================
  // GESTIÓN DE TOKENS
  // ============================================================================

  private loadTokenFromStorage() {
    this.token = localStorage.getItem('wizard_token');
  }

  private saveTokenToStorage(token: string) {
    this.token = token;
    localStorage.setItem('wizard_token', token);
  }

  private getOrganizationId(): string | null {
    return localStorage.getItem('wizard_org_id');
  }

  public setOrganizationId(orgId: string) {
    localStorage.setItem('wizard_org_id', orgId);
  }

  public logout() {
    this.token = null;
    localStorage.removeItem('wizard_token');
    localStorage.removeItem('wizard_org_id');
    localStorage.removeItem('wizard_user');
    window.location.href = '/login';
  }

  public isAuthenticated(): boolean {
    return !!this.token;
  }

  // ============================================================================
  // AUTENTICACIÓN
  // ============================================================================

  async login(credentials: LoginRequest): Promise<JwtResponse> {
    try {
      const response = await this.api.post<JwtResponse>('/auth/login', credentials);
      const { token } = response.data;
      
      this.saveTokenToStorage(token);
      message.success('Sesión iniciada correctamente');
      
      return response.data;
    } catch (error) {
      console.error('Login failed:', error);
      throw error;
    }
  }

  async testAuth(): Promise<boolean> {
    try {
      await this.api.get('/auth/test');
      return true;
    } catch {
      return false;
    }
  }

  // ============================================================================
  // USUARIOS
  // ============================================================================

  async getUsers(): Promise<User[]> {
    const response = await this.api.get<User[]>('/users');
    return response.data;
  }

  async getUserById(id: string): Promise<User> {
    const response = await this.api.get<User>(`/users/${id}`);
    return response.data;
  }

  async createUser(userData: Partial<User>): Promise<User> {
    const response = await this.api.post<User>('/users', userData);
    return response.data;
  }

  async deleteUser(id: string): Promise<void> {
    await this.api.delete(`/users/${id}`);
  }

  // ============================================================================
  // ORGANIZACIONES
  // ============================================================================

  async getOrganizations(): Promise<Organization[]> {
    const response = await this.api.get<Organization[]>('/organizations');
    return response.data;
  }

  async getOrganizationById(id: string): Promise<Organization> {
    const response = await this.api.get<Organization>(`/organizations/${id}`);
    return response.data;
  }

  async createOrganization(orgData: OrganizationRequest): Promise<Organization> {
    const response = await this.api.post<Organization>('/organizations', orgData);
    return response.data;
  }

  async updateOrganization(id: string, orgData: OrganizationRequest): Promise<Organization> {
    const response = await this.api.put<Organization>(`/organizations/${id}`, orgData);
    return response.data;
  }

  async deleteOrganization(id: string): Promise<void> {
    await this.api.delete(`/organizations/${id}`);
  }

  // ============================================================================
  // ROLES Y PERMISOS
  // ============================================================================

  async getRoles(): Promise<Role[]> {
    const response = await this.api.get<Role[]>('/roles');
    return response.data;
  }

  async getRoleById(id: string): Promise<Role> {
    const response = await this.api.get<Role>(`/roles/${id}`);
    return response.data;
  }

  async createRole(roleData: RoleRequest): Promise<Role> {
    const response = await this.api.post<Role>('/roles', roleData);
    return response.data;
  }

  async updateRole(id: string, roleData: RoleRequest): Promise<Role> {
    const response = await this.api.put<Role>(`/roles/${id}`, roleData);
    return response.data;
  }

  async deleteRole(id: string): Promise<void> {
    await this.api.delete(`/roles/${id}`);
  }

  async assignPermissionsToRole(roleId: string, permissionIds: string[]): Promise<Role> {
    const response = await this.api.put<Role>(`/roles/${roleId}/permissions`, permissionIds);
    return response.data;
  }

  async getPermissions(): Promise<Permission[]> {
    const response = await this.api.get<Permission[]>('/permissions');
    return response.data;
  }

  async getModules(): Promise<Module[]> {
    const response = await this.api.get<Module[]>('/permissions/modules');
    return response.data;
  }

  // ============================================================================
  // SEDES
  // ============================================================================

  async getSedes(): Promise<Sede[]> {
    const response = await this.api.get<Sede[]>('/sedes');
    return response.data;
  }

  async getSedeById(id: string): Promise<Sede> {
    const response = await this.api.get<Sede>(`/sedes/${id}`);
    return response.data;
  }

  async createSede(sedeData: SedeRequest): Promise<Sede> {
    const response = await this.api.post<Sede>('/sedes', sedeData);
    return response.data;
  }

  async updateSede(id: string, sedeData: SedeRequest): Promise<Sede> {
    const response = await this.api.put<Sede>(`/sedes/${id}`, sedeData);
    return response.data;
  }

  async deleteSede(id: string): Promise<void> {
    await this.api.delete(`/sedes/${id}`);
  }

  // ============================================================================
  // ASIGNACIONES USUARIO-ROL
  // ============================================================================

  async getUserRoles(userId: string): Promise<UserRole[]> {
    const response = await this.api.get<UserRole[]>(`/user-roles/user/${userId}`);
    return response.data;
  }

  async assignRoleToUser(userId: string, roleId: string, organizationId: string): Promise<UserRole> {
    const response = await this.api.post<UserRole>('/user-roles', {
      userId,
      roleId,
      organizationId
    });
    return response.data;
  }

  async removeRoleFromUser(userId: string, roleId: string): Promise<void> {
    await this.api.delete(`/user-roles/${userId}/${roleId}`);
  }

  async getUserPermissions(userId: string): Promise<{ permissions: string[] }> {
    const response = await this.api.get<{ permissions: string[] }>(`/user-roles/user/${userId}/permissions`);
    return response.data;
  }

  // ============================================================================
  // ASIGNACIONES USUARIO-SEDE
  // ============================================================================

  async getUserSedes(userId: string): Promise<UserSede[]> {
    const response = await this.api.get<UserSede[]>(`/user-sedes/user/${userId}`);
    return response.data;
  }

  async assignUserToSede(userId: string, sedeId: string): Promise<UserSede> {
    const response = await this.api.post<UserSede>('/user-sedes', {
      userId,
      sedeId
    });
    return response.data;
  }

  async removeUserFromSede(userId: string, sedeId: string): Promise<void> {
    await this.api.delete(`/user-sedes/${userId}/${sedeId}`);
  }

  async checkUserSedeAssignment(userId: string, sedeId: string): Promise<{ isAssigned: boolean }> {
    const response = await this.api.get<{ isAssigned: boolean }>(`/user-sedes/check/${userId}/${sedeId}`);
    return response.data;
  }

  // ============================================================================
  // PACS - NODOS DICOM
  // ============================================================================

  async getDicomNodes(): Promise<DicomNode[]> {
    const response = await this.api.get<DicomNode[]>('/pacs/dicom-nodes');
    return response.data;
  }

  async getDicomNodeById(id: string): Promise<DicomNode> {
    const response = await this.api.get<DicomNode>(`/pacs/dicom-nodes/${id}`);
    return response.data;
  }

  async createDicomNode(nodeData: DicomNodeRequest): Promise<DicomNode> {
    const response = await this.api.post<DicomNode>('/pacs/dicom-nodes', nodeData);
    return response.data;
  }

  async updateDicomNode(id: string, nodeData: DicomNodeRequest): Promise<DicomNode> {
    const response = await this.api.put<DicomNode>(`/pacs/dicom-nodes/${id}`, nodeData);
    return response.data;
  }

  async deleteDicomNode(id: string): Promise<void> {
    await this.api.delete(`/pacs/dicom-nodes/${id}`);
  }

  async performEcho(nodeId: string): Promise<{ success: boolean; timestamp: number; message: string }> {
    const response = await this.api.post(`/pacs/dicom-nodes/${nodeId}/echo`);
    return response.data;
  }

  async performBatchEcho(): Promise<{ results: Record<string, boolean>; totalTested: number; successCount: number; failureCount: number }> {
    const response = await this.api.post('/pacs/dicom-nodes/batch-echo');
    return response.data;
  }

  async getActiveDicomNodes(): Promise<DicomNode[]> {
    const response = await this.api.get<DicomNode[]>('/pacs/dicom-nodes/active');
    return response.data;
  }

  async toggleDicomNodeStatus(id: string): Promise<DicomNode> {
    const response = await this.api.post<DicomNode>(`/pacs/dicom-nodes/${id}/toggle`);
    return response.data;
  }

  async getDicomNodeStats(): Promise<any> {
    const response = await this.api.get('/pacs/dicom-nodes/stats');
    return response.data;
  }

  // ============================================================================
  // PACS - ESTUDIOS
  // ============================================================================

  async getStudies(): Promise<Study[]> {
    const response = await this.api.get<Study[]>('/pacs/studies');
    return response.data;
  }

  async getStudyById(id: string): Promise<Study> {
    const response = await this.api.get<Study>(`/pacs/studies/${id}`);
    return response.data;
  }

  async getStudyByUID(studyInstanceUID: string): Promise<Study> {
    const response = await this.api.get<Study>(`/pacs/studies/uid/${studyInstanceUID}`);
    return response.data;
  }

  async searchStudies(criteria: StudySearchCriteria): Promise<Study[]> {
    const params = new URLSearchParams();
    
    Object.entries(criteria).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params.append(key, value.toString());
      }
    });

    const response = await this.api.get<Study[]>(`/pacs/studies/search?${params.toString()}`);
    return response.data;
  }

  async getStudiesByPatient(patientId: string): Promise<Study[]> {
    const response = await this.api.get<Study[]>(`/pacs/studies/patient/${patientId}`);
    return response.data;
  }

  async deleteStudy(id: string): Promise<void> {
    await this.api.delete(`/pacs/studies/${id}`);
  }

  async reprocessStudy(id: string): Promise<Study> {
    const response = await this.api.post<Study>(`/pacs/studies/${id}/reprocess`);
    return response.data;
  }

  async getStudyStats(): Promise<{ totalStudies: number; timestamp: number }> {
    const response = await this.api.get('/pacs/studies/stats');
    return response.data;
  }

  // ============================================================================
  // VISOR WEB
  // ============================================================================

  async launchViewer(viewerRequest: ViewerRequest): Promise<ViewerResponse> {
    const response = await this.api.post<ViewerResponse>('/pacs/viewer/launch', viewerRequest);
    return response.data;
  }

  async launchViewerForStudy(studyInstanceUID: string): Promise<ViewerResponse> {
    const response = await this.api.get<ViewerResponse>(`/pacs/viewer/launch/${studyInstanceUID}`);
    return response.data;
  }

  async validateViewerSession(sessionToken: string): Promise<{ valid: boolean }> {
    const response = await this.api.post<{ valid: boolean }>('/pacs/viewer/validate', { sessionToken });
    return response.data;
  }

  // ============================================================================
  // PACS ENGINE
  // ============================================================================

  async getPacsEngineStatus(): Promise<any> {
    const response = await this.api.get('/pacs/engine/status');
    return response.data;
  }

  async testPacsEngineConnection(): Promise<{ connected: boolean; timestamp: number }> {
    const response = await this.api.post('/pacs/engine/test-connection');
    return response.data;
  }

  async getPacsEngineSystemInfo(): Promise<any> {
    const response = await this.api.get('/pacs/engine/system-info');
    return response.data;
  }

  async getPacsEngineHealth(): Promise<any> {
    const response = await this.api.get('/pacs/engine/health');
    return response.data;
  }

  // ============================================================================
  // UTILIDADES
  // ============================================================================

  async ping(): Promise<string> {
    const response = await this.api.get<string>('/ping');
    return response.data;
  }

  async testDatabase(): Promise<string> {
    const response = await this.api.get<string>('/test/db');
    return response.data;
  }
}

// Exportar instancia singleton
export const apiService = new ApiService();
export default apiService;