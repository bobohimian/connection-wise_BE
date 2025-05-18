# 构建阶段
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# 生产镜像
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 环境变量配置
ARG POSTGRES_USERNAME
ARG POSTGRES_PASSWORD
ARG DEEPSEEK_API_KEY
ARG MINIO_USERNAME
ARG MINIO_PASSWORD

# 生产阶段环境变量
ENV POSTGRES_USERNAME=${POSTGRES_USERNAME} \
    POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
    DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY} \
    MINIO_USERNAME=${MINIO_USERNAME} \
    MINIO_PASSWORD=${MINIO_PASSWORD}

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]