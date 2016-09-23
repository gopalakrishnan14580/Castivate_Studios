package com.sdi.castivate.model;

import java.io.Serializable;

/**
 * Created by twilightuser on 29/8/16.
 */
public class ImageUrl implements Serializable {

    public boolean isSelected;
    private String uploadImageUrl;

    public String getUploadImageUrl() {
        return uploadImageUrl;
    }

    public void setUploadImageUrl(String uploadImageUrl) {
        this.uploadImageUrl = uploadImageUrl;
    }
}
