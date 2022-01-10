package bgu.spl.net.impl.Bidi;
import bgu.spl.net.api.Connections;
import bgu.spl.net.api.Message;
import bgu.spl.net.api.*;
//import bgu.spl.net.api.srv.*;
import bgu.spl.net.impl.Bidi.messages.*;
import bgu.spl.net.api.BidiMessagingProtocol;
import java.util.*;


import java.util.LinkedList;
import java.util.List;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private int connectionId;
    private Connections<Message> connections;
    private Database db;
    private boolean isTerminated = false;

    public BidiMessagingProtocolImpl(Database database) {
        db = database;
    }

    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    // execute message actions
    public void process(Message msg) {
        int intOpcode = Integer.valueOf(msg.getOpcode());
        // preform case depand on message type
        if(intOpcode==1){
            //  register message
            REGISTERmsg registerMsg = (REGISTERmsg)msg;
            // if not register- register. else- error
            if (db.isRegistered(registerMsg.getUsername())==false){
                db.registerUser(connectionId,registerMsg.getUsername(),registerMsg.getPassword(),registerMsg.getBirthday());
                connections.send(connectionId,new ACKmsg((short)1));
            }
            else{
                connections.send(connectionId,new ERRORmsg((short)1));
            }
        }
        // LOGIN
        else if(intOpcode==2){
            //login attempt
            LOGINmsg loginMsg = (LOGINmsg)msg;
            // successful login conditions:
            // registered user && not logged in && correct password && captcha ==1
            if(db.isRegistered(loginMsg.getUsername()) && db.isLogged(connectionId)==false &&
                    db.passwordConfirm(loginMsg.getUsername(),loginMsg.getPassword()) && loginMsg.getCaptcha().equals("1")){
                db.loginUser(loginMsg.getUsername(),connectionId);
                connections.send(connectionId,new ACKmsg((short)2));
                LinkedList<NOTIFICATIONmsg> notificationQueue = db.getUserById(connectionId).takeAwaitingNotifications();
                if(notificationQueue != null){
                    for(NOTIFICATIONmsg note: notificationQueue){
                        if(note != null){
                            connections.send(connectionId,note);
                        }
                    }
                }
            }
            else{
                connections.send(connectionId,new ERRORmsg((short)2));
            }
        }
        // LOGOUT
        else if(intOpcode==3){
            // logout attempt
            LOGOUTmsg logoutMsg = (LOGOUTmsg)msg;
            // if user logged in - log him out
            if(db.isLogged(connectionId)){
                db.logoutUser(connectionId);
                connections.send(connectionId,new ACKmsg((short)3));
                isTerminated = true;
                //        connections.disconnect(connectionId);
            }
            else{
                connections.send(connectionId,new ERRORmsg((short)3));
            }

        }
        // FOLLOW AND UNFOLLOW
        else if(intOpcode==4){
            FOLLOWmsg followMsg = (FOLLOWmsg)msg;
            boolean actionSuccess = false;
            // only logged users will try act
            if(db.isLogged(connectionId)){
                // dont worry - for block user follow will fail
                // follow yourself will fail too
                actionSuccess = db.tryFollow(followMsg.getFollowType(),db.getUserById(connectionId),db.getUserByUsernameString(followMsg.getUserForFollow()));
            }

            if (actionSuccess){
                connections.send(connectionId,new ACKmsg(followMsg.getFollowTypeByte(),followMsg.getUserForFollow()));
            }
            // error if not logged in or act didnt succeed
            else{
                connections.send(connectionId, new ERRORmsg((short)4));
            }

        }
        // POST
        else if(intOpcode==5){
            POSTmsg postMsg = (POSTmsg)msg;
            // if user logged in - send post
            if(db.isLogged(connectionId)){
                // ++ for user post counter and save the content in db
                db.newPost(connectionId,postMsg.getContent());
                connections.send(connectionId,new ACKmsg((short)5));
                // find @hashtags and collect int list
                LinkedList<User> hashtagsList = contentToHashtags(postMsg.getContent());
                Set<User> mergeUsersForPost = new HashSet<>();
                // add @users to destination set
                while(hashtagsList != null && hashtagsList.isEmpty() == false){
                    int currentIdToNotify = hashtagsList.removeFirst().getId();
                    // if didnt block this @user send him notification
                    if(db.getUserById(connectionId).didI_BlockedHim(db.getUserById(currentIdToNotify))==false && db.getUserById(connectionId).didThisUserBlockedMe(db.getUserById(currentIdToNotify))==false){
                        //connections.send(currentIdToNotify,new NOTIFICATIONmsg('1',db.getUsernameById(connectionId),postMsg.getContent()));
                        mergeUsersForPost.add(db.getUserById(currentIdToNotify));
                    }
                }
                // add followers to destination list. ignore doubles
                LinkedList<User> followersList = db.getUserById(connectionId).getFolowersList();
                for (User user: followersList){
                    //connections.send(user.getId(), new NOTIFICATIONmsg('1',db.getUsernameById(connectionId),postMsg.getContent()));
                    mergeUsersForPost.add(user);
                }

                //acctually send notification
                for (User user: mergeUsersForPost){
                    NOTIFICATIONmsg note = new NOTIFICATIONmsg('1',db.getUsernameById(connectionId),postMsg.getContent());
                    // for logged users- notify immdiatly
                    if(db.isLogged(user.getId())){
                        connections.send(user.getId(),note);
                    }
                    // not logged- notify later
                    else{
                        user.addAwaitingNotification(note);
                    }
                }

            }
            else{
                // error for posting while not logged in
                connections.send(connectionId,new ERRORmsg((short)5));
            }
        }

        // PM - Private Msg
        else if(intOpcode==6){
            PMmsg pmMsg = (PMmsg)msg;
            User destUser = db.getUserByUsernameString(pmMsg.getUsername());
            // if user logged in - send post
            boolean isReceiverRegistered = db.isRegistered(pmMsg.getUsername());
            // send only if  Sender is logged && Receiver registerd && send follow receiver
            if(db.isLogged(connectionId) && isReceiverRegistered && db.getUserById(connectionId).isFollowing(destUser)){
                    String filterdMsg = db.filterMsg(pmMsg.getContent());
                    db.newPM(filterdMsg);
                    connections.send(connectionId,new ACKmsg((short)6));
                    NOTIFICATIONmsg note = new NOTIFICATIONmsg('0',db.getUsernameById(connectionId),filterdMsg+" "+pmMsg.getDate());
                    // for logged users- notify immdiatly
                    if(db.isLogged(destUser.getId())){
                        connections.send(destUser.getId(),note);
                    }
                    // not logged- notify later
                    else{
                        destUser.addAwaitingNotification(note);
                    }
                }
            // error cases
            else{
                // get Error with  the text " @username isn’t applicable for private messages."
                if(isReceiverRegistered==false){
                    connections.send(connectionId,new ERRORmsg((short)6,"@"+pmMsg.getUsername()+" isn’t applicable for private messages."));
                }
                // else - regular error
                // because sender not loggedin or not follow the receiver
                else{
                    connections.send(connectionId,new ERRORmsg((short)6));
                }
            }

        }
        // LOGSTAT
        else if(intOpcode==7){
            LOGSTATmsg logstatMsg = (LOGSTATmsg)msg;
            // must be registered for logstat
            User myUser = db.getUserById(connectionId);
            if(db.isRegistered(connectionId)){
                LinkedList<User> usersList = db.getUsersList();
                LinkedList<User> validUsersForLOGSTAT = new LinkedList<>();
                for(User user: usersList){
                    //send stats on user only if - Logged && not block each other
                    if(user.isLogged()==true && user.didI_BlockedHim(myUser) == false && user.didThisUserBlockedMe(myUser) == false){
            //            validUsersForLOGSTAT.add(user);
                        connections.send(connectionId,new ACKmsg((short)7,user));
                    }
                }
            //    //actually send ack here - for loop is for collecting the relevant users
            //    //actually send ack here - for loop is for collecting the relevant users
            //    connections.send(connectionId,new ACKmsg((short)7,validUsersForLOGSTAT));
            }
            // error cases
            else{
                connections.send(connectionId,new ERRORmsg((short)7));
            }

        }
        // STAT
        else if(intOpcode==8){
            STATmsg statMsg = (STATmsg)msg;
            // must be logged for STAT
            if(db.isLogged(connectionId)){
                String[] usersStringList = statMsg.getStringsOfUsernames();
                for(String userString: usersStringList){
                    User user = db.getUserByUsernameString(userString);
                    //no such user
                    if(user==null){
                        // Error for this user
                        // maybe we need add text on the error
                        connections.send(connectionId,new ERRORmsg((short)8));
                    }
                    else{
                        // error if user block me
                        if(db.getUserById(connectionId).didThisUserBlockedMe(user) || db.getUserById(connectionId).didI_BlockedHim(user)){
                            connections.send(connectionId,new ERRORmsg((short)8));
                        }
                        // send stats of user if not block me
                        else{
                            connections.send(connectionId,new ACKmsg((short)8,user));
                        }
                    }
                }
            }
            else{
                // maybe we need more Text with this error
                connections.send(connectionId,new ERRORmsg((short)8));
            }

        }
        // BLOCK
        else if(intOpcode==12){
            BLOCKmsg blockMsg = (BLOCKmsg)msg;
            //blockThisUser maybe null
            User myUser = db.getUserById(connectionId);
            User blockThisUser = db.getUserByUsernameString(blockMsg.getUsername());
            //
            // ** File don't say you need to be logged to BLOCK!!!?!!?!?!?!?
            // if the user exist && not me && didnt block him already;
            if( blockThisUser != null && blockThisUser != myUser && myUser.didI_BlockedHim(blockThisUser)==false){
                myUser.block(blockThisUser);
                connections.send((connectionId),new ACKmsg((short)12));
            }
            else{
                // no such user || its your user || blocked already
                connections.send(connectionId,new ERRORmsg((short)12));
            }
        }
    }

    private LinkedList<User> contentToHashtags(String content){
        LinkedList<User> list = new LinkedList<User>();
        String str = content;
        int index = 0;
        //find hashtags only if end with ' ' or end the text "sadfasdf @hi and also @mai"
        while(str.length()>0){
            index = str.indexOf('@');
            if(index==-1)
                break;
            str = str.substring(index+1,str.length());
            int hashtag_end_index = str.indexOf(' ');
            String userString;
            User user;
            if(hashtag_end_index == -1){
                userString = str;
                user = db.getUserByUsernameString(userString);
            }
            else{
                userString = str.substring(0,hashtag_end_index);
                str = str.substring(hashtag_end_index+1);
                user = db.getUserByUsernameString(userString);
            }
            if(user != null)
                list.add(user);
        }
        return list;
    }

    /**
     * @return true if the connection should be terminated
     */
    //************** ERRORRRRRR
    public boolean shouldTerminate(){
        return isTerminated;
    }

}
