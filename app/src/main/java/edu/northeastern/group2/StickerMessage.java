package edu.northeastern.group2;

public class StickerMessage {
    public String sender, receiver, stickerId;
    public long timestamp;

    public StickerMessage() {}

    public StickerMessage(String sender, String receiver,
                          String stickerId, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.stickerId = stickerId;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStickerId() {
        return stickerId;
    }

    public void setStickerId(String stickerId) {
        this.stickerId = stickerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

