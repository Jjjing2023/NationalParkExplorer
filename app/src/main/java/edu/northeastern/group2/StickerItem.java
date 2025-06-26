package edu.northeastern.group2;

public class StickerItem {
    private final String id;
    private final int drawableRes;

    public StickerItem(String id, int drawableRes) {
        this.id = id;
        this.drawableRes = drawableRes;
    }
    public String getId() { return id; }
    public int getDrawableRes() { return drawableRes; }
}
