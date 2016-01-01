package com.apcom.instagramapp;

import android.graphics.Bitmap;

public class GalleryItem {
    private String id;
    private String image;

    public GalleryItem(String image, String id) {
        super();
        this.image = image;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getImageUrl() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
