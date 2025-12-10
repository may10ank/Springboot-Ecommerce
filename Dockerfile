FROM openjdk:21-jdk
ADD target/EcommerceWeb.jar EcommerceWeb.jar
ENTRYPOINT ["java","-jar","/EcommerceWeb.jar"]