(1)How To Run:
(1.1)
Thread Per Client:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"

(1.2)
Reactor:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 3"

(1.3)
Client:
make clean
make
./bin/BGSclient 127.0.0.1 7777

(2)Messages Commands:
REGISTER <username> <password> <DD-MM-YYYY>
LOGIN <username> <password> <0/1>
LOGOUT
FOLLOW <0/1> <username>
POST <text>
PM <username> <text>
LOGSTAT
STAT <usnameA|usernameB|usernameC|...>
BLOCK <username>

(3)Filtered words:
inside LINKEDLIST forbiddenWordsList.
Can be changed manually in the constructor of Database class
Server\src\main\java\bgu\spl\net\impl\Bidi\Database