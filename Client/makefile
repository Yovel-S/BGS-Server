CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/BGSclient.o bin/clientEncDec.o bin/KeyboardHandler.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/BGSclient.o bin/clientEncDec.o bin/KeyboardHandler.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/clientEncDec.o: src/clientEncDec.cpp
	g++ $(CFLAGS) -o bin/clientEncDec.o src/clientEncDec.cpp

bin/KeyboardHandler.o: src/KeyboardHandler.cpp
	g++ $(CFLAGS) -o bin/KeyboardHandler.o src/KeyboardHandler.cpp



.PHONY: clean
clean:
	rm -f bin/*