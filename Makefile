#!make

#https://makefiletutorial.com/

# this is not working
MVN_VERSION := $(shell mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)

.PHONY: install package test verify compile fmt clean runClient runServer

install: fmt
	@ mvn install

package: fmt
	@ mvn package

test: fmt
	@ mvn test

verify: fmt
	@ mvn verify

compile: fmt
	@ mvn compile

fmt:
	@ mvn spotless:apply

clean:
	@ mvn clean

runClient:
	@ java -jar ./target/client-all-1.0-SNAPSHOT.jar

runServer:
	@ java -jar ./target/server-all-1.0-SNAPSHOT.jar
