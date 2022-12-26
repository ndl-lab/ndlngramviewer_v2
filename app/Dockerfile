FROM openjdk:11-jre-slim
LABEL maintainer="lab@ndl.go.jp"
WORKDIR /work
COPY ./target/ngramviewer-0.1.jar .
COPY ./target/lib lib/
RUN apt-get update && apt-get install -y && \
    apt-get install tzdata -y && \
    rm /etc/localtime && \
    echo "Asia/Tokyo" > /etc/timezone && \
    cp /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && \
    apt-get remove tzdata -y && \
    apt-get clean
RUN ls
EXPOSE 9981
CMD java -jar ngramviewer-0.1.jar web
