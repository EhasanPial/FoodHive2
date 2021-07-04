package Model;

public class ChatModel  {
    private String message, sender, timestamp, notify ;

    public ChatModel() {
    }

    public ChatModel(String message, String sender, String timestamp, String notify) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.notify = notify;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }
}
