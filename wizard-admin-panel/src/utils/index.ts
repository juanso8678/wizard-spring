// ============================================================================
// UTILIDADES - WIZARD Admin Panel
// ============================================================================

import dayjs from 'dayjs';
import { message } from 'antd';

// ============================================================================
// CONSTANTES
// ============================================================================

export const WIZARD_CONSTANTS = {
  APP_NAME: 'WIZARD PACS System',
  VERSION: '1.0.0',
  
  // Configuración de tabla
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZE_OPTIONS: ['10', '20', '50', '100'],
  
  // Configuración de fechas
  DATE_FORMAT: 'YYYY-MM-DD',
  DATETIME_FORMAT: 'YYYY-MM-DD HH:mm:ss',
  TIME_FORMAT: 'HH:mm:ss',
  
  // Storage keys
  STORAGE_KEYS: {
    TOKEN: 'wizard_token',
    USER: 'wizard_user',
    ORG_ID: 'wizard_org_id',
    THEME: 'wizard_theme',
    SIDEBAR_COLLAPSED: 'wizard_sidebar_collapsed'
  },
  
  // Roles del sistema
  SYSTEM_ROLES: {
    SUPER_ADMIN: 'SUPER_ADMIN',
    ADMIN_ORGANIZACION: 'ADMIN_ORGANIZACION',
    TECNICO_RADIOLOGO: 'TECNICO_RADIOLOGO',
    ADMIN_PACS: 'ADMIN_PACS',
    OPERADOR_VISOR: 'OPERADOR_VISOR',
    AUDITOR: 'AUDITOR'
  },
  
  // Tipos de nodos DICOM
  DICOM_NODE_TYPES: {
    SCU: 'SCU',
    SCP: 'SCP',
    BOTH: 'BOTH'
  },
  
  // Modalidades DICOM comunes
  DICOM_MODALITIES: [
    'CR', 'CT', 'MR', 'NM', 'PT', 'RF', 'RG', 'SC', 'US', 'XA', 'DX',
    'MG', 'IO', 'PX', 'GM', 'SM', 'XC', 'OP', 'ES', 'RT', 'HC', 'DG',
    'LS', 'VL', 'OT'
  ],
  
  // Estados de estudios
  STUDY_STATUSES: {
    RECEIVED: 'RECEIVED',
    PROCESSING: 'PROCESSING',
    COMPLETED: 'COMPLETED',
    ERROR: 'ERROR'
  }
};

// ============================================================================
// VALIDACIONES
// ============================================================================

export const validators = {
  // Email
  email: (email: string): boolean => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },
  
  // Teléfono (básico)
  phone: (phone: string): boolean => {
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''));
  },
  
  // AE Title DICOM (máximo 16 caracteres alfanuméricos)
  aeTitle: (aeTitle: string): boolean => {
    const aeTitleRegex = /^[A-Z0-9_]{1,16}$/;
    return aeTitleRegex.test(aeTitle);
  },
  
  // Puerto (1-65535)
  port: (port: number): boolean => {
    return port >= 1 && port <= 65535;
  },
  
  // IP Address
  ipAddress: (ip: string): boolean => {
    const ipRegex = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    return ipRegex.test(ip);
  },
  
  // Hostname
  hostname: (hostname: string): boolean => {
    const hostnameRegex = /^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\.)*[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$/;
    return hostnameRegex.test(hostname) || validators.ipAddress(hostname);
  },
  
  // UUID
  uuid: (uuid: string): boolean => {
    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    return uuidRegex.test(uuid);
  }
};

// ============================================================================
// FORMATTERS
// ============================================================================

