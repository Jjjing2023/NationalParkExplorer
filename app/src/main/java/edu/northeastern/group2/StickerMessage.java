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
}

