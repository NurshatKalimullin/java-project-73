FROM gradle:7.4.0-jdk17

WORKDIR /java-project-73

COPY /java-project-73

RUN gradle installDist

CMD ./build/install/java-project-73/bin/java-project-73