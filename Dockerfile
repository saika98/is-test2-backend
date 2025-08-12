# ---- Build stage ----
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Gradle wrapper を先にコピーして実行権限付与（Windows改行^M対策も）
COPY gradlew ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# 依存関係のキャッシュを効かせるために設定ファイルを先にコピー
COPY settings.gradle build.gradle ./
RUN ./gradlew --no-daemon dependencies || true

# 残りのソース
COPY src ./src

# テストをスキップしてJarビルド
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV JAVA_OPTS=""
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
