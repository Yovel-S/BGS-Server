package bgu.spl.net.impl.Bidi;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Database {
    HashMap<String, User> usernameToUser = new HashMap<String, User>();
    HashMap<Integer, User> userIdToUser = new HashMap<Integer, User>();
    LinkedList<User> usersList = new LinkedList<User>();
    ConcurrentLinkedQueue<String> allPosts = new ConcurrentLinkedQueue<>();
    LinkedList<String> forbiddenWordsList = new LinkedList<String>();


    public Database(){
        forbiddenWordsList.add("Hi");forbiddenWordsList.add("fuck");forbiddenWordsList.add("love");
    }

    public void newPost(int connectionId, String content){
        allPosts.add(content + "  [post by: " + getUsernameById(connectionId) + " ]");
        userIdToUser.get(connectionId).increaseNumOfPosts();
    }

    public void newPM(String filterdMsg) {
        allPosts.add(filterdMsg);
    }

    // create User object and put in the db stracutre for users
    public synchronized void registerUser(int id, String username, String password, String birthday){
        User user = new User(id, username,password,birthday);
        usersList.add(user);
        usernameToUser.put(username,user);
        userIdToUser.put(id,user);
    }

    public boolean isLogged(int id){
        //no such user - return false;
        if(userIdToUser.get(id)==null)
            return false;
        else{
            // user exist - return his own log status boolean field;
            return userIdToUser.get(id).isLogged();
        }
    }

    public boolean isRegistered(String username){
        if(usernameToUser.get(username) != null)
            return true;
        else{
            return false;
        }
    }

    public boolean isRegistered(int connectionID){
        if(userIdToUser.get(connectionID) != null)
            return true;
        else{
            return false;
        }
    }

    public boolean passwordConfirm(String username, String password){
        User user = usernameToUser.get(username);
        if(user != null && user.getPassword().equals(password))
            return true;
        else{
            return false;
        }
    }

    //called only after all the coditions are right
    public void loginUser(String username , int connectionID){
        ///login and set new connection ID
        getUserByUsernameString(username).login(connectionID);
        // update db with newID-user
        userIdToUser.put(connectionID,getUserByUsernameString(username));
    }
    //called only if logged already
    public void logoutUser(int id){
        userIdToUser.get(id).logout();
    }

    // search user by username string - return user or null if doesnt find
    public User getUserByUsernameString(String userString){
        return usernameToUser.get(userString);
    }

    // search user by id and return is username
    public String getUsernameById(int id) {

        if (userIdToUser.get(id) != null)
            return userIdToUser.get(id).getUsername();
        else {
            return null;
        }
    }

    public User getUserById(int id){
           return userIdToUser.get(id);
    }


    public boolean tryFollow(String followType, User user, User followThisUser) {
        boolean actionSuccess = false;
        // one of the user doesnt exist;
        if(user == null || followThisUser == null || user == followThisUser){
            return false;
        }

        if (followType=="FOLLOW"){
            //follow if not follow already && not blocked by him
            if(user.isFollowing(followThisUser)==false && !user.didThisUserBlockedMe(followThisUser) &&  !user.didI_BlockedHim(followThisUser)){
                user.newFollow(followThisUser);
                followThisUser.addFollower(user);
                actionSuccess = true;
            }
        }
        //UNFOLLOW try
        else{
            // only unfollow if you actually fallow this user
            if(user.isFollowing(followThisUser)==true){
                user.newUNFollow(followThisUser);
                followThisUser.removeFollower(user);
                actionSuccess = true;
            }

        }

        return actionSuccess;
    }

    public String filterMsg(String msg){
        String filteredMsg = msg;
        for(String badWord : forbiddenWordsList){
            filteredMsg = filteredMsg.replaceAll(badWord,"<filtered>");
        }
        return filteredMsg;
    }


    public LinkedList<User> getUsersList() {
        return usersList;
    }


//    public void printDB(){
//        String usersData = "";
//        System.out.println("DB data: ");
//        for(int i=0 ; i < usersList.size() ; i++){
//            System.out.println(usersList.get(i).userStringForPRINT());
//        }
//        System.out.println(" all posts/pm: ");
//        System.out.println(allPosts.toString());
//
//    }


}




//          if(db.isRegistered(loginMsg.getUsername()) && db.isLogged(connectionId) &&
//                  db.passwordConfirm(loginMsg.getUsername(),loginMsg.getPassword()) && loginMsg.getCaptcha() == "1"){
//                  db.loginUser(loginMsg.getUsername());
//                  }
//                  else{
//                  connections.send(connectionId,new ERRORmsg((short)2));
//                  }
//                  }
//                  // LOGOUT
//                  else if(intOpcode==3){
//                  // logout attempt
//                  LOGOUTmsg logoutMsg = (LOGOUTmsg)msg;
//                  // if user logged in - log him out
//                  if(db.isLogged(connectionId)){
//                  db.logOut(connectionId);