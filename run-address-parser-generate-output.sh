mvn clean install
export MallocStackLogging=1
leaks --atExit --list -outputGraph=g1 -fullContent -- java -jar ./target/address-jar-with-dependencies.ja
leaks g1.memgraph