# Building the app
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /OurHome

# Копираме всички файлове в работната директория на контейнера
COPY . .

# Даваме права за изпълнение на mvnw
RUN chmod +x ./mvnw

# Изпълняваме Maven, за да изградим проекта (без тестове за по-бърза компилация)
RUN ./mvnw clean package -DskipTests

# Стъпка 2: Създаване на финалния образ
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /OurHome

# Дефинираме аргумент за текущата дата
ARG BUILD_DATE=unknown
ARG BUILD_VERSION=unknown

# Метаданни
LABEL MAINTAINER="Milen Zapryanov <milen.zapryanov@gmail.com>" \
      DESCRIPTION="OurHome Residential entity management platform" \
      VERSION=${BUILD_VERSION} \
      BUILD_DATE=${BUILD_DATE} \
      VCS-URL="https://github.com/MilenZapryanoff/OurHome-Residential-entity-management-platform"

# Копираме JAR файла от предишната стъпка
COPY --from=build /OurHome/target/OurHome*.jar OurHome.jar

# Копираме папката с ресурсите в контейнера
COPY --from=build /OurHome/src/main/resources /OurHome/src/main/resources

# Експонираме порта, който приложението използва (8080 по подразбиране)
EXPOSE 8080

# Дефинираме командата за стартиране на приложението
ENTRYPOINT ["java", "-jar", "OurHome.jar"]

# IMAGE BUILD
# docker docker build --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") --build-arg BUILD_VERSION=2.$(date -u +"%y.%m%d") -t milenzapryanov/ourhome:2.$(date -u +"%y.%m%d") .