package com.example.finalproject;

import android.net.Uri;

public class RecyclerItem {
    private Uri imageUri;
    private String imagePath;

    public RecyclerItem(Uri imageUri, String imagePath) {
        this.imageUri = imageUri;
        this.imagePath = imagePath;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
