#!make

.PHONY: install package test verify compile fmt clean

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
