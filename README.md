# libpostal-memory-leak
A project using libpostal with a java binding provided by javacpp-presets 

prerequisite:
1. maven 3.x
2. java
3. macos

This project uses generated address provided by the java Faker library.

In order to run the memory leak simulation use case:
* run the `run-address-parsing.sh`
script, which will run the address parsing program and print out to console all the memory leak objects
with a stack trace of the relevant method calls