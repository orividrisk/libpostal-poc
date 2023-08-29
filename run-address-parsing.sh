mvn clean install
export MallocStackLogging=1
leaks --atExit --list -- java -jar ./target/address-jar-with-dependencies.jar