#include <stdlib.h>
#include <connectionHandler.h>
#include <clientEncDec.h>
#include <KeyboardHandler.h>
#include <thread>


int main (int argc, char *argv[]) {
    bool connected = true;
    clientEncDec encDec;
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    KeyboardHandler keyboardHandler;
    std::thread keyboardThread(&KeyboardHandler::run, &keyboardHandler, std::ref(connectionHandler), std::ref(encDec));
    while (connected) {
        std::string answer="";
        char nextByte[1];
        if (!connectionHandler.getBytes(nextByte,1)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        answer=encDec.decodeNextByte(nextByte[0]);
        if(answer.size()>0){
            std::cout<<answer<<std::endl;
            if(answer.find("ACK 3")!=-1){
                connected=false;
            }
        }
    }
        keyboardThread.join();
        return 0;
    }