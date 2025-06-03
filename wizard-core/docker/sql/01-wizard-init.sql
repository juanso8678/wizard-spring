-- Extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Crear permisos básicos PACS (solo si no existen las tablas)
DO $$
BEGIN
    -- Verificar si existe la tabla permissions
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'permissions') THEN
        -- Insertar permisos PACS
        INSERT INTO permissions (id, code, description, active, created_at) VALUES
        (uuid_generate_v4(), 'PACS_VIEW', 'Ver estudios PACS', true, NOW()),
        (uuid_generate_v4(), 'PACS_IMPORT', 'Importar estudios DICOM', true, NOW()),
        (uuid_generate_v4(), 'PACS_EDIT', 'Editar estudios PACS', true, NOW()),
        (uuid_generate_v4(), 'PACS_DELETE', 'Eliminar estudios PACS', true, NOW()),
        (uuid_generate_v4(), 'ENRUTAMIENTO_VIEW', 'Ver nodos DICOM', true, NOW()),
        (uuid_generate_v4(), 'ENRUTAMIENTO_CREATE', 'Crear nodos DICOM', true, NOW()),
        (uuid_generate_v4(), 'ENRUTAMIENTO_EDIT', 'Editar nodos DICOM', true, NOW()),
        (uuid_generate_v4(), 'ENRUTAMIENTO_DELETE', 'Eliminar nodos DICOM', true, NOW()),
        (uuid_generate_v4(), 'VISOR_WEB_VIEW', 'Acceder al visor web', true, NOW()),
        (uuid_generate_v4(), 'ADMIN_ACCESS', 'Acceso de administrador', true, NOW()),
        (uuid_generate_v4(), 'SUPER_ADMIN_ACCESS', 'Acceso de super administrador', true, NOW())
        ON CONFLICT (code) DO NOTHING;
        
        RAISE NOTICE 'Permisos PACS insertados correctamente';
    ELSE
        RAISE NOTICE 'Tabla permissions no existe - se creará con JPA';
    END IF;
END
$$;
EOF