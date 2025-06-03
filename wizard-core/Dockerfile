FROM openjdk:17-jdk-slim

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar JAR (se generará con Maven)
COPY target/wizard-core-*.jar wizard-core.jar

# Crear directorios
RUN mkdir -p /app/logs /app/storage /app/temp

# Configurar usuario no-root (buena práctica)
RUN groupadd -r wizard && useradd -r -g wizard wizard
RUN chown -R wizard:wizard /app
USER wizard

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=docker,pacs
ENV JAVA_OPTS="-Xms512m -Xmx1g"

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar wizard-core.jar"]