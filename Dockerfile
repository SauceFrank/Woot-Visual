# specify the base image to match your environment requirements
FROM maven:3.6.3-jdk-8
ARG USER_HOME_DIR="/root"
WORKDIR /workdir

# copy the project code
COPY . .

# install necessary dependencies
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
RUN mvn clean; exit 0