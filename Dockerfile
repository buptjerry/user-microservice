FROM java:8
MAINTAINER bupt.coder
ADD build/libs/usermicroservice-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# --spring.profiles.active="create"