export const formatters = {
  // Fechas
  date: (date: string | Date, format: string = WIZARD_CONSTANTS.DATE_FORMAT): string => {
    if (!date) return '-';
    return dayjs(date).format(format);
  },
  
  datetime: (date: string | Date): string => {
    if (!date) return '-';
    return dayjs(date).format(WIZARD_CONSTANTS.DATETIME_FORMAT);
  },
  
  time: (time: string | Date): string => {
    if (!time) return '-';
    return dayjs(time).format(WIZARD_CONSTANTS.TIME_FORMAT);
  },
  
  // Tamaños de archivo
  fileSize: (bytes: number): string => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(2))} ${sizes[i]}`;
  },
  
  // Números
  number: (num: number): string => {
    return new Intl.NumberFormat('es-ES').format(num);
  },
  
  // Moneda (EUR por defecto)
  currency: (amount: number, currency: string = 'EUR'): string => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: currency
    }).format(amount);
  },
  
  // Duración en milisegundos a texto legible
  duration: (ms: number): string => {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    
    if (days > 0) return `${days}d ${hours % 24}h`;
    if (hours > 0) return `${hours}h ${minutes % 60}m`;
    if (minutes > 0) return `${minutes}m ${seconds % 60}s`;
    return `${seconds}s`;
  },
  
  // Capitalizar primera letra
  capitalize: (str: string): string => {
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
  },
  
  // Truncar texto
  truncate: (str: string, length: number = 50): string => {
    if (str.length <= length) return str;
    return str.substring(0, length) + '...';
  }
};

// ============================================================================
// UTILIDADES DE STORAGE
// ============================================================================

export const storage = {
  // Local Storage
  get: (key: string): string | null => {
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  },
  
  set: (key: string, value: string): void => {
    try {
      localStorage.setItem(key, value);
    } catch (error) {
      console.error('Error saving to localStorage:', error);
    }
  },
  
  remove: (key: string): void => {
    try {
      localStorage.removeItem(key);
    } catch (error) {
      console.error('Error removing from localStorage:', error);
    }
  },
  
  // Helpers para objetos JSON
  getObject: <T>(key: string): T | null => {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : null;
    } catch {
      return null;
    }
  },
  
  setObject: (key: string, value: any): void => {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error('Error saving object to localStorage:', error);
    }
  }
};

// ============================================================================
// UTILIDADES DE COLORES PARA ESTADOS
// ============================================================================

export const getStatusColor = (status: string): string => {
  const statusColors: Record<string, string> = {
    // Estados generales
    'ACTIVE': '#52c41a',
    'INACTIVE': '#ff4d4f',
    'PENDING': '#faad14',
    
    // Estados PACS
    'RECEIVED': '#1890ff',
    'PROCESSING': '#faad14', 
    'COMPLETED': '#52c41a',
    'ERROR': '#ff4d4f',
    
    // Estados de conexión
    'UP': '#52c41a',
    'DOWN': '#ff4d4f',
    'UNKNOWN': '#d9d9d9',
    
    // Estados de nodos DICOM
    'ONLINE': '#52c41a',
    'OFFLINE': '#ff4d4f',
    'TESTING': '#faad14'
  };
  
  return statusColors[status.toUpperCase()] || '#d9d9d9';
};

export const getModalityColor = (modality: string): string => {
  const modalityColors: Record<string, string> = {
    'CT': '#1890ff',
    'MR': '#52c41a', 
    'CR': '#faad14',
    'DX': '#722ed1',
    'US': '#13c2c2',
    'NM': '#eb2f96',
    'PT': '#f5222d',
    'RF': '#fa8c16',
    'XA': '#a0d911',
    'MG': '#fa541c'
  };
  
  return modalityColors[modality] || '#d9d9d9';
};

// ============================================================================
// UTILIDADES PARA PERMISOS
// ============================================================================

export const permissionUtils = {
  // Verificar si el usuario actual tiene un permiso específico
  hasPermission: (userPermissions: string[], requiredPermission: string): boolean => {
    return userPermissions.includes(requiredPermission) || 
           userPermissions.includes('SUPER_ADMIN_ACCESS');
  },
  
  // Verificar si tiene todos los permisos
  hasAllPermissions: (userPermissions: string[], requiredPermissions: string[]): boolean => {
    if (userPermissions.includes('SUPER_ADMIN_ACCESS')) return true;
    return requiredPermissions.every(permission => userPermissions.includes(permission));
  },
  
  // Verificar si tiene alguno de los permisos
  hasAnyPermission: (userPermissions: string[], requiredPermissions: string[]): boolean => {
    if (userPermissions.includes('SUPER_ADMIN_ACCESS')) return true;
    return requiredPermissions.some(permission => userPermissions.includes(permission));
  }
};

// ============================================================================
// UTILIDADES PARA NOTIFICACIONES
// ============================================================================

export const notify = {
  success: (msg: string) => message.success(msg),
  error: (msg: string) => message.error(msg),
  warning: (msg: string) => message.warning(msg),
  info: (msg: string) => message.info(msg),
  loading: (msg: string) => message.loading(msg)
};

// ============================================================================
// UTILIDADES PARA URLS Y NAVEGACIÓN
// ============================================================================

export const urlUtils = {
  // Construir query string desde objeto
  buildQueryString: (params: Record<string, any>): string => {
    const searchParams = new URLSearchParams();
    
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        searchParams.append(key, value.toString());
      }
    });
    
    const queryString = searchParams.toString();
    return queryString ? `?${queryString}` : '';
  },
  
  // Parsear query string a objeto
  parseQueryString: (queryString: string): Record<string, string> => {
    const params = new URLSearchParams(queryString);
    const result: Record<string, string> = {};
    
    params.forEach((value, key) => {
      result[key] = value;
    });
    
    return result;
  }
};

// ============================================================================
// UTILIDADES PARA DESCARGAS
// ============================================================================

export const downloadUtils = {
  // Descargar archivo desde blob
  downloadBlob: (blob: Blob, filename: string): void => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
  
  // Descargar JSON como archivo
  downloadJSON: (data: any, filename: string): void => {
    const jsonString = JSON.stringify(data, null, 2);
    const blob = new Blob([jsonString], { type: 'application/json' });
    downloadUtils.downloadBlob(blob, `${filename}.json`);
  },
  
  // Descargar CSV
  downloadCSV: (data: any[], filename: string): void => {
    if (!data.length) return;
    
    const headers = Object.keys(data[0]);
    const csvContent = [
      headers.join(','),
      ...data.map(row => headers.map(header => `"${row[header] || ''}"`).join(','))
    ].join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    downloadUtils.downloadBlob(blob, `${filename}.csv`);
  }
};

// ============================================================================
// UTILIDADES PARA DEBUGGING
// ============================================================================

export const debug = {
  log: (message: string, data?: any) => {
    if (process.env.NODE_ENV === 'development') {
      console.log(`🔍 [WIZARD DEBUG] ${message}`, data || '');
    }
  },
  
  error: (message: string, error?: any) => {
    if (process.env.NODE_ENV === 'development') {
      console.error(`❌ [WIZARD ERROR] ${message}`, error || '');
    }
  },
  
  table: (data: any) => {
    if (process.env.NODE_ENV === 'development') {
      console.table(data);
    }
  }
};

// ============================================================================
// EXPORTAR TODO
// ============================================================================

export default {
  WIZARD_CONSTANTS,
  validators,
  formatters,
  storage,
  getStatusColor,
  getModalityColor,
  permissionUtils,
  notify,
  urlUtils,
  downloadUtils,
  debug
};