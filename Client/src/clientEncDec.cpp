#include <clientEncDec.h>
#include <sstream>
#include <cstring>
#include <vector>
#include <iostream>
#include <ctime>
using namespace std;
clientEncDec::clientEncDec() : numOfBytes(0), numOfZeros(0), outputToPrint(""), tmpOutput(""), msgTypeFlag(false) {}
clientEncDec::~clientEncDec() {};

std::vector<char> clientEncDec::encode(std::string& msg) {
    std::vector<char> output;
    if (msg.find("REGISTER") != std::string::npos) {
        std::string username;
        std::string parts;
        std::string bDay;
        std::string password;
        std::istringstream input_String(msg);
        int part = 1;
        while (std::getline(input_String, parts, ' ')) {
            // reading the opcode
            switch (part) {
                case (1): {
                    part++;
                    break;
                }
                    //reading the user name
                case (2): {
                    username = parts;
                    part++;
                    break;
                }
                case (3): {
                    password = parts;
                    part++;
                    break;
                }
                    //reading the birthday
                case (4): {
                    bDay = parts;
                    break;
                }
            }
        }
        shortToBytes(1, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        for (int i = 0; (unsigned)i < username.length(); i++) {
            output.push_back(username[i]);
        }
        output.push_back('\0');
        for (int i = 0;(unsigned) i < password.length(); i++) {
            output.push_back(password[i]);
        }
        output.push_back('\0');
        for (int i = 0;(unsigned) i < bDay.length(); i++) {
            output.push_back(bDay[i]);
        }
        output.push_back('\0');
        output.push_back(';');
        return output;
    }
    if (msg.find("LOGIN") != std::string::npos){
        std::string username;
        std::string parts;
        std::string captcha;
        std::string password;
        std::istringstream input_String(msg);
        int part = 1;
        while (std::getline(input_String, parts, ' ')) {
            // reading the opcode --> continue
            switch(part) {
                case (1): {
                    part++;
                    break;
                }
                    // reading the user name
                case (2): {
                    username = parts;
                    part++;
                    break;
                }
                    // reading the password
                case (3): {
                    password = parts;
                    part++;
                    break;
                }
                    // reading the captcha
                case (4): {
                    captcha = parts;
                    break;
                }
            }
        }
        shortToBytes(2, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        for (int i = 0; (unsigned)i < username.length(); i++) {
            output.push_back(username[i]);
        }
        output.push_back('\0');
        for (int i = 0;(unsigned) i < password.length(); i++) {
            output.push_back(password[i]);
        }
        output.push_back('\0');
        for (int i = 0;(unsigned) i < captcha.length(); i++) {
            output.push_back(captcha[i]);
        }
        output.push_back(';');
        return output;
    }
    if (msg.find("LOGOUT") != std::string::npos) {
        shortToBytes(3, result);
        output.push_back(result[0]);
        output.push_back(result[1]);
        output.push_back(result[1]);
        output.push_back(';');
        return output;
    }
    if (msg.find("FOLLOW") != std::string::npos) {
        std::string parts;
        std::string username;
        char followUnfollow = -1;
        std::istringstream input_String(msg);
        int part = 1;
        while (std::getline(input_String, parts, ' ')) {
            switch(part) {
                // reading the opcode --> continue
                case(1): {
                    part = 2;
                    break;
                }
                    // reading the follow\unfollow byte
                case(2):{
                    if (parts[0] == '0')
                        followUnfollow = '\0';
                    else
                        followUnfollow = '\1';
                    part++;
                    break;
                }
            }
        }
        shortToBytes(4, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        output.push_back(followUnfollow);
        for (int i = 0; (unsigned)i < parts.length(); i++) {
            output.push_back(parts[i]);
        }
        output.push_back(';');
        return output;
    }
    if (msg.find("POST") != std::string::npos) {
        std::string parts;
        std::string content;
        std::istringstream input_String(msg);
        std::getline(input_String, parts,' ');//removes the opcode
        std::getline(input_String, parts); //assigns the content to parts
        shortToBytes(5, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        for (int i = 0; (unsigned) i < (unsigned)((int) parts.length()); i++) {
            output.push_back(parts[i]);
        }
        output.push_back('\0');
        return output;
    }
    if (msg.find("PM") != std::string::npos) {
        std::string date_time = getCurrDate();
        std::string parts;
        std::string username;
        std::string content;
        std::istringstream input_String(msg);
        std::getline(input_String, parts, ' ');//removes the opcode
        std::getline(input_String, username, ' ');
        std::getline(input_String, content);
        shortToBytes(6, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        for (int i = 0;(unsigned) i < (unsigned)((int)username.length()); i++)
            output.push_back(username[i]);
        output.push_back('\0');
        for (int i = 0;(unsigned) i < (unsigned)((int)content.length()); i++)
            output.push_back(content[i]);
        output.push_back('\0');
        for (int i = 0;(unsigned) i < (unsigned)((int)date_time.length()); i++)
            output.push_back(date_time[i]);
        output.push_back('\0');
        output.push_back(';');
        return output;
    }
    if (msg.find("LOGSTAT") != std::string::npos) {
        shortToBytes(7, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        output.push_back(';');
        return output;
    }
    if (msg.find("STAT") != std::string::npos) {
        std::string content;
        std::istringstream input_String(msg);
        std::getline(input_String, content, ' ');//removes the opcode
        std::getline(input_String, content);
        shortToBytes(8, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        for (int i = 0;(unsigned) i < (unsigned)((int)content.length()); i++)
            output.push_back(content[i]);
        output.push_back('\0');
        output.push_back(';');
        return output;
    }
    if (msg.find("BLOCK") != std::string::npos){
        std::string username;
        std::istringstream input_String(msg);
        std::getline(input_String, username, ' ');//removes the opcode
        std::getline(input_String, username);
        shortToBytes(12, opcode);
        output.push_back(opcode[0]);
        output.push_back(opcode[1]);
        for (int i = 0;(unsigned) i < (unsigned)((int)username.length()); i++)
            output.push_back(username[i]);
        output.push_back('\0');
        output.push_back(';');
        return output;
    }
    return output;
}

std::string clientEncDec::decodeNextByte(char byte) {
    if (byte == ';') {
        return createMessage();
    }

    bytesVector.push_back(byte);
    numOfBytes++;
    return "";
}

string clientEncDec::createMessage() {
    outputToPrint="";
    if (numOfBytes >= 2) {
        opcode[0] = bytesVector[0];
        opcode[1] = bytesVector[1];
        switch (bytesToShort(opcode)) {
            // Notification
            case (9): {
                outputToPrint = "NOTIFICATION ";
                char notificationType = bytesVector[2];
                if (notificationType == '\0')
                    outputToPrint += "PM ";
                else
                    outputToPrint += "Public ";
                int currIndex = 3;
                std::vector<char> usernameBytes;
                while(bytesVector[currIndex]!='\0'){
                    usernameBytes.push_back((char)bytesVector[currIndex]);
                    currIndex++;
                }
                currIndex++;
                string name(usernameBytes.begin(),usernameBytes.end());
                outputToPrint+=name;
                outputToPrint+=" ";
                std::vector<char> contentBytes;
                while(bytesVector[currIndex]!='\0'){
                    contentBytes.push_back((char)bytesVector[currIndex]);
                    currIndex++;
                }
                string content(contentBytes.begin(),contentBytes.end());
                outputToPrint+=content;
                break;
            }
            // ACK
            case (10): {
                outputToPrint = "ACK ";
                msgOpcode[0] = bytesVector[2];
                msgOpcode[1] = bytesVector[3];
                outputToPrint = outputToPrint+to_string((bytesToShort(msgOpcode)))+" ";
                switch (bytesToShort(msgOpcode)) {
                    case(4):{ //FollowUnfollow
                        int currIndex=4;
                        std::vector<char> usernameBytes;
                        while(bytesVector[currIndex]!='\0'){
                            usernameBytes.push_back((char)bytesVector[currIndex]);
                            currIndex++;
                        }
                        string name(usernameBytes.begin(),usernameBytes.end());
                        outputToPrint+=name;
                        break;
                    }
                    case(8):
                    case (7):{  //LOGSTAT & STAT
                        char age[2];
                        age[0] = bytesVector[4];
                        age[1] = bytesVector[5];
                        char numOfPosts[2];
                        numOfPosts[0] = bytesVector[6];
                        numOfPosts[1] = bytesVector[7];
                        char numOfFollowers[2];
                        numOfFollowers[0] = bytesVector[8];
                        numOfFollowers[1] = bytesVector[9];
                        char numOfFollowing[2];
                        numOfFollowing[0] = bytesVector[10];
                        numOfFollowing[1] = bytesVector[11];
                        outputToPrint+= " "+to_string(bytesToShort(age));
                        outputToPrint+= " "+to_string(bytesToShort(numOfPosts));
                        outputToPrint+= " "+to_string(bytesToShort(numOfFollowers));
                        outputToPrint+= " "+to_string(bytesToShort(numOfFollowing));
                        break;
                    }
                }
                break;
            }
            // ERROR
            case(11):{
                outputToPrint="ERROR ";
                msgOpcode[0] = bytesVector[2];
                msgOpcode[1] = bytesVector[3];
                outputToPrint+= to_string(bytesToShort(msgOpcode));
                std::vector<char> errorContentBytes;
                for(std::vector<char>::iterator it =bytesVector.begin()+4;it!=bytesVector.end();++it)
                    errorContentBytes.push_back(*it);
                if(errorContentBytes.size()!=0) {
                    outputToPrint = outputToPrint + " " + string(errorContentBytes.begin(), errorContentBytes.end());
                }
                break;
            }
        }
    }
    cleanDec();
    return outputToPrint;
}

void clientEncDec::cleanDec(){
    numOfBytes=0;
    bytesVector.clear();
}

short clientEncDec::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}

void clientEncDec::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

std::string clientEncDec::getCurrDate() {
    time_t curr_time = time(0);
    tm *local_time = localtime(&curr_time);
    int year = 1900 + local_time->tm_year;
    int month = 1 + local_time->tm_mon;
    int day = local_time->tm_mday;
    std::string str_year = to_string(year);
    std::string str_month = to_string(month);
    std::string str_day = to_string(day);
    if(str_month.length()==1)
        str_month="0"+str_month;
    if(str_day.length()==1)
        str_day="0"+str_day;
    return str_day+"-"+str_month+"-"+str_year;
}
