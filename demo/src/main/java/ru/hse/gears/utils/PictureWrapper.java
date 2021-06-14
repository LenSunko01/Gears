package ru.hse.gears.utils;

public class PictureWrapper {
    private byte[] picture;

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public PictureWrapper(byte[] picture) {
        this.picture = picture;
    }

    public byte[] getPicture() {
        return picture;
    }
}
