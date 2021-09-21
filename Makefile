#!make

.PHONY: install test verify compile fmt clean

install: fmt
	@ mvn install

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
