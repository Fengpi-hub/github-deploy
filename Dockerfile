# 多阶段构建：先编译，再打包成精简的运行镜像

# 阶段 1：编译（Maven + JDK17）
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
# 构建上下文是仓库根目录，所以这里复制 backend 目录进去
COPY backend .
RUN mvn -B package -DskipTests

# 阶段 2：运行（只要 JRE）
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/*.jar app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]