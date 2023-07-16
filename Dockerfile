FROM gradle:8.2.1-jdk8

WORKDIR /

COPY / .

RUN gradle --version
RUN echo $GRADLE_HOME
RUN echo $APP_HOME
RUN echo $JAVA_HOME

RUN gradle installDist

CMD ./build/install/app/bin/app