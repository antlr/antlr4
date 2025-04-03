FROM adoptopenjdk/openjdk11:jre

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

RUN curl https://www.antlr.org/download/antlr-4.12.0-complete.jar -o /usr/local/lib/antlr-4.12.0-complete.jar

WORKDIR /work
ENTRYPOINT ["java", "-Xmx500M", "-cp", "/usr/local/lib/antlr-4.12.0-complete.jar", "org.antlr.v4.Tool"]
