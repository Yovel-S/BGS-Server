#ifndef BOOST_BGS_CLIENT_KEYBOARDTHREAD_H
#define BOOST_BGS_CLIENT_KEYBOARDTHREAD_H


#include "connectionHandler.h"
#include "clientEncDec.h"

class KeyboardHandler {
private:
    bool terminate;
    bool login;
public:

    KeyboardHandler();
    virtual ~KeyboardHandler();
    void run(ConnectionHandler& handler, clientEncDec& dec);

};


#endif //BOOST_BGS_CLIENT_KEYBOARDTHREAD_H