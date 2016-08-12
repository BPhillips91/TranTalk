package generalassemb.ly.trantalk;

/**
 * Created by brendan on 8/9/16.
 */
public class Chat {
    String user;
    String message;
    String userLan;
    long timestamp;

    public String getUser() {
        return user;
    }

    public String getUserLan() {
        return userLan;
    }

    public Chat(){}

    public Chat(String user, String message, long timestamp) {
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }



    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
