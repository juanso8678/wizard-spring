# Wizard - Sistema PACS con Spring Boot

Sistema de gestión PACS (Picture Archiving and Communication System) desarrollado con Spring Boot 3.5.0 y arquitectura modular.

## 📋 Estructura del Proyecto

```
wizard/
├── wizard-core/          # Módulo principal con lógica de negocio
├── wizard-auth/          # Módulo de autenticación y autorización
├── wizard-gateway/       # API Gateway
├── wizard-common/        # Utilidades compartidas
└── pom.xml              # POM padre
```

## 🛠️ Tecnologías

- **Java**: 17
- **Spring Boot**: 3.5.0
- **Spring Security**: 6.5.0
- **Spring Data JPA**: Integrado
- **PostgreSQL**: Base de datos principal
- **JWT**: Autenticación con tokens
- **Maven**: Gestión de dependencias
- **Lombok**: Reducción de código boilerplate
- **OpenAPI/Swagger**: Documentación de API
- **TestContainers**: Pruebas de integración
- **Micrometer**: Métricas y monitoreo

## 🚀 Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- PostgreSQL 12+
- Git

## ⚡ Inicio Rápido

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/wizard.git
cd wizard
```

### 2. Configurar base de datos
```bash
# Crear base de datos PostgreSQL
createdb wizard_db
```

### 3. Configurar variables de entorno
```bash
# Crear archivo application-local.yml en wizard-core/src/main/resources/
cp wizard-core/src/main/resources/application.yml wizard-core/src/main/resources/application-local.yml
# Editar con tus credenciales de base de datos
```

### 4. Compilar el proyecto
```bash
mvn clean compile
```

### 5. Ejecutar la aplicación
```bash
mvn spring-boot:run -pl wizard-core
```

### 6. Verificar que funciona
```bash
curl http://localhost:8080/actuator/health
```

## 📖 Documentación de API

Una vez que la aplicación esté corriendo, puedes acceder a:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Métricas**: http://localhost:8080/actuator/metrics

## 🧪 Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas de integración
mvn verify

# Ejecutar pruebas con cobertura
mvn clean test jacoco:report
```

## 📁 Configuración

### Variables de Entorno Principales

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de PostgreSQL | `localhost` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `wizard_db` |
| `DB_USERNAME` | Usuario de base de datos | - |
| `DB_PASSWORD` | Contraseña de base de datos | - |
| `JWT_SECRET` | Secreto para firmar JWT | - |

## 🏗️ Arquitectura

### Módulos

- **wizard-core**: Contiene la lógica principal, entidades JPA, servicios y controladores REST
- **wizard-auth**: Maneja autenticación JWT y autorización basada en roles
- **wizard-gateway**: API Gateway para enrutamiento y load balancing
- **wizard-common**: Utilidades, DTOs y configuraciones compartidas

### Características Principales

- ✅ API REST con Spring Web
- ✅ Autenticación JWT
- ✅ Validación con Bean Validation
- ✅ Documentación automática con OpenAPI
- ✅ Métricas con Micrometer
- ✅ Health checks con Actuator
- ✅ Pruebas de integración con TestContainers
- ✅ Retry automático con Spring Retry

## 🤝 Contribuir

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir un Pull Request

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 🐛 Reporte de Bugs

Si encuentras algún bug, por favor abre un [issue](https://github.com/tu-usuario/wizard/issues) describiendo:

- Pasos para reproducir el error
- Comportamiento esperado vs actual
- Versión de Java y sistema operativo
- Logs relevantes

## 📞 Contacto

- **Desarrollador**: Tu Nombre
- **Email**: tu-email@ejemplo.com
- **LinkedIn**: tu-perfil-linkedin