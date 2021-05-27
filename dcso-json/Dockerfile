# use multi-Stage build to create lightweight final image

# build stage to compile dcso-json tool with maven and openJDK 11
FROM maven:3.8.1-openjdk-11-slim AS build

COPY src /app/src
COPY pom.xml /app/pom.xml

RUN mvn -f /app/pom.xml clean install

# execution stage only requires java runtime, not full sdk
FROM openjdk:11.0.11-jre-slim AS execution

# only copy artifacts into final image that are really required to run the dcso-json
# tool and leave rest behind (sources, target/ folder including compiled class files)
COPY --from=build /app-bin/libs /dcso-json/libs
COPY --from=build /app-bin/dcso-json-*.jar /dcso-json/tool.jar

RUN mkdir -p /dcso-json/output

# copy input file(s) from input folder into container
# this is the last step, so everything up until now will be cached
# => rebuilding the container after adding files is quick
COPY input /dcso-json/input

WORKDIR /dcso-json

# in order to expand environment variables, we need to arrange for a shell using sh -c
# (otherwise, Docker would execute the command literally)
CMD ["sh", "-c", "java -jar /dcso-json/tool.jar -i=\"$INPUT_FORMAT\" -o=\"$OUTPUT_FORMAT\" \"/dcso-json/input/$INPUT_FILE\" \"/dcso-json/output/$OUTPUT_FILE\""]