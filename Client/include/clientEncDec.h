//
// Created by spl211 on 04/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_CLIENTENCDEC_H
#define BOOST_ECHO_CLIENT_CLIENTENCDEC_H
#include <iostream>
#include <vector>
#include <thread>

class clientEncDec {


public:
    clientEncDec();
    virtual ~clientEncDec();
    std::vector<char> encode(std::string& msg);
    std::string decodeNextByte(char byte);
    void cleanDec();
    std::string createMessage();

        private:
    short bytesToShort(char *bytesArr);
    void shortToBytes(short num, char* bytesArr);
    std::string getCurrDate();
    int numOfBytes;
    int numOfZeros;
    std::string outputToPrint;
    std::string tmpOutput;
    char result[2];
    char opcode[2];
    char msgOpcode[2];
    char byteAge[2];
    char byteNumOfUsers[2];
    char byteNumOfPosts[2];
    char byteNumOfFollowing[2];
    char byteNumOfFollowers[2];
    char numOfUserArr[2];
    bool msgTypeFlag;
    std::vector<char> bytesVector;
};
#endif //BOOST_ECHO_CLIENT_CLIENTENCDEC_H)
