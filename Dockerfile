# Building the app
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /OurHome

# Copy all files in working directory of the container
COPY . .

# Giving rights to mvnw (optional)
RUN chmod +x ./mvnw

# Creating JAR file
RUN ./mvnw clean package -DskipTests

# Creating final image
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /OurHome

# Defining variables
ARG BUILD_DATE=unknown
ARG BUILD_VERSION=unknown

# Adding meta data
LABEL MAINTAINER="Milen Zapryanov <milen.zapryanov@gmail.com>" \
      DESCRIPTION="OurHome Residential entity management platform" \
      VERSION=${BUILD_VERSION} \
      BUILD_DATE=${BUILD_DATE} \
      VCS-URL="https://github.com/MilenZapryanoff/OurHome-Residential-entity-management-platform"

# Copying JAR file
COPY --from=build /OurHome/target/OurHome*.jar OurHome.jar

# Copy resources folder. Important because of the upload photos and documents
COPY --from=build /OurHome/src/main/resources /OurHome/src/main/resources

# Port expose
EXPOSE 8080

# Starting the App
ENTRYPOINT ["java", "-jar", "OurHome.jar"]

# IMAGE BUILD
# docker build --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") --build-arg BUILD_VERSION=2.$(date -u +"%y.%m%d") -t milenzapryanov/ourhome:2.$(date -u +"%y.%m%d") .
# docker build --build-arg BUILD_DATE=$(date -u +"%Y-%m-%dT%H:%M:%SZ") --build-arg BUILD_VERSION=2.$(date -u +"%y.%m%d") -t milenzapryanov/ourhome:latest .