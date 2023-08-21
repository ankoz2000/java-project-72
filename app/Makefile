.DEFAULT_GOAL := build-run

clean:
	./gradlew clean

build:
	./gradlew clean build

run-dist:
	./build/install/app/bin/app

start:
	APP_ENV=development ./gradlew run

start-dist:
	APP_ENV=production ./build/install/java-javalin-blog/bin/java-javalin-blog

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain checkstyleTest

update-deps:
	./gradlew useLatestVersions


build-run: build run

.PHONY: build