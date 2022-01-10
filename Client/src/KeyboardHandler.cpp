
#include "KeyboardHandler.h"
#include <utmp.h>

KeyboardHandler::KeyboardHandler():terminate(false),login(false){


}


#include "KeyboardHandler.h"

void KeyboardHandler::run(ConnectionHandler& handler, clientEncDec& dec) {
    while (!terminate) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<char> output=dec.encode(line);
        if(line.find("LOGOUT")!=std::string::npos&&login)
            terminate= true;
        if(line.find("LOGIN")!=std::string::npos)
            login= true;

        char tmp[output.size()];

        for(int i=0;(unsigned)i<output.size();i++)
            tmp[i]=output.at(i);
        if (!handler.sendBytes(tmp,output.size())) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
    }
}

KeyboardHandler::~KeyboardHandler() {
}