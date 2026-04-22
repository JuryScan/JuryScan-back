# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Definir diretório de trabalho
WORKDIR /app

# Copiar apenas arquivos de dependências primeiro (melhor cache de layers)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixar dependências (essa layer será cacheada se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação (pular compilação e execução de testes)
RUN mvn clean package -Dmaven.test.skip=true -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Metadados
LABEL maintainer="juryscan@example.com"
LABEL description="JuryScan Backend API"
LABEL version="1.0.0"

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring

# Definir diretório de trabalho
WORKDIR /app

# Copiar apenas o JAR da stage de build
COPY --from=build /app/target/*.jar app.jar

# Mudar ownership para usuário não-root
RUN chown spring:spring app.jar

# Trocar para usuário não-root
USER spring:spring

# Expor porta (configurável via variável de ambiente)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Configurações JVM otimizadas
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Djava.security.egd=file:/dev/./urandom"

# Executar aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

