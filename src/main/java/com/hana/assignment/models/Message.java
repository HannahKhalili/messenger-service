package com.hana.assignment.models;

public class Message {

    private Long id;
    private String receiverId;
    private Long timestamp;
    private String text;
    private boolean isSeen;

    public Message(String receiverId, String text) {
        this.receiverId = receiverId;
        this.text = text;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Message &&
                text.equals(((Message) obj).getText()) &&
                receiverId.equals(((Message) obj).getReceiverId()) &&
                timestamp.equals(((Message) obj).getTimestamp()) &&
                isSeen == (((Message) obj).isSeen());
    }

    @Override
    public String toString() {
        return "{ \n\tid: " + id + "\n\ttext: \"" + text + "\"\n\ttimestamps: " + timestamp + "\n}";
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
