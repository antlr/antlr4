FROM debian:jessie

# Suppresses some prompts/errors from apt-get
ENV DEBIAN_FRONTEND=noninteractive;

# We do NOT install maven from apt-get since it pulls 3.0.5 which is too old.
RUN apt-get update \
    && apt-get install -y \
        openjdk-7-jdk \
        wget \
        git \
    && apt-get purge -y --auto-remove \
    ;

# Grab a newer version of Maven from a mirror
ADD install-maven.sh /tmp/
RUN bash /tmp/install-maven.sh "3.5.0"

# Needed when running ANTLR as a plugin in Maven
ENV MAVEN_OPTS="-Xmx1G"

ADD antlr4.sh /usr/local/bin/
ADD grun.sh /usr/local/bin/
RUN chmod u+x /usr/local/bin/*.sh   


RUN mkdir /opt/project;
VOLUME /opt/project
WORKDIR /opt/project

CMD ["bash"]
