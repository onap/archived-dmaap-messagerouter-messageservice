FROM openjdk:8-jdk
ADD appl /appl/
ADD startup.sh /startup.sh
RUN chmod 700 /startup.sh
ENTRYPOINT ./startup.sh
EXPOSE 3904 3905
