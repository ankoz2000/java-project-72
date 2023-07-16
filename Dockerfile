FROM gradle:8.2.1-jdk8

WORKDIR /

COPY / .

RUN gradle installDist

CMD ./build/install/app/bin/app