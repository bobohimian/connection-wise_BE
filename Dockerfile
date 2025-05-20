# 构建阶段
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# 生产镜像
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]