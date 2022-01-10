package bgu.spl.net.impl.Bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.Bidi.messages.*;
import bgu.spl.net.api.Message;
import bgu.spl.net.impl.Bidi.messages.ACKmsg;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class BidiMessageEncoderDecoder implements MessageEncoderDecoder<Message> {
    byte[] encodedMessage = null;
    ArrayList<Byte> readerArray = new ArrayList<>();
    int numOfBytes = 0;
    byte[] opcodeArray = new byte[2];
    short opcode = -1;

    int numOfZeros = 0;

    Message msgToReturn = null;

    public Message decodeNextByte(byte nextByte) {
        /** Extracts opcode **/
        if(numOfBytes==0 && nextByte==(double)';'){
            cleanDecoder();
            return null;
        }
        if(numOfBytes < 2){
            opcodeArray[numOfBytes] = nextByte;
            if(numOfBytes == 1){
                opcode = bytesToShort(opcodeArray);
                if(opcode == 3){
                    cleanDecoder();//check
                    return new LOGOUTmsg();
                }/** LOGOUT **/
                if(opcode == 7){
                    cleanDecoder();//check
                    return new LOGSTATmsg();
                }/** LOGSTAT **/
            } /** LOGOUT & LOGSTAT **/
        }
        else{
            msgToReturn = null;
            switch(opcode){
                case 1: {     // username0password0birthday0
                    if (nextByte == (byte) '\0') {
                        if (numOfZeros == 2) {
                            String[] msgContentArray = new String(toBytesArray(readerArray), StandardCharsets.UTF_8).split("\0");
                            cleanDecoder();//check
                            return new REGISTERmsg(msgContentArray[0], msgContentArray[1], msgContentArray[2]);
                        } else {
                            numOfZeros++;
                        }
                    }
                    //when reading the data, every iteration will involve a write to the readerArray.
                    //the only iteration the will not involve it, its the iteration that we read \n and
                    //we saw 1 already one \n before. so we will construct the array and return it
                    readerArray.add(nextByte);
                    break;
                }/** REGISTER **/
                case 2:{ // LOGIN - username0password0captcha
                    if(nextByte ==(byte) '\0')
                        numOfZeros++;
                    else{
                        if(numOfZeros == 2) {
                            readerArray.add(nextByte);
                            String[] msgContentArray = new String(toBytesArray(readerArray),StandardCharsets.UTF_8).split("\0");
                            cleanDecoder();
                            opcode = 0; // check

                            return new LOGINmsg(msgContentArray[0], msgContentArray[1],msgContentArray[2]);
                        }
                    }
                    readerArray.add(nextByte);
                    break;
                }/** LOGIN **/
                case 4:{ //Follow(0)\Unfollow(1) Username
                    if(nextByte==';')
                    {
                        byte follow = readerArray.get(0);
                        String username = new String(toBytesArray(readerArray.subList(1,readerArray.size())), StandardCharsets.UTF_8);
                        cleanDecoder();//check
                        return new FOLLOWmsg(follow, username);
                    }
                    readerArray.add(nextByte);
                    break;
                }/** FOLLOW **/
                case 5:{ //Content0
                    if(nextByte == (byte)'\0'){
                        String content = new String(toBytesArray(readerArray), StandardCharsets.UTF_8);
                        cleanDecoder();//check
                        return new POSTmsg(content);
                    }
                    else
                        readerArray.add(nextByte);
                    break;
                }/** POST **/
                case 6:{ // Username0Contents0Date&Time0
                    if(nextByte == (byte)'\0') {
                        if(numOfZeros == 2) {
                            String[] msgContentArray = new String(toBytesArray(readerArray),
                                    StandardCharsets.UTF_8).split("\0");
                            cleanDecoder();//check
                            return new PMmsg(msgContentArray[0], msgContentArray[1], msgContentArray[2]);
                        }
                        else
                            numOfZeros++;
                    }
                    readerArray.add(nextByte);
                    break;
                }/** PM **/
                case 8:{ // ListOfUsernames0
                    if(nextByte == (byte)'\0'){
                        String[] msgContentArray = new String(toBytesArray(readerArray), StandardCharsets.UTF_8).split("\\|");
                        cleanDecoder();
                        return new STATmsg(msgContentArray);
                    }
                    else
                        readerArray.add(nextByte);
                    break;
                }/** STAT **/
                case 12:{
                    if(nextByte == (byte)'\0'){
                        String username = new String(toBytesArray(readerArray), StandardCharsets.UTF_8);
                        cleanDecoder();
                        return new BLOCKmsg(username);
                    }
                    else
                        readerArray.add(nextByte);
                    break;
                }/** BLOCK **/
            }
        }
        numOfBytes++;
        return msgToReturn;
    }

    public byte[] encode(Message message) {
        byte[] opcodeBytes = shortToBytes(message.getOpcode());
        int currEncodeIndex = 0;
        switch (message.getOpcode()) {
            case 9: {
                NOTIFICATIONmsg msg = (NOTIFICATIONmsg) message;
                String notificationType = String.valueOf(msg.getNotificationType());
                encodedMessage = new byte[msg.getPostingUser().getBytes().length + msg.getContent().getBytes().length + 6];
                encodeBytes(opcodeBytes, currEncodeIndex);
                currEncodeIndex +=2;
                encodeBytes(notificationType.getBytes(), currEncodeIndex);
                currEncodeIndex++;
                encodeBytes(msg.getPostingUser().getBytes(), currEncodeIndex);
                currEncodeIndex = currEncodeIndex + msg.getPostingUser().getBytes().length;
                encodedMessage[currEncodeIndex++] = (byte) '\0';
                encodeBytes(msg.getContent().getBytes(), currEncodeIndex);
                currEncodeIndex = currEncodeIndex + msg.getContent().getBytes().length;
                encodedMessage[currEncodeIndex++] = (byte) '\0';
                break;
            } /** NOTIFICATION **/
            // Acknowledgement recievers:
            //8 STAT - have the following form:  (for each username!)
            // 2 bytes    , 2 bytes       , 2 Bytes  , 2 Bytes   , 2 bytes      , 2 Bytes
            // Ack Opcode   STAT opcode    Age        NumPosts    NumFollowers   NumFollowing

            //7 LOGSTAT - have the following form:  (for each logged-in user!)
            // 2 bytes    , 2 bytes         , 2 Bytes   , 2 Bytes   , 2 bytes      , 2 Bytes
            // Ack Opcode   LOGSTAT opcode    Age        NumPosts    NumFollowers   NumFollowing

            //4 FOLLOW - have the following form:
            // 2 bytes    , 2 bytes         , String       , 1 bytes
            // Ack Opcode   Follow opcode    UserName       0

            //3 LOGOUT - nothing in optional
            //1 REGISTER - nothing in optional

            // ACK - structure for LOGOUT & REGISTER
            // 2 byte      , 2 Byte
            // Ack Opcode   MessageOpCode
            case 10: {
                ACKmsg ackMessage = (ACKmsg)message;
                short msgOpcode = ackMessage.getMsgOpcode();
                switch (msgOpcode) {
                    case 8:
                    case 7:{
                        User user = ackMessage.getUser();
                        encodedMessage = new byte[14];
                        encodeBytes(shortToBytes(message.getOpcode()), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + opcodeBytes.length;
                        encodeBytes(shortToBytes(msgOpcode), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + opcodeBytes.length;
                        encodeBytes(shortToBytes(user.getAge()), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + 2;
                        encodeBytes(shortToBytes(user.getNumOfPosts()), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + 2;
                        encodeBytes(shortToBytes(user.getNumOfFollowers()), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + 2;
                        encodeBytes(shortToBytes(user.getNumOfFollowing()), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + 2;
                        encodedMessage[currEncodeIndex++] = (byte) '\0';
                        break;
                    }/** LOGSTAT & STAT**/
                    case 4:{
                        encodedMessage = new byte[6 + ackMessage.getUserName().length()];
                        encodeBytes(shortToBytes(message.getOpcode()), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + opcodeBytes.length;
                        encodeBytes(shortToBytes(msgOpcode), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + opcodeBytes.length;
                        encodeBytes(ackMessage.getUserName().getBytes(), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + ackMessage.getUserName().length();
                        encodedMessage[currEncodeIndex++] = (byte) '\0';
                        break;
                    }/** FOLLOW **/
                    default: {
                        encodedMessage = new byte[5];
                        encodeBytes(shortToBytes(message.getOpcode()), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + opcodeBytes.length;
                        encodeBytes(shortToBytes(msgOpcode), currEncodeIndex);
                        currEncodeIndex = currEncodeIndex + shortToBytes(msgOpcode).length;
                        break;
                    }/** non-content ACKs **/
                }
                break;
            }/** ACK **/
            case 11: {
                ERRORmsg errMessage = (ERRORmsg) message;
                String errorContent = errMessage.getErrorText();
                encodedMessage = new byte[5+(errorContent.getBytes(StandardCharsets.UTF_8)).length];
                encodeBytes(opcodeBytes, currEncodeIndex);
                currEncodeIndex = currEncodeIndex + opcodeBytes.length;
                encodeBytes(shortToBytes(errMessage.getErrorMsgOptNum()), currEncodeIndex);
                currEncodeIndex = currEncodeIndex + shortToBytes(errMessage.getErrorMsgOptNum()).length;
                if(errorContent.length()!=0)
                    encodeBytes(errorContent.getBytes(StandardCharsets.UTF_8), currEncodeIndex);
                currEncodeIndex = currEncodeIndex+((errorContent.getBytes(StandardCharsets.UTF_8)).length);
                break;
            }/** ERROR **/
        }
        encodedMessage[currEncodeIndex++] = (byte) ';';
        return encodedMessage;
    }

    private void encodeBytes(byte[] toEncode, int startIndex){
        for(int i = 0; i < toEncode.length; i++){
            encodedMessage[startIndex+i] = toEncode[i];
        }
    }

    public static short bytesToShort(byte[] bytesArray){
        short result = (short) ((bytesArray[0] & 0xFF) << 8);
        result += (short) (bytesArray[1] & 0xFF);
        return result;
    }

    private byte[] toBytesArray(List<Byte> toConvert){
        byte[] toReturn = new byte[toConvert.size()];
        for(int i =0; i < toConvert.size(); i++){
            toReturn[i] = toConvert.get(i);
        }

        return toReturn;
    }

    private void cleanDecoder(){
        numOfBytes = 0;
        numOfZeros = 0;
        readerArray.clear();
    }

    private byte[] shortToBytes(short num) {
        byte[] shortInBytes = new byte[2];
        shortInBytes[0] = (byte)((num >> 8) & 0xFF);
        shortInBytes[1] = (byte)(num & 0xFF);
        return shortInBytes;
    }
}
