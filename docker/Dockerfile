FROM adoptopenjdk/openjdk11:alpine AS builder

WORKDIR /opt/antlr4

ARG ANTLR_VERSION="4.11.1"
ARG MAVEN_OPTS="-Xmx1G"


RUN apk add --no-cache maven git \
    && git clone https://github.com/antlr/antlr4.git \
    && cd antlr4 \
    && git checkout $ANTLR_VERSION \
    && mvn clean --projects tool --also-make \
    && mvn -DskipTests install --projects tool --also-make \
    && mv ./tool/target/antlr4-*-complete.jar antlr4-tool.jar

FROM adoptopenjdk/openjdk11:alpine-jre

ARG user=appuser
ARG group=appuser
ARG uid=1000
ARG gid=1000

RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "$(pwd)" \
    --no-create-home \
    --uid "${uid}" \
    "${user}"

COPY --from=builder /opt/antlr4/antlr4/antlr4-tool.jar /usr/local/lib/
WORKDIR /work
ENTRYPOINT ["java", "-Xmx500M", "-cp", "/usr/local/lib/antlr4-tool.jar", "org.antlr.v4.Tool"]


