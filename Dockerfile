FROM maven:3.8.6-eclipse-temurin-19-alpine AS build

COPY ./ /workingdir/
WORKDIR /workingdir

RUN mvn clean install

FROM eclipse-temurin:19.0.1_10-jre-alpine

COPY --from=build /workingdir/target/*.jar ./lens_beacon_service.jar

CMD java -jar lens_beacon_service.jar

