package edu.northeastern.group2;

public class StickerSentItem {
    private String stickerId;
    private int count;

    public StickerSentItem(String stickerId, int count) {
        this.stickerId = stickerId;
        this.count = count;
    }

    public String getStickerId() {
        return stickerId;
    }

    public void setStickerId(String stickerId) {
        this.stickerId = stickerId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
} 