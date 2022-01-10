package bgu.spl.net.impl.Bidi;

import bgu.spl.net.impl.Bidi.messages.NOTIFICATIONmsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {
    private int id;
    private String username;
    private String password;
    private String birthday;
    private int numOfPosts;
    private boolean isLogged;
    private LinkedList<User> followersList; // they follow me
    private LinkedList<User> followingList; // i follow them
    private LinkedList<User> myBlackList; // users i blocked
    private ConcurrentLinkedQueue<User> whoBlockedMeList; //
    private LinkedList<NOTIFICATIONmsg> awaitingNoticationQueue; //


    public User(int _id, String _username, String _password, String _birthday) {
        id = _id;
        username = _username;
        password = _password;
        birthday = _birthday;
        numOfPosts = 0;
        followersList = new LinkedList<User>();
        followingList = new LinkedList<User>();
        myBlackList = new LinkedList<User>();
        whoBlockedMeList = new ConcurrentLinkedQueue<>();
        awaitingNoticationQueue = new LinkedList<NOTIFICATIONmsg>();
    }

    public int getId() {
        return id;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public LinkedList<User> getFolowersList() {
        return followersList;
    }

    public LinkedList<User> getFolowingList() {
        return followingList;
    }

    public boolean isFollowing(User user) {
        if (followingList.contains(user))
            return true;
        else {
            return false;
        }
    }

    public void newFollow(User followThisUser) {
        followingList.add(followThisUser);
    }

    public void newUNFollow(User UNfollowThisUser) {
        followingList.remove(UNfollowThisUser);
    }

    public synchronized void addFollower(User user) {
        followersList.add(user);
    }

    public synchronized void removeFollower(User user) {
        followersList.remove(user);
    }

    public String getPassword() {
        return password;
    }

    public void login(int connectionID) {
        this.isLogged = true;
        this.id = connectionID;
    }

    public void logout() {
        this.isLogged = false;
    }

    public String getUsername() {
        return username;
    }

    public short getNumOfPosts() {
        return (short) numOfPosts;
    }

    public short getNumOfFollowers() {
        return (short) followersList.size();
    }

    public short getNumOfFollowing() {
        return (short) followingList.size();

    }

    //birthday 201
    public short getAge() {
        int age = 2022 - Integer.valueOf(birthday.substring(birthday.length() - 4));
        //age for current date 09-01-2022
        if (Integer.valueOf(birthday.substring(3, 5)) > 1) {
            age = age - 1;
        } else if (Integer.valueOf(birthday.substring(3, 5)) == 1 && Integer.valueOf(birthday.substring(0, 2)) > 9) {
            age = age - 1;
        }
        return (short) age;
    }


    public void increaseNumOfPosts() {
        numOfPosts++;
    }

    public boolean didThisUserBlockedMe(User user) {
        // he didnt block me - return false
        if (whoBlockedMeList.isEmpty()) {
            return false;
        } else if (!whoBlockedMeList.contains(user)) {
            return false;
        }
        // he blocked me
        else {
            return true;
        }
    }

    public boolean didI_BlockedHim(User user) {
        // he didnt block me - return false
        if (myBlackList.isEmpty()) {
            return false;
        } else if (!myBlackList.contains(user)) {
            return false;
        }
        // i blocked him
        else {
            return true;
        }
    }

    public void block(User blockThisUser) {
        // blocker side
        myBlackList.add(blockThisUser);
        followingList.remove(blockThisUser);
        followersList.remove(blockThisUser);
        // blocked user side
        blockThisUser.followingList.remove(this);
        blockThisUser.followersList.remove(this);
        blockThisUser.whoBlockedMeList.add(this);
    }

//    public String userStringForPRINT() {
//        String str = "";
//        String a = "";
//        for (int i =0 ; i < followersList.size() ; i++){
//            a = a+  followersList.get(i).getUsername() + " , ";
//        }
//        String b = "";
//        for (int i =0 ; i < followingList.size() ; i++){
//            b = b+  followingList.get(i).getUsername() + " , ";
//        }
//        String c = "";
//        for (int i =0 ; i < myBlackList.size() ; i++){
//            c = c+  myBlackList.get(i).getUsername() + " , ";
//        }
//        String d = "";
//        str = "user " + id + " info: " + getUsername() + "|" + getPassword() + "|" + birthday + "| log: " + isLogged + " | awaiting n: " + awaitingNoticationQueue.size() +  "|postNum: " + numOfPosts + "| followers:" +
//                a + "| following: " + b + "| blackList: " + c + "| who blocked me: " + d;
//
//        return str;
//    }

    public LinkedList<NOTIFICATIONmsg> takeAwaitingNotifications(){
        if(awaitingNoticationQueue.isEmpty()){
            return null;
        }
        LinkedList<NOTIFICATIONmsg> output = new LinkedList<>();
        while (awaitingNoticationQueue.isEmpty()==false){
            output.add(new NOTIFICATIONmsg(awaitingNoticationQueue.remove()));
        }
            return output;
    }

    public synchronized void addAwaitingNotification(NOTIFICATIONmsg msg){
        awaitingNoticationQueue.add(msg);
    }
}
