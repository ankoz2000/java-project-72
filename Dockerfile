FROM eclipse-temurin:20-jdk

ARG GRADLE_VERSION=8.2

RUN apt-get update && apt-get install -yq unzip

RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip

ENV GRADLE_HOME=/opt/gradle

RUN mv gradle-${GRADLE_VERSION} ${GRADLE_HOME}

ENV PATH=$PATH:$GRADLE_HOME/bin

WORKDIR /

COPY / .

RUN gradle installDist

RUN ls -la

RUN ls -la ./build/install/
RUN #ls -la ./build/install/app/
#RUN ls -la ./build/install/app/bin/
#RUN ls -la ./app/build/


CMD ./build/install/app/bin/app