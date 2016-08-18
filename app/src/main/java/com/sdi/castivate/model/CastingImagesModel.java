package com.sdi.castivate.model;

/** Holds casting image url data */
public class CastingImagesModel {

	public String imageRole, userName, imgThumb;
	public int imgId;
	public boolean favImg;

	public String localFile = "";

	public CastingImagesModel(String imageRole, String userName, String imgThumb, int imgId, boolean favImg, String localFile) {
		super();

		this.imageRole = imageRole;
		this.userName = userName;
		this.imgThumb = imgThumb;
		this.imgId = imgId;
		this.favImg = favImg;
		this.localFile = localFile;
	}
}
