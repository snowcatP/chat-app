package com.example.photoeditingapp.entity;


// Vu Xuan Hoang 21110770
public class SharedImage {
    private String imageURL;
    private String caption;

    public SharedImage() {}
    public SharedImage(String imageURL, String caption) {
        this.imageURL = imageURL;
        this.caption = caption;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
