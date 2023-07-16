FROM gradle:8.2.1-jdk8

WORKDIR /

COPY / .

RUN gradle --version
CMD $GRADLE_HOME
CMD $APP_HOME

RUN gradle installDist

CMD ./build/install/app/bin/app