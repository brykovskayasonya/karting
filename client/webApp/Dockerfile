# Собирает Compose Multiplatform (wasmJs) веб-клиент и отдаёт статику через nginx,
# чтобы приложение открывалось в браузере как обычный сайт.
# Контекст сборки — папка client/.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew gradlew.bat gradle.properties settings.docker.gradle.kts ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY shared ./shared
COPY webApp ./webApp

RUN ./gradlew -c settings.docker.gradle.kts :webApp:wasmJsBrowserDistribution --no-daemon --stacktrace

FROM nginx:1.27-alpine AS runtime
COPY webApp/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/webApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html

EXPOSE 80
