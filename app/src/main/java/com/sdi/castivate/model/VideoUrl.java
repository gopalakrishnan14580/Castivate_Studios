package com.sdi.castivate.model;

import java.io.Serializable;

/**
 * Created by twilightuser on 29/8/16.
 */
public class VideoUrl implements Serializable {

    private String uploadVideoUrl;

    public String getUploadVideoUrl() {
        return uploadVideoUrl;
    }

    public void setUploadVideoUrl(String uploadVideoUrl) {
        this.uploadVideoUrl = uploadVideoUrl;
    }
}
