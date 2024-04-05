# Docker container used to build the Clojure app
FROM clojure:temurin-17-lein-alpine as builder

WORKDIR /usr/src/app

COPY project.clj /usr/src/app/project.clj

# Cache deps so they aren't fetched every time a .clj file changes
RUN lein deps

COPY src/ /usr/src/app/src

RUN lein uberjar


# Build the docker container we will use in the lambda
FROM amazoncorretto:17

RUN mkdir /opt/app

# Change the name of the source jar file (this should be the uberjar from the project)
COPY --from=builder /usr/src/app/target/ring-lambda-standalone.jar /opt/app/app.jar

COPY ./entry_script.sh /entry_script.sh
RUN chmod +x /entry_script.sh
ADD aws-lambda-rie /usr/local/bin/aws-lambda-rie

# Change the classname and handler method below
ENTRYPOINT [ "/entry_script.sh", "ring_lambda.core::handleRequest" ]

# CMD ["ring_lambda.core::handleRequest"]