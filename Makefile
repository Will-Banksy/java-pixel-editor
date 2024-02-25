# A simple Makefile for running the project, as if you don't know how java works (like me) it's an absolute pain to figure out
#
# Circumventing the module system for now because I can't figure out how to get it to work with CLI

SOURCE_DIR = src
SOURCES = src/jpixeleditor/Main.java src/jpixeleditor/tools/*.java src/jpixeleditor/ui/*.java src/jpixeleditor/utils/*.java
DEPS = lib/Filters.jar
RESOURCES_DIR = resources

run: build
	java --class-path ${SOURCE_DIR}:${DEPS}:${RESOURCES_DIR} jpixeleditor/Main

build:
	javac --class-path ${DEPS}:${RESOURCES_DIR} ${SOURCES}
