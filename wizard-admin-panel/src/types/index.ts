// ============================================================================
// TIPOS BASE DEL SISTEMA WIZARD
// ============================================================================

// Auth Types
export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface JwtResponse {
  token: string;
}

export interface User {
  id: string;
  email: string;
  username: string;
  name: string;
  role: string;
  activo: boolean;
  organizationId?: string;
}

// Organization Types
export interface Organization {
  id: string;
  name: string;
  logo?: string;
  contactEmail: string;
  contactPhone: string;
  address: string;
  description?: string;
}

export interface OrganizationRequest {
  name: string;
  logo?: string;
  contactEmail: string;
  contactPhone: string;
  address: string;
  description?: string;
}

// Role Types
export interface Role {
  id: string;
  name: string;
  displayName: string;
  description: string;
  organizationId?: string;
  active: boolean;
  permissions: Permission[];
}

export interface RoleRequest {
  name: string;
  displayName: string;
  description: string;
  organizationId?: string;
  permissionIds: string[];
}

// Permission Types
export interface Permission {
  id: string;
  code: string;
  displayName: string;
  description: string;
  moduleName: string;
  moduleDisplayName: string;
  functionName: string;
  functionDisplayName: string;
  active: boolean;
}

export interface Module {
  id: string;
  name: string;
  displayName: string;
  description: string;
  icon: string;
  active: boolean;
  permissions?: Permission[];
}

// Sede Types
export interface Sede {
  id: string;
  name: string;
  address: string;
  contact: string;
  email: string;
  phone: string;
  typeSede: string;
  description?: string;
  organizationId: string;
}

export interface SedeRequest {
  name: string;
  address: string;
  contact: string;
  email: string;
  phone: string;
  typeSede: string;
  description?: string;
  organizationId: string;
}

// User-Role Assignment
export interface UserRole {
  id: string;
  userId: string;
  username: string;
  userEmail: string;
  roleId: string;
  roleName: string;
  roleDisplayName: string;
  organizationId: string;
  active: boolean;
}

// User-Sede Assignment
export interface UserSede {
  id: string;
  userId: string;
  username: string;
  userEmail: string;
  userName: string;
  sedeId: string;
  sedeName: string;
  sedeAddress: string;
  organizationId: string;
  organizationName: string;
}

// PACS Types
export interface DicomNode {
  id: string;
  aeTitle: string;
  hostname: string;
  port: number;
  description?: string;
  nodeType: 'SCU' | 'SCP' | 'BOTH';
  organizationId: string;
  queryRetrieve: boolean;
  store: boolean;
  echo: boolean;
  find: boolean;
  move: boolean;
  get: boolean;
  active: boolean;
  createdAt: string;
}

export interface DicomNodeRequest {
  aeTitle: string;
  hostname: string;
  port: number;
  description?: string;
  nodeType: 'SCU' | 'SCP' | 'BOTH';
  organizationId: string;
  queryRetrieve: boolean;
  store: boolean;
  echo: boolean;
  find: boolean;
  move: boolean;
  get: boolean;
}

export interface Study {
  id: string;
  studyInstanceUID: string;
  patientId: string;
  patientName: string;
  patientBirthDate?: string;
  patientSex?: string;
  studyDate: string;
  studyTime?: string;
  studyDescription?: string;
  accessionNumber?: string;
  referringPhysician?: string;
  modality: string;
  institutionName?: string;
  numberOfSeries: number;
  numberOfInstances: number;
  status: string;
  organizationId: string;
  createdAt: string;
  series?: Series[];
}

export interface Series {
  id: string;
  seriesInstanceUID: string;
  seriesNumber?: string;
  seriesDescription?: string;
  modality: string;
  seriesTime?: string;
  bodyPartExamined?: string;
  protocolName?: string;
  manufacturer?: string;
  numberOfInstances: number;
  status: string;
  createdAt: string;
  instances?: Instance[];
}

export interface Instance {
  id: string;
  sopInstanceUID: string;
  instanceNumber?: string;
  sopClassUID?: string;
  rows?: number;
  columns?: number;
  fileName?: string;
  fileSize?: number;
  status: string;
  createdAt: string;
}

// Search Types
export interface StudySearchCriteria {
  patientId?: string;
  patientName?: string;
  studyDate?: string;
  studyDateFrom?: string;
  studyDateTo?: string;
  modality?: string;
  accessionNumber?: string;
  limit?: number;
  offset?: number;
}

// Viewer Types
export interface ViewerRequest {
  studyInstanceUID: string;
  seriesInstanceUID?: string;
  organizationId: string;
  viewerType: 'WEB' | 'DESKTOP' | 'MOBILE';
  fullscreen: boolean;
}

export interface ViewerResponse {
  viewerUrl: string;
  studyInstanceUID: string;
  viewerType: string;
  sessionToken: string;
  fullscreen: boolean;
  timeoutMinutes: number;
}

// Statistics Types
export interface ModalityStats {
  modality: string;
  count: number;
}

export interface StudyStats {
  totalStudies: number;
  modalityStats: ModalityStats[];
  organizationId: string;
  timestamp: number;
}

// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

// Error Types
export interface ApiError {
  status: number;
  message: string;
  timestamp?: number;
}

// Menu Types
export interface MenuItem {
  key: string;
  label: string;
  icon?: React.ReactNode;
  children?: MenuItem[];
  permission?: string;
  path?: string;
}

// Dashboard Types
export interface DashboardStats {
  totalUsers: number;
  totalStudies: number;
  totalOrganizations: number;
  activeNodes: number;
  totalSedes: number;
  pacsEngineStatus: 'UP' | 'DOWN' | 'UNKNOWN';
}

// Table Types
export interface TableColumn<T = any> {
  title: string;
  dataIndex: keyof T;
  key: string;
  width?: number;
  fixed?: 'left' | 'right';
  sorter?: boolean;
  render?: (value: any, record: T, index: number) => React.ReactNode;
  filters?: Array<{ text: string; value: any }>;
  filterDropdown?: React.ReactNode;
  ellipsis?: boolean;
}