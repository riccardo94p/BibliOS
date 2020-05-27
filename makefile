.PHONY: all clean

all: pom.xml src/main/java/*.java
	mvn clean javafx:run
	
